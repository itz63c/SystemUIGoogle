package com.android.settingslib.media;

import android.app.Notification;
import android.content.Context;
import android.media.MediaRoute2Info;
import android.media.MediaRouter2Manager;
import android.media.MediaRouter2Manager.Callback;
import android.media.RoutingSessionInfo;
import android.text.TextUtils;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settingslib.bluetooth.LocalBluetoothManager;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class InfoMediaManager extends MediaManager {
    private LocalBluetoothManager mBluetoothManager;
    /* access modifiers changed from: private */
    public MediaDevice mCurrentConnectedDevice;
    @VisibleForTesting
    final Executor mExecutor = Executors.newSingleThreadExecutor();
    @VisibleForTesting
    final RouterManagerCallback mMediaRouterCallback = new RouterManagerCallback();
    @VisibleForTesting
    String mPackageName;
    @VisibleForTesting
    MediaRouter2Manager mRouterManager;

    class RouterManagerCallback extends Callback {
        public void onTransferred(RoutingSessionInfo routingSessionInfo, RoutingSessionInfo routingSessionInfo2) {
        }

        RouterManagerCallback() {
        }

        public void onRoutesAdded(List<MediaRoute2Info> list) {
            InfoMediaManager.this.refreshDevices();
        }

        public void onControlCategoriesChanged(String str, List<String> list) {
            if (TextUtils.equals(InfoMediaManager.this.mPackageName, str)) {
                InfoMediaManager.this.refreshDevices();
            }
        }

        public void onRoutesChanged(List<MediaRoute2Info> list) {
            InfoMediaManager.this.mMediaDevices.clear();
            String str = null;
            InfoMediaManager.this.mCurrentConnectedDevice = null;
            if (TextUtils.isEmpty(InfoMediaManager.this.mPackageName)) {
                InfoMediaManager.this.buildAllRoutes();
            } else {
                InfoMediaManager.this.buildAvailableRoutes();
            }
            if (InfoMediaManager.this.mCurrentConnectedDevice != null) {
                str = InfoMediaManager.this.mCurrentConnectedDevice.getId();
            }
            InfoMediaManager.this.dispatchConnectedDeviceChanged(str);
        }

        public void onRoutesRemoved(List<MediaRoute2Info> list) {
            InfoMediaManager.this.refreshDevices();
        }

        public void onTransferFailed(RoutingSessionInfo routingSessionInfo, MediaRoute2Info mediaRoute2Info) {
            InfoMediaManager.this.dispatchOnRequestFailed(0);
        }

        public void onRequestFailed(int i) {
            InfoMediaManager.this.dispatchOnRequestFailed(i);
        }
    }

    public InfoMediaManager(Context context, String str, Notification notification, LocalBluetoothManager localBluetoothManager) {
        super(context, notification);
        this.mRouterManager = MediaRouter2Manager.getInstance(context);
        this.mBluetoothManager = localBluetoothManager;
        if (!TextUtils.isEmpty(str)) {
            this.mPackageName = str;
        }
    }

    public void startScan() {
        this.mMediaDevices.clear();
        this.mRouterManager.registerCallback(this.mExecutor, this.mMediaRouterCallback);
        refreshDevices();
    }

    public void stopScan() {
        this.mRouterManager.unregisterCallback(this.mMediaRouterCallback);
    }

    /* access modifiers changed from: 0000 */
    public MediaDevice getCurrentConnectedDevice() {
        return this.mCurrentConnectedDevice;
    }

    /* access modifiers changed from: private */
    public void refreshDevices() {
        this.mMediaDevices.clear();
        this.mCurrentConnectedDevice = null;
        if (TextUtils.isEmpty(this.mPackageName)) {
            buildAllRoutes();
        } else {
            buildAvailableRoutes();
        }
        dispatchDeviceListAdded();
    }

    /* access modifiers changed from: private */
    public void buildAllRoutes() {
        for (MediaRoute2Info mediaRoute2Info : this.mRouterManager.getAllRoutes()) {
            if (mediaRoute2Info.isSystemRoute()) {
                addMediaDevice(mediaRoute2Info);
            }
        }
    }

    /* access modifiers changed from: private */
    public void buildAvailableRoutes() {
        for (MediaRoute2Info addMediaDevice : this.mRouterManager.getAvailableRoutes(this.mPackageName)) {
            addMediaDevice(addMediaDevice);
        }
    }

    /* JADX WARNING: type inference failed for: r9v1, types: [java.lang.Object] */
    /* JADX WARNING: type inference failed for: r0v1 */
    /* JADX WARNING: type inference failed for: r9v2 */
    /* JADX WARNING: type inference failed for: r0v2, types: [com.android.settingslib.media.MediaDevice, com.android.settingslib.media.InfoMediaDevice] */
    /* JADX WARNING: type inference failed for: r2v1, types: [com.android.settingslib.media.BluetoothMediaDevice] */
    /* JADX WARNING: type inference failed for: r0v6, types: [com.android.settingslib.media.PhoneMediaDevice] */
    /* JADX WARNING: type inference failed for: r9v8 */
    /* JADX WARNING: type inference failed for: r2v3, types: [com.android.settingslib.media.BluetoothMediaDevice] */
    /* JADX WARNING: type inference failed for: r0v8 */
    /* JADX WARNING: type inference failed for: r9v9 */
    /* JADX WARNING: Multi-variable type inference failed. Error: jadx.core.utils.exceptions.JadxRuntimeException: No candidate types for var: r0v2, types: [com.android.settingslib.media.MediaDevice, com.android.settingslib.media.InfoMediaDevice]
      assigns: [com.android.settingslib.media.InfoMediaDevice, com.android.settingslib.media.BluetoothMediaDevice]
      uses: [com.android.settingslib.media.MediaDevice, ?[OBJECT, ARRAY], com.android.settingslib.media.BluetoothMediaDevice]
      mth insns count: 56
    	at jadx.core.dex.visitors.typeinference.TypeSearch.fillTypeCandidates(TypeSearch.java:237)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.run(TypeSearch.java:53)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runMultiVariableSearch(TypeInferenceVisitor.java:99)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:92)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
    	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
    	at jadx.core.ProcessClass.process(ProcessClass.java:30)
    	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:311)
    	at jadx.api.JavaClass.decompile(JavaClass.java:62)
    	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:217)
     */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0093  */
    /* JADX WARNING: Removed duplicated region for block: B:32:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Unknown variable types count: 5 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void addMediaDevice(android.media.MediaRoute2Info r9) {
        /*
            r8 = this;
            int r0 = r9.getType()
            if (r0 == 0) goto L_0x006b
            r1 = 8
            if (r0 == r1) goto L_0x0047
            r1 = 23
            if (r0 == r1) goto L_0x0047
            r1 = 2000(0x7d0, float:2.803E-42)
            if (r0 == r1) goto L_0x006b
            r1 = 2
            if (r0 == r1) goto L_0x003b
            r1 = 3
            if (r0 == r1) goto L_0x003b
            r1 = 4
            if (r0 == r1) goto L_0x003b
            r1 = 1001(0x3e9, float:1.403E-42)
            if (r0 == r1) goto L_0x006b
            r1 = 1002(0x3ea, float:1.404E-42)
            if (r0 == r1) goto L_0x006b
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r1 = "addMediaDevice() unknown device type : "
            r9.append(r1)
            r9.append(r0)
            java.lang.String r9 = r9.toString()
            java.lang.String r0 = "InfoMediaManager"
            android.util.Log.w(r0, r9)
            r9 = 0
            goto L_0x0091
        L_0x003b:
            com.android.settingslib.media.PhoneMediaDevice r0 = new com.android.settingslib.media.PhoneMediaDevice
            android.content.Context r1 = r8.mContext
            android.media.MediaRouter2Manager r2 = r8.mRouterManager
            java.lang.String r3 = r8.mPackageName
            r0.<init>(r1, r2, r9, r3)
            goto L_0x0090
        L_0x0047:
            android.bluetooth.BluetoothAdapter r0 = android.bluetooth.BluetoothAdapter.getDefaultAdapter()
            java.lang.String r1 = r9.getOriginalId()
            android.bluetooth.BluetoothDevice r0 = r0.getRemoteDevice(r1)
            com.android.settingslib.bluetooth.LocalBluetoothManager r1 = r8.mBluetoothManager
            com.android.settingslib.bluetooth.CachedBluetoothDeviceManager r1 = r1.getCachedDeviceManager()
            com.android.settingslib.bluetooth.CachedBluetoothDevice r4 = r1.findDevice(r0)
            com.android.settingslib.media.BluetoothMediaDevice r0 = new com.android.settingslib.media.BluetoothMediaDevice
            android.content.Context r3 = r8.mContext
            android.media.MediaRouter2Manager r5 = r8.mRouterManager
            java.lang.String r7 = r8.mPackageName
            r2 = r0
            r6 = r9
            r2.<init>(r3, r4, r5, r6, r7)
            goto L_0x0090
        L_0x006b:
            com.android.settingslib.media.InfoMediaDevice r0 = new com.android.settingslib.media.InfoMediaDevice
            android.content.Context r1 = r8.mContext
            android.media.MediaRouter2Manager r2 = r8.mRouterManager
            java.lang.String r3 = r8.mPackageName
            r0.<init>(r1, r2, r9, r3)
            java.lang.String r1 = r8.mPackageName
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0090
            java.lang.String r9 = r9.getClientPackageName()
            java.lang.String r1 = r8.mPackageName
            boolean r9 = android.text.TextUtils.equals(r9, r1)
            if (r9 == 0) goto L_0x0090
            com.android.settingslib.media.MediaDevice r9 = r8.mCurrentConnectedDevice
            if (r9 != 0) goto L_0x0090
            r8.mCurrentConnectedDevice = r0
        L_0x0090:
            r9 = r0
        L_0x0091:
            if (r9 == 0) goto L_0x0098
            java.util.List<com.android.settingslib.media.MediaDevice> r8 = r8.mMediaDevices
            r8.add(r9)
        L_0x0098:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.media.InfoMediaManager.addMediaDevice(android.media.MediaRoute2Info):void");
    }
}
