package com.google.android.systemui.columbus.gates;

import android.content.Context;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.statusbar.CommandQueue;
import com.google.android.systemui.columbus.actions.Action;
import dagger.internal.Factory;
import java.util.List;
import javax.inject.Provider;

public final class NavigationBarVisibility_Factory implements Factory<NavigationBarVisibility> {
    private final Provider<AssistManager> assistManagerProvider;
    private final Provider<CommandQueue> commandQueueProvider;
    private final Provider<Context> contextProvider;
    private final Provider<List<Action>> exceptionsProvider;
    private final Provider<KeyguardVisibility> keyguardGateProvider;
    private final Provider<NonGesturalNavigation> navigationModeGateProvider;

    public NavigationBarVisibility_Factory(Provider<Context> provider, Provider<List<Action>> provider2, Provider<AssistManager> provider3, Provider<KeyguardVisibility> provider4, Provider<NonGesturalNavigation> provider5, Provider<CommandQueue> provider6) {
        this.contextProvider = provider;
        this.exceptionsProvider = provider2;
        this.assistManagerProvider = provider3;
        this.keyguardGateProvider = provider4;
        this.navigationModeGateProvider = provider5;
        this.commandQueueProvider = provider6;
    }

    public NavigationBarVisibility get() {
        return provideInstance(this.contextProvider, this.exceptionsProvider, this.assistManagerProvider, this.keyguardGateProvider, this.navigationModeGateProvider, this.commandQueueProvider);
    }

    public static NavigationBarVisibility provideInstance(Provider<Context> provider, Provider<List<Action>> provider2, Provider<AssistManager> provider3, Provider<KeyguardVisibility> provider4, Provider<NonGesturalNavigation> provider5, Provider<CommandQueue> provider6) {
        NavigationBarVisibility navigationBarVisibility = new NavigationBarVisibility((Context) provider.get(), (List) provider2.get(), (AssistManager) provider3.get(), (KeyguardVisibility) provider4.get(), (NonGesturalNavigation) provider5.get(), (CommandQueue) provider6.get());
        return navigationBarVisibility;
    }

    public static NavigationBarVisibility_Factory create(Provider<Context> provider, Provider<List<Action>> provider2, Provider<AssistManager> provider3, Provider<KeyguardVisibility> provider4, Provider<NonGesturalNavigation> provider5, Provider<CommandQueue> provider6) {
        NavigationBarVisibility_Factory navigationBarVisibility_Factory = new NavigationBarVisibility_Factory(provider, provider2, provider3, provider4, provider5, provider6);
        return navigationBarVisibility_Factory;
    }
}
