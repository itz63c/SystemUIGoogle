package com.google.android.systemui.columbus.gates;

import com.google.android.systemui.columbus.gates.Gate.Listener;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: KeyguardProximity.kt */
public final class KeyguardProximity$gateListener$1 implements Listener {
    final /* synthetic */ KeyguardProximity this$0;

    KeyguardProximity$gateListener$1(KeyguardProximity keyguardProximity) {
        this.this$0 = keyguardProximity;
    }

    public void onGateChanged(Gate gate) {
        Intrinsics.checkParameterIsNotNull(gate, "gate");
        this.this$0.updateProximityListener();
    }
}
