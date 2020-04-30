package com.android.systemui.statusbar.notification.row.dagger;

import android.content.Context;
import android.service.notification.StatusBarNotification;
import com.android.systemui.statusbar.notification.row.dagger.ExpandableNotificationRowComponent.ExpandableNotificationRowModule;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

/* renamed from: com.android.systemui.statusbar.notification.row.dagger.ExpandableNotificationRowComponent_ExpandableNotificationRowModule_ProvideAppNameFactory */
public final class C1292x3e2d0aca implements Factory<String> {
    private final Provider<Context> contextProvider;
    private final Provider<StatusBarNotification> statusBarNotificationProvider;

    public C1292x3e2d0aca(Provider<Context> provider, Provider<StatusBarNotification> provider2) {
        this.contextProvider = provider;
        this.statusBarNotificationProvider = provider2;
    }

    public String get() {
        return provideInstance(this.contextProvider, this.statusBarNotificationProvider);
    }

    public static String provideInstance(Provider<Context> provider, Provider<StatusBarNotification> provider2) {
        return proxyProvideAppName((Context) provider.get(), (StatusBarNotification) provider2.get());
    }

    public static C1292x3e2d0aca create(Provider<Context> provider, Provider<StatusBarNotification> provider2) {
        return new C1292x3e2d0aca(provider, provider2);
    }

    public static String proxyProvideAppName(Context context, StatusBarNotification statusBarNotification) {
        String provideAppName = ExpandableNotificationRowModule.provideAppName(context, statusBarNotification);
        Preconditions.checkNotNull(provideAppName, "Cannot return null from a non-@Nullable @Provides method");
        return provideAppName;
    }
}
