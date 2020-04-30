package com.android.systemui.statusbar.notification.init;

import android.service.notification.StatusBarNotification;
import com.android.systemui.plugins.statusbar.NotificationSwipeActionHelper.SnoozeOption;
import com.android.systemui.statusbar.NotificationListener;
import com.android.systemui.statusbar.NotificationPresenter;
import com.android.systemui.statusbar.notification.NotificationActivityStarter;
import com.android.systemui.statusbar.notification.collection.inflation.NotificationRowBinderImpl.BindRowCallback;
import com.android.systemui.statusbar.notification.stack.NotificationListContainer;
import com.android.systemui.statusbar.phone.StatusBar;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: NotificationsControllerStub.kt */
public final class NotificationsControllerStub implements NotificationsController {
    private final NotificationListener notificationListener;

    public int getActiveNotificationsCount() {
        return 0;
    }

    public void requestNotificationUpdate(String str) {
        Intrinsics.checkParameterIsNotNull(str, "reason");
    }

    public void resetUserExpandedStates() {
    }

    public void setNotificationSnoozed(StatusBarNotification statusBarNotification, int i) {
        Intrinsics.checkParameterIsNotNull(statusBarNotification, "sbn");
    }

    public void setNotificationSnoozed(StatusBarNotification statusBarNotification, SnoozeOption snoozeOption) {
        Intrinsics.checkParameterIsNotNull(statusBarNotification, "sbn");
        Intrinsics.checkParameterIsNotNull(snoozeOption, "snoozeOption");
    }

    public NotificationsControllerStub(NotificationListener notificationListener2) {
        Intrinsics.checkParameterIsNotNull(notificationListener2, "notificationListener");
        this.notificationListener = notificationListener2;
    }

    public void initialize(StatusBar statusBar, NotificationPresenter notificationPresenter, NotificationListContainer notificationListContainer, NotificationActivityStarter notificationActivityStarter, BindRowCallback bindRowCallback) {
        Intrinsics.checkParameterIsNotNull(statusBar, "statusBar");
        Intrinsics.checkParameterIsNotNull(notificationPresenter, "presenter");
        Intrinsics.checkParameterIsNotNull(notificationListContainer, "listContainer");
        Intrinsics.checkParameterIsNotNull(notificationActivityStarter, "notificationActivityStarter");
        Intrinsics.checkParameterIsNotNull(bindRowCallback, "bindRowCallback");
        this.notificationListener.registerAsSystemService();
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr, boolean z) {
        Intrinsics.checkParameterIsNotNull(fileDescriptor, "fd");
        Intrinsics.checkParameterIsNotNull(printWriter, "pw");
        Intrinsics.checkParameterIsNotNull(strArr, "args");
        printWriter.println();
        printWriter.println("Notification handling disabled");
        printWriter.println();
    }
}
