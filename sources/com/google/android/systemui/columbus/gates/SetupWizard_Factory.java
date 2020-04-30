package com.google.android.systemui.columbus.gates;

import android.content.Context;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.google.android.systemui.columbus.actions.Action;
import dagger.internal.DoubleCheck;
import dagger.internal.Factory;
import java.util.Set;
import javax.inject.Provider;

public final class SetupWizard_Factory implements Factory<SetupWizard> {
    private final Provider<Context> contextProvider;
    private final Provider<DeviceProvisionedController> provisionedControllerProvider;
    private final Provider<Set<Action>> setupWizardExceptionsProvider;

    public SetupWizard_Factory(Provider<Context> provider, Provider<Set<Action>> provider2, Provider<DeviceProvisionedController> provider3) {
        this.contextProvider = provider;
        this.setupWizardExceptionsProvider = provider2;
        this.provisionedControllerProvider = provider3;
    }

    public SetupWizard get() {
        return provideInstance(this.contextProvider, this.setupWizardExceptionsProvider, this.provisionedControllerProvider);
    }

    public static SetupWizard provideInstance(Provider<Context> provider, Provider<Set<Action>> provider2, Provider<DeviceProvisionedController> provider3) {
        return new SetupWizard((Context) provider.get(), (Set) provider2.get(), DoubleCheck.lazy(provider3));
    }

    public static SetupWizard_Factory create(Provider<Context> provider, Provider<Set<Action>> provider2, Provider<DeviceProvisionedController> provider3) {
        return new SetupWizard_Factory(provider, provider2, provider3);
    }
}
