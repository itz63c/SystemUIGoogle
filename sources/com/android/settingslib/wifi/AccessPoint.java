package com.android.settingslib.wifi;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.NetworkInfo.State;
import android.net.NetworkScoreManager;
import android.net.NetworkScorerAppData;
import android.net.ScoredNetwork;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.ActionListener;
import android.net.wifi.WifiNetworkScoreCache;
import android.net.wifi.hotspot2.OsuProvider;
import android.net.wifi.hotspot2.PasspointConfiguration;
import android.net.wifi.hotspot2.ProvisioningCallback;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings.Global;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Log;
import android.util.Pair;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.CollectionUtils;
import com.android.settingslib.R$array;
import com.android.settingslib.R$string;
import com.android.settingslib.utils.ThreadUtils;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class AccessPoint implements Comparable<AccessPoint> {
    private String bssid;
    AccessPointListener mAccessPointListener;
    private WifiConfiguration mConfig;
    /* access modifiers changed from: private */
    public ActionListener mConnectListener;
    /* access modifiers changed from: private */
    public final Context mContext;
    private final ArraySet<ScanResult> mExtraScanResults;
    private WifiInfo mInfo;
    private boolean mIsOweTransitionMode;
    private boolean mIsPskSaeTransitionMode;
    private boolean mIsScoredNetworkMetered;
    private String mKey;
    private final Object mLock;
    private NetworkInfo mNetworkInfo;
    /* access modifiers changed from: private */
    public String mOsuFailure;
    /* access modifiers changed from: private */
    public OsuProvider mOsuProvider;
    /* access modifiers changed from: private */
    public boolean mOsuProvisioningComplete;
    /* access modifiers changed from: private */
    public String mOsuStatus;
    private int mPasspointConfigurationVersion;
    private String mPasspointUniqueId;
    private String mProviderFriendlyName;
    private int mRssi;
    private final ArraySet<ScanResult> mScanResults;
    private final Map<String, TimestampedScoredNetwork> mScoredNetworkCache;
    private int mSpeed;
    private long mSubscriptionExpirationTimeInMillis;
    private Object mTag;
    private WifiManager mWifiManager;
    private int networkId;
    private int pskType;
    private int security;
    private String ssid;

    public interface AccessPointListener {
        void onAccessPointChanged(AccessPoint accessPoint);

        void onLevelChanged(AccessPoint accessPoint);
    }

    @VisibleForTesting
    class AccessPointProvisioningCallback extends ProvisioningCallback {
        final /* synthetic */ AccessPoint this$0;

        public void onProvisioningFailure(int i) {
            if (TextUtils.equals(this.this$0.mOsuStatus, this.this$0.mContext.getString(R$string.osu_completing_sign_up))) {
                AccessPoint accessPoint = this.this$0;
                accessPoint.mOsuFailure = accessPoint.mContext.getString(R$string.osu_sign_up_failed);
            } else {
                AccessPoint accessPoint2 = this.this$0;
                accessPoint2.mOsuFailure = accessPoint2.mContext.getString(R$string.osu_connect_failed);
            }
            this.this$0.mOsuStatus = null;
            this.this$0.mOsuProvisioningComplete = false;
            ThreadUtils.postOnMainThread(new Runnable() {
                public final void run() {
                    AccessPointProvisioningCallback.this.mo9284x1703172f();
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onProvisioningFailure$0 */
        public /* synthetic */ void mo9284x1703172f() {
            AccessPoint accessPoint = this.this$0;
            AccessPointListener accessPointListener = accessPoint.mAccessPointListener;
            if (accessPointListener != null) {
                accessPointListener.onAccessPointChanged(accessPoint);
            }
        }

        public void onProvisioningStatus(int i) {
            String str;
            switch (i) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    str = String.format(this.this$0.mContext.getString(R$string.osu_opening_provider), new Object[]{this.this$0.mOsuProvider.getFriendlyName()});
                    break;
                case 8:
                case 9:
                case 10:
                case 11:
                    str = this.this$0.mContext.getString(R$string.osu_completing_sign_up);
                    break;
                default:
                    str = null;
                    break;
            }
            boolean equals = true ^ TextUtils.equals(this.this$0.mOsuStatus, str);
            this.this$0.mOsuStatus = str;
            this.this$0.mOsuFailure = null;
            this.this$0.mOsuProvisioningComplete = false;
            if (equals) {
                ThreadUtils.postOnMainThread(new Runnable() {
                    public final void run() {
                        AccessPointProvisioningCallback.this.mo9285x8b585e0a();
                    }
                });
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onProvisioningStatus$1 */
        public /* synthetic */ void mo9285x8b585e0a() {
            AccessPoint accessPoint = this.this$0;
            AccessPointListener accessPointListener = accessPoint.mAccessPointListener;
            if (accessPointListener != null) {
                accessPointListener.onAccessPointChanged(accessPoint);
            }
        }

        public void onProvisioningComplete() {
            this.this$0.mOsuProvisioningComplete = true;
            this.this$0.mOsuFailure = null;
            this.this$0.mOsuStatus = null;
            ThreadUtils.postOnMainThread(new Runnable() {
                public final void run() {
                    AccessPointProvisioningCallback.this.mo9283x4793df52();
                }
            });
            WifiManager access$500 = this.this$0.getWifiManager();
            PasspointConfiguration passpointConfiguration = (PasspointConfiguration) access$500.getMatchingPasspointConfigsForOsuProviders(Collections.singleton(this.this$0.mOsuProvider)).get(this.this$0.mOsuProvider);
            if (passpointConfiguration == null) {
                Log.e("SettingsLib.AccessPoint", "Missing PasspointConfiguration for newly provisioned network!");
                if (this.this$0.mConnectListener != null) {
                    this.this$0.mConnectListener.onFailure(0);
                }
                return;
            }
            String uniqueId = passpointConfiguration.getUniqueId();
            for (Pair pair : access$500.getAllMatchingWifiConfigs(access$500.getScanResults())) {
                WifiConfiguration wifiConfiguration = (WifiConfiguration) pair.first;
                if (TextUtils.equals(wifiConfiguration.getKey(), uniqueId)) {
                    access$500.connect(new AccessPoint(this.this$0.mContext, wifiConfiguration, (List) ((Map) pair.second).get(Integer.valueOf(0)), (List) ((Map) pair.second).get(Integer.valueOf(1))).getConfig(), this.this$0.mConnectListener);
                    return;
                }
            }
            if (this.this$0.mConnectListener != null) {
                this.this$0.mConnectListener.onFailure(0);
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onProvisioningComplete$2 */
        public /* synthetic */ void mo9283x4793df52() {
            AccessPoint accessPoint = this.this$0;
            AccessPointListener accessPointListener = accessPoint.mAccessPointListener;
            if (accessPointListener != null) {
                accessPointListener.onAccessPointChanged(accessPoint);
            }
        }
    }

    private static int roundToClosestSpeedEnum(int i) {
        if (i < 5) {
            return 0;
        }
        if (i < 7) {
            return 5;
        }
        if (i < 15) {
            return 10;
        }
        return i < 25 ? 20 : 30;
    }

    public static String securityToString(int i, int i2) {
        return i == 1 ? "WEP" : i == 2 ? i2 == 1 ? "WPA" : i2 == 2 ? "WPA2" : i2 == 3 ? "WPA_WPA2" : "PSK" : i == 3 ? "EAP" : i == 5 ? "SAE" : i == 6 ? "SUITE_B" : i == 4 ? "OWE" : "NONE";
    }

    static {
        new AtomicInteger(0);
    }

    public AccessPoint(Context context, Bundle bundle) {
        this.mLock = new Object();
        this.mScanResults = new ArraySet<>();
        this.mExtraScanResults = new ArraySet<>();
        this.mScoredNetworkCache = new HashMap();
        this.networkId = -1;
        this.pskType = 0;
        this.mRssi = Integer.MIN_VALUE;
        this.mSpeed = 0;
        this.mIsScoredNetworkMetered = false;
        this.mPasspointConfigurationVersion = 0;
        this.mOsuProvisioningComplete = false;
        this.mIsPskSaeTransitionMode = false;
        this.mIsOweTransitionMode = false;
        this.mContext = context;
        String str = "key_config";
        if (bundle.containsKey(str)) {
            this.mConfig = (WifiConfiguration) bundle.getParcelable(str);
        }
        WifiConfiguration wifiConfiguration = this.mConfig;
        if (wifiConfiguration != null) {
            loadConfig(wifiConfiguration);
        }
        String str2 = "key_ssid";
        if (bundle.containsKey(str2)) {
            this.ssid = bundle.getString(str2);
        }
        String str3 = "key_security";
        if (bundle.containsKey(str3)) {
            this.security = bundle.getInt(str3);
        }
        String str4 = "key_speed";
        if (bundle.containsKey(str4)) {
            this.mSpeed = bundle.getInt(str4);
        }
        String str5 = "key_psktype";
        if (bundle.containsKey(str5)) {
            this.pskType = bundle.getInt(str5);
        }
        String str6 = "eap_psktype";
        if (bundle.containsKey(str6)) {
            bundle.getInt(str6);
        }
        this.mInfo = (WifiInfo) bundle.getParcelable("key_wifiinfo");
        String str7 = "key_networkinfo";
        if (bundle.containsKey(str7)) {
            this.mNetworkInfo = (NetworkInfo) bundle.getParcelable(str7);
        }
        String str8 = "key_scanresults";
        if (bundle.containsKey(str8)) {
            Parcelable[] parcelableArray = bundle.getParcelableArray(str8);
            this.mScanResults.clear();
            for (Parcelable parcelable : parcelableArray) {
                this.mScanResults.add((ScanResult) parcelable);
            }
        }
        String str9 = "key_scorednetworkcache";
        if (bundle.containsKey(str9)) {
            Iterator it = bundle.getParcelableArrayList(str9).iterator();
            while (it.hasNext()) {
                TimestampedScoredNetwork timestampedScoredNetwork = (TimestampedScoredNetwork) it.next();
                this.mScoredNetworkCache.put(timestampedScoredNetwork.getScore().networkKey.wifiKey.bssid, timestampedScoredNetwork);
            }
        }
        String str10 = "key_passpoint_unique_id";
        if (bundle.containsKey(str10)) {
            this.mPasspointUniqueId = bundle.getString(str10);
        }
        String str11 = "key_fqdn";
        if (bundle.containsKey(str11)) {
            bundle.getString(str11);
        }
        String str12 = "key_provider_friendly_name";
        if (bundle.containsKey(str12)) {
            this.mProviderFriendlyName = bundle.getString(str12);
        }
        String str13 = "key_subscription_expiration_time_in_millis";
        if (bundle.containsKey(str13)) {
            this.mSubscriptionExpirationTimeInMillis = bundle.getLong(str13);
        }
        String str14 = "key_passpoint_configuration_version";
        if (bundle.containsKey(str14)) {
            this.mPasspointConfigurationVersion = bundle.getInt(str14);
        }
        String str15 = "key_is_psk_sae_transition_mode";
        if (bundle.containsKey(str15)) {
            this.mIsPskSaeTransitionMode = bundle.getBoolean(str15);
        }
        String str16 = "key_is_owe_transition_mode";
        if (bundle.containsKey(str16)) {
            this.mIsOweTransitionMode = bundle.getBoolean(str16);
        }
        update(this.mConfig, this.mInfo, this.mNetworkInfo);
        updateKey();
        updateBestRssiInfo();
    }

    public AccessPoint(Context context, WifiConfiguration wifiConfiguration) {
        this.mLock = new Object();
        this.mScanResults = new ArraySet<>();
        this.mExtraScanResults = new ArraySet<>();
        this.mScoredNetworkCache = new HashMap();
        this.networkId = -1;
        this.pskType = 0;
        this.mRssi = Integer.MIN_VALUE;
        this.mSpeed = 0;
        this.mIsScoredNetworkMetered = false;
        this.mPasspointConfigurationVersion = 0;
        this.mOsuProvisioningComplete = false;
        this.mIsPskSaeTransitionMode = false;
        this.mIsOweTransitionMode = false;
        this.mContext = context;
        loadConfig(wifiConfiguration);
        updateKey();
    }

    public AccessPoint(Context context, WifiConfiguration wifiConfiguration, Collection<ScanResult> collection, Collection<ScanResult> collection2) {
        this.mLock = new Object();
        this.mScanResults = new ArraySet<>();
        this.mExtraScanResults = new ArraySet<>();
        this.mScoredNetworkCache = new HashMap();
        this.networkId = -1;
        this.pskType = 0;
        this.mRssi = Integer.MIN_VALUE;
        this.mSpeed = 0;
        this.mIsScoredNetworkMetered = false;
        this.mPasspointConfigurationVersion = 0;
        this.mOsuProvisioningComplete = false;
        this.mIsPskSaeTransitionMode = false;
        this.mIsOweTransitionMode = false;
        this.mContext = context;
        this.networkId = wifiConfiguration.networkId;
        this.mConfig = wifiConfiguration;
        this.mPasspointUniqueId = wifiConfiguration.getKey();
        String str = wifiConfiguration.FQDN;
        setScanResultsPasspoint(collection, collection2);
        updateKey();
    }

    public AccessPoint(Context context, OsuProvider osuProvider, Collection<ScanResult> collection) {
        this.mLock = new Object();
        this.mScanResults = new ArraySet<>();
        this.mExtraScanResults = new ArraySet<>();
        this.mScoredNetworkCache = new HashMap();
        this.networkId = -1;
        this.pskType = 0;
        this.mRssi = Integer.MIN_VALUE;
        this.mSpeed = 0;
        this.mIsScoredNetworkMetered = false;
        this.mPasspointConfigurationVersion = 0;
        this.mOsuProvisioningComplete = false;
        this.mIsPskSaeTransitionMode = false;
        this.mIsOweTransitionMode = false;
        this.mContext = context;
        this.mOsuProvider = osuProvider;
        setScanResults(collection);
        updateKey();
    }

    AccessPoint(Context context, Collection<ScanResult> collection) {
        this.mLock = new Object();
        this.mScanResults = new ArraySet<>();
        this.mExtraScanResults = new ArraySet<>();
        this.mScoredNetworkCache = new HashMap();
        this.networkId = -1;
        this.pskType = 0;
        this.mRssi = Integer.MIN_VALUE;
        this.mSpeed = 0;
        this.mIsScoredNetworkMetered = false;
        this.mPasspointConfigurationVersion = 0;
        this.mOsuProvisioningComplete = false;
        this.mIsPskSaeTransitionMode = false;
        this.mIsOweTransitionMode = false;
        this.mContext = context;
        setScanResults(collection);
        updateKey();
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void loadConfig(WifiConfiguration wifiConfiguration) {
        String str = wifiConfiguration.SSID;
        this.ssid = str == null ? "" : removeDoubleQuotes(str);
        this.bssid = wifiConfiguration.BSSID;
        this.security = getSecurity(wifiConfiguration);
        this.networkId = wifiConfiguration.networkId;
        this.mConfig = wifiConfiguration;
    }

    private void updateKey() {
        if (isPasspoint()) {
            this.mKey = getKey(this.mConfig);
        } else if (isPasspointConfig()) {
            this.mKey = getKey(this.mPasspointUniqueId);
        } else if (isOsuProvider()) {
            this.mKey = getKey(this.mOsuProvider);
        } else {
            this.mKey = getKey(getSsidStr(), getBssid(), getSecurity());
        }
    }

    public int compareTo(AccessPoint accessPoint) {
        if (isActive() && !accessPoint.isActive()) {
            return -1;
        }
        if (!isActive() && accessPoint.isActive()) {
            return 1;
        }
        if (isReachable() && !accessPoint.isReachable()) {
            return -1;
        }
        if (!isReachable() && accessPoint.isReachable()) {
            return 1;
        }
        if (isSaved() && !accessPoint.isSaved()) {
            return -1;
        }
        if (!isSaved() && accessPoint.isSaved()) {
            return 1;
        }
        if (getSpeed() != accessPoint.getSpeed()) {
            return accessPoint.getSpeed() - getSpeed();
        }
        WifiManager wifiManager = getWifiManager();
        int calculateSignalLevel = wifiManager.calculateSignalLevel(accessPoint.mRssi) - wifiManager.calculateSignalLevel(this.mRssi);
        if (calculateSignalLevel != 0) {
            return calculateSignalLevel;
        }
        int compareToIgnoreCase = getTitle().compareToIgnoreCase(accessPoint.getTitle());
        if (compareToIgnoreCase != 0) {
            return compareToIgnoreCase;
        }
        return getSsidStr().compareTo(accessPoint.getSsidStr());
    }

    public boolean equals(Object obj) {
        boolean z = false;
        if (!(obj instanceof AccessPoint)) {
            return false;
        }
        if (compareTo((AccessPoint) obj) == 0) {
            z = true;
        }
        return z;
    }

    public int hashCode() {
        WifiInfo wifiInfo = this.mInfo;
        int i = 0;
        if (wifiInfo != null) {
            i = 0 + (wifiInfo.hashCode() * 13);
        }
        return i + (this.mRssi * 19) + (this.networkId * 23) + (this.ssid.hashCode() * 29);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AccessPoint(");
        sb.append(this.ssid);
        if (this.bssid != null) {
            sb.append(":");
            sb.append(this.bssid);
        }
        if (isSaved()) {
            sb.append(',');
            sb.append("saved");
        }
        if (isActive()) {
            sb.append(',');
            sb.append("active");
        }
        if (isEphemeral()) {
            sb.append(',');
            sb.append("ephemeral");
        }
        if (isConnectable()) {
            sb.append(',');
            sb.append("connectable");
        }
        int i = this.security;
        if (!(i == 0 || i == 4)) {
            sb.append(',');
            sb.append(securityToString(this.security, this.pskType));
        }
        sb.append(",level=");
        sb.append(getLevel());
        if (this.mSpeed != 0) {
            sb.append(",speed=");
            sb.append(this.mSpeed);
        }
        sb.append(",metered=");
        sb.append(isMetered());
        if (isVerboseLoggingEnabled()) {
            sb.append(",rssi=");
            sb.append(this.mRssi);
            synchronized (this.mLock) {
                sb.append(",scan cache size=");
                sb.append(this.mScanResults.size() + this.mExtraScanResults.size());
            }
        }
        sb.append(')');
        return sb.toString();
    }

    /* access modifiers changed from: 0000 */
    public boolean update(WifiNetworkScoreCache wifiNetworkScoreCache, boolean z, long j) {
        boolean updateScores = z ? updateScores(wifiNetworkScoreCache, j) : false;
        if (updateMetered(wifiNetworkScoreCache) || updateScores) {
            return true;
        }
        return false;
    }

    private boolean updateScores(WifiNetworkScoreCache wifiNetworkScoreCache, long j) {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        synchronized (this.mLock) {
            Iterator it = this.mScanResults.iterator();
            while (it.hasNext()) {
                ScanResult scanResult = (ScanResult) it.next();
                ScoredNetwork scoredNetwork = wifiNetworkScoreCache.getScoredNetwork(scanResult);
                if (scoredNetwork != null) {
                    TimestampedScoredNetwork timestampedScoredNetwork = (TimestampedScoredNetwork) this.mScoredNetworkCache.get(scanResult.BSSID);
                    if (timestampedScoredNetwork == null) {
                        this.mScoredNetworkCache.put(scanResult.BSSID, new TimestampedScoredNetwork(scoredNetwork, elapsedRealtime));
                    } else {
                        timestampedScoredNetwork.update(scoredNetwork, elapsedRealtime);
                    }
                }
            }
        }
        long j2 = elapsedRealtime - j;
        Iterator it2 = this.mScoredNetworkCache.values().iterator();
        it2.forEachRemaining(new Consumer(j2, it2) {
            public final /* synthetic */ long f$0;
            public final /* synthetic */ Iterator f$1;

            {
                this.f$0 = r1;
                this.f$1 = r3;
            }

            public final void accept(Object obj) {
                AccessPoint.lambda$updateScores$0(this.f$0, this.f$1, (TimestampedScoredNetwork) obj);
            }
        });
        return updateSpeed();
    }

    static /* synthetic */ void lambda$updateScores$0(long j, Iterator it, TimestampedScoredNetwork timestampedScoredNetwork) {
        if (timestampedScoredNetwork.getUpdatedTimestampMillis() < j) {
            it.remove();
        }
    }

    private boolean updateSpeed() {
        int i = this.mSpeed;
        int generateAverageSpeedForSsid = generateAverageSpeedForSsid();
        this.mSpeed = generateAverageSpeedForSsid;
        boolean z = i != generateAverageSpeedForSsid;
        if (isVerboseLoggingEnabled() && z) {
            Log.i("SettingsLib.AccessPoint", String.format("%s: Set speed to %d", new Object[]{this.ssid, Integer.valueOf(this.mSpeed)}));
        }
        return z;
    }

    private int generateAverageSpeedForSsid() {
        int i;
        if (this.mScoredNetworkCache.isEmpty()) {
            return 0;
        }
        String str = "SettingsLib.AccessPoint";
        if (Log.isLoggable(str, 3)) {
            Log.d(str, String.format("Generating fallbackspeed for %s using cache: %s", new Object[]{getSsidStr(), this.mScoredNetworkCache}));
        }
        int i2 = 0;
        int i3 = 0;
        for (TimestampedScoredNetwork score : this.mScoredNetworkCache.values()) {
            int calculateBadge = score.getScore().calculateBadge(this.mRssi);
            if (calculateBadge != 0) {
                i2++;
                i3 += calculateBadge;
            }
        }
        if (i2 == 0) {
            i = 0;
        } else {
            i = i3 / i2;
        }
        if (isVerboseLoggingEnabled()) {
            Log.i(str, String.format("%s generated fallback speed is: %d", new Object[]{getSsidStr(), Integer.valueOf(i)}));
        }
        return roundToClosestSpeedEnum(i);
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x004a  */
    /* JADX WARNING: Removed duplicated region for block: B:33:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean updateMetered(android.net.wifi.WifiNetworkScoreCache r7) {
        /*
            r6 = this;
            boolean r0 = r6.mIsScoredNetworkMetered
            r1 = 0
            r6.mIsScoredNetworkMetered = r1
            boolean r2 = r6.isActive()
            if (r2 == 0) goto L_0x0021
            android.net.wifi.WifiInfo r2 = r6.mInfo
            if (r2 == 0) goto L_0x0021
            android.net.NetworkKey r2 = android.net.NetworkKey.createFromWifiInfo(r2)
            android.net.ScoredNetwork r7 = r7.getScoredNetwork(r2)
            if (r7 == 0) goto L_0x0046
            boolean r2 = r6.mIsScoredNetworkMetered
            boolean r7 = r7.meteredHint
            r7 = r7 | r2
            r6.mIsScoredNetworkMetered = r7
            goto L_0x0046
        L_0x0021:
            java.lang.Object r2 = r6.mLock
            monitor-enter(r2)
            android.util.ArraySet<android.net.wifi.ScanResult> r3 = r6.mScanResults     // Catch:{ all -> 0x004c }
            java.util.Iterator r3 = r3.iterator()     // Catch:{ all -> 0x004c }
        L_0x002a:
            boolean r4 = r3.hasNext()     // Catch:{ all -> 0x004c }
            if (r4 == 0) goto L_0x0045
            java.lang.Object r4 = r3.next()     // Catch:{ all -> 0x004c }
            android.net.wifi.ScanResult r4 = (android.net.wifi.ScanResult) r4     // Catch:{ all -> 0x004c }
            android.net.ScoredNetwork r4 = r7.getScoredNetwork(r4)     // Catch:{ all -> 0x004c }
            if (r4 != 0) goto L_0x003d
            goto L_0x002a
        L_0x003d:
            boolean r5 = r6.mIsScoredNetworkMetered     // Catch:{ all -> 0x004c }
            boolean r4 = r4.meteredHint     // Catch:{ all -> 0x004c }
            r4 = r4 | r5
            r6.mIsScoredNetworkMetered = r4     // Catch:{ all -> 0x004c }
            goto L_0x002a
        L_0x0045:
            monitor-exit(r2)     // Catch:{ all -> 0x004c }
        L_0x0046:
            boolean r6 = r6.mIsScoredNetworkMetered
            if (r0 == r6) goto L_0x004b
            r1 = 1
        L_0x004b:
            return r1
        L_0x004c:
            r6 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x004c }
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.wifi.AccessPoint.updateMetered(android.net.wifi.WifiNetworkScoreCache):boolean");
    }

    public static String getKey(Context context, ScanResult scanResult) {
        return getKey(scanResult.SSID, scanResult.BSSID, getSecurity(context, scanResult));
    }

    public static String getKey(WifiConfiguration wifiConfiguration) {
        if (wifiConfiguration.isPasspoint()) {
            return getKey(wifiConfiguration.getKey());
        }
        return getKey(removeDoubleQuotes(wifiConfiguration.SSID), wifiConfiguration.BSSID, getSecurity(wifiConfiguration));
    }

    public static String getKey(String str) {
        StringBuilder sb = new StringBuilder();
        sb.append("PASSPOINT:");
        sb.append(str);
        return sb.toString();
    }

    public static String getKey(OsuProvider osuProvider) {
        StringBuilder sb = new StringBuilder();
        sb.append("OSU:");
        sb.append(osuProvider.getFriendlyName());
        sb.append(',');
        sb.append(osuProvider.getServerUri());
        return sb.toString();
    }

    private static String getKey(String str, String str2, int i) {
        StringBuilder sb = new StringBuilder();
        sb.append("AP:");
        if (TextUtils.isEmpty(str)) {
            sb.append(str2);
        } else {
            sb.append(str);
        }
        sb.append(',');
        sb.append(i);
        return sb.toString();
    }

    public String getKey() {
        return this.mKey;
    }

    public boolean matches(WifiConfiguration wifiConfiguration) {
        boolean z = false;
        if (wifiConfiguration.isPasspoint()) {
            if (isPasspoint() && wifiConfiguration.getKey().equals(this.mConfig.getKey())) {
                z = true;
            }
            return z;
        }
        if (this.ssid.equals(removeDoubleQuotes(wifiConfiguration.SSID))) {
            WifiConfiguration wifiConfiguration2 = this.mConfig;
            if (wifiConfiguration2 == null || wifiConfiguration2.shared == wifiConfiguration.shared) {
                int security2 = getSecurity(wifiConfiguration);
                if (this.mIsPskSaeTransitionMode && ((security2 == 5 && getWifiManager().isWpa3SaeSupported()) || security2 == 2)) {
                    return true;
                }
                if (this.mIsOweTransitionMode && ((security2 == 4 && getWifiManager().isEnhancedOpenSupported()) || security2 == 0)) {
                    return true;
                }
                if (this.security == getSecurity(wifiConfiguration)) {
                    z = true;
                }
            }
        }
        return z;
    }

    private boolean matches(WifiConfiguration wifiConfiguration, WifiInfo wifiInfo) {
        if (wifiConfiguration == null || wifiInfo == null) {
            return false;
        }
        if (wifiConfiguration.isPasspoint() || isSameSsidOrBssid(wifiInfo)) {
            return matches(wifiConfiguration);
        }
        return false;
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public boolean matches(ScanResult scanResult) {
        boolean z = false;
        if (scanResult == null) {
            return false;
        }
        if (isPasspoint() || isOsuProvider()) {
            throw new IllegalStateException("Should not matches a Passpoint by ScanResult");
        } else if (!isSameSsidOrBssid(scanResult)) {
            return false;
        } else {
            if (!this.mIsPskSaeTransitionMode) {
                int i = this.security;
                if ((i == 5 || i == 2) && isPskSaeTransitionMode(scanResult)) {
                    return true;
                }
            } else if ((scanResult.capabilities.contains("SAE") && getWifiManager().isWpa3SaeSupported()) || scanResult.capabilities.contains("PSK")) {
                return true;
            }
            if (this.mIsOweTransitionMode) {
                int security2 = getSecurity(this.mContext, scanResult);
                if ((security2 == 4 && getWifiManager().isEnhancedOpenSupported()) || security2 == 0) {
                    return true;
                }
            } else {
                int i2 = this.security;
                if ((i2 == 4 || i2 == 0) && isOweTransitionMode(scanResult)) {
                    return true;
                }
            }
            if (this.security == getSecurity(this.mContext, scanResult)) {
                z = true;
            }
            return z;
        }
    }

    public WifiConfiguration getConfig() {
        return this.mConfig;
    }

    public WifiInfo getInfo() {
        return this.mInfo;
    }

    public int getLevel() {
        return getWifiManager().calculateSignalLevel(this.mRssi);
    }

    public Set<ScanResult> getScanResults() {
        ArraySet arraySet = new ArraySet();
        synchronized (this.mLock) {
            arraySet.addAll(this.mScanResults);
            arraySet.addAll(this.mExtraScanResults);
        }
        return arraySet;
    }

    public Map<String, TimestampedScoredNetwork> getScoredNetworkCache() {
        return this.mScoredNetworkCache;
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x0039  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x006e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateBestRssiInfo() {
        /*
            r7 = this;
            boolean r0 = r7.isActive()
            if (r0 == 0) goto L_0x0007
            return
        L_0x0007:
            r0 = 0
            java.lang.Object r1 = r7.mLock
            monitor-enter(r1)
            android.util.ArraySet<android.net.wifi.ScanResult> r2 = r7.mScanResults     // Catch:{ all -> 0x0079 }
            java.util.Iterator r2 = r2.iterator()     // Catch:{ all -> 0x0079 }
            r3 = -2147483648(0xffffffff80000000, float:-0.0)
            r4 = r3
        L_0x0014:
            boolean r5 = r2.hasNext()     // Catch:{ all -> 0x0079 }
            if (r5 == 0) goto L_0x0028
            java.lang.Object r5 = r2.next()     // Catch:{ all -> 0x0079 }
            android.net.wifi.ScanResult r5 = (android.net.wifi.ScanResult) r5     // Catch:{ all -> 0x0079 }
            int r6 = r5.level     // Catch:{ all -> 0x0079 }
            if (r6 <= r4) goto L_0x0014
            int r4 = r5.level     // Catch:{ all -> 0x0079 }
            r0 = r5
            goto L_0x0014
        L_0x0028:
            monitor-exit(r1)     // Catch:{ all -> 0x0079 }
            r1 = 2
            if (r4 == r3) goto L_0x0035
            int r2 = r7.mRssi
            if (r2 == r3) goto L_0x0035
            int r2 = r2 + r4
            int r2 = r2 / r1
            r7.mRssi = r2
            goto L_0x0037
        L_0x0035:
            r7.mRssi = r4
        L_0x0037:
            if (r0 == 0) goto L_0x0068
            java.lang.String r2 = r0.SSID
            r7.ssid = r2
            java.lang.String r2 = r0.BSSID
            r7.bssid = r2
            android.content.Context r2 = r7.mContext
            int r2 = getSecurity(r2, r0)
            r7.security = r2
            if (r2 == r1) goto L_0x004e
            r1 = 5
            if (r2 != r1) goto L_0x0054
        L_0x004e:
            int r1 = getPskType(r0)
            r7.pskType = r1
        L_0x0054:
            int r1 = r7.security
            r2 = 3
            if (r1 != r2) goto L_0x005c
            getEapType(r0)
        L_0x005c:
            boolean r1 = isPskSaeTransitionMode(r0)
            r7.mIsPskSaeTransitionMode = r1
            boolean r0 = isOweTransitionMode(r0)
            r7.mIsOweTransitionMode = r0
        L_0x0068:
            boolean r0 = r7.isPasspoint()
            if (r0 == 0) goto L_0x0078
            android.net.wifi.WifiConfiguration r0 = r7.mConfig
            java.lang.String r7 = r7.ssid
            java.lang.String r7 = convertToQuotedString(r7)
            r0.SSID = r7
        L_0x0078:
            return
        L_0x0079:
            r7 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0079 }
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.wifi.AccessPoint.updateBestRssiInfo():void");
    }

    public boolean isMetered() {
        return this.mIsScoredNetworkMetered || WifiConfiguration.isMetered(this.mConfig, this.mInfo);
    }

    public int getSecurity() {
        return this.security;
    }

    public String getSsidStr() {
        return this.ssid;
    }

    public String getBssid() {
        return this.bssid;
    }

    public CharSequence getSsid() {
        return this.ssid;
    }

    public DetailedState getDetailedState() {
        NetworkInfo networkInfo = this.mNetworkInfo;
        if (networkInfo != null) {
            return networkInfo.getDetailedState();
        }
        Log.w("SettingsLib.AccessPoint", "NetworkInfo is null, cannot return detailed state");
        return null;
    }

    public String getTitle() {
        if (isPasspoint()) {
            return this.mConfig.providerFriendlyName;
        }
        if (isPasspointConfig()) {
            return this.mProviderFriendlyName;
        }
        if (isOsuProvider()) {
            return this.mOsuProvider.getFriendlyName();
        }
        return getSsidStr();
    }

    public String getSummary() {
        return getSettingsSummary();
    }

    public String getSettingsSummary() {
        return getSettingsSummary(false);
    }

    public String getSettingsSummary(boolean z) {
        int i;
        if (isPasspointConfigurationR1() && isExpired()) {
            return this.mContext.getString(R$string.wifi_passpoint_expired);
        }
        StringBuilder sb = new StringBuilder();
        if (isOsuProvider()) {
            if (this.mOsuProvisioningComplete) {
                sb.append(this.mContext.getString(R$string.osu_sign_up_complete));
            } else {
                String str = this.mOsuFailure;
                if (str != null) {
                    sb.append(str);
                } else {
                    String str2 = this.mOsuStatus;
                    if (str2 != null) {
                        sb.append(str2);
                    } else {
                        sb.append(this.mContext.getString(R$string.tap_to_sign_up));
                    }
                }
            }
        } else if (isActive()) {
            Context context = this.mContext;
            DetailedState detailedState = getDetailedState();
            WifiInfo wifiInfo = this.mInfo;
            boolean z2 = wifiInfo != null && wifiInfo.isEphemeral();
            WifiInfo wifiInfo2 = this.mInfo;
            sb.append(getSummary(context, null, detailedState, z2, wifiInfo2 != null ? wifiInfo2.getRequestingPackageName() : null));
        } else {
            WifiConfiguration wifiConfiguration = this.mConfig;
            if (wifiConfiguration == null || !wifiConfiguration.hasNoInternetAccess()) {
                WifiConfiguration wifiConfiguration2 = this.mConfig;
                if (wifiConfiguration2 != null && wifiConfiguration2.getNetworkSelectionStatus().getNetworkSelectionStatus() != 0) {
                    int networkSelectionDisableReason = this.mConfig.getNetworkSelectionStatus().getNetworkSelectionDisableReason();
                    if (networkSelectionDisableReason == 1) {
                        sb.append(this.mContext.getString(R$string.wifi_disabled_generic));
                    } else if (networkSelectionDisableReason == 2) {
                        sb.append(this.mContext.getString(R$string.wifi_disabled_password_failure));
                    } else if (networkSelectionDisableReason == 3) {
                        sb.append(this.mContext.getString(R$string.wifi_disabled_network_failure));
                    } else if (networkSelectionDisableReason == 8) {
                        sb.append(this.mContext.getString(R$string.wifi_check_password_try_again));
                    }
                } else if (!isReachable()) {
                    sb.append(this.mContext.getString(R$string.wifi_not_in_range));
                } else {
                    WifiConfiguration wifiConfiguration3 = this.mConfig;
                    if (wifiConfiguration3 != null) {
                        if (wifiConfiguration3.getRecentFailureReason() == 17) {
                            sb.append(this.mContext.getString(R$string.wifi_ap_unable_to_handle_new_sta));
                        } else if (z) {
                            sb.append(this.mContext.getString(R$string.wifi_disconnected));
                        } else {
                            sb.append(this.mContext.getString(R$string.wifi_remembered));
                        }
                    }
                }
            } else {
                if (this.mConfig.getNetworkSelectionStatus().getNetworkSelectionStatus() == 2) {
                    i = R$string.wifi_no_internet_no_reconnect;
                } else {
                    i = R$string.wifi_no_internet;
                }
                sb.append(this.mContext.getString(i));
            }
        }
        if (isVerboseLoggingEnabled()) {
            sb.append(WifiUtils.buildLoggingSummary(this, this.mConfig));
        }
        WifiConfiguration wifiConfiguration4 = this.mConfig;
        if (wifiConfiguration4 != null && (WifiUtils.isMeteredOverridden(wifiConfiguration4) || this.mConfig.meteredHint)) {
            return this.mContext.getResources().getString(R$string.preference_summary_default_combination, new Object[]{WifiUtils.getMeteredLabel(this.mContext, this.mConfig), sb.toString()});
        } else if (getSpeedLabel() != null && sb.length() != 0) {
            return this.mContext.getResources().getString(R$string.preference_summary_default_combination, new Object[]{getSpeedLabel(), sb.toString()});
        } else if (getSpeedLabel() != null) {
            return getSpeedLabel();
        } else {
            return sb.toString();
        }
    }

    public boolean isActive() {
        NetworkInfo networkInfo = this.mNetworkInfo;
        return (networkInfo == null || (this.networkId == -1 && networkInfo.getState() == State.DISCONNECTED)) ? false : true;
    }

    public boolean isConnectable() {
        return getLevel() != -1 && getDetailedState() == null;
    }

    public boolean isEphemeral() {
        WifiInfo wifiInfo = this.mInfo;
        if (wifiInfo != null && wifiInfo.isEphemeral()) {
            NetworkInfo networkInfo = this.mNetworkInfo;
            if (!(networkInfo == null || networkInfo.getState() == State.DISCONNECTED)) {
                return true;
            }
        }
        return false;
    }

    public boolean isPasspoint() {
        WifiConfiguration wifiConfiguration = this.mConfig;
        return wifiConfiguration != null && wifiConfiguration.isPasspoint();
    }

    public boolean isPasspointConfig() {
        return this.mPasspointUniqueId != null && this.mConfig == null;
    }

    public boolean isOsuProvider() {
        return this.mOsuProvider != null;
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

    public boolean isPasspointConfigurationR1() {
        return this.mPasspointConfigurationVersion == 1;
    }

    private boolean isInfoForThisAccessPoint(WifiConfiguration wifiConfiguration, WifiInfo wifiInfo) {
        boolean z = true;
        if (wifiInfo.isOsuAp() || this.mOsuStatus != null) {
            if (!wifiInfo.isOsuAp() || this.mOsuStatus == null) {
                z = false;
            }
            return z;
        } else if (wifiInfo.isPasspointAp() || isPasspoint()) {
            if (!wifiInfo.isPasspointAp() || !isPasspoint() || !TextUtils.equals(wifiInfo.getPasspointFqdn(), this.mConfig.FQDN) || !TextUtils.equals(wifiInfo.getPasspointProviderFriendlyName(), this.mConfig.providerFriendlyName)) {
                z = false;
            }
            return z;
        } else {
            int i = this.networkId;
            if (i != -1) {
                if (i != wifiInfo.getNetworkId()) {
                    z = false;
                }
                return z;
            } else if (wifiConfiguration != null) {
                return matches(wifiConfiguration, wifiInfo);
            } else {
                return TextUtils.equals(removeDoubleQuotes(wifiInfo.getSSID()), this.ssid);
            }
        }
    }

    public boolean isSaved() {
        return this.mConfig != null;
    }

    public void setTag(Object obj) {
        this.mTag = obj;
    }

    public void generateOpenNetworkConfig() {
        if (!isOpenNetwork()) {
            throw new IllegalStateException();
        } else if (this.mConfig == null) {
            WifiConfiguration wifiConfiguration = new WifiConfiguration();
            this.mConfig = wifiConfiguration;
            wifiConfiguration.SSID = convertToQuotedString(this.ssid);
            if (this.security == 0) {
                this.mConfig.allowedKeyManagement.set(0);
            } else {
                this.mConfig.allowedKeyManagement.set(9);
                this.mConfig.requirePmf = true;
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void setScanResults(Collection<ScanResult> collection) {
        if (CollectionUtils.isEmpty(collection)) {
            Log.d("SettingsLib.AccessPoint", "Cannot set scan results to empty list");
            return;
        }
        if (this.mKey != null && !isPasspoint() && !isOsuProvider()) {
            for (ScanResult scanResult : collection) {
                if (!matches(scanResult)) {
                    Log.d("SettingsLib.AccessPoint", String.format("ScanResult %s\nkey of %s did not match current AP key %s", new Object[]{scanResult, getKey(this.mContext, scanResult), this.mKey}));
                    return;
                }
            }
        }
        int level = getLevel();
        synchronized (this.mLock) {
            this.mScanResults.clear();
            this.mScanResults.addAll(collection);
        }
        updateBestRssiInfo();
        int level2 = getLevel();
        if (level2 > 0 && level2 != level) {
            updateSpeed();
            ThreadUtils.postOnMainThread(new Runnable() {
                public final void run() {
                    AccessPoint.this.lambda$setScanResults$1$AccessPoint();
                }
            });
        }
        ThreadUtils.postOnMainThread(new Runnable() {
            public final void run() {
                AccessPoint.this.lambda$setScanResults$2$AccessPoint();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setScanResults$1 */
    public /* synthetic */ void lambda$setScanResults$1$AccessPoint() {
        AccessPointListener accessPointListener = this.mAccessPointListener;
        if (accessPointListener != null) {
            accessPointListener.onLevelChanged(this);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setScanResults$2 */
    public /* synthetic */ void lambda$setScanResults$2$AccessPoint() {
        AccessPointListener accessPointListener = this.mAccessPointListener;
        if (accessPointListener != null) {
            accessPointListener.onAccessPointChanged(this);
        }
    }

    /* access modifiers changed from: 0000 */
    public void setScanResultsPasspoint(Collection<ScanResult> collection, Collection<ScanResult> collection2) {
        synchronized (this.mLock) {
            this.mExtraScanResults.clear();
            if (!CollectionUtils.isEmpty(collection)) {
                if (!CollectionUtils.isEmpty(collection2)) {
                    this.mExtraScanResults.addAll(collection2);
                }
                setScanResults(collection);
            } else if (!CollectionUtils.isEmpty(collection2)) {
                setScanResults(collection2);
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0046, code lost:
        if (r5.getDetailedState() != r7.getDetailedState()) goto L_0x0036;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean update(android.net.wifi.WifiConfiguration r5, android.net.wifi.WifiInfo r6, android.net.NetworkInfo r7) {
        /*
            r4 = this;
            int r0 = r4.getLevel()
            r1 = 0
            r2 = 1
            if (r6 == 0) goto L_0x004e
            boolean r3 = r4.isInfoForThisAccessPoint(r5, r6)
            if (r3 == 0) goto L_0x004e
            android.net.wifi.WifiInfo r3 = r4.mInfo
            if (r3 != 0) goto L_0x0013
            r1 = r2
        L_0x0013:
            boolean r3 = r4.isPasspoint()
            if (r3 != 0) goto L_0x0020
            android.net.wifi.WifiConfiguration r3 = r4.mConfig
            if (r3 == r5) goto L_0x0020
            r4.update(r5)
        L_0x0020:
            int r5 = r4.mRssi
            int r3 = r6.getRssi()
            if (r5 == r3) goto L_0x0038
            int r5 = r6.getRssi()
            r3 = -127(0xffffffffffffff81, float:NaN)
            if (r5 == r3) goto L_0x0038
            int r5 = r6.getRssi()
            r4.mRssi = r5
        L_0x0036:
            r1 = r2
            goto L_0x0049
        L_0x0038:
            android.net.NetworkInfo r5 = r4.mNetworkInfo
            if (r5 == 0) goto L_0x0049
            if (r7 == 0) goto L_0x0049
            android.net.NetworkInfo$DetailedState r5 = r5.getDetailedState()
            android.net.NetworkInfo$DetailedState r3 = r7.getDetailedState()
            if (r5 == r3) goto L_0x0049
            goto L_0x0036
        L_0x0049:
            r4.mInfo = r6
            r4.mNetworkInfo = r7
            goto L_0x0058
        L_0x004e:
            android.net.wifi.WifiInfo r5 = r4.mInfo
            if (r5 == 0) goto L_0x0058
            r5 = 0
            r4.mInfo = r5
            r4.mNetworkInfo = r5
            r1 = r2
        L_0x0058:
            if (r1 == 0) goto L_0x0074
            com.android.settingslib.wifi.AccessPoint$AccessPointListener r5 = r4.mAccessPointListener
            if (r5 == 0) goto L_0x0074
            com.android.settingslib.wifi.-$$Lambda$AccessPoint$S7H59e_8IxpVPy0V68Oc2-zX-rg r5 = new com.android.settingslib.wifi.-$$Lambda$AccessPoint$S7H59e_8IxpVPy0V68Oc2-zX-rg
            r5.<init>()
            com.android.settingslib.utils.ThreadUtils.postOnMainThread(r5)
            int r5 = r4.getLevel()
            if (r0 == r5) goto L_0x0074
            com.android.settingslib.wifi.-$$Lambda$AccessPoint$QW-1Uw0oxoaKqUtEtPO0oPvH5ng r5 = new com.android.settingslib.wifi.-$$Lambda$AccessPoint$QW-1Uw0oxoaKqUtEtPO0oPvH5ng
            r5.<init>()
            com.android.settingslib.utils.ThreadUtils.postOnMainThread(r5)
        L_0x0074:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.wifi.AccessPoint.update(android.net.wifi.WifiConfiguration, android.net.wifi.WifiInfo, android.net.NetworkInfo):boolean");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$update$3 */
    public /* synthetic */ void lambda$update$3$AccessPoint() {
        AccessPointListener accessPointListener = this.mAccessPointListener;
        if (accessPointListener != null) {
            accessPointListener.onAccessPointChanged(this);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$update$4 */
    public /* synthetic */ void lambda$update$4$AccessPoint() {
        AccessPointListener accessPointListener = this.mAccessPointListener;
        if (accessPointListener != null) {
            accessPointListener.onLevelChanged(this);
        }
    }

    /* access modifiers changed from: 0000 */
    public void update(WifiConfiguration wifiConfiguration) {
        this.mConfig = wifiConfiguration;
        if (wifiConfiguration != null && !isPasspoint()) {
            this.ssid = removeDoubleQuotes(this.mConfig.SSID);
        }
        this.networkId = wifiConfiguration != null ? wifiConfiguration.networkId : -1;
        ThreadUtils.postOnMainThread(new Runnable() {
            public final void run() {
                AccessPoint.this.lambda$update$5$AccessPoint();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$update$5 */
    public /* synthetic */ void lambda$update$5$AccessPoint() {
        AccessPointListener accessPointListener = this.mAccessPointListener;
        if (accessPointListener != null) {
            accessPointListener.onAccessPointChanged(this);
        }
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void setRssi(int i) {
        this.mRssi = i;
    }

    /* access modifiers changed from: 0000 */
    public int getSpeed() {
        return this.mSpeed;
    }

    /* access modifiers changed from: 0000 */
    public String getSpeedLabel() {
        return getSpeedLabel(this.mSpeed);
    }

    /* access modifiers changed from: 0000 */
    public String getSpeedLabel(int i) {
        return getSpeedLabel(this.mContext, i);
    }

    private static String getSpeedLabel(Context context, int i) {
        if (i == 5) {
            return context.getString(R$string.speed_label_slow);
        }
        if (i == 10) {
            return context.getString(R$string.speed_label_okay);
        }
        if (i == 20) {
            return context.getString(R$string.speed_label_fast);
        }
        if (i != 30) {
            return null;
        }
        return context.getString(R$string.speed_label_very_fast);
    }

    public static String getSpeedLabel(Context context, ScoredNetwork scoredNetwork, int i) {
        return getSpeedLabel(context, roundToClosestSpeedEnum(scoredNetwork.calculateBadge(i)));
    }

    public boolean isReachable() {
        return this.mRssi != Integer.MIN_VALUE;
    }

    private static CharSequence getAppLabel(String str, PackageManager packageManager) {
        CharSequence charSequence = "";
        try {
            ApplicationInfo applicationInfoAsUser = packageManager.getApplicationInfoAsUser(str, 0, UserHandle.getUserId(-2));
            if (applicationInfoAsUser != null) {
                charSequence = applicationInfoAsUser.loadLabel(packageManager);
            }
            return charSequence;
        } catch (NameNotFoundException e) {
            Log.e("SettingsLib.AccessPoint", "Failed to get app info", e);
            return charSequence;
        }
    }

    public static String getSummary(Context context, String str, DetailedState detailedState, boolean z, String str2) {
        if (detailedState == DetailedState.CONNECTED) {
            if (z && !TextUtils.isEmpty(str2)) {
                return context.getString(R$string.connected_via_app, new Object[]{getAppLabel(str2, context.getPackageManager())});
            } else if (z) {
                NetworkScorerAppData activeScorer = ((NetworkScoreManager) context.getSystemService(NetworkScoreManager.class)).getActiveScorer();
                if (activeScorer == null || activeScorer.getRecommendationServiceLabel() == null) {
                    return context.getString(R$string.connected_via_network_scorer_default);
                }
                return String.format(context.getString(R$string.connected_via_network_scorer), new Object[]{activeScorer.getRecommendationServiceLabel()});
            }
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        if (detailedState == DetailedState.CONNECTED) {
            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(((WifiManager) context.getSystemService(WifiManager.class)).getCurrentNetwork());
            if (networkCapabilities != null) {
                if (networkCapabilities.hasCapability(17)) {
                    return context.getString(context.getResources().getIdentifier("network_available_sign_in", "string", "android"));
                }
                if (networkCapabilities.hasCapability(24)) {
                    return context.getString(R$string.wifi_limited_connection);
                }
                if (!networkCapabilities.hasCapability(16)) {
                    Global.getString(context.getContentResolver(), "private_dns_mode");
                    if (networkCapabilities.isPrivateDnsBroken()) {
                        return context.getString(R$string.private_dns_broken);
                    }
                    return context.getString(R$string.wifi_connected_no_internet);
                }
            }
        }
        String str3 = "";
        if (detailedState == null) {
            Log.w("SettingsLib.AccessPoint", "state is null, returning empty summary");
            return str3;
        }
        String[] stringArray = context.getResources().getStringArray(str == null ? R$array.wifi_status : R$array.wifi_status_with_ssid);
        int ordinal = detailedState.ordinal();
        if (ordinal >= stringArray.length || stringArray[ordinal].length() == 0) {
            return str3;
        }
        return String.format(stringArray[ordinal], new Object[]{str});
    }

    public static String convertToQuotedString(String str) {
        StringBuilder sb = new StringBuilder();
        String str2 = "\"";
        sb.append(str2);
        sb.append(str);
        sb.append(str2);
        return sb.toString();
    }

    private static int getPskType(ScanResult scanResult) {
        boolean contains = scanResult.capabilities.contains("WPA-PSK");
        boolean contains2 = scanResult.capabilities.contains("RSN-PSK");
        boolean contains3 = scanResult.capabilities.contains("RSN-SAE");
        if (contains2 && contains) {
            return 3;
        }
        if (contains2) {
            return 2;
        }
        if (contains) {
            return 1;
        }
        if (!contains3) {
            StringBuilder sb = new StringBuilder();
            sb.append("Received abnormal flag string: ");
            sb.append(scanResult.capabilities);
            Log.w("SettingsLib.AccessPoint", sb.toString());
        }
        return 0;
    }

    private static int getEapType(ScanResult scanResult) {
        if (scanResult.capabilities.contains("RSN-EAP")) {
            return 2;
        }
        return scanResult.capabilities.contains("WPA-EAP") ? 1 : 0;
    }

    private static int getSecurity(Context context, ScanResult scanResult) {
        boolean contains = scanResult.capabilities.contains("WEP");
        boolean contains2 = scanResult.capabilities.contains("SAE");
        boolean contains3 = scanResult.capabilities.contains("PSK");
        boolean contains4 = scanResult.capabilities.contains("EAP_SUITE_B_192");
        boolean contains5 = scanResult.capabilities.contains("EAP");
        boolean contains6 = scanResult.capabilities.contains("OWE");
        boolean contains7 = scanResult.capabilities.contains("OWE_TRANSITION");
        int i = 5;
        String str = "wifi";
        if (!contains2 || !contains3) {
            int i2 = 4;
            if (contains7) {
                if (!((WifiManager) context.getSystemService(str)).isEnhancedOpenSupported()) {
                    i2 = 0;
                }
                return i2;
            } else if (contains) {
                return 1;
            } else {
                if (contains2) {
                    return 5;
                }
                if (contains3) {
                    return 2;
                }
                if (contains4) {
                    return 6;
                }
                if (contains5) {
                    return 3;
                }
                return contains6 ? 4 : 0;
            }
        } else {
            if (!((WifiManager) context.getSystemService(str)).isWpa3SaeSupported()) {
                i = 2;
            }
            return i;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:24:0x004c, code lost:
        if (r4[r0] != null) goto L_0x0050;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static int getSecurity(android.net.wifi.WifiConfiguration r4) {
        /*
            java.util.BitSet r0 = r4.allowedKeyManagement
            r1 = 8
            boolean r0 = r0.get(r1)
            if (r0 == 0) goto L_0x000c
            r4 = 5
            return r4
        L_0x000c:
            java.util.BitSet r0 = r4.allowedKeyManagement
            r1 = 1
            boolean r0 = r0.get(r1)
            r2 = 2
            if (r0 == 0) goto L_0x0017
            return r2
        L_0x0017:
            java.util.BitSet r0 = r4.allowedKeyManagement
            r3 = 10
            boolean r0 = r0.get(r3)
            if (r0 == 0) goto L_0x0023
            r4 = 6
            return r4
        L_0x0023:
            java.util.BitSet r0 = r4.allowedKeyManagement
            boolean r0 = r0.get(r2)
            r2 = 3
            if (r0 != 0) goto L_0x0051
            java.util.BitSet r0 = r4.allowedKeyManagement
            boolean r0 = r0.get(r2)
            if (r0 == 0) goto L_0x0035
            goto L_0x0051
        L_0x0035:
            java.util.BitSet r0 = r4.allowedKeyManagement
            r2 = 9
            boolean r0 = r0.get(r2)
            if (r0 == 0) goto L_0x0041
            r4 = 4
            return r4
        L_0x0041:
            int r0 = r4.wepTxKeyIndex
            if (r0 < 0) goto L_0x004f
            java.lang.String[] r4 = r4.wepKeys
            int r2 = r4.length
            if (r0 >= r2) goto L_0x004f
            r4 = r4[r0]
            if (r4 == 0) goto L_0x004f
            goto L_0x0050
        L_0x004f:
            r1 = 0
        L_0x0050:
            return r1
        L_0x0051:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.wifi.AccessPoint.getSecurity(android.net.wifi.WifiConfiguration):int");
    }

    static String removeDoubleQuotes(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        int length = str.length();
        if (length > 1 && str.charAt(0) == '\"') {
            int i = length - 1;
            if (str.charAt(i) == '\"') {
                str = str.substring(1, i);
            }
        }
        return str;
    }

    /* access modifiers changed from: private */
    public WifiManager getWifiManager() {
        if (this.mWifiManager == null) {
            this.mWifiManager = (WifiManager) this.mContext.getSystemService("wifi");
        }
        return this.mWifiManager;
    }

    public boolean isOpenNetwork() {
        int i = this.security;
        return i == 0 || i == 4;
    }

    private static boolean isVerboseLoggingEnabled() {
        return WifiTracker.sVerboseLogging || Log.isLoggable("SettingsLib.AccessPoint", 2);
    }

    private static boolean isPskSaeTransitionMode(ScanResult scanResult) {
        return scanResult.capabilities.contains("PSK") && scanResult.capabilities.contains("SAE");
    }

    private static boolean isOweTransitionMode(ScanResult scanResult) {
        return scanResult.capabilities.contains("OWE_TRANSITION");
    }

    private boolean isSameSsidOrBssid(ScanResult scanResult) {
        if (scanResult == null) {
            return false;
        }
        if (TextUtils.equals(this.ssid, scanResult.SSID)) {
            return true;
        }
        String str = scanResult.BSSID;
        return str != null && TextUtils.equals(this.bssid, str);
    }

    private boolean isSameSsidOrBssid(WifiInfo wifiInfo) {
        if (wifiInfo == null) {
            return false;
        }
        if (TextUtils.equals(this.ssid, removeDoubleQuotes(wifiInfo.getSSID()))) {
            return true;
        }
        return wifiInfo.getBSSID() != null && TextUtils.equals(this.bssid, wifiInfo.getBSSID());
    }
}
