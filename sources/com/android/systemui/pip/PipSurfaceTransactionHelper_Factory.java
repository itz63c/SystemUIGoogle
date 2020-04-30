package com.android.systemui.pip;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class PipSurfaceTransactionHelper_Factory implements Factory<PipSurfaceTransactionHelper> {
    private final Provider<Context> contextProvider;

    public PipSurfaceTransactionHelper_Factory(Provider<Context> provider) {
        this.contextProvider = provider;
    }

    public PipSurfaceTransactionHelper get() {
        return provideInstance(this.contextProvider);
    }

    public static PipSurfaceTransactionHelper provideInstance(Provider<Context> provider) {
        return new PipSurfaceTransactionHelper((Context) provider.get());
    }

    public static PipSurfaceTransactionHelper_Factory create(Provider<Context> provider) {
        return new PipSurfaceTransactionHelper_Factory(provider);
    }
}
