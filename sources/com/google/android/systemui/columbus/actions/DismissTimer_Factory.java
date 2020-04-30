package com.google.android.systemui.columbus.actions;

import android.content.Context;
import com.google.android.systemui.columbus.ColumbusContentObserver;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class DismissTimer_Factory implements Factory<DismissTimer> {
    private final Provider<Context> contextProvider;
    private final Provider<ColumbusContentObserver.Factory> settingsObserverFactoryProvider;

    public DismissTimer_Factory(Provider<Context> provider, Provider<ColumbusContentObserver.Factory> provider2) {
        this.contextProvider = provider;
        this.settingsObserverFactoryProvider = provider2;
    }

    public DismissTimer get() {
        return provideInstance(this.contextProvider, this.settingsObserverFactoryProvider);
    }

    public static DismissTimer provideInstance(Provider<Context> provider, Provider<ColumbusContentObserver.Factory> provider2) {
        return new DismissTimer((Context) provider.get(), (ColumbusContentObserver.Factory) provider2.get());
    }

    public static DismissTimer_Factory create(Provider<Context> provider, Provider<ColumbusContentObserver.Factory> provider2) {
        return new DismissTimer_Factory(provider, provider2);
    }
}
