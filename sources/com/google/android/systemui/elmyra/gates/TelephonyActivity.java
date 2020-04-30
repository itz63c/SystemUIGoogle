package com.google.android.systemui.elmyra.gates;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class TelephonyActivity extends Gate {
    /* access modifiers changed from: private */
    public boolean mIsCallBlocked;
    private final PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        public void onCallStateChanged(int i, String str) {
            boolean access$000 = TelephonyActivity.this.isCallBlocked(i);
            if (access$000 != TelephonyActivity.this.mIsCallBlocked) {
                TelephonyActivity.this.mIsCallBlocked = access$000;
                TelephonyActivity.this.notifyListener();
            }
        }
    };
    private final TelephonyManager mTelephonyManager;

    /* access modifiers changed from: private */
    public boolean isCallBlocked(int i) {
        return i == 2;
    }

    public TelephonyActivity(Context context) {
        super(context);
        this.mTelephonyManager = (TelephonyManager) context.getSystemService("phone");
    }

    /* access modifiers changed from: protected */
    public void onActivate() {
        this.mIsCallBlocked = isCallBlocked(this.mTelephonyManager.getCallState());
        this.mTelephonyManager.listen(this.mPhoneStateListener, 32);
    }

    /* access modifiers changed from: protected */
    public void onDeactivate() {
        this.mTelephonyManager.listen(this.mPhoneStateListener, 0);
    }

    /* access modifiers changed from: protected */
    public boolean isBlocked() {
        return this.mIsCallBlocked;
    }
}
