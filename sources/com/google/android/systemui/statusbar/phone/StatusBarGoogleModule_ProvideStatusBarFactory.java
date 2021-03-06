package com.google.android.systemui.statusbar.phone;

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
import com.android.systemui.statusbar.phone.ShadeController;
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
import com.google.android.systemui.LiveWallpaperScrimController;
import com.google.android.systemui.smartspace.SmartSpaceController;
import dagger.Lazy;
import dagger.internal.DoubleCheck;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import java.util.Optional;
import java.util.concurrent.Executor;
import javax.inject.Provider;

public final class StatusBarGoogleModule_ProvideStatusBarFactory implements Factory<StatusBarGoogle> {
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
    private final Provider<NotificationInterruptStateProvider> notificationInterruptionStateProvider;
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
    private final Provider<Optional<Recents>> recentsProvider;
    private final Provider<NotificationRemoteInputManager> remoteInputManagerProvider;
    private final Provider<RemoteInputQuickSettingsDisabler> remoteInputQuickSettingsDisablerProvider;
    private final Provider<ScreenLifecycle> screenLifecycleProvider;
    private final Provider<ScreenPinningRequest> screenPinningRequestProvider;
    private final Provider<LiveWallpaperScrimController> scrimControllerProvider;
    private final Provider<ShadeController> shadeControllerProvider;
    private final Provider<SmartSpaceController> smartSpaceControllerProvider;
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
    private final Provider<WallpaperNotifier> wallpaperNotifierProvider;

    public StatusBarGoogleModule_ProvideStatusBarFactory(Provider<SmartSpaceController> provider, Provider<WallpaperNotifier> provider2, Provider<Context> provider3, Provider<NotificationsController> provider4, Provider<LightBarController> provider5, Provider<AutoHideController> provider6, Provider<KeyguardUpdateMonitor> provider7, Provider<StatusBarIconController> provider8, Provider<PulseExpansionHandler> provider9, Provider<NotificationWakeUpCoordinator> provider10, Provider<KeyguardBypassController> provider11, Provider<KeyguardStateController> provider12, Provider<HeadsUpManagerPhone> provider13, Provider<DynamicPrivacyController> provider14, Provider<BypassHeadsUpNotifier> provider15, Provider<FalsingManager> provider16, Provider<BroadcastDispatcher> provider17, Provider<RemoteInputQuickSettingsDisabler> provider18, Provider<NotificationGutsManager> provider19, Provider<NotificationLogger> provider20, Provider<NotificationInterruptStateProvider> provider21, Provider<NotificationViewHierarchyManager> provider22, Provider<KeyguardViewMediator> provider23, Provider<NotificationAlertingManager> provider24, Provider<DisplayMetrics> provider25, Provider<MetricsLogger> provider26, Provider<Executor> provider27, Provider<NotificationMediaManager> provider28, Provider<NotificationLockscreenUserManager> provider29, Provider<NotificationRemoteInputManager> provider30, Provider<UserSwitcherController> provider31, Provider<NetworkController> provider32, Provider<BatteryController> provider33, Provider<SysuiColorExtractor> provider34, Provider<ScreenLifecycle> provider35, Provider<WakefulnessLifecycle> provider36, Provider<SysuiStatusBarStateController> provider37, Provider<VibratorHelper> provider38, Provider<BubbleController> provider39, Provider<NotificationGroupManager> provider40, Provider<VisualStabilityManager> provider41, Provider<DeviceProvisionedController> provider42, Provider<NavigationBarController> provider43, Provider<AssistManager> provider44, Provider<ConfigurationController> provider45, Provider<NotificationShadeWindowController> provider46, Provider<LockscreenLockIconController> provider47, Provider<DozeParameters> provider48, Provider<LiveWallpaperScrimController> provider49, Provider<KeyguardLiftController> provider50, Provider<LockscreenWallpaper> provider51, Provider<BiometricUnlockController> provider52, Provider<DozeServiceHost> provider53, Provider<PowerManager> provider54, Provider<ScreenPinningRequest> provider55, Provider<DozeScrimController> provider56, Provider<VolumeComponent> provider57, Provider<CommandQueue> provider58, Provider<Optional<Recents>> provider59, Provider<Builder> provider60, Provider<PluginManager> provider61, Provider<Optional<Divider>> provider62, Provider<LightsOutNotifController> provider63, Provider<StatusBarNotificationActivityStarter.Builder> provider64, Provider<ShadeController> provider65, Provider<SuperStatusBarViewFactory> provider66, Provider<StatusBarKeyguardViewManager> provider67, Provider<ViewMediatorCallback> provider68, Provider<InitController> provider69, Provider<DarkIconDispatcher> provider70, Provider<Handler> provider71, Provider<PluginDependencyProvider> provider72, Provider<KeyguardDismissUtil> provider73, Provider<ExtensionController> provider74, Provider<UserInfoControllerImpl> provider75, Provider<PhoneStatusBarPolicy> provider76, Provider<KeyguardIndicationController> provider77, Provider<DismissCallbackRegistry> provider78, Provider<StatusBarTouchableRegionManager> provider79) {
        this.smartSpaceControllerProvider = provider;
        this.wallpaperNotifierProvider = provider2;
        this.contextProvider = provider3;
        this.notificationsControllerProvider = provider4;
        this.lightBarControllerProvider = provider5;
        this.autoHideControllerProvider = provider6;
        this.keyguardUpdateMonitorProvider = provider7;
        this.statusBarIconControllerProvider = provider8;
        this.pulseExpansionHandlerProvider = provider9;
        this.notificationWakeUpCoordinatorProvider = provider10;
        this.keyguardBypassControllerProvider = provider11;
        this.keyguardStateControllerProvider = provider12;
        this.headsUpManagerPhoneProvider = provider13;
        this.dynamicPrivacyControllerProvider = provider14;
        this.bypassHeadsUpNotifierProvider = provider15;
        this.falsingManagerProvider = provider16;
        this.broadcastDispatcherProvider = provider17;
        this.remoteInputQuickSettingsDisablerProvider = provider18;
        this.notificationGutsManagerProvider = provider19;
        this.notificationLoggerProvider = provider20;
        this.notificationInterruptionStateProvider = provider21;
        this.notificationViewHierarchyManagerProvider = provider22;
        this.keyguardViewMediatorProvider = provider23;
        this.notificationAlertingManagerProvider = provider24;
        this.displayMetricsProvider = provider25;
        this.metricsLoggerProvider = provider26;
        this.uiBgExecutorProvider = provider27;
        this.notificationMediaManagerProvider = provider28;
        this.lockScreenUserManagerProvider = provider29;
        this.remoteInputManagerProvider = provider30;
        this.userSwitcherControllerProvider = provider31;
        this.networkControllerProvider = provider32;
        this.batteryControllerProvider = provider33;
        this.colorExtractorProvider = provider34;
        this.screenLifecycleProvider = provider35;
        this.wakefulnessLifecycleProvider = provider36;
        this.statusBarStateControllerProvider = provider37;
        this.vibratorHelperProvider = provider38;
        this.bubbleControllerProvider = provider39;
        this.groupManagerProvider = provider40;
        this.visualStabilityManagerProvider = provider41;
        this.deviceProvisionedControllerProvider = provider42;
        this.navigationBarControllerProvider = provider43;
        this.assistManagerLazyProvider = provider44;
        this.configurationControllerProvider = provider45;
        this.notificationShadeWindowControllerProvider = provider46;
        this.lockscreenLockIconControllerProvider = provider47;
        this.dozeParametersProvider = provider48;
        this.scrimControllerProvider = provider49;
        this.keyguardLiftControllerProvider = provider50;
        this.lockscreenWallpaperLazyProvider = provider51;
        this.biometricUnlockControllerLazyProvider = provider52;
        this.dozeServiceHostProvider = provider53;
        this.powerManagerProvider = provider54;
        this.screenPinningRequestProvider = provider55;
        this.dozeScrimControllerProvider = provider56;
        this.volumeComponentProvider = provider57;
        this.commandQueueProvider = provider58;
        this.recentsProvider = provider59;
        this.statusBarComponentBuilderProvider = provider60;
        this.pluginManagerProvider = provider61;
        this.dividerOptionalProvider = provider62;
        this.lightsOutNotifControllerProvider = provider63;
        this.statusBarNotificationActivityStarterBuilderProvider = provider64;
        this.shadeControllerProvider = provider65;
        this.superStatusBarViewFactoryProvider = provider66;
        this.statusBarKeyguardViewManagerProvider = provider67;
        this.viewMediatorCallbackProvider = provider68;
        this.initControllerProvider = provider69;
        this.darkIconDispatcherProvider = provider70;
        this.timeTickHandlerProvider = provider71;
        this.pluginDependencyProvider = provider72;
        this.keyguardDismissUtilProvider = provider73;
        this.extensionControllerProvider = provider74;
        this.userInfoControllerImplProvider = provider75;
        this.phoneStatusBarPolicyProvider = provider76;
        this.keyguardIndicationControllerProvider = provider77;
        this.dismissCallbackRegistryProvider = provider78;
        this.statusBarTouchableRegionManagerProvider = provider79;
    }

    public StatusBarGoogle get() {
        return provideInstance(this.smartSpaceControllerProvider, this.wallpaperNotifierProvider, this.contextProvider, this.notificationsControllerProvider, this.lightBarControllerProvider, this.autoHideControllerProvider, this.keyguardUpdateMonitorProvider, this.statusBarIconControllerProvider, this.pulseExpansionHandlerProvider, this.notificationWakeUpCoordinatorProvider, this.keyguardBypassControllerProvider, this.keyguardStateControllerProvider, this.headsUpManagerPhoneProvider, this.dynamicPrivacyControllerProvider, this.bypassHeadsUpNotifierProvider, this.falsingManagerProvider, this.broadcastDispatcherProvider, this.remoteInputQuickSettingsDisablerProvider, this.notificationGutsManagerProvider, this.notificationLoggerProvider, this.notificationInterruptionStateProvider, this.notificationViewHierarchyManagerProvider, this.keyguardViewMediatorProvider, this.notificationAlertingManagerProvider, this.displayMetricsProvider, this.metricsLoggerProvider, this.uiBgExecutorProvider, this.notificationMediaManagerProvider, this.lockScreenUserManagerProvider, this.remoteInputManagerProvider, this.userSwitcherControllerProvider, this.networkControllerProvider, this.batteryControllerProvider, this.colorExtractorProvider, this.screenLifecycleProvider, this.wakefulnessLifecycleProvider, this.statusBarStateControllerProvider, this.vibratorHelperProvider, this.bubbleControllerProvider, this.groupManagerProvider, this.visualStabilityManagerProvider, this.deviceProvisionedControllerProvider, this.navigationBarControllerProvider, this.assistManagerLazyProvider, this.configurationControllerProvider, this.notificationShadeWindowControllerProvider, this.lockscreenLockIconControllerProvider, this.dozeParametersProvider, this.scrimControllerProvider, this.keyguardLiftControllerProvider, this.lockscreenWallpaperLazyProvider, this.biometricUnlockControllerLazyProvider, this.dozeServiceHostProvider, this.powerManagerProvider, this.screenPinningRequestProvider, this.dozeScrimControllerProvider, this.volumeComponentProvider, this.commandQueueProvider, this.recentsProvider, this.statusBarComponentBuilderProvider, this.pluginManagerProvider, this.dividerOptionalProvider, this.lightsOutNotifControllerProvider, this.statusBarNotificationActivityStarterBuilderProvider, this.shadeControllerProvider, this.superStatusBarViewFactoryProvider, this.statusBarKeyguardViewManagerProvider, this.viewMediatorCallbackProvider, this.initControllerProvider, this.darkIconDispatcherProvider, this.timeTickHandlerProvider, this.pluginDependencyProvider, this.keyguardDismissUtilProvider, this.extensionControllerProvider, this.userInfoControllerImplProvider, this.phoneStatusBarPolicyProvider, this.keyguardIndicationControllerProvider, this.dismissCallbackRegistryProvider, this.statusBarTouchableRegionManagerProvider);
    }

    public static StatusBarGoogle provideInstance(Provider<SmartSpaceController> provider, Provider<WallpaperNotifier> provider2, Provider<Context> provider3, Provider<NotificationsController> provider4, Provider<LightBarController> provider5, Provider<AutoHideController> provider6, Provider<KeyguardUpdateMonitor> provider7, Provider<StatusBarIconController> provider8, Provider<PulseExpansionHandler> provider9, Provider<NotificationWakeUpCoordinator> provider10, Provider<KeyguardBypassController> provider11, Provider<KeyguardStateController> provider12, Provider<HeadsUpManagerPhone> provider13, Provider<DynamicPrivacyController> provider14, Provider<BypassHeadsUpNotifier> provider15, Provider<FalsingManager> provider16, Provider<BroadcastDispatcher> provider17, Provider<RemoteInputQuickSettingsDisabler> provider18, Provider<NotificationGutsManager> provider19, Provider<NotificationLogger> provider20, Provider<NotificationInterruptStateProvider> provider21, Provider<NotificationViewHierarchyManager> provider22, Provider<KeyguardViewMediator> provider23, Provider<NotificationAlertingManager> provider24, Provider<DisplayMetrics> provider25, Provider<MetricsLogger> provider26, Provider<Executor> provider27, Provider<NotificationMediaManager> provider28, Provider<NotificationLockscreenUserManager> provider29, Provider<NotificationRemoteInputManager> provider30, Provider<UserSwitcherController> provider31, Provider<NetworkController> provider32, Provider<BatteryController> provider33, Provider<SysuiColorExtractor> provider34, Provider<ScreenLifecycle> provider35, Provider<WakefulnessLifecycle> provider36, Provider<SysuiStatusBarStateController> provider37, Provider<VibratorHelper> provider38, Provider<BubbleController> provider39, Provider<NotificationGroupManager> provider40, Provider<VisualStabilityManager> provider41, Provider<DeviceProvisionedController> provider42, Provider<NavigationBarController> provider43, Provider<AssistManager> provider44, Provider<ConfigurationController> provider45, Provider<NotificationShadeWindowController> provider46, Provider<LockscreenLockIconController> provider47, Provider<DozeParameters> provider48, Provider<LiveWallpaperScrimController> provider49, Provider<KeyguardLiftController> provider50, Provider<LockscreenWallpaper> provider51, Provider<BiometricUnlockController> provider52, Provider<DozeServiceHost> provider53, Provider<PowerManager> provider54, Provider<ScreenPinningRequest> provider55, Provider<DozeScrimController> provider56, Provider<VolumeComponent> provider57, Provider<CommandQueue> provider58, Provider<Optional<Recents>> provider59, Provider<Builder> provider60, Provider<PluginManager> provider61, Provider<Optional<Divider>> provider62, Provider<LightsOutNotifController> provider63, Provider<StatusBarNotificationActivityStarter.Builder> provider64, Provider<ShadeController> provider65, Provider<SuperStatusBarViewFactory> provider66, Provider<StatusBarKeyguardViewManager> provider67, Provider<ViewMediatorCallback> provider68, Provider<InitController> provider69, Provider<DarkIconDispatcher> provider70, Provider<Handler> provider71, Provider<PluginDependencyProvider> provider72, Provider<KeyguardDismissUtil> provider73, Provider<ExtensionController> provider74, Provider<UserInfoControllerImpl> provider75, Provider<PhoneStatusBarPolicy> provider76, Provider<KeyguardIndicationController> provider77, Provider<DismissCallbackRegistry> provider78, Provider<StatusBarTouchableRegionManager> provider79) {
        return proxyProvideStatusBar((SmartSpaceController) provider.get(), (WallpaperNotifier) provider2.get(), (Context) provider3.get(), (NotificationsController) provider4.get(), (LightBarController) provider5.get(), (AutoHideController) provider6.get(), (KeyguardUpdateMonitor) provider7.get(), (StatusBarIconController) provider8.get(), (PulseExpansionHandler) provider9.get(), (NotificationWakeUpCoordinator) provider10.get(), (KeyguardBypassController) provider11.get(), (KeyguardStateController) provider12.get(), (HeadsUpManagerPhone) provider13.get(), (DynamicPrivacyController) provider14.get(), (BypassHeadsUpNotifier) provider15.get(), (FalsingManager) provider16.get(), (BroadcastDispatcher) provider17.get(), (RemoteInputQuickSettingsDisabler) provider18.get(), (NotificationGutsManager) provider19.get(), (NotificationLogger) provider20.get(), (NotificationInterruptStateProvider) provider21.get(), (NotificationViewHierarchyManager) provider22.get(), (KeyguardViewMediator) provider23.get(), (NotificationAlertingManager) provider24.get(), (DisplayMetrics) provider25.get(), (MetricsLogger) provider26.get(), (Executor) provider27.get(), (NotificationMediaManager) provider28.get(), (NotificationLockscreenUserManager) provider29.get(), (NotificationRemoteInputManager) provider30.get(), (UserSwitcherController) provider31.get(), (NetworkController) provider32.get(), (BatteryController) provider33.get(), (SysuiColorExtractor) provider34.get(), (ScreenLifecycle) provider35.get(), (WakefulnessLifecycle) provider36.get(), (SysuiStatusBarStateController) provider37.get(), (VibratorHelper) provider38.get(), (BubbleController) provider39.get(), (NotificationGroupManager) provider40.get(), (VisualStabilityManager) provider41.get(), (DeviceProvisionedController) provider42.get(), (NavigationBarController) provider43.get(), DoubleCheck.lazy(provider44), (ConfigurationController) provider45.get(), (NotificationShadeWindowController) provider46.get(), (LockscreenLockIconController) provider47.get(), (DozeParameters) provider48.get(), (LiveWallpaperScrimController) provider49.get(), (KeyguardLiftController) provider50.get(), DoubleCheck.lazy(provider51), DoubleCheck.lazy(provider52), (DozeServiceHost) provider53.get(), (PowerManager) provider54.get(), (ScreenPinningRequest) provider55.get(), (DozeScrimController) provider56.get(), (VolumeComponent) provider57.get(), (CommandQueue) provider58.get(), (Optional) provider59.get(), provider60, (PluginManager) provider61.get(), (Optional) provider62.get(), (LightsOutNotifController) provider63.get(), (StatusBarNotificationActivityStarter.Builder) provider64.get(), (ShadeController) provider65.get(), (SuperStatusBarViewFactory) provider66.get(), (StatusBarKeyguardViewManager) provider67.get(), (ViewMediatorCallback) provider68.get(), (InitController) provider69.get(), (DarkIconDispatcher) provider70.get(), (Handler) provider71.get(), (PluginDependencyProvider) provider72.get(), (KeyguardDismissUtil) provider73.get(), (ExtensionController) provider74.get(), (UserInfoControllerImpl) provider75.get(), (PhoneStatusBarPolicy) provider76.get(), (KeyguardIndicationController) provider77.get(), (DismissCallbackRegistry) provider78.get(), (StatusBarTouchableRegionManager) provider79.get());
    }

    public static StatusBarGoogleModule_ProvideStatusBarFactory create(Provider<SmartSpaceController> provider, Provider<WallpaperNotifier> provider2, Provider<Context> provider3, Provider<NotificationsController> provider4, Provider<LightBarController> provider5, Provider<AutoHideController> provider6, Provider<KeyguardUpdateMonitor> provider7, Provider<StatusBarIconController> provider8, Provider<PulseExpansionHandler> provider9, Provider<NotificationWakeUpCoordinator> provider10, Provider<KeyguardBypassController> provider11, Provider<KeyguardStateController> provider12, Provider<HeadsUpManagerPhone> provider13, Provider<DynamicPrivacyController> provider14, Provider<BypassHeadsUpNotifier> provider15, Provider<FalsingManager> provider16, Provider<BroadcastDispatcher> provider17, Provider<RemoteInputQuickSettingsDisabler> provider18, Provider<NotificationGutsManager> provider19, Provider<NotificationLogger> provider20, Provider<NotificationInterruptStateProvider> provider21, Provider<NotificationViewHierarchyManager> provider22, Provider<KeyguardViewMediator> provider23, Provider<NotificationAlertingManager> provider24, Provider<DisplayMetrics> provider25, Provider<MetricsLogger> provider26, Provider<Executor> provider27, Provider<NotificationMediaManager> provider28, Provider<NotificationLockscreenUserManager> provider29, Provider<NotificationRemoteInputManager> provider30, Provider<UserSwitcherController> provider31, Provider<NetworkController> provider32, Provider<BatteryController> provider33, Provider<SysuiColorExtractor> provider34, Provider<ScreenLifecycle> provider35, Provider<WakefulnessLifecycle> provider36, Provider<SysuiStatusBarStateController> provider37, Provider<VibratorHelper> provider38, Provider<BubbleController> provider39, Provider<NotificationGroupManager> provider40, Provider<VisualStabilityManager> provider41, Provider<DeviceProvisionedController> provider42, Provider<NavigationBarController> provider43, Provider<AssistManager> provider44, Provider<ConfigurationController> provider45, Provider<NotificationShadeWindowController> provider46, Provider<LockscreenLockIconController> provider47, Provider<DozeParameters> provider48, Provider<LiveWallpaperScrimController> provider49, Provider<KeyguardLiftController> provider50, Provider<LockscreenWallpaper> provider51, Provider<BiometricUnlockController> provider52, Provider<DozeServiceHost> provider53, Provider<PowerManager> provider54, Provider<ScreenPinningRequest> provider55, Provider<DozeScrimController> provider56, Provider<VolumeComponent> provider57, Provider<CommandQueue> provider58, Provider<Optional<Recents>> provider59, Provider<Builder> provider60, Provider<PluginManager> provider61, Provider<Optional<Divider>> provider62, Provider<LightsOutNotifController> provider63, Provider<StatusBarNotificationActivityStarter.Builder> provider64, Provider<ShadeController> provider65, Provider<SuperStatusBarViewFactory> provider66, Provider<StatusBarKeyguardViewManager> provider67, Provider<ViewMediatorCallback> provider68, Provider<InitController> provider69, Provider<DarkIconDispatcher> provider70, Provider<Handler> provider71, Provider<PluginDependencyProvider> provider72, Provider<KeyguardDismissUtil> provider73, Provider<ExtensionController> provider74, Provider<UserInfoControllerImpl> provider75, Provider<PhoneStatusBarPolicy> provider76, Provider<KeyguardIndicationController> provider77, Provider<DismissCallbackRegistry> provider78, Provider<StatusBarTouchableRegionManager> provider79) {
        StatusBarGoogleModule_ProvideStatusBarFactory statusBarGoogleModule_ProvideStatusBarFactory = new StatusBarGoogleModule_ProvideStatusBarFactory(provider, provider2, provider3, provider4, provider5, provider6, provider7, provider8, provider9, provider10, provider11, provider12, provider13, provider14, provider15, provider16, provider17, provider18, provider19, provider20, provider21, provider22, provider23, provider24, provider25, provider26, provider27, provider28, provider29, provider30, provider31, provider32, provider33, provider34, provider35, provider36, provider37, provider38, provider39, provider40, provider41, provider42, provider43, provider44, provider45, provider46, provider47, provider48, provider49, provider50, provider51, provider52, provider53, provider54, provider55, provider56, provider57, provider58, provider59, provider60, provider61, provider62, provider63, provider64, provider65, provider66, provider67, provider68, provider69, provider70, provider71, provider72, provider73, provider74, provider75, provider76, provider77, provider78, provider79);
        return statusBarGoogleModule_ProvideStatusBarFactory;
    }

    public static StatusBarGoogle proxyProvideStatusBar(SmartSpaceController smartSpaceController, WallpaperNotifier wallpaperNotifier, Context context, NotificationsController notificationsController, LightBarController lightBarController, AutoHideController autoHideController, KeyguardUpdateMonitor keyguardUpdateMonitor, StatusBarIconController statusBarIconController, PulseExpansionHandler pulseExpansionHandler, NotificationWakeUpCoordinator notificationWakeUpCoordinator, KeyguardBypassController keyguardBypassController, KeyguardStateController keyguardStateController, HeadsUpManagerPhone headsUpManagerPhone, DynamicPrivacyController dynamicPrivacyController, BypassHeadsUpNotifier bypassHeadsUpNotifier, FalsingManager falsingManager, BroadcastDispatcher broadcastDispatcher, RemoteInputQuickSettingsDisabler remoteInputQuickSettingsDisabler, NotificationGutsManager notificationGutsManager, NotificationLogger notificationLogger, NotificationInterruptStateProvider notificationInterruptStateProvider, NotificationViewHierarchyManager notificationViewHierarchyManager, KeyguardViewMediator keyguardViewMediator, NotificationAlertingManager notificationAlertingManager, DisplayMetrics displayMetrics, MetricsLogger metricsLogger, Executor executor, NotificationMediaManager notificationMediaManager, NotificationLockscreenUserManager notificationLockscreenUserManager, NotificationRemoteInputManager notificationRemoteInputManager, UserSwitcherController userSwitcherController, NetworkController networkController, BatteryController batteryController, SysuiColorExtractor sysuiColorExtractor, ScreenLifecycle screenLifecycle, WakefulnessLifecycle wakefulnessLifecycle, SysuiStatusBarStateController sysuiStatusBarStateController, VibratorHelper vibratorHelper, BubbleController bubbleController, NotificationGroupManager notificationGroupManager, VisualStabilityManager visualStabilityManager, DeviceProvisionedController deviceProvisionedController, NavigationBarController navigationBarController, Lazy<AssistManager> lazy, ConfigurationController configurationController, NotificationShadeWindowController notificationShadeWindowController, LockscreenLockIconController lockscreenLockIconController, DozeParameters dozeParameters, LiveWallpaperScrimController liveWallpaperScrimController, KeyguardLiftController keyguardLiftController, Lazy<LockscreenWallpaper> lazy2, Lazy<BiometricUnlockController> lazy3, DozeServiceHost dozeServiceHost, PowerManager powerManager, ScreenPinningRequest screenPinningRequest, DozeScrimController dozeScrimController, VolumeComponent volumeComponent, CommandQueue commandQueue, Optional<Recents> optional, Provider<Builder> provider, PluginManager pluginManager, Optional<Divider> optional2, LightsOutNotifController lightsOutNotifController, StatusBarNotificationActivityStarter.Builder builder, ShadeController shadeController, SuperStatusBarViewFactory superStatusBarViewFactory, StatusBarKeyguardViewManager statusBarKeyguardViewManager, ViewMediatorCallback viewMediatorCallback, InitController initController, DarkIconDispatcher darkIconDispatcher, Handler handler, PluginDependencyProvider pluginDependencyProvider2, KeyguardDismissUtil keyguardDismissUtil, ExtensionController extensionController, UserInfoControllerImpl userInfoControllerImpl, PhoneStatusBarPolicy phoneStatusBarPolicy, KeyguardIndicationController keyguardIndicationController, DismissCallbackRegistry dismissCallbackRegistry, StatusBarTouchableRegionManager statusBarTouchableRegionManager) {
        StatusBarGoogle provideStatusBar = StatusBarGoogleModule.provideStatusBar(smartSpaceController, wallpaperNotifier, context, notificationsController, lightBarController, autoHideController, keyguardUpdateMonitor, statusBarIconController, pulseExpansionHandler, notificationWakeUpCoordinator, keyguardBypassController, keyguardStateController, headsUpManagerPhone, dynamicPrivacyController, bypassHeadsUpNotifier, falsingManager, broadcastDispatcher, remoteInputQuickSettingsDisabler, notificationGutsManager, notificationLogger, notificationInterruptStateProvider, notificationViewHierarchyManager, keyguardViewMediator, notificationAlertingManager, displayMetrics, metricsLogger, executor, notificationMediaManager, notificationLockscreenUserManager, notificationRemoteInputManager, userSwitcherController, networkController, batteryController, sysuiColorExtractor, screenLifecycle, wakefulnessLifecycle, sysuiStatusBarStateController, vibratorHelper, bubbleController, notificationGroupManager, visualStabilityManager, deviceProvisionedController, navigationBarController, lazy, configurationController, notificationShadeWindowController, lockscreenLockIconController, dozeParameters, liveWallpaperScrimController, keyguardLiftController, lazy2, lazy3, dozeServiceHost, powerManager, screenPinningRequest, dozeScrimController, volumeComponent, commandQueue, optional, provider, pluginManager, optional2, lightsOutNotifController, builder, shadeController, superStatusBarViewFactory, statusBarKeyguardViewManager, viewMediatorCallback, initController, darkIconDispatcher, handler, pluginDependencyProvider2, keyguardDismissUtil, extensionController, userInfoControllerImpl, phoneStatusBarPolicy, keyguardIndicationController, dismissCallbackRegistry, statusBarTouchableRegionManager);
        Preconditions.checkNotNull(provideStatusBar, "Cannot return null from a non-@Nullable @Provides method");
        return provideStatusBar;
    }
}
