package com.android.systemui.controls.management;

import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.controls.controller.ControlsControllerImpl;
import dagger.internal.Factory;
import java.util.concurrent.Executor;
import javax.inject.Provider;

public final class ControlsFavoritingActivity_Factory implements Factory<ControlsFavoritingActivity> {
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<ControlsControllerImpl> controllerProvider;
    private final Provider<Executor> executorProvider;
    private final Provider<ControlsListingController> listingControllerProvider;

    public ControlsFavoritingActivity_Factory(Provider<Executor> provider, Provider<ControlsControllerImpl> provider2, Provider<ControlsListingController> provider3, Provider<BroadcastDispatcher> provider4) {
        this.executorProvider = provider;
        this.controllerProvider = provider2;
        this.listingControllerProvider = provider3;
        this.broadcastDispatcherProvider = provider4;
    }

    public ControlsFavoritingActivity get() {
        return provideInstance(this.executorProvider, this.controllerProvider, this.listingControllerProvider, this.broadcastDispatcherProvider);
    }

    public static ControlsFavoritingActivity provideInstance(Provider<Executor> provider, Provider<ControlsControllerImpl> provider2, Provider<ControlsListingController> provider3, Provider<BroadcastDispatcher> provider4) {
        return new ControlsFavoritingActivity((Executor) provider.get(), (ControlsControllerImpl) provider2.get(), (ControlsListingController) provider3.get(), (BroadcastDispatcher) provider4.get());
    }

    public static ControlsFavoritingActivity_Factory create(Provider<Executor> provider, Provider<ControlsControllerImpl> provider2, Provider<ControlsListingController> provider3, Provider<BroadcastDispatcher> provider4) {
        return new ControlsFavoritingActivity_Factory(provider, provider2, provider3, provider4);
    }
}
