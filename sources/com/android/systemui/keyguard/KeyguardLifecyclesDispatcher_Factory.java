package com.android.systemui.keyguard;

import dagger.internal.Factory;
import javax.inject.Provider;

public final class KeyguardLifecyclesDispatcher_Factory implements Factory<KeyguardLifecyclesDispatcher> {
    private final Provider<ScreenLifecycle> screenLifecycleProvider;
    private final Provider<WakefulnessLifecycle> wakefulnessLifecycleProvider;

    public KeyguardLifecyclesDispatcher_Factory(Provider<ScreenLifecycle> provider, Provider<WakefulnessLifecycle> provider2) {
        this.screenLifecycleProvider = provider;
        this.wakefulnessLifecycleProvider = provider2;
    }

    public KeyguardLifecyclesDispatcher get() {
        return provideInstance(this.screenLifecycleProvider, this.wakefulnessLifecycleProvider);
    }

    public static KeyguardLifecyclesDispatcher provideInstance(Provider<ScreenLifecycle> provider, Provider<WakefulnessLifecycle> provider2) {
        return new KeyguardLifecyclesDispatcher((ScreenLifecycle) provider.get(), (WakefulnessLifecycle) provider2.get());
    }

    public static KeyguardLifecyclesDispatcher_Factory create(Provider<ScreenLifecycle> provider, Provider<WakefulnessLifecycle> provider2) {
        return new KeyguardLifecyclesDispatcher_Factory(provider, provider2);
    }
}
