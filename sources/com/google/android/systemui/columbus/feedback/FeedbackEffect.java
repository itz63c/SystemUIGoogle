package com.google.android.systemui.columbus.feedback;

import com.google.android.systemui.columbus.sensors.GestureSensor.DetectionProperties;

/* compiled from: FeedbackEffect.kt */
public interface FeedbackEffect {
    void onProgress(int i, DetectionProperties detectionProperties);
}
