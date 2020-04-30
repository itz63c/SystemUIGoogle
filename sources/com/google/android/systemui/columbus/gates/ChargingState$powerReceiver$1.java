package com.google.android.systemui.columbus.gates;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ChargingState.kt */
public final class ChargingState$powerReceiver$1 extends BroadcastReceiver {
    final /* synthetic */ ChargingState this$0;

    ChargingState$powerReceiver$1(ChargingState chargingState) {
        this.this$0 = chargingState;
    }

    public void onReceive(Context context, Intent intent) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(intent, "intent");
        ChargingState chargingState = this.this$0;
        chargingState.blockForMillis(chargingState.gateDuration);
    }
}
