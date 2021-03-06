package com.android.systemui.statusbar.phone;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.AlarmManager;
import android.app.AlarmManager.OnAlarmListener;
import android.graphics.Color;
import android.os.Handler;
import android.os.Trace;
import android.util.Log;
import android.util.MathUtils;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.colorextraction.ColorExtractor;
import com.android.internal.colorextraction.ColorExtractor.GradientColors;
import com.android.internal.colorextraction.ColorExtractor.OnColorsChangedListener;
import com.android.internal.graphics.ColorUtils;
import com.android.internal.util.function.TriConsumer;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.C2011R$id;
import com.android.systemui.DejankUtils;
import com.android.systemui.Dumpable;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.dock.DockManager;
import com.android.systemui.statusbar.BlurUtils;
import com.android.systemui.statusbar.ScrimView;
import com.android.systemui.statusbar.notification.stack.ViewState;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.util.AlarmTimeout;
import com.android.systemui.util.wakelock.DelayedWakeLock.Builder;
import com.android.systemui.util.wakelock.WakeLock;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.function.Consumer;

public class ScrimController implements OnPreDrawListener, OnColorsChangedListener, Dumpable {
    private static final boolean DEBUG = Log.isLoggable("ScrimController", 3);
    private static final int TAG_END_ALPHA = C2011R$id.scrim_alpha_end;
    static final int TAG_KEY_ANIM = C2011R$id.scrim;
    private static final int TAG_START_ALPHA = C2011R$id.scrim_alpha_start;
    private boolean mAnimateChange;
    private long mAnimationDelay;
    private long mAnimationDuration = -1;
    private AnimatorListener mAnimatorListener;
    private float mBehindAlpha = -1.0f;
    private int mBehindTint;
    private boolean mBlankScreen;
    private Runnable mBlankingTransitionRunnable;
    private float mBubbleAlpha = -1.0f;
    private int mBubbleTint;
    /* access modifiers changed from: private */
    public Callback mCallback;
    private final SysuiColorExtractor mColorExtractor;
    private GradientColors mColors;
    private boolean mDarkenWhileDragging;
    private final float mDefaultScrimAlpha;
    private final DockManager mDockManager;
    private final DozeParameters mDozeParameters;
    private boolean mExpansionAffectsAlpha = true;
    private float mExpansionFraction = 1.0f;
    private final Handler mHandler;
    private float mInFrontAlpha = -1.0f;
    private int mInFrontTint;
    private final Interpolator mInterpolator = new DecelerateInterpolator();
    private boolean mKeyguardOccluded;
    private final KeyguardStateController mKeyguardStateController;
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private final KeyguardVisibilityCallback mKeyguardVisibilityCallback;
    /* access modifiers changed from: private */
    public boolean mNeedsDrawableColorUpdate;
    private Runnable mPendingFrameCallback;
    private boolean mScreenBlankingCallbackCalled;
    private boolean mScreenOn;
    private ScrimView mScrimBehind;
    private float mScrimBehindAlphaKeyguard = 0.2f;
    private ScrimView mScrimForBubble;
    private ScrimView mScrimInFront;
    private final TriConsumer<ScrimState, Float, GradientColors> mScrimStateListener;
    private Consumer<Integer> mScrimVisibleListener;
    private int mScrimsVisibility;
    private ScrimState mState = ScrimState.UNINITIALIZED;
    private final AlarmTimeout mTimeTicker;
    private boolean mTracking;
    private boolean mUpdatePending;
    private final WakeLock mWakeLock;
    private boolean mWakeLockHeld;
    private boolean mWallpaperSupportsAmbientMode;
    private boolean mWallpaperVisibilityTimedOut;

    public interface Callback {
        void onCancelled() {
        }

        void onDisplayBlanked() {
        }

        void onFinished() {
        }

        void onStart() {
        }
    }

    private class KeyguardVisibilityCallback extends KeyguardUpdateMonitorCallback {
        private KeyguardVisibilityCallback() {
        }

        public void onKeyguardVisibilityChanged(boolean z) {
            ScrimController.this.mNeedsDrawableColorUpdate = true;
            ScrimController.this.scheduleUpdate();
        }
    }

    public void setCurrentUser(int i) {
    }

    public ScrimController(LightBarController lightBarController, DozeParameters dozeParameters, AlarmManager alarmManager, final KeyguardStateController keyguardStateController, Builder builder, Handler handler, KeyguardUpdateMonitor keyguardUpdateMonitor, SysuiColorExtractor sysuiColorExtractor, DockManager dockManager, BlurUtils blurUtils) {
        Objects.requireNonNull(lightBarController);
        this.mScrimStateListener = new TriConsumer() {
            public final void accept(Object obj, Object obj2, Object obj3) {
                LightBarController.this.setScrimState((ScrimState) obj, ((Float) obj2).floatValue(), (GradientColors) obj3);
            }
        };
        this.mDefaultScrimAlpha = blurUtils.supportsBlursOnWindows() ? 0.54f : 0.75f;
        this.mKeyguardStateController = keyguardStateController;
        this.mDarkenWhileDragging = !keyguardStateController.canDismissLockScreen();
        this.mKeyguardUpdateMonitor = keyguardUpdateMonitor;
        this.mKeyguardVisibilityCallback = new KeyguardVisibilityCallback();
        this.mHandler = handler;
        this.mTimeTicker = new AlarmTimeout(alarmManager, new OnAlarmListener() {
            public final void onAlarm() {
                ScrimController.this.onHideWallpaperTimeout();
            }
        }, "hide_aod_wallpaper", this.mHandler);
        builder.setHandler(this.mHandler);
        builder.setTag("Scrims");
        this.mWakeLock = builder.build();
        this.mDozeParameters = dozeParameters;
        this.mDockManager = dockManager;
        keyguardStateController.addCallback(new com.android.systemui.statusbar.policy.KeyguardStateController.Callback() {
            public void onKeyguardFadingAwayChanged() {
                ScrimController.this.setKeyguardFadingAway(keyguardStateController.isKeyguardFadingAway(), keyguardStateController.getKeyguardFadingAwayDuration());
            }
        });
        this.mColorExtractor = sysuiColorExtractor;
        sysuiColorExtractor.addOnColorsChangedListener(this);
        this.mColors = this.mColorExtractor.getNeutralColors();
        this.mNeedsDrawableColorUpdate = true;
    }

    public void attachViews(ScrimView scrimView, ScrimView scrimView2, ScrimView scrimView3) {
        this.mScrimBehind = scrimView;
        this.mScrimInFront = scrimView2;
        this.mScrimForBubble = scrimView3;
        ScrimState[] values = ScrimState.values();
        for (int i = 0; i < values.length; i++) {
            values[i].init(this.mScrimInFront, this.mScrimBehind, this.mScrimForBubble, this.mDozeParameters, this.mDockManager);
            values[i].setScrimBehindAlphaKeyguard(this.mScrimBehindAlphaKeyguard);
            values[i].setDefaultScrimAlpha(this.mDefaultScrimAlpha);
        }
        this.mScrimBehind.setDefaultFocusHighlightEnabled(false);
        this.mScrimInFront.setDefaultFocusHighlightEnabled(false);
        this.mScrimForBubble.setDefaultFocusHighlightEnabled(false);
        updateScrims();
        this.mKeyguardUpdateMonitor.registerCallback(this.mKeyguardVisibilityCallback);
    }

    /* access modifiers changed from: 0000 */
    public void setScrimVisibleListener(Consumer<Integer> consumer) {
        this.mScrimVisibleListener = consumer;
    }

    public void transitionTo(ScrimState scrimState) {
        transitionTo(scrimState, null);
    }

    public void transitionTo(ScrimState scrimState, Callback callback) {
        if (scrimState == this.mState) {
            if (!(callback == null || this.mCallback == callback)) {
                callback.onFinished();
            }
            return;
        }
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("State changed to: ");
            sb.append(scrimState);
            Log.d("ScrimController", sb.toString());
        }
        if (scrimState != ScrimState.UNINITIALIZED) {
            ScrimState scrimState2 = this.mState;
            this.mState = scrimState;
            Trace.traceCounter(4096, "scrim_state", scrimState.ordinal());
            Callback callback2 = this.mCallback;
            if (callback2 != null) {
                callback2.onCancelled();
            }
            this.mCallback = callback;
            scrimState.prepare(scrimState2);
            this.mScreenBlankingCallbackCalled = false;
            this.mAnimationDelay = 0;
            this.mBlankScreen = scrimState.getBlanksScreen();
            this.mAnimateChange = scrimState.getAnimateChange();
            this.mAnimationDuration = scrimState.getAnimationDuration();
            this.mInFrontTint = scrimState.getFrontTint();
            this.mBehindTint = scrimState.getBehindTint();
            this.mBubbleTint = scrimState.getBubbleTint();
            this.mInFrontAlpha = scrimState.getFrontAlpha();
            this.mBehindAlpha = scrimState.getBehindAlpha();
            this.mBubbleAlpha = scrimState.getBubbleAlpha();
            if (Float.isNaN(this.mBehindAlpha) || Float.isNaN(this.mInFrontAlpha)) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Scrim opacity is NaN for state: ");
                sb2.append(scrimState);
                sb2.append(", front: ");
                sb2.append(this.mInFrontAlpha);
                sb2.append(", back: ");
                sb2.append(this.mBehindAlpha);
                throw new IllegalStateException(sb2.toString());
            }
            applyExpansionToAlpha();
            boolean z = true;
            this.mScrimInFront.setFocusable(!scrimState.isLowPowerState());
            this.mScrimBehind.setFocusable(!scrimState.isLowPowerState());
            Runnable runnable = this.mPendingFrameCallback;
            if (runnable != null) {
                this.mScrimBehind.removeCallbacks(runnable);
                this.mPendingFrameCallback = null;
            }
            if (this.mHandler.hasCallbacks(this.mBlankingTransitionRunnable)) {
                this.mHandler.removeCallbacks(this.mBlankingTransitionRunnable);
                this.mBlankingTransitionRunnable = null;
            }
            if (scrimState == ScrimState.BRIGHTNESS_MIRROR) {
                z = false;
            }
            this.mNeedsDrawableColorUpdate = z;
            if (this.mState.isLowPowerState()) {
                holdWakeLock();
            }
            this.mWallpaperVisibilityTimedOut = false;
            if (shouldFadeAwayWallpaper()) {
                DejankUtils.postAfterTraversal(new Runnable() {
                    public final void run() {
                        ScrimController.this.lambda$transitionTo$0$ScrimController();
                    }
                });
            } else {
                AlarmTimeout alarmTimeout = this.mTimeTicker;
                Objects.requireNonNull(alarmTimeout);
                DejankUtils.postAfterTraversal(new Runnable() {
                    public final void run() {
                        AlarmTimeout.this.cancel();
                    }
                });
            }
            if (this.mKeyguardUpdateMonitor.needsSlowUnlockTransition() && this.mState == ScrimState.UNLOCKED) {
                this.mScrimInFront.postOnAnimationDelayed(new Runnable() {
                    public final void run() {
                        ScrimController.this.scheduleUpdate();
                    }
                }, 16);
                this.mAnimationDelay = 100;
            } else if ((this.mDozeParameters.getAlwaysOn() || scrimState2 != ScrimState.AOD) && (this.mState != ScrimState.AOD || this.mDozeParameters.getDisplayNeedsBlanking())) {
                scheduleUpdate();
            } else {
                onPreDraw();
            }
            dispatchScrimState(this.mScrimBehind.getViewAlpha());
            return;
        }
        throw new IllegalArgumentException("Cannot change to UNINITIALIZED.");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$transitionTo$0 */
    public /* synthetic */ void lambda$transitionTo$0$ScrimController() {
        this.mTimeTicker.schedule(this.mDozeParameters.getWallpaperAodDuration(), 1);
    }

    private boolean shouldFadeAwayWallpaper() {
        if (this.mWallpaperSupportsAmbientMode && this.mState == ScrimState.AOD && (this.mDozeParameters.getAlwaysOn() || this.mDockManager.isDocked())) {
            return true;
        }
        return false;
    }

    public ScrimState getState() {
        return this.mState;
    }

    /* access modifiers changed from: protected */
    public void setScrimBehindValues(float f) {
        this.mScrimBehindAlphaKeyguard = f;
        ScrimState[] values = ScrimState.values();
        for (ScrimState scrimBehindAlphaKeyguard : values) {
            scrimBehindAlphaKeyguard.setScrimBehindAlphaKeyguard(f);
        }
        scheduleUpdate();
    }

    public void onTrackingStarted() {
        this.mTracking = true;
        this.mDarkenWhileDragging = true ^ this.mKeyguardStateController.canDismissLockScreen();
    }

    public void onExpandingFinished() {
        this.mTracking = false;
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void onHideWallpaperTimeout() {
        ScrimState scrimState = this.mState;
        if (scrimState == ScrimState.AOD || scrimState == ScrimState.PULSING) {
            holdWakeLock();
            this.mWallpaperVisibilityTimedOut = true;
            this.mAnimateChange = true;
            this.mAnimationDuration = this.mDozeParameters.getWallpaperFadeOutDuration();
            scheduleUpdate();
        }
    }

    private void holdWakeLock() {
        if (!this.mWakeLockHeld) {
            WakeLock wakeLock = this.mWakeLock;
            String str = "ScrimController";
            if (wakeLock != null) {
                this.mWakeLockHeld = true;
                wakeLock.acquire(str);
                return;
            }
            Log.w(str, "Cannot hold wake lock, it has not been set yet");
        }
    }

    public void setPanelExpansion(float f) {
        if (!Float.isNaN(f)) {
            if (this.mExpansionFraction != f) {
                this.mExpansionFraction = f;
                ScrimState scrimState = this.mState;
                if ((scrimState == ScrimState.UNLOCKED || scrimState == ScrimState.KEYGUARD || scrimState == ScrimState.PULSING || scrimState == ScrimState.BUBBLE_EXPANDED) && this.mExpansionAffectsAlpha) {
                    applyExpansionToAlpha();
                    if (!this.mUpdatePending) {
                        setOrAdaptCurrentAnimation(this.mScrimBehind);
                        setOrAdaptCurrentAnimation(this.mScrimInFront);
                        setOrAdaptCurrentAnimation(this.mScrimForBubble);
                        dispatchScrimState(this.mScrimBehind.getViewAlpha());
                        if (this.mWallpaperVisibilityTimedOut) {
                            this.mWallpaperVisibilityTimedOut = false;
                            DejankUtils.postAfterTraversal(new Runnable() {
                                public final void run() {
                                    ScrimController.this.lambda$setPanelExpansion$1$ScrimController();
                                }
                            });
                        }
                    } else {
                        return;
                    }
                }
            }
            return;
        }
        throw new IllegalArgumentException("Fraction should not be NaN");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setPanelExpansion$1 */
    public /* synthetic */ void lambda$setPanelExpansion$1$ScrimController() {
        this.mTimeTicker.schedule(this.mDozeParameters.getWallpaperAodDuration(), 1);
    }

    private void setOrAdaptCurrentAnimation(View view) {
        float currentScrimAlpha = getCurrentScrimAlpha(view);
        if (isAnimating(view)) {
            ValueAnimator valueAnimator = (ValueAnimator) view.getTag(TAG_KEY_ANIM);
            view.setTag(TAG_START_ALPHA, Float.valueOf(((Float) view.getTag(TAG_START_ALPHA)).floatValue() + (currentScrimAlpha - ((Float) view.getTag(TAG_END_ALPHA)).floatValue())));
            view.setTag(TAG_END_ALPHA, Float.valueOf(currentScrimAlpha));
            valueAnimator.setCurrentPlayTime(valueAnimator.getCurrentPlayTime());
            return;
        }
        updateScrimColor(view, currentScrimAlpha, getCurrentScrimTint(view));
    }

    private void applyExpansionToAlpha() {
        if (this.mExpansionAffectsAlpha) {
            ScrimState scrimState = this.mState;
            if (scrimState == ScrimState.UNLOCKED || scrimState == ScrimState.BUBBLE_EXPANDED) {
                this.mBehindAlpha = ((float) Math.pow((double) getInterpolatedFraction(), 0.800000011920929d)) * this.mDefaultScrimAlpha;
                this.mInFrontAlpha = 0.0f;
            } else if (scrimState == ScrimState.KEYGUARD || scrimState == ScrimState.PULSING) {
                float interpolatedFraction = getInterpolatedFraction();
                float behindAlpha = this.mState.getBehindAlpha();
                if (this.mDarkenWhileDragging) {
                    this.mBehindAlpha = MathUtils.lerp(this.mDefaultScrimAlpha, behindAlpha, interpolatedFraction);
                    this.mInFrontAlpha = this.mState.getFrontAlpha();
                } else {
                    this.mBehindAlpha = MathUtils.lerp(0.0f, behindAlpha, interpolatedFraction);
                    this.mInFrontAlpha = this.mState.getFrontAlpha();
                }
                this.mBehindTint = ColorUtils.blendARGB(ScrimState.BOUNCER.getBehindTint(), this.mState.getBehindTint(), interpolatedFraction);
            }
            if (Float.isNaN(this.mBehindAlpha) || Float.isNaN(this.mInFrontAlpha)) {
                StringBuilder sb = new StringBuilder();
                sb.append("Scrim opacity is NaN for state: ");
                sb.append(this.mState);
                sb.append(", front: ");
                sb.append(this.mInFrontAlpha);
                sb.append(", back: ");
                sb.append(this.mBehindAlpha);
                throw new IllegalStateException(sb.toString());
            }
        }
    }

    public void setAodFrontScrimAlpha(float f) {
        if (this.mInFrontAlpha != f && shouldUpdateFrontScrimAlpha()) {
            this.mInFrontAlpha = f;
            updateScrims();
        }
        ScrimState.AOD.setAodFrontScrimAlpha(f);
        ScrimState.PULSING.setAodFrontScrimAlpha(f);
    }

    private boolean shouldUpdateFrontScrimAlpha() {
        if ((this.mState != ScrimState.AOD || (!this.mDozeParameters.getAlwaysOn() && !this.mDockManager.isDocked())) && this.mState != ScrimState.PULSING) {
            return false;
        }
        return true;
    }

    public void setWakeLockScreenSensorActive(boolean z) {
        for (ScrimState wakeLockScreenSensorActive : ScrimState.values()) {
            wakeLockScreenSensorActive.setWakeLockScreenSensorActive(z);
        }
        ScrimState scrimState = this.mState;
        if (scrimState == ScrimState.PULSING) {
            float behindAlpha = scrimState.getBehindAlpha();
            if (this.mBehindAlpha != behindAlpha) {
                this.mBehindAlpha = behindAlpha;
                if (!Float.isNaN(behindAlpha)) {
                    updateScrims();
                    return;
                }
                StringBuilder sb = new StringBuilder();
                sb.append("Scrim opacity is NaN for state: ");
                sb.append(this.mState);
                sb.append(", back: ");
                sb.append(this.mBehindAlpha);
                throw new IllegalStateException(sb.toString());
            }
        }
    }

    /* access modifiers changed from: protected */
    public void scheduleUpdate() {
        if (!this.mUpdatePending) {
            ScrimView scrimView = this.mScrimBehind;
            if (scrimView != null) {
                scrimView.invalidate();
                this.mScrimBehind.getViewTreeObserver().addOnPreDrawListener(this);
                this.mUpdatePending = true;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void updateScrims() {
        boolean z = true;
        if (this.mNeedsDrawableColorUpdate) {
            this.mNeedsDrawableColorUpdate = false;
            boolean z2 = this.mScrimInFront.getViewAlpha() != 0.0f && !this.mBlankScreen;
            boolean z3 = this.mScrimBehind.getViewAlpha() != 0.0f && !this.mBlankScreen;
            boolean z4 = this.mScrimForBubble.getViewAlpha() != 0.0f && !this.mBlankScreen;
            this.mScrimInFront.setColors(this.mColors, z2);
            this.mScrimBehind.setColors(this.mColors, z3);
            this.mScrimForBubble.setColors(this.mColors, z4);
            ColorUtils.calculateMinimumBackgroundAlpha(this.mColors.supportsDarkText() ? -16777216 : -1, this.mColors.getMainColor(), 4.5f);
            dispatchScrimState(this.mScrimBehind.getViewAlpha());
        }
        ScrimState scrimState = this.mState;
        boolean z5 = (scrimState == ScrimState.AOD || scrimState == ScrimState.PULSING) && this.mWallpaperVisibilityTimedOut;
        ScrimState scrimState2 = this.mState;
        if (!(scrimState2 == ScrimState.PULSING || scrimState2 == ScrimState.AOD) || !this.mKeyguardOccluded) {
            z = false;
        }
        if (z5 || z) {
            this.mBehindAlpha = 1.0f;
        }
        setScrimAlpha(this.mScrimInFront, this.mInFrontAlpha);
        setScrimAlpha(this.mScrimBehind, this.mBehindAlpha);
        setScrimAlpha(this.mScrimForBubble, this.mBubbleAlpha);
        onFinished();
        dispatchScrimsVisible();
    }

    private void dispatchScrimState(float f) {
        this.mScrimStateListener.accept(this.mState, Float.valueOf(f), this.mScrimInFront.getColors());
    }

    /* access modifiers changed from: private */
    public void dispatchScrimsVisible() {
        int i = (this.mScrimInFront.getViewAlpha() == 1.0f || this.mScrimBehind.getViewAlpha() == 1.0f) ? 2 : (this.mScrimInFront.getViewAlpha() == 0.0f && this.mScrimBehind.getViewAlpha() == 0.0f) ? 0 : 1;
        if (this.mScrimsVisibility != i) {
            this.mScrimsVisibility = i;
            this.mScrimVisibleListener.accept(Integer.valueOf(i));
        }
    }

    private float getInterpolatedFraction() {
        float f = (this.mExpansionFraction * 1.2f) - 0.2f;
        if (f <= 0.0f) {
            return 0.0f;
        }
        return (float) (1.0d - ((1.0d - Math.cos(Math.pow((double) (1.0f - f), 2.0d) * 3.141590118408203d)) * 0.5d));
    }

    private void setScrimAlpha(ScrimView scrimView, float f) {
        boolean z = false;
        if (f == 0.0f) {
            scrimView.setClickable(false);
        } else {
            if (this.mState != ScrimState.AOD) {
                z = true;
            }
            scrimView.setClickable(z);
        }
        updateScrim(scrimView, f);
    }

    private String getScrimName(ScrimView scrimView) {
        if (scrimView == this.mScrimInFront) {
            return "front_scrim";
        }
        if (scrimView == this.mScrimBehind) {
            return "back_scrim";
        }
        return scrimView == this.mScrimForBubble ? "bubble_scrim" : "unknown_scrim";
    }

    private void updateScrimColor(View view, float f, int i) {
        float max = Math.max(0.0f, Math.min(1.0f, f));
        if (view instanceof ScrimView) {
            ScrimView scrimView = (ScrimView) view;
            StringBuilder sb = new StringBuilder();
            sb.append(getScrimName(scrimView));
            sb.append("_alpha");
            Trace.traceCounter(4096, sb.toString(), (int) (255.0f * max));
            StringBuilder sb2 = new StringBuilder();
            sb2.append(getScrimName(scrimView));
            sb2.append("_tint");
            Trace.traceCounter(4096, sb2.toString(), Color.alpha(i));
            scrimView.setTint(i);
            scrimView.setViewAlpha(max);
        } else {
            view.setAlpha(max);
        }
        dispatchScrimsVisible();
    }

    private void startScrimAnimation(final View view, float f) {
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        AnimatorListener animatorListener = this.mAnimatorListener;
        if (animatorListener != null) {
            ofFloat.addListener(animatorListener);
        }
        ofFloat.addUpdateListener(new AnimatorUpdateListener(view, view instanceof ScrimView ? ((ScrimView) view).getTint() : 0) {
            public final /* synthetic */ View f$1;
            public final /* synthetic */ int f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                ScrimController.this.lambda$startScrimAnimation$2$ScrimController(this.f$1, this.f$2, valueAnimator);
            }
        });
        ofFloat.setInterpolator(this.mInterpolator);
        ofFloat.setStartDelay(this.mAnimationDelay);
        ofFloat.setDuration(this.mAnimationDuration);
        ofFloat.addListener(new AnimatorListenerAdapter() {
            private Callback lastCallback = ScrimController.this.mCallback;

            public void onAnimationEnd(Animator animator) {
                view.setTag(ScrimController.TAG_KEY_ANIM, null);
                ScrimController.this.onFinished(this.lastCallback);
                ScrimController.this.dispatchScrimsVisible();
            }
        });
        view.setTag(TAG_START_ALPHA, Float.valueOf(f));
        view.setTag(TAG_END_ALPHA, Float.valueOf(getCurrentScrimAlpha(view)));
        view.setTag(TAG_KEY_ANIM, ofFloat);
        ofFloat.start();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startScrimAnimation$2 */
    public /* synthetic */ void lambda$startScrimAnimation$2$ScrimController(View view, int i, ValueAnimator valueAnimator) {
        float floatValue = ((Float) view.getTag(TAG_START_ALPHA)).floatValue();
        float floatValue2 = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        updateScrimColor(view, MathUtils.constrain(MathUtils.lerp(floatValue, getCurrentScrimAlpha(view), floatValue2), 0.0f, 1.0f), ColorUtils.blendARGB(i, getCurrentScrimTint(view), floatValue2));
        dispatchScrimsVisible();
    }

    private float getCurrentScrimAlpha(View view) {
        if (view == this.mScrimInFront) {
            return this.mInFrontAlpha;
        }
        if (view == this.mScrimBehind) {
            return this.mBehindAlpha;
        }
        if (view == this.mScrimForBubble) {
            return this.mBubbleAlpha;
        }
        throw new IllegalArgumentException("Unknown scrim view");
    }

    private int getCurrentScrimTint(View view) {
        if (view == this.mScrimInFront) {
            return this.mInFrontTint;
        }
        if (view == this.mScrimBehind) {
            return this.mBehindTint;
        }
        if (view == this.mScrimForBubble) {
            return this.mBubbleTint;
        }
        throw new IllegalArgumentException("Unknown scrim view");
    }

    public boolean onPreDraw() {
        this.mScrimBehind.getViewTreeObserver().removeOnPreDrawListener(this);
        this.mUpdatePending = false;
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onStart();
        }
        updateScrims();
        return true;
    }

    private void onFinished() {
        onFinished(this.mCallback);
    }

    /* access modifiers changed from: private */
    public void onFinished(Callback callback) {
        if (this.mPendingFrameCallback == null) {
            if (isAnimating(this.mScrimBehind) || isAnimating(this.mScrimInFront) || isAnimating(this.mScrimForBubble)) {
                if (!(callback == null || callback == this.mCallback)) {
                    callback.onFinished();
                }
                return;
            }
            if (this.mWakeLockHeld) {
                this.mWakeLock.release("ScrimController");
                this.mWakeLockHeld = false;
            }
            if (callback != null) {
                callback.onFinished();
                if (callback == this.mCallback) {
                    this.mCallback = null;
                }
            }
            if (this.mState == ScrimState.UNLOCKED) {
                this.mInFrontTint = 0;
                this.mBehindTint = 0;
                this.mBubbleTint = 0;
                updateScrimColor(this.mScrimInFront, this.mInFrontAlpha, 0);
                updateScrimColor(this.mScrimBehind, this.mBehindAlpha, this.mBehindTint);
                updateScrimColor(this.mScrimForBubble, this.mBubbleAlpha, this.mBubbleTint);
            }
        }
    }

    private boolean isAnimating(View view) {
        return view.getTag(TAG_KEY_ANIM) != null;
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void setAnimatorListener(AnimatorListener animatorListener) {
        this.mAnimatorListener = animatorListener;
    }

    private void updateScrim(ScrimView scrimView, float f) {
        float viewAlpha = scrimView.getViewAlpha();
        ValueAnimator valueAnimator = (ValueAnimator) ViewState.getChildTag(scrimView, TAG_KEY_ANIM);
        if (valueAnimator != null) {
            cancelAnimator(valueAnimator);
        }
        if (this.mPendingFrameCallback == null) {
            if (this.mBlankScreen) {
                blankDisplay();
                return;
            }
            boolean z = true;
            if (!this.mScreenBlankingCallbackCalled) {
                Callback callback = this.mCallback;
                if (callback != null) {
                    callback.onDisplayBlanked();
                    this.mScreenBlankingCallbackCalled = true;
                }
            }
            if (scrimView == this.mScrimBehind) {
                dispatchScrimState(f);
            }
            boolean z2 = f != viewAlpha;
            if (scrimView.getTint() == getCurrentScrimTint(scrimView)) {
                z = false;
            }
            if (z2 || z) {
                if (this.mAnimateChange) {
                    startScrimAnimation(scrimView, viewAlpha);
                } else {
                    updateScrimColor(scrimView, f, getCurrentScrimTint(scrimView));
                }
            }
        }
    }

    private void cancelAnimator(ValueAnimator valueAnimator) {
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
    }

    private void blankDisplay() {
        updateScrimColor(this.mScrimInFront, 1.0f, -16777216);
        $$Lambda$ScrimController$ag08GXJhpSWypcA8hrLE9y1Zo r0 = new Runnable() {
            public final void run() {
                ScrimController.this.lambda$blankDisplay$4$ScrimController();
            }
        };
        this.mPendingFrameCallback = r0;
        doOnTheNextFrame(r0);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$blankDisplay$4 */
    public /* synthetic */ void lambda$blankDisplay$4$ScrimController() {
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onDisplayBlanked();
            this.mScreenBlankingCallbackCalled = true;
        }
        this.mBlankingTransitionRunnable = new Runnable() {
            public final void run() {
                ScrimController.this.lambda$blankDisplay$3$ScrimController();
            }
        };
        int i = this.mScreenOn ? 32 : 500;
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("Fading out scrims with delay: ");
            sb.append(i);
            Log.d("ScrimController", sb.toString());
        }
        this.mHandler.postDelayed(this.mBlankingTransitionRunnable, (long) i);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$blankDisplay$3 */
    public /* synthetic */ void lambda$blankDisplay$3$ScrimController() {
        this.mBlankingTransitionRunnable = null;
        this.mPendingFrameCallback = null;
        this.mBlankScreen = false;
        updateScrims();
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public void doOnTheNextFrame(Runnable runnable) {
        this.mScrimBehind.postOnAnimationDelayed(runnable, 32);
    }

    public void setScrimBehindChangeRunnable(Runnable runnable) {
        this.mScrimBehind.setChangeRunnable(runnable);
    }

    public void onColorsChanged(ColorExtractor colorExtractor, int i) {
        this.mColors = this.mColorExtractor.getNeutralColors();
        this.mNeedsDrawableColorUpdate = true;
        scheduleUpdate();
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println(" ScrimController: ");
        printWriter.print("  state: ");
        printWriter.println(this.mState);
        printWriter.print("  frontScrim:");
        String str = " viewAlpha=";
        printWriter.print(str);
        printWriter.print(this.mScrimInFront.getViewAlpha());
        String str2 = " alpha=";
        printWriter.print(str2);
        printWriter.print(this.mInFrontAlpha);
        String str3 = " tint=0x";
        printWriter.print(str3);
        printWriter.println(Integer.toHexString(this.mScrimInFront.getTint()));
        printWriter.print("  backScrim:");
        printWriter.print(str);
        printWriter.print(this.mScrimBehind.getViewAlpha());
        printWriter.print(str2);
        printWriter.print(this.mBehindAlpha);
        printWriter.print(str3);
        printWriter.println(Integer.toHexString(this.mScrimBehind.getTint()));
        printWriter.print("  bubbleScrim:");
        printWriter.print(str);
        printWriter.print(this.mScrimForBubble.getViewAlpha());
        printWriter.print(str2);
        printWriter.print(this.mBubbleAlpha);
        printWriter.print(str3);
        printWriter.println(Integer.toHexString(this.mScrimForBubble.getTint()));
        printWriter.print("  mTracking=");
        printWriter.println(this.mTracking);
        printWriter.print("  mDefaultScrimAlpha=");
        printWriter.println(this.mDefaultScrimAlpha);
        printWriter.print("  mExpansionFraction=");
        printWriter.println(this.mExpansionFraction);
    }

    public void setWallpaperSupportsAmbientMode(boolean z) {
        this.mWallpaperSupportsAmbientMode = z;
        ScrimState[] values = ScrimState.values();
        for (ScrimState wallpaperSupportsAmbientMode : values) {
            wallpaperSupportsAmbientMode.setWallpaperSupportsAmbientMode(z);
        }
    }

    public void onScreenTurnedOn() {
        this.mScreenOn = true;
        if (this.mHandler.hasCallbacks(this.mBlankingTransitionRunnable)) {
            if (DEBUG) {
                Log.d("ScrimController", "Shorter blanking because screen turned on. All good.");
            }
            this.mHandler.removeCallbacks(this.mBlankingTransitionRunnable);
            this.mBlankingTransitionRunnable.run();
        }
    }

    public void onScreenTurnedOff() {
        this.mScreenOn = false;
    }

    public void setExpansionAffectsAlpha(boolean z) {
        this.mExpansionAffectsAlpha = z;
    }

    public void setKeyguardOccluded(boolean z) {
        this.mKeyguardOccluded = z;
        updateScrims();
    }

    public void setHasBackdrop(boolean z) {
        for (ScrimState hasBackdrop : ScrimState.values()) {
            hasBackdrop.setHasBackdrop(z);
        }
        ScrimState scrimState = this.mState;
        if (scrimState == ScrimState.AOD || scrimState == ScrimState.PULSING) {
            float behindAlpha = this.mState.getBehindAlpha();
            if (Float.isNaN(behindAlpha)) {
                StringBuilder sb = new StringBuilder();
                sb.append("Scrim opacity is NaN for state: ");
                sb.append(this.mState);
                sb.append(", back: ");
                sb.append(this.mBehindAlpha);
                throw new IllegalStateException(sb.toString());
            } else if (this.mBehindAlpha != behindAlpha) {
                this.mBehindAlpha = behindAlpha;
                updateScrims();
            }
        }
    }

    /* access modifiers changed from: private */
    public void setKeyguardFadingAway(boolean z, long j) {
        for (ScrimState keyguardFadingAway : ScrimState.values()) {
            keyguardFadingAway.setKeyguardFadingAway(z, j);
        }
    }

    public void setLaunchingAffordanceWithPreview(boolean z) {
        for (ScrimState launchingAffordanceWithPreview : ScrimState.values()) {
            launchingAffordanceWithPreview.setLaunchingAffordanceWithPreview(z);
        }
    }
}
