package com.google.android.systemui.p012qs.tiles;

import android.content.Intent;
import android.widget.Switch;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.C2010R$drawable;
import com.android.systemui.C2017R$string;
import com.android.systemui.p007qs.QSHost;
import com.android.systemui.p007qs.tileimpl.QSTileImpl;
import com.android.systemui.p007qs.tileimpl.QSTileImpl.ResourceIcon;
import com.android.systemui.plugins.p006qs.QSTile.BooleanState;
import com.android.systemui.plugins.p006qs.QSTile.Icon;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.statusbar.policy.BatteryController.BatteryStateChangeCallback;

/* renamed from: com.google.android.systemui.qs.tiles.BatteryShareTile */
public class BatteryShareTile extends QSTileImpl<BooleanState> implements BatteryStateChangeCallback {
    @VisibleForTesting
    static final String BATTERY_SHARE_SETTINGS = "android.settings.BATTERY_SHARE_SETTINGS";
    @VisibleForTesting
    private BatteryController mBatteryController;
    private boolean mBatteryShareOn = false;
    private Icon mIcon = ResourceIcon.get(C2010R$drawable.ic_qs_battery_share);

    public int getMetricsCategory() {
        return 0;
    }

    public void onBatteryLevelChanged(int i, boolean z, boolean z2) {
    }

    public void onPowerSaveChanged(boolean z) {
    }

    public BatteryShareTile(QSHost qSHost, BatteryController batteryController) {
        super(qSHost);
        this.mBatteryController = batteryController;
        batteryController.observe(getLifecycle(), this);
    }

    public BooleanState newTileState() {
        return new BooleanState();
    }

    /* access modifiers changed from: protected */
    public void handleDestroy() {
        super.handleDestroy();
    }

    public boolean isAvailable() {
        return this.mBatteryController.isReverseSupported();
    }

    public Intent getLongClickIntent() {
        return new Intent(BATTERY_SHARE_SETTINGS);
    }

    /* access modifiers changed from: protected */
    public void handleClick() {
        boolean z = !this.mBatteryShareOn;
        this.mBatteryShareOn = z;
        this.mBatteryController.setReverseState(z);
    }

    public CharSequence getTileLabel() {
        return this.mContext.getString(C2017R$string.battery_share_title);
    }

    /* access modifiers changed from: protected */
    public void handleUpdateState(BooleanState booleanState, Object obj) {
        boolean z = this.mBatteryShareOn;
        booleanState.value = z;
        booleanState.state = z ? 2 : 1;
        booleanState.icon = this.mIcon;
        CharSequence tileLabel = getTileLabel();
        booleanState.label = tileLabel;
        booleanState.contentDescription = tileLabel;
        booleanState.expandedAccessibilityClassName = Switch.class.getName();
    }

    public void onReverseChanged(boolean z, int i, String str) {
        this.mBatteryShareOn = z;
        refreshState(null);
    }
}
