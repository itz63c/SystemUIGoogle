package com.google.android.systemui.elmyra.gates;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import com.android.internal.annotations.GuardedBy;
import com.android.systemui.Dependency;
import com.android.systemui.broadcast.BroadcastDispatcher;

public class PowerSaveState extends Gate {
    @GuardedBy({"mLock"})
    private boolean mBatterySaverEnabled;
    private BroadcastDispatcher mBroadcastDispatcher;
    @GuardedBy({"mLock"})
    private boolean mIsDeviceInteractive;
    private final Object mLock = new Object();
    private final PowerManager mPowerManager;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            PowerSaveState.this.refreshStatus();
            PowerSaveState.this.notifyListener();
        }
    };

    public PowerSaveState(Context context) {
        super(context);
        this.mPowerManager = (PowerManager) context.getSystemService("power");
        this.mBroadcastDispatcher = (BroadcastDispatcher) Dependency.get(BroadcastDispatcher.class);
    }

    /* access modifiers changed from: private */
    public void refreshStatus() {
        synchronized (this.mLock) {
            this.mBatterySaverEnabled = this.mPowerManager.getPowerSaveState(13).batterySaverEnabled;
            this.mIsDeviceInteractive = this.mPowerManager.isInteractive();
        }
    }

    /* access modifiers changed from: protected */
    public void onActivate() {
        IntentFilter intentFilter = new IntentFilter("android.os.action.POWER_SAVE_MODE_CHANGED");
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        this.mBroadcastDispatcher.registerReceiver(this.mReceiver, intentFilter);
        refreshStatus();
    }

    /* access modifiers changed from: protected */
    public void onDeactivate() {
        this.mBroadcastDispatcher.unregisterReceiver(this.mReceiver);
    }

    /* access modifiers changed from: protected */
    public boolean isBlocked() {
        return shouldBlock();
    }

    private boolean shouldBlock() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mBatterySaverEnabled && !this.mIsDeviceInteractive;
        }
        return z;
    }
}
