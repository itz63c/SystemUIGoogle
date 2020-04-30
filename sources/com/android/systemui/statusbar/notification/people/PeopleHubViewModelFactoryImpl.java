package com.android.systemui.statusbar.notification.people;

import android.view.View;
import com.android.systemui.plugins.ActivityStarter;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: PeopleHubViewController.kt */
final class PeopleHubViewModelFactoryImpl implements PeopleHubViewModelFactory {
    private final PeopleHubModel model;

    public PeopleHubViewModelFactoryImpl(PeopleHubModel peopleHubModel, ActivityStarter activityStarter) {
        Intrinsics.checkParameterIsNotNull(peopleHubModel, "model");
        Intrinsics.checkParameterIsNotNull(activityStarter, "activityStarter");
        this.model = peopleHubModel;
    }

    public PeopleHubViewModel createWithAssociatedClickView(View view) {
        Intrinsics.checkParameterIsNotNull(view, "view");
        return new PeopleHubViewModel(SequencesKt___SequencesKt.map(CollectionsKt___CollectionsKt.asSequence(this.model.getPeople()), C1239xa9b90728.INSTANCE), !this.model.getPeople().isEmpty());
    }
}
