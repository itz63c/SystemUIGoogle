package com.android.systemui.statusbar.notification.people;

import android.app.NotificationChannel;
import android.service.notification.NotificationListenerService.Ranking;
import android.service.notification.StatusBarNotification;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import java.util.ArrayList;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.Sequence;

/* compiled from: PeopleNotificationIdentifier.kt */
public final class PeopleNotificationIdentifierImpl implements PeopleNotificationIdentifier {
    private final NotificationGroupManager groupManager;
    private final NotificationPersonExtractor personExtractor;

    public PeopleNotificationIdentifierImpl(NotificationPersonExtractor notificationPersonExtractor, NotificationGroupManager notificationGroupManager) {
        Intrinsics.checkParameterIsNotNull(notificationPersonExtractor, "personExtractor");
        Intrinsics.checkParameterIsNotNull(notificationGroupManager, "groupManager");
        this.personExtractor = notificationPersonExtractor;
        this.groupManager = notificationGroupManager;
    }

    public int getPeopleNotificationType(StatusBarNotification statusBarNotification, Ranking ranking) {
        Intrinsics.checkParameterIsNotNull(statusBarNotification, "sbn");
        Intrinsics.checkParameterIsNotNull(ranking, "ranking");
        int personTypeInfo = getPersonTypeInfo(ranking);
        if (personTypeInfo == 2) {
            return 2;
        }
        int upperBound = upperBound(personTypeInfo, extractPersonTypeInfo(statusBarNotification));
        if (upperBound == 2) {
            return 2;
        }
        return upperBound(upperBound, getPeopleTypeOfSummary(statusBarNotification));
    }

    private final int upperBound(int i, int i2) {
        return Math.max(i, i2);
    }

    private final int getPersonTypeInfo(Ranking ranking) {
        NotificationChannel channel = ranking.getChannel();
        if (channel != null && channel.isImportantConversation()) {
            return 2;
        }
        if (ranking.isConversation()) {
            return 1;
        }
        return 0;
    }

    private final int extractPersonTypeInfo(StatusBarNotification statusBarNotification) {
        return this.personExtractor.isPersonNotification(statusBarNotification) ? 1 : 0;
    }

    private final int getPeopleTypeOfSummary(StatusBarNotification statusBarNotification) {
        int i = 0;
        if (!this.groupManager.isSummaryOfGroup(statusBarNotification)) {
            return 0;
        }
        ArrayList logicalChildren = this.groupManager.getLogicalChildren(statusBarNotification);
        if (logicalChildren != null) {
            Sequence asSequence = CollectionsKt___CollectionsKt.asSequence(logicalChildren);
            if (asSequence != null) {
                Sequence<Number> map = SequencesKt___SequencesKt.map(asSequence, new C1241x4801521d(this));
                if (map != null) {
                    for (Number intValue : map) {
                        i = upperBound(i, intValue.intValue());
                        if (i == 2) {
                            break;
                        }
                    }
                }
            }
        }
        return i;
    }
}
