package com.android.systemui.dump;

import android.content.Context;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class DumpManager_Factory implements Factory<DumpManager> {
    private final Provider<Context> contextProvider;

    public DumpManager_Factory(Provider<Context> provider) {
        this.contextProvider = provider;
    }

    public DumpManager get() {
        return provideInstance(this.contextProvider);
    }

    public static DumpManager provideInstance(Provider<Context> provider) {
        return new DumpManager((Context) provider.get());
    }

    public static DumpManager_Factory create(Provider<Context> provider) {
        return new DumpManager_Factory(provider);
    }
}
