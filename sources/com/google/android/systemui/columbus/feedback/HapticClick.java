package com.google.android.systemui.columbus.feedback;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioAttributes.Builder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import com.google.android.systemui.columbus.sensors.GestureSensor.DetectionProperties;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: HapticClick.kt */
public final class HapticClick implements FeedbackEffect {
    private static final AudioAttributes SONIFICATION_AUDIO_ATTRIBUTES = new Builder().setContentType(4).setUsage(13).build();
    private final VibrationEffect progressVibrationEffect = VibrationEffect.get(0);
    private final VibrationEffect resolveVibrationEffect = VibrationEffect.get(5);
    private final Vibrator vibrator;

    public HapticClick(Context context) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        this.vibrator = (Vibrator) context.getSystemService("vibrator");
    }

    public void onProgress(int i, DetectionProperties detectionProperties) {
        if (detectionProperties == null || !detectionProperties.isHapticConsumed()) {
            Vibrator vibrator2 = this.vibrator;
            if (vibrator2 == null) {
                return;
            }
            if (i == 3) {
                vibrator2.vibrate(this.resolveVibrationEffect, SONIFICATION_AUDIO_ATTRIBUTES);
            } else if (i == 1) {
                vibrator2.vibrate(this.progressVibrationEffect, SONIFICATION_AUDIO_ATTRIBUTES);
            }
        }
    }
}
