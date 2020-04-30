package com.android.systemui.statusbar.p008tv;

import android.content.Context;
import com.android.systemui.statusbar.CommandQueue;
import dagger.internal.Factory;
import javax.inject.Provider;

/* renamed from: com.android.systemui.statusbar.tv.TvStatusBar_Factory */
public final class TvStatusBar_Factory implements Factory<TvStatusBar> {
    private final Provider<CommandQueue> commandQueueProvider;
    private final Provider<Context> contextProvider;

    public TvStatusBar_Factory(Provider<Context> provider, Provider<CommandQueue> provider2) {
        this.contextProvider = provider;
        this.commandQueueProvider = provider2;
    }

    public TvStatusBar get() {
        return provideInstance(this.contextProvider, this.commandQueueProvider);
    }

    public static TvStatusBar provideInstance(Provider<Context> provider, Provider<CommandQueue> provider2) {
        return new TvStatusBar((Context) provider.get(), (CommandQueue) provider2.get());
    }

    public static TvStatusBar_Factory create(Provider<Context> provider, Provider<CommandQueue> provider2) {
        return new TvStatusBar_Factory(provider, provider2);
    }
}
