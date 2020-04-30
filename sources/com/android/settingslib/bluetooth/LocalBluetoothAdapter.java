package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import java.util.Set;

@Deprecated
public class LocalBluetoothAdapter {
    private static LocalBluetoothAdapter sInstance;
    private final BluetoothAdapter mAdapter;
    private LocalBluetoothProfileManager mProfileManager;
    private int mState = Integer.MIN_VALUE;

    private LocalBluetoothAdapter(BluetoothAdapter bluetoothAdapter) {
        this.mAdapter = bluetoothAdapter;
    }

    /* access modifiers changed from: 0000 */
    public void setProfileManager(LocalBluetoothProfileManager localBluetoothProfileManager) {
        this.mProfileManager = localBluetoothProfileManager;
    }

    static synchronized LocalBluetoothAdapter getInstance() {
        LocalBluetoothAdapter localBluetoothAdapter;
        synchronized (LocalBluetoothAdapter.class) {
            if (sInstance == null) {
                BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
                if (defaultAdapter != null) {
                    sInstance = new LocalBluetoothAdapter(defaultAdapter);
                }
            }
            localBluetoothAdapter = sInstance;
        }
        return localBluetoothAdapter;
    }

    public boolean enable() {
        return this.mAdapter.enable();
    }

    public Set<BluetoothDevice> getBondedDevices() {
        return this.mAdapter.getBondedDevices();
    }

    public BluetoothLeScanner getBluetoothLeScanner() {
        return this.mAdapter.getBluetoothLeScanner();
    }

    public int getState() {
        return this.mAdapter.getState();
    }

    public int getConnectionState() {
        return this.mAdapter.getConnectionState();
    }

    public synchronized int getBluetoothState() {
        syncBluetoothState();
        return this.mState;
    }

    /* access modifiers changed from: 0000 */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x000e, code lost:
        r1 = r1.mProfileManager;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0010, code lost:
        if (r1 == null) goto L_0x0015;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0012, code lost:
        r1.setBluetoothStateOn();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0015, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x000c, code lost:
        if (r2 != 12) goto L_0x0015;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setBluetoothStateInt(int r2) {
        /*
            r1 = this;
            monitor-enter(r1)
            int r0 = r1.mState     // Catch:{ all -> 0x0016 }
            if (r0 != r2) goto L_0x0007
            monitor-exit(r1)     // Catch:{ all -> 0x0016 }
            return
        L_0x0007:
            r1.mState = r2     // Catch:{ all -> 0x0016 }
            monitor-exit(r1)     // Catch:{ all -> 0x0016 }
            r0 = 12
            if (r2 != r0) goto L_0x0015
            com.android.settingslib.bluetooth.LocalBluetoothProfileManager r1 = r1.mProfileManager
            if (r1 == 0) goto L_0x0015
            r1.setBluetoothStateOn()
        L_0x0015:
            return
        L_0x0016:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0016 }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.bluetooth.LocalBluetoothAdapter.setBluetoothStateInt(int):void");
    }

    /* access modifiers changed from: 0000 */
    public boolean syncBluetoothState() {
        if (this.mAdapter.getState() == this.mState) {
            return false;
        }
        setBluetoothStateInt(this.mAdapter.getState());
        return true;
    }

    public boolean setBluetoothEnabled(boolean z) {
        boolean z2;
        if (z) {
            z2 = this.mAdapter.enable();
        } else {
            z2 = this.mAdapter.disable();
        }
        if (z2) {
            setBluetoothStateInt(z ? 11 : 13);
        } else {
            syncBluetoothState();
        }
        return z2;
    }
}
