package com.android.settingslib.wifi;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkKey;
import android.net.NetworkRequest;
import android.net.NetworkRequest.Builder;
import android.net.NetworkScoreManager;
import android.net.ScoredNetwork;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkScoreCache;
import android.net.wifi.WifiNetworkScoreCache.CacheListener;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings.Global;
import com.android.settingslib.R$string;
import java.util.List;

public class WifiStatusTracker extends NetworkCallback {
    public boolean connected;
    public boolean enabled;
    public boolean isCaptivePortal;
    public int level;
    private final CacheListener mCacheListener = new CacheListener(this.mHandler) {
        public void networkCacheUpdated(List<ScoredNetwork> list) {
            WifiStatusTracker.this.updateStatusLabel();
            WifiStatusTracker.this.mCallback.run();
        }
    };
    /* access modifiers changed from: private */
    public final Runnable mCallback;
    private final ConnectivityManager mConnectivityManager;
    private final Context mContext;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final NetworkCallback mNetworkCallback = new NetworkCallback() {
        public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
            WifiStatusTracker.this.updateStatusLabel();
            WifiStatusTracker.this.mCallback.run();
        }
    };
    private final NetworkRequest mNetworkRequest = new Builder().clearCapabilities().addCapability(15).addTransportType(1).build();
    private final NetworkScoreManager mNetworkScoreManager;
    private WifiInfo mWifiInfo;
    private final WifiManager mWifiManager;
    private final WifiNetworkScoreCache mWifiNetworkScoreCache;
    public int rssi;
    public String ssid;
    public int state;
    public String statusLabel;

    public WifiStatusTracker(Context context, WifiManager wifiManager, NetworkScoreManager networkScoreManager, ConnectivityManager connectivityManager, Runnable runnable) {
        this.mContext = context;
        this.mWifiManager = wifiManager;
        this.mWifiNetworkScoreCache = new WifiNetworkScoreCache(context);
        this.mNetworkScoreManager = networkScoreManager;
        this.mConnectivityManager = connectivityManager;
        this.mCallback = runnable;
    }

    public void setListening(boolean z) {
        if (z) {
            this.mNetworkScoreManager.registerNetworkScoreCache(1, this.mWifiNetworkScoreCache, 1);
            this.mWifiNetworkScoreCache.registerListener(this.mCacheListener);
            this.mConnectivityManager.registerNetworkCallback(this.mNetworkRequest, this.mNetworkCallback, this.mHandler);
            return;
        }
        this.mNetworkScoreManager.unregisterNetworkScoreCache(1, this.mWifiNetworkScoreCache);
        this.mWifiNetworkScoreCache.unregisterListener();
        this.mConnectivityManager.unregisterNetworkCallback(this.mNetworkCallback);
    }

    public void handleBroadcast(Intent intent) {
        if (this.mWifiManager != null) {
            String action = intent.getAction();
            if (action.equals("android.net.wifi.WIFI_STATE_CHANGED")) {
                updateWifiState();
            } else if (action.equals("android.net.wifi.STATE_CHANGE")) {
                updateWifiState();
                NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
                boolean z = networkInfo != null && networkInfo.isConnected();
                this.connected = z;
                this.mWifiInfo = null;
                this.ssid = null;
                if (z) {
                    WifiInfo connectionInfo = this.mWifiManager.getConnectionInfo();
                    this.mWifiInfo = connectionInfo;
                    if (connectionInfo != null) {
                        if (connectionInfo.isPasspointAp() || this.mWifiInfo.isOsuAp()) {
                            this.ssid = this.mWifiInfo.getPasspointProviderFriendlyName();
                        } else {
                            this.ssid = getValidSsid(this.mWifiInfo);
                        }
                        updateRssi(this.mWifiInfo.getRssi());
                        maybeRequestNetworkScore();
                    }
                }
                updateStatusLabel();
            } else if (action.equals("android.net.wifi.RSSI_CHANGED")) {
                updateRssi(intent.getIntExtra("newRssi", -200));
                updateStatusLabel();
            }
        }
    }

    private void updateWifiState() {
        int wifiState = this.mWifiManager.getWifiState();
        this.state = wifiState;
        this.enabled = wifiState == 3;
    }

    private void updateRssi(int i) {
        this.rssi = i;
        this.level = this.mWifiManager.calculateSignalLevel(i);
    }

    private void maybeRequestNetworkScore() {
        NetworkKey createFromWifiInfo = NetworkKey.createFromWifiInfo(this.mWifiInfo);
        if (this.mWifiNetworkScoreCache.getScoredNetwork(createFromWifiInfo) == null) {
            this.mNetworkScoreManager.requestScores(new NetworkKey[]{createFromWifiInfo});
        }
    }

    /* access modifiers changed from: private */
    public void updateStatusLabel() {
        String str;
        NetworkCapabilities networkCapabilities = this.mConnectivityManager.getNetworkCapabilities(this.mWifiManager.getCurrentNetwork());
        this.isCaptivePortal = false;
        if (networkCapabilities != null) {
            if (networkCapabilities.hasCapability(17)) {
                this.statusLabel = this.mContext.getString(R$string.wifi_status_sign_in_required);
                this.isCaptivePortal = true;
                return;
            } else if (networkCapabilities.hasCapability(24)) {
                this.statusLabel = this.mContext.getString(R$string.wifi_limited_connection);
                return;
            } else if (!networkCapabilities.hasCapability(16)) {
                Global.getString(this.mContext.getContentResolver(), "private_dns_mode");
                if (networkCapabilities.isPrivateDnsBroken()) {
                    this.statusLabel = this.mContext.getString(R$string.private_dns_broken);
                } else {
                    this.statusLabel = this.mContext.getString(R$string.wifi_status_no_internet);
                }
                return;
            }
        }
        ScoredNetwork scoredNetwork = this.mWifiNetworkScoreCache.getScoredNetwork(NetworkKey.createFromWifiInfo(this.mWifiInfo));
        if (scoredNetwork == null) {
            str = null;
        } else {
            str = AccessPoint.getSpeedLabel(this.mContext, scoredNetwork, this.rssi);
        }
        this.statusLabel = str;
    }

    public void refreshLocale() {
        updateStatusLabel();
        this.mCallback.run();
    }

    private String getValidSsid(WifiInfo wifiInfo) {
        String ssid2 = wifiInfo.getSSID();
        if (ssid2 != null && !"<unknown ssid>".equals(ssid2)) {
            return ssid2;
        }
        List configuredNetworks = this.mWifiManager.getConfiguredNetworks();
        int size = configuredNetworks.size();
        for (int i = 0; i < size; i++) {
            if (((WifiConfiguration) configuredNetworks.get(i)).networkId == wifiInfo.getNetworkId()) {
                return ((WifiConfiguration) configuredNetworks.get(i)).SSID;
            }
        }
        return null;
    }
}
