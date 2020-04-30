package com.android.systemui.classifier;

public class DirectionEvaluator {
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x002a, code lost:
        if (r7 != 9) goto L_0x0056;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static float evaluate(float r5, float r6, int r7) {
        /*
            float r0 = java.lang.Math.abs(r6)
            float r1 = java.lang.Math.abs(r5)
            int r0 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1))
            r1 = 1
            if (r0 < 0) goto L_0x000f
            r0 = r1
            goto L_0x0010
        L_0x000f:
            r0 = 0
        L_0x0010:
            r2 = 0
            r4 = 1085276160(0x40b00000, float:5.5)
            if (r7 == 0) goto L_0x004e
            if (r7 == r1) goto L_0x004b
            r1 = 2
            if (r7 == r1) goto L_0x004e
            r1 = 4
            if (r7 == r1) goto L_0x0043
            r1 = 5
            if (r7 == r1) goto L_0x0038
            r1 = 6
            if (r7 == r1) goto L_0x002d
            r5 = 8
            if (r7 == r5) goto L_0x0043
            r5 = 9
            if (r7 == r5) goto L_0x004e
            goto L_0x0056
        L_0x002d:
            double r0 = (double) r5
            int r5 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r5 <= 0) goto L_0x0056
            double r5 = (double) r6
            int r5 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r5 <= 0) goto L_0x0056
            return r4
        L_0x0038:
            double r0 = (double) r5
            int r5 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r5 >= 0) goto L_0x0056
            double r5 = (double) r6
            int r5 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r5 <= 0) goto L_0x0056
            return r4
        L_0x0043:
            if (r0 == 0) goto L_0x004a
            double r5 = (double) r6
            int r5 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r5 < 0) goto L_0x0056
        L_0x004a:
            return r4
        L_0x004b:
            if (r0 == 0) goto L_0x0056
            return r4
        L_0x004e:
            if (r0 == 0) goto L_0x0058
            double r5 = (double) r6
            int r5 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r5 > 0) goto L_0x0056
            goto L_0x0058
        L_0x0056:
            r5 = 0
            return r5
        L_0x0058:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.classifier.DirectionEvaluator.evaluate(float, float, int):float");
    }
}
