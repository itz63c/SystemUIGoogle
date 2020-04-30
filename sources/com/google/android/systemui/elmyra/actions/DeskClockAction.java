package com.google.android.systemui.elmyra.actions;

import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.UserHandle;
import android.provider.Settings.Secure;
import android.util.Log;
import com.google.android.systemui.elmyra.UserContentObserver;
import com.google.android.systemui.elmyra.sensors.GestureSensor.DetectionProperties;
import java.util.function.Consumer;

abstract class DeskClockAction extends Action {
    /* access modifiers changed from: private */
    public boolean mAlertFiring;
    private final BroadcastReceiver mAlertReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DeskClockAction.this.getAlertAction())) {
                DeskClockAction.this.mAlertFiring = true;
            } else if (intent.getAction().equals(DeskClockAction.this.getDoneAction())) {
                DeskClockAction.this.mAlertFiring = false;
            }
            DeskClockAction.this.notifyListener();
        }
    };
    private boolean mReceiverRegistered;

    /* access modifiers changed from: protected */
    public abstract Intent createDismissIntent();

    /* access modifiers changed from: protected */
    public abstract String getAlertAction();

    /* access modifiers changed from: protected */
    public abstract String getDoneAction();

    DeskClockAction(Context context) {
        super(context, null);
        updateBroadcastReceiver();
        new UserContentObserver(getContext(), Secure.getUriFor("assist_gesture_silence_alerts_enabled"), new Consumer() {
            public final void accept(Object obj) {
                DeskClockAction.this.lambda$new$0$DeskClockAction((Uri) obj);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$DeskClockAction(Uri uri) {
        updateBroadcastReceiver();
    }

    private void updateBroadcastReceiver() {
        boolean z = false;
        this.mAlertFiring = false;
        if (this.mReceiverRegistered) {
            getContext().unregisterReceiver(this.mAlertReceiver);
            this.mReceiverRegistered = false;
        }
        if (Secure.getIntForUser(getContext().getContentResolver(), "assist_gesture_silence_alerts_enabled", 1, -2) != 0) {
            z = true;
        }
        if (z) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(getAlertAction());
            intentFilter.addAction(getDoneAction());
            getContext().registerReceiverAsUser(this.mAlertReceiver, UserHandle.CURRENT, intentFilter, "com.android.systemui.permission.SEND_ALERT_BROADCASTS", null);
            this.mReceiverRegistered = true;
        }
        notifyListener();
    }

    public boolean isAvailable() {
        return this.mAlertFiring;
    }

    public void onTrigger(DetectionProperties detectionProperties) {
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
            Log.e("Elmyra/DeskClockAction", "Failed to dismiss alert", e);
        }
        this.mAlertFiring = false;
        notifyListener();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" [mReceiverRegistered -> ");
        sb.append(this.mReceiverRegistered);
        sb.append("]");
        return sb.toString();
    }
}
