package com.android.systemui;

import android.app.AlarmManager;
import android.app.INotificationManager;
import android.app.IWallpaperManager;
import android.hardware.SensorPrivacyManager;
import android.hardware.display.NightDisplayListener;
import android.os.Handler;
import android.os.Looper;
import android.util.ArrayMap;
import android.util.DisplayMetrics;
import android.view.IWindowManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.util.Preconditions;
import com.android.keyguard.KeyguardSecurityModel;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.clock.ClockManager;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.systemui.appops.AppOpsController;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.bubbles.BubbleController;
import com.android.systemui.colorextraction.SysuiColorExtractor;
import com.android.systemui.dock.DockManager;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.fragments.FragmentService;
import com.android.systemui.keyguard.ScreenLifecycle;
import com.android.systemui.keyguard.WakefulnessLifecycle;
import com.android.systemui.model.SysUiState;
import com.android.systemui.p010wm.DisplayController;
import com.android.systemui.p010wm.DisplayImeController;
import com.android.systemui.p010wm.SystemWindows;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.DarkIconDispatcher;
import com.android.systemui.plugins.PluginDependencyProvider;
import com.android.systemui.plugins.VolumeDialogController;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.power.EnhancedEstimates;
import com.android.systemui.power.PowerUI.WarningsUI;
import com.android.systemui.recents.OverviewProxyService;
import com.android.systemui.recents.Recents;
import com.android.systemui.screenrecord.RecordingController;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.DevicePolicyManagerWrapper;
import com.android.systemui.shared.system.PackageManagerWrapper;
import com.android.systemui.stackdivider.Divider;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.NavigationBarController;
import com.android.systemui.statusbar.NotificationListener;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.NotificationRemoteInputManager.Callback;
import com.android.systemui.statusbar.NotificationViewHierarchyManager;
import com.android.systemui.statusbar.SmartReplyController;
import com.android.systemui.statusbar.VibratorHelper;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.NotificationEntryManager.KeyguardEnvironment;
import com.android.systemui.statusbar.notification.NotificationFilter;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import com.android.systemui.statusbar.notification.interruption.NotificationAlertingManager;
import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import com.android.systemui.statusbar.notification.row.ChannelEditorDialogController;
import com.android.systemui.statusbar.notification.row.NotificationBlockingHelperManager;
import com.android.systemui.statusbar.notification.row.NotificationGutsManager;
import com.android.systemui.statusbar.phone.AutoHideController;
import com.android.systemui.statusbar.phone.DozeParameters;
import com.android.systemui.statusbar.phone.KeyguardDismissUtil;
import com.android.systemui.statusbar.phone.LightBarController;
import com.android.systemui.statusbar.phone.LockscreenGestureLogger;
import com.android.systemui.statusbar.phone.ManagedProfileController;
import com.android.systemui.statusbar.phone.NavigationModeController;
import com.android.systemui.statusbar.phone.NotificationGroupAlertTransferHelper;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.phone.NotificationShadeWindowController;
import com.android.systemui.statusbar.phone.ShadeController;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.phone.StatusBarIconController;
import com.android.systemui.statusbar.phone.StatusBarWindowController;
import com.android.systemui.statusbar.policy.AccessibilityController;
import com.android.systemui.statusbar.policy.AccessibilityManagerWrapper;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.statusbar.policy.BluetoothController;
import com.android.systemui.statusbar.policy.CastController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.DataSaverController;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.policy.ExtensionController;
import com.android.systemui.statusbar.policy.FlashlightController;
import com.android.systemui.statusbar.policy.HotspotController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.statusbar.policy.LocationController;
import com.android.systemui.statusbar.policy.NetworkController;
import com.android.systemui.statusbar.policy.NextAlarmController;
import com.android.systemui.statusbar.policy.RemoteInputQuickSettingsDisabler;
import com.android.systemui.statusbar.policy.RotationLockController;
import com.android.systemui.statusbar.policy.SecurityController;
import com.android.systemui.statusbar.policy.SensorPrivacyController;
import com.android.systemui.statusbar.policy.SmartReplyConstants;
import com.android.systemui.statusbar.policy.UserInfoController;
import com.android.systemui.statusbar.policy.UserSwitcherController;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.tracing.ProtoTracer;
import com.android.systemui.tuner.TunablePadding.TunablePaddingService;
import com.android.systemui.tuner.TunerService;
import com.android.systemui.util.leak.GarbageMonitor;
import com.android.systemui.util.leak.LeakDetector;
import com.android.systemui.util.leak.LeakReporter;
import com.android.systemui.util.sensors.AsyncSensorManager;
import dagger.Lazy;
import java.util.Objects;
import java.util.function.Consumer;

public class Dependency {
    public static final DependencyKey<Looper> BG_LOOPER = new DependencyKey<>("background_looper");
    public static final DependencyKey<String> LEAK_REPORT_EMAIL = new DependencyKey<>("leak_report_email");
    public static final DependencyKey<Handler> MAIN_HANDLER = new DependencyKey<>("main_handler");
    public static final DependencyKey<Looper> MAIN_LOOPER = new DependencyKey<>("main_looper");
    public static final DependencyKey<Handler> TIME_TICK_HANDLER = new DependencyKey<>("time_tick_handler");
    private static Dependency sDependency;
    Lazy<AccessibilityController> mAccessibilityController;
    Lazy<AccessibilityManagerWrapper> mAccessibilityManagerWrapper;
    Lazy<ActivityManagerWrapper> mActivityManagerWrapper;
    Lazy<ActivityStarter> mActivityStarter;
    Lazy<AlarmManager> mAlarmManager;
    Lazy<AppOpsController> mAppOpsController;
    Lazy<AssistManager> mAssistManager;
    Lazy<AsyncSensorManager> mAsyncSensorManager;
    Lazy<AutoHideController> mAutoHideController;
    Lazy<BatteryController> mBatteryController;
    Lazy<Handler> mBgHandler;
    Lazy<Looper> mBgLooper;
    Lazy<BluetoothController> mBluetoothController;
    Lazy<BroadcastDispatcher> mBroadcastDispatcher;
    Lazy<BubbleController> mBubbleController;
    Lazy<CastController> mCastController;
    Lazy<ChannelEditorDialogController> mChannelEditorDialogController;
    Lazy<ClockManager> mClockManager;
    Lazy<CommandQueue> mCommandQueue;
    Lazy<ConfigurationController> mConfigurationController;
    Lazy<DarkIconDispatcher> mDarkIconDispatcher;
    Lazy<DataSaverController> mDataSaverController;
    private final ArrayMap<Object, Object> mDependencies = new ArrayMap<>();
    Lazy<DevicePolicyManagerWrapper> mDevicePolicyManagerWrapper;
    Lazy<DeviceProvisionedController> mDeviceProvisionedController;
    Lazy<DisplayController> mDisplayController;
    Lazy<DisplayImeController> mDisplayImeController;
    Lazy<DisplayMetrics> mDisplayMetrics;
    Lazy<Divider> mDivider;
    Lazy<DockManager> mDockManager;
    Lazy<DozeParameters> mDozeParameters;
    DumpManager mDumpManager;
    Lazy<EnhancedEstimates> mEnhancedEstimates;
    Lazy<ExtensionController> mExtensionController;
    Lazy<FlashlightController> mFlashlightController;
    Lazy<ForegroundServiceController> mForegroundServiceController;
    Lazy<ForegroundServiceNotificationListener> mForegroundServiceNotificationListener;
    Lazy<FragmentService> mFragmentService;
    Lazy<GarbageMonitor> mGarbageMonitor;
    Lazy<HotspotController> mHotspotController;
    Lazy<INotificationManager> mINotificationManager;
    Lazy<IStatusBarService> mIStatusBarService;
    Lazy<IWindowManager> mIWindowManager;
    Lazy<KeyguardDismissUtil> mKeyguardDismissUtil;
    Lazy<KeyguardEnvironment> mKeyguardEnvironment;
    Lazy<KeyguardStateController> mKeyguardMonitor;
    Lazy<KeyguardSecurityModel> mKeyguardSecurityModel;
    Lazy<KeyguardUpdateMonitor> mKeyguardUpdateMonitor;
    Lazy<LeakDetector> mLeakDetector;
    Lazy<String> mLeakReportEmail;
    Lazy<LeakReporter> mLeakReporter;
    Lazy<LightBarController> mLightBarController;
    Lazy<LocalBluetoothManager> mLocalBluetoothManager;
    Lazy<LocationController> mLocationController;
    Lazy<LockscreenGestureLogger> mLockscreenGestureLogger;
    Lazy<Handler> mMainHandler;
    Lazy<Looper> mMainLooper;
    Lazy<ManagedProfileController> mManagedProfileController;
    Lazy<MetricsLogger> mMetricsLogger;
    Lazy<NavigationModeController> mNavBarModeController;
    Lazy<NavigationBarController> mNavigationBarController;
    Lazy<NetworkController> mNetworkController;
    Lazy<NextAlarmController> mNextAlarmController;
    Lazy<NightDisplayListener> mNightDisplayListener;
    Lazy<NotificationAlertingManager> mNotificationAlertingManager;
    Lazy<NotificationBlockingHelperManager> mNotificationBlockingHelperManager;
    Lazy<NotificationEntryManager> mNotificationEntryManager;
    Lazy<NotificationFilter> mNotificationFilter;
    Lazy<NotificationGroupAlertTransferHelper> mNotificationGroupAlertTransferHelper;
    Lazy<NotificationGroupManager> mNotificationGroupManager;
    Lazy<NotificationGutsManager> mNotificationGutsManager;
    Lazy<NotificationListener> mNotificationListener;
    Lazy<NotificationLockscreenUserManager> mNotificationLockscreenUserManager;
    Lazy<NotificationLogger> mNotificationLogger;
    Lazy<NotificationMediaManager> mNotificationMediaManager;
    Lazy<NotificationRemoteInputManager> mNotificationRemoteInputManager;
    Lazy<Callback> mNotificationRemoteInputManagerCallback;
    Lazy<NotificationShadeWindowController> mNotificationShadeWindowController;
    Lazy<NotificationViewHierarchyManager> mNotificationViewHierarchyManager;
    Lazy<OverviewProxyService> mOverviewProxyService;
    Lazy<PackageManagerWrapper> mPackageManagerWrapper;
    Lazy<PluginDependencyProvider> mPluginDependencyProvider;
    Lazy<PluginManager> mPluginManager;
    Lazy<ProtoTracer> mProtoTracer;
    private final ArrayMap<Object, LazyDependencyCreator> mProviders = new ArrayMap<>();
    Lazy<Recents> mRecents;
    Lazy<RecordingController> mRecordingController;
    Lazy<RemoteInputQuickSettingsDisabler> mRemoteInputQuickSettingsDisabler;
    Lazy<RotationLockController> mRotationLockController;
    Lazy<ScreenLifecycle> mScreenLifecycle;
    Lazy<SecurityController> mSecurityController;
    Lazy<SensorPrivacyController> mSensorPrivacyController;
    Lazy<SensorPrivacyManager> mSensorPrivacyManager;
    Lazy<ShadeController> mShadeController;
    Lazy<SmartReplyConstants> mSmartReplyConstants;
    Lazy<SmartReplyController> mSmartReplyController;
    Lazy<StatusBar> mStatusBar;
    Lazy<StatusBarIconController> mStatusBarIconController;
    Lazy<StatusBarStateController> mStatusBarStateController;
    Lazy<SysUiState> mSysUiStateFlagsContainer;
    Lazy<SystemWindows> mSystemWindows;
    Lazy<SysuiColorExtractor> mSysuiColorExtractor;
    Lazy<StatusBarWindowController> mTempStatusBarWindowController;
    Lazy<Handler> mTimeTickHandler;
    Lazy<TunablePaddingService> mTunablePaddingService;
    Lazy<TunerService> mTunerService;
    Lazy<UiOffloadThread> mUiOffloadThread;
    Lazy<UserInfoController> mUserInfoController;
    Lazy<UserSwitcherController> mUserSwitcherController;
    Lazy<VibratorHelper> mVibratorHelper;
    Lazy<VisualStabilityManager> mVisualStabilityManager;
    Lazy<VolumeDialogController> mVolumeDialogController;
    Lazy<WakefulnessLifecycle> mWakefulnessLifecycle;
    Lazy<IWallpaperManager> mWallpaperManager;
    Lazy<WarningsUI> mWarningsUI;
    Lazy<ZenModeController> mZenModeController;

    public interface DependencyInjector {
        void createSystemUI(Dependency dependency);
    }

    public static final class DependencyKey<V> {
        private final String mDisplayName;

        public DependencyKey(String str) {
            this.mDisplayName = str;
        }

        public String toString() {
            return this.mDisplayName;
        }
    }

    private interface LazyDependencyCreator<T> {
        T createDependency();
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public boolean autoRegisterModulesForDump() {
        return true;
    }

    /* access modifiers changed from: protected */
    public void start() {
        ArrayMap<Object, LazyDependencyCreator> arrayMap = this.mProviders;
        DependencyKey<Handler> dependencyKey = TIME_TICK_HANDLER;
        Lazy<Handler> lazy = this.mTimeTickHandler;
        Objects.requireNonNull(lazy);
        arrayMap.put(dependencyKey, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap2 = this.mProviders;
        DependencyKey<Looper> dependencyKey2 = BG_LOOPER;
        Lazy<Looper> lazy2 = this.mBgLooper;
        Objects.requireNonNull(lazy2);
        arrayMap2.put(dependencyKey2, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap3 = this.mProviders;
        DependencyKey<Looper> dependencyKey3 = MAIN_LOOPER;
        Lazy<Looper> lazy3 = this.mMainLooper;
        Objects.requireNonNull(lazy3);
        arrayMap3.put(dependencyKey3, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap4 = this.mProviders;
        DependencyKey<Handler> dependencyKey4 = MAIN_HANDLER;
        Lazy<Handler> lazy4 = this.mMainHandler;
        Objects.requireNonNull(lazy4);
        arrayMap4.put(dependencyKey4, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<ActivityStarter> lazy5 = this.mActivityStarter;
        Objects.requireNonNull(lazy5);
        this.mProviders.put(ActivityStarter.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<BroadcastDispatcher> lazy6 = this.mBroadcastDispatcher;
        Objects.requireNonNull(lazy6);
        this.mProviders.put(BroadcastDispatcher.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<AsyncSensorManager> lazy7 = this.mAsyncSensorManager;
        Objects.requireNonNull(lazy7);
        this.mProviders.put(AsyncSensorManager.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<BluetoothController> lazy8 = this.mBluetoothController;
        Objects.requireNonNull(lazy8);
        this.mProviders.put(BluetoothController.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<SensorPrivacyManager> lazy9 = this.mSensorPrivacyManager;
        Objects.requireNonNull(lazy9);
        this.mProviders.put(SensorPrivacyManager.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<LocationController> lazy10 = this.mLocationController;
        Objects.requireNonNull(lazy10);
        this.mProviders.put(LocationController.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<RotationLockController> lazy11 = this.mRotationLockController;
        Objects.requireNonNull(lazy11);
        this.mProviders.put(RotationLockController.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<NetworkController> lazy12 = this.mNetworkController;
        Objects.requireNonNull(lazy12);
        this.mProviders.put(NetworkController.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<ZenModeController> lazy13 = this.mZenModeController;
        Objects.requireNonNull(lazy13);
        this.mProviders.put(ZenModeController.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<HotspotController> lazy14 = this.mHotspotController;
        Objects.requireNonNull(lazy14);
        this.mProviders.put(HotspotController.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<CastController> lazy15 = this.mCastController;
        Objects.requireNonNull(lazy15);
        this.mProviders.put(CastController.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<FlashlightController> lazy16 = this.mFlashlightController;
        Objects.requireNonNull(lazy16);
        this.mProviders.put(FlashlightController.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<KeyguardStateController> lazy17 = this.mKeyguardMonitor;
        Objects.requireNonNull(lazy17);
        this.mProviders.put(KeyguardStateController.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<KeyguardUpdateMonitor> lazy18 = this.mKeyguardUpdateMonitor;
        Objects.requireNonNull(lazy18);
        this.mProviders.put(KeyguardUpdateMonitor.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<UserSwitcherController> lazy19 = this.mUserSwitcherController;
        Objects.requireNonNull(lazy19);
        this.mProviders.put(UserSwitcherController.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<UserInfoController> lazy20 = this.mUserInfoController;
        Objects.requireNonNull(lazy20);
        this.mProviders.put(UserInfoController.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<BatteryController> lazy21 = this.mBatteryController;
        Objects.requireNonNull(lazy21);
        this.mProviders.put(BatteryController.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<NightDisplayListener> lazy22 = this.mNightDisplayListener;
        Objects.requireNonNull(lazy22);
        this.mProviders.put(NightDisplayListener.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<ManagedProfileController> lazy23 = this.mManagedProfileController;
        Objects.requireNonNull(lazy23);
        this.mProviders.put(ManagedProfileController.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<NextAlarmController> lazy24 = this.mNextAlarmController;
        Objects.requireNonNull(lazy24);
        this.mProviders.put(NextAlarmController.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<DataSaverController> lazy25 = this.mDataSaverController;
        Objects.requireNonNull(lazy25);
        this.mProviders.put(DataSaverController.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<AccessibilityController> lazy26 = this.mAccessibilityController;
        Objects.requireNonNull(lazy26);
        this.mProviders.put(AccessibilityController.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<DeviceProvisionedController> lazy27 = this.mDeviceProvisionedController;
        Objects.requireNonNull(lazy27);
        this.mProviders.put(DeviceProvisionedController.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<PluginManager> lazy28 = this.mPluginManager;
        Objects.requireNonNull(lazy28);
        this.mProviders.put(PluginManager.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<AssistManager> lazy29 = this.mAssistManager;
        Objects.requireNonNull(lazy29);
        this.mProviders.put(AssistManager.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<SecurityController> lazy30 = this.mSecurityController;
        Objects.requireNonNull(lazy30);
        this.mProviders.put(SecurityController.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<LeakDetector> lazy31 = this.mLeakDetector;
        Objects.requireNonNull(lazy31);
        this.mProviders.put(LeakDetector.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        ArrayMap<Object, LazyDependencyCreator> arrayMap5 = this.mProviders;
        DependencyKey<String> dependencyKey5 = LEAK_REPORT_EMAIL;
        Lazy<String> lazy32 = this.mLeakReportEmail;
        Objects.requireNonNull(lazy32);
        arrayMap5.put(dependencyKey5, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<LeakReporter> lazy33 = this.mLeakReporter;
        Objects.requireNonNull(lazy33);
        this.mProviders.put(LeakReporter.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<GarbageMonitor> lazy34 = this.mGarbageMonitor;
        Objects.requireNonNull(lazy34);
        this.mProviders.put(GarbageMonitor.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<TunerService> lazy35 = this.mTunerService;
        Objects.requireNonNull(lazy35);
        this.mProviders.put(TunerService.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<NotificationShadeWindowController> lazy36 = this.mNotificationShadeWindowController;
        Objects.requireNonNull(lazy36);
        this.mProviders.put(NotificationShadeWindowController.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<StatusBarWindowController> lazy37 = this.mTempStatusBarWindowController;
        Objects.requireNonNull(lazy37);
        this.mProviders.put(StatusBarWindowController.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<DarkIconDispatcher> lazy38 = this.mDarkIconDispatcher;
        Objects.requireNonNull(lazy38);
        this.mProviders.put(DarkIconDispatcher.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<ConfigurationController> lazy39 = this.mConfigurationController;
        Objects.requireNonNull(lazy39);
        this.mProviders.put(ConfigurationController.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<StatusBarIconController> lazy40 = this.mStatusBarIconController;
        Objects.requireNonNull(lazy40);
        this.mProviders.put(StatusBarIconController.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<ScreenLifecycle> lazy41 = this.mScreenLifecycle;
        Objects.requireNonNull(lazy41);
        this.mProviders.put(ScreenLifecycle.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<WakefulnessLifecycle> lazy42 = this.mWakefulnessLifecycle;
        Objects.requireNonNull(lazy42);
        this.mProviders.put(WakefulnessLifecycle.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<FragmentService> lazy43 = this.mFragmentService;
        Objects.requireNonNull(lazy43);
        this.mProviders.put(FragmentService.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<ExtensionController> lazy44 = this.mExtensionController;
        Objects.requireNonNull(lazy44);
        this.mProviders.put(ExtensionController.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<PluginDependencyProvider> lazy45 = this.mPluginDependencyProvider;
        Objects.requireNonNull(lazy45);
        this.mProviders.put(PluginDependencyProvider.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<LocalBluetoothManager> lazy46 = this.mLocalBluetoothManager;
        Objects.requireNonNull(lazy46);
        this.mProviders.put(LocalBluetoothManager.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<VolumeDialogController> lazy47 = this.mVolumeDialogController;
        Objects.requireNonNull(lazy47);
        this.mProviders.put(VolumeDialogController.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<MetricsLogger> lazy48 = this.mMetricsLogger;
        Objects.requireNonNull(lazy48);
        this.mProviders.put(MetricsLogger.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<AccessibilityManagerWrapper> lazy49 = this.mAccessibilityManagerWrapper;
        Objects.requireNonNull(lazy49);
        this.mProviders.put(AccessibilityManagerWrapper.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<SysuiColorExtractor> lazy50 = this.mSysuiColorExtractor;
        Objects.requireNonNull(lazy50);
        this.mProviders.put(SysuiColorExtractor.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<TunablePaddingService> lazy51 = this.mTunablePaddingService;
        Objects.requireNonNull(lazy51);
        this.mProviders.put(TunablePaddingService.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<ForegroundServiceController> lazy52 = this.mForegroundServiceController;
        Objects.requireNonNull(lazy52);
        this.mProviders.put(ForegroundServiceController.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<UiOffloadThread> lazy53 = this.mUiOffloadThread;
        Objects.requireNonNull(lazy53);
        this.mProviders.put(UiOffloadThread.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<WarningsUI> lazy54 = this.mWarningsUI;
        Objects.requireNonNull(lazy54);
        this.mProviders.put(WarningsUI.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<LightBarController> lazy55 = this.mLightBarController;
        Objects.requireNonNull(lazy55);
        this.mProviders.put(LightBarController.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<IWindowManager> lazy56 = this.mIWindowManager;
        Objects.requireNonNull(lazy56);
        this.mProviders.put(IWindowManager.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<OverviewProxyService> lazy57 = this.mOverviewProxyService;
        Objects.requireNonNull(lazy57);
        this.mProviders.put(OverviewProxyService.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<NavigationModeController> lazy58 = this.mNavBarModeController;
        Objects.requireNonNull(lazy58);
        this.mProviders.put(NavigationModeController.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<EnhancedEstimates> lazy59 = this.mEnhancedEstimates;
        Objects.requireNonNull(lazy59);
        this.mProviders.put(EnhancedEstimates.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<VibratorHelper> lazy60 = this.mVibratorHelper;
        Objects.requireNonNull(lazy60);
        this.mProviders.put(VibratorHelper.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<IStatusBarService> lazy61 = this.mIStatusBarService;
        Objects.requireNonNull(lazy61);
        this.mProviders.put(IStatusBarService.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<DisplayMetrics> lazy62 = this.mDisplayMetrics;
        Objects.requireNonNull(lazy62);
        this.mProviders.put(DisplayMetrics.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<LockscreenGestureLogger> lazy63 = this.mLockscreenGestureLogger;
        Objects.requireNonNull(lazy63);
        this.mProviders.put(LockscreenGestureLogger.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<KeyguardEnvironment> lazy64 = this.mKeyguardEnvironment;
        Objects.requireNonNull(lazy64);
        this.mProviders.put(KeyguardEnvironment.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<ShadeController> lazy65 = this.mShadeController;
        Objects.requireNonNull(lazy65);
        this.mProviders.put(ShadeController.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<Callback> lazy66 = this.mNotificationRemoteInputManagerCallback;
        Objects.requireNonNull(lazy66);
        this.mProviders.put(Callback.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<AppOpsController> lazy67 = this.mAppOpsController;
        Objects.requireNonNull(lazy67);
        this.mProviders.put(AppOpsController.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<NavigationBarController> lazy68 = this.mNavigationBarController;
        Objects.requireNonNull(lazy68);
        this.mProviders.put(NavigationBarController.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<StatusBarStateController> lazy69 = this.mStatusBarStateController;
        Objects.requireNonNull(lazy69);
        this.mProviders.put(StatusBarStateController.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<NotificationLockscreenUserManager> lazy70 = this.mNotificationLockscreenUserManager;
        Objects.requireNonNull(lazy70);
        this.mProviders.put(NotificationLockscreenUserManager.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<VisualStabilityManager> lazy71 = this.mVisualStabilityManager;
        Objects.requireNonNull(lazy71);
        this.mProviders.put(VisualStabilityManager.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<NotificationGroupManager> lazy72 = this.mNotificationGroupManager;
        Objects.requireNonNull(lazy72);
        this.mProviders.put(NotificationGroupManager.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<NotificationGroupAlertTransferHelper> lazy73 = this.mNotificationGroupAlertTransferHelper;
        Objects.requireNonNull(lazy73);
        this.mProviders.put(NotificationGroupAlertTransferHelper.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<NotificationMediaManager> lazy74 = this.mNotificationMediaManager;
        Objects.requireNonNull(lazy74);
        this.mProviders.put(NotificationMediaManager.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<NotificationGutsManager> lazy75 = this.mNotificationGutsManager;
        Objects.requireNonNull(lazy75);
        this.mProviders.put(NotificationGutsManager.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<NotificationBlockingHelperManager> lazy76 = this.mNotificationBlockingHelperManager;
        Objects.requireNonNull(lazy76);
        this.mProviders.put(NotificationBlockingHelperManager.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<NotificationRemoteInputManager> lazy77 = this.mNotificationRemoteInputManager;
        Objects.requireNonNull(lazy77);
        this.mProviders.put(NotificationRemoteInputManager.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<SmartReplyConstants> lazy78 = this.mSmartReplyConstants;
        Objects.requireNonNull(lazy78);
        this.mProviders.put(SmartReplyConstants.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<NotificationListener> lazy79 = this.mNotificationListener;
        Objects.requireNonNull(lazy79);
        this.mProviders.put(NotificationListener.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<NotificationLogger> lazy80 = this.mNotificationLogger;
        Objects.requireNonNull(lazy80);
        this.mProviders.put(NotificationLogger.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<NotificationViewHierarchyManager> lazy81 = this.mNotificationViewHierarchyManager;
        Objects.requireNonNull(lazy81);
        this.mProviders.put(NotificationViewHierarchyManager.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<NotificationFilter> lazy82 = this.mNotificationFilter;
        Objects.requireNonNull(lazy82);
        this.mProviders.put(NotificationFilter.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<KeyguardDismissUtil> lazy83 = this.mKeyguardDismissUtil;
        Objects.requireNonNull(lazy83);
        this.mProviders.put(KeyguardDismissUtil.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<SmartReplyController> lazy84 = this.mSmartReplyController;
        Objects.requireNonNull(lazy84);
        this.mProviders.put(SmartReplyController.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<RemoteInputQuickSettingsDisabler> lazy85 = this.mRemoteInputQuickSettingsDisabler;
        Objects.requireNonNull(lazy85);
        this.mProviders.put(RemoteInputQuickSettingsDisabler.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<BubbleController> lazy86 = this.mBubbleController;
        Objects.requireNonNull(lazy86);
        this.mProviders.put(BubbleController.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<NotificationEntryManager> lazy87 = this.mNotificationEntryManager;
        Objects.requireNonNull(lazy87);
        this.mProviders.put(NotificationEntryManager.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<NotificationAlertingManager> lazy88 = this.mNotificationAlertingManager;
        Objects.requireNonNull(lazy88);
        this.mProviders.put(NotificationAlertingManager.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<ForegroundServiceNotificationListener> lazy89 = this.mForegroundServiceNotificationListener;
        Objects.requireNonNull(lazy89);
        this.mProviders.put(ForegroundServiceNotificationListener.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<ClockManager> lazy90 = this.mClockManager;
        Objects.requireNonNull(lazy90);
        this.mProviders.put(ClockManager.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<ActivityManagerWrapper> lazy91 = this.mActivityManagerWrapper;
        Objects.requireNonNull(lazy91);
        this.mProviders.put(ActivityManagerWrapper.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<DevicePolicyManagerWrapper> lazy92 = this.mDevicePolicyManagerWrapper;
        Objects.requireNonNull(lazy92);
        this.mProviders.put(DevicePolicyManagerWrapper.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<PackageManagerWrapper> lazy93 = this.mPackageManagerWrapper;
        Objects.requireNonNull(lazy93);
        this.mProviders.put(PackageManagerWrapper.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<SensorPrivacyController> lazy94 = this.mSensorPrivacyController;
        Objects.requireNonNull(lazy94);
        this.mProviders.put(SensorPrivacyController.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<DockManager> lazy95 = this.mDockManager;
        Objects.requireNonNull(lazy95);
        this.mProviders.put(DockManager.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<ChannelEditorDialogController> lazy96 = this.mChannelEditorDialogController;
        Objects.requireNonNull(lazy96);
        this.mProviders.put(ChannelEditorDialogController.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<INotificationManager> lazy97 = this.mINotificationManager;
        Objects.requireNonNull(lazy97);
        this.mProviders.put(INotificationManager.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<SysUiState> lazy98 = this.mSysUiStateFlagsContainer;
        Objects.requireNonNull(lazy98);
        this.mProviders.put(SysUiState.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<AlarmManager> lazy99 = this.mAlarmManager;
        Objects.requireNonNull(lazy99);
        this.mProviders.put(AlarmManager.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<KeyguardSecurityModel> lazy100 = this.mKeyguardSecurityModel;
        Objects.requireNonNull(lazy100);
        this.mProviders.put(KeyguardSecurityModel.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<DozeParameters> lazy101 = this.mDozeParameters;
        Objects.requireNonNull(lazy101);
        this.mProviders.put(DozeParameters.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<IWallpaperManager> lazy102 = this.mWallpaperManager;
        Objects.requireNonNull(lazy102);
        this.mProviders.put(IWallpaperManager.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<CommandQueue> lazy103 = this.mCommandQueue;
        Objects.requireNonNull(lazy103);
        this.mProviders.put(CommandQueue.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<Recents> lazy104 = this.mRecents;
        Objects.requireNonNull(lazy104);
        this.mProviders.put(Recents.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<StatusBar> lazy105 = this.mStatusBar;
        Objects.requireNonNull(lazy105);
        this.mProviders.put(StatusBar.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<DisplayController> lazy106 = this.mDisplayController;
        Objects.requireNonNull(lazy106);
        this.mProviders.put(DisplayController.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<SystemWindows> lazy107 = this.mSystemWindows;
        Objects.requireNonNull(lazy107);
        this.mProviders.put(SystemWindows.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<DisplayImeController> lazy108 = this.mDisplayImeController;
        Objects.requireNonNull(lazy108);
        this.mProviders.put(DisplayImeController.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<ProtoTracer> lazy109 = this.mProtoTracer;
        Objects.requireNonNull(lazy109);
        this.mProviders.put(ProtoTracer.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<AutoHideController> lazy110 = this.mAutoHideController;
        Objects.requireNonNull(lazy110);
        this.mProviders.put(AutoHideController.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<RecordingController> lazy111 = this.mRecordingController;
        Objects.requireNonNull(lazy111);
        this.mProviders.put(RecordingController.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        Lazy<Divider> lazy112 = this.mDivider;
        Objects.requireNonNull(lazy112);
        this.mProviders.put(Divider.class, new LazyDependencyCreator() {
            public final Object createDependency() {
                return Lazy.this.get();
            }
        });
        sDependency = this;
    }

    /* access modifiers changed from: protected */
    public final <T> T getDependency(Class<T> cls) {
        return getDependencyInner(cls);
    }

    /* access modifiers changed from: protected */
    public final <T> T getDependency(DependencyKey<T> dependencyKey) {
        return getDependencyInner(dependencyKey);
    }

    private synchronized <T> T getDependencyInner(Object obj) {
        T t;
        t = this.mDependencies.get(obj);
        if (t == null) {
            t = createDependency(obj);
            this.mDependencies.put(obj, t);
            if (autoRegisterModulesForDump() && (t instanceof Dumpable)) {
                this.mDumpManager.registerDumpable(t.getClass().getName(), (Dumpable) t);
            }
        }
        return t;
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public <T> T createDependency(Object obj) {
        Preconditions.checkArgument((obj instanceof DependencyKey) || (obj instanceof Class));
        LazyDependencyCreator lazyDependencyCreator = (LazyDependencyCreator) this.mProviders.get(obj);
        if (lazyDependencyCreator != null) {
            return lazyDependencyCreator.createDependency();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Unsupported dependency ");
        sb.append(obj);
        sb.append(". ");
        sb.append(this.mProviders.size());
        sb.append(" providers known.");
        throw new IllegalArgumentException(sb.toString());
    }

    private <T> void destroyDependency(Class<T> cls, Consumer<T> consumer) {
        Object remove = this.mDependencies.remove(cls);
        if (remove instanceof Dumpable) {
            this.mDumpManager.unregisterDumpable(remove.getClass().getName());
        }
        if (remove != null && consumer != null) {
            consumer.accept(remove);
        }
    }

    public static <T> void destroy(Class<T> cls, Consumer<T> consumer) {
        sDependency.destroyDependency(cls, consumer);
    }

    @Deprecated
    public static <T> T get(Class<T> cls) {
        return sDependency.getDependency(cls);
    }

    @Deprecated
    public static <T> T get(DependencyKey<T> dependencyKey) {
        return sDependency.getDependency(dependencyKey);
    }
}
