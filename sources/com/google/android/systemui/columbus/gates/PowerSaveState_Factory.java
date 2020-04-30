package com.google.android.systemui.columbus.gates;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class PowerSaveState_Factory implements Factory<PowerSaveState> {
    private final Provider<Context> contextProvider;

    public PowerSaveState_Factory(Provider<Context> provider) {
        this.contextProvider = provider;
    }

    public PowerSaveState get() {
        return provideInstance(this.contextProvider);
    }

    public static PowerSaveState provideInstance(Provider<Context> provider) {
        return new PowerSaveState((Context) provider.get());
    }

    public static PowerSaveState_Factory create(Provider<Context> provider) {
        return new PowerSaveState_Factory(provider);
    }
}
