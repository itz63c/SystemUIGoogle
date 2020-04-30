package com.android.systemui.statusbar.notification.collection;

import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* compiled from: NotificationRankingManager.kt */
final class NotificationRankingManager$filterAndSortLocked$2 extends Lambda implements Function1<NotificationEntry, NotificationEntry> {
    final /* synthetic */ NotificationRankingManager this$0;

    NotificationRankingManager$filterAndSortLocked$2(NotificationRankingManager notificationRankingManager) {
        this.this$0 = notificationRankingManager;
        super(1);
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj) {
        NotificationEntry notificationEntry = (NotificationEntry) obj;
        invoke(notificationEntry);
        return notificationEntry;
    }

    public final NotificationEntry invoke(NotificationEntry notificationEntry) {
        Intrinsics.checkParameterIsNotNull(notificationEntry, "it");
        this.this$0.assignBucketForEntry(notificationEntry);
        return notificationEntry;
    }
}
