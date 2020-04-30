package com.google.android.systemui.columbus.gates;

import com.google.android.systemui.columbus.gates.Gate.Listener;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: CameraVisibility.kt */
public final class CameraVisibility$gateListener$1 implements Listener {
    final /* synthetic */ CameraVisibility this$0;

    CameraVisibility$gateListener$1(CameraVisibility cameraVisibility) {
        this.this$0 = cameraVisibility;
    }

    public void onGateChanged(Gate gate) {
        Intrinsics.checkParameterIsNotNull(gate, "gate");
        this.this$0.updateHandler.post(new CameraVisibility$gateListener$1$onGateChanged$1(this));
    }
}
