package com.android.settingslib.wifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.NetworkSelectionStatus;
import android.net.wifi.WifiInfo;
import android.os.SystemClock;
import com.android.settingslib.R$string;
import java.util.Map;

public class WifiUtils {
    public static String buildLoggingSummary(AccessPoint accessPoint, WifiConfiguration wifiConfiguration) {
        StringBuilder sb = new StringBuilder();
        WifiInfo info = accessPoint.getInfo();
        if (accessPoint.isActive() && info != null) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(" f=");
            sb2.append(Integer.toString(info.getFrequency()));
            sb.append(sb2.toString());
        }
        StringBuilder sb3 = new StringBuilder();
        String str = " ";
        sb3.append(str);
        sb3.append(getVisibilityStatus(accessPoint));
        sb.append(sb3.toString());
        if (!(wifiConfiguration == null || wifiConfiguration.getNetworkSelectionStatus().getNetworkSelectionStatus() == 0)) {
            StringBuilder sb4 = new StringBuilder();
            sb4.append(" (");
            sb4.append(wifiConfiguration.getNetworkSelectionStatus().getNetworkStatusString());
            sb.append(sb4.toString());
            if (wifiConfiguration.getNetworkSelectionStatus().getDisableTime() > 0) {
                long currentTimeMillis = (System.currentTimeMillis() - wifiConfiguration.getNetworkSelectionStatus().getDisableTime()) / 1000;
                long j = currentTimeMillis % 60;
                long j2 = (currentTimeMillis / 60) % 60;
                long j3 = (j2 / 60) % 60;
                sb.append(", ");
                if (j3 > 0) {
                    StringBuilder sb5 = new StringBuilder();
                    sb5.append(Long.toString(j3));
                    sb5.append("h ");
                    sb.append(sb5.toString());
                }
                StringBuilder sb6 = new StringBuilder();
                sb6.append(Long.toString(j2));
                sb6.append("m ");
                sb.append(sb6.toString());
                StringBuilder sb7 = new StringBuilder();
                sb7.append(Long.toString(j));
                sb7.append("s ");
                sb.append(sb7.toString());
            }
            sb.append(")");
        }
        if (wifiConfiguration != null) {
            NetworkSelectionStatus networkSelectionStatus = wifiConfiguration.getNetworkSelectionStatus();
            for (int i = 0; i <= NetworkSelectionStatus.getMaxNetworkSelectionDisableReason(); i++) {
                if (networkSelectionStatus.getDisableReasonCounter(i) != 0) {
                    sb.append(str);
                    sb.append(NetworkSelectionStatus.getNetworkSelectionDisableReasonString(i));
                    sb.append("=");
                    sb.append(networkSelectionStatus.getDisableReasonCounter(i));
                }
            }
        }
        return sb.toString();
    }

    static String getVisibilityStatus(AccessPoint accessPoint) {
        String str;
        WifiInfo info = accessPoint.getInfo();
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        StringBuilder sb3 = new StringBuilder();
        int i = 0;
        if (!accessPoint.isActive() || info == null) {
            str = null;
        } else {
            str = info.getBSSID();
            String str2 = " ";
            if (str != null) {
                sb.append(str2);
                sb.append(str);
            }
            sb.append(" standard = ");
            sb.append(info.getWifiStandard());
            sb.append(" rssi=");
            sb.append(info.getRssi());
            sb.append(str2);
            sb.append(" score=");
            sb.append(info.getScore());
            if (accessPoint.getSpeed() != 0) {
                sb.append(" speed=");
                sb.append(accessPoint.getSpeedLabel());
            }
            sb.append(String.format(" tx=%.1f,", new Object[]{Double.valueOf(info.getSuccessfulTxPacketsPerSecond())}));
            sb.append(String.format("%.1f,", new Object[]{Double.valueOf(info.getRetriedTxPacketsPerSecond())}));
            sb.append(String.format("%.1f ", new Object[]{Double.valueOf(info.getLostTxPacketsPerSecond())}));
            sb.append(String.format("rx=%.1f", new Object[]{Double.valueOf(info.getSuccessfulRxPacketsPerSecond())}));
        }
        long elapsedRealtime = SystemClock.elapsedRealtime();
        int i2 = -127;
        int i3 = -127;
        int i4 = 0;
        for (ScanResult scanResult : accessPoint.getScanResults()) {
            if (scanResult != null) {
                int i5 = scanResult.frequency;
                if (i5 < 4900 || i5 > 5900) {
                    int i6 = scanResult.frequency;
                    if (i6 >= 2400 && i6 <= 2500) {
                        i++;
                        int i7 = scanResult.level;
                        if (i7 > i2) {
                            i2 = i7;
                        }
                        if (i <= 4) {
                            sb2.append(verboseScanResultSummary(accessPoint, scanResult, str, elapsedRealtime));
                        }
                    }
                } else {
                    i4++;
                    int i8 = scanResult.level;
                    if (i8 > i3) {
                        i3 = i8;
                    }
                    if (i4 <= 4) {
                        sb3.append(verboseScanResultSummary(accessPoint, scanResult, str, elapsedRealtime));
                    }
                }
            }
        }
        sb.append(" [");
        String str3 = ",";
        String str4 = "max=";
        String str5 = ")";
        String str6 = "(";
        if (i > 0) {
            sb.append(str6);
            sb.append(i);
            sb.append(str5);
            if (i > 4) {
                sb.append(str4);
                sb.append(i2);
                sb.append(str3);
            }
            sb.append(sb2.toString());
        }
        sb.append(";");
        if (i4 > 0) {
            sb.append(str6);
            sb.append(i4);
            sb.append(str5);
            if (i4 > 4) {
                sb.append(str4);
                sb.append(i3);
                sb.append(str3);
            }
            sb.append(sb3.toString());
        }
        sb.append("]");
        return sb.toString();
    }

    static String verboseScanResultSummary(AccessPoint accessPoint, ScanResult scanResult, String str, long j) {
        StringBuilder sb = new StringBuilder();
        sb.append(" \n{");
        sb.append(scanResult.BSSID);
        if (scanResult.BSSID.equals(str)) {
            sb.append("*");
        }
        sb.append("=");
        sb.append(scanResult.frequency);
        String str2 = ",";
        sb.append(str2);
        sb.append(scanResult.level);
        int specificApSpeed = getSpecificApSpeed(scanResult, accessPoint.getScoredNetworkCache());
        if (specificApSpeed != 0) {
            sb.append(str2);
            sb.append(accessPoint.getSpeedLabel(specificApSpeed));
        }
        int i = ((int) (j - (scanResult.timestamp / 1000))) / 1000;
        sb.append(str2);
        sb.append(i);
        sb.append("s");
        sb.append("}");
        return sb.toString();
    }

    private static int getSpecificApSpeed(ScanResult scanResult, Map<String, TimestampedScoredNetwork> map) {
        TimestampedScoredNetwork timestampedScoredNetwork = (TimestampedScoredNetwork) map.get(scanResult.BSSID);
        if (timestampedScoredNetwork == null) {
            return 0;
        }
        return timestampedScoredNetwork.getScore().calculateBadge(scanResult.level);
    }

    public static String getMeteredLabel(Context context, WifiConfiguration wifiConfiguration) {
        if (wifiConfiguration.meteredOverride == 1 || (wifiConfiguration.meteredHint && !isMeteredOverridden(wifiConfiguration))) {
            return context.getString(R$string.wifi_metered_label);
        }
        return context.getString(R$string.wifi_unmetered_label);
    }

    public static boolean isMeteredOverridden(WifiConfiguration wifiConfiguration) {
        return wifiConfiguration.meteredOverride != 0;
    }
}
