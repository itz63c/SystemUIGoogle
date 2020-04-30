package com.google.android.systemui.columbus;

import com.google.android.systemui.columbus.gates.CameraVisibility;
import com.google.android.systemui.columbus.gates.ChargingState;
import com.google.android.systemui.columbus.gates.FlagEnabled;
import com.google.android.systemui.columbus.gates.Gate;
import com.google.android.systemui.columbus.gates.KeyguardDeferredSetup;
import com.google.android.systemui.columbus.gates.KeyguardProximity;
import com.google.android.systemui.columbus.gates.NavigationBarVisibility;
import com.google.android.systemui.columbus.gates.PowerSaveState;
import com.google.android.systemui.columbus.gates.SetupWizard;
import com.google.android.systemui.columbus.gates.SystemKeyPress;
import com.google.android.systemui.columbus.gates.TelephonyActivity;
import com.google.android.systemui.columbus.gates.UsbState;
import com.google.android.systemui.columbus.gates.VrMode;
import com.google.android.systemui.columbus.gates.WakeMode;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import java.util.Set;
import javax.inject.Provider;

public final class ColumbusModule_ProvideColumbusGatesFactory implements Factory<Set<Gate>> {
    private final Provider<CameraVisibility> cameraVisibilityProvider;
    private final Provider<ChargingState> chargingStateProvider;
    private final Provider<FlagEnabled> flagEnabledProvider;
    private final Provider<KeyguardDeferredSetup> keyguardDeferredSetupProvider;
    private final Provider<KeyguardProximity> keyguardProximityProvider;
    private final Provider<NavigationBarVisibility> navigationBarVisibilityProvider;
    private final Provider<PowerSaveState> powerSaveStateProvider;
    private final Provider<SetupWizard> setupWizardProvider;
    private final Provider<SystemKeyPress> systemKeyPressProvider;
    private final Provider<TelephonyActivity> telephonyActivityProvider;
    private final Provider<UsbState> usbStateProvider;
    private final Provider<VrMode> vrModeProvider;
    private final Provider<WakeMode> wakeModeProvider;

    public ColumbusModule_ProvideColumbusGatesFactory(Provider<FlagEnabled> provider, Provider<WakeMode> provider2, Provider<ChargingState> provider3, Provider<UsbState> provider4, Provider<KeyguardProximity> provider5, Provider<SetupWizard> provider6, Provider<NavigationBarVisibility> provider7, Provider<SystemKeyPress> provider8, Provider<TelephonyActivity> provider9, Provider<VrMode> provider10, Provider<KeyguardDeferredSetup> provider11, Provider<CameraVisibility> provider12, Provider<PowerSaveState> provider13) {
        this.flagEnabledProvider = provider;
        this.wakeModeProvider = provider2;
        this.chargingStateProvider = provider3;
        this.usbStateProvider = provider4;
        this.keyguardProximityProvider = provider5;
        this.setupWizardProvider = provider6;
        this.navigationBarVisibilityProvider = provider7;
        this.systemKeyPressProvider = provider8;
        this.telephonyActivityProvider = provider9;
        this.vrModeProvider = provider10;
        this.keyguardDeferredSetupProvider = provider11;
        this.cameraVisibilityProvider = provider12;
        this.powerSaveStateProvider = provider13;
    }

    public Set<Gate> get() {
        return provideInstance(this.flagEnabledProvider, this.wakeModeProvider, this.chargingStateProvider, this.usbStateProvider, this.keyguardProximityProvider, this.setupWizardProvider, this.navigationBarVisibilityProvider, this.systemKeyPressProvider, this.telephonyActivityProvider, this.vrModeProvider, this.keyguardDeferredSetupProvider, this.cameraVisibilityProvider, this.powerSaveStateProvider);
    }

    public static Set<Gate> provideInstance(Provider<FlagEnabled> provider, Provider<WakeMode> provider2, Provider<ChargingState> provider3, Provider<UsbState> provider4, Provider<KeyguardProximity> provider5, Provider<SetupWizard> provider6, Provider<NavigationBarVisibility> provider7, Provider<SystemKeyPress> provider8, Provider<TelephonyActivity> provider9, Provider<VrMode> provider10, Provider<KeyguardDeferredSetup> provider11, Provider<CameraVisibility> provider12, Provider<PowerSaveState> provider13) {
        return proxyProvideColumbusGates((FlagEnabled) provider.get(), (WakeMode) provider2.get(), (ChargingState) provider3.get(), (UsbState) provider4.get(), (KeyguardProximity) provider5.get(), (SetupWizard) provider6.get(), (NavigationBarVisibility) provider7.get(), (SystemKeyPress) provider8.get(), (TelephonyActivity) provider9.get(), (VrMode) provider10.get(), (KeyguardDeferredSetup) provider11.get(), (CameraVisibility) provider12.get(), (PowerSaveState) provider13.get());
    }

    public static ColumbusModule_ProvideColumbusGatesFactory create(Provider<FlagEnabled> provider, Provider<WakeMode> provider2, Provider<ChargingState> provider3, Provider<UsbState> provider4, Provider<KeyguardProximity> provider5, Provider<SetupWizard> provider6, Provider<NavigationBarVisibility> provider7, Provider<SystemKeyPress> provider8, Provider<TelephonyActivity> provider9, Provider<VrMode> provider10, Provider<KeyguardDeferredSetup> provider11, Provider<CameraVisibility> provider12, Provider<PowerSaveState> provider13) {
        ColumbusModule_ProvideColumbusGatesFactory columbusModule_ProvideColumbusGatesFactory = new ColumbusModule_ProvideColumbusGatesFactory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11, provider12, provider13);
        return columbusModule_ProvideColumbusGatesFactory;
    }

    public static Set<Gate> proxyProvideColumbusGates(FlagEnabled flagEnabled, WakeMode wakeMode, ChargingState chargingState, UsbState usbState, KeyguardProximity keyguardProximity, SetupWizard setupWizard, NavigationBarVisibility navigationBarVisibility, SystemKeyPress systemKeyPress, TelephonyActivity telephonyActivity, VrMode vrMode, KeyguardDeferredSetup keyguardDeferredSetup, CameraVisibility cameraVisibility, PowerSaveState powerSaveState) {
        Set<Gate> provideColumbusGates = ColumbusModule.provideColumbusGates(flagEnabled, wakeMode, chargingState, usbState, keyguardProximity, setupWizard, navigationBarVisibility, systemKeyPress, telephonyActivity, vrMode, keyguardDeferredSetup, cameraVisibility, powerSaveState);
        Preconditions.checkNotNull(provideColumbusGates, "Cannot return null from a non-@Nullable @Provides method");
        return provideColumbusGates;
    }
}
