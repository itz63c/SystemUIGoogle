package com.android.systemui.p007qs.tiles;

import android.provider.DeviceConfig;

/* renamed from: com.android.systemui.qs.tiles.QSSettingsControllerKt */
/* compiled from: QSSettingsController.kt */
public final class QSSettingsControllerKt {
    public static final QSSettingsPanel getQSSettingsPanelOption() {
        int i = DeviceConfig.getInt("systemui", "qs_use_settings_panels", 0);
        if (i == 1) {
            return QSSettingsPanel.OPEN_LONG_PRESS;
        }
        if (i == 2) {
            return QSSettingsPanel.OPEN_CLICK;
        }
        if (i != 3) {
            return QSSettingsPanel.DEFAULT;
        }
        return QSSettingsPanel.USE_DETAIL;
    }
}
