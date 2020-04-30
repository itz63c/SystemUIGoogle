package com.google.android.systemui;

import android.content.Context;
import com.android.systemui.statusbar.phone.StatusBar;
import com.google.android.systemui.elmyra.ServiceConfigurationGoogle;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class GoogleServices_Factory implements Factory<GoogleServices> {
    private final Provider<Context> contextProvider;
    private final Provider<ServiceConfigurationGoogle> serviceConfigurationGoogleProvider;
    private final Provider<StatusBar> statusBarProvider;

    public GoogleServices_Factory(Provider<Context> provider, Provider<ServiceConfigurationGoogle> provider2, Provider<StatusBar> provider3) {
        this.contextProvider = provider;
        this.serviceConfigurationGoogleProvider = provider2;
        this.statusBarProvider = provider3;
    }

    public GoogleServices get() {
        return provideInstance(this.contextProvider, this.serviceConfigurationGoogleProvider, this.statusBarProvider);
    }

    public static GoogleServices provideInstance(Provider<Context> provider, Provider<ServiceConfigurationGoogle> provider2, Provider<StatusBar> provider3) {
        return new GoogleServices((Context) provider.get(), (ServiceConfigurationGoogle) provider2.get(), (StatusBar) provider3.get());
    }

    public static GoogleServices_Factory create(Provider<Context> provider, Provider<ServiceConfigurationGoogle> provider2, Provider<StatusBar> provider3) {
        return new GoogleServices_Factory(provider, provider2, provider3);
    }
}
