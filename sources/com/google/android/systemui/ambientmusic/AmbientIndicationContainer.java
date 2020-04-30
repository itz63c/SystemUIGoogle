package com.google.android.systemui.ambientmusic;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadata;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.MathUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.widget.TextView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.AutoReinflateContainer;
import com.android.systemui.AutoReinflateContainer.InflateListener;
import com.android.systemui.C2004R$anim;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2011R$id;
import com.android.systemui.Dependency;
import com.android.systemui.Interpolators;
import com.android.systemui.doze.DozeReceiver;
import com.android.systemui.doze.util.BurnInHelperKt;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.NotificationMediaManager.MediaListener;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.util.wakelock.DelayedWakeLock;
import com.android.systemui.util.wakelock.WakeLock;

public class AmbientIndicationContainer extends AutoReinflateContainer implements DozeReceiver, OnClickListener, StateListener, MediaListener {
    private int mAmbientIndicationIconSize;
    private Drawable mAmbientMusicAnimation;
    private PendingIntent mAmbientMusicIntent;
    private CharSequence mAmbientMusicText;
    private boolean mAmbientSkipUnlock;
    private int mBurnInPreventionOffset;
    private float mDozeAmount;
    private boolean mDozing;
    private int mDrawablePadding;
    private final Handler mHandler;
    private final Rect mIconBounds = new Rect();
    private int mMediaPlaybackState;
    private boolean mNotificationsHidden;
    private StatusBar mStatusBar;
    private TextView mText;
    private int mTextColor;
    /* access modifiers changed from: private */
    public ValueAnimator mTextColorAnimator;
    /* access modifiers changed from: private */
    public final WakeLock mWakeLock;

    static /* synthetic */ void lambda$updatePill$2() {
    }

    static /* synthetic */ void lambda$updatePill$3() {
    }

    public void onStateChanged(int i) {
    }

    public AmbientIndicationContainer(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        Handler handler = new Handler(Looper.getMainLooper());
        this.mHandler = handler;
        this.mWakeLock = createWakeLock(this.mContext, handler);
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public WakeLock createWakeLock(Context context, Handler handler) {
        return new DelayedWakeLock(handler, WakeLock.createPartial(context, "AmbientIndication"));
    }

    public void initializeView(StatusBar statusBar) {
        this.mStatusBar = statusBar;
        addInflateListener(new InflateListener() {
            public final void onInflated(View view) {
                AmbientIndicationContainer.this.lambda$initializeView$0$AmbientIndicationContainer(view);
            }
        });
        addOnLayoutChangeListener(new OnLayoutChangeListener() {
            public final void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                AmbientIndicationContainer.this.lambda$initializeView$1$AmbientIndicationContainer(view, i, i2, i3, i4, i5, i6, i7, i8);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$initializeView$0 */
    public /* synthetic */ void lambda$initializeView$0$AmbientIndicationContainer(View view) {
        this.mText = (TextView) findViewById(C2011R$id.ambient_indication_text);
        this.mAmbientMusicAnimation = getResources().getDrawable(C2004R$anim.audioanim_animation, this.mContext.getTheme());
        this.mTextColor = this.mText.getCurrentTextColor();
        this.mAmbientIndicationIconSize = getResources().getDimensionPixelSize(C2009R$dimen.ambient_indication_icon_size);
        this.mBurnInPreventionOffset = getResources().getDimensionPixelSize(C2009R$dimen.default_burn_in_prevention_offset);
        this.mDrawablePadding = this.mText.getCompoundDrawablePadding();
        updateColors();
        updatePill();
        this.mText.setOnClickListener(this);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$initializeView$1 */
    public /* synthetic */ void lambda$initializeView$1$AmbientIndicationContainer(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        updateBottomPadding();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        ((StatusBarStateController) Dependency.get(StatusBarStateController.class)).addCallback(this);
        ((NotificationMediaManager) Dependency.get(NotificationMediaManager.class)).addCallback(this);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ((StatusBarStateController) Dependency.get(StatusBarStateController.class)).removeCallback(this);
        ((NotificationMediaManager) Dependency.get(NotificationMediaManager.class)).removeCallback(this);
        this.mMediaPlaybackState = 0;
    }

    public void setAmbientMusic(CharSequence charSequence, PendingIntent pendingIntent, boolean z) {
        this.mAmbientMusicText = charSequence;
        this.mAmbientMusicIntent = pendingIntent;
        this.mAmbientSkipUnlock = z;
        updatePill();
    }

    private void updatePill() {
        CharSequence charSequence = this.mAmbientMusicText;
        Drawable drawable = this.mAmbientMusicAnimation;
        boolean z = true;
        int i = 0;
        boolean z2 = charSequence != null && charSequence.length() == 0;
        this.mText.setClickable(this.mAmbientMusicIntent != null);
        this.mText.setText(charSequence);
        this.mText.setContentDescription(charSequence);
        if (drawable != null) {
            this.mIconBounds.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            MathUtils.fitRect(this.mIconBounds, this.mAmbientIndicationIconSize);
            drawable.setBounds(this.mIconBounds);
        }
        Drawable drawable2 = isLayoutRtl() ? null : drawable;
        this.mText.setCompoundDrawables(drawable2, null, drawable2 == null ? drawable : null, null);
        this.mText.setCompoundDrawablePadding(z2 ? 0 : this.mDrawablePadding);
        boolean z3 = (!TextUtils.isEmpty(charSequence) || z2) && !this.mNotificationsHidden;
        if (this.mText.getVisibility() != 0) {
            z = false;
        }
        TextView textView = this.mText;
        if (!z3) {
            i = 8;
        }
        textView.setVisibility(i);
        if (!z3) {
            this.mText.animate().cancel();
            if (drawable instanceof AnimatedVectorDrawable) {
                ((AnimatedVectorDrawable) drawable).reset();
            }
            this.mHandler.post(this.mWakeLock.wrap($$Lambda$AmbientIndicationContainer$KVRCJYirK1TYPyrHOw9GLDna6JI.INSTANCE));
        } else if (!z) {
            this.mWakeLock.acquire("AmbientIndication");
            if (drawable instanceof AnimatedVectorDrawable) {
                ((AnimatedVectorDrawable) drawable).start();
            }
            TextView textView2 = this.mText;
            textView2.setTranslationY((float) (textView2.getHeight() / 2));
            this.mText.setAlpha(0.0f);
            this.mText.animate().withLayer().alpha(1.0f).translationY(0.0f).setStartDelay(150).setDuration(100).setListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    AmbientIndicationContainer.this.mWakeLock.release("AmbientIndication");
                }
            }).setInterpolator(Interpolators.DECELERATE_QUINT).start();
        } else {
            this.mHandler.post(this.mWakeLock.wrap($$Lambda$AmbientIndicationContainer$20zsf8sDdIOT7QAgQCZNUNsXjE.INSTANCE));
        }
        updateBottomPadding();
    }

    private void updateBottomPadding() {
        this.mStatusBar.getPanelController().setAmbientIndicationBottomPadding(this.mText.getVisibility() == 0 ? this.mStatusBar.getNotificationScrollLayout().getBottom() - getTop() : 0);
    }

    public void hideAmbientMusic() {
        setAmbientMusic(null, null, false);
    }

    public void onClick(View view) {
        if (this.mAmbientMusicIntent != null) {
            this.mStatusBar.wakeUpIfDozing(SystemClock.uptimeMillis(), this.mText, "AMBIENT_MUSIC_CLICK");
            if (this.mAmbientSkipUnlock) {
                sendBroadcastWithoutDismissingKeyguard(this.mAmbientMusicIntent);
            } else {
                this.mStatusBar.lambda$postStartActivityDismissingKeyguard$23(this.mAmbientMusicIntent);
            }
        }
    }

    public void onDozingChanged(boolean z) {
        this.mDozing = z;
        this.mText.setEnabled(!z);
        updateColors();
        updateBurnInOffsets();
    }

    public void dozeTimeTick() {
        updatePill();
        updateBurnInOffsets();
    }

    private void updateBurnInOffsets() {
        int burnInOffset = BurnInHelperKt.getBurnInOffset(this.mBurnInPreventionOffset * 2, true);
        int i = this.mBurnInPreventionOffset;
        float f = (float) (burnInOffset - i);
        float burnInOffset2 = (float) (BurnInHelperKt.getBurnInOffset(i * 2, false) - this.mBurnInPreventionOffset);
        setTranslationX(f * this.mDozeAmount);
        setTranslationY(burnInOffset2 * this.mDozeAmount);
    }

    private void updateColors() {
        ValueAnimator valueAnimator = this.mTextColorAnimator;
        if (valueAnimator != null && valueAnimator.isRunning()) {
            this.mTextColorAnimator.cancel();
        }
        int defaultColor = this.mText.getTextColors().getDefaultColor();
        int i = this.mDozing ? -1 : this.mTextColor;
        if (defaultColor != i) {
            ValueAnimator ofArgb = ValueAnimator.ofArgb(new int[]{defaultColor, i});
            this.mTextColorAnimator = ofArgb;
            ofArgb.setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN);
            this.mTextColorAnimator.setDuration(500);
            this.mTextColorAnimator.addUpdateListener(new AnimatorUpdateListener() {
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    AmbientIndicationContainer.this.lambda$updateColors$4$AmbientIndicationContainer(valueAnimator);
                }
            });
            this.mTextColorAnimator.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    AmbientIndicationContainer.this.mTextColorAnimator = null;
                }
            });
            this.mTextColorAnimator.start();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateColors$4 */
    public /* synthetic */ void lambda$updateColors$4$AmbientIndicationContainer(ValueAnimator valueAnimator) {
        int intValue = ((Integer) valueAnimator.getAnimatedValue()).intValue();
        this.mText.setTextColor(intValue);
        this.mText.setCompoundDrawableTintList(ColorStateList.valueOf(intValue));
    }

    public void onDozeAmountChanged(float f, float f2) {
        this.mDozeAmount = f2;
        updateBurnInOffsets();
    }

    private void sendBroadcastWithoutDismissingKeyguard(PendingIntent pendingIntent) {
        if (!pendingIntent.isActivity()) {
            try {
                pendingIntent.send();
            } catch (CanceledException e) {
                StringBuilder sb = new StringBuilder();
                sb.append("Sending intent failed: ");
                sb.append(e);
                Log.w("AmbientIndication", sb.toString());
            }
        }
    }

    public void onMetadataOrStateChanged(MediaMetadata mediaMetadata, int i) {
        if (this.mMediaPlaybackState != i) {
            this.mMediaPlaybackState = i;
            if (isMediaPlaying()) {
                hideAmbientMusic();
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public boolean isMediaPlaying() {
        return NotificationMediaManager.isPlayingState(this.mMediaPlaybackState);
    }
}
