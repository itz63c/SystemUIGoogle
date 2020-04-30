package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.content.Intent;
import android.telephony.SubscriptionInfo;
import com.android.settingslib.net.DataUsageController;
import com.android.settingslib.wifi.AccessPoint;
import com.android.systemui.DemoMode;
import java.util.List;

public interface NetworkController extends CallbackController<SignalCallback>, DemoMode {

    public interface AccessPointController {

        public interface AccessPointCallback {
            void onAccessPointsChanged(List<AccessPoint> list);

            void onSettingsActivityTriggered(Intent intent);
        }

        void addAccessPointCallback(AccessPointCallback accessPointCallback);

        boolean canConfigWifi();

        boolean connect(AccessPoint accessPoint);

        int getIcon(AccessPoint accessPoint);

        void removeAccessPointCallback(AccessPointCallback accessPointCallback);

        void scanForAccessPoints();
    }

    public interface EmergencyListener {
        void setEmergencyCallsOnly(boolean z);
    }

    public static class IconState {
        public final String contentDescription;
        public final int icon;
        public final boolean visible;

        public IconState(boolean z, int i, String str) {
            this.visible = z;
            this.icon = i;
            this.contentDescription = str;
        }

        public IconState(boolean z, int i, int i2, Context context) {
            this(z, i, context.getString(i2));
        }
    }

    public interface SignalCallback {
        void setEthernetIndicators(IconState iconState) {
        }

        void setIsAirplaneMode(IconState iconState) {
        }

        void setMobileDataEnabled(boolean z) {
        }

        void setMobileDataIndicators(IconState iconState, IconState iconState2, int i, int i2, boolean z, boolean z2, CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3, boolean z3, int i3, boolean z4) {
        }

        void setNoSims(boolean z, boolean z2) {
        }

        void setSubs(List<SubscriptionInfo> list) {
        }

        void setWifiIndicators(boolean z, IconState iconState, IconState iconState2, boolean z2, boolean z3, String str, boolean z4, String str2) {
        }
    }

    void addCallback(SignalCallback signalCallback);

    AccessPointController getAccessPointController();

    DataSaverController getDataSaverController();

    DataUsageController getMobileDataController();

    String getMobileDataNetworkName();

    int getNumberSubscriptions();

    boolean hasEmergencyCryptKeeperText();

    boolean hasMobileDataFeature();

    boolean hasVoiceCallingFeature();

    boolean isRadioOn();

    void removeCallback(SignalCallback signalCallback);

    void setWifiEnabled(boolean z);
}
