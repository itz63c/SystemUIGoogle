package com.google.android.systemui.columbus;

import com.google.android.systemui.columbus.actions.Action;
import com.google.android.systemui.columbus.actions.SetupWizardAction;
import com.google.android.systemui.columbus.actions.UnpinNotifications;
import com.google.android.systemui.columbus.actions.UserSelectedAction;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import java.util.List;
import javax.inject.Provider;

public final class ColumbusModule_ProvideColumbusActionsFactory implements Factory<List<Action>> {
    private final Provider<List<Action>> fullscreenActionsProvider;
    private final Provider<SetupWizardAction> setupWizardActionProvider;
    private final Provider<UnpinNotifications> unpinNotificationsProvider;
    private final Provider<UserSelectedAction> userSelectedActionProvider;

    public ColumbusModule_ProvideColumbusActionsFactory(Provider<List<Action>> provider, Provider<UnpinNotifications> provider2, Provider<SetupWizardAction> provider3, Provider<UserSelectedAction> provider4) {
        this.fullscreenActionsProvider = provider;
        this.unpinNotificationsProvider = provider2;
        this.setupWizardActionProvider = provider3;
        this.userSelectedActionProvider = provider4;
    }

    public List<Action> get() {
        return provideInstance(this.fullscreenActionsProvider, this.unpinNotificationsProvider, this.setupWizardActionProvider, this.userSelectedActionProvider);
    }

    public static List<Action> provideInstance(Provider<List<Action>> provider, Provider<UnpinNotifications> provider2, Provider<SetupWizardAction> provider3, Provider<UserSelectedAction> provider4) {
        return proxyProvideColumbusActions((List) provider.get(), (UnpinNotifications) provider2.get(), (SetupWizardAction) provider3.get(), (UserSelectedAction) provider4.get());
    }

    public static ColumbusModule_ProvideColumbusActionsFactory create(Provider<List<Action>> provider, Provider<UnpinNotifications> provider2, Provider<SetupWizardAction> provider3, Provider<UserSelectedAction> provider4) {
        return new ColumbusModule_ProvideColumbusActionsFactory(provider, provider2, provider3, provider4);
    }

    public static List<Action> proxyProvideColumbusActions(List<Action> list, UnpinNotifications unpinNotifications, SetupWizardAction setupWizardAction, UserSelectedAction userSelectedAction) {
        List<Action> provideColumbusActions = ColumbusModule.provideColumbusActions(list, unpinNotifications, setupWizardAction, userSelectedAction);
        Preconditions.checkNotNull(provideColumbusActions, "Cannot return null from a non-@Nullable @Provides method");
        return provideColumbusActions;
    }
}
