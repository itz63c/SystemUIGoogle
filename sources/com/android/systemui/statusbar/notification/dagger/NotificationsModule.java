package com.android.systemui.statusbar.notification.dagger;

import android.app.INotificationManager;
import android.content.Context;
import android.content.pm.LauncherApps;
import android.content.pm.ShortcutManager;
import android.os.Handler;
import android.view.accessibility.AccessibilityManager;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.UiEventLogger;
import com.android.internal.logging.UiEventLoggerImpl;
import com.android.systemui.C2007R$bool;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.NotificationListener;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.notification.ForegroundServiceDismissalFeatureController;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.NotificationEntryManager.KeyguardEnvironment;
import com.android.systemui.statusbar.notification.NotificationEntryManagerLogger;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import com.android.systemui.statusbar.notification.collection.NotificationRankingManager;
import com.android.systemui.statusbar.notification.collection.inflation.NotificationRowBinder;
import com.android.systemui.statusbar.notification.collection.notifcollection.CommonNotifCollection;
import com.android.systemui.statusbar.notification.collection.provider.HighPriorityProvider;
import com.android.systemui.statusbar.notification.init.NotificationsController;
import com.android.systemui.statusbar.notification.init.NotificationsControllerImpl;
import com.android.systemui.statusbar.notification.init.NotificationsControllerStub;
import com.android.systemui.statusbar.notification.interruption.NotificationAlertingManager;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProvider;
import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import com.android.systemui.statusbar.notification.logging.NotificationLogger.ExpansionStateLogger;
import com.android.systemui.statusbar.notification.logging.NotificationPanelLogger;
import com.android.systemui.statusbar.notification.logging.NotificationPanelLoggerImpl;
import com.android.systemui.statusbar.notification.row.NotificationBlockingHelperManager;
import com.android.systemui.statusbar.notification.row.NotificationGutsManager;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.util.leak.LeakDetector;
import dagger.Lazy;
import java.util.concurrent.Executor;

public interface NotificationsModule {
    static NotificationEntryManager provideNotificationEntryManager(NotificationEntryManagerLogger notificationEntryManagerLogger, NotificationGroupManager notificationGroupManager, NotificationRankingManager notificationRankingManager, KeyguardEnvironment keyguardEnvironment, FeatureFlags featureFlags, Lazy<NotificationRowBinder> lazy, Lazy<NotificationRemoteInputManager> lazy2, LeakDetector leakDetector, ForegroundServiceDismissalFeatureController foregroundServiceDismissalFeatureController) {
        NotificationEntryManager notificationEntryManager = new NotificationEntryManager(notificationEntryManagerLogger, notificationGroupManager, notificationRankingManager, keyguardEnvironment, featureFlags, lazy, lazy2, leakDetector, foregroundServiceDismissalFeatureController);
        return notificationEntryManager;
    }

    static NotificationGutsManager provideNotificationGutsManager(Context context, VisualStabilityManager visualStabilityManager, Lazy<StatusBar> lazy, Handler handler, AccessibilityManager accessibilityManager, HighPriorityProvider highPriorityProvider, INotificationManager iNotificationManager, LauncherApps launcherApps, ShortcutManager shortcutManager) {
        NotificationGutsManager notificationGutsManager = new NotificationGutsManager(context, visualStabilityManager, lazy, handler, accessibilityManager, highPriorityProvider, iNotificationManager, launcherApps, shortcutManager);
        return notificationGutsManager;
    }

    static VisualStabilityManager provideVisualStabilityManager(NotificationEntryManager notificationEntryManager, Handler handler) {
        return new VisualStabilityManager(notificationEntryManager, handler);
    }

    static NotificationAlertingManager provideNotificationAlertingManager(NotificationEntryManager notificationEntryManager, NotificationRemoteInputManager notificationRemoteInputManager, VisualStabilityManager visualStabilityManager, StatusBarStateController statusBarStateController, NotificationInterruptStateProvider notificationInterruptStateProvider, NotificationListener notificationListener, HeadsUpManager headsUpManager) {
        NotificationAlertingManager notificationAlertingManager = new NotificationAlertingManager(notificationEntryManager, notificationRemoteInputManager, visualStabilityManager, statusBarStateController, notificationInterruptStateProvider, notificationListener, headsUpManager);
        return notificationAlertingManager;
    }

    static NotificationLogger provideNotificationLogger(NotificationListener notificationListener, Executor executor, NotificationEntryManager notificationEntryManager, StatusBarStateController statusBarStateController, ExpansionStateLogger expansionStateLogger, NotificationPanelLogger notificationPanelLogger) {
        NotificationLogger notificationLogger = new NotificationLogger(notificationListener, executor, notificationEntryManager, statusBarStateController, expansionStateLogger, notificationPanelLogger);
        return notificationLogger;
    }

    static NotificationPanelLogger provideNotificationPanelLogger() {
        return new NotificationPanelLoggerImpl();
    }

    static UiEventLogger provideUiEventLogger() {
        return new UiEventLoggerImpl();
    }

    static NotificationBlockingHelperManager provideNotificationBlockingHelperManager(Context context, NotificationGutsManager notificationGutsManager, NotificationEntryManager notificationEntryManager, MetricsLogger metricsLogger) {
        return new NotificationBlockingHelperManager(context, notificationGutsManager, notificationEntryManager, metricsLogger);
    }

    static NotificationsController provideNotificationsController(Context context, Lazy<NotificationsControllerImpl> lazy, Lazy<NotificationsControllerStub> lazy2) {
        if (context.getResources().getBoolean(C2007R$bool.config_renderNotifications)) {
            return (NotificationsController) lazy.get();
        }
        return (NotificationsController) lazy2.get();
    }

    static CommonNotifCollection provideCommonNotifCollection(FeatureFlags featureFlags, Lazy<NotifPipeline> lazy, NotificationEntryManager notificationEntryManager) {
        return featureFlags.isNewNotifPipelineRenderingEnabled() ? (CommonNotifCollection) lazy.get() : notificationEntryManager;
    }
}
