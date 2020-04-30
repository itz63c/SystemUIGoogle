package com.google.android.systemui.columbus.gates;

import android.content.Context;
import android.os.Handler;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class UsbState_Factory implements Factory<UsbState> {
    private final Provider<Context> contextProvider;
    private final Provider<Long> gateDurationProvider;
    private final Provider<Handler> handlerProvider;

    public UsbState_Factory(Provider<Context> provider, Provider<Handler> provider2, Provider<Long> provider3) {
        this.contextProvider = provider;
        this.handlerProvider = provider2;
        this.gateDurationProvider = provider3;
    }

    public UsbState get() {
        return provideInstance(this.contextProvider, this.handlerProvider, this.gateDurationProvider);
    }

    public static UsbState provideInstance(Provider<Context> provider, Provider<Handler> provider2, Provider<Long> provider3) {
        return new UsbState((Context) provider.get(), (Handler) provider2.get(), ((Long) provider3.get()).longValue());
    }

    public static UsbState_Factory create(Provider<Context> provider, Provider<Handler> provider2, Provider<Long> provider3) {
        return new UsbState_Factory(provider, provider2, provider3);
    }
}
