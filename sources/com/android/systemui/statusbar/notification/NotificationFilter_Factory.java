package com.android.systemui.statusbar.notification;

import com.android.systemui.plugins.statusbar.StatusBarStateController;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class NotificationFilter_Factory implements Factory<NotificationFilter> {
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;

    public NotificationFilter_Factory(Provider<StatusBarStateController> provider) {
        this.statusBarStateControllerProvider = provider;
    }

    public NotificationFilter get() {
        return provideInstance(this.statusBarStateControllerProvider);
    }

    public static NotificationFilter provideInstance(Provider<StatusBarStateController> provider) {
        return new NotificationFilter((StatusBarStateController) provider.get());
    }

    public static NotificationFilter_Factory create(Provider<StatusBarStateController> provider) {
        return new NotificationFilter_Factory(provider);
    }
}
