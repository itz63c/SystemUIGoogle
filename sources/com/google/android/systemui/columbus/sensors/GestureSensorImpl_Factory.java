package com.google.android.systemui.columbus.sensors;

import android.content.Context;
import com.google.android.systemui.columbus.sensors.config.GestureConfiguration;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class GestureSensorImpl_Factory implements Factory<GestureSensorImpl> {
    private final Provider<Context> contextProvider;
    private final Provider<GestureConfiguration> gestureConfigurationProvider;

    public GestureSensorImpl_Factory(Provider<Context> provider, Provider<GestureConfiguration> provider2) {
        this.contextProvider = provider;
        this.gestureConfigurationProvider = provider2;
    }

    public GestureSensorImpl get() {
        return provideInstance(this.contextProvider, this.gestureConfigurationProvider);
    }

    public static GestureSensorImpl provideInstance(Provider<Context> provider, Provider<GestureConfiguration> provider2) {
        return new GestureSensorImpl((Context) provider.get(), (GestureConfiguration) provider2.get());
    }

    public static GestureSensorImpl_Factory create(Provider<Context> provider, Provider<GestureConfiguration> provider2) {
        return new GestureSensorImpl_Factory(provider, provider2);
    }
}
