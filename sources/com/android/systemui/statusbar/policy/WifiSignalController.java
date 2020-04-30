package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkScoreManager;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.TrafficStateCallback;
import android.text.TextUtils;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settingslib.wifi.WifiStatusTracker;
import com.android.systemui.C2007R$bool;
import com.android.systemui.C2010R$drawable;
import com.android.systemui.C2017R$string;
import com.android.systemui.statusbar.policy.NetworkController.IconState;
import com.android.systemui.statusbar.policy.NetworkController.SignalCallback;
import java.util.Objects;

public class WifiSignalController extends SignalController<WifiState, IconGroup> {
    private final boolean mHasMobileData;
    private final WifiStatusTracker mWifiTracker;

    static class WifiState extends State {
        boolean isTransient;
        String ssid;
        String statusLabel;

        WifiState() {
        }

        public void copyFrom(State state) {
            super.copyFrom(state);
            WifiState wifiState = (WifiState) state;
            this.ssid = wifiState.ssid;
            this.isTransient = wifiState.isTransient;
            this.statusLabel = wifiState.statusLabel;
        }

        /* access modifiers changed from: protected */
        public void toString(StringBuilder sb) {
            super.toString(sb);
            sb.append(",ssid=");
            sb.append(this.ssid);
            sb.append(",isTransient=");
            sb.append(this.isTransient);
            sb.append(",statusLabel=");
            sb.append(this.statusLabel);
        }

        public boolean equals(Object obj) {
            boolean z = false;
            if (!super.equals(obj)) {
                return false;
            }
            WifiState wifiState = (WifiState) obj;
            if (Objects.equals(wifiState.ssid, this.ssid) && wifiState.isTransient == this.isTransient && TextUtils.equals(wifiState.statusLabel, this.statusLabel)) {
                z = true;
            }
            return z;
        }
    }

    private class WifiTrafficStateCallback implements TrafficStateCallback {
        private WifiTrafficStateCallback() {
        }

        public void onStateChanged(int i) {
            WifiSignalController.this.setActivity(i);
        }
    }

    public WifiSignalController(Context context, boolean z, CallbackHandler callbackHandler, NetworkControllerImpl networkControllerImpl, WifiManager wifiManager) {
        super("WifiSignalController", context, 1, callbackHandler, networkControllerImpl);
        WifiManager wifiManager2 = wifiManager;
        WifiStatusTracker wifiStatusTracker = new WifiStatusTracker(this.mContext, wifiManager2, (NetworkScoreManager) context.getSystemService(NetworkScoreManager.class), (ConnectivityManager) context.getSystemService(ConnectivityManager.class), new Runnable() {
            public final void run() {
                WifiSignalController.this.handleStatusUpdated();
            }
        });
        this.mWifiTracker = wifiStatusTracker;
        wifiStatusTracker.setListening(true);
        this.mHasMobileData = z;
        if (wifiManager != null) {
            wifiManager.registerTrafficStateCallback(context.getMainExecutor(), new WifiTrafficStateCallback());
        }
        WifiState wifiState = (WifiState) this.mCurrentState;
        WifiState wifiState2 = (WifiState) this.mLastState;
        IconGroup iconGroup = new IconGroup("Wi-Fi Icons", WifiIcons.WIFI_SIGNAL_STRENGTH, WifiIcons.QS_WIFI_SIGNAL_STRENGTH, AccessibilityContentDescriptions.WIFI_CONNECTION_STRENGTH, 17302861, 17302861, 17302861, 17302861, AccessibilityContentDescriptions.WIFI_NO_CONNECTION);
        wifiState2.iconGroup = iconGroup;
        wifiState.iconGroup = iconGroup;
    }

    /* access modifiers changed from: protected */
    public WifiState cleanState() {
        return new WifiState();
    }

    /* access modifiers changed from: 0000 */
    public void refreshLocale() {
        this.mWifiTracker.refreshLocale();
    }

    public void notifyListeners(SignalCallback signalCallback) {
        int i;
        boolean z = this.mContext.getResources().getBoolean(C2007R$bool.config_showWifiIndicatorWhenEnabled);
        T t = this.mCurrentState;
        boolean z2 = ((WifiState) t).enabled && ((((WifiState) t).connected && ((WifiState) t).inetCondition == 1) || !this.mHasMobileData || z);
        T t2 = this.mCurrentState;
        String str = ((WifiState) t2).connected ? ((WifiState) t2).ssid : null;
        boolean z3 = z2 && ((WifiState) this.mCurrentState).ssid != null;
        String charSequence = getTextIfExists(getContentDescription()).toString();
        if (((WifiState) this.mCurrentState).inetCondition == 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(charSequence);
            sb.append(",");
            sb.append(this.mContext.getString(C2017R$string.data_connection_no_internet));
            charSequence = sb.toString();
        }
        IconState iconState = new IconState(z2, getCurrentIconId(), charSequence);
        boolean z4 = ((WifiState) this.mCurrentState).connected;
        if (this.mWifiTracker.isCaptivePortal) {
            i = C2010R$drawable.ic_qs_wifi_disconnected;
        } else {
            i = getQsCurrentIconId();
        }
        IconState iconState2 = new IconState(z4, i, charSequence);
        T t3 = this.mCurrentState;
        boolean z5 = ((WifiState) t3).enabled;
        boolean z6 = z3 && ((WifiState) t3).activityIn;
        boolean z7 = z3 && ((WifiState) this.mCurrentState).activityOut;
        T t4 = this.mCurrentState;
        signalCallback.setWifiIndicators(z5, iconState, iconState2, z6, z7, str, ((WifiState) t4).isTransient, ((WifiState) t4).statusLabel);
    }

    public void handleBroadcast(Intent intent) {
        this.mWifiTracker.handleBroadcast(intent);
        T t = this.mCurrentState;
        WifiState wifiState = (WifiState) t;
        WifiStatusTracker wifiStatusTracker = this.mWifiTracker;
        wifiState.enabled = wifiStatusTracker.enabled;
        ((WifiState) t).connected = wifiStatusTracker.connected;
        ((WifiState) t).ssid = wifiStatusTracker.ssid;
        ((WifiState) t).rssi = wifiStatusTracker.rssi;
        ((WifiState) t).level = wifiStatusTracker.level;
        ((WifiState) t).statusLabel = wifiStatusTracker.statusLabel;
        notifyListenersIfNecessary();
    }

    /* access modifiers changed from: private */
    public void handleStatusUpdated() {
        ((WifiState) this.mCurrentState).statusLabel = this.mWifiTracker.statusLabel;
        notifyListenersIfNecessary();
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void setActivity(int i) {
        boolean z = false;
        ((WifiState) this.mCurrentState).activityIn = i == 3 || i == 1;
        WifiState wifiState = (WifiState) this.mCurrentState;
        if (i == 3 || i == 2) {
            z = true;
        }
        wifiState.activityOut = z;
        notifyListenersIfNecessary();
    }
}
