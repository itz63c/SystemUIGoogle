package com.android.systemui.statusbar.notification.people;

/* compiled from: PeopleHubViewController.kt */
public final class PeopleHubViewModelFactoryDataSourceImpl$registerListener$2 implements Subscription {
    final /* synthetic */ Subscription $dataSub;

    PeopleHubViewModelFactoryDataSourceImpl$registerListener$2(Subscription subscription) {
        this.$dataSub = subscription;
    }

    public void unsubscribe() {
        this.$dataSub.unsubscribe();
    }
}
