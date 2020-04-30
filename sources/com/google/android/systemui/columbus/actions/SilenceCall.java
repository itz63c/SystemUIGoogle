package com.google.android.systemui.columbus.actions;

import android.content.Context;
import android.net.Uri;
import android.provider.Settings.Secure;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import com.google.android.systemui.columbus.ColumbusContentObserver;
import com.google.android.systemui.columbus.ColumbusContentObserver.Factory;
import com.google.android.systemui.columbus.sensors.GestureSensor.DetectionProperties;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: SilenceCall.kt */
public final class SilenceCall extends Action {
    /* access modifiers changed from: private */
    public boolean isPhoneRinging;
    private final SilenceCall$phoneStateListener$1 phoneStateListener = new SilenceCall$phoneStateListener$1(this);
    private final ColumbusContentObserver settingsObserver;
    private boolean silenceSettingEnabled;
    private final TelecomManager telecomManager;
    private final TelephonyManager telephonyManager;

    /* access modifiers changed from: private */
    public final boolean isPhoneRinging(int i) {
        return i == 1;
    }

    public SilenceCall(Context context, Factory factory) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(factory, "settingsObserverFactory");
        super(context, null);
        this.telecomManager = (TelecomManager) context.getSystemService(TelecomManager.class);
        this.telephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        Uri uriFor = Secure.getUriFor("assist_gesture_silence_alerts_enabled");
        Intrinsics.checkExpressionValueIsNotNull(uriFor, "Settings.Secure.getUriFo…E_SILENCE_ALERTS_ENABLED)");
        ColumbusContentObserver create = factory.create(uriFor, new SilenceCall$settingsObserver$1(this));
        this.settingsObserver = create;
        create.activate();
        updatePhoneStateListener();
    }

    /* access modifiers changed from: private */
    public final void updatePhoneStateListener() {
        boolean z = true;
        int i = 0;
        if (Secure.getIntForUser(getContext().getContentResolver(), "assist_gesture_silence_alerts_enabled", 1, -2) == 0) {
            z = false;
        }
        if (z != this.silenceSettingEnabled) {
            this.silenceSettingEnabled = z;
            if (z) {
                i = 32;
            }
            TelephonyManager telephonyManager2 = this.telephonyManager;
            if (telephonyManager2 != null) {
                telephonyManager2.listen(this.phoneStateListener, i);
                this.isPhoneRinging = isPhoneRinging(telephonyManager2.getCallState());
            }
            notifyListener();
        }
    }

    public boolean isAvailable() {
        if (this.silenceSettingEnabled) {
            return this.isPhoneRinging;
        }
        return false;
    }

    public void onProgress(int i, DetectionProperties detectionProperties) {
        if (i == 3) {
            TelecomManager telecomManager2 = this.telecomManager;
            if (telecomManager2 != null) {
                telecomManager2.silenceRinger();
            }
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" [silenceSettingEnabled -> ");
        sb.append(this.silenceSettingEnabled);
        sb.append("]");
        return sb.toString();
    }
}
