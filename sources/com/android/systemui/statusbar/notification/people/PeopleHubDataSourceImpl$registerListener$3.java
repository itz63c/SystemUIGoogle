package com.android.systemui.statusbar.notification.people;

/* compiled from: PeopleHubNotificationListener.kt */
public final class PeopleHubDataSourceImpl$registerListener$3 implements Subscription {
    final /* synthetic */ DataListener $listener;
    final /* synthetic */ PeopleHubDataSourceImpl this$0;

    PeopleHubDataSourceImpl$registerListener$3(PeopleHubDataSourceImpl peopleHubDataSourceImpl, DataListener dataListener) {
        this.this$0 = peopleHubDataSourceImpl;
        this.$listener = dataListener;
    }

    public void unsubscribe() {
        this.this$0.dataListeners.remove(this.$listener);
        if (this.this$0.dataListeners.isEmpty()) {
            Subscription access$getUserChangeSubscription$p = this.this$0.userChangeSubscription;
            if (access$getUserChangeSubscription$p != null) {
                access$getUserChangeSubscription$p.unsubscribe();
            }
            this.this$0.userChangeSubscription = null;
            this.this$0.notificationEntryManager.removeNotificationEntryListener(this.this$0.notificationEntryListener);
        }
    }
}
