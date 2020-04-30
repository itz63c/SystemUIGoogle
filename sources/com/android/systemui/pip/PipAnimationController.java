package com.android.systemui.pip;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Rect;
import android.view.SurfaceControl;
import android.view.SurfaceControl.Transaction;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import com.android.internal.annotations.VisibleForTesting;

public class PipAnimationController {
    private PipTransitionAnimator mCurrentAnimator;
    private final Interpolator mFastOutSlowInInterpolator;
    private final PipSurfaceTransactionHelper mSurfaceTransactionHelper;

    public static class PipAnimationCallback {
        public abstract void onPipAnimationCancel(PipTransitionAnimator pipTransitionAnimator);

        public abstract void onPipAnimationEnd(Transaction transaction, PipTransitionAnimator pipTransitionAnimator);

        public abstract void onPipAnimationStart(PipTransitionAnimator pipTransitionAnimator);
    }

    public static abstract class PipTransitionAnimator<T> extends ValueAnimator implements AnimatorUpdateListener, AnimatorListener {
        private final int mAnimationType;
        private T mCurrentValue;
        private final Rect mDestinationBounds;
        private T mEndValue;
        private final SurfaceControl mLeash;
        private PipAnimationCallback mPipAnimationCallback;
        private T mStartValue;
        private SurfaceControlTransactionFactory mSurfaceControlTransactionFactory;
        private PipSurfaceTransactionHelper mSurfaceTransactionHelper;
        private int mTransitionDirection;

        /* access modifiers changed from: 0000 */
        public abstract void applySurfaceControlTransaction(SurfaceControl surfaceControl, Transaction transaction, float f);

        public void onAnimationRepeat(Animator animator) {
        }

        private PipTransitionAnimator(SurfaceControl surfaceControl, int i, Rect rect, T t, T t2) {
            Rect rect2 = new Rect();
            this.mDestinationBounds = rect2;
            this.mLeash = surfaceControl;
            this.mAnimationType = i;
            rect2.set(rect);
            this.mStartValue = t;
            this.mEndValue = t2;
            addListener(this);
            addUpdateListener(this);
            this.mSurfaceControlTransactionFactory = $$Lambda$0FLZQAxNoOm85ohJ3bgjkYQDWsU.INSTANCE;
            this.mTransitionDirection = 0;
        }

        public void onAnimationStart(Animator animator) {
            this.mCurrentValue = this.mStartValue;
            applySurfaceControlTransaction(this.mLeash, newSurfaceControlTransaction(), 0.0f);
            PipAnimationCallback pipAnimationCallback = this.mPipAnimationCallback;
            if (pipAnimationCallback != null) {
                pipAnimationCallback.onPipAnimationStart(this);
            }
        }

        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            applySurfaceControlTransaction(this.mLeash, newSurfaceControlTransaction(), valueAnimator.getAnimatedFraction());
        }

        public void onAnimationEnd(Animator animator) {
            this.mCurrentValue = this.mEndValue;
            Transaction newSurfaceControlTransaction = newSurfaceControlTransaction();
            applySurfaceControlTransaction(this.mLeash, newSurfaceControlTransaction, 1.0f);
            PipAnimationCallback pipAnimationCallback = this.mPipAnimationCallback;
            if (pipAnimationCallback != null) {
                pipAnimationCallback.onPipAnimationEnd(newSurfaceControlTransaction, this);
            }
        }

        public void onAnimationCancel(Animator animator) {
            PipAnimationCallback pipAnimationCallback = this.mPipAnimationCallback;
            if (pipAnimationCallback != null) {
                pipAnimationCallback.onPipAnimationCancel(this);
            }
        }

        /* access modifiers changed from: 0000 */
        public int getAnimationType() {
            return this.mAnimationType;
        }

        /* access modifiers changed from: 0000 */
        public PipTransitionAnimator<T> setPipAnimationCallback(PipAnimationCallback pipAnimationCallback) {
            this.mPipAnimationCallback = pipAnimationCallback;
            return this;
        }

        /* access modifiers changed from: 0000 */
        public int getTransitionDirection() {
            return this.mTransitionDirection;
        }

        /* access modifiers changed from: 0000 */
        public PipTransitionAnimator<T> setTransitionDirection(int i) {
            if (i != 1) {
                this.mTransitionDirection = i;
            }
            return this;
        }

        /* access modifiers changed from: 0000 */
        public T getStartValue() {
            return this.mStartValue;
        }

        /* access modifiers changed from: 0000 */
        public T getEndValue() {
            return this.mEndValue;
        }

        /* access modifiers changed from: 0000 */
        public Rect getDestinationBounds() {
            return this.mDestinationBounds;
        }

        /* access modifiers changed from: 0000 */
        public void setDestinationBounds(Rect rect) {
            this.mDestinationBounds.set(rect);
        }

        /* access modifiers changed from: 0000 */
        public void setCurrentValue(T t) {
            this.mCurrentValue = t;
        }

        /* access modifiers changed from: 0000 */
        public boolean shouldApplyCornerRadius() {
            return this.mTransitionDirection != 3;
        }

        /* access modifiers changed from: 0000 */
        public void updateEndValue(T t) {
            this.mEndValue = t;
            this.mStartValue = this.mCurrentValue;
        }

        /* access modifiers changed from: 0000 */
        public Transaction newSurfaceControlTransaction() {
            return this.mSurfaceControlTransactionFactory.getTransaction();
        }

        /* access modifiers changed from: 0000 */
        @VisibleForTesting
        public void setSurfaceControlTransactionFactory(SurfaceControlTransactionFactory surfaceControlTransactionFactory) {
            this.mSurfaceControlTransactionFactory = surfaceControlTransactionFactory;
        }

        /* access modifiers changed from: 0000 */
        public PipSurfaceTransactionHelper getSurfaceTransactionHelper() {
            return this.mSurfaceTransactionHelper;
        }

        /* access modifiers changed from: 0000 */
        public void setSurfaceTransactionHelper(PipSurfaceTransactionHelper pipSurfaceTransactionHelper) {
            this.mSurfaceTransactionHelper = pipSurfaceTransactionHelper;
        }

        static PipTransitionAnimator<Float> ofAlpha(SurfaceControl surfaceControl, Rect rect, float f, float f2) {
            C08921 r0 = new PipTransitionAnimator<Float>(surfaceControl, 1, rect, Float.valueOf(f), Float.valueOf(f2)) {
                /* access modifiers changed from: 0000 */
                public void applySurfaceControlTransaction(SurfaceControl surfaceControl, Transaction transaction, float f) {
                    float floatValue = (((Float) getStartValue()).floatValue() * (1.0f - f)) + (((Float) getEndValue()).floatValue() * f);
                    setCurrentValue(Float.valueOf(floatValue));
                    getSurfaceTransactionHelper().alpha(transaction, surfaceControl, floatValue);
                    if (Float.compare(f, 0.0f) == 0) {
                        PipSurfaceTransactionHelper surfaceTransactionHelper = getSurfaceTransactionHelper();
                        surfaceTransactionHelper.crop(transaction, surfaceControl, getDestinationBounds());
                        surfaceTransactionHelper.round(transaction, surfaceControl, shouldApplyCornerRadius());
                    }
                    transaction.apply();
                }
            };
            return r0;
        }

        static PipTransitionAnimator<Rect> ofBounds(SurfaceControl surfaceControl, Rect rect, Rect rect2) {
            C08932 r0 = new PipTransitionAnimator<Rect>(surfaceControl, 0, rect2, new Rect(rect), new Rect(rect2)) {
                private final Rect mTmpRect = new Rect();

                private int getCastedFractionValue(float f, float f2, float f3) {
                    return (int) ((f * (1.0f - f3)) + (f2 * f3) + 0.5f);
                }

                /* access modifiers changed from: 0000 */
                public void applySurfaceControlTransaction(SurfaceControl surfaceControl, Transaction transaction, float f) {
                    Rect rect = (Rect) getStartValue();
                    Rect rect2 = (Rect) getEndValue();
                    this.mTmpRect.set(getCastedFractionValue((float) rect.left, (float) rect2.left, f), getCastedFractionValue((float) rect.top, (float) rect2.top, f), getCastedFractionValue((float) rect.right, (float) rect2.right, f), getCastedFractionValue((float) rect.bottom, (float) rect2.bottom, f));
                    setCurrentValue(this.mTmpRect);
                    getSurfaceTransactionHelper().crop(transaction, surfaceControl, this.mTmpRect);
                    if (Float.compare(f, 0.0f) == 0) {
                        PipSurfaceTransactionHelper surfaceTransactionHelper = getSurfaceTransactionHelper();
                        surfaceTransactionHelper.alpha(transaction, surfaceControl, 1.0f);
                        surfaceTransactionHelper.round(transaction, surfaceControl, shouldApplyCornerRadius());
                    }
                    transaction.apply();
                }
            };
            return r0;
        }
    }

    PipAnimationController(Context context, PipSurfaceTransactionHelper pipSurfaceTransactionHelper) {
        this.mFastOutSlowInInterpolator = AnimationUtils.loadInterpolator(context, 17563661);
        this.mSurfaceTransactionHelper = pipSurfaceTransactionHelper;
    }

    /* access modifiers changed from: 0000 */
    public PipTransitionAnimator getAnimator(SurfaceControl surfaceControl, Rect rect, float f, float f2) {
        PipTransitionAnimator pipTransitionAnimator = this.mCurrentAnimator;
        if (pipTransitionAnimator == null) {
            PipTransitionAnimator ofAlpha = PipTransitionAnimator.ofAlpha(surfaceControl, rect, f, f2);
            setupPipTransitionAnimator(ofAlpha);
            this.mCurrentAnimator = ofAlpha;
        } else if (pipTransitionAnimator.getAnimationType() != 1 || !this.mCurrentAnimator.isRunning()) {
            this.mCurrentAnimator.cancel();
            PipTransitionAnimator ofAlpha2 = PipTransitionAnimator.ofAlpha(surfaceControl, rect, f, f2);
            setupPipTransitionAnimator(ofAlpha2);
            this.mCurrentAnimator = ofAlpha2;
        } else {
            this.mCurrentAnimator.updateEndValue(Float.valueOf(f2));
        }
        return this.mCurrentAnimator;
    }

    /* access modifiers changed from: 0000 */
    public PipTransitionAnimator getAnimator(SurfaceControl surfaceControl, Rect rect, Rect rect2) {
        PipTransitionAnimator pipTransitionAnimator = this.mCurrentAnimator;
        if (pipTransitionAnimator == null) {
            PipTransitionAnimator ofBounds = PipTransitionAnimator.ofBounds(surfaceControl, rect, rect2);
            setupPipTransitionAnimator(ofBounds);
            this.mCurrentAnimator = ofBounds;
        } else if (pipTransitionAnimator.getAnimationType() != 0 || !this.mCurrentAnimator.isRunning()) {
            this.mCurrentAnimator.cancel();
            PipTransitionAnimator ofBounds2 = PipTransitionAnimator.ofBounds(surfaceControl, rect, rect2);
            setupPipTransitionAnimator(ofBounds2);
            this.mCurrentAnimator = ofBounds2;
        } else {
            this.mCurrentAnimator.setDestinationBounds(rect2);
            this.mCurrentAnimator.updateEndValue(new Rect(rect2));
        }
        return this.mCurrentAnimator;
    }

    private PipTransitionAnimator setupPipTransitionAnimator(PipTransitionAnimator pipTransitionAnimator) {
        pipTransitionAnimator.setSurfaceTransactionHelper(this.mSurfaceTransactionHelper);
        pipTransitionAnimator.setInterpolator(this.mFastOutSlowInInterpolator);
        pipTransitionAnimator.setFloatValues(new float[]{0.0f, 1.0f});
        return pipTransitionAnimator;
    }
}
