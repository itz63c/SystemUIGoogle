package kotlin.coroutines.jvm.internal;

import kotlin.coroutines.Continuation;
import kotlin.coroutines.ContinuationInterceptor;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.CoroutineContext.Element;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ContinuationImpl.kt */
public abstract class ContinuationImpl extends BaseContinuationImpl {
    private final CoroutineContext _context;
    private transient Continuation<Object> intercepted;

    public ContinuationImpl(Continuation<Object> continuation, CoroutineContext coroutineContext) {
        super(continuation);
        this._context = coroutineContext;
    }

    public CoroutineContext getContext() {
        CoroutineContext coroutineContext = this._context;
        if (coroutineContext != null) {
            return coroutineContext;
        }
        Intrinsics.throwNpe();
        throw null;
    }

    /* access modifiers changed from: protected */
    public void releaseIntercepted() {
        Continuation<Object> continuation = this.intercepted;
        if (!(continuation == null || continuation == this)) {
            Element element = getContext().get(ContinuationInterceptor.Key);
            if (element != null) {
                ((ContinuationInterceptor) element).releaseInterceptedContinuation(continuation);
            } else {
                Intrinsics.throwNpe();
                throw null;
            }
        }
        this.intercepted = CompletedContinuation.INSTANCE;
    }
}
