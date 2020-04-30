package com.android.systemui.p007qs.tiles;

import android.content.Intent;
import android.content.res.Resources;
import android.widget.Switch;
import androidx.appcompat.R$styleable;
import com.android.systemui.C2017R$string;
import com.android.systemui.p007qs.tileimpl.QSTileImpl;
import com.android.systemui.p007qs.tileimpl.QSTileImpl.ResourceIcon;
import com.android.systemui.plugins.p006qs.QSTile.BooleanState;
import com.android.systemui.plugins.p006qs.QSTile.Icon;
import com.android.systemui.statusbar.policy.RotationLockController;
import com.android.systemui.statusbar.policy.RotationLockController.RotationLockControllerCallback;

/* renamed from: com.android.systemui.qs.tiles.RotationLockTile */
public class RotationLockTile extends QSTileImpl<BooleanState> {
    private final RotationLockControllerCallback mCallback;
    private final RotationLockController mController;
    private final Icon mIcon = ResourceIcon.get(17302792);

    public int getMetricsCategory() {
        return R$styleable.AppCompatTheme_windowMinWidthMinor;
    }

    /* JADX WARNING: type inference failed for: r2v0, types: [com.android.systemui.statusbar.policy.CallbackController, com.android.systemui.statusbar.policy.RotationLockController] */
    /* JADX WARNING: Incorrect type for immutable var: ssa=com.android.systemui.statusbar.policy.RotationLockController, code=null, for r2v0, types: [com.android.systemui.statusbar.policy.CallbackController, com.android.systemui.statusbar.policy.RotationLockController] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public RotationLockTile(com.android.systemui.p007qs.QSHost r1, com.android.systemui.statusbar.policy.RotationLockController r2) {
        /*
            r0 = this;
            r0.<init>(r1)
            r1 = 17302792(0x1080508, float:2.4982865E-38)
            com.android.systemui.plugins.qs.QSTile$Icon r1 = com.android.systemui.p007qs.tileimpl.QSTileImpl.ResourceIcon.get(r1)
            r0.mIcon = r1
            com.android.systemui.qs.tiles.RotationLockTile$1 r1 = new com.android.systemui.qs.tiles.RotationLockTile$1
            r1.<init>()
            r0.mCallback = r1
            r0.mController = r2
            r2.observe(r0, r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.p007qs.tiles.RotationLockTile.<init>(com.android.systemui.qs.QSHost, com.android.systemui.statusbar.policy.RotationLockController):void");
    }

    public BooleanState newTileState() {
        return new BooleanState();
    }

    public Intent getLongClickIntent() {
        return new Intent("android.settings.DISPLAY_SETTINGS");
    }

    /* access modifiers changed from: protected */
    public void handleClick() {
        boolean z = !((BooleanState) this.mState).value;
        this.mController.setRotationLocked(!z);
        refreshState(Boolean.valueOf(z));
    }

    public CharSequence getTileLabel() {
        return ((BooleanState) getState()).label;
    }

    /* access modifiers changed from: protected */
    public void handleUpdateState(BooleanState booleanState, Object obj) {
        boolean isRotationLocked = this.mController.isRotationLocked();
        booleanState.value = !isRotationLocked;
        booleanState.label = this.mContext.getString(C2017R$string.quick_settings_rotation_unlocked_label);
        booleanState.icon = this.mIcon;
        booleanState.contentDescription = getAccessibilityString(isRotationLocked);
        booleanState.expandedAccessibilityClassName = Switch.class.getName();
        booleanState.state = booleanState.value ? 2 : 1;
    }

    public static boolean isCurrentOrientationLockPortrait(RotationLockController rotationLockController, Resources resources) {
        int rotationLockOrientation = rotationLockController.getRotationLockOrientation();
        boolean z = true;
        if (rotationLockOrientation == 0) {
            if (resources.getConfiguration().orientation == 2) {
                z = false;
            }
            return z;
        }
        if (rotationLockOrientation == 2) {
            z = false;
        }
        return z;
    }

    private String getAccessibilityString(boolean z) {
        return this.mContext.getString(C2017R$string.accessibility_quick_settings_rotation);
    }

    /* access modifiers changed from: protected */
    public String composeChangeAnnouncement() {
        return getAccessibilityString(((BooleanState) this.mState).value);
    }
}
