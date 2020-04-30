package com.android.systemui.p007qs.tiles;

import com.android.systemui.p007qs.QSHost;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import dagger.internal.Factory;
import javax.inject.Provider;

/* renamed from: com.android.systemui.qs.tiles.UiModeNightTile_Factory */
public final class UiModeNightTile_Factory implements Factory<UiModeNightTile> {
    private final Provider<BatteryController> batteryControllerProvider;
    private final Provider<ConfigurationController> configurationControllerProvider;
    private final Provider<QSHost> hostProvider;

    public UiModeNightTile_Factory(Provider<QSHost> provider, Provider<ConfigurationController> provider2, Provider<BatteryController> provider3) {
        this.hostProvider = provider;
        this.configurationControllerProvider = provider2;
        this.batteryControllerProvider = provider3;
    }

    public UiModeNightTile get() {
        return provideInstance(this.hostProvider, this.configurationControllerProvider, this.batteryControllerProvider);
    }

    public static UiModeNightTile provideInstance(Provider<QSHost> provider, Provider<ConfigurationController> provider2, Provider<BatteryController> provider3) {
        return new UiModeNightTile((QSHost) provider.get(), (ConfigurationController) provider2.get(), (BatteryController) provider3.get());
    }

    public static UiModeNightTile_Factory create(Provider<QSHost> provider, Provider<ConfigurationController> provider2, Provider<BatteryController> provider3) {
        return new UiModeNightTile_Factory(provider, provider2, provider3);
    }
}
