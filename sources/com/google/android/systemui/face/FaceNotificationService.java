package com.google.android.systemui.face;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.biometrics.BiometricSourceType;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.util.Log;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.C2017R$string;
import com.android.systemui.Dependency;

public class FaceNotificationService {
    private FaceNotificationBroadcastReceiver mBroadcastReceiver;
    /* access modifiers changed from: private */
    public Context mContext;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final KeyguardUpdateMonitorCallback mKeyguardUpdateMonitorCallback = new KeyguardUpdateMonitorCallback() {
        public void onBiometricError(int i, String str, BiometricSourceType biometricSourceType) {
            if (i == 1004) {
                FaceNotificationSettings.updateReenrollSetting(FaceNotificationService.this.mContext, 3);
            }
        }

        public void onBiometricHelp(int i, String str, BiometricSourceType biometricSourceType) {
            if (i == 13) {
                FaceNotificationSettings.updateReenrollSetting(FaceNotificationService.this.mContext, 1);
            }
        }

        public void onUserUnlocked() {
            if (FaceNotificationService.this.mNotificationQueued) {
                Log.d("FaceNotificationService", "Not showing notification; already queued.");
                return;
            }
            if (FaceNotificationSettings.isReenrollRequired(FaceNotificationService.this.mContext)) {
                FaceNotificationService.this.queueReenrollNotification();
            }
        }
    };
    /* access modifiers changed from: private */
    public boolean mNotificationQueued;

    public FaceNotificationService(Context context) {
        this.mContext = context;
        start();
    }

    private void start() {
        ((KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class)).registerCallback(this.mKeyguardUpdateMonitorCallback);
        this.mBroadcastReceiver = new FaceNotificationBroadcastReceiver(this.mContext);
    }

    /* access modifiers changed from: private */
    public void queueReenrollNotification() {
        this.mNotificationQueued = true;
        this.mHandler.postDelayed(new Runnable(this.mContext.getString(C2017R$string.face_reenroll_notification_title), this.mContext.getString(C2017R$string.face_reenroll_notification_content)) {
            public final /* synthetic */ String f$1;
            public final /* synthetic */ String f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                FaceNotificationService.this.lambda$queueReenrollNotification$0$FaceNotificationService(this.f$1, this.f$2);
            }
        }, 10000);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$queueReenrollNotification$0 */
    public /* synthetic */ void lambda$queueReenrollNotification$0$FaceNotificationService(String str, String str2) {
        showNotification("face_action_show_reenroll_dialog", str, str2);
    }

    private void showNotification(String str, CharSequence charSequence, CharSequence charSequence2) {
        this.mNotificationQueued = false;
        NotificationManager notificationManager = (NotificationManager) this.mContext.getSystemService(NotificationManager.class);
        String str2 = "FaceNotificationService";
        if (notificationManager == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Failed to show notification ");
            sb.append(str);
            sb.append(". Notification manager is null!");
            Log.e(str2, sb.toString());
            return;
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(str);
        String str3 = "face_action_notification_dismissed";
        intentFilter.addAction(str3);
        this.mContext.registerReceiver(this.mBroadcastReceiver, intentFilter);
        PendingIntent broadcastAsUser = PendingIntent.getBroadcastAsUser(this.mContext, 0, new Intent(str), 0, UserHandle.CURRENT);
        PendingIntent broadcastAsUser2 = PendingIntent.getBroadcastAsUser(this.mContext, 0, new Intent(str3), 0, UserHandle.CURRENT);
        String string = this.mContext.getString(C2017R$string.face_notification_name);
        String str4 = "FaceHiPriNotificationChannel";
        NotificationChannel notificationChannel = new NotificationChannel(str4, string, 4);
        Notification build = new Builder(this.mContext, str4).setCategory("sys").setSmallIcon(17302458).setContentTitle(charSequence).setContentText(charSequence2).setSubText(string).setContentIntent(broadcastAsUser).setDeleteIntent(broadcastAsUser2).setAutoCancel(true).setLocalOnly(true).setOnlyAlertOnce(true).setVisibility(-1).build();
        notificationManager.createNotificationChannel(notificationChannel);
        notificationManager.notifyAsUser(str2, 1, build, UserHandle.CURRENT);
    }
}
