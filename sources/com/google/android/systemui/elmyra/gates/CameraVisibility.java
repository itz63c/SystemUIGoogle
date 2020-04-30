package com.google.android.systemui.elmyra.gates;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.IActivityManager;
import android.app.TaskStackListener;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.UserInfo;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import com.android.systemui.C2017R$string;
import com.google.android.systemui.elmyra.actions.Action;
import com.google.android.systemui.elmyra.actions.CameraAction;
import com.google.android.systemui.elmyra.gates.Gate.Listener;
import java.util.List;

public class CameraVisibility extends Gate {
    private final IActivityManager mActivityManager;
    private final CameraAction mCameraAction;
    private final String mCameraPackageName;
    private boolean mCameraShowing;
    private final List<Action> mExceptions;
    private final Listener mGateListener = new Listener() {
        public void onGateChanged(Gate gate) {
            CameraVisibility.this.mUpdateHandler.post(new Runnable() {
                public final void run() {
                    CameraVisibility.this.updateCameraIsShowing();
                }
            });
        }
    };
    private final KeyguardVisibility mKeyguardGate;
    private final PackageManager mPackageManager;
    private final PowerState mPowerState;
    private final TaskStackListener mTaskStackListener = new TaskStackListener() {
        public void onTaskStackChanged() {
            CameraVisibility.this.mUpdateHandler.post(new Runnable() {
                public final void run() {
                    CameraVisibility.this.updateCameraIsShowing();
                }
            });
        }
    };
    /* access modifiers changed from: private */
    public final Handler mUpdateHandler;

    public CameraVisibility(Context context, CameraAction cameraAction, List<Action> list) {
        super(context);
        this.mCameraAction = cameraAction;
        this.mExceptions = list;
        this.mPackageManager = context.getPackageManager();
        ActivityManager activityManager = (ActivityManager) context.getSystemService("activity");
        this.mActivityManager = ActivityManager.getService();
        this.mKeyguardGate = new KeyguardVisibility(context);
        this.mPowerState = new PowerState(context);
        this.mKeyguardGate.setListener(this.mGateListener);
        this.mPowerState.setListener(this.mGateListener);
        this.mCameraPackageName = context.getResources().getString(C2017R$string.google_camera_app_package_name);
        this.mUpdateHandler = new Handler(context.getMainLooper());
    }

    /* access modifiers changed from: protected */
    public void onActivate() {
        this.mKeyguardGate.activate();
        this.mPowerState.activate();
        this.mCameraShowing = isCameraShowing();
        try {
            this.mActivityManager.registerTaskStackListener(this.mTaskStackListener);
        } catch (RemoteException e) {
            Log.e("Elmyra/CameraVisibility", "Could not register task stack listener", e);
        }
    }

    /* access modifiers changed from: protected */
    public void onDeactivate() {
        this.mKeyguardGate.deactivate();
        this.mPowerState.deactivate();
        try {
            this.mActivityManager.unregisterTaskStackListener(this.mTaskStackListener);
        } catch (RemoteException e) {
            Log.e("Elmyra/CameraVisibility", "Could not unregister task stack listener", e);
        }
    }

    /* access modifiers changed from: protected */
    public boolean isBlocked() {
        boolean z = false;
        for (int i = 0; i < this.mExceptions.size(); i++) {
            if (((Action) this.mExceptions.get(i)).isAvailable()) {
                return false;
            }
        }
        if (this.mCameraShowing && !this.mCameraAction.isAvailable()) {
            z = true;
        }
        return z;
    }

    /* access modifiers changed from: private */
    public void updateCameraIsShowing() {
        boolean isCameraShowing = isCameraShowing();
        if (this.mCameraShowing != isCameraShowing) {
            this.mCameraShowing = isCameraShowing;
            notifyListener();
        }
    }

    public boolean isCameraShowing() {
        return isCameraTopActivity() && isCameraInForeground() && !this.mPowerState.isBlocking();
    }

    private boolean isCameraTopActivity() {
        try {
            List tasks = ActivityManager.getService().getTasks(1);
            if (tasks.isEmpty()) {
                return false;
            }
            return ((RunningTaskInfo) tasks.get(0)).topActivity.getPackageName().equalsIgnoreCase(this.mCameraPackageName);
        } catch (RemoteException e) {
            Log.e("Elmyra/CameraVisibility", "unable to check task stack", e);
            return false;
        }
    }

    private boolean isCameraInForeground() {
        boolean z = false;
        try {
            UserInfo currentUser = this.mActivityManager.getCurrentUser();
            int i = this.mPackageManager.getApplicationInfoAsUser(this.mCameraPackageName, 0, currentUser != null ? currentUser.id : 0).uid;
            List runningAppProcesses = this.mActivityManager.getRunningAppProcesses();
            int i2 = 0;
            while (i2 < runningAppProcesses.size()) {
                RunningAppProcessInfo runningAppProcessInfo = (RunningAppProcessInfo) runningAppProcesses.get(i2);
                if (runningAppProcessInfo.uid != i || !runningAppProcessInfo.processName.equalsIgnoreCase(this.mCameraPackageName)) {
                    i2++;
                } else {
                    if (runningAppProcessInfo.importance == 100) {
                        z = true;
                    }
                    return z;
                }
            }
        } catch (NameNotFoundException unused) {
        } catch (RemoteException e) {
            Log.e("Elmyra/CameraVisibility", "Could not check camera foreground status", e);
        }
        return false;
    }
}
