package com.android.systemui.statusbar.notification.collection;

import com.android.systemui.dump.DumpManager;
import com.android.systemui.statusbar.notification.collection.listbuilder.ShadeListBuilderLogger;
import com.android.systemui.util.time.SystemClock;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class ShadeListBuilder_Factory implements Factory<ShadeListBuilder> {
    private final Provider<DumpManager> dumpManagerProvider;
    private final Provider<ShadeListBuilderLogger> loggerProvider;
    private final Provider<SystemClock> systemClockProvider;

    public ShadeListBuilder_Factory(Provider<SystemClock> provider, Provider<ShadeListBuilderLogger> provider2, Provider<DumpManager> provider3) {
        this.systemClockProvider = provider;
        this.loggerProvider = provider2;
        this.dumpManagerProvider = provider3;
    }

    public ShadeListBuilder get() {
        return provideInstance(this.systemClockProvider, this.loggerProvider, this.dumpManagerProvider);
    }

    public static ShadeListBuilder provideInstance(Provider<SystemClock> provider, Provider<ShadeListBuilderLogger> provider2, Provider<DumpManager> provider3) {
        return new ShadeListBuilder((SystemClock) provider.get(), (ShadeListBuilderLogger) provider2.get(), (DumpManager) provider3.get());
    }

    public static ShadeListBuilder_Factory create(Provider<SystemClock> provider, Provider<ShadeListBuilderLogger> provider2, Provider<DumpManager> provider3) {
        return new ShadeListBuilder_Factory(provider, provider2, provider3);
    }
}
