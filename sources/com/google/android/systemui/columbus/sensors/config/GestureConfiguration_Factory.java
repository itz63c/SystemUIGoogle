package com.google.android.systemui.columbus.sensors.config;

import android.content.Context;
import com.google.android.systemui.columbus.ColumbusContentObserver;
import dagger.internal.Factory;
import java.util.Set;
import javax.inject.Provider;

public final class GestureConfiguration_Factory implements Factory<GestureConfiguration> {
    private final Provider<Set<Adjustment>> adjustmentsProvider;
    private final Provider<Context> contextProvider;
    private final Provider<ColumbusContentObserver.Factory> settingsObserverFactoryProvider;

    public GestureConfiguration_Factory(Provider<Context> provider, Provider<Set<Adjustment>> provider2, Provider<ColumbusContentObserver.Factory> provider3) {
        this.contextProvider = provider;
        this.adjustmentsProvider = provider2;
        this.settingsObserverFactoryProvider = provider3;
    }

    public GestureConfiguration get() {
        return provideInstance(this.contextProvider, this.adjustmentsProvider, this.settingsObserverFactoryProvider);
    }

    public static GestureConfiguration provideInstance(Provider<Context> provider, Provider<Set<Adjustment>> provider2, Provider<ColumbusContentObserver.Factory> provider3) {
        return new GestureConfiguration((Context) provider.get(), (Set) provider2.get(), (ColumbusContentObserver.Factory) provider3.get());
    }

    public static GestureConfiguration_Factory create(Provider<Context> provider, Provider<Set<Adjustment>> provider2, Provider<ColumbusContentObserver.Factory> provider3) {
        return new GestureConfiguration_Factory(provider, provider2, provider3);
    }
}
