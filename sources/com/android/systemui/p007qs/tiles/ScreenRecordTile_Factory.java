package com.android.systemui.p007qs.tiles;

import com.android.systemui.p007qs.QSHost;
import com.android.systemui.screenrecord.RecordingController;
import dagger.internal.Factory;
import javax.inject.Provider;

/* renamed from: com.android.systemui.qs.tiles.ScreenRecordTile_Factory */
public final class ScreenRecordTile_Factory implements Factory<ScreenRecordTile> {
    private final Provider<RecordingController> controllerProvider;
    private final Provider<QSHost> hostProvider;

    public ScreenRecordTile_Factory(Provider<QSHost> provider, Provider<RecordingController> provider2) {
        this.hostProvider = provider;
        this.controllerProvider = provider2;
    }

    public ScreenRecordTile get() {
        return provideInstance(this.hostProvider, this.controllerProvider);
    }

    public static ScreenRecordTile provideInstance(Provider<QSHost> provider, Provider<RecordingController> provider2) {
        return new ScreenRecordTile((QSHost) provider.get(), (RecordingController) provider2.get());
    }

    public static ScreenRecordTile_Factory create(Provider<QSHost> provider, Provider<RecordingController> provider2) {
        return new ScreenRecordTile_Factory(provider, provider2);
    }
}
