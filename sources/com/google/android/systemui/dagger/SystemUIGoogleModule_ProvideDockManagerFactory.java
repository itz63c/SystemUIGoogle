package com.google.android.systemui.dagger;

import android.content.Context;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.dock.DockManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProvider;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

public final class SystemUIGoogleModule_ProvideDockManagerFactory implements Factory<DockManager> {
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<Context> contextProvider;
    private final Provider<NotificationInterruptStateProvider> notificationInterruptionStateProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;

    public SystemUIGoogleModule_ProvideDockManagerFactory(Provider<Context> provider, Provider<BroadcastDispatcher> provider2, Provider<StatusBarStateController> provider3, Provider<NotificationInterruptStateProvider> provider4) {
        this.contextProvider = provider;
        this.broadcastDispatcherProvider = provider2;
        this.statusBarStateControllerProvider = provider3;
        this.notificationInterruptionStateProvider = provider4;
    }

    public DockManager get() {
        return provideInstance(this.contextProvider, this.broadcastDispatcherProvider, this.statusBarStateControllerProvider, this.notificationInterruptionStateProvider);
    }

    public static DockManager provideInstance(Provider<Context> provider, Provider<BroadcastDispatcher> provider2, Provider<StatusBarStateController> provider3, Provider<NotificationInterruptStateProvider> provider4) {
        return proxyProvideDockManager((Context) provider.get(), (BroadcastDispatcher) provider2.get(), (StatusBarStateController) provider3.get(), (NotificationInterruptStateProvider) provider4.get());
    }

    public static SystemUIGoogleModule_ProvideDockManagerFactory create(Provider<Context> provider, Provider<BroadcastDispatcher> provider2, Provider<StatusBarStateController> provider3, Provider<NotificationInterruptStateProvider> provider4) {
        return new SystemUIGoogleModule_ProvideDockManagerFactory(provider, provider2, provider3, provider4);
    }

    public static DockManager proxyProvideDockManager(Context context, BroadcastDispatcher broadcastDispatcher, StatusBarStateController statusBarStateController, NotificationInterruptStateProvider notificationInterruptStateProvider) {
        DockManager provideDockManager = SystemUIGoogleModule.provideDockManager(context, broadcastDispatcher, statusBarStateController, notificationInterruptStateProvider);
        Preconditions.checkNotNull(provideDockManager, "Cannot return null from a non-@Nullable @Provides method");
        return provideDockManager;
    }
}
