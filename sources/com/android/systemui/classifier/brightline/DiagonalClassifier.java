package com.android.systemui.classifier.brightline;

import com.android.systemui.util.DeviceConfigProxy;

class DiagonalClassifier extends FalsingClassifier {
    private final float mHorizontalAngleRange;
    private final float mVerticalAngleRange;

    private float normalizeAngle(float f) {
        if (f < 0.0f) {
            return (f % 6.2831855f) + 6.2831855f;
        }
        if (f > 6.2831855f) {
            f %= 6.2831855f;
        }
        return f;
    }

    DiagonalClassifier(FalsingDataProvider falsingDataProvider, DeviceConfigProxy deviceConfigProxy) {
        super(falsingDataProvider);
        String str = "systemui";
        String str2 = "brightline_falsing_diagonal_horizontal_angle_range";
        this.mHorizontalAngleRange = deviceConfigProxy.getFloat(str, str2, 0.08726646f);
        this.mVerticalAngleRange = deviceConfigProxy.getFloat(str, str2, 0.08726646f);
    }

    /* access modifiers changed from: 0000 */
    public boolean isFalseTouch() {
        float angle = getAngle();
        boolean z = false;
        if (angle == Float.MAX_VALUE) {
            return false;
        }
        if (!(getInteractionType() == 5 || getInteractionType() == 6)) {
            float f = this.mHorizontalAngleRange;
            float f2 = 0.7853982f - f;
            float f3 = f + 0.7853982f;
            if (isVertical()) {
                float f4 = this.mVerticalAngleRange;
                f2 = 0.7853982f - f4;
                f3 = f4 + 0.7853982f;
            }
            if (angleBetween(angle, f2, f3) || angleBetween(angle, f2 + 1.5707964f, f3 + 1.5707964f) || angleBetween(angle, f2 - 1.5707964f, f3 - 1.5707964f) || angleBetween(angle, f2 + 3.1415927f, f3 + 3.1415927f)) {
                z = true;
            }
        }
        return z;
    }

    /* access modifiers changed from: 0000 */
    public String getReason() {
        return String.format(null, "{angle=%f, vertical=%s}", new Object[]{Float.valueOf(getAngle()), Boolean.valueOf(isVertical())});
    }

    private boolean angleBetween(float f, float f2, float f3) {
        float normalizeAngle = normalizeAngle(f2);
        float normalizeAngle2 = normalizeAngle(f3);
        boolean z = true;
        if (normalizeAngle > normalizeAngle2) {
            if (f < normalizeAngle && f > normalizeAngle2) {
                z = false;
            }
            return z;
        }
        if (f < normalizeAngle || f > normalizeAngle2) {
            z = false;
        }
        return z;
    }
}
