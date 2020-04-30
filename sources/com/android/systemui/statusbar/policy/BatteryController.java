package com.android.systemui.statusbar.policy;

import com.android.systemui.DemoMode;
import com.android.systemui.Dumpable;

public interface BatteryController extends DemoMode, Dumpable, CallbackController<BatteryStateChangeCallback> {

    public interface BatteryStateChangeCallback {
        void onBatteryLevelChanged(int i, boolean z, boolean z2) {
        }

        void onPowerSaveChanged(boolean z) {
        }

        void onReverseChanged(boolean z, int i, String str) {
        }
    }

    public interface EstimateFetchCompletion {
        void onBatteryRemainingEstimateRetrieved(String str);
    }

    void getEstimatedTimeRemainingString(EstimateFetchCompletion estimateFetchCompletion) {
    }

    boolean isAodPowerSave();

    boolean isPowerSave();

    boolean isReverseSupported() {
        return false;
    }

    void setPowerSaveMode(boolean z);

    void setReverseState(boolean z) {
    }
}
