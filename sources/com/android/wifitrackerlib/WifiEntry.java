package com.android.wifitrackerlib;

import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkUtils;
import android.net.RouteInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import androidx.core.util.Preconditions;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public abstract class WifiEntry implements Comparable<WifiEntry> {
    protected Handler mCallbackHandler;
    protected boolean mCalledConnect = false;
    protected boolean mCalledDisconnect = false;
    protected ConnectCallback mConnectCallback;
    protected ConnectedInfo mConnectedInfo;
    protected DisconnectCallback mDisconnectCallback;
    final boolean mForSavedNetworksPage;
    protected int mLevel = -1;
    private WifiEntryCallback mListener;
    protected NetworkCapabilities mNetworkCapabilities;
    protected NetworkInfo mNetworkInfo;
    protected WifiInfo mWifiInfo;
    protected final WifiManager mWifiManager;

    /* renamed from: com.android.wifitrackerlib.WifiEntry$1 */
    static /* synthetic */ class C17961 {
        static final /* synthetic */ int[] $SwitchMap$android$net$NetworkInfo$DetailedState;

        /* JADX WARNING: Can't wrap try/catch for region: R(14:0|1|2|3|4|5|6|7|8|9|10|11|12|(3:13|14|16)) */
        /* JADX WARNING: Can't wrap try/catch for region: R(16:0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|16) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x003e */
        /* JADX WARNING: Missing exception handler attribute for start block: B:13:0x0049 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x0028 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0033 */
        static {
            /*
                android.net.NetworkInfo$DetailedState[] r0 = android.net.NetworkInfo.DetailedState.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$android$net$NetworkInfo$DetailedState = r0
                android.net.NetworkInfo$DetailedState r1 = android.net.NetworkInfo.DetailedState.SCANNING     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = $SwitchMap$android$net$NetworkInfo$DetailedState     // Catch:{ NoSuchFieldError -> 0x001d }
                android.net.NetworkInfo$DetailedState r1 = android.net.NetworkInfo.DetailedState.CONNECTING     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = $SwitchMap$android$net$NetworkInfo$DetailedState     // Catch:{ NoSuchFieldError -> 0x0028 }
                android.net.NetworkInfo$DetailedState r1 = android.net.NetworkInfo.DetailedState.AUTHENTICATING     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                int[] r0 = $SwitchMap$android$net$NetworkInfo$DetailedState     // Catch:{ NoSuchFieldError -> 0x0033 }
                android.net.NetworkInfo$DetailedState r1 = android.net.NetworkInfo.DetailedState.OBTAINING_IPADDR     // Catch:{ NoSuchFieldError -> 0x0033 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0033 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0033 }
            L_0x0033:
                int[] r0 = $SwitchMap$android$net$NetworkInfo$DetailedState     // Catch:{ NoSuchFieldError -> 0x003e }
                android.net.NetworkInfo$DetailedState r1 = android.net.NetworkInfo.DetailedState.VERIFYING_POOR_LINK     // Catch:{ NoSuchFieldError -> 0x003e }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x003e }
                r2 = 5
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x003e }
            L_0x003e:
                int[] r0 = $SwitchMap$android$net$NetworkInfo$DetailedState     // Catch:{ NoSuchFieldError -> 0x0049 }
                android.net.NetworkInfo$DetailedState r1 = android.net.NetworkInfo.DetailedState.CAPTIVE_PORTAL_CHECK     // Catch:{ NoSuchFieldError -> 0x0049 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0049 }
                r2 = 6
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0049 }
            L_0x0049:
                int[] r0 = $SwitchMap$android$net$NetworkInfo$DetailedState     // Catch:{ NoSuchFieldError -> 0x0054 }
                android.net.NetworkInfo$DetailedState r1 = android.net.NetworkInfo.DetailedState.CONNECTED     // Catch:{ NoSuchFieldError -> 0x0054 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0054 }
                r2 = 7
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0054 }
            L_0x0054:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.wifitrackerlib.WifiEntry.C17961.<clinit>():void");
        }
    }

    public interface ConnectCallback {
        void onConnectResult(int i);
    }

    public static class ConnectedInfo {
        public List<String> dnsServers;
        public int frequencyMhz;
        public String gateway;
        public String ipAddress;
        public List<String> ipv6Addresses;
        public int linkSpeedMbps;
        public String subnetMask;

        public ConnectedInfo() {
            new ArrayList();
            new ArrayList();
        }
    }

    public interface DisconnectCallback {
        void onDisconnectResult(int i);
    }

    public interface WifiEntryCallback {
        void onUpdated();
    }

    public abstract boolean canSetAutoJoinEnabled();

    public abstract boolean canSetMeteredChoice();

    /* access modifiers changed from: protected */
    public abstract boolean connectionInfoMatches(WifiInfo wifiInfo, NetworkInfo networkInfo);

    public abstract String getKey();

    public abstract int getLevel();

    public abstract int getMeteredChoice();

    /* access modifiers changed from: 0000 */
    public abstract String getScanResultDescription();

    public abstract int getSecurity();

    public abstract String getSummary(boolean z);

    public abstract String getTitle();

    public abstract WifiConfiguration getWifiConfiguration();

    public abstract boolean isAutoJoinEnabled();

    public abstract boolean isMetered();

    public abstract boolean isSaved();

    public abstract boolean isSubscription();

    WifiEntry(Handler handler, WifiManager wifiManager, boolean z) throws IllegalArgumentException {
        Preconditions.checkNotNull(handler, "Cannot construct with null handler!");
        Preconditions.checkNotNull(wifiManager, "Cannot construct with null WifiManager!");
        this.mCallbackHandler = handler;
        this.mForSavedNetworksPage = z;
        this.mWifiManager = wifiManager;
    }

    public int getConnectedState() {
        NetworkInfo networkInfo = this.mNetworkInfo;
        if (networkInfo == null) {
            return 0;
        }
        switch (C17961.$SwitchMap$android$net$NetworkInfo$DetailedState[networkInfo.getDetailedState().ordinal()]) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                return 1;
            case 7:
                return 2;
            default:
                return 0;
        }
    }

    public String getSummary() {
        return getSummary(true);
    }

    public ConnectedInfo getConnectedInfo() {
        if (getConnectedState() != 2) {
            return null;
        }
        return this.mConnectedInfo;
    }

    public void setListener(WifiEntryCallback wifiEntryCallback) {
        this.mListener = wifiEntryCallback;
    }

    /* access modifiers changed from: protected */
    public void notifyOnUpdated() {
        if (this.mListener != null) {
            this.mCallbackHandler.post(new Runnable() {
                public final void run() {
                    WifiEntry.this.lambda$notifyOnUpdated$0$WifiEntry();
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$notifyOnUpdated$0 */
    public /* synthetic */ void lambda$notifyOnUpdated$0$WifiEntry() {
        this.mListener.onUpdated();
    }

    /* access modifiers changed from: 0000 */
    public void updateConnectionInfo(WifiInfo wifiInfo, NetworkInfo networkInfo) {
        if (wifiInfo == null || networkInfo == null || !connectionInfoMatches(wifiInfo, networkInfo)) {
            this.mNetworkInfo = null;
            this.mNetworkCapabilities = null;
            this.mConnectedInfo = null;
            if (this.mCalledDisconnect) {
                this.mCalledDisconnect = false;
                this.mCallbackHandler.post(new Runnable() {
                    public final void run() {
                        WifiEntry.this.lambda$updateConnectionInfo$2$WifiEntry();
                    }
                });
            }
        } else {
            this.mWifiInfo = wifiInfo;
            this.mNetworkInfo = networkInfo;
            int rssi = wifiInfo.getRssi();
            if (rssi != -127) {
                this.mLevel = this.mWifiManager.calculateSignalLevel(rssi);
            }
            if (getConnectedState() == 2) {
                if (this.mCalledConnect) {
                    this.mCalledConnect = false;
                    this.mCallbackHandler.post(new Runnable() {
                        public final void run() {
                            WifiEntry.this.lambda$updateConnectionInfo$1$WifiEntry();
                        }
                    });
                }
                if (this.mConnectedInfo == null) {
                    this.mConnectedInfo = new ConnectedInfo();
                }
                this.mConnectedInfo.frequencyMhz = wifiInfo.getFrequency();
                this.mConnectedInfo.linkSpeedMbps = wifiInfo.getLinkSpeed();
            }
        }
        notifyOnUpdated();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateConnectionInfo$1 */
    public /* synthetic */ void lambda$updateConnectionInfo$1$WifiEntry() {
        ConnectCallback connectCallback = this.mConnectCallback;
        if (connectCallback != null) {
            connectCallback.onConnectResult(0);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateConnectionInfo$2 */
    public /* synthetic */ void lambda$updateConnectionInfo$2$WifiEntry() {
        DisconnectCallback disconnectCallback = this.mDisconnectCallback;
        if (disconnectCallback != null) {
            disconnectCallback.onDisconnectResult(0);
        }
    }

    /* access modifiers changed from: 0000 */
    public void updateLinkProperties(LinkProperties linkProperties) {
        if (linkProperties == null || getConnectedState() != 2) {
            this.mConnectedInfo = null;
            notifyOnUpdated();
            return;
        }
        if (this.mConnectedInfo == null) {
            this.mConnectedInfo = new ConnectedInfo();
        }
        ArrayList arrayList = new ArrayList();
        for (LinkAddress linkAddress : linkProperties.getLinkAddresses()) {
            if (linkAddress.getAddress() instanceof Inet4Address) {
                this.mConnectedInfo.ipAddress = linkAddress.getAddress().getHostAddress();
                try {
                    InetAddress byAddress = InetAddress.getByAddress(new byte[]{-1, -1, -1, -1});
                    this.mConnectedInfo.subnetMask = NetworkUtils.getNetworkPart(byAddress, linkAddress.getPrefixLength()).getHostAddress();
                } catch (UnknownHostException unused) {
                }
            } else if (linkAddress.getAddress() instanceof Inet6Address) {
                arrayList.add(linkAddress.getAddress().getHostAddress());
            }
        }
        this.mConnectedInfo.ipv6Addresses = arrayList;
        Iterator it = linkProperties.getRoutes().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            RouteInfo routeInfo = (RouteInfo) it.next();
            if (routeInfo.isIPv4Default() && routeInfo.hasGateway()) {
                this.mConnectedInfo.gateway = routeInfo.getGateway().getHostAddress();
                break;
            }
        }
        this.mConnectedInfo.dnsServers = (List) linkProperties.getDnsServers().stream().map($$Lambda$XZAGhHrbkIDyusER4MAM6luKcT0.INSTANCE).collect(Collectors.toList());
        notifyOnUpdated();
    }

    /* access modifiers changed from: 0000 */
    public String getWifiInfoDescription() {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (getConnectedState() == 2 && this.mWifiInfo != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("f = ");
            sb.append(this.mWifiInfo.getFrequency());
            stringJoiner.add(sb.toString());
            String bssid = this.mWifiInfo.getBSSID();
            if (bssid != null) {
                stringJoiner.add(bssid);
            }
            StringBuilder sb2 = new StringBuilder();
            sb2.append("standard = ");
            sb2.append(this.mWifiInfo.getWifiStandard());
            stringJoiner.add(sb2.toString());
            StringBuilder sb3 = new StringBuilder();
            sb3.append("rssi = ");
            sb3.append(this.mWifiInfo.getRssi());
            stringJoiner.add(sb3.toString());
            StringBuilder sb4 = new StringBuilder();
            sb4.append("score = ");
            sb4.append(this.mWifiInfo.getScore());
            stringJoiner.add(sb4.toString());
            stringJoiner.add(String.format(" tx=%.1f,", new Object[]{Double.valueOf(this.mWifiInfo.getSuccessfulTxPacketsPerSecond())}));
            stringJoiner.add(String.format("%.1f,", new Object[]{Double.valueOf(this.mWifiInfo.getRetriedTxPacketsPerSecond())}));
            stringJoiner.add(String.format("%.1f ", new Object[]{Double.valueOf(this.mWifiInfo.getLostTxPacketsPerSecond())}));
            stringJoiner.add(String.format("rx=%.1f", new Object[]{Double.valueOf(this.mWifiInfo.getSuccessfulRxPacketsPerSecond())}));
        }
        return stringJoiner.toString();
    }

    public int compareTo(WifiEntry wifiEntry) {
        if (getLevel() != -1 && wifiEntry.getLevel() == -1) {
            return -1;
        }
        if (getLevel() == -1 && wifiEntry.getLevel() != -1) {
            return 1;
        }
        if (isSubscription() && !wifiEntry.isSubscription()) {
            return -1;
        }
        if (!isSubscription() && wifiEntry.isSubscription()) {
            return 1;
        }
        if (isSaved() && !wifiEntry.isSaved()) {
            return -1;
        }
        if (!isSaved() && wifiEntry.isSaved()) {
            return 1;
        }
        if (getLevel() > wifiEntry.getLevel()) {
            return -1;
        }
        if (getLevel() < wifiEntry.getLevel()) {
            return 1;
        }
        return getTitle().compareTo(wifiEntry.getTitle());
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof WifiEntry)) {
            return false;
        }
        return getKey().equals(((WifiEntry) obj).getKey());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getKey());
        sb.append(",title:");
        sb.append(getTitle());
        sb.append(",summary:");
        sb.append(getSummary());
        sb.append(",level:");
        sb.append(getLevel());
        sb.append(",security:");
        sb.append(getSecurity());
        sb.append(",connected:");
        sb.append(getConnectedState() == 2 ? "true" : "false");
        sb.append(",connectedInfo:");
        sb.append(getConnectedInfo());
        return sb.toString();
    }
}
