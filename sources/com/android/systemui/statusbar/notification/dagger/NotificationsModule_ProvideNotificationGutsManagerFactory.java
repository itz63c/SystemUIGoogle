package com.android.systemui.statusbar.notification.dagger;

import android.app.INotificationManager;
import android.content.Context;
import android.content.pm.LauncherApps;
import android.content.pm.ShortcutManager;
import android.os.Handler;
import android.view.accessibility.AccessibilityManager;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import com.android.systemui.statusbar.notification.collection.provider.HighPriorityProvider;
import com.android.systemui.statusbar.notification.row.NotificationGutsManager;
import com.android.systemui.statusbar.phone.StatusBar;
import dagger.Lazy;
import dagger.internal.DoubleCheck;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

public final class NotificationsModule_ProvideNotificationGutsManagerFactory implements Factory<NotificationGutsManager> {
    private final Provider<AccessibilityManager> accessibilityManagerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<HighPriorityProvider> highPriorityProvider;
    private final Provider<LauncherApps> launcherAppsProvider;
    private final Provider<Handler> mainHandlerProvider;
    private final Provider<INotificationManager> notificationManagerProvider;
    private final Provider<ShortcutManager> shortcutManagerProvider;
    private final Provider<StatusBar> statusBarLazyProvider;
    private final Provider<VisualStabilityManager> visualStabilityManagerProvider;

    public NotificationsModule_ProvideNotificationGutsManagerFactory(Provider<Context> provider, Provider<VisualStabilityManager> provider2, Provider<StatusBar> provider3, Provider<Handler> provider4, Provider<AccessibilityManager> provider5, Provider<HighPriorityProvider> provider6, Provider<INotificationManager> provider7, Provider<LauncherApps> provider8, Provider<ShortcutManager> provider9) {
        this.contextProvider = provider;
        this.visualStabilityManagerProvider = provider2;
        this.statusBarLazyProvider = provider3;
        this.mainHandlerProvider = provider4;
        this.accessibilityManagerProvider = provider5;
        this.highPriorityProvider = provider6;
        this.notificationManagerProvider = provider7;
        this.launcherAppsProvider = provider8;
        this.shortcutManagerProvider = provider9;
    }

    public NotificationGutsManager get() {
        return provideInstance(this.contextProvider, this.visualStabilityManagerProvider, this.statusBarLazyProvider, this.mainHandlerProvider, this.accessibilityManagerProvider, this.highPriorityProvider, this.notificationManagerProvider, this.launcherAppsProvider, this.shortcutManagerProvider);
    }

    public static NotificationGutsManager provideInstance(Provider<Context> provider, Provider<VisualStabilityManager> provider2, Provider<StatusBar> provider3, Provider<Handler> provider4, Provider<AccessibilityManager> provider5, Provider<HighPriorityProvider> provider6, Provider<INotificationManager> provider7, Provider<LauncherApps> provider8, Provider<ShortcutManager> provider9) {
        return proxyProvideNotificationGutsManager((Context) provider.get(), (VisualStabilityManager) provider2.get(), DoubleCheck.lazy(provider3), (Handler) provider4.get(), (AccessibilityManager) provider5.get(), (HighPriorityProvider) provider6.get(), (INotificationManager) provider7.get(), (LauncherApps) provider8.get(), (ShortcutManager) provider9.get());
    }

    public static NotificationsModule_ProvideNotificationGutsManagerFactory create(Provider<Context> provider, Provider<VisualStabilityManager> provider2, Provider<StatusBar> provider3, Provider<Handler> provider4, Provider<AccessibilityManager> provider5, Provider<HighPriorityProvider> provider6, Provider<INotificationManager> provider7, Provider<LauncherApps> provider8, Provider<ShortcutManager> provider9) {
        NotificationsModule_ProvideNotificationGutsManagerFactory notificationsModule_ProvideNotificationGutsManagerFactory = new NotificationsModule_ProvideNotificationGutsManagerFactory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9);
        return notificationsModule_ProvideNotificationGutsManagerFactory;
    }

    public static NotificationGutsManager proxyProvideNotificationGutsManager(Context context, VisualStabilityManager visualStabilityManager, Lazy<StatusBar> lazy, Handler handler, AccessibilityManager accessibilityManager, HighPriorityProvider highPriorityProvider2, INotificationManager iNotificationManager, LauncherApps launcherApps, ShortcutManager shortcutManager) {
        NotificationGutsManager provideNotificationGutsManager = NotificationsModule.provideNotificationGutsManager(context, visualStabilityManager, lazy, handler, accessibilityManager, highPriorityProvider2, iNotificationManager, launcherApps, shortcutManager);
        Preconditions.checkNotNull(provideNotificationGutsManager, "Cannot return null from a non-@Nullable @Provides method");
        return provideNotificationGutsManager;
    }
}
