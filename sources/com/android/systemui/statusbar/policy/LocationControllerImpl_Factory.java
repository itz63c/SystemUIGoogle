package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.os.Looper;
import com.android.systemui.BootCompleteCache;
import com.android.systemui.broadcast.BroadcastDispatcher;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class LocationControllerImpl_Factory implements Factory<LocationControllerImpl> {
    private final Provider<Looper> bgLooperProvider;
    private final Provider<BootCompleteCache> bootCompleteCacheProvider;
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<Context> contextProvider;

    public LocationControllerImpl_Factory(Provider<Context> provider, Provider<Looper> provider2, Provider<BroadcastDispatcher> provider3, Provider<BootCompleteCache> provider4) {
        this.contextProvider = provider;
        this.bgLooperProvider = provider2;
        this.broadcastDispatcherProvider = provider3;
        this.bootCompleteCacheProvider = provider4;
    }

    public LocationControllerImpl get() {
        return provideInstance(this.contextProvider, this.bgLooperProvider, this.broadcastDispatcherProvider, this.bootCompleteCacheProvider);
    }

    public static LocationControllerImpl provideInstance(Provider<Context> provider, Provider<Looper> provider2, Provider<BroadcastDispatcher> provider3, Provider<BootCompleteCache> provider4) {
        return new LocationControllerImpl((Context) provider.get(), (Looper) provider2.get(), (BroadcastDispatcher) provider3.get(), (BootCompleteCache) provider4.get());
    }

    public static LocationControllerImpl_Factory create(Provider<Context> provider, Provider<Looper> provider2, Provider<BroadcastDispatcher> provider3, Provider<BootCompleteCache> provider4) {
        return new LocationControllerImpl_Factory(provider, provider2, provider3, provider4);
    }
}
