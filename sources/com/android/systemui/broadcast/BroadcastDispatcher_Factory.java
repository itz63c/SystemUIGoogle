package com.android.systemui.broadcast;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.android.systemui.dump.DumpManager;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class BroadcastDispatcher_Factory implements Factory<BroadcastDispatcher> {
    private final Provider<Looper> bgLooperProvider;
    private final Provider<Context> contextProvider;
    private final Provider<DumpManager> dumpManagerProvider;
    private final Provider<Handler> mainHandlerProvider;

    public BroadcastDispatcher_Factory(Provider<Context> provider, Provider<Handler> provider2, Provider<Looper> provider3, Provider<DumpManager> provider4) {
        this.contextProvider = provider;
        this.mainHandlerProvider = provider2;
        this.bgLooperProvider = provider3;
        this.dumpManagerProvider = provider4;
    }

    public BroadcastDispatcher get() {
        return provideInstance(this.contextProvider, this.mainHandlerProvider, this.bgLooperProvider, this.dumpManagerProvider);
    }

    public static BroadcastDispatcher provideInstance(Provider<Context> provider, Provider<Handler> provider2, Provider<Looper> provider3, Provider<DumpManager> provider4) {
        return new BroadcastDispatcher((Context) provider.get(), (Handler) provider2.get(), (Looper) provider3.get(), (DumpManager) provider4.get());
    }

    public static BroadcastDispatcher_Factory create(Provider<Context> provider, Provider<Handler> provider2, Provider<Looper> provider3, Provider<DumpManager> provider4) {
        return new BroadcastDispatcher_Factory(provider, provider2, provider3, provider4);
    }
}
