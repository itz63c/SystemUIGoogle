package com.android.systemui.util.animation;

import android.graphics.Rect;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import kotlin.jvm.internal.FloatCompanionObject;

/* compiled from: FloatProperties.kt */
public final class FloatProperties$Companion$RECT_Y$1 extends FloatPropertyCompat<Rect> {
    FloatProperties$Companion$RECT_Y$1(String str) {
        super(str);
    }

    public void setValue(Rect rect, float f) {
        if (rect != null) {
            rect.offsetTo(rect.left, (int) f);
        }
    }

    public float getValue(Rect rect) {
        return rect != null ? (float) rect.top : -FloatCompanionObject.INSTANCE.getMAX_VALUE();
    }
}
