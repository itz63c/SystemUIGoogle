package com.google.android.systemui.elmyra.actions;

import android.content.Context;
import android.net.Uri;
import android.provider.Settings.Secure;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import com.google.android.systemui.elmyra.UserContentObserver;
import com.google.android.systemui.elmyra.sensors.GestureSensor.DetectionProperties;
import java.util.function.Consumer;

public class SilenceCall extends Action {
    /* access modifiers changed from: private */
    public boolean mIsPhoneRinging;
    private final PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        public void onCallStateChanged(int i, String str) {
            boolean access$000 = SilenceCall.this.isPhoneRinging(i);
            if (SilenceCall.this.mIsPhoneRinging != access$000) {
                SilenceCall.this.mIsPhoneRinging = access$000;
                SilenceCall.this.notifyListener();
            }
        }
    };
    private boolean mSilenceSettingEnabled;
    private final TelecomManager mTelecomManager;
    private final TelephonyManager mTelephonyManager;

    /* access modifiers changed from: private */
    public boolean isPhoneRinging(int i) {
        return i == 1;
    }

    public SilenceCall(Context context) {
        super(context, null);
        this.mTelecomManager = (TelecomManager) context.getSystemService(TelecomManager.class);
        this.mTelephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        updatePhoneStateListener();
        new UserContentObserver(getContext(), Secure.getUriFor("assist_gesture_silence_alerts_enabled"), new Consumer() {
            public final void accept(Object obj) {
                SilenceCall.this.lambda$new$0$SilenceCall((Uri) obj);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$SilenceCall(Uri uri) {
        updatePhoneStateListener();
    }

    private void updatePhoneStateListener() {
        boolean z = true;
        int i = 0;
        if (Secure.getIntForUser(getContext().getContentResolver(), "assist_gesture_silence_alerts_enabled", 1, -2) == 0) {
            z = false;
        }
        if (z != this.mSilenceSettingEnabled) {
            this.mSilenceSettingEnabled = z;
            if (z) {
                i = 32;
            }
            this.mTelephonyManager.listen(this.mPhoneStateListener, i);
            this.mIsPhoneRinging = isPhoneRinging(this.mTelephonyManager.getCallState());
            notifyListener();
        }
    }

    public boolean isAvailable() {
        if (this.mSilenceSettingEnabled) {
            return this.mIsPhoneRinging;
        }
        return false;
    }

    public void onTrigger(DetectionProperties detectionProperties) {
        this.mTelecomManager.silenceRinger();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" [mSilenceSettingEnabled -> ");
        sb.append(this.mSilenceSettingEnabled);
        sb.append("]");
        return sb.toString();
    }
}
