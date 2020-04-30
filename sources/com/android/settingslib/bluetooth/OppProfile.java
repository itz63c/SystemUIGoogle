package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;

final class OppProfile implements LocalBluetoothProfile {
    public int getConnectionStatus(BluetoothDevice bluetoothDevice) {
        return 0;
    }

    public int getDrawableResource(BluetoothClass bluetoothClass) {
        return 0;
    }

    public int getProfileId() {
        return 20;
    }

    public boolean setEnabled(BluetoothDevice bluetoothDevice, boolean z) {
        return false;
    }

    public String toString() {
        return "OPP";
    }

    OppProfile() {
    }
}
