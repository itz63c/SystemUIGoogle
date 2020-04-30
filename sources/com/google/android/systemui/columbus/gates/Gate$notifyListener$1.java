package com.google.android.systemui.columbus.gates;

import com.google.android.systemui.columbus.gates.Gate.Listener;

/* compiled from: Gate.kt */
final class Gate$notifyListener$1 implements Runnable {
    final /* synthetic */ Gate this$0;

    Gate$notifyListener$1(Gate gate) {
        this.this$0 = gate;
    }

    public final void run() {
        Listener listener = this.this$0.getListener();
        if (listener != null) {
            listener.onGateChanged(this.this$0);
        }
    }
}
