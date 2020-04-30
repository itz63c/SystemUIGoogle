package androidx.leanback.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

public class ScaleFrameLayout extends FrameLayout {
    private float mChildScale;
    private float mLayoutScaleX;
    private float mLayoutScaleY;

    public ScaleFrameLayout(Context context) {
        this(context, null);
    }

    public ScaleFrameLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ScaleFrameLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mLayoutScaleX = 1.0f;
        this.mLayoutScaleY = 1.0f;
        this.mChildScale = 1.0f;
    }

    public void setLayoutScaleY(float f) {
        if (f != this.mLayoutScaleY) {
            this.mLayoutScaleY = f;
            requestLayout();
        }
    }

    public void setChildScale(float f) {
        if (this.mChildScale != f) {
            this.mChildScale = f;
            for (int i = 0; i < getChildCount(); i++) {
                getChildAt(i).setScaleX(f);
                getChildAt(i).setScaleY(f);
            }
        }
    }

    public void addView(View view, int i, LayoutParams layoutParams) {
        super.addView(view, i, layoutParams);
        view.setScaleX(this.mChildScale);
        view.setScaleY(this.mChildScale);
    }

    /* access modifiers changed from: protected */
    public boolean addViewInLayout(View view, int i, LayoutParams layoutParams, boolean z) {
        boolean addViewInLayout = super.addViewInLayout(view, i, layoutParams, z);
        if (addViewInLayout) {
            view.setScaleX(this.mChildScale);
            view.setScaleY(this.mChildScale);
        }
        return addViewInLayout;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00c6  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00da  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onLayout(boolean r17, int r18, int r19, int r20, int r21) {
        /*
            r16 = this;
            r0 = r16
            int r1 = r16.getChildCount()
            int r2 = r16.getLayoutDirection()
            r3 = 1
            if (r2 != r3) goto L_0x0018
            int r4 = r16.getWidth()
            float r4 = (float) r4
            float r5 = r16.getPivotX()
            float r4 = r4 - r5
            goto L_0x001c
        L_0x0018:
            float r4 = r16.getPivotX()
        L_0x001c:
            float r5 = r0.mLayoutScaleX
            r6 = 1065353216(0x3f800000, float:1.0)
            int r5 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            r7 = 1056964608(0x3f000000, float:0.5)
            if (r5 == 0) goto L_0x0040
            int r5 = r16.getPaddingLeft()
            float r8 = r0.mLayoutScaleX
            float r9 = r4 / r8
            float r9 = r4 - r9
            float r9 = r9 + r7
            int r9 = (int) r9
            int r5 = r5 + r9
            int r9 = r20 - r18
            float r9 = (float) r9
            float r9 = r9 - r4
            float r9 = r9 / r8
            float r9 = r9 + r4
            float r9 = r9 + r7
            int r8 = (int) r9
            int r9 = r16.getPaddingRight()
            goto L_0x004a
        L_0x0040:
            int r5 = r16.getPaddingLeft()
            int r8 = r20 - r18
            int r9 = r16.getPaddingRight()
        L_0x004a:
            int r8 = r8 - r9
            float r9 = r16.getPivotY()
            float r10 = r0.mLayoutScaleY
            int r6 = (r10 > r6 ? 1 : (r10 == r6 ? 0 : -1))
            if (r6 == 0) goto L_0x006f
            int r6 = r16.getPaddingTop()
            float r10 = r0.mLayoutScaleY
            float r11 = r9 / r10
            float r11 = r9 - r11
            float r11 = r11 + r7
            int r11 = (int) r11
            int r6 = r6 + r11
            int r11 = r21 - r19
            float r11 = (float) r11
            float r11 = r11 - r9
            float r11 = r11 / r10
            float r11 = r11 + r9
            float r11 = r11 + r7
            int r7 = (int) r11
            int r10 = r16.getPaddingBottom()
            goto L_0x0079
        L_0x006f:
            int r6 = r16.getPaddingTop()
            int r7 = r21 - r19
            int r10 = r16.getPaddingBottom()
        L_0x0079:
            int r7 = r7 - r10
            r10 = 0
        L_0x007b:
            if (r10 >= r1) goto L_0x00fe
            android.view.View r11 = r0.getChildAt(r10)
            int r12 = r11.getVisibility()
            r13 = 8
            if (r12 == r13) goto L_0x00f7
            android.view.ViewGroup$LayoutParams r12 = r11.getLayoutParams()
            android.widget.FrameLayout$LayoutParams r12 = (android.widget.FrameLayout.LayoutParams) r12
            int r13 = r11.getMeasuredWidth()
            int r14 = r11.getMeasuredHeight()
            int r15 = r12.gravity
            r3 = -1
            if (r15 != r3) goto L_0x009f
            r15 = 8388659(0x800033, float:1.1755015E-38)
        L_0x009f:
            int r3 = android.view.Gravity.getAbsoluteGravity(r15, r2)
            r15 = r15 & 112(0x70, float:1.57E-43)
            r3 = r3 & 7
            r0 = 1
            if (r3 == r0) goto L_0x00b6
            r0 = 5
            if (r3 == r0) goto L_0x00b1
            int r0 = r12.leftMargin
            int r0 = r0 + r5
            goto L_0x00c2
        L_0x00b1:
            int r0 = r8 - r13
            int r3 = r12.rightMargin
            goto L_0x00c1
        L_0x00b6:
            int r0 = r8 - r5
            int r0 = r0 - r13
            int r0 = r0 / 2
            int r0 = r0 + r5
            int r3 = r12.leftMargin
            int r0 = r0 + r3
            int r3 = r12.rightMargin
        L_0x00c1:
            int r0 = r0 - r3
        L_0x00c2:
            r3 = 16
            if (r15 == r3) goto L_0x00da
            r3 = 48
            if (r15 == r3) goto L_0x00d6
            r3 = 80
            if (r15 == r3) goto L_0x00d1
            int r3 = r12.topMargin
            goto L_0x00d8
        L_0x00d1:
            int r3 = r7 - r14
            int r12 = r12.bottomMargin
            goto L_0x00e5
        L_0x00d6:
            int r3 = r12.topMargin
        L_0x00d8:
            int r3 = r3 + r6
            goto L_0x00e6
        L_0x00da:
            int r3 = r7 - r6
            int r3 = r3 - r14
            int r3 = r3 / 2
            int r3 = r3 + r6
            int r15 = r12.topMargin
            int r3 = r3 + r15
            int r12 = r12.bottomMargin
        L_0x00e5:
            int r3 = r3 - r12
        L_0x00e6:
            int r13 = r13 + r0
            int r14 = r14 + r3
            r11.layout(r0, r3, r13, r14)
            float r0 = (float) r0
            float r0 = r4 - r0
            r11.setPivotX(r0)
            float r0 = (float) r3
            float r0 = r9 - r0
            r11.setPivotY(r0)
        L_0x00f7:
            int r10 = r10 + 1
            r0 = r16
            r3 = 1
            goto L_0x007b
        L_0x00fe:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.leanback.widget.ScaleFrameLayout.onLayout(boolean, int, int, int, int):void");
    }

    private static int getScaledMeasureSpec(int i, float f) {
        return f == 1.0f ? i : MeasureSpec.makeMeasureSpec((int) ((((float) MeasureSpec.getSize(i)) / f) + 0.5f), MeasureSpec.getMode(i));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        if (this.mLayoutScaleX == 1.0f && this.mLayoutScaleY == 1.0f) {
            super.onMeasure(i, i2);
            return;
        }
        super.onMeasure(getScaledMeasureSpec(i, this.mLayoutScaleX), getScaledMeasureSpec(i2, this.mLayoutScaleY));
        setMeasuredDimension((int) ((((float) getMeasuredWidth()) * this.mLayoutScaleX) + 0.5f), (int) ((((float) getMeasuredHeight()) * this.mLayoutScaleY) + 0.5f));
    }

    public void setForeground(Drawable drawable) {
        throw new UnsupportedOperationException();
    }
}
