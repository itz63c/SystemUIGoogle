package com.google.android.systemui.p012qs.tiles;

import com.android.systemui.p007qs.QSHost;
import com.android.systemui.statusbar.policy.BatteryController;
import dagger.internal.Factory;
import javax.inject.Provider;

/* renamed from: com.google.android.systemui.qs.tiles.BatteryShareTile_Factory */
public final class BatteryShareTile_Factory implements Factory<BatteryShareTile> {
    private final Provider<BatteryController> batteryControllerProvider;
    private final Provider<QSHost> hostProvider;

    public BatteryShareTile_Factory(Provider<QSHost> provider, Provider<BatteryController> provider2) {
        this.hostProvider = provider;
        this.batteryControllerProvider = provider2;
    }

    public BatteryShareTile get() {
        return provideInstance(this.hostProvider, this.batteryControllerProvider);
    }

    public static BatteryShareTile provideInstance(Provider<QSHost> provider, Provider<BatteryController> provider2) {
        return new BatteryShareTile((QSHost) provider.get(), (BatteryController) provider2.get());
    }

    public static BatteryShareTile_Factory create(Provider<QSHost> provider, Provider<BatteryController> provider2) {
        return new BatteryShareTile_Factory(provider, provider2);
    }
}
