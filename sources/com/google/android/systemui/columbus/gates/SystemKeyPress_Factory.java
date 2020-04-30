package com.google.android.systemui.columbus.gates;

import android.content.Context;
import android.os.Handler;
import com.android.systemui.statusbar.CommandQueue;
import dagger.internal.Factory;
import java.util.Set;
import javax.inject.Provider;

public final class SystemKeyPress_Factory implements Factory<SystemKeyPress> {
    private final Provider<Set<Integer>> blockingKeysProvider;
    private final Provider<CommandQueue> commandQueueProvider;
    private final Provider<Context> contextProvider;
    private final Provider<Long> gateDurationProvider;
    private final Provider<Handler> handlerProvider;

    public SystemKeyPress_Factory(Provider<Context> provider, Provider<Handler> provider2, Provider<CommandQueue> provider3, Provider<Long> provider4, Provider<Set<Integer>> provider5) {
        this.contextProvider = provider;
        this.handlerProvider = provider2;
        this.commandQueueProvider = provider3;
        this.gateDurationProvider = provider4;
        this.blockingKeysProvider = provider5;
    }

    public SystemKeyPress get() {
        return provideInstance(this.contextProvider, this.handlerProvider, this.commandQueueProvider, this.gateDurationProvider, this.blockingKeysProvider);
    }

    public static SystemKeyPress provideInstance(Provider<Context> provider, Provider<Handler> provider2, Provider<CommandQueue> provider3, Provider<Long> provider4, Provider<Set<Integer>> provider5) {
        SystemKeyPress systemKeyPress = new SystemKeyPress((Context) provider.get(), (Handler) provider2.get(), (CommandQueue) provider3.get(), ((Long) provider4.get()).longValue(), (Set) provider5.get());
        return systemKeyPress;
    }

    public static SystemKeyPress_Factory create(Provider<Context> provider, Provider<Handler> provider2, Provider<CommandQueue> provider3, Provider<Long> provider4, Provider<Set<Integer>> provider5) {
        SystemKeyPress_Factory systemKeyPress_Factory = new SystemKeyPress_Factory(provider, provider2, provider3, provider4, provider5);
        return systemKeyPress_Factory;
    }
}
