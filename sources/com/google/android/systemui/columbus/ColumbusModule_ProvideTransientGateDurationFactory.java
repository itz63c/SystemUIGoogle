package com.google.android.systemui.columbus;

import dagger.internal.Factory;

public final class ColumbusModule_ProvideTransientGateDurationFactory implements Factory<Long> {
    private static final ColumbusModule_ProvideTransientGateDurationFactory INSTANCE = new ColumbusModule_ProvideTransientGateDurationFactory();

    public Long get() {
        return provideInstance();
    }

    public static Long provideInstance() {
        return Long.valueOf(proxyProvideTransientGateDuration());
    }

    public static ColumbusModule_ProvideTransientGateDurationFactory create() {
        return INSTANCE;
    }

    public static long proxyProvideTransientGateDuration() {
        return ColumbusModule.provideTransientGateDuration();
    }
}
