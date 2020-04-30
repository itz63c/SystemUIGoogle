package com.google.android.systemui.elmyra.feedback;

import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class OpaLockscreen_Factory implements Factory<OpaLockscreen> {
    private final Provider<KeyguardStateController> keyguardStateControllerProvider;
    private final Provider<StatusBar> statusBarProvider;

    public OpaLockscreen_Factory(Provider<StatusBar> provider, Provider<KeyguardStateController> provider2) {
        this.statusBarProvider = provider;
        this.keyguardStateControllerProvider = provider2;
    }

    public OpaLockscreen get() {
        return provideInstance(this.statusBarProvider, this.keyguardStateControllerProvider);
    }

    public static OpaLockscreen provideInstance(Provider<StatusBar> provider, Provider<KeyguardStateController> provider2) {
        return new OpaLockscreen((StatusBar) provider.get(), (KeyguardStateController) provider2.get());
    }

    public static OpaLockscreen_Factory create(Provider<StatusBar> provider, Provider<KeyguardStateController> provider2) {
        return new OpaLockscreen_Factory(provider, provider2);
    }
}
