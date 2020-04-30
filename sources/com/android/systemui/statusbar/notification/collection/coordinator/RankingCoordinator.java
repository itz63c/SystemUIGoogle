package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener;
import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifFilter;

public class RankingCoordinator implements Coordinator {
    /* access modifiers changed from: private */
    public final NotifFilter mNotifFilter = new NotifFilter("RankingNotificationCoordinator") {
        public boolean shouldFilterOut(NotificationEntry notificationEntry, long j) {
            if (notificationEntry.getRanking().isSuspended()) {
                return true;
            }
            if (RankingCoordinator.this.mStatusBarStateController.isDozing() && notificationEntry.shouldSuppressAmbient()) {
                return true;
            }
            if (RankingCoordinator.this.mStatusBarStateController.isDozing() || !notificationEntry.shouldSuppressNotificationList()) {
                return false;
            }
            return true;
        }
    };
    private final StateListener mStatusBarStateCallback = new StateListener() {
        public void onDozingChanged(boolean z) {
            RankingCoordinator.this.mNotifFilter.invalidateList();
        }
    };
    /* access modifiers changed from: private */
    public final StatusBarStateController mStatusBarStateController;

    public RankingCoordinator(StatusBarStateController statusBarStateController) {
        this.mStatusBarStateController = statusBarStateController;
    }

    public void attach(NotifPipeline notifPipeline) {
        this.mStatusBarStateController.addCallback(this.mStatusBarStateCallback);
        notifPipeline.addPreGroupFilter(this.mNotifFilter);
    }
}
