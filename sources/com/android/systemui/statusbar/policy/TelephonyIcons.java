package com.android.systemui.statusbar.policy;

import com.android.systemui.C2010R$drawable;
import com.android.systemui.C2017R$string;
import java.util.HashMap;
import java.util.Map;

class TelephonyIcons {
    static final MobileIconGroup CARRIER_NETWORK_CHANGE;
    static final MobileIconGroup DATA_DISABLED;

    /* renamed from: E */
    static final MobileIconGroup f80E;
    static final int FLIGHT_MODE_ICON = C2010R$drawable.stat_sys_airplane_mode;
    static final MobileIconGroup FOUR_G;
    static final MobileIconGroup FOUR_G_PLUS;

    /* renamed from: G */
    static final MobileIconGroup f81G;

    /* renamed from: H */
    static final MobileIconGroup f82H;
    static final MobileIconGroup H_PLUS;
    static final int ICON_1X = C2010R$drawable.ic_1x_mobiledata;
    static final int ICON_3G = C2010R$drawable.ic_3g_mobiledata;
    static final int ICON_4G = C2010R$drawable.ic_4g_mobiledata;
    static final int ICON_4G_PLUS = C2010R$drawable.ic_4g_plus_mobiledata;
    static final int ICON_5G = C2010R$drawable.ic_5g_mobiledata;
    static final int ICON_5G_E = C2010R$drawable.ic_5g_e_mobiledata;
    static final int ICON_5G_PLUS = C2010R$drawable.ic_5g_plus_mobiledata;
    static final int ICON_E = C2010R$drawable.ic_e_mobiledata;
    static final int ICON_G = C2010R$drawable.ic_g_mobiledata;
    static final int ICON_H = C2010R$drawable.ic_h_mobiledata;
    static final int ICON_H_PLUS = C2010R$drawable.ic_h_plus_mobiledata;
    static final int ICON_LTE = C2010R$drawable.ic_lte_mobiledata;
    static final int ICON_LTE_PLUS = C2010R$drawable.ic_lte_plus_mobiledata;
    static final Map<String, MobileIconGroup> ICON_NAME_TO_ICON;
    static final MobileIconGroup LTE;
    static final MobileIconGroup LTE_CA_5G_E;
    static final MobileIconGroup LTE_PLUS;
    static final MobileIconGroup NOT_DEFAULT_DATA;
    static final MobileIconGroup NR_5G;
    static final MobileIconGroup NR_5G_PLUS;
    static final MobileIconGroup ONE_X;
    static final MobileIconGroup THREE_G;
    static final MobileIconGroup UNKNOWN;
    static final MobileIconGroup WFC;

    static {
        int[] iArr = AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH;
        MobileIconGroup mobileIconGroup = new MobileIconGroup("CARRIER_NETWORK_CHANGE", null, null, iArr, 0, 0, 0, 0, iArr[0], C2017R$string.carrier_network_change_mode, 0, false);
        CARRIER_NETWORK_CHANGE = mobileIconGroup;
        int[] iArr2 = AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH;
        MobileIconGroup mobileIconGroup2 = new MobileIconGroup("3G", null, null, iArr2, 0, 0, 0, 0, iArr2[0], C2017R$string.data_connection_3g, ICON_3G, true);
        THREE_G = mobileIconGroup2;
        int[] iArr3 = AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH;
        MobileIconGroup mobileIconGroup3 = new MobileIconGroup("WFC", null, null, iArr3, 0, 0, 0, 0, iArr3[0], 0, 0, false);
        WFC = mobileIconGroup3;
        int[] iArr4 = AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH;
        MobileIconGroup mobileIconGroup4 = new MobileIconGroup("Unknown", null, null, iArr4, 0, 0, 0, 0, iArr4[0], 0, 0, false);
        UNKNOWN = mobileIconGroup4;
        int[] iArr5 = AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH;
        MobileIconGroup mobileIconGroup5 = new MobileIconGroup("E", null, null, iArr5, 0, 0, 0, 0, iArr5[0], C2017R$string.data_connection_edge, ICON_E, false);
        f80E = mobileIconGroup5;
        int[] iArr6 = AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH;
        MobileIconGroup mobileIconGroup6 = new MobileIconGroup("1X", null, null, iArr6, 0, 0, 0, 0, iArr6[0], C2017R$string.data_connection_cdma, ICON_1X, true);
        ONE_X = mobileIconGroup6;
        int[] iArr7 = AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH;
        MobileIconGroup mobileIconGroup7 = new MobileIconGroup("G", null, null, iArr7, 0, 0, 0, 0, iArr7[0], C2017R$string.data_connection_gprs, ICON_G, false);
        f81G = mobileIconGroup7;
        int[] iArr8 = AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH;
        MobileIconGroup mobileIconGroup8 = new MobileIconGroup("H", null, null, iArr8, 0, 0, 0, 0, iArr8[0], C2017R$string.data_connection_3_5g, ICON_H, false);
        f82H = mobileIconGroup8;
        int[] iArr9 = AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH;
        MobileIconGroup mobileIconGroup9 = new MobileIconGroup("H+", null, null, iArr9, 0, 0, 0, 0, iArr9[0], C2017R$string.data_connection_3_5g_plus, ICON_H_PLUS, false);
        H_PLUS = mobileIconGroup9;
        int[] iArr10 = AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH;
        MobileIconGroup mobileIconGroup10 = new MobileIconGroup("4G", null, null, iArr10, 0, 0, 0, 0, iArr10[0], C2017R$string.data_connection_4g, ICON_4G, true);
        FOUR_G = mobileIconGroup10;
        int[] iArr11 = AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH;
        MobileIconGroup mobileIconGroup11 = new MobileIconGroup("4G+", null, null, iArr11, 0, 0, 0, 0, iArr11[0], C2017R$string.data_connection_4g_plus, ICON_4G_PLUS, true);
        FOUR_G_PLUS = mobileIconGroup11;
        int[] iArr12 = AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH;
        MobileIconGroup mobileIconGroup12 = new MobileIconGroup("LTE", null, null, iArr12, 0, 0, 0, 0, iArr12[0], C2017R$string.data_connection_lte, ICON_LTE, true);
        LTE = mobileIconGroup12;
        int[] iArr13 = AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH;
        MobileIconGroup mobileIconGroup13 = new MobileIconGroup("LTE+", null, null, iArr13, 0, 0, 0, 0, iArr13[0], C2017R$string.data_connection_lte_plus, ICON_LTE_PLUS, true);
        LTE_PLUS = mobileIconGroup13;
        int[] iArr14 = AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH;
        MobileIconGroup mobileIconGroup14 = new MobileIconGroup("5Ge", null, null, iArr14, 0, 0, 0, 0, iArr14[0], C2017R$string.data_connection_5ge_html, ICON_5G_E, true);
        LTE_CA_5G_E = mobileIconGroup14;
        int[] iArr15 = AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH;
        MobileIconGroup mobileIconGroup15 = new MobileIconGroup("5G", null, null, iArr15, 0, 0, 0, 0, iArr15[0], C2017R$string.data_connection_5g, ICON_5G, true);
        NR_5G = mobileIconGroup15;
        int[] iArr16 = AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH;
        MobileIconGroup mobileIconGroup16 = new MobileIconGroup("5G_PLUS", null, null, iArr16, 0, 0, 0, 0, iArr16[0], C2017R$string.data_connection_5g_plus, ICON_5G_PLUS, true);
        NR_5G_PLUS = mobileIconGroup16;
        int[] iArr17 = AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH;
        MobileIconGroup mobileIconGroup17 = new MobileIconGroup("DataDisabled", null, null, iArr17, 0, 0, 0, 0, iArr17[0], C2017R$string.cell_data_off_content_description, 0, false);
        DATA_DISABLED = mobileIconGroup17;
        int[] iArr18 = AccessibilityContentDescriptions.PHONE_SIGNAL_STRENGTH;
        MobileIconGroup mobileIconGroup18 = new MobileIconGroup("NotDefaultData", null, null, iArr18, 0, 0, 0, 0, iArr18[0], C2017R$string.not_default_data_content_description, 0, false);
        NOT_DEFAULT_DATA = mobileIconGroup18;
        HashMap hashMap = new HashMap();
        ICON_NAME_TO_ICON = hashMap;
        hashMap.put("carrier_network_change", CARRIER_NETWORK_CHANGE);
        ICON_NAME_TO_ICON.put("3g", THREE_G);
        ICON_NAME_TO_ICON.put("wfc", WFC);
        ICON_NAME_TO_ICON.put("unknown", UNKNOWN);
        ICON_NAME_TO_ICON.put("e", f80E);
        ICON_NAME_TO_ICON.put("1x", ONE_X);
        ICON_NAME_TO_ICON.put("g", f81G);
        ICON_NAME_TO_ICON.put("h", f82H);
        ICON_NAME_TO_ICON.put("h+", H_PLUS);
        ICON_NAME_TO_ICON.put("4g", FOUR_G);
        ICON_NAME_TO_ICON.put("4g+", FOUR_G_PLUS);
        ICON_NAME_TO_ICON.put("5ge", LTE_CA_5G_E);
        ICON_NAME_TO_ICON.put("lte", LTE);
        ICON_NAME_TO_ICON.put("lte+", LTE_PLUS);
        ICON_NAME_TO_ICON.put("5g", NR_5G);
        ICON_NAME_TO_ICON.put("5g_plus", NR_5G_PLUS);
        ICON_NAME_TO_ICON.put("datadisable", DATA_DISABLED);
        ICON_NAME_TO_ICON.put("notdefaultdata", NOT_DEFAULT_DATA);
    }
}
