package com.android.systemui.statusbar.notification.dagger;

import com.android.internal.logging.UiEventLogger;
import dagger.internal.Factory;
import dagger.internal.Preconditions;

public final class NotificationsModule_ProvideUiEventLoggerFactory implements Factory<UiEventLogger> {
    private static final NotificationsModule_ProvideUiEventLoggerFactory INSTANCE = new NotificationsModule_ProvideUiEventLoggerFactory();

    public UiEventLogger get() {
        return provideInstance();
    }

    public static UiEventLogger provideInstance() {
        return proxyProvideUiEventLogger();
    }

    public static NotificationsModule_ProvideUiEventLoggerFactory create() {
        return INSTANCE;
    }

    public static UiEventLogger proxyProvideUiEventLogger() {
        UiEventLogger provideUiEventLogger = NotificationsModule.provideUiEventLogger();
        Preconditions.checkNotNull(provideUiEventLogger, "Cannot return null from a non-@Nullable @Provides method");
        return provideUiEventLogger;
    }
}
