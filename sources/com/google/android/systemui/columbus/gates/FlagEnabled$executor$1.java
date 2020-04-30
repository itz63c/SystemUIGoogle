package com.google.android.systemui.columbus.gates;

import java.util.concurrent.Executor;

/* compiled from: FlagEnabled.kt */
final class FlagEnabled$executor$1 implements Executor {
    final /* synthetic */ FlagEnabled this$0;

    FlagEnabled$executor$1(FlagEnabled flagEnabled) {
        this.this$0 = flagEnabled;
    }

    public final void execute(Runnable runnable) {
        this.this$0.handler.post(runnable);
    }
}
