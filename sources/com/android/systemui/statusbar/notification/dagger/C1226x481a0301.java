package com.android.systemui.statusbar.notification.dagger;

import android.content.Context;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.row.NotificationBlockingHelperManager;
import com.android.systemui.statusbar.notification.row.NotificationGutsManager;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

/* renamed from: com.android.systemui.statusbar.notification.dagger.NotificationsModule_ProvideNotificationBlockingHelperManagerFactory */
public final class C1226x481a0301 implements Factory<NotificationBlockingHelperManager> {
    private final Provider<Context> contextProvider;
    private final Provider<MetricsLogger> metricsLoggerProvider;
    private final Provider<NotificationEntryManager> notificationEntryManagerProvider;
    private final Provider<NotificationGutsManager> notificationGutsManagerProvider;

    public C1226x481a0301(Provider<Context> provider, Provider<NotificationGutsManager> provider2, Provider<NotificationEntryManager> provider3, Provider<MetricsLogger> provider4) {
        this.contextProvider = provider;
        this.notificationGutsManagerProvider = provider2;
        this.notificationEntryManagerProvider = provider3;
        this.metricsLoggerProvider = provider4;
    }

    public NotificationBlockingHelperManager get() {
        return provideInstance(this.contextProvider, this.notificationGutsManagerProvider, this.notificationEntryManagerProvider, this.metricsLoggerProvider);
    }

    public static NotificationBlockingHelperManager provideInstance(Provider<Context> provider, Provider<NotificationGutsManager> provider2, Provider<NotificationEntryManager> provider3, Provider<MetricsLogger> provider4) {
        return proxyProvideNotificationBlockingHelperManager((Context) provider.get(), (NotificationGutsManager) provider2.get(), (NotificationEntryManager) provider3.get(), (MetricsLogger) provider4.get());
    }

    public static C1226x481a0301 create(Provider<Context> provider, Provider<NotificationGutsManager> provider2, Provider<NotificationEntryManager> provider3, Provider<MetricsLogger> provider4) {
        return new C1226x481a0301(provider, provider2, provider3, provider4);
    }

    public static NotificationBlockingHelperManager proxyProvideNotificationBlockingHelperManager(Context context, NotificationGutsManager notificationGutsManager, NotificationEntryManager notificationEntryManager, MetricsLogger metricsLogger) {
        NotificationBlockingHelperManager provideNotificationBlockingHelperManager = NotificationsModule.provideNotificationBlockingHelperManager(context, notificationGutsManager, notificationEntryManager, metricsLogger);
        Preconditions.checkNotNull(provideNotificationBlockingHelperManager, "Cannot return null from a non-@Nullable @Provides method");
        return provideNotificationBlockingHelperManager;
    }
}
