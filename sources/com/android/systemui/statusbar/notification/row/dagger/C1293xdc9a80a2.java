package com.android.systemui.statusbar.notification.row.dagger;

import android.service.notification.StatusBarNotification;
import com.android.systemui.statusbar.notification.row.dagger.ExpandableNotificationRowComponent.ExpandableNotificationRowModule;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

/* renamed from: com.android.systemui.statusbar.notification.row.dagger.ExpandableNotificationRowComponent_ExpandableNotificationRowModule_ProvideNotificationKeyFactory */
public final class C1293xdc9a80a2 implements Factory<String> {
    private final Provider<StatusBarNotification> statusBarNotificationProvider;

    public C1293xdc9a80a2(Provider<StatusBarNotification> provider) {
        this.statusBarNotificationProvider = provider;
    }

    public String get() {
        return provideInstance(this.statusBarNotificationProvider);
    }

    public static String provideInstance(Provider<StatusBarNotification> provider) {
        return proxyProvideNotificationKey((StatusBarNotification) provider.get());
    }

    public static C1293xdc9a80a2 create(Provider<StatusBarNotification> provider) {
        return new C1293xdc9a80a2(provider);
    }

    public static String proxyProvideNotificationKey(StatusBarNotification statusBarNotification) {
        String provideNotificationKey = ExpandableNotificationRowModule.provideNotificationKey(statusBarNotification);
        Preconditions.checkNotNull(provideNotificationKey, "Cannot return null from a non-@Nullable @Provides method");
        return provideNotificationKey;
    }
}
