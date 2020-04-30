package com.android.systemui.power;

import com.android.settingslib.fuelgauge.Estimate;

public class EnhancedEstimatesImpl implements EnhancedEstimates {
    public Estimate getEstimate() {
        return null;
    }

    public boolean getLowWarningEnabled() {
        return true;
    }

    public long getLowWarningThreshold() {
        return 0;
    }

    public long getSevereWarningThreshold() {
        return 0;
    }

    public boolean isHybridNotificationEnabled() {
        return false;
    }
}
