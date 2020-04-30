package com.android.systemui.p007qs.tiles;

import com.android.systemui.p007qs.QSHost;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.statusbar.policy.NetworkController;
import dagger.internal.Factory;
import javax.inject.Provider;

/* renamed from: com.android.systemui.qs.tiles.WifiTile_Factory */
public final class WifiTile_Factory implements Factory<WifiTile> {
    private final Provider<ActivityStarter> activityStarterProvider;
    private final Provider<QSHost> hostProvider;
    private final Provider<NetworkController> networkControllerProvider;

    public WifiTile_Factory(Provider<QSHost> provider, Provider<NetworkController> provider2, Provider<ActivityStarter> provider3) {
        this.hostProvider = provider;
        this.networkControllerProvider = provider2;
        this.activityStarterProvider = provider3;
    }

    public WifiTile get() {
        return provideInstance(this.hostProvider, this.networkControllerProvider, this.activityStarterProvider);
    }

    public static WifiTile provideInstance(Provider<QSHost> provider, Provider<NetworkController> provider2, Provider<ActivityStarter> provider3) {
        return new WifiTile((QSHost) provider.get(), (NetworkController) provider2.get(), (ActivityStarter) provider3.get());
    }

    public static WifiTile_Factory create(Provider<QSHost> provider, Provider<NetworkController> provider2, Provider<ActivityStarter> provider3) {
        return new WifiTile_Factory(provider, provider2, provider3);
    }
}
