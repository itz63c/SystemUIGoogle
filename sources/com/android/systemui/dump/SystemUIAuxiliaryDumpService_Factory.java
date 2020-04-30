package com.android.systemui.dump;

import dagger.internal.Factory;
import javax.inject.Provider;

public final class SystemUIAuxiliaryDumpService_Factory implements Factory<SystemUIAuxiliaryDumpService> {
    private final Provider<DumpManager> dumpManagerProvider;

    public SystemUIAuxiliaryDumpService_Factory(Provider<DumpManager> provider) {
        this.dumpManagerProvider = provider;
    }

    public SystemUIAuxiliaryDumpService get() {
        return provideInstance(this.dumpManagerProvider);
    }

    public static SystemUIAuxiliaryDumpService provideInstance(Provider<DumpManager> provider) {
        return new SystemUIAuxiliaryDumpService((DumpManager) provider.get());
    }

    public static SystemUIAuxiliaryDumpService_Factory create(Provider<DumpManager> provider) {
        return new SystemUIAuxiliaryDumpService_Factory(provider);
    }
}
