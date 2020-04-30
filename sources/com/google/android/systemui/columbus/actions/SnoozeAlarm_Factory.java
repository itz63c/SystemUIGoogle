package com.google.android.systemui.columbus.actions;

import android.content.Context;
import com.google.android.systemui.columbus.ColumbusContentObserver;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class SnoozeAlarm_Factory implements Factory<SnoozeAlarm> {
    private final Provider<Context> contextProvider;
    private final Provider<ColumbusContentObserver.Factory> settingsObserverFactoryProvider;

    public SnoozeAlarm_Factory(Provider<Context> provider, Provider<ColumbusContentObserver.Factory> provider2) {
        this.contextProvider = provider;
        this.settingsObserverFactoryProvider = provider2;
    }

    public SnoozeAlarm get() {
        return provideInstance(this.contextProvider, this.settingsObserverFactoryProvider);
    }

    public static SnoozeAlarm provideInstance(Provider<Context> provider, Provider<ColumbusContentObserver.Factory> provider2) {
        return new SnoozeAlarm((Context) provider.get(), (ColumbusContentObserver.Factory) provider2.get());
    }

    public static SnoozeAlarm_Factory create(Provider<Context> provider, Provider<ColumbusContentObserver.Factory> provider2) {
        return new SnoozeAlarm_Factory(provider, provider2);
    }
}
