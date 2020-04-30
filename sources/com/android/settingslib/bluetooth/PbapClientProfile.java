package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothPbapClient;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.bluetooth.BluetoothUuid;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;
import java.util.List;

public final class PbapClientProfile implements LocalBluetoothProfile {
    static final ParcelUuid[] SRC_UUIDS = {BluetoothUuid.PBAP_PSE};
    /* access modifiers changed from: private */
    public final CachedBluetoothDeviceManager mDeviceManager;
    /* access modifiers changed from: private */
    public boolean mIsProfileReady;
    /* access modifiers changed from: private */
    public BluetoothPbapClient mService;

    private final class PbapClientServiceListener implements ServiceListener {
        private PbapClientServiceListener() {
        }

        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            PbapClientProfile.this.mService = (BluetoothPbapClient) bluetoothProfile;
            List connectedDevices = PbapClientProfile.this.mService.getConnectedDevices();
            while (!connectedDevices.isEmpty()) {
                BluetoothDevice bluetoothDevice = (BluetoothDevice) connectedDevices.remove(0);
                CachedBluetoothDevice findDevice = PbapClientProfile.this.mDeviceManager.findDevice(bluetoothDevice);
                if (findDevice == null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("PbapClientProfile found new device: ");
                    sb.append(bluetoothDevice);
                    Log.w("PbapClientProfile", sb.toString());
                    findDevice = PbapClientProfile.this.mDeviceManager.addDevice(bluetoothDevice);
                }
                findDevice.onProfileStateChanged(PbapClientProfile.this, 2);
                findDevice.refresh();
            }
            PbapClientProfile.this.mIsProfileReady = true;
        }

        public void onServiceDisconnected(int i) {
            PbapClientProfile.this.mIsProfileReady = false;
        }
    }

    public int getDrawableResource(BluetoothClass bluetoothClass) {
        return 17302781;
    }

    public int getProfileId() {
        return 17;
    }

    public String toString() {
        return "PbapClient";
    }

    PbapClientProfile(Context context, CachedBluetoothDeviceManager cachedBluetoothDeviceManager, LocalBluetoothProfileManager localBluetoothProfileManager) {
        this.mDeviceManager = cachedBluetoothDeviceManager;
        BluetoothAdapter.getDefaultAdapter().getProfileProxy(context, new PbapClientServiceListener(), 17);
    }

    public int getConnectionStatus(BluetoothDevice bluetoothDevice) {
        BluetoothPbapClient bluetoothPbapClient = this.mService;
        if (bluetoothPbapClient == null) {
            return 0;
        }
        return bluetoothPbapClient.getConnectionState(bluetoothDevice);
    }

    public boolean setEnabled(BluetoothDevice bluetoothDevice, boolean z) {
        BluetoothPbapClient bluetoothPbapClient = this.mService;
        boolean z2 = false;
        if (bluetoothPbapClient == null) {
            return false;
        }
        if (!z) {
            z2 = bluetoothPbapClient.setConnectionPolicy(bluetoothDevice, 0);
        } else if (bluetoothPbapClient.getConnectionPolicy(bluetoothDevice) < 100) {
            z2 = this.mService.setConnectionPolicy(bluetoothDevice, 100);
        }
        return z2;
    }

    /* access modifiers changed from: protected */
    public void finalize() {
        String str = "PbapClientProfile";
        Log.d(str, "finalize()");
        if (this.mService != null) {
            try {
                BluetoothAdapter.getDefaultAdapter().closeProfileProxy(17, this.mService);
                this.mService = null;
            } catch (Throwable th) {
                Log.w(str, "Error cleaning up PBAP Client proxy", th);
            }
        }
    }
}
