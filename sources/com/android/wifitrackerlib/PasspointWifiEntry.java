package com.android.wifitrackerlib;

import android.content.Context;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.hotspot2.PasspointConfiguration;
import android.os.Handler;
import android.text.TextUtils;
import androidx.core.util.Preconditions;
import com.android.internal.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

@VisibleForTesting
public class PasspointWifiEntry extends WifiEntry {
    private final Context mContext;
    private final List<ScanResult> mCurrentHomeScanResults = new ArrayList();
    private final List<ScanResult> mCurrentRoamingScanResults = new ArrayList();
    private String mFriendlyName;
    private final String mKey;
    private int mLevel = -1;
    private int mMeteredOverride;
    private PasspointConfiguration mPasspointConfig;
    private int mSecurity;
    protected long mSubscriptionExpirationTimeInMillis;
    private WifiConfiguration mWifiConfig;

    public boolean canSetAutoJoinEnabled() {
        return true;
    }

    public boolean canSetMeteredChoice() {
        return true;
    }

    /* access modifiers changed from: 0000 */
    public String getScanResultDescription() {
        return "";
    }

    public WifiConfiguration getWifiConfiguration() {
        return null;
    }

    public boolean isMetered() {
        return false;
    }

    public boolean isSaved() {
        return false;
    }

    public boolean isSubscription() {
        return true;
    }

    PasspointWifiEntry(Context context, Handler handler, PasspointConfiguration passpointConfiguration, WifiManager wifiManager, boolean z) throws IllegalArgumentException {
        super(handler, wifiManager, z);
        Preconditions.checkNotNull(passpointConfiguration, "Cannot construct with null PasspointConfiguration!");
        this.mContext = context;
        this.mPasspointConfig = passpointConfiguration;
        this.mKey = uniqueIdToPasspointWifiEntryKey(passpointConfiguration.getUniqueId());
        this.mFriendlyName = passpointConfiguration.getHomeSp().getFriendlyName();
        this.mSecurity = 0;
        this.mSubscriptionExpirationTimeInMillis = passpointConfiguration.getSubscriptionExpirationTimeMillis();
        this.mMeteredOverride = this.mPasspointConfig.getMeteredOverride();
    }

    public String getKey() {
        return this.mKey;
    }

    public String getTitle() {
        return this.mFriendlyName;
    }

    public String getSummary(boolean z) {
        if (isExpired()) {
            return this.mContext.getString(R$string.wifi_passpoint_expired);
        }
        StringJoiner stringJoiner = new StringJoiner(this.mContext.getString(R$string.summary_separator));
        String speedDescription = Utils.getSpeedDescription(this.mContext, this);
        if (!TextUtils.isEmpty(speedDescription)) {
            stringJoiner.add(speedDescription);
        }
        if (getConnectedState() == 0) {
            String disconnectedStateDescription = Utils.getDisconnectedStateDescription(this.mContext, this);
            if (!TextUtils.isEmpty(disconnectedStateDescription)) {
                stringJoiner.add(disconnectedStateDescription);
            } else if (z) {
                stringJoiner.add(this.mContext.getString(R$string.wifi_disconnected));
            } else if (!this.mForSavedNetworksPage) {
                stringJoiner.add(this.mContext.getString(R$string.wifi_remembered));
            }
        } else {
            String connectStateDescription = getConnectStateDescription();
            if (!TextUtils.isEmpty(connectStateDescription)) {
                stringJoiner.add(connectStateDescription);
            }
        }
        String autoConnectDescription = Utils.getAutoConnectDescription(this.mContext, this);
        if (!TextUtils.isEmpty(autoConnectDescription)) {
            stringJoiner.add(autoConnectDescription);
        }
        String meteredDescription = Utils.getMeteredDescription(this.mContext, this);
        if (!TextUtils.isEmpty(meteredDescription)) {
            stringJoiner.add(meteredDescription);
        }
        if (!z) {
            String verboseLoggingDescription = Utils.getVerboseLoggingDescription(this);
            if (!TextUtils.isEmpty(verboseLoggingDescription)) {
                stringJoiner.add(verboseLoggingDescription);
            }
        }
        return stringJoiner.toString();
    }

    private String getConnectStateDescription() {
        if (getConnectedState() == 2) {
            String currentNetworkCapabilitiesInformation = Utils.getCurrentNetworkCapabilitiesInformation(this.mContext, this.mNetworkCapabilities);
            if (!TextUtils.isEmpty(currentNetworkCapabilitiesInformation)) {
                return currentNetworkCapabilitiesInformation;
            }
        }
        return Utils.getNetworkDetailedState(this.mContext, this.mNetworkInfo);
    }

    public int getLevel() {
        return this.mLevel;
    }

    public int getSecurity() {
        return this.mSecurity;
    }

    public int getMeteredChoice() {
        int i = this.mMeteredOverride;
        if (i == 1) {
            return 1;
        }
        return i == 2 ? 2 : 0;
    }

    public boolean isAutoJoinEnabled() {
        return this.mPasspointConfig.isAutojoinEnabled();
    }

    public boolean isExpired() {
        boolean z = false;
        if (this.mSubscriptionExpirationTimeInMillis <= 0) {
            return false;
        }
        if (System.currentTimeMillis() >= this.mSubscriptionExpirationTimeInMillis) {
            z = true;
        }
        return z;
    }

    /* access modifiers changed from: 0000 */
    public void updatePasspointConfig(PasspointConfiguration passpointConfiguration) {
        this.mPasspointConfig = passpointConfiguration;
        this.mFriendlyName = passpointConfiguration.getHomeSp().getFriendlyName();
        this.mSubscriptionExpirationTimeInMillis = passpointConfiguration.getSubscriptionExpirationTimeMillis();
        this.mMeteredOverride = this.mPasspointConfig.getMeteredOverride();
        notifyOnUpdated();
    }

    /* access modifiers changed from: 0000 */
    public void updateScanResultInfo(WifiConfiguration wifiConfiguration, List<ScanResult> list, List<ScanResult> list2) throws IllegalArgumentException {
        this.mWifiConfig = wifiConfiguration;
        this.mCurrentHomeScanResults.clear();
        this.mCurrentRoamingScanResults.clear();
        if (list != null) {
            this.mCurrentHomeScanResults.addAll(list);
        }
        if (list2 != null) {
            this.mCurrentRoamingScanResults.addAll(list2);
        }
        if (this.mWifiConfig != null) {
            this.mSecurity = Utils.getSecurityTypeFromWifiConfiguration(wifiConfiguration);
            ScanResult scanResult = null;
            if (list != null && !list.isEmpty()) {
                scanResult = Utils.getBestScanResultByLevel(list);
            } else if (list2 != null && !list2.isEmpty()) {
                scanResult = Utils.getBestScanResultByLevel(list2);
            }
            if (scanResult == null) {
                this.mLevel = -1;
            } else {
                WifiConfiguration wifiConfiguration2 = this.mWifiConfig;
                StringBuilder sb = new StringBuilder();
                String str = "\"";
                sb.append(str);
                sb.append(scanResult.SSID);
                sb.append(str);
                wifiConfiguration2.SSID = sb.toString();
                this.mLevel = this.mWifiManager.calculateSignalLevel(scanResult.level);
            }
        } else {
            this.mLevel = -1;
        }
        notifyOnUpdated();
    }

    /* access modifiers changed from: protected */
    public boolean connectionInfoMatches(WifiInfo wifiInfo, NetworkInfo networkInfo) {
        if (!wifiInfo.isPasspointAp()) {
            return false;
        }
        return TextUtils.equals(wifiInfo.getPasspointFqdn(), this.mPasspointConfig.getHomeSp().getFqdn());
    }

    /* access modifiers changed from: 0000 */
    public static String uniqueIdToPasspointWifiEntryKey(String str) {
        Preconditions.checkNotNull(str, "Cannot create key with null unique id!");
        StringBuilder sb = new StringBuilder();
        sb.append("PasspointWifiEntry:");
        sb.append(str);
        return sb.toString();
    }
}
