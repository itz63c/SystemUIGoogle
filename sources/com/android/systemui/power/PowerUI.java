package com.android.systemui.power;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.IThermalEventListener;
import android.os.IThermalEventListener.Stub;
import android.os.IThermalService;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.Temperature;
import android.provider.Settings.Global;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settingslib.fuelgauge.Estimate;
import com.android.settingslib.utils.ThreadUtils;
import com.android.systemui.C2012R$integer;
import com.android.systemui.Dependency;
import com.android.systemui.SystemUI;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.CommandQueue.Callbacks;
import com.android.systemui.statusbar.phone.StatusBar;
import dagger.Lazy;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.Future;

public class PowerUI extends SystemUI implements Callbacks {
    static final boolean DEBUG = Log.isLoggable("PowerUI", 3);
    private static final long SIX_HOURS_MILLIS = Duration.ofHours(6).toMillis();
    @VisibleForTesting
    int mBatteryLevel = 100;
    @VisibleForTesting
    int mBatteryStatus = 1;
    /* access modifiers changed from: private */
    public final BroadcastDispatcher mBroadcastDispatcher;
    private final CommandQueue mCommandQueue;
    @VisibleForTesting
    BatteryStateSnapshot mCurrentBatteryStateSnapshot;
    private boolean mEnableSkinTemperatureWarning;
    private boolean mEnableUsbTemperatureAlarm;
    private EnhancedEstimates mEnhancedEstimates;
    /* access modifiers changed from: private */
    public final Handler mHandler = new Handler();
    /* access modifiers changed from: private */
    public int mInvalidCharger = 0;
    @VisibleForTesting
    BatteryStateSnapshot mLastBatteryStateSnapshot;
    private final Configuration mLastConfiguration = new Configuration();
    /* access modifiers changed from: private */
    public Future mLastShowWarningTask;
    /* access modifiers changed from: private */
    public int mLowBatteryAlertCloseLevel;
    /* access modifiers changed from: private */
    public final int[] mLowBatteryReminderLevels = new int[2];
    @VisibleForTesting
    boolean mLowWarningShownThisChargeCycle;
    private InattentiveSleepWarningView mOverlayView;
    /* access modifiers changed from: private */
    public int mPlugType = 0;
    /* access modifiers changed from: private */
    public PowerManager mPowerManager;
    @VisibleForTesting
    final Receiver mReceiver = new Receiver();
    /* access modifiers changed from: private */
    public long mScreenOffTime = -1;
    @VisibleForTesting
    boolean mSevereWarningShownThisChargeCycle;
    private IThermalEventListener mSkinThermalEventListener;
    /* access modifiers changed from: private */
    public final Lazy<StatusBar> mStatusBarLazy;
    @VisibleForTesting
    IThermalService mThermalService;
    private IThermalEventListener mUsbThermalEventListener;
    /* access modifiers changed from: private */
    public WarningsUI mWarnings;

    @VisibleForTesting
    final class Receiver extends BroadcastReceiver {
        Receiver() {
        }

        public void init() {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.os.action.POWER_SAVE_MODE_CHANGED");
            intentFilter.addAction("android.intent.action.BATTERY_CHANGED");
            intentFilter.addAction("android.intent.action.SCREEN_OFF");
            intentFilter.addAction("android.intent.action.SCREEN_ON");
            intentFilter.addAction("android.intent.action.USER_SWITCHED");
            PowerUI.this.mBroadcastDispatcher.registerReceiverWithHandler(this, intentFilter, PowerUI.this.mHandler);
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.os.action.POWER_SAVE_MODE_CHANGED".equals(action)) {
                ThreadUtils.postOnBackgroundThread(new Runnable() {
                    public final void run() {
                        Receiver.this.lambda$onReceive$0$PowerUI$Receiver();
                    }
                });
            } else {
                String str = "PowerUI";
                if ("android.intent.action.BATTERY_CHANGED".equals(action)) {
                    PowerUI powerUI = PowerUI.this;
                    int i = powerUI.mBatteryLevel;
                    powerUI.mBatteryLevel = intent.getIntExtra("level", 100);
                    PowerUI powerUI2 = PowerUI.this;
                    int i2 = powerUI2.mBatteryStatus;
                    powerUI2.mBatteryStatus = intent.getIntExtra("status", 1);
                    int access$200 = PowerUI.this.mPlugType;
                    PowerUI.this.mPlugType = intent.getIntExtra("plugged", 1);
                    int access$300 = PowerUI.this.mInvalidCharger;
                    PowerUI.this.mInvalidCharger = intent.getIntExtra("invalid_charger", 0);
                    PowerUI powerUI3 = PowerUI.this;
                    powerUI3.mLastBatteryStateSnapshot = powerUI3.mCurrentBatteryStateSnapshot;
                    boolean z = powerUI3.mPlugType != 0;
                    boolean z2 = access$200 != 0;
                    int access$400 = PowerUI.this.findBatteryLevelBucket(i);
                    PowerUI powerUI4 = PowerUI.this;
                    int access$4002 = powerUI4.findBatteryLevelBucket(powerUI4.mBatteryLevel);
                    if (PowerUI.DEBUG) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("buckets   .....");
                        sb.append(PowerUI.this.mLowBatteryAlertCloseLevel);
                        String str2 = " .. ";
                        sb.append(str2);
                        sb.append(PowerUI.this.mLowBatteryReminderLevels[0]);
                        sb.append(str2);
                        sb.append(PowerUI.this.mLowBatteryReminderLevels[1]);
                        Slog.d(str, sb.toString());
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("level          ");
                        sb2.append(i);
                        String str3 = " --> ";
                        sb2.append(str3);
                        sb2.append(PowerUI.this.mBatteryLevel);
                        Slog.d(str, sb2.toString());
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append("status         ");
                        sb3.append(i2);
                        sb3.append(str3);
                        sb3.append(PowerUI.this.mBatteryStatus);
                        Slog.d(str, sb3.toString());
                        StringBuilder sb4 = new StringBuilder();
                        sb4.append("plugType       ");
                        sb4.append(access$200);
                        sb4.append(str3);
                        sb4.append(PowerUI.this.mPlugType);
                        Slog.d(str, sb4.toString());
                        StringBuilder sb5 = new StringBuilder();
                        sb5.append("invalidCharger ");
                        sb5.append(access$300);
                        sb5.append(str3);
                        sb5.append(PowerUI.this.mInvalidCharger);
                        Slog.d(str, sb5.toString());
                        StringBuilder sb6 = new StringBuilder();
                        sb6.append("bucket         ");
                        sb6.append(access$400);
                        sb6.append(str3);
                        sb6.append(access$4002);
                        Slog.d(str, sb6.toString());
                        StringBuilder sb7 = new StringBuilder();
                        sb7.append("plugged        ");
                        sb7.append(z2);
                        sb7.append(str3);
                        sb7.append(z);
                        Slog.d(str, sb7.toString());
                    }
                    WarningsUI access$800 = PowerUI.this.mWarnings;
                    PowerUI powerUI5 = PowerUI.this;
                    access$800.update(powerUI5.mBatteryLevel, access$4002, powerUI5.mScreenOffTime);
                    if (access$300 != 0 || PowerUI.this.mInvalidCharger == 0) {
                        if (access$300 != 0 && PowerUI.this.mInvalidCharger == 0) {
                            PowerUI.this.mWarnings.dismissInvalidChargerWarning();
                        } else if (PowerUI.this.mWarnings.isInvalidChargerWarningShowing()) {
                            if (PowerUI.DEBUG) {
                                Slog.d(str, "Bad Charger");
                            }
                            return;
                        }
                        if (PowerUI.this.mLastShowWarningTask != null) {
                            PowerUI.this.mLastShowWarningTask.cancel(true);
                            if (PowerUI.DEBUG) {
                                Slog.d(str, "cancelled task");
                            }
                        }
                        PowerUI.this.mLastShowWarningTask = ThreadUtils.postOnBackgroundThread(new Runnable(z, access$4002) {
                            public final /* synthetic */ boolean f$1;
                            public final /* synthetic */ int f$2;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                            }

                            public final void run() {
                                Receiver.this.lambda$onReceive$1$PowerUI$Receiver(this.f$1, this.f$2);
                            }
                        });
                    } else {
                        Slog.d(str, "showing invalid charger warning");
                        PowerUI.this.mWarnings.showInvalidChargerWarning();
                    }
                } else if ("android.intent.action.SCREEN_OFF".equals(action)) {
                    PowerUI.this.mScreenOffTime = SystemClock.elapsedRealtime();
                } else if ("android.intent.action.SCREEN_ON".equals(action)) {
                    PowerUI.this.mScreenOffTime = -1;
                } else if ("android.intent.action.USER_SWITCHED".equals(action)) {
                    PowerUI.this.mWarnings.userSwitched();
                } else {
                    StringBuilder sb8 = new StringBuilder();
                    sb8.append("unknown intent: ");
                    sb8.append(intent);
                    Slog.w(str, sb8.toString());
                }
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onReceive$0 */
        public /* synthetic */ void lambda$onReceive$0$PowerUI$Receiver() {
            if (PowerUI.this.mPowerManager.isPowerSaveMode()) {
                PowerUI.this.mWarnings.dismissLowBatteryWarning();
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onReceive$1 */
        public /* synthetic */ void lambda$onReceive$1$PowerUI$Receiver(boolean z, int i) {
            PowerUI.this.maybeShowBatteryWarningV2(z, i);
        }
    }

    @VisibleForTesting
    final class SkinThermalEventListener extends Stub {
        SkinThermalEventListener() {
        }

        public void notifyThrottling(Temperature temperature) {
            int status = temperature.getStatus();
            if (status < 5) {
                PowerUI.this.mWarnings.dismissHighTemperatureWarning();
            } else if (!((StatusBar) PowerUI.this.mStatusBarLazy.get()).isDeviceInVrMode()) {
                PowerUI.this.mWarnings.showHighTemperatureWarning();
                StringBuilder sb = new StringBuilder();
                sb.append("SkinThermalEventListener: notifyThrottling was called , current skin status = ");
                sb.append(status);
                sb.append(", temperature = ");
                sb.append(temperature.getValue());
                Slog.d("PowerUI", sb.toString());
            }
        }
    }

    @VisibleForTesting
    final class UsbThermalEventListener extends Stub {
        UsbThermalEventListener() {
        }

        public void notifyThrottling(Temperature temperature) {
            int status = temperature.getStatus();
            if (status >= 5) {
                PowerUI.this.mWarnings.showUsbHighTemperatureAlarm();
                StringBuilder sb = new StringBuilder();
                sb.append("UsbThermalEventListener: notifyThrottling was called , current usb port status = ");
                sb.append(status);
                sb.append(", temperature = ");
                sb.append(temperature.getValue());
                Slog.d("PowerUI", sb.toString());
            }
        }
    }

    public interface WarningsUI {
        void dismissHighTemperatureWarning();

        void dismissInvalidChargerWarning();

        void dismissLowBatteryWarning();

        void dump(PrintWriter printWriter);

        boolean isInvalidChargerWarningShowing();

        void showHighTemperatureWarning();

        void showInvalidChargerWarning();

        void showLowBatteryWarning(boolean z);

        void showThermalShutdownWarning();

        void showUsbHighTemperatureAlarm();

        void update(int i, int i2, long j);

        void updateLowBatteryWarning();

        void updateSnapshot(BatteryStateSnapshot batteryStateSnapshot);

        void userSwitched();
    }

    public PowerUI(Context context, BroadcastDispatcher broadcastDispatcher, CommandQueue commandQueue, Lazy<StatusBar> lazy) {
        super(context);
        this.mBroadcastDispatcher = broadcastDispatcher;
        this.mCommandQueue = commandQueue;
        this.mStatusBarLazy = lazy;
    }

    public void start() {
        PowerManager powerManager = (PowerManager) this.mContext.getSystemService("power");
        this.mPowerManager = powerManager;
        this.mScreenOffTime = powerManager.isScreenOn() ? -1 : SystemClock.elapsedRealtime();
        this.mWarnings = (WarningsUI) Dependency.get(WarningsUI.class);
        this.mEnhancedEstimates = (EnhancedEstimates) Dependency.get(EnhancedEstimates.class);
        this.mLastConfiguration.setTo(this.mContext.getResources().getConfiguration());
        C09441 r0 = new ContentObserver(this.mHandler) {
            public void onChange(boolean z) {
                PowerUI.this.updateBatteryWarningLevels();
            }
        };
        ContentResolver contentResolver = this.mContext.getContentResolver();
        contentResolver.registerContentObserver(Global.getUriFor("low_power_trigger_level"), false, r0, -1);
        updateBatteryWarningLevels();
        this.mReceiver.init();
        showWarnOnThermalShutdown();
        contentResolver.registerContentObserver(Global.getUriFor("show_temperature_warning"), false, new ContentObserver(this.mHandler) {
            public void onChange(boolean z) {
                PowerUI.this.doSkinThermalEventListenerRegistration();
            }
        });
        contentResolver.registerContentObserver(Global.getUriFor("show_usb_temperature_alarm"), false, new ContentObserver(this.mHandler) {
            public void onChange(boolean z) {
                PowerUI.this.doUsbThermalEventListenerRegistration();
            }
        });
        initThermalEventListeners();
        this.mCommandQueue.addCallback((Callbacks) this);
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        if ((this.mLastConfiguration.updateFrom(configuration) & 3) != 0) {
            this.mHandler.post(new Runnable() {
                public final void run() {
                    PowerUI.this.initThermalEventListeners();
                }
            });
        }
    }

    /* access modifiers changed from: 0000 */
    public void updateBatteryWarningLevels() {
        int integer = this.mContext.getResources().getInteger(17694765);
        int integer2 = this.mContext.getResources().getInteger(17694829);
        if (integer2 < integer) {
            integer2 = integer;
        }
        int[] iArr = this.mLowBatteryReminderLevels;
        iArr[0] = integer2;
        iArr[1] = integer;
        this.mLowBatteryAlertCloseLevel = iArr[0] + this.mContext.getResources().getInteger(17694828);
    }

    /* access modifiers changed from: private */
    public int findBatteryLevelBucket(int i) {
        if (i >= this.mLowBatteryAlertCloseLevel) {
            return 1;
        }
        int[] iArr = this.mLowBatteryReminderLevels;
        if (i > iArr[0]) {
            return 0;
        }
        for (int length = iArr.length - 1; length >= 0; length--) {
            if (i <= this.mLowBatteryReminderLevels[length]) {
                return -1 - length;
            }
        }
        throw new RuntimeException("not possible!");
    }

    /* access modifiers changed from: protected */
    public void maybeShowBatteryWarningV2(boolean z, int i) {
        boolean isHybridNotificationEnabled = this.mEnhancedEstimates.isHybridNotificationEnabled();
        boolean isPowerSaveMode = this.mPowerManager.isPowerSaveMode();
        String str = "PowerUI";
        if (DEBUG) {
            Slog.d(str, "evaluating which notification to show");
        }
        if (isHybridNotificationEnabled) {
            if (DEBUG) {
                Slog.d(str, "using hybrid");
            }
            Estimate refreshEstimateIfNeeded = refreshEstimateIfNeeded();
            int i2 = this.mBatteryLevel;
            int i3 = this.mBatteryStatus;
            int[] iArr = this.mLowBatteryReminderLevels;
            BatteryStateSnapshot batteryStateSnapshot = r3;
            BatteryStateSnapshot batteryStateSnapshot2 = new BatteryStateSnapshot(i2, isPowerSaveMode, z, i, i3, iArr[1], iArr[0], refreshEstimateIfNeeded.getEstimateMillis(), refreshEstimateIfNeeded.getAverageDischargeTime(), this.mEnhancedEstimates.getSevereWarningThreshold(), this.mEnhancedEstimates.getLowWarningThreshold(), refreshEstimateIfNeeded.isBasedOnUsage(), this.mEnhancedEstimates.getLowWarningEnabled());
            this.mCurrentBatteryStateSnapshot = batteryStateSnapshot;
        } else {
            if (DEBUG) {
                Slog.d(str, "using standard");
            }
            int i4 = this.mBatteryLevel;
            int i5 = this.mBatteryStatus;
            int[] iArr2 = this.mLowBatteryReminderLevels;
            BatteryStateSnapshot batteryStateSnapshot3 = new BatteryStateSnapshot(i4, isPowerSaveMode, z, i, i5, iArr2[1], iArr2[0]);
            this.mCurrentBatteryStateSnapshot = batteryStateSnapshot3;
        }
        this.mWarnings.updateSnapshot(this.mCurrentBatteryStateSnapshot);
        if (this.mCurrentBatteryStateSnapshot.isHybrid()) {
            maybeShowHybridWarning(this.mCurrentBatteryStateSnapshot, this.mLastBatteryStateSnapshot);
        } else {
            maybeShowBatteryWarning(this.mCurrentBatteryStateSnapshot, this.mLastBatteryStateSnapshot);
        }
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public Estimate refreshEstimateIfNeeded() {
        BatteryStateSnapshot batteryStateSnapshot = this.mLastBatteryStateSnapshot;
        if (batteryStateSnapshot == null || batteryStateSnapshot.getTimeRemainingMillis() == -1 || this.mBatteryLevel != this.mLastBatteryStateSnapshot.getBatteryLevel()) {
            Estimate estimate = this.mEnhancedEstimates.getEstimate();
            if (DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("updated estimate: ");
                sb.append(estimate.getEstimateMillis());
                Slog.d("PowerUI", sb.toString());
            }
            return estimate;
        }
        Estimate estimate2 = new Estimate(this.mLastBatteryStateSnapshot.getTimeRemainingMillis(), this.mLastBatteryStateSnapshot.isBasedOnUsage(), this.mLastBatteryStateSnapshot.getAverageTimeToDischargeMillis());
        return estimate2;
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void maybeShowHybridWarning(BatteryStateSnapshot batteryStateSnapshot, BatteryStateSnapshot batteryStateSnapshot2) {
        boolean z = false;
        String str = "PowerUI";
        if (batteryStateSnapshot.getBatteryLevel() >= 45 && batteryStateSnapshot.getTimeRemainingMillis() > SIX_HOURS_MILLIS) {
            this.mLowWarningShownThisChargeCycle = false;
            this.mSevereWarningShownThisChargeCycle = false;
            if (DEBUG) {
                Slog.d(str, "Charge cycle reset! Can show warnings again");
            }
        }
        if (batteryStateSnapshot.getBucket() != batteryStateSnapshot2.getBucket() || batteryStateSnapshot2.getPlugged()) {
            z = true;
        }
        if (shouldShowHybridWarning(batteryStateSnapshot)) {
            this.mWarnings.showLowBatteryWarning(z);
            if (batteryStateSnapshot.getTimeRemainingMillis() <= batteryStateSnapshot.getSevereThresholdMillis() || batteryStateSnapshot.getBatteryLevel() <= batteryStateSnapshot.getSevereLevelThreshold()) {
                this.mSevereWarningShownThisChargeCycle = true;
                this.mLowWarningShownThisChargeCycle = true;
                if (DEBUG) {
                    Slog.d(str, "Severe warning marked as shown this cycle");
                    return;
                }
                return;
            }
            Slog.d(str, "Low warning marked as shown this cycle");
            this.mLowWarningShownThisChargeCycle = true;
        } else if (shouldDismissHybridWarning(batteryStateSnapshot)) {
            if (DEBUG) {
                Slog.d(str, "Dismissing warning");
            }
            this.mWarnings.dismissLowBatteryWarning();
        } else {
            if (DEBUG) {
                Slog.d(str, "Updating warning");
            }
            this.mWarnings.updateLowBatteryWarning();
        }
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public boolean shouldShowHybridWarning(BatteryStateSnapshot batteryStateSnapshot) {
        String str = "PowerUI";
        boolean z = false;
        boolean z2 = true;
        if (batteryStateSnapshot.getPlugged() || batteryStateSnapshot.getBatteryStatus() == 1) {
            StringBuilder sb = new StringBuilder();
            sb.append("can't show warning due to - plugged: ");
            sb.append(batteryStateSnapshot.getPlugged());
            sb.append(" status unknown: ");
            if (batteryStateSnapshot.getBatteryStatus() != 1) {
                z2 = false;
            }
            sb.append(z2);
            Slog.d(str, sb.toString());
            return false;
        }
        boolean z3 = batteryStateSnapshot.isLowWarningEnabled() && !this.mLowWarningShownThisChargeCycle && !batteryStateSnapshot.isPowerSaver() && (batteryStateSnapshot.getTimeRemainingMillis() < batteryStateSnapshot.getLowThresholdMillis() || batteryStateSnapshot.getBatteryLevel() <= batteryStateSnapshot.getLowLevelThreshold());
        boolean z4 = !this.mSevereWarningShownThisChargeCycle && (batteryStateSnapshot.getTimeRemainingMillis() < batteryStateSnapshot.getSevereThresholdMillis() || batteryStateSnapshot.getBatteryLevel() <= batteryStateSnapshot.getSevereLevelThreshold());
        if (z3 || z4) {
            z = true;
        }
        if (DEBUG) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Enhanced trigger is: ");
            sb2.append(z);
            sb2.append("\nwith battery snapshot: mLowWarningShownThisChargeCycle: ");
            sb2.append(this.mLowWarningShownThisChargeCycle);
            sb2.append(" mSevereWarningShownThisChargeCycle: ");
            sb2.append(this.mSevereWarningShownThisChargeCycle);
            sb2.append("\n");
            sb2.append(batteryStateSnapshot.toString());
            Slog.d(str, sb2.toString());
        }
        return z;
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public boolean shouldDismissHybridWarning(BatteryStateSnapshot batteryStateSnapshot) {
        return batteryStateSnapshot.getPlugged() || batteryStateSnapshot.getTimeRemainingMillis() > batteryStateSnapshot.getLowThresholdMillis();
    }

    /* access modifiers changed from: protected */
    public void maybeShowBatteryWarning(BatteryStateSnapshot batteryStateSnapshot, BatteryStateSnapshot batteryStateSnapshot2) {
        boolean z = batteryStateSnapshot.getBucket() != batteryStateSnapshot2.getBucket() || batteryStateSnapshot2.getPlugged();
        if (shouldShowLowBatteryWarning(batteryStateSnapshot, batteryStateSnapshot2)) {
            this.mWarnings.showLowBatteryWarning(z);
        } else if (shouldDismissLowBatteryWarning(batteryStateSnapshot, batteryStateSnapshot2)) {
            this.mWarnings.dismissLowBatteryWarning();
        } else {
            this.mWarnings.updateLowBatteryWarning();
        }
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public boolean shouldShowLowBatteryWarning(BatteryStateSnapshot batteryStateSnapshot, BatteryStateSnapshot batteryStateSnapshot2) {
        if (batteryStateSnapshot.getPlugged() || batteryStateSnapshot.isPowerSaver() || ((batteryStateSnapshot.getBucket() >= batteryStateSnapshot2.getBucket() && !batteryStateSnapshot2.getPlugged()) || batteryStateSnapshot.getBucket() >= 0 || batteryStateSnapshot.getBatteryStatus() == 1)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public boolean shouldDismissLowBatteryWarning(BatteryStateSnapshot batteryStateSnapshot, BatteryStateSnapshot batteryStateSnapshot2) {
        return batteryStateSnapshot.isPowerSaver() || batteryStateSnapshot.getPlugged() || (batteryStateSnapshot.getBucket() > batteryStateSnapshot2.getBucket() && batteryStateSnapshot.getBucket() > 0);
    }

    /* access modifiers changed from: private */
    public void initThermalEventListeners() {
        doSkinThermalEventListenerRegistration();
        doUsbThermalEventListenerRegistration();
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public synchronized void doSkinThermalEventListenerRegistration() {
        boolean z;
        boolean z2 = this.mEnableSkinTemperatureWarning;
        boolean z3 = true;
        boolean z4 = Global.getInt(this.mContext.getContentResolver(), "show_temperature_warning", this.mContext.getResources().getInteger(C2012R$integer.config_showTemperatureWarning)) != 0;
        this.mEnableSkinTemperatureWarning = z4;
        if (z4 != z2) {
            try {
                if (this.mSkinThermalEventListener == null) {
                    this.mSkinThermalEventListener = new SkinThermalEventListener();
                }
                if (this.mThermalService == null) {
                    this.mThermalService = IThermalService.Stub.asInterface(ServiceManager.getService("thermalservice"));
                }
                if (this.mEnableSkinTemperatureWarning) {
                    z = this.mThermalService.registerThermalEventListenerWithType(this.mSkinThermalEventListener, 3);
                } else {
                    z = this.mThermalService.unregisterThermalEventListener(this.mSkinThermalEventListener);
                }
            } catch (RemoteException e) {
                Slog.e("PowerUI", "Exception while (un)registering skin thermal event listener.", e);
                z = false;
            }
            if (!z) {
                if (this.mEnableSkinTemperatureWarning) {
                    z3 = false;
                }
                this.mEnableSkinTemperatureWarning = z3;
                Slog.e("PowerUI", "Failed to register or unregister skin thermal event listener.");
            }
        }
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public synchronized void doUsbThermalEventListenerRegistration() {
        boolean z;
        boolean z2 = this.mEnableUsbTemperatureAlarm;
        boolean z3 = true;
        boolean z4 = Global.getInt(this.mContext.getContentResolver(), "show_usb_temperature_alarm", this.mContext.getResources().getInteger(C2012R$integer.config_showUsbPortAlarm)) != 0;
        this.mEnableUsbTemperatureAlarm = z4;
        if (z4 != z2) {
            try {
                if (this.mUsbThermalEventListener == null) {
                    this.mUsbThermalEventListener = new UsbThermalEventListener();
                }
                if (this.mThermalService == null) {
                    this.mThermalService = IThermalService.Stub.asInterface(ServiceManager.getService("thermalservice"));
                }
                if (this.mEnableUsbTemperatureAlarm) {
                    z = this.mThermalService.registerThermalEventListenerWithType(this.mUsbThermalEventListener, 4);
                } else {
                    z = this.mThermalService.unregisterThermalEventListener(this.mUsbThermalEventListener);
                }
            } catch (RemoteException e) {
                Slog.e("PowerUI", "Exception while (un)registering usb thermal event listener.", e);
                z = false;
            }
            if (!z) {
                if (this.mEnableUsbTemperatureAlarm) {
                    z3 = false;
                }
                this.mEnableUsbTemperatureAlarm = z3;
                Slog.e("PowerUI", "Failed to register or unregister usb thermal event listener.");
            }
        }
    }

    private void showWarnOnThermalShutdown() {
        String str = "powerui_prefs";
        String str2 = "boot_count";
        int i = -1;
        int i2 = this.mContext.getSharedPreferences(str, 0).getInt(str2, -1);
        try {
            i = Global.getInt(this.mContext.getContentResolver(), str2);
        } catch (SettingNotFoundException unused) {
            Slog.e("PowerUI", "Failed to read system boot count from Settings.Global.BOOT_COUNT");
        }
        if (i > i2) {
            this.mContext.getSharedPreferences(str, 0).edit().putInt(str2, i).apply();
            if (this.mPowerManager.getLastShutdownReason() == 4) {
                this.mWarnings.showThermalShutdownWarning();
            }
        }
    }

    public void showInattentiveSleepWarning() {
        if (this.mOverlayView == null) {
            this.mOverlayView = new InattentiveSleepWarningView(this.mContext);
        }
        this.mOverlayView.show();
    }

    public void dismissInattentiveSleepWarning(boolean z) {
        InattentiveSleepWarningView inattentiveSleepWarningView = this.mOverlayView;
        if (inattentiveSleepWarningView != null) {
            inattentiveSleepWarningView.dismiss(z);
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.print("mLowBatteryAlertCloseLevel=");
        printWriter.println(this.mLowBatteryAlertCloseLevel);
        printWriter.print("mLowBatteryReminderLevels=");
        printWriter.println(Arrays.toString(this.mLowBatteryReminderLevels));
        printWriter.print("mBatteryLevel=");
        printWriter.println(Integer.toString(this.mBatteryLevel));
        printWriter.print("mBatteryStatus=");
        printWriter.println(Integer.toString(this.mBatteryStatus));
        printWriter.print("mPlugType=");
        printWriter.println(Integer.toString(this.mPlugType));
        printWriter.print("mInvalidCharger=");
        printWriter.println(Integer.toString(this.mInvalidCharger));
        printWriter.print("mScreenOffTime=");
        printWriter.print(this.mScreenOffTime);
        if (this.mScreenOffTime >= 0) {
            printWriter.print(" (");
            printWriter.print(SystemClock.elapsedRealtime() - this.mScreenOffTime);
            printWriter.print(" ago)");
        }
        printWriter.println();
        printWriter.print("soundTimeout=");
        printWriter.println(Global.getInt(this.mContext.getContentResolver(), "low_battery_sound_timeout", 0));
        printWriter.print("bucket: ");
        printWriter.println(Integer.toString(findBatteryLevelBucket(this.mBatteryLevel)));
        printWriter.print("mEnableSkinTemperatureWarning=");
        printWriter.println(this.mEnableSkinTemperatureWarning);
        printWriter.print("mEnableUsbTemperatureAlarm=");
        printWriter.println(this.mEnableUsbTemperatureAlarm);
        this.mWarnings.dump(printWriter);
    }
}
