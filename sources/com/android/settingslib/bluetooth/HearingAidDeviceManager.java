package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import java.util.HashSet;
import java.util.List;

public class HearingAidDeviceManager {
    private final LocalBluetoothManager mBtManager;
    private final List<CachedBluetoothDevice> mCachedDevices;

    private boolean isValidHiSyncId(long j) {
        return j != 0;
    }

    HearingAidDeviceManager(LocalBluetoothManager localBluetoothManager, List<CachedBluetoothDevice> list) {
        this.mBtManager = localBluetoothManager;
        this.mCachedDevices = list;
    }

    /* access modifiers changed from: 0000 */
    public void initHearingAidDeviceIfNeeded(CachedBluetoothDevice cachedBluetoothDevice) {
        long hiSyncId = getHiSyncId(cachedBluetoothDevice.getDevice());
        if (isValidHiSyncId(hiSyncId)) {
            cachedBluetoothDevice.setHiSyncId(hiSyncId);
        }
    }

    private long getHiSyncId(BluetoothDevice bluetoothDevice) {
        HearingAidProfile hearingAidProfile = this.mBtManager.getProfileManager().getHearingAidProfile();
        if (hearingAidProfile != null) {
            return hearingAidProfile.getHiSyncId(bluetoothDevice);
        }
        return 0;
    }

    /* access modifiers changed from: 0000 */
    public boolean setSubDeviceIfNeeded(CachedBluetoothDevice cachedBluetoothDevice) {
        long hiSyncId = cachedBluetoothDevice.getHiSyncId();
        if (isValidHiSyncId(hiSyncId)) {
            CachedBluetoothDevice cachedDevice = getCachedDevice(hiSyncId);
            if (cachedDevice != null) {
                cachedDevice.setSubDevice(cachedBluetoothDevice);
                return true;
            }
        }
        return false;
    }

    private CachedBluetoothDevice getCachedDevice(long j) {
        for (int size = this.mCachedDevices.size() - 1; size >= 0; size--) {
            CachedBluetoothDevice cachedBluetoothDevice = (CachedBluetoothDevice) this.mCachedDevices.get(size);
            if (cachedBluetoothDevice.getHiSyncId() == j) {
                return cachedBluetoothDevice;
            }
        }
        return null;
    }

    /* access modifiers changed from: 0000 */
    public void updateHearingAidsDevices() {
        HashSet<Long> hashSet = new HashSet<>();
        for (CachedBluetoothDevice cachedBluetoothDevice : this.mCachedDevices) {
            if (!isValidHiSyncId(cachedBluetoothDevice.getHiSyncId())) {
                long hiSyncId = getHiSyncId(cachedBluetoothDevice.getDevice());
                if (isValidHiSyncId(hiSyncId)) {
                    cachedBluetoothDevice.setHiSyncId(hiSyncId);
                    hashSet.add(Long.valueOf(hiSyncId));
                }
            }
        }
        for (Long longValue : hashSet) {
            onHiSyncIdChanged(longValue.longValue());
        }
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void onHiSyncIdChanged(long j) {
        CachedBluetoothDevice cachedBluetoothDevice;
        int size = this.mCachedDevices.size() - 1;
        int i = -1;
        while (size >= 0) {
            CachedBluetoothDevice cachedBluetoothDevice2 = (CachedBluetoothDevice) this.mCachedDevices.get(size);
            if (cachedBluetoothDevice2.getHiSyncId() == j) {
                if (i == -1) {
                    i = size;
                } else {
                    if (cachedBluetoothDevice2.isConnected()) {
                        cachedBluetoothDevice = (CachedBluetoothDevice) this.mCachedDevices.get(i);
                        size = i;
                    } else {
                        CachedBluetoothDevice cachedBluetoothDevice3 = cachedBluetoothDevice2;
                        cachedBluetoothDevice2 = (CachedBluetoothDevice) this.mCachedDevices.get(i);
                        cachedBluetoothDevice = cachedBluetoothDevice3;
                    }
                    cachedBluetoothDevice2.setSubDevice(cachedBluetoothDevice);
                    this.mCachedDevices.remove(size);
                    StringBuilder sb = new StringBuilder();
                    sb.append("onHiSyncIdChanged: removed from UI device =");
                    sb.append(cachedBluetoothDevice);
                    sb.append(", with hiSyncId=");
                    sb.append(j);
                    log(sb.toString());
                    this.mBtManager.getEventManager().dispatchDeviceRemoved(cachedBluetoothDevice);
                    return;
                }
            }
            size--;
        }
    }

    /* access modifiers changed from: 0000 */
    public boolean onProfileConnectionStateChangedIfProcessed(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        if (i == 0) {
            CachedBluetoothDevice findMainDevice = findMainDevice(cachedBluetoothDevice);
            if (findMainDevice != null) {
                findMainDevice.refresh();
                return true;
            }
            CachedBluetoothDevice subDevice = cachedBluetoothDevice.getSubDevice();
            if (subDevice != null && subDevice.isConnected()) {
                this.mBtManager.getEventManager().dispatchDeviceRemoved(cachedBluetoothDevice);
                cachedBluetoothDevice.switchSubDeviceContent();
                cachedBluetoothDevice.refresh();
                this.mBtManager.getEventManager().dispatchDeviceAdded(cachedBluetoothDevice);
                return true;
            }
        } else if (i == 2) {
            onHiSyncIdChanged(cachedBluetoothDevice.getHiSyncId());
            CachedBluetoothDevice findMainDevice2 = findMainDevice(cachedBluetoothDevice);
            if (findMainDevice2 != null) {
                if (findMainDevice2.isConnected()) {
                    findMainDevice2.refresh();
                    return true;
                }
                this.mBtManager.getEventManager().dispatchDeviceRemoved(findMainDevice2);
                findMainDevice2.switchSubDeviceContent();
                findMainDevice2.refresh();
                this.mBtManager.getEventManager().dispatchDeviceAdded(findMainDevice2);
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: 0000 */
    public CachedBluetoothDevice findMainDevice(CachedBluetoothDevice cachedBluetoothDevice) {
        for (CachedBluetoothDevice cachedBluetoothDevice2 : this.mCachedDevices) {
            if (isValidHiSyncId(cachedBluetoothDevice2.getHiSyncId())) {
                CachedBluetoothDevice subDevice = cachedBluetoothDevice2.getSubDevice();
                if (subDevice != null && subDevice.equals(cachedBluetoothDevice)) {
                    return cachedBluetoothDevice2;
                }
            }
        }
        return null;
    }

    private void log(String str) {
        Log.d("HearingAidDeviceManager", str);
    }
}
