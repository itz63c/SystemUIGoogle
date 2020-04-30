package com.android.systemui.controls.management;

import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.controls.controller.ControlsController;
import dagger.internal.Factory;
import java.util.concurrent.Executor;
import javax.inject.Provider;

public final class ControlsProviderSelectorActivity_Factory implements Factory<ControlsProviderSelectorActivity> {
    private final Provider<Executor> backExecutorProvider;
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<ControlsController> controlsControllerProvider;
    private final Provider<Executor> executorProvider;
    private final Provider<ControlsListingController> listingControllerProvider;

    public ControlsProviderSelectorActivity_Factory(Provider<Executor> provider, Provider<Executor> provider2, Provider<ControlsListingController> provider3, Provider<ControlsController> provider4, Provider<BroadcastDispatcher> provider5) {
        this.executorProvider = provider;
        this.backExecutorProvider = provider2;
        this.listingControllerProvider = provider3;
        this.controlsControllerProvider = provider4;
        this.broadcastDispatcherProvider = provider5;
    }

    public ControlsProviderSelectorActivity get() {
        return provideInstance(this.executorProvider, this.backExecutorProvider, this.listingControllerProvider, this.controlsControllerProvider, this.broadcastDispatcherProvider);
    }

    public static ControlsProviderSelectorActivity provideInstance(Provider<Executor> provider, Provider<Executor> provider2, Provider<ControlsListingController> provider3, Provider<ControlsController> provider4, Provider<BroadcastDispatcher> provider5) {
        ControlsProviderSelectorActivity controlsProviderSelectorActivity = new ControlsProviderSelectorActivity((Executor) provider.get(), (Executor) provider2.get(), (ControlsListingController) provider3.get(), (ControlsController) provider4.get(), (BroadcastDispatcher) provider5.get());
        return controlsProviderSelectorActivity;
    }

    public static ControlsProviderSelectorActivity_Factory create(Provider<Executor> provider, Provider<Executor> provider2, Provider<ControlsListingController> provider3, Provider<ControlsController> provider4, Provider<BroadcastDispatcher> provider5) {
        ControlsProviderSelectorActivity_Factory controlsProviderSelectorActivity_Factory = new ControlsProviderSelectorActivity_Factory(provider, provider2, provider3, provider4, provider5);
        return controlsProviderSelectorActivity_Factory;
    }
}
