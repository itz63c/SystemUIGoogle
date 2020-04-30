package com.android.systemui.statusbar.phone;

import android.content.Context;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class StatusBarRemoteInputCallback_Factory implements Factory<StatusBarRemoteInputCallback> {
    private final Provider<ActivityStarter> activityStarterProvider;
    private final Provider<CommandQueue> commandQueueProvider;
    private final Provider<Context> contextProvider;
    private final Provider<NotificationGroupManager> groupManagerProvider;
    private final Provider<KeyguardStateController> keyguardStateControllerProvider;
    private final Provider<NotificationLockscreenUserManager> notificationLockscreenUserManagerProvider;
    private final Provider<ShadeController> shadeControllerProvider;
    private final Provider<StatusBarKeyguardViewManager> statusBarKeyguardViewManagerProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;

    public StatusBarRemoteInputCallback_Factory(Provider<Context> provider, Provider<NotificationGroupManager> provider2, Provider<NotificationLockscreenUserManager> provider3, Provider<KeyguardStateController> provider4, Provider<StatusBarStateController> provider5, Provider<StatusBarKeyguardViewManager> provider6, Provider<ActivityStarter> provider7, Provider<ShadeController> provider8, Provider<CommandQueue> provider9) {
        this.contextProvider = provider;
        this.groupManagerProvider = provider2;
        this.notificationLockscreenUserManagerProvider = provider3;
        this.keyguardStateControllerProvider = provider4;
        this.statusBarStateControllerProvider = provider5;
        this.statusBarKeyguardViewManagerProvider = provider6;
        this.activityStarterProvider = provider7;
        this.shadeControllerProvider = provider8;
        this.commandQueueProvider = provider9;
    }

    public StatusBarRemoteInputCallback get() {
        return provideInstance(this.contextProvider, this.groupManagerProvider, this.notificationLockscreenUserManagerProvider, this.keyguardStateControllerProvider, this.statusBarStateControllerProvider, this.statusBarKeyguardViewManagerProvider, this.activityStarterProvider, this.shadeControllerProvider, this.commandQueueProvider);
    }

    public static StatusBarRemoteInputCallback provideInstance(Provider<Context> provider, Provider<NotificationGroupManager> provider2, Provider<NotificationLockscreenUserManager> provider3, Provider<KeyguardStateController> provider4, Provider<StatusBarStateController> provider5, Provider<StatusBarKeyguardViewManager> provider6, Provider<ActivityStarter> provider7, Provider<ShadeController> provider8, Provider<CommandQueue> provider9) {
        StatusBarRemoteInputCallback statusBarRemoteInputCallback = new StatusBarRemoteInputCallback((Context) provider.get(), (NotificationGroupManager) provider2.get(), (NotificationLockscreenUserManager) provider3.get(), (KeyguardStateController) provider4.get(), (StatusBarStateController) provider5.get(), (StatusBarKeyguardViewManager) provider6.get(), (ActivityStarter) provider7.get(), (ShadeController) provider8.get(), (CommandQueue) provider9.get());
        return statusBarRemoteInputCallback;
    }

    public static StatusBarRemoteInputCallback_Factory create(Provider<Context> provider, Provider<NotificationGroupManager> provider2, Provider<NotificationLockscreenUserManager> provider3, Provider<KeyguardStateController> provider4, Provider<StatusBarStateController> provider5, Provider<StatusBarKeyguardViewManager> provider6, Provider<ActivityStarter> provider7, Provider<ShadeController> provider8, Provider<CommandQueue> provider9) {
        StatusBarRemoteInputCallback_Factory statusBarRemoteInputCallback_Factory = new StatusBarRemoteInputCallback_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9);
        return statusBarRemoteInputCallback_Factory;
    }
}
