package com.google.android.systemui.elmyra.actions;

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
import com.google.android.systemui.elmyra.ElmyraServiceProxy;
import com.google.android.systemui.elmyra.IElmyraService;
import com.google.android.systemui.elmyra.IElmyraService.Stub;
import com.google.android.systemui.elmyra.IElmyraServiceGestureListener;
import com.google.android.systemui.elmyra.IElmyraServiceListener;
import com.google.android.systemui.elmyra.feedback.FeedbackEffect;
import com.google.android.systemui.elmyra.sensors.GestureSensor.DetectionProperties;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

public abstract class ServiceAction extends Action implements DeathRecipient {
    /* access modifiers changed from: private */
    public IElmyraService mElmyraService;
    private final ElmyraServiceConnection mElmyraServiceConnection = new ElmyraServiceConnection();
    /* access modifiers changed from: private */
    public IElmyraServiceGestureListener mElmyraServiceGestureListener;
    /* access modifiers changed from: private */
    public final ElmyraServiceListener mElmyraServiceListener = new ElmyraServiceListener();
    /* access modifiers changed from: private */
    public final IBinder mToken = new Binder();

    private class ElmyraServiceConnection implements ServiceConnection {
        private ElmyraServiceConnection() {
        }

        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ServiceAction.this.mElmyraService = Stub.asInterface(iBinder);
            try {
                ServiceAction.this.mElmyraService.registerServiceListener(ServiceAction.this.mToken, ServiceAction.this.mElmyraServiceListener);
            } catch (RemoteException e) {
                Log.e("Elmyra/ServiceAction", "Error registering listener", e);
            }
            ServiceAction.this.onServiceConnected();
        }

        public void onServiceDisconnected(ComponentName componentName) {
            ServiceAction.this.mElmyraService = null;
            ServiceAction.this.onServiceDisconnected();
        }
    }

    private class ElmyraServiceListener extends IElmyraServiceListener.Stub {
        private ElmyraServiceListener() {
        }

        public void setListener(IBinder iBinder, IBinder iBinder2) {
            if (ServiceAction.this.checkSupportedCaller()) {
                if (iBinder2 != null || ServiceAction.this.mElmyraServiceGestureListener != null) {
                    IElmyraServiceGestureListener asInterface = IElmyraServiceGestureListener.Stub.asInterface(iBinder2);
                    if (asInterface != ServiceAction.this.mElmyraServiceGestureListener) {
                        ServiceAction.this.mElmyraServiceGestureListener = asInterface;
                        ServiceAction.this.notifyListener();
                    }
                    if (iBinder != null) {
                        if (iBinder2 != null) {
                            try {
                                iBinder.linkToDeath(ServiceAction.this, 0);
                            } catch (RemoteException e) {
                                Log.e("Elmyra/ServiceAction", "RemoteException during linkToDeath", e);
                            } catch (NoSuchElementException unused) {
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
    public void triggerAction() {
    }

    public ServiceAction(Context context, List<FeedbackEffect> list) {
        super(context, list);
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(getContext(), ElmyraServiceProxy.class));
            getContext().bindService(intent, this.mElmyraServiceConnection, 1);
        } catch (SecurityException e) {
            Log.e("Elmyra/ServiceAction", "Unable to bind to ElmyraServiceProxy", e);
        }
    }

    public boolean isAvailable() {
        return this.mElmyraServiceGestureListener != null;
    }

    /* access modifiers changed from: protected */
    public boolean checkSupportedCaller(String str) {
        String[] packagesForUid = getContext().getPackageManager().getPackagesForUid(Binder.getCallingUid());
        if (packagesForUid == null) {
            return false;
        }
        return Arrays.asList(packagesForUid).contains(str);
    }

    public void onTrigger(DetectionProperties detectionProperties) {
        String str = "Elmyra/ServiceAction";
        if (this.mElmyraServiceGestureListener != null) {
            triggerFeedbackEffects(detectionProperties);
            try {
                this.mElmyraServiceGestureListener.onGestureDetected();
            } catch (DeadObjectException e) {
                Log.e(str, "Listener crashed or closed without unregistering", e);
                this.mElmyraServiceGestureListener = null;
                notifyListener();
            } catch (RemoteException e2) {
                Log.e(str, "Unable to send onGestureDetected; removing listener", e2);
                this.mElmyraServiceGestureListener = null;
                notifyListener();
            }
        }
    }

    public void onProgress(float f, int i) {
        String str = "Elmyra/ServiceAction";
        if (this.mElmyraServiceGestureListener != null) {
            updateFeedbackEffects(f, i);
            try {
                this.mElmyraServiceGestureListener.onGestureProgress(f, i);
            } catch (DeadObjectException e) {
                Log.e(str, "Listener crashed or closed without unregistering", e);
                this.mElmyraServiceGestureListener = null;
                notifyListener();
            } catch (RemoteException e2) {
                Log.e(str, "Unable to send progress, setting listener to null", e2);
                this.mElmyraServiceGestureListener = null;
                notifyListener();
            }
        }
    }

    public void binderDied() {
        Log.w("Elmyra/ServiceAction", "Binder died");
        this.mElmyraServiceGestureListener = null;
        notifyListener();
    }
}
