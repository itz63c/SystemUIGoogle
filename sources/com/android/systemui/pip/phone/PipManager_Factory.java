package com.android.systemui.pip.phone;

import android.content.Context;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.p010wm.DisplayController;
import com.android.systemui.pip.PipBoundsHandler;
import com.android.systemui.pip.PipSnapAlgorithm;
import com.android.systemui.pip.PipSurfaceTransactionHelper;
import com.android.systemui.util.DeviceConfigProxy;
import com.android.systemui.util.FloatingContentCoordinator;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class PipManager_Factory implements Factory<PipManager> {
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<Context> contextProvider;
    private final Provider<DeviceConfigProxy> deviceConfigProvider;
    private final Provider<DisplayController> displayControllerProvider;
    private final Provider<FloatingContentCoordinator> floatingContentCoordinatorProvider;
    private final Provider<PipBoundsHandler> pipBoundsHandlerProvider;
    private final Provider<PipSnapAlgorithm> pipSnapAlgorithmProvider;
    private final Provider<PipSurfaceTransactionHelper> surfaceTransactionHelperProvider;

    public PipManager_Factory(Provider<Context> provider, Provider<BroadcastDispatcher> provider2, Provider<DisplayController> provider3, Provider<FloatingContentCoordinator> provider4, Provider<DeviceConfigProxy> provider5, Provider<PipBoundsHandler> provider6, Provider<PipSnapAlgorithm> provider7, Provider<PipSurfaceTransactionHelper> provider8) {
        this.contextProvider = provider;
        this.broadcastDispatcherProvider = provider2;
        this.displayControllerProvider = provider3;
        this.floatingContentCoordinatorProvider = provider4;
        this.deviceConfigProvider = provider5;
        this.pipBoundsHandlerProvider = provider6;
        this.pipSnapAlgorithmProvider = provider7;
        this.surfaceTransactionHelperProvider = provider8;
    }

    public PipManager get() {
        return provideInstance(this.contextProvider, this.broadcastDispatcherProvider, this.displayControllerProvider, this.floatingContentCoordinatorProvider, this.deviceConfigProvider, this.pipBoundsHandlerProvider, this.pipSnapAlgorithmProvider, this.surfaceTransactionHelperProvider);
    }

    public static PipManager provideInstance(Provider<Context> provider, Provider<BroadcastDispatcher> provider2, Provider<DisplayController> provider3, Provider<FloatingContentCoordinator> provider4, Provider<DeviceConfigProxy> provider5, Provider<PipBoundsHandler> provider6, Provider<PipSnapAlgorithm> provider7, Provider<PipSurfaceTransactionHelper> provider8) {
        PipManager pipManager = new PipManager((Context) provider.get(), (BroadcastDispatcher) provider2.get(), (DisplayController) provider3.get(), (FloatingContentCoordinator) provider4.get(), (DeviceConfigProxy) provider5.get(), (PipBoundsHandler) provider6.get(), (PipSnapAlgorithm) provider7.get(), (PipSurfaceTransactionHelper) provider8.get());
        return pipManager;
    }

    public static PipManager_Factory create(Provider<Context> provider, Provider<BroadcastDispatcher> provider2, Provider<DisplayController> provider3, Provider<FloatingContentCoordinator> provider4, Provider<DeviceConfigProxy> provider5, Provider<PipBoundsHandler> provider6, Provider<PipSnapAlgorithm> provider7, Provider<PipSurfaceTransactionHelper> provider8) {
        PipManager_Factory pipManager_Factory = new PipManager_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8);
        return pipManager_Factory;
    }
}
