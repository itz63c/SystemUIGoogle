package com.google.android.systemui.columbus.actions;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class LaunchCamera_Factory implements Factory<LaunchCamera> {
    private final Provider<Context> contextProvider;

    public LaunchCamera_Factory(Provider<Context> provider) {
        this.contextProvider = provider;
    }

    public LaunchCamera get() {
        return provideInstance(this.contextProvider);
    }

    public static LaunchCamera provideInstance(Provider<Context> provider) {
        return new LaunchCamera((Context) provider.get());
    }

    public static LaunchCamera_Factory create(Provider<Context> provider) {
        return new LaunchCamera_Factory(provider);
    }
}
