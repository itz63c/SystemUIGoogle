package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothPan;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.content.Context;
import android.util.Log;
import java.util.HashMap;
import java.util.List;

public class PanProfile implements LocalBluetoothProfile {
    private final HashMap<BluetoothDevice, Integer> mDeviceRoleMap = new HashMap<>();
    /* access modifiers changed from: private */
    public boolean mIsProfileReady;
    /* access modifiers changed from: private */
    public BluetoothPan mService;

    private final class PanServiceListener implements ServiceListener {
        private PanServiceListener() {
        }

        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            PanProfile.this.mService = (BluetoothPan) bluetoothProfile;
            PanProfile.this.mIsProfileReady = true;
        }

        public void onServiceDisconnected(int i) {
            PanProfile.this.mIsProfileReady = false;
        }
    }

    public int getDrawableResource(BluetoothClass bluetoothClass) {
        return 17302323;
    }

    public int getProfileId() {
        return 5;
    }

    public String toString() {
        return "PAN";
    }

    PanProfile(Context context) {
        BluetoothAdapter.getDefaultAdapter().getProfileProxy(context, new PanServiceListener(), 5);
    }

    public int getConnectionStatus(BluetoothDevice bluetoothDevice) {
        BluetoothPan bluetoothPan = this.mService;
        if (bluetoothPan == null) {
            return 0;
        }
        return bluetoothPan.getConnectionState(bluetoothDevice);
    }

    public boolean setEnabled(BluetoothDevice bluetoothDevice, boolean z) {
        boolean z2;
        BluetoothPan bluetoothPan = this.mService;
        if (bluetoothPan == null) {
            return false;
        }
        if (z) {
            List<BluetoothDevice> connectedDevices = bluetoothPan.getConnectedDevices();
            if (connectedDevices != null) {
                for (BluetoothDevice connectionPolicy : connectedDevices) {
                    this.mService.setConnectionPolicy(connectionPolicy, 0);
                }
            }
            z2 = this.mService.setConnectionPolicy(bluetoothDevice, 100);
        } else {
            z2 = bluetoothPan.setConnectionPolicy(bluetoothDevice, 0);
        }
        return z2;
    }

    /* access modifiers changed from: 0000 */
    public void setLocalRole(BluetoothDevice bluetoothDevice, int i) {
        this.mDeviceRoleMap.put(bluetoothDevice, Integer.valueOf(i));
    }

    /* access modifiers changed from: 0000 */
    public boolean isLocalRoleNap(BluetoothDevice bluetoothDevice) {
        if (!this.mDeviceRoleMap.containsKey(bluetoothDevice) || ((Integer) this.mDeviceRoleMap.get(bluetoothDevice)).intValue() != 1) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public void finalize() {
        String str = "PanProfile";
        Log.d(str, "finalize()");
        if (this.mService != null) {
            try {
                BluetoothAdapter.getDefaultAdapter().closeProfileProxy(5, this.mService);
                this.mService = null;
            } catch (Throwable th) {
                Log.w(str, "Error cleaning up PAN proxy", th);
            }
        }
    }
}
