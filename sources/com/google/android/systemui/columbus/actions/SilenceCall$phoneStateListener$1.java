package com.google.android.systemui.columbus.actions;

import android.telephony.PhoneStateListener;

/* compiled from: SilenceCall.kt */
public final class SilenceCall$phoneStateListener$1 extends PhoneStateListener {
    final /* synthetic */ SilenceCall this$0;

    SilenceCall$phoneStateListener$1(SilenceCall silenceCall) {
        this.this$0 = silenceCall;
    }

    public void onCallStateChanged(int i, String str) {
        boolean access$isPhoneRinging = this.this$0.isPhoneRinging(i);
        if (this.this$0.isPhoneRinging != access$isPhoneRinging) {
            this.this$0.isPhoneRinging = access$isPhoneRinging;
            this.this$0.notifyListener();
        }
    }
}
