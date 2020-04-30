package com.android.systemui.statusbar.phone.dagger;

import android.content.Context;
import android.os.Handler;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import com.android.internal.logging.MetricsLogger;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.ViewMediatorCallback;
import com.android.systemui.InitController;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.bubbles.BubbleController;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.keyguard.DismissCallbackRegistry;
import com.android.systemui.keyguard.KeyguardViewMediator;
import com.android.systemui.keyguard.ScreenLifecycle;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.PluginDependencyProvider;
import com.android.systemui.recents.Recents;
import com.android.systemui.recents.ScreenPinningRequest;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.stackdivider.Divider;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.KeyguardIndicationController;
import com.android.systemui.statusbar.NavigationBarController;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.NotificationViewHierarchyManager;
import com.android.systemui.statusbar.PulseExpansionHandler;
import com.android.systemui.statusbar.SuperStatusBarViewFactory;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.statusbar.VibratorHelper;
import com.android.systemui.statusbar.notification.DynamicPrivacyController;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import com.android.systemui.statusbar.notification.init.NotificationsController;
import com.android.systemui.statusbar.notification.interruption.BypassHeadsUpNotifier;
import com.android.systemui.statusbar.notification.interruption.NotificationAlertingManager;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProvider;
import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import com.android.systemui.statusbar.notification.row.NotificationGutsManager;
import com.android.systemui.statusbar.phone.AutoHideController;
import com.android.systemui.statusbar.phone.BiometricUnlockController;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.statusbar.phone.DozeScrimController;
import com.android.systemui.statusbar.phone.DozeServiceHost;
import com.android.systemui.statusbar.phone.HeadsUpManagerPhone;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.phone.KeyguardDismissUtil;
import com.android.systemui.statusbar.phone.KeyguardLiftController;
import com.android.systemui.statusbar.phone.LightBarController;
import com.android.systemui.statusbar.phone.LightsOutNotifController;
import com.android.systemui.statusbar.phone.LockscreenLockIconController;
import com.android.systemui.statusbar.phone.LockscreenWallpaper;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.phone.NotificationShadeWindowController;
import com.android.systemui.statusbar.phone.PhoneStatusBarPolicy;
import com.android.systemui.statusbar.phone.ScrimController;
import com.android.systemui.statusbar.phone.ShadeController;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager;
import com.android.systemui.statusbar.phone.StatusBarNotificationActivityStarter;
import com.android.systemui.statusbar.phone.StatusBarTouchableRegionManager;
import com.android.systemui.statusbar.phone.dagger.StatusBarComponent.Builder;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.policy.ExtensionController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.statusbar.policy.RemoteInputQuickSettingsDisabler;
import com.android.systemui.statusbar.policy.UserInfoControllerImpl;
import com.android.systemui.statusbar.policy.UserSwitcherController;
import com.android.systemui.volume.VolumeComponent;
import dagger.Lazy;
import dagger.internal.DoubleCheck;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import java.util.Optional;
import java.util.concurrent.Executor;
import javax.inject.Provider;

public final class StatusBarPhoneModule_ProvideStatusBarFactory implements Factory<StatusBar> {
    private final Provider<AssistManager> assistManagerLazyProvider;
    private final Provider<AutoHideController> autoHideControllerProvider;
    private final Provider<BatteryController> batteryControllerProvider;
    private final Provider<BiometricUnlockController> biometricUnlockControllerLazyProvider;
    private final Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private final Provider<BubbleController> bubbleControllerProvider;
    private final Provider<BypassHeadsUpNotifier> bypassHeadsUpNotifierProvider;
    private final Provider<SysuiColorExtractor> colorExtractorProvider;
    private final Provider<CommandQueue> commandQueueProvider;
    private final Provider<ConfigurationController> configurationControllerProvider;
    private final Provider<Context> contextProvider;
    private final Provider<DarkIconDispatcher> darkIconDispatcherProvider;
    private final Provider<DeviceProvisionedController> deviceProvisionedControllerProvider;
    private final Provider<DismissCallbackRegistry> dismissCallbackRegistryProvider;
    private final Provider<DisplayMetrics> displayMetricsProvider;
    private final Provider<Optional<Divider>> dividerOptionalProvider;
    private final Provider<DozeParameters> dozeParametersProvider;
    private final Provider<DozeScrimController> dozeScrimControllerProvider;
    private final Provider<DozeServiceHost> dozeServiceHostProvider;
    private final Provider<DynamicPrivacyController> dynamicPrivacyControllerProvider;
    private final Provider<ExtensionController> extensionControllerProvider;
    private final Provider<FalsingManager> falsingManagerProvider;
    private final Provider<NotificationGroupManager> groupManagerProvider;
    private final Provider<HeadsUpManagerPhone> headsUpManagerPhoneProvider;
    private final Provider<InitController> initControllerProvider;
    private final Provider<KeyguardBypassController> keyguardBypassControllerProvider;
    private final Provider<KeyguardDismissUtil> keyguardDismissUtilProvider;
    private final Provider<KeyguardIndicationController> keyguardIndicationControllerProvider;
    private final Provider<KeyguardLiftController> keyguardLiftControllerProvider;
    private final Provider<KeyguardStateController> keyguardStateControllerProvider;
    private final Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider;
    private final Provider<KeyguardViewMediator> keyguardViewMediatorProvider;
    private final Provider<LightBarController> lightBarControllerProvider;
    private final Provider<LightsOutNotifController> lightsOutNotifControllerProvider;
    private final Provider<NotificationLockscreenUserManager> lockScreenUserManagerProvider;
    private final Provider<LockscreenLockIconController> lockscreenLockIconControllerProvider;
    private final Provider<LockscreenWallpaper> lockscreenWallpaperLazyProvider;
    private final Provider<MetricsLogger> metricsLoggerProvider;
    private final Provider<NavigationBarController> navigationBarControllerProvider;
    private final Provider<NetworkController> networkControllerProvider;
    private final Provider<NotificationAlertingManager> notificationAlertingManagerProvider;
    private final Provider<NotificationGutsManager> notificationGutsManagerProvider;
    private final Provider<NotificationInterruptStateProvider> notificationInterruptStateProvider;
    private final Provider<NotificationLogger> notificationLoggerProvider;
    private final Provider<NotificationMediaManager> notificationMediaManagerProvider;
    private final Provider<NotificationShadeWindowController> notificationShadeWindowControllerProvider;
    private final Provider<NotificationViewHierarchyManager> notificationViewHierarchyManagerProvider;
    private final Provider<NotificationWakeUpCoordinator> notificationWakeUpCoordinatorProvider;
    private final Provider<NotificationsController> notificationsControllerProvider;
    private final Provider<PhoneStatusBarPolicy> phoneStatusBarPolicyProvider;
    private final Provider<PluginDependencyProvider> pluginDependencyProvider;
    private final Provider<PluginManager> pluginManagerProvider;
    private final Provider<PowerManager> powerManagerProvider;
    private final Provider<PulseExpansionHandler> pulseExpansionHandlerProvider;
    private final Provider<Optional<Recents>> recentsOptionalProvider;
    private final Provider<NotificationRemoteInputManager> remoteInputManagerProvider;
    private final Provider<RemoteInputQuickSettingsDisabler> remoteInputQuickSettingsDisablerProvider;
    private final Provider<ScreenLifecycle> screenLifecycleProvider;
    private final Provider<ScreenPinningRequest> screenPinningRequestProvider;
    private final Provider<ScrimController> scrimControllerProvider;
    private final Provider<ShadeController> shadeControllerProvider;
    private final Provider<Builder> statusBarComponentBuilderProvider;
    private final Provider<StatusBarIconController> statusBarIconControllerProvider;
    private final Provider<StatusBarKeyguardViewManager> statusBarKeyguardViewManagerProvider;
    private final Provider<StatusBarNotificationActivityStarter.Builder> statusBarNotificationActivityStarterBuilderProvider;
    private final Provider<SysuiStatusBarStateController> statusBarStateControllerProvider;
    private final Provider<StatusBarTouchableRegionManager> statusBarTouchableRegionManagerProvider;
    private final Provider<SuperStatusBarViewFactory> superStatusBarViewFactoryProvider;
    private final Provider<Handler> timeTickHandlerProvider;
    private final Provider<Executor> uiBgExecutorProvider;
    private final Provider<UserInfoControllerImpl> userInfoControllerImplProvider;
    private final Provider<UserSwitcherController> userSwitcherControllerProvider;
    private final Provider<VibratorHelper> vibratorHelperProvider;
    private final Provider<ViewMediatorCallback> viewMediatorCallbackProvider;
    private final Provider<VisualStabilityManager> visualStabilityManagerProvider;
    private final Provider<VolumeComponent> volumeComponentProvider;
    private final Provider<WakefulnessLifecycle> wakefulnessLifecycleProvider;

    public StatusBarPhoneModule_ProvideStatusBarFactory(Provider<Context> provider, Provider<NotificationsController> provider2, Provider<LightBarController> provider3, Provider<AutoHideController> provider4, Provider<KeyguardUpdateMonitor> provider5, Provider<StatusBarIconController> provider6, Provider<PulseExpansionHandler> provider7, Provider<NotificationWakeUpCoordinator> provider8, Provider<KeyguardBypassController> provider9, Provider<KeyguardStateController> provider10, Provider<HeadsUpManagerPhone> provider11, Provider<DynamicPrivacyController> provider12, Provider<BypassHeadsUpNotifier> provider13, Provider<FalsingManager> provider14, Provider<BroadcastDispatcher> provider15, Provider<RemoteInputQuickSettingsDisabler> provider16, Provider<NotificationGutsManager> provider17, Provider<NotificationLogger> provider18, Provider<NotificationInterruptStateProvider> provider19, Provider<NotificationViewHierarchyManager> provider20, Provider<KeyguardViewMediator> provider21, Provider<NotificationAlertingManager> provider22, Provider<DisplayMetrics> provider23, Provider<MetricsLogger> provider24, Provider<Executor> provider25, Provider<NotificationMediaManager> provider26, Provider<NotificationLockscreenUserManager> provider27, Provider<NotificationRemoteInputManager> provider28, Provider<UserSwitcherController> provider29, Provider<NetworkController> provider30, Provider<BatteryController> provider31, Provider<SysuiColorExtractor> provider32, Provider<ScreenLifecycle> provider33, Provider<WakefulnessLifecycle> provider34, Provider<SysuiStatusBarStateController> provider35, Provider<VibratorHelper> provider36, Provider<BubbleController> provider37, Provider<NotificationGroupManager> provider38, Provider<VisualStabilityManager> provider39, Provider<DeviceProvisionedController> provider40, Provider<NavigationBarController> provider41, Provider<AssistManager> provider42, Provider<ConfigurationController> provider43, Provider<NotificationShadeWindowController> provider44, Provider<LockscreenLockIconController> provider45, Provider<DozeParameters> provider46, Provider<ScrimController> provider47, Provider<KeyguardLiftController> provider48, Provider<LockscreenWallpaper> provider49, Provider<BiometricUnlockController> provider50, Provider<DozeServiceHost> provider51, Provider<PowerManager> provider52, Provider<ScreenPinningRequest> provider53, Provider<DozeScrimController> provider54, Provider<VolumeComponent> provider55, Provider<CommandQueue> provider56, Provider<Optional<Recents>> provider57, Provider<Builder> provider58, Provider<PluginManager> provider59, Provider<Optional<Divider>> provider60, Provider<LightsOutNotifController> provider61, Provider<StatusBarNotificationActivityStarter.Builder> provider62, Provider<ShadeController> provider63, Provider<SuperStatusBarViewFactory> provider64, Provider<StatusBarKeyguardViewManager> provider65, Provider<ViewMediatorCallback> provider66, Provider<InitController> provider67, Provider<DarkIconDispatcher> provider68, Provider<Handler> provider69, Provider<PluginDependencyProvider> provider70, Provider<KeyguardDismissUtil> provider71, Provider<ExtensionController> provider72, Provider<UserInfoControllerImpl> provider73, Provider<PhoneStatusBarPolicy> provider74, Provider<KeyguardIndicationController> provider75, Provider<DismissCallbackRegistry> provider76, Provider<StatusBarTouchableRegionManager> provider77) {
        this.contextProvider = provider;
        this.notificationsControllerProvider = provider2;
        this.lightBarControllerProvider = provider3;
        this.autoHideControllerProvider = provider4;
        this.keyguardUpdateMonitorProvider = provider5;
        this.statusBarIconControllerProvider = provider6;
        this.pulseExpansionHandlerProvider = provider7;
        this.notificationWakeUpCoordinatorProvider = provider8;
        this.keyguardBypassControllerProvider = provider9;
        this.keyguardStateControllerProvider = provider10;
        this.headsUpManagerPhoneProvider = provider11;
        this.dynamicPrivacyControllerProvider = provider12;
        this.bypassHeadsUpNotifierProvider = provider13;
        this.falsingManagerProvider = provider14;
        this.broadcastDispatcherProvider = provider15;
        this.remoteInputQuickSettingsDisablerProvider = provider16;
        this.notificationGutsManagerProvider = provider17;
        this.notificationLoggerProvider = provider18;
        this.notificationInterruptStateProvider = provider19;
        this.notificationViewHierarchyManagerProvider = provider20;
        this.keyguardViewMediatorProvider = provider21;
        this.notificationAlertingManagerProvider = provider22;
        this.displayMetricsProvider = provider23;
        this.metricsLoggerProvider = provider24;
        this.uiBgExecutorProvider = provider25;
        this.notificationMediaManagerProvider = provider26;
        this.lockScreenUserManagerProvider = provider27;
        this.remoteInputManagerProvider = provider28;
        this.userSwitcherControllerProvider = provider29;
        this.networkControllerProvider = provider30;
        this.batteryControllerProvider = provider31;
        this.colorExtractorProvider = provider32;
        this.screenLifecycleProvider = provider33;
        this.wakefulnessLifecycleProvider = provider34;
        this.statusBarStateControllerProvider = provider35;
        this.vibratorHelperProvider = provider36;
        this.bubbleControllerProvider = provider37;
        this.groupManagerProvider = provider38;
        this.visualStabilityManagerProvider = provider39;
        this.deviceProvisionedControllerProvider = provider40;
        this.navigationBarControllerProvider = provider41;
        this.assistManagerLazyProvider = provider42;
        this.configurationControllerProvider = provider43;
        this.notificationShadeWindowControllerProvider = provider44;
        this.lockscreenLockIconControllerProvider = provider45;
        this.dozeParametersProvider = provider46;
        this.scrimControllerProvider = provider47;
        this.keyguardLiftControllerProvider = provider48;
        this.lockscreenWallpaperLazyProvider = provider49;
        this.biometricUnlockControllerLazyProvider = provider50;
        this.dozeServiceHostProvider = provider51;
        this.powerManagerProvider = provider52;
        this.screenPinningRequestProvider = provider53;
        this.dozeScrimControllerProvider = provider54;
        this.volumeComponentProvider = provider55;
        this.commandQueueProvider = provider56;
        this.recentsOptionalProvider = provider57;
        this.statusBarComponentBuilderProvider = provider58;
        this.pluginManagerProvider = provider59;
        this.dividerOptionalProvider = provider60;
        this.lightsOutNotifControllerProvider = provider61;
        this.statusBarNotificationActivityStarterBuilderProvider = provider62;
        this.shadeControllerProvider = provider63;
        this.superStatusBarViewFactoryProvider = provider64;
        this.statusBarKeyguardViewManagerProvider = provider65;
        this.viewMediatorCallbackProvider = provider66;
        this.initControllerProvider = provider67;
        this.darkIconDispatcherProvider = provider68;
        this.timeTickHandlerProvider = provider69;
        this.pluginDependencyProvider = provider70;
        this.keyguardDismissUtilProvider = provider71;
        this.extensionControllerProvider = provider72;
        this.userInfoControllerImplProvider = provider73;
        this.phoneStatusBarPolicyProvider = provider74;
        this.keyguardIndicationControllerProvider = provider75;
        this.dismissCallbackRegistryProvider = provider76;
        this.statusBarTouchableRegionManagerProvider = provider77;
    }

    public StatusBar get() {
        return provideInstance(this.contextProvider, this.notificationsControllerProvider, this.lightBarControllerProvider, this.autoHideControllerProvider, this.keyguardUpdateMonitorProvider, this.statusBarIconControllerProvider, this.pulseExpansionHandlerProvider, this.notificationWakeUpCoordinatorProvider, this.keyguardBypassControllerProvider, this.keyguardStateControllerProvider, this.headsUpManagerPhoneProvider, this.dynamicPrivacyControllerProvider, this.bypassHeadsUpNotifierProvider, this.falsingManagerProvider, this.broadcastDispatcherProvider, this.remoteInputQuickSettingsDisablerProvider, this.notificationGutsManagerProvider, this.notificationLoggerProvider, this.notificationInterruptStateProvider, this.notificationViewHierarchyManagerProvider, this.keyguardViewMediatorProvider, this.notificationAlertingManagerProvider, this.displayMetricsProvider, this.metricsLoggerProvider, this.uiBgExecutorProvider, this.notificationMediaManagerProvider, this.lockScreenUserManagerProvider, this.remoteInputManagerProvider, this.userSwitcherControllerProvider, this.networkControllerProvider, this.batteryControllerProvider, this.colorExtractorProvider, this.screenLifecycleProvider, this.wakefulnessLifecycleProvider, this.statusBarStateControllerProvider, this.vibratorHelperProvider, this.bubbleControllerProvider, this.groupManagerProvider, this.visualStabilityManagerProvider, this.deviceProvisionedControllerProvider, this.navigationBarControllerProvider, this.assistManagerLazyProvider, this.configurationControllerProvider, this.notificationShadeWindowControllerProvider, this.lockscreenLockIconControllerProvider, this.dozeParametersProvider, this.scrimControllerProvider, this.keyguardLiftControllerProvider, this.lockscreenWallpaperLazyProvider, this.biometricUnlockControllerLazyProvider, this.dozeServiceHostProvider, this.powerManagerProvider, this.screenPinningRequestProvider, this.dozeScrimControllerProvider, this.volumeComponentProvider, this.commandQueueProvider, this.recentsOptionalProvider, this.statusBarComponentBuilderProvider, this.pluginManagerProvider, this.dividerOptionalProvider, this.lightsOutNotifControllerProvider, this.statusBarNotificationActivityStarterBuilderProvider, this.shadeControllerProvider, this.superStatusBarViewFactoryProvider, this.statusBarKeyguardViewManagerProvider, this.viewMediatorCallbackProvider, this.initControllerProvider, this.darkIconDispatcherProvider, this.timeTickHandlerProvider, this.pluginDependencyProvider, this.keyguardDismissUtilProvider, this.extensionControllerProvider, this.userInfoControllerImplProvider, this.phoneStatusBarPolicyProvider, this.keyguardIndicationControllerProvider, this.dismissCallbackRegistryProvider, this.statusBarTouchableRegionManagerProvider);
    }

    public static StatusBar provideInstance(Provider<Context> provider, Provider<NotificationsController> provider2, Provider<LightBarController> provider3, Provider<AutoHideController> provider4, Provider<KeyguardUpdateMonitor> provider5, Provider<StatusBarIconController> provider6, Provider<PulseExpansionHandler> provider7, Provider<NotificationWakeUpCoordinator> provider8, Provider<KeyguardBypassController> provider9, Provider<KeyguardStateController> provider10, Provider<HeadsUpManagerPhone> provider11, Provider<DynamicPrivacyController> provider12, Provider<BypassHeadsUpNotifier> provider13, Provider<FalsingManager> provider14, Provider<BroadcastDispatcher> provider15, Provider<RemoteInputQuickSettingsDisabler> provider16, Provider<NotificationGutsManager> provider17, Provider<NotificationLogger> provider18, Provider<NotificationInterruptStateProvider> provider19, Provider<NotificationViewHierarchyManager> provider20, Provider<KeyguardViewMediator> provider21, Provider<NotificationAlertingManager> provider22, Provider<DisplayMetrics> provider23, Provider<MetricsLogger> provider24, Provider<Executor> provider25, Provider<NotificationMediaManager> provider26, Provider<NotificationLockscreenUserManager> provider27, Provider<NotificationRemoteInputManager> provider28, Provider<UserSwitcherController> provider29, Provider<NetworkController> provider30, Provider<BatteryController> provider31, Provider<SysuiColorExtractor> provider32, Provider<ScreenLifecycle> provider33, Provider<WakefulnessLifecycle> provider34, Provider<SysuiStatusBarStateController> provider35, Provider<VibratorHelper> provider36, Provider<BubbleController> provider37, Provider<NotificationGroupManager> provider38, Provider<VisualStabilityManager> provider39, Provider<DeviceProvisionedController> provider40, Provider<NavigationBarController> provider41, Provider<AssistManager> provider42, Provider<ConfigurationController> provider43, Provider<NotificationShadeWindowController> provider44, Provider<LockscreenLockIconController> provider45, Provider<DozeParameters> provider46, Provider<ScrimController> provider47, Provider<KeyguardLiftController> provider48, Provider<LockscreenWallpaper> provider49, Provider<BiometricUnlockController> provider50, Provider<DozeServiceHost> provider51, Provider<PowerManager> provider52, Provider<ScreenPinningRequest> provider53, Provider<DozeScrimController> provider54, Provider<VolumeComponent> provider55, Provider<CommandQueue> provider56, Provider<Optional<Recents>> provider57, Provider<Builder> provider58, Provider<PluginManager> provider59, Provider<Optional<Divider>> provider60, Provider<LightsOutNotifController> provider61, Provider<StatusBarNotificationActivityStarter.Builder> provider62, Provider<ShadeController> provider63, Provider<SuperStatusBarViewFactory> provider64, Provider<StatusBarKeyguardViewManager> provider65, Provider<ViewMediatorCallback> provider66, Provider<InitController> provider67, Provider<DarkIconDispatcher> provider68, Provider<Handler> provider69, Provider<PluginDependencyProvider> provider70, Provider<KeyguardDismissUtil> provider71, Provider<ExtensionController> provider72, Provider<UserInfoControllerImpl> provider73, Provider<PhoneStatusBarPolicy> provider74, Provider<KeyguardIndicationController> provider75, Provider<DismissCallbackRegistry> provider76, Provider<StatusBarTouchableRegionManager> provider77) {
        return proxyProvideStatusBar((Context) provider.get(), (NotificationsController) provider2.get(), (LightBarController) provider3.get(), (AutoHideController) provider4.get(), (KeyguardUpdateMonitor) provider5.get(), (StatusBarIconController) provider6.get(), (PulseExpansionHandler) provider7.get(), (NotificationWakeUpCoordinator) provider8.get(), (KeyguardBypassController) provider9.get(), (KeyguardStateController) provider10.get(), (HeadsUpManagerPhone) provider11.get(), (DynamicPrivacyController) provider12.get(), (BypassHeadsUpNotifier) provider13.get(), (FalsingManager) provider14.get(), (BroadcastDispatcher) provider15.get(), (RemoteInputQuickSettingsDisabler) provider16.get(), (NotificationGutsManager) provider17.get(), (NotificationLogger) provider18.get(), (NotificationInterruptStateProvider) provider19.get(), (NotificationViewHierarchyManager) provider20.get(), (KeyguardViewMediator) provider21.get(), (NotificationAlertingManager) provider22.get(), (DisplayMetrics) provider23.get(), (MetricsLogger) provider24.get(), (Executor) provider25.get(), (NotificationMediaManager) provider26.get(), (NotificationLockscreenUserManager) provider27.get(), (NotificationRemoteInputManager) provider28.get(), (UserSwitcherController) provider29.get(), (NetworkController) provider30.get(), (BatteryController) provider31.get(), (SysuiColorExtractor) provider32.get(), (ScreenLifecycle) provider33.get(), (WakefulnessLifecycle) provider34.get(), (SysuiStatusBarStateController) provider35.get(), (VibratorHelper) provider36.get(), (BubbleController) provider37.get(), (NotificationGroupManager) provider38.get(), (VisualStabilityManager) provider39.get(), (DeviceProvisionedController) provider40.get(), (NavigationBarController) provider41.get(), DoubleCheck.lazy(provider42), (ConfigurationController) provider43.get(), (NotificationShadeWindowController) provider44.get(), (LockscreenLockIconController) provider45.get(), (DozeParameters) provider46.get(), (ScrimController) provider47.get(), (KeyguardLiftController) provider48.get(), DoubleCheck.lazy(provider49), DoubleCheck.lazy(provider50), (DozeServiceHost) provider51.get(), (PowerManager) provider52.get(), (ScreenPinningRequest) provider53.get(), (DozeScrimController) provider54.get(), (VolumeComponent) provider55.get(), (CommandQueue) provider56.get(), (Optional) provider57.get(), provider58, (PluginManager) provider59.get(), (Optional) provider60.get(), (LightsOutNotifController) provider61.get(), (StatusBarNotificationActivityStarter.Builder) provider62.get(), (ShadeController) provider63.get(), (SuperStatusBarViewFactory) provider64.get(), (StatusBarKeyguardViewManager) provider65.get(), (ViewMediatorCallback) provider66.get(), (InitController) provider67.get(), (DarkIconDispatcher) provider68.get(), (Handler) provider69.get(), (PluginDependencyProvider) provider70.get(), (KeyguardDismissUtil) provider71.get(), (ExtensionController) provider72.get(), (UserInfoControllerImpl) provider73.get(), (PhoneStatusBarPolicy) provider74.get(), (KeyguardIndicationController) provider75.get(), (DismissCallbackRegistry) provider76.get(), (StatusBarTouchableRegionManager) provider77.get());
    }

    public static StatusBarPhoneModule_ProvideStatusBarFactory create(Provider<Context> provider, Provider<NotificationsController> provider2, Provider<LightBarController> provider3, Provider<AutoHideController> provider4, Provider<KeyguardUpdateMonitor> provider5, Provider<StatusBarIconController> provider6, Provider<PulseExpansionHandler> provider7, Provider<NotificationWakeUpCoordinator> provider8, Provider<KeyguardBypassController> provider9, Provider<KeyguardStateController> provider10, Provider<HeadsUpManagerPhone> provider11, Provider<DynamicPrivacyController> provider12, Provider<BypassHeadsUpNotifier> provider13, Provider<FalsingManager> provider14, Provider<BroadcastDispatcher> provider15, Provider<RemoteInputQuickSettingsDisabler> provider16, Provider<NotificationGutsManager> provider17, Provider<NotificationLogger> provider18, Provider<NotificationInterruptStateProvider> provider19, Provider<NotificationViewHierarchyManager> provider20, Provider<KeyguardViewMediator> provider21, Provider<NotificationAlertingManager> provider22, Provider<DisplayMetrics> provider23, Provider<MetricsLogger> provider24, Provider<Executor> provider25, Provider<NotificationMediaManager> provider26, Provider<NotificationLockscreenUserManager> provider27, Provider<NotificationRemoteInputManager> provider28, Provider<UserSwitcherController> provider29, Provider<NetworkController> provider30, Provider<BatteryController> provider31, Provider<SysuiColorExtractor> provider32, Provider<ScreenLifecycle> provider33, Provider<WakefulnessLifecycle> provider34, Provider<SysuiStatusBarStateController> provider35, Provider<VibratorHelper> provider36, Provider<BubbleController> provider37, Provider<NotificationGroupManager> provider38, Provider<VisualStabilityManager> provider39, Provider<DeviceProvisionedController> provider40, Provider<NavigationBarController> provider41, Provider<AssistManager> provider42, Provider<ConfigurationController> provider43, Provider<NotificationShadeWindowController> provider44, Provider<LockscreenLockIconController> provider45, Provider<DozeParameters> provider46, Provider<ScrimController> provider47, Provider<KeyguardLiftController> provider48, Provider<LockscreenWallpaper> provider49, Provider<BiometricUnlockController> provider50, Provider<DozeServiceHost> provider51, Provider<PowerManager> provider52, Provider<ScreenPinningRequest> provider53, Provider<DozeScrimController> provider54, Provider<VolumeComponent> provider55, Provider<CommandQueue> provider56, Provider<Optional<Recents>> provider57, Provider<Builder> provider58, Provider<PluginManager> provider59, Provider<Optional<Divider>> provider60, Provider<LightsOutNotifController> provider61, Provider<StatusBarNotificationActivityStarter.Builder> provider62, Provider<ShadeController> provider63, Provider<SuperStatusBarViewFactory> provider64, Provider<StatusBarKeyguardViewManager> provider65, Provider<ViewMediatorCallback> provider66, Provider<InitController> provider67, Provider<DarkIconDispatcher> provider68, Provider<Handler> provider69, Provider<PluginDependencyProvider> provider70, Provider<KeyguardDismissUtil> provider71, Provider<ExtensionController> provider72, Provider<UserInfoControllerImpl> provider73, Provider<PhoneStatusBarPolicy> provider74, Provider<KeyguardIndicationController> provider75, Provider<DismissCallbackRegistry> provider76, Provider<StatusBarTouchableRegionManager> provider77) {
        StatusBarPhoneModule_ProvideStatusBarFactory statusBarPhoneModule_ProvideStatusBarFactory = new StatusBarPhoneModule_ProvideStatusBarFactory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11, provider12, provider13, provider14, provider15, provider16, provider17, provider18, provider19, provider20, provider21, provider22, provider23, provider24, provider25, provider26, provider27, provider28, provider29, provider30, provider31, provider32, provider33, provider34, provider35, provider36, provider37, provider38, provider39, provider40, provider41, provider42, provider43, provider44, provider45, provider46, provider47, provider48, provider49, provider50, provider51, provider52, provider53, provider54, provider55, provider56, provider57, provider58, provider59, provider60, provider61, provider62, provider63, provider64, provider65, provider66, provider67, provider68, provider69, provider70, provider71, provider72, provider73, provider74, provider75, provider76, provider77);
        return statusBarPhoneModule_ProvideStatusBarFactory;
    }

    public static StatusBar proxyProvideStatusBar(Context context, NotificationsController notificationsController, LightBarController lightBarController, AutoHideController autoHideController, KeyguardUpdateMonitor keyguardUpdateMonitor, StatusBarIconController statusBarIconController, PulseExpansionHandler pulseExpansionHandler, NotificationWakeUpCoordinator notificationWakeUpCoordinator, KeyguardBypassController keyguardBypassController, KeyguardStateController keyguardStateController, HeadsUpManagerPhone headsUpManagerPhone, DynamicPrivacyController dynamicPrivacyController, BypassHeadsUpNotifier bypassHeadsUpNotifier, FalsingManager falsingManager, BroadcastDispatcher broadcastDispatcher, RemoteInputQuickSettingsDisabler remoteInputQuickSettingsDisabler, NotificationGutsManager notificationGutsManager, NotificationLogger notificationLogger, NotificationInterruptStateProvider notificationInterruptStateProvider2, NotificationViewHierarchyManager notificationViewHierarchyManager, KeyguardViewMediator keyguardViewMediator, NotificationAlertingManager notificationAlertingManager, DisplayMetrics displayMetrics, MetricsLogger metricsLogger, Executor executor, NotificationMediaManager notificationMediaManager, NotificationLockscreenUserManager notificationLockscreenUserManager, NotificationRemoteInputManager notificationRemoteInputManager, UserSwitcherController userSwitcherController, NetworkController networkController, BatteryController batteryController, SysuiColorExtractor sysuiColorExtractor, ScreenLifecycle screenLifecycle, WakefulnessLifecycle wakefulnessLifecycle, SysuiStatusBarStateController sysuiStatusBarStateController, VibratorHelper vibratorHelper, BubbleController bubbleController, NotificationGroupManager notificationGroupManager, VisualStabilityManager visualStabilityManager, DeviceProvisionedController deviceProvisionedController, NavigationBarController navigationBarController, Lazy<AssistManager> lazy, ConfigurationController configurationController, NotificationShadeWindowController notificationShadeWindowController, LockscreenLockIconController lockscreenLockIconController, DozeParameters dozeParameters, ScrimController scrimController, KeyguardLiftController keyguardLiftController, Lazy<LockscreenWallpaper> lazy2, Lazy<BiometricUnlockController> lazy3, DozeServiceHost dozeServiceHost, PowerManager powerManager, ScreenPinningRequest screenPinningRequest, DozeScrimController dozeScrimController, VolumeComponent volumeComponent, CommandQueue commandQueue, Optional<Recents> optional, Provider<Builder> provider, PluginManager pluginManager, Optional<Divider> optional2, LightsOutNotifController lightsOutNotifController, StatusBarNotificationActivityStarter.Builder builder, ShadeController shadeController, SuperStatusBarViewFactory superStatusBarViewFactory, StatusBarKeyguardViewManager statusBarKeyguardViewManager, ViewMediatorCallback viewMediatorCallback, InitController initController, DarkIconDispatcher darkIconDispatcher, Handler handler, PluginDependencyProvider pluginDependencyProvider2, KeyguardDismissUtil keyguardDismissUtil, ExtensionController extensionController, UserInfoControllerImpl userInfoControllerImpl, PhoneStatusBarPolicy phoneStatusBarPolicy, KeyguardIndicationController keyguardIndicationController, DismissCallbackRegistry dismissCallbackRegistry, StatusBarTouchableRegionManager statusBarTouchableRegionManager) {
        StatusBar provideStatusBar = StatusBarPhoneModule.provideStatusBar(context, notificationsController, lightBarController, autoHideController, keyguardUpdateMonitor, statusBarIconController, pulseExpansionHandler, notificationWakeUpCoordinator, keyguardBypassController, keyguardStateController, headsUpManagerPhone, dynamicPrivacyController, bypassHeadsUpNotifier, falsingManager, broadcastDispatcher, remoteInputQuickSettingsDisabler, notificationGutsManager, notificationLogger, notificationInterruptStateProvider2, notificationViewHierarchyManager, keyguardViewMediator, notificationAlertingManager, displayMetrics, metricsLogger, executor, notificationMediaManager, notificationLockscreenUserManager, notificationRemoteInputManager, userSwitcherController, networkController, batteryController, sysuiColorExtractor, screenLifecycle, wakefulnessLifecycle, sysuiStatusBarStateController, vibratorHelper, bubbleController, notificationGroupManager, visualStabilityManager, deviceProvisionedController, navigationBarController, lazy, configurationController, notificationShadeWindowController, lockscreenLockIconController, dozeParameters, scrimController, keyguardLiftController, lazy2, lazy3, dozeServiceHost, powerManager, screenPinningRequest, dozeScrimController, volumeComponent, commandQueue, optional, provider, pluginManager, optional2, lightsOutNotifController, builder, shadeController, superStatusBarViewFactory, statusBarKeyguardViewManager, viewMediatorCallback, initController, darkIconDispatcher, handler, pluginDependencyProvider2, keyguardDismissUtil, extensionController, userInfoControllerImpl, phoneStatusBarPolicy, keyguardIndicationController, dismissCallbackRegistry, statusBarTouchableRegionManager);
        Preconditions.checkNotNull(provideStatusBar, "Cannot return null from a non-@Nullable @Provides method");
        return provideStatusBar;
    }
}
