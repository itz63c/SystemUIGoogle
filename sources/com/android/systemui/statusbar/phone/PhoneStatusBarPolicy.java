package com.android.systemui.statusbar.phone;

import android.app.ActivityTaskManager;
import android.app.AlarmManager;
import android.app.AlarmManager.AlarmClockInfo;
import android.app.IActivityManager;
import android.app.SynchronousUserSwitchObserver;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.Handler;
import android.os.RemoteException;
import android.os.UserManager;
import android.service.notification.ZenModeConfig;
import android.telecom.TelecomManager;
import android.text.format.DateFormat;
import android.util.Log;
import com.android.systemui.C2010R$drawable;
import com.android.systemui.C2017R$string;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.p007qs.tiles.RotationLockTile;
import com.android.systemui.screenrecord.RecordingController;
import com.android.systemui.screenrecord.RecordingController.RecordingStateChangeCallback;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.CommandQueue.Callbacks;
import com.android.systemui.statusbar.policy.BluetoothController;
import com.android.systemui.statusbar.policy.BluetoothController.Callback;
import com.android.systemui.statusbar.policy.CastController;
import com.android.systemui.statusbar.policy.DataSaverController;
import com.android.systemui.statusbar.policy.DataSaverController.Listener;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.policy.DeviceProvisionedController.DeviceProvisionedListener;
import com.android.systemui.statusbar.policy.HotspotController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.statusbar.policy.LocationController;
import com.android.systemui.statusbar.policy.LocationController.LocationChangeCallback;
import com.android.systemui.statusbar.policy.NextAlarmController;
import com.android.systemui.statusbar.policy.NextAlarmController.NextAlarmChangeCallback;
import com.android.systemui.statusbar.policy.RotationLockController;
import com.android.systemui.statusbar.policy.RotationLockController.RotationLockControllerCallback;
import com.android.systemui.statusbar.policy.SensorPrivacyController;
import com.android.systemui.statusbar.policy.SensorPrivacyController.OnSensorPrivacyChangedListener;
import com.android.systemui.statusbar.policy.UserInfoController;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.util.time.DateFormatUtil;
import java.util.Locale;
import java.util.concurrent.Executor;

public class PhoneStatusBarPolicy implements Callback, Callbacks, RotationLockControllerCallback, Listener, ZenModeController.Callback, DeviceProvisionedListener, KeyguardStateController.Callback, LocationChangeCallback, RecordingStateChangeCallback {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = Log.isLoggable("PhoneStatusBarPolicy", 3);
    private final AlarmManager mAlarmManager;
    private final AudioManager mAudioManager;
    private BluetoothController mBluetooth;
    private final BroadcastDispatcher mBroadcastDispatcher;
    private final CastController mCast;
    private final CastController.Callback mCastCallback = new CastController.Callback() {
        public void onCastDevicesChanged() {
            PhoneStatusBarPolicy.this.updateCast();
        }
    };
    private final CommandQueue mCommandQueue;
    private boolean mCurrentUserSetup;
    private final DataSaverController mDataSaver;
    private final DateFormatUtil mDateFormatUtil;
    private final int mDisplayId;
    /* access modifiers changed from: private */
    public final Handler mHandler = new Handler();
    private final HotspotController mHotspot;
    private final HotspotController.Callback mHotspotCallback = new HotspotController.Callback() {
        public void onHotspotChanged(boolean z, int i) {
            PhoneStatusBarPolicy.this.mIconController.setIconVisibility(PhoneStatusBarPolicy.this.mSlotHotspot, z);
        }
    };
    private final IActivityManager mIActivityManager;
    /* access modifiers changed from: private */
    public final StatusBarIconController mIconController;
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onReceive(android.content.Context r3, android.content.Intent r4) {
            /*
                r2 = this;
                java.lang.String r3 = r4.getAction()
                int r0 = r3.hashCode()
                r1 = 0
                switch(r0) {
                    case -1676458352: goto L_0x0053;
                    case -1238404651: goto L_0x0049;
                    case -864107122: goto L_0x003f;
                    case -229777127: goto L_0x0035;
                    case 100931828: goto L_0x002b;
                    case 1051344550: goto L_0x0021;
                    case 1051477093: goto L_0x0017;
                    case 2070024785: goto L_0x000d;
                    default: goto L_0x000c;
                }
            L_0x000c:
                goto L_0x005d
            L_0x000d:
                java.lang.String r0 = "android.media.RINGER_MODE_CHANGED"
                boolean r3 = r3.equals(r0)
                if (r3 == 0) goto L_0x005d
                r3 = r1
                goto L_0x005e
            L_0x0017:
                java.lang.String r0 = "android.intent.action.MANAGED_PROFILE_REMOVED"
                boolean r3 = r3.equals(r0)
                if (r3 == 0) goto L_0x005d
                r3 = 6
                goto L_0x005e
            L_0x0021:
                java.lang.String r0 = "android.telecom.action.CURRENT_TTY_MODE_CHANGED"
                boolean r3 = r3.equals(r0)
                if (r3 == 0) goto L_0x005d
                r3 = 3
                goto L_0x005e
            L_0x002b:
                java.lang.String r0 = "android.media.INTERNAL_RINGER_MODE_CHANGED_ACTION"
                boolean r3 = r3.equals(r0)
                if (r3 == 0) goto L_0x005d
                r3 = 1
                goto L_0x005e
            L_0x0035:
                java.lang.String r0 = "android.intent.action.SIM_STATE_CHANGED"
                boolean r3 = r3.equals(r0)
                if (r3 == 0) goto L_0x005d
                r3 = 2
                goto L_0x005e
            L_0x003f:
                java.lang.String r0 = "android.intent.action.MANAGED_PROFILE_AVAILABLE"
                boolean r3 = r3.equals(r0)
                if (r3 == 0) goto L_0x005d
                r3 = 4
                goto L_0x005e
            L_0x0049:
                java.lang.String r0 = "android.intent.action.MANAGED_PROFILE_UNAVAILABLE"
                boolean r3 = r3.equals(r0)
                if (r3 == 0) goto L_0x005d
                r3 = 5
                goto L_0x005e
            L_0x0053:
                java.lang.String r0 = "android.intent.action.HEADSET_PLUG"
                boolean r3 = r3.equals(r0)
                if (r3 == 0) goto L_0x005d
                r3 = 7
                goto L_0x005e
            L_0x005d:
                r3 = -1
            L_0x005e:
                switch(r3) {
                    case 0: goto L_0x0081;
                    case 1: goto L_0x0081;
                    case 2: goto L_0x007a;
                    case 3: goto L_0x006e;
                    case 4: goto L_0x0068;
                    case 5: goto L_0x0068;
                    case 6: goto L_0x0068;
                    case 7: goto L_0x0062;
                    default: goto L_0x0061;
                }
            L_0x0061:
                goto L_0x0086
            L_0x0062:
                com.android.systemui.statusbar.phone.PhoneStatusBarPolicy r2 = com.android.systemui.statusbar.phone.PhoneStatusBarPolicy.this
                r2.updateHeadsetPlug(r4)
                goto L_0x0086
            L_0x0068:
                com.android.systemui.statusbar.phone.PhoneStatusBarPolicy r2 = com.android.systemui.statusbar.phone.PhoneStatusBarPolicy.this
                r2.updateManagedProfile()
                goto L_0x0086
            L_0x006e:
                com.android.systemui.statusbar.phone.PhoneStatusBarPolicy r2 = com.android.systemui.statusbar.phone.PhoneStatusBarPolicy.this
                java.lang.String r3 = "android.telecom.extra.CURRENT_TTY_MODE"
                int r3 = r4.getIntExtra(r3, r1)
                r2.updateTTY(r3)
                goto L_0x0086
            L_0x007a:
                java.lang.String r2 = "rebroadcastOnUnlock"
                boolean r2 = r4.getBooleanExtra(r2, r1)
                goto L_0x0086
            L_0x0081:
                com.android.systemui.statusbar.phone.PhoneStatusBarPolicy r2 = com.android.systemui.statusbar.phone.PhoneStatusBarPolicy.this
                r2.updateVolumeZen()
            L_0x0086:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.PhoneStatusBarPolicy.C15636.onReceive(android.content.Context, android.content.Intent):void");
        }
    };
    private final KeyguardStateController mKeyguardStateController;
    private final LocationController mLocationController;
    private boolean mManagedProfileIconVisible = false;
    /* access modifiers changed from: private */
    public AlarmClockInfo mNextAlarm;
    private final NextAlarmChangeCallback mNextAlarmCallback = new NextAlarmChangeCallback() {
        public void onNextAlarmChanged(AlarmClockInfo alarmClockInfo) {
            PhoneStatusBarPolicy.this.mNextAlarm = alarmClockInfo;
            PhoneStatusBarPolicy.this.updateAlarm();
        }
    };
    private final NextAlarmController mNextAlarmController;
    private final DeviceProvisionedController mProvisionedController;
    private final RecordingController mRecordingController;
    private Runnable mRemoveCastIconRunnable = new Runnable() {
        public void run() {
            if (PhoneStatusBarPolicy.DEBUG) {
                Log.v("PhoneStatusBarPolicy", "updateCast: hiding icon NOW");
            }
            PhoneStatusBarPolicy.this.mIconController.setIconVisibility(PhoneStatusBarPolicy.this.mSlotCast, false);
        }
    };
    private final Resources mResources;
    private final RotationLockController mRotationLockController;
    private final SensorPrivacyController mSensorPrivacyController;
    private final OnSensorPrivacyChangedListener mSensorPrivacyListener = new OnSensorPrivacyChangedListener() {
        public void onSensorPrivacyChanged(boolean z) {
            PhoneStatusBarPolicy.this.mHandler.post(new Runnable(z) {
                public final /* synthetic */ boolean f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    C15625.this.lambda$onSensorPrivacyChanged$0$PhoneStatusBarPolicy$5(this.f$1);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onSensorPrivacyChanged$0 */
        public /* synthetic */ void lambda$onSensorPrivacyChanged$0$PhoneStatusBarPolicy$5(boolean z) {
            PhoneStatusBarPolicy.this.mIconController.setIconVisibility(PhoneStatusBarPolicy.this.mSlotSensorsOff, z);
        }
    };
    private final SharedPreferences mSharedPreferences;
    private final String mSlotAlarmClock;
    private final String mSlotBluetooth;
    /* access modifiers changed from: private */
    public final String mSlotCast;
    private final String mSlotDataSaver;
    private final String mSlotHeadset;
    /* access modifiers changed from: private */
    public final String mSlotHotspot;
    private final String mSlotLocation;
    private final String mSlotManagedProfile;
    private final String mSlotRotate;
    private final String mSlotScreenRecord;
    /* access modifiers changed from: private */
    public final String mSlotSensorsOff;
    private final String mSlotTty;
    private final String mSlotVolume;
    private final String mSlotZen;
    private final TelecomManager mTelecomManager;
    private final Executor mUiBgExecutor;
    /* access modifiers changed from: private */
    public final UserInfoController mUserInfoController;
    private final UserManager mUserManager;
    private final SynchronousUserSwitchObserver mUserSwitchListener = new SynchronousUserSwitchObserver() {
        /* access modifiers changed from: private */
        /* renamed from: lambda$onUserSwitching$0 */
        public /* synthetic */ void lambda$onUserSwitching$0$PhoneStatusBarPolicy$1() {
            PhoneStatusBarPolicy.this.mUserInfoController.reloadUserInfo();
        }

        public void onUserSwitching(int i) throws RemoteException {
            PhoneStatusBarPolicy.this.mHandler.post(new Runnable() {
                public final void run() {
                    C15581.this.lambda$onUserSwitching$0$PhoneStatusBarPolicy$1();
                }
            });
        }

        public void onUserSwitchComplete(int i) throws RemoteException {
            PhoneStatusBarPolicy.this.mHandler.post(new Runnable() {
                public final void run() {
                    C15581.this.lambda$onUserSwitchComplete$1$PhoneStatusBarPolicy$1();
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onUserSwitchComplete$1 */
        public /* synthetic */ void lambda$onUserSwitchComplete$1$PhoneStatusBarPolicy$1() {
            PhoneStatusBarPolicy.this.updateAlarm();
            PhoneStatusBarPolicy.this.updateManagedProfile();
        }
    };
    private boolean mVolumeVisible;
    private final ZenModeController mZenController;
    private boolean mZenVisible;

    public PhoneStatusBarPolicy(StatusBarIconController statusBarIconController, CommandQueue commandQueue, BroadcastDispatcher broadcastDispatcher, Executor executor, Resources resources, CastController castController, HotspotController hotspotController, BluetoothController bluetoothController, NextAlarmController nextAlarmController, UserInfoController userInfoController, RotationLockController rotationLockController, DataSaverController dataSaverController, ZenModeController zenModeController, DeviceProvisionedController deviceProvisionedController, KeyguardStateController keyguardStateController, LocationController locationController, SensorPrivacyController sensorPrivacyController, IActivityManager iActivityManager, AlarmManager alarmManager, UserManager userManager, AudioManager audioManager, RecordingController recordingController, TelecomManager telecomManager, int i, SharedPreferences sharedPreferences, DateFormatUtil dateFormatUtil) {
        Resources resources2 = resources;
        this.mIconController = statusBarIconController;
        this.mCommandQueue = commandQueue;
        this.mBroadcastDispatcher = broadcastDispatcher;
        this.mResources = resources2;
        this.mCast = castController;
        this.mHotspot = hotspotController;
        this.mBluetooth = bluetoothController;
        this.mNextAlarmController = nextAlarmController;
        this.mAlarmManager = alarmManager;
        this.mUserInfoController = userInfoController;
        this.mIActivityManager = iActivityManager;
        this.mUserManager = userManager;
        this.mRotationLockController = rotationLockController;
        this.mDataSaver = dataSaverController;
        this.mZenController = zenModeController;
        this.mProvisionedController = deviceProvisionedController;
        this.mKeyguardStateController = keyguardStateController;
        this.mLocationController = locationController;
        this.mSensorPrivacyController = sensorPrivacyController;
        this.mRecordingController = recordingController;
        this.mUiBgExecutor = executor;
        this.mAudioManager = audioManager;
        this.mTelecomManager = telecomManager;
        this.mSlotCast = resources.getString(17041186);
        this.mSlotHotspot = resources.getString(17041193);
        this.mSlotBluetooth = resources.getString(17041184);
        this.mSlotTty = resources.getString(17041210);
        this.mSlotZen = resources.getString(17041214);
        this.mSlotVolume = resources.getString(17041211);
        this.mSlotAlarmClock = resources.getString(17041182);
        this.mSlotManagedProfile = resources.getString(17041196);
        this.mSlotRotate = resources.getString(17041203);
        this.mSlotHeadset = resources.getString(17041192);
        this.mSlotDataSaver = resources.getString(17041190);
        this.mSlotLocation = resources.getString(17041195);
        this.mSlotSensorsOff = resources.getString(17041206);
        this.mSlotScreenRecord = resources.getString(17041204);
        this.mDisplayId = i;
        this.mSharedPreferences = sharedPreferences;
        this.mDateFormatUtil = dateFormatUtil;
    }

    /* JADX WARNING: type inference failed for: r0v21, types: [com.android.systemui.statusbar.policy.CallbackController, com.android.systemui.statusbar.policy.RotationLockController] */
    /* JADX WARNING: Multi-variable type inference failed. Error: jadx.core.utils.exceptions.JadxRuntimeException: No candidate types for var: r0v21, types: [com.android.systemui.statusbar.policy.CallbackController, com.android.systemui.statusbar.policy.RotationLockController]
      assigns: [com.android.systemui.statusbar.policy.RotationLockController]
      uses: [com.android.systemui.statusbar.policy.CallbackController]
      mth insns count: 148
    	at jadx.core.dex.visitors.typeinference.TypeSearch.fillTypeCandidates(TypeSearch.java:237)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.run(TypeSearch.java:53)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runMultiVariableSearch(TypeInferenceVisitor.java:99)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:92)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
    	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
    	at jadx.core.ProcessClass.process(ProcessClass.java:30)
    	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:311)
    	at jadx.api.JavaClass.decompile(JavaClass.java:62)
    	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:217)
     */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void init() {
        /*
            r7 = this;
            android.content.IntentFilter r0 = new android.content.IntentFilter
            r0.<init>()
            java.lang.String r1 = "android.media.RINGER_MODE_CHANGED"
            r0.addAction(r1)
            java.lang.String r1 = "android.media.INTERNAL_RINGER_MODE_CHANGED_ACTION"
            r0.addAction(r1)
            java.lang.String r1 = "android.intent.action.HEADSET_PLUG"
            r0.addAction(r1)
            java.lang.String r1 = "android.intent.action.SIM_STATE_CHANGED"
            r0.addAction(r1)
            java.lang.String r1 = "android.telecom.action.CURRENT_TTY_MODE_CHANGED"
            r0.addAction(r1)
            java.lang.String r1 = "android.intent.action.MANAGED_PROFILE_AVAILABLE"
            r0.addAction(r1)
            java.lang.String r1 = "android.intent.action.MANAGED_PROFILE_UNAVAILABLE"
            r0.addAction(r1)
            java.lang.String r1 = "android.intent.action.MANAGED_PROFILE_REMOVED"
            r0.addAction(r1)
            com.android.systemui.broadcast.BroadcastDispatcher r1 = r7.mBroadcastDispatcher
            android.content.BroadcastReceiver r2 = r7.mIntentReceiver
            android.os.Handler r3 = r7.mHandler
            r1.registerReceiverWithHandler(r2, r0, r3)
            android.app.IActivityManager r0 = r7.mIActivityManager     // Catch:{ RemoteException -> 0x003f }
            android.app.SynchronousUserSwitchObserver r1 = r7.mUserSwitchListener     // Catch:{ RemoteException -> 0x003f }
            java.lang.String r2 = "PhoneStatusBarPolicy"
            r0.registerUserSwitchObserver(r1, r2)     // Catch:{ RemoteException -> 0x003f }
        L_0x003f:
            r7.updateTTY()
            r7.updateBluetooth()
            com.android.systemui.statusbar.phone.StatusBarIconController r0 = r7.mIconController
            java.lang.String r1 = r7.mSlotAlarmClock
            int r2 = com.android.systemui.C2010R$drawable.stat_sys_alarm
            r3 = 0
            r0.setIcon(r1, r2, r3)
            com.android.systemui.statusbar.phone.StatusBarIconController r0 = r7.mIconController
            java.lang.String r1 = r7.mSlotAlarmClock
            r2 = 0
            r0.setIconVisibility(r1, r2)
            com.android.systemui.statusbar.phone.StatusBarIconController r0 = r7.mIconController
            java.lang.String r1 = r7.mSlotZen
            int r4 = com.android.systemui.C2010R$drawable.stat_sys_dnd
            r0.setIcon(r1, r4, r3)
            com.android.systemui.statusbar.phone.StatusBarIconController r0 = r7.mIconController
            java.lang.String r1 = r7.mSlotZen
            r0.setIconVisibility(r1, r2)
            com.android.systemui.statusbar.phone.StatusBarIconController r0 = r7.mIconController
            java.lang.String r1 = r7.mSlotVolume
            int r4 = com.android.systemui.C2010R$drawable.stat_sys_ringer_vibrate
            r0.setIcon(r1, r4, r3)
            com.android.systemui.statusbar.phone.StatusBarIconController r0 = r7.mIconController
            java.lang.String r1 = r7.mSlotVolume
            r0.setIconVisibility(r1, r2)
            r7.updateVolumeZen()
            com.android.systemui.statusbar.phone.StatusBarIconController r0 = r7.mIconController
            java.lang.String r1 = r7.mSlotCast
            int r4 = com.android.systemui.C2010R$drawable.stat_sys_cast
            r0.setIcon(r1, r4, r3)
            com.android.systemui.statusbar.phone.StatusBarIconController r0 = r7.mIconController
            java.lang.String r1 = r7.mSlotCast
            r0.setIconVisibility(r1, r2)
            com.android.systemui.statusbar.phone.StatusBarIconController r0 = r7.mIconController
            java.lang.String r1 = r7.mSlotHotspot
            int r4 = com.android.systemui.C2010R$drawable.stat_sys_hotspot
            android.content.res.Resources r5 = r7.mResources
            int r6 = com.android.systemui.C2017R$string.accessibility_status_bar_hotspot
            java.lang.String r5 = r5.getString(r6)
            r0.setIcon(r1, r4, r5)
            com.android.systemui.statusbar.phone.StatusBarIconController r0 = r7.mIconController
            java.lang.String r1 = r7.mSlotHotspot
            com.android.systemui.statusbar.policy.HotspotController r4 = r7.mHotspot
            boolean r4 = r4.isHotspotEnabled()
            r0.setIconVisibility(r1, r4)
            com.android.systemui.statusbar.phone.StatusBarIconController r0 = r7.mIconController
            java.lang.String r1 = r7.mSlotManagedProfile
            int r4 = com.android.systemui.C2010R$drawable.stat_sys_managed_profile_status
            android.content.res.Resources r5 = r7.mResources
            int r6 = com.android.systemui.C2017R$string.accessibility_managed_profile
            java.lang.String r5 = r5.getString(r6)
            r0.setIcon(r1, r4, r5)
            com.android.systemui.statusbar.phone.StatusBarIconController r0 = r7.mIconController
            java.lang.String r1 = r7.mSlotManagedProfile
            boolean r4 = r7.mManagedProfileIconVisible
            r0.setIconVisibility(r1, r4)
            com.android.systemui.statusbar.phone.StatusBarIconController r0 = r7.mIconController
            java.lang.String r1 = r7.mSlotDataSaver
            int r4 = com.android.systemui.C2010R$drawable.stat_sys_data_saver
            android.content.res.Resources r5 = r7.mResources
            int r6 = com.android.systemui.C2017R$string.accessibility_data_saver_on
            java.lang.String r5 = r5.getString(r6)
            r0.setIcon(r1, r4, r5)
            com.android.systemui.statusbar.phone.StatusBarIconController r0 = r7.mIconController
            java.lang.String r1 = r7.mSlotDataSaver
            r0.setIconVisibility(r1, r2)
            com.android.systemui.statusbar.phone.StatusBarIconController r0 = r7.mIconController
            java.lang.String r1 = r7.mSlotLocation
            r4 = 17303121(0x1080651, float:2.4983787E-38)
            android.content.res.Resources r5 = r7.mResources
            int r6 = com.android.systemui.C2017R$string.accessibility_location_active
            java.lang.String r5 = r5.getString(r6)
            r0.setIcon(r1, r4, r5)
            com.android.systemui.statusbar.phone.StatusBarIconController r0 = r7.mIconController
            java.lang.String r1 = r7.mSlotLocation
            r0.setIconVisibility(r1, r2)
            com.android.systemui.statusbar.phone.StatusBarIconController r0 = r7.mIconController
            java.lang.String r1 = r7.mSlotSensorsOff
            int r4 = com.android.systemui.C2010R$drawable.stat_sys_sensors_off
            android.content.res.Resources r5 = r7.mResources
            int r6 = com.android.systemui.C2017R$string.accessibility_sensors_off_active
            java.lang.String r5 = r5.getString(r6)
            r0.setIcon(r1, r4, r5)
            com.android.systemui.statusbar.phone.StatusBarIconController r0 = r7.mIconController
            java.lang.String r1 = r7.mSlotSensorsOff
            com.android.systemui.statusbar.policy.SensorPrivacyController r4 = r7.mSensorPrivacyController
            boolean r4 = r4.isSensorPrivacyEnabled()
            r0.setIconVisibility(r1, r4)
            com.android.systemui.statusbar.phone.StatusBarIconController r0 = r7.mIconController
            java.lang.String r1 = r7.mSlotScreenRecord
            int r4 = com.android.systemui.C2010R$drawable.stat_sys_screen_record
            r0.setIcon(r1, r4, r3)
            com.android.systemui.statusbar.phone.StatusBarIconController r0 = r7.mIconController
            java.lang.String r1 = r7.mSlotScreenRecord
            r0.setIconVisibility(r1, r2)
            com.android.systemui.statusbar.policy.RotationLockController r0 = r7.mRotationLockController
            r0.addCallback(r7)
            com.android.systemui.statusbar.policy.BluetoothController r0 = r7.mBluetooth
            r0.addCallback(r7)
            com.android.systemui.statusbar.policy.DeviceProvisionedController r0 = r7.mProvisionedController
            r0.addCallback(r7)
            com.android.systemui.statusbar.policy.ZenModeController r0 = r7.mZenController
            r0.addCallback(r7)
            com.android.systemui.statusbar.policy.CastController r0 = r7.mCast
            com.android.systemui.statusbar.policy.CastController$Callback r1 = r7.mCastCallback
            r0.addCallback(r1)
            com.android.systemui.statusbar.policy.HotspotController r0 = r7.mHotspot
            com.android.systemui.statusbar.policy.HotspotController$Callback r1 = r7.mHotspotCallback
            r0.addCallback(r1)
            com.android.systemui.statusbar.policy.NextAlarmController r0 = r7.mNextAlarmController
            com.android.systemui.statusbar.policy.NextAlarmController$NextAlarmChangeCallback r1 = r7.mNextAlarmCallback
            r0.addCallback(r1)
            com.android.systemui.statusbar.policy.DataSaverController r0 = r7.mDataSaver
            r0.addCallback(r7)
            com.android.systemui.statusbar.policy.KeyguardStateController r0 = r7.mKeyguardStateController
            r0.addCallback(r7)
            com.android.systemui.statusbar.policy.SensorPrivacyController r0 = r7.mSensorPrivacyController
            com.android.systemui.statusbar.policy.SensorPrivacyController$OnSensorPrivacyChangedListener r1 = r7.mSensorPrivacyListener
            r0.addCallback(r1)
            com.android.systemui.statusbar.policy.LocationController r0 = r7.mLocationController
            r0.addCallback(r7)
            com.android.systemui.screenrecord.RecordingController r0 = r7.mRecordingController
            r0.addCallback(r7)
            com.android.systemui.statusbar.CommandQueue r0 = r7.mCommandQueue
            r0.addCallback(r7)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.PhoneStatusBarPolicy.init():void");
    }

    public void onZenChanged(int i) {
        updateVolumeZen();
    }

    public void onConfigChanged(ZenModeConfig zenModeConfig) {
        updateVolumeZen();
    }

    /* access modifiers changed from: private */
    public void updateAlarm() {
        int i;
        AlarmClockInfo nextAlarmClock = this.mAlarmManager.getNextAlarmClock(-2);
        boolean z = true;
        boolean z2 = nextAlarmClock != null && nextAlarmClock.getTriggerTime() > 0;
        boolean z3 = this.mZenController.getZen() == 2;
        StatusBarIconController statusBarIconController = this.mIconController;
        String str = this.mSlotAlarmClock;
        if (z3) {
            i = C2010R$drawable.stat_sys_alarm_dim;
        } else {
            i = C2010R$drawable.stat_sys_alarm;
        }
        statusBarIconController.setIcon(str, i, buildAlarmContentDescription());
        StatusBarIconController statusBarIconController2 = this.mIconController;
        String str2 = this.mSlotAlarmClock;
        if (!this.mCurrentUserSetup || !z2) {
            z = false;
        }
        statusBarIconController2.setIconVisibility(str2, z);
    }

    private String buildAlarmContentDescription() {
        if (this.mNextAlarm == null) {
            return this.mResources.getString(C2017R$string.status_bar_alarm);
        }
        return this.mResources.getString(C2017R$string.accessibility_quick_settings_alarm, new Object[]{DateFormat.format(DateFormat.getBestDateTimePattern(Locale.getDefault(), this.mDateFormatUtil.is24HourFormat() ? "EHm" : "Ehma"), this.mNextAlarm.getTriggerTime()).toString()});
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0082  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x008d  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0098  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00a3  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void updateVolumeZen() {
        /*
            r8 = this;
            com.android.systemui.statusbar.policy.ZenModeController r0 = r8.mZenController
            int r0 = r0.getZen()
            android.content.SharedPreferences r1 = r8.mSharedPreferences
            boolean r1 = com.android.systemui.p007qs.tiles.DndTile.isVisible(r1)
            r2 = 0
            r3 = 0
            r4 = 1
            if (r1 != 0) goto L_0x003c
            android.content.SharedPreferences r1 = r8.mSharedPreferences
            boolean r1 = com.android.systemui.p007qs.tiles.DndTile.isCombinedIcon(r1)
            if (r1 == 0) goto L_0x001a
            goto L_0x003c
        L_0x001a:
            r1 = 2
            if (r0 != r1) goto L_0x002b
            int r1 = com.android.systemui.C2010R$drawable.stat_sys_dnd
            android.content.res.Resources r5 = r8.mResources
            int r6 = com.android.systemui.C2017R$string.interruption_level_none
            java.lang.String r5 = r5.getString(r6)
        L_0x0027:
            r6 = r5
            r5 = r1
            r1 = r4
            goto L_0x004b
        L_0x002b:
            if (r0 != r4) goto L_0x0038
            int r1 = com.android.systemui.C2010R$drawable.stat_sys_dnd
            android.content.res.Resources r5 = r8.mResources
            int r6 = com.android.systemui.C2017R$string.interruption_level_priority
            java.lang.String r5 = r5.getString(r6)
            goto L_0x0027
        L_0x0038:
            r6 = r2
            r1 = r3
            r5 = r1
            goto L_0x004b
        L_0x003c:
            if (r0 == 0) goto L_0x0040
            r1 = r4
            goto L_0x0041
        L_0x0040:
            r1 = r3
        L_0x0041:
            int r5 = com.android.systemui.C2010R$drawable.stat_sys_dnd
            android.content.res.Resources r6 = r8.mResources
            int r7 = com.android.systemui.C2017R$string.quick_settings_dnd_label
            java.lang.String r6 = r6.getString(r7)
        L_0x004b:
            com.android.systemui.statusbar.policy.ZenModeController r7 = r8.mZenController
            android.app.NotificationManager$Policy r7 = r7.getConsolidatedPolicy()
            boolean r0 = android.service.notification.ZenModeConfig.isZenOverridingRinger(r0, r7)
            if (r0 != 0) goto L_0x007f
            android.media.AudioManager r0 = r8.mAudioManager
            int r0 = r0.getRingerModeInternal()
            if (r0 != r4) goto L_0x006c
            int r3 = com.android.systemui.C2010R$drawable.stat_sys_ringer_vibrate
            android.content.res.Resources r0 = r8.mResources
            int r2 = com.android.systemui.C2017R$string.accessibility_ringer_vibrate
            java.lang.String r2 = r0.getString(r2)
        L_0x0069:
            r0 = r3
            r3 = r4
            goto L_0x0080
        L_0x006c:
            android.media.AudioManager r0 = r8.mAudioManager
            int r0 = r0.getRingerModeInternal()
            if (r0 != 0) goto L_0x007f
            int r3 = com.android.systemui.C2010R$drawable.stat_sys_ringer_silent
            android.content.res.Resources r0 = r8.mResources
            int r2 = com.android.systemui.C2017R$string.accessibility_ringer_silent
            java.lang.String r2 = r0.getString(r2)
            goto L_0x0069
        L_0x007f:
            r0 = r3
        L_0x0080:
            if (r1 == 0) goto L_0x0089
            com.android.systemui.statusbar.phone.StatusBarIconController r4 = r8.mIconController
            java.lang.String r7 = r8.mSlotZen
            r4.setIcon(r7, r5, r6)
        L_0x0089:
            boolean r4 = r8.mZenVisible
            if (r1 == r4) goto L_0x0096
            com.android.systemui.statusbar.phone.StatusBarIconController r4 = r8.mIconController
            java.lang.String r5 = r8.mSlotZen
            r4.setIconVisibility(r5, r1)
            r8.mZenVisible = r1
        L_0x0096:
            if (r3 == 0) goto L_0x009f
            com.android.systemui.statusbar.phone.StatusBarIconController r1 = r8.mIconController
            java.lang.String r4 = r8.mSlotVolume
            r1.setIcon(r4, r0, r2)
        L_0x009f:
            boolean r0 = r8.mVolumeVisible
            if (r3 == r0) goto L_0x00ac
            com.android.systemui.statusbar.phone.StatusBarIconController r0 = r8.mIconController
            java.lang.String r1 = r8.mSlotVolume
            r0.setIconVisibility(r1, r3)
            r8.mVolumeVisible = r3
        L_0x00ac:
            r8.updateAlarm()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.PhoneStatusBarPolicy.updateVolumeZen():void");
    }

    public void onBluetoothDevicesChanged() {
        updateBluetooth();
    }

    public void onBluetoothStateChange(boolean z) {
        updateBluetooth();
    }

    private final void updateBluetooth() {
        boolean z;
        int i = C2010R$drawable.stat_sys_data_bluetooth_connected;
        String string = this.mResources.getString(C2017R$string.accessibility_quick_settings_bluetooth_on);
        BluetoothController bluetoothController = this.mBluetooth;
        if (bluetoothController == null || !bluetoothController.isBluetoothConnected() || (!this.mBluetooth.isBluetoothAudioActive() && this.mBluetooth.isBluetoothAudioProfileOnly())) {
            z = false;
        } else {
            string = this.mResources.getString(C2017R$string.accessibility_bluetooth_connected);
            z = this.mBluetooth.isBluetoothEnabled();
        }
        this.mIconController.setIcon(this.mSlotBluetooth, i, string);
        this.mIconController.setIconVisibility(this.mSlotBluetooth, z);
    }

    private final void updateTTY() {
        TelecomManager telecomManager = this.mTelecomManager;
        if (telecomManager == null) {
            updateTTY(0);
        } else {
            updateTTY(telecomManager.getCurrentTtyMode());
        }
    }

    /* access modifiers changed from: private */
    public final void updateTTY(int i) {
        boolean z = i != 0;
        String str = "PhoneStatusBarPolicy";
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("updateTTY: enabled: ");
            sb.append(z);
            Log.v(str, sb.toString());
        }
        if (z) {
            if (DEBUG) {
                Log.v(str, "updateTTY: set TTY on");
            }
            this.mIconController.setIcon(this.mSlotTty, C2010R$drawable.stat_sys_tty_mode, this.mResources.getString(C2017R$string.accessibility_tty_enabled));
            this.mIconController.setIconVisibility(this.mSlotTty, true);
            return;
        }
        if (DEBUG) {
            Log.v(str, "updateTTY: set TTY off");
        }
        this.mIconController.setIconVisibility(this.mSlotTty, false);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:7:0x001e, code lost:
        r0 = true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateCast() {
        /*
            r6 = this;
            com.android.systemui.statusbar.policy.CastController r0 = r6.mCast
            java.util.List r0 = r0.getCastDevices()
            java.util.Iterator r0 = r0.iterator()
        L_0x000a:
            boolean r1 = r0.hasNext()
            r2 = 1
            if (r1 == 0) goto L_0x0020
            java.lang.Object r1 = r0.next()
            com.android.systemui.statusbar.policy.CastController$CastDevice r1 = (com.android.systemui.statusbar.policy.CastController.CastDevice) r1
            int r1 = r1.state
            if (r1 == r2) goto L_0x001e
            r3 = 2
            if (r1 != r3) goto L_0x000a
        L_0x001e:
            r0 = r2
            goto L_0x0021
        L_0x0020:
            r0 = 0
        L_0x0021:
            boolean r1 = DEBUG
            java.lang.String r3 = "PhoneStatusBarPolicy"
            if (r1 == 0) goto L_0x003b
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r4 = "updateCast: isCasting: "
            r1.append(r4)
            r1.append(r0)
            java.lang.String r1 = r1.toString()
            android.util.Log.v(r3, r1)
        L_0x003b:
            android.os.Handler r1 = r6.mHandler
            java.lang.Runnable r4 = r6.mRemoveCastIconRunnable
            r1.removeCallbacks(r4)
            if (r0 == 0) goto L_0x0065
            com.android.systemui.screenrecord.RecordingController r0 = r6.mRecordingController
            boolean r0 = r0.isRecording()
            if (r0 != 0) goto L_0x0065
            com.android.systemui.statusbar.phone.StatusBarIconController r0 = r6.mIconController
            java.lang.String r1 = r6.mSlotCast
            int r3 = com.android.systemui.C2010R$drawable.stat_sys_cast
            android.content.res.Resources r4 = r6.mResources
            int r5 = com.android.systemui.C2017R$string.accessibility_casting
            java.lang.String r4 = r4.getString(r5)
            r0.setIcon(r1, r3, r4)
            com.android.systemui.statusbar.phone.StatusBarIconController r0 = r6.mIconController
            java.lang.String r6 = r6.mSlotCast
            r0.setIconVisibility(r6, r2)
            goto L_0x0077
        L_0x0065:
            boolean r0 = DEBUG
            if (r0 == 0) goto L_0x006e
            java.lang.String r0 = "updateCast: hiding icon in 3 sec..."
            android.util.Log.v(r3, r0)
        L_0x006e:
            android.os.Handler r0 = r6.mHandler
            java.lang.Runnable r6 = r6.mRemoveCastIconRunnable
            r1 = 3000(0xbb8, double:1.482E-320)
            r0.postDelayed(r6, r1)
        L_0x0077:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.PhoneStatusBarPolicy.updateCast():void");
    }

    /* access modifiers changed from: private */
    public void updateManagedProfile() {
        this.mUiBgExecutor.execute(new Runnable() {
            public final void run() {
                PhoneStatusBarPolicy.this.lambda$updateManagedProfile$1$PhoneStatusBarPolicy();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateManagedProfile$1 */
    public /* synthetic */ void lambda$updateManagedProfile$1$PhoneStatusBarPolicy() {
        try {
            this.mHandler.post(new Runnable(this.mUserManager.isManagedProfile(ActivityTaskManager.getService().getLastResumedActivityUserId())) {
                public final /* synthetic */ boolean f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    PhoneStatusBarPolicy.this.lambda$updateManagedProfile$0$PhoneStatusBarPolicy(this.f$1);
                }
            });
        } catch (RemoteException e) {
            Log.w("PhoneStatusBarPolicy", "updateManagedProfile: ", e);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateManagedProfile$0 */
    public /* synthetic */ void lambda$updateManagedProfile$0$PhoneStatusBarPolicy(boolean z) {
        boolean z2;
        if (!z || (this.mKeyguardStateController.isShowing() && !this.mKeyguardStateController.isOccluded())) {
            z2 = false;
        } else {
            z2 = true;
            this.mIconController.setIcon(this.mSlotManagedProfile, C2010R$drawable.stat_sys_managed_profile_status, this.mResources.getString(C2017R$string.accessibility_managed_profile));
        }
        if (this.mManagedProfileIconVisible != z2) {
            this.mIconController.setIconVisibility(this.mSlotManagedProfile, z2);
            this.mManagedProfileIconVisible = z2;
        }
    }

    public void appTransitionStarting(int i, long j, long j2, boolean z) {
        if (this.mDisplayId == i) {
            updateManagedProfile();
        }
    }

    public void onKeyguardShowingChanged() {
        updateManagedProfile();
    }

    public void onUserSetupChanged() {
        DeviceProvisionedController deviceProvisionedController = this.mProvisionedController;
        boolean isUserSetup = deviceProvisionedController.isUserSetup(deviceProvisionedController.getCurrentUser());
        if (this.mCurrentUserSetup != isUserSetup) {
            this.mCurrentUserSetup = isUserSetup;
            updateAlarm();
        }
    }

    public void onRotationLockStateChanged(boolean z, boolean z2) {
        boolean isCurrentOrientationLockPortrait = RotationLockTile.isCurrentOrientationLockPortrait(this.mRotationLockController, this.mResources);
        if (z) {
            if (isCurrentOrientationLockPortrait) {
                this.mIconController.setIcon(this.mSlotRotate, C2010R$drawable.stat_sys_rotate_portrait, this.mResources.getString(C2017R$string.accessibility_rotation_lock_on_portrait));
            } else {
                this.mIconController.setIcon(this.mSlotRotate, C2010R$drawable.stat_sys_rotate_landscape, this.mResources.getString(C2017R$string.accessibility_rotation_lock_on_landscape));
            }
            this.mIconController.setIconVisibility(this.mSlotRotate, true);
            return;
        }
        this.mIconController.setIconVisibility(this.mSlotRotate, false);
    }

    /* access modifiers changed from: private */
    public void updateHeadsetPlug(Intent intent) {
        int i;
        int i2;
        boolean z = intent.getIntExtra("state", 0) != 0;
        boolean z2 = intent.getIntExtra("microphone", 0) != 0;
        if (z) {
            Resources resources = this.mResources;
            if (z2) {
                i = C2017R$string.accessibility_status_bar_headset;
            } else {
                i = C2017R$string.accessibility_status_bar_headphones;
            }
            String string = resources.getString(i);
            StatusBarIconController statusBarIconController = this.mIconController;
            String str = this.mSlotHeadset;
            if (z2) {
                i2 = C2010R$drawable.stat_sys_headset_mic;
            } else {
                i2 = C2010R$drawable.stat_sys_headset;
            }
            statusBarIconController.setIcon(str, i2, string);
            this.mIconController.setIconVisibility(this.mSlotHeadset, true);
            return;
        }
        this.mIconController.setIconVisibility(this.mSlotHeadset, false);
    }

    public void onDataSaverChanged(boolean z) {
        this.mIconController.setIconVisibility(this.mSlotDataSaver, z);
    }

    public void onLocationActiveChanged(boolean z) {
        updateLocation();
    }

    private void updateLocation() {
        if (this.mLocationController.isLocationActive()) {
            this.mIconController.setIconVisibility(this.mSlotLocation, true);
        } else {
            this.mIconController.setIconVisibility(this.mSlotLocation, false);
        }
    }

    public void onCountdown(long j) {
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("screenrecord: countdown ");
            sb.append(j);
            Log.d("PhoneStatusBarPolicy", sb.toString());
        }
        int floorDiv = (int) Math.floorDiv(j + 500, 1000);
        int i = C2010R$drawable.stat_sys_screen_record;
        if (floorDiv == 1) {
            i = C2010R$drawable.stat_sys_screen_record_1;
        } else if (floorDiv == 2) {
            i = C2010R$drawable.stat_sys_screen_record_2;
        } else if (floorDiv == 3) {
            i = C2010R$drawable.stat_sys_screen_record_3;
        }
        this.mIconController.setIcon(this.mSlotScreenRecord, i, null);
        this.mIconController.setIconVisibility(this.mSlotScreenRecord, true);
    }

    public void onCountdownEnd() {
        if (DEBUG) {
            Log.d("PhoneStatusBarPolicy", "screenrecord: hiding icon during countdown");
        }
        this.mHandler.post(new Runnable() {
            public final void run() {
                PhoneStatusBarPolicy.this.lambda$onCountdownEnd$2$PhoneStatusBarPolicy();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onCountdownEnd$2 */
    public /* synthetic */ void lambda$onCountdownEnd$2$PhoneStatusBarPolicy() {
        this.mIconController.setIconVisibility(this.mSlotScreenRecord, false);
    }

    public void onRecordingStart() {
        if (DEBUG) {
            Log.d("PhoneStatusBarPolicy", "screenrecord: showing icon");
        }
        this.mIconController.setIcon(this.mSlotScreenRecord, C2010R$drawable.stat_sys_screen_record, null);
        this.mIconController.setIconVisibility(this.mSlotScreenRecord, true);
    }

    public void onRecordingEnd() {
        if (DEBUG) {
            Log.d("PhoneStatusBarPolicy", "screenrecord: hiding icon");
        }
        this.mHandler.post(new Runnable() {
            public final void run() {
                PhoneStatusBarPolicy.this.lambda$onRecordingEnd$3$PhoneStatusBarPolicy();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onRecordingEnd$3 */
    public /* synthetic */ void lambda$onRecordingEnd$3$PhoneStatusBarPolicy() {
        this.mIconController.setIconVisibility(this.mSlotScreenRecord, false);
    }
}
