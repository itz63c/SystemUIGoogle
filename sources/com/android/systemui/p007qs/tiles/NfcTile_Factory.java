package com.android.systemui.p007qs.tiles;

import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.p007qs.QSHost;
import dagger.internal.Factory;
import javax.inject.Provider;

/* renamed from: com.android.systemui.qs.tiles.NfcTile_Factory */
public final class NfcTile_Factory implements Factory<NfcTile> {
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<QSHost> hostProvider;

    public NfcTile_Factory(Provider<QSHost> provider, Provider<BroadcastDispatcher> provider2) {
        this.hostProvider = provider;
        this.broadcastDispatcherProvider = provider2;
    }

    public NfcTile get() {
        return provideInstance(this.hostProvider, this.broadcastDispatcherProvider);
    }

    public static NfcTile provideInstance(Provider<QSHost> provider, Provider<BroadcastDispatcher> provider2) {
        return new NfcTile((QSHost) provider.get(), (BroadcastDispatcher) provider2.get());
    }

    public static NfcTile_Factory create(Provider<QSHost> provider, Provider<BroadcastDispatcher> provider2) {
        return new NfcTile_Factory(provider, provider2);
    }
}
