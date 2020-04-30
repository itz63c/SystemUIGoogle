package com.google.android.systemui.assist.uihints;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.android.systemui.statusbar.policy.ConfigurationController;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class IconController_Factory implements Factory<IconController> {
    private final Provider<ConfigurationController> configurationControllerProvider;
    private final Provider<LayoutInflater> inflaterProvider;
    private final Provider<ViewGroup> parentProvider;

    public IconController_Factory(Provider<LayoutInflater> provider, Provider<ViewGroup> provider2, Provider<ConfigurationController> provider3) {
        this.inflaterProvider = provider;
        this.parentProvider = provider2;
        this.configurationControllerProvider = provider3;
    }

    public IconController get() {
        return provideInstance(this.inflaterProvider, this.parentProvider, this.configurationControllerProvider);
    }

    public static IconController provideInstance(Provider<LayoutInflater> provider, Provider<ViewGroup> provider2, Provider<ConfigurationController> provider3) {
        return new IconController((LayoutInflater) provider.get(), (ViewGroup) provider2.get(), (ConfigurationController) provider3.get());
    }

    public static IconController_Factory create(Provider<LayoutInflater> provider, Provider<ViewGroup> provider2, Provider<ConfigurationController> provider3) {
        return new IconController_Factory(provider, provider2, provider3);
    }
}
