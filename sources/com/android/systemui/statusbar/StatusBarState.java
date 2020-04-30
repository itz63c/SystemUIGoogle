package com.android.systemui.statusbar;

public class StatusBarState {
    public static String toShortString(int i) {
        if (i == 0) {
            return "SHD";
        }
        if (i == 1) {
            return "KGRD";
        }
        if (i == 2) {
            return "SHD_LCK";
        }
        if (i == 3) {
            return "FS_USRSW";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("bad_value_");
        sb.append(i);
        return sb.toString();
    }
}
