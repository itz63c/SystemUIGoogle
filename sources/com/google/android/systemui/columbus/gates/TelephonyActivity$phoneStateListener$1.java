package com.google.android.systemui.columbus.gates;

import android.telephony.PhoneStateListener;

/* compiled from: TelephonyActivity.kt */
public final class TelephonyActivity$phoneStateListener$1 extends PhoneStateListener {
    final /* synthetic */ TelephonyActivity this$0;

    TelephonyActivity$phoneStateListener$1(TelephonyActivity telephonyActivity) {
        this.this$0 = telephonyActivity;
    }

    public void onCallStateChanged(int i, String str) {
        boolean access$isCallBlocked = this.this$0.isCallBlocked(Integer.valueOf(i));
        if (access$isCallBlocked != this.this$0.isCallBlocked) {
            this.this$0.isCallBlocked = access$isCallBlocked;
            this.this$0.notifyListener();
        }
    }
}
