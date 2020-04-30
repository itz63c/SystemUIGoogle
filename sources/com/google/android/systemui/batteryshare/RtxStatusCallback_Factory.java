package com.google.android.systemui.batteryshare;

import com.android.systemui.statusbar.policy.BatteryController;
import dagger.internal.DoubleCheck;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class RtxStatusCallback_Factory implements Factory<RtxStatusCallback> {
    private final Provider<BatteryController> batteryControllerLazyProvider;

    public RtxStatusCallback_Factory(Provider<BatteryController> provider) {
        this.batteryControllerLazyProvider = provider;
    }

    public RtxStatusCallback get() {
        return provideInstance(this.batteryControllerLazyProvider);
    }

    public static RtxStatusCallback provideInstance(Provider<BatteryController> provider) {
        return new RtxStatusCallback(DoubleCheck.lazy(provider));
    }

    public static RtxStatusCallback_Factory create(Provider<BatteryController> provider) {
        return new RtxStatusCallback_Factory(provider);
    }
}
