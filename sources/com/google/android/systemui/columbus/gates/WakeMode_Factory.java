package com.google.android.systemui.columbus.gates;

import android.content.Context;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.google.android.systemui.columbus.ColumbusContentObserver;
import dagger.internal.DoubleCheck;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class WakeMode_Factory implements Factory<WakeMode> {
    private final Provider<Context> contextProvider;
    private final Provider<ColumbusContentObserver.Factory> settingsObserverFactoryProvider;
    private final Provider<WakefulnessLifecycle> wakefulnessLifecycleProvider;

    public WakeMode_Factory(Provider<Context> provider, Provider<WakefulnessLifecycle> provider2, Provider<ColumbusContentObserver.Factory> provider3) {
        this.contextProvider = provider;
        this.wakefulnessLifecycleProvider = provider2;
        this.settingsObserverFactoryProvider = provider3;
    }

    public WakeMode get() {
        return provideInstance(this.contextProvider, this.wakefulnessLifecycleProvider, this.settingsObserverFactoryProvider);
    }

    public static WakeMode provideInstance(Provider<Context> provider, Provider<WakefulnessLifecycle> provider2, Provider<ColumbusContentObserver.Factory> provider3) {
        return new WakeMode((Context) provider.get(), DoubleCheck.lazy(provider2), (ColumbusContentObserver.Factory) provider3.get());
    }

    public static WakeMode_Factory create(Provider<Context> provider, Provider<WakefulnessLifecycle> provider2, Provider<ColumbusContentObserver.Factory> provider3) {
        return new WakeMode_Factory(provider, provider2, provider3);
    }
}
