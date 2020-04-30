package com.google.android.systemui.columbus.gates;

import android.content.Context;
import android.os.Handler;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: TransientGate.kt */
public abstract class TransientGate extends Gate {
    /* access modifiers changed from: private */
    public boolean blocking;
    private final Function0<Unit> resetGate = new TransientGate$resetGate$1(this);
    private final Handler resetGateHandler;

    public TransientGate(Context context, Handler handler) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(handler, "resetGateHandler");
        super(context);
        this.resetGateHandler = handler;
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [kotlin.jvm.functions.Function0<kotlin.Unit>, kotlin.jvm.functions.Function0] */
    /* JADX WARNING: type inference failed for: r1v1 */
    /* JADX WARNING: type inference failed for: r3v1, types: [kotlin.jvm.functions.Function0<kotlin.Unit>, kotlin.jvm.functions.Function0] */
    /* JADX WARNING: type inference failed for: r3v2 */
    /* JADX WARNING: type inference failed for: r1v3, types: [com.google.android.systemui.columbus.gates.TransientGate$sam$java_lang_Runnable$0] */
    /* JADX WARNING: type inference failed for: r3v4 */
    /* JADX WARNING: type inference failed for: r2v0, types: [com.google.android.systemui.columbus.gates.TransientGate$sam$java_lang_Runnable$0] */
    /* JADX WARNING: type inference failed for: r1v4 */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 4 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void blockForMillis(long r4) {
        /*
            r3 = this;
            r0 = 1
            r3.blocking = r0
            r3.notifyListener()
            android.os.Handler r0 = r3.resetGateHandler
            kotlin.jvm.functions.Function0<kotlin.Unit> r1 = r3.resetGate
            if (r1 == 0) goto L_0x0012
            com.google.android.systemui.columbus.gates.TransientGate$sam$java_lang_Runnable$0 r2 = new com.google.android.systemui.columbus.gates.TransientGate$sam$java_lang_Runnable$0
            r2.<init>(r1)
            r1 = r2
        L_0x0012:
            java.lang.Runnable r1 = (java.lang.Runnable) r1
            r0.removeCallbacks(r1)
            android.os.Handler r0 = r3.resetGateHandler
            kotlin.jvm.functions.Function0<kotlin.Unit> r3 = r3.resetGate
            if (r3 == 0) goto L_0x0023
            com.google.android.systemui.columbus.gates.TransientGate$sam$java_lang_Runnable$0 r1 = new com.google.android.systemui.columbus.gates.TransientGate$sam$java_lang_Runnable$0
            r1.<init>(r3)
            r3 = r1
        L_0x0023:
            java.lang.Runnable r3 = (java.lang.Runnable) r3
            r0.postDelayed(r3, r4)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.systemui.columbus.gates.TransientGate.blockForMillis(long):void");
    }

    /* access modifiers changed from: protected */
    public boolean isBlocked() {
        return this.blocking;
    }
}
