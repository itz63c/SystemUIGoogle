package com.android.systemui.statusbar.phone;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.Interpolators;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.statusbar.FlingAnimationUtils;
import com.android.systemui.statusbar.KeyguardAffordanceView;

public class KeyguardAffordanceHelper {
    private Runnable mAnimationEndRunnable = new Runnable() {
        public void run() {
            KeyguardAffordanceHelper.this.mCallback.onAnimationToSideEnded();
        }
    };
    /* access modifiers changed from: private */
    public final Callback mCallback;
    private final Context mContext;
    private final FalsingManager mFalsingManager;
    private FlingAnimationUtils mFlingAnimationUtils;
    private AnimatorListenerAdapter mFlingEndListener = new AnimatorListenerAdapter() {
        public void onAnimationEnd(Animator animator) {
            KeyguardAffordanceHelper.this.mSwipeAnimator = null;
            KeyguardAffordanceHelper.this.mSwipingInProgress = false;
            KeyguardAffordanceHelper.this.mTargetedView = null;
        }
    };
    private int mHintGrowAmount;
    private float mInitialTouchX;
    private float mInitialTouchY;
    private KeyguardAffordanceView mLeftIcon;
    private int mMinBackgroundRadius;
    private int mMinFlingVelocity;
    private int mMinTranslationAmount;
    private boolean mMotionCancelled;
    private KeyguardAffordanceView mRightIcon;
    /* access modifiers changed from: private */
    public Animator mSwipeAnimator;
    /* access modifiers changed from: private */
    public boolean mSwipingInProgress;
    /* access modifiers changed from: private */
    public View mTargetedView;
    private int mTouchSlop;
    private boolean mTouchSlopExeeded;
    private int mTouchTargetSize;
    /* access modifiers changed from: private */
    public float mTranslation;
    private float mTranslationOnDown;
    private VelocityTracker mVelocityTracker;

    public interface Callback {
        float getAffordanceFalsingFactor();

        KeyguardAffordanceView getLeftIcon();

        View getLeftPreview();

        float getMaxTranslationDistance();

        KeyguardAffordanceView getRightIcon();

        View getRightPreview();

        boolean needsAntiFalsing();

        void onAnimationToSideEnded();

        void onAnimationToSideStarted(boolean z, float f, float f2);

        void onIconClicked(boolean z);

        void onSwipingAborted();

        void onSwipingStarted(boolean z);
    }

    KeyguardAffordanceHelper(Callback callback, Context context, FalsingManager falsingManager) {
        this.mContext = context;
        this.mCallback = callback;
        initIcons();
        KeyguardAffordanceView keyguardAffordanceView = this.mLeftIcon;
        updateIcon(keyguardAffordanceView, 0.0f, keyguardAffordanceView.getRestingAlpha(), false, false, true, false);
        KeyguardAffordanceView keyguardAffordanceView2 = this.mRightIcon;
        updateIcon(keyguardAffordanceView2, 0.0f, keyguardAffordanceView2.getRestingAlpha(), false, false, true, false);
        this.mFalsingManager = falsingManager;
        initDimens();
    }

    private void initDimens() {
        ViewConfiguration viewConfiguration = ViewConfiguration.get(this.mContext);
        this.mTouchSlop = viewConfiguration.getScaledPagingTouchSlop();
        this.mMinFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        this.mMinTranslationAmount = this.mContext.getResources().getDimensionPixelSize(C2009R$dimen.keyguard_min_swipe_amount);
        this.mMinBackgroundRadius = this.mContext.getResources().getDimensionPixelSize(C2009R$dimen.keyguard_affordance_min_background_radius);
        this.mTouchTargetSize = this.mContext.getResources().getDimensionPixelSize(C2009R$dimen.keyguard_affordance_touch_target_size);
        this.mHintGrowAmount = this.mContext.getResources().getDimensionPixelSize(C2009R$dimen.hint_grow_amount_sideways);
        this.mFlingAnimationUtils = new FlingAnimationUtils(this.mContext.getResources().getDisplayMetrics(), 0.4f);
    }

    private void initIcons() {
        this.mLeftIcon = this.mCallback.getLeftIcon();
        this.mRightIcon = this.mCallback.getRightIcon();
        updatePreviews();
    }

    public void updatePreviews() {
        this.mLeftIcon.setPreviewView(this.mCallback.getLeftPreview());
        this.mRightIcon.setPreviewView(this.mCallback.getRightPreview());
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean z;
        float f;
        int actionMasked = motionEvent.getActionMasked();
        boolean z2 = false;
        if (this.mMotionCancelled && actionMasked != 0) {
            return false;
        }
        float y = motionEvent.getY();
        float x = motionEvent.getX();
        if (actionMasked != 0) {
            if (actionMasked == 1) {
                z = true;
            } else if (actionMasked == 2) {
                trackMovement(motionEvent);
                float hypot = (float) Math.hypot((double) (x - this.mInitialTouchX), (double) (y - this.mInitialTouchY));
                if (!this.mTouchSlopExeeded && hypot > ((float) this.mTouchSlop)) {
                    this.mTouchSlopExeeded = true;
                }
                if (this.mSwipingInProgress) {
                    if (this.mTargetedView == this.mRightIcon) {
                        f = Math.min(0.0f, this.mTranslationOnDown - hypot);
                    } else {
                        f = Math.max(0.0f, this.mTranslationOnDown + hypot);
                    }
                    setTranslation(f, false, false);
                }
            } else if (actionMasked == 3) {
                z = false;
            } else if (actionMasked == 5) {
                this.mMotionCancelled = true;
                endMotion(true, x, y);
            }
            if (this.mTargetedView == this.mRightIcon) {
                z2 = true;
            }
            trackMovement(motionEvent);
            endMotion(!z, x, y);
            if (!this.mTouchSlopExeeded && z) {
                this.mCallback.onIconClicked(z2);
            }
        } else {
            View iconAtPosition = getIconAtPosition(x, y);
            if (iconAtPosition != null) {
                View view = this.mTargetedView;
                if (view == null || view == iconAtPosition) {
                    if (this.mTargetedView != null) {
                        cancelAnimation();
                    } else {
                        this.mTouchSlopExeeded = false;
                    }
                    startSwiping(iconAtPosition);
                    this.mInitialTouchX = x;
                    this.mInitialTouchY = y;
                    this.mTranslationOnDown = this.mTranslation;
                    initVelocityTracker();
                    trackMovement(motionEvent);
                    this.mMotionCancelled = false;
                }
            }
            this.mMotionCancelled = true;
            return false;
        }
        return true;
    }

    private void startSwiping(View view) {
        this.mCallback.onSwipingStarted(view == this.mRightIcon);
        this.mSwipingInProgress = true;
        this.mTargetedView = view;
    }

    private View getIconAtPosition(float f, float f2) {
        if (leftSwipePossible() && isOnIcon(this.mLeftIcon, f, f2)) {
            return this.mLeftIcon;
        }
        if (!rightSwipePossible() || !isOnIcon(this.mRightIcon, f, f2)) {
            return null;
        }
        return this.mRightIcon;
    }

    public boolean isOnAffordanceIcon(float f, float f2) {
        return isOnIcon(this.mLeftIcon, f, f2) || isOnIcon(this.mRightIcon, f, f2);
    }

    private boolean isOnIcon(View view, float f, float f2) {
        return Math.hypot((double) (f - (view.getX() + (((float) view.getWidth()) / 2.0f))), (double) (f2 - (view.getY() + (((float) view.getHeight()) / 2.0f)))) <= ((double) (this.mTouchTargetSize / 2));
    }

    private void endMotion(boolean z, float f, float f2) {
        if (this.mSwipingInProgress) {
            flingWithCurrentVelocity(z, f, f2);
        } else {
            this.mTargetedView = null;
        }
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
            this.mVelocityTracker = null;
        }
    }

    private boolean rightSwipePossible() {
        return this.mRightIcon.getVisibility() == 0;
    }

    private boolean leftSwipePossible() {
        return this.mLeftIcon.getVisibility() == 0;
    }

    public void startHintAnimation(boolean z, Runnable runnable) {
        cancelAnimation();
        startHintAnimationPhase1(z, runnable);
    }

    private void startHintAnimationPhase1(final boolean z, final Runnable runnable) {
        KeyguardAffordanceView keyguardAffordanceView = z ? this.mRightIcon : this.mLeftIcon;
        ValueAnimator animatorToRadius = getAnimatorToRadius(z, this.mHintGrowAmount);
        animatorToRadius.addListener(new AnimatorListenerAdapter() {
            private boolean mCancelled;

            public void onAnimationCancel(Animator animator) {
                this.mCancelled = true;
            }

            public void onAnimationEnd(Animator animator) {
                if (this.mCancelled) {
                    KeyguardAffordanceHelper.this.mSwipeAnimator = null;
                    KeyguardAffordanceHelper.this.mTargetedView = null;
                    runnable.run();
                    return;
                }
                KeyguardAffordanceHelper.this.startUnlockHintAnimationPhase2(z, runnable);
            }
        });
        animatorToRadius.setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN);
        animatorToRadius.setDuration(200);
        animatorToRadius.start();
        this.mSwipeAnimator = animatorToRadius;
        this.mTargetedView = keyguardAffordanceView;
    }

    /* access modifiers changed from: private */
    public void startUnlockHintAnimationPhase2(boolean z, final Runnable runnable) {
        ValueAnimator animatorToRadius = getAnimatorToRadius(z, 0);
        animatorToRadius.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                KeyguardAffordanceHelper.this.mSwipeAnimator = null;
                KeyguardAffordanceHelper.this.mTargetedView = null;
                runnable.run();
            }
        });
        animatorToRadius.setInterpolator(Interpolators.FAST_OUT_LINEAR_IN);
        animatorToRadius.setDuration(350);
        animatorToRadius.setStartDelay(500);
        animatorToRadius.start();
        this.mSwipeAnimator = animatorToRadius;
    }

    private ValueAnimator getAnimatorToRadius(final boolean z, int i) {
        final KeyguardAffordanceView keyguardAffordanceView = z ? this.mRightIcon : this.mLeftIcon;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{keyguardAffordanceView.getCircleRadius(), (float) i});
        ofFloat.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                keyguardAffordanceView.setCircleRadiusWithoutAnimation(floatValue);
                float access$500 = KeyguardAffordanceHelper.this.getTranslationFromRadius(floatValue);
                KeyguardAffordanceHelper keyguardAffordanceHelper = KeyguardAffordanceHelper.this;
                if (z) {
                    access$500 = -access$500;
                }
                keyguardAffordanceHelper.mTranslation = access$500;
                KeyguardAffordanceHelper.this.updateIconsFromTranslation(keyguardAffordanceView);
            }
        });
        return ofFloat;
    }

    private void cancelAnimation() {
        Animator animator = this.mSwipeAnimator;
        if (animator != null) {
            animator.cancel();
        }
    }

    private void flingWithCurrentVelocity(boolean z, float f, float f2) {
        float currentVelocity = getCurrentVelocity(f, f2);
        boolean z2 = true;
        boolean z3 = (this.mCallback.needsAntiFalsing() && this.mFalsingManager.isFalseTouch()) || isBelowFalsingThreshold();
        boolean z4 = this.mTranslation * currentVelocity < 0.0f;
        boolean z5 = z3 | (Math.abs(currentVelocity) > ((float) this.mMinFlingVelocity) && z4);
        if (z4 ^ z5) {
            currentVelocity = 0.0f;
        }
        boolean z6 = z5 || z;
        if (this.mTranslation >= 0.0f) {
            z2 = false;
        }
        fling(currentVelocity, z6, z2);
    }

    private boolean isBelowFalsingThreshold() {
        return Math.abs(this.mTranslation) < Math.abs(this.mTranslationOnDown) + ((float) getMinTranslationAmount());
    }

    private int getMinTranslationAmount() {
        return (int) (((float) this.mMinTranslationAmount) * this.mCallback.getAffordanceFalsingFactor());
    }

    private void fling(float f, boolean z, boolean z2) {
        float f2;
        if (z2) {
            f2 = -this.mCallback.getMaxTranslationDistance();
        } else {
            f2 = this.mCallback.getMaxTranslationDistance();
        }
        if (z) {
            f2 = 0.0f;
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.mTranslation, f2});
        this.mFlingAnimationUtils.apply(ofFloat, this.mTranslation, f2, f);
        ofFloat.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                KeyguardAffordanceHelper.this.mTranslation = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            }
        });
        ofFloat.addListener(this.mFlingEndListener);
        if (!z) {
            startFinishingCircleAnimation(0.375f * f, this.mAnimationEndRunnable, z2);
            this.mCallback.onAnimationToSideStarted(z2, this.mTranslation, f);
        } else {
            reset(true);
        }
        ofFloat.start();
        this.mSwipeAnimator = ofFloat;
        if (z) {
            this.mCallback.onSwipingAborted();
        }
    }

    private void startFinishingCircleAnimation(float f, Runnable runnable, boolean z) {
        (z ? this.mRightIcon : this.mLeftIcon).finishAnimation(f, runnable);
    }

    private void setTranslation(float f, boolean z, boolean z2) {
        float max = rightSwipePossible() ? f : Math.max(0.0f, f);
        if (!leftSwipePossible()) {
            max = Math.min(0.0f, max);
        }
        float f2 = max;
        float abs = Math.abs(f2);
        if (f2 != this.mTranslation || z) {
            int i = (f2 > 0.0f ? 1 : (f2 == 0.0f ? 0 : -1));
            KeyguardAffordanceView keyguardAffordanceView = i > 0 ? this.mLeftIcon : this.mRightIcon;
            KeyguardAffordanceView keyguardAffordanceView2 = i > 0 ? this.mRightIcon : this.mLeftIcon;
            float minTranslationAmount = abs / ((float) getMinTranslationAmount());
            float max2 = Math.max(1.0f - minTranslationAmount, 0.0f);
            boolean z3 = z && z2;
            boolean z4 = z && !z2;
            float radiusFromTranslation = getRadiusFromTranslation(abs);
            boolean z5 = z && isBelowFalsingThreshold();
            if (!z) {
                updateIcon(keyguardAffordanceView, radiusFromTranslation, minTranslationAmount + (keyguardAffordanceView.getRestingAlpha() * max2), false, false, false, false);
            } else {
                updateIcon(keyguardAffordanceView, 0.0f, max2 * keyguardAffordanceView.getRestingAlpha(), z3, z5, true, z4);
            }
            updateIcon(keyguardAffordanceView2, 0.0f, max2 * keyguardAffordanceView2.getRestingAlpha(), z3, z5, z, z4);
            this.mTranslation = f2;
        }
    }

    /* access modifiers changed from: private */
    public void updateIconsFromTranslation(KeyguardAffordanceView keyguardAffordanceView) {
        float abs = Math.abs(this.mTranslation) / ((float) getMinTranslationAmount());
        float max = Math.max(0.0f, 1.0f - abs);
        KeyguardAffordanceView keyguardAffordanceView2 = this.mRightIcon;
        if (keyguardAffordanceView == keyguardAffordanceView2) {
            keyguardAffordanceView2 = this.mLeftIcon;
        }
        updateIconAlpha(keyguardAffordanceView, abs + (keyguardAffordanceView.getRestingAlpha() * max), false);
        updateIconAlpha(keyguardAffordanceView2, max * keyguardAffordanceView2.getRestingAlpha(), false);
    }

    /* access modifiers changed from: private */
    public float getTranslationFromRadius(float f) {
        float f2 = (f - ((float) this.mMinBackgroundRadius)) / 0.25f;
        if (f2 > 0.0f) {
            return f2 + ((float) this.mTouchSlop);
        }
        return 0.0f;
    }

    private float getRadiusFromTranslation(float f) {
        int i = this.mTouchSlop;
        if (f <= ((float) i)) {
            return 0.0f;
        }
        return ((f - ((float) i)) * 0.25f) + ((float) this.mMinBackgroundRadius);
    }

    public void animateHideLeftRightIcon() {
        cancelAnimation();
        updateIcon(this.mRightIcon, 0.0f, 0.0f, true, false, false, false);
        updateIcon(this.mLeftIcon, 0.0f, 0.0f, true, false, false, false);
    }

    private void updateIcon(KeyguardAffordanceView keyguardAffordanceView, float f, float f2, boolean z, boolean z2, boolean z3, boolean z4) {
        if (keyguardAffordanceView.getVisibility() == 0 || z3) {
            if (z4) {
                keyguardAffordanceView.setCircleRadiusWithoutAnimation(f);
            } else {
                keyguardAffordanceView.setCircleRadius(f, z2);
            }
            updateIconAlpha(keyguardAffordanceView, f2, z);
        }
    }

    private void updateIconAlpha(KeyguardAffordanceView keyguardAffordanceView, float f, boolean z) {
        float scale = getScale(f, keyguardAffordanceView);
        keyguardAffordanceView.setImageAlpha(Math.min(1.0f, f), z);
        keyguardAffordanceView.setImageScale(scale, z);
    }

    private float getScale(float f, KeyguardAffordanceView keyguardAffordanceView) {
        return Math.min(((f / keyguardAffordanceView.getRestingAlpha()) * 0.2f) + 0.8f, 1.5f);
    }

    private void trackMovement(MotionEvent motionEvent) {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.addMovement(motionEvent);
        }
    }

    private void initVelocityTracker() {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
        }
        this.mVelocityTracker = VelocityTracker.obtain();
    }

    private float getCurrentVelocity(float f, float f2) {
        VelocityTracker velocityTracker = this.mVelocityTracker;
        if (velocityTracker == null) {
            return 0.0f;
        }
        velocityTracker.computeCurrentVelocity(1000);
        float f3 = f - this.mInitialTouchX;
        float f4 = f2 - this.mInitialTouchY;
        float xVelocity = ((this.mVelocityTracker.getXVelocity() * f3) + (this.mVelocityTracker.getYVelocity() * f4)) / ((float) Math.hypot((double) f3, (double) f4));
        if (this.mTargetedView == this.mRightIcon) {
            xVelocity = -xVelocity;
        }
        return xVelocity;
    }

    public void onConfigurationChanged() {
        initDimens();
        initIcons();
    }

    public void onRtlPropertiesChanged() {
        initIcons();
    }

    public void reset(boolean z) {
        cancelAnimation();
        setTranslation(0.0f, true, z);
        this.mMotionCancelled = true;
        if (this.mSwipingInProgress) {
            this.mCallback.onSwipingAborted();
            this.mSwipingInProgress = false;
        }
    }

    public boolean isSwipingInProgress() {
        return this.mSwipingInProgress;
    }

    public void launchAffordance(boolean z, boolean z2) {
        float f;
        if (!this.mSwipingInProgress) {
            KeyguardAffordanceView keyguardAffordanceView = z2 ? this.mLeftIcon : this.mRightIcon;
            KeyguardAffordanceView keyguardAffordanceView2 = z2 ? this.mRightIcon : this.mLeftIcon;
            startSwiping(keyguardAffordanceView);
            if (keyguardAffordanceView.getVisibility() != 0) {
                z = false;
            }
            if (z) {
                fling(0.0f, false, !z2);
                updateIcon(keyguardAffordanceView2, 0.0f, 0.0f, true, false, true, false);
            } else {
                this.mCallback.onAnimationToSideStarted(!z2, this.mTranslation, 0.0f);
                if (z2) {
                    f = this.mCallback.getMaxTranslationDistance();
                } else {
                    f = this.mCallback.getMaxTranslationDistance();
                }
                this.mTranslation = f;
                updateIcon(keyguardAffordanceView2, 0.0f, 0.0f, false, false, true, false);
                keyguardAffordanceView.instantFinishAnimation();
                this.mFlingEndListener.onAnimationEnd(null);
                this.mAnimationEndRunnable.run();
            }
        }
    }
}
