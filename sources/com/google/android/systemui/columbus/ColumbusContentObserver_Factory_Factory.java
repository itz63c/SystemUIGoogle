package com.google.android.systemui.columbus;

import android.app.IActivityManager;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class ColumbusContentObserver_Factory_Factory implements Factory<ColumbusContentObserver.Factory> {
    private final Provider<IActivityManager> activityManagerServiceProvider;
    private final Provider<ContentResolverWrapper> contentResolverProvider;

    public ColumbusContentObserver_Factory_Factory(Provider<ContentResolverWrapper> provider, Provider<IActivityManager> provider2) {
        this.contentResolverProvider = provider;
        this.activityManagerServiceProvider = provider2;
    }

    public ColumbusContentObserver.Factory get() {
        return provideInstance(this.contentResolverProvider, this.activityManagerServiceProvider);
    }

    public static ColumbusContentObserver.Factory provideInstance(Provider<ContentResolverWrapper> provider, Provider<IActivityManager> provider2) {
        return new ColumbusContentObserver.Factory((ContentResolverWrapper) provider.get(), (IActivityManager) provider2.get());
    }

    public static ColumbusContentObserver_Factory_Factory create(Provider<ContentResolverWrapper> provider, Provider<IActivityManager> provider2) {
        return new ColumbusContentObserver_Factory_Factory(provider, provider2);
    }
}
