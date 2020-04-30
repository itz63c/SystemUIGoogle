package com.android.systemui.tuner;

import com.android.systemui.tuner.TunablePadding.TunablePaddingService;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class TunablePadding_TunablePaddingService_Factory implements Factory<TunablePaddingService> {
    private final Provider<TunerService> tunerServiceProvider;

    public TunablePadding_TunablePaddingService_Factory(Provider<TunerService> provider) {
        this.tunerServiceProvider = provider;
    }

    public TunablePaddingService get() {
        return provideInstance(this.tunerServiceProvider);
    }

    public static TunablePaddingService provideInstance(Provider<TunerService> provider) {
        return new TunablePaddingService((TunerService) provider.get());
    }

    public static TunablePadding_TunablePaddingService_Factory create(Provider<TunerService> provider) {
        return new TunablePadding_TunablePaddingService_Factory(provider);
    }
}
