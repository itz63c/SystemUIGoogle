package com.android.systemui.statusbar.phone;

import android.content.res.Resources;
import android.hardware.display.AmbientDisplayConfiguration;
import android.os.PowerManager;
import com.android.systemui.doze.AlwaysOnDisplayPolicy;
import com.android.systemui.tuner.TunerService;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class DozeParameters_Factory implements Factory<DozeParameters> {
    private final Provider<AlwaysOnDisplayPolicy> alwaysOnDisplayPolicyProvider;
    private final Provider<AmbientDisplayConfiguration> ambientDisplayConfigurationProvider;
    private final Provider<PowerManager> powerManagerProvider;
    private final Provider<Resources> resourcesProvider;
    private final Provider<TunerService> tunerServiceProvider;

    public DozeParameters_Factory(Provider<Resources> provider, Provider<AmbientDisplayConfiguration> provider2, Provider<AlwaysOnDisplayPolicy> provider3, Provider<PowerManager> provider4, Provider<TunerService> provider5) {
        this.resourcesProvider = provider;
        this.ambientDisplayConfigurationProvider = provider2;
        this.alwaysOnDisplayPolicyProvider = provider3;
        this.powerManagerProvider = provider4;
        this.tunerServiceProvider = provider5;
    }

    public DozeParameters get() {
        return provideInstance(this.resourcesProvider, this.ambientDisplayConfigurationProvider, this.alwaysOnDisplayPolicyProvider, this.powerManagerProvider, this.tunerServiceProvider);
    }

    public static DozeParameters provideInstance(Provider<Resources> provider, Provider<AmbientDisplayConfiguration> provider2, Provider<AlwaysOnDisplayPolicy> provider3, Provider<PowerManager> provider4, Provider<TunerService> provider5) {
        DozeParameters dozeParameters = new DozeParameters((Resources) provider.get(), (AmbientDisplayConfiguration) provider2.get(), (AlwaysOnDisplayPolicy) provider3.get(), (PowerManager) provider4.get(), (TunerService) provider5.get());
        return dozeParameters;
    }

    public static DozeParameters_Factory create(Provider<Resources> provider, Provider<AmbientDisplayConfiguration> provider2, Provider<AlwaysOnDisplayPolicy> provider3, Provider<PowerManager> provider4, Provider<TunerService> provider5) {
        DozeParameters_Factory dozeParameters_Factory = new DozeParameters_Factory(provider, provider2, provider3, provider4, provider5);
        return dozeParameters_Factory;
    }
}
