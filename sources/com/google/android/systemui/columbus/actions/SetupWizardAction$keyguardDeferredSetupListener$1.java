package com.google.android.systemui.columbus.actions;

import com.google.android.systemui.columbus.gates.Gate;
import com.google.android.systemui.columbus.gates.Gate.Listener;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: SetupWizardAction.kt */
public final class SetupWizardAction$keyguardDeferredSetupListener$1 implements Listener {
    final /* synthetic */ SetupWizardAction this$0;

    SetupWizardAction$keyguardDeferredSetupListener$1(SetupWizardAction setupWizardAction) {
        this.this$0 = setupWizardAction;
    }

    public void onGateChanged(Gate gate) {
        Intrinsics.checkParameterIsNotNull(gate, "gate");
        SetupWizardAction setupWizardAction = this.this$0;
        setupWizardAction.userCompletedSuw = setupWizardAction.keyguardDeferredSetupGate.isSuwComplete();
        this.this$0.notifyListener();
    }
}
