package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.bluetooth.BluetoothUuid;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;
import java.util.List;

public class HeadsetProfile implements LocalBluetoothProfile {
    private final BluetoothAdapter mBluetoothAdapter;
    /* access modifiers changed from: private */
    public final CachedBluetoothDeviceManager mDeviceManager;
    /* access modifiers changed from: private */
    public boolean mIsProfileReady;
    /* access modifiers changed from: private */
    public final LocalBluetoothProfileManager mProfileManager;
    /* access modifiers changed from: private */
    public BluetoothHeadset mService;

    private final class HeadsetServiceListener implements ServiceListener {
        private HeadsetServiceListener() {
        }

        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            HeadsetProfile.this.mService = (BluetoothHeadset) bluetoothProfile;
            List connectedDevices = HeadsetProfile.this.mService.getConnectedDevices();
            while (!connectedDevices.isEmpty()) {
                BluetoothDevice bluetoothDevice = (BluetoothDevice) connectedDevices.remove(0);
                CachedBluetoothDevice findDevice = HeadsetProfile.this.mDeviceManager.findDevice(bluetoothDevice);
                if (findDevice == null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("HeadsetProfile found new device: ");
                    sb.append(bluetoothDevice);
                    Log.w("HeadsetProfile", sb.toString());
                    findDevice = HeadsetProfile.this.mDeviceManager.addDevice(bluetoothDevice);
                }
                findDevice.onProfileStateChanged(HeadsetProfile.this, 2);
                findDevice.refresh();
            }
            HeadsetProfile.this.mIsProfileReady = true;
            HeadsetProfile.this.mProfileManager.callServiceConnectedListeners();
        }

        public void onServiceDisconnected(int i) {
            HeadsetProfile.this.mProfileManager.callServiceDisconnectedListeners();
            HeadsetProfile.this.mIsProfileReady = false;
        }
    }

    public int getDrawableResource(BluetoothClass bluetoothClass) {
        return 17302319;
    }

    public int getProfileId() {
        return 1;
    }

    public String toString() {
        return "HEADSET";
    }

    static {
        ParcelUuid parcelUuid = BluetoothUuid.HSP;
        ParcelUuid parcelUuid2 = BluetoothUuid.HFP;
    }

    HeadsetProfile(Context context, CachedBluetoothDeviceManager cachedBluetoothDeviceManager, LocalBluetoothProfileManager localBluetoothProfileManager) {
        this.mDeviceManager = cachedBluetoothDeviceManager;
        this.mProfileManager = localBluetoothProfileManager;
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mBluetoothAdapter = defaultAdapter;
        defaultAdapter.getProfileProxy(context, new HeadsetServiceListener(), 1);
    }

    public int getConnectionStatus(BluetoothDevice bluetoothDevice) {
        BluetoothHeadset bluetoothHeadset = this.mService;
        if (bluetoothHeadset == null) {
            return 0;
        }
        return bluetoothHeadset.getConnectionState(bluetoothDevice);
    }

    public BluetoothDevice getActiveDevice() {
        BluetoothHeadset bluetoothHeadset = this.mService;
        if (bluetoothHeadset == null) {
            return null;
        }
        return bluetoothHeadset.getActiveDevice();
    }

    public boolean setEnabled(BluetoothDevice bluetoothDevice, boolean z) {
        BluetoothHeadset bluetoothHeadset = this.mService;
        boolean z2 = false;
        if (bluetoothHeadset == null) {
            return false;
        }
        if (!z) {
            z2 = bluetoothHeadset.setConnectionPolicy(bluetoothDevice, 0);
        } else if (bluetoothHeadset.getConnectionPolicy(bluetoothDevice) < 100) {
            z2 = this.mService.setConnectionPolicy(bluetoothDevice, 100);
        }
        return z2;
    }

    /* access modifiers changed from: protected */
    public void finalize() {
        String str = "HeadsetProfile";
        Log.d(str, "finalize()");
        if (this.mService != null) {
            try {
                BluetoothAdapter.getDefaultAdapter().closeProfileProxy(1, this.mService);
                this.mService = null;
            } catch (Throwable th) {
                Log.w(str, "Error cleaning up HID proxy", th);
            }
        }
    }
}
