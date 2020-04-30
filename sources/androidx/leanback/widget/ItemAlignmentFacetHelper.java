package androidx.leanback.widget;

import android.graphics.Rect;

class ItemAlignmentFacetHelper {
    private static Rect sRect = new Rect();

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x000e, code lost:
        if (r1 == null) goto L_0x0010;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static int getAlignmentPosition(android.view.View r7, androidx.leanback.widget.ItemAlignmentFacet.ItemAlignmentDef r8, int r9) {
        /*
            android.view.ViewGroup$LayoutParams r0 = r7.getLayoutParams()
            androidx.leanback.widget.GridLayoutManager$LayoutParams r0 = (androidx.leanback.widget.GridLayoutManager.LayoutParams) r0
            int r1 = r8.mViewId
            if (r1 == 0) goto L_0x0010
            android.view.View r1 = r7.findViewById(r1)
            if (r1 != 0) goto L_0x0011
        L_0x0010:
            r1 = r7
        L_0x0011:
            int r2 = r8.mOffset
            r3 = 0
            r4 = -1082130432(0xffffffffbf800000, float:-1.0)
            r5 = 1120403456(0x42c80000, float:100.0)
            if (r9 != 0) goto L_0x00bd
            int r9 = r7.getLayoutDirection()
            r6 = 1
            if (r9 != r6) goto L_0x0075
            if (r1 != r7) goto L_0x0028
            int r9 = r0.getOpticalWidth(r1)
            goto L_0x002c
        L_0x0028:
            int r9 = r1.getWidth()
        L_0x002c:
            int r9 = r9 - r2
            boolean r2 = r8.mOffsetWithPadding
            if (r2 == 0) goto L_0x0046
            float r2 = r8.mOffsetPercent
            int r3 = (r2 > r3 ? 1 : (r2 == r3 ? 0 : -1))
            if (r3 != 0) goto L_0x003d
            int r2 = r1.getPaddingRight()
            int r9 = r9 - r2
            goto L_0x0046
        L_0x003d:
            int r2 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1))
            if (r2 != 0) goto L_0x0046
            int r2 = r1.getPaddingLeft()
            int r9 = r9 + r2
        L_0x0046:
            float r2 = r8.mOffsetPercent
            int r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r2 == 0) goto L_0x005e
            if (r1 != r7) goto L_0x0053
            int r2 = r0.getOpticalWidth(r1)
            goto L_0x0057
        L_0x0053:
            int r2 = r1.getWidth()
        L_0x0057:
            float r2 = (float) r2
            float r8 = r8.mOffsetPercent
            float r2 = r2 * r8
            float r2 = r2 / r5
            int r8 = (int) r2
            int r9 = r9 - r8
        L_0x005e:
            if (r7 == r1) goto L_0x0110
            android.graphics.Rect r8 = sRect
            r8.right = r9
            android.view.ViewGroup r7 = (android.view.ViewGroup) r7
            r7.offsetDescendantRectToMyCoords(r1, r8)
            android.graphics.Rect r7 = sRect
            int r7 = r7.right
            int r8 = r0.getOpticalRightInset()
            int r9 = r7 + r8
            goto L_0x0110
        L_0x0075:
            boolean r9 = r8.mOffsetWithPadding
            if (r9 == 0) goto L_0x008e
            float r9 = r8.mOffsetPercent
            int r3 = (r9 > r3 ? 1 : (r9 == r3 ? 0 : -1))
            if (r3 != 0) goto L_0x0085
            int r9 = r1.getPaddingLeft()
            int r2 = r2 + r9
            goto L_0x008e
        L_0x0085:
            int r9 = (r9 > r5 ? 1 : (r9 == r5 ? 0 : -1))
            if (r9 != 0) goto L_0x008e
            int r9 = r1.getPaddingRight()
            int r2 = r2 - r9
        L_0x008e:
            float r9 = r8.mOffsetPercent
            int r9 = (r9 > r4 ? 1 : (r9 == r4 ? 0 : -1))
            if (r9 == 0) goto L_0x00a6
            if (r1 != r7) goto L_0x009b
            int r9 = r0.getOpticalWidth(r1)
            goto L_0x009f
        L_0x009b:
            int r9 = r1.getWidth()
        L_0x009f:
            float r9 = (float) r9
            float r8 = r8.mOffsetPercent
            float r9 = r9 * r8
            float r9 = r9 / r5
            int r8 = (int) r9
            int r2 = r2 + r8
        L_0x00a6:
            r9 = r2
            if (r7 == r1) goto L_0x0110
            android.graphics.Rect r8 = sRect
            r8.left = r9
            android.view.ViewGroup r7 = (android.view.ViewGroup) r7
            r7.offsetDescendantRectToMyCoords(r1, r8)
            android.graphics.Rect r7 = sRect
            int r7 = r7.left
            int r8 = r0.getOpticalLeftInset()
            int r9 = r7 - r8
            goto L_0x0110
        L_0x00bd:
            boolean r9 = r8.mOffsetWithPadding
            if (r9 == 0) goto L_0x00d6
            float r9 = r8.mOffsetPercent
            int r3 = (r9 > r3 ? 1 : (r9 == r3 ? 0 : -1))
            if (r3 != 0) goto L_0x00cd
            int r9 = r1.getPaddingTop()
            int r2 = r2 + r9
            goto L_0x00d6
        L_0x00cd:
            int r9 = (r9 > r5 ? 1 : (r9 == r5 ? 0 : -1))
            if (r9 != 0) goto L_0x00d6
            int r9 = r1.getPaddingBottom()
            int r2 = r2 - r9
        L_0x00d6:
            float r9 = r8.mOffsetPercent
            int r9 = (r9 > r4 ? 1 : (r9 == r4 ? 0 : -1))
            if (r9 == 0) goto L_0x00ee
            if (r1 != r7) goto L_0x00e3
            int r9 = r0.getOpticalHeight(r1)
            goto L_0x00e7
        L_0x00e3:
            int r9 = r1.getHeight()
        L_0x00e7:
            float r9 = (float) r9
            float r3 = r8.mOffsetPercent
            float r9 = r9 * r3
            float r9 = r9 / r5
            int r9 = (int) r9
            int r2 = r2 + r9
        L_0x00ee:
            if (r7 == r1) goto L_0x0104
            android.graphics.Rect r9 = sRect
            r9.top = r2
            android.view.ViewGroup r7 = (android.view.ViewGroup) r7
            r7.offsetDescendantRectToMyCoords(r1, r9)
            android.graphics.Rect r7 = sRect
            int r7 = r7.top
            int r9 = r0.getOpticalTopInset()
            int r7 = r7 - r9
            r9 = r7
            goto L_0x0105
        L_0x0104:
            r9 = r2
        L_0x0105:
            boolean r7 = r8.isAlignedToTextViewBaseLine()
            if (r7 == 0) goto L_0x0110
            int r7 = r1.getBaseline()
            int r9 = r9 + r7
        L_0x0110:
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.leanback.widget.ItemAlignmentFacetHelper.getAlignmentPosition(android.view.View, androidx.leanback.widget.ItemAlignmentFacet$ItemAlignmentDef, int):int");
    }
}
