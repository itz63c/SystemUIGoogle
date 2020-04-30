package com.google.android.systemui.elmyra.actions;

import android.content.Context;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import dagger.internal.Factory;
import java.util.Optional;
import javax.inject.Provider;

public final class UnpinNotifications_Factory implements Factory<UnpinNotifications> {
    private final Provider<Context> contextProvider;
    private final Provider<Optional<HeadsUpManager>> headsUpManagerOptionalProvider;

    public UnpinNotifications_Factory(Provider<Context> provider, Provider<Optional<HeadsUpManager>> provider2) {
        this.contextProvider = provider;
        this.headsUpManagerOptionalProvider = provider2;
    }

    public UnpinNotifications get() {
        return provideInstance(this.contextProvider, this.headsUpManagerOptionalProvider);
    }

    public static UnpinNotifications provideInstance(Provider<Context> provider, Provider<Optional<HeadsUpManager>> provider2) {
        return new UnpinNotifications((Context) provider.get(), (Optional) provider2.get());
    }

    public static UnpinNotifications_Factory create(Provider<Context> provider, Provider<Optional<HeadsUpManager>> provider2) {
        return new UnpinNotifications_Factory(provider, provider2);
    }
}
