package com.android.systemui.statusbar.notification.init;

import com.android.systemui.bubbles.BubbleController;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.NotificationListener;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.collection.inflation.NotificationRowBinderImpl;
import com.android.systemui.statusbar.notification.collection.init.NotifPipelineInitializer;
import com.android.systemui.statusbar.notification.row.NotifBindPipelineInitializer;
import com.android.systemui.statusbar.phone.NotificationGroupAlertTransferHelper;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.statusbar.policy.RemoteInputUriController;
import dagger.internal.DoubleCheck;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class NotificationsControllerImpl_Factory implements Factory<NotificationsControllerImpl> {
    private final Provider<BubbleController> bubbleControllerProvider;
    private final Provider<DeviceProvisionedController> deviceProvisionedControllerProvider;
    private final Provider<NotificationEntryManager> entryManagerProvider;
    private final Provider<FeatureFlags> featureFlagsProvider;
    private final Provider<NotificationGroupAlertTransferHelper> groupAlertTransferHelperProvider;
    private final Provider<NotificationGroupManager> groupManagerProvider;
    private final Provider<HeadsUpManager> headsUpManagerProvider;
    private final Provider<NotifPipelineInitializer> newNotifPipelineProvider;
    private final Provider<NotifBindPipelineInitializer> notifBindPipelineInitializerProvider;
    private final Provider<NotificationListener> notificationListenerProvider;
    private final Provider<NotificationRowBinderImpl> notificationRowBinderProvider;
    private final Provider<RemoteInputUriController> remoteInputUriControllerProvider;

    public NotificationsControllerImpl_Factory(Provider<FeatureFlags> provider, Provider<NotificationListener> provider2, Provider<NotificationEntryManager> provider3, Provider<NotifPipelineInitializer> provider4, Provider<NotifBindPipelineInitializer> provider5, Provider<DeviceProvisionedController> provider6, Provider<NotificationRowBinderImpl> provider7, Provider<RemoteInputUriController> provider8, Provider<BubbleController> provider9, Provider<NotificationGroupManager> provider10, Provider<NotificationGroupAlertTransferHelper> provider11, Provider<HeadsUpManager> provider12) {
        this.featureFlagsProvider = provider;
        this.notificationListenerProvider = provider2;
        this.entryManagerProvider = provider3;
        this.newNotifPipelineProvider = provider4;
        this.notifBindPipelineInitializerProvider = provider5;
        this.deviceProvisionedControllerProvider = provider6;
        this.notificationRowBinderProvider = provider7;
        this.remoteInputUriControllerProvider = provider8;
        this.bubbleControllerProvider = provider9;
        this.groupManagerProvider = provider10;
        this.groupAlertTransferHelperProvider = provider11;
        this.headsUpManagerProvider = provider12;
    }

    public NotificationsControllerImpl get() {
        return provideInstance(this.featureFlagsProvider, this.notificationListenerProvider, this.entryManagerProvider, this.newNotifPipelineProvider, this.notifBindPipelineInitializerProvider, this.deviceProvisionedControllerProvider, this.notificationRowBinderProvider, this.remoteInputUriControllerProvider, this.bubbleControllerProvider, this.groupManagerProvider, this.groupAlertTransferHelperProvider, this.headsUpManagerProvider);
    }

    public static NotificationsControllerImpl provideInstance(Provider<FeatureFlags> provider, Provider<NotificationListener> provider2, Provider<NotificationEntryManager> provider3, Provider<NotifPipelineInitializer> provider4, Provider<NotifBindPipelineInitializer> provider5, Provider<DeviceProvisionedController> provider6, Provider<NotificationRowBinderImpl> provider7, Provider<RemoteInputUriController> provider8, Provider<BubbleController> provider9, Provider<NotificationGroupManager> provider10, Provider<NotificationGroupAlertTransferHelper> provider11, Provider<HeadsUpManager> provider12) {
        NotificationsControllerImpl notificationsControllerImpl = new NotificationsControllerImpl((FeatureFlags) provider.get(), (NotificationListener) provider2.get(), (NotificationEntryManager) provider3.get(), DoubleCheck.lazy(provider4), (NotifBindPipelineInitializer) provider5.get(), (DeviceProvisionedController) provider6.get(), (NotificationRowBinderImpl) provider7.get(), (RemoteInputUriController) provider8.get(), (BubbleController) provider9.get(), (NotificationGroupManager) provider10.get(), (NotificationGroupAlertTransferHelper) provider11.get(), (HeadsUpManager) provider12.get());
        return notificationsControllerImpl;
    }

    public static NotificationsControllerImpl_Factory create(Provider<FeatureFlags> provider, Provider<NotificationListener> provider2, Provider<NotificationEntryManager> provider3, Provider<NotifPipelineInitializer> provider4, Provider<NotifBindPipelineInitializer> provider5, Provider<DeviceProvisionedController> provider6, Provider<NotificationRowBinderImpl> provider7, Provider<RemoteInputUriController> provider8, Provider<BubbleController> provider9, Provider<NotificationGroupManager> provider10, Provider<NotificationGroupAlertTransferHelper> provider11, Provider<HeadsUpManager> provider12) {
        NotificationsControllerImpl_Factory notificationsControllerImpl_Factory = new NotificationsControllerImpl_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11, provider12);
        return notificationsControllerImpl_Factory;
    }
}
