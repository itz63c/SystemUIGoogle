package com.android.wifitrackerlib;

import android.app.AppGlobals;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.os.RemoteException;
import android.os.UserHandle;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringJoiner;

class Utils {
    static String getSpeedDescription(Context context, WifiEntry wifiEntry) {
        String str = "";
        if (context == null || wifiEntry == null) {
        }
        return str;
    }

    static ScanResult getBestScanResultByLevel(List<ScanResult> list) {
        if (list.isEmpty()) {
            return null;
        }
        return (ScanResult) Collections.max(list, Comparator.comparingInt($$Lambda$Utils$wGn2sVTZ5l1wFLkqd7rxKtPh0RU.INSTANCE));
    }

    static List<Integer> getSecurityTypesFromScanResult(ScanResult scanResult) {
        ArrayList arrayList = new ArrayList();
        String str = scanResult.capabilities;
        Integer valueOf = Integer.valueOf(0);
        if (str == null) {
            arrayList.add(valueOf);
        } else {
            String str2 = "PSK";
            String str3 = "SAE";
            if (str.contains(str2) && scanResult.capabilities.contains(str3)) {
                arrayList.add(Integer.valueOf(2));
                arrayList.add(Integer.valueOf(5));
            } else if (scanResult.capabilities.contains("OWE_TRANSITION")) {
                arrayList.add(valueOf);
                arrayList.add(Integer.valueOf(4));
            } else if (scanResult.capabilities.contains("OWE")) {
                arrayList.add(Integer.valueOf(4));
            } else if (scanResult.capabilities.contains("WEP")) {
                arrayList.add(Integer.valueOf(1));
            } else if (scanResult.capabilities.contains(str3)) {
                arrayList.add(Integer.valueOf(5));
            } else if (scanResult.capabilities.contains(str2)) {
                arrayList.add(Integer.valueOf(2));
            } else if (scanResult.capabilities.contains("EAP_SUITE_B_192")) {
                arrayList.add(Integer.valueOf(6));
            } else if (scanResult.capabilities.contains("EAP")) {
                arrayList.add(Integer.valueOf(3));
            } else {
                arrayList.add(valueOf);
            }
        }
        return arrayList;
    }

    static int getSecurityTypeFromWifiConfiguration(WifiConfiguration wifiConfiguration) {
        if (wifiConfiguration.allowedKeyManagement.get(8)) {
            return 5;
        }
        int i = 1;
        if (wifiConfiguration.allowedKeyManagement.get(1)) {
            return 2;
        }
        if (wifiConfiguration.allowedKeyManagement.get(10)) {
            return 6;
        }
        if (wifiConfiguration.allowedKeyManagement.get(2) || wifiConfiguration.allowedKeyManagement.get(3)) {
            return 3;
        }
        if (wifiConfiguration.allowedKeyManagement.get(9)) {
            return 4;
        }
        if (wifiConfiguration.wepKeys[0] == null) {
            i = 0;
        }
        return i;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:100:0x0209, code lost:
        if (r12 == false) goto L_0x0284;
     */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x023e  */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x0258  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x011e  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x012c  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x013c  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x015a  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x016a  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x0178  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x0186  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x0196  */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x01a7  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static java.util.Map<java.lang.String, java.util.List<android.net.wifi.ScanResult>> mapScanResultsToKey(java.util.List<android.net.wifi.ScanResult> r26, boolean r27, java.util.Map<java.lang.String, android.net.wifi.WifiConfiguration> r28, boolean r29, boolean r30, boolean r31) {
        /*
            if (r28 != 0) goto L_0x0008
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            goto L_0x000a
        L_0x0008:
            r0 = r28
        L_0x000a:
            java.util.stream.Stream r1 = r26.stream()
            com.android.wifitrackerlib.-$$Lambda$Utils$YXgA7eQ3EufOS8jlgf9HRQs4bfM r2 = com.android.wifitrackerlib.$$Lambda$Utils$YXgA7eQ3EufOS8jlgf9HRQs4bfM.INSTANCE
            java.util.stream.Stream r1 = r1.filter(r2)
            com.android.wifitrackerlib.-$$Lambda$Utils$_MVjtMEczmHvXav1qgSkgxMj5iE r2 = com.android.wifitrackerlib.$$Lambda$Utils$_MVjtMEczmHvXav1qgSkgxMj5iE.INSTANCE
            java.util.stream.Collector r2 = java.util.stream.Collectors.groupingBy(r2)
            java.lang.Object r1 = r1.collect(r2)
            java.util.Map r1 = (java.util.Map) r1
            java.util.HashMap r2 = new java.util.HashMap
            r2.<init>()
            java.util.Set r3 = r1.keySet()
            java.util.Iterator r3 = r3.iterator()
        L_0x002d:
            boolean r4 = r3.hasNext()
            if (r4 == 0) goto L_0x0291
            java.lang.Object r4 = r3.next()
            java.lang.String r4 = (java.lang.String) r4
            r5 = 2
            java.lang.String r6 = com.android.wifitrackerlib.StandardWifiEntry.ssidAndSecurityToStandardWifiEntryKey(r4, r5)
            boolean r6 = r0.containsKey(r6)
            r7 = 5
            java.lang.String r8 = com.android.wifitrackerlib.StandardWifiEntry.ssidAndSecurityToStandardWifiEntryKey(r4, r7)
            boolean r8 = r0.containsKey(r8)
            r9 = 0
            java.lang.String r10 = com.android.wifitrackerlib.StandardWifiEntry.ssidAndSecurityToStandardWifiEntryKey(r4, r9)
            boolean r10 = r0.containsKey(r10)
            r11 = 4
            java.lang.String r12 = com.android.wifitrackerlib.StandardWifiEntry.ssidAndSecurityToStandardWifiEntryKey(r4, r11)
            boolean r12 = r0.containsKey(r12)
            java.lang.Object r13 = r1.get(r4)
            java.util.List r13 = (java.util.List) r13
            java.util.Iterator r13 = r13.iterator()
            r14 = r9
            r15 = r14
            r16 = r15
            r17 = r16
        L_0x006d:
            boolean r18 = r13.hasNext()
            r19 = 1
            if (r18 == 0) goto L_0x00b5
            java.lang.Object r18 = r13.next()
            android.net.wifi.ScanResult r18 = (android.net.wifi.ScanResult) r18
            java.util.List r9 = getSecurityTypesFromScanResult(r18)
            java.lang.Integer r11 = java.lang.Integer.valueOf(r5)
            boolean r11 = r9.contains(r11)
            if (r11 == 0) goto L_0x008b
            r14 = r19
        L_0x008b:
            java.lang.Integer r11 = java.lang.Integer.valueOf(r7)
            boolean r11 = r9.contains(r11)
            if (r11 == 0) goto L_0x0097
            r15 = r19
        L_0x0097:
            r11 = 4
            java.lang.Integer r5 = java.lang.Integer.valueOf(r11)
            boolean r5 = r9.contains(r5)
            if (r5 == 0) goto L_0x00a4
            r16 = r19
        L_0x00a4:
            r5 = 0
            java.lang.Integer r11 = java.lang.Integer.valueOf(r5)
            boolean r5 = r9.contains(r11)
            if (r5 == 0) goto L_0x00b1
            r17 = r19
        L_0x00b1:
            r5 = 2
            r9 = 0
            r11 = 4
            goto L_0x006d
        L_0x00b5:
            java.lang.Object r5 = r1.get(r4)
            java.util.List r5 = (java.util.List) r5
            java.util.Iterator r5 = r5.iterator()
        L_0x00bf:
            boolean r9 = r5.hasNext()
            if (r9 == 0) goto L_0x002d
            java.lang.Object r9 = r5.next()
            android.net.wifi.ScanResult r9 = (android.net.wifi.ScanResult) r9
            java.util.List r11 = getSecurityTypesFromScanResult(r9)
            java.util.ArrayList r13 = new java.util.ArrayList
            r13.<init>()
            r20 = r0
            if (r29 != 0) goto L_0x00df
            java.lang.Integer r0 = java.lang.Integer.valueOf(r7)
            r11.remove(r0)
        L_0x00df:
            if (r30 != 0) goto L_0x00e9
            r0 = 6
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            r11.remove(r0)
        L_0x00e9:
            if (r31 != 0) goto L_0x00f5
            r0 = 4
            java.lang.Integer r7 = java.lang.Integer.valueOf(r0)
            r11.remove(r7)
            r0 = 5
            goto L_0x00f6
        L_0x00f5:
            r0 = r7
        L_0x00f6:
            java.lang.Integer r7 = java.lang.Integer.valueOf(r0)
            boolean r0 = r11.contains(r7)
            if (r0 == 0) goto L_0x0110
            r0 = 2
            java.lang.Integer r7 = java.lang.Integer.valueOf(r0)
            boolean r7 = r11.contains(r7)
            if (r7 != 0) goto L_0x0111
            r21 = r1
            r7 = r19
            goto L_0x0114
        L_0x0110:
            r0 = 2
        L_0x0111:
            r21 = r1
            r7 = 0
        L_0x0114:
            java.lang.Integer r1 = java.lang.Integer.valueOf(r0)
            boolean r1 = r11.contains(r1)
            if (r1 == 0) goto L_0x012c
            r1 = 5
            java.lang.Integer r0 = java.lang.Integer.valueOf(r1)
            boolean r0 = r11.contains(r0)
            if (r0 != 0) goto L_0x012d
            r0 = r19
            goto L_0x012e
        L_0x012c:
            r1 = 5
        L_0x012d:
            r0 = 0
        L_0x012e:
            r18 = 2
            java.lang.Integer r1 = java.lang.Integer.valueOf(r18)
            boolean r1 = r11.contains(r1)
            r22 = r3
            if (r1 == 0) goto L_0x014c
            r1 = 5
            java.lang.Integer r3 = java.lang.Integer.valueOf(r1)
            boolean r1 = r11.contains(r3)
            if (r1 == 0) goto L_0x014c
            r23 = r5
            r1 = r19
            goto L_0x014f
        L_0x014c:
            r23 = r5
            r1 = 0
        L_0x014f:
            r3 = 4
            java.lang.Integer r5 = java.lang.Integer.valueOf(r3)
            boolean r3 = r11.contains(r5)
            if (r3 == 0) goto L_0x016a
            r3 = 0
            java.lang.Integer r5 = java.lang.Integer.valueOf(r3)
            boolean r5 = r11.contains(r5)
            if (r5 != 0) goto L_0x016b
            r24 = r9
            r5 = r19
            goto L_0x016e
        L_0x016a:
            r3 = 0
        L_0x016b:
            r5 = r3
            r24 = r9
        L_0x016e:
            java.lang.Integer r9 = java.lang.Integer.valueOf(r3)
            boolean r9 = r11.contains(r9)
            if (r9 == 0) goto L_0x0186
            r9 = 4
            java.lang.Integer r3 = java.lang.Integer.valueOf(r9)
            boolean r3 = r11.contains(r3)
            if (r3 == 0) goto L_0x0187
            r3 = r19
            goto L_0x0188
        L_0x0186:
            r9 = 4
        L_0x0187:
            r3 = 0
        L_0x0188:
            r25 = 0
            java.lang.Integer r9 = java.lang.Integer.valueOf(r25)
            boolean r9 = r11.contains(r9)
            r25 = r2
            if (r9 == 0) goto L_0x01a4
            r9 = 4
            java.lang.Integer r2 = java.lang.Integer.valueOf(r9)
            boolean r2 = r11.contains(r2)
            if (r2 != 0) goto L_0x01a4
            r2 = r19
            goto L_0x01a5
        L_0x01a4:
            r2 = 0
        L_0x01a5:
            if (r27 == 0) goto L_0x023e
            if (r0 == 0) goto L_0x01ca
            if (r6 != 0) goto L_0x01bc
            if (r8 == 0) goto L_0x01bc
            if (r15 == 0) goto L_0x01bc
            r0 = r20
            r1 = r21
            r3 = r22
            r5 = r23
            r2 = r25
            r7 = 5
            goto L_0x00bf
        L_0x01bc:
            r0 = 2
            java.lang.Integer r1 = java.lang.Integer.valueOf(r0)
            r13.add(r1)
        L_0x01c4:
            r3 = r0
            r0 = 5
            r1 = 4
            r2 = 0
            goto L_0x024e
        L_0x01ca:
            r0 = 2
            if (r1 == 0) goto L_0x01e7
            if (r6 != 0) goto L_0x01da
            if (r8 == 0) goto L_0x01da
            r1 = 5
            java.lang.Integer r2 = java.lang.Integer.valueOf(r1)
            r13.add(r2)
            goto L_0x01c4
        L_0x01da:
            java.lang.Integer r1 = java.lang.Integer.valueOf(r0)
            r13.add(r1)
            r0 = 5
        L_0x01e2:
            r1 = 4
        L_0x01e3:
            r2 = 0
        L_0x01e4:
            r3 = 2
            goto L_0x024e
        L_0x01e7:
            if (r7 == 0) goto L_0x0202
            if (r6 != 0) goto L_0x01f8
            if (r8 != 0) goto L_0x01ef
            if (r14 != 0) goto L_0x01f8
        L_0x01ef:
            r0 = 5
            java.lang.Integer r1 = java.lang.Integer.valueOf(r0)
            r13.add(r1)
            goto L_0x01e2
        L_0x01f8:
            r0 = 5
            r1 = 2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r1)
            r13.add(r2)
            goto L_0x01e2
        L_0x0202:
            r0 = 5
            if (r5 == 0) goto L_0x020d
            if (r17 == 0) goto L_0x01e2
            if (r10 == 0) goto L_0x01e2
            if (r12 != 0) goto L_0x01e2
            goto L_0x0284
        L_0x020d:
            if (r3 == 0) goto L_0x0226
            if (r12 != 0) goto L_0x021d
            if (r10 != 0) goto L_0x0214
            goto L_0x021d
        L_0x0214:
            r1 = 0
            java.lang.Integer r2 = java.lang.Integer.valueOf(r1)
            r13.add(r2)
            goto L_0x01e2
        L_0x021d:
            r1 = 4
            java.lang.Integer r2 = java.lang.Integer.valueOf(r1)
            r13.add(r2)
            goto L_0x01e3
        L_0x0226:
            r1 = 4
            if (r2 == 0) goto L_0x0239
            if (r16 == 0) goto L_0x0230
            if (r12 != 0) goto L_0x0284
            if (r10 != 0) goto L_0x0230
            goto L_0x0284
        L_0x0230:
            r2 = 0
            java.lang.Integer r3 = java.lang.Integer.valueOf(r2)
            r13.add(r3)
            goto L_0x01e4
        L_0x0239:
            r2 = 0
            r13.addAll(r11)
            goto L_0x01e4
        L_0x023e:
            r0 = 5
            r1 = 4
            r2 = 0
            r13.addAll(r11)
            r3 = 2
            if (r7 == 0) goto L_0x024e
            java.lang.Integer r5 = java.lang.Integer.valueOf(r3)
            r13.add(r5)
        L_0x024e:
            java.util.Iterator r5 = r13.iterator()
        L_0x0252:
            boolean r7 = r5.hasNext()
            if (r7 == 0) goto L_0x0284
            java.lang.Object r7 = r5.next()
            java.lang.Integer r7 = (java.lang.Integer) r7
            int r7 = r7.intValue()
            java.lang.String r7 = com.android.wifitrackerlib.StandardWifiEntry.ssidAndSecurityToStandardWifiEntryKey(r4, r7)
            r9 = r25
            boolean r11 = r9.containsKey(r7)
            if (r11 != 0) goto L_0x0276
            java.util.ArrayList r11 = new java.util.ArrayList
            r11.<init>()
            r9.put(r7, r11)
        L_0x0276:
            java.lang.Object r7 = r9.get(r7)
            java.util.List r7 = (java.util.List) r7
            r11 = r24
            r7.add(r11)
            r25 = r9
            goto L_0x0252
        L_0x0284:
            r7 = r0
            r0 = r20
            r1 = r21
            r3 = r22
            r5 = r23
            r2 = r25
            goto L_0x00bf
        L_0x0291:
            r9 = r2
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.wifitrackerlib.Utils.mapScanResultsToKey(java.util.List, boolean, java.util.Map, boolean, boolean, boolean):java.util.Map");
    }

    static /* synthetic */ boolean lambda$mapScanResultsToKey$1(ScanResult scanResult) {
        return !TextUtils.isEmpty(scanResult.SSID);
    }

    static CharSequence getAppLabel(Context context, String str) {
        try {
            return context.getPackageManager().getApplicationInfoAsUser(str, 0, UserHandle.getUserId(-2)).loadLabel(context.getPackageManager());
        } catch (NameNotFoundException unused) {
            return "";
        }
    }

    static CharSequence getAppLabelForSavedNetwork(Context context, WifiEntry wifiEntry) {
        WifiConfiguration wifiConfiguration = wifiEntry.getWifiConfiguration();
        String str = "";
        if (!(context == null || wifiEntry == null || wifiConfiguration == null)) {
            PackageManager packageManager = context.getPackageManager();
            String nameForUid = packageManager.getNameForUid(1000);
            int userId = UserHandle.getUserId(wifiConfiguration.creatorUid);
            ApplicationInfo applicationInfo = null;
            String str2 = wifiConfiguration.creatorName;
            if (str2 == null || !str2.equals(nameForUid)) {
                try {
                    applicationInfo = AppGlobals.getPackageManager().getApplicationInfo(wifiConfiguration.creatorName, 0, userId);
                } catch (RemoteException unused) {
                }
            } else {
                applicationInfo = context.getApplicationInfo();
            }
            if (applicationInfo != null && !applicationInfo.packageName.equals(context.getString(R$string.settings_package)) && !applicationInfo.packageName.equals(context.getString(R$string.certinstaller_package))) {
                return applicationInfo.loadLabel(packageManager);
            }
        }
        return str;
    }

    static String getDisconnectedStateDescription(Context context, WifiEntry wifiEntry) {
        String str = "";
        if (!(context == null || wifiEntry == null)) {
            WifiConfiguration wifiConfiguration = wifiEntry.getWifiConfiguration();
            if (wifiConfiguration == null) {
                return null;
            }
            if (wifiConfiguration.hasNoInternetAccess()) {
                return context.getString(wifiConfiguration.getNetworkSelectionStatus().getNetworkSelectionStatus() == 2 ? R$string.wifi_no_internet_no_reconnect : R$string.wifi_no_internet);
            } else if (wifiConfiguration.getNetworkSelectionStatus().getNetworkSelectionStatus() != 0) {
                int networkSelectionDisableReason = wifiConfiguration.getNetworkSelectionStatus().getNetworkSelectionDisableReason();
                if (networkSelectionDisableReason == 1) {
                    return context.getString(R$string.wifi_disabled_generic);
                }
                if (networkSelectionDisableReason == 2) {
                    return context.getString(R$string.wifi_disabled_password_failure);
                }
                if (networkSelectionDisableReason == 3) {
                    return context.getString(R$string.wifi_disabled_network_failure);
                }
                if (networkSelectionDisableReason == 8) {
                    return context.getString(R$string.wifi_check_password_try_again);
                }
            } else if (wifiEntry.getLevel() != -1 && wifiConfiguration.getRecentFailureReason() == 17) {
                return context.getString(R$string.wifi_ap_unable_to_handle_new_sta);
            }
        }
        return str;
    }

    static String getAutoConnectDescription(Context context, WifiEntry wifiEntry) {
        String str = "";
        return (context == null || wifiEntry == null || !wifiEntry.canSetAutoJoinEnabled() || wifiEntry.isAutoJoinEnabled()) ? str : context.getString(R$string.auto_connect_disable);
    }

    static String getMeteredDescription(Context context, WifiEntry wifiEntry) {
        String str = "";
        if (!(context == null || wifiEntry == null)) {
            if (!wifiEntry.canSetMeteredChoice() && wifiEntry.getMeteredChoice() != 1) {
                return str;
            }
            if (wifiEntry.getMeteredChoice() == 1) {
                return context.getString(R$string.wifi_metered_label);
            }
            if (wifiEntry.getMeteredChoice() == 2) {
                return context.getString(R$string.wifi_unmetered_label);
            }
            if (wifiEntry.isMetered()) {
                str = context.getString(R$string.wifi_metered_label);
            }
        }
        return str;
    }

    static String getVerboseLoggingDescription(WifiEntry wifiEntry) {
        if (!BaseWifiTracker.isVerboseLoggingEnabled() || wifiEntry == null) {
            return "";
        }
        StringJoiner stringJoiner = new StringJoiner(" ");
        String wifiInfoDescription = wifiEntry.getWifiInfoDescription();
        if (!TextUtils.isEmpty(wifiInfoDescription)) {
            stringJoiner.add(wifiInfoDescription);
        }
        String scanResultDescription = wifiEntry.getScanResultDescription();
        if (!TextUtils.isEmpty(scanResultDescription)) {
            stringJoiner.add(scanResultDescription);
        }
        return stringJoiner.toString();
    }

    static String getCurrentNetworkCapabilitiesInformation(Context context, NetworkCapabilities networkCapabilities) {
        String str = "";
        if (!(context == null || networkCapabilities == null)) {
            if (networkCapabilities.hasCapability(17)) {
                return context.getString(context.getResources().getIdentifier("network_available_sign_in", "string", "android"));
            }
            if (networkCapabilities.hasCapability(24)) {
                return context.getString(R$string.wifi_limited_connection);
            }
            if (!networkCapabilities.hasCapability(16)) {
                if (networkCapabilities.isPrivateDnsBroken()) {
                    return context.getString(R$string.private_dns_broken);
                }
                return context.getString(R$string.wifi_connected_no_internet);
            }
        }
        return str;
    }

    static String getNetworkDetailedState(Context context, NetworkInfo networkInfo) {
        String str = "";
        if (!(context == null || networkInfo == null)) {
            DetailedState detailedState = networkInfo.getDetailedState();
            if (detailedState == null) {
                return str;
            }
            String[] stringArray = context.getResources().getStringArray(R$array.wifi_status);
            int ordinal = detailedState.ordinal();
            if (ordinal < stringArray.length) {
                str = stringArray[ordinal];
            }
        }
        return str;
    }
}
