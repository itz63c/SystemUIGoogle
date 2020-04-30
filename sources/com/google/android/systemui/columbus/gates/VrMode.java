package com.google.android.systemui.columbus.gates;

import android.content.Context;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.service.vr.IVrManager;
import android.service.vr.IVrManager.Stub;
import android.util.Log;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: VrMode.kt */
public final class VrMode extends Gate {
    /* access modifiers changed from: private */
    public boolean inVrMode;
    private final IVrManager vrManager = Stub.asInterface(ServiceManager.getService("vrmanager"));
    private final VrMode$vrStateCallbacks$1 vrStateCallbacks = new VrMode$vrStateCallbacks$1(this);

    public VrMode(Context context) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        super(context);
    }

    /* access modifiers changed from: protected */
    public void onActivate() {
        IVrManager iVrManager = this.vrManager;
        if (iVrManager != null) {
            try {
                boolean z = true;
                if (!iVrManager.getVrModeState()) {
                    z = false;
                }
                this.inVrMode = z;
                iVrManager.registerListener(this.vrStateCallbacks);
            } catch (RemoteException e) {
                Log.e("Columbus/VrMode", "Could not register IVrManager listener", e);
                this.inVrMode = false;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDeactivate() {
        try {
            IVrManager iVrManager = this.vrManager;
            if (iVrManager != null) {
                iVrManager.unregisterListener(this.vrStateCallbacks);
            }
        } catch (RemoteException e) {
            Log.e("Columbus/VrMode", "Could not unregister IVrManager listener", e);
            this.inVrMode = false;
        }
    }

    /* access modifiers changed from: protected */
    public boolean isBlocked() {
        return this.inVrMode;
    }
}
