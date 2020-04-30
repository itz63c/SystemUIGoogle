package com.android.systemui.statusbar.policy;

public interface LocationController extends CallbackController<LocationChangeCallback> {

    public interface LocationChangeCallback {
        void onLocationActiveChanged(boolean z) {
        }

        void onLocationSettingsChanged(boolean z) {
        }
    }

    boolean isLocationActive();

    boolean isLocationEnabled();

    boolean setLocationEnabled(boolean z);
}
