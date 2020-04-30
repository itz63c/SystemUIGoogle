package com.google.android.systemui.columbus;

import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.systemui.columbus.IColumbusService.Stub;
import kotlin.jvm.functions.Function1;

/* compiled from: ColumbusServiceProxy.kt */
public final class ColumbusServiceProxy$binder$1 extends Stub {
    final /* synthetic */ ColumbusServiceProxy this$0;

    ColumbusServiceProxy$binder$1(ColumbusServiceProxy columbusServiceProxy) {
        this.this$0 = columbusServiceProxy;
    }

    public void registerGestureListener(IBinder iBinder, IBinder iBinder2) {
        this.this$0.checkPermission();
        try {
            for (int size = this.this$0.columbusServiceListeners.size() - 1; size >= 0; size--) {
                IColumbusServiceListener listener = ((ColumbusServiceListener) this.this$0.columbusServiceListeners.get(size)).getListener();
                if (listener == null) {
                    this.this$0.columbusServiceListeners.remove(size);
                } else {
                    listener.setListener(iBinder, iBinder2);
                }
            }
        } catch (RemoteException e) {
            Log.e("Columbus/ColumbusServiceProxy", "Action isn't connected", e);
        }
    }

    public void triggerAction() {
        this.this$0.checkPermission();
        try {
            for (int size = this.this$0.columbusServiceListeners.size() - 1; size >= 0; size--) {
                IColumbusServiceListener listener = ((ColumbusServiceListener) this.this$0.columbusServiceListeners.get(size)).getListener();
                if (listener == null) {
                    this.this$0.columbusServiceListeners.remove(size);
                } else {
                    listener.triggerAction();
                }
            }
        } catch (RemoteException e) {
            Log.e("Columbus/ColumbusServiceProxy", "Error launching assistant", e);
        }
    }

    public void registerServiceListener(IBinder iBinder, IBinder iBinder2) {
        this.this$0.checkPermission();
        if (iBinder == null) {
            Log.e("Columbus/ColumbusServiceProxy", "Binder token must not be null");
            return;
        }
        if (iBinder2 == null) {
            CollectionsKt__MutableCollectionsKt.removeAll(this.this$0.columbusServiceListeners, (Function1) new ColumbusServiceProxy$binder$1$registerServiceListener$1(iBinder));
        } else {
            this.this$0.columbusServiceListeners.add(new ColumbusServiceListener(iBinder, IColumbusServiceListener.Stub.asInterface(iBinder2)));
        }
    }
}
