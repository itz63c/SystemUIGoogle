package com.android.systemui.statusbar.phone;

import android.content.Context;
import com.android.systemui.bubbles.BubbleController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class StatusBarTouchableRegionManager_Factory implements Factory<StatusBarTouchableRegionManager> {
    private final Provider<BubbleController> bubbleControllerProvider;
    private final Provider<ConfigurationController> configurationControllerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<HeadsUpManagerPhone> headsUpManagerProvider;
    private final Provider<NotificationShadeWindowController> notificationShadeWindowControllerProvider;

    public StatusBarTouchableRegionManager_Factory(Provider<Context> provider, Provider<NotificationShadeWindowController> provider2, Provider<ConfigurationController> provider3, Provider<HeadsUpManagerPhone> provider4, Provider<BubbleController> provider5) {
        this.contextProvider = provider;
        this.notificationShadeWindowControllerProvider = provider2;
        this.configurationControllerProvider = provider3;
        this.headsUpManagerProvider = provider4;
        this.bubbleControllerProvider = provider5;
    }

    public StatusBarTouchableRegionManager get() {
        return provideInstance(this.contextProvider, this.notificationShadeWindowControllerProvider, this.configurationControllerProvider, this.headsUpManagerProvider, this.bubbleControllerProvider);
    }

    public static StatusBarTouchableRegionManager provideInstance(Provider<Context> provider, Provider<NotificationShadeWindowController> provider2, Provider<ConfigurationController> provider3, Provider<HeadsUpManagerPhone> provider4, Provider<BubbleController> provider5) {
        StatusBarTouchableRegionManager statusBarTouchableRegionManager = new StatusBarTouchableRegionManager((Context) provider.get(), (NotificationShadeWindowController) provider2.get(), (ConfigurationController) provider3.get(), (HeadsUpManagerPhone) provider4.get(), (BubbleController) provider5.get());
        return statusBarTouchableRegionManager;
    }

    public static StatusBarTouchableRegionManager_Factory create(Provider<Context> provider, Provider<NotificationShadeWindowController> provider2, Provider<ConfigurationController> provider3, Provider<HeadsUpManagerPhone> provider4, Provider<BubbleController> provider5) {
        StatusBarTouchableRegionManager_Factory statusBarTouchableRegionManager_Factory = new StatusBarTouchableRegionManager_Factory(provider, provider2, provider3, provider4, provider5);
        return statusBarTouchableRegionManager_Factory;
    }
}
