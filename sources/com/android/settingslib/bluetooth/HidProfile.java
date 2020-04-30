package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHidHost;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.content.Context;
import android.util.Log;
import java.util.List;

public class HidProfile implements LocalBluetoothProfile {
    /* access modifiers changed from: private */
    public final CachedBluetoothDeviceManager mDeviceManager;
    /* access modifiers changed from: private */
    public boolean mIsProfileReady;
    /* access modifiers changed from: private */
    public BluetoothHidHost mService;

    private final class HidHostServiceListener implements ServiceListener {
        private HidHostServiceListener() {
        }

        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            HidProfile.this.mService = (BluetoothHidHost) bluetoothProfile;
            List connectedDevices = HidProfile.this.mService.getConnectedDevices();
            while (!connectedDevices.isEmpty()) {
                BluetoothDevice bluetoothDevice = (BluetoothDevice) connectedDevices.remove(0);
                CachedBluetoothDevice findDevice = HidProfile.this.mDeviceManager.findDevice(bluetoothDevice);
                if (findDevice == null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("HidProfile found new device: ");
                    sb.append(bluetoothDevice);
                    Log.w("HidProfile", sb.toString());
                    findDevice = HidProfile.this.mDeviceManager.addDevice(bluetoothDevice);
                }
                findDevice.onProfileStateChanged(HidProfile.this, 2);
                findDevice.refresh();
            }
            HidProfile.this.mIsProfileReady = true;
        }

        public void onServiceDisconnected(int i) {
            HidProfile.this.mIsProfileReady = false;
        }
    }

    public int getProfileId() {
        return 4;
    }

    public String toString() {
        return "HID";
    }

    HidProfile(Context context, CachedBluetoothDeviceManager cachedBluetoothDeviceManager, LocalBluetoothProfileManager localBluetoothProfileManager) {
        this.mDeviceManager = cachedBluetoothDeviceManager;
        BluetoothAdapter.getDefaultAdapter().getProfileProxy(context, new HidHostServiceListener(), 4);
    }

    public int getConnectionStatus(BluetoothDevice bluetoothDevice) {
        BluetoothHidHost bluetoothHidHost = this.mService;
        if (bluetoothHidHost == null) {
            return 0;
        }
        return bluetoothHidHost.getConnectionState(bluetoothDevice);
    }

    public boolean setEnabled(BluetoothDevice bluetoothDevice, boolean z) {
        BluetoothHidHost bluetoothHidHost = this.mService;
        boolean z2 = false;
        if (bluetoothHidHost == null) {
            return false;
        }
        if (!z) {
            z2 = bluetoothHidHost.setConnectionPolicy(bluetoothDevice, 0);
        } else if (bluetoothHidHost.getConnectionPolicy(bluetoothDevice) < 100) {
            z2 = this.mService.setConnectionPolicy(bluetoothDevice, 100);
        }
        return z2;
    }

    public int getDrawableResource(BluetoothClass bluetoothClass) {
        if (bluetoothClass == null) {
            return 17302496;
        }
        return getHidClassDrawable(bluetoothClass);
    }

    public static int getHidClassDrawable(BluetoothClass bluetoothClass) {
        int deviceClass = bluetoothClass.getDeviceClass();
        if (deviceClass != 1344) {
            if (deviceClass == 1408) {
                return 17302324;
            }
            if (deviceClass != 1472) {
                return 17302322;
            }
        }
        return 17302496;
    }

    /* access modifiers changed from: protected */
    public void finalize() {
        String str = "HidProfile";
        Log.d(str, "finalize()");
        if (this.mService != null) {
            try {
                BluetoothAdapter.getDefaultAdapter().closeProfileProxy(4, this.mService);
                this.mService = null;
            } catch (Throwable th) {
                Log.w(str, "Error cleaning up HID proxy", th);
            }
        }
    }
}
