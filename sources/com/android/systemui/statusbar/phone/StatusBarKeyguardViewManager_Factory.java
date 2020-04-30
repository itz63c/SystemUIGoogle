package com.android.systemui.statusbar.phone;

import android.content.Context;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.ViewMediatorCallback;
import com.android.systemui.dock.DockManager;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class StatusBarKeyguardViewManager_Factory implements Factory<StatusBarKeyguardViewManager> {
    private final Provider<ViewMediatorCallback> callbackProvider;
    private final Provider<ConfigurationController> configurationControllerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<DockManager> dockManagerProvider;
    private final Provider<KeyguardStateController> keyguardStateControllerProvider;
    private final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider;
    private final Provider<LockPatternUtils> lockPatternUtilsProvider;
    private final Provider<NavigationModeController> navigationModeControllerProvider;
    private final Provider<NotificationMediaManager> notificationMediaManagerProvider;
    private final Provider<NotificationShadeWindowController> notificationShadeWindowControllerProvider;
    private final Provider<SysuiStatusBarStateController> sysuiStatusBarStateControllerProvider;

    public StatusBarKeyguardViewManager_Factory(Provider<Context> provider, Provider<ViewMediatorCallback> provider2, Provider<LockPatternUtils> provider3, Provider<SysuiStatusBarStateController> provider4, Provider<ConfigurationController> provider5, Provider<KeyguardUpdateMonitor> provider6, Provider<NavigationModeController> provider7, Provider<DockManager> provider8, Provider<NotificationShadeWindowController> provider9, Provider<KeyguardStateController> provider10, Provider<NotificationMediaManager> provider11) {
        this.contextProvider = provider;
        this.callbackProvider = provider2;
        this.lockPatternUtilsProvider = provider3;
        this.sysuiStatusBarStateControllerProvider = provider4;
        this.configurationControllerProvider = provider5;
        this.keyguardUpdateMonitorProvider = provider6;
        this.navigationModeControllerProvider = provider7;
        this.dockManagerProvider = provider8;
        this.notificationShadeWindowControllerProvider = provider9;
        this.keyguardStateControllerProvider = provider10;
        this.notificationMediaManagerProvider = provider11;
    }

    public StatusBarKeyguardViewManager get() {
        return provideInstance(this.contextProvider, this.callbackProvider, this.lockPatternUtilsProvider, this.sysuiStatusBarStateControllerProvider, this.configurationControllerProvider, this.keyguardUpdateMonitorProvider, this.navigationModeControllerProvider, this.dockManagerProvider, this.notificationShadeWindowControllerProvider, this.keyguardStateControllerProvider, this.notificationMediaManagerProvider);
    }

    public static StatusBarKeyguardViewManager provideInstance(Provider<Context> provider, Provider<ViewMediatorCallback> provider2, Provider<LockPatternUtils> provider3, Provider<SysuiStatusBarStateController> provider4, Provider<ConfigurationController> provider5, Provider<KeyguardUpdateMonitor> provider6, Provider<NavigationModeController> provider7, Provider<DockManager> provider8, Provider<NotificationShadeWindowController> provider9, Provider<KeyguardStateController> provider10, Provider<NotificationMediaManager> provider11) {
        StatusBarKeyguardViewManager statusBarKeyguardViewManager = new StatusBarKeyguardViewManager((Context) provider.get(), (ViewMediatorCallback) provider2.get(), (LockPatternUtils) provider3.get(), (SysuiStatusBarStateController) provider4.get(), (ConfigurationController) provider5.get(), (KeyguardUpdateMonitor) provider6.get(), (NavigationModeController) provider7.get(), (DockManager) provider8.get(), (NotificationShadeWindowController) provider9.get(), (KeyguardStateController) provider10.get(), (NotificationMediaManager) provider11.get());
        return statusBarKeyguardViewManager;
    }

    public static StatusBarKeyguardViewManager_Factory create(Provider<Context> provider, Provider<ViewMediatorCallback> provider2, Provider<LockPatternUtils> provider3, Provider<SysuiStatusBarStateController> provider4, Provider<ConfigurationController> provider5, Provider<KeyguardUpdateMonitor> provider6, Provider<NavigationModeController> provider7, Provider<DockManager> provider8, Provider<NotificationShadeWindowController> provider9, Provider<KeyguardStateController> provider10, Provider<NotificationMediaManager> provider11) {
        StatusBarKeyguardViewManager_Factory statusBarKeyguardViewManager_Factory = new StatusBarKeyguardViewManager_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11);
        return statusBarKeyguardViewManager_Factory;
    }
}
