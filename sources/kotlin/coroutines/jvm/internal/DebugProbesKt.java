package kotlin.coroutines.jvm.internal;

import kotlin.coroutines.Continuation;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: DebugProbes.kt */
public final class DebugProbesKt {
    public static final <T> Continuation<T> probeCoroutineCreated(Continuation<? super T> continuation) {
        Intrinsics.checkParameterIsNotNull(continuation, "completion");
        return continuation;
    }

    public static final void probeCoroutineResumed(Continuation<?> continuation) {
        Intrinsics.checkParameterIsNotNull(continuation, "frame");
    }

    public static final void probeCoroutineSuspended(Continuation<?> continuation) {
        Intrinsics.checkParameterIsNotNull(continuation, "frame");
    }
}
