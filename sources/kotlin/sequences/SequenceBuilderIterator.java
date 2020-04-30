package kotlin.sequences;

import java.util.Iterator;
import java.util.NoSuchElementException;
import kotlin.Result;
import kotlin.Result.Companion;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlin.coroutines.jvm.internal.DebugProbesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.markers.KMappedMarker;

/* compiled from: SequenceBuilder.kt */
final class SequenceBuilderIterator<T> extends SequenceScope<T> implements Iterator<T>, Continuation<Unit>, KMappedMarker {
    private Iterator<? extends T> nextIterator;
    private Continuation<? super Unit> nextStep;
    private T nextValue;
    private int state;

    public void remove() {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }

    public final void setNextStep(Continuation<? super Unit> continuation) {
        this.nextStep = continuation;
    }

    public boolean hasNext() {
        while (true) {
            int i = this.state;
            if (i != 0) {
                if (i == 1) {
                    Iterator<? extends T> it = this.nextIterator;
                    if (it == null) {
                        Intrinsics.throwNpe();
                        throw null;
                    } else if (it.hasNext()) {
                        this.state = 2;
                        return true;
                    } else {
                        this.nextIterator = null;
                    }
                } else if (i == 2 || i == 3) {
                    return true;
                } else {
                    if (i == 4) {
                        return false;
                    }
                    throw exceptionalState();
                }
            }
            this.state = 5;
            Continuation<? super Unit> continuation = this.nextStep;
            if (continuation != null) {
                this.nextStep = null;
                Unit unit = Unit.INSTANCE;
                Companion companion = Result.Companion;
                Result.m171constructorimpl(unit);
                continuation.resumeWith(unit);
            } else {
                Intrinsics.throwNpe();
                throw null;
            }
        }
    }

    public T next() {
        int i = this.state;
        if (i == 0 || i == 1) {
            return nextNotReady();
        }
        if (i == 2) {
            this.state = 1;
            Iterator<? extends T> it = this.nextIterator;
            if (it != null) {
                return it.next();
            }
            Intrinsics.throwNpe();
            throw null;
        } else if (i == 3) {
            this.state = 0;
            T t = this.nextValue;
            this.nextValue = null;
            return t;
        } else {
            throw exceptionalState();
        }
    }

    private final T nextNotReady() {
        if (hasNext()) {
            return next();
        }
        throw new NoSuchElementException();
    }

    private final Throwable exceptionalState() {
        int i = this.state;
        if (i == 4) {
            return new NoSuchElementException();
        }
        if (i == 5) {
            return new IllegalStateException("Iterator has failed.");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Unexpected state of the iterator: ");
        sb.append(this.state);
        return new IllegalStateException(sb.toString());
    }

    public Object yield(T t, Continuation<? super Unit> continuation) {
        this.nextValue = t;
        this.state = 3;
        this.nextStep = continuation;
        Object coroutine_suspended = IntrinsicsKt__IntrinsicsKt.getCOROUTINE_SUSPENDED();
        IntrinsicsKt__IntrinsicsKt.getCOROUTINE_SUSPENDED();
        DebugProbesKt.probeCoroutineSuspended(continuation);
        return coroutine_suspended;
    }

    public void resumeWith(Object obj) {
        ResultKt.throwOnFailure(obj);
        this.state = 4;
    }

    public CoroutineContext getContext() {
        return EmptyCoroutineContext.INSTANCE;
    }
}
