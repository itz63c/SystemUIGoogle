package com.google.android.systemui.elmyra;

import android.content.Context;
import com.google.android.systemui.elmyra.actions.CameraAction.Builder;
import com.google.android.systemui.elmyra.actions.LaunchOpa;
import com.google.android.systemui.elmyra.actions.SettingsAction;
import com.google.android.systemui.elmyra.actions.SetupWizardAction;
import com.google.android.systemui.elmyra.actions.UnpinNotifications;
import com.google.android.systemui.elmyra.feedback.AssistInvocationEffect;
import com.google.android.systemui.elmyra.feedback.SquishyNavigationButtons;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class ServiceConfigurationGoogle_Factory implements Factory<ServiceConfigurationGoogle> {
    private final Provider<AssistInvocationEffect> assistInvocationEffectProvider;
    private final Provider<Builder> cameraActionBuilderProvider;
    private final Provider<Context> contextProvider;
    private final Provider<LaunchOpa.Builder> launchOpaBuilderProvider;
    private final Provider<SettingsAction.Builder> settingsActionBuilderProvider;
    private final Provider<SetupWizardAction.Builder> setupWizardActionBuilderProvider;
    private final Provider<SquishyNavigationButtons> squishyNavigationButtonsProvider;
    private final Provider<UnpinNotifications> unpinNotificationsProvider;

    public ServiceConfigurationGoogle_Factory(Provider<Context> provider, Provider<AssistInvocationEffect> provider2, Provider<LaunchOpa.Builder> provider3, Provider<SettingsAction.Builder> provider4, Provider<Builder> provider5, Provider<SetupWizardAction.Builder> provider6, Provider<SquishyNavigationButtons> provider7, Provider<UnpinNotifications> provider8) {
        this.contextProvider = provider;
        this.assistInvocationEffectProvider = provider2;
        this.launchOpaBuilderProvider = provider3;
        this.settingsActionBuilderProvider = provider4;
        this.cameraActionBuilderProvider = provider5;
        this.setupWizardActionBuilderProvider = provider6;
        this.squishyNavigationButtonsProvider = provider7;
        this.unpinNotificationsProvider = provider8;
    }

    public ServiceConfigurationGoogle get() {
        return provideInstance(this.contextProvider, this.assistInvocationEffectProvider, this.launchOpaBuilderProvider, this.settingsActionBuilderProvider, this.cameraActionBuilderProvider, this.setupWizardActionBuilderProvider, this.squishyNavigationButtonsProvider, this.unpinNotificationsProvider);
    }

    public static ServiceConfigurationGoogle provideInstance(Provider<Context> provider, Provider<AssistInvocationEffect> provider2, Provider<LaunchOpa.Builder> provider3, Provider<SettingsAction.Builder> provider4, Provider<Builder> provider5, Provider<SetupWizardAction.Builder> provider6, Provider<SquishyNavigationButtons> provider7, Provider<UnpinNotifications> provider8) {
        ServiceConfigurationGoogle serviceConfigurationGoogle = new ServiceConfigurationGoogle((Context) provider.get(), (AssistInvocationEffect) provider2.get(), (LaunchOpa.Builder) provider3.get(), (SettingsAction.Builder) provider4.get(), (Builder) provider5.get(), (SetupWizardAction.Builder) provider6.get(), (SquishyNavigationButtons) provider7.get(), (UnpinNotifications) provider8.get());
        return serviceConfigurationGoogle;
    }

    public static ServiceConfigurationGoogle_Factory create(Provider<Context> provider, Provider<AssistInvocationEffect> provider2, Provider<LaunchOpa.Builder> provider3, Provider<SettingsAction.Builder> provider4, Provider<Builder> provider5, Provider<SetupWizardAction.Builder> provider6, Provider<SquishyNavigationButtons> provider7, Provider<UnpinNotifications> provider8) {
        ServiceConfigurationGoogle_Factory serviceConfigurationGoogle_Factory = new ServiceConfigurationGoogle_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8);
        return serviceConfigurationGoogle_Factory;
    }
}
