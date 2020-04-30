package com.google.android.systemui.elmyra.feedback;

import android.content.Context;
import com.android.systemui.keyguard.KeyguardViewMediator;
import com.android.systemui.statusbar.phone.StatusBar;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class SquishyNavigationButtons_Factory implements Factory<SquishyNavigationButtons> {
    private final Provider<Context> contextProvider;
    private final Provider<KeyguardViewMediator> keyguardViewMediatorProvider;
    private final Provider<StatusBar> statusBarProvider;

    public SquishyNavigationButtons_Factory(Provider<Context> provider, Provider<KeyguardViewMediator> provider2, Provider<StatusBar> provider3) {
        this.contextProvider = provider;
        this.keyguardViewMediatorProvider = provider2;
        this.statusBarProvider = provider3;
    }

    public SquishyNavigationButtons get() {
        return provideInstance(this.contextProvider, this.keyguardViewMediatorProvider, this.statusBarProvider);
    }

    public static SquishyNavigationButtons provideInstance(Provider<Context> provider, Provider<KeyguardViewMediator> provider2, Provider<StatusBar> provider3) {
        return new SquishyNavigationButtons((Context) provider.get(), (KeyguardViewMediator) provider2.get(), (StatusBar) provider3.get());
    }

    public static SquishyNavigationButtons_Factory create(Provider<Context> provider, Provider<KeyguardViewMediator> provider2, Provider<StatusBar> provider3) {
        return new SquishyNavigationButtons_Factory(provider, provider2, provider3);
    }
}
