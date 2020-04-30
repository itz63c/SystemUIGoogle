package com.android.systemui.p007qs.tiles;

import android.app.UiModeManager;
import android.content.Intent;
import android.content.res.Resources;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.widget.Switch;
import com.android.systemui.C2017R$string;
import com.android.systemui.p007qs.QSHost;
import com.android.systemui.p007qs.tileimpl.QSTileImpl;
import com.android.systemui.p007qs.tileimpl.QSTileImpl.ResourceIcon;
import com.android.systemui.plugins.p006qs.QSTile.BooleanState;
import com.android.systemui.plugins.p006qs.QSTile.Icon;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.statusbar.policy.BatteryController.BatteryStateChangeCallback;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/* renamed from: com.android.systemui.qs.tiles.UiModeNightTile */
public class UiModeNightTile extends QSTileImpl<BooleanState> implements ConfigurationListener, BatteryStateChangeCallback {
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
    private final BatteryController mBatteryController;
    private final Icon mIcon = ResourceIcon.get(17302798);
    private final UiModeManager mUiModeManager;

    public int getMetricsCategory() {
        return 1706;
    }

    public UiModeNightTile(QSHost qSHost, ConfigurationController configurationController, BatteryController batteryController) {
        super(qSHost);
        this.mBatteryController = batteryController;
        this.mUiModeManager = (UiModeManager) this.mContext.getSystemService(UiModeManager.class);
        configurationController.observe(getLifecycle(), this);
        batteryController.observe(getLifecycle(), this);
    }

    public void onUiModeChanged() {
        refreshState();
    }

    public void onPowerSaveChanged(boolean z) {
        refreshState();
    }

    public BooleanState newTileState() {
        return new BooleanState();
    }

    /* access modifiers changed from: protected */
    public void handleClick() {
        if (((BooleanState) getState()).state != 0) {
            boolean z = !((BooleanState) this.mState).value;
            this.mUiModeManager.setNightModeActivated(z);
            refreshState(Boolean.valueOf(z));
        }
    }

    /* access modifiers changed from: protected */
    public void handleUpdateState(BooleanState booleanState, Object obj) {
        CharSequence charSequence;
        LocalTime localTime;
        int i;
        int i2;
        int nightMode = this.mUiModeManager.getNightMode();
        boolean isPowerSave = this.mBatteryController.isPowerSave();
        int i3 = 1;
        boolean z = (this.mContext.getResources().getConfiguration().uiMode & 48) == 32;
        if (isPowerSave) {
            booleanState.secondaryLabel = this.mContext.getResources().getString(C2017R$string.quick_settings_dark_mode_secondary_label_battery_saver);
        } else if (nightMode == 0) {
            Resources resources = this.mContext.getResources();
            if (z) {
                i2 = C2017R$string.quick_settings_dark_mode_secondary_label_until_sunrise;
            } else {
                i2 = C2017R$string.quick_settings_dark_mode_secondary_label_on_at_sunset;
            }
            booleanState.secondaryLabel = resources.getString(i2);
        } else if (nightMode == 3) {
            boolean is24HourFormat = DateFormat.is24HourFormat(this.mContext);
            if (z) {
                localTime = this.mUiModeManager.getCustomNightModeEnd();
            } else {
                localTime = this.mUiModeManager.getCustomNightModeStart();
            }
            Resources resources2 = this.mContext.getResources();
            if (z) {
                i = C2017R$string.quick_settings_dark_mode_secondary_label_until;
            } else {
                i = C2017R$string.quick_settings_dark_mode_secondary_label_on_at;
            }
            Object[] objArr = new Object[1];
            objArr[0] = is24HourFormat ? localTime.toString() : formatter.format(localTime);
            booleanState.secondaryLabel = resources2.getString(i, objArr);
        } else {
            booleanState.secondaryLabel = null;
        }
        booleanState.value = z;
        booleanState.label = this.mContext.getString(C2017R$string.quick_settings_ui_mode_night_label);
        booleanState.icon = this.mIcon;
        if (TextUtils.isEmpty(booleanState.secondaryLabel)) {
            charSequence = booleanState.label;
        } else {
            charSequence = TextUtils.concat(new CharSequence[]{booleanState.label, ", ", booleanState.secondaryLabel});
        }
        booleanState.contentDescription = charSequence;
        if (isPowerSave) {
            booleanState.state = 0;
        } else {
            if (booleanState.value) {
                i3 = 2;
            }
            booleanState.state = i3;
        }
        booleanState.showRippleEffect = false;
        booleanState.expandedAccessibilityClassName = Switch.class.getName();
    }

    public Intent getLongClickIntent() {
        return new Intent("android.settings.DARK_THEME_SETTINGS");
    }

    public CharSequence getTileLabel() {
        return ((BooleanState) getState()).label;
    }
}
