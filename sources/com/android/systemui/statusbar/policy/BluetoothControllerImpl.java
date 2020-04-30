package com.android.systemui.statusbar.policy;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import com.android.settingslib.bluetooth.BluetoothCallback;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.bluetooth.CachedBluetoothDevice.Callback;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import com.android.settingslib.bluetooth.LocalBluetoothProfile;
import com.android.settingslib.bluetooth.LocalBluetoothProfileManager.ServiceListener;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.WeakHashMap;

public class BluetoothControllerImpl implements BluetoothController, BluetoothCallback, Callback, ServiceListener {
    private static final boolean DEBUG = Log.isLoggable("BluetoothController", 3);
    private boolean mAudioProfileOnly;
    private final Handler mBgHandler;
    private final WeakHashMap<CachedBluetoothDevice, ActuallyCachedState> mCachedState = new WeakHashMap<>();
    private final List<CachedBluetoothDevice> mConnectedDevices = new ArrayList();
    private int mConnectionState = 0;
    private final int mCurrentUser;
    /* access modifiers changed from: private */
    public boolean mEnabled;
    private final C1632H mHandler;
    private boolean mIsActive;
    private final LocalBluetoothManager mLocalBluetoothManager;
    private int mState;
    private final UserManager mUserManager;

    private static class ActuallyCachedState implements Runnable {
        /* access modifiers changed from: private */
        public int mBondState;
        private final WeakReference<CachedBluetoothDevice> mDevice;
        private final Handler mUiHandler;

        private ActuallyCachedState(CachedBluetoothDevice cachedBluetoothDevice, Handler handler) {
            this.mBondState = 10;
            this.mDevice = new WeakReference<>(cachedBluetoothDevice);
            this.mUiHandler = handler;
        }

        public void run() {
            CachedBluetoothDevice cachedBluetoothDevice = (CachedBluetoothDevice) this.mDevice.get();
            if (cachedBluetoothDevice != null) {
                this.mBondState = cachedBluetoothDevice.getBondState();
                cachedBluetoothDevice.getMaxConnectionState();
                this.mUiHandler.removeMessages(1);
                this.mUiHandler.sendEmptyMessage(1);
            }
        }
    }

    /* renamed from: com.android.systemui.statusbar.policy.BluetoothControllerImpl$H */
    private final class C1632H extends Handler {
        /* access modifiers changed from: private */
        public final ArrayList<BluetoothController.Callback> mCallbacks = new ArrayList<>();

        public C1632H(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                firePairedDevicesChanged();
            } else if (i == 2) {
                fireStateChange();
            } else if (i == 3) {
                this.mCallbacks.add((BluetoothController.Callback) message.obj);
            } else if (i == 4) {
                this.mCallbacks.remove((BluetoothController.Callback) message.obj);
            }
        }

        private void firePairedDevicesChanged() {
            Iterator it = this.mCallbacks.iterator();
            while (it.hasNext()) {
                ((BluetoothController.Callback) it.next()).onBluetoothDevicesChanged();
            }
        }

        private void fireStateChange() {
            Iterator it = this.mCallbacks.iterator();
            while (it.hasNext()) {
                fireStateChange((BluetoothController.Callback) it.next());
            }
        }

        private void fireStateChange(BluetoothController.Callback callback) {
            callback.onBluetoothStateChange(BluetoothControllerImpl.this.mEnabled);
        }
    }

    public void onServiceDisconnected() {
    }

    public BluetoothControllerImpl(Context context, Looper looper, Looper looper2, LocalBluetoothManager localBluetoothManager) {
        this.mLocalBluetoothManager = localBluetoothManager;
        this.mBgHandler = new Handler(looper);
        this.mHandler = new C1632H(looper2);
        LocalBluetoothManager localBluetoothManager2 = this.mLocalBluetoothManager;
        if (localBluetoothManager2 != null) {
            localBluetoothManager2.getEventManager().registerCallback(this);
            this.mLocalBluetoothManager.getProfileManager().addServiceListener(this);
            onBluetoothStateChanged(this.mLocalBluetoothManager.getBluetoothAdapter().getBluetoothState());
        }
        this.mUserManager = (UserManager) context.getSystemService("user");
        this.mCurrentUser = ActivityManager.getCurrentUser();
    }

    public boolean canConfigBluetooth() {
        if (!this.mUserManager.hasUserRestriction("no_config_bluetooth", UserHandle.of(this.mCurrentUser))) {
            if (!this.mUserManager.hasUserRestriction("no_bluetooth", UserHandle.of(this.mCurrentUser))) {
                return true;
            }
        }
        return false;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("BluetoothController state:");
        printWriter.print("  mLocalBluetoothManager=");
        printWriter.println(this.mLocalBluetoothManager);
        if (this.mLocalBluetoothManager != null) {
            printWriter.print("  mEnabled=");
            printWriter.println(this.mEnabled);
            printWriter.print("  mConnectionState=");
            printWriter.println(stateToString(this.mConnectionState));
            printWriter.print("  mAudioProfileOnly=");
            printWriter.println(this.mAudioProfileOnly);
            printWriter.print("  mIsActive=");
            printWriter.println(this.mIsActive);
            printWriter.print("  mConnectedDevices=");
            printWriter.println(this.mConnectedDevices);
            printWriter.print("  mCallbacks.size=");
            printWriter.println(this.mHandler.mCallbacks.size());
            printWriter.println("  Bluetooth Devices:");
            for (CachedBluetoothDevice cachedBluetoothDevice : getDevices()) {
                StringBuilder sb = new StringBuilder();
                sb.append("    ");
                sb.append(getDeviceString(cachedBluetoothDevice));
                printWriter.println(sb.toString());
            }
        }
    }

    private static String stateToString(int i) {
        if (i == 0) {
            return "DISCONNECTED";
        }
        if (i == 1) {
            return "CONNECTING";
        }
        if (i == 2) {
            return "CONNECTED";
        }
        if (i == 3) {
            return "DISCONNECTING";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("UNKNOWN(");
        sb.append(i);
        sb.append(")");
        return sb.toString();
    }

    private String getDeviceString(CachedBluetoothDevice cachedBluetoothDevice) {
        StringBuilder sb = new StringBuilder();
        sb.append(cachedBluetoothDevice.getName());
        String str = " ";
        sb.append(str);
        sb.append(cachedBluetoothDevice.getBondState());
        sb.append(str);
        sb.append(cachedBluetoothDevice.isConnected());
        return sb.toString();
    }

    public int getBondState(CachedBluetoothDevice cachedBluetoothDevice) {
        return getCachedState(cachedBluetoothDevice).mBondState;
    }

    public List<CachedBluetoothDevice> getConnectedDevices() {
        return this.mConnectedDevices;
    }

    public void addCallback(BluetoothController.Callback callback) {
        this.mHandler.obtainMessage(3, callback).sendToTarget();
        this.mHandler.sendEmptyMessage(2);
    }

    public void removeCallback(BluetoothController.Callback callback) {
        this.mHandler.obtainMessage(4, callback).sendToTarget();
    }

    public boolean isBluetoothEnabled() {
        return this.mEnabled;
    }

    public int getBluetoothState() {
        return this.mState;
    }

    public boolean isBluetoothConnected() {
        return this.mConnectionState == 2;
    }

    public boolean isBluetoothConnecting() {
        return this.mConnectionState == 1;
    }

    public boolean isBluetoothAudioProfileOnly() {
        return this.mAudioProfileOnly;
    }

    public boolean isBluetoothAudioActive() {
        return this.mIsActive;
    }

    public void setBluetoothEnabled(boolean z) {
        LocalBluetoothManager localBluetoothManager = this.mLocalBluetoothManager;
        if (localBluetoothManager != null) {
            localBluetoothManager.getBluetoothAdapter().setBluetoothEnabled(z);
        }
    }

    public boolean isBluetoothSupported() {
        return this.mLocalBluetoothManager != null;
    }

    public void connect(CachedBluetoothDevice cachedBluetoothDevice) {
        if (this.mLocalBluetoothManager != null && cachedBluetoothDevice != null) {
            cachedBluetoothDevice.connect(true);
        }
    }

    public void disconnect(CachedBluetoothDevice cachedBluetoothDevice) {
        if (this.mLocalBluetoothManager != null && cachedBluetoothDevice != null) {
            cachedBluetoothDevice.disconnect();
        }
    }

    public String getConnectedDeviceName() {
        if (this.mConnectedDevices.size() == 1) {
            return ((CachedBluetoothDevice) this.mConnectedDevices.get(0)).getName();
        }
        return null;
    }

    public Collection<CachedBluetoothDevice> getDevices() {
        LocalBluetoothManager localBluetoothManager = this.mLocalBluetoothManager;
        if (localBluetoothManager != null) {
            return localBluetoothManager.getCachedDeviceManager().getCachedDevicesCopy();
        }
        return null;
    }

    private void updateConnected() {
        int connectionState = this.mLocalBluetoothManager.getBluetoothAdapter().getConnectionState();
        this.mConnectedDevices.clear();
        for (CachedBluetoothDevice cachedBluetoothDevice : getDevices()) {
            int maxConnectionState = cachedBluetoothDevice.getMaxConnectionState();
            if (maxConnectionState > connectionState) {
                connectionState = maxConnectionState;
            }
            if (cachedBluetoothDevice.isConnected()) {
                this.mConnectedDevices.add(cachedBluetoothDevice);
            }
        }
        if (this.mConnectedDevices.isEmpty() && connectionState == 2) {
            connectionState = 0;
        }
        if (connectionState != this.mConnectionState) {
            this.mConnectionState = connectionState;
            this.mHandler.sendEmptyMessage(2);
        }
        updateAudioProfile();
    }

    private void updateActive() {
        boolean z = false;
        for (CachedBluetoothDevice cachedBluetoothDevice : getDevices()) {
            boolean z2 = true;
            if (!cachedBluetoothDevice.isActiveDevice(1) && !cachedBluetoothDevice.isActiveDevice(2) && !cachedBluetoothDevice.isActiveDevice(21)) {
                z2 = false;
            }
            z |= z2;
        }
        if (this.mIsActive != z) {
            this.mIsActive = z;
            this.mHandler.sendEmptyMessage(2);
        }
    }

    private void updateAudioProfile() {
        boolean z = false;
        boolean z2 = false;
        boolean z3 = false;
        for (CachedBluetoothDevice cachedBluetoothDevice : getDevices()) {
            for (LocalBluetoothProfile localBluetoothProfile : cachedBluetoothDevice.getProfiles()) {
                int profileId = localBluetoothProfile.getProfileId();
                boolean isConnectedProfile = cachedBluetoothDevice.isConnectedProfile(localBluetoothProfile);
                if (profileId == 1 || profileId == 2 || profileId == 21) {
                    z2 |= isConnectedProfile;
                } else {
                    z3 |= isConnectedProfile;
                }
            }
        }
        if (z2 && !z3) {
            z = true;
        }
        if (z != this.mAudioProfileOnly) {
            this.mAudioProfileOnly = z;
            this.mHandler.sendEmptyMessage(2);
        }
    }

    public void onBluetoothStateChanged(int i) {
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("BluetoothStateChanged=");
            sb.append(stateToString(i));
            Log.d("BluetoothController", sb.toString());
        }
        this.mEnabled = i == 12 || i == 11;
        this.mState = i;
        updateConnected();
        this.mHandler.sendEmptyMessage(2);
    }

    public void onDeviceAdded(CachedBluetoothDevice cachedBluetoothDevice) {
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("DeviceAdded=");
            sb.append(cachedBluetoothDevice.getAddress());
            Log.d("BluetoothController", sb.toString());
        }
        cachedBluetoothDevice.registerCallback(this);
        updateConnected();
        this.mHandler.sendEmptyMessage(1);
    }

    public void onDeviceDeleted(CachedBluetoothDevice cachedBluetoothDevice) {
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("DeviceDeleted=");
            sb.append(cachedBluetoothDevice.getAddress());
            Log.d("BluetoothController", sb.toString());
        }
        this.mCachedState.remove(cachedBluetoothDevice);
        updateConnected();
        this.mHandler.sendEmptyMessage(1);
    }

    public void onDeviceBondStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("DeviceBondStateChanged=");
            sb.append(cachedBluetoothDevice.getAddress());
            Log.d("BluetoothController", sb.toString());
        }
        this.mCachedState.remove(cachedBluetoothDevice);
        updateConnected();
        this.mHandler.sendEmptyMessage(1);
    }

    public void onDeviceAttributesChanged() {
        if (DEBUG) {
            Log.d("BluetoothController", "DeviceAttributesChanged");
        }
        updateConnected();
        this.mHandler.sendEmptyMessage(1);
    }

    public void onConnectionStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("ConnectionStateChanged=");
            sb.append(cachedBluetoothDevice.getAddress());
            sb.append(" ");
            sb.append(stateToString(i));
            Log.d("BluetoothController", sb.toString());
        }
        this.mCachedState.remove(cachedBluetoothDevice);
        updateConnected();
        this.mHandler.sendEmptyMessage(2);
    }

    public void onProfileConnectionStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i, int i2) {
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("ProfileConnectionStateChanged=");
            sb.append(cachedBluetoothDevice.getAddress());
            sb.append(" ");
            sb.append(stateToString(i));
            sb.append(" profileId=");
            sb.append(i2);
            Log.d("BluetoothController", sb.toString());
        }
        this.mCachedState.remove(cachedBluetoothDevice);
        updateConnected();
        this.mHandler.sendEmptyMessage(2);
    }

    public void onActiveDeviceChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("ActiveDeviceChanged=");
            sb.append(cachedBluetoothDevice.getAddress());
            sb.append(" profileId=");
            sb.append(i);
            Log.d("BluetoothController", sb.toString());
        }
        updateActive();
        this.mHandler.sendEmptyMessage(2);
    }

    public void onAclConnectionStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("ACLConnectionStateChanged=");
            sb.append(cachedBluetoothDevice.getAddress());
            sb.append(" ");
            sb.append(stateToString(i));
            Log.d("BluetoothController", sb.toString());
        }
        this.mCachedState.remove(cachedBluetoothDevice);
        updateConnected();
        this.mHandler.sendEmptyMessage(2);
    }

    private ActuallyCachedState getCachedState(CachedBluetoothDevice cachedBluetoothDevice) {
        ActuallyCachedState actuallyCachedState = (ActuallyCachedState) this.mCachedState.get(cachedBluetoothDevice);
        if (actuallyCachedState != null) {
            return actuallyCachedState;
        }
        ActuallyCachedState actuallyCachedState2 = new ActuallyCachedState(cachedBluetoothDevice, this.mHandler);
        this.mBgHandler.post(actuallyCachedState2);
        this.mCachedState.put(cachedBluetoothDevice, actuallyCachedState2);
        return actuallyCachedState2;
    }

    public void onServiceConnected() {
        updateConnected();
        this.mHandler.sendEmptyMessage(1);
    }
}
