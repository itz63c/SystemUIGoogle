package com.android.systemui.stackdivider;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.animation.Interpolator;
import com.android.systemui.C2008R$color;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.Interpolators;

public class DividerHandleView extends View {
    private static final Property<DividerHandleView, Integer> HEIGHT_PROPERTY;
    private static final Property<DividerHandleView, Integer> WIDTH_PROPERTY;
    /* access modifiers changed from: private */
    public AnimatorSet mAnimator;
    private final int mCircleDiameter;
    /* access modifiers changed from: private */
    public int mCurrentHeight;
    /* access modifiers changed from: private */
    public int mCurrentWidth;
    private final int mHeight;
    private final Paint mPaint;
    private boolean mTouching;
    private final int mWidth = getResources().getDimensionPixelSize(C2009R$dimen.docked_divider_handle_width);

    public boolean hasOverlappingRendering() {
        return false;
    }

    static {
        Class<Integer> cls = Integer.class;
        WIDTH_PROPERTY = new Property<DividerHandleView, Integer>(cls, "width") {
            public Integer get(DividerHandleView dividerHandleView) {
                return Integer.valueOf(dividerHandleView.mCurrentWidth);
            }

            public void set(DividerHandleView dividerHandleView, Integer num) {
                dividerHandleView.mCurrentWidth = num.intValue();
                dividerHandleView.invalidate();
            }
        };
        HEIGHT_PROPERTY = new Property<DividerHandleView, Integer>(cls, "height") {
            public Integer get(DividerHandleView dividerHandleView) {
                return Integer.valueOf(dividerHandleView.mCurrentHeight);
            }

            public void set(DividerHandleView dividerHandleView, Integer num) {
                dividerHandleView.mCurrentHeight = num.intValue();
                dividerHandleView.invalidate();
            }
        };
    }

    public DividerHandleView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        Paint paint = new Paint();
        this.mPaint = paint;
        paint.setColor(getResources().getColor(C2008R$color.docked_divider_handle, null));
        this.mPaint.setAntiAlias(true);
        int dimensionPixelSize = getResources().getDimensionPixelSize(C2009R$dimen.docked_divider_handle_height);
        this.mHeight = dimensionPixelSize;
        int i = this.mWidth;
        this.mCurrentWidth = i;
        this.mCurrentHeight = dimensionPixelSize;
        this.mCircleDiameter = (i + dimensionPixelSize) / 3;
    }

    public void setTouching(boolean z, boolean z2) {
        if (z != this.mTouching) {
            AnimatorSet animatorSet = this.mAnimator;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.mAnimator = null;
            }
            if (!z2) {
                if (z) {
                    int i = this.mCircleDiameter;
                    this.mCurrentWidth = i;
                    this.mCurrentHeight = i;
                } else {
                    this.mCurrentWidth = this.mWidth;
                    this.mCurrentHeight = this.mHeight;
                }
                invalidate();
            } else {
                animateToTarget(z ? this.mCircleDiameter : this.mWidth, z ? this.mCircleDiameter : this.mHeight, z);
            }
            this.mTouching = z;
        }
    }

    private void animateToTarget(int i, int i2, boolean z) {
        Interpolator interpolator;
        ObjectAnimator ofInt = ObjectAnimator.ofInt(this, WIDTH_PROPERTY, new int[]{this.mCurrentWidth, i});
        ObjectAnimator ofInt2 = ObjectAnimator.ofInt(this, HEIGHT_PROPERTY, new int[]{this.mCurrentHeight, i2});
        AnimatorSet animatorSet = new AnimatorSet();
        this.mAnimator = animatorSet;
        animatorSet.playTogether(new Animator[]{ofInt, ofInt2});
        this.mAnimator.setDuration(z ? 150 : 200);
        AnimatorSet animatorSet2 = this.mAnimator;
        if (z) {
            interpolator = Interpolators.TOUCH_RESPONSE;
        } else {
            interpolator = Interpolators.FAST_OUT_SLOW_IN;
        }
        animatorSet2.setInterpolator(interpolator);
        this.mAnimator.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animator) {
                DividerHandleView.this.mAnimator = null;
            }
        });
        this.mAnimator.start();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int width = (getWidth() / 2) - (this.mCurrentWidth / 2);
        int height = getHeight() / 2;
        int i = this.mCurrentHeight;
        int i2 = height - (i / 2);
        float min = (float) (Math.min(this.mCurrentWidth, i) / 2);
        canvas.drawRoundRect((float) width, (float) i2, (float) (width + this.mCurrentWidth), (float) (i2 + this.mCurrentHeight), min, min, this.mPaint);
    }
}
