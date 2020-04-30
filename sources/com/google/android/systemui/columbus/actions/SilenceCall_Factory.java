package com.google.android.systemui.columbus.actions;

import android.content.Context;
import com.google.android.systemui.columbus.ColumbusContentObserver;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class SilenceCall_Factory implements Factory<SilenceCall> {
    private final Provider<Context> contextProvider;
    private final Provider<ColumbusContentObserver.Factory> settingsObserverFactoryProvider;

    public SilenceCall_Factory(Provider<Context> provider, Provider<ColumbusContentObserver.Factory> provider2) {
        this.contextProvider = provider;
        this.settingsObserverFactoryProvider = provider2;
    }

    public SilenceCall get() {
        return provideInstance(this.contextProvider, this.settingsObserverFactoryProvider);
    }

    public static SilenceCall provideInstance(Provider<Context> provider, Provider<ColumbusContentObserver.Factory> provider2) {
        return new SilenceCall((Context) provider.get(), (ColumbusContentObserver.Factory) provider2.get());
    }

    public static SilenceCall_Factory create(Provider<Context> provider, Provider<ColumbusContentObserver.Factory> provider2) {
        return new SilenceCall_Factory(provider, provider2);
    }
}
