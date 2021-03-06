package com.android.systemui.statusbar;

import dagger.internal.Factory;
import java.util.concurrent.Executor;
import javax.inject.Provider;

public final class FeatureFlags_Factory implements Factory<FeatureFlags> {
    private final Provider<Executor> executorProvider;

    public FeatureFlags_Factory(Provider<Executor> provider) {
        this.executorProvider = provider;
    }

    public FeatureFlags get() {
        return provideInstance(this.executorProvider);
    }

    public static FeatureFlags provideInstance(Provider<Executor> provider) {
        return new FeatureFlags((Executor) provider.get());
    }

    public static FeatureFlags_Factory create(Provider<Executor> provider) {
        return new FeatureFlags_Factory(provider);
    }
}
