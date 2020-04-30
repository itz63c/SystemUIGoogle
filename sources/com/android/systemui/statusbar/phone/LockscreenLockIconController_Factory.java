package com.android.systemui.statusbar.phone;

import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.dock.DockManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.KeyguardIndicationController;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator;
import com.android.systemui.statusbar.policy.AccessibilityController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class LockscreenLockIconController_Factory implements Factory<LockscreenLockIconController> {
    private final Provider<AccessibilityController> accessibilityControllerProvider;
    private final Provider<ConfigurationController> configurationControllerProvider;
    private final Provider<DockManager> dockManagerProvider;
    private final Provider<KeyguardBypassController> keyguardBypassControllerProvider;
    private final Provider<KeyguardIndicationController> keyguardIndicationControllerProvider;
    private final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider;
    private final Provider<LockPatternUtils> lockPatternUtilsProvider;
    private final Provider<LockscreenGestureLogger> lockscreenGestureLoggerProvider;
    private final Provider<NotificationWakeUpCoordinator> notificationWakeUpCoordinatorProvider;
    private final Provider<ShadeController> shadeControllerProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;

    public LockscreenLockIconController_Factory(Provider<LockscreenGestureLogger> provider, Provider<KeyguardUpdateMonitor> provider2, Provider<LockPatternUtils> provider3, Provider<ShadeController> provider4, Provider<AccessibilityController> provider5, Provider<KeyguardIndicationController> provider6, Provider<StatusBarStateController> provider7, Provider<ConfigurationController> provider8, Provider<NotificationWakeUpCoordinator> provider9, Provider<KeyguardBypassController> provider10, Provider<DockManager> provider11) {
        this.lockscreenGestureLoggerProvider = provider;
        this.keyguardUpdateMonitorProvider = provider2;
        this.lockPatternUtilsProvider = provider3;
        this.shadeControllerProvider = provider4;
        this.accessibilityControllerProvider = provider5;
        this.keyguardIndicationControllerProvider = provider6;
        this.statusBarStateControllerProvider = provider7;
        this.configurationControllerProvider = provider8;
        this.notificationWakeUpCoordinatorProvider = provider9;
        this.keyguardBypassControllerProvider = provider10;
        this.dockManagerProvider = provider11;
    }

    public LockscreenLockIconController get() {
        return provideInstance(this.lockscreenGestureLoggerProvider, this.keyguardUpdateMonitorProvider, this.lockPatternUtilsProvider, this.shadeControllerProvider, this.accessibilityControllerProvider, this.keyguardIndicationControllerProvider, this.statusBarStateControllerProvider, this.configurationControllerProvider, this.notificationWakeUpCoordinatorProvider, this.keyguardBypassControllerProvider, this.dockManagerProvider);
    }

    public static LockscreenLockIconController provideInstance(Provider<LockscreenGestureLogger> provider, Provider<KeyguardUpdateMonitor> provider2, Provider<LockPatternUtils> provider3, Provider<ShadeController> provider4, Provider<AccessibilityController> provider5, Provider<KeyguardIndicationController> provider6, Provider<StatusBarStateController> provider7, Provider<ConfigurationController> provider8, Provider<NotificationWakeUpCoordinator> provider9, Provider<KeyguardBypassController> provider10, Provider<DockManager> provider11) {
        LockscreenLockIconController lockscreenLockIconController = new LockscreenLockIconController((LockscreenGestureLogger) provider.get(), (KeyguardUpdateMonitor) provider2.get(), (LockPatternUtils) provider3.get(), (ShadeController) provider4.get(), (AccessibilityController) provider5.get(), (KeyguardIndicationController) provider6.get(), (StatusBarStateController) provider7.get(), (ConfigurationController) provider8.get(), (NotificationWakeUpCoordinator) provider9.get(), (KeyguardBypassController) provider10.get(), (DockManager) provider11.get());
        return lockscreenLockIconController;
    }

    public static LockscreenLockIconController_Factory create(Provider<LockscreenGestureLogger> provider, Provider<KeyguardUpdateMonitor> provider2, Provider<LockPatternUtils> provider3, Provider<ShadeController> provider4, Provider<AccessibilityController> provider5, Provider<KeyguardIndicationController> provider6, Provider<StatusBarStateController> provider7, Provider<ConfigurationController> provider8, Provider<NotificationWakeUpCoordinator> provider9, Provider<KeyguardBypassController> provider10, Provider<DockManager> provider11) {
        LockscreenLockIconController_Factory lockscreenLockIconController_Factory = new LockscreenLockIconController_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11);
        return lockscreenLockIconController_Factory;
    }
}
