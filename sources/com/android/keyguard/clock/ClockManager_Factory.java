package com.android.keyguard.clock;

import android.content.Context;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.dock.DockManager;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.util.InjectionInflationController;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class ClockManager_Factory implements Factory<ClockManager> {
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<SysuiColorExtractor> colorExtractorProvider;
    private final Provider<Context> contextProvider;
    private final Provider<DockManager> dockManagerProvider;
    private final Provider<InjectionInflationController> injectionInflaterProvider;
    private final Provider<PluginManager> pluginManagerProvider;

    public ClockManager_Factory(Provider<Context> provider, Provider<InjectionInflationController> provider2, Provider<PluginManager> provider3, Provider<SysuiColorExtractor> provider4, Provider<DockManager> provider5, Provider<BroadcastDispatcher> provider6) {
        this.contextProvider = provider;
        this.injectionInflaterProvider = provider2;
        this.pluginManagerProvider = provider3;
        this.colorExtractorProvider = provider4;
        this.dockManagerProvider = provider5;
        this.broadcastDispatcherProvider = provider6;
    }

    public ClockManager get() {
        return provideInstance(this.contextProvider, this.injectionInflaterProvider, this.pluginManagerProvider, this.colorExtractorProvider, this.dockManagerProvider, this.broadcastDispatcherProvider);
    }

    public static ClockManager provideInstance(Provider<Context> provider, Provider<InjectionInflationController> provider2, Provider<PluginManager> provider3, Provider<SysuiColorExtractor> provider4, Provider<DockManager> provider5, Provider<BroadcastDispatcher> provider6) {
        ClockManager clockManager = new ClockManager((Context) provider.get(), (InjectionInflationController) provider2.get(), (PluginManager) provider3.get(), (SysuiColorExtractor) provider4.get(), (DockManager) provider5.get(), (BroadcastDispatcher) provider6.get());
        return clockManager;
    }

    public static ClockManager_Factory create(Provider<Context> provider, Provider<InjectionInflationController> provider2, Provider<PluginManager> provider3, Provider<SysuiColorExtractor> provider4, Provider<DockManager> provider5, Provider<BroadcastDispatcher> provider6) {
        ClockManager_Factory clockManager_Factory = new ClockManager_Factory(provider, provider2, provider3, provider4, provider5, provider6);
        return clockManager_Factory;
    }
}
