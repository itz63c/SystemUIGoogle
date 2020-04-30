package com.android.systemui.statusbar.phone;

import com.android.systemui.plugins.statusbar.StatusBarStateController;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class NotificationGroupManager_Factory implements Factory<NotificationGroupManager> {
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;

    public NotificationGroupManager_Factory(Provider<StatusBarStateController> provider) {
        this.statusBarStateControllerProvider = provider;
    }

    public NotificationGroupManager get() {
        return provideInstance(this.statusBarStateControllerProvider);
    }

    public static NotificationGroupManager provideInstance(Provider<StatusBarStateController> provider) {
        return new NotificationGroupManager((StatusBarStateController) provider.get());
    }

    public static NotificationGroupManager_Factory create(Provider<StatusBarStateController> provider) {
        return new NotificationGroupManager_Factory(provider);
    }
}
