package com.android.systemui.statusbar.notification.people;

import android.service.notification.StatusBarNotification;

/* compiled from: PeopleHubNotificationListener.kt */
public interface NotificationPersonExtractor {
    String extractPersonKey(StatusBarNotification statusBarNotification);

    boolean isPersonNotification(StatusBarNotification statusBarNotification);
}
