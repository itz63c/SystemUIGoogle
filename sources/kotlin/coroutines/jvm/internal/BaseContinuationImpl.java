package kotlin.coroutines.jvm.internal;

import kotlin.Result;
import kotlin.Result.Companion;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ContinuationImpl.kt */
public abstract class BaseContinuationImpl implements Continuation<Object>, Object {
    private final Continuation<Object> completion;

    /* access modifiers changed from: protected */
    public abstract Object invokeSuspend(Object obj);

    /* access modifiers changed from: protected */
    public void releaseIntercepted() {
    }

    public BaseContinuationImpl(Continuation<Object> continuation) {
        this.completion = continuation;
    }

    public final Continuation<Object> getCompletion() {
        return this.completion;
    }

    public final void resumeWith(Object obj) {
        while (true) {
            DebugProbesKt.probeCoroutineResumed(this);
            Continuation<Object> continuation = this.completion;
            if (continuation != null) {
                try {
                    obj = this.invokeSuspend(obj);
                    if (obj != IntrinsicsKt__IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
                        Companion companion = Result.Companion;
                        Result.m171constructorimpl(obj);
                        this.releaseIntercepted();
                        if (continuation instanceof BaseContinuationImpl) {
                            this = (BaseContinuationImpl) continuation;
                        } else {
                            continuation.resumeWith(obj);
                            return;
                        }
                    } else {
                        return;
                    }
                } catch (Throwable th) {
                    Companion companion2 = Result.Companion;
                    obj = ResultKt.createFailure(th);
                    Result.m171constructorimpl(obj);
                }
            } else {
                Intrinsics.throwNpe();
                throw null;
            }
        }
    }

    public Continuation<Unit> create(Object obj, Continuation<?> continuation) {
        Intrinsics.checkParameterIsNotNull(continuation, "completion");
        throw new UnsupportedOperationException("create(Any?;Continuation) has not been overridden");
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Continuation at ");
        Object stackTraceElement = getStackTraceElement();
        if (stackTraceElement == null) {
            stackTraceElement = getClass().getName();
        }
        sb.append(stackTraceElement);
        return sb.toString();
    }

    public StackTraceElement getStackTraceElement() {
        return DebugMetadataKt.getStackTraceElement(this);
    }
}
