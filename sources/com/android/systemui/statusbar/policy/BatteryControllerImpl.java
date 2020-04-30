package com.android.systemui.statusbar.policy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settingslib.fuelgauge.BatterySaverUtils;
import com.android.settingslib.fuelgauge.Estimate;
import com.android.settingslib.utils.PowerUtil;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.power.EnhancedEstimates;
import com.android.systemui.statusbar.policy.BatteryController.BatteryStateChangeCallback;
import com.android.systemui.statusbar.policy.BatteryController.EstimateFetchCompletion;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

public class BatteryControllerImpl extends BroadcastReceiver implements BatteryController {
    private static final boolean DEBUG = Log.isLoggable("BatteryController", 3);
    private boolean mAodPowerSave;
    private final Handler mBgHandler;
    private final BroadcastDispatcher mBroadcastDispatcher;
    protected final ArrayList<BatteryStateChangeCallback> mChangeCallbacks = new ArrayList<>();
    private boolean mCharged;
    private boolean mCharging;
    protected final Context mContext;
    private boolean mDemoMode;
    private Estimate mEstimate;
    private final EnhancedEstimates mEstimates;
    private final ArrayList<EstimateFetchCompletion> mFetchCallbacks = new ArrayList<>();
    private boolean mFetchingEstimate = false;
    private boolean mHasReceivedBattery = false;
    /* access modifiers changed from: private */
    public int mLevel;
    /* access modifiers changed from: private */
    public final Handler mMainHandler;
    /* access modifiers changed from: private */
    public boolean mPluggedIn;
    private final PowerManager mPowerManager;
    private boolean mPowerSave;
    /* access modifiers changed from: private */
    public boolean mTestmode = false;

    @VisibleForTesting
    protected BatteryControllerImpl(Context context, EnhancedEstimates enhancedEstimates, PowerManager powerManager, BroadcastDispatcher broadcastDispatcher, Handler handler, Handler handler2) {
        this.mContext = context;
        this.mMainHandler = handler;
        this.mBgHandler = handler2;
        this.mPowerManager = powerManager;
        this.mEstimates = enhancedEstimates;
        this.mBroadcastDispatcher = broadcastDispatcher;
        registerReceiver();
        updatePowerSave();
        updateEstimate();
    }

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.BATTERY_CHANGED");
        intentFilter.addAction("android.os.action.POWER_SAVE_MODE_CHANGED");
        intentFilter.addAction("com.android.systemui.BATTERY_LEVEL_TEST");
        this.mBroadcastDispatcher.registerReceiver(this, intentFilter);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("BatteryController state:");
        printWriter.print("  mLevel=");
        printWriter.println(this.mLevel);
        printWriter.print("  mPluggedIn=");
        printWriter.println(this.mPluggedIn);
        printWriter.print("  mCharging=");
        printWriter.println(this.mCharging);
        printWriter.print("  mCharged=");
        printWriter.println(this.mCharged);
        printWriter.print("  mPowerSave=");
        printWriter.println(this.mPowerSave);
    }

    public void setPowerSaveMode(boolean z) {
        BatterySaverUtils.setPowerSaveMode(this.mContext, z, true);
    }

    public void addCallback(BatteryStateChangeCallback batteryStateChangeCallback) {
        synchronized (this.mChangeCallbacks) {
            this.mChangeCallbacks.add(batteryStateChangeCallback);
        }
        if (this.mHasReceivedBattery) {
            batteryStateChangeCallback.onBatteryLevelChanged(this.mLevel, this.mPluggedIn, this.mCharging);
            batteryStateChangeCallback.onPowerSaveChanged(this.mPowerSave);
        }
    }

    public void removeCallback(BatteryStateChangeCallback batteryStateChangeCallback) {
        synchronized (this.mChangeCallbacks) {
            this.mChangeCallbacks.remove(batteryStateChangeCallback);
        }
    }

    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        boolean z = true;
        if (action.equals("android.intent.action.BATTERY_CHANGED")) {
            if (!this.mTestmode || intent.getBooleanExtra("testmode", false)) {
                this.mHasReceivedBattery = true;
                this.mLevel = (int) ((((float) intent.getIntExtra("level", 0)) * 100.0f) / ((float) intent.getIntExtra("scale", 100)));
                this.mPluggedIn = intent.getIntExtra("plugged", 0) != 0;
                int intExtra = intent.getIntExtra("status", 1);
                boolean z2 = intExtra == 5;
                this.mCharged = z2;
                if (!z2 && intExtra != 2) {
                    z = false;
                }
                this.mCharging = z;
                fireBatteryLevelChanged();
            }
        } else if (action.equals("android.os.action.POWER_SAVE_MODE_CHANGED")) {
            updatePowerSave();
        } else if (action.equals("com.android.systemui.BATTERY_LEVEL_TEST")) {
            this.mTestmode = true;
            this.mMainHandler.post(new Runnable() {
                int curLevel = 0;
                Intent dummy = new Intent("android.intent.action.BATTERY_CHANGED");
                int incr = 1;
                int saveLevel = BatteryControllerImpl.this.mLevel;
                boolean savePlugged = BatteryControllerImpl.this.mPluggedIn;

                public void run() {
                    int i = this.curLevel;
                    String str = "testmode";
                    String str2 = "plugged";
                    String str3 = "level";
                    int i2 = 0;
                    if (i < 0) {
                        BatteryControllerImpl.this.mTestmode = false;
                        this.dummy.putExtra(str3, this.saveLevel);
                        this.dummy.putExtra(str2, this.savePlugged);
                        this.dummy.putExtra(str, false);
                    } else {
                        this.dummy.putExtra(str3, i);
                        Intent intent = this.dummy;
                        if (this.incr > 0) {
                            i2 = 1;
                        }
                        intent.putExtra(str2, i2);
                        this.dummy.putExtra(str, true);
                    }
                    context.sendBroadcast(this.dummy);
                    if (BatteryControllerImpl.this.mTestmode) {
                        int i3 = this.curLevel;
                        int i4 = this.incr;
                        int i5 = i3 + i4;
                        this.curLevel = i5;
                        if (i5 == 100) {
                            this.incr = i4 * -1;
                        }
                        BatteryControllerImpl.this.mMainHandler.postDelayed(this, 200);
                    }
                }
            });
        }
    }

    public boolean isPowerSave() {
        return this.mPowerSave;
    }

    public boolean isAodPowerSave() {
        return this.mAodPowerSave;
    }

    public void getEstimatedTimeRemainingString(EstimateFetchCompletion estimateFetchCompletion) {
        synchronized (this.mFetchCallbacks) {
            this.mFetchCallbacks.add(estimateFetchCompletion);
        }
        updateEstimateInBackground();
    }

    private String generateTimeRemainingString() {
        synchronized (this.mFetchCallbacks) {
            if (this.mEstimate == null) {
                return null;
            }
            String batteryRemainingShortStringFormatted = PowerUtil.getBatteryRemainingShortStringFormatted(this.mContext, this.mEstimate.getEstimateMillis());
            return batteryRemainingShortStringFormatted;
        }
    }

    private void updateEstimateInBackground() {
        if (!this.mFetchingEstimate) {
            this.mFetchingEstimate = true;
            this.mBgHandler.post(new Runnable() {
                public final void run() {
                    BatteryControllerImpl.this.lambda$updateEstimateInBackground$0$BatteryControllerImpl();
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateEstimateInBackground$0 */
    public /* synthetic */ void lambda$updateEstimateInBackground$0$BatteryControllerImpl() {
        synchronized (this.mFetchCallbacks) {
            this.mEstimate = null;
            if (this.mEstimates.isHybridNotificationEnabled()) {
                updateEstimate();
            }
        }
        this.mFetchingEstimate = false;
        this.mMainHandler.post(new Runnable() {
            public final void run() {
                BatteryControllerImpl.this.notifyEstimateFetchCallbacks();
            }
        });
    }

    /* access modifiers changed from: private */
    public void notifyEstimateFetchCallbacks() {
        synchronized (this.mFetchCallbacks) {
            String generateTimeRemainingString = generateTimeRemainingString();
            Iterator it = this.mFetchCallbacks.iterator();
            while (it.hasNext()) {
                ((EstimateFetchCompletion) it.next()).onBatteryRemainingEstimateRetrieved(generateTimeRemainingString);
            }
            this.mFetchCallbacks.clear();
        }
    }

    private void updateEstimate() {
        Estimate cachedEstimateIfAvailable = Estimate.getCachedEstimateIfAvailable(this.mContext);
        this.mEstimate = cachedEstimateIfAvailable;
        if (cachedEstimateIfAvailable == null) {
            Estimate estimate = this.mEstimates.getEstimate();
            this.mEstimate = estimate;
            if (estimate != null) {
                Estimate.storeCachedEstimate(this.mContext, estimate);
            }
        }
    }

    private void updatePowerSave() {
        setPowerSave(this.mPowerManager.isPowerSaveMode());
    }

    private void setPowerSave(boolean z) {
        if (z != this.mPowerSave) {
            this.mPowerSave = z;
            this.mAodPowerSave = this.mPowerManager.getPowerSaveState(14).batterySaverEnabled;
            if (DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("Power save is ");
                sb.append(this.mPowerSave ? "on" : "off");
                Log.d("BatteryController", sb.toString());
            }
            firePowerSaveChanged();
        }
    }

    /* access modifiers changed from: protected */
    public void fireBatteryLevelChanged() {
        synchronized (this.mChangeCallbacks) {
            int size = this.mChangeCallbacks.size();
            for (int i = 0; i < size; i++) {
                ((BatteryStateChangeCallback) this.mChangeCallbacks.get(i)).onBatteryLevelChanged(this.mLevel, this.mPluggedIn, this.mCharging);
            }
        }
    }

    private void firePowerSaveChanged() {
        synchronized (this.mChangeCallbacks) {
            int size = this.mChangeCallbacks.size();
            for (int i = 0; i < size; i++) {
                ((BatteryStateChangeCallback) this.mChangeCallbacks.get(i)).onPowerSaveChanged(this.mPowerSave);
            }
        }
    }

    public void dispatchDemoCommand(String str, Bundle bundle) {
        if (!this.mDemoMode && str.equals("enter")) {
            this.mDemoMode = true;
            this.mBroadcastDispatcher.unregisterReceiver(this);
        } else if (this.mDemoMode && str.equals("exit")) {
            this.mDemoMode = false;
            registerReceiver();
            updatePowerSave();
        } else if (this.mDemoMode && str.equals("battery")) {
            String string = bundle.getString("level");
            String string2 = bundle.getString("plugged");
            String string3 = bundle.getString("powersave");
            if (string != null) {
                this.mLevel = Math.min(Math.max(Integer.parseInt(string), 0), 100);
            }
            if (string2 != null) {
                this.mPluggedIn = Boolean.parseBoolean(string2);
            }
            if (string3 != null) {
                this.mPowerSave = string3.equals("true");
                firePowerSaveChanged();
            }
            fireBatteryLevelChanged();
        }
    }
}
