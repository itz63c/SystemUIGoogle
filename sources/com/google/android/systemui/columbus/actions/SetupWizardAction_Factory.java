package com.google.android.systemui.columbus.actions;

import android.content.Context;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.statusbar.phone.StatusBar;
import com.google.android.systemui.columbus.gates.KeyguardDeferredSetup;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class SetupWizardAction_Factory implements Factory<SetupWizardAction> {
    private final Provider<Context> contextProvider;
    private final Provider<KeyguardDeferredSetup> keyguardDeferredSetupGateProvider;
    private final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider;
    private final Provider<SettingsAction> settingsActionProvider;
    private final Provider<StatusBar> statusBarProvider;
    private final Provider<UserSelectedAction> userSelectedActionProvider;

    public SetupWizardAction_Factory(Provider<Context> provider, Provider<SettingsAction> provider2, Provider<UserSelectedAction> provider3, Provider<KeyguardDeferredSetup> provider4, Provider<StatusBar> provider5, Provider<KeyguardUpdateMonitor> provider6) {
        this.contextProvider = provider;
        this.settingsActionProvider = provider2;
        this.userSelectedActionProvider = provider3;
        this.keyguardDeferredSetupGateProvider = provider4;
        this.statusBarProvider = provider5;
        this.keyguardUpdateMonitorProvider = provider6;
    }

    public SetupWizardAction get() {
        return provideInstance(this.contextProvider, this.settingsActionProvider, this.userSelectedActionProvider, this.keyguardDeferredSetupGateProvider, this.statusBarProvider, this.keyguardUpdateMonitorProvider);
    }

    public static SetupWizardAction provideInstance(Provider<Context> provider, Provider<SettingsAction> provider2, Provider<UserSelectedAction> provider3, Provider<KeyguardDeferredSetup> provider4, Provider<StatusBar> provider5, Provider<KeyguardUpdateMonitor> provider6) {
        SetupWizardAction setupWizardAction = new SetupWizardAction((Context) provider.get(), (SettingsAction) provider2.get(), (UserSelectedAction) provider3.get(), (KeyguardDeferredSetup) provider4.get(), (StatusBar) provider5.get(), (KeyguardUpdateMonitor) provider6.get());
        return setupWizardAction;
    }

    public static SetupWizardAction_Factory create(Provider<Context> provider, Provider<SettingsAction> provider2, Provider<UserSelectedAction> provider3, Provider<KeyguardDeferredSetup> provider4, Provider<StatusBar> provider5, Provider<KeyguardUpdateMonitor> provider6) {
        SetupWizardAction_Factory setupWizardAction_Factory = new SetupWizardAction_Factory(provider, provider2, provider3, provider4, provider5, provider6);
        return setupWizardAction_Factory;
    }
}
