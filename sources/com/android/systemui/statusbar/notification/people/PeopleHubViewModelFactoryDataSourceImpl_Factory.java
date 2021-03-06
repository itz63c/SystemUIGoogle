package com.android.systemui.statusbar.notification.people;

import com.android.systemui.plugins.ActivityStarter;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class PeopleHubViewModelFactoryDataSourceImpl_Factory implements Factory<PeopleHubViewModelFactoryDataSourceImpl> {
    private final Provider<ActivityStarter> activityStarterProvider;
    private final Provider<DataSource<PeopleHubModel>> dataSourceProvider;

    public PeopleHubViewModelFactoryDataSourceImpl_Factory(Provider<ActivityStarter> provider, Provider<DataSource<PeopleHubModel>> provider2) {
        this.activityStarterProvider = provider;
        this.dataSourceProvider = provider2;
    }

    public PeopleHubViewModelFactoryDataSourceImpl get() {
        return provideInstance(this.activityStarterProvider, this.dataSourceProvider);
    }

    public static PeopleHubViewModelFactoryDataSourceImpl provideInstance(Provider<ActivityStarter> provider, Provider<DataSource<PeopleHubModel>> provider2) {
        return new PeopleHubViewModelFactoryDataSourceImpl((ActivityStarter) provider.get(), (DataSource) provider2.get());
    }

    public static PeopleHubViewModelFactoryDataSourceImpl_Factory create(Provider<ActivityStarter> provider, Provider<DataSource<PeopleHubModel>> provider2) {
        return new PeopleHubViewModelFactoryDataSourceImpl_Factory(provider, provider2);
    }
}
