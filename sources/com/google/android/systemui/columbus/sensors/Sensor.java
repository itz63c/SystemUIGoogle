package com.google.android.systemui.columbus.sensors;

/* compiled from: Sensor.kt */
public interface Sensor {
    boolean isListening();

    void startListening(boolean z);

    void stopListening();
}
