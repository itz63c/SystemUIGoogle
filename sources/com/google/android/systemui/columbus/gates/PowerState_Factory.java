package com.google.android.systemui.columbus.gates;

import android.content.Context;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import dagger.internal.DoubleCheck;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class PowerState_Factory implements Factory<PowerState> {
    private final Provider<Context> contextProvider;
    private final Provider<WakefulnessLifecycle> wakefulnessLifecycleProvider;

    public PowerState_Factory(Provider<Context> provider, Provider<WakefulnessLifecycle> provider2) {
        this.contextProvider = provider;
        this.wakefulnessLifecycleProvider = provider2;
    }

    public PowerState get() {
        return provideInstance(this.contextProvider, this.wakefulnessLifecycleProvider);
    }

    public static PowerState provideInstance(Provider<Context> provider, Provider<WakefulnessLifecycle> provider2) {
        return new PowerState((Context) provider.get(), DoubleCheck.lazy(provider2));
    }

    public static PowerState_Factory create(Provider<Context> provider, Provider<WakefulnessLifecycle> provider2) {
        return new PowerState_Factory(provider, provider2);
    }
}
