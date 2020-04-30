package com.google.android.systemui.columbus;

import com.android.internal.logging.MetricsLogger;
import dagger.internal.Factory;
import dagger.internal.Preconditions;

public final class ColumbusModule_ProvideColumbusLoggerFactory implements Factory<MetricsLogger> {
    private static final ColumbusModule_ProvideColumbusLoggerFactory INSTANCE = new ColumbusModule_ProvideColumbusLoggerFactory();

    public MetricsLogger get() {
        return provideInstance();
    }

    public static MetricsLogger provideInstance() {
        return proxyProvideColumbusLogger();
    }

    public static ColumbusModule_ProvideColumbusLoggerFactory create() {
        return INSTANCE;
    }

    public static MetricsLogger proxyProvideColumbusLogger() {
        MetricsLogger provideColumbusLogger = ColumbusModule.provideColumbusLogger();
        Preconditions.checkNotNull(provideColumbusLogger, "Cannot return null from a non-@Nullable @Provides method");
        return provideColumbusLogger;
    }
}
