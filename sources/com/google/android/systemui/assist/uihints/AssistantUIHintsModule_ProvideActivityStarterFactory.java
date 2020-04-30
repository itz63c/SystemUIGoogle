package com.google.android.systemui.assist.uihints;

import com.android.systemui.statusbar.phone.StatusBar;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.StartActivityInfoListener;
import dagger.Lazy;
import dagger.internal.DoubleCheck;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

public final class AssistantUIHintsModule_ProvideActivityStarterFactory implements Factory<StartActivityInfoListener> {
    private final Provider<StatusBar> statusBarLazyProvider;

    public AssistantUIHintsModule_ProvideActivityStarterFactory(Provider<StatusBar> provider) {
        this.statusBarLazyProvider = provider;
    }

    public StartActivityInfoListener get() {
        return provideInstance(this.statusBarLazyProvider);
    }

    public static StartActivityInfoListener provideInstance(Provider<StatusBar> provider) {
        return proxyProvideActivityStarter(DoubleCheck.lazy(provider));
    }

    public static AssistantUIHintsModule_ProvideActivityStarterFactory create(Provider<StatusBar> provider) {
        return new AssistantUIHintsModule_ProvideActivityStarterFactory(provider);
    }

    public static StartActivityInfoListener proxyProvideActivityStarter(Lazy<StatusBar> lazy) {
        StartActivityInfoListener provideActivityStarter = AssistantUIHintsModule.provideActivityStarter(lazy);
        Preconditions.checkNotNull(provideActivityStarter, "Cannot return null from a non-@Nullable @Provides method");
        return provideActivityStarter;
    }
}
