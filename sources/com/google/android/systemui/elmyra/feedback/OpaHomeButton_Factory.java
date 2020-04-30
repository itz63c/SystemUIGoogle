package com.google.android.systemui.elmyra.feedback;

import com.android.systemui.keyguard.KeyguardViewMediator;
import com.android.systemui.statusbar.phone.StatusBar;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class OpaHomeButton_Factory implements Factory<OpaHomeButton> {
    private final Provider<KeyguardViewMediator> keyguardViewMediatorProvider;
    private final Provider<StatusBar> statusBarProvider;

    public OpaHomeButton_Factory(Provider<KeyguardViewMediator> provider, Provider<StatusBar> provider2) {
        this.keyguardViewMediatorProvider = provider;
        this.statusBarProvider = provider2;
    }

    public OpaHomeButton get() {
        return provideInstance(this.keyguardViewMediatorProvider, this.statusBarProvider);
    }

    public static OpaHomeButton provideInstance(Provider<KeyguardViewMediator> provider, Provider<StatusBar> provider2) {
        return new OpaHomeButton((KeyguardViewMediator) provider.get(), (StatusBar) provider2.get());
    }

    public static OpaHomeButton_Factory create(Provider<KeyguardViewMediator> provider, Provider<StatusBar> provider2) {
        return new OpaHomeButton_Factory(provider, provider2);
    }
}
