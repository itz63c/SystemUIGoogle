package com.google.android.systemui.columbus.gates;

import android.content.Context;
import android.content.IntentFilter;
import android.os.PowerManager;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: PowerSaveState.kt */
public final class PowerSaveState extends Gate {
    private boolean batterySaverEnabled;
    private boolean isDeviceInteractive;
    private final Object lock = new Object();
    private final PowerManager powerManager;
    private final PowerSaveState$receiver$1 receiver;

    public PowerSaveState(Context context) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        super(context);
        this.powerManager = (PowerManager) context.getSystemService("power");
        this.receiver = new PowerSaveState$receiver$1(this);
    }

    /* access modifiers changed from: private */
    public final void refreshStatus() {
        boolean z;
        synchronized (this.lock) {
            PowerManager powerManager2 = this.powerManager;
            boolean z2 = false;
            if (powerManager2 != null) {
                android.os.PowerSaveState powerSaveState = powerManager2.getPowerSaveState(13);
                if (powerSaveState != null && powerSaveState.batterySaverEnabled) {
                    z = true;
                    this.batterySaverEnabled = z;
                    PowerManager powerManager3 = this.powerManager;
                    if (powerManager3 != null && powerManager3.isInteractive()) {
                        z2 = true;
                    }
                    this.isDeviceInteractive = z2;
                    Unit unit = Unit.INSTANCE;
                }
            }
            z = false;
            this.batterySaverEnabled = z;
            PowerManager powerManager32 = this.powerManager;
            z2 = true;
            this.isDeviceInteractive = z2;
            Unit unit2 = Unit.INSTANCE;
        }
    }

    /* access modifiers changed from: protected */
    public void onActivate() {
        IntentFilter intentFilter = new IntentFilter("android.os.action.POWER_SAVE_MODE_CHANGED");
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        getContext().registerReceiver(this.receiver, intentFilter);
        refreshStatus();
    }

    /* access modifiers changed from: protected */
    public void onDeactivate() {
        getContext().unregisterReceiver(this.receiver);
    }

    /* access modifiers changed from: protected */
    public boolean isBlocked() {
        return shouldBlock();
    }

    private final boolean shouldBlock() {
        boolean z;
        synchronized (this.lock) {
            z = this.batterySaverEnabled && !this.isDeviceInteractive;
        }
        return z;
    }
}
