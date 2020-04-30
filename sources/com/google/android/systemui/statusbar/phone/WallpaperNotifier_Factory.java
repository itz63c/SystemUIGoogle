package com.google.android.systemui.statusbar.phone;

import android.content.Context;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class WallpaperNotifier_Factory implements Factory<WallpaperNotifier> {
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<Context> contextProvider;
    private final Provider<NotificationEntryManager> entryManagerProvider;

    public WallpaperNotifier_Factory(Provider<Context> provider, Provider<NotificationEntryManager> provider2, Provider<BroadcastDispatcher> provider3) {
        this.contextProvider = provider;
        this.entryManagerProvider = provider2;
        this.broadcastDispatcherProvider = provider3;
    }

    public WallpaperNotifier get() {
        return provideInstance(this.contextProvider, this.entryManagerProvider, this.broadcastDispatcherProvider);
    }

    public static WallpaperNotifier provideInstance(Provider<Context> provider, Provider<NotificationEntryManager> provider2, Provider<BroadcastDispatcher> provider3) {
        return new WallpaperNotifier((Context) provider.get(), (NotificationEntryManager) provider2.get(), (BroadcastDispatcher) provider3.get());
    }

    public static WallpaperNotifier_Factory create(Provider<Context> provider, Provider<NotificationEntryManager> provider2, Provider<BroadcastDispatcher> provider3) {
        return new WallpaperNotifier_Factory(provider, provider2, provider3);
    }
}
