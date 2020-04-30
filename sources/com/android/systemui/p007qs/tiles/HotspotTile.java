package com.android.systemui.p007qs.tiles;

import android.content.Intent;
import android.util.Log;
import android.widget.Switch;
import androidx.appcompat.R$styleable;
import androidx.lifecycle.LifecycleOwner;
import com.android.systemui.C2010R$drawable;
import com.android.systemui.C2015R$plurals;
import com.android.systemui.C2017R$string;
import com.android.systemui.p007qs.QSHost;
import com.android.systemui.p007qs.tileimpl.QSTileImpl;
import com.android.systemui.p007qs.tileimpl.QSTileImpl.ResourceIcon;
import com.android.systemui.plugins.p006qs.QSTile.BooleanState;
import com.android.systemui.plugins.p006qs.QSTile.Icon;
import com.android.systemui.plugins.p006qs.QSTile.SlashState;
import com.android.systemui.statusbar.policy.DataSaverController;
import com.android.systemui.statusbar.policy.DataSaverController.Listener;
import com.android.systemui.statusbar.policy.HotspotController;
import com.android.systemui.statusbar.policy.HotspotController.Callback;

/* renamed from: com.android.systemui.qs.tiles.HotspotTile */
public class HotspotTile extends QSTileImpl<BooleanState> {
    private final HotspotAndDataSaverCallbacks mCallbacks;
    private final DataSaverController mDataSaverController;
    private final Icon mEnabledStatic = ResourceIcon.get(C2010R$drawable.ic_hotspot);
    private final HotspotController mHotspotController;
    private boolean mListening;

    /* renamed from: com.android.systemui.qs.tiles.HotspotTile$CallbackInfo */
    protected static final class CallbackInfo {
        boolean isDataSaverEnabled;
        boolean isHotspotEnabled;
        int numConnectedDevices;

        protected CallbackInfo() {
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("CallbackInfo[");
            sb.append("isHotspotEnabled=");
            sb.append(this.isHotspotEnabled);
            sb.append(",numConnectedDevices=");
            sb.append(this.numConnectedDevices);
            sb.append(",isDataSaverEnabled=");
            sb.append(this.isDataSaverEnabled);
            sb.append(']');
            return sb.toString();
        }
    }

    /* renamed from: com.android.systemui.qs.tiles.HotspotTile$HotspotAndDataSaverCallbacks */
    private final class HotspotAndDataSaverCallbacks implements Callback, Listener {
        CallbackInfo mCallbackInfo;

        private HotspotAndDataSaverCallbacks() {
            this.mCallbackInfo = new CallbackInfo();
        }

        public void onDataSaverChanged(boolean z) {
            CallbackInfo callbackInfo = this.mCallbackInfo;
            callbackInfo.isDataSaverEnabled = z;
            HotspotTile.this.refreshState(callbackInfo);
        }

        public void onHotspotChanged(boolean z, int i) {
            CallbackInfo callbackInfo = this.mCallbackInfo;
            callbackInfo.isHotspotEnabled = z;
            callbackInfo.numConnectedDevices = i;
            HotspotTile.this.refreshState(callbackInfo);
        }

        public void onHotspotAvailabilityChanged(boolean z) {
            if (!z) {
                Log.d(HotspotTile.this.TAG, "Tile removed. Hotspot no longer available");
                HotspotTile.this.mHost.removeTile(HotspotTile.this.getTileSpec());
            }
        }
    }

    public int getMetricsCategory() {
        return R$styleable.AppCompatTheme_windowFixedWidthMajor;
    }

    public HotspotTile(QSHost qSHost, HotspotController hotspotController, DataSaverController dataSaverController) {
        super(qSHost);
        HotspotAndDataSaverCallbacks hotspotAndDataSaverCallbacks = new HotspotAndDataSaverCallbacks();
        this.mCallbacks = hotspotAndDataSaverCallbacks;
        this.mHotspotController = hotspotController;
        this.mDataSaverController = dataSaverController;
        hotspotController.observe((LifecycleOwner) this, hotspotAndDataSaverCallbacks);
        this.mDataSaverController.observe((LifecycleOwner) this, this.mCallbacks);
    }

    public boolean isAvailable() {
        return this.mHotspotController.isHotspotSupported();
    }

    /* access modifiers changed from: protected */
    public void handleDestroy() {
        super.handleDestroy();
    }

    public void handleSetListening(boolean z) {
        super.handleSetListening(z);
        if (this.mListening != z) {
            this.mListening = z;
            if (z) {
                refreshState();
            }
        }
    }

    public Intent getLongClickIntent() {
        return new Intent("android.settings.TETHER_SETTINGS");
    }

    public BooleanState newTileState() {
        return new BooleanState();
    }

    /* access modifiers changed from: protected */
    public void handleClick() {
        Object obj;
        boolean z = ((BooleanState) this.mState).value;
        if (z || !this.mDataSaverController.isDataSaverEnabled()) {
            if (z) {
                obj = null;
            } else {
                obj = QSTileImpl.ARG_SHOW_TRANSIENT_ENABLING;
            }
            refreshState(obj);
            this.mHotspotController.setHotspotEnabled(!z);
        }
    }

    public CharSequence getTileLabel() {
        return this.mContext.getString(C2017R$string.quick_settings_hotspot_label);
    }

    /* access modifiers changed from: protected */
    public void handleUpdateState(BooleanState booleanState, Object obj) {
        boolean z;
        int i;
        int i2 = 1;
        boolean z2 = obj == QSTileImpl.ARG_SHOW_TRANSIENT_ENABLING;
        if (booleanState.slash == null) {
            booleanState.slash = new SlashState();
        }
        boolean z3 = z2 || this.mHotspotController.isHotspotTransient();
        checkIfRestrictionEnforcedByAdminOnly(booleanState, "no_config_tethering");
        if (obj instanceof CallbackInfo) {
            CallbackInfo callbackInfo = (CallbackInfo) obj;
            booleanState.value = z2 || callbackInfo.isHotspotEnabled;
            i = callbackInfo.numConnectedDevices;
            z = callbackInfo.isDataSaverEnabled;
        } else {
            booleanState.value = z2 || this.mHotspotController.isHotspotEnabled();
            i = this.mHotspotController.getNumConnectedDevices();
            z = this.mDataSaverController.isDataSaverEnabled();
        }
        booleanState.icon = this.mEnabledStatic;
        booleanState.label = this.mContext.getString(C2017R$string.quick_settings_hotspot_label);
        booleanState.isTransient = z3;
        booleanState.slash.isSlashed = !booleanState.value && !z3;
        if (booleanState.isTransient) {
            booleanState.icon = ResourceIcon.get(17302437);
        }
        booleanState.expandedAccessibilityClassName = Switch.class.getName();
        booleanState.contentDescription = booleanState.label;
        boolean z4 = booleanState.value || booleanState.isTransient;
        if (z) {
            booleanState.state = 0;
        } else {
            if (z4) {
                i2 = 2;
            }
            booleanState.state = i2;
        }
        String secondaryLabel = getSecondaryLabel(z4, z3, z, i);
        booleanState.secondaryLabel = secondaryLabel;
        booleanState.stateDescription = secondaryLabel;
    }

    private String getSecondaryLabel(boolean z, boolean z2, boolean z3, int i) {
        if (z2) {
            return this.mContext.getString(C2017R$string.quick_settings_hotspot_secondary_label_transient);
        }
        if (z3) {
            return this.mContext.getString(C2017R$string.quick_settings_hotspot_secondary_label_data_saver_enabled);
        }
        if (i <= 0 || !z) {
            return null;
        }
        return this.mContext.getResources().getQuantityString(C2015R$plurals.quick_settings_hotspot_secondary_label_num_devices, i, new Object[]{Integer.valueOf(i)});
    }

    /* access modifiers changed from: protected */
    public String composeChangeAnnouncement() {
        if (((BooleanState) this.mState).value) {
            return this.mContext.getString(C2017R$string.accessibility_quick_settings_hotspot_changed_on);
        }
        return this.mContext.getString(C2017R$string.accessibility_quick_settings_hotspot_changed_off);
    }
}
