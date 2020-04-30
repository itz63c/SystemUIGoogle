package com.android.systemui.p007qs.tiles;

import com.android.systemui.p007qs.QSHost;
import dagger.internal.Factory;
import javax.inject.Provider;

/* renamed from: com.android.systemui.qs.tiles.ColorInversionTile_Factory */
public final class ColorInversionTile_Factory implements Factory<ColorInversionTile> {
    private final Provider<QSHost> hostProvider;

    public ColorInversionTile_Factory(Provider<QSHost> provider) {
        this.hostProvider = provider;
    }

    public ColorInversionTile get() {
        return provideInstance(this.hostProvider);
    }

    public static ColorInversionTile provideInstance(Provider<QSHost> provider) {
        return new ColorInversionTile((QSHost) provider.get());
    }

    public static ColorInversionTile_Factory create(Provider<QSHost> provider) {
        return new ColorInversionTile_Factory(provider);
    }
}
