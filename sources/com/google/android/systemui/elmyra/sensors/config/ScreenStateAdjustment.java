package com.google.android.systemui.elmyra.sensors.config;

import android.content.Context;
import android.os.PowerManager;
import android.util.TypedValue;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.Dependency;

public class ScreenStateAdjustment extends Adjustment {
    private final KeyguardUpdateMonitorCallback mKeyguardUpdateMonitorCallback = new KeyguardUpdateMonitorCallback() {
        public void onStartedWakingUp() {
            ScreenStateAdjustment.this.onSensitivityChanged();
        }

        public void onFinishedGoingToSleep(int i) {
            ScreenStateAdjustment.this.onSensitivityChanged();
        }
    };
    private final PowerManager mPowerManager = ((PowerManager) getContext().getSystemService("power"));
    private final float mScreenOffAdjustment;

    public ScreenStateAdjustment(Context context) {
        super(context);
        TypedValue typedValue = new TypedValue();
        context.getResources().getValue(C2009R$dimen.elmyra_screen_off_adjustment, typedValue, true);
        this.mScreenOffAdjustment = typedValue.getFloat();
        ((KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class)).registerCallback(this.mKeyguardUpdateMonitorCallback);
    }

    public float adjustSensitivity(float f) {
        return !this.mPowerManager.isInteractive() ? f + this.mScreenOffAdjustment : f;
    }
}
