package com.android.systemui.statusbar;

import android.content.res.Resources;
import com.android.systemui.dump.DumpManager;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class BlurUtils_Factory implements Factory<BlurUtils> {
    private final Provider<DumpManager> dumpManagerProvider;
    private final Provider<Resources> resourcesProvider;

    public BlurUtils_Factory(Provider<Resources> provider, Provider<DumpManager> provider2) {
        this.resourcesProvider = provider;
        this.dumpManagerProvider = provider2;
    }

    public BlurUtils get() {
        return provideInstance(this.resourcesProvider, this.dumpManagerProvider);
    }

    public static BlurUtils provideInstance(Provider<Resources> provider, Provider<DumpManager> provider2) {
        return new BlurUtils((Resources) provider.get(), (DumpManager) provider2.get());
    }

    public static BlurUtils_Factory create(Provider<Resources> provider, Provider<DumpManager> provider2) {
        return new BlurUtils_Factory(provider, provider2);
    }
}
