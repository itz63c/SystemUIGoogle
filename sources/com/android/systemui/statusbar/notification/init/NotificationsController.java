package com.android.systemui.statusbar.notification.init;

import android.service.notification.StatusBarNotification;
import com.android.systemui.plugins.statusbar.NotificationSwipeActionHelper.SnoozeOption;
import com.android.systemui.statusbar.NotificationPresenter;
import com.android.systemui.statusbar.notification.NotificationActivityStarter;
import com.android.systemui.statusbar.notification.collection.inflation.NotificationRowBinderImpl.BindRowCallback;
import com.android.systemui.statusbar.notification.stack.NotificationListContainer;
import com.android.systemui.statusbar.phone.StatusBar;
import java.io.FileDescriptor;
import java.io.PrintWriter;

/* compiled from: NotificationsController.kt */
public interface NotificationsController {
    void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr, boolean z);

    int getActiveNotificationsCount();

    void initialize(StatusBar statusBar, NotificationPresenter notificationPresenter, NotificationListContainer notificationListContainer, NotificationActivityStarter notificationActivityStarter, BindRowCallback bindRowCallback);

    void requestNotificationUpdate(String str);

    void resetUserExpandedStates();

    void setNotificationSnoozed(StatusBarNotification statusBarNotification, int i);

    void setNotificationSnoozed(StatusBarNotification statusBarNotification, SnoozeOption snoozeOption);
}
