package com.google.android.systemui.columbus;

import com.google.android.systemui.columbus.gates.Gate;
import com.google.android.systemui.columbus.gates.Gate.Listener;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ColumbusService.kt */
public final class ColumbusService$gateListener$1 implements Listener {
    final /* synthetic */ ColumbusService this$0;

    ColumbusService$gateListener$1(ColumbusService columbusService) {
        this.this$0 = columbusService;
    }

    public void onGateChanged(Gate gate) {
        Intrinsics.checkParameterIsNotNull(gate, "gate");
        this.this$0.updateSensorListener();
    }
}
