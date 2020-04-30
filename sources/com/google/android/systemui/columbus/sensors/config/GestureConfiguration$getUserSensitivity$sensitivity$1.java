package com.google.android.systemui.columbus.sensors.config;

import android.provider.Settings.Secure;
import java.util.function.Supplier;

/* compiled from: GestureConfiguration.kt */
final class GestureConfiguration$getUserSensitivity$sensitivity$1<T> implements Supplier<T> {
    final /* synthetic */ GestureConfiguration this$0;

    GestureConfiguration$getUserSensitivity$sensitivity$1(GestureConfiguration gestureConfiguration) {
        this.this$0 = gestureConfiguration;
    }

    public final float get() {
        return Secure.getFloatForUser(this.this$0.context.getContentResolver(), "assist_gesture_sensitivity", 0.5f, -2);
    }
}
