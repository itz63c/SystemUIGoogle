package com.google.android.systemui.columbus.gates;

import android.content.Context;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class KeyguardVisibility_Factory implements Factory<KeyguardVisibility> {
    private final Provider<Context> contextProvider;
    private final Provider<KeyguardStateController> keyguardStateControllerProvider;

    public KeyguardVisibility_Factory(Provider<Context> provider, Provider<KeyguardStateController> provider2) {
        this.contextProvider = provider;
        this.keyguardStateControllerProvider = provider2;
    }

    public KeyguardVisibility get() {
        return provideInstance(this.contextProvider, this.keyguardStateControllerProvider);
    }

    public static KeyguardVisibility provideInstance(Provider<Context> provider, Provider<KeyguardStateController> provider2) {
        return new KeyguardVisibility((Context) provider.get(), (KeyguardStateController) provider2.get());
    }

    public static KeyguardVisibility_Factory create(Provider<Context> provider, Provider<KeyguardStateController> provider2) {
        return new KeyguardVisibility_Factory(provider, provider2);
    }
}
