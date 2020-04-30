package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothA2dpSink;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.bluetooth.BluetoothUuid;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;
import java.util.List;

final class A2dpSinkProfile implements LocalBluetoothProfile {
    static final ParcelUuid[] SRC_UUIDS = {BluetoothUuid.A2DP_SOURCE, BluetoothUuid.ADV_AUDIO_DIST};
    /* access modifiers changed from: private */
    public final CachedBluetoothDeviceManager mDeviceManager;
    /* access modifiers changed from: private */
    public boolean mIsProfileReady;
    /* access modifiers changed from: private */
    public BluetoothA2dpSink mService;

    private final class A2dpSinkServiceListener implements ServiceListener {
        private A2dpSinkServiceListener() {
        }

        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            A2dpSinkProfile.this.mService = (BluetoothA2dpSink) bluetoothProfile;
            List connectedDevices = A2dpSinkProfile.this.mService.getConnectedDevices();
            while (!connectedDevices.isEmpty()) {
                BluetoothDevice bluetoothDevice = (BluetoothDevice) connectedDevices.remove(0);
                CachedBluetoothDevice findDevice = A2dpSinkProfile.this.mDeviceManager.findDevice(bluetoothDevice);
                if (findDevice == null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("A2dpSinkProfile found new device: ");
                    sb.append(bluetoothDevice);
                    Log.w("A2dpSinkProfile", sb.toString());
                    findDevice = A2dpSinkProfile.this.mDeviceManager.addDevice(bluetoothDevice);
                }
                findDevice.onProfileStateChanged(A2dpSinkProfile.this, 2);
                findDevice.refresh();
            }
            A2dpSinkProfile.this.mIsProfileReady = true;
        }

        public void onServiceDisconnected(int i) {
            A2dpSinkProfile.this.mIsProfileReady = false;
        }
    }

    public int getDrawableResource(BluetoothClass bluetoothClass) {
        return 17302318;
    }

    public int getProfileId() {
        return 11;
    }

    public String toString() {
        return "A2DPSink";
    }

    A2dpSinkProfile(Context context, CachedBluetoothDeviceManager cachedBluetoothDeviceManager, LocalBluetoothProfileManager localBluetoothProfileManager) {
        this.mDeviceManager = cachedBluetoothDeviceManager;
        BluetoothAdapter.getDefaultAdapter().getProfileProxy(context, new A2dpSinkServiceListener(), 11);
    }

    public int getConnectionStatus(BluetoothDevice bluetoothDevice) {
        BluetoothA2dpSink bluetoothA2dpSink = this.mService;
        if (bluetoothA2dpSink == null) {
            return 0;
        }
        return bluetoothA2dpSink.getConnectionState(bluetoothDevice);
    }

    public boolean setEnabled(BluetoothDevice bluetoothDevice, boolean z) {
        BluetoothA2dpSink bluetoothA2dpSink = this.mService;
        boolean z2 = false;
        if (bluetoothA2dpSink == null) {
            return false;
        }
        if (!z) {
            z2 = bluetoothA2dpSink.setConnectionPolicy(bluetoothDevice, 0);
        } else if (bluetoothA2dpSink.getConnectionPolicy(bluetoothDevice) < 100) {
            z2 = this.mService.setConnectionPolicy(bluetoothDevice, 100);
        }
        return z2;
    }

    /* access modifiers changed from: protected */
    public void finalize() {
        String str = "A2dpSinkProfile";
        Log.d(str, "finalize()");
        if (this.mService != null) {
            try {
                BluetoothAdapter.getDefaultAdapter().closeProfileProxy(11, this.mService);
                this.mService = null;
            } catch (Throwable th) {
                Log.w(str, "Error cleaning up A2DP proxy", th);
            }
        }
    }
}
