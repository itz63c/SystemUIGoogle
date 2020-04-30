package kotlin.coroutines.jvm.internal;

import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;

/* compiled from: ContinuationImpl.kt */
public final class CompletedContinuation implements Continuation<Object> {
    public static final CompletedContinuation INSTANCE = new CompletedContinuation();

    public String toString() {
        return "This continuation is already complete";
    }

    private CompletedContinuation() {
    }

    public CoroutineContext getContext() {
        throw new IllegalStateException("This continuation is already complete".toString());
    }

    public void resumeWith(Object obj) {
        throw new IllegalStateException("This continuation is already complete".toString());
    }
}
