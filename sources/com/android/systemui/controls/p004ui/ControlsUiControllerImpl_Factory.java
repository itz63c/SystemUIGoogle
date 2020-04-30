package com.android.systemui.controls.p004ui;

import android.content.Context;
import android.content.SharedPreferences;
import com.android.systemui.controls.controller.ControlsController;
import com.android.systemui.controls.management.ControlsListingController;
import com.android.systemui.util.concurrency.DelayableExecutor;
import dagger.internal.DoubleCheck;
import dagger.internal.Factory;
import javax.inject.Provider;

/* renamed from: com.android.systemui.controls.ui.ControlsUiControllerImpl_Factory */
public final class ControlsUiControllerImpl_Factory implements Factory<ControlsUiControllerImpl> {
    private final Provider<DelayableExecutor> bgExecutorProvider;
    private final Provider<Context> contextProvider;
    private final Provider<ControlsController> controlsControllerProvider;
    private final Provider<ControlsListingController> controlsListingControllerProvider;
    private final Provider<SharedPreferences> sharedPreferencesProvider;
    private final Provider<DelayableExecutor> uiExecutorProvider;

    public ControlsUiControllerImpl_Factory(Provider<ControlsController> provider, Provider<Context> provider2, Provider<DelayableExecutor> provider3, Provider<DelayableExecutor> provider4, Provider<ControlsListingController> provider5, Provider<SharedPreferences> provider6) {
        this.controlsControllerProvider = provider;
        this.contextProvider = provider2;
        this.uiExecutorProvider = provider3;
        this.bgExecutorProvider = provider4;
        this.controlsListingControllerProvider = provider5;
        this.sharedPreferencesProvider = provider6;
    }

    public ControlsUiControllerImpl get() {
        return provideInstance(this.controlsControllerProvider, this.contextProvider, this.uiExecutorProvider, this.bgExecutorProvider, this.controlsListingControllerProvider, this.sharedPreferencesProvider);
    }

    public static ControlsUiControllerImpl provideInstance(Provider<ControlsController> provider, Provider<Context> provider2, Provider<DelayableExecutor> provider3, Provider<DelayableExecutor> provider4, Provider<ControlsListingController> provider5, Provider<SharedPreferences> provider6) {
        ControlsUiControllerImpl controlsUiControllerImpl = new ControlsUiControllerImpl(DoubleCheck.lazy(provider), (Context) provider2.get(), (DelayableExecutor) provider3.get(), (DelayableExecutor) provider4.get(), DoubleCheck.lazy(provider5), (SharedPreferences) provider6.get());
        return controlsUiControllerImpl;
    }

    public static ControlsUiControllerImpl_Factory create(Provider<ControlsController> provider, Provider<Context> provider2, Provider<DelayableExecutor> provider3, Provider<DelayableExecutor> provider4, Provider<ControlsListingController> provider5, Provider<SharedPreferences> provider6) {
        ControlsUiControllerImpl_Factory controlsUiControllerImpl_Factory = new ControlsUiControllerImpl_Factory(provider, provider2, provider3, provider4, provider5, provider6);
        return controlsUiControllerImpl_Factory;
    }
}
