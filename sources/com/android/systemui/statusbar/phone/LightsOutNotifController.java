package com.android.systemui.statusbar.phone;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.internal.view.AppearanceRegion;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.CommandQueue.Callbacks;
import com.android.systemui.statusbar.notification.NotificationEntryListener;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;

public class LightsOutNotifController {
    @VisibleForTesting
    int mAppearance;
    private final Callbacks mCallback = new Callbacks() {
        public void onSystemBarAppearanceChanged(int i, int i2, AppearanceRegion[] appearanceRegionArr, boolean z) {
            if (i == LightsOutNotifController.this.mDisplayId) {
                LightsOutNotifController lightsOutNotifController = LightsOutNotifController.this;
                lightsOutNotifController.mAppearance = i2;
                lightsOutNotifController.updateLightsOutView();
            }
        }
    };
    private final CommandQueue mCommandQueue;
    /* access modifiers changed from: private */
    public int mDisplayId;
    private final NotificationEntryListener mEntryListener = new NotificationEntryListener() {
        public void onNotificationAdded(NotificationEntry notificationEntry) {
            LightsOutNotifController.this.updateLightsOutView();
        }

        public void onPostEntryUpdated(NotificationEntry notificationEntry) {
            LightsOutNotifController.this.updateLightsOutView();
        }

        public void onEntryRemoved(NotificationEntry notificationEntry, NotificationVisibility notificationVisibility, boolean z, int i) {
            LightsOutNotifController.this.updateLightsOutView();
        }
    };
    private final NotificationEntryManager mEntryManager;
    /* access modifiers changed from: private */
    public View mLightsOutNotifView;
    private final WindowManager mWindowManager;

    LightsOutNotifController(WindowManager windowManager, NotificationEntryManager notificationEntryManager, CommandQueue commandQueue) {
        this.mWindowManager = windowManager;
        this.mEntryManager = notificationEntryManager;
        this.mCommandQueue = commandQueue;
    }

    /* access modifiers changed from: 0000 */
    public void setLightsOutNotifView(View view) {
        destroy();
        this.mLightsOutNotifView = view;
        if (view != null) {
            view.setVisibility(8);
            this.mLightsOutNotifView.setAlpha(0.0f);
            init();
        }
    }

    private void destroy() {
        this.mEntryManager.removeNotificationEntryListener(this.mEntryListener);
        this.mCommandQueue.removeCallback(this.mCallback);
    }

    private void init() {
        this.mDisplayId = this.mWindowManager.getDefaultDisplay().getDisplayId();
        this.mEntryManager.addNotificationEntryListener(this.mEntryListener);
        this.mCommandQueue.addCallback(this.mCallback);
        updateLightsOutView();
    }

    private boolean hasActiveNotifications() {
        return this.mEntryManager.hasActiveNotifications();
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void updateLightsOutView() {
        if (this.mLightsOutNotifView != null) {
            final boolean shouldShowDot = shouldShowDot();
            if (shouldShowDot != isShowingDot()) {
                float f = 0.0f;
                if (shouldShowDot) {
                    this.mLightsOutNotifView.setAlpha(0.0f);
                    this.mLightsOutNotifView.setVisibility(0);
                }
                ViewPropertyAnimator animate = this.mLightsOutNotifView.animate();
                if (shouldShowDot) {
                    f = 1.0f;
                }
                animate.alpha(f).setDuration(shouldShowDot ? 750 : 250).setInterpolator(new AccelerateInterpolator(2.0f)).setListener(new AnimatorListenerAdapter() {
                    public void onAnimationEnd(Animator animator) {
                        LightsOutNotifController.this.mLightsOutNotifView.setAlpha(shouldShowDot ? 1.0f : 0.0f);
                        LightsOutNotifController.this.mLightsOutNotifView.setVisibility(shouldShowDot ? 0 : 8);
                    }
                }).start();
            }
        }
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public boolean isShowingDot() {
        return this.mLightsOutNotifView.getVisibility() == 0 && this.mLightsOutNotifView.getAlpha() == 1.0f;
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public boolean shouldShowDot() {
        return hasActiveNotifications() && areLightsOut();
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public boolean areLightsOut() {
        return (this.mAppearance & 4) != 0;
    }
}
