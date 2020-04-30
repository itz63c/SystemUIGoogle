package com.android.systemui.statusbar.notification.people;

import dagger.internal.Factory;
import javax.inject.Provider;

public final class PeopleHubViewAdapterImpl_Factory implements Factory<PeopleHubViewAdapterImpl> {
    private final Provider<DataSource<PeopleHubViewModelFactory>> dataSourceProvider;

    public PeopleHubViewAdapterImpl_Factory(Provider<DataSource<PeopleHubViewModelFactory>> provider) {
        this.dataSourceProvider = provider;
    }

    public PeopleHubViewAdapterImpl get() {
        return provideInstance(this.dataSourceProvider);
    }

    public static PeopleHubViewAdapterImpl provideInstance(Provider<DataSource<PeopleHubViewModelFactory>> provider) {
        return new PeopleHubViewAdapterImpl((DataSource) provider.get());
    }

    public static PeopleHubViewAdapterImpl_Factory create(Provider<DataSource<PeopleHubViewModelFactory>> provider) {
        return new PeopleHubViewAdapterImpl_Factory(provider);
    }
}
