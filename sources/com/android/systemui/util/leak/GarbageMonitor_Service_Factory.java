package com.android.systemui.util.leak;

import android.content.Context;
import com.android.systemui.util.leak.GarbageMonitor.Service;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class GarbageMonitor_Service_Factory implements Factory<Service> {
    private final Provider<Context> contextProvider;
    private final Provider<GarbageMonitor> garbageMonitorProvider;

    public GarbageMonitor_Service_Factory(Provider<Context> provider, Provider<GarbageMonitor> provider2) {
        this.contextProvider = provider;
        this.garbageMonitorProvider = provider2;
    }

    public Service get() {
        return provideInstance(this.contextProvider, this.garbageMonitorProvider);
    }

    public static Service provideInstance(Provider<Context> provider, Provider<GarbageMonitor> provider2) {
        return new Service((Context) provider.get(), (GarbageMonitor) provider2.get());
    }

    public static GarbageMonitor_Service_Factory create(Provider<Context> provider, Provider<GarbageMonitor> provider2) {
        return new GarbageMonitor_Service_Factory(provider, provider2);
    }
}
