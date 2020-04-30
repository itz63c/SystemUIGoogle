package com.google.android.systemui.ambientmusic;

import android.app.AlarmManager;
import android.app.AlarmManager.OnAlarmListener;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.os.UserHandle;
import android.util.Log;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.Dependency;
import java.util.Objects;

public class AmbientIndicationService extends BroadcastReceiver {
    private final AlarmManager mAlarmManager;
    private final AmbientIndicationContainer mAmbientIndicationContainer;
    private final KeyguardUpdateMonitorCallback mCallback = new KeyguardUpdateMonitorCallback() {
        public void onUserSwitchComplete(int i) {
            AmbientIndicationService.this.onUserSwitched();
        }
    };
    private final Context mContext;
    private final OnAlarmListener mHideIndicationListener;

    public AmbientIndicationService(Context context, AmbientIndicationContainer ambientIndicationContainer) {
        this.mContext = context;
        this.mAmbientIndicationContainer = ambientIndicationContainer;
        this.mAlarmManager = (AlarmManager) context.getSystemService(AlarmManager.class);
        AmbientIndicationContainer ambientIndicationContainer2 = this.mAmbientIndicationContainer;
        Objects.requireNonNull(ambientIndicationContainer2);
        this.mHideIndicationListener = new OnAlarmListener() {
            public final void onAlarm() {
                AmbientIndicationContainer.this.hideAmbientMusic();
            }
        };
        start();
    }

    /* access modifiers changed from: 0000 */
    public void start() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.google.android.ambientindication.action.AMBIENT_INDICATION_SHOW");
        intentFilter.addAction("com.google.android.ambientindication.action.AMBIENT_INDICATION_HIDE");
        this.mContext.registerReceiverAsUser(this, UserHandle.ALL, intentFilter, "com.google.android.ambientindication.permission.AMBIENT_INDICATION", null);
        ((KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class)).registerCallback(this.mCallback);
    }

    public void onReceive(Context context, Intent intent) {
        String str = "AmbientIndication";
        if (!isForCurrentUser()) {
            Log.i(str, "Suppressing ambient, not for this user.");
        } else if (verifyAmbientApiVersion(intent)) {
            if (this.mAmbientIndicationContainer.isMediaPlaying()) {
                Log.i(str, "Suppressing ambient intent due to media playback.");
                return;
            }
            String action = intent.getAction();
            char c = 65535;
            int hashCode = action.hashCode();
            if (hashCode != -1032272569) {
                if (hashCode == -1031945470 && action.equals("com.google.android.ambientindication.action.AMBIENT_INDICATION_SHOW")) {
                    c = 0;
                }
            } else if (action.equals("com.google.android.ambientindication.action.AMBIENT_INDICATION_HIDE")) {
                c = 1;
            }
            if (c == 0) {
                CharSequence charSequenceExtra = intent.getCharSequenceExtra("com.google.android.ambientindication.extra.TEXT");
                PendingIntent pendingIntent = (PendingIntent) intent.getParcelableExtra("com.google.android.ambientindication.extra.OPEN_INTENT");
                long min = Math.min(Math.max(intent.getLongExtra("com.google.android.ambientindication.extra.TTL_MILLIS", 180000), 0), 180000);
                this.mAmbientIndicationContainer.setAmbientMusic(charSequenceExtra, pendingIntent, intent.getBooleanExtra("com.google.android.ambientindication.extra.SKIP_UNLOCK", false));
                this.mAlarmManager.setExact(2, SystemClock.elapsedRealtime() + min, "AmbientIndication", this.mHideIndicationListener, null);
                Log.i(str, "Showing ambient indication.");
            } else if (c == 1) {
                this.mAlarmManager.cancel(this.mHideIndicationListener);
                this.mAmbientIndicationContainer.hideAmbientMusic();
                Log.i(str, "Hiding ambient indication.");
            }
        }
    }

    private boolean verifyAmbientApiVersion(Intent intent) {
        int intExtra = intent.getIntExtra("com.google.android.ambientindication.extra.VERSION", 0);
        if (intExtra == 1) {
            return true;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("AmbientIndicationApi.EXTRA_VERSION is ");
        sb.append(1);
        sb.append(", but received an intent with version ");
        sb.append(intExtra);
        sb.append(", dropping intent.");
        Log.e("AmbientIndication", sb.toString());
        return false;
    }

    /* access modifiers changed from: 0000 */
    public boolean isForCurrentUser() {
        return getSendingUserId() == getCurrentUser() || getSendingUserId() == -1;
    }

    /* access modifiers changed from: 0000 */
    public int getCurrentUser() {
        return KeyguardUpdateMonitor.getCurrentUser();
    }

    /* access modifiers changed from: 0000 */
    public void onUserSwitched() {
        this.mAmbientIndicationContainer.hideAmbientMusic();
    }
}
