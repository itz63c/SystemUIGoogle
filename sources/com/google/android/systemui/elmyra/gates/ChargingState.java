package com.google.android.systemui.elmyra.gates;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.android.systemui.C2012R$integer;

public class ChargingState extends TransientGate {
    private final BroadcastReceiver mPowerReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            ChargingState.this.block();
        }
    };

    public ChargingState(Context context) {
        super(context, (long) context.getResources().getInteger(C2012R$integer.elmyra_charging_gate_duration));
    }

    /* access modifiers changed from: protected */
    public void onActivate() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.ACTION_POWER_CONNECTED");
        intentFilter.addAction("android.intent.action.ACTION_POWER_DISCONNECTED");
        getContext().registerReceiver(this.mPowerReceiver, intentFilter);
    }

    /* access modifiers changed from: protected */
    public void onDeactivate() {
        getContext().unregisterReceiver(this.mPowerReceiver);
    }
}
