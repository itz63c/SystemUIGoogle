package com.google.android.systemui.power;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class EnhancedEstimatesGoogleImpl_Factory implements Factory<EnhancedEstimatesGoogleImpl> {
    private final Provider<Context> contextProvider;

    public EnhancedEstimatesGoogleImpl_Factory(Provider<Context> provider) {
        this.contextProvider = provider;
    }

    public EnhancedEstimatesGoogleImpl get() {
        return provideInstance(this.contextProvider);
    }

    public static EnhancedEstimatesGoogleImpl provideInstance(Provider<Context> provider) {
        return new EnhancedEstimatesGoogleImpl((Context) provider.get());
    }

    public static EnhancedEstimatesGoogleImpl_Factory create(Provider<Context> provider) {
        return new EnhancedEstimatesGoogleImpl_Factory(provider);
    }
}
