package com.android.systemui.p007qs.tiles;

import com.android.systemui.p007qs.QSHost;
import com.android.systemui.statusbar.policy.FlashlightController;
import dagger.internal.Factory;
import javax.inject.Provider;

/* renamed from: com.android.systemui.qs.tiles.FlashlightTile_Factory */
public final class FlashlightTile_Factory implements Factory<FlashlightTile> {
    private final Provider<FlashlightController> flashlightControllerProvider;
    private final Provider<QSHost> hostProvider;

    public FlashlightTile_Factory(Provider<QSHost> provider, Provider<FlashlightController> provider2) {
        this.hostProvider = provider;
        this.flashlightControllerProvider = provider2;
    }

    public FlashlightTile get() {
        return provideInstance(this.hostProvider, this.flashlightControllerProvider);
    }

    public static FlashlightTile provideInstance(Provider<QSHost> provider, Provider<FlashlightController> provider2) {
        return new FlashlightTile((QSHost) provider.get(), (FlashlightController) provider2.get());
    }

    public static FlashlightTile_Factory create(Provider<QSHost> provider, Provider<FlashlightController> provider2) {
        return new FlashlightTile_Factory(provider, provider2);
    }
}
