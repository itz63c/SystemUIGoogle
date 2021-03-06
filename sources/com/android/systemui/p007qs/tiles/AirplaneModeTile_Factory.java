package com.android.systemui.p007qs.tiles;

import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.p007qs.QSHost;
import com.android.systemui.plugins.ActivityStarter;
import dagger.internal.Factory;
import javax.inject.Provider;

/* renamed from: com.android.systemui.qs.tiles.AirplaneModeTile_Factory */
public final class AirplaneModeTile_Factory implements Factory<AirplaneModeTile> {
    private final Provider<ActivityStarter> activityStarterProvider;
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<QSHost> hostProvider;

    public AirplaneModeTile_Factory(Provider<QSHost> provider, Provider<ActivityStarter> provider2, Provider<BroadcastDispatcher> provider3) {
        this.hostProvider = provider;
        this.activityStarterProvider = provider2;
        this.broadcastDispatcherProvider = provider3;
    }

    public AirplaneModeTile get() {
        return provideInstance(this.hostProvider, this.activityStarterProvider, this.broadcastDispatcherProvider);
    }

    public static AirplaneModeTile provideInstance(Provider<QSHost> provider, Provider<ActivityStarter> provider2, Provider<BroadcastDispatcher> provider3) {
        return new AirplaneModeTile((QSHost) provider.get(), (ActivityStarter) provider2.get(), (BroadcastDispatcher) provider3.get());
    }

    public static AirplaneModeTile_Factory create(Provider<QSHost> provider, Provider<ActivityStarter> provider2, Provider<BroadcastDispatcher> provider3) {
        return new AirplaneModeTile_Factory(provider, provider2, provider3);
    }
}
