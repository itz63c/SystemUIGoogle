package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothPbap;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.bluetooth.BluetoothUuid;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;

public class PbapServerProfile implements LocalBluetoothProfile {
    @VisibleForTesting
    public static final String NAME = "PBAP Server";
    static final ParcelUuid[] PBAB_CLIENT_UUIDS = {BluetoothUuid.HSP, BluetoothUuid.HFP, BluetoothUuid.PBAP_PCE};
    /* access modifiers changed from: private */
    public boolean mIsProfileReady;
    /* access modifiers changed from: private */
    public BluetoothPbap mService;

    private final class PbapServiceListener implements ServiceListener {
        private PbapServiceListener() {
        }

        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            PbapServerProfile.this.mService = (BluetoothPbap) bluetoothProfile;
            PbapServerProfile.this.mIsProfileReady = true;
        }

        public void onServiceDisconnected(int i) {
            PbapServerProfile.this.mIsProfileReady = false;
        }
    }

    public int getDrawableResource(BluetoothClass bluetoothClass) {
        return 17302781;
    }

    public int getProfileId() {
        return 6;
    }

    public String toString() {
        return NAME;
    }

    PbapServerProfile(Context context) {
        BluetoothAdapter.getDefaultAdapter().getProfileProxy(context, new PbapServiceListener(), 6);
    }

    public int getConnectionStatus(BluetoothDevice bluetoothDevice) {
        BluetoothPbap bluetoothPbap = this.mService;
        if (bluetoothPbap == null) {
            return 0;
        }
        return bluetoothPbap.getConnectionState(bluetoothDevice);
    }

    public boolean setEnabled(BluetoothDevice bluetoothDevice, boolean z) {
        BluetoothPbap bluetoothPbap = this.mService;
        boolean z2 = false;
        if (bluetoothPbap == null) {
            return false;
        }
        if (!z) {
            z2 = bluetoothPbap.setConnectionPolicy(bluetoothDevice, 0);
        }
        return z2;
    }

    /* access modifiers changed from: protected */
    public void finalize() {
        String str = "PbapServerProfile";
        Log.d(str, "finalize()");
        if (this.mService != null) {
            try {
                BluetoothAdapter.getDefaultAdapter().closeProfileProxy(6, this.mService);
                this.mService = null;
            } catch (Throwable th) {
                Log.w(str, "Error cleaning up PBAP proxy", th);
            }
        }
    }
}
