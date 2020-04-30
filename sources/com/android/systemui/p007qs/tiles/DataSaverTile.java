package com.android.systemui.p007qs.tiles;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.widget.Switch;
import com.android.systemui.C2010R$drawable;
import com.android.systemui.C2017R$string;
import com.android.systemui.Prefs;
import com.android.systemui.p007qs.QSHost;
import com.android.systemui.p007qs.tileimpl.QSTileImpl;
import com.android.systemui.p007qs.tileimpl.QSTileImpl.ResourceIcon;
import com.android.systemui.plugins.p006qs.QSTile.BooleanState;
import com.android.systemui.statusbar.phone.SystemUIDialog;
import com.android.systemui.statusbar.policy.DataSaverController;
import com.android.systemui.statusbar.policy.DataSaverController.Listener;
import com.android.systemui.statusbar.policy.NetworkController;

/* renamed from: com.android.systemui.qs.tiles.DataSaverTile */
public class DataSaverTile extends QSTileImpl<BooleanState> implements Listener {
    private final DataSaverController mDataSaverController;

    public int getMetricsCategory() {
        return 284;
    }

    public DataSaverTile(QSHost qSHost, NetworkController networkController) {
        super(qSHost);
        DataSaverController dataSaverController = networkController.getDataSaverController();
        this.mDataSaverController = dataSaverController;
        dataSaverController.observe(getLifecycle(), this);
    }

    public BooleanState newTileState() {
        return new BooleanState();
    }

    public Intent getLongClickIntent() {
        return new Intent("android.settings.DATA_SAVER_SETTINGS");
    }

    /* access modifiers changed from: protected */
    public void handleClick() {
        if (!((BooleanState) this.mState).value) {
            String str = "QsDataSaverDialogShown";
            if (!Prefs.getBoolean(this.mContext, str, false)) {
                SystemUIDialog systemUIDialog = new SystemUIDialog(this.mContext);
                systemUIDialog.setTitle(17039884);
                systemUIDialog.setMessage(17039882);
                systemUIDialog.setPositiveButton(17039883, new OnClickListener() {
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        DataSaverTile.this.lambda$handleClick$0$DataSaverTile(dialogInterface, i);
                    }
                });
                systemUIDialog.setNegativeButton(17039360, null);
                systemUIDialog.setShowForAllUsers(true);
                systemUIDialog.show();
                Prefs.putBoolean(this.mContext, str, true);
                return;
            }
        }
        toggleDataSaver();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$handleClick$0 */
    public /* synthetic */ void lambda$handleClick$0$DataSaverTile(DialogInterface dialogInterface, int i) {
        toggleDataSaver();
    }

    private void toggleDataSaver() {
        ((BooleanState) this.mState).value = !this.mDataSaverController.isDataSaverEnabled();
        this.mDataSaverController.setDataSaverEnabled(((BooleanState) this.mState).value);
        refreshState(Boolean.valueOf(((BooleanState) this.mState).value));
    }

    public CharSequence getTileLabel() {
        return this.mContext.getString(C2017R$string.data_saver);
    }

    /* access modifiers changed from: protected */
    public void handleUpdateState(BooleanState booleanState, Object obj) {
        boolean z;
        int i;
        if (obj instanceof Boolean) {
            z = ((Boolean) obj).booleanValue();
        } else {
            z = this.mDataSaverController.isDataSaverEnabled();
        }
        booleanState.value = z;
        booleanState.state = z ? 2 : 1;
        String string = this.mContext.getString(C2017R$string.data_saver);
        booleanState.label = string;
        booleanState.contentDescription = string;
        if (booleanState.value) {
            i = C2010R$drawable.ic_data_saver;
        } else {
            i = C2010R$drawable.ic_data_saver_off;
        }
        booleanState.icon = ResourceIcon.get(i);
        booleanState.expandedAccessibilityClassName = Switch.class.getName();
    }

    /* access modifiers changed from: protected */
    public String composeChangeAnnouncement() {
        if (((BooleanState) this.mState).value) {
            return this.mContext.getString(C2017R$string.accessibility_quick_settings_data_saver_changed_on);
        }
        return this.mContext.getString(C2017R$string.accessibility_quick_settings_data_saver_changed_off);
    }

    public void onDataSaverChanged(boolean z) {
        refreshState(Boolean.valueOf(z));
    }
}
