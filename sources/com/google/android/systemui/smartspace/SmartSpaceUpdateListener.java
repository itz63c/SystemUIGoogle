package com.google.android.systemui.smartspace;

public interface SmartSpaceUpdateListener {
    void onGsaChanged() {
    }

    void onSensitiveModeChanged(boolean z, boolean z2) {
    }

    void onSmartSpaceUpdated(SmartSpaceData smartSpaceData);
}
