package com.google.android.systemui.columbus.gates;

import android.content.Context;
import android.telephony.TelephonyManager;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: TelephonyActivity.kt */
public final class TelephonyActivity extends Gate {
    /* access modifiers changed from: private */
    public boolean isCallBlocked;
    private final TelephonyActivity$phoneStateListener$1 phoneStateListener = new TelephonyActivity$phoneStateListener$1(this);
    private final TelephonyManager telephonyManager;

    public TelephonyActivity(Context context) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        super(context);
        this.telephonyManager = (TelephonyManager) context.getSystemService("phone");
    }

    /* access modifiers changed from: protected */
    public void onActivate() {
        TelephonyManager telephonyManager2 = this.telephonyManager;
        this.isCallBlocked = isCallBlocked(telephonyManager2 != null ? Integer.valueOf(telephonyManager2.getCallState()) : null);
        TelephonyManager telephonyManager3 = this.telephonyManager;
        if (telephonyManager3 != null) {
            telephonyManager3.listen(this.phoneStateListener, 32);
        }
    }

    /* access modifiers changed from: protected */
    public void onDeactivate() {
        TelephonyManager telephonyManager2 = this.telephonyManager;
        if (telephonyManager2 != null) {
            telephonyManager2.listen(this.phoneStateListener, 0);
        }
    }

    /* access modifiers changed from: protected */
    public boolean isBlocked() {
        return this.isCallBlocked;
    }

    /* access modifiers changed from: private */
    public final boolean isCallBlocked(Integer num) {
        return num != null && num.intValue() == 2;
    }
}
