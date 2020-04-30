package com.google.android.systemui.columbus.gates;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class VrMode_Factory implements Factory<VrMode> {
    private final Provider<Context> contextProvider;

    public VrMode_Factory(Provider<Context> provider) {
        this.contextProvider = provider;
    }

    public VrMode get() {
        return provideInstance(this.contextProvider);
    }

    public static VrMode provideInstance(Provider<Context> provider) {
        return new VrMode((Context) provider.get());
    }

    public static VrMode_Factory create(Provider<Context> provider) {
        return new VrMode_Factory(provider);
    }
}
