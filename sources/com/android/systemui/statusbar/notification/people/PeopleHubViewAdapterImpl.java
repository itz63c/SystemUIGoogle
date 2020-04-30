package com.android.systemui.statusbar.notification.people;

import kotlin.jvm.internal.Intrinsics;

/* compiled from: PeopleHubViewController.kt */
public final class PeopleHubViewAdapterImpl implements PeopleHubViewAdapter {
    private final DataSource<PeopleHubViewModelFactory> dataSource;

    public PeopleHubViewAdapterImpl(DataSource<PeopleHubViewModelFactory> dataSource2) {
        Intrinsics.checkParameterIsNotNull(dataSource2, "dataSource");
        this.dataSource = dataSource2;
    }

    public Subscription bindView(PeopleHubViewBoundary peopleHubViewBoundary) {
        Intrinsics.checkParameterIsNotNull(peopleHubViewBoundary, "viewBoundary");
        return this.dataSource.registerListener(new PeopleHubDataListenerImpl(peopleHubViewBoundary));
    }
}
