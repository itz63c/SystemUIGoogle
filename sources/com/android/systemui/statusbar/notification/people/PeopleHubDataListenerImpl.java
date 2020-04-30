package com.android.systemui.statusbar.notification.people;

import kotlin.Pair;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: PeopleHubViewController.kt */
final class PeopleHubDataListenerImpl implements DataListener<PeopleHubViewModelFactory> {
    private final PeopleHubViewBoundary viewBoundary;

    public PeopleHubDataListenerImpl(PeopleHubViewBoundary peopleHubViewBoundary) {
        Intrinsics.checkParameterIsNotNull(peopleHubViewBoundary, "viewBoundary");
        this.viewBoundary = peopleHubViewBoundary;
    }

    public void onDataChanged(PeopleHubViewModelFactory peopleHubViewModelFactory) {
        Intrinsics.checkParameterIsNotNull(peopleHubViewModelFactory, "data");
        PeopleHubViewModel createWithAssociatedClickView = peopleHubViewModelFactory.createWithAssociatedClickView(this.viewBoundary.getAssociatedViewForClickAnimation());
        this.viewBoundary.setVisible(createWithAssociatedClickView.isVisible());
        for (Pair pair : SequencesKt___SequencesKt.zip(this.viewBoundary.getPersonViewAdapters(), SequencesKt___SequencesKt.plus(createWithAssociatedClickView.getPeople(), PeopleHubViewControllerKt.repeated(null)))) {
            ((DataListener) pair.component1()).onDataChanged((PersonViewModel) pair.component2());
        }
    }
}
