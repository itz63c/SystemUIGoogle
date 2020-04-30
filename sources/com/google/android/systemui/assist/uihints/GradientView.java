package com.google.android.systemui.assist.uihints;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.PathInterpolator;
import com.android.internal.graphics.ColorUtils;

public final class GradientView extends View {
    private int mBottomColor;
    private int[] mColors;
    private final Paint mGradientPaint;
    private final PathInterpolator mInterpolator;
    private final float[] mStops;
    private int mTopColor;

    public GradientView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public GradientView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public GradientView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mInterpolator = new PathInterpolator(0.5f, 0.5f, 0.7f, 1.0f);
        this.mColors = new int[100];
        this.mTopColor = 0;
        this.mBottomColor = 0;
        Paint paint = new Paint();
        this.mGradientPaint = paint;
        paint.setDither(true);
        this.mStops = new float[100];
        for (int i3 = 0; i3 < 100; i3++) {
            this.mStops[i3] = ((float) i3) / 100.0f;
        }
        updateGradient();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect((float) getLeft(), (float) getTop(), (float) getWidth(), (float) getHeight(), this.mGradientPaint);
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        updateGradient();
    }

    private void updateGradient() {
        for (int i = 0; i < 100; i++) {
            this.mColors[i] = ColorUtils.blendARGB(this.mBottomColor, this.mTopColor, this.mInterpolator.getInterpolation(this.mStops[i]));
        }
        Paint paint = this.mGradientPaint;
        LinearGradient linearGradient = new LinearGradient(0.0f, (float) getBottom(), 0.0f, (float) getTop(), this.mColors, this.mStops, TileMode.CLAMP);
        paint.setShader(linearGradient);
        invalidate();
    }
}
