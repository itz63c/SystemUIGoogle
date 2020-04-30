package com.android.systemui.pip.p005tv;

import android.content.Context;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.pip.PipBoundsHandler;
import com.android.systemui.pip.PipSurfaceTransactionHelper;
import dagger.internal.Factory;
import javax.inject.Provider;

/* renamed from: com.android.systemui.pip.tv.PipManager_Factory */
public final class PipManager_Factory implements Factory<PipManager> {
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<Context> contextProvider;
    private final Provider<PipBoundsHandler> pipBoundsHandlerProvider;
    private final Provider<PipSurfaceTransactionHelper> surfaceTransactionHelperProvider;

    public PipManager_Factory(Provider<Context> provider, Provider<BroadcastDispatcher> provider2, Provider<PipBoundsHandler> provider3, Provider<PipSurfaceTransactionHelper> provider4) {
        this.contextProvider = provider;
        this.broadcastDispatcherProvider = provider2;
        this.pipBoundsHandlerProvider = provider3;
        this.surfaceTransactionHelperProvider = provider4;
    }

    public PipManager get() {
        return provideInstance(this.contextProvider, this.broadcastDispatcherProvider, this.pipBoundsHandlerProvider, this.surfaceTransactionHelperProvider);
    }

    public static PipManager provideInstance(Provider<Context> provider, Provider<BroadcastDispatcher> provider2, Provider<PipBoundsHandler> provider3, Provider<PipSurfaceTransactionHelper> provider4) {
        return new PipManager((Context) provider.get(), (BroadcastDispatcher) provider2.get(), (PipBoundsHandler) provider3.get(), (PipSurfaceTransactionHelper) provider4.get());
    }

    public static PipManager_Factory create(Provider<Context> provider, Provider<BroadcastDispatcher> provider2, Provider<PipBoundsHandler> provider3, Provider<PipSurfaceTransactionHelper> provider4) {
        return new PipManager_Factory(provider, provider2, provider3, provider4);
    }
}
