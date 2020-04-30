package com.google.android.systemui.columbus.gates;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

/* compiled from: KeyguardProximity.kt */
public final class KeyguardProximity$sensorListener$1 implements SensorEventListener {
    final /* synthetic */ KeyguardProximity this$0;

    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    KeyguardProximity$sensorListener$1(KeyguardProximity keyguardProximity) {
        this.this$0 = keyguardProximity;
    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent != null) {
            this.this$0.handleSensorEvent(sensorEvent);
        }
    }
}
