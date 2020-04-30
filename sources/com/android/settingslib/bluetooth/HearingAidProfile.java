package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHearingAid;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.content.Context;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class HearingAidProfile implements LocalBluetoothProfile {
    /* access modifiers changed from: private */

    /* renamed from: V */
    public static boolean f23V = true;
    private final BluetoothAdapter mBluetoothAdapter;
    /* access modifiers changed from: private */
    public final CachedBluetoothDeviceManager mDeviceManager;
    /* access modifiers changed from: private */
    public boolean mIsProfileReady;
    /* access modifiers changed from: private */
    public final LocalBluetoothProfileManager mProfileManager;
    /* access modifiers changed from: private */
    public BluetoothHearingAid mService;

    private final class HearingAidServiceListener implements ServiceListener {
        private HearingAidServiceListener() {
        }

        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            HearingAidProfile.this.mService = (BluetoothHearingAid) bluetoothProfile;
            List connectedDevices = HearingAidProfile.this.mService.getConnectedDevices();
            while (!connectedDevices.isEmpty()) {
                BluetoothDevice bluetoothDevice = (BluetoothDevice) connectedDevices.remove(0);
                CachedBluetoothDevice findDevice = HearingAidProfile.this.mDeviceManager.findDevice(bluetoothDevice);
                if (findDevice == null) {
                    if (HearingAidProfile.f23V) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("HearingAidProfile found new device: ");
                        sb.append(bluetoothDevice);
                        Log.d("HearingAidProfile", sb.toString());
                    }
                    findDevice = HearingAidProfile.this.mDeviceManager.addDevice(bluetoothDevice);
                }
                findDevice.onProfileStateChanged(HearingAidProfile.this, 2);
                findDevice.refresh();
            }
            HearingAidProfile.this.mDeviceManager.updateHearingAidsDevices();
            HearingAidProfile.this.mIsProfileReady = true;
            HearingAidProfile.this.mProfileManager.callServiceConnectedListeners();
        }

        public void onServiceDisconnected(int i) {
            HearingAidProfile.this.mIsProfileReady = false;
        }
    }

    public int getDrawableResource(BluetoothClass bluetoothClass) {
        return 17302320;
    }

    public int getProfileId() {
        return 21;
    }

    public String toString() {
        return "HearingAid";
    }

    HearingAidProfile(Context context, CachedBluetoothDeviceManager cachedBluetoothDeviceManager, LocalBluetoothProfileManager localBluetoothProfileManager) {
        this.mDeviceManager = cachedBluetoothDeviceManager;
        this.mProfileManager = localBluetoothProfileManager;
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mBluetoothAdapter = defaultAdapter;
        defaultAdapter.getProfileProxy(context, new HearingAidServiceListener(), 21);
    }

    public int getConnectionStatus(BluetoothDevice bluetoothDevice) {
        BluetoothHearingAid bluetoothHearingAid = this.mService;
        if (bluetoothHearingAid == null) {
            return 0;
        }
        return bluetoothHearingAid.getConnectionState(bluetoothDevice);
    }

    public List<BluetoothDevice> getActiveDevices() {
        BluetoothHearingAid bluetoothHearingAid = this.mService;
        if (bluetoothHearingAid == null) {
            return new ArrayList();
        }
        return bluetoothHearingAid.getActiveDevices();
    }

    public boolean setEnabled(BluetoothDevice bluetoothDevice, boolean z) {
        BluetoothHearingAid bluetoothHearingAid = this.mService;
        boolean z2 = false;
        if (bluetoothHearingAid == null) {
            return false;
        }
        if (!z) {
            z2 = bluetoothHearingAid.setConnectionPolicy(bluetoothDevice, 0);
        } else if (bluetoothHearingAid.getConnectionPolicy(bluetoothDevice) < 100) {
            z2 = this.mService.setConnectionPolicy(bluetoothDevice, 100);
        }
        return z2;
    }

    public long getHiSyncId(BluetoothDevice bluetoothDevice) {
        BluetoothHearingAid bluetoothHearingAid = this.mService;
        if (bluetoothHearingAid == null) {
            return 0;
        }
        return bluetoothHearingAid.getHiSyncId(bluetoothDevice);
    }

    /* access modifiers changed from: protected */
    public void finalize() {
        String str = "HearingAidProfile";
        Log.d(str, "finalize()");
        if (this.mService != null) {
            try {
                BluetoothAdapter.getDefaultAdapter().closeProfileProxy(21, this.mService);
                this.mService = null;
            } catch (Throwable th) {
                Log.w(str, "Error cleaning up Hearing Aid proxy", th);
            }
        }
    }
}
