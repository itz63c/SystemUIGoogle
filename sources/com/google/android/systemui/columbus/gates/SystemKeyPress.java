package com.google.android.systemui.columbus.gates;

import android.content.Context;
import android.os.Handler;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.CommandQueue.Callbacks;
import java.util.Set;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: SystemKeyPress.kt */
public final class SystemKeyPress extends TransientGate {
    /* access modifiers changed from: private */
    public final Set<Integer> blockingKeys;
    private final CommandQueue commandQueue;
    private final SystemKeyPress$commandQueueCallbacks$1 commandQueueCallbacks = new SystemKeyPress$commandQueueCallbacks$1(this);
    /* access modifiers changed from: private */
    public final long gateDuration;

    public SystemKeyPress(Context context, Handler handler, CommandQueue commandQueue2, long j, Set<Integer> set) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(handler, "handler");
        Intrinsics.checkParameterIsNotNull(commandQueue2, "commandQueue");
        Intrinsics.checkParameterIsNotNull(set, "blockingKeys");
        super(context, handler);
        this.commandQueue = commandQueue2;
        this.gateDuration = j;
        this.blockingKeys = set;
    }

    /* access modifiers changed from: protected */
    public void onActivate() {
        this.commandQueue.addCallback((Callbacks) this.commandQueueCallbacks);
    }

    /* access modifiers changed from: protected */
    public void onDeactivate() {
        this.commandQueue.removeCallback((Callbacks) this.commandQueueCallbacks);
    }
}
