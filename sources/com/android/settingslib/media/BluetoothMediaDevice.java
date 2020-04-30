package com.android.settingslib.media;

import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaRoute2Info;
import android.media.MediaRouter2Manager;
import com.android.settingslib.bluetooth.BluetoothUtils;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;

public class BluetoothMediaDevice extends MediaDevice {
    private CachedBluetoothDevice mCachedDevice;

    BluetoothMediaDevice(Context context, CachedBluetoothDevice cachedBluetoothDevice, MediaRouter2Manager mediaRouter2Manager, MediaRoute2Info mediaRoute2Info, String str) {
        super(context, 3, mediaRouter2Manager, mediaRoute2Info, str);
        this.mCachedDevice = cachedBluetoothDevice;
        initDeviceRecord();
    }

    public String getName() {
        return this.mCachedDevice.getName();
    }

    public Drawable getIcon() {
        return (Drawable) BluetoothUtils.getBtRainbowDrawableWithDescription(this.mContext, this.mCachedDevice).first;
    }

    public String getId() {
        return MediaDeviceUtils.getId(this.mCachedDevice);
    }

    public CachedBluetoothDevice getCachedDevice() {
        return this.mCachedDevice;
    }

    /* access modifiers changed from: protected */
    public boolean isCarKitDevice() {
        BluetoothClass bluetoothClass = this.mCachedDevice.getDevice().getBluetoothClass();
        if (bluetoothClass != null) {
            int deviceClass = bluetoothClass.getDeviceClass();
            if (deviceClass == 1032 || deviceClass == 1056) {
                return true;
            }
        }
        return false;
    }

    public boolean isConnected() {
        return this.mCachedDevice.getBondState() == 12 && this.mCachedDevice.isConnected();
    }
}
