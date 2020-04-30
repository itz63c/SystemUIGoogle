package com.google.android.systemui.assist.uihints;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class ConfigurationHandler_Factory implements Factory<ConfigurationHandler> {
    private final Provider<Context> contextProvider;

    public ConfigurationHandler_Factory(Provider<Context> provider) {
        this.contextProvider = provider;
    }

    public ConfigurationHandler get() {
        return provideInstance(this.contextProvider);
    }

    public static ConfigurationHandler provideInstance(Provider<Context> provider) {
        return new ConfigurationHandler((Context) provider.get());
    }

    public static ConfigurationHandler_Factory create(Provider<Context> provider) {
        return new ConfigurationHandler_Factory(provider);
    }
}
