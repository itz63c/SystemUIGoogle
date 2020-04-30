package com.android.systemui;

import android.os.Handler;
import com.android.systemui.dump.DumpManager;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class SystemUIService_Factory implements Factory<SystemUIService> {
    private final Provider<DumpManager> dumpManagerProvider;
    private final Provider<Handler> mainHandlerProvider;

    public SystemUIService_Factory(Provider<Handler> provider, Provider<DumpManager> provider2) {
        this.mainHandlerProvider = provider;
        this.dumpManagerProvider = provider2;
    }

    public SystemUIService get() {
        return provideInstance(this.mainHandlerProvider, this.dumpManagerProvider);
    }

    public static SystemUIService provideInstance(Provider<Handler> provider, Provider<DumpManager> provider2) {
        return new SystemUIService((Handler) provider.get(), (DumpManager) provider2.get());
    }

    public static SystemUIService_Factory create(Provider<Handler> provider, Provider<DumpManager> provider2) {
        return new SystemUIService_Factory(provider, provider2);
    }
}
