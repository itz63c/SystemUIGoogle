package com.android.systemui.p007qs.tiles;

import android.content.Intent;
import android.widget.Switch;
import com.android.systemui.C2010R$drawable;
import com.android.systemui.C2017R$string;
import com.android.systemui.p007qs.QSHost;
import com.android.systemui.p007qs.tileimpl.QSTileImpl;
import com.android.systemui.p007qs.tileimpl.QSTileImpl.ResourceIcon;
import com.android.systemui.plugins.p006qs.QSTile.BooleanState;
import com.android.systemui.plugins.p006qs.QSTile.Icon;
import com.android.systemui.plugins.p006qs.QSTile.SlashState;
import com.android.systemui.statusbar.phone.ManagedProfileController;
import com.android.systemui.statusbar.phone.ManagedProfileController.Callback;

/* renamed from: com.android.systemui.qs.tiles.WorkModeTile */
public class WorkModeTile extends QSTileImpl<BooleanState> implements Callback {
    private final Icon mIcon = ResourceIcon.get(C2010R$drawable.stat_sys_managed_profile_status);
    private final ManagedProfileController mProfileController;

    public int getMetricsCategory() {
        return 257;
    }

    public WorkModeTile(QSHost qSHost, ManagedProfileController managedProfileController) {
        super(qSHost);
        this.mProfileController = managedProfileController;
        managedProfileController.observe(getLifecycle(), this);
    }

    public BooleanState newTileState() {
        return new BooleanState();
    }

    public Intent getLongClickIntent() {
        return new Intent("android.settings.MANAGED_PROFILE_SETTINGS");
    }

    public void handleClick() {
        this.mProfileController.setWorkModeEnabled(!((BooleanState) this.mState).value);
    }

    public boolean isAvailable() {
        return this.mProfileController.hasActiveProfile();
    }

    public void onManagedProfileChanged() {
        refreshState(Boolean.valueOf(this.mProfileController.isWorkModeEnabled()));
    }

    public void onManagedProfileRemoved() {
        this.mHost.removeTile(getTileSpec());
        this.mHost.unmarkTileAsAutoAdded(getTileSpec());
    }

    public CharSequence getTileLabel() {
        return this.mContext.getString(C2017R$string.quick_settings_work_mode_label);
    }

    /* access modifiers changed from: protected */
    public void handleUpdateState(BooleanState booleanState, Object obj) {
        if (!isAvailable()) {
            onManagedProfileRemoved();
        }
        if (booleanState.slash == null) {
            booleanState.slash = new SlashState();
        }
        if (obj instanceof Boolean) {
            booleanState.value = ((Boolean) obj).booleanValue();
        } else {
            booleanState.value = this.mProfileController.isWorkModeEnabled();
        }
        booleanState.icon = this.mIcon;
        int i = 1;
        if (booleanState.value) {
            booleanState.slash.isSlashed = false;
        } else {
            booleanState.slash.isSlashed = true;
        }
        String string = this.mContext.getString(C2017R$string.quick_settings_work_mode_label);
        booleanState.label = string;
        booleanState.contentDescription = string;
        booleanState.expandedAccessibilityClassName = Switch.class.getName();
        if (booleanState.value) {
            i = 2;
        }
        booleanState.state = i;
    }

    /* access modifiers changed from: protected */
    public String composeChangeAnnouncement() {
        if (((BooleanState) this.mState).value) {
            return this.mContext.getString(C2017R$string.accessibility_quick_settings_work_mode_changed_on);
        }
        return this.mContext.getString(C2017R$string.accessibility_quick_settings_work_mode_changed_off);
    }
}
