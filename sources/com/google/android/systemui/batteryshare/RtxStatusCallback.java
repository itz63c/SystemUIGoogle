package com.google.android.systemui.batteryshare;

import com.android.systemui.statusbar.policy.BatteryController;
import com.google.android.systemui.batteryshare.ReverseWirelessCharger.RtxModeCallback;
import dagger.Lazy;
import vendor.google.wireless_charger.V1_2.RtxStatusInfo;

public class RtxStatusCallback implements com.google.android.systemui.batteryshare.ReverseWirelessCharger.RtxStatusCallback, RtxModeCallback {
    private final Lazy<BatteryController> mBatteryControllerLazy;

    public RtxStatusCallback(Lazy<BatteryController> lazy) {
        this.mBatteryControllerLazy = lazy;
    }

    public void onRtxStatusChanged(RtxStatusInfo rtxStatusInfo) {
        boolean z = true;
        if (rtxStatusInfo.mode != 1) {
            z = false;
        }
        setReverseState(z);
    }

    public void onRtxModeChanged(RtxStatusInfo rtxStatusInfo) {
        boolean z = true;
        if (rtxStatusInfo.mode != 1) {
            z = false;
        }
        setReverseState(z);
    }

    private void setReverseState(boolean z) {
        ((BatteryController) this.mBatteryControllerLazy.get()).setReverseState(z);
    }
}
