package com.android.systemui.statusbar.notification.people;

import com.android.systemui.plugins.ActivityStarter;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref$ObjectRef;

/* compiled from: PeopleHubViewController.kt */
public final class PeopleHubViewModelFactoryDataSourceImpl implements DataSource<PeopleHubViewModelFactory> {
    /* access modifiers changed from: private */
    public final ActivityStarter activityStarter;
    private final DataSource<PeopleHubModel> dataSource;

    public PeopleHubViewModelFactoryDataSourceImpl(ActivityStarter activityStarter2, DataSource<PeopleHubModel> dataSource2) {
        Intrinsics.checkParameterIsNotNull(activityStarter2, "activityStarter");
        Intrinsics.checkParameterIsNotNull(dataSource2, "dataSource");
        this.activityStarter = activityStarter2;
        this.dataSource = dataSource2;
    }

    public Subscription registerListener(DataListener<? super PeopleHubViewModelFactory> dataListener) {
        Intrinsics.checkParameterIsNotNull(dataListener, "listener");
        Ref$ObjectRef ref$ObjectRef = new Ref$ObjectRef();
        ref$ObjectRef.element = null;
        return new PeopleHubViewModelFactoryDataSourceImpl$registerListener$2(this.dataSource.registerListener(new C1238x8c4359c9(ref$ObjectRef, new PeopleHubViewModelFactoryDataSourceImpl$registerListener$1(this, ref$ObjectRef, dataListener))));
    }
}
