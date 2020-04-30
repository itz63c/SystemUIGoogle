package com.google.android.systemui.statusbar.policy;

import android.content.Context;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.power.EnhancedEstimates;
import com.android.systemui.statusbar.policy.BatteryController.BatteryStateChangeCallback;
import com.android.systemui.statusbar.policy.BatteryControllerImpl;
import com.google.android.systemui.batteryshare.ReverseWirelessCharger;
import com.google.android.systemui.batteryshare.RtxStatusCallback;
import java.util.ArrayList;
import java.util.Optional;

public class BatteryControllerImplGoogle extends BatteryControllerImpl {
    private String mName = "";
    private boolean mReverse = false;
    private final Optional<ReverseWirelessCharger> mRtxChargerManager;
    private int mRxLevel = 0;

    BatteryControllerImplGoogle(Optional<ReverseWirelessCharger> optional, Context context, EnhancedEstimates enhancedEstimates, PowerManager powerManager, BroadcastDispatcher broadcastDispatcher, Handler handler, Handler handler2, RtxStatusCallback rtxStatusCallback) {
        super(context, enhancedEstimates, powerManager, broadcastDispatcher, handler, handler2);
        this.mRtxChargerManager = optional;
        addRtxCallback(rtxStatusCallback);
    }

    private void addRtxCallback(RtxStatusCallback rtxStatusCallback) {
        if (this.mRtxChargerManager.isPresent()) {
            ((ReverseWirelessCharger) this.mRtxChargerManager.get()).addRtxModeCallback(rtxStatusCallback);
            ((ReverseWirelessCharger) this.mRtxChargerManager.get()).addRtxStatusCallback(rtxStatusCallback);
        }
    }

    public void addCallback(BatteryStateChangeCallback batteryStateChangeCallback) {
        super.addCallback(batteryStateChangeCallback);
        batteryStateChangeCallback.onReverseChanged(this.mReverse, this.mRxLevel, this.mName);
    }

    public boolean isReverseSupported() {
        if (this.mRtxChargerManager.isPresent()) {
            return ((ReverseWirelessCharger) this.mRtxChargerManager.get()).isRtxSupported();
        }
        Log.w("BatteryControllerImplGoogle", "isReverseSupported() mRtxChargerManager is null!");
        return false;
    }

    public boolean isReverseOn() {
        if (this.mRtxChargerManager.isPresent()) {
            return ((ReverseWirelessCharger) this.mRtxChargerManager.get()).isRtxModeOn();
        }
        Log.w("BatteryControllerImplGoogle", "isReverseOn() mRtxChargerManager is null!");
        return false;
    }

    public void setReverseState(boolean z) {
        this.mReverse = z;
        if (z != isReverseOn()) {
            setRtxMode(z);
        }
        fireReverseChanged();
    }

    private void setRtxMode(boolean z) {
        if (this.mRtxChargerManager.isPresent()) {
            ((ReverseWirelessCharger) this.mRtxChargerManager.get()).setRtxMode(z);
        }
    }

    private void fireReverseChanged() {
        synchronized (this.mChangeCallbacks) {
            ArrayList arrayList = new ArrayList(this.mChangeCallbacks);
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                ((BatteryStateChangeCallback) arrayList.get(i)).onReverseChanged(this.mReverse, this.mRxLevel, this.mName);
            }
        }
    }
}
