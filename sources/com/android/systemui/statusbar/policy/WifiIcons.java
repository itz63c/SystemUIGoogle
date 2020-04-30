package com.android.systemui.statusbar.policy;

import com.android.systemui.C2010R$drawable;

public class WifiIcons {
    public static final int[][] QS_WIFI_SIGNAL_STRENGTH;
    static final int[] WIFI_FULL_ICONS = {17302861, 17302862, 17302863, 17302864, 17302865};
    static final int WIFI_LEVEL_COUNT;
    private static final int[] WIFI_NO_INTERNET_ICONS;
    static final int[][] WIFI_SIGNAL_STRENGTH;

    static {
        int[] iArr = {C2010R$drawable.ic_qs_wifi_0, C2010R$drawable.ic_qs_wifi_1, C2010R$drawable.ic_qs_wifi_2, C2010R$drawable.ic_qs_wifi_3, C2010R$drawable.ic_qs_wifi_4};
        WIFI_NO_INTERNET_ICONS = iArr;
        int[][] iArr2 = {iArr, WIFI_FULL_ICONS};
        QS_WIFI_SIGNAL_STRENGTH = iArr2;
        WIFI_SIGNAL_STRENGTH = iArr2;
        WIFI_LEVEL_COUNT = iArr2[0].length;
    }
}
