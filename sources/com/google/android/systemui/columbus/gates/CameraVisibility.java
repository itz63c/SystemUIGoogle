package com.google.android.systemui.columbus.gates;

import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.IActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.systemui.columbus.actions.Action;
import java.util.Iterator;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: CameraVisibility.kt */
public final class CameraVisibility extends Gate {
    private final IActivityManager activityManager;
    private boolean cameraShowing;
    private final List<Action> exceptions;
    private final CameraVisibility$gateListener$1 gateListener;
    private final KeyguardVisibility keyguardGate;
    private final PackageManager packageManager;
    private final PowerState powerState;
    private final CameraVisibility$taskStackListener$1 taskStackListener = new CameraVisibility$taskStackListener$1(this);
    /* access modifiers changed from: private */
    public final Handler updateHandler;

    public CameraVisibility(Context context, List<Action> list, KeyguardVisibility keyguardVisibility, PowerState powerState2, IActivityManager iActivityManager, Handler handler) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(list, "exceptions");
        Intrinsics.checkParameterIsNotNull(keyguardVisibility, "keyguardGate");
        Intrinsics.checkParameterIsNotNull(powerState2, "powerState");
        Intrinsics.checkParameterIsNotNull(iActivityManager, "activityManager");
        Intrinsics.checkParameterIsNotNull(handler, "updateHandler");
        super(context);
        this.exceptions = list;
        this.keyguardGate = keyguardVisibility;
        this.powerState = powerState2;
        this.activityManager = iActivityManager;
        this.updateHandler = handler;
        this.packageManager = context.getPackageManager();
        CameraVisibility$gateListener$1 cameraVisibility$gateListener$1 = new CameraVisibility$gateListener$1(this);
        this.gateListener = cameraVisibility$gateListener$1;
        this.keyguardGate.setListener(cameraVisibility$gateListener$1);
        this.powerState.setListener(this.gateListener);
    }

    /* access modifiers changed from: protected */
    public void onActivate() {
        this.keyguardGate.activate();
        this.powerState.activate();
        this.cameraShowing = isCameraShowing();
        try {
            this.activityManager.registerTaskStackListener(this.taskStackListener);
        } catch (RemoteException e) {
            Log.e("Columbus/CameraVisibility", "Could not register task stack listener", e);
        }
    }

    /* access modifiers changed from: protected */
    public void onDeactivate() {
        this.keyguardGate.deactivate();
        this.powerState.deactivate();
        try {
            this.activityManager.unregisterTaskStackListener(this.taskStackListener);
        } catch (RemoteException e) {
            Log.e("Columbus/CameraVisibility", "Could not unregister task stack listener", e);
        }
    }

    /* access modifiers changed from: protected */
    public boolean isBlocked() {
        Object obj;
        Iterator it = this.exceptions.iterator();
        while (true) {
            if (!it.hasNext()) {
                obj = null;
                break;
            }
            obj = it.next();
            if (((Action) obj).isAvailable()) {
                break;
            }
        }
        if (((Action) obj) == null) {
            return this.cameraShowing;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public final void updateCameraIsShowing() {
        boolean isCameraShowing = isCameraShowing();
        if (this.cameraShowing != isCameraShowing) {
            this.cameraShowing = isCameraShowing;
            notifyListener();
        }
    }

    public final boolean isCameraShowing() {
        return isCameraTopActivity() && isCameraInForeground() && !this.powerState.isBlocking();
    }

    private final boolean isCameraTopActivity() {
        try {
            List tasks = this.activityManager.getTasks(1);
            if (tasks.isEmpty()) {
                return false;
            }
            ComponentName componentName = ((RunningTaskInfo) tasks.get(0)).topActivity;
            Intrinsics.checkExpressionValueIsNotNull(componentName, "topActivityComponent");
            return StringsKt__StringsJVMKt.equals(componentName.getPackageName(), "com.google.android.GoogleCamera", true);
        } catch (RemoteException e) {
            Log.e("Columbus/CameraVisibility", "unable to check task stack", e);
            return false;
        }
    }

    private final boolean isCameraInForeground() {
        Object obj;
        boolean z;
        String str = "com.google.android.GoogleCamera";
        RunningAppProcessInfo runningAppProcessInfo = null;
        try {
            int i = this.packageManager.getApplicationInfoAsUser(str, 0, this.activityManager.getCurrentUser().id).uid;
            List runningAppProcesses = this.activityManager.getRunningAppProcesses();
            Intrinsics.checkExpressionValueIsNotNull(runningAppProcesses, "activityManager.runningAppProcesses");
            Iterator it = runningAppProcesses.iterator();
            while (true) {
                if (!it.hasNext()) {
                    obj = null;
                    break;
                }
                obj = it.next();
                RunningAppProcessInfo runningAppProcessInfo2 = (RunningAppProcessInfo) obj;
                if (runningAppProcessInfo2.uid != i || !StringsKt__StringsJVMKt.equals(runningAppProcessInfo2.processName, str, true)) {
                    z = false;
                    continue;
                } else {
                    z = true;
                    continue;
                }
                if (z) {
                    break;
                }
            }
            runningAppProcessInfo = (RunningAppProcessInfo) obj;
        } catch (NameNotFoundException unused) {
        } catch (RemoteException e) {
            Log.e("Columbus/CameraVisibility", "Could not check camera foreground status", e);
        }
        if (runningAppProcessInfo == null || runningAppProcessInfo.importance != 100) {
            return false;
        }
        return true;
    }
}
