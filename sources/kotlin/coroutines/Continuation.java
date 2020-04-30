package kotlin.coroutines;

/* compiled from: Continuation.kt */
public interface Continuation<T> {
    CoroutineContext getContext();

    void resumeWith(Object obj);
}
