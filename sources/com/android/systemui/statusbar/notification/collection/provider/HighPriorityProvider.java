package com.android.systemui.statusbar.notification.collection.provider;

import android.app.Notification.MessagingStyle;
import com.android.systemui.statusbar.notification.collection.GroupEntry;
import com.android.systemui.statusbar.notification.collection.ListEntry;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.people.PeopleNotificationIdentifier;

public class HighPriorityProvider {
    private final PeopleNotificationIdentifier mPeopleNotificationIdentifier;

    public HighPriorityProvider(PeopleNotificationIdentifier peopleNotificationIdentifier) {
        this.mPeopleNotificationIdentifier = peopleNotificationIdentifier;
    }

    public boolean isHighPriority(ListEntry listEntry) {
        boolean z = false;
        if (listEntry == null) {
            return false;
        }
        NotificationEntry representativeEntry = listEntry.getRepresentativeEntry();
        if (representativeEntry == null) {
            return false;
        }
        if (representativeEntry.getRanking().getImportance() >= 3 || hasHighPriorityCharacteristics(representativeEntry) || hasHighPriorityChild(listEntry)) {
            z = true;
        }
        return z;
    }

    private boolean hasHighPriorityChild(ListEntry listEntry) {
        if (listEntry instanceof GroupEntry) {
            for (NotificationEntry isHighPriority : ((GroupEntry) listEntry).getChildren()) {
                if (isHighPriority(isHighPriority)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasHighPriorityCharacteristics(NotificationEntry notificationEntry) {
        return !hasUserSetImportance(notificationEntry) && (isImportantOngoing(notificationEntry) || notificationEntry.getSbn().getNotification().hasMediaSession() || isPeopleNotification(notificationEntry) || isMessagingStyle(notificationEntry));
    }

    private boolean isImportantOngoing(NotificationEntry notificationEntry) {
        return notificationEntry.getSbn().getNotification().isForegroundService() && notificationEntry.getRanking().getImportance() >= 2;
    }

    private boolean isMessagingStyle(NotificationEntry notificationEntry) {
        return MessagingStyle.class.equals(notificationEntry.getSbn().getNotification().getNotificationStyle());
    }

    private boolean isPeopleNotification(NotificationEntry notificationEntry) {
        return this.mPeopleNotificationIdentifier.getPeopleNotificationType(notificationEntry.getSbn(), notificationEntry.getRanking()) != 0;
    }

    private boolean hasUserSetImportance(NotificationEntry notificationEntry) {
        return notificationEntry.getRanking().getChannel() != null && notificationEntry.getRanking().getChannel().hasUserSetImportance();
    }
}
