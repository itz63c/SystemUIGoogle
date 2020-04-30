package com.android.systemui.pip;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class PipBoundsHandler_Factory implements Factory<PipBoundsHandler> {
    private final Provider<Context> contextProvider;
    private final Provider<PipSnapAlgorithm> pipSnapAlgorithmProvider;

    public PipBoundsHandler_Factory(Provider<Context> provider, Provider<PipSnapAlgorithm> provider2) {
        this.contextProvider = provider;
        this.pipSnapAlgorithmProvider = provider2;
    }

    public PipBoundsHandler get() {
        return provideInstance(this.contextProvider, this.pipSnapAlgorithmProvider);
    }

    public static PipBoundsHandler provideInstance(Provider<Context> provider, Provider<PipSnapAlgorithm> provider2) {
        return new PipBoundsHandler((Context) provider.get(), (PipSnapAlgorithm) provider2.get());
    }

    public static PipBoundsHandler_Factory create(Provider<Context> provider, Provider<PipSnapAlgorithm> provider2) {
        return new PipBoundsHandler_Factory(provider, provider2);
    }
}
