package com.google.android.systemui.columbus.actions;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.systemui.columbus.ColumbusServiceProxy;
import com.google.android.systemui.columbus.IColumbusService;
import com.google.android.systemui.columbus.IColumbusService.Stub;
import com.google.android.systemui.columbus.IColumbusServiceGestureListener;
import com.google.android.systemui.columbus.IColumbusServiceListener;
import com.google.android.systemui.columbus.feedback.FeedbackEffect;
import com.google.android.systemui.columbus.sensors.GestureSensor.DetectionProperties;
import java.util.List;
import java.util.NoSuchElementException;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ServiceAction.kt */
public abstract class ServiceAction extends Action implements DeathRecipient {
    /* access modifiers changed from: private */
    public IColumbusService columbusService;
    private final ColumbusServiceConnection columbusServiceConnection = new ColumbusServiceConnection();
    /* access modifiers changed from: private */
    public IColumbusServiceGestureListener columbusServiceGestureListener;
    /* access modifiers changed from: private */
    public final ColumbusServiceListener columbusServiceListener = new ColumbusServiceListener();
    /* access modifiers changed from: private */
    public final IBinder token = new Binder();

    /* compiled from: ServiceAction.kt */
    private final class ColumbusServiceConnection implements ServiceConnection {
        public ColumbusServiceConnection() {
        }

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ServiceAction.this.columbusService = Stub.asInterface(iBinder);
            try {
                IColumbusService access$getColumbusService$p = ServiceAction.this.columbusService;
                if (access$getColumbusService$p != null) {
                    access$getColumbusService$p.registerGestureListener(ServiceAction.this.token, ServiceAction.this.columbusServiceListener);
                }
            } catch (RemoteException e) {
                Log.e("Columbus/ServiceAction", "Error registering listener", e);
            }
            ServiceAction.this.onServiceConnected();
        }

        public void onServiceDisconnected(ComponentName componentName) {
            ServiceAction.this.columbusService = null;
            ServiceAction.this.onServiceDisconnected();
        }
    }

    /* compiled from: ServiceAction.kt */
    private final class ColumbusServiceListener extends IColumbusServiceListener.Stub {
        public ColumbusServiceListener() {
        }

        public void setListener(IBinder iBinder, IBinder iBinder2) {
            if (ServiceAction.this.checkSupportedCaller()) {
                if (iBinder2 != null || ServiceAction.this.columbusServiceGestureListener != null) {
                    IColumbusServiceGestureListener asInterface = IColumbusServiceGestureListener.Stub.asInterface(iBinder2);
                    if (!Intrinsics.areEqual((Object) asInterface, (Object) ServiceAction.this.columbusServiceGestureListener)) {
                        ServiceAction.this.columbusServiceGestureListener = asInterface;
                        ServiceAction.this.notifyListener();
                    }
                    if (iBinder != null) {
                        String str = "Columbus/ServiceAction";
                        if (iBinder2 != null) {
                            try {
                                iBinder.linkToDeath(ServiceAction.this, 0);
                            } catch (RemoteException e) {
                                Log.e(str, "RemoteException during linkToDeath", e);
                            } catch (NoSuchElementException e2) {
                                Log.e(str, "NoSuchElementException during linkToDeath", e2);
                            }
                        } else {
                            iBinder.unlinkToDeath(ServiceAction.this, 0);
                        }
                    }
                }
            }
        }

        public void triggerAction() {
            if (ServiceAction.this.checkSupportedCaller()) {
                ServiceAction.this.triggerAction();
            }
        }
    }

    /* access modifiers changed from: protected */
    public abstract boolean checkSupportedCaller();

    /* access modifiers changed from: protected */
    public void onServiceConnected() {
    }

    /* access modifiers changed from: protected */
    public void onServiceDisconnected() {
    }

    /* access modifiers changed from: protected */
    public abstract void triggerAction();

    public ServiceAction(Context context, List<? extends FeedbackEffect> list) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        super(context, list);
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(context, ColumbusServiceProxy.class));
            context.bindService(intent, this.columbusServiceConnection, 1);
        } catch (SecurityException e) {
            Log.e("Columbus/ServiceAction", "Unable to bind to ColumbusServiceProxy", e);
        }
    }

    public boolean isAvailable() {
        return this.columbusServiceGestureListener != null;
    }

    /* access modifiers changed from: protected */
    public final boolean checkSupportedCaller(String str) {
        Intrinsics.checkParameterIsNotNull(str, "packageName");
        String[] packagesForUid = getContext().getPackageManager().getPackagesForUid(Binder.getCallingUid());
        if (packagesForUid != null) {
            return ArraysKt___ArraysKt.contains(packagesForUid, str);
        }
        return false;
    }

    public void onProgress(int i, DetectionProperties detectionProperties) {
        String str = "Columbus/ServiceAction";
        if (this.columbusServiceGestureListener != null) {
            updateFeedbackEffects(i, detectionProperties);
            try {
                IColumbusServiceGestureListener iColumbusServiceGestureListener = this.columbusServiceGestureListener;
                if (iColumbusServiceGestureListener != null) {
                    iColumbusServiceGestureListener.onGestureProgress(i);
                }
            } catch (DeadObjectException e) {
                Log.e(str, "Listener crashed or closed without unregistering", e);
                this.columbusServiceGestureListener = null;
                notifyListener();
            } catch (RemoteException e2) {
                Log.e(str, "Unable to send progress, setting listener to null", e2);
                this.columbusServiceGestureListener = null;
                notifyListener();
            }
        }
    }

    public void binderDied() {
        Log.w("Columbus/ServiceAction", "Binder died");
        this.columbusServiceGestureListener = null;
        notifyListener();
    }
}
