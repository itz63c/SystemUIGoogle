package com.android.systemui.p007qs.tiles;

import android.content.Intent;
import android.widget.Switch;
import androidx.appcompat.R$styleable;
import com.android.systemui.C2010R$drawable;
import com.android.systemui.C2017R$string;
import com.android.systemui.p007qs.QSHost;
import com.android.systemui.p007qs.SecureSetting;
import com.android.systemui.p007qs.tileimpl.QSTileImpl;
import com.android.systemui.p007qs.tileimpl.QSTileImpl.ResourceIcon;
import com.android.systemui.plugins.p006qs.QSTile.BooleanState;
import com.android.systemui.plugins.p006qs.QSTile.Icon;
import com.android.systemui.plugins.p006qs.QSTile.SlashState;

/* renamed from: com.android.systemui.qs.tiles.ColorInversionTile */
public class ColorInversionTile extends QSTileImpl<BooleanState> {
    private final Icon mIcon = ResourceIcon.get(C2010R$drawable.ic_invert_colors);
    private final SecureSetting mSetting = new SecureSetting(this.mContext, this.mHandler, "accessibility_display_inversion_enabled") {
        /* access modifiers changed from: protected */
        public void handleValueChanged(int i, boolean z) {
            ColorInversionTile.this.handleRefreshState(Integer.valueOf(i));
        }
    };

    public int getMetricsCategory() {
        return R$styleable.AppCompatTheme_windowActionBarOverlay;
    }

    public ColorInversionTile(QSHost qSHost) {
        super(qSHost);
    }

    /* access modifiers changed from: protected */
    public void handleDestroy() {
        super.handleDestroy();
        this.mSetting.setListening(false);
    }

    public BooleanState newTileState() {
        return new BooleanState();
    }

    public void handleSetListening(boolean z) {
        super.handleSetListening(z);
        this.mSetting.setListening(z);
    }

    /* access modifiers changed from: protected */
    public void handleUserSwitch(int i) {
        this.mSetting.setUserId(i);
        handleRefreshState(Integer.valueOf(this.mSetting.getValue()));
    }

    public Intent getLongClickIntent() {
        return new Intent("android.settings.ACCESSIBILITY_SETTINGS");
    }

    /* access modifiers changed from: protected */
    public void handleClick() {
        this.mSetting.setValue(((BooleanState) this.mState).value ^ true ? 1 : 0);
    }

    public CharSequence getTileLabel() {
        return this.mContext.getString(C2017R$string.quick_settings_inversion_label);
    }

    /* access modifiers changed from: protected */
    public void handleUpdateState(BooleanState booleanState, Object obj) {
        int i = 1;
        boolean z = (obj instanceof Integer ? ((Integer) obj).intValue() : this.mSetting.getValue()) != 0;
        if (booleanState.slash == null) {
            booleanState.slash = new SlashState();
        }
        booleanState.value = z;
        booleanState.slash.isSlashed = !z;
        if (z) {
            i = 2;
        }
        booleanState.state = i;
        booleanState.label = this.mContext.getString(C2017R$string.quick_settings_inversion_label);
        booleanState.icon = this.mIcon;
        booleanState.expandedAccessibilityClassName = Switch.class.getName();
        booleanState.contentDescription = booleanState.label;
    }

    /* access modifiers changed from: protected */
    public String composeChangeAnnouncement() {
        if (((BooleanState) this.mState).value) {
            return this.mContext.getString(C2017R$string.accessibility_quick_settings_color_inversion_changed_on);
        }
        return this.mContext.getString(C2017R$string.accessibility_quick_settings_color_inversion_changed_off);
    }
}
