package com.google.android.systemui.columbus.feedback;

import android.content.Context;
import android.os.PowerManager;
import android.os.SystemClock;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.google.android.systemui.columbus.sensors.GestureSensor.DetectionProperties;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: UserActivity.kt */
public final class UserActivity implements FeedbackEffect {
    private final KeyguardStateController keyguardStateController;
    private int lastStage;
    private final PowerManager powerManager;
    private int triggerCount;

    public UserActivity(Context context, KeyguardStateController keyguardStateController2) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(keyguardStateController2, "keyguardStateController");
        this.keyguardStateController = keyguardStateController2;
        this.powerManager = (PowerManager) context.getSystemService(PowerManager.class);
    }

    public void onProgress(int i, DetectionProperties detectionProperties) {
        if (i != this.lastStage && i == 1 && !this.keyguardStateController.isShowing()) {
            PowerManager powerManager2 = this.powerManager;
            if (powerManager2 != null) {
                powerManager2.userActivity(SystemClock.uptimeMillis(), 0, 0);
                this.triggerCount++;
                this.lastStage = i;
            }
        }
        if (i == 3) {
            this.triggerCount--;
        }
        this.lastStage = i;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" [triggerCount -> ");
        sb.append(this.triggerCount);
        sb.append("]");
        return sb.toString();
    }
}
