package com.google.android.systemui.columbus.actions;

import android.content.Context;
import com.android.systemui.recents.Recents;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class LaunchOverview_Factory implements Factory<LaunchOverview> {
    private final Provider<Context> contextProvider;
    private final Provider<Recents> recentsProvider;

    public LaunchOverview_Factory(Provider<Context> provider, Provider<Recents> provider2) {
        this.contextProvider = provider;
        this.recentsProvider = provider2;
    }

    public LaunchOverview get() {
        return provideInstance(this.contextProvider, this.recentsProvider);
    }

    public static LaunchOverview provideInstance(Provider<Context> provider, Provider<Recents> provider2) {
        return new LaunchOverview((Context) provider.get(), (Recents) provider2.get());
    }

    public static LaunchOverview_Factory create(Provider<Context> provider, Provider<Recents> provider2) {
        return new LaunchOverview_Factory(provider, provider2);
    }
}
