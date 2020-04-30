package com.google.android.systemui.elmyra.gates;

import android.content.Context;
import android.os.PowerManager;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.Dependency;

public class PowerState extends Gate {
    private final KeyguardUpdateMonitorCallback mKeyguardUpdateMonitorCallback = new KeyguardUpdateMonitorCallback() {
        public void onStartedWakingUp() {
            PowerState.this.notifyListener();
        }

        public void onFinishedGoingToSleep(int i) {
            PowerState.this.notifyListener();
        }
    };
    private final PowerManager mPowerManager;

    public PowerState(Context context) {
        super(context);
        this.mPowerManager = (PowerManager) context.getSystemService("power");
    }

    /* access modifiers changed from: protected */
    public void onActivate() {
        ((KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class)).registerCallback(this.mKeyguardUpdateMonitorCallback);
    }

    /* access modifiers changed from: protected */
    public void onDeactivate() {
        ((KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class)).removeCallback(this.mKeyguardUpdateMonitorCallback);
    }

    /* access modifiers changed from: protected */
    public boolean isBlocked() {
        return !this.mPowerManager.isInteractive();
    }
}
