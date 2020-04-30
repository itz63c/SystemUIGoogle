package com.android.systemui.statusbar.notification.people;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;

/* compiled from: PeopleHubNotificationListener.kt */
final class PeopleHubDataSourceImpl$extractPerson$clickRunnable$1 implements Runnable {
    final /* synthetic */ NotificationEntry $this_extractPerson;
    final /* synthetic */ PeopleHubDataSourceImpl this$0;

    PeopleHubDataSourceImpl$extractPerson$clickRunnable$1(PeopleHubDataSourceImpl peopleHubDataSourceImpl, NotificationEntry notificationEntry) {
        this.this$0 = peopleHubDataSourceImpl;
        this.$this_extractPerson = notificationEntry;
    }

    public final void run() {
        this.this$0.notificationListener.unsnoozeNotification(this.$this_extractPerson.getKey());
    }
}
