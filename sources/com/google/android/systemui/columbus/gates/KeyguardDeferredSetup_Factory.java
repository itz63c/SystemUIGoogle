package com.google.android.systemui.columbus.gates;

import android.content.Context;
import com.google.android.systemui.columbus.ColumbusContentObserver;
import com.google.android.systemui.columbus.actions.Action;
import dagger.internal.Factory;
import java.util.List;
import javax.inject.Provider;

public final class KeyguardDeferredSetup_Factory implements Factory<KeyguardDeferredSetup> {
    private final Provider<Context> contextProvider;
    private final Provider<List<Action>> exceptionsProvider;
    private final Provider<KeyguardVisibility> keyguardGateProvider;
    private final Provider<ColumbusContentObserver.Factory> settingsObserverFactoryProvider;

    public KeyguardDeferredSetup_Factory(Provider<Context> provider, Provider<List<Action>> provider2, Provider<KeyguardVisibility> provider3, Provider<ColumbusContentObserver.Factory> provider4) {
        this.contextProvider = provider;
        this.exceptionsProvider = provider2;
        this.keyguardGateProvider = provider3;
        this.settingsObserverFactoryProvider = provider4;
    }

    public KeyguardDeferredSetup get() {
        return provideInstance(this.contextProvider, this.exceptionsProvider, this.keyguardGateProvider, this.settingsObserverFactoryProvider);
    }

    public static KeyguardDeferredSetup provideInstance(Provider<Context> provider, Provider<List<Action>> provider2, Provider<KeyguardVisibility> provider3, Provider<ColumbusContentObserver.Factory> provider4) {
        return new KeyguardDeferredSetup((Context) provider.get(), (List) provider2.get(), (KeyguardVisibility) provider3.get(), (ColumbusContentObserver.Factory) provider4.get());
    }

    public static KeyguardDeferredSetup_Factory create(Provider<Context> provider, Provider<List<Action>> provider2, Provider<KeyguardVisibility> provider3, Provider<ColumbusContentObserver.Factory> provider4) {
        return new KeyguardDeferredSetup_Factory(provider, provider2, provider3, provider4);
    }
}
