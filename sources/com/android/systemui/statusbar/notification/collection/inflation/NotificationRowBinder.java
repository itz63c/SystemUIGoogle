package com.android.systemui.statusbar.notification.collection.inflation;

import com.android.systemui.statusbar.NotificationUiAdjustment;
import com.android.systemui.statusbar.notification.InflationException;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;

public interface NotificationRowBinder {
    void inflateViews(NotificationEntry notificationEntry, Runnable runnable) throws InflationException;

    void onNotificationRankingUpdated(NotificationEntry notificationEntry, Integer num, NotificationUiAdjustment notificationUiAdjustment, NotificationUiAdjustment notificationUiAdjustment2);
}
