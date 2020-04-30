package com.google.android.systemui.assist.uihints;

import android.view.ViewGroup;
import com.android.systemui.statusbar.policy.ConfigurationController;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class TranscriptionController_Factory implements Factory<TranscriptionController> {
    private final Provider<ConfigurationController> configurationControllerProvider;
    private final Provider<TouchInsideHandler> defaultOnTapProvider;
    private final Provider<FlingVelocityWrapper> flingVelocityProvider;
    private final Provider<ViewGroup> parentProvider;

    public TranscriptionController_Factory(Provider<ViewGroup> provider, Provider<TouchInsideHandler> provider2, Provider<FlingVelocityWrapper> provider3, Provider<ConfigurationController> provider4) {
        this.parentProvider = provider;
        this.defaultOnTapProvider = provider2;
        this.flingVelocityProvider = provider3;
        this.configurationControllerProvider = provider4;
    }

    public TranscriptionController get() {
        return provideInstance(this.parentProvider, this.defaultOnTapProvider, this.flingVelocityProvider, this.configurationControllerProvider);
    }

    public static TranscriptionController provideInstance(Provider<ViewGroup> provider, Provider<TouchInsideHandler> provider2, Provider<FlingVelocityWrapper> provider3, Provider<ConfigurationController> provider4) {
        return new TranscriptionController((ViewGroup) provider.get(), (TouchInsideHandler) provider2.get(), (FlingVelocityWrapper) provider3.get(), (ConfigurationController) provider4.get());
    }

    public static TranscriptionController_Factory create(Provider<ViewGroup> provider, Provider<TouchInsideHandler> provider2, Provider<FlingVelocityWrapper> provider3, Provider<ConfigurationController> provider4) {
        return new TranscriptionController_Factory(provider, provider2, provider3, provider4);
    }
}
