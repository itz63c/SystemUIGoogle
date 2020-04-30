package com.android.systemui.statusbar.phone;

import android.content.Context;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import dagger.internal.Factory;
import java.util.concurrent.Executor;
import javax.inject.Provider;

public final class NavigationModeController_Factory implements Factory<NavigationModeController> {
    private final Provider<Context> contextProvider;
    private final Provider<DeviceProvisionedController> deviceProvisionedControllerProvider;
    private final Provider<Executor> uiBgExecutorProvider;

    public NavigationModeController_Factory(Provider<Context> provider, Provider<DeviceProvisionedController> provider2, Provider<Executor> provider3) {
        this.contextProvider = provider;
        this.deviceProvisionedControllerProvider = provider2;
        this.uiBgExecutorProvider = provider3;
    }

    public NavigationModeController get() {
        return provideInstance(this.contextProvider, this.deviceProvisionedControllerProvider, this.uiBgExecutorProvider);
    }

    public static NavigationModeController provideInstance(Provider<Context> provider, Provider<DeviceProvisionedController> provider2, Provider<Executor> provider3) {
        return new NavigationModeController((Context) provider.get(), (DeviceProvisionedController) provider2.get(), (Executor) provider3.get());
    }

    public static NavigationModeController_Factory create(Provider<Context> provider, Provider<DeviceProvisionedController> provider2, Provider<Executor> provider3) {
        return new NavigationModeController_Factory(provider, provider2, provider3);
    }
}
