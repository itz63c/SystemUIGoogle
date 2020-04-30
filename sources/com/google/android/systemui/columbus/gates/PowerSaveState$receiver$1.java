package com.google.android.systemui.columbus.gates;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/* compiled from: PowerSaveState.kt */
public final class PowerSaveState$receiver$1 extends BroadcastReceiver {
    final /* synthetic */ PowerSaveState this$0;

    PowerSaveState$receiver$1(PowerSaveState powerSaveState) {
        this.this$0 = powerSaveState;
    }

    public void onReceive(Context context, Intent intent) {
        this.this$0.refreshStatus();
        this.this$0.notifyListener();
    }
}
