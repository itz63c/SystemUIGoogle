package com.android.systemui.statusbar.policy;

import android.content.Context;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.util.leak.LeakDetector;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class ExtensionControllerImpl_Factory implements Factory<ExtensionControllerImpl> {
    private final Provider<ConfigurationController> configurationControllerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<LeakDetector> leakDetectorProvider;
    private final Provider<PluginManager> pluginManagerProvider;
    private final Provider<TunerService> tunerServiceProvider;

    public ExtensionControllerImpl_Factory(Provider<Context> provider, Provider<LeakDetector> provider2, Provider<PluginManager> provider3, Provider<TunerService> provider4, Provider<ConfigurationController> provider5) {
        this.contextProvider = provider;
        this.leakDetectorProvider = provider2;
        this.pluginManagerProvider = provider3;
        this.tunerServiceProvider = provider4;
        this.configurationControllerProvider = provider5;
    }

    public ExtensionControllerImpl get() {
        return provideInstance(this.contextProvider, this.leakDetectorProvider, this.pluginManagerProvider, this.tunerServiceProvider, this.configurationControllerProvider);
    }

    public static ExtensionControllerImpl provideInstance(Provider<Context> provider, Provider<LeakDetector> provider2, Provider<PluginManager> provider3, Provider<TunerService> provider4, Provider<ConfigurationController> provider5) {
        ExtensionControllerImpl extensionControllerImpl = new ExtensionControllerImpl((Context) provider.get(), (LeakDetector) provider2.get(), (PluginManager) provider3.get(), (TunerService) provider4.get(), (ConfigurationController) provider5.get());
        return extensionControllerImpl;
    }

    public static ExtensionControllerImpl_Factory create(Provider<Context> provider, Provider<LeakDetector> provider2, Provider<PluginManager> provider3, Provider<TunerService> provider4, Provider<ConfigurationController> provider5) {
        ExtensionControllerImpl_Factory extensionControllerImpl_Factory = new ExtensionControllerImpl_Factory(provider, provider2, provider3, provider4, provider5);
        return extensionControllerImpl_Factory;
    }
}
