package com.android.systemui.p007qs.tiles;

import com.android.systemui.p007qs.QSHost;
import dagger.internal.Factory;
import javax.inject.Provider;

/* renamed from: com.android.systemui.qs.tiles.NightDisplayTile_Factory */
public final class NightDisplayTile_Factory implements Factory<NightDisplayTile> {
    private final Provider<QSHost> hostProvider;

    public NightDisplayTile_Factory(Provider<QSHost> provider) {
        this.hostProvider = provider;
    }

    public NightDisplayTile get() {
        return provideInstance(this.hostProvider);
    }

    public static NightDisplayTile provideInstance(Provider<QSHost> provider) {
        return new NightDisplayTile((QSHost) provider.get());
    }

    public static NightDisplayTile_Factory create(Provider<QSHost> provider) {
        return new NightDisplayTile_Factory(provider);
    }
}
