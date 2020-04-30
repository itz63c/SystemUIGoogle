package com.google.android.systemui.columbus.gates;

import com.android.systemui.keyguard.WakefulnessLifecycle.Observer;

/* compiled from: PowerState.kt */
public final class PowerState$wakefulnessLifecycleObserver$1 implements Observer {
    final /* synthetic */ PowerState this$0;

    PowerState$wakefulnessLifecycleObserver$1(PowerState powerState) {
        this.this$0 = powerState;
    }

    public void onFinishedGoingToSleep() {
        this.this$0.notifyListener();
    }

    public void onStartedWakingUp() {
        this.this$0.notifyListener();
    }
}
