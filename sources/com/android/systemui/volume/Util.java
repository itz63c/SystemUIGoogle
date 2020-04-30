package com.android.systemui.volume;

import android.view.View;

class Util extends com.android.settingslib.volume.Util {
    public static String logTag(Class<?> cls) {
        StringBuilder sb = new StringBuilder();
        sb.append("vol.");
        sb.append(cls.getSimpleName());
        String sb2 = sb.toString();
        return sb2.length() < 23 ? sb2 : sb2.substring(0, 23);
    }

    public static String ringerModeToString(int i) {
        if (i == 0) {
            return "RINGER_MODE_SILENT";
        }
        if (i == 1) {
            return "RINGER_MODE_VIBRATE";
        }
        if (i == 2) {
            return "RINGER_MODE_NORMAL";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("RINGER_MODE_UNKNOWN_");
        sb.append(i);
        return sb.toString();
    }

    public static final void setVisOrGone(View view, boolean z) {
        if (view != null) {
            int i = 0;
            if ((view.getVisibility() == 0) != z) {
                if (!z) {
                    i = 8;
                }
                view.setVisibility(i);
            }
        }
    }
}
