package com.android.systemui.statusbar.phone;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.Handler;
import android.service.dreams.IDreamManager;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.widget.LockPatternUtils;
import com.android.systemui.ActivityIntentHelper;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.bubbles.BubbleController;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.collection.NotifCollection;
import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProvider;
import com.android.systemui.statusbar.phone.StatusBarNotificationActivityStarter.Builder;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import dagger.internal.DoubleCheck;
import dagger.internal.Factory;
import java.util.concurrent.Executor;
import javax.inject.Provider;

public final class StatusBarNotificationActivityStarter_Builder_Factory implements Factory<Builder> {
    private final Provider<ActivityIntentHelper> activityIntentHelperProvider;
    private final Provider<ActivityStarter> activityStarterProvider;
    private final Provider<AssistManager> assistManagerLazyProvider;
    private final Provider<Handler> backgroundHandlerProvider;
    private final Provider<BubbleController> bubbleControllerProvider;
    private final Provider<CommandQueue> commandQueueProvider;
    private final Provider<Context> contextProvider;
    private final Provider<IDreamManager> dreamManagerProvider;
    private final Provider<NotificationEntryManager> entryManagerProvider;
    private final Provider<FeatureFlags> featureFlagsProvider;
    private final Provider<NotificationGroupManager> groupManagerProvider;
    private final Provider<HeadsUpManagerPhone> headsUpManagerProvider;
    private final Provider<KeyguardManager> keyguardManagerProvider;
    private final Provider<KeyguardStateController> keyguardStateControllerProvider;
    private final Provider<LockPatternUtils> lockPatternUtilsProvider;
    private final Provider<NotificationLockscreenUserManager> lockscreenUserManagerProvider;
    private final Provider<Handler> mainThreadHandlerProvider;
    private final Provider<MetricsLogger> metricsLoggerProvider;
    private final Provider<NotifCollection> notifCollectionProvider;
    private final Provider<NotifPipeline> notifPipelineProvider;
    private final Provider<NotificationInterruptStateProvider> notificationInterruptStateProvider;
    private final Provider<StatusBarRemoteInputCallback> remoteInputCallbackProvider;
    private final Provider<NotificationRemoteInputManager> remoteInputManagerProvider;
    private final Provider<ShadeController> shadeControllerProvider;
    private final Provider<StatusBarKeyguardViewManager> statusBarKeyguardViewManagerProvider;
    private final Provider<IStatusBarService> statusBarServiceProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    private final Provider<Executor> uiBgExecutorProvider;

    public StatusBarNotificationActivityStarter_Builder_Factory(Provider<Context> provider, Provider<CommandQueue> provider2, Provider<AssistManager> provider3, Provider<NotificationEntryManager> provider4, Provider<HeadsUpManagerPhone> provider5, Provider<ActivityStarter> provider6, Provider<IStatusBarService> provider7, Provider<StatusBarStateController> provider8, Provider<StatusBarKeyguardViewManager> provider9, Provider<KeyguardManager> provider10, Provider<IDreamManager> provider11, Provider<NotificationRemoteInputManager> provider12, Provider<StatusBarRemoteInputCallback> provider13, Provider<NotificationGroupManager> provider14, Provider<NotificationLockscreenUserManager> provider15, Provider<KeyguardStateController> provider16, Provider<NotificationInterruptStateProvider> provider17, Provider<MetricsLogger> provider18, Provider<LockPatternUtils> provider19, Provider<Handler> provider20, Provider<Handler> provider21, Provider<Executor> provider22, Provider<ActivityIntentHelper> provider23, Provider<BubbleController> provider24, Provider<ShadeController> provider25, Provider<FeatureFlags> provider26, Provider<NotifPipeline> provider27, Provider<NotifCollection> provider28) {
        this.contextProvider = provider;
        this.commandQueueProvider = provider2;
        this.assistManagerLazyProvider = provider3;
        this.entryManagerProvider = provider4;
        this.headsUpManagerProvider = provider5;
        this.activityStarterProvider = provider6;
        this.statusBarServiceProvider = provider7;
        this.statusBarStateControllerProvider = provider8;
        this.statusBarKeyguardViewManagerProvider = provider9;
        this.keyguardManagerProvider = provider10;
        this.dreamManagerProvider = provider11;
        this.remoteInputManagerProvider = provider12;
        this.remoteInputCallbackProvider = provider13;
        this.groupManagerProvider = provider14;
        this.lockscreenUserManagerProvider = provider15;
        this.keyguardStateControllerProvider = provider16;
        this.notificationInterruptStateProvider = provider17;
        this.metricsLoggerProvider = provider18;
        this.lockPatternUtilsProvider = provider19;
        this.mainThreadHandlerProvider = provider20;
        this.backgroundHandlerProvider = provider21;
        this.uiBgExecutorProvider = provider22;
        this.activityIntentHelperProvider = provider23;
        this.bubbleControllerProvider = provider24;
        this.shadeControllerProvider = provider25;
        this.featureFlagsProvider = provider26;
        this.notifPipelineProvider = provider27;
        this.notifCollectionProvider = provider28;
    }

    public Builder get() {
        return provideInstance(this.contextProvider, this.commandQueueProvider, this.assistManagerLazyProvider, this.entryManagerProvider, this.headsUpManagerProvider, this.activityStarterProvider, this.statusBarServiceProvider, this.statusBarStateControllerProvider, this.statusBarKeyguardViewManagerProvider, this.keyguardManagerProvider, this.dreamManagerProvider, this.remoteInputManagerProvider, this.remoteInputCallbackProvider, this.groupManagerProvider, this.lockscreenUserManagerProvider, this.keyguardStateControllerProvider, this.notificationInterruptStateProvider, this.metricsLoggerProvider, this.lockPatternUtilsProvider, this.mainThreadHandlerProvider, this.backgroundHandlerProvider, this.uiBgExecutorProvider, this.activityIntentHelperProvider, this.bubbleControllerProvider, this.shadeControllerProvider, this.featureFlagsProvider, this.notifPipelineProvider, this.notifCollectionProvider);
    }

    public static Builder provideInstance(Provider<Context> provider, Provider<CommandQueue> provider2, Provider<AssistManager> provider3, Provider<NotificationEntryManager> provider4, Provider<HeadsUpManagerPhone> provider5, Provider<ActivityStarter> provider6, Provider<IStatusBarService> provider7, Provider<StatusBarStateController> provider8, Provider<StatusBarKeyguardViewManager> provider9, Provider<KeyguardManager> provider10, Provider<IDreamManager> provider11, Provider<NotificationRemoteInputManager> provider12, Provider<StatusBarRemoteInputCallback> provider13, Provider<NotificationGroupManager> provider14, Provider<NotificationLockscreenUserManager> provider15, Provider<KeyguardStateController> provider16, Provider<NotificationInterruptStateProvider> provider17, Provider<MetricsLogger> provider18, Provider<LockPatternUtils> provider19, Provider<Handler> provider20, Provider<Handler> provider21, Provider<Executor> provider22, Provider<ActivityIntentHelper> provider23, Provider<BubbleController> provider24, Provider<ShadeController> provider25, Provider<FeatureFlags> provider26, Provider<NotifPipeline> provider27, Provider<NotifCollection> provider28) {
        Builder builder = new Builder((Context) provider.get(), (CommandQueue) provider2.get(), DoubleCheck.lazy(provider3), (NotificationEntryManager) provider4.get(), (HeadsUpManagerPhone) provider5.get(), (ActivityStarter) provider6.get(), (IStatusBarService) provider7.get(), (StatusBarStateController) provider8.get(), (StatusBarKeyguardViewManager) provider9.get(), (KeyguardManager) provider10.get(), (IDreamManager) provider11.get(), (NotificationRemoteInputManager) provider12.get(), (StatusBarRemoteInputCallback) provider13.get(), (NotificationGroupManager) provider14.get(), (NotificationLockscreenUserManager) provider15.get(), (KeyguardStateController) provider16.get(), (NotificationInterruptStateProvider) provider17.get(), (MetricsLogger) provider18.get(), (LockPatternUtils) provider19.get(), (Handler) provider20.get(), (Handler) provider21.get(), (Executor) provider22.get(), (ActivityIntentHelper) provider23.get(), (BubbleController) provider24.get(), (ShadeController) provider25.get(), (FeatureFlags) provider26.get(), (NotifPipeline) provider27.get(), (NotifCollection) provider28.get());
        return builder;
    }

    public static StatusBarNotificationActivityStarter_Builder_Factory create(Provider<Context> provider, Provider<CommandQueue> provider2, Provider<AssistManager> provider3, Provider<NotificationEntryManager> provider4, Provider<HeadsUpManagerPhone> provider5, Provider<ActivityStarter> provider6, Provider<IStatusBarService> provider7, Provider<StatusBarStateController> provider8, Provider<StatusBarKeyguardViewManager> provider9, Provider<KeyguardManager> provider10, Provider<IDreamManager> provider11, Provider<NotificationRemoteInputManager> provider12, Provider<StatusBarRemoteInputCallback> provider13, Provider<NotificationGroupManager> provider14, Provider<NotificationLockscreenUserManager> provider15, Provider<KeyguardStateController> provider16, Provider<NotificationInterruptStateProvider> provider17, Provider<MetricsLogger> provider18, Provider<LockPatternUtils> provider19, Provider<Handler> provider20, Provider<Handler> provider21, Provider<Executor> provider22, Provider<ActivityIntentHelper> provider23, Provider<BubbleController> provider24, Provider<ShadeController> provider25, Provider<FeatureFlags> provider26, Provider<NotifPipeline> provider27, Provider<NotifCollection> provider28) {
        StatusBarNotificationActivityStarter_Builder_Factory statusBarNotificationActivityStarter_Builder_Factory = new StatusBarNotificationActivityStarter_Builder_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11, provider12, provider13, provider14, provider15, provider16, provider17, provider18, provider19, provider20, provider21, provider22, provider23, provider24, provider25, provider26, provider27, provider28);
        return statusBarNotificationActivityStarter_Builder_Factory;
    }
}
