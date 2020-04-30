package com.google.android.systemui.dagger;

import android.content.Context;
import com.google.android.systemui.batteryshare.ReverseWirelessCharger;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import java.util.Optional;
import javax.inject.Provider;

public final class SystemUIGoogleModule_ProvideReverseWirelessChargerFactory implements Factory<Optional<ReverseWirelessCharger>> {
    private final Provider<Context> contextProvider;

    public SystemUIGoogleModule_ProvideReverseWirelessChargerFactory(Provider<Context> provider) {
        this.contextProvider = provider;
    }

    public Optional<ReverseWirelessCharger> get() {
        return provideInstance(this.contextProvider);
    }

    public static Optional<ReverseWirelessCharger> provideInstance(Provider<Context> provider) {
        return proxyProvideReverseWirelessCharger((Context) provider.get());
    }

    public static SystemUIGoogleModule_ProvideReverseWirelessChargerFactory create(Provider<Context> provider) {
        return new SystemUIGoogleModule_ProvideReverseWirelessChargerFactory(provider);
    }

    public static Optional<ReverseWirelessCharger> proxyProvideReverseWirelessCharger(Context context) {
        Optional<ReverseWirelessCharger> provideReverseWirelessCharger = SystemUIGoogleModule.provideReverseWirelessCharger(context);
        Preconditions.checkNotNull(provideReverseWirelessCharger, "Cannot return null from a non-@Nullable @Provides method");
        return provideReverseWirelessCharger;
    }
}
