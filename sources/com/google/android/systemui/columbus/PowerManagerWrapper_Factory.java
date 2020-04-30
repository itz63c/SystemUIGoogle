package com.google.android.systemui.columbus;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class PowerManagerWrapper_Factory implements Factory<PowerManagerWrapper> {
    private final Provider<Context> contextProvider;

    public PowerManagerWrapper_Factory(Provider<Context> provider) {
        this.contextProvider = provider;
    }

    public PowerManagerWrapper get() {
        return provideInstance(this.contextProvider);
    }

    public static PowerManagerWrapper provideInstance(Provider<Context> provider) {
        return new PowerManagerWrapper((Context) provider.get());
    }

    public static PowerManagerWrapper_Factory create(Provider<Context> provider) {
        return new PowerManagerWrapper_Factory(provider);
    }
}
