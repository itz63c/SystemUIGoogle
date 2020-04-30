package com.android.wifitrackerlib;

import android.content.Context;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.hotspot2.OsuProvider;
import android.os.Handler;
import android.text.TextUtils;
import androidx.core.util.Preconditions;
import java.util.ArrayList;
import java.util.List;

class OsuWifiEntry extends WifiEntry {
    private final Context mContext;
    private final List<ScanResult> mCurrentScanResults = new ArrayList();
    private final String mKey;
    private int mLevel = -1;
    private OsuProvider mOsuProvider;
    private String mOsuStatusString;

    public boolean canSetAutoJoinEnabled() {
        return false;
    }

    public boolean canSetMeteredChoice() {
        return false;
    }

    public int getMeteredChoice() {
        return 0;
    }

    /* access modifiers changed from: 0000 */
    public String getScanResultDescription() {
        return "";
    }

    public int getSecurity() {
        return 0;
    }

    public WifiConfiguration getWifiConfiguration() {
        return null;
    }

    public boolean isAutoJoinEnabled() {
        return false;
    }

    public boolean isMetered() {
        return false;
    }

    public boolean isSaved() {
        return false;
    }

    public boolean isSubscription() {
        return false;
    }

    OsuWifiEntry(Context context, Handler handler, OsuProvider osuProvider, WifiManager wifiManager, boolean z) throws IllegalArgumentException {
        super(handler, wifiManager, z);
        Preconditions.checkNotNull(osuProvider, "Cannot construct with null osuProvider!");
        this.mContext = context;
        this.mOsuProvider = osuProvider;
        this.mKey = osuProviderToOsuWifiEntryKey(osuProvider);
    }

    public String getKey() {
        return this.mKey;
    }

    public String getTitle() {
        return this.mOsuProvider.getFriendlyName();
    }

    public String getSummary(boolean z) {
        String str = this.mOsuStatusString;
        return str != null ? str : this.mContext.getString(R$string.tap_to_sign_up);
    }

    public int getLevel() {
        return this.mLevel;
    }

    /* access modifiers changed from: 0000 */
    public void updateScanResultInfo(List<ScanResult> list) throws IllegalArgumentException {
        if (list == null) {
            list = new ArrayList<>();
        }
        this.mCurrentScanResults.clear();
        this.mCurrentScanResults.addAll(list);
        ScanResult bestScanResultByLevel = Utils.getBestScanResultByLevel(this.mCurrentScanResults);
        if (bestScanResultByLevel == null) {
            this.mLevel = -1;
        } else {
            this.mLevel = this.mWifiManager.calculateSignalLevel(bestScanResultByLevel.level);
        }
        notifyOnUpdated();
    }

    static String osuProviderToOsuWifiEntryKey(OsuProvider osuProvider) {
        Preconditions.checkNotNull(osuProvider, "Cannot create key with null OsuProvider!");
        StringBuilder sb = new StringBuilder();
        sb.append("OsuWifiEntry:");
        sb.append(osuProvider.getFriendlyName());
        sb.append(",");
        sb.append(osuProvider.getServerUri().toString());
        return sb.toString();
    }

    /* access modifiers changed from: protected */
    public boolean connectionInfoMatches(WifiInfo wifiInfo, NetworkInfo networkInfo) {
        return wifiInfo.isOsuAp() && TextUtils.equals(wifiInfo.getPasspointProviderFriendlyName(), this.mOsuProvider.getFriendlyName());
    }

    /* access modifiers changed from: 0000 */
    public OsuProvider getOsuProvider() {
        return this.mOsuProvider;
    }
}
