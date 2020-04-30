package com.google.android.systemui.elmyra.feedback;

import android.content.Context;
import android.os.PowerManager;
import android.os.SystemClock;
import com.android.systemui.Dependency;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.google.android.systemui.elmyra.sensors.GestureSensor.DetectionProperties;

public class UserActivity implements FeedbackEffect {
    private final KeyguardStateController mKeyguardStateController = ((KeyguardStateController) Dependency.get(KeyguardStateController.class));
    private int mLastStage = 0;
    private final PowerManager mPowerManager;
    private int mTriggerCount = 0;

    public void onRelease() {
    }

    public UserActivity(Context context) {
        this.mPowerManager = (PowerManager) context.getSystemService(PowerManager.class);
    }

    public void onProgress(float f, int i) {
        if (i != this.mLastStage && i == 2 && !this.mKeyguardStateController.isShowing()) {
            PowerManager powerManager = this.mPowerManager;
            if (powerManager != null) {
                powerManager.userActivity(SystemClock.uptimeMillis(), 0, 0);
                this.mTriggerCount++;
            }
        }
        this.mLastStage = i;
    }

    public void onResolve(DetectionProperties detectionProperties) {
        this.mTriggerCount--;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" [mTriggerCount -> ");
        sb.append(this.mTriggerCount);
        sb.append("]");
        return sb.toString();
    }
}
