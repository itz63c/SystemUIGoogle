package com.android.systemui.statusbar.phone;

import android.content.Context;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.statusbar.policy.BatteryController;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class LightBarController_Factory implements Factory<LightBarController> {
    private final Provider<BatteryController> batteryControllerProvider;
    private final Provider<Context> ctxProvider;
    private final Provider<DarkIconDispatcher> darkIconDispatcherProvider;

    public LightBarController_Factory(Provider<Context> provider, Provider<DarkIconDispatcher> provider2, Provider<BatteryController> provider3) {
        this.ctxProvider = provider;
        this.darkIconDispatcherProvider = provider2;
        this.batteryControllerProvider = provider3;
    }

    public LightBarController get() {
        return provideInstance(this.ctxProvider, this.darkIconDispatcherProvider, this.batteryControllerProvider);
    }

    public static LightBarController provideInstance(Provider<Context> provider, Provider<DarkIconDispatcher> provider2, Provider<BatteryController> provider3) {
        return new LightBarController((Context) provider.get(), (DarkIconDispatcher) provider2.get(), (BatteryController) provider3.get());
    }

    public static LightBarController_Factory create(Provider<Context> provider, Provider<DarkIconDispatcher> provider2, Provider<BatteryController> provider3) {
        return new LightBarController_Factory(provider, provider2, provider3);
    }
}
