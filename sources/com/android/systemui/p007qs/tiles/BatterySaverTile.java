package com.android.systemui.p007qs.tiles;

import android.content.Intent;
import android.widget.Switch;
import com.android.systemui.C2017R$string;
import com.android.systemui.p007qs.QSHost;
import com.android.systemui.p007qs.SecureSetting;
import com.android.systemui.p007qs.tileimpl.QSTileImpl;
import com.android.systemui.p007qs.tileimpl.QSTileImpl.ResourceIcon;
import com.android.systemui.plugins.p006qs.QSTile.BooleanState;
import com.android.systemui.plugins.p006qs.QSTile.Icon;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.statusbar.policy.BatteryController.BatteryStateChangeCallback;

/* renamed from: com.android.systemui.qs.tiles.BatterySaverTile */
public class BatterySaverTile extends QSTileImpl<BooleanState> implements BatteryStateChangeCallback {
    private final BatteryController mBatteryController;
    private Icon mIcon = ResourceIcon.get(17302793);
    private boolean mPluggedIn;
    private boolean mPowerSave;
    private final SecureSetting mSetting;

    public int getMetricsCategory() {
        return 261;
    }

    public BatterySaverTile(QSHost qSHost, BatteryController batteryController) {
        super(qSHost);
        this.mBatteryController = batteryController;
        batteryController.observe(getLifecycle(), this);
        this.mSetting = new SecureSetting(this.mContext, this.mHandler, "low_power_warning_acknowledged") {
            /* access modifiers changed from: protected */
            public void handleValueChanged(int i, boolean z) {
                BatterySaverTile.this.handleRefreshState(null);
            }
        };
    }

    public BooleanState newTileState() {
        return new BooleanState();
    }

    /* access modifiers changed from: protected */
    public void handleDestroy() {
        super.handleDestroy();
        this.mSetting.setListening(false);
    }

    public void handleSetListening(boolean z) {
        super.handleSetListening(z);
        this.mSetting.setListening(z);
    }

    public Intent getLongClickIntent() {
        return new Intent("android.intent.action.POWER_USAGE_SUMMARY");
    }

    /* access modifiers changed from: protected */
    public void handleClick() {
        if (((BooleanState) getState()).state != 0) {
            this.mBatteryController.setPowerSaveMode(!this.mPowerSave);
        }
    }

    public CharSequence getTileLabel() {
        return this.mContext.getString(C2017R$string.battery_detail_switch_title);
    }

    /* access modifiers changed from: protected */
    public void handleUpdateState(BooleanState booleanState, Object obj) {
        boolean z = true;
        int i = this.mPluggedIn ? 0 : this.mPowerSave ? 2 : 1;
        booleanState.state = i;
        booleanState.icon = this.mIcon;
        String string = this.mContext.getString(C2017R$string.battery_detail_switch_title);
        booleanState.label = string;
        booleanState.contentDescription = string;
        booleanState.value = this.mPowerSave;
        booleanState.expandedAccessibilityClassName = Switch.class.getName();
        if (this.mSetting.getValue() != 0) {
            z = false;
        }
        booleanState.showRippleEffect = z;
    }

    public void onBatteryLevelChanged(int i, boolean z, boolean z2) {
        this.mPluggedIn = z;
        refreshState(Integer.valueOf(i));
    }

    public void onPowerSaveChanged(boolean z) {
        this.mPowerSave = z;
        refreshState(null);
    }
}
