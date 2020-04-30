package com.android.wifitrackerlib;

import android.net.LinkProperties;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.hotspot2.OsuProvider;
import android.net.wifi.hotspot2.PasspointConfiguration;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import androidx.core.util.Preconditions;
import com.android.wifitrackerlib.WifiPickerTracker.WifiPickerTrackerCallback;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class WifiPickerTracker extends BaseWifiTracker {
    private WifiEntry mConnectedWifiEntry;
    private final WifiPickerTrackerCallback mListener;
    private final Object mLock;
    private final Map<String, OsuWifiEntry> mOsuWifiEntryCache;
    private final Map<String, PasspointConfiguration> mPasspointConfigCache;
    private final Map<String, PasspointWifiEntry> mPasspointWifiEntryCache;
    private final Map<String, StandardWifiEntry> mStandardWifiEntryCache;
    private final Map<String, WifiConfiguration> mWifiConfigCache;
    private final List<WifiEntry> mWifiEntries;

    public interface WifiPickerTrackerCallback {
        void onNumSavedNetworksChanged();

        void onNumSavedSubscriptionsChanged();

        void onWifiEntriesChanged();
    }

    /* access modifiers changed from: protected */
    public void handleOnStart() {
        updateStandardWifiEntryConfigs(this.mWifiManager.getConfiguredNetworks());
        updatePasspointWifiEntryConfigs(this.mWifiManager.getPasspointConfigurations());
        this.mScanResultUpdater.update(this.mWifiManager.getScanResults());
        conditionallyUpdateScanResults(true);
        WifiInfo connectionInfo = this.mWifiManager.getConnectionInfo();
        NetworkInfo activeNetworkInfo = this.mConnectivityManager.getActiveNetworkInfo();
        updateConnectionInfo(connectionInfo, activeNetworkInfo);
        conditionallyCreateConnectedStandardWifiEntry(connectionInfo, activeNetworkInfo);
        conditionallyCreateConnectedPasspointWifiEntry(connectionInfo, activeNetworkInfo);
        handleLinkPropertiesChanged(this.mConnectivityManager.getLinkProperties(this.mWifiManager.getCurrentNetwork()));
        notifyOnNumSavedNetworksChanged();
        notifyOnNumSavedSubscriptionsChanged();
        updateWifiEntries();
    }

    /* access modifiers changed from: protected */
    public void handleLinkPropertiesChanged(LinkProperties linkProperties) {
        WifiEntry wifiEntry = this.mConnectedWifiEntry;
        if (wifiEntry != null && wifiEntry.getConnectedState() == 2) {
            this.mConnectedWifiEntry.updateLinkProperties(linkProperties);
        }
    }

    private void updateWifiEntries() {
        synchronized (this.mLock) {
            this.mWifiEntries.clear();
            this.mWifiEntries.addAll((Collection) this.mStandardWifiEntryCache.values().stream().filter($$Lambda$WifiPickerTracker$0TUEBOvt53oJDxdg7kKxPrqyWlc.INSTANCE).collect(Collectors.toList()));
            this.mWifiEntries.addAll((Collection) this.mPasspointWifiEntryCache.values().stream().filter($$Lambda$WifiPickerTracker$6WbSZ1lKNmv8PhTwxNfveIZ0yiM.INSTANCE).collect(Collectors.toList()));
            this.mWifiEntries.addAll((Collection) this.mOsuWifiEntryCache.values().stream().filter($$Lambda$WifiPickerTracker$7YHALy072Rfd4Jsnb0ioyWutznQ.INSTANCE).collect(Collectors.toList()));
            WifiEntry wifiEntry = (WifiEntry) this.mStandardWifiEntryCache.values().stream().filter($$Lambda$WifiPickerTracker$CZgqbuYbXTc6MQ_6NDRShQis8LA.INSTANCE).findAny().orElse(null);
            this.mConnectedWifiEntry = wifiEntry;
            if (wifiEntry == null) {
                this.mConnectedWifiEntry = (WifiEntry) this.mPasspointWifiEntryCache.values().stream().filter($$Lambda$WifiPickerTracker$WKgAoiYch4kxijsyncL19Qk1UE8.INSTANCE).findAny().orElse(null);
            }
            if (this.mConnectedWifiEntry == null) {
                this.mConnectedWifiEntry = (WifiEntry) this.mOsuWifiEntryCache.values().stream().filter($$Lambda$WifiPickerTracker$mzRxZpSNrk0X2p6ZMFGh4dZXQc.INSTANCE).findAny().orElse(null);
            }
            Collections.sort(this.mWifiEntries);
            if (BaseWifiTracker.isVerboseLoggingEnabled()) {
                StringBuilder sb = new StringBuilder();
                sb.append("Connected WifiEntry: ");
                sb.append(this.mConnectedWifiEntry);
                Log.v("WifiPickerTracker", sb.toString());
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Updated WifiEntries: ");
                sb2.append(Arrays.toString(this.mWifiEntries.toArray()));
                Log.v("WifiPickerTracker", sb2.toString());
            }
        }
        notifyOnWifiEntriesChanged();
    }

    static /* synthetic */ boolean lambda$updateWifiEntries$0(StandardWifiEntry standardWifiEntry) {
        return standardWifiEntry.getConnectedState() == 0;
    }

    static /* synthetic */ boolean lambda$updateWifiEntries$1(PasspointWifiEntry passpointWifiEntry) {
        return passpointWifiEntry.getConnectedState() == 0;
    }

    static /* synthetic */ boolean lambda$updateWifiEntries$2(OsuWifiEntry osuWifiEntry) {
        return osuWifiEntry.getConnectedState() == 0;
    }

    static /* synthetic */ boolean lambda$updateWifiEntries$3(StandardWifiEntry standardWifiEntry) {
        int connectedState = standardWifiEntry.getConnectedState();
        return connectedState == 2 || connectedState == 1;
    }

    static /* synthetic */ boolean lambda$updateWifiEntries$4(PasspointWifiEntry passpointWifiEntry) {
        int connectedState = passpointWifiEntry.getConnectedState();
        return connectedState == 2 || connectedState == 1;
    }

    static /* synthetic */ boolean lambda$updateWifiEntries$5(OsuWifiEntry osuWifiEntry) {
        int connectedState = osuWifiEntry.getConnectedState();
        return connectedState == 2 || connectedState == 1;
    }

    private void updateStandardWifiEntryScans(List<ScanResult> list) {
        Preconditions.checkNotNull(list, "Scan Result list should not be null!");
        Map mapScanResultsToKey = Utils.mapScanResultsToKey(list, true, this.mWifiConfigCache, this.mWifiManager.isWpa3SaeSupported(), this.mWifiManager.isWpa3SuiteBSupported(), this.mWifiManager.isEnhancedOpenSupported());
        this.mStandardWifiEntryCache.entrySet().removeIf(new Predicate(mapScanResultsToKey) {
            public final /* synthetic */ Map f$0;

            {
                this.f$0 = r1;
            }

            public final boolean test(Object obj) {
                return WifiPickerTracker.lambda$updateStandardWifiEntryScans$6(this.f$0, (Entry) obj);
            }
        });
        for (Entry entry : mapScanResultsToKey.entrySet()) {
            StandardWifiEntry standardWifiEntry = new StandardWifiEntry(this.mContext, this.mMainHandler, (String) entry.getKey(), (List) entry.getValue(), this.mWifiManager, false);
            standardWifiEntry.updateConfig((WifiConfiguration) this.mWifiConfigCache.get(standardWifiEntry.getKey()));
            this.mStandardWifiEntryCache.put(standardWifiEntry.getKey(), standardWifiEntry);
        }
    }

    static /* synthetic */ boolean lambda$updateStandardWifiEntryScans$6(Map map, Entry entry) {
        String str = (String) entry.getKey();
        StandardWifiEntry standardWifiEntry = (StandardWifiEntry) entry.getValue();
        standardWifiEntry.updateScanResultInfo((List) map.remove(str));
        return standardWifiEntry.getLevel() == -1;
    }

    private void updatePasspointWifiEntryScans(List<ScanResult> list) {
        Preconditions.checkNotNull(list, "Scan Result list should not be null!");
        TreeSet treeSet = new TreeSet();
        for (Pair pair : this.mWifiManager.getAllMatchingWifiConfigs(list)) {
            WifiConfiguration wifiConfiguration = (WifiConfiguration) pair.first;
            List list2 = (List) ((Map) pair.second).get(Integer.valueOf(0));
            List list3 = (List) ((Map) pair.second).get(Integer.valueOf(1));
            String uniqueIdToPasspointWifiEntryKey = PasspointWifiEntry.uniqueIdToPasspointWifiEntryKey(wifiConfiguration.getKey());
            treeSet.add(uniqueIdToPasspointWifiEntryKey);
            if (this.mPasspointConfigCache.containsKey(uniqueIdToPasspointWifiEntryKey)) {
                if (!this.mPasspointWifiEntryCache.containsKey(uniqueIdToPasspointWifiEntryKey)) {
                    Map<String, PasspointWifiEntry> map = this.mPasspointWifiEntryCache;
                    PasspointWifiEntry passpointWifiEntry = new PasspointWifiEntry(this.mContext, this.mMainHandler, (PasspointConfiguration) this.mPasspointConfigCache.get(uniqueIdToPasspointWifiEntryKey), this.mWifiManager, false);
                    map.put(uniqueIdToPasspointWifiEntryKey, passpointWifiEntry);
                }
                ((PasspointWifiEntry) this.mPasspointWifiEntryCache.get(uniqueIdToPasspointWifiEntryKey)).updateScanResultInfo(wifiConfiguration, list2, list3);
            }
        }
        this.mPasspointWifiEntryCache.entrySet().removeIf(new Predicate(treeSet) {
            public final /* synthetic */ Set f$0;

            {
                this.f$0 = r1;
            }

            public final boolean test(Object obj) {
                return WifiPickerTracker.lambda$updatePasspointWifiEntryScans$7(this.f$0, (Entry) obj);
            }
        });
    }

    static /* synthetic */ boolean lambda$updatePasspointWifiEntryScans$7(Set set, Entry entry) {
        return ((PasspointWifiEntry) entry.getValue()).getLevel() == -1 || !set.contains(entry.getKey());
    }

    private void updateOsuWifiEntryScans(List<ScanResult> list) {
        Preconditions.checkNotNull(list, "Scan Result list should not be null!");
        Map matchingOsuProviders = this.mWifiManager.getMatchingOsuProviders(list);
        Set keySet = this.mWifiManager.getMatchingPasspointConfigsForOsuProviders(matchingOsuProviders.keySet()).keySet();
        for (OsuWifiEntry osuWifiEntry : this.mOsuWifiEntryCache.values()) {
            osuWifiEntry.updateScanResultInfo((List) matchingOsuProviders.remove(osuWifiEntry.getOsuProvider()));
        }
        for (OsuProvider osuProvider : matchingOsuProviders.keySet()) {
            OsuWifiEntry osuWifiEntry2 = new OsuWifiEntry(this.mContext, this.mMainHandler, osuProvider, this.mWifiManager, false);
            osuWifiEntry2.updateScanResultInfo((List) matchingOsuProviders.get(osuProvider));
            this.mOsuWifiEntryCache.put(OsuWifiEntry.osuProviderToOsuWifiEntryKey(osuProvider), osuWifiEntry2);
        }
        this.mOsuWifiEntryCache.entrySet().removeIf(new Predicate(keySet) {
            public final /* synthetic */ Set f$0;

            {
                this.f$0 = r1;
            }

            public final boolean test(Object obj) {
                return WifiPickerTracker.lambda$updateOsuWifiEntryScans$8(this.f$0, (Entry) obj);
            }
        });
    }

    static /* synthetic */ boolean lambda$updateOsuWifiEntryScans$8(Set set, Entry entry) {
        return ((OsuWifiEntry) entry.getValue()).getLevel() == -1 || set.contains(((OsuWifiEntry) entry.getValue()).getOsuProvider());
    }

    private void conditionallyUpdateScanResults(boolean z) {
        if (this.mWifiManager.getWifiState() == 1) {
            updateStandardWifiEntryScans(Collections.emptyList());
            updatePasspointWifiEntryScans(Collections.emptyList());
            updateOsuWifiEntryScans(Collections.emptyList());
            return;
        }
        long j = this.mMaxScanAgeMillis;
        if (z) {
            this.mScanResultUpdater.update(this.mWifiManager.getScanResults());
        } else {
            j += this.mScanIntervalMillis;
        }
        List scanResults = this.mScanResultUpdater.getScanResults(j);
        updateStandardWifiEntryScans(scanResults);
        updatePasspointWifiEntryScans(scanResults);
        updateOsuWifiEntryScans(scanResults);
    }

    private void updateStandardWifiEntryConfigs(List<WifiConfiguration> list) {
        Preconditions.checkNotNull(list, "Config list should not be null!");
        this.mWifiConfigCache.clear();
        this.mWifiConfigCache.putAll((Map) list.stream().collect(Collectors.toMap($$Lambda$eRhiL3TPu1j8op3nmit378jGeyk.INSTANCE, Function.identity())));
        this.mStandardWifiEntryCache.entrySet().forEach(new Consumer() {
            public final void accept(Object obj) {
                WifiPickerTracker.this.lambda$updateStandardWifiEntryConfigs$9$WifiPickerTracker((Entry) obj);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateStandardWifiEntryConfigs$9 */
    public /* synthetic */ void lambda$updateStandardWifiEntryConfigs$9$WifiPickerTracker(Entry entry) {
        StandardWifiEntry standardWifiEntry = (StandardWifiEntry) entry.getValue();
        standardWifiEntry.updateConfig((WifiConfiguration) this.mWifiConfigCache.get(standardWifiEntry.getKey()));
    }

    private void updatePasspointWifiEntryConfigs(List<PasspointConfiguration> list) {
        Preconditions.checkNotNull(list, "Config list should not be null!");
        this.mPasspointConfigCache.clear();
        this.mPasspointConfigCache.putAll((Map) list.stream().collect(Collectors.toMap($$Lambda$WifiPickerTracker$yWqMIohRea3hwyWs7tE9nZPEI0.INSTANCE, Function.identity())));
        this.mPasspointWifiEntryCache.entrySet().removeIf(new Predicate() {
            public final boolean test(Object obj) {
                return WifiPickerTracker.this.lambda$updatePasspointWifiEntryConfigs$11$WifiPickerTracker((Entry) obj);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updatePasspointWifiEntryConfigs$11 */
    public /* synthetic */ boolean lambda$updatePasspointWifiEntryConfigs$11$WifiPickerTracker(Entry entry) {
        PasspointWifiEntry passpointWifiEntry = (PasspointWifiEntry) entry.getValue();
        PasspointConfiguration passpointConfiguration = (PasspointConfiguration) this.mPasspointConfigCache.get(passpointWifiEntry.getKey());
        if (passpointConfiguration == null) {
            return true;
        }
        passpointWifiEntry.updatePasspointConfig(passpointConfiguration);
        return false;
    }

    private void updateConnectionInfo(WifiInfo wifiInfo, NetworkInfo networkInfo) {
        for (WifiEntry updateConnectionInfo : this.mStandardWifiEntryCache.values()) {
            updateConnectionInfo.updateConnectionInfo(wifiInfo, networkInfo);
        }
        for (WifiEntry updateConnectionInfo2 : this.mPasspointWifiEntryCache.values()) {
            updateConnectionInfo2.updateConnectionInfo(wifiInfo, networkInfo);
        }
        for (WifiEntry updateConnectionInfo3 : this.mOsuWifiEntryCache.values()) {
            updateConnectionInfo3.updateConnectionInfo(wifiInfo, networkInfo);
        }
    }

    private void conditionallyCreateConnectedStandardWifiEntry(WifiInfo wifiInfo, NetworkInfo networkInfo) {
        if (!wifiInfo.isPasspointAp()) {
            this.mWifiConfigCache.values().stream().filter(new Predicate(wifiInfo.getNetworkId()) {
                public final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final boolean test(Object obj) {
                    return WifiPickerTracker.this.mo19971x6cc82ccd(this.f$1, (WifiConfiguration) obj);
                }
            }).findAny().ifPresent(new Consumer(wifiInfo, networkInfo) {
                public final /* synthetic */ WifiInfo f$1;
                public final /* synthetic */ NetworkInfo f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void accept(Object obj) {
                    WifiPickerTracker.this.mo19972x3a728e8e(this.f$1, this.f$2, (WifiConfiguration) obj);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$conditionallyCreateConnectedStandardWifiEntry$12 */
    public /* synthetic */ boolean mo19971x6cc82ccd(int i, WifiConfiguration wifiConfiguration) {
        return wifiConfiguration.networkId == i && !this.mStandardWifiEntryCache.containsKey(StandardWifiEntry.wifiConfigToStandardWifiEntryKey(wifiConfiguration));
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$conditionallyCreateConnectedStandardWifiEntry$13 */
    public /* synthetic */ void mo19972x3a728e8e(WifiInfo wifiInfo, NetworkInfo networkInfo, WifiConfiguration wifiConfiguration) {
        StandardWifiEntry standardWifiEntry = new StandardWifiEntry(this.mContext, this.mMainHandler, StandardWifiEntry.wifiConfigToStandardWifiEntryKey(wifiConfiguration), wifiConfiguration, this.mWifiManager, false);
        standardWifiEntry.updateConnectionInfo(wifiInfo, networkInfo);
        this.mStandardWifiEntryCache.put(standardWifiEntry.getKey(), standardWifiEntry);
    }

    private void conditionallyCreateConnectedPasspointWifiEntry(WifiInfo wifiInfo, NetworkInfo networkInfo) {
        if (wifiInfo.isPasspointAp()) {
            this.mPasspointConfigCache.values().stream().filter(new Predicate(wifiInfo.getPasspointFqdn()) {
                public final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final boolean test(Object obj) {
                    return WifiPickerTracker.this.mo19969x345da323(this.f$1, (PasspointConfiguration) obj);
                }
            }).findAny().ifPresent(new Consumer(wifiInfo, networkInfo) {
                public final /* synthetic */ WifiInfo f$1;
                public final /* synthetic */ NetworkInfo f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void accept(Object obj) {
                    WifiPickerTracker.this.mo19970x20804e4(this.f$1, this.f$2, (PasspointConfiguration) obj);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$conditionallyCreateConnectedPasspointWifiEntry$14 */
    public /* synthetic */ boolean mo19969x345da323(String str, PasspointConfiguration passpointConfiguration) {
        return passpointConfiguration.getHomeSp().getFqdn() == str && !this.mPasspointWifiEntryCache.containsKey(PasspointWifiEntry.uniqueIdToPasspointWifiEntryKey(passpointConfiguration.getUniqueId()));
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$conditionallyCreateConnectedPasspointWifiEntry$15 */
    public /* synthetic */ void mo19970x20804e4(WifiInfo wifiInfo, NetworkInfo networkInfo, PasspointConfiguration passpointConfiguration) {
        PasspointWifiEntry passpointWifiEntry = new PasspointWifiEntry(this.mContext, this.mMainHandler, passpointConfiguration, this.mWifiManager, false);
        passpointWifiEntry.updateConnectionInfo(wifiInfo, networkInfo);
        this.mPasspointWifiEntryCache.put(passpointWifiEntry.getKey(), passpointWifiEntry);
    }

    private void notifyOnWifiEntriesChanged() {
        WifiPickerTrackerCallback wifiPickerTrackerCallback = this.mListener;
        if (wifiPickerTrackerCallback != null) {
            Handler handler = this.mMainHandler;
            Objects.requireNonNull(wifiPickerTrackerCallback);
            handler.post(new Runnable() {
                public final void run() {
                    WifiPickerTrackerCallback.this.onWifiEntriesChanged();
                }
            });
        }
    }

    private void notifyOnNumSavedNetworksChanged() {
        WifiPickerTrackerCallback wifiPickerTrackerCallback = this.mListener;
        if (wifiPickerTrackerCallback != null) {
            Handler handler = this.mMainHandler;
            Objects.requireNonNull(wifiPickerTrackerCallback);
            handler.post(new Runnable() {
                public final void run() {
                    WifiPickerTrackerCallback.this.onNumSavedNetworksChanged();
                }
            });
        }
    }

    private void notifyOnNumSavedSubscriptionsChanged() {
        WifiPickerTrackerCallback wifiPickerTrackerCallback = this.mListener;
        if (wifiPickerTrackerCallback != null) {
            Handler handler = this.mMainHandler;
            Objects.requireNonNull(wifiPickerTrackerCallback);
            handler.post(new Runnable() {
                public final void run() {
                    WifiPickerTrackerCallback.this.onNumSavedSubscriptionsChanged();
                }
            });
        }
    }
}
