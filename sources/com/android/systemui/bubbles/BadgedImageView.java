package com.android.systemui.bubbles;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.PathParser;
import android.widget.ImageView;
import com.android.launcher3.icons.DotRenderer;
import com.android.launcher3.icons.DotRenderer.DrawParams;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.Interpolators;

public class BadgedImageView extends ImageView {
    private BubbleViewProvider mBubble;
    private int mBubbleBitmapSize;
    private int mCurrentDotState;
    private int mDotColor;
    private boolean mDotDrawn;
    private DotRenderer mDotRenderer;
    private float mDotScale;
    private DrawParams mDrawParams;
    private boolean mOnLeft;
    private Rect mTempBounds;

    public BadgedImageView(Context context) {
        this(context, null);
    }

    public BadgedImageView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public BadgedImageView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public BadgedImageView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mCurrentDotState = 1;
        this.mDotScale = 0.0f;
        this.mTempBounds = new Rect();
        this.mBubbleBitmapSize = getResources().getDimensionPixelSize(C2009R$dimen.bubble_bitmap_size);
        this.mDrawParams = new DrawParams();
        this.mDotRenderer = new DotRenderer(this.mBubbleBitmapSize, PathParser.createPathFromPathData(getResources().getString(17039801)), 100);
    }

    public void update(BubbleViewProvider bubbleViewProvider) {
        this.mBubble = bubbleViewProvider;
        setImageBitmap(bubbleViewProvider.getBadgedImage());
        setDotState(1);
        this.mDotColor = bubbleViewProvider.getDotColor();
        drawDot(bubbleViewProvider.getDotPath());
        animateDot();
    }

    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        boolean z = false;
        if (isDotHidden()) {
            this.mDotDrawn = false;
            return;
        }
        if (this.mDotScale > 0.1f) {
            z = true;
        }
        this.mDotDrawn = z;
        getDrawingRect(this.mTempBounds);
        DrawParams drawParams = this.mDrawParams;
        drawParams.color = this.mDotColor;
        drawParams.iconBounds = this.mTempBounds;
        drawParams.leftAlign = this.mOnLeft;
        drawParams.scale = this.mDotScale;
        this.mDotRenderer.draw(canvas, drawParams);
    }

    /* access modifiers changed from: 0000 */
    public void setDotState(int i) {
        this.mCurrentDotState = i;
        if (i == 1 || i == 0) {
            this.mDotScale = this.mBubble.showDot() ? 1.0f : 0.0f;
            invalidate();
        }
    }

    private boolean isDotHidden() {
        return (this.mCurrentDotState == 0 && !this.mBubble.showDot()) || this.mCurrentDotState == 1;
    }

    /* access modifiers changed from: 0000 */
    public void setDotOnLeft(boolean z) {
        this.mOnLeft = z;
        invalidate();
    }

    /* access modifiers changed from: 0000 */
    public void drawDot(Path path) {
        this.mDotRenderer = new DotRenderer(this.mBubbleBitmapSize, path, 100);
        invalidate();
    }

    /* access modifiers changed from: 0000 */
    public void setDotScale(float f) {
        this.mDotScale = f;
        invalidate();
    }

    /* access modifiers changed from: 0000 */
    public boolean getDotOnLeft() {
        return this.mOnLeft;
    }

    /* access modifiers changed from: 0000 */
    public float[] getDotCenter() {
        float[] fArr;
        if (this.mOnLeft) {
            fArr = this.mDotRenderer.getLeftDotPosition();
        } else {
            fArr = this.mDotRenderer.getRightDotPosition();
        }
        getDrawingRect(this.mTempBounds);
        return new float[]{((float) this.mTempBounds.width()) * fArr[0], ((float) this.mTempBounds.height()) * fArr[1]};
    }

    public String getKey() {
        BubbleViewProvider bubbleViewProvider = this.mBubble;
        if (bubbleViewProvider != null) {
            return bubbleViewProvider.getKey();
        }
        return null;
    }

    /* access modifiers changed from: 0000 */
    public int getDotColor() {
        return this.mDotColor;
    }

    /* access modifiers changed from: 0000 */
    public void setDotPosition(boolean z, boolean z2) {
        if (!z2 || z == getDotOnLeft() || isDotHidden()) {
            setDotOnLeft(z);
        } else {
            animateDot(false, new Runnable(z) {
                public final /* synthetic */ boolean f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    BadgedImageView.this.lambda$setDotPosition$0$BadgedImageView(this.f$1);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setDotPosition$0 */
    public /* synthetic */ void lambda$setDotPosition$0$BadgedImageView(boolean z) {
        setDotOnLeft(z);
        animateDot(true, null);
    }

    /* access modifiers changed from: 0000 */
    public boolean getDotPositionOnLeft() {
        return getDotOnLeft();
    }

    /* access modifiers changed from: 0000 */
    public void animateDot() {
        if (this.mCurrentDotState == 0) {
            animateDot(this.mBubble.showDot(), null);
        }
    }

    private void animateDot(boolean z, Runnable runnable) {
        if (this.mDotDrawn != z) {
            setDotState(2);
            clearAnimation();
            animate().setDuration(200).setInterpolator(Interpolators.FAST_OUT_SLOW_IN).setUpdateListener(new AnimatorUpdateListener(z) {
                public final /* synthetic */ boolean f$1;

                {
                    this.f$1 = r2;
                }

                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    BadgedImageView.this.lambda$animateDot$1$BadgedImageView(this.f$1, valueAnimator);
                }
            }).withEndAction(new Runnable(z, runnable) {
                public final /* synthetic */ boolean f$1;
                public final /* synthetic */ Runnable f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    BadgedImageView.this.lambda$animateDot$2$BadgedImageView(this.f$1, this.f$2);
                }
            }).start();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$animateDot$1 */
    public /* synthetic */ void lambda$animateDot$1$BadgedImageView(boolean z, ValueAnimator valueAnimator) {
        float animatedFraction = valueAnimator.getAnimatedFraction();
        if (!z) {
            animatedFraction = 1.0f - animatedFraction;
        }
        setDotScale(animatedFraction);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$animateDot$2 */
    public /* synthetic */ void lambda$animateDot$2$BadgedImageView(boolean z, Runnable runnable) {
        setDotScale(z ? 1.0f : 0.0f);
        setDotState(0);
        if (runnable != null) {
            runnable.run();
        }
    }
}
