package com.android.settingslib.bluetooth;

import android.content.Context;
import android.os.Handler;
import android.os.UserHandle;

public class LocalBluetoothManager {
    private final CachedBluetoothDeviceManager mCachedDeviceManager;
    private final Context mContext;
    private final BluetoothEventManager mEventManager;
    private final LocalBluetoothAdapter mLocalAdapter;
    private final LocalBluetoothProfileManager mProfileManager;

    public static LocalBluetoothManager create(Context context, Handler handler, UserHandle userHandle) {
        LocalBluetoothAdapter instance = LocalBluetoothAdapter.getInstance();
        if (instance == null) {
            return null;
        }
        return new LocalBluetoothManager(instance, context, handler, userHandle);
    }

    private LocalBluetoothManager(LocalBluetoothAdapter localBluetoothAdapter, Context context, Handler handler, UserHandle userHandle) {
        Context applicationContext = context.getApplicationContext();
        this.mContext = applicationContext;
        this.mLocalAdapter = localBluetoothAdapter;
        CachedBluetoothDeviceManager cachedBluetoothDeviceManager = new CachedBluetoothDeviceManager(applicationContext, this);
        this.mCachedDeviceManager = cachedBluetoothDeviceManager;
        BluetoothEventManager bluetoothEventManager = new BluetoothEventManager(this.mLocalAdapter, cachedBluetoothDeviceManager, this.mContext, handler, userHandle);
        this.mEventManager = bluetoothEventManager;
        LocalBluetoothProfileManager localBluetoothProfileManager = new LocalBluetoothProfileManager(this.mContext, this.mLocalAdapter, this.mCachedDeviceManager, bluetoothEventManager);
        this.mProfileManager = localBluetoothProfileManager;
        localBluetoothProfileManager.updateLocalProfiles();
        this.mEventManager.readPairedDevices();
    }

    public LocalBluetoothAdapter getBluetoothAdapter() {
        return this.mLocalAdapter;
    }

    public CachedBluetoothDeviceManager getCachedDeviceManager() {
        return this.mCachedDeviceManager;
    }

    public BluetoothEventManager getEventManager() {
        return this.mEventManager;
    }

    public LocalBluetoothProfileManager getProfileManager() {
        return this.mProfileManager;
    }
}
