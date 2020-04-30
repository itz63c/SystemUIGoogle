package com.android.systemui.statusbar.notification.init;

import android.service.notification.SnoozeCriterion;
import android.service.notification.StatusBarNotification;
import com.android.systemui.bubbles.BubbleController;
import com.android.systemui.plugins.statusbar.NotificationSwipeActionHelper.SnoozeOption;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.NotificationListener;
import com.android.systemui.statusbar.NotificationPresenter;
import com.android.systemui.statusbar.notification.NotificationActivityStarter;
import com.android.systemui.statusbar.notification.NotificationClicker;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.NotificationListController;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.inflation.NotificationRowBinderImpl;
import com.android.systemui.statusbar.notification.collection.inflation.NotificationRowBinderImpl.BindRowCallback;
import com.android.systemui.statusbar.notification.collection.init.NotifPipelineInitializer;
import com.android.systemui.statusbar.notification.row.NotifBindPipelineInitializer;
import com.android.systemui.statusbar.notification.stack.NotificationListContainer;
import com.android.systemui.statusbar.phone.NotificationGroupAlertTransferHelper;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.statusbar.policy.RemoteInputUriController;
import dagger.Lazy;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Optional;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: NotificationsControllerImpl.kt */
public final class NotificationsControllerImpl implements NotificationsController {
    private final BubbleController bubbleController;
    private final DeviceProvisionedController deviceProvisionedController;
    private final NotificationEntryManager entryManager;
    private final FeatureFlags featureFlags;
    private final NotificationGroupAlertTransferHelper groupAlertTransferHelper;
    private final NotificationGroupManager groupManager;
    private final HeadsUpManager headsUpManager;
    private final Lazy<NotifPipelineInitializer> newNotifPipeline;
    private final NotifBindPipelineInitializer notifBindPipelineInitializer;
    private final NotificationListener notificationListener;
    private final NotificationRowBinderImpl notificationRowBinder;
    private final RemoteInputUriController remoteInputUriController;

    public NotificationsControllerImpl(FeatureFlags featureFlags2, NotificationListener notificationListener2, NotificationEntryManager notificationEntryManager, Lazy<NotifPipelineInitializer> lazy, NotifBindPipelineInitializer notifBindPipelineInitializer2, DeviceProvisionedController deviceProvisionedController2, NotificationRowBinderImpl notificationRowBinderImpl, RemoteInputUriController remoteInputUriController2, BubbleController bubbleController2, NotificationGroupManager notificationGroupManager, NotificationGroupAlertTransferHelper notificationGroupAlertTransferHelper, HeadsUpManager headsUpManager2) {
        Intrinsics.checkParameterIsNotNull(featureFlags2, "featureFlags");
        Intrinsics.checkParameterIsNotNull(notificationListener2, "notificationListener");
        Intrinsics.checkParameterIsNotNull(notificationEntryManager, "entryManager");
        Intrinsics.checkParameterIsNotNull(lazy, "newNotifPipeline");
        Intrinsics.checkParameterIsNotNull(notifBindPipelineInitializer2, "notifBindPipelineInitializer");
        Intrinsics.checkParameterIsNotNull(deviceProvisionedController2, "deviceProvisionedController");
        Intrinsics.checkParameterIsNotNull(notificationRowBinderImpl, "notificationRowBinder");
        Intrinsics.checkParameterIsNotNull(remoteInputUriController2, "remoteInputUriController");
        Intrinsics.checkParameterIsNotNull(bubbleController2, "bubbleController");
        Intrinsics.checkParameterIsNotNull(notificationGroupManager, "groupManager");
        Intrinsics.checkParameterIsNotNull(notificationGroupAlertTransferHelper, "groupAlertTransferHelper");
        Intrinsics.checkParameterIsNotNull(headsUpManager2, "headsUpManager");
        this.featureFlags = featureFlags2;
        this.notificationListener = notificationListener2;
        this.entryManager = notificationEntryManager;
        this.newNotifPipeline = lazy;
        this.notifBindPipelineInitializer = notifBindPipelineInitializer2;
        this.deviceProvisionedController = deviceProvisionedController2;
        this.notificationRowBinder = notificationRowBinderImpl;
        this.remoteInputUriController = remoteInputUriController2;
        this.bubbleController = bubbleController2;
        this.groupManager = notificationGroupManager;
        this.groupAlertTransferHelper = notificationGroupAlertTransferHelper;
        this.headsUpManager = headsUpManager2;
    }

    public void initialize(StatusBar statusBar, NotificationPresenter notificationPresenter, NotificationListContainer notificationListContainer, NotificationActivityStarter notificationActivityStarter, BindRowCallback bindRowCallback) {
        Intrinsics.checkParameterIsNotNull(statusBar, "statusBar");
        Intrinsics.checkParameterIsNotNull(notificationPresenter, "presenter");
        Intrinsics.checkParameterIsNotNull(notificationListContainer, "listContainer");
        Intrinsics.checkParameterIsNotNull(notificationActivityStarter, "notificationActivityStarter");
        Intrinsics.checkParameterIsNotNull(bindRowCallback, "bindRowCallback");
        this.notificationListener.registerAsSystemService();
        new NotificationListController(this.entryManager, notificationListContainer, this.deviceProvisionedController).bind();
        this.notificationRowBinder.setNotificationClicker(new NotificationClicker(Optional.of(statusBar), this.bubbleController, notificationActivityStarter));
        this.notificationRowBinder.setUpWithPresenter(notificationPresenter, notificationListContainer, bindRowCallback);
        this.notifBindPipelineInitializer.initialize();
        if (this.featureFlags.isNewNotifPipelineEnabled()) {
            ((NotifPipelineInitializer) this.newNotifPipeline.get()).initialize(this.notificationListener, this.notificationRowBinder, notificationListContainer);
        }
        if (!this.featureFlags.isNewNotifPipelineRenderingEnabled()) {
            this.notificationRowBinder.setInflationCallback(this.entryManager);
            this.remoteInputUriController.attach(this.entryManager);
            this.groupAlertTransferHelper.bind(this.entryManager, this.groupManager);
            this.headsUpManager.addListener(this.groupManager);
            this.headsUpManager.addListener(this.groupAlertTransferHelper);
            this.groupManager.setHeadsUpManager(this.headsUpManager);
            this.groupAlertTransferHelper.setHeadsUpManager(this.headsUpManager);
            this.entryManager.attach(this.notificationListener);
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr, boolean z) {
        Intrinsics.checkParameterIsNotNull(fileDescriptor, "fd");
        Intrinsics.checkParameterIsNotNull(printWriter, "pw");
        Intrinsics.checkParameterIsNotNull(strArr, "args");
        if (z) {
            this.entryManager.dump(printWriter, "  ");
        }
        this.groupManager.dump(fileDescriptor, printWriter, strArr);
    }

    public void requestNotificationUpdate(String str) {
        Intrinsics.checkParameterIsNotNull(str, "reason");
        this.entryManager.updateNotifications(str);
    }

    public void resetUserExpandedStates() {
        for (NotificationEntry resetUserExpansion : this.entryManager.getVisibleNotifications()) {
            resetUserExpansion.resetUserExpansion();
        }
    }

    public void setNotificationSnoozed(StatusBarNotification statusBarNotification, SnoozeOption snoozeOption) {
        Intrinsics.checkParameterIsNotNull(statusBarNotification, "sbn");
        Intrinsics.checkParameterIsNotNull(snoozeOption, "snoozeOption");
        if (snoozeOption.getSnoozeCriterion() != null) {
            NotificationListener notificationListener2 = this.notificationListener;
            String key = statusBarNotification.getKey();
            SnoozeCriterion snoozeCriterion = snoozeOption.getSnoozeCriterion();
            Intrinsics.checkExpressionValueIsNotNull(snoozeCriterion, "snoozeOption.snoozeCriterion");
            notificationListener2.snoozeNotification(key, snoozeCriterion.getId());
            return;
        }
        this.notificationListener.snoozeNotification(statusBarNotification.getKey(), ((long) (snoozeOption.getMinutesToSnoozeFor() * 60)) * ((long) 1000));
    }

    public int getActiveNotificationsCount() {
        return this.entryManager.getActiveNotificationsCount();
    }

    public void setNotificationSnoozed(StatusBarNotification statusBarNotification, int i) {
        Intrinsics.checkParameterIsNotNull(statusBarNotification, "sbn");
        this.notificationListener.snoozeNotification(statusBarNotification.getKey(), ((long) (i * 60 * 60)) * ((long) 1000));
    }
}
