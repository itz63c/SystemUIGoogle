package com.google.android.systemui.columbus.gates;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: UsbState.kt */
public final class UsbState extends TransientGate {
    /* access modifiers changed from: private */
    public final long gateDuration;
    /* access modifiers changed from: private */
    public boolean usbConnected;
    private final UsbState$usbReceiver$1 usbReceiver = new UsbState$usbReceiver$1(this);

    public UsbState(Context context, Handler handler, long j) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(handler, "handler");
        super(context, handler);
        this.gateDuration = j;
    }

    /* access modifiers changed from: protected */
    public void onActivate() {
        IntentFilter intentFilter = new IntentFilter("android.hardware.usb.action.USB_STATE");
        Intent registerReceiver = getContext().registerReceiver(null, intentFilter);
        if (registerReceiver != null) {
            this.usbConnected = registerReceiver.getBooleanExtra("connected", false);
        }
        getContext().registerReceiver(this.usbReceiver, intentFilter);
    }

    /* access modifiers changed from: protected */
    public void onDeactivate() {
        getContext().unregisterReceiver(this.usbReceiver);
    }
}
