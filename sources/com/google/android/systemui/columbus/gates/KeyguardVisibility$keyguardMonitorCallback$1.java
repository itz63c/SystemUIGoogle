package com.google.android.systemui.columbus.gates;

import com.android.systemui.statusbar.policy.KeyguardStateController.Callback;

/* compiled from: KeyguardVisibility.kt */
public final class KeyguardVisibility$keyguardMonitorCallback$1 implements Callback {
    final /* synthetic */ KeyguardVisibility this$0;

    KeyguardVisibility$keyguardMonitorCallback$1(KeyguardVisibility keyguardVisibility) {
        this.this$0 = keyguardVisibility;
    }

    public void onKeyguardShowingChanged() {
        this.this$0.notifyListener();
    }
}
