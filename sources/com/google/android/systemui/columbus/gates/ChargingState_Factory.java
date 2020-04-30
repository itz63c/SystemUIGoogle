package com.google.android.systemui.columbus.gates;

import android.content.Context;
import android.os.Handler;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class ChargingState_Factory implements Factory<ChargingState> {
    private final Provider<Context> contextProvider;
    private final Provider<Long> gateDurationProvider;
    private final Provider<Handler> handlerProvider;

    public ChargingState_Factory(Provider<Context> provider, Provider<Handler> provider2, Provider<Long> provider3) {
        this.contextProvider = provider;
        this.handlerProvider = provider2;
        this.gateDurationProvider = provider3;
    }

    public ChargingState get() {
        return provideInstance(this.contextProvider, this.handlerProvider, this.gateDurationProvider);
    }

    public static ChargingState provideInstance(Provider<Context> provider, Provider<Handler> provider2, Provider<Long> provider3) {
        return new ChargingState((Context) provider.get(), (Handler) provider2.get(), ((Long) provider3.get()).longValue());
    }

    public static ChargingState_Factory create(Provider<Context> provider, Provider<Handler> provider2, Provider<Long> provider3) {
        return new ChargingState_Factory(provider, provider2, provider3);
    }
}
