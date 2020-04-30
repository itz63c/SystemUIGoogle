package kotlin.coroutines;

import kotlin.coroutines.CoroutineContext.Element;

/* compiled from: ContinuationInterceptor.kt */
public interface ContinuationInterceptor extends Element {
    public static final Key Key = Key.$$INSTANCE;

    /* compiled from: ContinuationInterceptor.kt */
    public static final class Key implements kotlin.coroutines.CoroutineContext.Key<ContinuationInterceptor> {
        static final /* synthetic */ Key $$INSTANCE = new Key();

        private Key() {
        }
    }

    void releaseInterceptedContinuation(Continuation<?> continuation);
}
