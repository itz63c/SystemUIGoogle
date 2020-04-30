package com.google.android.systemui.elmyra.gates;

import android.content.Context;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.service.vr.IVrManager;
import android.service.vr.IVrManager.Stub;
import android.service.vr.IVrStateCallbacks;
import android.util.Log;

public class VrMode extends Gate {
    /* access modifiers changed from: private */
    public boolean mInVrMode;
    private final IVrManager mVrManager = Stub.asInterface(ServiceManager.getService("vrmanager"));
    private final IVrStateCallbacks mVrStateCallbacks = new IVrStateCallbacks.Stub() {
        public void onVrStateChanged(boolean z) {
            if (z != VrMode.this.mInVrMode) {
                VrMode.this.mInVrMode = z;
                VrMode.this.notifyListener();
            }
        }
    };

    public VrMode(Context context) {
        super(context);
    }

    /* access modifiers changed from: protected */
    public void onActivate() {
        IVrManager iVrManager = this.mVrManager;
        if (iVrManager != null) {
            try {
                this.mInVrMode = iVrManager.getVrModeState();
                this.mVrManager.registerListener(this.mVrStateCallbacks);
            } catch (RemoteException e) {
                Log.e("Elmyra/VrMode", "Could not register IVrManager listener", e);
                this.mInVrMode = false;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onDeactivate() {
        IVrManager iVrManager = this.mVrManager;
        if (iVrManager != null) {
            try {
                iVrManager.unregisterListener(this.mVrStateCallbacks);
            } catch (RemoteException e) {
                Log.e("Elmyra/VrMode", "Could not unregister IVrManager listener", e);
                this.mInVrMode = false;
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean isBlocked() {
        return this.mInVrMode;
    }
}
