package com.google.android.systemui.columbus.gates;

import com.google.android.systemui.columbus.gates.Gate.Listener;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: NavigationBarVisibility.kt */
public final class NavigationBarVisibility$gateListener$1 implements Listener {
    final /* synthetic */ NavigationBarVisibility this$0;

    NavigationBarVisibility$gateListener$1(NavigationBarVisibility navigationBarVisibility) {
        this.this$0 = navigationBarVisibility;
    }

    public void onGateChanged(Gate gate) {
        Intrinsics.checkParameterIsNotNull(gate, "gate");
        if (Intrinsics.areEqual((Object) gate, (Object) this.this$0.keyguardGate)) {
            this.this$0.updateKeyguardState();
        } else if (Intrinsics.areEqual((Object) gate, (Object) this.this$0.navigationModeGate)) {
            this.this$0.updateNavigationModeState();
        }
    }
}
