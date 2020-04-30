package com.android.systemui.statusbar.policy;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager.ActionListener;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import com.android.settingslib.wifi.AccessPoint;
import com.android.settingslib.wifi.WifiTracker;
import com.android.settingslib.wifi.WifiTracker.WifiListener;
import com.android.systemui.statusbar.policy.NetworkController.AccessPointController;
import com.android.systemui.statusbar.policy.NetworkController.AccessPointController.AccessPointCallback;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AccessPointControllerImpl implements AccessPointController, WifiListener {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = Log.isLoggable("AccessPointController", 3);
    private static final int[] ICONS = WifiIcons.WIFI_FULL_ICONS;
    private final ArrayList<AccessPointCallback> mCallbacks = new ArrayList<>();
    private final ActionListener mConnectListener = new ActionListener() {
        public void onSuccess() {
            if (AccessPointControllerImpl.DEBUG) {
                Log.d("AccessPointController", "connect success");
            }
        }

        public void onFailure(int i) {
            if (AccessPointControllerImpl.DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("connect failure reason=");
                sb.append(i);
                Log.d("AccessPointController", sb.toString());
            }
        }
    };
    private final Context mContext;
    private int mCurrentUser;
    private final UserManager mUserManager;
    private final WifiTracker mWifiTracker;

    public void onWifiStateChanged(int i) {
    }

    public AccessPointControllerImpl(Context context) {
        this.mContext = context;
        this.mUserManager = (UserManager) context.getSystemService("user");
        this.mWifiTracker = new WifiTracker(context, this, false, true);
        this.mCurrentUser = ActivityManager.getCurrentUser();
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        super.finalize();
        this.mWifiTracker.onDestroy();
    }

    public boolean canConfigWifi() {
        return !this.mUserManager.hasUserRestriction("no_config_wifi", new UserHandle(this.mCurrentUser));
    }

    public void onUserSwitched(int i) {
        this.mCurrentUser = i;
    }

    public void addAccessPointCallback(AccessPointCallback accessPointCallback) {
        if (accessPointCallback != null && !this.mCallbacks.contains(accessPointCallback)) {
            if (DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("addCallback ");
                sb.append(accessPointCallback);
                Log.d("AccessPointController", sb.toString());
            }
            this.mCallbacks.add(accessPointCallback);
            if (this.mCallbacks.size() == 1) {
                this.mWifiTracker.onStart();
            }
        }
    }

    public void removeAccessPointCallback(AccessPointCallback accessPointCallback) {
        if (accessPointCallback != null) {
            if (DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("removeCallback ");
                sb.append(accessPointCallback);
                Log.d("AccessPointController", sb.toString());
            }
            this.mCallbacks.remove(accessPointCallback);
            if (this.mCallbacks.isEmpty()) {
                this.mWifiTracker.onStop();
            }
        }
    }

    public void scanForAccessPoints() {
        fireAcccessPointsCallback(this.mWifiTracker.getAccessPoints());
    }

    public int getIcon(AccessPoint accessPoint) {
        int level = accessPoint.getLevel();
        int[] iArr = ICONS;
        if (level < 0) {
            level = 0;
        }
        return iArr[level];
    }

    public boolean connect(AccessPoint accessPoint) {
        if (accessPoint == null) {
            return false;
        }
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("connect networkId=");
            sb.append(accessPoint.getConfig().networkId);
            Log.d("AccessPointController", sb.toString());
        }
        if (accessPoint.isSaved()) {
            this.mWifiTracker.getManager().connect(accessPoint.getConfig().networkId, this.mConnectListener);
        } else if (accessPoint.getSecurity() != 0) {
            Intent intent = new Intent("android.settings.WIFI_SETTINGS");
            intent.putExtra("wifi_start_connect_ssid", accessPoint.getSsidStr());
            intent.addFlags(268435456);
            fireSettingsIntentCallback(intent);
            return true;
        } else {
            accessPoint.generateOpenNetworkConfig();
            this.mWifiTracker.getManager().connect(accessPoint.getConfig(), this.mConnectListener);
        }
        return false;
    }

    private void fireSettingsIntentCallback(Intent intent) {
        Iterator it = this.mCallbacks.iterator();
        while (it.hasNext()) {
            ((AccessPointCallback) it.next()).onSettingsActivityTriggered(intent);
        }
    }

    private void fireAcccessPointsCallback(List<AccessPoint> list) {
        Iterator it = this.mCallbacks.iterator();
        while (it.hasNext()) {
            ((AccessPointCallback) it.next()).onAccessPointsChanged(list);
        }
    }

    public void dump(PrintWriter printWriter) {
        this.mWifiTracker.dump(printWriter);
    }

    public void onConnectedChanged() {
        fireAcccessPointsCallback(this.mWifiTracker.getAccessPoints());
    }

    public void onAccessPointsChanged() {
        fireAcccessPointsCallback(this.mWifiTracker.getAccessPoints());
    }
}
