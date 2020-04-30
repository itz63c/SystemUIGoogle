package com.google.android.systemui.columbus.actions;

import android.content.Context;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.google.android.systemui.columbus.ColumbusContentObserver;
import dagger.internal.Factory;
import java.util.Optional;
import javax.inject.Provider;

public final class UnpinNotifications_Factory implements Factory<UnpinNotifications> {
    private final Provider<Context> contextProvider;
    private final Provider<Optional<HeadsUpManager>> headsUpManagerOptionalProvider;
    private final Provider<ColumbusContentObserver.Factory> settingsObserverFactoryProvider;

    public UnpinNotifications_Factory(Provider<Optional<HeadsUpManager>> provider, Provider<Context> provider2, Provider<ColumbusContentObserver.Factory> provider3) {
        this.headsUpManagerOptionalProvider = provider;
        this.contextProvider = provider2;
        this.settingsObserverFactoryProvider = provider3;
    }

    public UnpinNotifications get() {
        return provideInstance(this.headsUpManagerOptionalProvider, this.contextProvider, this.settingsObserverFactoryProvider);
    }

    public static UnpinNotifications provideInstance(Provider<Optional<HeadsUpManager>> provider, Provider<Context> provider2, Provider<ColumbusContentObserver.Factory> provider3) {
        return new UnpinNotifications((Optional) provider.get(), (Context) provider2.get(), (ColumbusContentObserver.Factory) provider3.get());
    }

    public static UnpinNotifications_Factory create(Provider<Optional<HeadsUpManager>> provider, Provider<Context> provider2, Provider<ColumbusContentObserver.Factory> provider3) {
        return new UnpinNotifications_Factory(provider, provider2, provider3);
    }
}
