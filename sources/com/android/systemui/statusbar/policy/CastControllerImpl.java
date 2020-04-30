package com.android.systemui.statusbar.policy;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.MediaRouter;
import android.media.MediaRouter.RouteInfo;
import android.media.MediaRouter.SimpleCallback;
import android.media.projection.MediaProjectionInfo;
import android.media.projection.MediaProjectionManager;
import android.os.Handler;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import com.android.systemui.C2017R$string;
import com.android.systemui.statusbar.policy.CastController.Callback;
import com.android.systemui.statusbar.policy.CastController.CastDevice;
import com.android.systemui.util.Utils;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CastControllerImpl implements CastController {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = Log.isLoggable("CastController", 3);
    private boolean mCallbackRegistered;
    @GuardedBy({"mCallbacks"})
    private final ArrayList<Callback> mCallbacks = new ArrayList<>();
    private final Context mContext;
    private boolean mDiscovering;
    private final Object mDiscoveringLock = new Object();
    private final SimpleCallback mMediaCallback = new SimpleCallback() {
        public void onRouteAdded(MediaRouter mediaRouter, RouteInfo routeInfo) {
            if (CastControllerImpl.DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("onRouteAdded: ");
                sb.append(CastControllerImpl.routeToString(routeInfo));
                Log.d("CastController", sb.toString());
            }
            CastControllerImpl.this.updateRemoteDisplays();
        }

        public void onRouteChanged(MediaRouter mediaRouter, RouteInfo routeInfo) {
            if (CastControllerImpl.DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("onRouteChanged: ");
                sb.append(CastControllerImpl.routeToString(routeInfo));
                Log.d("CastController", sb.toString());
            }
            CastControllerImpl.this.updateRemoteDisplays();
        }

        public void onRouteRemoved(MediaRouter mediaRouter, RouteInfo routeInfo) {
            if (CastControllerImpl.DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("onRouteRemoved: ");
                sb.append(CastControllerImpl.routeToString(routeInfo));
                Log.d("CastController", sb.toString());
            }
            CastControllerImpl.this.updateRemoteDisplays();
        }

        public void onRouteSelected(MediaRouter mediaRouter, int i, RouteInfo routeInfo) {
            if (CastControllerImpl.DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("onRouteSelected(");
                sb.append(i);
                sb.append("): ");
                sb.append(CastControllerImpl.routeToString(routeInfo));
                Log.d("CastController", sb.toString());
            }
            CastControllerImpl.this.updateRemoteDisplays();
        }

        public void onRouteUnselected(MediaRouter mediaRouter, int i, RouteInfo routeInfo) {
            if (CastControllerImpl.DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("onRouteUnselected(");
                sb.append(i);
                sb.append("): ");
                sb.append(CastControllerImpl.routeToString(routeInfo));
                Log.d("CastController", sb.toString());
            }
            CastControllerImpl.this.updateRemoteDisplays();
        }
    };
    private final MediaRouter mMediaRouter;
    private MediaProjectionInfo mProjection;
    private final MediaProjectionManager.Callback mProjectionCallback = new MediaProjectionManager.Callback() {
        public void onStart(MediaProjectionInfo mediaProjectionInfo) {
            CastControllerImpl.this.setProjection(mediaProjectionInfo, true);
        }

        public void onStop(MediaProjectionInfo mediaProjectionInfo) {
            CastControllerImpl.this.setProjection(mediaProjectionInfo, false);
        }
    };
    private final Object mProjectionLock = new Object();
    private final MediaProjectionManager mProjectionManager;
    private final ArrayMap<String, RouteInfo> mRoutes = new ArrayMap<>();

    public CastControllerImpl(Context context) {
        this.mContext = context;
        MediaRouter mediaRouter = (MediaRouter) context.getSystemService("media_router");
        this.mMediaRouter = mediaRouter;
        mediaRouter.setRouterGroupId("android.media.mirroring_group");
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) context.getSystemService("media_projection");
        this.mProjectionManager = mediaProjectionManager;
        this.mProjection = mediaProjectionManager.getActiveProjectionInfo();
        this.mProjectionManager.addCallback(this.mProjectionCallback, new Handler());
        if (DEBUG) {
            Log.d("CastController", "new CastController()");
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("CastController state:");
        printWriter.print("  mDiscovering=");
        printWriter.println(this.mDiscovering);
        printWriter.print("  mCallbackRegistered=");
        printWriter.println(this.mCallbackRegistered);
        printWriter.print("  mCallbacks.size=");
        synchronized (this.mCallbacks) {
            printWriter.println(this.mCallbacks.size());
        }
        printWriter.print("  mRoutes.size=");
        printWriter.println(this.mRoutes.size());
        for (int i = 0; i < this.mRoutes.size(); i++) {
            RouteInfo routeInfo = (RouteInfo) this.mRoutes.valueAt(i);
            printWriter.print("    ");
            printWriter.println(routeToString(routeInfo));
        }
        printWriter.print("  mProjection=");
        printWriter.println(this.mProjection);
    }

    public void addCallback(Callback callback) {
        synchronized (this.mCallbacks) {
            this.mCallbacks.add(callback);
        }
        fireOnCastDevicesChanged(callback);
        synchronized (this.mDiscoveringLock) {
            handleDiscoveryChangeLocked();
        }
    }

    public void removeCallback(Callback callback) {
        synchronized (this.mCallbacks) {
            this.mCallbacks.remove(callback);
        }
        synchronized (this.mDiscoveringLock) {
            handleDiscoveryChangeLocked();
        }
    }

    public void setDiscovering(boolean z) {
        synchronized (this.mDiscoveringLock) {
            if (this.mDiscovering != z) {
                this.mDiscovering = z;
                if (DEBUG) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("setDiscovering: ");
                    sb.append(z);
                    Log.d("CastController", sb.toString());
                }
                handleDiscoveryChangeLocked();
            }
        }
    }

    private void handleDiscoveryChangeLocked() {
        boolean isEmpty;
        if (this.mCallbackRegistered) {
            this.mMediaRouter.removeCallback(this.mMediaCallback);
            this.mCallbackRegistered = false;
        }
        if (this.mDiscovering) {
            this.mMediaRouter.addCallback(4, this.mMediaCallback, 4);
            this.mCallbackRegistered = true;
            return;
        }
        synchronized (this.mCallbacks) {
            isEmpty = this.mCallbacks.isEmpty();
        }
        if (!isEmpty) {
            this.mMediaRouter.addCallback(4, this.mMediaCallback, 8);
            this.mCallbackRegistered = true;
        }
    }

    public void setCurrentUserId(int i) {
        this.mMediaRouter.rebindAsUser(i);
    }

    public List<CastDevice> getCastDevices() {
        ArrayList arrayList = new ArrayList();
        synchronized (this.mRoutes) {
            for (RouteInfo routeInfo : this.mRoutes.values()) {
                CastDevice castDevice = new CastDevice();
                castDevice.f79id = routeInfo.getTag().toString();
                CharSequence name = routeInfo.getName(this.mContext);
                castDevice.name = name != null ? name.toString() : null;
                CharSequence description = routeInfo.getDescription();
                if (description != null) {
                    description.toString();
                }
                int statusCode = routeInfo.getStatusCode();
                if (statusCode == 2) {
                    castDevice.state = 1;
                } else {
                    if (!routeInfo.isSelected()) {
                        if (statusCode != 6) {
                            castDevice.state = 0;
                        }
                    }
                    castDevice.state = 2;
                }
                castDevice.tag = routeInfo;
                arrayList.add(castDevice);
            }
        }
        synchronized (this.mProjectionLock) {
            if (this.mProjection != null) {
                CastDevice castDevice2 = new CastDevice();
                castDevice2.f79id = this.mProjection.getPackageName();
                castDevice2.name = getAppName(this.mProjection.getPackageName());
                this.mContext.getString(C2017R$string.quick_settings_casting);
                castDevice2.state = 2;
                castDevice2.tag = this.mProjection;
                arrayList.add(castDevice2);
            }
        }
        return arrayList;
    }

    public void startCasting(CastDevice castDevice) {
        if (castDevice != null) {
            Object obj = castDevice.tag;
            if (obj != null) {
                RouteInfo routeInfo = (RouteInfo) obj;
                if (DEBUG) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("startCasting: ");
                    sb.append(routeToString(routeInfo));
                    Log.d("CastController", sb.toString());
                }
                this.mMediaRouter.selectRoute(4, routeInfo);
            }
        }
    }

    public void stopCasting(CastDevice castDevice) {
        boolean z = castDevice.tag instanceof MediaProjectionInfo;
        String str = "CastController";
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("stopCasting isProjection=");
            sb.append(z);
            Log.d(str, sb.toString());
        }
        if (z) {
            MediaProjectionInfo mediaProjectionInfo = (MediaProjectionInfo) castDevice.tag;
            if (Objects.equals(this.mProjectionManager.getActiveProjectionInfo(), mediaProjectionInfo)) {
                this.mProjectionManager.stopActiveProjection();
                return;
            }
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Projection is no longer active: ");
            sb2.append(mediaProjectionInfo);
            Log.w(str, sb2.toString());
            return;
        }
        this.mMediaRouter.getFallbackRoute().select();
    }

    /* access modifiers changed from: private */
    public void setProjection(MediaProjectionInfo mediaProjectionInfo, boolean z) {
        boolean z2;
        MediaProjectionInfo mediaProjectionInfo2 = this.mProjection;
        synchronized (this.mProjectionLock) {
            boolean equals = Objects.equals(mediaProjectionInfo, this.mProjection);
            z2 = true;
            if (z && !equals) {
                this.mProjection = mediaProjectionInfo;
            } else if (z || !equals) {
                z2 = false;
            } else {
                this.mProjection = null;
            }
        }
        if (z2) {
            if (DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("setProjection: ");
                sb.append(mediaProjectionInfo2);
                sb.append(" -> ");
                sb.append(this.mProjection);
                Log.d("CastController", sb.toString());
            }
            fireOnCastDevicesChanged();
        }
    }

    private String getAppName(String str) {
        String str2 = "CastController";
        PackageManager packageManager = this.mContext.getPackageManager();
        if (Utils.isHeadlessRemoteDisplayProvider(packageManager, str)) {
            return "";
        }
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(str, 0);
            if (applicationInfo != null) {
                CharSequence loadLabel = applicationInfo.loadLabel(packageManager);
                if (!TextUtils.isEmpty(loadLabel)) {
                    return loadLabel.toString();
                }
            }
            StringBuilder sb = new StringBuilder();
            sb.append("No label found for package: ");
            sb.append(str);
            Log.w(str2, sb.toString());
        } catch (NameNotFoundException e) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Error getting appName for package: ");
            sb2.append(str);
            Log.w(str2, sb2.toString(), e);
        }
        return str;
    }

    /* access modifiers changed from: private */
    public void updateRemoteDisplays() {
        synchronized (this.mRoutes) {
            this.mRoutes.clear();
            int routeCount = this.mMediaRouter.getRouteCount();
            for (int i = 0; i < routeCount; i++) {
                RouteInfo routeAt = this.mMediaRouter.getRouteAt(i);
                if (routeAt.isEnabled()) {
                    if (routeAt.matchesTypes(4)) {
                        ensureTagExists(routeAt);
                        this.mRoutes.put(routeAt.getTag().toString(), routeAt);
                    }
                }
            }
            RouteInfo selectedRoute = this.mMediaRouter.getSelectedRoute(4);
            if (selectedRoute != null && !selectedRoute.isDefault()) {
                ensureTagExists(selectedRoute);
                this.mRoutes.put(selectedRoute.getTag().toString(), selectedRoute);
            }
        }
        fireOnCastDevicesChanged();
    }

    private void ensureTagExists(RouteInfo routeInfo) {
        if (routeInfo.getTag() == null) {
            routeInfo.setTag(UUID.randomUUID().toString());
        }
    }

    /* access modifiers changed from: 0000 */
    public void fireOnCastDevicesChanged() {
        synchronized (this.mCallbacks) {
            Iterator it = this.mCallbacks.iterator();
            while (it.hasNext()) {
                fireOnCastDevicesChanged((Callback) it.next());
            }
        }
    }

    private void fireOnCastDevicesChanged(Callback callback) {
        callback.onCastDevicesChanged();
    }

    /* access modifiers changed from: private */
    public static String routeToString(RouteInfo routeInfo) {
        if (routeInfo == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(routeInfo.getName());
        sb.append('/');
        sb.append(routeInfo.getDescription());
        sb.append('@');
        sb.append(routeInfo.getDeviceAddress());
        sb.append(",status=");
        sb.append(routeInfo.getStatus());
        if (routeInfo.isDefault()) {
            sb.append(",default");
        }
        if (routeInfo.isEnabled()) {
            sb.append(",enabled");
        }
        if (routeInfo.isConnecting()) {
            sb.append(",connecting");
        }
        if (routeInfo.isSelected()) {
            sb.append(",selected");
        }
        sb.append(",id=");
        sb.append(routeInfo.getTag());
        return sb.toString();
    }
}
