package com.android.systemui.bubbles.dagger;

import android.content.Context;
import com.android.systemui.bubbles.BubbleController;
import com.android.systemui.bubbles.BubbleData;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProvider;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.phone.NotificationShadeWindowController;
import com.android.systemui.statusbar.phone.ShadeController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.util.FloatingContentCoordinator;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.inject.Provider;

public final class BubbleModule_NewBubbleControllerFactory implements Factory<BubbleController> {
    private final Provider<ConfigurationController> configurationControllerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<BubbleData> dataProvider;
    private final Provider<DumpManager> dumpManagerProvider;
    private final Provider<NotificationEntryManager> entryManagerProvider;
    private final Provider<FeatureFlags> featureFlagsProvider;
    private final Provider<FloatingContentCoordinator> floatingContentCoordinatorProvider;
    private final Provider<NotificationGroupManager> groupManagerProvider;
    private final Provider<NotificationInterruptStateProvider> interruptionStateProvider;
    private final Provider<NotifPipeline> notifPipelineProvider;
    private final Provider<NotificationLockscreenUserManager> notifUserManagerProvider;
    private final Provider<NotificationShadeWindowController> notificationShadeWindowControllerProvider;
    private final Provider<ShadeController> shadeControllerProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    private final Provider<ZenModeController> zenModeControllerProvider;

    public BubbleModule_NewBubbleControllerFactory(Provider<Context> provider, Provider<NotificationShadeWindowController> provider2, Provider<StatusBarStateController> provider3, Provider<ShadeController> provider4, Provider<BubbleData> provider5, Provider<ConfigurationController> provider6, Provider<NotificationInterruptStateProvider> provider7, Provider<ZenModeController> provider8, Provider<NotificationLockscreenUserManager> provider9, Provider<NotificationGroupManager> provider10, Provider<NotificationEntryManager> provider11, Provider<NotifPipeline> provider12, Provider<FeatureFlags> provider13, Provider<DumpManager> provider14, Provider<FloatingContentCoordinator> provider15) {
        this.contextProvider = provider;
        this.notificationShadeWindowControllerProvider = provider2;
        this.statusBarStateControllerProvider = provider3;
        this.shadeControllerProvider = provider4;
        this.dataProvider = provider5;
        this.configurationControllerProvider = provider6;
        this.interruptionStateProvider = provider7;
        this.zenModeControllerProvider = provider8;
        this.notifUserManagerProvider = provider9;
        this.groupManagerProvider = provider10;
        this.entryManagerProvider = provider11;
        this.notifPipelineProvider = provider12;
        this.featureFlagsProvider = provider13;
        this.dumpManagerProvider = provider14;
        this.floatingContentCoordinatorProvider = provider15;
    }

    public BubbleController get() {
        return provideInstance(this.contextProvider, this.notificationShadeWindowControllerProvider, this.statusBarStateControllerProvider, this.shadeControllerProvider, this.dataProvider, this.configurationControllerProvider, this.interruptionStateProvider, this.zenModeControllerProvider, this.notifUserManagerProvider, this.groupManagerProvider, this.entryManagerProvider, this.notifPipelineProvider, this.featureFlagsProvider, this.dumpManagerProvider, this.floatingContentCoordinatorProvider);
    }

    public static BubbleController provideInstance(Provider<Context> provider, Provider<NotificationShadeWindowController> provider2, Provider<StatusBarStateController> provider3, Provider<ShadeController> provider4, Provider<BubbleData> provider5, Provider<ConfigurationController> provider6, Provider<NotificationInterruptStateProvider> provider7, Provider<ZenModeController> provider8, Provider<NotificationLockscreenUserManager> provider9, Provider<NotificationGroupManager> provider10, Provider<NotificationEntryManager> provider11, Provider<NotifPipeline> provider12, Provider<FeatureFlags> provider13, Provider<DumpManager> provider14, Provider<FloatingContentCoordinator> provider15) {
        return proxyNewBubbleController((Context) provider.get(), (NotificationShadeWindowController) provider2.get(), (StatusBarStateController) provider3.get(), (ShadeController) provider4.get(), (BubbleData) provider5.get(), (ConfigurationController) provider6.get(), (NotificationInterruptStateProvider) provider7.get(), (ZenModeController) provider8.get(), (NotificationLockscreenUserManager) provider9.get(), (NotificationGroupManager) provider10.get(), (NotificationEntryManager) provider11.get(), (NotifPipeline) provider12.get(), (FeatureFlags) provider13.get(), (DumpManager) provider14.get(), (FloatingContentCoordinator) provider15.get());
    }

    public static BubbleModule_NewBubbleControllerFactory create(Provider<Context> provider, Provider<NotificationShadeWindowController> provider2, Provider<StatusBarStateController> provider3, Provider<ShadeController> provider4, Provider<BubbleData> provider5, Provider<ConfigurationController> provider6, Provider<NotificationInterruptStateProvider> provider7, Provider<ZenModeController> provider8, Provider<NotificationLockscreenUserManager> provider9, Provider<NotificationGroupManager> provider10, Provider<NotificationEntryManager> provider11, Provider<NotifPipeline> provider12, Provider<FeatureFlags> provider13, Provider<DumpManager> provider14, Provider<FloatingContentCoordinator> provider15) {
        BubbleModule_NewBubbleControllerFactory bubbleModule_NewBubbleControllerFactory = new BubbleModule_NewBubbleControllerFactory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11, provider12, provider13, provider14, provider15);
        return bubbleModule_NewBubbleControllerFactory;
    }

    public static BubbleController proxyNewBubbleController(Context context, NotificationShadeWindowController notificationShadeWindowController, StatusBarStateController statusBarStateController, ShadeController shadeController, BubbleData bubbleData, ConfigurationController configurationController, NotificationInterruptStateProvider notificationInterruptStateProvider, ZenModeController zenModeController, NotificationLockscreenUserManager notificationLockscreenUserManager, NotificationGroupManager notificationGroupManager, NotificationEntryManager notificationEntryManager, NotifPipeline notifPipeline, FeatureFlags featureFlags, DumpManager dumpManager, FloatingContentCoordinator floatingContentCoordinator) {
        BubbleController newBubbleController = BubbleModule.newBubbleController(context, notificationShadeWindowController, statusBarStateController, shadeController, bubbleData, configurationController, notificationInterruptStateProvider, zenModeController, notificationLockscreenUserManager, notificationGroupManager, notificationEntryManager, notifPipeline, featureFlags, dumpManager, floatingContentCoordinator);
        Preconditions.checkNotNull(newBubbleController, "Cannot return null from a non-@Nullable @Provides method");
        return newBubbleController;
    }
}
