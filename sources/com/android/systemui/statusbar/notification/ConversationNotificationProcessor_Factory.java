package com.android.systemui.statusbar.notification;

import android.content.pm.LauncherApps;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class ConversationNotificationProcessor_Factory implements Factory<ConversationNotificationProcessor> {
    private final Provider<LauncherApps> launcherAppsProvider;

    public ConversationNotificationProcessor_Factory(Provider<LauncherApps> provider) {
        this.launcherAppsProvider = provider;
    }

    public ConversationNotificationProcessor get() {
        return provideInstance(this.launcherAppsProvider);
    }

    public static ConversationNotificationProcessor provideInstance(Provider<LauncherApps> provider) {
        return new ConversationNotificationProcessor((LauncherApps) provider.get());
    }

    public static ConversationNotificationProcessor_Factory create(Provider<LauncherApps> provider) {
        return new ConversationNotificationProcessor_Factory(provider);
    }
}
