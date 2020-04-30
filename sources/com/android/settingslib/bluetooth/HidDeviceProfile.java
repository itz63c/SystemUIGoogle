package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHidDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.content.Context;
import android.util.Log;

public class HidDeviceProfile implements LocalBluetoothProfile {
    /* access modifiers changed from: private */
    public final CachedBluetoothDeviceManager mDeviceManager;
    /* access modifiers changed from: private */
    public boolean mIsProfileReady;
    /* access modifiers changed from: private */
    public BluetoothHidDevice mService;

    private final class HidDeviceServiceListener implements ServiceListener {
        private HidDeviceServiceListener() {
        }

        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            HidDeviceProfile.this.mService = (BluetoothHidDevice) bluetoothProfile;
            for (BluetoothDevice bluetoothDevice : HidDeviceProfile.this.mService.getConnectedDevices()) {
                CachedBluetoothDevice findDevice = HidDeviceProfile.this.mDeviceManager.findDevice(bluetoothDevice);
                String str = "HidDeviceProfile";
                if (findDevice == null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("HidProfile found new device: ");
                    sb.append(bluetoothDevice);
                    Log.w(str, sb.toString());
                    findDevice = HidDeviceProfile.this.mDeviceManager.addDevice(bluetoothDevice);
                }
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Connection status changed: ");
                sb2.append(findDevice);
                Log.d(str, sb2.toString());
                findDevice.onProfileStateChanged(HidDeviceProfile.this, 2);
                findDevice.refresh();
            }
            HidDeviceProfile.this.mIsProfileReady = true;
        }

        public void onServiceDisconnected(int i) {
            HidDeviceProfile.this.mIsProfileReady = false;
        }
    }

    public int getDrawableResource(BluetoothClass bluetoothClass) {
        return 17302322;
    }

    public int getProfileId() {
        return 19;
    }

    public String toString() {
        return "HID DEVICE";
    }

    HidDeviceProfile(Context context, CachedBluetoothDeviceManager cachedBluetoothDeviceManager, LocalBluetoothProfileManager localBluetoothProfileManager) {
        this.mDeviceManager = cachedBluetoothDeviceManager;
        BluetoothAdapter.getDefaultAdapter().getProfileProxy(context, new HidDeviceServiceListener(), 19);
    }

    public int getConnectionStatus(BluetoothDevice bluetoothDevice) {
        BluetoothHidDevice bluetoothHidDevice = this.mService;
        if (bluetoothHidDevice == null) {
            return 0;
        }
        return bluetoothHidDevice.getConnectionState(bluetoothDevice);
    }

    public boolean setEnabled(BluetoothDevice bluetoothDevice, boolean z) {
        if (!z) {
            return this.mService.setConnectionPolicy(bluetoothDevice, 0);
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void finalize() {
        String str = "HidDeviceProfile";
        Log.d(str, "finalize()");
        if (this.mService != null) {
            try {
                BluetoothAdapter.getDefaultAdapter().closeProfileProxy(19, this.mService);
                this.mService = null;
            } catch (Throwable th) {
                Log.w(str, "Error cleaning up HID proxy", th);
            }
        }
    }
}
