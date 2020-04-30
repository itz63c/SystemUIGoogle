package com.android.systemui.doze;

import android.content.Context;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.Dependency;
import com.android.systemui.doze.DozeMachine.Part;
import com.android.systemui.doze.DozeMachine.State;

public class DozeAuthRemover implements Part {
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor = ((KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class));

    public DozeAuthRemover(Context context) {
    }

    public void transitionTo(State state, State state2) {
        if (state2 == State.DOZE || state2 == State.DOZE_AOD) {
            if (this.mKeyguardUpdateMonitor.getUserUnlockedWithBiometric(KeyguardUpdateMonitor.getCurrentUser())) {
                this.mKeyguardUpdateMonitor.clearBiometricRecognized();
            }
        }
    }
}
