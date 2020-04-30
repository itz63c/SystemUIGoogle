package com.android.systemui.globalactions;

import android.content.Context;
import com.android.systemui.plugins.GlobalActions;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.policy.ExtensionController;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class GlobalActionsComponent_Factory implements Factory<GlobalActionsComponent> {
    private final Provider<CommandQueue> commandQueueProvider;
    private final Provider<Context> contextProvider;
    private final Provider<ExtensionController> extensionControllerProvider;
    private final Provider<GlobalActions> globalActionsProvider;

    public GlobalActionsComponent_Factory(Provider<Context> provider, Provider<CommandQueue> provider2, Provider<ExtensionController> provider3, Provider<GlobalActions> provider4) {
        this.contextProvider = provider;
        this.commandQueueProvider = provider2;
        this.extensionControllerProvider = provider3;
        this.globalActionsProvider = provider4;
    }

    public GlobalActionsComponent get() {
        return provideInstance(this.contextProvider, this.commandQueueProvider, this.extensionControllerProvider, this.globalActionsProvider);
    }

    public static GlobalActionsComponent provideInstance(Provider<Context> provider, Provider<CommandQueue> provider2, Provider<ExtensionController> provider3, Provider<GlobalActions> provider4) {
        return new GlobalActionsComponent((Context) provider.get(), (CommandQueue) provider2.get(), (ExtensionController) provider3.get(), provider4);
    }

    public static GlobalActionsComponent_Factory create(Provider<Context> provider, Provider<CommandQueue> provider2, Provider<ExtensionController> provider3, Provider<GlobalActions> provider4) {
        return new GlobalActionsComponent_Factory(provider, provider2, provider3, provider4);
    }
}
