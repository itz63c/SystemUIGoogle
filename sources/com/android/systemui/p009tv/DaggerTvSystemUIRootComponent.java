package com.android.systemui.p009tv;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.IActivityManager;
import android.app.INotificationManager;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.app.trust.TrustManager;
import android.content.BroadcastReceiver;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.IPackageManager;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutManager;
import android.content.res.Resources;
import android.hardware.SensorPrivacyManager;
import android.hardware.display.NightDisplayListener;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.UserManager;
import android.os.Vibrator;
import android.service.dreams.IDreamManager;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Choreographer;
import android.view.IWindowManager;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import androidx.slice.Clock;
import com.android.internal.app.AssistUtils;
import com.android.internal.app.IBatteryStats;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.UiEventLogger;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.util.LatencyTracker;
import com.android.keyguard.KeyguardClockSwitch;
import com.android.keyguard.KeyguardMessageArea;
import com.android.keyguard.KeyguardSecurityModel;
import com.android.keyguard.KeyguardSecurityModel_Factory;
import com.android.keyguard.KeyguardSliceView;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitor_Factory;
import com.android.keyguard.clock.ClockManager;
import com.android.keyguard.clock.ClockManager_Factory;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.systemui.ActivityIntentHelper;
import com.android.systemui.ActivityIntentHelper_Factory;
import com.android.systemui.ActivityStarterDelegate;
import com.android.systemui.ActivityStarterDelegate_Factory;
import com.android.systemui.BootCompleteCacheImpl;
import com.android.systemui.BootCompleteCacheImpl_Factory;
import com.android.systemui.Dependency;
import com.android.systemui.Dependency.DependencyInjector;
import com.android.systemui.Dependency_MembersInjector;
import com.android.systemui.ForegroundServiceController;
import com.android.systemui.ForegroundServiceController_Factory;
import com.android.systemui.ForegroundServiceNotificationListener;
import com.android.systemui.ForegroundServiceNotificationListener_Factory;
import com.android.systemui.ForegroundServicesDialog;
import com.android.systemui.ForegroundServicesDialog_Factory;
import com.android.systemui.ImageWallpaper;
import com.android.systemui.ImageWallpaper_Factory;
import com.android.systemui.InitController;
import com.android.systemui.InitController_Factory;
import com.android.systemui.LatencyTester;
import com.android.systemui.LatencyTester_Factory;
import com.android.systemui.ScreenDecorations;
import com.android.systemui.ScreenDecorations_Factory;
import com.android.systemui.SizeCompatModeActivityController;
import com.android.systemui.SizeCompatModeActivityController_Factory;
import com.android.systemui.SliceBroadcastRelayHandler;
import com.android.systemui.SliceBroadcastRelayHandler_Factory;
import com.android.systemui.SystemUI;
import com.android.systemui.SystemUIAppComponentFactory;
import com.android.systemui.SystemUIAppComponentFactory_MembersInjector;
import com.android.systemui.SystemUIService;
import com.android.systemui.SystemUIService_Factory;
import com.android.systemui.TransactionPool;
import com.android.systemui.TransactionPool_Factory;
import com.android.systemui.UiOffloadThread;
import com.android.systemui.UiOffloadThread_Factory;
import com.android.systemui.accessibility.SystemActions;
import com.android.systemui.accessibility.SystemActions_Factory;
import com.android.systemui.accessibility.WindowMagnification;
import com.android.systemui.accessibility.WindowMagnification_Factory;
import com.android.systemui.appops.AppOpsControllerImpl;
import com.android.systemui.appops.AppOpsControllerImpl_Factory;
import com.android.systemui.assist.AssistHandleBehaviorController;
import com.android.systemui.assist.AssistHandleBehaviorController_Factory;
import com.android.systemui.assist.AssistHandleLikeHomeBehavior_Factory;
import com.android.systemui.assist.AssistHandleOffBehavior_Factory;
import com.android.systemui.assist.AssistHandleReminderExpBehavior_Factory;
import com.android.systemui.assist.AssistHandleService;
import com.android.systemui.assist.AssistHandleService_Factory;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.assist.AssistManager_Factory;
import com.android.systemui.assist.AssistModule_ProvideAssistHandleBehaviorControllerMapFactory;
import com.android.systemui.assist.AssistModule_ProvideAssistHandleViewControllerFactory;
import com.android.systemui.assist.AssistModule_ProvideAssistUtilsFactory;
import com.android.systemui.assist.AssistModule_ProvideBackgroundHandlerFactory;
import com.android.systemui.assist.AssistModule_ProvideSystemClockFactory;
import com.android.systemui.assist.DeviceConfigHelper;
import com.android.systemui.assist.DeviceConfigHelper_Factory;
import com.android.systemui.assist.PhoneStateMonitor;
import com.android.systemui.assist.PhoneStateMonitor_Factory;
import com.android.systemui.biometrics.AuthController;
import com.android.systemui.biometrics.AuthController_Factory;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.broadcast.BroadcastDispatcher_Factory;
import com.android.systemui.bubbles.BubbleController;
import com.android.systemui.bubbles.BubbleData;
import com.android.systemui.bubbles.BubbleData_Factory;
import com.android.systemui.bubbles.BubbleOverflowActivity;
import com.android.systemui.bubbles.BubbleOverflowActivity_Factory;
import com.android.systemui.bubbles.dagger.BubbleModule_NewBubbleControllerFactory;
import com.android.systemui.classifier.FalsingManagerProxy;
import com.android.systemui.classifier.FalsingManagerProxy_Factory;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.colorextraction.SysuiColorExtractor_Factory;
import com.android.systemui.controls.controller.ControlsBindingControllerImpl;
import com.android.systemui.controls.controller.ControlsBindingControllerImpl_Factory;
import com.android.systemui.controls.controller.ControlsControllerImpl;
import com.android.systemui.controls.controller.ControlsControllerImpl_Factory;
import com.android.systemui.controls.controller.ControlsFavoritePersistenceWrapper;
import com.android.systemui.controls.management.ControlsFavoritingActivity;
import com.android.systemui.controls.management.ControlsFavoritingActivity_Factory;
import com.android.systemui.controls.management.ControlsListingControllerImpl;
import com.android.systemui.controls.management.ControlsListingControllerImpl_Factory;
import com.android.systemui.controls.management.ControlsProviderSelectorActivity;
import com.android.systemui.controls.management.ControlsProviderSelectorActivity_Factory;
import com.android.systemui.controls.management.ControlsRequestDialog;
import com.android.systemui.controls.management.ControlsRequestDialog_Factory;
import com.android.systemui.controls.p004ui.ControlsUiControllerImpl;
import com.android.systemui.controls.p004ui.ControlsUiControllerImpl_Factory;
import com.android.systemui.dagger.ContextComponentHelper;
import com.android.systemui.dagger.ContextComponentResolver;
import com.android.systemui.dagger.ContextComponentResolver_Factory;
import com.android.systemui.dagger.DependencyProvider;
import com.android.systemui.dagger.DependencyProvider_ProvideActivityManagerWrapperFactory;
import com.android.systemui.dagger.DependencyProvider_ProvideAlwaysOnDisplayPolicyFactory;
import com.android.systemui.dagger.DependencyProvider_ProvideAmbientDisplayConfigurationFactory;
import com.android.systemui.dagger.DependencyProvider_ProvideAutoHideControllerFactory;
import com.android.systemui.dagger.DependencyProvider_ProvideConfigurationControllerFactory;
import com.android.systemui.dagger.DependencyProvider_ProvideDataSaverControllerFactory;
import com.android.systemui.dagger.DependencyProvider_ProvideDevicePolicyManagerWrapperFactory;
import com.android.systemui.dagger.DependencyProvider_ProvideDisplayMetricsFactory;
import com.android.systemui.dagger.DependencyProvider_ProvideHandlerFactory;
import com.android.systemui.dagger.DependencyProvider_ProvideINotificationManagerFactory;
import com.android.systemui.dagger.DependencyProvider_ProvideLeakDetectorFactory;
import com.android.systemui.dagger.DependencyProvider_ProvideLockPatternUtilsFactory;
import com.android.systemui.dagger.DependencyProvider_ProvideMetricsLoggerFactory;
import com.android.systemui.dagger.DependencyProvider_ProvideNavigationBarControllerFactory;
import com.android.systemui.dagger.DependencyProvider_ProvideNightDisplayListenerFactory;
import com.android.systemui.dagger.DependencyProvider_ProvideNotificationMessagingUtilFactory;
import com.android.systemui.dagger.DependencyProvider_ProvidePluginManagerFactory;
import com.android.systemui.dagger.DependencyProvider_ProvideSharePreferencesFactory;
import com.android.systemui.dagger.DependencyProvider_ProvideTimeTickHandlerFactory;
import com.android.systemui.dagger.DependencyProvider_ProviderLayoutInflaterFactory;
import com.android.systemui.dagger.DependencyProvider_ProvidesChoreographerFactory;
import com.android.systemui.dagger.DependencyProvider_ProvidesViewMediatorCallbackFactory;
import com.android.systemui.dagger.SystemServicesModule_ProvideAccessibilityManagerFactory;
import com.android.systemui.dagger.SystemServicesModule_ProvideActivityManagerFactory;
import com.android.systemui.dagger.SystemServicesModule_ProvideAlarmManagerFactory;
import com.android.systemui.dagger.SystemServicesModule_ProvideAudioManagerFactory;
import com.android.systemui.dagger.SystemServicesModule_ProvideConnectivityManagagerFactory;
import com.android.systemui.dagger.SystemServicesModule_ProvideContentResolverFactory;
import com.android.systemui.dagger.SystemServicesModule_ProvideDevicePolicyManagerFactory;
import com.android.systemui.dagger.SystemServicesModule_ProvideDisplayIdFactory;
import com.android.systemui.dagger.SystemServicesModule_ProvideIActivityManagerFactory;
import com.android.systemui.dagger.SystemServicesModule_ProvideIBatteryStatsFactory;
import com.android.systemui.dagger.SystemServicesModule_ProvideIDreamManagerFactory;
import com.android.systemui.dagger.SystemServicesModule_ProvideIPackageManagerFactory;
import com.android.systemui.dagger.SystemServicesModule_ProvideIStatusBarServiceFactory;
import com.android.systemui.dagger.SystemServicesModule_ProvideIWallPaperManagerFactory;
import com.android.systemui.dagger.SystemServicesModule_ProvideIWindowManagerFactory;
import com.android.systemui.dagger.SystemServicesModule_ProvideKeyguardManagerFactory;
import com.android.systemui.dagger.SystemServicesModule_ProvideLatencyTrackerFactory;
import com.android.systemui.dagger.SystemServicesModule_ProvideLauncherAppsFactory;
import com.android.systemui.dagger.SystemServicesModule_ProvideLocalBluetoothControllerFactory;
import com.android.systemui.dagger.SystemServicesModule_ProvideNotificationManagerFactory;
import com.android.systemui.dagger.SystemServicesModule_ProvidePackageManagerFactory;
import com.android.systemui.dagger.SystemServicesModule_ProvidePackageManagerWrapperFactory;
import com.android.systemui.dagger.SystemServicesModule_ProvidePowerManagerFactory;
import com.android.systemui.dagger.SystemServicesModule_ProvideResourcesFactory;
import com.android.systemui.dagger.SystemServicesModule_ProvideSensorPrivacyManagerFactory;
import com.android.systemui.dagger.SystemServicesModule_ProvideShortcutManagerFactory;
import com.android.systemui.dagger.SystemServicesModule_ProvideTelecomManagerFactory;
import com.android.systemui.dagger.SystemServicesModule_ProvideTelephonyManagerFactory;
import com.android.systemui.dagger.SystemServicesModule_ProvideTrustManagerFactory;
import com.android.systemui.dagger.SystemServicesModule_ProvideUserManagerFactory;
import com.android.systemui.dagger.SystemServicesModule_ProvideVibratorFactory;
import com.android.systemui.dagger.SystemServicesModule_ProvideWallpaperManagerFactory;
import com.android.systemui.dagger.SystemServicesModule_ProvideWindowManagerFactory;
import com.android.systemui.dagger.SystemUIDefaultModule_ProvideAllowNotificationLongPressFactory;
import com.android.systemui.dagger.SystemUIDefaultModule_ProvideHeadsUpManagerPhoneFactory;
import com.android.systemui.dagger.SystemUIDefaultModule_ProvideLeakReportEmailFactory;
import com.android.systemui.dagger.SystemUIDefaultModule_ProvideRecentsFactory;
import com.android.systemui.dagger.SystemUIModule_ProvideKeyguardLiftControllerFactory;
import com.android.systemui.dagger.SystemUIModule_ProvideSysUiStateFactory;
import com.android.systemui.dock.DockManager;
import com.android.systemui.dock.DockManagerImpl;
import com.android.systemui.dock.DockManagerImpl_Factory;
import com.android.systemui.doze.DozeFactory_Factory;
import com.android.systemui.doze.DozeLog;
import com.android.systemui.doze.DozeLog_Factory;
import com.android.systemui.doze.DozeLogger_Factory;
import com.android.systemui.doze.DozeService;
import com.android.systemui.doze.DozeService_Factory;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.dump.DumpManager_Factory;
import com.android.systemui.dump.SystemUIAuxiliaryDumpService;
import com.android.systemui.dump.SystemUIAuxiliaryDumpService_Factory;
import com.android.systemui.fragments.FragmentService;
import com.android.systemui.fragments.FragmentService.FragmentCreator;
import com.android.systemui.fragments.FragmentService_Factory;
import com.android.systemui.globalactions.GlobalActionsComponent;
import com.android.systemui.globalactions.GlobalActionsComponent_Factory;
import com.android.systemui.globalactions.GlobalActionsDialog_Factory;
import com.android.systemui.globalactions.GlobalActionsImpl_Factory;
import com.android.systemui.keyguard.DismissCallbackRegistry;
import com.android.systemui.keyguard.DismissCallbackRegistry_Factory;
import com.android.systemui.keyguard.KeyguardLifecyclesDispatcher;
import com.android.systemui.keyguard.KeyguardLifecyclesDispatcher_Factory;
import com.android.systemui.keyguard.KeyguardService;
import com.android.systemui.keyguard.KeyguardService_Factory;
import com.android.systemui.keyguard.KeyguardSliceProvider;
import com.android.systemui.keyguard.KeyguardSliceProvider_MembersInjector;
import com.android.systemui.keyguard.KeyguardViewMediator;
import com.android.systemui.keyguard.ScreenLifecycle;
import com.android.systemui.keyguard.ScreenLifecycle_Factory;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.keyguard.WakefulnessLifecycle_Factory;
import com.android.systemui.keyguard.WorkLockActivity;
import com.android.systemui.keyguard.WorkLockActivity_Factory;
import com.android.systemui.keyguard.dagger.KeyguardModule_NewKeyguardViewMediatorFactory;
import com.android.systemui.log.LogBuffer;
import com.android.systemui.log.LogcatEchoTracker;
import com.android.systemui.log.dagger.LogModule_ProvideDozeLogBufferFactory;
import com.android.systemui.log.dagger.LogModule_ProvideLogcatEchoTrackerFactory;
import com.android.systemui.log.dagger.LogModule_ProvideNotificationsLogBufferFactory;
import com.android.systemui.log.dagger.LogModule_ProvideQuickSettingsLogBufferFactory;
import com.android.systemui.model.SysUiState;
import com.android.systemui.p007qs.AutoAddTracker_Factory;
import com.android.systemui.p007qs.QSFooterImpl;
import com.android.systemui.p007qs.QSFragment;
import com.android.systemui.p007qs.QSPanel;
import com.android.systemui.p007qs.QSTileHost;
import com.android.systemui.p007qs.QSTileHost_Factory;
import com.android.systemui.p007qs.QuickQSPanel;
import com.android.systemui.p007qs.QuickStatusBarHeader;
import com.android.systemui.p007qs.customize.QSCustomizer;
import com.android.systemui.p007qs.customize.TileQueryHelper;
import com.android.systemui.p007qs.logging.QSLogger;
import com.android.systemui.p007qs.logging.QSLogger_Factory;
import com.android.systemui.p007qs.tileimpl.QSFactoryImpl;
import com.android.systemui.p007qs.tileimpl.QSFactoryImpl_Factory;
import com.android.systemui.p007qs.tiles.AirplaneModeTile_Factory;
import com.android.systemui.p007qs.tiles.BatterySaverTile_Factory;
import com.android.systemui.p007qs.tiles.BluetoothTile_Factory;
import com.android.systemui.p007qs.tiles.CastTile_Factory;
import com.android.systemui.p007qs.tiles.CellularTile_Factory;
import com.android.systemui.p007qs.tiles.ColorInversionTile_Factory;
import com.android.systemui.p007qs.tiles.DataSaverTile_Factory;
import com.android.systemui.p007qs.tiles.DndTile_Factory;
import com.android.systemui.p007qs.tiles.FlashlightTile_Factory;
import com.android.systemui.p007qs.tiles.HotspotTile_Factory;
import com.android.systemui.p007qs.tiles.LocationTile_Factory;
import com.android.systemui.p007qs.tiles.NfcTile_Factory;
import com.android.systemui.p007qs.tiles.NightDisplayTile_Factory;
import com.android.systemui.p007qs.tiles.RotationLockTile_Factory;
import com.android.systemui.p007qs.tiles.ScreenRecordTile_Factory;
import com.android.systemui.p007qs.tiles.UiModeNightTile_Factory;
import com.android.systemui.p007qs.tiles.UserTile_Factory;
import com.android.systemui.p007qs.tiles.WifiTile_Factory;
import com.android.systemui.p007qs.tiles.WorkModeTile_Factory;
import com.android.systemui.p010wm.DisplayController;
import com.android.systemui.p010wm.DisplayController_Factory;
import com.android.systemui.p010wm.DisplayImeController;
import com.android.systemui.p010wm.DisplayImeController_Factory;
import com.android.systemui.p010wm.SystemWindows;
import com.android.systemui.p010wm.SystemWindows_Factory;
import com.android.systemui.pip.PipBoundsHandler_Factory;
import com.android.systemui.pip.PipSnapAlgorithm_Factory;
import com.android.systemui.pip.PipSurfaceTransactionHelper;
import com.android.systemui.pip.PipSurfaceTransactionHelper_Factory;
import com.android.systemui.pip.PipUI;
import com.android.systemui.pip.PipUI_Factory;
import com.android.systemui.pip.p005tv.PipControlsView;
import com.android.systemui.pip.p005tv.PipControlsViewController;
import com.android.systemui.pip.p005tv.PipManager;
import com.android.systemui.pip.p005tv.PipManager_Factory;
import com.android.systemui.pip.p005tv.PipMenuActivity;
import com.android.systemui.pip.p005tv.PipMenuActivity_Factory;
import com.android.systemui.pip.p005tv.dagger.TvPipComponent;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.PluginDependencyProvider;
import com.android.systemui.plugins.PluginDependencyProvider_Factory;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.power.EnhancedEstimatesImpl;
import com.android.systemui.power.EnhancedEstimatesImpl_Factory;
import com.android.systemui.power.PowerNotificationWarnings;
import com.android.systemui.power.PowerNotificationWarnings_Factory;
import com.android.systemui.power.PowerUI;
import com.android.systemui.power.PowerUI_Factory;
import com.android.systemui.recents.OverviewProxyRecentsImpl;
import com.android.systemui.recents.OverviewProxyRecentsImpl_Factory;
import com.android.systemui.recents.OverviewProxyService;
import com.android.systemui.recents.OverviewProxyService_Factory;
import com.android.systemui.recents.Recents;
import com.android.systemui.recents.RecentsImplementation;
import com.android.systemui.recents.RecentsModule_ProvideRecentsImplFactory;
import com.android.systemui.recents.ScreenPinningRequest_Factory;
import com.android.systemui.screenrecord.RecordingController;
import com.android.systemui.screenrecord.RecordingController_Factory;
import com.android.systemui.screenrecord.RecordingService;
import com.android.systemui.screenrecord.RecordingService_Factory;
import com.android.systemui.screenrecord.ScreenRecordDialog;
import com.android.systemui.screenrecord.ScreenRecordDialog_Factory;
import com.android.systemui.screenshot.GlobalScreenshot;
import com.android.systemui.screenshot.GlobalScreenshot.ActionProxyReceiver;
import com.android.systemui.screenshot.GlobalScreenshotLegacy;
import com.android.systemui.screenshot.GlobalScreenshotLegacy_Factory;
import com.android.systemui.screenshot.GlobalScreenshot_ActionProxyReceiver_Factory;
import com.android.systemui.screenshot.GlobalScreenshot_Factory;
import com.android.systemui.screenshot.ScreenshotNotificationsController_Factory;
import com.android.systemui.screenshot.TakeScreenshotService;
import com.android.systemui.screenshot.TakeScreenshotService_Factory;
import com.android.systemui.settings.BrightnessDialog;
import com.android.systemui.settings.BrightnessDialog_Factory;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.DevicePolicyManagerWrapper;
import com.android.systemui.shared.system.PackageManagerWrapper;
import com.android.systemui.shortcut.ShortcutKeyDispatcher;
import com.android.systemui.shortcut.ShortcutKeyDispatcher_Factory;
import com.android.systemui.stackdivider.Divider;
import com.android.systemui.stackdivider.DividerModule_ProvideDividerFactory;
import com.android.systemui.statusbar.BlurUtils;
import com.android.systemui.statusbar.BlurUtils_Factory;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.FeatureFlags_Factory;
import com.android.systemui.statusbar.FlingAnimationUtils_Builder_Factory;
import com.android.systemui.statusbar.KeyguardIndicationController;
import com.android.systemui.statusbar.KeyguardIndicationController_Factory;
import com.android.systemui.statusbar.MediaArtworkProcessor;
import com.android.systemui.statusbar.MediaArtworkProcessor_Factory;
import com.android.systemui.statusbar.NavigationBarController;
import com.android.systemui.statusbar.NotificationListener;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl;
import com.android.systemui.statusbar.NotificationLockscreenUserManagerImpl_Factory;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.NotificationShadeDepthController;
import com.android.systemui.statusbar.NotificationShadeDepthController_Factory;
import com.android.systemui.statusbar.NotificationShelf;
import com.android.systemui.statusbar.NotificationViewHierarchyManager;
import com.android.systemui.statusbar.PulseExpansionHandler;
import com.android.systemui.statusbar.PulseExpansionHandler_Factory;
import com.android.systemui.statusbar.SmartReplyController;
import com.android.systemui.statusbar.StatusBarStateControllerImpl;
import com.android.systemui.statusbar.StatusBarStateControllerImpl_Factory;
import com.android.systemui.statusbar.SuperStatusBarViewFactory;
import com.android.systemui.statusbar.SuperStatusBarViewFactory_Factory;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.statusbar.VibratorHelper;
import com.android.systemui.statusbar.VibratorHelper_Factory;
import com.android.systemui.statusbar.dagger.C1171x30c882de;
import com.android.systemui.statusbar.dagger.C1172xfa996c5e;
import com.android.systemui.statusbar.dagger.C1173x3f8faa0a;
import com.android.systemui.statusbar.dagger.StatusBarDependenciesModule_ProvideCommandQueueFactory;
import com.android.systemui.statusbar.dagger.StatusBarDependenciesModule_ProvideNotificationListenerFactory;
import com.android.systemui.statusbar.dagger.StatusBarDependenciesModule_ProvideSmartReplyControllerFactory;
import com.android.systemui.statusbar.notification.ConversationNotificationProcessor_Factory;
import com.android.systemui.statusbar.notification.DynamicChildBindController_Factory;
import com.android.systemui.statusbar.notification.DynamicPrivacyController;
import com.android.systemui.statusbar.notification.DynamicPrivacyController_Factory;
import com.android.systemui.statusbar.notification.ForegroundServiceDismissalFeatureController;
import com.android.systemui.statusbar.notification.ForegroundServiceDismissalFeatureController_Factory;
import com.android.systemui.statusbar.notification.InstantAppNotifier;
import com.android.systemui.statusbar.notification.InstantAppNotifier_Factory;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.NotificationEntryManagerLogger_Factory;
import com.android.systemui.statusbar.notification.NotificationFilter;
import com.android.systemui.statusbar.notification.NotificationFilter_Factory;
import com.android.systemui.statusbar.notification.NotificationSectionsFeatureManager;
import com.android.systemui.statusbar.notification.NotificationSectionsFeatureManager_Factory;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator_Factory;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import com.android.systemui.statusbar.notification.collection.NotifCollection;
import com.android.systemui.statusbar.notification.collection.NotifCollection_Factory;
import com.android.systemui.statusbar.notification.collection.NotifInflaterImpl;
import com.android.systemui.statusbar.notification.collection.NotifInflaterImpl_Factory;
import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import com.android.systemui.statusbar.notification.collection.NotifPipeline_Factory;
import com.android.systemui.statusbar.notification.collection.NotifViewBarn;
import com.android.systemui.statusbar.notification.collection.NotifViewBarn_Factory;
import com.android.systemui.statusbar.notification.collection.NotifViewManager;
import com.android.systemui.statusbar.notification.collection.NotifViewManager_Factory;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.NotificationRankingManager_Factory;
import com.android.systemui.statusbar.notification.collection.ShadeListBuilder;
import com.android.systemui.statusbar.notification.collection.ShadeListBuilder_Factory;
import com.android.systemui.statusbar.notification.collection.coalescer.GroupCoalescerLogger_Factory;
import com.android.systemui.statusbar.notification.collection.coalescer.GroupCoalescer_Factory;
import com.android.systemui.statusbar.notification.collection.coordinator.BubbleCoordinator;
import com.android.systemui.statusbar.notification.collection.coordinator.BubbleCoordinator_Factory;
import com.android.systemui.statusbar.notification.collection.coordinator.DeviceProvisionedCoordinator;
import com.android.systemui.statusbar.notification.collection.coordinator.DeviceProvisionedCoordinator_Factory;
import com.android.systemui.statusbar.notification.collection.coordinator.ForegroundCoordinator;
import com.android.systemui.statusbar.notification.collection.coordinator.ForegroundCoordinator_Factory;
import com.android.systemui.statusbar.notification.collection.coordinator.HeadsUpCoordinator;
import com.android.systemui.statusbar.notification.collection.coordinator.HeadsUpCoordinator_Factory;
import com.android.systemui.statusbar.notification.collection.coordinator.KeyguardCoordinator;
import com.android.systemui.statusbar.notification.collection.coordinator.KeyguardCoordinator_Factory;
import com.android.systemui.statusbar.notification.collection.coordinator.NotifCoordinators;
import com.android.systemui.statusbar.notification.collection.coordinator.NotifCoordinators_Factory;
import com.android.systemui.statusbar.notification.collection.coordinator.PreparationCoordinator;
import com.android.systemui.statusbar.notification.collection.coordinator.PreparationCoordinatorLogger_Factory;
import com.android.systemui.statusbar.notification.collection.coordinator.PreparationCoordinator_Factory;
import com.android.systemui.statusbar.notification.collection.coordinator.RankingCoordinator;
import com.android.systemui.statusbar.notification.collection.coordinator.RankingCoordinator_Factory;
import com.android.systemui.statusbar.notification.collection.inflation.NotificationRowBinderImpl;
import com.android.systemui.statusbar.notification.collection.inflation.NotificationRowBinderImpl_Factory;
import com.android.systemui.statusbar.notification.collection.init.NotifPipelineInitializer;
import com.android.systemui.statusbar.notification.collection.init.NotifPipelineInitializer_Factory;
import com.android.systemui.statusbar.notification.collection.listbuilder.ShadeListBuilderLogger_Factory;
import com.android.systemui.statusbar.notification.collection.notifcollection.CommonNotifCollection;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionLogger_Factory;
import com.android.systemui.statusbar.notification.collection.provider.HighPriorityProvider;
import com.android.systemui.statusbar.notification.collection.provider.HighPriorityProvider_Factory;
import com.android.systemui.statusbar.notification.dagger.C1226x481a0301;
import com.android.systemui.statusbar.notification.dagger.NotificationsModule_ProvideCommonNotifCollectionFactory;
import com.android.systemui.statusbar.notification.dagger.NotificationsModule_ProvideNotificationAlertingManagerFactory;
import com.android.systemui.statusbar.notification.dagger.NotificationsModule_ProvideNotificationEntryManagerFactory;
import com.android.systemui.statusbar.notification.dagger.NotificationsModule_ProvideNotificationGutsManagerFactory;
import com.android.systemui.statusbar.notification.dagger.NotificationsModule_ProvideNotificationLoggerFactory;
import com.android.systemui.statusbar.notification.dagger.NotificationsModule_ProvideNotificationPanelLoggerFactory;
import com.android.systemui.statusbar.notification.dagger.NotificationsModule_ProvideNotificationsControllerFactory;
import com.android.systemui.statusbar.notification.dagger.NotificationsModule_ProvideUiEventLoggerFactory;
import com.android.systemui.statusbar.notification.dagger.NotificationsModule_ProvideVisualStabilityManagerFactory;
import com.android.systemui.statusbar.notification.icon.IconBuilder_Factory;
import com.android.systemui.statusbar.notification.icon.IconManager_Factory;
import com.android.systemui.statusbar.notification.init.NotificationsController;
import com.android.systemui.statusbar.notification.init.NotificationsControllerImpl;
import com.android.systemui.statusbar.notification.init.NotificationsControllerImpl_Factory;
import com.android.systemui.statusbar.notification.init.NotificationsControllerStub_Factory;
import com.android.systemui.statusbar.notification.interruption.BypassHeadsUpNotifier;
import com.android.systemui.statusbar.notification.interruption.BypassHeadsUpNotifier_Factory;
import com.android.systemui.statusbar.notification.interruption.NotificationAlertingManager;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProviderImpl;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProviderImpl_Factory;
import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import com.android.systemui.statusbar.notification.logging.NotificationLogger_ExpansionStateLogger_Factory;
import com.android.systemui.statusbar.notification.logging.NotificationPanelLogger;
import com.android.systemui.statusbar.notification.people.NotificationPersonExtractorPluginBoundary;
import com.android.systemui.statusbar.notification.people.NotificationPersonExtractorPluginBoundary_Factory;
import com.android.systemui.statusbar.notification.people.PeopleHubDataSourceImpl;
import com.android.systemui.statusbar.notification.people.PeopleHubDataSourceImpl_Factory;
import com.android.systemui.statusbar.notification.people.PeopleHubViewAdapter;
import com.android.systemui.statusbar.notification.people.PeopleHubViewAdapterImpl;
import com.android.systemui.statusbar.notification.people.PeopleHubViewAdapterImpl_Factory;
import com.android.systemui.statusbar.notification.people.PeopleHubViewModelFactoryDataSourceImpl;
import com.android.systemui.statusbar.notification.people.PeopleHubViewModelFactoryDataSourceImpl_Factory;
import com.android.systemui.statusbar.notification.people.PeopleNotificationIdentifierImpl;
import com.android.systemui.statusbar.notification.people.PeopleNotificationIdentifierImpl_Factory;
import com.android.systemui.statusbar.notification.row.ActivatableNotificationView;
import com.android.systemui.statusbar.notification.row.ActivatableNotificationViewController;
import com.android.systemui.statusbar.notification.row.ActivatableNotificationViewController_Factory;
import com.android.systemui.statusbar.notification.row.ChannelEditorDialogController;
import com.android.systemui.statusbar.notification.row.ChannelEditorDialogController_Factory;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow.OnExpandClickListener;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRowController;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRowController_Factory;
import com.android.systemui.statusbar.notification.row.ExpandableOutlineViewController;
import com.android.systemui.statusbar.notification.row.ExpandableOutlineViewController_Factory;
import com.android.systemui.statusbar.notification.row.ExpandableViewController;
import com.android.systemui.statusbar.notification.row.ExpandableViewController_Factory;
import com.android.systemui.statusbar.notification.row.NotifBindPipeline;
import com.android.systemui.statusbar.notification.row.NotifBindPipelineInitializer_Factory;
import com.android.systemui.statusbar.notification.row.NotifBindPipelineLogger_Factory;
import com.android.systemui.statusbar.notification.row.NotifBindPipeline_Factory;
import com.android.systemui.statusbar.notification.row.NotifInflationErrorManager;
import com.android.systemui.statusbar.notification.row.NotifInflationErrorManager_Factory;
import com.android.systemui.statusbar.notification.row.NotifRemoteViewCache;
import com.android.systemui.statusbar.notification.row.NotifRemoteViewCacheImpl_Factory;
import com.android.systemui.statusbar.notification.row.NotificationBlockingHelperManager;
import com.android.systemui.statusbar.notification.row.NotificationContentInflater;
import com.android.systemui.statusbar.notification.row.NotificationContentInflater_Factory;
import com.android.systemui.statusbar.notification.row.NotificationGutsManager;
import com.android.systemui.statusbar.notification.row.NotificationRowContentBinder.InflationCallback;
import com.android.systemui.statusbar.notification.row.RowContentBindStage;
import com.android.systemui.statusbar.notification.row.RowContentBindStageLogger_Factory;
import com.android.systemui.statusbar.notification.row.RowContentBindStage_Factory;
import com.android.systemui.statusbar.notification.row.RowInflaterTask_Factory;
import com.android.systemui.statusbar.notification.row.dagger.C1292x3e2d0aca;
import com.android.systemui.statusbar.notification.row.dagger.C1293xdc9a80a2;
import com.android.systemui.statusbar.notification.row.dagger.C1294xc255c3ca;
import com.android.systemui.statusbar.notification.row.dagger.ExpandableNotificationRowComponent;
import com.android.systemui.statusbar.notification.row.dagger.NotificationRowComponent;
import com.android.systemui.statusbar.notification.stack.ForegroundServiceSectionController;
import com.android.systemui.statusbar.notification.stack.ForegroundServiceSectionController_Factory;
import com.android.systemui.statusbar.notification.stack.NotificationRoundnessManager;
import com.android.systemui.statusbar.notification.stack.NotificationRoundnessManager_Factory;
import com.android.systemui.statusbar.notification.stack.NotificationSectionsManager;
import com.android.systemui.statusbar.notification.stack.NotificationSectionsManager_Factory;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout;
import com.android.systemui.statusbar.p008tv.TvStatusBar;
import com.android.systemui.statusbar.p008tv.TvStatusBar_Factory;
import com.android.systemui.statusbar.phone.AutoHideController;
import com.android.systemui.statusbar.phone.AutoTileManager_Factory;
import com.android.systemui.statusbar.phone.BiometricUnlockController;
import com.android.systemui.statusbar.phone.BiometricUnlockController_Factory;
import com.android.systemui.statusbar.phone.DarkIconDispatcherImpl;
import com.android.systemui.statusbar.phone.DarkIconDispatcherImpl_Factory;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.statusbar.phone.DozeParameters_Factory;
import com.android.systemui.statusbar.phone.DozeScrimController;
import com.android.systemui.statusbar.phone.DozeScrimController_Factory;
import com.android.systemui.statusbar.phone.DozeServiceHost;
import com.android.systemui.statusbar.phone.DozeServiceHost_Factory;
import com.android.systemui.statusbar.phone.HeadsUpManagerPhone;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.phone.KeyguardBypassController_Factory;
import com.android.systemui.statusbar.phone.KeyguardDismissUtil;
import com.android.systemui.statusbar.phone.KeyguardDismissUtil_Factory;
import com.android.systemui.statusbar.phone.KeyguardEnvironmentImpl;
import com.android.systemui.statusbar.phone.KeyguardEnvironmentImpl_Factory;
import com.android.systemui.statusbar.phone.KeyguardLiftController;
import com.android.systemui.statusbar.phone.LightBarController;
import com.android.systemui.statusbar.phone.LightBarController_Factory;
import com.android.systemui.statusbar.phone.LightsOutNotifController;
import com.android.systemui.statusbar.phone.LightsOutNotifController_Factory;
import com.android.systemui.statusbar.phone.LockIcon;
import com.android.systemui.statusbar.phone.LockscreenGestureLogger;
import com.android.systemui.statusbar.phone.LockscreenGestureLogger_Factory;
import com.android.systemui.statusbar.phone.LockscreenLockIconController;
import com.android.systemui.statusbar.phone.LockscreenLockIconController_Factory;
import com.android.systemui.statusbar.phone.LockscreenWallpaper;
import com.android.systemui.statusbar.phone.LockscreenWallpaper_Factory;
import com.android.systemui.statusbar.phone.ManagedProfileControllerImpl;
import com.android.systemui.statusbar.phone.ManagedProfileControllerImpl_Factory;
import com.android.systemui.statusbar.phone.NavigationBarFragment;
import com.android.systemui.statusbar.phone.NavigationModeController;
import com.android.systemui.statusbar.phone.NavigationModeController_Factory;
import com.android.systemui.statusbar.phone.NotificationGroupAlertTransferHelper;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.phone.NotificationGroupManager_Factory;
import com.android.systemui.statusbar.phone.NotificationPanelView;
import com.android.systemui.statusbar.phone.NotificationPanelViewController;
import com.android.systemui.statusbar.phone.NotificationPanelViewController_Factory;
import com.android.systemui.statusbar.phone.NotificationShadeWindowController;
import com.android.systemui.statusbar.phone.NotificationShadeWindowController_Factory;
import com.android.systemui.statusbar.phone.NotificationShadeWindowView;
import com.android.systemui.statusbar.phone.NotificationShadeWindowViewController;
import com.android.systemui.statusbar.phone.PhoneStatusBarPolicy_Factory;
import com.android.systemui.statusbar.phone.ScrimController;
import com.android.systemui.statusbar.phone.ScrimController_Factory;
import com.android.systemui.statusbar.phone.ShadeController;
import com.android.systemui.statusbar.phone.ShadeControllerImpl;
import com.android.systemui.statusbar.phone.ShadeControllerImpl_Factory;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.statusbar.phone.StatusBarIconControllerImpl;
import com.android.systemui.statusbar.phone.StatusBarIconControllerImpl_Factory;
import com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager;
import com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager_Factory;
import com.android.systemui.statusbar.phone.StatusBarNotificationActivityStarter_Builder_Factory;
import com.android.systemui.statusbar.phone.StatusBarRemoteInputCallback;
import com.android.systemui.statusbar.phone.StatusBarRemoteInputCallback_Factory;
import com.android.systemui.statusbar.phone.StatusBarTouchableRegionManager;
import com.android.systemui.statusbar.phone.StatusBarTouchableRegionManager_Factory;
import com.android.systemui.statusbar.phone.StatusBarWindowController;
import com.android.systemui.statusbar.phone.StatusBarWindowController_Factory;
import com.android.systemui.statusbar.phone.dagger.C1628x3053f5c5;
import com.android.systemui.statusbar.phone.dagger.StatusBarComponent;
import com.android.systemui.statusbar.phone.dagger.StatusBarPhoneModule_ProvideStatusBarFactory;
import com.android.systemui.statusbar.phone.dagger.StatusBarViewModule_GetNotificationPanelViewFactory;
import com.android.systemui.statusbar.policy.AccessibilityController;
import com.android.systemui.statusbar.policy.AccessibilityController_Factory;
import com.android.systemui.statusbar.policy.AccessibilityManagerWrapper;
import com.android.systemui.statusbar.policy.AccessibilityManagerWrapper_Factory;
import com.android.systemui.statusbar.policy.BatteryControllerImpl;
import com.android.systemui.statusbar.policy.BatteryControllerImpl_Factory;
import com.android.systemui.statusbar.policy.BluetoothControllerImpl;
import com.android.systemui.statusbar.policy.BluetoothControllerImpl_Factory;
import com.android.systemui.statusbar.policy.CastControllerImpl;
import com.android.systemui.statusbar.policy.CastControllerImpl_Factory;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.DataSaverController;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.policy.DeviceProvisionedControllerImpl;
import com.android.systemui.statusbar.policy.DeviceProvisionedControllerImpl_Factory;
import com.android.systemui.statusbar.policy.ExtensionControllerImpl;
import com.android.systemui.statusbar.policy.ExtensionControllerImpl_Factory;
import com.android.systemui.statusbar.policy.FlashlightControllerImpl;
import com.android.systemui.statusbar.policy.FlashlightControllerImpl_Factory;
import com.android.systemui.statusbar.policy.HotspotControllerImpl;
import com.android.systemui.statusbar.policy.HotspotControllerImpl_Factory;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.statusbar.policy.KeyguardStateControllerImpl;
import com.android.systemui.statusbar.policy.KeyguardStateControllerImpl_Factory;
import com.android.systemui.statusbar.policy.LocationControllerImpl;
import com.android.systemui.statusbar.policy.LocationControllerImpl_Factory;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.statusbar.policy.NetworkControllerImpl;
import com.android.systemui.statusbar.policy.NetworkControllerImpl_Factory;
import com.android.systemui.statusbar.policy.NextAlarmController;
import com.android.systemui.statusbar.policy.NextAlarmControllerImpl;
import com.android.systemui.statusbar.policy.NextAlarmControllerImpl_Factory;
import com.android.systemui.statusbar.policy.RemoteInputQuickSettingsDisabler;
import com.android.systemui.statusbar.policy.RemoteInputQuickSettingsDisabler_Factory;
import com.android.systemui.statusbar.policy.RemoteInputUriController;
import com.android.systemui.statusbar.policy.RemoteInputUriController_Factory;
import com.android.systemui.statusbar.policy.RotationLockControllerImpl;
import com.android.systemui.statusbar.policy.RotationLockControllerImpl_Factory;
import com.android.systemui.statusbar.policy.SecurityControllerImpl;
import com.android.systemui.statusbar.policy.SecurityControllerImpl_Factory;
import com.android.systemui.statusbar.policy.SensorPrivacyControllerImpl;
import com.android.systemui.statusbar.policy.SensorPrivacyControllerImpl_Factory;
import com.android.systemui.statusbar.policy.SmartReplyConstants;
import com.android.systemui.statusbar.policy.SmartReplyConstants_Factory;
import com.android.systemui.statusbar.policy.UserInfoController;
import com.android.systemui.statusbar.policy.UserInfoControllerImpl;
import com.android.systemui.statusbar.policy.UserInfoControllerImpl_Factory;
import com.android.systemui.statusbar.policy.UserSwitcherController;
import com.android.systemui.statusbar.policy.UserSwitcherController_Factory;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.statusbar.policy.ZenModeControllerImpl;
import com.android.systemui.statusbar.policy.ZenModeControllerImpl_Factory;
import com.android.systemui.theme.ThemeOverlayController;
import com.android.systemui.theme.ThemeOverlayController_Factory;
import com.android.systemui.toast.ToastUI;
import com.android.systemui.toast.ToastUI_Factory;
import com.android.systemui.tracing.ProtoTracer;
import com.android.systemui.tracing.ProtoTracer_Factory;
import com.android.systemui.tuner.TunablePadding.TunablePaddingService;
import com.android.systemui.tuner.TunablePadding_TunablePaddingService_Factory;
import com.android.systemui.tuner.TunerActivity;
import com.android.systemui.tuner.TunerActivity_Factory;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.tuner.TunerServiceImpl;
import com.android.systemui.tuner.TunerServiceImpl_Factory;
import com.android.systemui.util.C1736xf2fddc0a;
import com.android.systemui.util.C1737x240b4695;
import com.android.systemui.util.DeviceConfigProxy;
import com.android.systemui.util.DeviceConfigProxy_Factory;
import com.android.systemui.util.FloatingContentCoordinator;
import com.android.systemui.util.FloatingContentCoordinator_Factory;
import com.android.systemui.util.InjectionInflationController;
import com.android.systemui.util.InjectionInflationController.ViewAttributeProvider;
import com.android.systemui.util.InjectionInflationController.ViewCreator;
import com.android.systemui.util.InjectionInflationController.ViewInstanceCreator;
import com.android.systemui.util.InjectionInflationController_Factory;
import com.android.systemui.util.concurrency.ConcurrencyModule_ProvideBackgroundDelayableExecutorFactory;
import com.android.systemui.util.concurrency.ConcurrencyModule_ProvideBackgroundExecutorFactory;
import com.android.systemui.util.concurrency.ConcurrencyModule_ProvideBgHandlerFactory;
import com.android.systemui.util.concurrency.ConcurrencyModule_ProvideBgLooperFactory;
import com.android.systemui.util.concurrency.ConcurrencyModule_ProvideExecutorFactory;
import com.android.systemui.util.concurrency.ConcurrencyModule_ProvideMainDelayableExecutorFactory;
import com.android.systemui.util.concurrency.ConcurrencyModule_ProvideMainExecutorFactory;
import com.android.systemui.util.concurrency.ConcurrencyModule_ProvideMainHandlerFactory;
import com.android.systemui.util.concurrency.ConcurrencyModule_ProvideMainLooperFactory;
import com.android.systemui.util.concurrency.ConcurrencyModule_ProvideUiBackgroundExecutorFactory;
import com.android.systemui.util.concurrency.DelayableExecutor;
import com.android.systemui.util.leak.GarbageMonitor;
import com.android.systemui.util.leak.GarbageMonitor_Factory;
import com.android.systemui.util.leak.GarbageMonitor_MemoryTile_Factory;
import com.android.systemui.util.leak.GarbageMonitor_Service_Factory;
import com.android.systemui.util.leak.LeakDetector;
import com.android.systemui.util.leak.LeakReporter;
import com.android.systemui.util.leak.LeakReporter_Factory;
import com.android.systemui.util.sensors.AsyncSensorManager;
import com.android.systemui.util.sensors.AsyncSensorManager_Factory;
import com.android.systemui.util.sensors.ProximitySensor_Factory;
import com.android.systemui.util.time.DateFormatUtil_Factory;
import com.android.systemui.util.time.SystemClock;
import com.android.systemui.util.time.SystemClockImpl_Factory;
import com.android.systemui.util.wakelock.DelayedWakeLock_Builder_Factory;
import com.android.systemui.util.wakelock.WakeLock_Builder_Factory;
import com.android.systemui.volume.VolumeDialogComponent;
import com.android.systemui.volume.VolumeDialogComponent_Factory;
import com.android.systemui.volume.VolumeDialogControllerImpl;
import com.android.systemui.volume.VolumeDialogControllerImpl_Factory;
import com.android.systemui.volume.VolumeUI;
import com.android.systemui.volume.VolumeUI_Factory;
import dagger.Lazy;
import dagger.internal.DelegateFactory;
import dagger.internal.DoubleCheck;
import dagger.internal.Factory;
import dagger.internal.InstanceFactory;
import dagger.internal.MapProviderFactory;
import dagger.internal.Preconditions;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executor;
import javax.inject.Provider;

/* renamed from: com.android.systemui.tv.DaggerTvSystemUIRootComponent */
public final class DaggerTvSystemUIRootComponent implements TvSystemUIRootComponent {
    private static final Provider ABSENT_JDK_OPTIONAL_PROVIDER = InstanceFactory.create(Optional.empty());
    /* access modifiers changed from: private */
    public Provider<AccessibilityController> accessibilityControllerProvider;
    /* access modifiers changed from: private */
    public Provider<AccessibilityManagerWrapper> accessibilityManagerWrapperProvider;
    private GlobalScreenshot_ActionProxyReceiver_Factory actionProxyReceiverProvider;
    private Provider<ActivityIntentHelper> activityIntentHelperProvider;
    /* access modifiers changed from: private */
    public Provider<ActivityStarterDelegate> activityStarterDelegateProvider;
    private AirplaneModeTile_Factory airplaneModeTileProvider;
    /* access modifiers changed from: private */
    public Provider<AppOpsControllerImpl> appOpsControllerImplProvider;
    private Provider<AssistHandleBehaviorController> assistHandleBehaviorControllerProvider;
    private Provider assistHandleLikeHomeBehaviorProvider;
    private Provider assistHandleOffBehaviorProvider;
    private Provider assistHandleReminderExpBehaviorProvider;
    private AssistHandleService_Factory assistHandleServiceProvider;
    /* access modifiers changed from: private */
    public Provider<AssistManager> assistManagerProvider;
    /* access modifiers changed from: private */
    public Provider<AsyncSensorManager> asyncSensorManagerProvider;
    private Provider<AuthController> authControllerProvider;
    private AutoAddTracker_Factory autoAddTrackerProvider;
    private AutoTileManager_Factory autoTileManagerProvider;
    /* access modifiers changed from: private */
    public Provider<BatteryControllerImpl> batteryControllerImplProvider;
    private BatterySaverTile_Factory batterySaverTileProvider;
    /* access modifiers changed from: private */
    public Provider<SystemClock> bindSystemClockProvider;
    private Provider<BiometricUnlockController> biometricUnlockControllerProvider;
    /* access modifiers changed from: private */
    public Provider<BluetoothControllerImpl> bluetoothControllerImplProvider;
    private BluetoothTile_Factory bluetoothTileProvider;
    private Provider<BlurUtils> blurUtilsProvider;
    private Provider<BootCompleteCacheImpl> bootCompleteCacheImplProvider;
    private BrightnessDialog_Factory brightnessDialogProvider;
    /* access modifiers changed from: private */
    public Provider<BroadcastDispatcher> broadcastDispatcherProvider;
    private Provider<BubbleCoordinator> bubbleCoordinatorProvider;
    private Provider<BubbleData> bubbleDataProvider;
    private BubbleOverflowActivity_Factory bubbleOverflowActivityProvider;
    private WakeLock_Builder_Factory builderProvider;
    private DelayedWakeLock_Builder_Factory builderProvider2;
    private Provider<com.android.systemui.statusbar.phone.StatusBarNotificationActivityStarter.Builder> builderProvider3;
    private Provider<BypassHeadsUpNotifier> bypassHeadsUpNotifierProvider;
    /* access modifiers changed from: private */
    public Provider<CastControllerImpl> castControllerImplProvider;
    private CastTile_Factory castTileProvider;
    private CellularTile_Factory cellularTileProvider;
    /* access modifiers changed from: private */
    public Provider<ChannelEditorDialogController> channelEditorDialogControllerProvider;
    /* access modifiers changed from: private */
    public Provider<ClockManager> clockManagerProvider;
    private ColorInversionTile_Factory colorInversionTileProvider;
    /* access modifiers changed from: private */
    public Context context;
    private Provider<ContextComponentResolver> contextComponentResolverProvider;
    /* access modifiers changed from: private */
    public Provider<Context> contextProvider;
    private Provider<ControlsBindingControllerImpl> controlsBindingControllerImplProvider;
    private Provider<ControlsControllerImpl> controlsControllerImplProvider;
    private ControlsFavoritingActivity_Factory controlsFavoritingActivityProvider;
    private Provider<ControlsListingControllerImpl> controlsListingControllerImplProvider;
    private ControlsProviderSelectorActivity_Factory controlsProviderSelectorActivityProvider;
    private ControlsRequestDialog_Factory controlsRequestDialogProvider;
    private Provider<ControlsUiControllerImpl> controlsUiControllerImplProvider;
    private ConversationNotificationProcessor_Factory conversationNotificationProcessorProvider;
    /* access modifiers changed from: private */
    public Provider<DarkIconDispatcherImpl> darkIconDispatcherImplProvider;
    private DataSaverTile_Factory dataSaverTileProvider;
    private DateFormatUtil_Factory dateFormatUtilProvider;
    private Provider<DeviceConfigHelper> deviceConfigHelperProvider;
    /* access modifiers changed from: private */
    public Provider<DeviceProvisionedControllerImpl> deviceProvisionedControllerImplProvider;
    private Provider<DeviceProvisionedCoordinator> deviceProvisionedCoordinatorProvider;
    private Provider<DismissCallbackRegistry> dismissCallbackRegistryProvider;
    /* access modifiers changed from: private */
    public Provider<DisplayController> displayControllerProvider;
    /* access modifiers changed from: private */
    public Provider<DisplayImeController> displayImeControllerProvider;
    private DndTile_Factory dndTileProvider;
    /* access modifiers changed from: private */
    public Provider<DockManagerImpl> dockManagerImplProvider;
    private DozeFactory_Factory dozeFactoryProvider;
    /* access modifiers changed from: private */
    public Provider<DozeLog> dozeLogProvider;
    private DozeLogger_Factory dozeLoggerProvider;
    /* access modifiers changed from: private */
    public Provider<DozeParameters> dozeParametersProvider;
    private Provider<DozeScrimController> dozeScrimControllerProvider;
    private Provider<DozeServiceHost> dozeServiceHostProvider;
    private DozeService_Factory dozeServiceProvider;
    /* access modifiers changed from: private */
    public Provider<DumpManager> dumpManagerProvider;
    private DynamicChildBindController_Factory dynamicChildBindControllerProvider;
    /* access modifiers changed from: private */
    public Provider<DynamicPrivacyController> dynamicPrivacyControllerProvider;
    /* access modifiers changed from: private */
    public Provider<EnhancedEstimatesImpl> enhancedEstimatesImplProvider;
    private Provider<com.android.systemui.statusbar.notification.row.dagger.ExpandableNotificationRowComponent.Builder> expandableNotificationRowComponentBuilderProvider;
    private NotificationLogger_ExpansionStateLogger_Factory expansionStateLoggerProvider;
    /* access modifiers changed from: private */
    public Provider<ExtensionControllerImpl> extensionControllerImplProvider;
    /* access modifiers changed from: private */
    public Provider<FalsingManagerProxy> falsingManagerProxyProvider;
    /* access modifiers changed from: private */
    public Provider<FeatureFlags> featureFlagsProvider;
    /* access modifiers changed from: private */
    public Provider<FlashlightControllerImpl> flashlightControllerImplProvider;
    private FlashlightTile_Factory flashlightTileProvider;
    private Provider<FloatingContentCoordinator> floatingContentCoordinatorProvider;
    private Provider<ForegroundCoordinator> foregroundCoordinatorProvider;
    /* access modifiers changed from: private */
    public Provider<ForegroundServiceController> foregroundServiceControllerProvider;
    /* access modifiers changed from: private */
    public Provider<ForegroundServiceDismissalFeatureController> foregroundServiceDismissalFeatureControllerProvider;
    /* access modifiers changed from: private */
    public Provider<ForegroundServiceNotificationListener> foregroundServiceNotificationListenerProvider;
    /* access modifiers changed from: private */
    public Provider<ForegroundServiceSectionController> foregroundServiceSectionControllerProvider;
    /* access modifiers changed from: private */
    public Provider<FragmentService> fragmentServiceProvider;
    /* access modifiers changed from: private */
    public Provider<GarbageMonitor> garbageMonitorProvider;
    private Provider<GlobalActionsComponent> globalActionsComponentProvider;
    private GlobalActionsDialog_Factory globalActionsDialogProvider;
    private GlobalActionsImpl_Factory globalActionsImplProvider;
    private Provider<GlobalScreenshotLegacy> globalScreenshotLegacyProvider;
    private Provider<GlobalScreenshot> globalScreenshotProvider;
    private GroupCoalescerLogger_Factory groupCoalescerLoggerProvider;
    private GroupCoalescer_Factory groupCoalescerProvider;
    private Provider<HeadsUpCoordinator> headsUpCoordinatorProvider;
    private Provider<HighPriorityProvider> highPriorityProvider;
    /* access modifiers changed from: private */
    public Provider<HotspotControllerImpl> hotspotControllerImplProvider;
    private HotspotTile_Factory hotspotTileProvider;
    private IconBuilder_Factory iconBuilderProvider;
    private IconManager_Factory iconManagerProvider;
    private ImageWallpaper_Factory imageWallpaperProvider;
    private Provider<InitController> initControllerProvider;
    /* access modifiers changed from: private */
    public Provider<InjectionInflationController> injectionInflationControllerProvider;
    private Provider<InstantAppNotifier> instantAppNotifierProvider;
    /* access modifiers changed from: private */
    public Provider<KeyguardBypassController> keyguardBypassControllerProvider;
    private Provider<KeyguardCoordinator> keyguardCoordinatorProvider;
    /* access modifiers changed from: private */
    public Provider<KeyguardDismissUtil> keyguardDismissUtilProvider;
    /* access modifiers changed from: private */
    public Provider<KeyguardEnvironmentImpl> keyguardEnvironmentImplProvider;
    private Provider<KeyguardIndicationController> keyguardIndicationControllerProvider;
    private Provider<KeyguardLifecyclesDispatcher> keyguardLifecyclesDispatcherProvider;
    /* access modifiers changed from: private */
    public Provider<KeyguardSecurityModel> keyguardSecurityModelProvider;
    private KeyguardService_Factory keyguardServiceProvider;
    /* access modifiers changed from: private */
    public Provider<KeyguardStateControllerImpl> keyguardStateControllerImplProvider;
    /* access modifiers changed from: private */
    public Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider;
    private Provider<LatencyTester> latencyTesterProvider;
    /* access modifiers changed from: private */
    public Provider<LeakReporter> leakReporterProvider;
    /* access modifiers changed from: private */
    public Provider<LightBarController> lightBarControllerProvider;
    private Provider<LightsOutNotifController> lightsOutNotifControllerProvider;
    /* access modifiers changed from: private */
    public Provider<LocationControllerImpl> locationControllerImplProvider;
    private LocationTile_Factory locationTileProvider;
    /* access modifiers changed from: private */
    public Provider<LockscreenGestureLogger> lockscreenGestureLoggerProvider;
    private Provider<LockscreenLockIconController> lockscreenLockIconControllerProvider;
    private Provider<LockscreenWallpaper> lockscreenWallpaperProvider;
    /* access modifiers changed from: private */
    public Provider<ManagedProfileControllerImpl> managedProfileControllerImplProvider;
    private Provider<Map<Class<?>, Provider<Activity>>> mapOfClassOfAndProviderOfActivityProvider;
    private Provider<Map<Class<?>, Provider<BroadcastReceiver>>> mapOfClassOfAndProviderOfBroadcastReceiverProvider;
    private Provider<Map<Class<?>, Provider<RecentsImplementation>>> mapOfClassOfAndProviderOfRecentsImplementationProvider;
    private Provider<Map<Class<?>, Provider<Service>>> mapOfClassOfAndProviderOfServiceProvider;
    private Provider<Map<Class<?>, Provider<SystemUI>>> mapOfClassOfAndProviderOfSystemUIProvider;
    private Provider<MediaArtworkProcessor> mediaArtworkProcessorProvider;
    private GarbageMonitor_MemoryTile_Factory memoryTileProvider;
    /* access modifiers changed from: private */
    public Provider<NavigationModeController> navigationModeControllerProvider;
    /* access modifiers changed from: private */
    public Provider<NetworkControllerImpl> networkControllerImplProvider;
    /* access modifiers changed from: private */
    public Provider<BubbleController> newBubbleControllerProvider;
    private Provider<KeyguardViewMediator> newKeyguardViewMediatorProvider;
    /* access modifiers changed from: private */
    public Provider<NextAlarmControllerImpl> nextAlarmControllerImplProvider;
    private NfcTile_Factory nfcTileProvider;
    private NightDisplayTile_Factory nightDisplayTileProvider;
    private NotifBindPipelineInitializer_Factory notifBindPipelineInitializerProvider;
    private NotifBindPipelineLogger_Factory notifBindPipelineLoggerProvider;
    private Provider<NotifBindPipeline> notifBindPipelineProvider;
    private NotifCollectionLogger_Factory notifCollectionLoggerProvider;
    /* access modifiers changed from: private */
    public Provider<NotifCollection> notifCollectionProvider;
    private Provider<NotifCoordinators> notifCoordinatorsProvider;
    private Provider<NotifInflaterImpl> notifInflaterImplProvider;
    private Provider<NotifInflationErrorManager> notifInflationErrorManagerProvider;
    private Provider<NotifPipelineInitializer> notifPipelineInitializerProvider;
    /* access modifiers changed from: private */
    public Provider<NotifPipeline> notifPipelineProvider;
    private NotifRemoteViewCacheImpl_Factory notifRemoteViewCacheImplProvider;
    private Provider<NotifViewBarn> notifViewBarnProvider;
    private Provider<NotifViewManager> notifViewManagerProvider;
    private Provider<NotificationContentInflater> notificationContentInflaterProvider;
    private NotificationEntryManagerLogger_Factory notificationEntryManagerLoggerProvider;
    /* access modifiers changed from: private */
    public Provider<NotificationFilter> notificationFilterProvider;
    /* access modifiers changed from: private */
    public Provider<NotificationGroupManager> notificationGroupManagerProvider;
    private Provider<NotificationInterruptStateProviderImpl> notificationInterruptStateProviderImplProvider;
    /* access modifiers changed from: private */
    public Provider<NotificationLockscreenUserManagerImpl> notificationLockscreenUserManagerImplProvider;
    private Provider<NotificationPersonExtractorPluginBoundary> notificationPersonExtractorPluginBoundaryProvider;
    private NotificationRankingManager_Factory notificationRankingManagerProvider;
    /* access modifiers changed from: private */
    public Provider<NotificationRoundnessManager> notificationRoundnessManagerProvider;
    private Provider<NotificationRowBinderImpl> notificationRowBinderImplProvider;
    private Provider<com.android.systemui.statusbar.notification.row.dagger.NotificationRowComponent.Builder> notificationRowComponentBuilderProvider;
    private NotificationSectionsFeatureManager_Factory notificationSectionsFeatureManagerProvider;
    /* access modifiers changed from: private */
    public Provider<NotificationShadeDepthController> notificationShadeDepthControllerProvider;
    /* access modifiers changed from: private */
    public Provider<NotificationShadeWindowController> notificationShadeWindowControllerProvider;
    /* access modifiers changed from: private */
    public Provider<NotificationWakeUpCoordinator> notificationWakeUpCoordinatorProvider;
    private Provider<NotificationsControllerImpl> notificationsControllerImplProvider;
    private NotificationsControllerStub_Factory notificationsControllerStubProvider;
    private Provider<Optional<ControlsFavoritePersistenceWrapper>> optionalOfControlsFavoritePersistenceWrapperProvider;
    private Provider<Optional<Divider>> optionalOfDividerProvider;
    private Provider<Optional<Lazy<Recents>>> optionalOfLazyOfRecentsProvider;
    private Provider<Optional<Lazy<StatusBar>>> optionalOfLazyOfStatusBarProvider;
    private Provider<Optional<Recents>> optionalOfRecentsProvider;
    private Provider<Optional<StatusBar>> optionalOfStatusBarProvider;
    private Provider<OverviewProxyRecentsImpl> overviewProxyRecentsImplProvider;
    /* access modifiers changed from: private */
    public Provider<OverviewProxyService> overviewProxyServiceProvider;
    private Provider<PeopleHubDataSourceImpl> peopleHubDataSourceImplProvider;
    /* access modifiers changed from: private */
    public Provider<PeopleHubViewAdapterImpl> peopleHubViewAdapterImplProvider;
    private Provider<PeopleHubViewModelFactoryDataSourceImpl> peopleHubViewModelFactoryDataSourceImplProvider;
    private Provider<PeopleNotificationIdentifierImpl> peopleNotificationIdentifierImplProvider;
    private Provider<PhoneStateMonitor> phoneStateMonitorProvider;
    private PhoneStatusBarPolicy_Factory phoneStatusBarPolicyProvider;
    private PipBoundsHandler_Factory pipBoundsHandlerProvider;
    /* access modifiers changed from: private */
    public Provider<PipManager> pipManagerProvider;
    private PipMenuActivity_Factory pipMenuActivityProvider;
    private PipSnapAlgorithm_Factory pipSnapAlgorithmProvider;
    private Provider<PipSurfaceTransactionHelper> pipSurfaceTransactionHelperProvider;
    private Provider<PipUI> pipUIProvider;
    /* access modifiers changed from: private */
    public Provider<PluginDependencyProvider> pluginDependencyProvider;
    /* access modifiers changed from: private */
    public Provider<PowerNotificationWarnings> powerNotificationWarningsProvider;
    private Provider<PowerUI> powerUIProvider;
    private PreparationCoordinatorLogger_Factory preparationCoordinatorLoggerProvider;
    private Provider<PreparationCoordinator> preparationCoordinatorProvider;
    /* access modifiers changed from: private */
    public Provider<ProtoTracer> protoTracerProvider;
    /* access modifiers changed from: private */
    public Provider<AccessibilityManager> provideAccessibilityManagerProvider;
    /* access modifiers changed from: private */
    public Provider<ActivityManager> provideActivityManagerProvider;
    /* access modifiers changed from: private */
    public Provider<ActivityManagerWrapper> provideActivityManagerWrapperProvider;
    /* access modifiers changed from: private */
    public Provider<AlarmManager> provideAlarmManagerProvider;
    /* access modifiers changed from: private */
    public Provider<Boolean> provideAllowNotificationLongPressProvider;
    private DependencyProvider_ProvideAlwaysOnDisplayPolicyFactory provideAlwaysOnDisplayPolicyProvider;
    private DependencyProvider_ProvideAmbientDisplayConfigurationFactory provideAmbientDisplayConfigurationProvider;
    private Provider provideAssistHandleBehaviorControllerMapProvider;
    private AssistModule_ProvideAssistHandleViewControllerFactory provideAssistHandleViewControllerProvider;
    private Provider<AssistUtils> provideAssistUtilsProvider;
    private Provider<AudioManager> provideAudioManagerProvider;
    /* access modifiers changed from: private */
    public Provider<AutoHideController> provideAutoHideControllerProvider;
    private Provider<DelayableExecutor> provideBackgroundDelayableExecutorProvider;
    /* access modifiers changed from: private */
    public Provider<Executor> provideBackgroundExecutorProvider;
    private Provider<Handler> provideBackgroundHandlerProvider;
    /* access modifiers changed from: private */
    public ConcurrencyModule_ProvideBgHandlerFactory provideBgHandlerProvider;
    /* access modifiers changed from: private */
    public Provider<Looper> provideBgLooperProvider;
    /* access modifiers changed from: private */
    public Provider<CommandQueue> provideCommandQueueProvider;
    private Provider<CommonNotifCollection> provideCommonNotifCollectionProvider;
    /* access modifiers changed from: private */
    public Provider<ConfigurationController> provideConfigurationControllerProvider;
    private Provider<ConnectivityManager> provideConnectivityManagagerProvider;
    private Provider<ContentResolver> provideContentResolverProvider;
    /* access modifiers changed from: private */
    public Provider<DataSaverController> provideDataSaverControllerProvider;
    private Provider<DevicePolicyManager> provideDevicePolicyManagerProvider;
    /* access modifiers changed from: private */
    public Provider<DevicePolicyManagerWrapper> provideDevicePolicyManagerWrapperProvider;
    /* access modifiers changed from: private */
    public SystemServicesModule_ProvideDisplayIdFactory provideDisplayIdProvider;
    /* access modifiers changed from: private */
    public Provider<DisplayMetrics> provideDisplayMetricsProvider;
    /* access modifiers changed from: private */
    public Provider<Divider> provideDividerProvider;
    private Provider<LogBuffer> provideDozeLogBufferProvider;
    private Provider<Executor> provideExecutorProvider;
    private DependencyProvider_ProvideHandlerFactory provideHandlerProvider;
    /* access modifiers changed from: private */
    public Provider<HeadsUpManagerPhone> provideHeadsUpManagerPhoneProvider;
    private Provider<IActivityManager> provideIActivityManagerProvider;
    private Provider<IBatteryStats> provideIBatteryStatsProvider;
    private Provider<IDreamManager> provideIDreamManagerProvider;
    /* access modifiers changed from: private */
    public Provider<INotificationManager> provideINotificationManagerProvider;
    private Provider<IPackageManager> provideIPackageManagerProvider;
    /* access modifiers changed from: private */
    public Provider<IStatusBarService> provideIStatusBarServiceProvider;
    /* access modifiers changed from: private */
    public Provider<IWindowManager> provideIWindowManagerProvider;
    private Provider<KeyguardLiftController> provideKeyguardLiftControllerProvider;
    private Provider<KeyguardManager> provideKeyguardManagerProvider;
    /* access modifiers changed from: private */
    public Provider<LatencyTracker> provideLatencyTrackerProvider;
    private Provider<LauncherApps> provideLauncherAppsProvider;
    /* access modifiers changed from: private */
    public Provider<LeakDetector> provideLeakDetectorProvider;
    /* access modifiers changed from: private */
    public Provider<String> provideLeakReportEmailProvider;
    /* access modifiers changed from: private */
    public Provider<LocalBluetoothManager> provideLocalBluetoothControllerProvider;
    private DependencyProvider_ProvideLockPatternUtilsFactory provideLockPatternUtilsProvider;
    private Provider<LogcatEchoTracker> provideLogcatEchoTrackerProvider;
    private Provider<DelayableExecutor> provideMainDelayableExecutorProvider;
    private ConcurrencyModule_ProvideMainExecutorFactory provideMainExecutorProvider;
    /* access modifiers changed from: private */
    public ConcurrencyModule_ProvideMainHandlerFactory provideMainHandlerProvider;
    /* access modifiers changed from: private */
    public Provider<MetricsLogger> provideMetricsLoggerProvider;
    /* access modifiers changed from: private */
    public Provider<NavigationBarController> provideNavigationBarControllerProvider;
    /* access modifiers changed from: private */
    public Provider<NightDisplayListener> provideNightDisplayListenerProvider;
    private Provider<NotifRemoteViewCache> provideNotifRemoteViewCacheProvider;
    /* access modifiers changed from: private */
    public Provider<NotificationAlertingManager> provideNotificationAlertingManagerProvider;
    /* access modifiers changed from: private */
    public Provider<NotificationBlockingHelperManager> provideNotificationBlockingHelperManagerProvider;
    /* access modifiers changed from: private */
    public Provider<NotificationEntryManager> provideNotificationEntryManagerProvider;
    /* access modifiers changed from: private */
    public Provider<NotificationGroupAlertTransferHelper> provideNotificationGroupAlertTransferHelperProvider;
    /* access modifiers changed from: private */
    public Provider<NotificationGutsManager> provideNotificationGutsManagerProvider;
    /* access modifiers changed from: private */
    public Provider<NotificationListener> provideNotificationListenerProvider;
    /* access modifiers changed from: private */
    public Provider<NotificationLogger> provideNotificationLoggerProvider;
    private Provider<NotificationManager> provideNotificationManagerProvider;
    /* access modifiers changed from: private */
    public Provider<NotificationMediaManager> provideNotificationMediaManagerProvider;
    private DependencyProvider_ProvideNotificationMessagingUtilFactory provideNotificationMessagingUtilProvider;
    private Provider<NotificationPanelLogger> provideNotificationPanelLoggerProvider;
    /* access modifiers changed from: private */
    public Provider<NotificationRemoteInputManager> provideNotificationRemoteInputManagerProvider;
    /* access modifiers changed from: private */
    public Provider<NotificationViewHierarchyManager> provideNotificationViewHierarchyManagerProvider;
    private Provider<NotificationsController> provideNotificationsControllerProvider;
    private Provider<LogBuffer> provideNotificationsLogBufferProvider;
    private Provider<PackageManager> providePackageManagerProvider;
    /* access modifiers changed from: private */
    public Provider<PackageManagerWrapper> providePackageManagerWrapperProvider;
    /* access modifiers changed from: private */
    public Provider<PluginManager> providePluginManagerProvider;
    /* access modifiers changed from: private */
    public Provider<PowerManager> providePowerManagerProvider;
    private Provider<LogBuffer> provideQuickSettingsLogBufferProvider;
    private RecentsModule_ProvideRecentsImplFactory provideRecentsImplProvider;
    /* access modifiers changed from: private */
    public Provider<Recents> provideRecentsProvider;
    private SystemServicesModule_ProvideResourcesFactory provideResourcesProvider;
    /* access modifiers changed from: private */
    public Provider<SensorPrivacyManager> provideSensorPrivacyManagerProvider;
    private DependencyProvider_ProvideSharePreferencesFactory provideSharePreferencesProvider;
    private Provider<ShortcutManager> provideShortcutManagerProvider;
    /* access modifiers changed from: private */
    public Provider<SmartReplyController> provideSmartReplyControllerProvider;
    /* access modifiers changed from: private */
    public Provider<StatusBar> provideStatusBarProvider;
    /* access modifiers changed from: private */
    public Provider<SysUiState> provideSysUiStateProvider;
    private Provider<Clock> provideSystemClockProvider;
    private Provider<TelecomManager> provideTelecomManagerProvider;
    private Provider<TelephonyManager> provideTelephonyManagerProvider;
    /* access modifiers changed from: private */
    public Provider<Handler> provideTimeTickHandlerProvider;
    private Provider<TrustManager> provideTrustManagerProvider;
    private Provider<Executor> provideUiBackgroundExecutorProvider;
    /* access modifiers changed from: private */
    public Provider<UiEventLogger> provideUiEventLoggerProvider;
    private Provider<UserManager> provideUserManagerProvider;
    private Provider<Vibrator> provideVibratorProvider;
    /* access modifiers changed from: private */
    public Provider<VisualStabilityManager> provideVisualStabilityManagerProvider;
    private SystemServicesModule_ProvideWallpaperManagerFactory provideWallpaperManagerProvider;
    private Provider<WindowManager> provideWindowManagerProvider;
    /* access modifiers changed from: private */
    public Provider<LayoutInflater> providerLayoutInflaterProvider;
    private Provider<Choreographer> providesChoreographerProvider;
    private DependencyProvider_ProvidesViewMediatorCallbackFactory providesViewMediatorCallbackProvider;
    private ProximitySensor_Factory proximitySensorProvider;
    /* access modifiers changed from: private */
    public Provider<PulseExpansionHandler> pulseExpansionHandlerProvider;
    private Provider<QSFactoryImpl> qSFactoryImplProvider;
    private QSLogger_Factory qSLoggerProvider;
    /* access modifiers changed from: private */
    public Provider<QSTileHost> qSTileHostProvider;
    private Provider<RankingCoordinator> rankingCoordinatorProvider;
    /* access modifiers changed from: private */
    public Provider<RecordingController> recordingControllerProvider;
    private RecordingService_Factory recordingServiceProvider;
    /* access modifiers changed from: private */
    public Provider<RemoteInputQuickSettingsDisabler> remoteInputQuickSettingsDisablerProvider;
    private Provider<RemoteInputUriController> remoteInputUriControllerProvider;
    /* access modifiers changed from: private */
    public Provider<RotationLockControllerImpl> rotationLockControllerImplProvider;
    private RotationLockTile_Factory rotationLockTileProvider;
    private RowContentBindStageLogger_Factory rowContentBindStageLoggerProvider;
    private Provider<RowContentBindStage> rowContentBindStageProvider;
    private Provider<ScreenDecorations> screenDecorationsProvider;
    /* access modifiers changed from: private */
    public Provider<ScreenLifecycle> screenLifecycleProvider;
    private ScreenPinningRequest_Factory screenPinningRequestProvider;
    private ScreenRecordDialog_Factory screenRecordDialogProvider;
    private ScreenRecordTile_Factory screenRecordTileProvider;
    private ScreenshotNotificationsController_Factory screenshotNotificationsControllerProvider;
    private Provider<ScrimController> scrimControllerProvider;
    /* access modifiers changed from: private */
    public Provider<SecurityControllerImpl> securityControllerImplProvider;
    /* access modifiers changed from: private */
    public Provider<SensorPrivacyControllerImpl> sensorPrivacyControllerImplProvider;
    private Provider<GarbageMonitor.Service> serviceProvider;
    /* access modifiers changed from: private */
    public Provider<ShadeControllerImpl> shadeControllerImplProvider;
    private ShadeListBuilderLogger_Factory shadeListBuilderLoggerProvider;
    private Provider<ShadeListBuilder> shadeListBuilderProvider;
    private Provider<ShortcutKeyDispatcher> shortcutKeyDispatcherProvider;
    private Provider<SizeCompatModeActivityController> sizeCompatModeActivityControllerProvider;
    private Provider<SliceBroadcastRelayHandler> sliceBroadcastRelayHandlerProvider;
    /* access modifiers changed from: private */
    public Provider<SmartReplyConstants> smartReplyConstantsProvider;
    private Provider<com.android.systemui.statusbar.phone.dagger.StatusBarComponent.Builder> statusBarComponentBuilderProvider;
    /* access modifiers changed from: private */
    public Provider<StatusBarIconControllerImpl> statusBarIconControllerImplProvider;
    private Provider<StatusBarKeyguardViewManager> statusBarKeyguardViewManagerProvider;
    /* access modifiers changed from: private */
    public Provider<StatusBarRemoteInputCallback> statusBarRemoteInputCallbackProvider;
    /* access modifiers changed from: private */
    public Provider<StatusBarStateControllerImpl> statusBarStateControllerImplProvider;
    /* access modifiers changed from: private */
    public Provider<StatusBarTouchableRegionManager> statusBarTouchableRegionManagerProvider;
    /* access modifiers changed from: private */
    public Provider<StatusBarWindowController> statusBarWindowControllerProvider;
    /* access modifiers changed from: private */
    public Provider<SuperStatusBarViewFactory> superStatusBarViewFactoryProvider;
    private Provider<SystemActions> systemActionsProvider;
    private SystemUIAuxiliaryDumpService_Factory systemUIAuxiliaryDumpServiceProvider;
    private SystemUIService_Factory systemUIServiceProvider;
    /* access modifiers changed from: private */
    public Provider<SystemWindows> systemWindowsProvider;
    /* access modifiers changed from: private */
    public Provider<SysuiColorExtractor> sysuiColorExtractorProvider;
    private TakeScreenshotService_Factory takeScreenshotServiceProvider;
    private Provider<ThemeOverlayController> themeOverlayControllerProvider;
    private Provider<ToastUI> toastUIProvider;
    private Provider<TransactionPool> transactionPoolProvider;
    /* access modifiers changed from: private */
    public Provider<TunablePaddingService> tunablePaddingServiceProvider;
    /* access modifiers changed from: private */
    public Provider<TunerServiceImpl> tunerServiceImplProvider;
    private Provider<com.android.systemui.pip.p005tv.dagger.TvPipComponent.Builder> tvPipComponentBuilderProvider;
    private Provider<TvStatusBar> tvStatusBarProvider;
    private Provider<TvSystemUIRootComponent> tvSystemUIRootComponentProvider;
    private UiModeNightTile_Factory uiModeNightTileProvider;
    /* access modifiers changed from: private */
    public Provider<UiOffloadThread> uiOffloadThreadProvider;
    /* access modifiers changed from: private */
    public Provider<UserInfoControllerImpl> userInfoControllerImplProvider;
    /* access modifiers changed from: private */
    public Provider<UserSwitcherController> userSwitcherControllerProvider;
    private UserTile_Factory userTileProvider;
    /* access modifiers changed from: private */
    public Provider<VibratorHelper> vibratorHelperProvider;
    private Provider<VolumeDialogComponent> volumeDialogComponentProvider;
    /* access modifiers changed from: private */
    public Provider<VolumeDialogControllerImpl> volumeDialogControllerImplProvider;
    private Provider<VolumeUI> volumeUIProvider;
    /* access modifiers changed from: private */
    public Provider<WakefulnessLifecycle> wakefulnessLifecycleProvider;
    private WifiTile_Factory wifiTileProvider;
    private Provider<WindowMagnification> windowMagnificationProvider;
    private WorkLockActivity_Factory workLockActivityProvider;
    private WorkModeTile_Factory workModeTileProvider;
    /* access modifiers changed from: private */
    public Provider<ZenModeControllerImpl> zenModeControllerImplProvider;

    /* renamed from: com.android.systemui.tv.DaggerTvSystemUIRootComponent$Builder */
    private static final class Builder implements com.android.systemui.p009tv.TvSystemUIRootComponent.Builder {
        /* access modifiers changed from: private */
        public Context context;
        /* access modifiers changed from: private */
        public DependencyProvider dependencyProvider;

        private Builder() {
        }

        public TvSystemUIRootComponent build() {
            if (this.dependencyProvider == null) {
                this.dependencyProvider = new DependencyProvider();
            }
            if (this.context != null) {
                return new DaggerTvSystemUIRootComponent(this);
            }
            StringBuilder sb = new StringBuilder();
            sb.append(Context.class.getCanonicalName());
            sb.append(" must be set");
            throw new IllegalStateException(sb.toString());
        }

        public Builder context(Context context2) {
            Preconditions.checkNotNull(context2);
            this.context = context2;
            return this;
        }
    }

    /* renamed from: com.android.systemui.tv.DaggerTvSystemUIRootComponent$DependencyInjectorImpl */
    private final class DependencyInjectorImpl implements DependencyInjector {
        private DependencyInjectorImpl() {
        }

        public void createSystemUI(Dependency dependency) {
            injectDependency(dependency);
        }

        private Dependency injectDependency(Dependency dependency) {
            Dependency_MembersInjector.injectMDumpManager(dependency, (DumpManager) DaggerTvSystemUIRootComponent.this.dumpManagerProvider.get());
            Dependency_MembersInjector.injectMActivityStarter(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.activityStarterDelegateProvider));
            Dependency_MembersInjector.injectMBroadcastDispatcher(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.broadcastDispatcherProvider));
            Dependency_MembersInjector.injectMAsyncSensorManager(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.asyncSensorManagerProvider));
            Dependency_MembersInjector.injectMBluetoothController(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.bluetoothControllerImplProvider));
            Dependency_MembersInjector.injectMLocationController(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.locationControllerImplProvider));
            Dependency_MembersInjector.injectMRotationLockController(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.rotationLockControllerImplProvider));
            Dependency_MembersInjector.injectMNetworkController(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.networkControllerImplProvider));
            Dependency_MembersInjector.injectMZenModeController(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.zenModeControllerImplProvider));
            Dependency_MembersInjector.injectMHotspotController(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.hotspotControllerImplProvider));
            Dependency_MembersInjector.injectMCastController(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.castControllerImplProvider));
            Dependency_MembersInjector.injectMFlashlightController(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.flashlightControllerImplProvider));
            Dependency_MembersInjector.injectMUserSwitcherController(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.userSwitcherControllerProvider));
            Dependency_MembersInjector.injectMUserInfoController(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.userInfoControllerImplProvider));
            Dependency_MembersInjector.injectMKeyguardMonitor(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.keyguardStateControllerImplProvider));
            Dependency_MembersInjector.injectMKeyguardUpdateMonitor(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.keyguardUpdateMonitorProvider));
            Dependency_MembersInjector.injectMBatteryController(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.batteryControllerImplProvider));
            Dependency_MembersInjector.injectMNightDisplayListener(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.provideNightDisplayListenerProvider));
            Dependency_MembersInjector.injectMManagedProfileController(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.managedProfileControllerImplProvider));
            Dependency_MembersInjector.injectMNextAlarmController(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.nextAlarmControllerImplProvider));
            Dependency_MembersInjector.injectMDataSaverController(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.provideDataSaverControllerProvider));
            Dependency_MembersInjector.injectMAccessibilityController(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.accessibilityControllerProvider));
            Dependency_MembersInjector.injectMDeviceProvisionedController(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.deviceProvisionedControllerImplProvider));
            Dependency_MembersInjector.injectMPluginManager(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.providePluginManagerProvider));
            Dependency_MembersInjector.injectMAssistManager(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.assistManagerProvider));
            Dependency_MembersInjector.injectMSecurityController(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.securityControllerImplProvider));
            Dependency_MembersInjector.injectMLeakDetector(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.provideLeakDetectorProvider));
            Dependency_MembersInjector.injectMLeakReporter(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.leakReporterProvider));
            Dependency_MembersInjector.injectMGarbageMonitor(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.garbageMonitorProvider));
            Dependency_MembersInjector.injectMTunerService(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.tunerServiceImplProvider));
            Dependency_MembersInjector.injectMNotificationShadeWindowController(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.notificationShadeWindowControllerProvider));
            Dependency_MembersInjector.injectMTempStatusBarWindowController(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.statusBarWindowControllerProvider));
            Dependency_MembersInjector.injectMDarkIconDispatcher(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.darkIconDispatcherImplProvider));
            Dependency_MembersInjector.injectMConfigurationController(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.provideConfigurationControllerProvider));
            Dependency_MembersInjector.injectMStatusBarIconController(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.statusBarIconControllerImplProvider));
            Dependency_MembersInjector.injectMScreenLifecycle(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.screenLifecycleProvider));
            Dependency_MembersInjector.injectMWakefulnessLifecycle(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.wakefulnessLifecycleProvider));
            Dependency_MembersInjector.injectMFragmentService(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.fragmentServiceProvider));
            Dependency_MembersInjector.injectMExtensionController(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.extensionControllerImplProvider));
            Dependency_MembersInjector.injectMPluginDependencyProvider(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.pluginDependencyProvider));
            Dependency_MembersInjector.injectMLocalBluetoothManager(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.provideLocalBluetoothControllerProvider));
            Dependency_MembersInjector.injectMVolumeDialogController(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.volumeDialogControllerImplProvider));
            Dependency_MembersInjector.injectMMetricsLogger(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.provideMetricsLoggerProvider));
            Dependency_MembersInjector.injectMAccessibilityManagerWrapper(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.accessibilityManagerWrapperProvider));
            Dependency_MembersInjector.injectMSysuiColorExtractor(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.sysuiColorExtractorProvider));
            Dependency_MembersInjector.injectMTunablePaddingService(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.tunablePaddingServiceProvider));
            Dependency_MembersInjector.injectMForegroundServiceController(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.foregroundServiceControllerProvider));
            Dependency_MembersInjector.injectMUiOffloadThread(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.uiOffloadThreadProvider));
            Dependency_MembersInjector.injectMWarningsUI(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.powerNotificationWarningsProvider));
            Dependency_MembersInjector.injectMLightBarController(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.lightBarControllerProvider));
            Dependency_MembersInjector.injectMIWindowManager(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.provideIWindowManagerProvider));
            Dependency_MembersInjector.injectMOverviewProxyService(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.overviewProxyServiceProvider));
            Dependency_MembersInjector.injectMNavBarModeController(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.navigationModeControllerProvider));
            Dependency_MembersInjector.injectMEnhancedEstimates(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.enhancedEstimatesImplProvider));
            Dependency_MembersInjector.injectMVibratorHelper(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.vibratorHelperProvider));
            Dependency_MembersInjector.injectMIStatusBarService(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.provideIStatusBarServiceProvider));
            Dependency_MembersInjector.injectMDisplayMetrics(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.provideDisplayMetricsProvider));
            Dependency_MembersInjector.injectMLockscreenGestureLogger(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.lockscreenGestureLoggerProvider));
            Dependency_MembersInjector.injectMKeyguardEnvironment(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.keyguardEnvironmentImplProvider));
            Dependency_MembersInjector.injectMShadeController(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.shadeControllerImplProvider));
            Dependency_MembersInjector.injectMNotificationRemoteInputManagerCallback(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.statusBarRemoteInputCallbackProvider));
            Dependency_MembersInjector.injectMAppOpsController(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.appOpsControllerImplProvider));
            Dependency_MembersInjector.injectMNavigationBarController(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.provideNavigationBarControllerProvider));
            Dependency_MembersInjector.injectMStatusBarStateController(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.statusBarStateControllerImplProvider));
            Dependency_MembersInjector.injectMNotificationLockscreenUserManager(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.notificationLockscreenUserManagerImplProvider));
            Dependency_MembersInjector.injectMNotificationGroupAlertTransferHelper(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.provideNotificationGroupAlertTransferHelperProvider));
            Dependency_MembersInjector.injectMNotificationGroupManager(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.notificationGroupManagerProvider));
            Dependency_MembersInjector.injectMVisualStabilityManager(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.provideVisualStabilityManagerProvider));
            Dependency_MembersInjector.injectMNotificationGutsManager(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.provideNotificationGutsManagerProvider));
            Dependency_MembersInjector.injectMNotificationMediaManager(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.provideNotificationMediaManagerProvider));
            Dependency_MembersInjector.injectMNotificationBlockingHelperManager(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.provideNotificationBlockingHelperManagerProvider));
            Dependency_MembersInjector.injectMNotificationRemoteInputManager(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.provideNotificationRemoteInputManagerProvider));
            Dependency_MembersInjector.injectMSmartReplyConstants(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.smartReplyConstantsProvider));
            Dependency_MembersInjector.injectMNotificationListener(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.provideNotificationListenerProvider));
            Dependency_MembersInjector.injectMNotificationLogger(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.provideNotificationLoggerProvider));
            Dependency_MembersInjector.injectMNotificationViewHierarchyManager(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.provideNotificationViewHierarchyManagerProvider));
            Dependency_MembersInjector.injectMNotificationFilter(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.notificationFilterProvider));
            Dependency_MembersInjector.injectMKeyguardDismissUtil(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.keyguardDismissUtilProvider));
            Dependency_MembersInjector.injectMSmartReplyController(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.provideSmartReplyControllerProvider));
            Dependency_MembersInjector.injectMRemoteInputQuickSettingsDisabler(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.remoteInputQuickSettingsDisablerProvider));
            Dependency_MembersInjector.injectMBubbleController(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.newBubbleControllerProvider));
            Dependency_MembersInjector.injectMNotificationEntryManager(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.provideNotificationEntryManagerProvider));
            Dependency_MembersInjector.injectMNotificationAlertingManager(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.provideNotificationAlertingManagerProvider));
            Dependency_MembersInjector.injectMSensorPrivacyManager(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.provideSensorPrivacyManagerProvider));
            Dependency_MembersInjector.injectMAutoHideController(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.provideAutoHideControllerProvider));
            Dependency_MembersInjector.injectMForegroundServiceNotificationListener(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.foregroundServiceNotificationListenerProvider));
            Dependency_MembersInjector.injectMBgLooper(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.provideBgLooperProvider));
            Dependency_MembersInjector.injectMBgHandler(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.provideBgHandlerProvider));
            Dependency_MembersInjector.injectMMainLooper(dependency, DoubleCheck.lazy(ConcurrencyModule_ProvideMainLooperFactory.create()));
            Dependency_MembersInjector.injectMMainHandler(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.provideMainHandlerProvider));
            Dependency_MembersInjector.injectMTimeTickHandler(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.provideTimeTickHandlerProvider));
            Dependency_MembersInjector.injectMLeakReportEmail(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.provideLeakReportEmailProvider));
            Dependency_MembersInjector.injectMClockManager(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.clockManagerProvider));
            Dependency_MembersInjector.injectMActivityManagerWrapper(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.provideActivityManagerWrapperProvider));
            Dependency_MembersInjector.injectMDevicePolicyManagerWrapper(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.provideDevicePolicyManagerWrapperProvider));
            Dependency_MembersInjector.injectMPackageManagerWrapper(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.providePackageManagerWrapperProvider));
            Dependency_MembersInjector.injectMSensorPrivacyController(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.sensorPrivacyControllerImplProvider));
            Dependency_MembersInjector.injectMDockManager(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.dockManagerImplProvider));
            Dependency_MembersInjector.injectMChannelEditorDialogController(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.channelEditorDialogControllerProvider));
            Dependency_MembersInjector.injectMINotificationManager(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.provideINotificationManagerProvider));
            Dependency_MembersInjector.injectMSysUiStateFlagsContainer(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.provideSysUiStateProvider));
            Dependency_MembersInjector.injectMAlarmManager(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.provideAlarmManagerProvider));
            Dependency_MembersInjector.injectMKeyguardSecurityModel(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.keyguardSecurityModelProvider));
            Dependency_MembersInjector.injectMDozeParameters(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.dozeParametersProvider));
            Dependency_MembersInjector.injectMWallpaperManager(dependency, DoubleCheck.lazy(SystemServicesModule_ProvideIWallPaperManagerFactory.create()));
            Dependency_MembersInjector.injectMCommandQueue(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.provideCommandQueueProvider));
            Dependency_MembersInjector.injectMRecents(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.provideRecentsProvider));
            Dependency_MembersInjector.injectMStatusBar(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.provideStatusBarProvider));
            Dependency_MembersInjector.injectMDisplayController(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.displayControllerProvider));
            Dependency_MembersInjector.injectMSystemWindows(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.systemWindowsProvider));
            Dependency_MembersInjector.injectMDisplayImeController(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.displayImeControllerProvider));
            Dependency_MembersInjector.injectMRecordingController(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.recordingControllerProvider));
            Dependency_MembersInjector.injectMProtoTracer(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.protoTracerProvider));
            Dependency_MembersInjector.injectMDivider(dependency, DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.provideDividerProvider));
            return dependency;
        }
    }

    /* renamed from: com.android.systemui.tv.DaggerTvSystemUIRootComponent$ExpandableNotificationRowComponentBuilder */
    private final class ExpandableNotificationRowComponentBuilder implements com.android.systemui.statusbar.notification.row.dagger.ExpandableNotificationRowComponent.Builder {
        /* access modifiers changed from: private */
        public ExpandableNotificationRow expandableNotificationRow;
        /* access modifiers changed from: private */
        public InflationCallback inflationCallback;
        /* access modifiers changed from: private */
        public NotificationEntry notificationEntry;
        /* access modifiers changed from: private */
        public Runnable onDismissRunnable;
        /* access modifiers changed from: private */
        public OnExpandClickListener onExpandClickListener;
        /* access modifiers changed from: private */
        public RowContentBindStage rowContentBindStage;

        private ExpandableNotificationRowComponentBuilder() {
        }

        public ExpandableNotificationRowComponent build() {
            String str = " must be set";
            if (this.expandableNotificationRow == null) {
                StringBuilder sb = new StringBuilder();
                sb.append(ExpandableNotificationRow.class.getCanonicalName());
                sb.append(str);
                throw new IllegalStateException(sb.toString());
            } else if (this.notificationEntry == null) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(NotificationEntry.class.getCanonicalName());
                sb2.append(str);
                throw new IllegalStateException(sb2.toString());
            } else if (this.onDismissRunnable == null) {
                StringBuilder sb3 = new StringBuilder();
                sb3.append(Runnable.class.getCanonicalName());
                sb3.append(str);
                throw new IllegalStateException(sb3.toString());
            } else if (this.rowContentBindStage == null) {
                StringBuilder sb4 = new StringBuilder();
                sb4.append(RowContentBindStage.class.getCanonicalName());
                sb4.append(str);
                throw new IllegalStateException(sb4.toString());
            } else if (this.inflationCallback == null) {
                StringBuilder sb5 = new StringBuilder();
                sb5.append(InflationCallback.class.getCanonicalName());
                sb5.append(str);
                throw new IllegalStateException(sb5.toString());
            } else if (this.onExpandClickListener != null) {
                return new ExpandableNotificationRowComponentImpl(this);
            } else {
                StringBuilder sb6 = new StringBuilder();
                sb6.append(OnExpandClickListener.class.getCanonicalName());
                sb6.append(str);
                throw new IllegalStateException(sb6.toString());
            }
        }

        public ExpandableNotificationRowComponentBuilder expandableNotificationRow(ExpandableNotificationRow expandableNotificationRow2) {
            Preconditions.checkNotNull(expandableNotificationRow2);
            this.expandableNotificationRow = expandableNotificationRow2;
            return this;
        }

        public ExpandableNotificationRowComponentBuilder notificationEntry(NotificationEntry notificationEntry2) {
            Preconditions.checkNotNull(notificationEntry2);
            this.notificationEntry = notificationEntry2;
            return this;
        }

        public ExpandableNotificationRowComponentBuilder onDismissRunnable(Runnable runnable) {
            Preconditions.checkNotNull(runnable);
            this.onDismissRunnable = runnable;
            return this;
        }

        public ExpandableNotificationRowComponentBuilder rowContentBindStage(RowContentBindStage rowContentBindStage2) {
            Preconditions.checkNotNull(rowContentBindStage2);
            this.rowContentBindStage = rowContentBindStage2;
            return this;
        }

        public ExpandableNotificationRowComponentBuilder inflationCallback(InflationCallback inflationCallback2) {
            Preconditions.checkNotNull(inflationCallback2);
            this.inflationCallback = inflationCallback2;
            return this;
        }

        public ExpandableNotificationRowComponentBuilder onExpandClickListener(OnExpandClickListener onExpandClickListener2) {
            Preconditions.checkNotNull(onExpandClickListener2);
            this.onExpandClickListener = onExpandClickListener2;
            return this;
        }
    }

    /* renamed from: com.android.systemui.tv.DaggerTvSystemUIRootComponent$ExpandableNotificationRowComponentImpl */
    private final class ExpandableNotificationRowComponentImpl implements ExpandableNotificationRowComponent {
        private ActivatableNotificationViewController_Factory activatableNotificationViewControllerProvider;
        private Provider<ExpandableNotificationRowController> expandableNotificationRowControllerProvider;
        private Provider<ExpandableNotificationRow> expandableNotificationRowProvider;
        private ExpandableOutlineViewController_Factory expandableOutlineViewControllerProvider;
        private ExpandableViewController_Factory expandableViewControllerProvider;
        private Provider<InflationCallback> inflationCallbackProvider;
        private Provider<NotificationEntry> notificationEntryProvider;
        private Provider<Runnable> onDismissRunnableProvider;
        private Provider<OnExpandClickListener> onExpandClickListenerProvider;
        private C1292x3e2d0aca provideAppNameProvider;
        private C1293xdc9a80a2 provideNotificationKeyProvider;
        private C1294xc255c3ca provideStatusBarNotificationProvider;
        private Provider<RowContentBindStage> rowContentBindStageProvider;

        private ExpandableNotificationRowComponentImpl(ExpandableNotificationRowComponentBuilder expandableNotificationRowComponentBuilder) {
            initialize(expandableNotificationRowComponentBuilder);
        }

        private void initialize(ExpandableNotificationRowComponentBuilder expandableNotificationRowComponentBuilder) {
            Factory create = InstanceFactory.create(expandableNotificationRowComponentBuilder.expandableNotificationRow);
            this.expandableNotificationRowProvider = create;
            ExpandableViewController_Factory create2 = ExpandableViewController_Factory.create(create);
            this.expandableViewControllerProvider = create2;
            ExpandableOutlineViewController_Factory create3 = ExpandableOutlineViewController_Factory.create(this.expandableNotificationRowProvider, create2);
            this.expandableOutlineViewControllerProvider = create3;
            this.activatableNotificationViewControllerProvider = ActivatableNotificationViewController_Factory.create(this.expandableNotificationRowProvider, create3, DaggerTvSystemUIRootComponent.this.provideAccessibilityManagerProvider, DaggerTvSystemUIRootComponent.this.falsingManagerProxyProvider);
            Factory create4 = InstanceFactory.create(expandableNotificationRowComponentBuilder.notificationEntry);
            this.notificationEntryProvider = create4;
            this.provideStatusBarNotificationProvider = C1294xc255c3ca.create(create4);
            this.provideAppNameProvider = C1292x3e2d0aca.create(DaggerTvSystemUIRootComponent.this.contextProvider, this.provideStatusBarNotificationProvider);
            this.provideNotificationKeyProvider = C1293xdc9a80a2.create(this.provideStatusBarNotificationProvider);
            this.rowContentBindStageProvider = InstanceFactory.create(expandableNotificationRowComponentBuilder.rowContentBindStage);
            this.onExpandClickListenerProvider = InstanceFactory.create(expandableNotificationRowComponentBuilder.onExpandClickListener);
            this.inflationCallbackProvider = InstanceFactory.create(expandableNotificationRowComponentBuilder.inflationCallback);
            this.onDismissRunnableProvider = InstanceFactory.create(expandableNotificationRowComponentBuilder.onDismissRunnable);
            this.expandableNotificationRowControllerProvider = DoubleCheck.provider(ExpandableNotificationRowController_Factory.create(this.expandableNotificationRowProvider, this.activatableNotificationViewControllerProvider, DaggerTvSystemUIRootComponent.this.provideNotificationMediaManagerProvider, DaggerTvSystemUIRootComponent.this.providePluginManagerProvider, DaggerTvSystemUIRootComponent.this.bindSystemClockProvider, this.provideAppNameProvider, this.provideNotificationKeyProvider, DaggerTvSystemUIRootComponent.this.keyguardBypassControllerProvider, DaggerTvSystemUIRootComponent.this.notificationGroupManagerProvider, this.rowContentBindStageProvider, DaggerTvSystemUIRootComponent.this.provideNotificationLoggerProvider, DaggerTvSystemUIRootComponent.this.provideHeadsUpManagerPhoneProvider, this.onExpandClickListenerProvider, DaggerTvSystemUIRootComponent.this.statusBarStateControllerImplProvider, this.inflationCallbackProvider, DaggerTvSystemUIRootComponent.this.provideNotificationGutsManagerProvider, DaggerTvSystemUIRootComponent.this.provideAllowNotificationLongPressProvider, this.onDismissRunnableProvider, DaggerTvSystemUIRootComponent.this.falsingManagerProxyProvider));
        }

        public ExpandableNotificationRowController getExpandableNotificationRowController() {
            return (ExpandableNotificationRowController) this.expandableNotificationRowControllerProvider.get();
        }
    }

    /* renamed from: com.android.systemui.tv.DaggerTvSystemUIRootComponent$FragmentCreatorImpl */
    private final class FragmentCreatorImpl implements FragmentCreator {
        private FragmentCreatorImpl() {
        }

        private com.android.keyguard.CarrierTextController.Builder getBuilder4() {
            return new com.android.keyguard.CarrierTextController.Builder(DaggerTvSystemUIRootComponent.this.context, DaggerTvSystemUIRootComponent.this.getMainResources());
        }

        private com.android.systemui.p007qs.carrier.QSCarrierGroupController.Builder getBuilder3() {
            com.android.systemui.p007qs.carrier.QSCarrierGroupController.Builder builder = new com.android.systemui.p007qs.carrier.QSCarrierGroupController.Builder((ActivityStarter) DaggerTvSystemUIRootComponent.this.activityStarterDelegateProvider.get(), DaggerTvSystemUIRootComponent.this.getBackgroundHandler(), ConcurrencyModule_ProvideMainLooperFactory.proxyProvideMainLooper(), (NetworkController) DaggerTvSystemUIRootComponent.this.networkControllerImplProvider.get(), getBuilder4());
            return builder;
        }

        private com.android.systemui.p007qs.QuickStatusBarHeaderController.Builder getBuilder2() {
            return new com.android.systemui.p007qs.QuickStatusBarHeaderController.Builder(getBuilder3());
        }

        private com.android.systemui.p007qs.QSContainerImplController.Builder getBuilder() {
            return new com.android.systemui.p007qs.QSContainerImplController.Builder(getBuilder2());
        }

        public NavigationBarFragment createNavigationBarFragment() {
            NavigationBarFragment navigationBarFragment = new NavigationBarFragment((AccessibilityManagerWrapper) DaggerTvSystemUIRootComponent.this.accessibilityManagerWrapperProvider.get(), (DeviceProvisionedController) DaggerTvSystemUIRootComponent.this.deviceProvisionedControllerImplProvider.get(), (MetricsLogger) DaggerTvSystemUIRootComponent.this.provideMetricsLoggerProvider.get(), (AssistManager) DaggerTvSystemUIRootComponent.this.assistManagerProvider.get(), (OverviewProxyService) DaggerTvSystemUIRootComponent.this.overviewProxyServiceProvider.get(), (NavigationModeController) DaggerTvSystemUIRootComponent.this.navigationModeControllerProvider.get(), (StatusBarStateController) DaggerTvSystemUIRootComponent.this.statusBarStateControllerImplProvider.get(), (SysUiState) DaggerTvSystemUIRootComponent.this.provideSysUiStateProvider.get(), (BroadcastDispatcher) DaggerTvSystemUIRootComponent.this.broadcastDispatcherProvider.get(), (CommandQueue) DaggerTvSystemUIRootComponent.this.provideCommandQueueProvider.get(), (Divider) DaggerTvSystemUIRootComponent.this.provideDividerProvider.get(), Optional.of((Recents) DaggerTvSystemUIRootComponent.this.provideRecentsProvider.get()), DoubleCheck.lazy(DaggerTvSystemUIRootComponent.this.provideStatusBarProvider), (ShadeController) DaggerTvSystemUIRootComponent.this.shadeControllerImplProvider.get(), (NotificationRemoteInputManager) DaggerTvSystemUIRootComponent.this.provideNotificationRemoteInputManagerProvider.get(), DaggerTvSystemUIRootComponent.this.getMainHandler());
            return navigationBarFragment;
        }

        public QSFragment createQSFragment() {
            QSFragment qSFragment = new QSFragment((RemoteInputQuickSettingsDisabler) DaggerTvSystemUIRootComponent.this.remoteInputQuickSettingsDisablerProvider.get(), (InjectionInflationController) DaggerTvSystemUIRootComponent.this.injectionInflationControllerProvider.get(), (QSTileHost) DaggerTvSystemUIRootComponent.this.qSTileHostProvider.get(), (StatusBarStateController) DaggerTvSystemUIRootComponent.this.statusBarStateControllerImplProvider.get(), (CommandQueue) DaggerTvSystemUIRootComponent.this.provideCommandQueueProvider.get(), getBuilder());
            return qSFragment;
        }
    }

    /* renamed from: com.android.systemui.tv.DaggerTvSystemUIRootComponent$NotificationRowComponentBuilder */
    private final class NotificationRowComponentBuilder implements com.android.systemui.statusbar.notification.row.dagger.NotificationRowComponent.Builder {
        /* access modifiers changed from: private */
        public ActivatableNotificationView activatableNotificationView;

        private NotificationRowComponentBuilder() {
        }

        public NotificationRowComponent build() {
            if (this.activatableNotificationView != null) {
                return new NotificationRowComponentImpl(this);
            }
            StringBuilder sb = new StringBuilder();
            sb.append(ActivatableNotificationView.class.getCanonicalName());
            sb.append(" must be set");
            throw new IllegalStateException(sb.toString());
        }

        public NotificationRowComponentBuilder activatableNotificationView(ActivatableNotificationView activatableNotificationView2) {
            Preconditions.checkNotNull(activatableNotificationView2);
            this.activatableNotificationView = activatableNotificationView2;
            return this;
        }
    }

    /* renamed from: com.android.systemui.tv.DaggerTvSystemUIRootComponent$NotificationRowComponentImpl */
    private final class NotificationRowComponentImpl implements NotificationRowComponent {
        private ActivatableNotificationView activatableNotificationView;

        private NotificationRowComponentImpl(NotificationRowComponentBuilder notificationRowComponentBuilder) {
            initialize(notificationRowComponentBuilder);
        }

        private ExpandableViewController getExpandableViewController() {
            return new ExpandableViewController(this.activatableNotificationView);
        }

        private ExpandableOutlineViewController getExpandableOutlineViewController() {
            return new ExpandableOutlineViewController(this.activatableNotificationView, getExpandableViewController());
        }

        private void initialize(NotificationRowComponentBuilder notificationRowComponentBuilder) {
            this.activatableNotificationView = notificationRowComponentBuilder.activatableNotificationView;
        }

        public ActivatableNotificationViewController getActivatableNotificationViewController() {
            return new ActivatableNotificationViewController(this.activatableNotificationView, getExpandableOutlineViewController(), (AccessibilityManager) DaggerTvSystemUIRootComponent.this.provideAccessibilityManagerProvider.get(), (FalsingManager) DaggerTvSystemUIRootComponent.this.falsingManagerProxyProvider.get());
        }
    }

    /* renamed from: com.android.systemui.tv.DaggerTvSystemUIRootComponent$PresentJdkOptionalInstanceProvider */
    private static final class PresentJdkOptionalInstanceProvider<T> implements Provider<Optional<T>> {
        private final Provider<T> delegate;

        private PresentJdkOptionalInstanceProvider(Provider<T> provider) {
            Preconditions.checkNotNull(provider);
            this.delegate = provider;
        }

        public Optional<T> get() {
            return Optional.of(this.delegate.get());
        }

        /* access modifiers changed from: private */
        /* renamed from: of */
        public static <T> Provider<Optional<T>> m86of(Provider<T> provider) {
            return new PresentJdkOptionalInstanceProvider(provider);
        }
    }

    /* renamed from: com.android.systemui.tv.DaggerTvSystemUIRootComponent$PresentJdkOptionalLazyProvider */
    private static final class PresentJdkOptionalLazyProvider<T> implements Provider<Optional<Lazy<T>>> {
        private final Provider<T> delegate;

        private PresentJdkOptionalLazyProvider(Provider<T> provider) {
            Preconditions.checkNotNull(provider);
            this.delegate = provider;
        }

        public Optional<Lazy<T>> get() {
            return Optional.of(DoubleCheck.lazy(this.delegate));
        }

        /* access modifiers changed from: private */
        /* renamed from: of */
        public static <T> Provider<Optional<Lazy<T>>> m87of(Provider<T> provider) {
            return new PresentJdkOptionalLazyProvider(provider);
        }
    }

    /* renamed from: com.android.systemui.tv.DaggerTvSystemUIRootComponent$StatusBarComponentBuilder */
    private final class StatusBarComponentBuilder implements com.android.systemui.statusbar.phone.dagger.StatusBarComponent.Builder {
        /* access modifiers changed from: private */
        public NotificationShadeWindowView statusBarWindowView;

        private StatusBarComponentBuilder() {
        }

        public StatusBarComponent build() {
            if (this.statusBarWindowView != null) {
                return new StatusBarComponentImpl(this);
            }
            StringBuilder sb = new StringBuilder();
            sb.append(NotificationShadeWindowView.class.getCanonicalName());
            sb.append(" must be set");
            throw new IllegalStateException(sb.toString());
        }

        public StatusBarComponentBuilder statusBarWindowView(NotificationShadeWindowView notificationShadeWindowView) {
            Preconditions.checkNotNull(notificationShadeWindowView);
            this.statusBarWindowView = notificationShadeWindowView;
            return this;
        }
    }

    /* renamed from: com.android.systemui.tv.DaggerTvSystemUIRootComponent$StatusBarComponentImpl */
    private final class StatusBarComponentImpl implements StatusBarComponent {
        private FlingAnimationUtils_Builder_Factory builderProvider;
        private Provider<NotificationPanelView> getNotificationPanelViewProvider;
        private Provider<NotificationPanelViewController> notificationPanelViewControllerProvider;
        private NotificationShadeWindowView statusBarWindowView;
        private Provider<NotificationShadeWindowView> statusBarWindowViewProvider;

        private StatusBarComponentImpl(StatusBarComponentBuilder statusBarComponentBuilder) {
            initialize(statusBarComponentBuilder);
        }

        private void initialize(StatusBarComponentBuilder statusBarComponentBuilder) {
            this.statusBarWindowView = statusBarComponentBuilder.statusBarWindowView;
            Factory create = InstanceFactory.create(statusBarComponentBuilder.statusBarWindowView);
            this.statusBarWindowViewProvider = create;
            this.getNotificationPanelViewProvider = DoubleCheck.provider(StatusBarViewModule_GetNotificationPanelViewFactory.create(create));
            this.builderProvider = FlingAnimationUtils_Builder_Factory.create(DaggerTvSystemUIRootComponent.this.provideDisplayMetricsProvider);
            this.notificationPanelViewControllerProvider = DoubleCheck.provider(NotificationPanelViewController_Factory.create(this.getNotificationPanelViewProvider, DaggerTvSystemUIRootComponent.this.injectionInflationControllerProvider, DaggerTvSystemUIRootComponent.this.notificationWakeUpCoordinatorProvider, DaggerTvSystemUIRootComponent.this.pulseExpansionHandlerProvider, DaggerTvSystemUIRootComponent.this.dynamicPrivacyControllerProvider, DaggerTvSystemUIRootComponent.this.keyguardBypassControllerProvider, DaggerTvSystemUIRootComponent.this.falsingManagerProxyProvider, DaggerTvSystemUIRootComponent.this.shadeControllerImplProvider, DaggerTvSystemUIRootComponent.this.notificationLockscreenUserManagerImplProvider, DaggerTvSystemUIRootComponent.this.provideNotificationEntryManagerProvider, DaggerTvSystemUIRootComponent.this.keyguardStateControllerImplProvider, DaggerTvSystemUIRootComponent.this.statusBarStateControllerImplProvider, DaggerTvSystemUIRootComponent.this.dozeLogProvider, DaggerTvSystemUIRootComponent.this.dozeParametersProvider, DaggerTvSystemUIRootComponent.this.provideCommandQueueProvider, DaggerTvSystemUIRootComponent.this.vibratorHelperProvider, DaggerTvSystemUIRootComponent.this.provideLatencyTrackerProvider, DaggerTvSystemUIRootComponent.this.providePowerManagerProvider, DaggerTvSystemUIRootComponent.this.provideAccessibilityManagerProvider, DaggerTvSystemUIRootComponent.this.provideDisplayIdProvider, DaggerTvSystemUIRootComponent.this.keyguardUpdateMonitorProvider, DaggerTvSystemUIRootComponent.this.provideMetricsLoggerProvider, DaggerTvSystemUIRootComponent.this.provideActivityManagerProvider, DaggerTvSystemUIRootComponent.this.zenModeControllerImplProvider, DaggerTvSystemUIRootComponent.this.provideConfigurationControllerProvider, this.builderProvider, DaggerTvSystemUIRootComponent.this.statusBarTouchableRegionManagerProvider));
        }

        public NotificationShadeWindowViewController getNotificationShadeWindowViewController() {
            NotificationShadeWindowViewController notificationShadeWindowViewController = new NotificationShadeWindowViewController((InjectionInflationController) DaggerTvSystemUIRootComponent.this.injectionInflationControllerProvider.get(), (NotificationWakeUpCoordinator) DaggerTvSystemUIRootComponent.this.notificationWakeUpCoordinatorProvider.get(), (PulseExpansionHandler) DaggerTvSystemUIRootComponent.this.pulseExpansionHandlerProvider.get(), (DynamicPrivacyController) DaggerTvSystemUIRootComponent.this.dynamicPrivacyControllerProvider.get(), (KeyguardBypassController) DaggerTvSystemUIRootComponent.this.keyguardBypassControllerProvider.get(), (FalsingManager) DaggerTvSystemUIRootComponent.this.falsingManagerProxyProvider.get(), (PluginManager) DaggerTvSystemUIRootComponent.this.providePluginManagerProvider.get(), (TunerService) DaggerTvSystemUIRootComponent.this.tunerServiceImplProvider.get(), (NotificationLockscreenUserManager) DaggerTvSystemUIRootComponent.this.notificationLockscreenUserManagerImplProvider.get(), (NotificationEntryManager) DaggerTvSystemUIRootComponent.this.provideNotificationEntryManagerProvider.get(), (KeyguardStateController) DaggerTvSystemUIRootComponent.this.keyguardStateControllerImplProvider.get(), (SysuiStatusBarStateController) DaggerTvSystemUIRootComponent.this.statusBarStateControllerImplProvider.get(), (DozeLog) DaggerTvSystemUIRootComponent.this.dozeLogProvider.get(), (DozeParameters) DaggerTvSystemUIRootComponent.this.dozeParametersProvider.get(), (CommandQueue) DaggerTvSystemUIRootComponent.this.provideCommandQueueProvider.get(), (ShadeController) DaggerTvSystemUIRootComponent.this.shadeControllerImplProvider.get(), (DockManager) DaggerTvSystemUIRootComponent.this.dockManagerImplProvider.get(), (NotificationShadeDepthController) DaggerTvSystemUIRootComponent.this.notificationShadeDepthControllerProvider.get(), this.statusBarWindowView, (NotificationPanelViewController) this.notificationPanelViewControllerProvider.get(), (SuperStatusBarViewFactory) DaggerTvSystemUIRootComponent.this.superStatusBarViewFactoryProvider.get());
            return notificationShadeWindowViewController;
        }

        public StatusBarWindowController getStatusBarWindowController() {
            return (StatusBarWindowController) DaggerTvSystemUIRootComponent.this.statusBarWindowControllerProvider.get();
        }

        public NotificationPanelViewController getNotificationPanelViewController() {
            return (NotificationPanelViewController) this.notificationPanelViewControllerProvider.get();
        }
    }

    /* renamed from: com.android.systemui.tv.DaggerTvSystemUIRootComponent$TvPipComponentBuilder */
    private final class TvPipComponentBuilder implements com.android.systemui.pip.p005tv.dagger.TvPipComponent.Builder {
        /* access modifiers changed from: private */
        public PipControlsView pipControlsView;

        private TvPipComponentBuilder() {
        }

        public TvPipComponent build() {
            if (this.pipControlsView != null) {
                return new TvPipComponentImpl(this);
            }
            StringBuilder sb = new StringBuilder();
            sb.append(PipControlsView.class.getCanonicalName());
            sb.append(" must be set");
            throw new IllegalStateException(sb.toString());
        }

        public TvPipComponentBuilder pipControlsView(PipControlsView pipControlsView2) {
            Preconditions.checkNotNull(pipControlsView2);
            this.pipControlsView = pipControlsView2;
            return this;
        }
    }

    /* renamed from: com.android.systemui.tv.DaggerTvSystemUIRootComponent$TvPipComponentImpl */
    private final class TvPipComponentImpl implements TvPipComponent {
        private PipControlsView pipControlsView;

        private TvPipComponentImpl(TvPipComponentBuilder tvPipComponentBuilder) {
            initialize(tvPipComponentBuilder);
        }

        private void initialize(TvPipComponentBuilder tvPipComponentBuilder) {
            this.pipControlsView = tvPipComponentBuilder.pipControlsView;
        }

        public PipControlsViewController getPipControlsViewController() {
            return new PipControlsViewController(this.pipControlsView, (PipManager) DaggerTvSystemUIRootComponent.this.pipManagerProvider.get(), (LayoutInflater) DaggerTvSystemUIRootComponent.this.providerLayoutInflaterProvider.get(), DaggerTvSystemUIRootComponent.this.getMainHandler());
        }
    }

    /* renamed from: com.android.systemui.tv.DaggerTvSystemUIRootComponent$ViewCreatorImpl */
    private final class ViewCreatorImpl implements ViewCreator {

        /* renamed from: com.android.systemui.tv.DaggerTvSystemUIRootComponent$ViewCreatorImpl$ViewInstanceCreatorImpl */
        private final class ViewInstanceCreatorImpl implements ViewInstanceCreator {
            private ViewAttributeProvider viewAttributeProvider;

            private ViewInstanceCreatorImpl(ViewAttributeProvider viewAttributeProvider2) {
                initialize(viewAttributeProvider2);
            }

            private NotificationSectionsManager getNotificationSectionsManager() {
                return NotificationSectionsManager_Factory.newNotificationSectionsManager((ActivityStarter) DaggerTvSystemUIRootComponent.this.activityStarterDelegateProvider.get(), (StatusBarStateController) DaggerTvSystemUIRootComponent.this.statusBarStateControllerImplProvider.get(), (ConfigurationController) DaggerTvSystemUIRootComponent.this.provideConfigurationControllerProvider.get(), (PeopleHubViewAdapter) DaggerTvSystemUIRootComponent.this.peopleHubViewAdapterImplProvider.get(), DaggerTvSystemUIRootComponent.this.getNotificationSectionsFeatureManager());
            }

            private TileQueryHelper getTileQueryHelper() {
                return new TileQueryHelper(DaggerTvSystemUIRootComponent.this.context, DaggerTvSystemUIRootComponent.this.getMainExecutor(), (Executor) DaggerTvSystemUIRootComponent.this.provideBackgroundExecutorProvider.get());
            }

            private void initialize(ViewAttributeProvider viewAttributeProvider2) {
                Preconditions.checkNotNull(viewAttributeProvider2);
                this.viewAttributeProvider = viewAttributeProvider2;
            }

            public QuickStatusBarHeader createQsHeader() {
                QuickStatusBarHeader quickStatusBarHeader = new QuickStatusBarHeader(C1737x240b4695.proxyProvideContext(this.viewAttributeProvider), C1736xf2fddc0a.proxyProvideAttributeSet(this.viewAttributeProvider), (NextAlarmController) DaggerTvSystemUIRootComponent.this.nextAlarmControllerImplProvider.get(), (ZenModeController) DaggerTvSystemUIRootComponent.this.zenModeControllerImplProvider.get(), (StatusBarIconController) DaggerTvSystemUIRootComponent.this.statusBarIconControllerImplProvider.get(), (ActivityStarter) DaggerTvSystemUIRootComponent.this.activityStarterDelegateProvider.get(), (CommandQueue) DaggerTvSystemUIRootComponent.this.provideCommandQueueProvider.get(), (BroadcastDispatcher) DaggerTvSystemUIRootComponent.this.broadcastDispatcherProvider.get());
                return quickStatusBarHeader;
            }

            public QSFooterImpl createQsFooter() {
                QSFooterImpl qSFooterImpl = new QSFooterImpl(C1737x240b4695.proxyProvideContext(this.viewAttributeProvider), C1736xf2fddc0a.proxyProvideAttributeSet(this.viewAttributeProvider), (ActivityStarter) DaggerTvSystemUIRootComponent.this.activityStarterDelegateProvider.get(), (UserInfoController) DaggerTvSystemUIRootComponent.this.userInfoControllerImplProvider.get(), (DeviceProvisionedController) DaggerTvSystemUIRootComponent.this.deviceProvisionedControllerImplProvider.get());
                return qSFooterImpl;
            }

            public NotificationStackScrollLayout createNotificationStackScrollLayout() {
                NotificationStackScrollLayout notificationStackScrollLayout = new NotificationStackScrollLayout(C1737x240b4695.proxyProvideContext(this.viewAttributeProvider), C1736xf2fddc0a.proxyProvideAttributeSet(this.viewAttributeProvider), ((Boolean) DaggerTvSystemUIRootComponent.this.provideAllowNotificationLongPressProvider.get()).booleanValue(), (NotificationRoundnessManager) DaggerTvSystemUIRootComponent.this.notificationRoundnessManagerProvider.get(), (DynamicPrivacyController) DaggerTvSystemUIRootComponent.this.dynamicPrivacyControllerProvider.get(), (SysuiStatusBarStateController) DaggerTvSystemUIRootComponent.this.statusBarStateControllerImplProvider.get(), (HeadsUpManagerPhone) DaggerTvSystemUIRootComponent.this.provideHeadsUpManagerPhoneProvider.get(), (KeyguardBypassController) DaggerTvSystemUIRootComponent.this.keyguardBypassControllerProvider.get(), (FalsingManager) DaggerTvSystemUIRootComponent.this.falsingManagerProxyProvider.get(), (NotificationLockscreenUserManager) DaggerTvSystemUIRootComponent.this.notificationLockscreenUserManagerImplProvider.get(), (NotificationGutsManager) DaggerTvSystemUIRootComponent.this.provideNotificationGutsManagerProvider.get(), (ZenModeController) DaggerTvSystemUIRootComponent.this.zenModeControllerImplProvider.get(), getNotificationSectionsManager(), (ForegroundServiceSectionController) DaggerTvSystemUIRootComponent.this.foregroundServiceSectionControllerProvider.get(), (ForegroundServiceDismissalFeatureController) DaggerTvSystemUIRootComponent.this.foregroundServiceDismissalFeatureControllerProvider.get(), (FeatureFlags) DaggerTvSystemUIRootComponent.this.featureFlagsProvider.get(), (NotifPipeline) DaggerTvSystemUIRootComponent.this.notifPipelineProvider.get(), (NotificationEntryManager) DaggerTvSystemUIRootComponent.this.provideNotificationEntryManagerProvider.get(), (NotifCollection) DaggerTvSystemUIRootComponent.this.notifCollectionProvider.get(), (UiEventLogger) DaggerTvSystemUIRootComponent.this.provideUiEventLoggerProvider.get());
                return notificationStackScrollLayout;
            }

            public NotificationShelf creatNotificationShelf() {
                return new NotificationShelf(C1737x240b4695.proxyProvideContext(this.viewAttributeProvider), C1736xf2fddc0a.proxyProvideAttributeSet(this.viewAttributeProvider), (KeyguardBypassController) DaggerTvSystemUIRootComponent.this.keyguardBypassControllerProvider.get());
            }

            public KeyguardClockSwitch createKeyguardClockSwitch() {
                KeyguardClockSwitch keyguardClockSwitch = new KeyguardClockSwitch(C1737x240b4695.proxyProvideContext(this.viewAttributeProvider), C1736xf2fddc0a.proxyProvideAttributeSet(this.viewAttributeProvider), (StatusBarStateController) DaggerTvSystemUIRootComponent.this.statusBarStateControllerImplProvider.get(), (SysuiColorExtractor) DaggerTvSystemUIRootComponent.this.sysuiColorExtractorProvider.get(), (ClockManager) DaggerTvSystemUIRootComponent.this.clockManagerProvider.get());
                return keyguardClockSwitch;
            }

            public KeyguardSliceView createKeyguardSliceView() {
                KeyguardSliceView keyguardSliceView = new KeyguardSliceView(C1737x240b4695.proxyProvideContext(this.viewAttributeProvider), C1736xf2fddc0a.proxyProvideAttributeSet(this.viewAttributeProvider), (ActivityStarter) DaggerTvSystemUIRootComponent.this.activityStarterDelegateProvider.get(), (ConfigurationController) DaggerTvSystemUIRootComponent.this.provideConfigurationControllerProvider.get(), (TunerService) DaggerTvSystemUIRootComponent.this.tunerServiceImplProvider.get(), DaggerTvSystemUIRootComponent.this.getMainResources());
                return keyguardSliceView;
            }

            public KeyguardMessageArea createKeyguardMessageArea() {
                return new KeyguardMessageArea(C1737x240b4695.proxyProvideContext(this.viewAttributeProvider), C1736xf2fddc0a.proxyProvideAttributeSet(this.viewAttributeProvider), (ConfigurationController) DaggerTvSystemUIRootComponent.this.provideConfigurationControllerProvider.get());
            }

            public LockIcon createLockIcon() {
                LockIcon lockIcon = new LockIcon(C1737x240b4695.proxyProvideContext(this.viewAttributeProvider), C1736xf2fddc0a.proxyProvideAttributeSet(this.viewAttributeProvider), (AccessibilityController) DaggerTvSystemUIRootComponent.this.accessibilityControllerProvider.get(), (KeyguardBypassController) DaggerTvSystemUIRootComponent.this.keyguardBypassControllerProvider.get(), (NotificationWakeUpCoordinator) DaggerTvSystemUIRootComponent.this.notificationWakeUpCoordinatorProvider.get(), (KeyguardStateController) DaggerTvSystemUIRootComponent.this.keyguardStateControllerImplProvider.get(), (HeadsUpManagerPhone) DaggerTvSystemUIRootComponent.this.provideHeadsUpManagerPhoneProvider.get());
                return lockIcon;
            }

            public QSPanel createQSPanel() {
                QSPanel qSPanel = new QSPanel(C1737x240b4695.proxyProvideContext(this.viewAttributeProvider), C1736xf2fddc0a.proxyProvideAttributeSet(this.viewAttributeProvider), (DumpManager) DaggerTvSystemUIRootComponent.this.dumpManagerProvider.get(), (BroadcastDispatcher) DaggerTvSystemUIRootComponent.this.broadcastDispatcherProvider.get(), DaggerTvSystemUIRootComponent.this.getQSLogger(), (NotificationMediaManager) DaggerTvSystemUIRootComponent.this.provideNotificationMediaManagerProvider.get(), DaggerTvSystemUIRootComponent.this.getMainExecutor(), (Executor) DaggerTvSystemUIRootComponent.this.provideBackgroundExecutorProvider.get(), (LocalBluetoothManager) DaggerTvSystemUIRootComponent.this.provideLocalBluetoothControllerProvider.get());
                return qSPanel;
            }

            public QuickQSPanel createQuickQSPanel() {
                QuickQSPanel quickQSPanel = new QuickQSPanel(C1737x240b4695.proxyProvideContext(this.viewAttributeProvider), C1736xf2fddc0a.proxyProvideAttributeSet(this.viewAttributeProvider), (DumpManager) DaggerTvSystemUIRootComponent.this.dumpManagerProvider.get(), (BroadcastDispatcher) DaggerTvSystemUIRootComponent.this.broadcastDispatcherProvider.get(), DaggerTvSystemUIRootComponent.this.getQSLogger(), (NotificationMediaManager) DaggerTvSystemUIRootComponent.this.provideNotificationMediaManagerProvider.get(), DaggerTvSystemUIRootComponent.this.getMainExecutor(), (Executor) DaggerTvSystemUIRootComponent.this.provideBackgroundExecutorProvider.get(), (LocalBluetoothManager) DaggerTvSystemUIRootComponent.this.provideLocalBluetoothControllerProvider.get());
                return quickQSPanel;
            }

            public QSCustomizer createQSCustomizer() {
                QSCustomizer qSCustomizer = new QSCustomizer(DaggerTvSystemUIRootComponent.this.context, C1736xf2fddc0a.proxyProvideAttributeSet(this.viewAttributeProvider), (LightBarController) DaggerTvSystemUIRootComponent.this.lightBarControllerProvider.get(), (KeyguardStateController) DaggerTvSystemUIRootComponent.this.keyguardStateControllerImplProvider.get(), (ScreenLifecycle) DaggerTvSystemUIRootComponent.this.screenLifecycleProvider.get(), getTileQueryHelper());
                return qSCustomizer;
            }
        }

        private ViewCreatorImpl() {
        }

        public ViewInstanceCreator createInstanceCreator(ViewAttributeProvider viewAttributeProvider) {
            return new ViewInstanceCreatorImpl(viewAttributeProvider);
        }
    }

    public void inject(ContentProvider contentProvider) {
    }

    private DaggerTvSystemUIRootComponent(Builder builder) {
        initialize(builder);
        initialize2(builder);
        initialize3(builder);
        initialize4(builder);
    }

    public static com.android.systemui.p009tv.TvSystemUIRootComponent.Builder builder() {
        return new Builder();
    }

    /* access modifiers changed from: private */
    public Handler getMainHandler() {
        return ConcurrencyModule_ProvideMainHandlerFactory.proxyProvideMainHandler(ConcurrencyModule_ProvideMainLooperFactory.proxyProvideMainLooper());
    }

    /* access modifiers changed from: private */
    public Handler getBackgroundHandler() {
        return ConcurrencyModule_ProvideBgHandlerFactory.proxyProvideBgHandler((Looper) this.provideBgLooperProvider.get());
    }

    /* access modifiers changed from: private */
    public Resources getMainResources() {
        return SystemServicesModule_ProvideResourcesFactory.proxyProvideResources(this.context);
    }

    /* access modifiers changed from: private */
    public NotificationSectionsFeatureManager getNotificationSectionsFeatureManager() {
        return new NotificationSectionsFeatureManager(new DeviceConfigProxy(), this.context);
    }

    /* access modifiers changed from: private */
    public QSLogger getQSLogger() {
        return new QSLogger((LogBuffer) this.provideQuickSettingsLogBufferProvider.get());
    }

    /* access modifiers changed from: private */
    public Executor getMainExecutor() {
        return ConcurrencyModule_ProvideMainExecutorFactory.proxyProvideMainExecutor(this.context);
    }

    private void initialize(Builder builder) {
        Factory create = InstanceFactory.create(builder.context);
        this.contextProvider = create;
        Provider<DumpManager> provider = DoubleCheck.provider(DumpManager_Factory.create(create));
        this.dumpManagerProvider = provider;
        this.bootCompleteCacheImplProvider = DoubleCheck.provider(BootCompleteCacheImpl_Factory.create(provider));
        this.provideConfigurationControllerProvider = DoubleCheck.provider(DependencyProvider_ProvideConfigurationControllerFactory.create(builder.dependencyProvider, this.contextProvider));
        this.provideMainHandlerProvider = ConcurrencyModule_ProvideMainHandlerFactory.create(ConcurrencyModule_ProvideMainLooperFactory.create());
        Provider<Looper> provider2 = DoubleCheck.provider(ConcurrencyModule_ProvideBgLooperFactory.create());
        this.provideBgLooperProvider = provider2;
        Provider<BroadcastDispatcher> provider3 = DoubleCheck.provider(BroadcastDispatcher_Factory.create(this.contextProvider, this.provideMainHandlerProvider, provider2, this.dumpManagerProvider));
        this.broadcastDispatcherProvider = provider3;
        this.workLockActivityProvider = WorkLockActivity_Factory.create(provider3);
        this.brightnessDialogProvider = BrightnessDialog_Factory.create(this.broadcastDispatcherProvider);
        Provider<RecordingController> provider4 = DoubleCheck.provider(RecordingController_Factory.create(this.contextProvider));
        this.recordingControllerProvider = provider4;
        this.screenRecordDialogProvider = ScreenRecordDialog_Factory.create(provider4);
        this.provideWindowManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideWindowManagerFactory.create(this.contextProvider));
        this.provideIActivityManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideIActivityManagerFactory.create());
        this.provideResourcesProvider = SystemServicesModule_ProvideResourcesFactory.create(this.contextProvider);
        this.provideAmbientDisplayConfigurationProvider = DependencyProvider_ProvideAmbientDisplayConfigurationFactory.create(builder.dependencyProvider, this.contextProvider);
        this.provideAlwaysOnDisplayPolicyProvider = DependencyProvider_ProvideAlwaysOnDisplayPolicyFactory.create(builder.dependencyProvider, this.contextProvider);
        this.providePowerManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvidePowerManagerFactory.create(this.contextProvider));
        Provider<LeakDetector> provider5 = DoubleCheck.provider(DependencyProvider_ProvideLeakDetectorFactory.create(builder.dependencyProvider));
        this.provideLeakDetectorProvider = provider5;
        Provider<TunerServiceImpl> provider6 = DoubleCheck.provider(TunerServiceImpl_Factory.create(this.contextProvider, this.provideMainHandlerProvider, provider5, this.broadcastDispatcherProvider));
        this.tunerServiceImplProvider = provider6;
        this.dozeParametersProvider = DoubleCheck.provider(DozeParameters_Factory.create(this.provideResourcesProvider, this.provideAmbientDisplayConfigurationProvider, this.provideAlwaysOnDisplayPolicyProvider, this.providePowerManagerProvider, provider6));
        this.statusBarStateControllerImplProvider = DoubleCheck.provider(StatusBarStateControllerImpl_Factory.create());
        this.provideDevicePolicyManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideDevicePolicyManagerFactory.create(this.contextProvider));
        this.provideUserManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideUserManagerFactory.create(this.contextProvider));
        this.provideIStatusBarServiceProvider = DoubleCheck.provider(SystemServicesModule_ProvideIStatusBarServiceFactory.create());
        this.provideKeyguardManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideKeyguardManagerFactory.create(this.contextProvider));
        this.deviceProvisionedControllerImplProvider = DoubleCheck.provider(DeviceProvisionedControllerImpl_Factory.create(this.contextProvider, this.provideMainHandlerProvider, this.broadcastDispatcherProvider));
        this.provideBackgroundExecutorProvider = DoubleCheck.provider(ConcurrencyModule_ProvideBackgroundExecutorFactory.create(this.provideBgLooperProvider));
        this.keyguardUpdateMonitorProvider = DoubleCheck.provider(KeyguardUpdateMonitor_Factory.create(this.contextProvider, ConcurrencyModule_ProvideMainLooperFactory.create(), this.broadcastDispatcherProvider, this.dumpManagerProvider, this.provideBackgroundExecutorProvider));
        DependencyProvider_ProvideLockPatternUtilsFactory create2 = DependencyProvider_ProvideLockPatternUtilsFactory.create(builder.dependencyProvider, this.contextProvider);
        this.provideLockPatternUtilsProvider = create2;
        Provider<KeyguardStateControllerImpl> provider7 = DoubleCheck.provider(KeyguardStateControllerImpl_Factory.create(this.contextProvider, this.keyguardUpdateMonitorProvider, create2));
        this.keyguardStateControllerImplProvider = provider7;
        Provider<NotificationLockscreenUserManagerImpl> provider8 = DoubleCheck.provider(NotificationLockscreenUserManagerImpl_Factory.create(this.contextProvider, this.broadcastDispatcherProvider, this.provideDevicePolicyManagerProvider, this.provideUserManagerProvider, this.provideIStatusBarServiceProvider, this.provideKeyguardManagerProvider, this.statusBarStateControllerImplProvider, this.provideMainHandlerProvider, this.deviceProvisionedControllerImplProvider, provider7));
        this.notificationLockscreenUserManagerImplProvider = provider8;
        this.keyguardBypassControllerProvider = DoubleCheck.provider(KeyguardBypassController_Factory.create(this.contextProvider, this.tunerServiceImplProvider, this.statusBarStateControllerImplProvider, provider8, this.keyguardStateControllerImplProvider, this.dumpManagerProvider));
        Provider<SysuiColorExtractor> provider9 = DoubleCheck.provider(SysuiColorExtractor_Factory.create(this.contextProvider, this.provideConfigurationControllerProvider));
        this.sysuiColorExtractorProvider = provider9;
        this.notificationShadeWindowControllerProvider = DoubleCheck.provider(NotificationShadeWindowController_Factory.create(this.contextProvider, this.provideWindowManagerProvider, this.provideIActivityManagerProvider, this.dozeParametersProvider, this.statusBarStateControllerImplProvider, this.provideConfigurationControllerProvider, this.keyguardBypassControllerProvider, provider9, this.dumpManagerProvider));
        Provider<ProtoTracer> provider10 = DoubleCheck.provider(ProtoTracer_Factory.create(this.contextProvider, this.dumpManagerProvider));
        this.protoTracerProvider = provider10;
        this.provideCommandQueueProvider = DoubleCheck.provider(StatusBarDependenciesModule_ProvideCommandQueueFactory.create(this.contextProvider, provider10));
        this.providePluginManagerProvider = DoubleCheck.provider(DependencyProvider_ProvidePluginManagerFactory.create(builder.dependencyProvider, this.contextProvider));
        this.provideMainExecutorProvider = ConcurrencyModule_ProvideMainExecutorFactory.create(this.contextProvider);
        this.provideDisplayMetricsProvider = DoubleCheck.provider(DependencyProvider_ProvideDisplayMetricsFactory.create(builder.dependencyProvider, this.contextProvider, this.provideWindowManagerProvider));
        Provider<AsyncSensorManager> provider11 = DoubleCheck.provider(AsyncSensorManager_Factory.create(this.contextProvider, this.providePluginManagerProvider));
        this.asyncSensorManagerProvider = provider11;
        this.proximitySensorProvider = ProximitySensor_Factory.create(this.provideResourcesProvider, provider11);
        this.dockManagerImplProvider = DoubleCheck.provider(DockManagerImpl_Factory.create());
        this.provideUiBackgroundExecutorProvider = DoubleCheck.provider(ConcurrencyModule_ProvideUiBackgroundExecutorFactory.create());
        this.falsingManagerProxyProvider = DoubleCheck.provider(FalsingManagerProxy_Factory.create(this.contextProvider, this.providePluginManagerProvider, this.provideMainExecutorProvider, this.provideDisplayMetricsProvider, this.proximitySensorProvider, DeviceConfigProxy_Factory.create(), this.dockManagerImplProvider, this.keyguardUpdateMonitorProvider, this.dumpManagerProvider, this.provideUiBackgroundExecutorProvider, this.statusBarStateControllerImplProvider));
        this.statusBarKeyguardViewManagerProvider = new DelegateFactory();
        this.dismissCallbackRegistryProvider = DoubleCheck.provider(DismissCallbackRegistry_Factory.create(this.provideUiBackgroundExecutorProvider));
        this.provideTrustManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideTrustManagerFactory.create(this.contextProvider));
        this.navigationModeControllerProvider = DoubleCheck.provider(NavigationModeController_Factory.create(this.contextProvider, this.deviceProvisionedControllerImplProvider, this.provideUiBackgroundExecutorProvider));
        this.newKeyguardViewMediatorProvider = DoubleCheck.provider(KeyguardModule_NewKeyguardViewMediatorFactory.create(this.contextProvider, this.falsingManagerProxyProvider, this.provideLockPatternUtilsProvider, this.broadcastDispatcherProvider, this.notificationShadeWindowControllerProvider, this.statusBarKeyguardViewManagerProvider, this.dismissCallbackRegistryProvider, this.keyguardUpdateMonitorProvider, this.dumpManagerProvider, this.providePowerManagerProvider, this.provideTrustManagerProvider, this.provideUiBackgroundExecutorProvider, DeviceConfigProxy_Factory.create(), this.navigationModeControllerProvider));
        this.providesViewMediatorCallbackProvider = DependencyProvider_ProvidesViewMediatorCallbackFactory.create(builder.dependencyProvider, this.newKeyguardViewMediatorProvider);
        this.featureFlagsProvider = DoubleCheck.provider(FeatureFlags_Factory.create(this.provideBackgroundExecutorProvider));
        Provider<NotificationManager> provider12 = DoubleCheck.provider(SystemServicesModule_ProvideNotificationManagerFactory.create(this.contextProvider));
        this.provideNotificationManagerProvider = provider12;
        this.provideNotificationListenerProvider = DoubleCheck.provider(StatusBarDependenciesModule_ProvideNotificationListenerFactory.create(this.contextProvider, provider12, this.provideMainHandlerProvider));
        Provider<ContentResolver> provider13 = DoubleCheck.provider(SystemServicesModule_ProvideContentResolverFactory.create(this.contextProvider));
        this.provideContentResolverProvider = provider13;
        Provider<LogcatEchoTracker> provider14 = DoubleCheck.provider(LogModule_ProvideLogcatEchoTrackerFactory.create(provider13, ConcurrencyModule_ProvideMainLooperFactory.create()));
        this.provideLogcatEchoTrackerProvider = provider14;
        Provider<LogBuffer> provider15 = DoubleCheck.provider(LogModule_ProvideNotificationsLogBufferFactory.create(provider14, this.dumpManagerProvider));
        this.provideNotificationsLogBufferProvider = provider15;
        this.notificationEntryManagerLoggerProvider = NotificationEntryManagerLogger_Factory.create(provider15);
        this.notificationGroupManagerProvider = DoubleCheck.provider(NotificationGroupManager_Factory.create(this.statusBarStateControllerImplProvider));
        this.provideNotificationMediaManagerProvider = new DelegateFactory();
        this.provideHeadsUpManagerPhoneProvider = DoubleCheck.provider(SystemUIDefaultModule_ProvideHeadsUpManagerPhoneFactory.create(this.contextProvider, this.statusBarStateControllerImplProvider, this.keyguardBypassControllerProvider, this.notificationGroupManagerProvider, this.provideConfigurationControllerProvider));
        this.notificationFilterProvider = DoubleCheck.provider(NotificationFilter_Factory.create(this.statusBarStateControllerImplProvider));
        this.notificationSectionsFeatureManagerProvider = NotificationSectionsFeatureManager_Factory.create(DeviceConfigProxy_Factory.create(), this.contextProvider);
        Provider<ExtensionControllerImpl> provider16 = DoubleCheck.provider(ExtensionControllerImpl_Factory.create(this.contextProvider, this.provideLeakDetectorProvider, this.providePluginManagerProvider, this.tunerServiceImplProvider, this.provideConfigurationControllerProvider));
        this.extensionControllerImplProvider = provider16;
        Provider<NotificationPersonExtractorPluginBoundary> provider17 = DoubleCheck.provider(NotificationPersonExtractorPluginBoundary_Factory.create(provider16));
        this.notificationPersonExtractorPluginBoundaryProvider = provider17;
        Provider<PeopleNotificationIdentifierImpl> provider18 = DoubleCheck.provider(PeopleNotificationIdentifierImpl_Factory.create(provider17, this.notificationGroupManagerProvider));
        this.peopleNotificationIdentifierImplProvider = provider18;
        Provider<HighPriorityProvider> provider19 = DoubleCheck.provider(HighPriorityProvider_Factory.create(provider18));
        this.highPriorityProvider = provider19;
        this.notificationRankingManagerProvider = NotificationRankingManager_Factory.create(this.provideNotificationMediaManagerProvider, this.notificationGroupManagerProvider, this.provideHeadsUpManagerPhoneProvider, this.notificationFilterProvider, this.notificationEntryManagerLoggerProvider, this.notificationSectionsFeatureManagerProvider, this.peopleNotificationIdentifierImplProvider, provider19);
        this.keyguardEnvironmentImplProvider = DoubleCheck.provider(KeyguardEnvironmentImpl_Factory.create());
        this.provideNotificationMessagingUtilProvider = DependencyProvider_ProvideNotificationMessagingUtilFactory.create(builder.dependencyProvider, this.contextProvider);
        DelegateFactory delegateFactory = new DelegateFactory();
        this.provideNotificationEntryManagerProvider = delegateFactory;
        this.provideSmartReplyControllerProvider = DoubleCheck.provider(StatusBarDependenciesModule_ProvideSmartReplyControllerFactory.create(delegateFactory, this.provideIStatusBarServiceProvider));
        this.provideStatusBarProvider = new DelegateFactory();
        this.provideHandlerProvider = DependencyProvider_ProvideHandlerFactory.create(builder.dependencyProvider);
        Provider<RemoteInputUriController> provider20 = DoubleCheck.provider(RemoteInputUriController_Factory.create(this.provideIStatusBarServiceProvider));
        this.remoteInputUriControllerProvider = provider20;
        this.provideNotificationRemoteInputManagerProvider = DoubleCheck.provider(C1172xfa996c5e.create(this.contextProvider, this.notificationLockscreenUserManagerImplProvider, this.provideSmartReplyControllerProvider, this.provideNotificationEntryManagerProvider, this.provideStatusBarProvider, this.statusBarStateControllerImplProvider, this.provideHandlerProvider, provider20));
        NotifCollectionLogger_Factory create3 = NotifCollectionLogger_Factory.create(this.provideNotificationsLogBufferProvider);
        this.notifCollectionLoggerProvider = create3;
        this.notifCollectionProvider = DoubleCheck.provider(NotifCollection_Factory.create(this.provideIStatusBarServiceProvider, this.dumpManagerProvider, this.featureFlagsProvider, create3));
        this.bindSystemClockProvider = DoubleCheck.provider(SystemClockImpl_Factory.create());
        ShadeListBuilderLogger_Factory create4 = ShadeListBuilderLogger_Factory.create(this.provideNotificationsLogBufferProvider);
        this.shadeListBuilderLoggerProvider = create4;
        Provider<ShadeListBuilder> provider21 = DoubleCheck.provider(ShadeListBuilder_Factory.create(this.bindSystemClockProvider, create4, this.dumpManagerProvider));
        this.shadeListBuilderProvider = provider21;
        Provider<NotifPipeline> provider22 = DoubleCheck.provider(NotifPipeline_Factory.create(this.notifCollectionProvider, provider21));
        this.notifPipelineProvider = provider22;
        this.provideCommonNotifCollectionProvider = DoubleCheck.provider(NotificationsModule_ProvideCommonNotifCollectionFactory.create(this.featureFlagsProvider, provider22, this.provideNotificationEntryManagerProvider));
        NotifBindPipelineLogger_Factory create5 = NotifBindPipelineLogger_Factory.create(this.provideNotificationsLogBufferProvider);
        this.notifBindPipelineLoggerProvider = create5;
        this.notifBindPipelineProvider = DoubleCheck.provider(NotifBindPipeline_Factory.create(this.provideCommonNotifCollectionProvider, create5));
        NotifRemoteViewCacheImpl_Factory create6 = NotifRemoteViewCacheImpl_Factory.create(this.provideCommonNotifCollectionProvider);
        this.notifRemoteViewCacheImplProvider = create6;
        this.provideNotifRemoteViewCacheProvider = DoubleCheck.provider(create6);
        this.smartReplyConstantsProvider = DoubleCheck.provider(SmartReplyConstants_Factory.create(this.provideMainHandlerProvider, this.contextProvider, DeviceConfigProxy_Factory.create()));
        Provider<LauncherApps> provider23 = DoubleCheck.provider(SystemServicesModule_ProvideLauncherAppsFactory.create(this.contextProvider));
        this.provideLauncherAppsProvider = provider23;
        ConversationNotificationProcessor_Factory create7 = ConversationNotificationProcessor_Factory.create(provider23);
        this.conversationNotificationProcessorProvider = create7;
        this.notificationContentInflaterProvider = DoubleCheck.provider(NotificationContentInflater_Factory.create(this.provideNotifRemoteViewCacheProvider, this.provideNotificationRemoteInputManagerProvider, this.smartReplyConstantsProvider, this.provideSmartReplyControllerProvider, create7, this.provideBackgroundExecutorProvider));
        this.notifInflationErrorManagerProvider = DoubleCheck.provider(NotifInflationErrorManager_Factory.create());
        RowContentBindStageLogger_Factory create8 = RowContentBindStageLogger_Factory.create(this.provideNotificationsLogBufferProvider);
        this.rowContentBindStageLoggerProvider = create8;
        this.rowContentBindStageProvider = DoubleCheck.provider(RowContentBindStage_Factory.create(this.notificationContentInflaterProvider, this.notifInflationErrorManagerProvider, create8));
        this.provideIDreamManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideIDreamManagerFactory.create());
        this.enhancedEstimatesImplProvider = DoubleCheck.provider(EnhancedEstimatesImpl_Factory.create());
        ConcurrencyModule_ProvideBgHandlerFactory create9 = ConcurrencyModule_ProvideBgHandlerFactory.create(this.provideBgLooperProvider);
        this.provideBgHandlerProvider = create9;
        Provider<BatteryControllerImpl> provider24 = DoubleCheck.provider(BatteryControllerImpl_Factory.create(this.contextProvider, this.enhancedEstimatesImplProvider, this.providePowerManagerProvider, this.broadcastDispatcherProvider, this.provideMainHandlerProvider, create9));
        this.batteryControllerImplProvider = provider24;
        this.notificationInterruptStateProviderImplProvider = DoubleCheck.provider(NotificationInterruptStateProviderImpl_Factory.create(this.provideContentResolverProvider, this.providePowerManagerProvider, this.provideIDreamManagerProvider, this.provideAmbientDisplayConfigurationProvider, this.notificationFilterProvider, provider24, this.statusBarStateControllerImplProvider, this.provideHeadsUpManagerPhoneProvider, this.provideMainHandlerProvider));
        this.expandableNotificationRowComponentBuilderProvider = new Provider<com.android.systemui.statusbar.notification.row.dagger.ExpandableNotificationRowComponent.Builder>() {
            public com.android.systemui.statusbar.notification.row.dagger.ExpandableNotificationRowComponent.Builder get() {
                return new ExpandableNotificationRowComponentBuilder();
            }
        };
        this.iconBuilderProvider = IconBuilder_Factory.create(this.contextProvider);
    }

    private void initialize2(Builder builder) {
        this.iconManagerProvider = IconManager_Factory.create(this.provideCommonNotifCollectionProvider, this.provideLauncherAppsProvider, this.iconBuilderProvider);
        this.notificationRowBinderImplProvider = DoubleCheck.provider(NotificationRowBinderImpl_Factory.create(this.contextProvider, this.provideNotificationMessagingUtilProvider, this.provideNotificationRemoteInputManagerProvider, this.notificationLockscreenUserManagerImplProvider, this.notifBindPipelineProvider, this.rowContentBindStageProvider, this.notificationInterruptStateProviderImplProvider, RowInflaterTask_Factory.create(), this.expandableNotificationRowComponentBuilderProvider, this.iconManagerProvider));
        Provider<ForegroundServiceDismissalFeatureController> provider = DoubleCheck.provider(ForegroundServiceDismissalFeatureController_Factory.create(DeviceConfigProxy_Factory.create(), this.contextProvider));
        this.foregroundServiceDismissalFeatureControllerProvider = provider;
        DelegateFactory delegateFactory = (DelegateFactory) this.provideNotificationEntryManagerProvider;
        Provider<NotificationEntryManager> provider2 = DoubleCheck.provider(NotificationsModule_ProvideNotificationEntryManagerFactory.create(this.notificationEntryManagerLoggerProvider, this.notificationGroupManagerProvider, this.notificationRankingManagerProvider, this.keyguardEnvironmentImplProvider, this.featureFlagsProvider, this.notificationRowBinderImplProvider, this.provideNotificationRemoteInputManagerProvider, this.provideLeakDetectorProvider, provider));
        this.provideNotificationEntryManagerProvider = provider2;
        delegateFactory.setDelegatedProvider(provider2);
        this.provideMainDelayableExecutorProvider = DoubleCheck.provider(ConcurrencyModule_ProvideMainDelayableExecutorFactory.create(ConcurrencyModule_ProvideMainLooperFactory.create()));
        GroupCoalescerLogger_Factory create = GroupCoalescerLogger_Factory.create(this.provideNotificationsLogBufferProvider);
        this.groupCoalescerLoggerProvider = create;
        this.groupCoalescerProvider = GroupCoalescer_Factory.create(this.provideMainDelayableExecutorProvider, this.bindSystemClockProvider, create);
        this.headsUpCoordinatorProvider = DoubleCheck.provider(HeadsUpCoordinator_Factory.create(this.provideHeadsUpManagerPhoneProvider, this.provideNotificationRemoteInputManagerProvider));
        this.keyguardCoordinatorProvider = DoubleCheck.provider(KeyguardCoordinator_Factory.create(this.contextProvider, this.provideHandlerProvider, this.keyguardStateControllerImplProvider, this.notificationLockscreenUserManagerImplProvider, this.broadcastDispatcherProvider, this.statusBarStateControllerImplProvider, this.keyguardUpdateMonitorProvider, this.highPriorityProvider));
        this.rankingCoordinatorProvider = DoubleCheck.provider(RankingCoordinator_Factory.create(this.statusBarStateControllerImplProvider));
        Provider<AppOpsControllerImpl> provider3 = DoubleCheck.provider(AppOpsControllerImpl_Factory.create(this.contextProvider, this.provideBgLooperProvider, this.dumpManagerProvider));
        this.appOpsControllerImplProvider = provider3;
        Provider<ForegroundServiceController> provider4 = DoubleCheck.provider(ForegroundServiceController_Factory.create(this.provideNotificationEntryManagerProvider, provider3, this.provideMainHandlerProvider));
        this.foregroundServiceControllerProvider = provider4;
        this.foregroundCoordinatorProvider = DoubleCheck.provider(ForegroundCoordinator_Factory.create(provider4, this.appOpsControllerImplProvider, this.provideMainDelayableExecutorProvider));
        Provider<IPackageManager> provider5 = DoubleCheck.provider(SystemServicesModule_ProvideIPackageManagerFactory.create());
        this.provideIPackageManagerProvider = provider5;
        this.deviceProvisionedCoordinatorProvider = DoubleCheck.provider(DeviceProvisionedCoordinator_Factory.create(this.deviceProvisionedControllerImplProvider, provider5));
        DelegateFactory delegateFactory2 = new DelegateFactory();
        this.newBubbleControllerProvider = delegateFactory2;
        this.bubbleCoordinatorProvider = DoubleCheck.provider(BubbleCoordinator_Factory.create(delegateFactory2, this.notifCollectionProvider));
        this.preparationCoordinatorLoggerProvider = PreparationCoordinatorLogger_Factory.create(this.provideNotificationsLogBufferProvider);
        this.notifInflaterImplProvider = DoubleCheck.provider(NotifInflaterImpl_Factory.create(this.provideIStatusBarServiceProvider, this.notifCollectionProvider, this.notifInflationErrorManagerProvider, this.notifPipelineProvider));
        Provider<NotifViewBarn> provider6 = DoubleCheck.provider(NotifViewBarn_Factory.create());
        this.notifViewBarnProvider = provider6;
        Provider<PreparationCoordinator> provider7 = DoubleCheck.provider(PreparationCoordinator_Factory.create(this.preparationCoordinatorLoggerProvider, this.notifInflaterImplProvider, this.notifInflationErrorManagerProvider, provider6, this.provideIStatusBarServiceProvider, this.notificationInterruptStateProviderImplProvider, this.provideHeadsUpManagerPhoneProvider));
        this.preparationCoordinatorProvider = provider7;
        this.notifCoordinatorsProvider = DoubleCheck.provider(NotifCoordinators_Factory.create(this.dumpManagerProvider, this.featureFlagsProvider, this.headsUpCoordinatorProvider, this.keyguardCoordinatorProvider, this.rankingCoordinatorProvider, this.foregroundCoordinatorProvider, this.deviceProvisionedCoordinatorProvider, this.bubbleCoordinatorProvider, provider7));
        Provider<VisualStabilityManager> provider8 = DoubleCheck.provider(NotificationsModule_ProvideVisualStabilityManagerFactory.create(this.provideNotificationEntryManagerProvider, this.provideHandlerProvider));
        this.provideVisualStabilityManagerProvider = provider8;
        Provider<NotifViewManager> provider9 = DoubleCheck.provider(NotifViewManager_Factory.create(this.notifViewBarnProvider, provider8, this.featureFlagsProvider));
        this.notifViewManagerProvider = provider9;
        this.notifPipelineInitializerProvider = DoubleCheck.provider(NotifPipelineInitializer_Factory.create(this.notifPipelineProvider, this.groupCoalescerProvider, this.notifCollectionProvider, this.shadeListBuilderProvider, this.notifCoordinatorsProvider, this.notifInflaterImplProvider, this.dumpManagerProvider, this.featureFlagsProvider, provider9));
        this.notifBindPipelineInitializerProvider = NotifBindPipelineInitializer_Factory.create(this.notifBindPipelineProvider, this.rowContentBindStageProvider);
        Provider<NotificationGroupAlertTransferHelper> provider10 = DoubleCheck.provider(C1628x3053f5c5.create(this.rowContentBindStageProvider));
        this.provideNotificationGroupAlertTransferHelperProvider = provider10;
        this.notificationsControllerImplProvider = DoubleCheck.provider(NotificationsControllerImpl_Factory.create(this.featureFlagsProvider, this.provideNotificationListenerProvider, this.provideNotificationEntryManagerProvider, this.notifPipelineInitializerProvider, this.notifBindPipelineInitializerProvider, this.deviceProvisionedControllerImplProvider, this.notificationRowBinderImplProvider, this.remoteInputUriControllerProvider, this.newBubbleControllerProvider, this.notificationGroupManagerProvider, provider10, this.provideHeadsUpManagerPhoneProvider));
        NotificationsControllerStub_Factory create2 = NotificationsControllerStub_Factory.create(this.provideNotificationListenerProvider);
        this.notificationsControllerStubProvider = create2;
        this.provideNotificationsControllerProvider = DoubleCheck.provider(NotificationsModule_ProvideNotificationsControllerFactory.create(this.contextProvider, this.notificationsControllerImplProvider, create2));
        Provider<DarkIconDispatcherImpl> provider11 = DoubleCheck.provider(DarkIconDispatcherImpl_Factory.create(this.contextProvider, this.provideCommandQueueProvider));
        this.darkIconDispatcherImplProvider = provider11;
        this.lightBarControllerProvider = DoubleCheck.provider(LightBarController_Factory.create(this.contextProvider, provider11, this.batteryControllerImplProvider));
        this.provideIWindowManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideIWindowManagerFactory.create());
        this.provideAutoHideControllerProvider = DoubleCheck.provider(DependencyProvider_ProvideAutoHideControllerFactory.create(builder.dependencyProvider, this.contextProvider, this.provideMainHandlerProvider, this.provideIWindowManagerProvider));
        this.statusBarIconControllerImplProvider = DoubleCheck.provider(StatusBarIconControllerImpl_Factory.create(this.contextProvider, this.provideCommandQueueProvider));
        this.notificationWakeUpCoordinatorProvider = DoubleCheck.provider(NotificationWakeUpCoordinator_Factory.create(this.provideHeadsUpManagerPhoneProvider, this.statusBarStateControllerImplProvider, this.keyguardBypassControllerProvider, this.dozeParametersProvider));
        Provider<NotificationRoundnessManager> provider12 = DoubleCheck.provider(NotificationRoundnessManager_Factory.create(this.keyguardBypassControllerProvider, this.notificationSectionsFeatureManagerProvider));
        this.notificationRoundnessManagerProvider = provider12;
        this.pulseExpansionHandlerProvider = DoubleCheck.provider(PulseExpansionHandler_Factory.create(this.contextProvider, this.notificationWakeUpCoordinatorProvider, this.keyguardBypassControllerProvider, this.provideHeadsUpManagerPhoneProvider, provider12, this.statusBarStateControllerImplProvider, this.falsingManagerProxyProvider));
        this.dynamicPrivacyControllerProvider = DoubleCheck.provider(DynamicPrivacyController_Factory.create(this.notificationLockscreenUserManagerImplProvider, this.keyguardStateControllerImplProvider, this.statusBarStateControllerImplProvider));
        this.bypassHeadsUpNotifierProvider = DoubleCheck.provider(BypassHeadsUpNotifier_Factory.create(this.contextProvider, this.keyguardBypassControllerProvider, this.statusBarStateControllerImplProvider, this.provideHeadsUpManagerPhoneProvider, this.notificationLockscreenUserManagerImplProvider, this.provideNotificationMediaManagerProvider, this.provideNotificationEntryManagerProvider, this.tunerServiceImplProvider));
        this.remoteInputQuickSettingsDisablerProvider = DoubleCheck.provider(RemoteInputQuickSettingsDisabler_Factory.create(this.contextProvider, this.provideConfigurationControllerProvider, this.provideCommandQueueProvider));
        this.provideAccessibilityManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideAccessibilityManagerFactory.create(this.contextProvider));
        this.provideINotificationManagerProvider = DoubleCheck.provider(DependencyProvider_ProvideINotificationManagerFactory.create(builder.dependencyProvider));
        Provider<ShortcutManager> provider13 = DoubleCheck.provider(SystemServicesModule_ProvideShortcutManagerFactory.create(this.contextProvider));
        this.provideShortcutManagerProvider = provider13;
        this.provideNotificationGutsManagerProvider = DoubleCheck.provider(NotificationsModule_ProvideNotificationGutsManagerFactory.create(this.contextProvider, this.provideVisualStabilityManagerProvider, this.provideStatusBarProvider, this.provideMainHandlerProvider, this.provideAccessibilityManagerProvider, this.highPriorityProvider, this.provideINotificationManagerProvider, this.provideLauncherAppsProvider, provider13));
        this.expansionStateLoggerProvider = NotificationLogger_ExpansionStateLogger_Factory.create(this.provideUiBackgroundExecutorProvider);
        Provider<NotificationPanelLogger> provider14 = DoubleCheck.provider(NotificationsModule_ProvideNotificationPanelLoggerFactory.create());
        this.provideNotificationPanelLoggerProvider = provider14;
        this.provideNotificationLoggerProvider = DoubleCheck.provider(NotificationsModule_ProvideNotificationLoggerFactory.create(this.provideNotificationListenerProvider, this.provideUiBackgroundExecutorProvider, this.provideNotificationEntryManagerProvider, this.statusBarStateControllerImplProvider, this.expansionStateLoggerProvider, provider14));
        this.foregroundServiceSectionControllerProvider = DoubleCheck.provider(ForegroundServiceSectionController_Factory.create(this.provideNotificationEntryManagerProvider, this.foregroundServiceDismissalFeatureControllerProvider));
        DynamicChildBindController_Factory create3 = DynamicChildBindController_Factory.create(this.rowContentBindStageProvider);
        this.dynamicChildBindControllerProvider = create3;
        this.provideNotificationViewHierarchyManagerProvider = DoubleCheck.provider(C1173x3f8faa0a.create(this.contextProvider, this.provideMainHandlerProvider, this.notificationLockscreenUserManagerImplProvider, this.notificationGroupManagerProvider, this.provideVisualStabilityManagerProvider, this.statusBarStateControllerImplProvider, this.provideNotificationEntryManagerProvider, this.keyguardBypassControllerProvider, this.newBubbleControllerProvider, this.dynamicPrivacyControllerProvider, this.foregroundServiceSectionControllerProvider, create3));
        this.provideNotificationAlertingManagerProvider = DoubleCheck.provider(NotificationsModule_ProvideNotificationAlertingManagerFactory.create(this.provideNotificationEntryManagerProvider, this.provideNotificationRemoteInputManagerProvider, this.provideVisualStabilityManagerProvider, this.statusBarStateControllerImplProvider, this.notificationInterruptStateProviderImplProvider, this.provideNotificationListenerProvider, this.provideHeadsUpManagerPhoneProvider));
        this.provideMetricsLoggerProvider = DoubleCheck.provider(DependencyProvider_ProvideMetricsLoggerFactory.create(builder.dependencyProvider));
        Provider<Optional<Lazy<StatusBar>>> access$400 = PresentJdkOptionalLazyProvider.m87of(this.provideStatusBarProvider);
        this.optionalOfLazyOfStatusBarProvider = access$400;
        Provider<ActivityStarterDelegate> provider15 = DoubleCheck.provider(ActivityStarterDelegate_Factory.create(access$400));
        this.activityStarterDelegateProvider = provider15;
        this.userSwitcherControllerProvider = DoubleCheck.provider(UserSwitcherController_Factory.create(this.contextProvider, this.keyguardStateControllerImplProvider, this.provideMainHandlerProvider, provider15, this.broadcastDispatcherProvider));
        this.networkControllerImplProvider = DoubleCheck.provider(NetworkControllerImpl_Factory.create(this.contextProvider, this.provideBgLooperProvider, this.deviceProvisionedControllerImplProvider, this.broadcastDispatcherProvider));
        this.screenLifecycleProvider = DoubleCheck.provider(ScreenLifecycle_Factory.create());
        this.wakefulnessLifecycleProvider = DoubleCheck.provider(WakefulnessLifecycle_Factory.create());
        this.vibratorHelperProvider = DoubleCheck.provider(VibratorHelper_Factory.create(this.contextProvider));
        this.provideNavigationBarControllerProvider = DoubleCheck.provider(DependencyProvider_ProvideNavigationBarControllerFactory.create(builder.dependencyProvider, this.contextProvider, this.provideMainHandlerProvider, this.provideCommandQueueProvider));
        this.provideAssistUtilsProvider = DoubleCheck.provider(AssistModule_ProvideAssistUtilsFactory.create(this.contextProvider));
        this.provideBackgroundHandlerProvider = DoubleCheck.provider(AssistModule_ProvideBackgroundHandlerFactory.create());
        this.provideAssistHandleViewControllerProvider = AssistModule_ProvideAssistHandleViewControllerFactory.create(this.provideNavigationBarControllerProvider);
        this.deviceConfigHelperProvider = DoubleCheck.provider(DeviceConfigHelper_Factory.create());
        this.assistHandleOffBehaviorProvider = DoubleCheck.provider(AssistHandleOffBehavior_Factory.create());
        Provider<SysUiState> provider16 = DoubleCheck.provider(SystemUIModule_ProvideSysUiStateFactory.create());
        this.provideSysUiStateProvider = provider16;
        this.assistHandleLikeHomeBehaviorProvider = DoubleCheck.provider(AssistHandleLikeHomeBehavior_Factory.create(this.statusBarStateControllerImplProvider, this.wakefulnessLifecycleProvider, provider16));
        this.provideSystemClockProvider = DoubleCheck.provider(AssistModule_ProvideSystemClockFactory.create());
        this.provideActivityManagerWrapperProvider = DoubleCheck.provider(DependencyProvider_ProvideActivityManagerWrapperFactory.create(builder.dependencyProvider));
        PipSnapAlgorithm_Factory create4 = PipSnapAlgorithm_Factory.create(this.contextProvider);
        this.pipSnapAlgorithmProvider = create4;
        this.pipBoundsHandlerProvider = PipBoundsHandler_Factory.create(this.contextProvider, create4);
        Provider<PipSurfaceTransactionHelper> provider17 = DoubleCheck.provider(PipSurfaceTransactionHelper_Factory.create(this.contextProvider));
        this.pipSurfaceTransactionHelperProvider = provider17;
        Provider<PipManager> provider18 = DoubleCheck.provider(PipManager_Factory.create(this.contextProvider, this.broadcastDispatcherProvider, this.pipBoundsHandlerProvider, provider17));
        this.pipManagerProvider = provider18;
        this.pipUIProvider = DoubleCheck.provider(PipUI_Factory.create(this.contextProvider, this.provideCommandQueueProvider, provider18));
        DelegateFactory delegateFactory3 = new DelegateFactory();
        this.contextComponentResolverProvider = delegateFactory3;
        RecentsModule_ProvideRecentsImplFactory create5 = RecentsModule_ProvideRecentsImplFactory.create(this.contextProvider, delegateFactory3);
        this.provideRecentsImplProvider = create5;
        Provider<Recents> provider19 = DoubleCheck.provider(SystemUIDefaultModule_ProvideRecentsFactory.create(this.contextProvider, create5, this.provideCommandQueueProvider));
        this.provideRecentsProvider = provider19;
        this.optionalOfLazyOfRecentsProvider = PresentJdkOptionalLazyProvider.m87of(provider19);
        Provider<DisplayController> provider20 = DoubleCheck.provider(DisplayController_Factory.create(this.contextProvider, this.provideMainHandlerProvider, this.provideIWindowManagerProvider));
        this.displayControllerProvider = provider20;
        this.systemWindowsProvider = DoubleCheck.provider(SystemWindows_Factory.create(this.contextProvider, provider20, this.provideIWindowManagerProvider));
        Provider<TransactionPool> provider21 = DoubleCheck.provider(TransactionPool_Factory.create());
        this.transactionPoolProvider = provider21;
        Provider<DisplayImeController> provider22 = DoubleCheck.provider(DisplayImeController_Factory.create(this.systemWindowsProvider, this.displayControllerProvider, this.provideMainHandlerProvider, provider21));
        this.displayImeControllerProvider = provider22;
        Provider<Divider> provider23 = DoubleCheck.provider(DividerModule_ProvideDividerFactory.create(this.contextProvider, this.optionalOfLazyOfRecentsProvider, this.displayControllerProvider, this.systemWindowsProvider, provider22, this.provideMainHandlerProvider, this.keyguardStateControllerImplProvider, this.transactionPoolProvider));
        this.provideDividerProvider = provider23;
        Provider<Optional<Divider>> access$500 = PresentJdkOptionalInstanceProvider.m86of(provider23);
        this.optionalOfDividerProvider = access$500;
        this.overviewProxyServiceProvider = DoubleCheck.provider(OverviewProxyService_Factory.create(this.contextProvider, this.provideCommandQueueProvider, this.deviceProvisionedControllerImplProvider, this.provideNavigationBarControllerProvider, this.navigationModeControllerProvider, this.notificationShadeWindowControllerProvider, this.provideSysUiStateProvider, this.pipUIProvider, access$500, this.optionalOfLazyOfStatusBarProvider));
        Provider<PackageManagerWrapper> provider24 = DoubleCheck.provider(SystemServicesModule_ProvidePackageManagerWrapperFactory.create());
        this.providePackageManagerWrapperProvider = provider24;
        Provider provider25 = DoubleCheck.provider(AssistHandleReminderExpBehavior_Factory.create(this.provideSystemClockProvider, this.provideBackgroundHandlerProvider, this.deviceConfigHelperProvider, this.statusBarStateControllerImplProvider, this.provideActivityManagerWrapperProvider, this.overviewProxyServiceProvider, this.provideSysUiStateProvider, this.wakefulnessLifecycleProvider, provider24, this.broadcastDispatcherProvider, this.bootCompleteCacheImplProvider));
        this.assistHandleReminderExpBehaviorProvider = provider25;
        Provider provider26 = DoubleCheck.provider(AssistModule_ProvideAssistHandleBehaviorControllerMapFactory.create(this.assistHandleOffBehaviorProvider, this.assistHandleLikeHomeBehaviorProvider, provider25));
        this.provideAssistHandleBehaviorControllerMapProvider = provider26;
        this.assistHandleBehaviorControllerProvider = DoubleCheck.provider(AssistHandleBehaviorController_Factory.create(this.contextProvider, this.provideAssistUtilsProvider, this.provideBackgroundHandlerProvider, this.provideAssistHandleViewControllerProvider, this.deviceConfigHelperProvider, provider26, this.navigationModeControllerProvider, this.dumpManagerProvider));
        Provider<PhoneStateMonitor> provider27 = DoubleCheck.provider(PhoneStateMonitor_Factory.create(this.contextProvider, this.broadcastDispatcherProvider, this.optionalOfLazyOfStatusBarProvider, this.bootCompleteCacheImplProvider));
        this.phoneStateMonitorProvider = provider27;
        this.assistManagerProvider = DoubleCheck.provider(AssistManager_Factory.create(this.deviceProvisionedControllerImplProvider, this.contextProvider, this.provideAssistUtilsProvider, this.assistHandleBehaviorControllerProvider, this.provideCommandQueueProvider, provider27, this.overviewProxyServiceProvider, this.provideConfigurationControllerProvider, this.provideSysUiStateProvider));
        this.lockscreenGestureLoggerProvider = DoubleCheck.provider(LockscreenGestureLogger_Factory.create());
        this.shadeControllerImplProvider = new DelegateFactory();
        this.accessibilityControllerProvider = DoubleCheck.provider(AccessibilityController_Factory.create(this.contextProvider));
        this.builderProvider = WakeLock_Builder_Factory.create(this.contextProvider);
        Provider<IBatteryStats> provider28 = DoubleCheck.provider(SystemServicesModule_ProvideIBatteryStatsFactory.create());
        this.provideIBatteryStatsProvider = provider28;
        Provider<KeyguardIndicationController> provider29 = DoubleCheck.provider(KeyguardIndicationController_Factory.create(this.contextProvider, this.builderProvider, this.keyguardStateControllerImplProvider, this.statusBarStateControllerImplProvider, this.keyguardUpdateMonitorProvider, this.dockManagerImplProvider, provider28));
        this.keyguardIndicationControllerProvider = provider29;
        this.lockscreenLockIconControllerProvider = DoubleCheck.provider(LockscreenLockIconController_Factory.create(this.lockscreenGestureLoggerProvider, this.keyguardUpdateMonitorProvider, this.provideLockPatternUtilsProvider, this.shadeControllerImplProvider, this.accessibilityControllerProvider, provider29, this.statusBarStateControllerImplProvider, this.provideConfigurationControllerProvider, this.notificationWakeUpCoordinatorProvider, this.keyguardBypassControllerProvider, this.dockManagerImplProvider));
        this.provideAlarmManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideAlarmManagerFactory.create(this.contextProvider));
    }

    private void initialize3(Builder builder) {
        this.builderProvider2 = DelayedWakeLock_Builder_Factory.create(this.contextProvider);
        Provider<BlurUtils> provider = DoubleCheck.provider(BlurUtils_Factory.create(this.provideResourcesProvider, this.dumpManagerProvider));
        this.blurUtilsProvider = provider;
        this.scrimControllerProvider = DoubleCheck.provider(ScrimController_Factory.create(this.lightBarControllerProvider, this.dozeParametersProvider, this.provideAlarmManagerProvider, this.keyguardStateControllerImplProvider, this.builderProvider2, this.provideHandlerProvider, this.keyguardUpdateMonitorProvider, this.sysuiColorExtractorProvider, this.dockManagerImplProvider, provider));
        this.provideKeyguardLiftControllerProvider = DoubleCheck.provider(SystemUIModule_ProvideKeyguardLiftControllerFactory.create(this.contextProvider, this.statusBarStateControllerImplProvider, this.asyncSensorManagerProvider, this.keyguardUpdateMonitorProvider, this.dumpManagerProvider));
        SystemServicesModule_ProvideWallpaperManagerFactory create = SystemServicesModule_ProvideWallpaperManagerFactory.create(this.contextProvider);
        this.provideWallpaperManagerProvider = create;
        this.lockscreenWallpaperProvider = DoubleCheck.provider(LockscreenWallpaper_Factory.create(create, SystemServicesModule_ProvideIWallPaperManagerFactory.create(), this.keyguardUpdateMonitorProvider, this.dumpManagerProvider, this.provideNotificationMediaManagerProvider, this.provideMainHandlerProvider));
        Provider<LogBuffer> provider2 = DoubleCheck.provider(LogModule_ProvideDozeLogBufferFactory.create(this.provideLogcatEchoTrackerProvider, this.dumpManagerProvider));
        this.provideDozeLogBufferProvider = provider2;
        DozeLogger_Factory create2 = DozeLogger_Factory.create(provider2);
        this.dozeLoggerProvider = create2;
        Provider<DozeLog> provider3 = DoubleCheck.provider(DozeLog_Factory.create(this.keyguardUpdateMonitorProvider, this.dumpManagerProvider, create2));
        this.dozeLogProvider = provider3;
        Provider<DozeScrimController> provider4 = DoubleCheck.provider(DozeScrimController_Factory.create(this.dozeParametersProvider, provider3));
        this.dozeScrimControllerProvider = provider4;
        Provider<BiometricUnlockController> provider5 = DoubleCheck.provider(BiometricUnlockController_Factory.create(this.contextProvider, provider4, this.newKeyguardViewMediatorProvider, this.scrimControllerProvider, this.provideStatusBarProvider, this.shadeControllerImplProvider, this.notificationShadeWindowControllerProvider, this.keyguardStateControllerImplProvider, this.provideHandlerProvider, this.keyguardUpdateMonitorProvider, this.provideResourcesProvider, this.keyguardBypassControllerProvider, this.dozeParametersProvider, this.provideMetricsLoggerProvider, this.dumpManagerProvider));
        Provider<BiometricUnlockController> provider6 = provider5;
        this.biometricUnlockControllerProvider = provider5;
        this.dozeServiceHostProvider = DoubleCheck.provider(DozeServiceHost_Factory.create(this.dozeLogProvider, this.providePowerManagerProvider, this.wakefulnessLifecycleProvider, this.statusBarStateControllerImplProvider, this.deviceProvisionedControllerImplProvider, this.provideHeadsUpManagerPhoneProvider, this.batteryControllerImplProvider, this.scrimControllerProvider, provider6, this.newKeyguardViewMediatorProvider, this.assistManagerProvider, this.dozeScrimControllerProvider, this.keyguardUpdateMonitorProvider, this.provideVisualStabilityManagerProvider, this.pulseExpansionHandlerProvider, this.notificationShadeWindowControllerProvider, this.notificationWakeUpCoordinatorProvider, this.lockscreenLockIconControllerProvider));
        this.screenPinningRequestProvider = ScreenPinningRequest_Factory.create(this.contextProvider, this.optionalOfLazyOfStatusBarProvider);
        Provider<VolumeDialogControllerImpl> provider7 = DoubleCheck.provider(VolumeDialogControllerImpl_Factory.create(this.contextProvider, this.broadcastDispatcherProvider, this.optionalOfLazyOfStatusBarProvider));
        this.volumeDialogControllerImplProvider = provider7;
        this.volumeDialogComponentProvider = DoubleCheck.provider(VolumeDialogComponent_Factory.create(this.contextProvider, this.newKeyguardViewMediatorProvider, provider7));
        this.optionalOfRecentsProvider = PresentJdkOptionalInstanceProvider.m86of(this.provideRecentsProvider);
        this.statusBarComponentBuilderProvider = new Provider<com.android.systemui.statusbar.phone.dagger.StatusBarComponent.Builder>() {
            public com.android.systemui.statusbar.phone.dagger.StatusBarComponent.Builder get() {
                return new StatusBarComponentBuilder();
            }
        };
        this.lightsOutNotifControllerProvider = DoubleCheck.provider(LightsOutNotifController_Factory.create(this.provideWindowManagerProvider, this.provideNotificationEntryManagerProvider, this.provideCommandQueueProvider));
        this.statusBarRemoteInputCallbackProvider = DoubleCheck.provider(StatusBarRemoteInputCallback_Factory.create(this.contextProvider, this.notificationGroupManagerProvider, this.notificationLockscreenUserManagerImplProvider, this.keyguardStateControllerImplProvider, this.statusBarStateControllerImplProvider, this.statusBarKeyguardViewManagerProvider, this.activityStarterDelegateProvider, this.shadeControllerImplProvider, this.provideCommandQueueProvider));
        Provider<ActivityIntentHelper> provider8 = DoubleCheck.provider(ActivityIntentHelper_Factory.create(this.contextProvider));
        Provider<ActivityIntentHelper> provider9 = provider8;
        this.activityIntentHelperProvider = provider8;
        this.builderProvider3 = DoubleCheck.provider(StatusBarNotificationActivityStarter_Builder_Factory.create(this.contextProvider, this.provideCommandQueueProvider, this.assistManagerProvider, this.provideNotificationEntryManagerProvider, this.provideHeadsUpManagerPhoneProvider, this.activityStarterDelegateProvider, this.provideIStatusBarServiceProvider, this.statusBarStateControllerImplProvider, this.statusBarKeyguardViewManagerProvider, this.provideKeyguardManagerProvider, this.provideIDreamManagerProvider, this.provideNotificationRemoteInputManagerProvider, this.statusBarRemoteInputCallbackProvider, this.notificationGroupManagerProvider, this.notificationLockscreenUserManagerImplProvider, this.keyguardStateControllerImplProvider, this.notificationInterruptStateProviderImplProvider, this.provideMetricsLoggerProvider, this.provideLockPatternUtilsProvider, this.provideMainHandlerProvider, this.provideBgHandlerProvider, this.provideUiBackgroundExecutorProvider, provider9, this.newBubbleControllerProvider, this.shadeControllerImplProvider, this.featureFlagsProvider, this.notifPipelineProvider, this.notifCollectionProvider));
        Factory create3 = InstanceFactory.create(this);
        this.tvSystemUIRootComponentProvider = create3;
        this.injectionInflationControllerProvider = DoubleCheck.provider(InjectionInflationController_Factory.create(create3));
        C17243 r1 = new Provider<com.android.systemui.statusbar.notification.row.dagger.NotificationRowComponent.Builder>() {
            public com.android.systemui.statusbar.notification.row.dagger.NotificationRowComponent.Builder get() {
                return new NotificationRowComponentBuilder();
            }
        };
        this.notificationRowComponentBuilderProvider = r1;
        this.superStatusBarViewFactoryProvider = DoubleCheck.provider(SuperStatusBarViewFactory_Factory.create(this.contextProvider, this.injectionInflationControllerProvider, r1, this.lockscreenLockIconControllerProvider));
        this.initControllerProvider = DoubleCheck.provider(InitController_Factory.create());
        this.provideTimeTickHandlerProvider = DoubleCheck.provider(DependencyProvider_ProvideTimeTickHandlerFactory.create(builder.dependencyProvider));
        this.pluginDependencyProvider = DoubleCheck.provider(PluginDependencyProvider_Factory.create(this.providePluginManagerProvider));
        this.keyguardDismissUtilProvider = DoubleCheck.provider(KeyguardDismissUtil_Factory.create());
        this.userInfoControllerImplProvider = DoubleCheck.provider(UserInfoControllerImpl_Factory.create(this.contextProvider));
        this.castControllerImplProvider = DoubleCheck.provider(CastControllerImpl_Factory.create(this.contextProvider));
        this.hotspotControllerImplProvider = DoubleCheck.provider(HotspotControllerImpl_Factory.create(this.contextProvider, this.provideMainHandlerProvider, this.provideBgHandlerProvider));
        this.provideLocalBluetoothControllerProvider = DoubleCheck.provider(SystemServicesModule_ProvideLocalBluetoothControllerFactory.create(this.contextProvider, this.provideBgHandlerProvider));
        this.bluetoothControllerImplProvider = DoubleCheck.provider(BluetoothControllerImpl_Factory.create(this.contextProvider, this.provideBgLooperProvider, ConcurrencyModule_ProvideMainLooperFactory.create(), this.provideLocalBluetoothControllerProvider));
        this.nextAlarmControllerImplProvider = DoubleCheck.provider(NextAlarmControllerImpl_Factory.create(this.contextProvider));
        this.rotationLockControllerImplProvider = DoubleCheck.provider(RotationLockControllerImpl_Factory.create(this.contextProvider));
        this.provideDataSaverControllerProvider = DoubleCheck.provider(DependencyProvider_ProvideDataSaverControllerFactory.create(builder.dependencyProvider, this.networkControllerImplProvider));
        this.zenModeControllerImplProvider = DoubleCheck.provider(ZenModeControllerImpl_Factory.create(this.contextProvider, this.provideMainHandlerProvider, this.broadcastDispatcherProvider));
        this.locationControllerImplProvider = DoubleCheck.provider(LocationControllerImpl_Factory.create(this.contextProvider, this.provideBgLooperProvider, this.broadcastDispatcherProvider, this.bootCompleteCacheImplProvider));
        this.sensorPrivacyControllerImplProvider = DoubleCheck.provider(SensorPrivacyControllerImpl_Factory.create(this.contextProvider));
        this.provideAudioManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideAudioManagerFactory.create(this.contextProvider));
        this.provideTelecomManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideTelecomManagerFactory.create(this.contextProvider));
        this.provideDisplayIdProvider = SystemServicesModule_ProvideDisplayIdFactory.create(this.contextProvider);
        this.provideSharePreferencesProvider = DependencyProvider_ProvideSharePreferencesFactory.create(builder.dependencyProvider, this.contextProvider);
        DateFormatUtil_Factory create4 = DateFormatUtil_Factory.create(this.contextProvider);
        DateFormatUtil_Factory dateFormatUtil_Factory = create4;
        this.dateFormatUtilProvider = create4;
        this.phoneStatusBarPolicyProvider = PhoneStatusBarPolicy_Factory.create(this.statusBarIconControllerImplProvider, this.provideCommandQueueProvider, this.broadcastDispatcherProvider, this.provideUiBackgroundExecutorProvider, this.provideResourcesProvider, this.castControllerImplProvider, this.hotspotControllerImplProvider, this.bluetoothControllerImplProvider, this.nextAlarmControllerImplProvider, this.userInfoControllerImplProvider, this.rotationLockControllerImplProvider, this.provideDataSaverControllerProvider, this.zenModeControllerImplProvider, this.deviceProvisionedControllerImplProvider, this.keyguardStateControllerImplProvider, this.locationControllerImplProvider, this.sensorPrivacyControllerImplProvider, this.provideIActivityManagerProvider, this.provideAlarmManagerProvider, this.provideUserManagerProvider, this.provideAudioManagerProvider, this.recordingControllerProvider, this.provideTelecomManagerProvider, this.provideDisplayIdProvider, this.provideSharePreferencesProvider, dateFormatUtil_Factory);
        Provider<StatusBarTouchableRegionManager> provider10 = DoubleCheck.provider(StatusBarTouchableRegionManager_Factory.create(this.contextProvider, this.notificationShadeWindowControllerProvider, this.provideConfigurationControllerProvider, this.provideHeadsUpManagerPhoneProvider, this.newBubbleControllerProvider));
        Provider<StatusBarTouchableRegionManager> provider11 = provider10;
        this.statusBarTouchableRegionManagerProvider = provider10;
        DelegateFactory delegateFactory = (DelegateFactory) this.provideStatusBarProvider;
        Provider<StatusBar> provider12 = DoubleCheck.provider(StatusBarPhoneModule_ProvideStatusBarFactory.create(this.contextProvider, this.provideNotificationsControllerProvider, this.lightBarControllerProvider, this.provideAutoHideControllerProvider, this.keyguardUpdateMonitorProvider, this.statusBarIconControllerImplProvider, this.pulseExpansionHandlerProvider, this.notificationWakeUpCoordinatorProvider, this.keyguardBypassControllerProvider, this.keyguardStateControllerImplProvider, this.provideHeadsUpManagerPhoneProvider, this.dynamicPrivacyControllerProvider, this.bypassHeadsUpNotifierProvider, this.falsingManagerProxyProvider, this.broadcastDispatcherProvider, this.remoteInputQuickSettingsDisablerProvider, this.provideNotificationGutsManagerProvider, this.provideNotificationLoggerProvider, this.notificationInterruptStateProviderImplProvider, this.provideNotificationViewHierarchyManagerProvider, this.newKeyguardViewMediatorProvider, this.provideNotificationAlertingManagerProvider, this.provideDisplayMetricsProvider, this.provideMetricsLoggerProvider, this.provideUiBackgroundExecutorProvider, this.provideNotificationMediaManagerProvider, this.notificationLockscreenUserManagerImplProvider, this.provideNotificationRemoteInputManagerProvider, this.userSwitcherControllerProvider, this.networkControllerImplProvider, this.batteryControllerImplProvider, this.sysuiColorExtractorProvider, this.screenLifecycleProvider, this.wakefulnessLifecycleProvider, this.statusBarStateControllerImplProvider, this.vibratorHelperProvider, this.newBubbleControllerProvider, this.notificationGroupManagerProvider, this.provideVisualStabilityManagerProvider, this.deviceProvisionedControllerImplProvider, this.provideNavigationBarControllerProvider, this.assistManagerProvider, this.provideConfigurationControllerProvider, this.notificationShadeWindowControllerProvider, this.lockscreenLockIconControllerProvider, this.dozeParametersProvider, this.scrimControllerProvider, this.provideKeyguardLiftControllerProvider, this.lockscreenWallpaperProvider, this.biometricUnlockControllerProvider, this.dozeServiceHostProvider, this.providePowerManagerProvider, this.screenPinningRequestProvider, this.dozeScrimControllerProvider, this.volumeDialogComponentProvider, this.provideCommandQueueProvider, this.optionalOfRecentsProvider, this.statusBarComponentBuilderProvider, this.providePluginManagerProvider, this.optionalOfDividerProvider, this.lightsOutNotifControllerProvider, this.builderProvider3, this.shadeControllerImplProvider, this.superStatusBarViewFactoryProvider, this.statusBarKeyguardViewManagerProvider, this.providesViewMediatorCallbackProvider, this.initControllerProvider, this.darkIconDispatcherImplProvider, this.provideTimeTickHandlerProvider, this.pluginDependencyProvider, this.keyguardDismissUtilProvider, this.extensionControllerImplProvider, this.userInfoControllerImplProvider, this.phoneStatusBarPolicyProvider, this.keyguardIndicationControllerProvider, this.dismissCallbackRegistryProvider, provider11));
        this.provideStatusBarProvider = provider12;
        delegateFactory.setDelegatedProvider(provider12);
        Provider<MediaArtworkProcessor> provider13 = DoubleCheck.provider(MediaArtworkProcessor_Factory.create());
        this.mediaArtworkProcessorProvider = provider13;
        DelegateFactory delegateFactory2 = (DelegateFactory) this.provideNotificationMediaManagerProvider;
        Provider<NotificationMediaManager> provider14 = DoubleCheck.provider(C1171x30c882de.create(this.contextProvider, this.provideStatusBarProvider, this.notificationShadeWindowControllerProvider, this.provideNotificationEntryManagerProvider, provider13, this.keyguardBypassControllerProvider, this.provideMainExecutorProvider, DeviceConfigProxy_Factory.create()));
        this.provideNotificationMediaManagerProvider = provider14;
        delegateFactory2.setDelegatedProvider(provider14);
        DelegateFactory delegateFactory3 = (DelegateFactory) this.statusBarKeyguardViewManagerProvider;
        Provider<StatusBarKeyguardViewManager> provider15 = DoubleCheck.provider(StatusBarKeyguardViewManager_Factory.create(this.contextProvider, this.providesViewMediatorCallbackProvider, this.provideLockPatternUtilsProvider, this.statusBarStateControllerImplProvider, this.provideConfigurationControllerProvider, this.keyguardUpdateMonitorProvider, this.navigationModeControllerProvider, this.dockManagerImplProvider, this.notificationShadeWindowControllerProvider, this.keyguardStateControllerImplProvider, this.provideNotificationMediaManagerProvider));
        this.statusBarKeyguardViewManagerProvider = provider15;
        delegateFactory3.setDelegatedProvider(provider15);
        DelegateFactory delegateFactory4 = (DelegateFactory) this.shadeControllerImplProvider;
        Provider<ShadeControllerImpl> provider16 = DoubleCheck.provider(ShadeControllerImpl_Factory.create(this.provideCommandQueueProvider, this.statusBarStateControllerImplProvider, this.notificationShadeWindowControllerProvider, this.statusBarKeyguardViewManagerProvider, this.provideWindowManagerProvider, this.provideStatusBarProvider, this.assistManagerProvider, this.newBubbleControllerProvider));
        this.shadeControllerImplProvider = provider16;
        delegateFactory4.setDelegatedProvider(provider16);
        this.bubbleDataProvider = DoubleCheck.provider(BubbleData_Factory.create(this.contextProvider));
        Provider<FloatingContentCoordinator> provider17 = DoubleCheck.provider(FloatingContentCoordinator_Factory.create());
        this.floatingContentCoordinatorProvider = provider17;
        DelegateFactory delegateFactory5 = (DelegateFactory) this.newBubbleControllerProvider;
        Provider<BubbleController> provider18 = DoubleCheck.provider(BubbleModule_NewBubbleControllerFactory.create(this.contextProvider, this.notificationShadeWindowControllerProvider, this.statusBarStateControllerImplProvider, this.shadeControllerImplProvider, this.bubbleDataProvider, this.provideConfigurationControllerProvider, this.notificationInterruptStateProviderImplProvider, this.zenModeControllerImplProvider, this.notificationLockscreenUserManagerImplProvider, this.notificationGroupManagerProvider, this.provideNotificationEntryManagerProvider, this.notifPipelineProvider, this.featureFlagsProvider, this.dumpManagerProvider, provider17));
        this.newBubbleControllerProvider = provider18;
        delegateFactory5.setDelegatedProvider(provider18);
        this.bubbleOverflowActivityProvider = BubbleOverflowActivity_Factory.create(this.newBubbleControllerProvider);
        Provider<Executor> provider19 = DoubleCheck.provider(ConcurrencyModule_ProvideExecutorFactory.create(this.provideBgLooperProvider));
        this.provideExecutorProvider = provider19;
        this.controlsListingControllerImplProvider = DoubleCheck.provider(ControlsListingControllerImpl_Factory.create(this.contextProvider, provider19));
        this.provideBackgroundDelayableExecutorProvider = DoubleCheck.provider(ConcurrencyModule_ProvideBackgroundDelayableExecutorFactory.create(this.provideBgLooperProvider));
        DelegateFactory delegateFactory6 = new DelegateFactory();
        this.controlsControllerImplProvider = delegateFactory6;
        this.controlsUiControllerImplProvider = DoubleCheck.provider(ControlsUiControllerImpl_Factory.create(delegateFactory6, this.contextProvider, this.provideMainDelayableExecutorProvider, this.provideBackgroundDelayableExecutorProvider, this.controlsListingControllerImplProvider, this.provideSharePreferencesProvider));
        this.controlsBindingControllerImplProvider = DoubleCheck.provider(ControlsBindingControllerImpl_Factory.create(this.contextProvider, this.provideBackgroundDelayableExecutorProvider, this.controlsControllerImplProvider));
        Provider<Optional<ControlsFavoritePersistenceWrapper>> absentJdkOptionalProvider = absentJdkOptionalProvider();
        this.optionalOfControlsFavoritePersistenceWrapperProvider = absentJdkOptionalProvider;
        DelegateFactory delegateFactory7 = (DelegateFactory) this.controlsControllerImplProvider;
        Provider<ControlsControllerImpl> provider20 = DoubleCheck.provider(ControlsControllerImpl_Factory.create(this.contextProvider, this.provideBackgroundDelayableExecutorProvider, this.controlsUiControllerImplProvider, this.controlsBindingControllerImplProvider, this.controlsListingControllerImplProvider, this.broadcastDispatcherProvider, absentJdkOptionalProvider, this.dumpManagerProvider));
        this.controlsControllerImplProvider = provider20;
        delegateFactory7.setDelegatedProvider(provider20);
        this.controlsProviderSelectorActivityProvider = ControlsProviderSelectorActivity_Factory.create(this.provideMainExecutorProvider, this.provideBackgroundExecutorProvider, this.controlsListingControllerImplProvider, this.controlsControllerImplProvider, this.broadcastDispatcherProvider);
        this.controlsFavoritingActivityProvider = ControlsFavoritingActivity_Factory.create(this.provideMainExecutorProvider, this.controlsControllerImplProvider, this.controlsListingControllerImplProvider, this.broadcastDispatcherProvider);
        this.controlsRequestDialogProvider = ControlsRequestDialog_Factory.create(this.controlsControllerImplProvider, this.broadcastDispatcherProvider, this.controlsListingControllerImplProvider);
        C17254 r12 = new Provider<com.android.systemui.pip.p005tv.dagger.TvPipComponent.Builder>() {
            public com.android.systemui.pip.p005tv.dagger.TvPipComponent.Builder get() {
                return new TvPipComponentBuilder();
            }
        };
        this.tvPipComponentBuilderProvider = r12;
        this.pipMenuActivityProvider = PipMenuActivity_Factory.create(r12, this.pipManagerProvider);
        dagger.internal.MapProviderFactory.Builder builder2 = MapProviderFactory.builder(10);
        builder2.put(TunerActivity.class, TunerActivity_Factory.create());
        builder2.put(ForegroundServicesDialog.class, ForegroundServicesDialog_Factory.create());
        builder2.put(WorkLockActivity.class, this.workLockActivityProvider);
        builder2.put(BrightnessDialog.class, this.brightnessDialogProvider);
        builder2.put(ScreenRecordDialog.class, this.screenRecordDialogProvider);
        builder2.put(BubbleOverflowActivity.class, this.bubbleOverflowActivityProvider);
        builder2.put(ControlsProviderSelectorActivity.class, this.controlsProviderSelectorActivityProvider);
        builder2.put(ControlsFavoritingActivity.class, this.controlsFavoritingActivityProvider);
        builder2.put(ControlsRequestDialog.class, this.controlsRequestDialogProvider);
        builder2.put(PipMenuActivity.class, this.pipMenuActivityProvider);
        this.mapOfClassOfAndProviderOfActivityProvider = builder2.build();
        DozeFactory_Factory create5 = DozeFactory_Factory.create(this.falsingManagerProxyProvider, this.dozeLogProvider, this.dozeParametersProvider, this.batteryControllerImplProvider, this.asyncSensorManagerProvider, this.provideAlarmManagerProvider, this.wakefulnessLifecycleProvider, this.keyguardUpdateMonitorProvider, this.dockManagerImplProvider, SystemServicesModule_ProvideIWallPaperManagerFactory.create(), this.proximitySensorProvider, this.builderProvider2, this.provideHandlerProvider, this.biometricUnlockControllerProvider, this.broadcastDispatcherProvider, this.dozeServiceHostProvider);
        this.dozeFactoryProvider = create5;
        this.dozeServiceProvider = DozeService_Factory.create(create5, this.providePluginManagerProvider);
        this.imageWallpaperProvider = ImageWallpaper_Factory.create(this.dozeParametersProvider);
        Provider<KeyguardLifecyclesDispatcher> provider21 = DoubleCheck.provider(KeyguardLifecyclesDispatcher_Factory.create(this.screenLifecycleProvider, this.wakefulnessLifecycleProvider));
        this.keyguardLifecyclesDispatcherProvider = provider21;
        this.keyguardServiceProvider = KeyguardService_Factory.create(this.newKeyguardViewMediatorProvider, provider21);
        this.systemUIServiceProvider = SystemUIService_Factory.create(this.provideMainHandlerProvider, this.dumpManagerProvider);
        this.systemUIAuxiliaryDumpServiceProvider = SystemUIAuxiliaryDumpService_Factory.create(this.dumpManagerProvider);
        this.providerLayoutInflaterProvider = DoubleCheck.provider(DependencyProvider_ProviderLayoutInflaterFactory.create(builder.dependencyProvider, this.contextProvider));
        ScreenshotNotificationsController_Factory create6 = ScreenshotNotificationsController_Factory.create(this.contextProvider, this.provideWindowManagerProvider);
        this.screenshotNotificationsControllerProvider = create6;
        this.globalScreenshotProvider = DoubleCheck.provider(GlobalScreenshot_Factory.create(this.contextProvider, this.provideResourcesProvider, this.providerLayoutInflaterProvider, create6));
        Provider<GlobalScreenshotLegacy> provider22 = DoubleCheck.provider(GlobalScreenshotLegacy_Factory.create(this.contextProvider, this.provideResourcesProvider, this.providerLayoutInflaterProvider, this.screenshotNotificationsControllerProvider));
        this.globalScreenshotLegacyProvider = provider22;
        this.takeScreenshotServiceProvider = TakeScreenshotService_Factory.create(this.globalScreenshotProvider, provider22, this.provideUserManagerProvider);
        this.recordingServiceProvider = RecordingService_Factory.create(this.recordingControllerProvider);
        this.assistHandleServiceProvider = AssistHandleService_Factory.create(this.assistManagerProvider);
        dagger.internal.MapProviderFactory.Builder builder3 = MapProviderFactory.builder(8);
        builder3.put(DozeService.class, this.dozeServiceProvider);
        builder3.put(ImageWallpaper.class, this.imageWallpaperProvider);
        builder3.put(KeyguardService.class, this.keyguardServiceProvider);
        builder3.put(SystemUIService.class, this.systemUIServiceProvider);
        builder3.put(SystemUIAuxiliaryDumpService.class, this.systemUIAuxiliaryDumpServiceProvider);
        builder3.put(TakeScreenshotService.class, this.takeScreenshotServiceProvider);
        builder3.put(RecordingService.class, this.recordingServiceProvider);
        builder3.put(AssistHandleService.class, this.assistHandleServiceProvider);
        this.mapOfClassOfAndProviderOfServiceProvider = builder3.build();
        this.authControllerProvider = DoubleCheck.provider(AuthController_Factory.create(this.contextProvider, this.provideCommandQueueProvider));
        Provider<String> provider23 = DoubleCheck.provider(SystemUIDefaultModule_ProvideLeakReportEmailFactory.create());
        this.provideLeakReportEmailProvider = provider23;
        Provider<LeakReporter> provider24 = DoubleCheck.provider(LeakReporter_Factory.create(this.contextProvider, this.provideLeakDetectorProvider, provider23));
        this.leakReporterProvider = provider24;
        Provider<GarbageMonitor> provider25 = DoubleCheck.provider(GarbageMonitor_Factory.create(this.contextProvider, this.provideBgLooperProvider, this.provideLeakDetectorProvider, provider24));
        this.garbageMonitorProvider = provider25;
        this.serviceProvider = DoubleCheck.provider(GarbageMonitor_Service_Factory.create(this.contextProvider, provider25));
        this.globalActionsComponentProvider = new DelegateFactory();
        this.provideConnectivityManagagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideConnectivityManagagerFactory.create(this.contextProvider));
        this.provideTelephonyManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideTelephonyManagerFactory.create(this.contextProvider));
        this.provideVibratorProvider = DoubleCheck.provider(SystemServicesModule_ProvideVibratorFactory.create(this.contextProvider));
        Provider<Choreographer> provider26 = DoubleCheck.provider(DependencyProvider_ProvidesChoreographerFactory.create(builder.dependencyProvider));
        this.providesChoreographerProvider = provider26;
        Provider<NotificationShadeDepthController> provider27 = DoubleCheck.provider(NotificationShadeDepthController_Factory.create(this.statusBarStateControllerImplProvider, this.blurUtilsProvider, this.biometricUnlockControllerProvider, this.keyguardStateControllerImplProvider, provider26, this.provideWallpaperManagerProvider, this.notificationShadeWindowControllerProvider, this.dumpManagerProvider));
        Provider<NotificationShadeDepthController> provider28 = provider27;
        this.notificationShadeDepthControllerProvider = provider27;
        GlobalActionsDialog_Factory create7 = GlobalActionsDialog_Factory.create(this.contextProvider, this.globalActionsComponentProvider, this.provideAudioManagerProvider, this.provideIDreamManagerProvider, this.provideDevicePolicyManagerProvider, this.provideLockPatternUtilsProvider, this.broadcastDispatcherProvider, this.provideConnectivityManagagerProvider, this.provideTelephonyManagerProvider, this.provideContentResolverProvider, this.provideVibratorProvider, this.provideResourcesProvider, this.provideConfigurationControllerProvider, this.activityStarterDelegateProvider, this.keyguardStateControllerImplProvider, this.provideUserManagerProvider, this.provideTrustManagerProvider, this.provideIActivityManagerProvider, this.provideTelecomManagerProvider, this.provideMetricsLoggerProvider, provider28, this.sysuiColorExtractorProvider, this.provideIStatusBarServiceProvider, this.blurUtilsProvider, this.notificationShadeWindowControllerProvider, this.controlsUiControllerImplProvider, this.provideIWindowManagerProvider, this.provideBackgroundExecutorProvider, this.controlsListingControllerImplProvider, this.controlsControllerImplProvider);
        this.globalActionsDialogProvider = create7;
        GlobalActionsImpl_Factory create8 = GlobalActionsImpl_Factory.create(this.contextProvider, this.provideCommandQueueProvider, create7, this.blurUtilsProvider);
        this.globalActionsImplProvider = create8;
        DelegateFactory delegateFactory8 = (DelegateFactory) this.globalActionsComponentProvider;
        Provider<GlobalActionsComponent> provider29 = DoubleCheck.provider(GlobalActionsComponent_Factory.create(this.contextProvider, this.provideCommandQueueProvider, this.extensionControllerImplProvider, create8));
        this.globalActionsComponentProvider = provider29;
        delegateFactory8.setDelegatedProvider(provider29);
        this.instantAppNotifierProvider = DoubleCheck.provider(InstantAppNotifier_Factory.create(this.contextProvider, this.provideCommandQueueProvider, this.provideUiBackgroundExecutorProvider, this.provideDividerProvider));
    }

    private void initialize4(Builder builder) {
        this.latencyTesterProvider = DoubleCheck.provider(LatencyTester_Factory.create(this.contextProvider, this.biometricUnlockControllerProvider, this.providePowerManagerProvider, this.broadcastDispatcherProvider));
        this.powerUIProvider = DoubleCheck.provider(PowerUI_Factory.create(this.contextProvider, this.broadcastDispatcherProvider, this.provideCommandQueueProvider, this.provideStatusBarProvider));
        this.screenDecorationsProvider = DoubleCheck.provider(ScreenDecorations_Factory.create(this.contextProvider, this.provideMainHandlerProvider, this.broadcastDispatcherProvider, this.tunerServiceImplProvider));
        this.shortcutKeyDispatcherProvider = DoubleCheck.provider(ShortcutKeyDispatcher_Factory.create(this.contextProvider, this.provideDividerProvider, this.provideRecentsProvider));
        this.sizeCompatModeActivityControllerProvider = DoubleCheck.provider(SizeCompatModeActivityController_Factory.create(this.contextProvider, this.provideActivityManagerWrapperProvider, this.provideCommandQueueProvider));
        this.sliceBroadcastRelayHandlerProvider = DoubleCheck.provider(SliceBroadcastRelayHandler_Factory.create(this.contextProvider, this.broadcastDispatcherProvider));
        this.systemActionsProvider = DoubleCheck.provider(SystemActions_Factory.create(this.contextProvider));
        this.themeOverlayControllerProvider = DoubleCheck.provider(ThemeOverlayController_Factory.create(this.contextProvider, this.broadcastDispatcherProvider, this.provideBgHandlerProvider));
        this.toastUIProvider = DoubleCheck.provider(ToastUI_Factory.create(this.contextProvider, this.provideCommandQueueProvider));
        this.tvStatusBarProvider = DoubleCheck.provider(TvStatusBar_Factory.create(this.contextProvider, this.provideCommandQueueProvider));
        this.volumeUIProvider = DoubleCheck.provider(VolumeUI_Factory.create(this.contextProvider, this.volumeDialogComponentProvider));
        this.windowMagnificationProvider = DoubleCheck.provider(WindowMagnification_Factory.create(this.contextProvider, this.provideMainHandlerProvider));
        dagger.internal.MapProviderFactory.Builder builder2 = MapProviderFactory.builder(21);
        builder2.put(AuthController.class, this.authControllerProvider);
        builder2.put(Divider.class, this.provideDividerProvider);
        builder2.put(GarbageMonitor.Service.class, this.serviceProvider);
        builder2.put(GlobalActionsComponent.class, this.globalActionsComponentProvider);
        builder2.put(InstantAppNotifier.class, this.instantAppNotifierProvider);
        builder2.put(KeyguardViewMediator.class, this.newKeyguardViewMediatorProvider);
        builder2.put(LatencyTester.class, this.latencyTesterProvider);
        builder2.put(PipUI.class, this.pipUIProvider);
        builder2.put(PowerUI.class, this.powerUIProvider);
        builder2.put(Recents.class, this.provideRecentsProvider);
        builder2.put(ScreenDecorations.class, this.screenDecorationsProvider);
        builder2.put(ShortcutKeyDispatcher.class, this.shortcutKeyDispatcherProvider);
        builder2.put(SizeCompatModeActivityController.class, this.sizeCompatModeActivityControllerProvider);
        builder2.put(SliceBroadcastRelayHandler.class, this.sliceBroadcastRelayHandlerProvider);
        builder2.put(StatusBar.class, this.provideStatusBarProvider);
        builder2.put(SystemActions.class, this.systemActionsProvider);
        builder2.put(ThemeOverlayController.class, this.themeOverlayControllerProvider);
        builder2.put(ToastUI.class, this.toastUIProvider);
        builder2.put(TvStatusBar.class, this.tvStatusBarProvider);
        builder2.put(VolumeUI.class, this.volumeUIProvider);
        builder2.put(WindowMagnification.class, this.windowMagnificationProvider);
        this.mapOfClassOfAndProviderOfSystemUIProvider = builder2.build();
        this.overviewProxyRecentsImplProvider = DoubleCheck.provider(OverviewProxyRecentsImpl_Factory.create(this.optionalOfLazyOfStatusBarProvider, this.optionalOfDividerProvider));
        dagger.internal.MapProviderFactory.Builder builder3 = MapProviderFactory.builder(1);
        builder3.put(OverviewProxyRecentsImpl.class, this.overviewProxyRecentsImplProvider);
        this.mapOfClassOfAndProviderOfRecentsImplementationProvider = builder3.build();
        this.actionProxyReceiverProvider = GlobalScreenshot_ActionProxyReceiver_Factory.create(this.optionalOfLazyOfStatusBarProvider);
        dagger.internal.MapProviderFactory.Builder builder4 = MapProviderFactory.builder(1);
        builder4.put(ActionProxyReceiver.class, this.actionProxyReceiverProvider);
        MapProviderFactory build = builder4.build();
        this.mapOfClassOfAndProviderOfBroadcastReceiverProvider = build;
        DelegateFactory delegateFactory = (DelegateFactory) this.contextComponentResolverProvider;
        Provider<ContextComponentResolver> provider = DoubleCheck.provider(ContextComponentResolver_Factory.create(this.mapOfClassOfAndProviderOfActivityProvider, this.mapOfClassOfAndProviderOfServiceProvider, this.mapOfClassOfAndProviderOfSystemUIProvider, this.mapOfClassOfAndProviderOfRecentsImplementationProvider, build));
        this.contextComponentResolverProvider = provider;
        delegateFactory.setDelegatedProvider(provider);
        this.provideAllowNotificationLongPressProvider = DoubleCheck.provider(SystemUIDefaultModule_ProvideAllowNotificationLongPressFactory.create());
        this.flashlightControllerImplProvider = DoubleCheck.provider(FlashlightControllerImpl_Factory.create(this.contextProvider));
        this.provideNightDisplayListenerProvider = DoubleCheck.provider(DependencyProvider_ProvideNightDisplayListenerFactory.create(builder.dependencyProvider, this.contextProvider, this.provideBgHandlerProvider));
        this.managedProfileControllerImplProvider = DoubleCheck.provider(ManagedProfileControllerImpl_Factory.create(this.contextProvider, this.broadcastDispatcherProvider));
        this.securityControllerImplProvider = DoubleCheck.provider(SecurityControllerImpl_Factory.create(this.contextProvider, this.provideBgHandlerProvider, this.broadcastDispatcherProvider, this.provideBackgroundExecutorProvider));
        this.statusBarWindowControllerProvider = DoubleCheck.provider(StatusBarWindowController_Factory.create(this.contextProvider, this.provideWindowManagerProvider, this.superStatusBarViewFactoryProvider, this.provideResourcesProvider));
        this.fragmentServiceProvider = DoubleCheck.provider(FragmentService_Factory.create(this.tvSystemUIRootComponentProvider, this.provideConfigurationControllerProvider));
        this.accessibilityManagerWrapperProvider = DoubleCheck.provider(AccessibilityManagerWrapper_Factory.create(this.contextProvider));
        this.tunablePaddingServiceProvider = DoubleCheck.provider(TunablePadding_TunablePaddingService_Factory.create(this.tunerServiceImplProvider));
        this.uiOffloadThreadProvider = DoubleCheck.provider(UiOffloadThread_Factory.create());
        this.powerNotificationWarningsProvider = DoubleCheck.provider(PowerNotificationWarnings_Factory.create(this.contextProvider, this.activityStarterDelegateProvider));
        this.provideNotificationBlockingHelperManagerProvider = DoubleCheck.provider(C1226x481a0301.create(this.contextProvider, this.provideNotificationGutsManagerProvider, this.provideNotificationEntryManagerProvider, this.provideMetricsLoggerProvider));
        this.provideSensorPrivacyManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideSensorPrivacyManagerFactory.create(this.contextProvider));
        this.foregroundServiceNotificationListenerProvider = DoubleCheck.provider(ForegroundServiceNotificationListener_Factory.create(this.contextProvider, this.foregroundServiceControllerProvider, this.provideNotificationEntryManagerProvider, this.notifPipelineProvider));
        this.clockManagerProvider = DoubleCheck.provider(ClockManager_Factory.create(this.contextProvider, this.injectionInflationControllerProvider, this.providePluginManagerProvider, this.sysuiColorExtractorProvider, this.dockManagerImplProvider, this.broadcastDispatcherProvider));
        this.provideDevicePolicyManagerWrapperProvider = DoubleCheck.provider(DependencyProvider_ProvideDevicePolicyManagerWrapperFactory.create(builder.dependencyProvider));
        this.channelEditorDialogControllerProvider = DoubleCheck.provider(ChannelEditorDialogController_Factory.create(this.contextProvider, this.provideINotificationManagerProvider));
        this.keyguardSecurityModelProvider = DoubleCheck.provider(KeyguardSecurityModel_Factory.create(this.contextProvider));
        DelegateFactory delegateFactory2 = new DelegateFactory();
        this.qSTileHostProvider = delegateFactory2;
        this.wifiTileProvider = WifiTile_Factory.create(delegateFactory2, this.networkControllerImplProvider, this.activityStarterDelegateProvider);
        this.bluetoothTileProvider = BluetoothTile_Factory.create(this.qSTileHostProvider, this.bluetoothControllerImplProvider, this.activityStarterDelegateProvider);
        this.cellularTileProvider = CellularTile_Factory.create(this.qSTileHostProvider, this.networkControllerImplProvider, this.activityStarterDelegateProvider);
        this.dndTileProvider = DndTile_Factory.create(this.qSTileHostProvider, this.zenModeControllerImplProvider, this.activityStarterDelegateProvider, this.broadcastDispatcherProvider, this.provideSharePreferencesProvider);
        this.colorInversionTileProvider = ColorInversionTile_Factory.create(this.qSTileHostProvider);
        this.airplaneModeTileProvider = AirplaneModeTile_Factory.create(this.qSTileHostProvider, this.activityStarterDelegateProvider, this.broadcastDispatcherProvider);
        this.workModeTileProvider = WorkModeTile_Factory.create(this.qSTileHostProvider, this.managedProfileControllerImplProvider);
        this.rotationLockTileProvider = RotationLockTile_Factory.create(this.qSTileHostProvider, this.rotationLockControllerImplProvider);
        this.flashlightTileProvider = FlashlightTile_Factory.create(this.qSTileHostProvider, this.flashlightControllerImplProvider);
        this.locationTileProvider = LocationTile_Factory.create(this.qSTileHostProvider, this.locationControllerImplProvider, this.keyguardStateControllerImplProvider, this.activityStarterDelegateProvider);
        this.castTileProvider = CastTile_Factory.create(this.qSTileHostProvider, this.castControllerImplProvider, this.keyguardStateControllerImplProvider, this.networkControllerImplProvider, this.activityStarterDelegateProvider);
        this.hotspotTileProvider = HotspotTile_Factory.create(this.qSTileHostProvider, this.hotspotControllerImplProvider, this.provideDataSaverControllerProvider);
        this.userTileProvider = UserTile_Factory.create(this.qSTileHostProvider, this.userSwitcherControllerProvider, this.userInfoControllerImplProvider);
        this.batterySaverTileProvider = BatterySaverTile_Factory.create(this.qSTileHostProvider, this.batteryControllerImplProvider);
        this.dataSaverTileProvider = DataSaverTile_Factory.create(this.qSTileHostProvider, this.networkControllerImplProvider);
        this.nightDisplayTileProvider = NightDisplayTile_Factory.create(this.qSTileHostProvider);
        this.nfcTileProvider = NfcTile_Factory.create(this.qSTileHostProvider, this.broadcastDispatcherProvider);
        this.memoryTileProvider = GarbageMonitor_MemoryTile_Factory.create(this.qSTileHostProvider, this.garbageMonitorProvider, this.activityStarterDelegateProvider);
        this.uiModeNightTileProvider = UiModeNightTile_Factory.create(this.qSTileHostProvider, this.provideConfigurationControllerProvider, this.batteryControllerImplProvider);
        ScreenRecordTile_Factory create = ScreenRecordTile_Factory.create(this.qSTileHostProvider, this.recordingControllerProvider);
        ScreenRecordTile_Factory screenRecordTile_Factory = create;
        this.screenRecordTileProvider = create;
        this.qSFactoryImplProvider = DoubleCheck.provider(QSFactoryImpl_Factory.create(this.qSTileHostProvider, this.wifiTileProvider, this.bluetoothTileProvider, this.cellularTileProvider, this.dndTileProvider, this.colorInversionTileProvider, this.airplaneModeTileProvider, this.workModeTileProvider, this.rotationLockTileProvider, this.flashlightTileProvider, this.locationTileProvider, this.castTileProvider, this.hotspotTileProvider, this.userTileProvider, this.batterySaverTileProvider, this.dataSaverTileProvider, this.nightDisplayTileProvider, this.nfcTileProvider, this.memoryTileProvider, this.uiModeNightTileProvider, screenRecordTile_Factory));
        AutoAddTracker_Factory create2 = AutoAddTracker_Factory.create(this.contextProvider);
        this.autoAddTrackerProvider = create2;
        this.autoTileManagerProvider = AutoTileManager_Factory.create(this.contextProvider, create2, this.qSTileHostProvider, this.provideBgHandlerProvider, this.hotspotControllerImplProvider, this.provideDataSaverControllerProvider, this.managedProfileControllerImplProvider, this.provideNightDisplayListenerProvider, this.castControllerImplProvider);
        this.optionalOfStatusBarProvider = PresentJdkOptionalInstanceProvider.m86of(this.provideStatusBarProvider);
        Provider<LogBuffer> provider2 = DoubleCheck.provider(LogModule_ProvideQuickSettingsLogBufferFactory.create(this.provideLogcatEchoTrackerProvider, this.dumpManagerProvider));
        this.provideQuickSettingsLogBufferProvider = provider2;
        QSLogger_Factory create3 = QSLogger_Factory.create(provider2);
        this.qSLoggerProvider = create3;
        DelegateFactory delegateFactory3 = (DelegateFactory) this.qSTileHostProvider;
        Provider<QSTileHost> provider3 = DoubleCheck.provider(QSTileHost_Factory.create(this.contextProvider, this.statusBarIconControllerImplProvider, this.qSFactoryImplProvider, this.provideMainHandlerProvider, this.provideBgLooperProvider, this.providePluginManagerProvider, this.tunerServiceImplProvider, this.autoTileManagerProvider, this.dumpManagerProvider, this.broadcastDispatcherProvider, this.optionalOfStatusBarProvider, create3));
        this.qSTileHostProvider = provider3;
        delegateFactory3.setDelegatedProvider(provider3);
        this.context = builder.context;
        Provider<PackageManager> provider4 = DoubleCheck.provider(SystemServicesModule_ProvidePackageManagerFactory.create(this.contextProvider));
        this.providePackageManagerProvider = provider4;
        Provider<PeopleHubDataSourceImpl> provider5 = DoubleCheck.provider(PeopleHubDataSourceImpl_Factory.create(this.provideNotificationEntryManagerProvider, this.notificationPersonExtractorPluginBoundaryProvider, this.provideUserManagerProvider, this.provideLauncherAppsProvider, provider4, this.contextProvider, this.provideNotificationListenerProvider, this.provideBackgroundExecutorProvider, this.provideMainExecutorProvider, this.notificationLockscreenUserManagerImplProvider, this.peopleNotificationIdentifierImplProvider));
        this.peopleHubDataSourceImplProvider = provider5;
        Provider<PeopleHubViewModelFactoryDataSourceImpl> provider6 = DoubleCheck.provider(PeopleHubViewModelFactoryDataSourceImpl_Factory.create(this.activityStarterDelegateProvider, provider5));
        this.peopleHubViewModelFactoryDataSourceImplProvider = provider6;
        this.peopleHubViewAdapterImplProvider = DoubleCheck.provider(PeopleHubViewAdapterImpl_Factory.create(provider6));
        this.provideUiEventLoggerProvider = DoubleCheck.provider(NotificationsModule_ProvideUiEventLoggerFactory.create());
        this.provideLatencyTrackerProvider = DoubleCheck.provider(SystemServicesModule_ProvideLatencyTrackerFactory.create(this.contextProvider));
        this.provideActivityManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideActivityManagerFactory.create(this.contextProvider));
    }

    public BootCompleteCacheImpl provideBootCacheImpl() {
        return (BootCompleteCacheImpl) this.bootCompleteCacheImplProvider.get();
    }

    public ConfigurationController getConfigurationController() {
        return (ConfigurationController) this.provideConfigurationControllerProvider.get();
    }

    public ContextComponentHelper getContextComponentHelper() {
        return (ContextComponentHelper) this.contextComponentResolverProvider.get();
    }

    public DumpManager createDumpManager() {
        return (DumpManager) this.dumpManagerProvider.get();
    }

    public InitController getInitController() {
        return (InitController) this.initControllerProvider.get();
    }

    public void inject(SystemUIAppComponentFactory systemUIAppComponentFactory) {
        injectSystemUIAppComponentFactory(systemUIAppComponentFactory);
    }

    public void inject(KeyguardSliceProvider keyguardSliceProvider) {
        injectKeyguardSliceProvider(keyguardSliceProvider);
    }

    public DependencyInjector createDependency() {
        return new DependencyInjectorImpl();
    }

    public FragmentCreator createFragmentCreator() {
        return new FragmentCreatorImpl();
    }

    public ViewCreator createViewCreator() {
        return new ViewCreatorImpl();
    }

    private SystemUIAppComponentFactory injectSystemUIAppComponentFactory(SystemUIAppComponentFactory systemUIAppComponentFactory) {
        SystemUIAppComponentFactory_MembersInjector.injectMComponentHelper(systemUIAppComponentFactory, (ContextComponentHelper) this.contextComponentResolverProvider.get());
        return systemUIAppComponentFactory;
    }

    private KeyguardSliceProvider injectKeyguardSliceProvider(KeyguardSliceProvider keyguardSliceProvider) {
        KeyguardSliceProvider_MembersInjector.injectMDozeParameters(keyguardSliceProvider, (DozeParameters) this.dozeParametersProvider.get());
        KeyguardSliceProvider_MembersInjector.injectMZenModeController(keyguardSliceProvider, (ZenModeController) this.zenModeControllerImplProvider.get());
        KeyguardSliceProvider_MembersInjector.injectMNextAlarmController(keyguardSliceProvider, (NextAlarmController) this.nextAlarmControllerImplProvider.get());
        KeyguardSliceProvider_MembersInjector.injectMAlarmManager(keyguardSliceProvider, (AlarmManager) this.provideAlarmManagerProvider.get());
        KeyguardSliceProvider_MembersInjector.injectMContentResolver(keyguardSliceProvider, (ContentResolver) this.provideContentResolverProvider.get());
        KeyguardSliceProvider_MembersInjector.injectMMediaManager(keyguardSliceProvider, (NotificationMediaManager) this.provideNotificationMediaManagerProvider.get());
        KeyguardSliceProvider_MembersInjector.injectMStatusBarStateController(keyguardSliceProvider, (StatusBarStateController) this.statusBarStateControllerImplProvider.get());
        KeyguardSliceProvider_MembersInjector.injectMKeyguardBypassController(keyguardSliceProvider, (KeyguardBypassController) this.keyguardBypassControllerProvider.get());
        return keyguardSliceProvider;
    }

    private static <T> Provider<Optional<T>> absentJdkOptionalProvider() {
        return ABSENT_JDK_OPTIONAL_PROVIDER;
    }
}
