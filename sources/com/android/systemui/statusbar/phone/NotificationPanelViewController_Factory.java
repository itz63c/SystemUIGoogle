package com.android.systemui.statusbar.phone;

import android.app.ActivityManager;
import android.os.PowerManager;
import android.view.accessibility.AccessibilityManager;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.util.LatencyTracker;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.doze.DozeLog;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.FlingAnimationUtils.Builder;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.PulseExpansionHandler;
import com.android.systemui.statusbar.VibratorHelper;
import com.android.systemui.statusbar.notification.DynamicPrivacyController;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.util.InjectionInflationController;
import dagger.internal.Factory;
import javax.inject.Provider;

public final class NotificationPanelViewController_Factory implements Factory<NotificationPanelViewController> {
    private final Provider<AccessibilityManager> accessibilityManagerProvider;
    private final Provider<ActivityManager> activityManagerProvider;
    private final Provider<KeyguardBypassController> bypassControllerProvider;
    private final Provider<CommandQueue> commandQueueProvider;
    private final Provider<ConfigurationController> configurationControllerProvider;
    private final Provider<NotificationWakeUpCoordinator> coordinatorProvider;
    private final Provider<Integer> displayIdProvider;
    private final Provider<DozeLog> dozeLogProvider;
    private final Provider<DozeParameters> dozeParametersProvider;
    private final Provider<DynamicPrivacyController> dynamicPrivacyControllerProvider;
    private final Provider<FalsingManager> falsingManagerProvider;
    private final Provider<Builder> flingAnimationUtilsBuilderProvider;
    private final Provider<InjectionInflationController> injectionInflationControllerProvider;
    private final Provider<KeyguardStateController> keyguardStateControllerProvider;
    private final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider;
    private final Provider<LatencyTracker> latencyTrackerProvider;
    private final Provider<MetricsLogger> metricsLoggerProvider;
    private final Provider<NotificationEntryManager> notificationEntryManagerProvider;
    private final Provider<NotificationLockscreenUserManager> notificationLockscreenUserManagerProvider;
    private final Provider<PowerManager> powerManagerProvider;
    private final Provider<PulseExpansionHandler> pulseExpansionHandlerProvider;
    private final Provider<ShadeController> shadeControllerProvider;
    private final Provider<StatusBarStateController> statusBarStateControllerProvider;
    private final Provider<StatusBarTouchableRegionManager> statusBarTouchableRegionManagerProvider;
    private final Provider<VibratorHelper> vibratorHelperProvider;
    private final Provider<NotificationPanelView> viewProvider;
    private final Provider<ZenModeController> zenModeControllerProvider;

    public NotificationPanelViewController_Factory(Provider<NotificationPanelView> provider, Provider<InjectionInflationController> provider2, Provider<NotificationWakeUpCoordinator> provider3, Provider<PulseExpansionHandler> provider4, Provider<DynamicPrivacyController> provider5, Provider<KeyguardBypassController> provider6, Provider<FalsingManager> provider7, Provider<ShadeController> provider8, Provider<NotificationLockscreenUserManager> provider9, Provider<NotificationEntryManager> provider10, Provider<KeyguardStateController> provider11, Provider<StatusBarStateController> provider12, Provider<DozeLog> provider13, Provider<DozeParameters> provider14, Provider<CommandQueue> provider15, Provider<VibratorHelper> provider16, Provider<LatencyTracker> provider17, Provider<PowerManager> provider18, Provider<AccessibilityManager> provider19, Provider<Integer> provider20, Provider<KeyguardUpdateMonitor> provider21, Provider<MetricsLogger> provider22, Provider<ActivityManager> provider23, Provider<ZenModeController> provider24, Provider<ConfigurationController> provider25, Provider<Builder> provider26, Provider<StatusBarTouchableRegionManager> provider27) {
        this.viewProvider = provider;
        this.injectionInflationControllerProvider = provider2;
        this.coordinatorProvider = provider3;
        this.pulseExpansionHandlerProvider = provider4;
        this.dynamicPrivacyControllerProvider = provider5;
        this.bypassControllerProvider = provider6;
        this.falsingManagerProvider = provider7;
        this.shadeControllerProvider = provider8;
        this.notificationLockscreenUserManagerProvider = provider9;
        this.notificationEntryManagerProvider = provider10;
        this.keyguardStateControllerProvider = provider11;
        this.statusBarStateControllerProvider = provider12;
        this.dozeLogProvider = provider13;
        this.dozeParametersProvider = provider14;
        this.commandQueueProvider = provider15;
        this.vibratorHelperProvider = provider16;
        this.latencyTrackerProvider = provider17;
        this.powerManagerProvider = provider18;
        this.accessibilityManagerProvider = provider19;
        this.displayIdProvider = provider20;
        this.keyguardUpdateMonitorProvider = provider21;
        this.metricsLoggerProvider = provider22;
        this.activityManagerProvider = provider23;
        this.zenModeControllerProvider = provider24;
        this.configurationControllerProvider = provider25;
        this.flingAnimationUtilsBuilderProvider = provider26;
        this.statusBarTouchableRegionManagerProvider = provider27;
    }

    public NotificationPanelViewController get() {
        return provideInstance(this.viewProvider, this.injectionInflationControllerProvider, this.coordinatorProvider, this.pulseExpansionHandlerProvider, this.dynamicPrivacyControllerProvider, this.bypassControllerProvider, this.falsingManagerProvider, this.shadeControllerProvider, this.notificationLockscreenUserManagerProvider, this.notificationEntryManagerProvider, this.keyguardStateControllerProvider, this.statusBarStateControllerProvider, this.dozeLogProvider, this.dozeParametersProvider, this.commandQueueProvider, this.vibratorHelperProvider, this.latencyTrackerProvider, this.powerManagerProvider, this.accessibilityManagerProvider, this.displayIdProvider, this.keyguardUpdateMonitorProvider, this.metricsLoggerProvider, this.activityManagerProvider, this.zenModeControllerProvider, this.configurationControllerProvider, this.flingAnimationUtilsBuilderProvider, this.statusBarTouchableRegionManagerProvider);
    }

    public static NotificationPanelViewController provideInstance(Provider<NotificationPanelView> provider, Provider<InjectionInflationController> provider2, Provider<NotificationWakeUpCoordinator> provider3, Provider<PulseExpansionHandler> provider4, Provider<DynamicPrivacyController> provider5, Provider<KeyguardBypassController> provider6, Provider<FalsingManager> provider7, Provider<ShadeController> provider8, Provider<NotificationLockscreenUserManager> provider9, Provider<NotificationEntryManager> provider10, Provider<KeyguardStateController> provider11, Provider<StatusBarStateController> provider12, Provider<DozeLog> provider13, Provider<DozeParameters> provider14, Provider<CommandQueue> provider15, Provider<VibratorHelper> provider16, Provider<LatencyTracker> provider17, Provider<PowerManager> provider18, Provider<AccessibilityManager> provider19, Provider<Integer> provider20, Provider<KeyguardUpdateMonitor> provider21, Provider<MetricsLogger> provider22, Provider<ActivityManager> provider23, Provider<ZenModeController> provider24, Provider<ConfigurationController> provider25, Provider<Builder> provider26, Provider<StatusBarTouchableRegionManager> provider27) {
        NotificationPanelViewController notificationPanelViewController = new NotificationPanelViewController((NotificationPanelView) provider.get(), (InjectionInflationController) provider2.get(), (NotificationWakeUpCoordinator) provider3.get(), (PulseExpansionHandler) provider4.get(), (DynamicPrivacyController) provider5.get(), (KeyguardBypassController) provider6.get(), (FalsingManager) provider7.get(), (ShadeController) provider8.get(), (NotificationLockscreenUserManager) provider9.get(), (NotificationEntryManager) provider10.get(), (KeyguardStateController) provider11.get(), (StatusBarStateController) provider12.get(), (DozeLog) provider13.get(), (DozeParameters) provider14.get(), (CommandQueue) provider15.get(), (VibratorHelper) provider16.get(), (LatencyTracker) provider17.get(), (PowerManager) provider18.get(), (AccessibilityManager) provider19.get(), ((Integer) provider20.get()).intValue(), (KeyguardUpdateMonitor) provider21.get(), (MetricsLogger) provider22.get(), (ActivityManager) provider23.get(), (ZenModeController) provider24.get(), (ConfigurationController) provider25.get(), (Builder) provider26.get(), (StatusBarTouchableRegionManager) provider27.get());
        return notificationPanelViewController;
    }

    public static NotificationPanelViewController_Factory create(Provider<NotificationPanelView> provider, Provider<InjectionInflationController> provider2, Provider<NotificationWakeUpCoordinator> provider3, Provider<PulseExpansionHandler> provider4, Provider<DynamicPrivacyController> provider5, Provider<KeyguardBypassController> provider6, Provider<FalsingManager> provider7, Provider<ShadeController> provider8, Provider<NotificationLockscreenUserManager> provider9, Provider<NotificationEntryManager> provider10, Provider<KeyguardStateController> provider11, Provider<StatusBarStateController> provider12, Provider<DozeLog> provider13, Provider<DozeParameters> provider14, Provider<CommandQueue> provider15, Provider<VibratorHelper> provider16, Provider<LatencyTracker> provider17, Provider<PowerManager> provider18, Provider<AccessibilityManager> provider19, Provider<Integer> provider20, Provider<KeyguardUpdateMonitor> provider21, Provider<MetricsLogger> provider22, Provider<ActivityManager> provider23, Provider<ZenModeController> provider24, Provider<ConfigurationController> provider25, Provider<Builder> provider26, Provider<StatusBarTouchableRegionManager> provider27) {
        NotificationPanelViewController_Factory notificationPanelViewController_Factory = new NotificationPanelViewController_Factory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11, provider12, provider13, provider14, provider15, provider16, provider17, provider18, provider19, provider20, provider21, provider22, provider23, provider24, provider25, provider26, provider27);
        return notificationPanelViewController_Factory;
    }
}
