package com.google.android.systemui.columbus.gates;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ChargingState.kt */
public final class ChargingState extends TransientGate {
    /* access modifiers changed from: private */
    public final long gateDuration;
    private final ChargingState$powerReceiver$1 powerReceiver = new ChargingState$powerReceiver$1(this);

    public ChargingState(Context context, Handler handler, long j) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(handler, "handler");
        super(context, handler);
        this.gateDuration = j;
    }

    /* access modifiers changed from: protected */
    public void onActivate() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.ACTION_POWER_CONNECTED");
        intentFilter.addAction("android.intent.action.ACTION_POWER_DISCONNECTED");
        getContext().registerReceiver(this.powerReceiver, intentFilter);
    }

    /* access modifiers changed from: protected */
    public void onDeactivate() {
        getContext().unregisterReceiver(this.powerReceiver);
    }
}
