package com.android.systemui.statusbar.phone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.om.IOverlayManager;
import android.content.om.IOverlayManager.Stub;
import android.content.om.OverlayInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.ApkAssets;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.provider.Settings.Secure;
import android.util.Log;
import android.util.SparseBooleanArray;
import com.android.systemui.Dumpable;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import com.android.systemui.statusbar.policy.DeviceProvisionedController.DeviceProvisionedListener;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executor;

public class NavigationModeController implements Dumpable {
    /* access modifiers changed from: private */
    public static final String TAG = "NavigationModeController";
    private final Context mContext;
    private Context mCurrentUserContext;
    private final DeviceProvisionedListener mDeviceProvisionedCallback = new DeviceProvisionedListener() {
        public void onDeviceProvisionedChanged() {
            String access$000 = NavigationModeController.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("onDeviceProvisionedChanged: ");
            sb.append(NavigationModeController.this.mDeviceProvisionedController.isDeviceProvisioned());
            Log.d(access$000, sb.toString());
            NavigationModeController.this.restoreGesturalNavOverlayIfNecessary();
        }

        public void onUserSetupChanged() {
            String access$000 = NavigationModeController.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("onUserSetupChanged: ");
            sb.append(NavigationModeController.this.mDeviceProvisionedController.isCurrentUserSetup());
            Log.d(access$000, sb.toString());
            NavigationModeController.this.restoreGesturalNavOverlayIfNecessary();
        }

        public void onUserSwitched() {
            String access$000 = NavigationModeController.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("onUserSwitched: ");
            sb.append(ActivityManagerWrapper.getInstance().getCurrentUserId());
            Log.d(access$000, sb.toString());
            NavigationModeController.this.updateCurrentInteractionMode(true);
            NavigationModeController.this.deferGesturalNavOverlayIfNecessary();
        }
    };
    /* access modifiers changed from: private */
    public final DeviceProvisionedController mDeviceProvisionedController;
    private ArrayList<ModeChangedListener> mListeners = new ArrayList<>();
    private final IOverlayManager mOverlayManager;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (((action.hashCode() == -1946981856 && action.equals("android.intent.action.OVERLAY_CHANGED")) ? (char) 0 : 65535) == 0) {
                Log.d(NavigationModeController.TAG, "ACTION_OVERLAY_CHANGED");
                NavigationModeController.this.updateCurrentInteractionMode(true);
            }
        }
    };
    private SparseBooleanArray mRestoreGesturalNavBarMode = new SparseBooleanArray();
    private final Executor mUiBgExecutor;

    public interface ModeChangedListener {
        void onNavigationModeChanged(int i);
    }

    public NavigationModeController(Context context, DeviceProvisionedController deviceProvisionedController, Executor executor) {
        this.mContext = context;
        this.mCurrentUserContext = context;
        this.mOverlayManager = Stub.asInterface(ServiceManager.getService("overlay"));
        this.mUiBgExecutor = executor;
        this.mDeviceProvisionedController = deviceProvisionedController;
        deviceProvisionedController.addCallback(this.mDeviceProvisionedCallback);
        IntentFilter intentFilter = new IntentFilter("android.intent.action.OVERLAY_CHANGED");
        intentFilter.addDataScheme("package");
        intentFilter.addDataSchemeSpecificPart("android", 0);
        this.mContext.registerReceiverAsUser(this.mReceiver, UserHandle.ALL, intentFilter, null, null);
        this.mContext.registerReceiverAsUser(this.mReceiver, UserHandle.ALL, new IntentFilter("android.intent.action.ACTION_PREFERRED_ACTIVITY_CHANGED"), null, null);
        updateCurrentInteractionMode(false);
        deferGesturalNavOverlayIfNecessary();
    }

    private boolean setGestureModeOverlayForMainLauncher() {
        if (getCurrentInteractionMode(this.mCurrentUserContext) == 2) {
            return true;
        }
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Switching system navigation to full-gesture mode: contextUser=");
        sb.append(this.mCurrentUserContext.getUserId());
        Log.d(str, sb.toString());
        setModeOverlay("com.android.internal.systemui.navbar.gestural", -2);
        return true;
    }

    public void updateCurrentInteractionMode(boolean z) {
        Context currentUserContext = getCurrentUserContext();
        this.mCurrentUserContext = currentUserContext;
        int currentInteractionMode = getCurrentInteractionMode(currentUserContext);
        if (currentInteractionMode == 2) {
            switchToDefaultGestureNavOverlayIfNecessary();
        }
        this.mUiBgExecutor.execute(new Runnable(currentInteractionMode) {
            public final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                NavigationModeController.this.lambda$updateCurrentInteractionMode$0$NavigationModeController(this.f$1);
            }
        });
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("updateCurrentInteractionMode: mode=");
        sb.append(currentInteractionMode);
        Log.e(str, sb.toString());
        dumpAssetPaths(this.mCurrentUserContext);
        if (z) {
            for (int i = 0; i < this.mListeners.size(); i++) {
                ((ModeChangedListener) this.mListeners.get(i)).onNavigationModeChanged(currentInteractionMode);
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateCurrentInteractionMode$0 */
    public /* synthetic */ void lambda$updateCurrentInteractionMode$0$NavigationModeController(int i) {
        Secure.putString(this.mCurrentUserContext.getContentResolver(), "navigation_mode", String.valueOf(i));
    }

    public int addListener(ModeChangedListener modeChangedListener) {
        this.mListeners.add(modeChangedListener);
        return getCurrentInteractionMode(this.mCurrentUserContext);
    }

    public void removeListener(ModeChangedListener modeChangedListener) {
        this.mListeners.remove(modeChangedListener);
    }

    private int getCurrentInteractionMode(Context context) {
        int integer = context.getResources().getInteger(17694849);
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("getCurrentInteractionMode: mode=");
        sb.append(integer);
        sb.append(" contextUser=");
        sb.append(context.getUserId());
        Log.d(str, sb.toString());
        return integer;
    }

    public Context getCurrentUserContext() {
        int currentUserId = ActivityManagerWrapper.getInstance().getCurrentUserId();
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("getCurrentUserContext: contextUser=");
        sb.append(this.mContext.getUserId());
        sb.append(" currentUser=");
        sb.append(currentUserId);
        Log.d(str, sb.toString());
        if (this.mContext.getUserId() == currentUserId) {
            return this.mContext;
        }
        try {
            return this.mContext.createPackageContextAsUser(this.mContext.getPackageName(), 0, UserHandle.of(currentUserId));
        } catch (NameNotFoundException e) {
            Log.e(TAG, "Failed to create package context", e);
            return null;
        }
    }

    /* access modifiers changed from: private */
    public void deferGesturalNavOverlayIfNecessary() {
        int currentUser = this.mDeviceProvisionedController.getCurrentUser();
        this.mRestoreGesturalNavBarMode.put(currentUser, false);
        if (!this.mDeviceProvisionedController.isDeviceProvisioned() || !this.mDeviceProvisionedController.isCurrentUserSetup()) {
            ArrayList arrayList = new ArrayList();
            try {
                arrayList.addAll(Arrays.asList(this.mOverlayManager.getDefaultOverlayPackages()));
            } catch (RemoteException unused) {
                Log.e(TAG, "deferGesturalNavOverlayIfNecessary: failed to fetch default overlays");
            }
            if (!arrayList.contains("com.android.internal.systemui.navbar.gestural")) {
                String str = TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("deferGesturalNavOverlayIfNecessary: no default gestural overlay, default=");
                sb.append(arrayList);
                Log.d(str, sb.toString());
                return;
            }
            setModeOverlay("com.android.internal.systemui.navbar.threebutton", -2);
            this.mRestoreGesturalNavBarMode.put(currentUser, true);
            Log.d(TAG, "deferGesturalNavOverlayIfNecessary: setting to 3 button mode");
            return;
        }
        Log.d(TAG, "deferGesturalNavOverlayIfNecessary: device is provisioned and user is setup");
    }

    /* access modifiers changed from: private */
    public void restoreGesturalNavOverlayIfNecessary() {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("restoreGesturalNavOverlayIfNecessary: needs restore=");
        sb.append(this.mRestoreGesturalNavBarMode);
        Log.d(str, sb.toString());
        int currentUser = this.mDeviceProvisionedController.getCurrentUser();
        if (this.mRestoreGesturalNavBarMode.get(currentUser)) {
            setGestureModeOverlayForMainLauncher();
            this.mRestoreGesturalNavBarMode.put(currentUser, false);
        }
    }

    private void switchToDefaultGestureNavOverlayIfNecessary() {
        String str = "com.android.internal.systemui.navbar.gestural";
        int userId = this.mCurrentUserContext.getUserId();
        try {
            IOverlayManager iOverlayManager = this.mOverlayManager;
            OverlayInfo overlayInfo = iOverlayManager.getOverlayInfo(str, userId);
            if (overlayInfo != null && !overlayInfo.isEnabled()) {
                int dimensionPixelSize = this.mCurrentUserContext.getResources().getDimensionPixelSize(17105053);
                iOverlayManager.setEnabledExclusiveInCategory(str, userId);
                int dimensionPixelSize2 = this.mCurrentUserContext.getResources().getDimensionPixelSize(17105053);
                float f = dimensionPixelSize2 == 0 ? 1.0f : ((float) dimensionPixelSize) / ((float) dimensionPixelSize2);
                Secure.putFloat(this.mCurrentUserContext.getContentResolver(), "back_gesture_inset_scale_left", f);
                Secure.putFloat(this.mCurrentUserContext.getContentResolver(), "back_gesture_inset_scale_right", f);
                String str2 = TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("Moved back sensitivity for user ");
                sb.append(userId);
                sb.append(" to scale ");
                sb.append(f);
                Log.v(str2, sb.toString());
            }
        } catch (RemoteException | IllegalStateException | SecurityException unused) {
            String str3 = TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Failed to switch to default gesture nav overlay for user ");
            sb2.append(userId);
            Log.e(str3, sb2.toString());
        }
    }

    public void setModeOverlay(String str, int i) {
        this.mUiBgExecutor.execute(new Runnable(str, i) {
            public final /* synthetic */ String f$1;
            public final /* synthetic */ int f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                NavigationModeController.this.lambda$setModeOverlay$1$NavigationModeController(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setModeOverlay$1 */
    public /* synthetic */ void lambda$setModeOverlay$1$NavigationModeController(String str, int i) {
        try {
            this.mOverlayManager.setEnabledExclusiveInCategory(str, i);
            String str2 = TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("setModeOverlay: overlayPackage=");
            sb.append(str);
            sb.append(" userId=");
            sb.append(i);
            Log.d(str2, sb.toString());
        } catch (RemoteException | IllegalStateException | SecurityException unused) {
            String str3 = TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Failed to enable overlay ");
            sb2.append(str);
            sb2.append(" for user ");
            sb2.append(i);
            Log.e(str3, sb2.toString());
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        String str;
        printWriter.println("NavigationModeController:");
        StringBuilder sb = new StringBuilder();
        sb.append("  mode=");
        sb.append(getCurrentInteractionMode(this.mCurrentUserContext));
        printWriter.println(sb.toString());
        try {
            str = String.join(", ", this.mOverlayManager.getDefaultOverlayPackages());
        } catch (RemoteException unused) {
            str = "failed_to_fetch";
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append("  defaultOverlays=");
        sb2.append(str);
        printWriter.println(sb2.toString());
        printWriter.println("  restoreGesturalNavMode:");
        for (int i = 0; i < this.mRestoreGesturalNavBarMode.size(); i++) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append("    userId=");
            sb3.append(this.mRestoreGesturalNavBarMode.keyAt(i));
            sb3.append(" shouldRestore=");
            sb3.append(this.mRestoreGesturalNavBarMode.valueAt(i));
            printWriter.println(sb3.toString());
        }
        dumpAssetPaths(this.mCurrentUserContext);
    }

    private void dumpAssetPaths(Context context) {
        ApkAssets[] apkAssets;
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("  contextUser=");
        sb.append(this.mCurrentUserContext.getUserId());
        Log.d(str, sb.toString());
        Log.d(TAG, "  assetPaths=");
        for (ApkAssets apkAssets2 : context.getResources().getAssets().getApkAssets()) {
            String str2 = TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("    ");
            sb2.append(apkAssets2.getAssetPath());
            Log.d(str2, sb2.toString());
        }
    }
}
