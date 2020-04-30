package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadsetClient;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.bluetooth.BluetoothUuid;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;
import java.util.List;

final class HfpClientProfile implements LocalBluetoothProfile {
    /* access modifiers changed from: private */
    public final CachedBluetoothDeviceManager mDeviceManager;
    /* access modifiers changed from: private */
    public boolean mIsProfileReady;
    /* access modifiers changed from: private */
    public BluetoothHeadsetClient mService;

    private final class HfpClientServiceListener implements ServiceListener {
        private HfpClientServiceListener() {
        }

        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            HfpClientProfile.this.mService = (BluetoothHeadsetClient) bluetoothProfile;
            List connectedDevices = HfpClientProfile.this.mService.getConnectedDevices();
            while (!connectedDevices.isEmpty()) {
                BluetoothDevice bluetoothDevice = (BluetoothDevice) connectedDevices.remove(0);
                CachedBluetoothDevice findDevice = HfpClientProfile.this.mDeviceManager.findDevice(bluetoothDevice);
                if (findDevice == null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("HfpClient profile found new device: ");
                    sb.append(bluetoothDevice);
                    Log.w("HfpClientProfile", sb.toString());
                    findDevice = HfpClientProfile.this.mDeviceManager.addDevice(bluetoothDevice);
                }
                findDevice.onProfileStateChanged(HfpClientProfile.this, 2);
                findDevice.refresh();
            }
            HfpClientProfile.this.mIsProfileReady = true;
        }

        public void onServiceDisconnected(int i) {
            HfpClientProfile.this.mIsProfileReady = false;
        }
    }

    public int getDrawableResource(BluetoothClass bluetoothClass) {
        return 17302319;
    }

    public int getProfileId() {
        return 16;
    }

    public String toString() {
        return "HEADSET_CLIENT";
    }

    static {
        ParcelUuid parcelUuid = BluetoothUuid.HSP_AG;
        ParcelUuid parcelUuid2 = BluetoothUuid.HFP_AG;
    }

    HfpClientProfile(Context context, CachedBluetoothDeviceManager cachedBluetoothDeviceManager, LocalBluetoothProfileManager localBluetoothProfileManager) {
        this.mDeviceManager = cachedBluetoothDeviceManager;
        BluetoothAdapter.getDefaultAdapter().getProfileProxy(context, new HfpClientServiceListener(), 16);
    }

    public int getConnectionStatus(BluetoothDevice bluetoothDevice) {
        BluetoothHeadsetClient bluetoothHeadsetClient = this.mService;
        if (bluetoothHeadsetClient == null) {
            return 0;
        }
        return bluetoothHeadsetClient.getConnectionState(bluetoothDevice);
    }

    public boolean setEnabled(BluetoothDevice bluetoothDevice, boolean z) {
        BluetoothHeadsetClient bluetoothHeadsetClient = this.mService;
        boolean z2 = false;
        if (bluetoothHeadsetClient == null) {
            return false;
        }
        if (!z) {
            z2 = bluetoothHeadsetClient.setConnectionPolicy(bluetoothDevice, 0);
        } else if (bluetoothHeadsetClient.getConnectionPolicy(bluetoothDevice) < 100) {
            z2 = this.mService.setConnectionPolicy(bluetoothDevice, 100);
        }
        return z2;
    }

    /* access modifiers changed from: protected */
    public void finalize() {
        String str = "HfpClientProfile";
        Log.d(str, "finalize()");
        if (this.mService != null) {
            try {
                BluetoothAdapter.getDefaultAdapter().closeProfileProxy(16, this.mService);
                this.mService = null;
            } catch (Throwable th) {
                Log.w(str, "Error cleaning up HfpClient proxy", th);
            }
        }
    }
}
