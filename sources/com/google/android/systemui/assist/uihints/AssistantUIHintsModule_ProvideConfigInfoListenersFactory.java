package com.google.android.systemui.assist.uihints;

import com.google.android.systemui.assist.uihints.NgaMessageHandler.ConfigInfoListener;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import java.util.Set;
import javax.inject.Provider;

public final class AssistantUIHintsModule_ProvideConfigInfoListenersFactory implements Factory<Set<ConfigInfoListener>> {
    private final Provider<AssistantPresenceHandler> assistantPresenceHandlerProvider;
    private final Provider<ColorChangeHandler> colorChangeHandlerProvider;
    private final Provider<ConfigurationHandler> configurationHandlerProvider;
    private final Provider<KeyboardMonitor> keyboardMonitorProvider;
    private final Provider<TaskStackNotifier> taskStackNotifierProvider;
    private final Provider<TouchInsideHandler> touchInsideHandlerProvider;
    private final Provider<TouchOutsideHandler> touchOutsideHandlerProvider;

    public AssistantUIHintsModule_ProvideConfigInfoListenersFactory(Provider<AssistantPresenceHandler> provider, Provider<TouchInsideHandler> provider2, Provider<TouchOutsideHandler> provider3, Provider<TaskStackNotifier> provider4, Provider<KeyboardMonitor> provider5, Provider<ColorChangeHandler> provider6, Provider<ConfigurationHandler> provider7) {
        this.assistantPresenceHandlerProvider = provider;
        this.touchInsideHandlerProvider = provider2;
        this.touchOutsideHandlerProvider = provider3;
        this.taskStackNotifierProvider = provider4;
        this.keyboardMonitorProvider = provider5;
        this.colorChangeHandlerProvider = provider6;
        this.configurationHandlerProvider = provider7;
    }

    public Set<ConfigInfoListener> get() {
        return provideInstance(this.assistantPresenceHandlerProvider, this.touchInsideHandlerProvider, this.touchOutsideHandlerProvider, this.taskStackNotifierProvider, this.keyboardMonitorProvider, this.colorChangeHandlerProvider, this.configurationHandlerProvider);
    }

    public static Set<ConfigInfoListener> provideInstance(Provider<AssistantPresenceHandler> provider, Provider<TouchInsideHandler> provider2, Provider<TouchOutsideHandler> provider3, Provider<TaskStackNotifier> provider4, Provider<KeyboardMonitor> provider5, Provider<ColorChangeHandler> provider6, Provider<ConfigurationHandler> provider7) {
        return proxyProvideConfigInfoListeners((AssistantPresenceHandler) provider.get(), (TouchInsideHandler) provider2.get(), provider3.get(), provider4.get(), provider5.get(), (ColorChangeHandler) provider6.get(), (ConfigurationHandler) provider7.get());
    }

    public static AssistantUIHintsModule_ProvideConfigInfoListenersFactory create(Provider<AssistantPresenceHandler> provider, Provider<TouchInsideHandler> provider2, Provider<TouchOutsideHandler> provider3, Provider<TaskStackNotifier> provider4, Provider<KeyboardMonitor> provider5, Provider<ColorChangeHandler> provider6, Provider<ConfigurationHandler> provider7) {
        AssistantUIHintsModule_ProvideConfigInfoListenersFactory assistantUIHintsModule_ProvideConfigInfoListenersFactory = new AssistantUIHintsModule_ProvideConfigInfoListenersFactory(provider, provider2, provider3, provider4, provider5, provider6, provider7);
        return assistantUIHintsModule_ProvideConfigInfoListenersFactory;
    }

    public static Set<ConfigInfoListener> proxyProvideConfigInfoListeners(AssistantPresenceHandler assistantPresenceHandler, TouchInsideHandler touchInsideHandler, Object obj, Object obj2, Object obj3, ColorChangeHandler colorChangeHandler, ConfigurationHandler configurationHandler) {
        Set<ConfigInfoListener> provideConfigInfoListeners = AssistantUIHintsModule.provideConfigInfoListeners(assistantPresenceHandler, touchInsideHandler, (TouchOutsideHandler) obj, (TaskStackNotifier) obj2, (KeyboardMonitor) obj3, colorChangeHandler, configurationHandler);
        Preconditions.checkNotNull(provideConfigInfoListeners, "Cannot return null from a non-@Nullable @Provides method");
        return provideConfigInfoListeners;
    }
}
