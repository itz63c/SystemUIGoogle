package com.google.android.systemui.elmyra.actions;

import android.content.Context;
import com.android.systemui.statusbar.phone.StatusBar;
import com.google.android.systemui.elmyra.actions.LaunchOpa.Builder;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class LaunchOpa_Builder_Factory implements Factory<Builder> {
    private final Provider<Context> contextProvider;
    private final Provider<StatusBar> statusBarProvider;

    public LaunchOpa_Builder_Factory(Provider<Context> provider, Provider<StatusBar> provider2) {
        this.contextProvider = provider;
        this.statusBarProvider = provider2;
    }

    public Builder get() {
        return provideInstance(this.contextProvider, this.statusBarProvider);
    }

    public static Builder provideInstance(Provider<Context> provider, Provider<StatusBar> provider2) {
        return new Builder((Context) provider.get(), (StatusBar) provider2.get());
    }

    public static LaunchOpa_Builder_Factory create(Provider<Context> provider, Provider<StatusBar> provider2) {
        return new LaunchOpa_Builder_Factory(provider, provider2);
    }
}
