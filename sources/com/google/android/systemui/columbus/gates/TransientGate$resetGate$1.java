package com.google.android.systemui.columbus.gates;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;

/* compiled from: TransientGate.kt */
final class TransientGate$resetGate$1 extends Lambda implements Function0<Unit> {
    final /* synthetic */ TransientGate this$0;

    TransientGate$resetGate$1(TransientGate transientGate) {
        this.this$0 = transientGate;
        super(0);
    }

    public final void invoke() {
        this.this$0.blocking = false;
        this.this$0.notifyListener();
    }
}
