package com.android.systemui.statusbar.notification.people;

import android.service.notification.NotificationListenerService.Ranking;
import android.service.notification.StatusBarNotification;

/* compiled from: PeopleNotificationIdentifier.kt */
public interface PeopleNotificationIdentifier {

    /* compiled from: PeopleNotificationIdentifier.kt */
    public static final class Companion {
        static final /* synthetic */ Companion $$INSTANCE = new Companion();

        private Companion() {
        }
    }

    static {
        Companion companion = Companion.$$INSTANCE;
    }

    int getPeopleNotificationType(StatusBarNotification statusBarNotification, Ranking ranking);
}
