package com.android.systemui;

import android.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;

public class ResizingSpace extends View {
    private final int mHeight;
    private final int mWidth;

    public void draw(Canvas canvas) {
    }

    public ResizingSpace(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        if (getVisibility() == 0) {
            setVisibility(4);
        }
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.ViewGroup_Layout);
        this.mWidth = obtainStyledAttributes.getResourceId(0, 0);
        this.mHeight = obtainStyledAttributes.getResourceId(1, 0);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x003f  */
    /* JADX WARNING: Removed duplicated region for block: B:15:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x0027  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onConfigurationChanged(android.content.res.Configuration r5) {
        /*
            r4 = this;
            super.onConfigurationChanged(r5)
            android.view.ViewGroup$LayoutParams r5 = r4.getLayoutParams()
            int r0 = r4.mWidth
            r1 = 1
            if (r0 <= 0) goto L_0x0022
            android.content.Context r0 = r4.getContext()
            android.content.res.Resources r0 = r0.getResources()
            int r2 = r4.mWidth
            int r0 = r0.getDimensionPixelOffset(r2)
            int r2 = r5.width
            if (r0 == r2) goto L_0x0022
            r5.width = r0
            r0 = r1
            goto L_0x0023
        L_0x0022:
            r0 = 0
        L_0x0023:
            int r2 = r4.mHeight
            if (r2 <= 0) goto L_0x003c
            android.content.Context r2 = r4.getContext()
            android.content.res.Resources r2 = r2.getResources()
            int r3 = r4.mHeight
            int r2 = r2.getDimensionPixelOffset(r3)
            int r3 = r5.height
            if (r2 == r3) goto L_0x003c
            r5.height = r2
            goto L_0x003d
        L_0x003c:
            r1 = r0
        L_0x003d:
            if (r1 == 0) goto L_0x0042
            r4.setLayoutParams(r5)
        L_0x0042:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.ResizingSpace.onConfigurationChanged(android.content.res.Configuration):void");
    }

    private static int getDefaultSize2(int i, int i2) {
        int mode = MeasureSpec.getMode(i2);
        int size = MeasureSpec.getSize(i2);
        if (mode == Integer.MIN_VALUE) {
            return Math.min(i, size);
        }
        if (mode != 1073741824) {
            return i;
        }
        return size;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        setMeasuredDimension(getDefaultSize2(getSuggestedMinimumWidth(), i), getDefaultSize2(getSuggestedMinimumHeight(), i2));
    }
}
