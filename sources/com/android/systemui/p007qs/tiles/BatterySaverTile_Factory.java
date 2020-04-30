package com.android.systemui.p007qs.tiles;

import com.android.systemui.p007qs.QSHost;
import com.android.systemui.statusbar.policy.BatteryController;
import dagger.internal.Factory;
import javax.inject.Provider;

/* renamed from: com.android.systemui.qs.tiles.BatterySaverTile_Factory */
public final class BatterySaverTile_Factory implements Factory<BatterySaverTile> {
    private final Provider<BatteryController> batteryControllerProvider;
    private final Provider<QSHost> hostProvider;

    public BatterySaverTile_Factory(Provider<QSHost> provider, Provider<BatteryController> provider2) {
        this.hostProvider = provider;
        this.batteryControllerProvider = provider2;
    }

    public BatterySaverTile get() {
        return provideInstance(this.hostProvider, this.batteryControllerProvider);
    }

    public static BatterySaverTile provideInstance(Provider<QSHost> provider, Provider<BatteryController> provider2) {
        return new BatterySaverTile((QSHost) provider.get(), (BatteryController) provider2.get());
    }

    public static BatterySaverTile_Factory create(Provider<QSHost> provider, Provider<BatteryController> provider2) {
        return new BatterySaverTile_Factory(provider, provider2);
    }
}
