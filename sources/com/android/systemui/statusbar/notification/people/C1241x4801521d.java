package com.android.systemui.statusbar.notification.people;

import android.service.notification.NotificationListenerService.Ranking;
import android.service.notification.StatusBarNotification;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* renamed from: com.android.systemui.statusbar.notification.people.PeopleNotificationIdentifierImpl$getPeopleTypeOfSummary$childTypes$1 */
/* compiled from: PeopleNotificationIdentifier.kt */
final class C1241x4801521d extends Lambda implements Function1<NotificationEntry, Integer> {
    final /* synthetic */ PeopleNotificationIdentifierImpl this$0;

    C1241x4801521d(PeopleNotificationIdentifierImpl peopleNotificationIdentifierImpl) {
        this.this$0 = peopleNotificationIdentifierImpl;
        super(1);
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj) {
        return Integer.valueOf(invoke((NotificationEntry) obj));
    }

    public final int invoke(NotificationEntry notificationEntry) {
        PeopleNotificationIdentifierImpl peopleNotificationIdentifierImpl = this.this$0;
        Intrinsics.checkExpressionValueIsNotNull(notificationEntry, "it");
        StatusBarNotification sbn = notificationEntry.getSbn();
        Intrinsics.checkExpressionValueIsNotNull(sbn, "it.sbn");
        Ranking ranking = notificationEntry.getRanking();
        Intrinsics.checkExpressionValueIsNotNull(ranking, "it.ranking");
        return peopleNotificationIdentifierImpl.getPeopleNotificationType(sbn, ranking);
    }
}
