package com.google.android.systemui.columbus.gates;

import android.content.Context;
import android.os.Handler;
import com.android.systemui.assist.DeviceConfigHelper;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class FlagEnabled_Factory implements Factory<FlagEnabled> {
    private final Provider<Context> contextProvider;
    private final Provider<DeviceConfigHelper> deviceConfigHelperProvider;
    private final Provider<Handler> handlerProvider;

    public FlagEnabled_Factory(Provider<Context> provider, Provider<Handler> provider2, Provider<DeviceConfigHelper> provider3) {
        this.contextProvider = provider;
        this.handlerProvider = provider2;
        this.deviceConfigHelperProvider = provider3;
    }

    public FlagEnabled get() {
        return provideInstance(this.contextProvider, this.handlerProvider, this.deviceConfigHelperProvider);
    }

    public static FlagEnabled provideInstance(Provider<Context> provider, Provider<Handler> provider2, Provider<DeviceConfigHelper> provider3) {
        return new FlagEnabled((Context) provider.get(), (Handler) provider2.get(), (DeviceConfigHelper) provider3.get());
    }

    public static FlagEnabled_Factory create(Provider<Context> provider, Provider<Handler> provider2, Provider<DeviceConfigHelper> provider3) {
        return new FlagEnabled_Factory(provider, provider2, provider3);
    }
}
