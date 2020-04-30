package com.android.systemui.statusbar.notification.collection;

import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* compiled from: NotificationRankingManager.kt */
final class NotificationRankingManager$filterAndSortLocked$1 extends Lambda implements Function1<NotificationEntry, Boolean> {
    final /* synthetic */ NotificationRankingManager this$0;

    NotificationRankingManager$filterAndSortLocked$1(NotificationRankingManager notificationRankingManager) {
        this.this$0 = notificationRankingManager;
        super(1);
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj) {
        return Boolean.valueOf(invoke((NotificationEntry) obj));
    }

    public final boolean invoke(NotificationEntry notificationEntry) {
        Intrinsics.checkParameterIsNotNull(notificationEntry, "it");
        return !this.this$0.notifFilter.shouldFilterOut(notificationEntry);
    }
}
