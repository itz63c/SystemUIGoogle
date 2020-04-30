package com.google.android.systemui.columbus.actions;

import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.UserHandle;
import android.provider.Settings.Secure;
import android.util.Log;
import com.google.android.systemui.columbus.ColumbusContentObserver;
import com.google.android.systemui.columbus.ColumbusContentObserver.Factory;
import com.google.android.systemui.columbus.sensors.GestureSensor.DetectionProperties;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: DeskClockAction.kt */
public abstract class DeskClockAction extends Action {
    /* access modifiers changed from: private */
    public boolean alertFiring;
    private final DeskClockAction$alertReceiver$1 alertReceiver = new DeskClockAction$alertReceiver$1(this);
    private boolean receiverRegistered;
    private final ColumbusContentObserver settingsObserver;

    /* access modifiers changed from: protected */
    public abstract Intent createDismissIntent();

    /* access modifiers changed from: protected */
    public abstract String getAlertAction();

    /* access modifiers changed from: protected */
    public abstract String getDoneAction();

    public DeskClockAction(Context context, Factory factory) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(factory, "settingsObserverFactory");
        super(context, null);
        Uri uriFor = Secure.getUriFor("assist_gesture_silence_alerts_enabled");
        Intrinsics.checkExpressionValueIsNotNull(uriFor, "Settings.Secure.getUriFo…E_SILENCE_ALERTS_ENABLED)");
        ColumbusContentObserver create = factory.create(uriFor, new DeskClockAction$settingsObserver$1(this));
        this.settingsObserver = create;
        create.activate();
        updateBroadcastReceiver();
    }

    /* access modifiers changed from: private */
    public final void updateBroadcastReceiver() {
        boolean z = false;
        this.alertFiring = false;
        if (this.receiverRegistered) {
            getContext().unregisterReceiver(this.alertReceiver);
            this.receiverRegistered = false;
        }
        if (Secure.getIntForUser(getContext().getContentResolver(), "assist_gesture_silence_alerts_enabled", 1, -2) != 0) {
            z = true;
        }
        if (z) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(getAlertAction());
            intentFilter.addAction(getDoneAction());
            getContext().registerReceiverAsUser(this.alertReceiver, UserHandle.CURRENT, intentFilter, "com.android.systemui.permission.SEND_ALERT_BROADCASTS", null);
            this.receiverRegistered = true;
        }
        notifyListener();
    }

    public boolean isAvailable() {
        return this.alertFiring;
    }

    public void onProgress(int i, DetectionProperties detectionProperties) {
        if (i == 3) {
            try {
                Intent createDismissIntent = createDismissIntent();
                ActivityOptions makeBasic = ActivityOptions.makeBasic();
                makeBasic.setDisallowEnterPictureInPictureWhileLaunching(true);
                createDismissIntent.setFlags(268435456);
                StringBuilder sb = new StringBuilder();
                sb.append("android-app://");
                sb.append(getContext().getPackageName());
                createDismissIntent.putExtra("android.intent.extra.REFERRER", Uri.parse(sb.toString()));
                getContext().startActivityAsUser(createDismissIntent, makeBasic.toBundle(), UserHandle.CURRENT);
            } catch (ActivityNotFoundException e) {
                Log.e("Columbus/DeskClockAction", "Failed to dismiss alert", e);
            }
            this.alertFiring = false;
            notifyListener();
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" [receiverRegistered -> ");
        sb.append(this.receiverRegistered);
        sb.append("]");
        return sb.toString();
    }
}
