package com.android.systemui.p010wm;

import android.content.Context;
import android.view.IWindowManager;
import dagger.internal.Factory;
import javax.inject.Provider;

/* renamed from: com.android.systemui.wm.SystemWindows_Factory */
public final class SystemWindows_Factory implements Factory<SystemWindows> {
    private final Provider<Context> contextProvider;
    private final Provider<DisplayController> displayControllerProvider;
    private final Provider<IWindowManager> wmServiceProvider;

    public SystemWindows_Factory(Provider<Context> provider, Provider<DisplayController> provider2, Provider<IWindowManager> provider3) {
        this.contextProvider = provider;
        this.displayControllerProvider = provider2;
        this.wmServiceProvider = provider3;
    }

    public SystemWindows get() {
        return provideInstance(this.contextProvider, this.displayControllerProvider, this.wmServiceProvider);
    }

    public static SystemWindows provideInstance(Provider<Context> provider, Provider<DisplayController> provider2, Provider<IWindowManager> provider3) {
        return new SystemWindows((Context) provider.get(), (DisplayController) provider2.get(), (IWindowManager) provider3.get());
    }

    public static SystemWindows_Factory create(Provider<Context> provider, Provider<DisplayController> provider2, Provider<IWindowManager> provider3) {
        return new SystemWindows_Factory(provider, provider2, provider3);
    }
}
