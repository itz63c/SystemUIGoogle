package com.android.systemui.statusbar.notification.people;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;
import kotlin.jvm.internal.Ref$ObjectRef;

/* compiled from: PeopleHubViewController.kt */
final class PeopleHubViewModelFactoryDataSourceImpl$registerListener$1 extends Lambda implements Function0<Unit> {
    final /* synthetic */ DataListener $listener;
    final /* synthetic */ Ref$ObjectRef $model;
    final /* synthetic */ PeopleHubViewModelFactoryDataSourceImpl this$0;

    PeopleHubViewModelFactoryDataSourceImpl$registerListener$1(PeopleHubViewModelFactoryDataSourceImpl peopleHubViewModelFactoryDataSourceImpl, Ref$ObjectRef ref$ObjectRef, DataListener dataListener) {
        this.this$0 = peopleHubViewModelFactoryDataSourceImpl;
        this.$model = ref$ObjectRef;
        this.$listener = dataListener;
        super(0);
    }

    public final void invoke() {
        PeopleHubModel peopleHubModel = (PeopleHubModel) this.$model.element;
        if (peopleHubModel != null) {
            this.$listener.onDataChanged(new PeopleHubViewModelFactoryImpl(peopleHubModel, this.this$0.activityStarter));
        }
    }
}
