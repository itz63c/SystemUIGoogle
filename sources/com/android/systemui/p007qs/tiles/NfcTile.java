package com.android.systemui.p007qs.tiles;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.widget.Switch;
import com.android.systemui.C2010R$drawable;
import com.android.systemui.C2017R$string;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.p007qs.QSHost;
import com.android.systemui.p007qs.tileimpl.QSTileImpl;
import com.android.systemui.p007qs.tileimpl.QSTileImpl.ResourceIcon;
import com.android.systemui.plugins.p006qs.QSTile.BooleanState;

/* renamed from: com.android.systemui.qs.tiles.NfcTile */
public class NfcTile extends QSTileImpl<BooleanState> {
    private NfcAdapter mAdapter;
    private BroadcastDispatcher mBroadcastDispatcher;
    private boolean mListening;
    private BroadcastReceiver mNfcReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            NfcTile.this.refreshState();
        }
    };

    public int getMetricsCategory() {
        return 800;
    }

    /* access modifiers changed from: protected */
    public void handleUserSwitch(int i) {
    }

    public NfcTile(QSHost qSHost, BroadcastDispatcher broadcastDispatcher) {
        super(qSHost);
        this.mBroadcastDispatcher = broadcastDispatcher;
    }

    public BooleanState newTileState() {
        return new BooleanState();
    }

    public void handleSetListening(boolean z) {
        super.handleSetListening(z);
        this.mListening = z;
        if (z) {
            this.mBroadcastDispatcher.registerReceiver(this.mNfcReceiver, new IntentFilter("android.nfc.action.ADAPTER_STATE_CHANGED"));
        } else {
            this.mBroadcastDispatcher.unregisterReceiver(this.mNfcReceiver);
        }
    }

    public boolean isAvailable() {
        return this.mContext.getPackageManager().hasSystemFeature("android.hardware.nfc");
    }

    public Intent getLongClickIntent() {
        return new Intent("android.settings.NFC_SETTINGS");
    }

    /* access modifiers changed from: protected */
    public void handleClick() {
        if (getAdapter() != null) {
            if (!getAdapter().isEnabled()) {
                getAdapter().enable();
            } else {
                getAdapter().disable();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void handleSecondaryClick() {
        handleClick();
    }

    public CharSequence getTileLabel() {
        return this.mContext.getString(C2017R$string.quick_settings_nfc_label);
    }

    /* access modifiers changed from: protected */
    public void handleUpdateState(BooleanState booleanState, Object obj) {
        int i = 1;
        booleanState.value = getAdapter() != null && getAdapter().isEnabled();
        if (getAdapter() == null) {
            i = 0;
        } else if (booleanState.value) {
            i = 2;
        }
        booleanState.state = i;
        booleanState.icon = ResourceIcon.get(booleanState.value ? C2010R$drawable.ic_qs_nfc_enabled : C2010R$drawable.ic_qs_nfc_disabled);
        booleanState.label = this.mContext.getString(C2017R$string.quick_settings_nfc_label);
        booleanState.expandedAccessibilityClassName = Switch.class.getName();
        booleanState.contentDescription = booleanState.label;
    }

    /* access modifiers changed from: protected */
    public String composeChangeAnnouncement() {
        if (((BooleanState) this.mState).value) {
            return this.mContext.getString(C2017R$string.quick_settings_nfc_on);
        }
        return this.mContext.getString(C2017R$string.quick_settings_nfc_off);
    }

    private NfcAdapter getAdapter() {
        if (this.mAdapter == null) {
            try {
                this.mAdapter = NfcAdapter.getDefaultAdapter(this.mContext);
            } catch (UnsupportedOperationException unused) {
                this.mAdapter = null;
            }
        }
        return this.mAdapter;
    }
}
