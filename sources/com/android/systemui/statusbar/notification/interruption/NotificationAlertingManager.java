package com.android.systemui.statusbar.notification.interruption;

import android.app.Notification;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.NotificationListener;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.notification.NotificationEntryListener;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.policy.HeadsUpManager;

public class NotificationAlertingManager {
    private HeadsUpManager mHeadsUpManager;
    private final NotificationInterruptStateProvider mNotificationInterruptStateProvider;
    private final NotificationListener mNotificationListener;
    private final NotificationRemoteInputManager mRemoteInputManager;
    private final StatusBarStateController mStatusBarStateController;
    private final VisualStabilityManager mVisualStabilityManager;

    public NotificationAlertingManager(NotificationEntryManager notificationEntryManager, NotificationRemoteInputManager notificationRemoteInputManager, VisualStabilityManager visualStabilityManager, StatusBarStateController statusBarStateController, NotificationInterruptStateProvider notificationInterruptStateProvider, NotificationListener notificationListener, HeadsUpManager headsUpManager) {
        this.mRemoteInputManager = notificationRemoteInputManager;
        this.mVisualStabilityManager = visualStabilityManager;
        this.mStatusBarStateController = statusBarStateController;
        this.mNotificationInterruptStateProvider = notificationInterruptStateProvider;
        this.mNotificationListener = notificationListener;
        this.mHeadsUpManager = headsUpManager;
        notificationEntryManager.addNotificationEntryListener(new NotificationEntryListener() {
            public void onEntryInflated(NotificationEntry notificationEntry) {
                NotificationAlertingManager.this.showAlertingView(notificationEntry);
            }

            public void onPreEntryUpdated(NotificationEntry notificationEntry) {
                NotificationAlertingManager.this.updateAlertState(notificationEntry);
            }

            public void onEntryRemoved(NotificationEntry notificationEntry, NotificationVisibility notificationVisibility, boolean z, int i) {
                NotificationAlertingManager.this.stopAlerting(notificationEntry.getKey());
            }
        });
    }

    /* access modifiers changed from: private */
    public void showAlertingView(NotificationEntry notificationEntry) {
        if (notificationEntry.getRow().getPrivateLayout().getHeadsUpChild() == null) {
            return;
        }
        if (this.mNotificationInterruptStateProvider.shouldHeadsUp(notificationEntry)) {
            this.mHeadsUpManager.showNotification(notificationEntry);
            if (!this.mStatusBarStateController.isDozing()) {
                setNotificationShown(notificationEntry.getSbn());
                return;
            }
            return;
        }
        notificationEntry.freeContentViewWhenSafe(4);
    }

    /* access modifiers changed from: private */
    public void updateAlertState(NotificationEntry notificationEntry) {
        boolean alertAgain = alertAgain(notificationEntry, notificationEntry.getSbn().getNotification());
        boolean shouldHeadsUp = this.mNotificationInterruptStateProvider.shouldHeadsUp(notificationEntry);
        if (this.mHeadsUpManager.isAlerting(notificationEntry.getKey())) {
            if (shouldHeadsUp) {
                this.mHeadsUpManager.updateNotification(notificationEntry.getKey(), alertAgain);
            } else if (!this.mHeadsUpManager.isEntryAutoHeadsUpped(notificationEntry.getKey())) {
                this.mHeadsUpManager.removeNotification(notificationEntry.getKey(), false);
            }
        } else if (shouldHeadsUp && alertAgain) {
            this.mHeadsUpManager.showNotification(notificationEntry);
        }
    }

    public static boolean alertAgain(NotificationEntry notificationEntry, Notification notification) {
        return notificationEntry == null || !notificationEntry.hasInterrupted() || (notification.flags & 8) == 0;
    }

    private void setNotificationShown(StatusBarNotification statusBarNotification) {
        try {
            this.mNotificationListener.setNotificationsShown(new String[]{statusBarNotification.getKey()});
        } catch (RuntimeException e) {
            Log.d("NotifAlertManager", "failed setNotificationsShown: ", e);
        }
    }

    /* access modifiers changed from: private */
    public void stopAlerting(String str) {
        if (this.mHeadsUpManager.isAlerting(str)) {
            this.mHeadsUpManager.removeNotification(str, (this.mRemoteInputManager.getController().isSpinning(str) && !NotificationRemoteInputManager.FORCE_REMOTE_INPUT_HISTORY) || !this.mVisualStabilityManager.isReorderingAllowed());
        }
    }
}
