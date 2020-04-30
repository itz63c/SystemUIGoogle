package com.android.keyguard;

import android.content.Context;
import android.os.Looper;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.dump.DumpManager;
import dagger.internal.Factory;
import java.util.concurrent.Executor;
import javax.inject.Provider;

public final class KeyguardUpdateMonitor_Factory implements Factory<KeyguardUpdateMonitor> {
    private final Provider<Executor> backgroundExecutorProvider;
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<Context> contextProvider;
    private final Provider<DumpManager> dumpManagerProvider;
    private final Provider<Looper> mainLooperProvider;

    public KeyguardUpdateMonitor_Factory(Provider<Context> provider, Provider<Looper> provider2, Provider<BroadcastDispatcher> provider3, Provider<DumpManager> provider4, Provider<Executor> provider5) {
        this.contextProvider = provider;
        this.mainLooperProvider = provider2;
        this.broadcastDispatcherProvider = provider3;
        this.dumpManagerProvider = provider4;
        this.backgroundExecutorProvider = provider5;
    }

    public KeyguardUpdateMonitor get() {
        return provideInstance(this.contextProvider, this.mainLooperProvider, this.broadcastDispatcherProvider, this.dumpManagerProvider, this.backgroundExecutorProvider);
    }

    public static KeyguardUpdateMonitor provideInstance(Provider<Context> provider, Provider<Looper> provider2, Provider<BroadcastDispatcher> provider3, Provider<DumpManager> provider4, Provider<Executor> provider5) {
        KeyguardUpdateMonitor keyguardUpdateMonitor = new KeyguardUpdateMonitor((Context) provider.get(), (Looper) provider2.get(), (BroadcastDispatcher) provider3.get(), (DumpManager) provider4.get(), (Executor) provider5.get());
        return keyguardUpdateMonitor;
    }

    public static KeyguardUpdateMonitor_Factory create(Provider<Context> provider, Provider<Looper> provider2, Provider<BroadcastDispatcher> provider3, Provider<DumpManager> provider4, Provider<Executor> provider5) {
        KeyguardUpdateMonitor_Factory keyguardUpdateMonitor_Factory = new KeyguardUpdateMonitor_Factory(provider, provider2, provider3, provider4, provider5);
        return keyguardUpdateMonitor_Factory;
    }
}
