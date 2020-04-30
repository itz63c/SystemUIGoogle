package com.android.systemui.p007qs.tiles;

import android.content.Intent;
import android.widget.Switch;
import androidx.appcompat.R$styleable;
import androidx.lifecycle.LifecycleOwner;
import com.android.systemui.C2010R$drawable;
import com.android.systemui.C2017R$string;
import com.android.systemui.p007qs.QSHost;
import com.android.systemui.p007qs.tileimpl.QSTileImpl;
import com.android.systemui.p007qs.tileimpl.QSTileImpl.ResourceIcon;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.p006qs.QSTile.BooleanState;
import com.android.systemui.plugins.p006qs.QSTile.Icon;
import com.android.systemui.plugins.p006qs.QSTile.SlashState;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.statusbar.policy.LocationController;
import com.android.systemui.statusbar.policy.LocationController.LocationChangeCallback;

/* renamed from: com.android.systemui.qs.tiles.LocationTile */
public class LocationTile extends QSTileImpl<BooleanState> {
    private final ActivityStarter mActivityStarter;
    private final Callback mCallback;
    private final LocationController mController;
    private final Icon mIcon = ResourceIcon.get(C2010R$drawable.ic_location);
    private final KeyguardStateController mKeyguard;

    /* renamed from: com.android.systemui.qs.tiles.LocationTile$Callback */
    private final class Callback implements LocationChangeCallback, com.android.systemui.statusbar.policy.KeyguardStateController.Callback {
        private Callback() {
        }

        public void onLocationSettingsChanged(boolean z) {
            LocationTile.this.refreshState();
        }

        public void onKeyguardShowingChanged() {
            LocationTile.this.refreshState();
        }
    }

    public int getMetricsCategory() {
        return R$styleable.AppCompatTheme_windowMinWidthMajor;
    }

    public LocationTile(QSHost qSHost, LocationController locationController, KeyguardStateController keyguardStateController, ActivityStarter activityStarter) {
        super(qSHost);
        Callback callback = new Callback();
        this.mCallback = callback;
        this.mController = locationController;
        this.mKeyguard = keyguardStateController;
        this.mActivityStarter = activityStarter;
        locationController.observe((LifecycleOwner) this, callback);
        this.mKeyguard.observe((LifecycleOwner) this, this.mCallback);
    }

    public BooleanState newTileState() {
        return new BooleanState();
    }

    public Intent getLongClickIntent() {
        return new Intent("android.settings.LOCATION_SOURCE_SETTINGS");
    }

    /* access modifiers changed from: protected */
    public void handleClick() {
        if (!this.mKeyguard.isMethodSecure() || !this.mKeyguard.isShowing()) {
            this.mController.setLocationEnabled(!((BooleanState) this.mState).value);
            return;
        }
        this.mActivityStarter.postQSRunnableDismissingKeyguard(new Runnable() {
            public final void run() {
                LocationTile.this.lambda$handleClick$0$LocationTile();
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$handleClick$0 */
    public /* synthetic */ void lambda$handleClick$0$LocationTile() {
        boolean z = ((BooleanState) this.mState).value;
        this.mHost.openPanels();
        this.mController.setLocationEnabled(!z);
    }

    public CharSequence getTileLabel() {
        return this.mContext.getString(C2017R$string.quick_settings_location_label);
    }

    /* access modifiers changed from: protected */
    public void handleUpdateState(BooleanState booleanState, Object obj) {
        if (booleanState.slash == null) {
            booleanState.slash = new SlashState();
        }
        booleanState.value = this.mController.isLocationEnabled();
        checkIfRestrictionEnforcedByAdminOnly(booleanState, "no_share_location");
        if (!booleanState.disabledByPolicy) {
            checkIfRestrictionEnforcedByAdminOnly(booleanState, "no_config_location");
        }
        booleanState.icon = this.mIcon;
        int i = 1;
        booleanState.slash.isSlashed = !booleanState.value;
        String string = this.mContext.getString(C2017R$string.quick_settings_location_label);
        booleanState.label = string;
        booleanState.contentDescription = string;
        if (booleanState.value) {
            i = 2;
        }
        booleanState.state = i;
        booleanState.expandedAccessibilityClassName = Switch.class.getName();
    }

    /* access modifiers changed from: protected */
    public String composeChangeAnnouncement() {
        if (((BooleanState) this.mState).value) {
            return this.mContext.getString(C2017R$string.accessibility_quick_settings_location_changed_on);
        }
        return this.mContext.getString(C2017R$string.accessibility_quick_settings_location_changed_off);
    }
}
