package com.android.systemui.statusbar.notification.row.dagger;

import android.service.notification.StatusBarNotification;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.dagger.ExpandableNotificationRowComponent.ExpandableNotificationRowModule;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

/* renamed from: com.android.systemui.statusbar.notification.row.dagger.ExpandableNotificationRowComponent_ExpandableNotificationRowModule_ProvideStatusBarNotificationFactory */
public final class C1294xc255c3ca implements Factory<StatusBarNotification> {
    private final Provider<NotificationEntry> notificationEntryProvider;

    public C1294xc255c3ca(Provider<NotificationEntry> provider) {
        this.notificationEntryProvider = provider;
    }

    public StatusBarNotification get() {
        return provideInstance(this.notificationEntryProvider);
    }

    public static StatusBarNotification provideInstance(Provider<NotificationEntry> provider) {
        return proxyProvideStatusBarNotification((NotificationEntry) provider.get());
    }

    public static C1294xc255c3ca create(Provider<NotificationEntry> provider) {
        return new C1294xc255c3ca(provider);
    }

    public static StatusBarNotification proxyProvideStatusBarNotification(NotificationEntry notificationEntry) {
        StatusBarNotification provideStatusBarNotification = ExpandableNotificationRowModule.provideStatusBarNotification(notificationEntry);
        Preconditions.checkNotNull(provideStatusBarNotification, "Cannot return null from a non-@Nullable @Provides method");
        return provideStatusBarNotification;
    }
}
