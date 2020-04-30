package com.google.android.systemui.columbus;

import android.os.Handler;
import com.android.systemui.assist.DeviceConfigHelper;
import dagger.internal.DoubleCheck;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class ColumbusServiceWrapper_Factory implements Factory<ColumbusServiceWrapper> {
    private final Provider<ColumbusService> columbusServiceProvider;
    private final Provider<DeviceConfigHelper> deviceConfigHelperProvider;
    private final Provider<Handler> handlerProvider;

    public ColumbusServiceWrapper_Factory(Provider<ColumbusService> provider, Provider<DeviceConfigHelper> provider2, Provider<Handler> provider3) {
        this.columbusServiceProvider = provider;
        this.deviceConfigHelperProvider = provider2;
        this.handlerProvider = provider3;
    }

    public ColumbusServiceWrapper get() {
        return provideInstance(this.columbusServiceProvider, this.deviceConfigHelperProvider, this.handlerProvider);
    }

    public static ColumbusServiceWrapper provideInstance(Provider<ColumbusService> provider, Provider<DeviceConfigHelper> provider2, Provider<Handler> provider3) {
        return new ColumbusServiceWrapper(DoubleCheck.lazy(provider), (DeviceConfigHelper) provider2.get(), (Handler) provider3.get());
    }

    public static ColumbusServiceWrapper_Factory create(Provider<ColumbusService> provider, Provider<DeviceConfigHelper> provider2, Provider<Handler> provider3) {
        return new ColumbusServiceWrapper_Factory(provider, provider2, provider3);
    }
}
