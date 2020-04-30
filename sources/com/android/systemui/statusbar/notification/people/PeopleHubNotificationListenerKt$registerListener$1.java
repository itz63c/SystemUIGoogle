package com.android.systemui.statusbar.notification.people;

import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationLockscreenUserManager.UserChangedListener;

/* compiled from: PeopleHubNotificationListener.kt */
public final class PeopleHubNotificationListenerKt$registerListener$1 implements Subscription {
    final /* synthetic */ UserChangedListener $listener;
    final /* synthetic */ NotificationLockscreenUserManager $this_registerListener;

    PeopleHubNotificationListenerKt$registerListener$1(NotificationLockscreenUserManager notificationLockscreenUserManager, UserChangedListener userChangedListener) {
        this.$this_registerListener = notificationLockscreenUserManager;
        this.$listener = userChangedListener;
    }

    public void unsubscribe() {
        this.$this_registerListener.removeUserChangedListener(this.$listener);
    }
}
