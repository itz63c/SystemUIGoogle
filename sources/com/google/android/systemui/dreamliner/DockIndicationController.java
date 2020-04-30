package com.google.android.systemui.dreamliner;

import android.content.Context;
import android.content.Intent;
import android.os.ResultReceiver;
import android.os.UserHandle;
import android.util.Log;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.View.OnClickListener;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.C2004R$anim;
import com.android.systemui.C2010R$drawable;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2017R$string;
import com.android.systemui.Dependency;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.statusbar.phone.KeyguardIndicationTextView;
import com.android.systemui.statusbar.phone.NotificationShadeWindowView;
import com.android.systemui.statusbar.phone.StatusBar;
import java.util.concurrent.TimeUnit;

public class DockIndicationController implements StateListener, OnClickListener, OnAttachStateChangeListener {
    @VisibleForTesting
    static final String ACTION_ASSISTANT_POODLE = "com.google.android.systemui.dreamliner.ASSISTANT_POODLE";
    private static final long KEYGUARD_INDICATION_TIMEOUT_MILLIS = TimeUnit.SECONDS.toMillis(15);
    /* access modifiers changed from: private */
    public static final long PROMO_SHOWING_TIME_MILLIS = TimeUnit.SECONDS.toMillis(2);
    private final AccessibilityManager mAccessibilityManager;
    private final Context mContext;
    private final Runnable mDisableLiveRegionRunnable = new Runnable() {
        public final void run() {
            DockIndicationController.this.disableLiveRegion();
        }
    };
    @VisibleForTesting
    FrameLayout mDockPromo;
    @VisibleForTesting
    ImageView mDockedTopIcon;
    private boolean mDocking;
    private boolean mDozing;
    private final Animation mHidePromoAnimation;
    /* access modifiers changed from: private */
    public final Runnable mHidePromoRunnable = new Runnable() {
        public final void run() {
            DockIndicationController.this.hidePromo();
        }
    };
    @VisibleForTesting
    boolean mIconViewsValidated;
    private KeyguardIndicationTextView mKeyguardIndicationTextView;
    private boolean mShowPromo;
    private final Animation mShowPromoAnimation;
    /* access modifiers changed from: private */
    public int mShowPromoTimes;
    private final StatusBar mStatusBar;
    private boolean mTopIconShowing;

    private static class PhotoAnimationListener implements AnimationListener {
        public void onAnimationRepeat(Animation animation) {
        }

        public void onAnimationStart(Animation animation) {
        }

        private PhotoAnimationListener() {
        }
    }

    public void onViewAttachedToWindow(View view) {
    }

    public DockIndicationController(Context context, StatusBar statusBar) {
        this.mContext = context;
        this.mStatusBar = statusBar;
        ((SysuiStatusBarStateController) Dependency.get(StatusBarStateController.class)).addCallback(this);
        Animation loadAnimation = AnimationUtils.loadAnimation(this.mContext, C2004R$anim.dock_promo_animation);
        this.mShowPromoAnimation = loadAnimation;
        loadAnimation.setAnimationListener(new PhotoAnimationListener() {
            public void onAnimationEnd(Animation animation) {
                DockIndicationController dockIndicationController = DockIndicationController.this;
                dockIndicationController.mDockPromo.postDelayed(dockIndicationController.mHidePromoRunnable, DockIndicationController.PROMO_SHOWING_TIME_MILLIS);
            }
        });
        Animation loadAnimation2 = AnimationUtils.loadAnimation(this.mContext, C2004R$anim.dock_promo_fade_out);
        this.mHidePromoAnimation = loadAnimation2;
        loadAnimation2.setAnimationListener(new PhotoAnimationListener() {
            public void onAnimationEnd(Animation animation) {
                if (DockIndicationController.this.mShowPromoTimes < 5) {
                    DockIndicationController.this.showPromoInner();
                }
            }
        });
        this.mAccessibilityManager = (AccessibilityManager) this.mContext.getSystemService("accessibility");
    }

    public void onClick(View view) {
        if (view.getId() == C2011R$id.docked_top_icon) {
            Intent intent = new Intent(ACTION_ASSISTANT_POODLE);
            intent.addFlags(1073741824);
            try {
                this.mContext.sendBroadcastAsUser(intent, UserHandle.CURRENT);
            } catch (SecurityException e) {
                StringBuilder sb = new StringBuilder();
                sb.append("Cannot send event for intent= ");
                sb.append(intent);
                Log.w("DLIndicator", sb.toString(), e);
            }
        }
    }

    public void onDozingChanged(boolean z) {
        this.mDozing = z;
        updateVisibility();
        updateLiveRegionIfNeeded();
        if (!this.mDozing) {
            this.mShowPromo = false;
        } else {
            showPromoInner();
        }
    }

    public void onViewDetachedFromWindow(View view) {
        view.removeOnAttachStateChangeListener(this);
        this.mIconViewsValidated = false;
        this.mDockedTopIcon = null;
    }

    public void setShowing(boolean z) {
        this.mTopIconShowing = z;
        updateVisibility();
    }

    public void setDocking(boolean z) {
        this.mDocking = z;
        if (!z) {
            this.mTopIconShowing = false;
            this.mShowPromo = false;
        }
        updateVisibility();
        updateLiveRegionIfNeeded();
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void initializeIconViews() {
        NotificationShadeWindowView notificationShadeWindowView = this.mStatusBar.getNotificationShadeWindowView();
        ImageView imageView = (ImageView) notificationShadeWindowView.findViewById(C2011R$id.docked_top_icon);
        this.mDockedTopIcon = imageView;
        imageView.setImageResource(C2010R$drawable.ic_assistant_logo);
        this.mDockedTopIcon.setContentDescription(this.mContext.getString(C2017R$string.accessibility_assistant_poodle));
        this.mDockedTopIcon.setTooltipText(this.mContext.getString(C2017R$string.accessibility_assistant_poodle));
        this.mDockedTopIcon.setOnClickListener(this);
        this.mDockPromo = (FrameLayout) notificationShadeWindowView.findViewById(C2011R$id.dock_promo);
        notificationShadeWindowView.findViewById(C2011R$id.ambient_indication).addOnAttachStateChangeListener(this);
        this.mKeyguardIndicationTextView = (KeyguardIndicationTextView) notificationShadeWindowView.findViewById(C2011R$id.keyguard_indication_text);
        this.mIconViewsValidated = true;
    }

    public void showPromo(ResultReceiver resultReceiver) {
        this.mShowPromoTimes = 0;
        this.mShowPromo = true;
        if (!this.mDozing || !this.mDocking) {
            resultReceiver.send(1, null);
            return;
        }
        showPromoInner();
        resultReceiver.send(0, null);
    }

    /* access modifiers changed from: private */
    public void showPromoInner() {
        if (this.mDozing && this.mDocking && this.mShowPromo) {
            this.mDockPromo.setVisibility(0);
            this.mDockPromo.startAnimation(this.mShowPromoAnimation);
            this.mShowPromoTimes++;
        }
    }

    /* access modifiers changed from: private */
    public void hidePromo() {
        if (this.mDozing && this.mDocking) {
            this.mDockPromo.startAnimation(this.mHidePromoAnimation);
        }
    }

    private void updateVisibility() {
        if (!this.mIconViewsValidated) {
            initializeIconViews();
        }
        if (!this.mDozing || !this.mDocking) {
            this.mDockPromo.setVisibility(8);
            this.mDockedTopIcon.setVisibility(8);
        } else if (!this.mTopIconShowing) {
            this.mDockedTopIcon.setVisibility(8);
        } else {
            this.mDockedTopIcon.setVisibility(0);
        }
    }

    private void updateLiveRegionIfNeeded() {
        int accessibilityLiveRegion = this.mKeyguardIndicationTextView.getAccessibilityLiveRegion();
        if (!this.mDozing || !this.mDocking) {
            if (accessibilityLiveRegion != 1) {
                this.mKeyguardIndicationTextView.setAccessibilityLiveRegion(1);
            }
            return;
        }
        this.mKeyguardIndicationTextView.removeCallbacks(this.mDisableLiveRegionRunnable);
        this.mKeyguardIndicationTextView.postDelayed(this.mDisableLiveRegionRunnable, getRecommendedTimeoutMillis(KEYGUARD_INDICATION_TIMEOUT_MILLIS));
    }

    /* access modifiers changed from: private */
    public void disableLiveRegion() {
        if (this.mDocking && this.mDozing) {
            this.mKeyguardIndicationTextView.setAccessibilityLiveRegion(0);
        }
    }

    private long getRecommendedTimeoutMillis(long j) {
        AccessibilityManager accessibilityManager = this.mAccessibilityManager;
        return accessibilityManager == null ? j : (long) accessibilityManager.getRecommendedTimeoutMillis(Math.toIntExact(j), 2);
    }
}
