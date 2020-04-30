package com.android.systemui.statusbar.notification.people;

import com.android.internal.statusbar.NotificationVisibility;
import com.android.systemui.statusbar.notification.NotificationEntryListener;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: PeopleHubNotificationListener.kt */
public final class PeopleHubDataSourceImpl$notificationEntryListener$1 implements NotificationEntryListener {
    final /* synthetic */ PeopleHubDataSourceImpl this$0;

    PeopleHubDataSourceImpl$notificationEntryListener$1(PeopleHubDataSourceImpl peopleHubDataSourceImpl) {
        this.this$0 = peopleHubDataSourceImpl;
    }

    public void onEntryInflated(NotificationEntry notificationEntry) {
        Intrinsics.checkParameterIsNotNull(notificationEntry, "entry");
        this.this$0.addVisibleEntry(notificationEntry);
    }

    public void onEntryReinflated(NotificationEntry notificationEntry) {
        Intrinsics.checkParameterIsNotNull(notificationEntry, "entry");
        this.this$0.addVisibleEntry(notificationEntry);
    }

    public void onPostEntryUpdated(NotificationEntry notificationEntry) {
        Intrinsics.checkParameterIsNotNull(notificationEntry, "entry");
        this.this$0.addVisibleEntry(notificationEntry);
    }

    public void onEntryRemoved(NotificationEntry notificationEntry, NotificationVisibility notificationVisibility, boolean z, int i) {
        Intrinsics.checkParameterIsNotNull(notificationEntry, "entry");
        this.this$0.removeVisibleEntry(notificationEntry, i);
    }
}
