package com.android.systemui.statusbar.notification.interruption;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.hardware.display.AmbientDisplayConfiguration;
import android.os.Handler;
import android.os.PowerManager;
import android.os.RemoteException;
import android.provider.Settings.Global;
import android.service.dreams.IDreamManager;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.notification.NotificationFilter;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import java.util.ArrayList;
import java.util.List;

public class NotificationInterruptStateProviderImpl implements NotificationInterruptStateProvider {
    private final AmbientDisplayConfiguration mAmbientDisplayConfiguration;
    private final BatteryController mBatteryController;
    /* access modifiers changed from: private */
    public final ContentResolver mContentResolver;
    private final IDreamManager mDreamManager;
    /* access modifiers changed from: private */
    public HeadsUpManager mHeadsUpManager;
    private final ContentObserver mHeadsUpObserver;
    private final NotificationFilter mNotificationFilter;
    private final PowerManager mPowerManager;
    private final StatusBarStateController mStatusBarStateController;
    private final List<NotificationInterruptSuppressor> mSuppressors = new ArrayList();
    @VisibleForTesting
    protected boolean mUseHeadsUp = false;

    public NotificationInterruptStateProviderImpl(ContentResolver contentResolver, PowerManager powerManager, IDreamManager iDreamManager, AmbientDisplayConfiguration ambientDisplayConfiguration, NotificationFilter notificationFilter, BatteryController batteryController, StatusBarStateController statusBarStateController, HeadsUpManager headsUpManager, Handler handler) {
        this.mContentResolver = contentResolver;
        this.mPowerManager = powerManager;
        this.mDreamManager = iDreamManager;
        this.mBatteryController = batteryController;
        this.mAmbientDisplayConfiguration = ambientDisplayConfiguration;
        this.mNotificationFilter = notificationFilter;
        this.mStatusBarStateController = statusBarStateController;
        this.mHeadsUpManager = headsUpManager;
        this.mHeadsUpObserver = new ContentObserver(handler) {
            public void onChange(boolean z) {
                NotificationInterruptStateProviderImpl notificationInterruptStateProviderImpl = NotificationInterruptStateProviderImpl.this;
                boolean z2 = notificationInterruptStateProviderImpl.mUseHeadsUp;
                boolean z3 = false;
                if (Global.getInt(notificationInterruptStateProviderImpl.mContentResolver, "heads_up_notifications_enabled", 0) != 0) {
                    z3 = true;
                }
                notificationInterruptStateProviderImpl.mUseHeadsUp = z3;
                StringBuilder sb = new StringBuilder();
                sb.append("heads up is ");
                sb.append(NotificationInterruptStateProviderImpl.this.mUseHeadsUp ? "enabled" : "disabled");
                String str = "InterruptionStateProvider";
                Log.d(str, sb.toString());
                boolean z4 = NotificationInterruptStateProviderImpl.this.mUseHeadsUp;
                if (z2 != z4 && !z4) {
                    Log.d(str, "dismissing any existing heads up notification on disable event");
                    NotificationInterruptStateProviderImpl.this.mHeadsUpManager.releaseAllImmediately();
                }
            }
        };
        this.mContentResolver.registerContentObserver(Global.getUriFor("heads_up_notifications_enabled"), true, this.mHeadsUpObserver);
        this.mContentResolver.registerContentObserver(Global.getUriFor("ticker_gets_heads_up"), true, this.mHeadsUpObserver);
        this.mHeadsUpObserver.onChange(true);
    }

    public void addSuppressor(NotificationInterruptSuppressor notificationInterruptSuppressor) {
        this.mSuppressors.add(notificationInterruptSuppressor);
    }

    public boolean shouldBubbleUp(NotificationEntry notificationEntry) {
        StatusBarNotification sbn = notificationEntry.getSbn();
        if (!canAlertCommon(notificationEntry) || !canAlertAwakeCommon(notificationEntry)) {
            return false;
        }
        String str = "InterruptionStateProvider";
        if (!notificationEntry.canBubble()) {
            StringBuilder sb = new StringBuilder();
            sb.append("No bubble up: not allowed to bubble: ");
            sb.append(sbn.getKey());
            Log.d(str, sb.toString());
            return false;
        } else if (!notificationEntry.isBubble()) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("No bubble up: notification ");
            sb2.append(sbn.getKey());
            sb2.append(" is bubble? ");
            sb2.append(notificationEntry.isBubble());
            Log.d(str, sb2.toString());
            return false;
        } else if (notificationEntry.getBubbleMetadata() != null && (notificationEntry.getBubbleMetadata().getShortcutId() != null || notificationEntry.getBubbleMetadata().getBubbleIntent() != null)) {
            return true;
        } else {
            StringBuilder sb3 = new StringBuilder();
            sb3.append("No bubble up: notification: ");
            sb3.append(sbn.getKey());
            sb3.append(" doesn't have valid metadata");
            Log.d(str, sb3.toString());
            return false;
        }
    }

    public boolean shouldHeadsUp(NotificationEntry notificationEntry) {
        if (this.mStatusBarStateController.isDozing()) {
            return shouldHeadsUpWhenDozing(notificationEntry);
        }
        return shouldHeadsUpWhenAwake(notificationEntry);
    }

    public boolean shouldLaunchFullScreenIntentWhenAdded(NotificationEntry notificationEntry) {
        if (notificationEntry.getSbn().getNotification().fullScreenIntent == null || (shouldHeadsUp(notificationEntry) && this.mStatusBarStateController.getState() != 1)) {
            return false;
        }
        return true;
    }

    private boolean shouldHeadsUpWhenAwake(NotificationEntry notificationEntry) {
        boolean z;
        StatusBarNotification sbn = notificationEntry.getSbn();
        String str = "InterruptionStateProvider";
        if (!this.mUseHeadsUp) {
            Log.d(str, "No heads up: no huns");
            return false;
        } else if (!canAlertCommon(notificationEntry) || !canAlertAwakeCommon(notificationEntry)) {
            return false;
        } else {
            boolean z2 = this.mStatusBarStateController.getState() == 0;
            if (notificationEntry.isBubble() && z2) {
                StringBuilder sb = new StringBuilder();
                sb.append("No heads up: in unlocked shade where notification is shown as a bubble: ");
                sb.append(sbn.getKey());
                Log.d(str, sb.toString());
                return false;
            } else if (notificationEntry.shouldSuppressPeek()) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("No heads up: suppressed by DND: ");
                sb2.append(sbn.getKey());
                Log.d(str, sb2.toString());
                return false;
            } else if (notificationEntry.getImportance() < 4) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append("No heads up: unimportant notification: ");
                sb3.append(sbn.getKey());
                Log.d(str, sb3.toString());
                return false;
            } else {
                try {
                    z = this.mDreamManager.isDreaming();
                } catch (RemoteException e) {
                    Log.e(str, "Failed to query dream manager.", e);
                    z = false;
                }
                if (!(this.mPowerManager.isScreenOn() && !z)) {
                    StringBuilder sb4 = new StringBuilder();
                    sb4.append("No heads up: not in use: ");
                    sb4.append(sbn.getKey());
                    Log.d(str, sb4.toString());
                    return false;
                }
                for (int i = 0; i < this.mSuppressors.size(); i++) {
                    if (((NotificationInterruptSuppressor) this.mSuppressors.get(i)).suppressAwakeHeadsUp(notificationEntry)) {
                        StringBuilder sb5 = new StringBuilder();
                        sb5.append("No heads up: aborted by suppressor: ");
                        sb5.append(((NotificationInterruptSuppressor) this.mSuppressors.get(i)).getName());
                        sb5.append(" sbnKey=");
                        sb5.append(sbn.getKey());
                        Log.d(str, sb5.toString());
                        return false;
                    }
                }
                return true;
            }
        }
    }

    private boolean shouldHeadsUpWhenDozing(NotificationEntry notificationEntry) {
        StatusBarNotification sbn = notificationEntry.getSbn();
        String str = "InterruptionStateProvider";
        if (!this.mAmbientDisplayConfiguration.pulseOnNotificationEnabled(-2)) {
            StringBuilder sb = new StringBuilder();
            sb.append("No pulsing: disabled by setting: ");
            sb.append(sbn.getKey());
            Log.d(str, sb.toString());
            return false;
        } else if (this.mBatteryController.isAodPowerSave()) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("No pulsing: disabled by battery saver: ");
            sb2.append(sbn.getKey());
            Log.d(str, sb2.toString());
            return false;
        } else if (!canAlertCommon(notificationEntry)) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append("No pulsing: notification shouldn't alert: ");
            sb3.append(sbn.getKey());
            Log.d(str, sb3.toString());
            return false;
        } else if (notificationEntry.shouldSuppressAmbient()) {
            StringBuilder sb4 = new StringBuilder();
            sb4.append("No pulsing: ambient effect suppressed: ");
            sb4.append(sbn.getKey());
            Log.d(str, sb4.toString());
            return false;
        } else if (notificationEntry.getImportance() >= 3) {
            return true;
        } else {
            StringBuilder sb5 = new StringBuilder();
            sb5.append("No pulsing: not important enough: ");
            sb5.append(sbn.getKey());
            Log.d(str, sb5.toString());
            return false;
        }
    }

    private boolean canAlertCommon(NotificationEntry notificationEntry) {
        StatusBarNotification sbn = notificationEntry.getSbn();
        String str = "InterruptionStateProvider";
        if (this.mNotificationFilter.shouldFilterOut(notificationEntry)) {
            StringBuilder sb = new StringBuilder();
            sb.append("No alerting: filtered notification: ");
            sb.append(sbn.getKey());
            Log.d(str, sb.toString());
            return false;
        } else if (!sbn.isGroup() || !sbn.getNotification().suppressAlertingDueToGrouping()) {
            for (int i = 0; i < this.mSuppressors.size(); i++) {
                if (((NotificationInterruptSuppressor) this.mSuppressors.get(i)).suppressInterruptions(notificationEntry)) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("No alerting: aborted by suppressor: ");
                    sb2.append(((NotificationInterruptSuppressor) this.mSuppressors.get(i)).getName());
                    sb2.append(" sbnKey=");
                    sb2.append(sbn.getKey());
                    Log.d(str, sb2.toString());
                    return false;
                }
            }
            return true;
        } else {
            Log.d(str, "No alerting: suppressed due to group alert behavior");
            return false;
        }
    }

    private boolean canAlertAwakeCommon(NotificationEntry notificationEntry) {
        StatusBarNotification sbn = notificationEntry.getSbn();
        int i = 0;
        while (true) {
            String str = "InterruptionStateProvider";
            if (i < this.mSuppressors.size()) {
                if (((NotificationInterruptSuppressor) this.mSuppressors.get(i)).suppressAwakeInterruptions(notificationEntry)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("No alerting: aborted by suppressor: ");
                    sb.append(((NotificationInterruptSuppressor) this.mSuppressors.get(i)).getName());
                    sb.append(" sbnKey=");
                    sb.append(sbn.getKey());
                    Log.d(str, sb.toString());
                    return false;
                }
                i++;
            } else if (isSnoozedPackage(sbn)) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("No alerting: snoozed package: ");
                sb2.append(sbn.getKey());
                Log.d(str, sb2.toString());
                return false;
            } else if (!notificationEntry.hasJustLaunchedFullScreenIntent()) {
                return true;
            } else {
                StringBuilder sb3 = new StringBuilder();
                sb3.append("No alerting: recent fullscreen: ");
                sb3.append(sbn.getKey());
                Log.d(str, sb3.toString());
                return false;
            }
        }
    }

    private boolean isSnoozedPackage(StatusBarNotification statusBarNotification) {
        return this.mHeadsUpManager.isSnoozed(statusBarNotification.getPackageName());
    }
}
