package com.android.settingslib.media;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settingslib.bluetooth.BluetoothCallback;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.CachedBluetoothDevice.Callback;
import com.android.settingslib.bluetooth.CachedBluetoothDeviceManager;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LocalMediaManager implements BluetoothCallback {
    @VisibleForTesting
    BluetoothAdapter mBluetoothAdapter;
    private final Collection<DeviceCallback> mCallbacks = new CopyOnWriteArrayList();
    /* access modifiers changed from: private */
    public Context mContext;
    @VisibleForTesting
    MediaDevice mCurrentConnectedDevice;
    @VisibleForTesting
    DeviceAttributeChangeCallback mDeviceAttributeChangeCallback = new DeviceAttributeChangeCallback();
    @VisibleForTesting
    List<MediaDevice> mDisconnectedMediaDevices = new ArrayList();
    /* access modifiers changed from: private */
    public InfoMediaManager mInfoMediaManager;
    /* access modifiers changed from: private */
    public LocalBluetoothManager mLocalBluetoothManager;
    @VisibleForTesting
    final MediaDeviceCallback mMediaDeviceCallback = new MediaDeviceCallback();
    @VisibleForTesting
    List<MediaDevice> mMediaDevices = new ArrayList();
    /* access modifiers changed from: private */
    public String mPackageName;
    @VisibleForTesting
    MediaDevice mPhoneDevice;

    @VisibleForTesting
    class DeviceAttributeChangeCallback implements Callback {
        DeviceAttributeChangeCallback() {
        }

        public void onDeviceAttributesChanged() {
            LocalMediaManager.this.dispatchDeviceAttributesChanged();
        }
    }

    public interface DeviceCallback {
        void onDeviceAttributesChanged() {
        }

        void onDeviceListUpdate(List<MediaDevice> list) {
        }

        void onRequestFailed(int i) {
        }

        void onSelectedDeviceStateChanged(MediaDevice mediaDevice, int i) {
        }
    }

    class MediaDeviceCallback implements com.android.settingslib.media.MediaManager.MediaDeviceCallback {
        MediaDeviceCallback() {
        }

        public void onDeviceListAdded(List<MediaDevice> list) {
            LocalMediaManager.this.mMediaDevices.clear();
            LocalMediaManager.this.mMediaDevices.addAll(list);
            LocalMediaManager.this.mMediaDevices.addAll(buildDisconnectedBluetoothDevice());
            MediaDevice currentConnectedDevice = LocalMediaManager.this.mInfoMediaManager.getCurrentConnectedDevice();
            LocalMediaManager localMediaManager = LocalMediaManager.this;
            if (currentConnectedDevice == null) {
                currentConnectedDevice = localMediaManager.updateCurrentConnectedDevice();
            }
            localMediaManager.mCurrentConnectedDevice = currentConnectedDevice;
            LocalMediaManager.this.dispatchDeviceListUpdate();
        }

        private List<MediaDevice> buildDisconnectedBluetoothDevice() {
            for (MediaDevice mediaDevice : LocalMediaManager.this.mDisconnectedMediaDevices) {
                ((BluetoothMediaDevice) mediaDevice).getCachedDevice().unregisterCallback(LocalMediaManager.this.mDeviceAttributeChangeCallback);
            }
            LocalMediaManager.this.mDisconnectedMediaDevices.clear();
            List<BluetoothDevice> mostRecentlyConnectedDevices = LocalMediaManager.this.mBluetoothAdapter.getMostRecentlyConnectedDevices();
            CachedBluetoothDeviceManager cachedDeviceManager = LocalMediaManager.this.mLocalBluetoothManager.getCachedDeviceManager();
            for (BluetoothDevice findDevice : mostRecentlyConnectedDevices) {
                CachedBluetoothDevice findDevice2 = cachedDeviceManager.findDevice(findDevice);
                if (findDevice2 != null && findDevice2.getBondState() == 12 && !findDevice2.isConnected()) {
                    BluetoothMediaDevice bluetoothMediaDevice = new BluetoothMediaDevice(LocalMediaManager.this.mContext, findDevice2, null, null, LocalMediaManager.this.mPackageName);
                    if (!LocalMediaManager.this.mMediaDevices.contains(bluetoothMediaDevice)) {
                        findDevice2.registerCallback(LocalMediaManager.this.mDeviceAttributeChangeCallback);
                        LocalMediaManager.this.mDisconnectedMediaDevices.add(bluetoothMediaDevice);
                    }
                }
            }
            return new ArrayList(LocalMediaManager.this.mDisconnectedMediaDevices);
        }

        public void onConnectedDeviceChanged(String str) {
            LocalMediaManager localMediaManager = LocalMediaManager.this;
            MediaDevice mediaDeviceById = localMediaManager.getMediaDeviceById(localMediaManager.mMediaDevices, str);
            if (mediaDeviceById == null) {
                mediaDeviceById = LocalMediaManager.this.updateCurrentConnectedDevice();
            }
            mediaDeviceById.setState(0);
            LocalMediaManager localMediaManager2 = LocalMediaManager.this;
            if (mediaDeviceById == localMediaManager2.mCurrentConnectedDevice) {
                Log.d("LocalMediaManager", "onConnectedDeviceChanged() this device all ready connected!");
                return;
            }
            localMediaManager2.mCurrentConnectedDevice = mediaDeviceById;
            localMediaManager2.dispatchSelectedDeviceStateChanged(mediaDeviceById, 0);
        }

        public void onRequestFailed(int i) {
            for (MediaDevice mediaDevice : LocalMediaManager.this.mMediaDevices) {
                if (mediaDevice.getState() == 1) {
                    mediaDevice.setState(3);
                }
            }
            LocalMediaManager.this.dispatchOnRequestFailed(i);
        }
    }

    static {
        Comparator.naturalOrder();
    }

    public void registerCallback(DeviceCallback deviceCallback) {
        this.mCallbacks.add(deviceCallback);
    }

    public void unregisterCallback(DeviceCallback deviceCallback) {
        this.mCallbacks.remove(deviceCallback);
    }

    public LocalMediaManager(Context context, LocalBluetoothManager localBluetoothManager, InfoMediaManager infoMediaManager, String str) {
        this.mContext = context;
        this.mLocalBluetoothManager = localBluetoothManager;
        this.mInfoMediaManager = infoMediaManager;
        this.mPackageName = str;
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /* access modifiers changed from: 0000 */
    public void dispatchSelectedDeviceStateChanged(MediaDevice mediaDevice, int i) {
        for (DeviceCallback onSelectedDeviceStateChanged : getCallbacks()) {
            onSelectedDeviceStateChanged.onSelectedDeviceStateChanged(mediaDevice, i);
        }
    }

    public void startScan() {
        this.mMediaDevices.clear();
        this.mInfoMediaManager.registerCallback(this.mMediaDeviceCallback);
        this.mInfoMediaManager.startScan();
    }

    /* access modifiers changed from: 0000 */
    public void dispatchDeviceListUpdate() {
        for (DeviceCallback onDeviceListUpdate : getCallbacks()) {
            onDeviceListUpdate.onDeviceListUpdate(new ArrayList(this.mMediaDevices));
        }
    }

    /* access modifiers changed from: 0000 */
    public void dispatchDeviceAttributesChanged() {
        for (DeviceCallback onDeviceAttributesChanged : getCallbacks()) {
            onDeviceAttributesChanged.onDeviceAttributesChanged();
        }
    }

    /* access modifiers changed from: 0000 */
    public void dispatchOnRequestFailed(int i) {
        for (DeviceCallback onRequestFailed : getCallbacks()) {
            onRequestFailed.onRequestFailed(i);
        }
    }

    public void stopScan() {
        this.mInfoMediaManager.unregisterCallback(this.mMediaDeviceCallback);
        this.mInfoMediaManager.stopScan();
    }

    public MediaDevice getMediaDeviceById(List<MediaDevice> list, String str) {
        for (MediaDevice mediaDevice : list) {
            if (mediaDevice.getId().equals(str)) {
                return mediaDevice;
            }
        }
        Log.i("LocalMediaManager", "getMediaDeviceById() can't found device");
        return null;
    }

    public MediaDevice getCurrentConnectedDevice() {
        return this.mCurrentConnectedDevice;
    }

    /* access modifiers changed from: private */
    public MediaDevice updateCurrentConnectedDevice() {
        MediaDevice mediaDevice = null;
        MediaDevice mediaDevice2 = null;
        for (MediaDevice mediaDevice3 : this.mMediaDevices) {
            if (mediaDevice3 instanceof BluetoothMediaDevice) {
                if (isActiveDevice(((BluetoothMediaDevice) mediaDevice3).getCachedDevice())) {
                    return mediaDevice3;
                }
            } else if (mediaDevice3 instanceof PhoneMediaDevice) {
                mediaDevice2 = mediaDevice3;
            }
        }
        if (this.mMediaDevices.contains(mediaDevice2)) {
            mediaDevice = mediaDevice2;
        }
        return mediaDevice;
    }

    private boolean isActiveDevice(CachedBluetoothDevice cachedBluetoothDevice) {
        return cachedBluetoothDevice.isActiveDevice(2) || cachedBluetoothDevice.isActiveDevice(21);
    }

    private Collection<DeviceCallback> getCallbacks() {
        return new CopyOnWriteArrayList(this.mCallbacks);
    }
}
