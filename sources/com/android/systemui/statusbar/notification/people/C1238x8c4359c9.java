package com.android.systemui.statusbar.notification.people;

import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref$ObjectRef;

/* renamed from: com.android.systemui.statusbar.notification.people.PeopleHubViewModelFactoryDataSourceImpl$registerListener$dataSub$1 */
/* compiled from: PeopleHubViewController.kt */
public final class C1238x8c4359c9 implements DataListener<PeopleHubModel> {
    final /* synthetic */ Ref$ObjectRef $model;
    final /* synthetic */ PeopleHubViewModelFactoryDataSourceImpl$registerListener$1 $updateListener$1;

    C1238x8c4359c9(Ref$ObjectRef ref$ObjectRef, PeopleHubViewModelFactoryDataSourceImpl$registerListener$1 peopleHubViewModelFactoryDataSourceImpl$registerListener$1) {
        this.$model = ref$ObjectRef;
        this.$updateListener$1 = peopleHubViewModelFactoryDataSourceImpl$registerListener$1;
    }

    public void onDataChanged(PeopleHubModel peopleHubModel) {
        Intrinsics.checkParameterIsNotNull(peopleHubModel, "data");
        this.$model.element = peopleHubModel;
        this.$updateListener$1.invoke();
    }
}
