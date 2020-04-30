package com.google.android.systemui.columbus.gates;

import android.service.vr.IVrStateCallbacks.Stub;

/* compiled from: VrMode.kt */
public final class VrMode$vrStateCallbacks$1 extends Stub {
    final /* synthetic */ VrMode this$0;

    VrMode$vrStateCallbacks$1(VrMode vrMode) {
        this.this$0 = vrMode;
    }

    public void onVrStateChanged(boolean z) {
        if (z != this.this$0.inVrMode) {
            this.this$0.inVrMode = z;
            this.this$0.notifyListener();
        }
    }
}
