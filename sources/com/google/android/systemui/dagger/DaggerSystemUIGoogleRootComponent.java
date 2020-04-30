package com.google.android.systemui.dagger;

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
import android.view.ViewGroup;
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
import com.android.systemui.SystemUIFactory.ContextHolder;
import com.android.systemui.SystemUIFactory_ContextHolder_ProvideContextFactory;
import com.android.systemui.SystemUIService;
import com.android.systemui.SystemUIService_Factory;
import com.android.systemui.TransactionPool;
import com.android.systemui.TransactionPool_Factory;
import com.android.systemui.UiOffloadThread;
import com.android.systemui.UiOffloadThread_Factory;
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
import com.android.systemui.dagger.SystemUIModule_ProvideKeyguardLiftControllerFactory;
import com.android.systemui.dagger.SystemUIModule_ProvideSysUiStateFactory;
import com.android.systemui.dock.DockManager;
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
import com.android.systemui.pip.phone.PipManager;
import com.android.systemui.pip.phone.PipManager_Factory;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.PluginDependencyProvider;
import com.android.systemui.plugins.PluginDependencyProvider_Factory;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
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
import com.android.systemui.statusbar.phone.dagger.StatusBarViewModule_GetNotificationPanelViewFactory;
import com.android.systemui.statusbar.policy.AccessibilityController;
import com.android.systemui.statusbar.policy.AccessibilityController_Factory;
import com.android.systemui.statusbar.policy.AccessibilityManagerWrapper;
import com.android.systemui.statusbar.policy.AccessibilityManagerWrapper_Factory;
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
import com.android.systemui.statusbar.policy.HeadsUpManager;
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
import com.google.android.systemui.GoogleServices;
import com.google.android.systemui.GoogleServices_Factory;
import com.google.android.systemui.LiveWallpaperScrimController;
import com.google.android.systemui.LiveWallpaperScrimController_Factory;
import com.google.android.systemui.NotificationLockscreenUserManagerGoogle;
import com.google.android.systemui.NotificationLockscreenUserManagerGoogle_Factory;
import com.google.android.systemui.assist.AssistManagerGoogle;
import com.google.android.systemui.assist.AssistManagerGoogle_Factory;
import com.google.android.systemui.assist.OpaEnabledDispatcher_Factory;
import com.google.android.systemui.assist.uihints.AssistantPresenceHandler;
import com.google.android.systemui.assist.uihints.AssistantPresenceHandler_Factory;
import com.google.android.systemui.assist.uihints.AssistantUIHintsModule_BindEdgeLightsInfoListenersFactory;
import com.google.android.systemui.assist.uihints.AssistantUIHintsModule_ProvideActivityStarterFactory;
import com.google.android.systemui.assist.uihints.AssistantUIHintsModule_ProvideAudioInfoListenersFactory;
import com.google.android.systemui.assist.uihints.AssistantUIHintsModule_ProvideCardInfoListenersFactory;
import com.google.android.systemui.assist.uihints.AssistantUIHintsModule_ProvideConfigInfoListenersFactory;
import com.google.android.systemui.assist.uihints.AssistantUIHintsModule_ProvideParentViewGroupFactory;
import com.google.android.systemui.assist.uihints.AssistantWarmer;
import com.google.android.systemui.assist.uihints.AssistantWarmer_Factory;
import com.google.android.systemui.assist.uihints.ColorChangeHandler;
import com.google.android.systemui.assist.uihints.ColorChangeHandler_Factory;
import com.google.android.systemui.assist.uihints.ConfigurationHandler;
import com.google.android.systemui.assist.uihints.ConfigurationHandler_Factory;
import com.google.android.systemui.assist.uihints.FlingVelocityWrapper_Factory;
import com.google.android.systemui.assist.uihints.GlowController;
import com.google.android.systemui.assist.uihints.GlowController_Factory;
import com.google.android.systemui.assist.uihints.GoBackHandler_Factory;
import com.google.android.systemui.assist.uihints.IconController;
import com.google.android.systemui.assist.uihints.IconController_Factory;
import com.google.android.systemui.assist.uihints.KeyboardMonitor_Factory;
import com.google.android.systemui.assist.uihints.LightnessProvider_Factory;
import com.google.android.systemui.assist.uihints.NgaMessageHandler;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.AudioInfoListener;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.CardInfoListener;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.ChipsInfoListener;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.ClearListener;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.ConfigInfoListener;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.EdgeLightsInfoListener;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.GoBackListener;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.GreetingInfoListener;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.KeepAliveListener;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.KeyboardInfoListener;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.StartActivityInfoListener;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.TakeScreenshotListener;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.TranscriptionInfoListener;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.WarmingListener;
import com.google.android.systemui.assist.uihints.NgaMessageHandler.ZerostateInfoListener;
import com.google.android.systemui.assist.uihints.NgaMessageHandler_Factory;
import com.google.android.systemui.assist.uihints.NgaUiController;
import com.google.android.systemui.assist.uihints.NgaUiController_Factory;
import com.google.android.systemui.assist.uihints.OverlappedElementController_Factory;
import com.google.android.systemui.assist.uihints.OverlayUiHost_Factory;
import com.google.android.systemui.assist.uihints.ScrimController;
import com.google.android.systemui.assist.uihints.ScrimController_Factory;
import com.google.android.systemui.assist.uihints.TakeScreenshotHandler_Factory;
import com.google.android.systemui.assist.uihints.TaskStackNotifier_Factory;
import com.google.android.systemui.assist.uihints.TimeoutManager_Factory;
import com.google.android.systemui.assist.uihints.TouchInsideHandler;
import com.google.android.systemui.assist.uihints.TouchInsideHandler_Factory;
import com.google.android.systemui.assist.uihints.TouchOutsideHandler_Factory;
import com.google.android.systemui.assist.uihints.TranscriptionController;
import com.google.android.systemui.assist.uihints.TranscriptionController_Factory;
import com.google.android.systemui.assist.uihints.edgelights.EdgeLightsController;
import com.google.android.systemui.assist.uihints.edgelights.EdgeLightsController_Factory;
import com.google.android.systemui.assist.uihints.input.InputModule_ProvideTouchActionRegionsFactory;
import com.google.android.systemui.assist.uihints.input.InputModule_ProvideTouchInsideRegionsFactory;
import com.google.android.systemui.assist.uihints.input.NgaInputHandler;
import com.google.android.systemui.assist.uihints.input.NgaInputHandler_Factory;
import com.google.android.systemui.assist.uihints.input.TouchActionRegion;
import com.google.android.systemui.assist.uihints.input.TouchInsideRegion;
import com.google.android.systemui.batteryshare.ReverseWirelessCharger;
import com.google.android.systemui.batteryshare.RtxStatusCallback_Factory;
import com.google.android.systemui.columbus.ColumbusContentObserver.Factory;
import com.google.android.systemui.columbus.ColumbusContentObserver_Factory_Factory;
import com.google.android.systemui.columbus.ColumbusModule_ProvideBlockingSystemKeysFactory;
import com.google.android.systemui.columbus.ColumbusModule_ProvideColumbusActionsFactory;
import com.google.android.systemui.columbus.ColumbusModule_ProvideColumbusEffectsFactory;
import com.google.android.systemui.columbus.ColumbusModule_ProvideColumbusGatesFactory;
import com.google.android.systemui.columbus.ColumbusModule_ProvideColumbusLoggerFactory;
import com.google.android.systemui.columbus.ColumbusModule_ProvideFullscreenActionsFactory;
import com.google.android.systemui.columbus.ColumbusModule_ProvideGestureAdjustmentsFactory;
import com.google.android.systemui.columbus.ColumbusModule_ProvideTransientGateDurationFactory;
import com.google.android.systemui.columbus.ColumbusModule_ProvideUserSelectedActionsFactory;
import com.google.android.systemui.columbus.ColumbusService;
import com.google.android.systemui.columbus.ColumbusServiceWrapper_Factory;
import com.google.android.systemui.columbus.ColumbusService_Factory;
import com.google.android.systemui.columbus.ContentResolverWrapper;
import com.google.android.systemui.columbus.ContentResolverWrapper_Factory;
import com.google.android.systemui.columbus.PowerManagerWrapper;
import com.google.android.systemui.columbus.PowerManagerWrapper_Factory;
import com.google.android.systemui.columbus.actions.Action;
import com.google.android.systemui.columbus.actions.DismissTimer;
import com.google.android.systemui.columbus.actions.DismissTimer_Factory;
import com.google.android.systemui.columbus.actions.LaunchCamera;
import com.google.android.systemui.columbus.actions.LaunchCamera_Factory;
import com.google.android.systemui.columbus.actions.LaunchOpa;
import com.google.android.systemui.columbus.actions.LaunchOpa_Factory;
import com.google.android.systemui.columbus.actions.LaunchOverview;
import com.google.android.systemui.columbus.actions.LaunchOverview_Factory;
import com.google.android.systemui.columbus.actions.ManageMedia;
import com.google.android.systemui.columbus.actions.ManageMedia_Factory;
import com.google.android.systemui.columbus.actions.SettingsAction;
import com.google.android.systemui.columbus.actions.SettingsAction_Factory;
import com.google.android.systemui.columbus.actions.SetupWizardAction;
import com.google.android.systemui.columbus.actions.SetupWizardAction_Factory;
import com.google.android.systemui.columbus.actions.SilenceCall;
import com.google.android.systemui.columbus.actions.SilenceCall_Factory;
import com.google.android.systemui.columbus.actions.SnoozeAlarm;
import com.google.android.systemui.columbus.actions.SnoozeAlarm_Factory;
import com.google.android.systemui.columbus.actions.TakeScreenshot;
import com.google.android.systemui.columbus.actions.TakeScreenshot_Factory;
import com.google.android.systemui.columbus.actions.UnpinNotifications;
import com.google.android.systemui.columbus.actions.UserSelectedAction;
import com.google.android.systemui.columbus.actions.UserSelectedAction_Factory;
import com.google.android.systemui.columbus.feedback.AssistInvocationEffect;
import com.google.android.systemui.columbus.feedback.FeedbackEffect;
import com.google.android.systemui.columbus.feedback.HapticClick;
import com.google.android.systemui.columbus.feedback.HapticClick_Factory;
import com.google.android.systemui.columbus.feedback.NavUndimEffect;
import com.google.android.systemui.columbus.feedback.NavUndimEffect_Factory;
import com.google.android.systemui.columbus.feedback.UserActivity;
import com.google.android.systemui.columbus.feedback.UserActivity_Factory;
import com.google.android.systemui.columbus.gates.CameraVisibility;
import com.google.android.systemui.columbus.gates.CameraVisibility_Factory;
import com.google.android.systemui.columbus.gates.ChargingState;
import com.google.android.systemui.columbus.gates.ChargingState_Factory;
import com.google.android.systemui.columbus.gates.FlagEnabled;
import com.google.android.systemui.columbus.gates.FlagEnabled_Factory;
import com.google.android.systemui.columbus.gates.Gate;
import com.google.android.systemui.columbus.gates.KeyguardDeferredSetup_Factory;
import com.google.android.systemui.columbus.gates.KeyguardProximity_Factory;
import com.google.android.systemui.columbus.gates.KeyguardVisibility_Factory;
import com.google.android.systemui.columbus.gates.NavigationBarVisibility;
import com.google.android.systemui.columbus.gates.NavigationBarVisibility_Factory;
import com.google.android.systemui.columbus.gates.NonGesturalNavigation_Factory;
import com.google.android.systemui.columbus.gates.PowerSaveState;
import com.google.android.systemui.columbus.gates.PowerSaveState_Factory;
import com.google.android.systemui.columbus.gates.PowerState_Factory;
import com.google.android.systemui.columbus.gates.SetupWizard;
import com.google.android.systemui.columbus.gates.SetupWizard_Factory;
import com.google.android.systemui.columbus.gates.SystemKeyPress;
import com.google.android.systemui.columbus.gates.SystemKeyPress_Factory;
import com.google.android.systemui.columbus.gates.TelephonyActivity;
import com.google.android.systemui.columbus.gates.TelephonyActivity_Factory;
import com.google.android.systemui.columbus.gates.UsbState;
import com.google.android.systemui.columbus.gates.UsbState_Factory;
import com.google.android.systemui.columbus.gates.VrMode;
import com.google.android.systemui.columbus.gates.VrMode_Factory;
import com.google.android.systemui.columbus.gates.WakeMode;
import com.google.android.systemui.columbus.gates.WakeMode_Factory;
import com.google.android.systemui.columbus.sensors.GestureSensorImpl;
import com.google.android.systemui.columbus.sensors.GestureSensorImpl_Factory;
import com.google.android.systemui.columbus.sensors.config.Adjustment;
import com.google.android.systemui.columbus.sensors.config.GestureConfiguration;
import com.google.android.systemui.columbus.sensors.config.GestureConfiguration_Factory;
import com.google.android.systemui.elmyra.ServiceConfigurationGoogle_Factory;
import com.google.android.systemui.elmyra.actions.CameraAction_Builder_Factory;
import com.google.android.systemui.elmyra.actions.LaunchOpa_Builder_Factory;
import com.google.android.systemui.elmyra.actions.SettingsAction_Builder_Factory;
import com.google.android.systemui.elmyra.actions.SetupWizardAction_Builder_Factory;
import com.google.android.systemui.elmyra.actions.UnpinNotifications_Factory;
import com.google.android.systemui.elmyra.feedback.AssistInvocationEffect_Factory;
import com.google.android.systemui.elmyra.feedback.OpaHomeButton_Factory;
import com.google.android.systemui.elmyra.feedback.OpaLockscreen_Factory;
import com.google.android.systemui.elmyra.feedback.SquishyNavigationButtons_Factory;
import com.google.android.systemui.keyguard.KeyguardSliceProviderGoogle;
import com.google.android.systemui.keyguard.KeyguardSliceProviderGoogle_MembersInjector;
import com.google.android.systemui.p012qs.tileimpl.QSFactoryImplGoogle;
import com.google.android.systemui.p012qs.tileimpl.QSFactoryImplGoogle_Factory;
import com.google.android.systemui.p012qs.tiles.BatteryShareTile_Factory;
import com.google.android.systemui.power.EnhancedEstimatesGoogleImpl;
import com.google.android.systemui.power.EnhancedEstimatesGoogleImpl_Factory;
import com.google.android.systemui.smartspace.SmartSpaceController;
import com.google.android.systemui.smartspace.SmartSpaceController_Factory;
import com.google.android.systemui.statusbar.phone.StatusBarGoogle;
import com.google.android.systemui.statusbar.phone.StatusBarGoogleModule_ProvideStatusBarFactory;
import com.google.android.systemui.statusbar.phone.WallpaperNotifier_Factory;
import com.google.android.systemui.statusbar.policy.BatteryControllerImplGoogle;
import com.google.android.systemui.statusbar.policy.BatteryControllerImplGoogle_Factory;
import dagger.Lazy;
import dagger.internal.DelegateFactory;
import dagger.internal.DoubleCheck;
import dagger.internal.InstanceFactory;
import dagger.internal.MapProviderFactory;
import dagger.internal.Preconditions;
import dagger.internal.SetFactory;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executor;
import javax.inject.Provider;

public final class DaggerSystemUIGoogleRootComponent implements SystemUIGoogleRootComponent {
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
    private AssistInvocationEffect_Factory assistInvocationEffectProvider;
    private Provider<AssistInvocationEffect> assistInvocationEffectProvider2;
    /* access modifiers changed from: private */
    public Provider<AssistManagerGoogle> assistManagerGoogleProvider;
    private Provider<AssistantPresenceHandler> assistantPresenceHandlerProvider;
    private Provider<AssistantWarmer> assistantWarmerProvider;
    /* access modifiers changed from: private */
    public Provider<AsyncSensorManager> asyncSensorManagerProvider;
    private Provider<AuthController> authControllerProvider;
    private AutoAddTracker_Factory autoAddTrackerProvider;
    private AutoTileManager_Factory autoTileManagerProvider;
    /* access modifiers changed from: private */
    public Provider<BatteryControllerImplGoogle> batteryControllerImplGoogleProvider;
    private BatterySaverTile_Factory batterySaverTileProvider;
    private BatteryShareTile_Factory batteryShareTileProvider;
    private AssistantUIHintsModule_BindEdgeLightsInfoListenersFactory bindEdgeLightsInfoListenersProvider;
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
    private LaunchOpa_Builder_Factory builderProvider4;
    private SettingsAction_Builder_Factory builderProvider5;
    private CameraAction_Builder_Factory builderProvider6;
    private SetupWizardAction_Builder_Factory builderProvider7;
    private Provider<BypassHeadsUpNotifier> bypassHeadsUpNotifierProvider;
    private Provider<CameraVisibility> cameraVisibilityProvider;
    /* access modifiers changed from: private */
    public Provider<CastControllerImpl> castControllerImplProvider;
    private CastTile_Factory castTileProvider;
    private CellularTile_Factory cellularTileProvider;
    /* access modifiers changed from: private */
    public Provider<ChannelEditorDialogController> channelEditorDialogControllerProvider;
    private Provider<ChargingState> chargingStateProvider;
    /* access modifiers changed from: private */
    public Provider<ClockManager> clockManagerProvider;
    private Provider<ColorChangeHandler> colorChangeHandlerProvider;
    private ColorInversionTile_Factory colorInversionTileProvider;
    private Provider<ColumbusService> columbusServiceProvider;
    private Provider<ConfigurationHandler> configurationHandlerProvider;
    private Provider<ContentResolverWrapper> contentResolverWrapperProvider;
    private Provider<ContextComponentResolver> contextComponentResolverProvider;
    /* access modifiers changed from: private */
    public ContextHolder contextHolder;
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
    private Provider<DismissTimer> dismissTimerProvider;
    /* access modifiers changed from: private */
    public Provider<DisplayController> displayControllerProvider;
    /* access modifiers changed from: private */
    public Provider<DisplayImeController> displayImeControllerProvider;
    private DndTile_Factory dndTileProvider;
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
    private Provider<EdgeLightsController> edgeLightsControllerProvider;
    /* access modifiers changed from: private */
    public Provider<EnhancedEstimatesGoogleImpl> enhancedEstimatesGoogleImplProvider;
    private Provider<com.android.systemui.statusbar.notification.row.dagger.ExpandableNotificationRowComponent.Builder> expandableNotificationRowComponentBuilderProvider;
    private NotificationLogger_ExpansionStateLogger_Factory expansionStateLoggerProvider;
    /* access modifiers changed from: private */
    public Provider<ExtensionControllerImpl> extensionControllerImplProvider;
    private Provider<Factory> factoryProvider;
    /* access modifiers changed from: private */
    public Provider<FalsingManagerProxy> falsingManagerProxyProvider;
    /* access modifiers changed from: private */
    public Provider<FeatureFlags> featureFlagsProvider;
    private Provider<FlagEnabled> flagEnabledProvider;
    /* access modifiers changed from: private */
    public Provider<FlashlightControllerImpl> flashlightControllerImplProvider;
    private FlashlightTile_Factory flashlightTileProvider;
    private Provider flingVelocityWrapperProvider;
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
    private Provider<GestureConfiguration> gestureConfigurationProvider;
    private Provider<GestureSensorImpl> gestureSensorImplProvider;
    private Provider<GlobalActionsComponent> globalActionsComponentProvider;
    private GlobalActionsDialog_Factory globalActionsDialogProvider;
    private GlobalActionsImpl_Factory globalActionsImplProvider;
    private Provider<GlobalScreenshotLegacy> globalScreenshotLegacyProvider;
    private Provider<GlobalScreenshot> globalScreenshotProvider;
    private Provider<GlowController> glowControllerProvider;
    private Provider goBackHandlerProvider;
    private Provider<GoogleServices> googleServicesProvider;
    private GroupCoalescerLogger_Factory groupCoalescerLoggerProvider;
    private GroupCoalescer_Factory groupCoalescerProvider;
    private Provider<HapticClick> hapticClickProvider;
    private Provider<HeadsUpCoordinator> headsUpCoordinatorProvider;
    private Provider<HighPriorityProvider> highPriorityProvider;
    /* access modifiers changed from: private */
    public Provider<HotspotControllerImpl> hotspotControllerImplProvider;
    private HotspotTile_Factory hotspotTileProvider;
    private IconBuilder_Factory iconBuilderProvider;
    private Provider<IconController> iconControllerProvider;
    private IconManager_Factory iconManagerProvider;
    private ImageWallpaper_Factory imageWallpaperProvider;
    private Provider<InitController> initControllerProvider;
    /* access modifiers changed from: private */
    public Provider<InjectionInflationController> injectionInflationControllerProvider;
    private Provider<InstantAppNotifier> instantAppNotifierProvider;
    private Provider keyboardMonitorProvider;
    /* access modifiers changed from: private */
    public Provider<KeyguardBypassController> keyguardBypassControllerProvider;
    private Provider<KeyguardCoordinator> keyguardCoordinatorProvider;
    private KeyguardDeferredSetup_Factory keyguardDeferredSetupProvider;
    /* access modifiers changed from: private */
    public Provider<KeyguardDismissUtil> keyguardDismissUtilProvider;
    /* access modifiers changed from: private */
    public Provider<KeyguardEnvironmentImpl> keyguardEnvironmentImplProvider;
    private Provider<KeyguardIndicationController> keyguardIndicationControllerProvider;
    private Provider<KeyguardLifecyclesDispatcher> keyguardLifecyclesDispatcherProvider;
    private KeyguardProximity_Factory keyguardProximityProvider;
    /* access modifiers changed from: private */
    public Provider<KeyguardSecurityModel> keyguardSecurityModelProvider;
    private KeyguardService_Factory keyguardServiceProvider;
    /* access modifiers changed from: private */
    public Provider<KeyguardStateControllerImpl> keyguardStateControllerImplProvider;
    /* access modifiers changed from: private */
    public Provider<KeyguardUpdateMonitor> keyguardUpdateMonitorProvider;
    private KeyguardVisibility_Factory keyguardVisibilityProvider;
    private Provider<LatencyTester> latencyTesterProvider;
    private Provider<LaunchCamera> launchCameraProvider;
    private Provider<LaunchOpa> launchOpaProvider;
    private Provider<LaunchOverview> launchOverviewProvider;
    /* access modifiers changed from: private */
    public Provider<LeakReporter> leakReporterProvider;
    /* access modifiers changed from: private */
    public Provider<LightBarController> lightBarControllerProvider;
    private Provider lightnessProvider;
    private Provider<LightsOutNotifController> lightsOutNotifControllerProvider;
    private Provider<LiveWallpaperScrimController> liveWallpaperScrimControllerProvider;
    /* access modifiers changed from: private */
    public Provider<LocationControllerImpl> locationControllerImplProvider;
    private LocationTile_Factory locationTileProvider;
    /* access modifiers changed from: private */
    public Provider<LockscreenGestureLogger> lockscreenGestureLoggerProvider;
    private Provider<LockscreenLockIconController> lockscreenLockIconControllerProvider;
    private Provider<LockscreenWallpaper> lockscreenWallpaperProvider;
    private Provider<ManageMedia> manageMediaProvider;
    /* access modifiers changed from: private */
    public Provider<ManagedProfileControllerImpl> managedProfileControllerImplProvider;
    private Provider<Map<Class<?>, Provider<Activity>>> mapOfClassOfAndProviderOfActivityProvider;
    private Provider<Map<Class<?>, Provider<BroadcastReceiver>>> mapOfClassOfAndProviderOfBroadcastReceiverProvider;
    private Provider<Map<Class<?>, Provider<RecentsImplementation>>> mapOfClassOfAndProviderOfRecentsImplementationProvider;
    private Provider<Map<Class<?>, Provider<Service>>> mapOfClassOfAndProviderOfServiceProvider;
    private Provider<Map<Class<?>, Provider<SystemUI>>> mapOfClassOfAndProviderOfSystemUIProvider;
    private Provider<MediaArtworkProcessor> mediaArtworkProcessorProvider;
    private GarbageMonitor_MemoryTile_Factory memoryTileProvider;
    private Provider<Set<Action>> namedSetOfActionProvider;
    private Provider<Set<Adjustment>> namedSetOfAdjustmentProvider;
    private Provider<Set<FeedbackEffect>> namedSetOfFeedbackEffectProvider;
    private Provider<Set<FeedbackEffect>> namedSetOfFeedbackEffectProvider2;
    private Provider<Set<Gate>> namedSetOfGateProvider;
    private Provider<Set<Integer>> namedSetOfIntegerProvider;
    private Provider<NavUndimEffect> navUndimEffectProvider;
    private Provider<NavigationBarVisibility> navigationBarVisibilityProvider;
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
    private Provider<NgaInputHandler> ngaInputHandlerProvider;
    private Provider<NgaMessageHandler> ngaMessageHandlerProvider;
    private Provider<NgaUiController> ngaUiControllerProvider;
    private NightDisplayTile_Factory nightDisplayTileProvider;
    private NonGesturalNavigation_Factory nonGesturalNavigationProvider;
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
    public Provider<NotificationLockscreenUserManagerGoogle> notificationLockscreenUserManagerGoogleProvider;
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
    private OpaEnabledDispatcher_Factory opaEnabledDispatcherProvider;
    private OpaHomeButton_Factory opaHomeButtonProvider;
    private OpaLockscreen_Factory opaLockscreenProvider;
    private Provider<Optional<CommandQueue>> optionalOfCommandQueueProvider;
    private Provider<Optional<ControlsFavoritePersistenceWrapper>> optionalOfControlsFavoritePersistenceWrapperProvider;
    private Provider<Optional<Divider>> optionalOfDividerProvider;
    private Provider<Optional<HeadsUpManager>> optionalOfHeadsUpManagerProvider;
    private Provider<Optional<Lazy<Recents>>> optionalOfLazyOfRecentsProvider;
    private Provider<Optional<Lazy<StatusBar>>> optionalOfLazyOfStatusBarProvider;
    private Provider<Optional<Recents>> optionalOfRecentsProvider;
    private Provider<Optional<StatusBar>> optionalOfStatusBarProvider;
    private Provider overlappedElementControllerProvider;
    private Provider overlayUiHostProvider;
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
    private Provider<PipManager> pipManagerProvider;
    private PipSnapAlgorithm_Factory pipSnapAlgorithmProvider;
    private Provider<PipSurfaceTransactionHelper> pipSurfaceTransactionHelperProvider;
    private Provider<PipUI> pipUIProvider;
    /* access modifiers changed from: private */
    public Provider<PluginDependencyProvider> pluginDependencyProvider;
    private Provider<PowerManagerWrapper> powerManagerWrapperProvider;
    /* access modifiers changed from: private */
    public Provider<PowerNotificationWarnings> powerNotificationWarningsProvider;
    private Provider<PowerSaveState> powerSaveStateProvider;
    private PowerState_Factory powerStateProvider;
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
    private AssistantUIHintsModule_ProvideActivityStarterFactory provideActivityStarterProvider;
    /* access modifiers changed from: private */
    public Provider<AlarmManager> provideAlarmManagerProvider;
    /* access modifiers changed from: private */
    public Provider<Boolean> provideAllowNotificationLongPressProvider;
    private DependencyProvider_ProvideAlwaysOnDisplayPolicyFactory provideAlwaysOnDisplayPolicyProvider;
    private DependencyProvider_ProvideAmbientDisplayConfigurationFactory provideAmbientDisplayConfigurationProvider;
    private Provider provideAssistHandleBehaviorControllerMapProvider;
    private AssistModule_ProvideAssistHandleViewControllerFactory provideAssistHandleViewControllerProvider;
    private Provider<AssistUtils> provideAssistUtilsProvider;
    private AssistantUIHintsModule_ProvideAudioInfoListenersFactory provideAudioInfoListenersProvider;
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
    private AssistantUIHintsModule_ProvideCardInfoListenersFactory provideCardInfoListenersProvider;
    private Provider<List<Action>> provideColumbusActionsProvider;
    private ColumbusModule_ProvideColumbusEffectsFactory provideColumbusEffectsProvider;
    private ColumbusModule_ProvideColumbusGatesFactory provideColumbusGatesProvider;
    private Provider<MetricsLogger> provideColumbusLoggerProvider;
    /* access modifiers changed from: private */
    public Provider<CommandQueue> provideCommandQueueProvider;
    private Provider<CommonNotifCollection> provideCommonNotifCollectionProvider;
    private AssistantUIHintsModule_ProvideConfigInfoListenersFactory provideConfigInfoListenersProvider;
    /* access modifiers changed from: private */
    public Provider<ConfigurationController> provideConfigurationControllerProvider;
    private Provider<ConnectivityManager> provideConnectivityManagagerProvider;
    private Provider<ContentResolver> provideContentResolverProvider;
    /* access modifiers changed from: private */
    public SystemUIFactory_ContextHolder_ProvideContextFactory provideContextProvider;
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
    /* access modifiers changed from: private */
    public Provider<DockManager> provideDockManagerProvider;
    private Provider<LogBuffer> provideDozeLogBufferProvider;
    private Provider<Executor> provideExecutorProvider;
    private Provider<List<Action>> provideFullscreenActionsProvider;
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
    private Provider<ViewGroup> provideParentViewGroupProvider;
    /* access modifiers changed from: private */
    public Provider<PluginManager> providePluginManagerProvider;
    /* access modifiers changed from: private */
    public Provider<PowerManager> providePowerManagerProvider;
    private Provider<LogBuffer> provideQuickSettingsLogBufferProvider;
    private RecentsModule_ProvideRecentsImplFactory provideRecentsImplProvider;
    /* access modifiers changed from: private */
    public Provider<Recents> provideRecentsProvider;
    private SystemServicesModule_ProvideResourcesFactory provideResourcesProvider;
    private Provider<Optional<ReverseWirelessCharger>> provideReverseWirelessChargerProvider;
    /* access modifiers changed from: private */
    public Provider<SensorPrivacyManager> provideSensorPrivacyManagerProvider;
    private DependencyProvider_ProvideSharePreferencesFactory provideSharePreferencesProvider;
    private Provider<ShortcutManager> provideShortcutManagerProvider;
    /* access modifiers changed from: private */
    public Provider<SmartReplyController> provideSmartReplyControllerProvider;
    /* access modifiers changed from: private */
    public Provider<StatusBarGoogle> provideStatusBarProvider;
    /* access modifiers changed from: private */
    public Provider<SysUiState> provideSysUiStateProvider;
    private Provider<Clock> provideSystemClockProvider;
    private Provider<TelecomManager> provideTelecomManagerProvider;
    private Provider<TelephonyManager> provideTelephonyManagerProvider;
    /* access modifiers changed from: private */
    public Provider<Handler> provideTimeTickHandlerProvider;
    private InputModule_ProvideTouchActionRegionsFactory provideTouchActionRegionsProvider;
    private InputModule_ProvideTouchInsideRegionsFactory provideTouchInsideRegionsProvider;
    private Provider<TrustManager> provideTrustManagerProvider;
    private Provider<Executor> provideUiBackgroundExecutorProvider;
    /* access modifiers changed from: private */
    public Provider<UiEventLogger> provideUiEventLoggerProvider;
    private Provider<UserManager> provideUserManagerProvider;
    private Provider<Map<String, Action>> provideUserSelectedActionsProvider;
    private Provider<Vibrator> provideVibratorProvider;
    /* access modifiers changed from: private */
    public Provider<VisualStabilityManager> provideVisualStabilityManagerProvider;
    private SystemServicesModule_ProvideWallpaperManagerFactory provideWallpaperManagerProvider;
    private Provider<WindowManager> provideWindowManagerProvider;
    private Provider<LayoutInflater> providerLayoutInflaterProvider;
    private Provider<Choreographer> providesChoreographerProvider;
    private DependencyProvider_ProvidesViewMediatorCallbackFactory providesViewMediatorCallbackProvider;
    private ProximitySensor_Factory proximitySensorProvider;
    /* access modifiers changed from: private */
    public Provider<PulseExpansionHandler> pulseExpansionHandlerProvider;
    private Provider<QSFactoryImplGoogle> qSFactoryImplGoogleProvider;
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
    private RtxStatusCallback_Factory rtxStatusCallbackProvider;
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
    private ServiceConfigurationGoogle_Factory serviceConfigurationGoogleProvider;
    private Provider<GarbageMonitor.Service> serviceProvider;
    private Provider<Set<AudioInfoListener>> setOfAudioInfoListenerProvider;
    private Provider<Set<CardInfoListener>> setOfCardInfoListenerProvider;
    private Provider<Set<ChipsInfoListener>> setOfChipsInfoListenerProvider;
    private Provider<Set<ClearListener>> setOfClearListenerProvider;
    private Provider<Set<ConfigInfoListener>> setOfConfigInfoListenerProvider;
    private Provider<Set<EdgeLightsInfoListener>> setOfEdgeLightsInfoListenerProvider;
    private Provider<Set<GoBackListener>> setOfGoBackListenerProvider;
    private Provider<Set<GreetingInfoListener>> setOfGreetingInfoListenerProvider;
    private Provider<Set<KeepAliveListener>> setOfKeepAliveListenerProvider;
    private Provider<Set<KeyboardInfoListener>> setOfKeyboardInfoListenerProvider;
    private Provider<Set<StartActivityInfoListener>> setOfStartActivityInfoListenerProvider;
    private Provider<Set<TakeScreenshotListener>> setOfTakeScreenshotListenerProvider;
    private Provider<Set<TouchActionRegion>> setOfTouchActionRegionProvider;
    private Provider<Set<TouchInsideRegion>> setOfTouchInsideRegionProvider;
    private Provider<Set<TranscriptionInfoListener>> setOfTranscriptionInfoListenerProvider;
    private Provider<Set<WarmingListener>> setOfWarmingListenerProvider;
    private Provider<Set<ZerostateInfoListener>> setOfZerostateInfoListenerProvider;
    private Provider<SettingsAction> settingsActionProvider;
    private Provider<SetupWizardAction> setupWizardActionProvider;
    private Provider<SetupWizard> setupWizardProvider;
    /* access modifiers changed from: private */
    public Provider<ShadeControllerImpl> shadeControllerImplProvider;
    private ShadeListBuilderLogger_Factory shadeListBuilderLoggerProvider;
    private Provider<ShadeListBuilder> shadeListBuilderProvider;
    private Provider<ShortcutKeyDispatcher> shortcutKeyDispatcherProvider;
    private Provider<SilenceCall> silenceCallProvider;
    private Provider<SizeCompatModeActivityController> sizeCompatModeActivityControllerProvider;
    private Provider<SliceBroadcastRelayHandler> sliceBroadcastRelayHandlerProvider;
    /* access modifiers changed from: private */
    public Provider<SmartReplyConstants> smartReplyConstantsProvider;
    private Provider<SmartSpaceController> smartSpaceControllerProvider;
    private Provider<SnoozeAlarm> snoozeAlarmProvider;
    private SquishyNavigationButtons_Factory squishyNavigationButtonsProvider;
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
    private Provider<SystemKeyPress> systemKeyPressProvider;
    private SystemUIAuxiliaryDumpService_Factory systemUIAuxiliaryDumpServiceProvider;
    private Provider<SystemUIGoogleRootComponent> systemUIGoogleRootComponentProvider;
    private SystemUIService_Factory systemUIServiceProvider;
    /* access modifiers changed from: private */
    public Provider<SystemWindows> systemWindowsProvider;
    /* access modifiers changed from: private */
    public Provider<SysuiColorExtractor> sysuiColorExtractorProvider;
    private Provider takeScreenshotHandlerProvider;
    private Provider<TakeScreenshot> takeScreenshotProvider;
    private TakeScreenshotService_Factory takeScreenshotServiceProvider;
    private Provider taskStackNotifierProvider;
    private Provider<TelephonyActivity> telephonyActivityProvider;
    private Provider<ThemeOverlayController> themeOverlayControllerProvider;
    private Provider timeoutManagerProvider;
    private Provider<ToastUI> toastUIProvider;
    private Provider<TouchInsideHandler> touchInsideHandlerProvider;
    private Provider touchOutsideHandlerProvider;
    private Provider<TransactionPool> transactionPoolProvider;
    private Provider<TranscriptionController> transcriptionControllerProvider;
    /* access modifiers changed from: private */
    public Provider<TunablePaddingService> tunablePaddingServiceProvider;
    /* access modifiers changed from: private */
    public Provider<TunerServiceImpl> tunerServiceImplProvider;
    private Provider<TvStatusBar> tvStatusBarProvider;
    private UiModeNightTile_Factory uiModeNightTileProvider;
    /* access modifiers changed from: private */
    public Provider<UiOffloadThread> uiOffloadThreadProvider;
    private UnpinNotifications_Factory unpinNotificationsProvider;
    private Provider<UnpinNotifications> unpinNotificationsProvider2;
    private Provider<UsbState> usbStateProvider;
    private Provider<UserActivity> userActivityProvider;
    /* access modifiers changed from: private */
    public Provider<UserInfoControllerImpl> userInfoControllerImplProvider;
    private Provider<UserSelectedAction> userSelectedActionProvider;
    /* access modifiers changed from: private */
    public Provider<UserSwitcherController> userSwitcherControllerProvider;
    private UserTile_Factory userTileProvider;
    /* access modifiers changed from: private */
    public Provider<VibratorHelper> vibratorHelperProvider;
    private Provider<VolumeDialogComponent> volumeDialogComponentProvider;
    /* access modifiers changed from: private */
    public Provider<VolumeDialogControllerImpl> volumeDialogControllerImplProvider;
    private Provider<VolumeUI> volumeUIProvider;
    private Provider<VrMode> vrModeProvider;
    private Provider<WakeMode> wakeModeProvider;
    /* access modifiers changed from: private */
    public Provider<WakefulnessLifecycle> wakefulnessLifecycleProvider;
    private WallpaperNotifier_Factory wallpaperNotifierProvider;
    private WifiTile_Factory wifiTileProvider;
    private Provider<WindowMagnification> windowMagnificationProvider;
    private WorkLockActivity_Factory workLockActivityProvider;
    private WorkModeTile_Factory workModeTileProvider;
    /* access modifiers changed from: private */
    public Provider<ZenModeControllerImpl> zenModeControllerImplProvider;

    public static final class Builder {
        /* access modifiers changed from: private */
        public ContextHolder contextHolder;
        /* access modifiers changed from: private */
        public DependencyProvider dependencyProvider;

        private Builder() {
        }

        public SystemUIGoogleRootComponent build() {
            if (this.contextHolder != null) {
                if (this.dependencyProvider == null) {
                    this.dependencyProvider = new DependencyProvider();
                }
                return new DaggerSystemUIGoogleRootComponent(this);
            }
            StringBuilder sb = new StringBuilder();
            sb.append(ContextHolder.class.getCanonicalName());
            sb.append(" must be set");
            throw new IllegalStateException(sb.toString());
        }

        public Builder dependencyProvider(DependencyProvider dependencyProvider2) {
            Preconditions.checkNotNull(dependencyProvider2);
            this.dependencyProvider = dependencyProvider2;
            return this;
        }

        public Builder contextHolder(ContextHolder contextHolder2) {
            Preconditions.checkNotNull(contextHolder2);
            this.contextHolder = contextHolder2;
            return this;
        }
    }

    private final class DependencyInjectorImpl implements DependencyInjector {
        private DependencyInjectorImpl() {
        }

        public void createSystemUI(Dependency dependency) {
            injectDependency(dependency);
        }

        private Dependency injectDependency(Dependency dependency) {
            Dependency_MembersInjector.injectMDumpManager(dependency, (DumpManager) DaggerSystemUIGoogleRootComponent.this.dumpManagerProvider.get());
            Dependency_MembersInjector.injectMActivityStarter(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.activityStarterDelegateProvider));
            Dependency_MembersInjector.injectMBroadcastDispatcher(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.broadcastDispatcherProvider));
            Dependency_MembersInjector.injectMAsyncSensorManager(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.asyncSensorManagerProvider));
            Dependency_MembersInjector.injectMBluetoothController(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.bluetoothControllerImplProvider));
            Dependency_MembersInjector.injectMLocationController(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.locationControllerImplProvider));
            Dependency_MembersInjector.injectMRotationLockController(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.rotationLockControllerImplProvider));
            Dependency_MembersInjector.injectMNetworkController(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.networkControllerImplProvider));
            Dependency_MembersInjector.injectMZenModeController(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.zenModeControllerImplProvider));
            Dependency_MembersInjector.injectMHotspotController(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.hotspotControllerImplProvider));
            Dependency_MembersInjector.injectMCastController(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.castControllerImplProvider));
            Dependency_MembersInjector.injectMFlashlightController(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.flashlightControllerImplProvider));
            Dependency_MembersInjector.injectMUserSwitcherController(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.userSwitcherControllerProvider));
            Dependency_MembersInjector.injectMUserInfoController(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.userInfoControllerImplProvider));
            Dependency_MembersInjector.injectMKeyguardMonitor(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.keyguardStateControllerImplProvider));
            Dependency_MembersInjector.injectMKeyguardUpdateMonitor(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.keyguardUpdateMonitorProvider));
            Dependency_MembersInjector.injectMBatteryController(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.batteryControllerImplGoogleProvider));
            Dependency_MembersInjector.injectMNightDisplayListener(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.provideNightDisplayListenerProvider));
            Dependency_MembersInjector.injectMManagedProfileController(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.managedProfileControllerImplProvider));
            Dependency_MembersInjector.injectMNextAlarmController(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.nextAlarmControllerImplProvider));
            Dependency_MembersInjector.injectMDataSaverController(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.provideDataSaverControllerProvider));
            Dependency_MembersInjector.injectMAccessibilityController(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.accessibilityControllerProvider));
            Dependency_MembersInjector.injectMDeviceProvisionedController(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.deviceProvisionedControllerImplProvider));
            Dependency_MembersInjector.injectMPluginManager(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.providePluginManagerProvider));
            Dependency_MembersInjector.injectMAssistManager(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.assistManagerGoogleProvider));
            Dependency_MembersInjector.injectMSecurityController(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.securityControllerImplProvider));
            Dependency_MembersInjector.injectMLeakDetector(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.provideLeakDetectorProvider));
            Dependency_MembersInjector.injectMLeakReporter(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.leakReporterProvider));
            Dependency_MembersInjector.injectMGarbageMonitor(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.garbageMonitorProvider));
            Dependency_MembersInjector.injectMTunerService(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.tunerServiceImplProvider));
            Dependency_MembersInjector.injectMNotificationShadeWindowController(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.notificationShadeWindowControllerProvider));
            Dependency_MembersInjector.injectMTempStatusBarWindowController(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.statusBarWindowControllerProvider));
            Dependency_MembersInjector.injectMDarkIconDispatcher(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.darkIconDispatcherImplProvider));
            Dependency_MembersInjector.injectMConfigurationController(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.provideConfigurationControllerProvider));
            Dependency_MembersInjector.injectMStatusBarIconController(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.statusBarIconControllerImplProvider));
            Dependency_MembersInjector.injectMScreenLifecycle(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.screenLifecycleProvider));
            Dependency_MembersInjector.injectMWakefulnessLifecycle(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.wakefulnessLifecycleProvider));
            Dependency_MembersInjector.injectMFragmentService(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.fragmentServiceProvider));
            Dependency_MembersInjector.injectMExtensionController(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.extensionControllerImplProvider));
            Dependency_MembersInjector.injectMPluginDependencyProvider(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.pluginDependencyProvider));
            Dependency_MembersInjector.injectMLocalBluetoothManager(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.provideLocalBluetoothControllerProvider));
            Dependency_MembersInjector.injectMVolumeDialogController(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.volumeDialogControllerImplProvider));
            Dependency_MembersInjector.injectMMetricsLogger(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.provideMetricsLoggerProvider));
            Dependency_MembersInjector.injectMAccessibilityManagerWrapper(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.accessibilityManagerWrapperProvider));
            Dependency_MembersInjector.injectMSysuiColorExtractor(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.sysuiColorExtractorProvider));
            Dependency_MembersInjector.injectMTunablePaddingService(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.tunablePaddingServiceProvider));
            Dependency_MembersInjector.injectMForegroundServiceController(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.foregroundServiceControllerProvider));
            Dependency_MembersInjector.injectMUiOffloadThread(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.uiOffloadThreadProvider));
            Dependency_MembersInjector.injectMWarningsUI(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.powerNotificationWarningsProvider));
            Dependency_MembersInjector.injectMLightBarController(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.lightBarControllerProvider));
            Dependency_MembersInjector.injectMIWindowManager(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.provideIWindowManagerProvider));
            Dependency_MembersInjector.injectMOverviewProxyService(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.overviewProxyServiceProvider));
            Dependency_MembersInjector.injectMNavBarModeController(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.navigationModeControllerProvider));
            Dependency_MembersInjector.injectMEnhancedEstimates(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.enhancedEstimatesGoogleImplProvider));
            Dependency_MembersInjector.injectMVibratorHelper(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.vibratorHelperProvider));
            Dependency_MembersInjector.injectMIStatusBarService(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.provideIStatusBarServiceProvider));
            Dependency_MembersInjector.injectMDisplayMetrics(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.provideDisplayMetricsProvider));
            Dependency_MembersInjector.injectMLockscreenGestureLogger(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.lockscreenGestureLoggerProvider));
            Dependency_MembersInjector.injectMKeyguardEnvironment(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.keyguardEnvironmentImplProvider));
            Dependency_MembersInjector.injectMShadeController(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.shadeControllerImplProvider));
            Dependency_MembersInjector.injectMNotificationRemoteInputManagerCallback(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.statusBarRemoteInputCallbackProvider));
            Dependency_MembersInjector.injectMAppOpsController(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.appOpsControllerImplProvider));
            Dependency_MembersInjector.injectMNavigationBarController(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.provideNavigationBarControllerProvider));
            Dependency_MembersInjector.injectMStatusBarStateController(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.statusBarStateControllerImplProvider));
            Dependency_MembersInjector.injectMNotificationLockscreenUserManager(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.notificationLockscreenUserManagerGoogleProvider));
            Dependency_MembersInjector.injectMNotificationGroupAlertTransferHelper(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.provideNotificationGroupAlertTransferHelperProvider));
            Dependency_MembersInjector.injectMNotificationGroupManager(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.notificationGroupManagerProvider));
            Dependency_MembersInjector.injectMVisualStabilityManager(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.provideVisualStabilityManagerProvider));
            Dependency_MembersInjector.injectMNotificationGutsManager(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.provideNotificationGutsManagerProvider));
            Dependency_MembersInjector.injectMNotificationMediaManager(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.provideNotificationMediaManagerProvider));
            Dependency_MembersInjector.injectMNotificationBlockingHelperManager(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.provideNotificationBlockingHelperManagerProvider));
            Dependency_MembersInjector.injectMNotificationRemoteInputManager(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.provideNotificationRemoteInputManagerProvider));
            Dependency_MembersInjector.injectMSmartReplyConstants(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.smartReplyConstantsProvider));
            Dependency_MembersInjector.injectMNotificationListener(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.provideNotificationListenerProvider));
            Dependency_MembersInjector.injectMNotificationLogger(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.provideNotificationLoggerProvider));
            Dependency_MembersInjector.injectMNotificationViewHierarchyManager(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.provideNotificationViewHierarchyManagerProvider));
            Dependency_MembersInjector.injectMNotificationFilter(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.notificationFilterProvider));
            Dependency_MembersInjector.injectMKeyguardDismissUtil(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.keyguardDismissUtilProvider));
            Dependency_MembersInjector.injectMSmartReplyController(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.provideSmartReplyControllerProvider));
            Dependency_MembersInjector.injectMRemoteInputQuickSettingsDisabler(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.remoteInputQuickSettingsDisablerProvider));
            Dependency_MembersInjector.injectMBubbleController(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.newBubbleControllerProvider));
            Dependency_MembersInjector.injectMNotificationEntryManager(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.provideNotificationEntryManagerProvider));
            Dependency_MembersInjector.injectMNotificationAlertingManager(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.provideNotificationAlertingManagerProvider));
            Dependency_MembersInjector.injectMSensorPrivacyManager(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.provideSensorPrivacyManagerProvider));
            Dependency_MembersInjector.injectMAutoHideController(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.provideAutoHideControllerProvider));
            Dependency_MembersInjector.injectMForegroundServiceNotificationListener(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.foregroundServiceNotificationListenerProvider));
            Dependency_MembersInjector.injectMBgLooper(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.provideBgLooperProvider));
            Dependency_MembersInjector.injectMBgHandler(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.provideBgHandlerProvider));
            Dependency_MembersInjector.injectMMainLooper(dependency, DoubleCheck.lazy(ConcurrencyModule_ProvideMainLooperFactory.create()));
            Dependency_MembersInjector.injectMMainHandler(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.provideMainHandlerProvider));
            Dependency_MembersInjector.injectMTimeTickHandler(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.provideTimeTickHandlerProvider));
            Dependency_MembersInjector.injectMLeakReportEmail(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.provideLeakReportEmailProvider));
            Dependency_MembersInjector.injectMClockManager(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.clockManagerProvider));
            Dependency_MembersInjector.injectMActivityManagerWrapper(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.provideActivityManagerWrapperProvider));
            Dependency_MembersInjector.injectMDevicePolicyManagerWrapper(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.provideDevicePolicyManagerWrapperProvider));
            Dependency_MembersInjector.injectMPackageManagerWrapper(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.providePackageManagerWrapperProvider));
            Dependency_MembersInjector.injectMSensorPrivacyController(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.sensorPrivacyControllerImplProvider));
            Dependency_MembersInjector.injectMDockManager(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.provideDockManagerProvider));
            Dependency_MembersInjector.injectMChannelEditorDialogController(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.channelEditorDialogControllerProvider));
            Dependency_MembersInjector.injectMINotificationManager(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.provideINotificationManagerProvider));
            Dependency_MembersInjector.injectMSysUiStateFlagsContainer(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.provideSysUiStateProvider));
            Dependency_MembersInjector.injectMAlarmManager(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.provideAlarmManagerProvider));
            Dependency_MembersInjector.injectMKeyguardSecurityModel(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.keyguardSecurityModelProvider));
            Dependency_MembersInjector.injectMDozeParameters(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.dozeParametersProvider));
            Dependency_MembersInjector.injectMWallpaperManager(dependency, DoubleCheck.lazy(SystemServicesModule_ProvideIWallPaperManagerFactory.create()));
            Dependency_MembersInjector.injectMCommandQueue(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.provideCommandQueueProvider));
            Dependency_MembersInjector.injectMRecents(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.provideRecentsProvider));
            Dependency_MembersInjector.injectMStatusBar(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.provideStatusBarProvider));
            Dependency_MembersInjector.injectMDisplayController(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.displayControllerProvider));
            Dependency_MembersInjector.injectMSystemWindows(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.systemWindowsProvider));
            Dependency_MembersInjector.injectMDisplayImeController(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.displayImeControllerProvider));
            Dependency_MembersInjector.injectMRecordingController(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.recordingControllerProvider));
            Dependency_MembersInjector.injectMProtoTracer(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.protoTracerProvider));
            Dependency_MembersInjector.injectMDivider(dependency, DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.provideDividerProvider));
            return dependency;
        }
    }

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
            dagger.internal.Factory create = InstanceFactory.create(expandableNotificationRowComponentBuilder.expandableNotificationRow);
            this.expandableNotificationRowProvider = create;
            ExpandableViewController_Factory create2 = ExpandableViewController_Factory.create(create);
            this.expandableViewControllerProvider = create2;
            ExpandableOutlineViewController_Factory create3 = ExpandableOutlineViewController_Factory.create(this.expandableNotificationRowProvider, create2);
            this.expandableOutlineViewControllerProvider = create3;
            this.activatableNotificationViewControllerProvider = ActivatableNotificationViewController_Factory.create(this.expandableNotificationRowProvider, create3, DaggerSystemUIGoogleRootComponent.this.provideAccessibilityManagerProvider, DaggerSystemUIGoogleRootComponent.this.falsingManagerProxyProvider);
            dagger.internal.Factory create4 = InstanceFactory.create(expandableNotificationRowComponentBuilder.notificationEntry);
            this.notificationEntryProvider = create4;
            this.provideStatusBarNotificationProvider = C1294xc255c3ca.create(create4);
            this.provideAppNameProvider = C1292x3e2d0aca.create(DaggerSystemUIGoogleRootComponent.this.provideContextProvider, this.provideStatusBarNotificationProvider);
            this.provideNotificationKeyProvider = C1293xdc9a80a2.create(this.provideStatusBarNotificationProvider);
            this.rowContentBindStageProvider = InstanceFactory.create(expandableNotificationRowComponentBuilder.rowContentBindStage);
            this.onExpandClickListenerProvider = InstanceFactory.create(expandableNotificationRowComponentBuilder.onExpandClickListener);
            this.inflationCallbackProvider = InstanceFactory.create(expandableNotificationRowComponentBuilder.inflationCallback);
            this.onDismissRunnableProvider = InstanceFactory.create(expandableNotificationRowComponentBuilder.onDismissRunnable);
            this.expandableNotificationRowControllerProvider = DoubleCheck.provider(ExpandableNotificationRowController_Factory.create(this.expandableNotificationRowProvider, this.activatableNotificationViewControllerProvider, DaggerSystemUIGoogleRootComponent.this.provideNotificationMediaManagerProvider, DaggerSystemUIGoogleRootComponent.this.providePluginManagerProvider, DaggerSystemUIGoogleRootComponent.this.bindSystemClockProvider, this.provideAppNameProvider, this.provideNotificationKeyProvider, DaggerSystemUIGoogleRootComponent.this.keyguardBypassControllerProvider, DaggerSystemUIGoogleRootComponent.this.notificationGroupManagerProvider, this.rowContentBindStageProvider, DaggerSystemUIGoogleRootComponent.this.provideNotificationLoggerProvider, DaggerSystemUIGoogleRootComponent.this.provideHeadsUpManagerPhoneProvider, this.onExpandClickListenerProvider, DaggerSystemUIGoogleRootComponent.this.statusBarStateControllerImplProvider, this.inflationCallbackProvider, DaggerSystemUIGoogleRootComponent.this.provideNotificationGutsManagerProvider, DaggerSystemUIGoogleRootComponent.this.provideAllowNotificationLongPressProvider, this.onDismissRunnableProvider, DaggerSystemUIGoogleRootComponent.this.falsingManagerProxyProvider));
        }

        public ExpandableNotificationRowController getExpandableNotificationRowController() {
            return (ExpandableNotificationRowController) this.expandableNotificationRowControllerProvider.get();
        }
    }

    private final class FragmentCreatorImpl implements FragmentCreator {
        private FragmentCreatorImpl() {
        }

        private com.android.keyguard.CarrierTextController.Builder getBuilder4() {
            return new com.android.keyguard.CarrierTextController.Builder(SystemUIFactory_ContextHolder_ProvideContextFactory.proxyProvideContext(DaggerSystemUIGoogleRootComponent.this.contextHolder), DaggerSystemUIGoogleRootComponent.this.getMainResources());
        }

        private com.android.systemui.p007qs.carrier.QSCarrierGroupController.Builder getBuilder3() {
            com.android.systemui.p007qs.carrier.QSCarrierGroupController.Builder builder = new com.android.systemui.p007qs.carrier.QSCarrierGroupController.Builder((ActivityStarter) DaggerSystemUIGoogleRootComponent.this.activityStarterDelegateProvider.get(), DaggerSystemUIGoogleRootComponent.this.getBackgroundHandler(), ConcurrencyModule_ProvideMainLooperFactory.proxyProvideMainLooper(), (NetworkController) DaggerSystemUIGoogleRootComponent.this.networkControllerImplProvider.get(), getBuilder4());
            return builder;
        }

        private com.android.systemui.p007qs.QuickStatusBarHeaderController.Builder getBuilder2() {
            return new com.android.systemui.p007qs.QuickStatusBarHeaderController.Builder(getBuilder3());
        }

        private com.android.systemui.p007qs.QSContainerImplController.Builder getBuilder() {
            return new com.android.systemui.p007qs.QSContainerImplController.Builder(getBuilder2());
        }

        public NavigationBarFragment createNavigationBarFragment() {
            NavigationBarFragment navigationBarFragment = new NavigationBarFragment((AccessibilityManagerWrapper) DaggerSystemUIGoogleRootComponent.this.accessibilityManagerWrapperProvider.get(), (DeviceProvisionedController) DaggerSystemUIGoogleRootComponent.this.deviceProvisionedControllerImplProvider.get(), (MetricsLogger) DaggerSystemUIGoogleRootComponent.this.provideMetricsLoggerProvider.get(), (AssistManager) DaggerSystemUIGoogleRootComponent.this.assistManagerGoogleProvider.get(), (OverviewProxyService) DaggerSystemUIGoogleRootComponent.this.overviewProxyServiceProvider.get(), (NavigationModeController) DaggerSystemUIGoogleRootComponent.this.navigationModeControllerProvider.get(), (StatusBarStateController) DaggerSystemUIGoogleRootComponent.this.statusBarStateControllerImplProvider.get(), (SysUiState) DaggerSystemUIGoogleRootComponent.this.provideSysUiStateProvider.get(), (BroadcastDispatcher) DaggerSystemUIGoogleRootComponent.this.broadcastDispatcherProvider.get(), (CommandQueue) DaggerSystemUIGoogleRootComponent.this.provideCommandQueueProvider.get(), (Divider) DaggerSystemUIGoogleRootComponent.this.provideDividerProvider.get(), Optional.of((Recents) DaggerSystemUIGoogleRootComponent.this.provideRecentsProvider.get()), DoubleCheck.lazy(DaggerSystemUIGoogleRootComponent.this.provideStatusBarProvider), (ShadeController) DaggerSystemUIGoogleRootComponent.this.shadeControllerImplProvider.get(), (NotificationRemoteInputManager) DaggerSystemUIGoogleRootComponent.this.provideNotificationRemoteInputManagerProvider.get(), DaggerSystemUIGoogleRootComponent.this.getMainHandler());
            return navigationBarFragment;
        }

        public QSFragment createQSFragment() {
            QSFragment qSFragment = new QSFragment((RemoteInputQuickSettingsDisabler) DaggerSystemUIGoogleRootComponent.this.remoteInputQuickSettingsDisablerProvider.get(), (InjectionInflationController) DaggerSystemUIGoogleRootComponent.this.injectionInflationControllerProvider.get(), (QSTileHost) DaggerSystemUIGoogleRootComponent.this.qSTileHostProvider.get(), (StatusBarStateController) DaggerSystemUIGoogleRootComponent.this.statusBarStateControllerImplProvider.get(), (CommandQueue) DaggerSystemUIGoogleRootComponent.this.provideCommandQueueProvider.get(), getBuilder());
            return qSFragment;
        }
    }

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
            return new ActivatableNotificationViewController(this.activatableNotificationView, getExpandableOutlineViewController(), (AccessibilityManager) DaggerSystemUIGoogleRootComponent.this.provideAccessibilityManagerProvider.get(), (FalsingManager) DaggerSystemUIGoogleRootComponent.this.falsingManagerProxyProvider.get());
        }
    }

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
        public static <T> Provider<Optional<T>> m121of(Provider<T> provider) {
            return new PresentJdkOptionalInstanceProvider(provider);
        }
    }

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
        public static <T> Provider<Optional<Lazy<T>>> m122of(Provider<T> provider) {
            return new PresentJdkOptionalLazyProvider(provider);
        }
    }

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
            dagger.internal.Factory create = InstanceFactory.create(statusBarComponentBuilder.statusBarWindowView);
            this.statusBarWindowViewProvider = create;
            this.getNotificationPanelViewProvider = DoubleCheck.provider(StatusBarViewModule_GetNotificationPanelViewFactory.create(create));
            this.builderProvider = FlingAnimationUtils_Builder_Factory.create(DaggerSystemUIGoogleRootComponent.this.provideDisplayMetricsProvider);
            this.notificationPanelViewControllerProvider = DoubleCheck.provider(NotificationPanelViewController_Factory.create(this.getNotificationPanelViewProvider, DaggerSystemUIGoogleRootComponent.this.injectionInflationControllerProvider, DaggerSystemUIGoogleRootComponent.this.notificationWakeUpCoordinatorProvider, DaggerSystemUIGoogleRootComponent.this.pulseExpansionHandlerProvider, DaggerSystemUIGoogleRootComponent.this.dynamicPrivacyControllerProvider, DaggerSystemUIGoogleRootComponent.this.keyguardBypassControllerProvider, DaggerSystemUIGoogleRootComponent.this.falsingManagerProxyProvider, DaggerSystemUIGoogleRootComponent.this.shadeControllerImplProvider, DaggerSystemUIGoogleRootComponent.this.notificationLockscreenUserManagerGoogleProvider, DaggerSystemUIGoogleRootComponent.this.provideNotificationEntryManagerProvider, DaggerSystemUIGoogleRootComponent.this.keyguardStateControllerImplProvider, DaggerSystemUIGoogleRootComponent.this.statusBarStateControllerImplProvider, DaggerSystemUIGoogleRootComponent.this.dozeLogProvider, DaggerSystemUIGoogleRootComponent.this.dozeParametersProvider, DaggerSystemUIGoogleRootComponent.this.provideCommandQueueProvider, DaggerSystemUIGoogleRootComponent.this.vibratorHelperProvider, DaggerSystemUIGoogleRootComponent.this.provideLatencyTrackerProvider, DaggerSystemUIGoogleRootComponent.this.providePowerManagerProvider, DaggerSystemUIGoogleRootComponent.this.provideAccessibilityManagerProvider, DaggerSystemUIGoogleRootComponent.this.provideDisplayIdProvider, DaggerSystemUIGoogleRootComponent.this.keyguardUpdateMonitorProvider, DaggerSystemUIGoogleRootComponent.this.provideMetricsLoggerProvider, DaggerSystemUIGoogleRootComponent.this.provideActivityManagerProvider, DaggerSystemUIGoogleRootComponent.this.zenModeControllerImplProvider, DaggerSystemUIGoogleRootComponent.this.provideConfigurationControllerProvider, this.builderProvider, DaggerSystemUIGoogleRootComponent.this.statusBarTouchableRegionManagerProvider));
        }

        public NotificationShadeWindowViewController getNotificationShadeWindowViewController() {
            NotificationShadeWindowViewController notificationShadeWindowViewController = new NotificationShadeWindowViewController((InjectionInflationController) DaggerSystemUIGoogleRootComponent.this.injectionInflationControllerProvider.get(), (NotificationWakeUpCoordinator) DaggerSystemUIGoogleRootComponent.this.notificationWakeUpCoordinatorProvider.get(), (PulseExpansionHandler) DaggerSystemUIGoogleRootComponent.this.pulseExpansionHandlerProvider.get(), (DynamicPrivacyController) DaggerSystemUIGoogleRootComponent.this.dynamicPrivacyControllerProvider.get(), (KeyguardBypassController) DaggerSystemUIGoogleRootComponent.this.keyguardBypassControllerProvider.get(), (FalsingManager) DaggerSystemUIGoogleRootComponent.this.falsingManagerProxyProvider.get(), (PluginManager) DaggerSystemUIGoogleRootComponent.this.providePluginManagerProvider.get(), (TunerService) DaggerSystemUIGoogleRootComponent.this.tunerServiceImplProvider.get(), (NotificationLockscreenUserManager) DaggerSystemUIGoogleRootComponent.this.notificationLockscreenUserManagerGoogleProvider.get(), (NotificationEntryManager) DaggerSystemUIGoogleRootComponent.this.provideNotificationEntryManagerProvider.get(), (KeyguardStateController) DaggerSystemUIGoogleRootComponent.this.keyguardStateControllerImplProvider.get(), (SysuiStatusBarStateController) DaggerSystemUIGoogleRootComponent.this.statusBarStateControllerImplProvider.get(), (DozeLog) DaggerSystemUIGoogleRootComponent.this.dozeLogProvider.get(), (DozeParameters) DaggerSystemUIGoogleRootComponent.this.dozeParametersProvider.get(), (CommandQueue) DaggerSystemUIGoogleRootComponent.this.provideCommandQueueProvider.get(), (ShadeController) DaggerSystemUIGoogleRootComponent.this.shadeControllerImplProvider.get(), (DockManager) DaggerSystemUIGoogleRootComponent.this.provideDockManagerProvider.get(), (NotificationShadeDepthController) DaggerSystemUIGoogleRootComponent.this.notificationShadeDepthControllerProvider.get(), this.statusBarWindowView, (NotificationPanelViewController) this.notificationPanelViewControllerProvider.get(), (SuperStatusBarViewFactory) DaggerSystemUIGoogleRootComponent.this.superStatusBarViewFactoryProvider.get());
            return notificationShadeWindowViewController;
        }

        public StatusBarWindowController getStatusBarWindowController() {
            return (StatusBarWindowController) DaggerSystemUIGoogleRootComponent.this.statusBarWindowControllerProvider.get();
        }

        public NotificationPanelViewController getNotificationPanelViewController() {
            return (NotificationPanelViewController) this.notificationPanelViewControllerProvider.get();
        }
    }

    private final class ViewCreatorImpl implements ViewCreator {

        private final class ViewInstanceCreatorImpl implements ViewInstanceCreator {
            private ViewAttributeProvider viewAttributeProvider;

            private ViewInstanceCreatorImpl(ViewAttributeProvider viewAttributeProvider2) {
                initialize(viewAttributeProvider2);
            }

            private NotificationSectionsManager getNotificationSectionsManager() {
                return NotificationSectionsManager_Factory.newNotificationSectionsManager((ActivityStarter) DaggerSystemUIGoogleRootComponent.this.activityStarterDelegateProvider.get(), (StatusBarStateController) DaggerSystemUIGoogleRootComponent.this.statusBarStateControllerImplProvider.get(), (ConfigurationController) DaggerSystemUIGoogleRootComponent.this.provideConfigurationControllerProvider.get(), (PeopleHubViewAdapter) DaggerSystemUIGoogleRootComponent.this.peopleHubViewAdapterImplProvider.get(), DaggerSystemUIGoogleRootComponent.this.getNotificationSectionsFeatureManager());
            }

            private TileQueryHelper getTileQueryHelper() {
                return new TileQueryHelper(SystemUIFactory_ContextHolder_ProvideContextFactory.proxyProvideContext(DaggerSystemUIGoogleRootComponent.this.contextHolder), DaggerSystemUIGoogleRootComponent.this.getMainExecutor(), (Executor) DaggerSystemUIGoogleRootComponent.this.provideBackgroundExecutorProvider.get());
            }

            private void initialize(ViewAttributeProvider viewAttributeProvider2) {
                Preconditions.checkNotNull(viewAttributeProvider2);
                this.viewAttributeProvider = viewAttributeProvider2;
            }

            public QuickStatusBarHeader createQsHeader() {
                QuickStatusBarHeader quickStatusBarHeader = new QuickStatusBarHeader(C1737x240b4695.proxyProvideContext(this.viewAttributeProvider), C1736xf2fddc0a.proxyProvideAttributeSet(this.viewAttributeProvider), (NextAlarmController) DaggerSystemUIGoogleRootComponent.this.nextAlarmControllerImplProvider.get(), (ZenModeController) DaggerSystemUIGoogleRootComponent.this.zenModeControllerImplProvider.get(), (StatusBarIconController) DaggerSystemUIGoogleRootComponent.this.statusBarIconControllerImplProvider.get(), (ActivityStarter) DaggerSystemUIGoogleRootComponent.this.activityStarterDelegateProvider.get(), (CommandQueue) DaggerSystemUIGoogleRootComponent.this.provideCommandQueueProvider.get(), (BroadcastDispatcher) DaggerSystemUIGoogleRootComponent.this.broadcastDispatcherProvider.get());
                return quickStatusBarHeader;
            }

            public QSFooterImpl createQsFooter() {
                QSFooterImpl qSFooterImpl = new QSFooterImpl(C1737x240b4695.proxyProvideContext(this.viewAttributeProvider), C1736xf2fddc0a.proxyProvideAttributeSet(this.viewAttributeProvider), (ActivityStarter) DaggerSystemUIGoogleRootComponent.this.activityStarterDelegateProvider.get(), (UserInfoController) DaggerSystemUIGoogleRootComponent.this.userInfoControllerImplProvider.get(), (DeviceProvisionedController) DaggerSystemUIGoogleRootComponent.this.deviceProvisionedControllerImplProvider.get());
                return qSFooterImpl;
            }

            public NotificationStackScrollLayout createNotificationStackScrollLayout() {
                NotificationStackScrollLayout notificationStackScrollLayout = new NotificationStackScrollLayout(C1737x240b4695.proxyProvideContext(this.viewAttributeProvider), C1736xf2fddc0a.proxyProvideAttributeSet(this.viewAttributeProvider), ((Boolean) DaggerSystemUIGoogleRootComponent.this.provideAllowNotificationLongPressProvider.get()).booleanValue(), (NotificationRoundnessManager) DaggerSystemUIGoogleRootComponent.this.notificationRoundnessManagerProvider.get(), (DynamicPrivacyController) DaggerSystemUIGoogleRootComponent.this.dynamicPrivacyControllerProvider.get(), (SysuiStatusBarStateController) DaggerSystemUIGoogleRootComponent.this.statusBarStateControllerImplProvider.get(), (HeadsUpManagerPhone) DaggerSystemUIGoogleRootComponent.this.provideHeadsUpManagerPhoneProvider.get(), (KeyguardBypassController) DaggerSystemUIGoogleRootComponent.this.keyguardBypassControllerProvider.get(), (FalsingManager) DaggerSystemUIGoogleRootComponent.this.falsingManagerProxyProvider.get(), (NotificationLockscreenUserManager) DaggerSystemUIGoogleRootComponent.this.notificationLockscreenUserManagerGoogleProvider.get(), (NotificationGutsManager) DaggerSystemUIGoogleRootComponent.this.provideNotificationGutsManagerProvider.get(), (ZenModeController) DaggerSystemUIGoogleRootComponent.this.zenModeControllerImplProvider.get(), getNotificationSectionsManager(), (ForegroundServiceSectionController) DaggerSystemUIGoogleRootComponent.this.foregroundServiceSectionControllerProvider.get(), (ForegroundServiceDismissalFeatureController) DaggerSystemUIGoogleRootComponent.this.foregroundServiceDismissalFeatureControllerProvider.get(), (FeatureFlags) DaggerSystemUIGoogleRootComponent.this.featureFlagsProvider.get(), (NotifPipeline) DaggerSystemUIGoogleRootComponent.this.notifPipelineProvider.get(), (NotificationEntryManager) DaggerSystemUIGoogleRootComponent.this.provideNotificationEntryManagerProvider.get(), (NotifCollection) DaggerSystemUIGoogleRootComponent.this.notifCollectionProvider.get(), (UiEventLogger) DaggerSystemUIGoogleRootComponent.this.provideUiEventLoggerProvider.get());
                return notificationStackScrollLayout;
            }

            public NotificationShelf creatNotificationShelf() {
                return new NotificationShelf(C1737x240b4695.proxyProvideContext(this.viewAttributeProvider), C1736xf2fddc0a.proxyProvideAttributeSet(this.viewAttributeProvider), (KeyguardBypassController) DaggerSystemUIGoogleRootComponent.this.keyguardBypassControllerProvider.get());
            }

            public KeyguardClockSwitch createKeyguardClockSwitch() {
                KeyguardClockSwitch keyguardClockSwitch = new KeyguardClockSwitch(C1737x240b4695.proxyProvideContext(this.viewAttributeProvider), C1736xf2fddc0a.proxyProvideAttributeSet(this.viewAttributeProvider), (StatusBarStateController) DaggerSystemUIGoogleRootComponent.this.statusBarStateControllerImplProvider.get(), (SysuiColorExtractor) DaggerSystemUIGoogleRootComponent.this.sysuiColorExtractorProvider.get(), (ClockManager) DaggerSystemUIGoogleRootComponent.this.clockManagerProvider.get());
                return keyguardClockSwitch;
            }

            public KeyguardSliceView createKeyguardSliceView() {
                KeyguardSliceView keyguardSliceView = new KeyguardSliceView(C1737x240b4695.proxyProvideContext(this.viewAttributeProvider), C1736xf2fddc0a.proxyProvideAttributeSet(this.viewAttributeProvider), (ActivityStarter) DaggerSystemUIGoogleRootComponent.this.activityStarterDelegateProvider.get(), (ConfigurationController) DaggerSystemUIGoogleRootComponent.this.provideConfigurationControllerProvider.get(), (TunerService) DaggerSystemUIGoogleRootComponent.this.tunerServiceImplProvider.get(), DaggerSystemUIGoogleRootComponent.this.getMainResources());
                return keyguardSliceView;
            }

            public KeyguardMessageArea createKeyguardMessageArea() {
                return new KeyguardMessageArea(C1737x240b4695.proxyProvideContext(this.viewAttributeProvider), C1736xf2fddc0a.proxyProvideAttributeSet(this.viewAttributeProvider), (ConfigurationController) DaggerSystemUIGoogleRootComponent.this.provideConfigurationControllerProvider.get());
            }

            public LockIcon createLockIcon() {
                LockIcon lockIcon = new LockIcon(C1737x240b4695.proxyProvideContext(this.viewAttributeProvider), C1736xf2fddc0a.proxyProvideAttributeSet(this.viewAttributeProvider), (AccessibilityController) DaggerSystemUIGoogleRootComponent.this.accessibilityControllerProvider.get(), (KeyguardBypassController) DaggerSystemUIGoogleRootComponent.this.keyguardBypassControllerProvider.get(), (NotificationWakeUpCoordinator) DaggerSystemUIGoogleRootComponent.this.notificationWakeUpCoordinatorProvider.get(), (KeyguardStateController) DaggerSystemUIGoogleRootComponent.this.keyguardStateControllerImplProvider.get(), (HeadsUpManagerPhone) DaggerSystemUIGoogleRootComponent.this.provideHeadsUpManagerPhoneProvider.get());
                return lockIcon;
            }

            public QSPanel createQSPanel() {
                QSPanel qSPanel = new QSPanel(C1737x240b4695.proxyProvideContext(this.viewAttributeProvider), C1736xf2fddc0a.proxyProvideAttributeSet(this.viewAttributeProvider), (DumpManager) DaggerSystemUIGoogleRootComponent.this.dumpManagerProvider.get(), (BroadcastDispatcher) DaggerSystemUIGoogleRootComponent.this.broadcastDispatcherProvider.get(), DaggerSystemUIGoogleRootComponent.this.getQSLogger(), (NotificationMediaManager) DaggerSystemUIGoogleRootComponent.this.provideNotificationMediaManagerProvider.get(), DaggerSystemUIGoogleRootComponent.this.getMainExecutor(), (Executor) DaggerSystemUIGoogleRootComponent.this.provideBackgroundExecutorProvider.get(), (LocalBluetoothManager) DaggerSystemUIGoogleRootComponent.this.provideLocalBluetoothControllerProvider.get());
                return qSPanel;
            }

            public QuickQSPanel createQuickQSPanel() {
                QuickQSPanel quickQSPanel = new QuickQSPanel(C1737x240b4695.proxyProvideContext(this.viewAttributeProvider), C1736xf2fddc0a.proxyProvideAttributeSet(this.viewAttributeProvider), (DumpManager) DaggerSystemUIGoogleRootComponent.this.dumpManagerProvider.get(), (BroadcastDispatcher) DaggerSystemUIGoogleRootComponent.this.broadcastDispatcherProvider.get(), DaggerSystemUIGoogleRootComponent.this.getQSLogger(), (NotificationMediaManager) DaggerSystemUIGoogleRootComponent.this.provideNotificationMediaManagerProvider.get(), DaggerSystemUIGoogleRootComponent.this.getMainExecutor(), (Executor) DaggerSystemUIGoogleRootComponent.this.provideBackgroundExecutorProvider.get(), (LocalBluetoothManager) DaggerSystemUIGoogleRootComponent.this.provideLocalBluetoothControllerProvider.get());
                return quickQSPanel;
            }

            public QSCustomizer createQSCustomizer() {
                QSCustomizer qSCustomizer = new QSCustomizer(SystemUIFactory_ContextHolder_ProvideContextFactory.proxyProvideContext(DaggerSystemUIGoogleRootComponent.this.contextHolder), C1736xf2fddc0a.proxyProvideAttributeSet(this.viewAttributeProvider), (LightBarController) DaggerSystemUIGoogleRootComponent.this.lightBarControllerProvider.get(), (KeyguardStateController) DaggerSystemUIGoogleRootComponent.this.keyguardStateControllerImplProvider.get(), (ScreenLifecycle) DaggerSystemUIGoogleRootComponent.this.screenLifecycleProvider.get(), getTileQueryHelper());
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

    private DaggerSystemUIGoogleRootComponent(Builder builder) {
        initialize(builder);
        initialize2(builder);
        initialize3(builder);
        initialize4(builder);
        initialize5(builder);
    }

    public static Builder builder() {
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
        return SystemServicesModule_ProvideResourcesFactory.proxyProvideResources(SystemUIFactory_ContextHolder_ProvideContextFactory.proxyProvideContext(this.contextHolder));
    }

    /* access modifiers changed from: private */
    public NotificationSectionsFeatureManager getNotificationSectionsFeatureManager() {
        return new NotificationSectionsFeatureManager(new DeviceConfigProxy(), SystemUIFactory_ContextHolder_ProvideContextFactory.proxyProvideContext(this.contextHolder));
    }

    /* access modifiers changed from: private */
    public QSLogger getQSLogger() {
        return new QSLogger((LogBuffer) this.provideQuickSettingsLogBufferProvider.get());
    }

    /* access modifiers changed from: private */
    public Executor getMainExecutor() {
        return ConcurrencyModule_ProvideMainExecutorFactory.proxyProvideMainExecutor(SystemUIFactory_ContextHolder_ProvideContextFactory.proxyProvideContext(this.contextHolder));
    }

    private void initialize(Builder builder) {
        SystemUIFactory_ContextHolder_ProvideContextFactory create = SystemUIFactory_ContextHolder_ProvideContextFactory.create(builder.contextHolder);
        this.provideContextProvider = create;
        Provider<DumpManager> provider = DoubleCheck.provider(DumpManager_Factory.create(create));
        this.dumpManagerProvider = provider;
        this.bootCompleteCacheImplProvider = DoubleCheck.provider(BootCompleteCacheImpl_Factory.create(provider));
        this.provideConfigurationControllerProvider = DoubleCheck.provider(DependencyProvider_ProvideConfigurationControllerFactory.create(builder.dependencyProvider, this.provideContextProvider));
        this.provideMainExecutorProvider = ConcurrencyModule_ProvideMainExecutorFactory.create(this.provideContextProvider);
        Provider<Looper> provider2 = DoubleCheck.provider(ConcurrencyModule_ProvideBgLooperFactory.create());
        this.provideBgLooperProvider = provider2;
        this.provideBackgroundExecutorProvider = DoubleCheck.provider(ConcurrencyModule_ProvideBackgroundExecutorFactory.create(provider2));
        Provider<Executor> provider3 = DoubleCheck.provider(ConcurrencyModule_ProvideExecutorFactory.create(this.provideBgLooperProvider));
        this.provideExecutorProvider = provider3;
        this.controlsListingControllerImplProvider = DoubleCheck.provider(ControlsListingControllerImpl_Factory.create(this.provideContextProvider, provider3));
        this.provideBackgroundDelayableExecutorProvider = DoubleCheck.provider(ConcurrencyModule_ProvideBackgroundDelayableExecutorFactory.create(this.provideBgLooperProvider));
        this.controlsControllerImplProvider = new DelegateFactory();
        this.provideMainDelayableExecutorProvider = DoubleCheck.provider(ConcurrencyModule_ProvideMainDelayableExecutorFactory.create(ConcurrencyModule_ProvideMainLooperFactory.create()));
        DependencyProvider_ProvideSharePreferencesFactory create2 = DependencyProvider_ProvideSharePreferencesFactory.create(builder.dependencyProvider, this.provideContextProvider);
        this.provideSharePreferencesProvider = create2;
        this.controlsUiControllerImplProvider = DoubleCheck.provider(ControlsUiControllerImpl_Factory.create(this.controlsControllerImplProvider, this.provideContextProvider, this.provideMainDelayableExecutorProvider, this.provideBackgroundDelayableExecutorProvider, this.controlsListingControllerImplProvider, create2));
        this.controlsBindingControllerImplProvider = DoubleCheck.provider(ControlsBindingControllerImpl_Factory.create(this.provideContextProvider, this.provideBackgroundDelayableExecutorProvider, this.controlsControllerImplProvider));
        ConcurrencyModule_ProvideMainHandlerFactory create3 = ConcurrencyModule_ProvideMainHandlerFactory.create(ConcurrencyModule_ProvideMainLooperFactory.create());
        this.provideMainHandlerProvider = create3;
        this.broadcastDispatcherProvider = DoubleCheck.provider(BroadcastDispatcher_Factory.create(this.provideContextProvider, create3, this.provideBgLooperProvider, this.dumpManagerProvider));
        Provider<Optional<ControlsFavoritePersistenceWrapper>> absentJdkOptionalProvider = absentJdkOptionalProvider();
        this.optionalOfControlsFavoritePersistenceWrapperProvider = absentJdkOptionalProvider;
        DelegateFactory delegateFactory = (DelegateFactory) this.controlsControllerImplProvider;
        Provider<ControlsControllerImpl> provider4 = DoubleCheck.provider(ControlsControllerImpl_Factory.create(this.provideContextProvider, this.provideBackgroundDelayableExecutorProvider, this.controlsUiControllerImplProvider, this.controlsBindingControllerImplProvider, this.controlsListingControllerImplProvider, this.broadcastDispatcherProvider, absentJdkOptionalProvider, this.dumpManagerProvider));
        this.controlsControllerImplProvider = provider4;
        delegateFactory.setDelegatedProvider(provider4);
        this.controlsProviderSelectorActivityProvider = ControlsProviderSelectorActivity_Factory.create(this.provideMainExecutorProvider, this.provideBackgroundExecutorProvider, this.controlsListingControllerImplProvider, this.controlsControllerImplProvider, this.broadcastDispatcherProvider);
        this.controlsFavoritingActivityProvider = ControlsFavoritingActivity_Factory.create(this.provideMainExecutorProvider, this.controlsControllerImplProvider, this.controlsListingControllerImplProvider, this.broadcastDispatcherProvider);
        this.controlsRequestDialogProvider = ControlsRequestDialog_Factory.create(this.controlsControllerImplProvider, this.broadcastDispatcherProvider, this.controlsListingControllerImplProvider);
        this.workLockActivityProvider = WorkLockActivity_Factory.create(this.broadcastDispatcherProvider);
        this.brightnessDialogProvider = BrightnessDialog_Factory.create(this.broadcastDispatcherProvider);
        Provider<RecordingController> provider5 = DoubleCheck.provider(RecordingController_Factory.create(this.provideContextProvider));
        this.recordingControllerProvider = provider5;
        this.screenRecordDialogProvider = ScreenRecordDialog_Factory.create(provider5);
        this.provideWindowManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideWindowManagerFactory.create(this.provideContextProvider));
        this.provideIActivityManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideIActivityManagerFactory.create());
        this.provideResourcesProvider = SystemServicesModule_ProvideResourcesFactory.create(this.provideContextProvider);
        this.provideAmbientDisplayConfigurationProvider = DependencyProvider_ProvideAmbientDisplayConfigurationFactory.create(builder.dependencyProvider, this.provideContextProvider);
        this.provideAlwaysOnDisplayPolicyProvider = DependencyProvider_ProvideAlwaysOnDisplayPolicyFactory.create(builder.dependencyProvider, this.provideContextProvider);
        this.providePowerManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvidePowerManagerFactory.create(this.provideContextProvider));
        Provider<LeakDetector> provider6 = DoubleCheck.provider(DependencyProvider_ProvideLeakDetectorFactory.create(builder.dependencyProvider));
        this.provideLeakDetectorProvider = provider6;
        Provider<TunerServiceImpl> provider7 = DoubleCheck.provider(TunerServiceImpl_Factory.create(this.provideContextProvider, this.provideMainHandlerProvider, provider6, this.broadcastDispatcherProvider));
        this.tunerServiceImplProvider = provider7;
        this.dozeParametersProvider = DoubleCheck.provider(DozeParameters_Factory.create(this.provideResourcesProvider, this.provideAmbientDisplayConfigurationProvider, this.provideAlwaysOnDisplayPolicyProvider, this.providePowerManagerProvider, provider7));
        this.statusBarStateControllerImplProvider = DoubleCheck.provider(StatusBarStateControllerImpl_Factory.create());
        this.provideDevicePolicyManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideDevicePolicyManagerFactory.create(this.provideContextProvider));
        this.provideUserManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideUserManagerFactory.create(this.provideContextProvider));
        this.provideIStatusBarServiceProvider = DoubleCheck.provider(SystemServicesModule_ProvideIStatusBarServiceFactory.create());
        this.provideKeyguardManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideKeyguardManagerFactory.create(this.provideContextProvider));
        this.deviceProvisionedControllerImplProvider = DoubleCheck.provider(DeviceProvisionedControllerImpl_Factory.create(this.provideContextProvider, this.provideMainHandlerProvider, this.broadcastDispatcherProvider));
        this.keyguardUpdateMonitorProvider = DoubleCheck.provider(KeyguardUpdateMonitor_Factory.create(this.provideContextProvider, ConcurrencyModule_ProvideMainLooperFactory.create(), this.broadcastDispatcherProvider, this.dumpManagerProvider, this.provideBackgroundExecutorProvider));
        DependencyProvider_ProvideLockPatternUtilsFactory create4 = DependencyProvider_ProvideLockPatternUtilsFactory.create(builder.dependencyProvider, this.provideContextProvider);
        this.provideLockPatternUtilsProvider = create4;
        this.keyguardStateControllerImplProvider = DoubleCheck.provider(KeyguardStateControllerImpl_Factory.create(this.provideContextProvider, this.keyguardUpdateMonitorProvider, create4));
        this.keyguardBypassControllerProvider = new DelegateFactory();
        Provider<AlarmManager> provider8 = DoubleCheck.provider(SystemServicesModule_ProvideAlarmManagerFactory.create(this.provideContextProvider));
        this.provideAlarmManagerProvider = provider8;
        Provider<SmartSpaceController> provider9 = DoubleCheck.provider(SmartSpaceController_Factory.create(this.provideContextProvider, this.keyguardUpdateMonitorProvider, this.provideMainHandlerProvider, provider8, this.dumpManagerProvider));
        this.smartSpaceControllerProvider = provider9;
        Provider<NotificationLockscreenUserManagerGoogle> provider10 = DoubleCheck.provider(NotificationLockscreenUserManagerGoogle_Factory.create(this.provideContextProvider, this.broadcastDispatcherProvider, this.provideDevicePolicyManagerProvider, this.provideUserManagerProvider, this.provideIStatusBarServiceProvider, this.provideKeyguardManagerProvider, this.statusBarStateControllerImplProvider, this.provideMainHandlerProvider, this.deviceProvisionedControllerImplProvider, this.keyguardStateControllerImplProvider, this.keyguardBypassControllerProvider, provider9));
        this.notificationLockscreenUserManagerGoogleProvider = provider10;
        DelegateFactory delegateFactory2 = (DelegateFactory) this.keyguardBypassControllerProvider;
        Provider<KeyguardBypassController> provider11 = DoubleCheck.provider(KeyguardBypassController_Factory.create(this.provideContextProvider, this.tunerServiceImplProvider, this.statusBarStateControllerImplProvider, provider10, this.keyguardStateControllerImplProvider, this.dumpManagerProvider));
        this.keyguardBypassControllerProvider = provider11;
        delegateFactory2.setDelegatedProvider(provider11);
        Provider<SysuiColorExtractor> provider12 = DoubleCheck.provider(SysuiColorExtractor_Factory.create(this.provideContextProvider, this.provideConfigurationControllerProvider));
        this.sysuiColorExtractorProvider = provider12;
        this.notificationShadeWindowControllerProvider = DoubleCheck.provider(NotificationShadeWindowController_Factory.create(this.provideContextProvider, this.provideWindowManagerProvider, this.provideIActivityManagerProvider, this.dozeParametersProvider, this.statusBarStateControllerImplProvider, this.provideConfigurationControllerProvider, this.keyguardBypassControllerProvider, provider12, this.dumpManagerProvider));
        Provider<ProtoTracer> provider13 = DoubleCheck.provider(ProtoTracer_Factory.create(this.provideContextProvider, this.dumpManagerProvider));
        this.protoTracerProvider = provider13;
        this.provideCommandQueueProvider = DoubleCheck.provider(StatusBarDependenciesModule_ProvideCommandQueueFactory.create(this.provideContextProvider, provider13));
        this.providePluginManagerProvider = DoubleCheck.provider(DependencyProvider_ProvidePluginManagerFactory.create(builder.dependencyProvider, this.provideContextProvider));
        this.provideDisplayMetricsProvider = DoubleCheck.provider(DependencyProvider_ProvideDisplayMetricsFactory.create(builder.dependencyProvider, this.provideContextProvider, this.provideWindowManagerProvider));
        Provider<AsyncSensorManager> provider14 = DoubleCheck.provider(AsyncSensorManager_Factory.create(this.provideContextProvider, this.providePluginManagerProvider));
        this.asyncSensorManagerProvider = provider14;
        this.proximitySensorProvider = ProximitySensor_Factory.create(this.provideResourcesProvider, provider14);
        this.provideContentResolverProvider = DoubleCheck.provider(SystemServicesModule_ProvideContentResolverFactory.create(this.provideContextProvider));
        this.provideIDreamManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideIDreamManagerFactory.create());
        this.notificationFilterProvider = DoubleCheck.provider(NotificationFilter_Factory.create(this.statusBarStateControllerImplProvider));
        this.provideReverseWirelessChargerProvider = DoubleCheck.provider(SystemUIGoogleModule_ProvideReverseWirelessChargerFactory.create(this.provideContextProvider));
        this.enhancedEstimatesGoogleImplProvider = DoubleCheck.provider(EnhancedEstimatesGoogleImpl_Factory.create(this.provideContextProvider));
        this.provideBgHandlerProvider = ConcurrencyModule_ProvideBgHandlerFactory.create(this.provideBgLooperProvider);
        DelegateFactory delegateFactory3 = new DelegateFactory();
        this.batteryControllerImplGoogleProvider = delegateFactory3;
        RtxStatusCallback_Factory create5 = RtxStatusCallback_Factory.create(delegateFactory3);
        this.rtxStatusCallbackProvider = create5;
        DelegateFactory delegateFactory4 = (DelegateFactory) this.batteryControllerImplGoogleProvider;
        Provider<BatteryControllerImplGoogle> provider15 = DoubleCheck.provider(BatteryControllerImplGoogle_Factory.create(this.provideReverseWirelessChargerProvider, this.provideContextProvider, this.enhancedEstimatesGoogleImplProvider, this.providePowerManagerProvider, this.broadcastDispatcherProvider, this.provideMainHandlerProvider, this.provideBgHandlerProvider, create5));
        this.batteryControllerImplGoogleProvider = provider15;
        delegateFactory4.setDelegatedProvider(provider15);
        Provider<NotificationGroupManager> provider16 = DoubleCheck.provider(NotificationGroupManager_Factory.create(this.statusBarStateControllerImplProvider));
        this.notificationGroupManagerProvider = provider16;
        Provider<HeadsUpManagerPhone> provider17 = DoubleCheck.provider(SystemUIGoogleModule_ProvideHeadsUpManagerPhoneFactory.create(this.provideContextProvider, this.statusBarStateControllerImplProvider, this.keyguardBypassControllerProvider, provider16, this.provideConfigurationControllerProvider));
        this.provideHeadsUpManagerPhoneProvider = provider17;
        Provider<NotificationInterruptStateProviderImpl> provider18 = DoubleCheck.provider(NotificationInterruptStateProviderImpl_Factory.create(this.provideContentResolverProvider, this.providePowerManagerProvider, this.provideIDreamManagerProvider, this.provideAmbientDisplayConfigurationProvider, this.notificationFilterProvider, this.batteryControllerImplGoogleProvider, this.statusBarStateControllerImplProvider, provider17, this.provideMainHandlerProvider));
        this.notificationInterruptStateProviderImplProvider = provider18;
        this.provideDockManagerProvider = DoubleCheck.provider(SystemUIGoogleModule_ProvideDockManagerFactory.create(this.provideContextProvider, this.broadcastDispatcherProvider, this.statusBarStateControllerImplProvider, provider18));
        this.provideUiBackgroundExecutorProvider = DoubleCheck.provider(ConcurrencyModule_ProvideUiBackgroundExecutorFactory.create());
        this.falsingManagerProxyProvider = DoubleCheck.provider(FalsingManagerProxy_Factory.create(this.provideContextProvider, this.providePluginManagerProvider, this.provideMainExecutorProvider, this.provideDisplayMetricsProvider, this.proximitySensorProvider, DeviceConfigProxy_Factory.create(), this.provideDockManagerProvider, this.keyguardUpdateMonitorProvider, this.dumpManagerProvider, this.provideUiBackgroundExecutorProvider, this.statusBarStateControllerImplProvider));
        this.statusBarKeyguardViewManagerProvider = new DelegateFactory();
        this.dismissCallbackRegistryProvider = DoubleCheck.provider(DismissCallbackRegistry_Factory.create(this.provideUiBackgroundExecutorProvider));
        this.provideTrustManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideTrustManagerFactory.create(this.provideContextProvider));
        this.navigationModeControllerProvider = DoubleCheck.provider(NavigationModeController_Factory.create(this.provideContextProvider, this.deviceProvisionedControllerImplProvider, this.provideUiBackgroundExecutorProvider));
        this.newKeyguardViewMediatorProvider = DoubleCheck.provider(KeyguardModule_NewKeyguardViewMediatorFactory.create(this.provideContextProvider, this.falsingManagerProxyProvider, this.provideLockPatternUtilsProvider, this.broadcastDispatcherProvider, this.notificationShadeWindowControllerProvider, this.statusBarKeyguardViewManagerProvider, this.dismissCallbackRegistryProvider, this.keyguardUpdateMonitorProvider, this.dumpManagerProvider, this.providePowerManagerProvider, this.provideTrustManagerProvider, this.provideUiBackgroundExecutorProvider, DeviceConfigProxy_Factory.create(), this.navigationModeControllerProvider));
        this.providesViewMediatorCallbackProvider = DependencyProvider_ProvidesViewMediatorCallbackFactory.create(builder.dependencyProvider, this.newKeyguardViewMediatorProvider);
        Provider<LogcatEchoTracker> provider19 = DoubleCheck.provider(LogModule_ProvideLogcatEchoTrackerFactory.create(this.provideContentResolverProvider, ConcurrencyModule_ProvideMainLooperFactory.create()));
        this.provideLogcatEchoTrackerProvider = provider19;
        Provider<LogBuffer> provider20 = DoubleCheck.provider(LogModule_ProvideNotificationsLogBufferFactory.create(provider19, this.dumpManagerProvider));
        this.provideNotificationsLogBufferProvider = provider20;
        this.notificationEntryManagerLoggerProvider = NotificationEntryManagerLogger_Factory.create(provider20);
        this.provideNotificationMediaManagerProvider = new DelegateFactory();
        this.notificationSectionsFeatureManagerProvider = NotificationSectionsFeatureManager_Factory.create(DeviceConfigProxy_Factory.create(), this.provideContextProvider);
        Provider<ExtensionControllerImpl> provider21 = DoubleCheck.provider(ExtensionControllerImpl_Factory.create(this.provideContextProvider, this.provideLeakDetectorProvider, this.providePluginManagerProvider, this.tunerServiceImplProvider, this.provideConfigurationControllerProvider));
        this.extensionControllerImplProvider = provider21;
        Provider<NotificationPersonExtractorPluginBoundary> provider22 = DoubleCheck.provider(NotificationPersonExtractorPluginBoundary_Factory.create(provider21));
        this.notificationPersonExtractorPluginBoundaryProvider = provider22;
        Provider<PeopleNotificationIdentifierImpl> provider23 = DoubleCheck.provider(PeopleNotificationIdentifierImpl_Factory.create(provider22, this.notificationGroupManagerProvider));
        this.peopleNotificationIdentifierImplProvider = provider23;
        Provider<HighPriorityProvider> provider24 = DoubleCheck.provider(HighPriorityProvider_Factory.create(provider23));
        this.highPriorityProvider = provider24;
        this.notificationRankingManagerProvider = NotificationRankingManager_Factory.create(this.provideNotificationMediaManagerProvider, this.notificationGroupManagerProvider, this.provideHeadsUpManagerPhoneProvider, this.notificationFilterProvider, this.notificationEntryManagerLoggerProvider, this.notificationSectionsFeatureManagerProvider, this.peopleNotificationIdentifierImplProvider, provider24);
        this.keyguardEnvironmentImplProvider = DoubleCheck.provider(KeyguardEnvironmentImpl_Factory.create());
        this.featureFlagsProvider = DoubleCheck.provider(FeatureFlags_Factory.create(this.provideBackgroundExecutorProvider));
        this.provideNotificationMessagingUtilProvider = DependencyProvider_ProvideNotificationMessagingUtilFactory.create(builder.dependencyProvider, this.provideContextProvider);
        DelegateFactory delegateFactory5 = new DelegateFactory();
        this.provideNotificationEntryManagerProvider = delegateFactory5;
        this.provideSmartReplyControllerProvider = DoubleCheck.provider(StatusBarDependenciesModule_ProvideSmartReplyControllerFactory.create(delegateFactory5, this.provideIStatusBarServiceProvider));
        this.provideStatusBarProvider = new DelegateFactory();
        this.provideHandlerProvider = DependencyProvider_ProvideHandlerFactory.create(builder.dependencyProvider);
        Provider<RemoteInputUriController> provider25 = DoubleCheck.provider(RemoteInputUriController_Factory.create(this.provideIStatusBarServiceProvider));
        this.remoteInputUriControllerProvider = provider25;
        this.provideNotificationRemoteInputManagerProvider = DoubleCheck.provider(C1172xfa996c5e.create(this.provideContextProvider, this.notificationLockscreenUserManagerGoogleProvider, this.provideSmartReplyControllerProvider, this.provideNotificationEntryManagerProvider, this.provideStatusBarProvider, this.statusBarStateControllerImplProvider, this.provideHandlerProvider, provider25));
        NotifCollectionLogger_Factory create6 = NotifCollectionLogger_Factory.create(this.provideNotificationsLogBufferProvider);
        this.notifCollectionLoggerProvider = create6;
        this.notifCollectionProvider = DoubleCheck.provider(NotifCollection_Factory.create(this.provideIStatusBarServiceProvider, this.dumpManagerProvider, this.featureFlagsProvider, create6));
        this.bindSystemClockProvider = DoubleCheck.provider(SystemClockImpl_Factory.create());
    }

    private void initialize2(Builder builder) {
        ShadeListBuilderLogger_Factory create = ShadeListBuilderLogger_Factory.create(this.provideNotificationsLogBufferProvider);
        this.shadeListBuilderLoggerProvider = create;
        Provider<ShadeListBuilder> provider = DoubleCheck.provider(ShadeListBuilder_Factory.create(this.bindSystemClockProvider, create, this.dumpManagerProvider));
        this.shadeListBuilderProvider = provider;
        Provider<NotifPipeline> provider2 = DoubleCheck.provider(NotifPipeline_Factory.create(this.notifCollectionProvider, provider));
        this.notifPipelineProvider = provider2;
        this.provideCommonNotifCollectionProvider = DoubleCheck.provider(NotificationsModule_ProvideCommonNotifCollectionFactory.create(this.featureFlagsProvider, provider2, this.provideNotificationEntryManagerProvider));
        NotifBindPipelineLogger_Factory create2 = NotifBindPipelineLogger_Factory.create(this.provideNotificationsLogBufferProvider);
        this.notifBindPipelineLoggerProvider = create2;
        this.notifBindPipelineProvider = DoubleCheck.provider(NotifBindPipeline_Factory.create(this.provideCommonNotifCollectionProvider, create2));
        NotifRemoteViewCacheImpl_Factory create3 = NotifRemoteViewCacheImpl_Factory.create(this.provideCommonNotifCollectionProvider);
        this.notifRemoteViewCacheImplProvider = create3;
        this.provideNotifRemoteViewCacheProvider = DoubleCheck.provider(create3);
        this.smartReplyConstantsProvider = DoubleCheck.provider(SmartReplyConstants_Factory.create(this.provideMainHandlerProvider, this.provideContextProvider, DeviceConfigProxy_Factory.create()));
        Provider<LauncherApps> provider3 = DoubleCheck.provider(SystemServicesModule_ProvideLauncherAppsFactory.create(this.provideContextProvider));
        this.provideLauncherAppsProvider = provider3;
        ConversationNotificationProcessor_Factory create4 = ConversationNotificationProcessor_Factory.create(provider3);
        this.conversationNotificationProcessorProvider = create4;
        this.notificationContentInflaterProvider = DoubleCheck.provider(NotificationContentInflater_Factory.create(this.provideNotifRemoteViewCacheProvider, this.provideNotificationRemoteInputManagerProvider, this.smartReplyConstantsProvider, this.provideSmartReplyControllerProvider, create4, this.provideBackgroundExecutorProvider));
        this.notifInflationErrorManagerProvider = DoubleCheck.provider(NotifInflationErrorManager_Factory.create());
        RowContentBindStageLogger_Factory create5 = RowContentBindStageLogger_Factory.create(this.provideNotificationsLogBufferProvider);
        this.rowContentBindStageLoggerProvider = create5;
        this.rowContentBindStageProvider = DoubleCheck.provider(RowContentBindStage_Factory.create(this.notificationContentInflaterProvider, this.notifInflationErrorManagerProvider, create5));
        this.expandableNotificationRowComponentBuilderProvider = new Provider<com.android.systemui.statusbar.notification.row.dagger.ExpandableNotificationRowComponent.Builder>() {
            public com.android.systemui.statusbar.notification.row.dagger.ExpandableNotificationRowComponent.Builder get() {
                return new ExpandableNotificationRowComponentBuilder();
            }
        };
        IconBuilder_Factory create6 = IconBuilder_Factory.create(this.provideContextProvider);
        this.iconBuilderProvider = create6;
        this.iconManagerProvider = IconManager_Factory.create(this.provideCommonNotifCollectionProvider, this.provideLauncherAppsProvider, create6);
        this.notificationRowBinderImplProvider = DoubleCheck.provider(NotificationRowBinderImpl_Factory.create(this.provideContextProvider, this.provideNotificationMessagingUtilProvider, this.provideNotificationRemoteInputManagerProvider, this.notificationLockscreenUserManagerGoogleProvider, this.notifBindPipelineProvider, this.rowContentBindStageProvider, this.notificationInterruptStateProviderImplProvider, RowInflaterTask_Factory.create(), this.expandableNotificationRowComponentBuilderProvider, this.iconManagerProvider));
        Provider<ForegroundServiceDismissalFeatureController> provider4 = DoubleCheck.provider(ForegroundServiceDismissalFeatureController_Factory.create(DeviceConfigProxy_Factory.create(), this.provideContextProvider));
        this.foregroundServiceDismissalFeatureControllerProvider = provider4;
        DelegateFactory delegateFactory = (DelegateFactory) this.provideNotificationEntryManagerProvider;
        Provider<NotificationEntryManager> provider5 = DoubleCheck.provider(NotificationsModule_ProvideNotificationEntryManagerFactory.create(this.notificationEntryManagerLoggerProvider, this.notificationGroupManagerProvider, this.notificationRankingManagerProvider, this.keyguardEnvironmentImplProvider, this.featureFlagsProvider, this.notificationRowBinderImplProvider, this.provideNotificationRemoteInputManagerProvider, this.provideLeakDetectorProvider, provider4));
        this.provideNotificationEntryManagerProvider = provider5;
        delegateFactory.setDelegatedProvider(provider5);
        this.wallpaperNotifierProvider = WallpaperNotifier_Factory.create(this.provideContextProvider, this.provideNotificationEntryManagerProvider, this.broadcastDispatcherProvider);
        Provider<NotificationManager> provider6 = DoubleCheck.provider(SystemServicesModule_ProvideNotificationManagerFactory.create(this.provideContextProvider));
        this.provideNotificationManagerProvider = provider6;
        this.provideNotificationListenerProvider = DoubleCheck.provider(StatusBarDependenciesModule_ProvideNotificationListenerFactory.create(this.provideContextProvider, provider6, this.provideMainHandlerProvider));
        GroupCoalescerLogger_Factory create7 = GroupCoalescerLogger_Factory.create(this.provideNotificationsLogBufferProvider);
        this.groupCoalescerLoggerProvider = create7;
        this.groupCoalescerProvider = GroupCoalescer_Factory.create(this.provideMainDelayableExecutorProvider, this.bindSystemClockProvider, create7);
        this.headsUpCoordinatorProvider = DoubleCheck.provider(HeadsUpCoordinator_Factory.create(this.provideHeadsUpManagerPhoneProvider, this.provideNotificationRemoteInputManagerProvider));
        this.keyguardCoordinatorProvider = DoubleCheck.provider(KeyguardCoordinator_Factory.create(this.provideContextProvider, this.provideHandlerProvider, this.keyguardStateControllerImplProvider, this.notificationLockscreenUserManagerGoogleProvider, this.broadcastDispatcherProvider, this.statusBarStateControllerImplProvider, this.keyguardUpdateMonitorProvider, this.highPriorityProvider));
        this.rankingCoordinatorProvider = DoubleCheck.provider(RankingCoordinator_Factory.create(this.statusBarStateControllerImplProvider));
        Provider<AppOpsControllerImpl> provider7 = DoubleCheck.provider(AppOpsControllerImpl_Factory.create(this.provideContextProvider, this.provideBgLooperProvider, this.dumpManagerProvider));
        this.appOpsControllerImplProvider = provider7;
        Provider<ForegroundServiceController> provider8 = DoubleCheck.provider(ForegroundServiceController_Factory.create(this.provideNotificationEntryManagerProvider, provider7, this.provideMainHandlerProvider));
        this.foregroundServiceControllerProvider = provider8;
        this.foregroundCoordinatorProvider = DoubleCheck.provider(ForegroundCoordinator_Factory.create(provider8, this.appOpsControllerImplProvider, this.provideMainDelayableExecutorProvider));
        Provider<IPackageManager> provider9 = DoubleCheck.provider(SystemServicesModule_ProvideIPackageManagerFactory.create());
        this.provideIPackageManagerProvider = provider9;
        this.deviceProvisionedCoordinatorProvider = DoubleCheck.provider(DeviceProvisionedCoordinator_Factory.create(this.deviceProvisionedControllerImplProvider, provider9));
        DelegateFactory delegateFactory2 = new DelegateFactory();
        this.newBubbleControllerProvider = delegateFactory2;
        this.bubbleCoordinatorProvider = DoubleCheck.provider(BubbleCoordinator_Factory.create(delegateFactory2, this.notifCollectionProvider));
        this.preparationCoordinatorLoggerProvider = PreparationCoordinatorLogger_Factory.create(this.provideNotificationsLogBufferProvider);
        this.notifInflaterImplProvider = DoubleCheck.provider(NotifInflaterImpl_Factory.create(this.provideIStatusBarServiceProvider, this.notifCollectionProvider, this.notifInflationErrorManagerProvider, this.notifPipelineProvider));
        Provider<NotifViewBarn> provider10 = DoubleCheck.provider(NotifViewBarn_Factory.create());
        this.notifViewBarnProvider = provider10;
        Provider<PreparationCoordinator> provider11 = DoubleCheck.provider(PreparationCoordinator_Factory.create(this.preparationCoordinatorLoggerProvider, this.notifInflaterImplProvider, this.notifInflationErrorManagerProvider, provider10, this.provideIStatusBarServiceProvider, this.notificationInterruptStateProviderImplProvider, this.provideHeadsUpManagerPhoneProvider));
        this.preparationCoordinatorProvider = provider11;
        this.notifCoordinatorsProvider = DoubleCheck.provider(NotifCoordinators_Factory.create(this.dumpManagerProvider, this.featureFlagsProvider, this.headsUpCoordinatorProvider, this.keyguardCoordinatorProvider, this.rankingCoordinatorProvider, this.foregroundCoordinatorProvider, this.deviceProvisionedCoordinatorProvider, this.bubbleCoordinatorProvider, provider11));
        Provider<VisualStabilityManager> provider12 = DoubleCheck.provider(NotificationsModule_ProvideVisualStabilityManagerFactory.create(this.provideNotificationEntryManagerProvider, this.provideHandlerProvider));
        this.provideVisualStabilityManagerProvider = provider12;
        Provider<NotifViewManager> provider13 = DoubleCheck.provider(NotifViewManager_Factory.create(this.notifViewBarnProvider, provider12, this.featureFlagsProvider));
        this.notifViewManagerProvider = provider13;
        this.notifPipelineInitializerProvider = DoubleCheck.provider(NotifPipelineInitializer_Factory.create(this.notifPipelineProvider, this.groupCoalescerProvider, this.notifCollectionProvider, this.shadeListBuilderProvider, this.notifCoordinatorsProvider, this.notifInflaterImplProvider, this.dumpManagerProvider, this.featureFlagsProvider, provider13));
        this.notifBindPipelineInitializerProvider = NotifBindPipelineInitializer_Factory.create(this.notifBindPipelineProvider, this.rowContentBindStageProvider);
        Provider<NotificationGroupAlertTransferHelper> provider14 = DoubleCheck.provider(C1628x3053f5c5.create(this.rowContentBindStageProvider));
        this.provideNotificationGroupAlertTransferHelperProvider = provider14;
        this.notificationsControllerImplProvider = DoubleCheck.provider(NotificationsControllerImpl_Factory.create(this.featureFlagsProvider, this.provideNotificationListenerProvider, this.provideNotificationEntryManagerProvider, this.notifPipelineInitializerProvider, this.notifBindPipelineInitializerProvider, this.deviceProvisionedControllerImplProvider, this.notificationRowBinderImplProvider, this.remoteInputUriControllerProvider, this.newBubbleControllerProvider, this.notificationGroupManagerProvider, provider14, this.provideHeadsUpManagerPhoneProvider));
        NotificationsControllerStub_Factory create8 = NotificationsControllerStub_Factory.create(this.provideNotificationListenerProvider);
        this.notificationsControllerStubProvider = create8;
        this.provideNotificationsControllerProvider = DoubleCheck.provider(NotificationsModule_ProvideNotificationsControllerFactory.create(this.provideContextProvider, this.notificationsControllerImplProvider, create8));
        Provider<DarkIconDispatcherImpl> provider15 = DoubleCheck.provider(DarkIconDispatcherImpl_Factory.create(this.provideContextProvider, this.provideCommandQueueProvider));
        this.darkIconDispatcherImplProvider = provider15;
        this.lightBarControllerProvider = DoubleCheck.provider(LightBarController_Factory.create(this.provideContextProvider, provider15, this.batteryControllerImplGoogleProvider));
        this.provideIWindowManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideIWindowManagerFactory.create());
        this.provideAutoHideControllerProvider = DoubleCheck.provider(DependencyProvider_ProvideAutoHideControllerFactory.create(builder.dependencyProvider, this.provideContextProvider, this.provideMainHandlerProvider, this.provideIWindowManagerProvider));
        this.statusBarIconControllerImplProvider = DoubleCheck.provider(StatusBarIconControllerImpl_Factory.create(this.provideContextProvider, this.provideCommandQueueProvider));
        this.notificationWakeUpCoordinatorProvider = DoubleCheck.provider(NotificationWakeUpCoordinator_Factory.create(this.provideHeadsUpManagerPhoneProvider, this.statusBarStateControllerImplProvider, this.keyguardBypassControllerProvider, this.dozeParametersProvider));
        Provider<NotificationRoundnessManager> provider16 = DoubleCheck.provider(NotificationRoundnessManager_Factory.create(this.keyguardBypassControllerProvider, this.notificationSectionsFeatureManagerProvider));
        this.notificationRoundnessManagerProvider = provider16;
        this.pulseExpansionHandlerProvider = DoubleCheck.provider(PulseExpansionHandler_Factory.create(this.provideContextProvider, this.notificationWakeUpCoordinatorProvider, this.keyguardBypassControllerProvider, this.provideHeadsUpManagerPhoneProvider, provider16, this.statusBarStateControllerImplProvider, this.falsingManagerProxyProvider));
        this.dynamicPrivacyControllerProvider = DoubleCheck.provider(DynamicPrivacyController_Factory.create(this.notificationLockscreenUserManagerGoogleProvider, this.keyguardStateControllerImplProvider, this.statusBarStateControllerImplProvider));
        this.bypassHeadsUpNotifierProvider = DoubleCheck.provider(BypassHeadsUpNotifier_Factory.create(this.provideContextProvider, this.keyguardBypassControllerProvider, this.statusBarStateControllerImplProvider, this.provideHeadsUpManagerPhoneProvider, this.notificationLockscreenUserManagerGoogleProvider, this.provideNotificationMediaManagerProvider, this.provideNotificationEntryManagerProvider, this.tunerServiceImplProvider));
        this.remoteInputQuickSettingsDisablerProvider = DoubleCheck.provider(RemoteInputQuickSettingsDisabler_Factory.create(this.provideContextProvider, this.provideConfigurationControllerProvider, this.provideCommandQueueProvider));
        this.provideAccessibilityManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideAccessibilityManagerFactory.create(this.provideContextProvider));
        this.provideINotificationManagerProvider = DoubleCheck.provider(DependencyProvider_ProvideINotificationManagerFactory.create(builder.dependencyProvider));
        Provider<ShortcutManager> provider17 = DoubleCheck.provider(SystemServicesModule_ProvideShortcutManagerFactory.create(this.provideContextProvider));
        this.provideShortcutManagerProvider = provider17;
        this.provideNotificationGutsManagerProvider = DoubleCheck.provider(NotificationsModule_ProvideNotificationGutsManagerFactory.create(this.provideContextProvider, this.provideVisualStabilityManagerProvider, this.provideStatusBarProvider, this.provideMainHandlerProvider, this.provideAccessibilityManagerProvider, this.highPriorityProvider, this.provideINotificationManagerProvider, this.provideLauncherAppsProvider, provider17));
        this.expansionStateLoggerProvider = NotificationLogger_ExpansionStateLogger_Factory.create(this.provideUiBackgroundExecutorProvider);
        Provider<NotificationPanelLogger> provider18 = DoubleCheck.provider(NotificationsModule_ProvideNotificationPanelLoggerFactory.create());
        this.provideNotificationPanelLoggerProvider = provider18;
        this.provideNotificationLoggerProvider = DoubleCheck.provider(NotificationsModule_ProvideNotificationLoggerFactory.create(this.provideNotificationListenerProvider, this.provideUiBackgroundExecutorProvider, this.provideNotificationEntryManagerProvider, this.statusBarStateControllerImplProvider, this.expansionStateLoggerProvider, provider18));
        this.foregroundServiceSectionControllerProvider = DoubleCheck.provider(ForegroundServiceSectionController_Factory.create(this.provideNotificationEntryManagerProvider, this.foregroundServiceDismissalFeatureControllerProvider));
        DynamicChildBindController_Factory create9 = DynamicChildBindController_Factory.create(this.rowContentBindStageProvider);
        this.dynamicChildBindControllerProvider = create9;
        this.provideNotificationViewHierarchyManagerProvider = DoubleCheck.provider(C1173x3f8faa0a.create(this.provideContextProvider, this.provideMainHandlerProvider, this.notificationLockscreenUserManagerGoogleProvider, this.notificationGroupManagerProvider, this.provideVisualStabilityManagerProvider, this.statusBarStateControllerImplProvider, this.provideNotificationEntryManagerProvider, this.keyguardBypassControllerProvider, this.newBubbleControllerProvider, this.dynamicPrivacyControllerProvider, this.foregroundServiceSectionControllerProvider, create9));
        this.provideNotificationAlertingManagerProvider = DoubleCheck.provider(NotificationsModule_ProvideNotificationAlertingManagerFactory.create(this.provideNotificationEntryManagerProvider, this.provideNotificationRemoteInputManagerProvider, this.provideVisualStabilityManagerProvider, this.statusBarStateControllerImplProvider, this.notificationInterruptStateProviderImplProvider, this.provideNotificationListenerProvider, this.provideHeadsUpManagerPhoneProvider));
        this.provideMetricsLoggerProvider = DoubleCheck.provider(DependencyProvider_ProvideMetricsLoggerFactory.create(builder.dependencyProvider));
        Provider<Optional<Lazy<StatusBar>>> access$400 = PresentJdkOptionalLazyProvider.m122of(this.provideStatusBarProvider);
        this.optionalOfLazyOfStatusBarProvider = access$400;
        Provider<ActivityStarterDelegate> provider19 = DoubleCheck.provider(ActivityStarterDelegate_Factory.create(access$400));
        this.activityStarterDelegateProvider = provider19;
        this.userSwitcherControllerProvider = DoubleCheck.provider(UserSwitcherController_Factory.create(this.provideContextProvider, this.keyguardStateControllerImplProvider, this.provideMainHandlerProvider, provider19, this.broadcastDispatcherProvider));
        this.networkControllerImplProvider = DoubleCheck.provider(NetworkControllerImpl_Factory.create(this.provideContextProvider, this.provideBgLooperProvider, this.deviceProvisionedControllerImplProvider, this.broadcastDispatcherProvider));
        this.screenLifecycleProvider = DoubleCheck.provider(ScreenLifecycle_Factory.create());
        this.wakefulnessLifecycleProvider = DoubleCheck.provider(WakefulnessLifecycle_Factory.create());
        this.vibratorHelperProvider = DoubleCheck.provider(VibratorHelper_Factory.create(this.provideContextProvider));
        this.provideNavigationBarControllerProvider = DoubleCheck.provider(DependencyProvider_ProvideNavigationBarControllerFactory.create(builder.dependencyProvider, this.provideContextProvider, this.provideMainHandlerProvider, this.provideCommandQueueProvider));
        this.provideAssistUtilsProvider = DoubleCheck.provider(AssistModule_ProvideAssistUtilsFactory.create(this.provideContextProvider));
        this.provideBackgroundHandlerProvider = DoubleCheck.provider(AssistModule_ProvideBackgroundHandlerFactory.create());
        this.provideAssistHandleViewControllerProvider = AssistModule_ProvideAssistHandleViewControllerFactory.create(this.provideNavigationBarControllerProvider);
        this.deviceConfigHelperProvider = DoubleCheck.provider(DeviceConfigHelper_Factory.create());
        this.assistHandleOffBehaviorProvider = DoubleCheck.provider(AssistHandleOffBehavior_Factory.create());
        Provider<SysUiState> provider20 = DoubleCheck.provider(SystemUIModule_ProvideSysUiStateFactory.create());
        this.provideSysUiStateProvider = provider20;
        this.assistHandleLikeHomeBehaviorProvider = DoubleCheck.provider(AssistHandleLikeHomeBehavior_Factory.create(this.statusBarStateControllerImplProvider, this.wakefulnessLifecycleProvider, provider20));
        this.provideSystemClockProvider = DoubleCheck.provider(AssistModule_ProvideSystemClockFactory.create());
        this.provideActivityManagerWrapperProvider = DoubleCheck.provider(DependencyProvider_ProvideActivityManagerWrapperFactory.create(builder.dependencyProvider));
        this.displayControllerProvider = DoubleCheck.provider(DisplayController_Factory.create(this.provideContextProvider, this.provideMainHandlerProvider, this.provideIWindowManagerProvider));
        this.floatingContentCoordinatorProvider = DoubleCheck.provider(FloatingContentCoordinator_Factory.create());
        PipSnapAlgorithm_Factory create10 = PipSnapAlgorithm_Factory.create(this.provideContextProvider);
        this.pipSnapAlgorithmProvider = create10;
        this.pipBoundsHandlerProvider = PipBoundsHandler_Factory.create(this.provideContextProvider, create10);
        this.pipSurfaceTransactionHelperProvider = DoubleCheck.provider(PipSurfaceTransactionHelper_Factory.create(this.provideContextProvider));
        Provider<PipManager> provider21 = DoubleCheck.provider(PipManager_Factory.create(this.provideContextProvider, this.broadcastDispatcherProvider, this.displayControllerProvider, this.floatingContentCoordinatorProvider, DeviceConfigProxy_Factory.create(), this.pipBoundsHandlerProvider, this.pipSnapAlgorithmProvider, this.pipSurfaceTransactionHelperProvider));
        this.pipManagerProvider = provider21;
        this.pipUIProvider = DoubleCheck.provider(PipUI_Factory.create(this.provideContextProvider, this.provideCommandQueueProvider, provider21));
        DelegateFactory delegateFactory3 = new DelegateFactory();
        this.contextComponentResolverProvider = delegateFactory3;
        RecentsModule_ProvideRecentsImplFactory create11 = RecentsModule_ProvideRecentsImplFactory.create(this.provideContextProvider, delegateFactory3);
        this.provideRecentsImplProvider = create11;
        Provider<Recents> provider22 = DoubleCheck.provider(SystemUIGoogleModule_ProvideRecentsFactory.create(this.provideContextProvider, create11, this.provideCommandQueueProvider));
        this.provideRecentsProvider = provider22;
        this.optionalOfLazyOfRecentsProvider = PresentJdkOptionalLazyProvider.m122of(provider22);
    }

    private void initialize3(Builder builder) {
        this.systemWindowsProvider = DoubleCheck.provider(SystemWindows_Factory.create(this.provideContextProvider, this.displayControllerProvider, this.provideIWindowManagerProvider));
        Provider<TransactionPool> provider = DoubleCheck.provider(TransactionPool_Factory.create());
        this.transactionPoolProvider = provider;
        Provider<DisplayImeController> provider2 = DoubleCheck.provider(DisplayImeController_Factory.create(this.systemWindowsProvider, this.displayControllerProvider, this.provideMainHandlerProvider, provider));
        this.displayImeControllerProvider = provider2;
        Provider<Divider> provider3 = DoubleCheck.provider(DividerModule_ProvideDividerFactory.create(this.provideContextProvider, this.optionalOfLazyOfRecentsProvider, this.displayControllerProvider, this.systemWindowsProvider, provider2, this.provideMainHandlerProvider, this.keyguardStateControllerImplProvider, this.transactionPoolProvider));
        this.provideDividerProvider = provider3;
        Provider<Optional<Divider>> access$500 = PresentJdkOptionalInstanceProvider.m121of(provider3);
        this.optionalOfDividerProvider = access$500;
        this.overviewProxyServiceProvider = DoubleCheck.provider(OverviewProxyService_Factory.create(this.provideContextProvider, this.provideCommandQueueProvider, this.deviceProvisionedControllerImplProvider, this.provideNavigationBarControllerProvider, this.navigationModeControllerProvider, this.notificationShadeWindowControllerProvider, this.provideSysUiStateProvider, this.pipUIProvider, access$500, this.optionalOfLazyOfStatusBarProvider));
        Provider<PackageManagerWrapper> provider4 = DoubleCheck.provider(SystemServicesModule_ProvidePackageManagerWrapperFactory.create());
        this.providePackageManagerWrapperProvider = provider4;
        Provider provider5 = DoubleCheck.provider(AssistHandleReminderExpBehavior_Factory.create(this.provideSystemClockProvider, this.provideBackgroundHandlerProvider, this.deviceConfigHelperProvider, this.statusBarStateControllerImplProvider, this.provideActivityManagerWrapperProvider, this.overviewProxyServiceProvider, this.provideSysUiStateProvider, this.wakefulnessLifecycleProvider, provider4, this.broadcastDispatcherProvider, this.bootCompleteCacheImplProvider));
        this.assistHandleReminderExpBehaviorProvider = provider5;
        Provider provider6 = DoubleCheck.provider(AssistModule_ProvideAssistHandleBehaviorControllerMapFactory.create(this.assistHandleOffBehaviorProvider, this.assistHandleLikeHomeBehaviorProvider, provider5));
        this.provideAssistHandleBehaviorControllerMapProvider = provider6;
        this.assistHandleBehaviorControllerProvider = DoubleCheck.provider(AssistHandleBehaviorController_Factory.create(this.provideContextProvider, this.provideAssistUtilsProvider, this.provideBackgroundHandlerProvider, this.provideAssistHandleViewControllerProvider, this.deviceConfigHelperProvider, provider6, this.navigationModeControllerProvider, this.dumpManagerProvider));
        DelegateFactory delegateFactory = new DelegateFactory();
        this.assistManagerGoogleProvider = delegateFactory;
        this.timeoutManagerProvider = DoubleCheck.provider(TimeoutManager_Factory.create(delegateFactory));
        this.assistantPresenceHandlerProvider = DoubleCheck.provider(AssistantPresenceHandler_Factory.create(this.provideAssistUtilsProvider));
        this.touchInsideHandlerProvider = DoubleCheck.provider(TouchInsideHandler_Factory.create(this.assistManagerGoogleProvider, this.navigationModeControllerProvider));
        this.colorChangeHandlerProvider = DoubleCheck.provider(ColorChangeHandler_Factory.create(this.provideContextProvider));
        Provider provider7 = DoubleCheck.provider(TouchOutsideHandler_Factory.create());
        this.touchOutsideHandlerProvider = provider7;
        Provider provider8 = DoubleCheck.provider(OverlayUiHost_Factory.create(this.provideContextProvider, provider7));
        this.overlayUiHostProvider = provider8;
        Provider<ViewGroup> provider9 = DoubleCheck.provider(AssistantUIHintsModule_ProvideParentViewGroupFactory.create(provider8));
        this.provideParentViewGroupProvider = provider9;
        this.edgeLightsControllerProvider = DoubleCheck.provider(EdgeLightsController_Factory.create(this.provideContextProvider, provider9));
        this.glowControllerProvider = DoubleCheck.provider(GlowController_Factory.create(this.provideContextProvider, this.provideParentViewGroupProvider, this.touchInsideHandlerProvider));
        this.overlappedElementControllerProvider = DoubleCheck.provider(OverlappedElementController_Factory.create(this.provideStatusBarProvider));
        Provider provider10 = DoubleCheck.provider(LightnessProvider_Factory.create());
        this.lightnessProvider = provider10;
        this.scrimControllerProvider = DoubleCheck.provider(ScrimController_Factory.create(this.provideParentViewGroupProvider, this.overlappedElementControllerProvider, provider10, this.touchInsideHandlerProvider));
        Provider provider11 = DoubleCheck.provider(FlingVelocityWrapper_Factory.create());
        this.flingVelocityWrapperProvider = provider11;
        this.transcriptionControllerProvider = DoubleCheck.provider(TranscriptionController_Factory.create(this.provideParentViewGroupProvider, this.touchInsideHandlerProvider, provider11, this.provideConfigurationControllerProvider));
        Provider<LayoutInflater> provider12 = DoubleCheck.provider(DependencyProvider_ProviderLayoutInflaterFactory.create(builder.dependencyProvider, this.provideContextProvider));
        this.providerLayoutInflaterProvider = provider12;
        this.iconControllerProvider = DoubleCheck.provider(IconController_Factory.create(provider12, this.provideParentViewGroupProvider, this.provideConfigurationControllerProvider));
        Provider<AssistantWarmer> provider13 = DoubleCheck.provider(AssistantWarmer_Factory.create(this.provideContextProvider));
        Provider<AssistantWarmer> provider14 = provider13;
        this.assistantWarmerProvider = provider13;
        this.ngaUiControllerProvider = DoubleCheck.provider(NgaUiController_Factory.create(this.provideContextProvider, this.timeoutManagerProvider, this.assistantPresenceHandlerProvider, this.touchInsideHandlerProvider, this.colorChangeHandlerProvider, this.overlayUiHostProvider, this.edgeLightsControllerProvider, this.glowControllerProvider, this.scrimControllerProvider, this.transcriptionControllerProvider, this.iconControllerProvider, this.lightnessProvider, this.statusBarStateControllerImplProvider, this.assistManagerGoogleProvider, this.provideNavigationBarControllerProvider, this.flingVelocityWrapperProvider, provider14));
        this.phoneStateMonitorProvider = DoubleCheck.provider(PhoneStateMonitor_Factory.create(this.provideContextProvider, this.broadcastDispatcherProvider, this.optionalOfLazyOfStatusBarProvider, this.bootCompleteCacheImplProvider));
        this.opaEnabledDispatcherProvider = OpaEnabledDispatcher_Factory.create(this.provideStatusBarProvider);
        dagger.internal.SetFactory.Builder builder2 = SetFactory.builder(1, 0);
        builder2.addProvider(this.timeoutManagerProvider);
        this.setOfKeepAliveListenerProvider = builder2.build();
        this.provideAudioInfoListenersProvider = AssistantUIHintsModule_ProvideAudioInfoListenersFactory.create(this.edgeLightsControllerProvider, this.glowControllerProvider);
        dagger.internal.SetFactory.Builder builder3 = SetFactory.builder(0, 1);
        builder3.addCollectionProvider(this.provideAudioInfoListenersProvider);
        this.setOfAudioInfoListenerProvider = builder3.build();
        this.provideCardInfoListenersProvider = AssistantUIHintsModule_ProvideCardInfoListenersFactory.create(this.glowControllerProvider, this.scrimControllerProvider, this.transcriptionControllerProvider, this.lightnessProvider);
        dagger.internal.SetFactory.Builder builder4 = SetFactory.builder(0, 1);
        builder4.addCollectionProvider(this.provideCardInfoListenersProvider);
        this.setOfCardInfoListenerProvider = builder4.build();
        this.taskStackNotifierProvider = DoubleCheck.provider(TaskStackNotifier_Factory.create());
        Provider<Optional<CommandQueue>> access$5002 = PresentJdkOptionalInstanceProvider.m121of(this.provideCommandQueueProvider);
        this.optionalOfCommandQueueProvider = access$5002;
        this.keyboardMonitorProvider = DoubleCheck.provider(KeyboardMonitor_Factory.create(this.provideContextProvider, access$5002));
        Provider<ConfigurationHandler> provider15 = DoubleCheck.provider(ConfigurationHandler_Factory.create(this.provideContextProvider));
        this.configurationHandlerProvider = provider15;
        this.provideConfigInfoListenersProvider = AssistantUIHintsModule_ProvideConfigInfoListenersFactory.create(this.assistantPresenceHandlerProvider, this.touchInsideHandlerProvider, this.touchOutsideHandlerProvider, this.taskStackNotifierProvider, this.keyboardMonitorProvider, this.colorChangeHandlerProvider, provider15);
        dagger.internal.SetFactory.Builder builder5 = SetFactory.builder(0, 1);
        builder5.addCollectionProvider(this.provideConfigInfoListenersProvider);
        this.setOfConfigInfoListenerProvider = builder5.build();
        this.provideTouchActionRegionsProvider = InputModule_ProvideTouchActionRegionsFactory.create(this.iconControllerProvider, this.transcriptionControllerProvider);
        dagger.internal.SetFactory.Builder builder6 = SetFactory.builder(0, 1);
        builder6.addCollectionProvider(this.provideTouchActionRegionsProvider);
        this.setOfTouchActionRegionProvider = builder6.build();
        this.provideTouchInsideRegionsProvider = InputModule_ProvideTouchInsideRegionsFactory.create(this.glowControllerProvider, this.scrimControllerProvider, this.transcriptionControllerProvider);
        dagger.internal.SetFactory.Builder builder7 = SetFactory.builder(0, 1);
        builder7.addCollectionProvider(this.provideTouchInsideRegionsProvider);
        SetFactory build = builder7.build();
        this.setOfTouchInsideRegionProvider = build;
        Provider<NgaInputHandler> provider16 = DoubleCheck.provider(NgaInputHandler_Factory.create(this.touchInsideHandlerProvider, this.setOfTouchActionRegionProvider, build));
        this.ngaInputHandlerProvider = provider16;
        this.bindEdgeLightsInfoListenersProvider = AssistantUIHintsModule_BindEdgeLightsInfoListenersFactory.create(this.edgeLightsControllerProvider, provider16);
        dagger.internal.SetFactory.Builder builder8 = SetFactory.builder(0, 1);
        builder8.addCollectionProvider(this.bindEdgeLightsInfoListenersProvider);
        this.setOfEdgeLightsInfoListenerProvider = builder8.build();
        dagger.internal.SetFactory.Builder builder9 = SetFactory.builder(1, 0);
        builder9.addProvider(this.transcriptionControllerProvider);
        this.setOfTranscriptionInfoListenerProvider = builder9.build();
        dagger.internal.SetFactory.Builder builder10 = SetFactory.builder(1, 0);
        builder10.addProvider(this.transcriptionControllerProvider);
        this.setOfGreetingInfoListenerProvider = builder10.build();
        dagger.internal.SetFactory.Builder builder11 = SetFactory.builder(1, 0);
        builder11.addProvider(this.transcriptionControllerProvider);
        this.setOfChipsInfoListenerProvider = builder11.build();
        dagger.internal.SetFactory.Builder builder12 = SetFactory.builder(1, 0);
        builder12.addProvider(this.transcriptionControllerProvider);
        this.setOfClearListenerProvider = builder12.build();
        this.provideActivityStarterProvider = AssistantUIHintsModule_ProvideActivityStarterFactory.create(this.provideStatusBarProvider);
        dagger.internal.SetFactory.Builder builder13 = SetFactory.builder(1, 0);
        builder13.addProvider(this.provideActivityStarterProvider);
        this.setOfStartActivityInfoListenerProvider = builder13.build();
        dagger.internal.SetFactory.Builder builder14 = SetFactory.builder(1, 0);
        builder14.addProvider(this.iconControllerProvider);
        this.setOfKeyboardInfoListenerProvider = builder14.build();
        dagger.internal.SetFactory.Builder builder15 = SetFactory.builder(1, 0);
        builder15.addProvider(this.iconControllerProvider);
        this.setOfZerostateInfoListenerProvider = builder15.build();
        this.goBackHandlerProvider = DoubleCheck.provider(GoBackHandler_Factory.create());
        dagger.internal.SetFactory.Builder builder16 = SetFactory.builder(1, 0);
        builder16.addProvider(this.goBackHandlerProvider);
        this.setOfGoBackListenerProvider = builder16.build();
        this.takeScreenshotHandlerProvider = DoubleCheck.provider(TakeScreenshotHandler_Factory.create(this.provideContextProvider));
        dagger.internal.SetFactory.Builder builder17 = SetFactory.builder(1, 0);
        builder17.addProvider(this.takeScreenshotHandlerProvider);
        this.setOfTakeScreenshotListenerProvider = builder17.build();
        dagger.internal.SetFactory.Builder builder18 = SetFactory.builder(1, 0);
        builder18.addProvider(this.assistantWarmerProvider);
        SetFactory build2 = builder18.build();
        SetFactory setFactory = build2;
        this.setOfWarmingListenerProvider = build2;
        Provider<NgaMessageHandler> provider17 = DoubleCheck.provider(NgaMessageHandler_Factory.create(this.ngaUiControllerProvider, this.assistantPresenceHandlerProvider, this.setOfKeepAliveListenerProvider, this.setOfAudioInfoListenerProvider, this.setOfCardInfoListenerProvider, this.setOfConfigInfoListenerProvider, this.setOfEdgeLightsInfoListenerProvider, this.setOfTranscriptionInfoListenerProvider, this.setOfGreetingInfoListenerProvider, this.setOfChipsInfoListenerProvider, this.setOfClearListenerProvider, this.setOfStartActivityInfoListenerProvider, this.setOfKeyboardInfoListenerProvider, this.setOfZerostateInfoListenerProvider, this.setOfGoBackListenerProvider, this.setOfTakeScreenshotListenerProvider, setFactory, this.provideMainHandlerProvider));
        Provider<NgaMessageHandler> provider18 = provider17;
        this.ngaMessageHandlerProvider = provider17;
        DelegateFactory delegateFactory2 = (DelegateFactory) this.assistManagerGoogleProvider;
        Provider<AssistManagerGoogle> provider19 = DoubleCheck.provider(AssistManagerGoogle_Factory.create(this.deviceProvisionedControllerImplProvider, this.provideContextProvider, this.provideAssistUtilsProvider, this.assistHandleBehaviorControllerProvider, this.ngaUiControllerProvider, this.provideCommandQueueProvider, this.broadcastDispatcherProvider, this.phoneStateMonitorProvider, this.overviewProxyServiceProvider, this.opaEnabledDispatcherProvider, this.keyguardUpdateMonitorProvider, this.navigationModeControllerProvider, this.provideConfigurationControllerProvider, this.assistantPresenceHandlerProvider, provider18, this.provideSysUiStateProvider, this.provideMainHandlerProvider));
        this.assistManagerGoogleProvider = provider19;
        delegateFactory2.setDelegatedProvider(provider19);
        this.lockscreenGestureLoggerProvider = DoubleCheck.provider(LockscreenGestureLogger_Factory.create());
        this.shadeControllerImplProvider = new DelegateFactory();
        this.accessibilityControllerProvider = DoubleCheck.provider(AccessibilityController_Factory.create(this.provideContextProvider));
        this.builderProvider = WakeLock_Builder_Factory.create(this.provideContextProvider);
        Provider<IBatteryStats> provider20 = DoubleCheck.provider(SystemServicesModule_ProvideIBatteryStatsFactory.create());
        this.provideIBatteryStatsProvider = provider20;
        Provider<KeyguardIndicationController> provider21 = DoubleCheck.provider(KeyguardIndicationController_Factory.create(this.provideContextProvider, this.builderProvider, this.keyguardStateControllerImplProvider, this.statusBarStateControllerImplProvider, this.keyguardUpdateMonitorProvider, this.provideDockManagerProvider, provider20));
        this.keyguardIndicationControllerProvider = provider21;
        this.lockscreenLockIconControllerProvider = DoubleCheck.provider(LockscreenLockIconController_Factory.create(this.lockscreenGestureLoggerProvider, this.keyguardUpdateMonitorProvider, this.provideLockPatternUtilsProvider, this.shadeControllerImplProvider, this.accessibilityControllerProvider, provider21, this.statusBarStateControllerImplProvider, this.provideConfigurationControllerProvider, this.notificationWakeUpCoordinatorProvider, this.keyguardBypassControllerProvider, this.provideDockManagerProvider));
        this.builderProvider2 = DelayedWakeLock_Builder_Factory.create(this.provideContextProvider);
        SystemServicesModule_ProvideWallpaperManagerFactory create = SystemServicesModule_ProvideWallpaperManagerFactory.create(this.provideContextProvider);
        this.provideWallpaperManagerProvider = create;
        this.lockscreenWallpaperProvider = DoubleCheck.provider(LockscreenWallpaper_Factory.create(create, SystemServicesModule_ProvideIWallPaperManagerFactory.create(), this.keyguardUpdateMonitorProvider, this.dumpManagerProvider, this.provideNotificationMediaManagerProvider, this.provideMainHandlerProvider));
        this.blurUtilsProvider = DoubleCheck.provider(BlurUtils_Factory.create(this.provideResourcesProvider, this.dumpManagerProvider));
        this.liveWallpaperScrimControllerProvider = DoubleCheck.provider(LiveWallpaperScrimController_Factory.create(this.lightBarControllerProvider, this.dozeParametersProvider, this.provideAlarmManagerProvider, this.keyguardStateControllerImplProvider, this.builderProvider2, this.provideHandlerProvider, SystemServicesModule_ProvideIWallPaperManagerFactory.create(), this.lockscreenWallpaperProvider, this.keyguardUpdateMonitorProvider, this.sysuiColorExtractorProvider, this.provideDockManagerProvider, this.blurUtilsProvider));
        this.provideKeyguardLiftControllerProvider = DoubleCheck.provider(SystemUIModule_ProvideKeyguardLiftControllerFactory.create(this.provideContextProvider, this.statusBarStateControllerImplProvider, this.asyncSensorManagerProvider, this.keyguardUpdateMonitorProvider, this.dumpManagerProvider));
        Provider<LogBuffer> provider22 = DoubleCheck.provider(LogModule_ProvideDozeLogBufferFactory.create(this.provideLogcatEchoTrackerProvider, this.dumpManagerProvider));
        this.provideDozeLogBufferProvider = provider22;
        DozeLogger_Factory create2 = DozeLogger_Factory.create(provider22);
        this.dozeLoggerProvider = create2;
        Provider<DozeLog> provider23 = DoubleCheck.provider(DozeLog_Factory.create(this.keyguardUpdateMonitorProvider, this.dumpManagerProvider, create2));
        this.dozeLogProvider = provider23;
        Provider<DozeScrimController> provider24 = DoubleCheck.provider(DozeScrimController_Factory.create(this.dozeParametersProvider, provider23));
        this.dozeScrimControllerProvider = provider24;
        Provider<BiometricUnlockController> provider25 = DoubleCheck.provider(BiometricUnlockController_Factory.create(this.provideContextProvider, provider24, this.newKeyguardViewMediatorProvider, this.liveWallpaperScrimControllerProvider, this.provideStatusBarProvider, this.shadeControllerImplProvider, this.notificationShadeWindowControllerProvider, this.keyguardStateControllerImplProvider, this.provideHandlerProvider, this.keyguardUpdateMonitorProvider, this.provideResourcesProvider, this.keyguardBypassControllerProvider, this.dozeParametersProvider, this.provideMetricsLoggerProvider, this.dumpManagerProvider));
        Provider<BiometricUnlockController> provider26 = provider25;
        this.biometricUnlockControllerProvider = provider25;
        this.dozeServiceHostProvider = DoubleCheck.provider(DozeServiceHost_Factory.create(this.dozeLogProvider, this.providePowerManagerProvider, this.wakefulnessLifecycleProvider, this.statusBarStateControllerImplProvider, this.deviceProvisionedControllerImplProvider, this.provideHeadsUpManagerPhoneProvider, this.batteryControllerImplGoogleProvider, this.liveWallpaperScrimControllerProvider, provider26, this.newKeyguardViewMediatorProvider, this.assistManagerGoogleProvider, this.dozeScrimControllerProvider, this.keyguardUpdateMonitorProvider, this.provideVisualStabilityManagerProvider, this.pulseExpansionHandlerProvider, this.notificationShadeWindowControllerProvider, this.notificationWakeUpCoordinatorProvider, this.lockscreenLockIconControllerProvider));
        this.screenPinningRequestProvider = ScreenPinningRequest_Factory.create(this.provideContextProvider, this.optionalOfLazyOfStatusBarProvider);
        Provider<VolumeDialogControllerImpl> provider27 = DoubleCheck.provider(VolumeDialogControllerImpl_Factory.create(this.provideContextProvider, this.broadcastDispatcherProvider, this.optionalOfLazyOfStatusBarProvider));
        this.volumeDialogControllerImplProvider = provider27;
        this.volumeDialogComponentProvider = DoubleCheck.provider(VolumeDialogComponent_Factory.create(this.provideContextProvider, this.newKeyguardViewMediatorProvider, provider27));
        this.optionalOfRecentsProvider = PresentJdkOptionalInstanceProvider.m121of(this.provideRecentsProvider);
        this.statusBarComponentBuilderProvider = new Provider<com.android.systemui.statusbar.phone.dagger.StatusBarComponent.Builder>() {
            public com.android.systemui.statusbar.phone.dagger.StatusBarComponent.Builder get() {
                return new StatusBarComponentBuilder();
            }
        };
        this.lightsOutNotifControllerProvider = DoubleCheck.provider(LightsOutNotifController_Factory.create(this.provideWindowManagerProvider, this.provideNotificationEntryManagerProvider, this.provideCommandQueueProvider));
        this.statusBarRemoteInputCallbackProvider = DoubleCheck.provider(StatusBarRemoteInputCallback_Factory.create(this.provideContextProvider, this.notificationGroupManagerProvider, this.notificationLockscreenUserManagerGoogleProvider, this.keyguardStateControllerImplProvider, this.statusBarStateControllerImplProvider, this.statusBarKeyguardViewManagerProvider, this.activityStarterDelegateProvider, this.shadeControllerImplProvider, this.provideCommandQueueProvider));
        Provider<ActivityIntentHelper> provider28 = DoubleCheck.provider(ActivityIntentHelper_Factory.create(this.provideContextProvider));
        Provider<ActivityIntentHelper> provider29 = provider28;
        this.activityIntentHelperProvider = provider28;
        this.builderProvider3 = DoubleCheck.provider(StatusBarNotificationActivityStarter_Builder_Factory.create(this.provideContextProvider, this.provideCommandQueueProvider, this.assistManagerGoogleProvider, this.provideNotificationEntryManagerProvider, this.provideHeadsUpManagerPhoneProvider, this.activityStarterDelegateProvider, this.provideIStatusBarServiceProvider, this.statusBarStateControllerImplProvider, this.statusBarKeyguardViewManagerProvider, this.provideKeyguardManagerProvider, this.provideIDreamManagerProvider, this.provideNotificationRemoteInputManagerProvider, this.statusBarRemoteInputCallbackProvider, this.notificationGroupManagerProvider, this.notificationLockscreenUserManagerGoogleProvider, this.keyguardStateControllerImplProvider, this.notificationInterruptStateProviderImplProvider, this.provideMetricsLoggerProvider, this.provideLockPatternUtilsProvider, this.provideMainHandlerProvider, this.provideBgHandlerProvider, this.provideUiBackgroundExecutorProvider, provider29, this.newBubbleControllerProvider, this.shadeControllerImplProvider, this.featureFlagsProvider, this.notifPipelineProvider, this.notifCollectionProvider));
        dagger.internal.Factory create3 = InstanceFactory.create(this);
        this.systemUIGoogleRootComponentProvider = create3;
        this.injectionInflationControllerProvider = DoubleCheck.provider(InjectionInflationController_Factory.create(create3));
        C18933 r1 = new Provider<com.android.systemui.statusbar.notification.row.dagger.NotificationRowComponent.Builder>() {
            public com.android.systemui.statusbar.notification.row.dagger.NotificationRowComponent.Builder get() {
                return new NotificationRowComponentBuilder();
            }
        };
        this.notificationRowComponentBuilderProvider = r1;
        this.superStatusBarViewFactoryProvider = DoubleCheck.provider(SuperStatusBarViewFactory_Factory.create(this.provideContextProvider, this.injectionInflationControllerProvider, r1, this.lockscreenLockIconControllerProvider));
        this.initControllerProvider = DoubleCheck.provider(InitController_Factory.create());
        this.provideTimeTickHandlerProvider = DoubleCheck.provider(DependencyProvider_ProvideTimeTickHandlerFactory.create(builder.dependencyProvider));
        this.pluginDependencyProvider = DoubleCheck.provider(PluginDependencyProvider_Factory.create(this.providePluginManagerProvider));
        this.keyguardDismissUtilProvider = DoubleCheck.provider(KeyguardDismissUtil_Factory.create());
    }

    private void initialize4(Builder builder) {
        this.userInfoControllerImplProvider = DoubleCheck.provider(UserInfoControllerImpl_Factory.create(this.provideContextProvider));
        this.castControllerImplProvider = DoubleCheck.provider(CastControllerImpl_Factory.create(this.provideContextProvider));
        this.hotspotControllerImplProvider = DoubleCheck.provider(HotspotControllerImpl_Factory.create(this.provideContextProvider, this.provideMainHandlerProvider, this.provideBgHandlerProvider));
        this.provideLocalBluetoothControllerProvider = DoubleCheck.provider(SystemServicesModule_ProvideLocalBluetoothControllerFactory.create(this.provideContextProvider, this.provideBgHandlerProvider));
        this.bluetoothControllerImplProvider = DoubleCheck.provider(BluetoothControllerImpl_Factory.create(this.provideContextProvider, this.provideBgLooperProvider, ConcurrencyModule_ProvideMainLooperFactory.create(), this.provideLocalBluetoothControllerProvider));
        this.nextAlarmControllerImplProvider = DoubleCheck.provider(NextAlarmControllerImpl_Factory.create(this.provideContextProvider));
        this.rotationLockControllerImplProvider = DoubleCheck.provider(RotationLockControllerImpl_Factory.create(this.provideContextProvider));
        this.provideDataSaverControllerProvider = DoubleCheck.provider(DependencyProvider_ProvideDataSaverControllerFactory.create(builder.dependencyProvider, this.networkControllerImplProvider));
        this.zenModeControllerImplProvider = DoubleCheck.provider(ZenModeControllerImpl_Factory.create(this.provideContextProvider, this.provideMainHandlerProvider, this.broadcastDispatcherProvider));
        this.locationControllerImplProvider = DoubleCheck.provider(LocationControllerImpl_Factory.create(this.provideContextProvider, this.provideBgLooperProvider, this.broadcastDispatcherProvider, this.bootCompleteCacheImplProvider));
        this.sensorPrivacyControllerImplProvider = DoubleCheck.provider(SensorPrivacyControllerImpl_Factory.create(this.provideContextProvider));
        this.provideAudioManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideAudioManagerFactory.create(this.provideContextProvider));
        this.provideTelecomManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideTelecomManagerFactory.create(this.provideContextProvider));
        this.provideDisplayIdProvider = SystemServicesModule_ProvideDisplayIdFactory.create(this.provideContextProvider);
        DateFormatUtil_Factory create = DateFormatUtil_Factory.create(this.provideContextProvider);
        DateFormatUtil_Factory dateFormatUtil_Factory = create;
        this.dateFormatUtilProvider = create;
        this.phoneStatusBarPolicyProvider = PhoneStatusBarPolicy_Factory.create(this.statusBarIconControllerImplProvider, this.provideCommandQueueProvider, this.broadcastDispatcherProvider, this.provideUiBackgroundExecutorProvider, this.provideResourcesProvider, this.castControllerImplProvider, this.hotspotControllerImplProvider, this.bluetoothControllerImplProvider, this.nextAlarmControllerImplProvider, this.userInfoControllerImplProvider, this.rotationLockControllerImplProvider, this.provideDataSaverControllerProvider, this.zenModeControllerImplProvider, this.deviceProvisionedControllerImplProvider, this.keyguardStateControllerImplProvider, this.locationControllerImplProvider, this.sensorPrivacyControllerImplProvider, this.provideIActivityManagerProvider, this.provideAlarmManagerProvider, this.provideUserManagerProvider, this.provideAudioManagerProvider, this.recordingControllerProvider, this.provideTelecomManagerProvider, this.provideDisplayIdProvider, this.provideSharePreferencesProvider, dateFormatUtil_Factory);
        Provider<StatusBarTouchableRegionManager> provider = DoubleCheck.provider(StatusBarTouchableRegionManager_Factory.create(this.provideContextProvider, this.notificationShadeWindowControllerProvider, this.provideConfigurationControllerProvider, this.provideHeadsUpManagerPhoneProvider, this.newBubbleControllerProvider));
        Provider<StatusBarTouchableRegionManager> provider2 = provider;
        this.statusBarTouchableRegionManagerProvider = provider;
        DelegateFactory delegateFactory = (DelegateFactory) this.provideStatusBarProvider;
        Provider<StatusBarGoogle> provider3 = DoubleCheck.provider(StatusBarGoogleModule_ProvideStatusBarFactory.create(this.smartSpaceControllerProvider, this.wallpaperNotifierProvider, this.provideContextProvider, this.provideNotificationsControllerProvider, this.lightBarControllerProvider, this.provideAutoHideControllerProvider, this.keyguardUpdateMonitorProvider, this.statusBarIconControllerImplProvider, this.pulseExpansionHandlerProvider, this.notificationWakeUpCoordinatorProvider, this.keyguardBypassControllerProvider, this.keyguardStateControllerImplProvider, this.provideHeadsUpManagerPhoneProvider, this.dynamicPrivacyControllerProvider, this.bypassHeadsUpNotifierProvider, this.falsingManagerProxyProvider, this.broadcastDispatcherProvider, this.remoteInputQuickSettingsDisablerProvider, this.provideNotificationGutsManagerProvider, this.provideNotificationLoggerProvider, this.notificationInterruptStateProviderImplProvider, this.provideNotificationViewHierarchyManagerProvider, this.newKeyguardViewMediatorProvider, this.provideNotificationAlertingManagerProvider, this.provideDisplayMetricsProvider, this.provideMetricsLoggerProvider, this.provideUiBackgroundExecutorProvider, this.provideNotificationMediaManagerProvider, this.notificationLockscreenUserManagerGoogleProvider, this.provideNotificationRemoteInputManagerProvider, this.userSwitcherControllerProvider, this.networkControllerImplProvider, this.batteryControllerImplGoogleProvider, this.sysuiColorExtractorProvider, this.screenLifecycleProvider, this.wakefulnessLifecycleProvider, this.statusBarStateControllerImplProvider, this.vibratorHelperProvider, this.newBubbleControllerProvider, this.notificationGroupManagerProvider, this.provideVisualStabilityManagerProvider, this.deviceProvisionedControllerImplProvider, this.provideNavigationBarControllerProvider, this.assistManagerGoogleProvider, this.provideConfigurationControllerProvider, this.notificationShadeWindowControllerProvider, this.lockscreenLockIconControllerProvider, this.dozeParametersProvider, this.liveWallpaperScrimControllerProvider, this.provideKeyguardLiftControllerProvider, this.lockscreenWallpaperProvider, this.biometricUnlockControllerProvider, this.dozeServiceHostProvider, this.providePowerManagerProvider, this.screenPinningRequestProvider, this.dozeScrimControllerProvider, this.volumeDialogComponentProvider, this.provideCommandQueueProvider, this.optionalOfRecentsProvider, this.statusBarComponentBuilderProvider, this.providePluginManagerProvider, this.optionalOfDividerProvider, this.lightsOutNotifControllerProvider, this.builderProvider3, this.shadeControllerImplProvider, this.superStatusBarViewFactoryProvider, this.statusBarKeyguardViewManagerProvider, this.providesViewMediatorCallbackProvider, this.initControllerProvider, this.darkIconDispatcherImplProvider, this.provideTimeTickHandlerProvider, this.pluginDependencyProvider, this.keyguardDismissUtilProvider, this.extensionControllerImplProvider, this.userInfoControllerImplProvider, this.phoneStatusBarPolicyProvider, this.keyguardIndicationControllerProvider, this.dismissCallbackRegistryProvider, provider2));
        this.provideStatusBarProvider = provider3;
        delegateFactory.setDelegatedProvider(provider3);
        Provider<MediaArtworkProcessor> provider4 = DoubleCheck.provider(MediaArtworkProcessor_Factory.create());
        this.mediaArtworkProcessorProvider = provider4;
        DelegateFactory delegateFactory2 = (DelegateFactory) this.provideNotificationMediaManagerProvider;
        Provider<NotificationMediaManager> provider5 = DoubleCheck.provider(C1171x30c882de.create(this.provideContextProvider, this.provideStatusBarProvider, this.notificationShadeWindowControllerProvider, this.provideNotificationEntryManagerProvider, provider4, this.keyguardBypassControllerProvider, this.provideMainExecutorProvider, DeviceConfigProxy_Factory.create()));
        this.provideNotificationMediaManagerProvider = provider5;
        delegateFactory2.setDelegatedProvider(provider5);
        DelegateFactory delegateFactory3 = (DelegateFactory) this.statusBarKeyguardViewManagerProvider;
        Provider<StatusBarKeyguardViewManager> provider6 = DoubleCheck.provider(StatusBarKeyguardViewManager_Factory.create(this.provideContextProvider, this.providesViewMediatorCallbackProvider, this.provideLockPatternUtilsProvider, this.statusBarStateControllerImplProvider, this.provideConfigurationControllerProvider, this.keyguardUpdateMonitorProvider, this.navigationModeControllerProvider, this.provideDockManagerProvider, this.notificationShadeWindowControllerProvider, this.keyguardStateControllerImplProvider, this.provideNotificationMediaManagerProvider));
        this.statusBarKeyguardViewManagerProvider = provider6;
        delegateFactory3.setDelegatedProvider(provider6);
        DelegateFactory delegateFactory4 = (DelegateFactory) this.shadeControllerImplProvider;
        Provider<ShadeControllerImpl> provider7 = DoubleCheck.provider(ShadeControllerImpl_Factory.create(this.provideCommandQueueProvider, this.statusBarStateControllerImplProvider, this.notificationShadeWindowControllerProvider, this.statusBarKeyguardViewManagerProvider, this.provideWindowManagerProvider, this.provideStatusBarProvider, this.assistManagerGoogleProvider, this.newBubbleControllerProvider));
        this.shadeControllerImplProvider = provider7;
        delegateFactory4.setDelegatedProvider(provider7);
        Provider<BubbleData> provider8 = DoubleCheck.provider(BubbleData_Factory.create(this.provideContextProvider));
        this.bubbleDataProvider = provider8;
        DelegateFactory delegateFactory5 = (DelegateFactory) this.newBubbleControllerProvider;
        Provider<BubbleController> provider9 = DoubleCheck.provider(BubbleModule_NewBubbleControllerFactory.create(this.provideContextProvider, this.notificationShadeWindowControllerProvider, this.statusBarStateControllerImplProvider, this.shadeControllerImplProvider, provider8, this.provideConfigurationControllerProvider, this.notificationInterruptStateProviderImplProvider, this.zenModeControllerImplProvider, this.notificationLockscreenUserManagerGoogleProvider, this.notificationGroupManagerProvider, this.provideNotificationEntryManagerProvider, this.notifPipelineProvider, this.featureFlagsProvider, this.dumpManagerProvider, this.floatingContentCoordinatorProvider));
        this.newBubbleControllerProvider = provider9;
        delegateFactory5.setDelegatedProvider(provider9);
        this.bubbleOverflowActivityProvider = BubbleOverflowActivity_Factory.create(this.newBubbleControllerProvider);
        dagger.internal.MapProviderFactory.Builder builder2 = MapProviderFactory.builder(9);
        builder2.put(ControlsProviderSelectorActivity.class, this.controlsProviderSelectorActivityProvider);
        builder2.put(ControlsFavoritingActivity.class, this.controlsFavoritingActivityProvider);
        builder2.put(ControlsRequestDialog.class, this.controlsRequestDialogProvider);
        builder2.put(TunerActivity.class, TunerActivity_Factory.create());
        builder2.put(ForegroundServicesDialog.class, ForegroundServicesDialog_Factory.create());
        builder2.put(WorkLockActivity.class, this.workLockActivityProvider);
        builder2.put(BrightnessDialog.class, this.brightnessDialogProvider);
        builder2.put(ScreenRecordDialog.class, this.screenRecordDialogProvider);
        builder2.put(BubbleOverflowActivity.class, this.bubbleOverflowActivityProvider);
        this.mapOfClassOfAndProviderOfActivityProvider = builder2.build();
        this.assistHandleServiceProvider = AssistHandleService_Factory.create(this.assistManagerGoogleProvider);
        DozeFactory_Factory create2 = DozeFactory_Factory.create(this.falsingManagerProxyProvider, this.dozeLogProvider, this.dozeParametersProvider, this.batteryControllerImplGoogleProvider, this.asyncSensorManagerProvider, this.provideAlarmManagerProvider, this.wakefulnessLifecycleProvider, this.keyguardUpdateMonitorProvider, this.provideDockManagerProvider, SystemServicesModule_ProvideIWallPaperManagerFactory.create(), this.proximitySensorProvider, this.builderProvider2, this.provideHandlerProvider, this.biometricUnlockControllerProvider, this.broadcastDispatcherProvider, this.dozeServiceHostProvider);
        this.dozeFactoryProvider = create2;
        this.dozeServiceProvider = DozeService_Factory.create(create2, this.providePluginManagerProvider);
        this.imageWallpaperProvider = ImageWallpaper_Factory.create(this.dozeParametersProvider);
        Provider<KeyguardLifecyclesDispatcher> provider10 = DoubleCheck.provider(KeyguardLifecyclesDispatcher_Factory.create(this.screenLifecycleProvider, this.wakefulnessLifecycleProvider));
        this.keyguardLifecyclesDispatcherProvider = provider10;
        this.keyguardServiceProvider = KeyguardService_Factory.create(this.newKeyguardViewMediatorProvider, provider10);
        this.systemUIServiceProvider = SystemUIService_Factory.create(this.provideMainHandlerProvider, this.dumpManagerProvider);
        this.systemUIAuxiliaryDumpServiceProvider = SystemUIAuxiliaryDumpService_Factory.create(this.dumpManagerProvider);
        ScreenshotNotificationsController_Factory create3 = ScreenshotNotificationsController_Factory.create(this.provideContextProvider, this.provideWindowManagerProvider);
        this.screenshotNotificationsControllerProvider = create3;
        this.globalScreenshotProvider = DoubleCheck.provider(GlobalScreenshot_Factory.create(this.provideContextProvider, this.provideResourcesProvider, this.providerLayoutInflaterProvider, create3));
        Provider<GlobalScreenshotLegacy> provider11 = DoubleCheck.provider(GlobalScreenshotLegacy_Factory.create(this.provideContextProvider, this.provideResourcesProvider, this.providerLayoutInflaterProvider, this.screenshotNotificationsControllerProvider));
        this.globalScreenshotLegacyProvider = provider11;
        this.takeScreenshotServiceProvider = TakeScreenshotService_Factory.create(this.globalScreenshotProvider, provider11, this.provideUserManagerProvider);
        this.recordingServiceProvider = RecordingService_Factory.create(this.recordingControllerProvider);
        dagger.internal.MapProviderFactory.Builder builder3 = MapProviderFactory.builder(8);
        builder3.put(AssistHandleService.class, this.assistHandleServiceProvider);
        builder3.put(DozeService.class, this.dozeServiceProvider);
        builder3.put(ImageWallpaper.class, this.imageWallpaperProvider);
        builder3.put(KeyguardService.class, this.keyguardServiceProvider);
        builder3.put(SystemUIService.class, this.systemUIServiceProvider);
        builder3.put(SystemUIAuxiliaryDumpService.class, this.systemUIAuxiliaryDumpServiceProvider);
        builder3.put(TakeScreenshotService.class, this.takeScreenshotServiceProvider);
        builder3.put(RecordingService.class, this.recordingServiceProvider);
        this.mapOfClassOfAndProviderOfServiceProvider = builder3.build();
        this.authControllerProvider = DoubleCheck.provider(AuthController_Factory.create(this.provideContextProvider, this.provideCommandQueueProvider));
        Provider<String> provider12 = DoubleCheck.provider(SystemUIGoogleModule_ProvideLeakReportEmailFactory.create());
        this.provideLeakReportEmailProvider = provider12;
        Provider<LeakReporter> provider13 = DoubleCheck.provider(LeakReporter_Factory.create(this.provideContextProvider, this.provideLeakDetectorProvider, provider12));
        this.leakReporterProvider = provider13;
        Provider<GarbageMonitor> provider14 = DoubleCheck.provider(GarbageMonitor_Factory.create(this.provideContextProvider, this.provideBgLooperProvider, this.provideLeakDetectorProvider, provider13));
        this.garbageMonitorProvider = provider14;
        this.serviceProvider = DoubleCheck.provider(GarbageMonitor_Service_Factory.create(this.provideContextProvider, provider14));
        this.globalActionsComponentProvider = new DelegateFactory();
        this.provideConnectivityManagagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideConnectivityManagagerFactory.create(this.provideContextProvider));
        this.provideTelephonyManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideTelephonyManagerFactory.create(this.provideContextProvider));
        this.provideVibratorProvider = DoubleCheck.provider(SystemServicesModule_ProvideVibratorFactory.create(this.provideContextProvider));
        Provider<Choreographer> provider15 = DoubleCheck.provider(DependencyProvider_ProvidesChoreographerFactory.create(builder.dependencyProvider));
        this.providesChoreographerProvider = provider15;
        Provider<NotificationShadeDepthController> provider16 = DoubleCheck.provider(NotificationShadeDepthController_Factory.create(this.statusBarStateControllerImplProvider, this.blurUtilsProvider, this.biometricUnlockControllerProvider, this.keyguardStateControllerImplProvider, provider15, this.provideWallpaperManagerProvider, this.notificationShadeWindowControllerProvider, this.dumpManagerProvider));
        Provider<NotificationShadeDepthController> provider17 = provider16;
        this.notificationShadeDepthControllerProvider = provider16;
        GlobalActionsDialog_Factory create4 = GlobalActionsDialog_Factory.create(this.provideContextProvider, this.globalActionsComponentProvider, this.provideAudioManagerProvider, this.provideIDreamManagerProvider, this.provideDevicePolicyManagerProvider, this.provideLockPatternUtilsProvider, this.broadcastDispatcherProvider, this.provideConnectivityManagagerProvider, this.provideTelephonyManagerProvider, this.provideContentResolverProvider, this.provideVibratorProvider, this.provideResourcesProvider, this.provideConfigurationControllerProvider, this.activityStarterDelegateProvider, this.keyguardStateControllerImplProvider, this.provideUserManagerProvider, this.provideTrustManagerProvider, this.provideIActivityManagerProvider, this.provideTelecomManagerProvider, this.provideMetricsLoggerProvider, provider17, this.sysuiColorExtractorProvider, this.provideIStatusBarServiceProvider, this.blurUtilsProvider, this.notificationShadeWindowControllerProvider, this.controlsUiControllerImplProvider, this.provideIWindowManagerProvider, this.provideBackgroundExecutorProvider, this.controlsListingControllerImplProvider, this.controlsControllerImplProvider);
        this.globalActionsDialogProvider = create4;
        GlobalActionsImpl_Factory create5 = GlobalActionsImpl_Factory.create(this.provideContextProvider, this.provideCommandQueueProvider, create4, this.blurUtilsProvider);
        this.globalActionsImplProvider = create5;
        DelegateFactory delegateFactory6 = (DelegateFactory) this.globalActionsComponentProvider;
        Provider<GlobalActionsComponent> provider18 = DoubleCheck.provider(GlobalActionsComponent_Factory.create(this.provideContextProvider, this.provideCommandQueueProvider, this.extensionControllerImplProvider, create5));
        this.globalActionsComponentProvider = provider18;
        delegateFactory6.setDelegatedProvider(provider18);
        this.opaHomeButtonProvider = OpaHomeButton_Factory.create(this.newKeyguardViewMediatorProvider, this.provideStatusBarProvider);
        OpaLockscreen_Factory create6 = OpaLockscreen_Factory.create(this.provideStatusBarProvider, this.keyguardStateControllerImplProvider);
        this.opaLockscreenProvider = create6;
        this.assistInvocationEffectProvider = AssistInvocationEffect_Factory.create(this.assistManagerGoogleProvider, this.opaHomeButtonProvider, create6);
        this.builderProvider4 = LaunchOpa_Builder_Factory.create(this.provideContextProvider, this.provideStatusBarProvider);
        this.builderProvider5 = SettingsAction_Builder_Factory.create(this.provideContextProvider, this.provideStatusBarProvider);
        this.builderProvider6 = CameraAction_Builder_Factory.create(this.provideContextProvider, this.provideStatusBarProvider);
        this.builderProvider7 = SetupWizardAction_Builder_Factory.create(this.provideContextProvider, this.provideStatusBarProvider);
        this.squishyNavigationButtonsProvider = SquishyNavigationButtons_Factory.create(this.provideContextProvider, this.newKeyguardViewMediatorProvider, this.provideStatusBarProvider);
        Provider<Optional<HeadsUpManager>> access$500 = PresentJdkOptionalInstanceProvider.m121of(this.provideHeadsUpManagerPhoneProvider);
        this.optionalOfHeadsUpManagerProvider = access$500;
        UnpinNotifications_Factory create7 = UnpinNotifications_Factory.create(this.provideContextProvider, access$500);
        this.unpinNotificationsProvider = create7;
        ServiceConfigurationGoogle_Factory create8 = ServiceConfigurationGoogle_Factory.create(this.provideContextProvider, this.assistInvocationEffectProvider, this.builderProvider4, this.builderProvider5, this.builderProvider6, this.builderProvider7, this.squishyNavigationButtonsProvider, create7);
        this.serviceConfigurationGoogleProvider = create8;
        this.googleServicesProvider = DoubleCheck.provider(GoogleServices_Factory.create(this.provideContextProvider, create8, this.provideStatusBarProvider));
        this.instantAppNotifierProvider = DoubleCheck.provider(InstantAppNotifier_Factory.create(this.provideContextProvider, this.provideCommandQueueProvider, this.provideUiBackgroundExecutorProvider, this.provideDividerProvider));
        this.latencyTesterProvider = DoubleCheck.provider(LatencyTester_Factory.create(this.provideContextProvider, this.biometricUnlockControllerProvider, this.providePowerManagerProvider, this.broadcastDispatcherProvider));
        this.powerUIProvider = DoubleCheck.provider(PowerUI_Factory.create(this.provideContextProvider, this.broadcastDispatcherProvider, this.provideCommandQueueProvider, this.provideStatusBarProvider));
        this.screenDecorationsProvider = DoubleCheck.provider(ScreenDecorations_Factory.create(this.provideContextProvider, this.provideMainHandlerProvider, this.broadcastDispatcherProvider, this.tunerServiceImplProvider));
        this.shortcutKeyDispatcherProvider = DoubleCheck.provider(ShortcutKeyDispatcher_Factory.create(this.provideContextProvider, this.provideDividerProvider, this.provideRecentsProvider));
        this.sizeCompatModeActivityControllerProvider = DoubleCheck.provider(SizeCompatModeActivityController_Factory.create(this.provideContextProvider, this.provideActivityManagerWrapperProvider, this.provideCommandQueueProvider));
        this.sliceBroadcastRelayHandlerProvider = DoubleCheck.provider(SliceBroadcastRelayHandler_Factory.create(this.provideContextProvider, this.broadcastDispatcherProvider));
        this.themeOverlayControllerProvider = DoubleCheck.provider(ThemeOverlayController_Factory.create(this.provideContextProvider, this.broadcastDispatcherProvider, this.provideBgHandlerProvider));
        this.toastUIProvider = DoubleCheck.provider(ToastUI_Factory.create(this.provideContextProvider, this.provideCommandQueueProvider));
        this.tvStatusBarProvider = DoubleCheck.provider(TvStatusBar_Factory.create(this.provideContextProvider, this.provideCommandQueueProvider));
        this.volumeUIProvider = DoubleCheck.provider(VolumeUI_Factory.create(this.provideContextProvider, this.volumeDialogComponentProvider));
        this.windowMagnificationProvider = DoubleCheck.provider(WindowMagnification_Factory.create(this.provideContextProvider, this.provideMainHandlerProvider));
        dagger.internal.MapProviderFactory.Builder builder4 = MapProviderFactory.builder(22);
        builder4.put(AuthController.class, this.authControllerProvider);
        builder4.put(Divider.class, this.provideDividerProvider);
        builder4.put(GarbageMonitor.Service.class, this.serviceProvider);
        builder4.put(GlobalActionsComponent.class, this.globalActionsComponentProvider);
        builder4.put(GoogleServices.class, this.googleServicesProvider);
        builder4.put(InstantAppNotifier.class, this.instantAppNotifierProvider);
        builder4.put(KeyguardViewMediator.class, this.newKeyguardViewMediatorProvider);
        builder4.put(LatencyTester.class, this.latencyTesterProvider);
        builder4.put(PipUI.class, this.pipUIProvider);
        builder4.put(PowerUI.class, this.powerUIProvider);
        builder4.put(Recents.class, this.provideRecentsProvider);
        builder4.put(ScreenDecorations.class, this.screenDecorationsProvider);
        builder4.put(ShortcutKeyDispatcher.class, this.shortcutKeyDispatcherProvider);
        builder4.put(SizeCompatModeActivityController.class, this.sizeCompatModeActivityControllerProvider);
        builder4.put(SliceBroadcastRelayHandler.class, this.sliceBroadcastRelayHandlerProvider);
        builder4.put(StatusBar.class, this.provideStatusBarProvider);
        builder4.put(StatusBarGoogle.class, this.provideStatusBarProvider);
        builder4.put(ThemeOverlayController.class, this.themeOverlayControllerProvider);
        builder4.put(ToastUI.class, this.toastUIProvider);
        builder4.put(TvStatusBar.class, this.tvStatusBarProvider);
        builder4.put(VolumeUI.class, this.volumeUIProvider);
        builder4.put(WindowMagnification.class, this.windowMagnificationProvider);
        this.mapOfClassOfAndProviderOfSystemUIProvider = builder4.build();
        this.overviewProxyRecentsImplProvider = DoubleCheck.provider(OverviewProxyRecentsImpl_Factory.create(this.optionalOfLazyOfStatusBarProvider, this.optionalOfDividerProvider));
        dagger.internal.MapProviderFactory.Builder builder5 = MapProviderFactory.builder(1);
        builder5.put(OverviewProxyRecentsImpl.class, this.overviewProxyRecentsImplProvider);
        this.mapOfClassOfAndProviderOfRecentsImplementationProvider = builder5.build();
        this.actionProxyReceiverProvider = GlobalScreenshot_ActionProxyReceiver_Factory.create(this.optionalOfLazyOfStatusBarProvider);
        dagger.internal.MapProviderFactory.Builder builder6 = MapProviderFactory.builder(1);
        builder6.put(ActionProxyReceiver.class, this.actionProxyReceiverProvider);
        MapProviderFactory build = builder6.build();
        this.mapOfClassOfAndProviderOfBroadcastReceiverProvider = build;
        DelegateFactory delegateFactory7 = (DelegateFactory) this.contextComponentResolverProvider;
        Provider<ContextComponentResolver> provider19 = DoubleCheck.provider(ContextComponentResolver_Factory.create(this.mapOfClassOfAndProviderOfActivityProvider, this.mapOfClassOfAndProviderOfServiceProvider, this.mapOfClassOfAndProviderOfSystemUIProvider, this.mapOfClassOfAndProviderOfRecentsImplementationProvider, build));
        this.contextComponentResolverProvider = provider19;
        delegateFactory7.setDelegatedProvider(provider19);
        this.provideAllowNotificationLongPressProvider = DoubleCheck.provider(SystemUIGoogleModule_ProvideAllowNotificationLongPressFactory.create());
        Provider<ContentResolverWrapper> provider20 = DoubleCheck.provider(ContentResolverWrapper_Factory.create(this.provideContextProvider));
        this.contentResolverWrapperProvider = provider20;
        Provider<Factory> provider21 = DoubleCheck.provider(ColumbusContentObserver_Factory_Factory.create(provider20, this.provideIActivityManagerProvider));
        this.factoryProvider = provider21;
        this.dismissTimerProvider = DoubleCheck.provider(DismissTimer_Factory.create(this.provideContextProvider, provider21));
        this.snoozeAlarmProvider = DoubleCheck.provider(SnoozeAlarm_Factory.create(this.provideContextProvider, this.factoryProvider));
        this.silenceCallProvider = DoubleCheck.provider(SilenceCall_Factory.create(this.provideContextProvider, this.factoryProvider));
        this.assistInvocationEffectProvider2 = DoubleCheck.provider(com.google.android.systemui.columbus.feedback.AssistInvocationEffect_Factory.create(this.assistManagerGoogleProvider));
        dagger.internal.SetFactory.Builder builder7 = SetFactory.builder(1, 0);
        builder7.addProvider(this.assistInvocationEffectProvider2);
        SetFactory build2 = builder7.build();
        this.namedSetOfFeedbackEffectProvider = build2;
        this.launchOpaProvider = DoubleCheck.provider(LaunchOpa_Factory.create(this.provideContextProvider, this.provideStatusBarProvider, build2, this.assistManagerGoogleProvider, this.tunerServiceImplProvider, this.factoryProvider));
        this.launchCameraProvider = DoubleCheck.provider(LaunchCamera_Factory.create(this.provideContextProvider));
        this.manageMediaProvider = DoubleCheck.provider(ManageMedia_Factory.create(this.provideContextProvider, this.provideMainHandlerProvider));
        this.takeScreenshotProvider = DoubleCheck.provider(TakeScreenshot_Factory.create(this.provideContextProvider, this.provideMainHandlerProvider));
        Provider<LaunchOverview> provider22 = DoubleCheck.provider(LaunchOverview_Factory.create(this.provideContextProvider, this.provideRecentsProvider));
        this.launchOverviewProvider = provider22;
        Provider<Map<String, Action>> provider23 = DoubleCheck.provider(ColumbusModule_ProvideUserSelectedActionsFactory.create(this.launchOpaProvider, this.launchCameraProvider, this.manageMediaProvider, this.takeScreenshotProvider, provider22));
        this.provideUserSelectedActionsProvider = provider23;
        Provider<UserSelectedAction> provider24 = DoubleCheck.provider(UserSelectedAction_Factory.create(this.provideContextProvider, this.deviceConfigHelperProvider, provider23, this.launchOpaProvider, this.provideMainHandlerProvider));
        this.userSelectedActionProvider = provider24;
        this.settingsActionProvider = DoubleCheck.provider(SettingsAction_Factory.create(this.provideContextProvider, provider24, this.provideStatusBarProvider));
    }

    private void initialize5(Builder builder) {
        this.provideFullscreenActionsProvider = DoubleCheck.provider(ColumbusModule_ProvideFullscreenActionsFactory.create(this.dismissTimerProvider, this.snoozeAlarmProvider, this.silenceCallProvider, this.settingsActionProvider));
        this.unpinNotificationsProvider2 = DoubleCheck.provider(com.google.android.systemui.columbus.actions.UnpinNotifications_Factory.create(this.optionalOfHeadsUpManagerProvider, this.provideContextProvider, this.factoryProvider));
        KeyguardVisibility_Factory create = KeyguardVisibility_Factory.create(this.provideContextProvider, this.keyguardStateControllerImplProvider);
        this.keyguardVisibilityProvider = create;
        KeyguardDeferredSetup_Factory create2 = KeyguardDeferredSetup_Factory.create(this.provideContextProvider, this.provideFullscreenActionsProvider, create, this.factoryProvider);
        this.keyguardDeferredSetupProvider = create2;
        Provider<SetupWizardAction> provider = DoubleCheck.provider(SetupWizardAction_Factory.create(this.provideContextProvider, this.settingsActionProvider, this.userSelectedActionProvider, create2, this.provideStatusBarProvider, this.keyguardUpdateMonitorProvider));
        this.setupWizardActionProvider = provider;
        this.provideColumbusActionsProvider = DoubleCheck.provider(ColumbusModule_ProvideColumbusActionsFactory.create(this.provideFullscreenActionsProvider, this.unpinNotificationsProvider2, provider, this.userSelectedActionProvider));
        this.hapticClickProvider = DoubleCheck.provider(HapticClick_Factory.create(this.provideContextProvider));
        this.navUndimEffectProvider = DoubleCheck.provider(NavUndimEffect_Factory.create(this.provideNavigationBarControllerProvider));
        Provider<UserActivity> provider2 = DoubleCheck.provider(UserActivity_Factory.create(this.provideContextProvider, this.keyguardStateControllerImplProvider));
        this.userActivityProvider = provider2;
        this.provideColumbusEffectsProvider = ColumbusModule_ProvideColumbusEffectsFactory.create(this.hapticClickProvider, this.navUndimEffectProvider, provider2);
        dagger.internal.SetFactory.Builder builder2 = SetFactory.builder(0, 1);
        builder2.addCollectionProvider(this.provideColumbusEffectsProvider);
        this.namedSetOfFeedbackEffectProvider2 = builder2.build();
        this.flagEnabledProvider = DoubleCheck.provider(FlagEnabled_Factory.create(this.provideContextProvider, this.provideMainHandlerProvider, this.deviceConfigHelperProvider));
        this.wakeModeProvider = DoubleCheck.provider(WakeMode_Factory.create(this.provideContextProvider, this.wakefulnessLifecycleProvider, this.factoryProvider));
        this.chargingStateProvider = DoubleCheck.provider(ChargingState_Factory.create(this.provideContextProvider, this.provideMainHandlerProvider, ColumbusModule_ProvideTransientGateDurationFactory.create()));
        this.usbStateProvider = DoubleCheck.provider(UsbState_Factory.create(this.provideContextProvider, this.provideMainHandlerProvider, ColumbusModule_ProvideTransientGateDurationFactory.create()));
        this.keyguardProximityProvider = KeyguardProximity_Factory.create(this.provideContextProvider, this.asyncSensorManagerProvider, this.keyguardVisibilityProvider);
        dagger.internal.SetFactory.Builder builder3 = SetFactory.builder(1, 0);
        builder3.addProvider(this.settingsActionProvider);
        SetFactory build = builder3.build();
        this.namedSetOfActionProvider = build;
        this.setupWizardProvider = DoubleCheck.provider(SetupWizard_Factory.create(this.provideContextProvider, build, this.deviceProvisionedControllerImplProvider));
        NonGesturalNavigation_Factory create3 = NonGesturalNavigation_Factory.create(this.provideContextProvider, this.navigationModeControllerProvider);
        this.nonGesturalNavigationProvider = create3;
        this.navigationBarVisibilityProvider = DoubleCheck.provider(NavigationBarVisibility_Factory.create(this.provideContextProvider, this.provideFullscreenActionsProvider, this.assistManagerGoogleProvider, this.keyguardVisibilityProvider, create3, this.provideCommandQueueProvider));
        dagger.internal.SetFactory.Builder builder4 = SetFactory.builder(0, 1);
        builder4.addCollectionProvider(ColumbusModule_ProvideBlockingSystemKeysFactory.create());
        this.namedSetOfIntegerProvider = builder4.build();
        this.systemKeyPressProvider = DoubleCheck.provider(SystemKeyPress_Factory.create(this.provideContextProvider, this.provideMainHandlerProvider, this.provideCommandQueueProvider, ColumbusModule_ProvideTransientGateDurationFactory.create(), this.namedSetOfIntegerProvider));
        this.telephonyActivityProvider = DoubleCheck.provider(TelephonyActivity_Factory.create(this.provideContextProvider));
        this.vrModeProvider = DoubleCheck.provider(VrMode_Factory.create(this.provideContextProvider));
        PowerState_Factory create4 = PowerState_Factory.create(this.provideContextProvider, this.wakefulnessLifecycleProvider);
        this.powerStateProvider = create4;
        this.cameraVisibilityProvider = DoubleCheck.provider(CameraVisibility_Factory.create(this.provideContextProvider, this.provideFullscreenActionsProvider, this.keyguardVisibilityProvider, create4, this.provideIActivityManagerProvider, this.provideMainHandlerProvider));
        Provider<PowerSaveState> provider3 = DoubleCheck.provider(PowerSaveState_Factory.create(this.provideContextProvider));
        this.powerSaveStateProvider = provider3;
        this.provideColumbusGatesProvider = ColumbusModule_ProvideColumbusGatesFactory.create(this.flagEnabledProvider, this.wakeModeProvider, this.chargingStateProvider, this.usbStateProvider, this.keyguardProximityProvider, this.setupWizardProvider, this.navigationBarVisibilityProvider, this.systemKeyPressProvider, this.telephonyActivityProvider, this.vrModeProvider, this.keyguardDeferredSetupProvider, this.cameraVisibilityProvider, provider3);
        dagger.internal.SetFactory.Builder builder5 = SetFactory.builder(0, 1);
        builder5.addCollectionProvider(this.provideColumbusGatesProvider);
        this.namedSetOfGateProvider = builder5.build();
        dagger.internal.SetFactory.Builder builder6 = SetFactory.builder(0, 1);
        builder6.addCollectionProvider(ColumbusModule_ProvideGestureAdjustmentsFactory.create());
        SetFactory build2 = builder6.build();
        this.namedSetOfAdjustmentProvider = build2;
        Provider<GestureConfiguration> provider4 = DoubleCheck.provider(GestureConfiguration_Factory.create(this.provideContextProvider, build2, this.factoryProvider));
        this.gestureConfigurationProvider = provider4;
        this.gestureSensorImplProvider = DoubleCheck.provider(GestureSensorImpl_Factory.create(this.provideContextProvider, provider4));
        this.powerManagerWrapperProvider = DoubleCheck.provider(PowerManagerWrapper_Factory.create(this.provideContextProvider));
        Provider<MetricsLogger> provider5 = DoubleCheck.provider(ColumbusModule_ProvideColumbusLoggerFactory.create());
        this.provideColumbusLoggerProvider = provider5;
        Provider<ColumbusService> provider6 = DoubleCheck.provider(ColumbusService_Factory.create(this.provideColumbusActionsProvider, this.namedSetOfFeedbackEffectProvider2, this.namedSetOfGateProvider, this.gestureSensorImplProvider, this.powerManagerWrapperProvider, provider5));
        this.columbusServiceProvider = provider6;
        DoubleCheck.provider(ColumbusServiceWrapper_Factory.create(provider6, this.deviceConfigHelperProvider, this.provideMainHandlerProvider));
        this.flashlightControllerImplProvider = DoubleCheck.provider(FlashlightControllerImpl_Factory.create(this.provideContextProvider));
        this.provideNightDisplayListenerProvider = DoubleCheck.provider(DependencyProvider_ProvideNightDisplayListenerFactory.create(builder.dependencyProvider, this.provideContextProvider, this.provideBgHandlerProvider));
        this.managedProfileControllerImplProvider = DoubleCheck.provider(ManagedProfileControllerImpl_Factory.create(this.provideContextProvider, this.broadcastDispatcherProvider));
        this.securityControllerImplProvider = DoubleCheck.provider(SecurityControllerImpl_Factory.create(this.provideContextProvider, this.provideBgHandlerProvider, this.broadcastDispatcherProvider, this.provideBackgroundExecutorProvider));
        this.statusBarWindowControllerProvider = DoubleCheck.provider(StatusBarWindowController_Factory.create(this.provideContextProvider, this.provideWindowManagerProvider, this.superStatusBarViewFactoryProvider, this.provideResourcesProvider));
        this.fragmentServiceProvider = DoubleCheck.provider(FragmentService_Factory.create(this.systemUIGoogleRootComponentProvider, this.provideConfigurationControllerProvider));
        this.accessibilityManagerWrapperProvider = DoubleCheck.provider(AccessibilityManagerWrapper_Factory.create(this.provideContextProvider));
        this.tunablePaddingServiceProvider = DoubleCheck.provider(TunablePadding_TunablePaddingService_Factory.create(this.tunerServiceImplProvider));
        this.uiOffloadThreadProvider = DoubleCheck.provider(UiOffloadThread_Factory.create());
        this.powerNotificationWarningsProvider = DoubleCheck.provider(PowerNotificationWarnings_Factory.create(this.provideContextProvider, this.activityStarterDelegateProvider));
        this.provideNotificationBlockingHelperManagerProvider = DoubleCheck.provider(C1226x481a0301.create(this.provideContextProvider, this.provideNotificationGutsManagerProvider, this.provideNotificationEntryManagerProvider, this.provideMetricsLoggerProvider));
        this.provideSensorPrivacyManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideSensorPrivacyManagerFactory.create(this.provideContextProvider));
        this.foregroundServiceNotificationListenerProvider = DoubleCheck.provider(ForegroundServiceNotificationListener_Factory.create(this.provideContextProvider, this.foregroundServiceControllerProvider, this.provideNotificationEntryManagerProvider, this.notifPipelineProvider));
        this.clockManagerProvider = DoubleCheck.provider(ClockManager_Factory.create(this.provideContextProvider, this.injectionInflationControllerProvider, this.providePluginManagerProvider, this.sysuiColorExtractorProvider, this.provideDockManagerProvider, this.broadcastDispatcherProvider));
        this.provideDevicePolicyManagerWrapperProvider = DoubleCheck.provider(DependencyProvider_ProvideDevicePolicyManagerWrapperFactory.create(builder.dependencyProvider));
        this.channelEditorDialogControllerProvider = DoubleCheck.provider(ChannelEditorDialogController_Factory.create(this.provideContextProvider, this.provideINotificationManagerProvider));
        this.keyguardSecurityModelProvider = DoubleCheck.provider(KeyguardSecurityModel_Factory.create(this.provideContextProvider));
        DelegateFactory delegateFactory = new DelegateFactory();
        this.qSTileHostProvider = delegateFactory;
        this.wifiTileProvider = WifiTile_Factory.create(delegateFactory, this.networkControllerImplProvider, this.activityStarterDelegateProvider);
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
        this.batterySaverTileProvider = BatterySaverTile_Factory.create(this.qSTileHostProvider, this.batteryControllerImplGoogleProvider);
        this.dataSaverTileProvider = DataSaverTile_Factory.create(this.qSTileHostProvider, this.networkControllerImplProvider);
        this.nightDisplayTileProvider = NightDisplayTile_Factory.create(this.qSTileHostProvider);
        this.nfcTileProvider = NfcTile_Factory.create(this.qSTileHostProvider, this.broadcastDispatcherProvider);
        this.memoryTileProvider = GarbageMonitor_MemoryTile_Factory.create(this.qSTileHostProvider, this.garbageMonitorProvider, this.activityStarterDelegateProvider);
        this.uiModeNightTileProvider = UiModeNightTile_Factory.create(this.qSTileHostProvider, this.provideConfigurationControllerProvider, this.batteryControllerImplGoogleProvider);
        this.screenRecordTileProvider = ScreenRecordTile_Factory.create(this.qSTileHostProvider, this.recordingControllerProvider);
        BatteryShareTile_Factory create5 = BatteryShareTile_Factory.create(this.qSTileHostProvider, this.batteryControllerImplGoogleProvider);
        BatteryShareTile_Factory batteryShareTile_Factory = create5;
        this.batteryShareTileProvider = create5;
        this.qSFactoryImplGoogleProvider = DoubleCheck.provider(QSFactoryImplGoogle_Factory.create(this.qSTileHostProvider, this.wifiTileProvider, this.bluetoothTileProvider, this.cellularTileProvider, this.dndTileProvider, this.colorInversionTileProvider, this.airplaneModeTileProvider, this.workModeTileProvider, this.rotationLockTileProvider, this.flashlightTileProvider, this.locationTileProvider, this.castTileProvider, this.hotspotTileProvider, this.userTileProvider, this.batterySaverTileProvider, this.dataSaverTileProvider, this.nightDisplayTileProvider, this.nfcTileProvider, this.memoryTileProvider, this.uiModeNightTileProvider, this.screenRecordTileProvider, batteryShareTile_Factory));
        AutoAddTracker_Factory create6 = AutoAddTracker_Factory.create(this.provideContextProvider);
        this.autoAddTrackerProvider = create6;
        this.autoTileManagerProvider = AutoTileManager_Factory.create(this.provideContextProvider, create6, this.qSTileHostProvider, this.provideBgHandlerProvider, this.hotspotControllerImplProvider, this.provideDataSaverControllerProvider, this.managedProfileControllerImplProvider, this.provideNightDisplayListenerProvider, this.castControllerImplProvider);
        this.optionalOfStatusBarProvider = PresentJdkOptionalInstanceProvider.m121of(this.provideStatusBarProvider);
        Provider<LogBuffer> provider7 = DoubleCheck.provider(LogModule_ProvideQuickSettingsLogBufferFactory.create(this.provideLogcatEchoTrackerProvider, this.dumpManagerProvider));
        this.provideQuickSettingsLogBufferProvider = provider7;
        QSLogger_Factory create7 = QSLogger_Factory.create(provider7);
        this.qSLoggerProvider = create7;
        DelegateFactory delegateFactory2 = (DelegateFactory) this.qSTileHostProvider;
        Provider<QSTileHost> provider8 = DoubleCheck.provider(QSTileHost_Factory.create(this.provideContextProvider, this.statusBarIconControllerImplProvider, this.qSFactoryImplGoogleProvider, this.provideMainHandlerProvider, this.provideBgLooperProvider, this.providePluginManagerProvider, this.tunerServiceImplProvider, this.autoTileManagerProvider, this.dumpManagerProvider, this.broadcastDispatcherProvider, this.optionalOfStatusBarProvider, create7));
        this.qSTileHostProvider = provider8;
        delegateFactory2.setDelegatedProvider(provider8);
        this.contextHolder = builder.contextHolder;
        Provider<PackageManager> provider9 = DoubleCheck.provider(SystemServicesModule_ProvidePackageManagerFactory.create(this.provideContextProvider));
        this.providePackageManagerProvider = provider9;
        Provider<PeopleHubDataSourceImpl> provider10 = DoubleCheck.provider(PeopleHubDataSourceImpl_Factory.create(this.provideNotificationEntryManagerProvider, this.notificationPersonExtractorPluginBoundaryProvider, this.provideUserManagerProvider, this.provideLauncherAppsProvider, provider9, this.provideContextProvider, this.provideNotificationListenerProvider, this.provideBackgroundExecutorProvider, this.provideMainExecutorProvider, this.notificationLockscreenUserManagerGoogleProvider, this.peopleNotificationIdentifierImplProvider));
        this.peopleHubDataSourceImplProvider = provider10;
        Provider<PeopleHubViewModelFactoryDataSourceImpl> provider11 = DoubleCheck.provider(PeopleHubViewModelFactoryDataSourceImpl_Factory.create(this.activityStarterDelegateProvider, provider10));
        this.peopleHubViewModelFactoryDataSourceImplProvider = provider11;
        this.peopleHubViewAdapterImplProvider = DoubleCheck.provider(PeopleHubViewAdapterImpl_Factory.create(provider11));
        this.provideUiEventLoggerProvider = DoubleCheck.provider(NotificationsModule_ProvideUiEventLoggerFactory.create());
        this.provideLatencyTrackerProvider = DoubleCheck.provider(SystemServicesModule_ProvideLatencyTrackerFactory.create(this.provideContextProvider));
        this.provideActivityManagerProvider = DoubleCheck.provider(SystemServicesModule_ProvideActivityManagerFactory.create(this.provideContextProvider));
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

    public void inject(KeyguardSliceProviderGoogle keyguardSliceProviderGoogle) {
        injectKeyguardSliceProviderGoogle(keyguardSliceProviderGoogle);
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

    private KeyguardSliceProviderGoogle injectKeyguardSliceProviderGoogle(KeyguardSliceProviderGoogle keyguardSliceProviderGoogle) {
        KeyguardSliceProvider_MembersInjector.injectMDozeParameters(keyguardSliceProviderGoogle, (DozeParameters) this.dozeParametersProvider.get());
        KeyguardSliceProvider_MembersInjector.injectMZenModeController(keyguardSliceProviderGoogle, (ZenModeController) this.zenModeControllerImplProvider.get());
        KeyguardSliceProvider_MembersInjector.injectMNextAlarmController(keyguardSliceProviderGoogle, (NextAlarmController) this.nextAlarmControllerImplProvider.get());
        KeyguardSliceProvider_MembersInjector.injectMAlarmManager(keyguardSliceProviderGoogle, (AlarmManager) this.provideAlarmManagerProvider.get());
        KeyguardSliceProvider_MembersInjector.injectMContentResolver(keyguardSliceProviderGoogle, (ContentResolver) this.provideContentResolverProvider.get());
        KeyguardSliceProvider_MembersInjector.injectMMediaManager(keyguardSliceProviderGoogle, (NotificationMediaManager) this.provideNotificationMediaManagerProvider.get());
        KeyguardSliceProvider_MembersInjector.injectMStatusBarStateController(keyguardSliceProviderGoogle, (StatusBarStateController) this.statusBarStateControllerImplProvider.get());
        KeyguardSliceProvider_MembersInjector.injectMKeyguardBypassController(keyguardSliceProviderGoogle, (KeyguardBypassController) this.keyguardBypassControllerProvider.get());
        KeyguardSliceProviderGoogle_MembersInjector.injectMSmartSpaceController(keyguardSliceProviderGoogle, (SmartSpaceController) this.smartSpaceControllerProvider.get());
        return keyguardSliceProviderGoogle;
    }

    private static <T> Provider<Optional<T>> absentJdkOptionalProvider() {
        return ABSENT_JDK_OPTIONAL_PROVIDER;
    }
}
