package com.google.android.systemui.columbus.actions;

import android.content.Context;
import com.android.systemui.statusbar.phone.StatusBar;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class SettingsAction_Factory implements Factory<SettingsAction> {
    private final Provider<Context> contextProvider;
    private final Provider<StatusBar> statusBarProvider;
    private final Provider<UserSelectedAction> userSelectedActionProvider;

    public SettingsAction_Factory(Provider<Context> provider, Provider<UserSelectedAction> provider2, Provider<StatusBar> provider3) {
        this.contextProvider = provider;
        this.userSelectedActionProvider = provider2;
        this.statusBarProvider = provider3;
    }

    public SettingsAction get() {
        return provideInstance(this.contextProvider, this.userSelectedActionProvider, this.statusBarProvider);
    }

    public static SettingsAction provideInstance(Provider<Context> provider, Provider<UserSelectedAction> provider2, Provider<StatusBar> provider3) {
        return new SettingsAction((Context) provider.get(), (UserSelectedAction) provider2.get(), (StatusBar) provider3.get());
    }

    public static SettingsAction_Factory create(Provider<Context> provider, Provider<UserSelectedAction> provider2, Provider<StatusBar> provider3) {
        return new SettingsAction_Factory(provider, provider2, provider3);
    }
}
