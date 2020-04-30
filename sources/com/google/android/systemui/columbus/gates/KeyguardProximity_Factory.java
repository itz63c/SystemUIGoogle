package com.google.android.systemui.columbus.gates;

import android.content.Context;
import com.android.systemui.util.sensors.AsyncSensorManager;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class KeyguardProximity_Factory implements Factory<KeyguardProximity> {
    private final Provider<AsyncSensorManager> asyncSensorManagerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<KeyguardVisibility> keyguardGateProvider;

    public KeyguardProximity_Factory(Provider<Context> provider, Provider<AsyncSensorManager> provider2, Provider<KeyguardVisibility> provider3) {
        this.contextProvider = provider;
        this.asyncSensorManagerProvider = provider2;
        this.keyguardGateProvider = provider3;
    }

    public KeyguardProximity get() {
        return provideInstance(this.contextProvider, this.asyncSensorManagerProvider, this.keyguardGateProvider);
    }

    public static KeyguardProximity provideInstance(Provider<Context> provider, Provider<AsyncSensorManager> provider2, Provider<KeyguardVisibility> provider3) {
        return new KeyguardProximity((Context) provider.get(), (AsyncSensorManager) provider2.get(), (KeyguardVisibility) provider3.get());
    }

    public static KeyguardProximity_Factory create(Provider<Context> provider, Provider<AsyncSensorManager> provider2, Provider<KeyguardVisibility> provider3) {
        return new KeyguardProximity_Factory(provider, provider2, provider3);
    }
}
