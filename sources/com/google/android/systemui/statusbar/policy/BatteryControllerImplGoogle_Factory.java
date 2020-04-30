package com.google.android.systemui.statusbar.policy;

import android.content.Context;
import android.os.Handler;
import android.os.PowerManager;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.power.EnhancedEstimates;
import com.google.android.systemui.batteryshare.ReverseWirelessCharger;
import com.google.android.systemui.batteryshare.RtxStatusCallback;
import dagger.internal.Factory;
import java.util.Optional;
import javax.inject.Provider;

public final class BatteryControllerImplGoogle_Factory implements Factory<BatteryControllerImplGoogle> {
    private final Provider<Handler> bgHandlerProvider;
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<Context> contextProvider;
    private final Provider<EnhancedEstimates> enhancedEstimatesProvider;
    private final Provider<Handler> mainHandlerProvider;
    private final Provider<PowerManager> powerManagerProvider;
    private final Provider<Optional<ReverseWirelessCharger>> rtxChargerManagerProvider;
    private final Provider<RtxStatusCallback> rtxStatusCallbackProvider;

    public BatteryControllerImplGoogle_Factory(Provider<Optional<ReverseWirelessCharger>> provider, Provider<Context> provider2, Provider<EnhancedEstimates> provider3, Provider<PowerManager> provider4, Provider<BroadcastDispatcher> provider5, Provider<Handler> provider6, Provider<Handler> provider7, Provider<RtxStatusCallback> provider8) {
        this.rtxChargerManagerProvider = provider;
        this.contextProvider = provider2;
        this.enhancedEstimatesProvider = provider3;
        this.powerManagerProvider = provider4;
        this.broadcastDispatcherProvider = provider5;
        this.mainHandlerProvider = provider6;
        this.bgHandlerProvider = provider7;
        this.rtxStatusCallbackProvider = provider8;
    }

    public BatteryControllerImplGoogle get() {
        return provideInstance(this.rtxChargerManagerProvider, this.contextProvider, this.enhancedEstimatesProvider, this.powerManagerProvider, this.broadcastDispatcherProvider, this.mainHandlerProvider, this.bgHandlerProvider, this.rtxStatusCallbackProvider);
    }

    public static BatteryControllerImplGoogle provideInstance(Provider<Optional<ReverseWirelessCharger>> provider, Provider<Context> provider2, Provider<EnhancedEstimates> provider3, Provider<PowerManager> provider4, Provider<BroadcastDispatcher> provider5, Provider<Handler> provider6, Provider<Handler> provider7, Provider<RtxStatusCallback> provider8) {
        BatteryControllerImplGoogle batteryControllerImplGoogle = new BatteryControllerImplGoogle((Optional) provider.get(), (Context) provider2.get(), (EnhancedEstimates) provider3.get(), (PowerManager) provider4.get(), (BroadcastDispatcher) provider5.get(), (Handler) provider6.get(), (Handler) provider7.get(), (RtxStatusCallback) provider8.get());
        return batteryControllerImplGoogle;
    }

    public static BatteryControllerImplGoogle_Factory create(Provider<Optional<ReverseWirelessCharger>> provider, Provider<Context> provider2, Provider<EnhancedEstimates> provider3, Provider<PowerManager> provider4, Provider<BroadcastDispatcher> provider5, Provider<Handler> provider6, Provider<Handler> provider7, Provider<RtxStatusCallback> provider8) {
        BatteryControllerImplGoogle_Factory batteryControllerImplGoogle_Factory = new BatteryControllerImplGoogle_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8);
        return batteryControllerImplGoogle_Factory;
    }
}
