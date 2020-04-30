package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.os.Handler;
import android.os.PowerManager;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.power.EnhancedEstimates;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class BatteryControllerImpl_Factory implements Factory<BatteryControllerImpl> {
    private final Provider<Handler> bgHandlerProvider;
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<Context> contextProvider;
    private final Provider<EnhancedEstimates> enhancedEstimatesProvider;
    private final Provider<Handler> mainHandlerProvider;
    private final Provider<PowerManager> powerManagerProvider;

    public BatteryControllerImpl_Factory(Provider<Context> provider, Provider<EnhancedEstimates> provider2, Provider<PowerManager> provider3, Provider<BroadcastDispatcher> provider4, Provider<Handler> provider5, Provider<Handler> provider6) {
        this.contextProvider = provider;
        this.enhancedEstimatesProvider = provider2;
        this.powerManagerProvider = provider3;
        this.broadcastDispatcherProvider = provider4;
        this.mainHandlerProvider = provider5;
        this.bgHandlerProvider = provider6;
    }

    public BatteryControllerImpl get() {
        return provideInstance(this.contextProvider, this.enhancedEstimatesProvider, this.powerManagerProvider, this.broadcastDispatcherProvider, this.mainHandlerProvider, this.bgHandlerProvider);
    }

    public static BatteryControllerImpl provideInstance(Provider<Context> provider, Provider<EnhancedEstimates> provider2, Provider<PowerManager> provider3, Provider<BroadcastDispatcher> provider4, Provider<Handler> provider5, Provider<Handler> provider6) {
        BatteryControllerImpl batteryControllerImpl = new BatteryControllerImpl((Context) provider.get(), (EnhancedEstimates) provider2.get(), (PowerManager) provider3.get(), (BroadcastDispatcher) provider4.get(), (Handler) provider5.get(), (Handler) provider6.get());
        return batteryControllerImpl;
    }

    public static BatteryControllerImpl_Factory create(Provider<Context> provider, Provider<EnhancedEstimates> provider2, Provider<PowerManager> provider3, Provider<BroadcastDispatcher> provider4, Provider<Handler> provider5, Provider<Handler> provider6) {
        BatteryControllerImpl_Factory batteryControllerImpl_Factory = new BatteryControllerImpl_Factory(provider, provider2, provider3, provider4, provider5, provider6);
        return batteryControllerImpl_Factory;
    }
}
