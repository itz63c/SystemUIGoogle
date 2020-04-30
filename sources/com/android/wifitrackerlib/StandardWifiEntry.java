package com.android.wifitrackerlib;

import android.content.Context;
import android.net.NetworkInfo;
import android.net.NetworkScoreManager;
import android.net.NetworkScorerAppData;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import androidx.core.util.Preconditions;
import com.android.internal.annotations.VisibleForTesting;
import com.android.wifitrackerlib.WifiEntry.ConnectedInfo;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@VisibleForTesting
public class StandardWifiEntry extends WifiEntry {
    private final Context mContext;
    private final List<ScanResult> mCurrentScanResults;
    private final String mKey;
    private String mRecommendationServiceLabel;
    private final int mSecurity;
    private final String mSsid;
    private WifiConfiguration mWifiConfig;

    public boolean isSubscription() {
        return false;
    }

    StandardWifiEntry(Context context, Handler handler, String str, List<ScanResult> list, WifiManager wifiManager, boolean z) throws IllegalArgumentException {
        this(context, handler, str, wifiManager, z);
        Preconditions.checkNotNull(list, "Cannot construct with null ScanResult list!");
        if (!list.isEmpty()) {
            updateScanResultInfo(list);
            updateRecommendationServiceLabel();
            return;
        }
        throw new IllegalArgumentException("Cannot construct with empty ScanResult list!");
    }

    StandardWifiEntry(Context context, Handler handler, String str, WifiConfiguration wifiConfiguration, WifiManager wifiManager, boolean z) throws IllegalArgumentException {
        this(context, handler, str, wifiManager, z);
        Preconditions.checkNotNull(wifiConfiguration, "Cannot construct with null config!");
        Preconditions.checkNotNull(wifiConfiguration.SSID, "Supplied config must have an SSID!");
        this.mWifiConfig = wifiConfiguration;
        updateRecommendationServiceLabel();
    }

    StandardWifiEntry(Context context, Handler handler, String str, WifiManager wifiManager, boolean z) {
        super(handler, wifiManager, z);
        this.mCurrentScanResults = new ArrayList();
        if (str.startsWith("StandardWifiEntry:")) {
            this.mContext = context;
            this.mKey = str;
            try {
                int lastIndexOf = str.lastIndexOf(",");
                this.mSsid = str.substring(18, lastIndexOf);
                this.mSecurity = Integer.valueOf(str.substring(lastIndexOf + 1)).intValue();
                updateRecommendationServiceLabel();
            } catch (NumberFormatException | StringIndexOutOfBoundsException unused) {
                StringBuilder sb = new StringBuilder();
                sb.append("Malformed key: ");
                sb.append(str);
                throw new IllegalArgumentException(sb.toString());
            }
        } else {
            throw new IllegalArgumentException("Key does not start with correct prefix!");
        }
    }

    public String getKey() {
        return this.mKey;
    }

    public String getTitle() {
        return this.mSsid;
    }

    public String getSummary(boolean z) {
        StringJoiner stringJoiner = new StringJoiner(this.mContext.getString(R$string.summary_separator));
        String speedDescription = Utils.getSpeedDescription(this.mContext, this);
        if (!TextUtils.isEmpty(speedDescription)) {
            stringJoiner.add(speedDescription);
        }
        if (!z && this.mForSavedNetworksPage && isSaved()) {
            CharSequence appLabelForSavedNetwork = Utils.getAppLabelForSavedNetwork(this.mContext, this);
            if (!TextUtils.isEmpty(appLabelForSavedNetwork)) {
                stringJoiner.add(this.mContext.getString(R$string.saved_network, new Object[]{appLabelForSavedNetwork}));
            }
        }
        if (getConnectedState() == 0) {
            String disconnectedStateDescription = Utils.getDisconnectedStateDescription(this.mContext, this);
            if (!TextUtils.isEmpty(disconnectedStateDescription)) {
                stringJoiner.add(disconnectedStateDescription);
            } else if (z) {
                stringJoiner.add(this.mContext.getString(R$string.wifi_disconnected));
            } else if (!this.mForSavedNetworksPage && isSaved()) {
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
            if (!isSaved()) {
                WifiInfo wifiInfo = this.mWifiInfo;
                String requestingPackageName = wifiInfo != null ? wifiInfo.getRequestingPackageName() : null;
                if (!TextUtils.isEmpty(requestingPackageName)) {
                    Context context = this.mContext;
                    return context.getString(R$string.connected_via_app, new Object[]{Utils.getAppLabel(context, requestingPackageName)});
                } else if (TextUtils.isEmpty(this.mRecommendationServiceLabel)) {
                    return this.mContext.getString(R$string.connected_via_network_scorer_default);
                } else {
                    return String.format(this.mContext.getString(R$string.connected_via_network_scorer), new Object[]{this.mRecommendationServiceLabel});
                }
            } else {
                String currentNetworkCapabilitiesInformation = Utils.getCurrentNetworkCapabilitiesInformation(this.mContext, this.mNetworkCapabilities);
                if (!TextUtils.isEmpty(currentNetworkCapabilitiesInformation)) {
                    return currentNetworkCapabilitiesInformation;
                }
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

    public boolean isMetered() {
        if (getMeteredChoice() == 1) {
            return true;
        }
        WifiConfiguration wifiConfiguration = this.mWifiConfig;
        return wifiConfiguration != null && wifiConfiguration.meteredHint;
    }

    public boolean isSaved() {
        return this.mWifiConfig != null;
    }

    public WifiConfiguration getWifiConfiguration() {
        return this.mWifiConfig;
    }

    public ConnectedInfo getConnectedInfo() {
        return this.mConnectedInfo;
    }

    public int getMeteredChoice() {
        WifiConfiguration wifiConfiguration = this.mWifiConfig;
        if (wifiConfiguration != null) {
            int i = wifiConfiguration.meteredOverride;
            if (i == 1) {
                return 1;
            }
            if (i == 2) {
                return 2;
            }
        }
        return 0;
    }

    public boolean canSetMeteredChoice() {
        return isSaved();
    }

    public boolean isAutoJoinEnabled() {
        WifiConfiguration wifiConfiguration = this.mWifiConfig;
        if (wifiConfiguration == null) {
            return false;
        }
        return wifiConfiguration.allowAutojoin;
    }

    public boolean canSetAutoJoinEnabled() {
        return isSaved();
    }

    /* access modifiers changed from: 0000 */
    public void updateScanResultInfo(List<ScanResult> list) throws IllegalArgumentException {
        if (list == null) {
            list = new ArrayList<>();
        }
        for (ScanResult scanResult : list) {
            if (!TextUtils.equals(scanResult.SSID, this.mSsid)) {
                StringBuilder sb = new StringBuilder();
                sb.append("Attempted to update with wrong SSID! Expected: ");
                sb.append(this.mSsid);
                sb.append(", Actual: ");
                sb.append(scanResult.SSID);
                sb.append(", ScanResult: ");
                sb.append(scanResult);
                throw new IllegalArgumentException(sb.toString());
            }
        }
        this.mCurrentScanResults.clear();
        this.mCurrentScanResults.addAll(list);
        ScanResult bestScanResultByLevel = Utils.getBestScanResultByLevel(this.mCurrentScanResults);
        if (bestScanResultByLevel == null) {
            this.mLevel = -1;
        } else {
            this.mLevel = this.mWifiManager.calculateSignalLevel(bestScanResultByLevel.level);
            updateEapType(bestScanResultByLevel);
            updatePskType(bestScanResultByLevel);
        }
        notifyOnUpdated();
    }

    private void updateEapType(ScanResult scanResult) {
        if (!scanResult.capabilities.contains("RSN-EAP")) {
            boolean contains = scanResult.capabilities.contains("WPA-EAP");
        }
    }

    private void updatePskType(ScanResult scanResult) {
        if (this.mSecurity == 2) {
            boolean contains = scanResult.capabilities.contains("WPA-PSK");
            boolean contains2 = scanResult.capabilities.contains("RSN-PSK");
        }
    }

    /* access modifiers changed from: 0000 */
    public void updateConfig(WifiConfiguration wifiConfiguration) throws IllegalArgumentException {
        if (wifiConfiguration != null) {
            String str = ", Config: ";
            String str2 = ", Actual: ";
            if (!TextUtils.equals(this.mSsid, WifiInfo.sanitizeSsid(wifiConfiguration.SSID))) {
                StringBuilder sb = new StringBuilder();
                sb.append("Attempted to update with wrong SSID! Expected: ");
                sb.append(this.mSsid);
                sb.append(str2);
                sb.append(WifiInfo.sanitizeSsid(wifiConfiguration.SSID));
                sb.append(str);
                sb.append(wifiConfiguration);
                throw new IllegalArgumentException(sb.toString());
            } else if (this.mSecurity != Utils.getSecurityTypeFromWifiConfiguration(wifiConfiguration)) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Attempted to update with wrong security! Expected: ");
                sb2.append(this.mSecurity);
                sb2.append(str2);
                sb2.append(Utils.getSecurityTypeFromWifiConfiguration(wifiConfiguration));
                sb2.append(str);
                sb2.append(wifiConfiguration);
                throw new IllegalArgumentException(sb2.toString());
            }
        }
        this.mWifiConfig = wifiConfiguration;
        notifyOnUpdated();
    }

    /* access modifiers changed from: protected */
    public boolean connectionInfoMatches(WifiInfo wifiInfo, NetworkInfo networkInfo) {
        if (wifiInfo.isPasspointAp() || wifiInfo.isOsuAp()) {
            return false;
        }
        WifiConfiguration wifiConfiguration = this.mWifiConfig;
        if (wifiConfiguration == null || wifiConfiguration.networkId != wifiInfo.getNetworkId()) {
            return false;
        }
        return true;
    }

    private void updateRecommendationServiceLabel() {
        NetworkScorerAppData activeScorer = ((NetworkScoreManager) this.mContext.getSystemService("network_score")).getActiveScorer();
        if (activeScorer != null) {
            this.mRecommendationServiceLabel = activeScorer.getRecommendationServiceLabel();
        }
    }

    static String ssidAndSecurityToStandardWifiEntryKey(String str, int i) {
        StringBuilder sb = new StringBuilder();
        sb.append("StandardWifiEntry:");
        sb.append(str);
        sb.append(",");
        sb.append(i);
        return sb.toString();
    }

    static String wifiConfigToStandardWifiEntryKey(WifiConfiguration wifiConfiguration) {
        Preconditions.checkNotNull(wifiConfiguration, "Cannot create key with null config!");
        Preconditions.checkNotNull(wifiConfiguration.SSID, "Cannot create key with null SSID in config!");
        StringBuilder sb = new StringBuilder();
        sb.append("StandardWifiEntry:");
        sb.append(WifiInfo.sanitizeSsid(wifiConfiguration.SSID));
        sb.append(",");
        sb.append(Utils.getSecurityTypeFromWifiConfiguration(wifiConfiguration));
        return sb.toString();
    }

    /* access modifiers changed from: 0000 */
    public String getScanResultDescription() {
        if (this.mCurrentScanResults.size() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(getScanResultDescription(2400, 2500));
        String str = ";";
        sb.append(str);
        sb.append(getScanResultDescription(4900, 5900));
        sb.append(str);
        sb.append(getScanResultDescription(5700, 7100));
        sb.append("]");
        return sb.toString();
    }

    private String getScanResultDescription(int i, int i2) {
        List list = (List) this.mCurrentScanResults.stream().filter(new Predicate(i, i2) {
            public final /* synthetic */ int f$0;
            public final /* synthetic */ int f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final boolean test(Object obj) {
                return StandardWifiEntry.lambda$getScanResultDescription$2(this.f$0, this.f$1, (ScanResult) obj);
            }
        }).sorted(Comparator.comparingInt($$Lambda$StandardWifiEntry$Lr4BrIBW8EpwljEjYsXvjwUzPU.INSTANCE)).collect(Collectors.toList());
        int size = list.size();
        if (size == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(size);
        sb.append(")");
        if (size > 4) {
            int asInt = list.stream().mapToInt($$Lambda$StandardWifiEntry$ulMGK6KYyQVXHFy8lpHK9UIg2Q4.INSTANCE).max().getAsInt();
            sb.append("max=");
            sb.append(asInt);
            sb.append(",");
        }
        list.forEach(new Consumer(sb, SystemClock.elapsedRealtime()) {
            public final /* synthetic */ StringBuilder f$1;
            public final /* synthetic */ long f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void accept(Object obj) {
                StandardWifiEntry.this.lambda$getScanResultDescription$5$StandardWifiEntry(this.f$1, this.f$2, (ScanResult) obj);
            }
        });
        return sb.toString();
    }

    static /* synthetic */ boolean lambda$getScanResultDescription$2(int i, int i2, ScanResult scanResult) {
        int i3 = scanResult.frequency;
        return i3 >= i && i3 <= i2;
    }

    static /* synthetic */ int lambda$getScanResultDescription$3(ScanResult scanResult) {
        return scanResult.level * -1;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$getScanResultDescription$5 */
    public /* synthetic */ void lambda$getScanResultDescription$5$StandardWifiEntry(StringBuilder sb, long j, ScanResult scanResult) {
        sb.append(getScanResultDescription(scanResult, j));
    }

    private String getScanResultDescription(ScanResult scanResult, long j) {
        StringBuilder sb = new StringBuilder();
        sb.append(" \n{");
        sb.append(scanResult.BSSID);
        WifiInfo wifiInfo = this.mWifiInfo;
        if (wifiInfo != null && scanResult.BSSID.equals(wifiInfo.getBSSID())) {
            sb.append("*");
        }
        sb.append("=");
        sb.append(scanResult.frequency);
        String str = ",";
        sb.append(str);
        sb.append(scanResult.level);
        int i = ((int) (j - (scanResult.timestamp / 1000))) / 1000;
        sb.append(str);
        sb.append(i);
        sb.append("s");
        sb.append("}");
        return sb.toString();
    }
}
