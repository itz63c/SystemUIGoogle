package com.android.settingslib.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.UserHandle;
import android.util.Log;
import com.android.settingslib.R$string;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class BluetoothEventManager {
    private final IntentFilter mAdapterIntentFilter;
    private final BroadcastReceiver mBroadcastReceiver = new BluetoothBroadcastReceiver();
    /* access modifiers changed from: private */
    public final Collection<BluetoothCallback> mCallbacks = new CopyOnWriteArrayList();
    private final Context mContext;
    /* access modifiers changed from: private */
    public final CachedBluetoothDeviceManager mDeviceManager;
    /* access modifiers changed from: private */
    public final Map<String, Handler> mHandlerMap;
    /* access modifiers changed from: private */
    public final LocalBluetoothAdapter mLocalAdapter;
    private final BroadcastReceiver mProfileBroadcastReceiver = new BluetoothBroadcastReceiver();
    private final IntentFilter mProfileIntentFilter;
    private final android.os.Handler mReceiverHandler;
    private final UserHandle mUserHandle;

    private class AclStateChangedHandler implements Handler {
        private AclStateChangedHandler() {
        }

        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            String str = "BluetoothEventManager";
            if (bluetoothDevice == null) {
                Log.w(str, "AclStateChangedHandler: device is null");
            } else if (!BluetoothEventManager.this.mDeviceManager.isSubDevice(bluetoothDevice)) {
                String action = intent.getAction();
                if (action == null) {
                    Log.w(str, "AclStateChangedHandler: action is null");
                    return;
                }
                CachedBluetoothDevice findDevice = BluetoothEventManager.this.mDeviceManager.findDevice(bluetoothDevice);
                if (findDevice == null) {
                    Log.w(str, "AclStateChangedHandler: activeDevice is null");
                    return;
                }
                char c = 65535;
                int hashCode = action.hashCode();
                int i = 0;
                if (hashCode != -301431627) {
                    if (hashCode == 1821585647 && action.equals("android.bluetooth.device.action.ACL_DISCONNECTED")) {
                        c = 1;
                    }
                } else if (action.equals("android.bluetooth.device.action.ACL_CONNECTED")) {
                    c = 0;
                }
                if (c == 0) {
                    i = 2;
                } else if (c != 1) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("ActiveDeviceChangedHandler: unknown action ");
                    sb.append(action);
                    Log.w(str, sb.toString());
                    return;
                }
                BluetoothEventManager.this.dispatchAclStateChanged(findDevice, i);
            }
        }
    }

    private class ActiveDeviceChangedHandler implements Handler {
        private ActiveDeviceChangedHandler() {
        }

        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            int i;
            String action = intent.getAction();
            String str = "BluetoothEventManager";
            if (action == null) {
                Log.w(str, "ActiveDeviceChangedHandler: action is null");
                return;
            }
            CachedBluetoothDevice findDevice = BluetoothEventManager.this.mDeviceManager.findDevice(bluetoothDevice);
            if (Objects.equals(action, "android.bluetooth.a2dp.profile.action.ACTIVE_DEVICE_CHANGED")) {
                i = 2;
            } else if (Objects.equals(action, "android.bluetooth.headset.profile.action.ACTIVE_DEVICE_CHANGED")) {
                i = 1;
            } else if (Objects.equals(action, "android.bluetooth.hearingaid.profile.action.ACTIVE_DEVICE_CHANGED")) {
                i = 21;
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("ActiveDeviceChangedHandler: unknown action ");
                sb.append(action);
                Log.w(str, sb.toString());
                return;
            }
            BluetoothEventManager.this.dispatchActiveDeviceChanged(findDevice, i);
        }
    }

    private class AdapterStateChangedHandler implements Handler {
        private AdapterStateChangedHandler() {
        }

        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            int intExtra = intent.getIntExtra("android.bluetooth.adapter.extra.STATE", Integer.MIN_VALUE);
            BluetoothEventManager.this.mLocalAdapter.setBluetoothStateInt(intExtra);
            for (BluetoothCallback onBluetoothStateChanged : BluetoothEventManager.this.mCallbacks) {
                onBluetoothStateChanged.onBluetoothStateChanged(intExtra);
            }
            BluetoothEventManager.this.mDeviceManager.onBluetoothStateChanged(intExtra);
        }
    }

    private class AudioModeChangedHandler implements Handler {
        private AudioModeChangedHandler() {
        }

        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            if (intent.getAction() == null) {
                Log.w("BluetoothEventManager", "AudioModeChangedHandler() action is null");
            } else {
                BluetoothEventManager.this.dispatchAudioModeChanged();
            }
        }
    }

    private class BatteryLevelChangedHandler implements Handler {
        private BatteryLevelChangedHandler() {
        }

        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            CachedBluetoothDevice findDevice = BluetoothEventManager.this.mDeviceManager.findDevice(bluetoothDevice);
            if (findDevice != null) {
                findDevice.refresh();
            }
        }
    }

    private class BluetoothBroadcastReceiver extends BroadcastReceiver {
        private BluetoothBroadcastReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            BluetoothDevice bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
            Handler handler = (Handler) BluetoothEventManager.this.mHandlerMap.get(intent.getAction());
            if (handler != null) {
                handler.onReceive(context, intent, bluetoothDevice);
            }
        }
    }

    private class BondStateChangedHandler implements Handler {
        private BondStateChangedHandler() {
        }

        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            String str = "BluetoothEventManager";
            if (bluetoothDevice == null) {
                Log.e(str, "ACTION_BOND_STATE_CHANGED with no EXTRA_DEVICE");
                return;
            }
            int intExtra = intent.getIntExtra("android.bluetooth.device.extra.BOND_STATE", Integer.MIN_VALUE);
            CachedBluetoothDevice findDevice = BluetoothEventManager.this.mDeviceManager.findDevice(bluetoothDevice);
            if (findDevice == null) {
                StringBuilder sb = new StringBuilder();
                sb.append("Got bonding state changed for ");
                sb.append(bluetoothDevice);
                sb.append(", but we have no record of that device.");
                Log.w(str, sb.toString());
                findDevice = BluetoothEventManager.this.mDeviceManager.addDevice(bluetoothDevice);
            }
            for (BluetoothCallback onDeviceBondStateChanged : BluetoothEventManager.this.mCallbacks) {
                onDeviceBondStateChanged.onDeviceBondStateChanged(findDevice, intExtra);
            }
            findDevice.onBondingStateChanged(intExtra);
            if (intExtra == 10) {
                if (findDevice.getHiSyncId() != 0) {
                    BluetoothEventManager.this.mDeviceManager.onDeviceUnpaired(findDevice);
                }
                showUnbondMessage(context, findDevice.getName(), intent.getIntExtra("android.bluetooth.device.extra.REASON", Integer.MIN_VALUE));
            }
        }

        private void showUnbondMessage(Context context, String str, int i) {
            int i2;
            switch (i) {
                case 1:
                    i2 = R$string.bluetooth_pairing_pin_error_message;
                    break;
                case 2:
                    i2 = R$string.bluetooth_pairing_rejected_error_message;
                    break;
                case 4:
                    i2 = R$string.bluetooth_pairing_device_down_error_message;
                    break;
                case 5:
                case 6:
                case 7:
                case 8:
                    i2 = R$string.bluetooth_pairing_error_message;
                    break;
                default:
                    StringBuilder sb = new StringBuilder();
                    sb.append("showUnbondMessage: Not displaying any message for reason: ");
                    sb.append(i);
                    Log.w("BluetoothEventManager", sb.toString());
                    return;
            }
            BluetoothUtils.showError(context, str, i2);
        }
    }

    private class ClassChangedHandler implements Handler {
        private ClassChangedHandler() {
        }

        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            CachedBluetoothDevice findDevice = BluetoothEventManager.this.mDeviceManager.findDevice(bluetoothDevice);
            if (findDevice != null) {
                findDevice.refresh();
            }
        }
    }

    private class ConnectionStateChangedHandler implements Handler {
        private ConnectionStateChangedHandler() {
        }

        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            BluetoothEventManager.this.dispatchConnectionStateChanged(BluetoothEventManager.this.mDeviceManager.findDevice(bluetoothDevice), intent.getIntExtra("android.bluetooth.adapter.extra.CONNECTION_STATE", Integer.MIN_VALUE));
        }
    }

    private class DeviceFoundHandler implements Handler {
        private DeviceFoundHandler() {
        }

        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            short shortExtra = intent.getShortExtra("android.bluetooth.device.extra.RSSI", Short.MIN_VALUE);
            intent.getStringExtra("android.bluetooth.device.extra.NAME");
            CachedBluetoothDevice findDevice = BluetoothEventManager.this.mDeviceManager.findDevice(bluetoothDevice);
            String str = "BluetoothEventManager";
            if (findDevice == null) {
                findDevice = BluetoothEventManager.this.mDeviceManager.addDevice(bluetoothDevice);
                StringBuilder sb = new StringBuilder();
                sb.append("DeviceFoundHandler created new CachedBluetoothDevice: ");
                sb.append(findDevice);
                Log.d(str, sb.toString());
            } else if (findDevice.getBondState() != 12 || findDevice.getDevice().isConnected()) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("DeviceFoundHandler found existing CachedBluetoothDevice:");
                sb2.append(findDevice);
                Log.d(str, sb2.toString());
            } else {
                BluetoothEventManager.this.dispatchDeviceAdded(findDevice);
                StringBuilder sb3 = new StringBuilder();
                sb3.append("DeviceFoundHandler found bonded and not connected device:");
                sb3.append(findDevice);
                Log.d(str, sb3.toString());
            }
            findDevice.setRssi(shortExtra);
            findDevice.setJustDiscovered(true);
        }
    }

    interface Handler {
        void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice);
    }

    private class NameChangedHandler implements Handler {
        private NameChangedHandler() {
        }

        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            BluetoothEventManager.this.mDeviceManager.onDeviceNameUpdated(bluetoothDevice);
        }
    }

    private class ScanningStateChangedHandler implements Handler {
        private final boolean mStarted;

        ScanningStateChangedHandler(boolean z) {
            this.mStarted = z;
        }

        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            for (BluetoothCallback onScanningStateChanged : BluetoothEventManager.this.mCallbacks) {
                onScanningStateChanged.onScanningStateChanged(this.mStarted);
            }
            BluetoothEventManager.this.mDeviceManager.onScanningStateChanged(this.mStarted);
        }
    }

    private class UuidChangedHandler implements Handler {
        private UuidChangedHandler() {
        }

        public void onReceive(Context context, Intent intent, BluetoothDevice bluetoothDevice) {
            CachedBluetoothDevice findDevice = BluetoothEventManager.this.mDeviceManager.findDevice(bluetoothDevice);
            if (findDevice != null) {
                findDevice.onUuidChanged();
            }
        }
    }

    BluetoothEventManager(LocalBluetoothAdapter localBluetoothAdapter, CachedBluetoothDeviceManager cachedBluetoothDeviceManager, Context context, android.os.Handler handler, UserHandle userHandle) {
        this.mLocalAdapter = localBluetoothAdapter;
        this.mDeviceManager = cachedBluetoothDeviceManager;
        this.mAdapterIntentFilter = new IntentFilter();
        this.mProfileIntentFilter = new IntentFilter();
        this.mHandlerMap = new HashMap();
        this.mContext = context;
        this.mUserHandle = userHandle;
        this.mReceiverHandler = handler;
        addHandler("android.bluetooth.adapter.action.STATE_CHANGED", new AdapterStateChangedHandler());
        addHandler("android.bluetooth.adapter.action.CONNECTION_STATE_CHANGED", new ConnectionStateChangedHandler());
        addHandler("android.bluetooth.adapter.action.DISCOVERY_STARTED", new ScanningStateChangedHandler(true));
        addHandler("android.bluetooth.adapter.action.DISCOVERY_FINISHED", new ScanningStateChangedHandler(false));
        addHandler("android.bluetooth.device.action.FOUND", new DeviceFoundHandler());
        addHandler("android.bluetooth.device.action.NAME_CHANGED", new NameChangedHandler());
        addHandler("android.bluetooth.device.action.ALIAS_CHANGED", new NameChangedHandler());
        addHandler("android.bluetooth.device.action.BOND_STATE_CHANGED", new BondStateChangedHandler());
        addHandler("android.bluetooth.device.action.CLASS_CHANGED", new ClassChangedHandler());
        addHandler("android.bluetooth.device.action.UUID", new UuidChangedHandler());
        addHandler("android.bluetooth.device.action.BATTERY_LEVEL_CHANGED", new BatteryLevelChangedHandler());
        addHandler("android.bluetooth.a2dp.profile.action.ACTIVE_DEVICE_CHANGED", new ActiveDeviceChangedHandler());
        addHandler("android.bluetooth.headset.profile.action.ACTIVE_DEVICE_CHANGED", new ActiveDeviceChangedHandler());
        addHandler("android.bluetooth.hearingaid.profile.action.ACTIVE_DEVICE_CHANGED", new ActiveDeviceChangedHandler());
        addHandler("android.bluetooth.headset.profile.action.AUDIO_STATE_CHANGED", new AudioModeChangedHandler());
        addHandler("android.intent.action.PHONE_STATE", new AudioModeChangedHandler());
        addHandler("android.bluetooth.device.action.ACL_CONNECTED", new AclStateChangedHandler());
        addHandler("android.bluetooth.device.action.ACL_DISCONNECTED", new AclStateChangedHandler());
        registerAdapterIntentReceiver();
    }

    public void registerCallback(BluetoothCallback bluetoothCallback) {
        this.mCallbacks.add(bluetoothCallback);
    }

    /* access modifiers changed from: 0000 */
    public void registerProfileIntentReceiver() {
        registerIntentReceiver(this.mProfileBroadcastReceiver, this.mProfileIntentFilter);
    }

    /* access modifiers changed from: 0000 */
    public void registerAdapterIntentReceiver() {
        registerIntentReceiver(this.mBroadcastReceiver, this.mAdapterIntentFilter);
    }

    private void registerIntentReceiver(BroadcastReceiver broadcastReceiver, IntentFilter intentFilter) {
        UserHandle userHandle = this.mUserHandle;
        if (userHandle == null) {
            this.mContext.registerReceiver(broadcastReceiver, intentFilter, null, this.mReceiverHandler);
            return;
        }
        this.mContext.registerReceiverAsUser(broadcastReceiver, userHandle, intentFilter, null, this.mReceiverHandler);
    }

    /* access modifiers changed from: 0000 */
    public void addProfileHandler(String str, Handler handler) {
        this.mHandlerMap.put(str, handler);
        this.mProfileIntentFilter.addAction(str);
    }

    /* access modifiers changed from: 0000 */
    public boolean readPairedDevices() {
        Set<BluetoothDevice> bondedDevices = this.mLocalAdapter.getBondedDevices();
        boolean z = false;
        if (bondedDevices == null) {
            return false;
        }
        for (BluetoothDevice bluetoothDevice : bondedDevices) {
            if (this.mDeviceManager.findDevice(bluetoothDevice) == null) {
                this.mDeviceManager.addDevice(bluetoothDevice);
                z = true;
            }
        }
        return z;
    }

    /* access modifiers changed from: 0000 */
    public void dispatchDeviceAdded(CachedBluetoothDevice cachedBluetoothDevice) {
        for (BluetoothCallback onDeviceAdded : this.mCallbacks) {
            onDeviceAdded.onDeviceAdded(cachedBluetoothDevice);
        }
    }

    /* access modifiers changed from: 0000 */
    public void dispatchDeviceRemoved(CachedBluetoothDevice cachedBluetoothDevice) {
        for (BluetoothCallback onDeviceDeleted : this.mCallbacks) {
            onDeviceDeleted.onDeviceDeleted(cachedBluetoothDevice);
        }
    }

    /* access modifiers changed from: 0000 */
    public void dispatchProfileConnectionStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i, int i2) {
        for (BluetoothCallback onProfileConnectionStateChanged : this.mCallbacks) {
            onProfileConnectionStateChanged.onProfileConnectionStateChanged(cachedBluetoothDevice, i, i2);
        }
    }

    /* access modifiers changed from: private */
    public void dispatchConnectionStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        for (BluetoothCallback onConnectionStateChanged : this.mCallbacks) {
            onConnectionStateChanged.onConnectionStateChanged(cachedBluetoothDevice, i);
        }
    }

    /* access modifiers changed from: private */
    public void dispatchAudioModeChanged() {
        for (CachedBluetoothDevice onAudioModeChanged : this.mDeviceManager.getCachedDevicesCopy()) {
            onAudioModeChanged.onAudioModeChanged();
        }
        for (BluetoothCallback onAudioModeChanged2 : this.mCallbacks) {
            onAudioModeChanged2.onAudioModeChanged();
        }
    }

    /* access modifiers changed from: 0000 */
    public void dispatchActiveDeviceChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        for (CachedBluetoothDevice cachedBluetoothDevice2 : this.mDeviceManager.getCachedDevicesCopy()) {
            cachedBluetoothDevice2.onActiveDeviceChanged(Objects.equals(cachedBluetoothDevice2, cachedBluetoothDevice), i);
        }
        for (BluetoothCallback onActiveDeviceChanged : this.mCallbacks) {
            onActiveDeviceChanged.onActiveDeviceChanged(cachedBluetoothDevice, i);
        }
    }

    /* access modifiers changed from: private */
    public void dispatchAclStateChanged(CachedBluetoothDevice cachedBluetoothDevice, int i) {
        for (BluetoothCallback onAclConnectionStateChanged : this.mCallbacks) {
            onAclConnectionStateChanged.onAclConnectionStateChanged(cachedBluetoothDevice, i);
        }
    }

    /* access modifiers changed from: 0000 */
    public void addHandler(String str, Handler handler) {
        this.mHandlerMap.put(str, handler);
        this.mAdapterIntentFilter.addAction(str);
    }
}
