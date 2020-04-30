package com.android.systemui.statusbar.notification.dagger;

import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.NotificationListener;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import com.android.systemui.statusbar.notification.interruption.NotificationAlertingManager;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProvider;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

public final class NotificationsModule_ProvideNotificationAlertingManagerFactory implements Factory<NotificationAlertingManager> {
    private final Provider<HeadsUpManager> headsUpManagerProvider;
    private final Provider<NotificationEntryManager> notificationEntryManagerProvider;
    private final Provider<NotificationInterruptStateProvider> notificationInterruptStateProvider;
    private final Provider<NotificationListener> notificationListenerProvider;
    private final Provider<NotificationRemoteInputManager> remoteInputManagerProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    private final Provider<VisualStabilityManager> visualStabilityManagerProvider;

    public NotificationsModule_ProvideNotificationAlertingManagerFactory(Provider<NotificationEntryManager> provider, Provider<NotificationRemoteInputManager> provider2, Provider<VisualStabilityManager> provider3, Provider<StatusBarStateController> provider4, Provider<NotificationInterruptStateProvider> provider5, Provider<NotificationListener> provider6, Provider<HeadsUpManager> provider7) {
        this.notificationEntryManagerProvider = provider;
        this.remoteInputManagerProvider = provider2;
        this.visualStabilityManagerProvider = provider3;
        this.statusBarStateControllerProvider = provider4;
        this.notificationInterruptStateProvider = provider5;
        this.notificationListenerProvider = provider6;
        this.headsUpManagerProvider = provider7;
    }

    public NotificationAlertingManager get() {
        return provideInstance(this.notificationEntryManagerProvider, this.remoteInputManagerProvider, this.visualStabilityManagerProvider, this.statusBarStateControllerProvider, this.notificationInterruptStateProvider, this.notificationListenerProvider, this.headsUpManagerProvider);
    }

    public static NotificationAlertingManager provideInstance(Provider<NotificationEntryManager> provider, Provider<NotificationRemoteInputManager> provider2, Provider<VisualStabilityManager> provider3, Provider<StatusBarStateController> provider4, Provider<NotificationInterruptStateProvider> provider5, Provider<NotificationListener> provider6, Provider<HeadsUpManager> provider7) {
        return proxyProvideNotificationAlertingManager((NotificationEntryManager) provider.get(), (NotificationRemoteInputManager) provider2.get(), (VisualStabilityManager) provider3.get(), (StatusBarStateController) provider4.get(), (NotificationInterruptStateProvider) provider5.get(), (NotificationListener) provider6.get(), (HeadsUpManager) provider7.get());
    }

    public static NotificationsModule_ProvideNotificationAlertingManagerFactory create(Provider<NotificationEntryManager> provider, Provider<NotificationRemoteInputManager> provider2, Provider<VisualStabilityManager> provider3, Provider<StatusBarStateController> provider4, Provider<NotificationInterruptStateProvider> provider5, Provider<NotificationListener> provider6, Provider<HeadsUpManager> provider7) {
        NotificationsModule_ProvideNotificationAlertingManagerFactory notificationsModule_ProvideNotificationAlertingManagerFactory = new NotificationsModule_ProvideNotificationAlertingManagerFactory(provider, provider2, provider3, provider4, provider5, provider6, provider7);
        return notificationsModule_ProvideNotificationAlertingManagerFactory;
    }

    public static NotificationAlertingManager proxyProvideNotificationAlertingManager(NotificationEntryManager notificationEntryManager, NotificationRemoteInputManager notificationRemoteInputManager, VisualStabilityManager visualStabilityManager, StatusBarStateController statusBarStateController, NotificationInterruptStateProvider notificationInterruptStateProvider2, NotificationListener notificationListener, HeadsUpManager headsUpManager) {
        NotificationAlertingManager provideNotificationAlertingManager = NotificationsModule.provideNotificationAlertingManager(notificationEntryManager, notificationRemoteInputManager, visualStabilityManager, statusBarStateController, notificationInterruptStateProvider2, notificationListener, headsUpManager);
        Preconditions.checkNotNull(provideNotificationAlertingManager, "Cannot return null from a non-@Nullable @Provides method");
        return provideNotificationAlertingManager;
    }
}
