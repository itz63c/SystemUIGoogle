package com.google.android.systemui.elmyra.feedback;

import android.media.AudioAttributes;
import android.media.AudioAttributes.Builder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import com.google.android.systemui.elmyra.sensors.GestureSensor.DetectionProperties;

public class HapticClick implements FeedbackEffect {
    private static final AudioAttributes SONIFICATION_AUDIO_ATTRIBUTES = new Builder().setContentType(4).setUsage(13).build();
    private int mLastGestureStage;
    private final VibrationEffect mProgressVibrationEffect = VibrationEffect.get(5);
    private final VibrationEffect mResolveVibrationEffect = VibrationEffect.get(0);
    private final Vibrator mVibrator;

    public void onRelease() {
    }

    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:4:0x0032 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public HapticClick(android.content.Context r5) {
        /*
            r4 = this;
            r4.<init>()
            java.lang.String r0 = "vibrator"
            java.lang.Object r0 = r5.getSystemService(r0)
            android.os.Vibrator r0 = (android.os.Vibrator) r0
            r4.mVibrator = r0
            r0 = 0
            android.os.VibrationEffect r0 = android.os.VibrationEffect.get(r0)
            r4.mResolveVibrationEffect = r0
            r0 = 5
            android.os.VibrationEffect r0 = android.os.VibrationEffect.get(r0)
            r4.mProgressVibrationEffect = r0
            android.os.Vibrator r0 = r4.mVibrator
            if (r0 == 0) goto L_0x0045
            android.content.res.Resources r0 = r5.getResources()     // Catch:{ NotFoundException -> 0x0032 }
            int r1 = com.android.systemui.C2012R$integer.elmyra_progress_always_on_vibration     // Catch:{ NotFoundException -> 0x0032 }
            int r0 = r0.getInteger(r1)     // Catch:{ NotFoundException -> 0x0032 }
            android.os.Vibrator r1 = r4.mVibrator     // Catch:{ NotFoundException -> 0x0032 }
            android.os.VibrationEffect r2 = r4.mProgressVibrationEffect     // Catch:{ NotFoundException -> 0x0032 }
            android.media.AudioAttributes r3 = SONIFICATION_AUDIO_ATTRIBUTES     // Catch:{ NotFoundException -> 0x0032 }
            r1.setAlwaysOnEffect(r0, r2, r3)     // Catch:{ NotFoundException -> 0x0032 }
        L_0x0032:
            android.content.res.Resources r5 = r5.getResources()     // Catch:{ NotFoundException -> 0x0045 }
            int r0 = com.android.systemui.C2012R$integer.elmyra_resolve_always_on_vibration     // Catch:{ NotFoundException -> 0x0045 }
            int r5 = r5.getInteger(r0)     // Catch:{ NotFoundException -> 0x0045 }
            android.os.Vibrator r0 = r4.mVibrator     // Catch:{ NotFoundException -> 0x0045 }
            android.os.VibrationEffect r4 = r4.mResolveVibrationEffect     // Catch:{ NotFoundException -> 0x0045 }
            android.media.AudioAttributes r1 = SONIFICATION_AUDIO_ATTRIBUTES     // Catch:{ NotFoundException -> 0x0045 }
            r0.setAlwaysOnEffect(r5, r4, r1)     // Catch:{ NotFoundException -> 0x0045 }
        L_0x0045:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.systemui.elmyra.feedback.HapticClick.<init>(android.content.Context):void");
    }

    public void onProgress(float f, int i) {
        if (this.mLastGestureStage != 2 && i == 2) {
            Vibrator vibrator = this.mVibrator;
            if (vibrator != null) {
                vibrator.vibrate(this.mProgressVibrationEffect, SONIFICATION_AUDIO_ATTRIBUTES);
            }
        }
        this.mLastGestureStage = i;
    }

    public void onResolve(DetectionProperties detectionProperties) {
        if (detectionProperties == null || !detectionProperties.isHapticConsumed()) {
            Vibrator vibrator = this.mVibrator;
            if (vibrator != null) {
                vibrator.vibrate(this.mResolveVibrationEffect, SONIFICATION_AUDIO_ATTRIBUTES);
            }
        }
    }
}
