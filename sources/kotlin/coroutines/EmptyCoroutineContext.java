package kotlin.coroutines;

import java.io.Serializable;
import kotlin.coroutines.CoroutineContext.Element;
import kotlin.coroutines.CoroutineContext.Key;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: CoroutineContextImpl.kt */
public final class EmptyCoroutineContext implements CoroutineContext, Serializable {
    public static final EmptyCoroutineContext INSTANCE = new EmptyCoroutineContext();
    private static final long serialVersionUID = 0;

    public <E extends Element> E get(Key<E> key) {
        Intrinsics.checkParameterIsNotNull(key, "key");
        return null;
    }

    public int hashCode() {
        return 0;
    }

    public String toString() {
        return "EmptyCoroutineContext";
    }

    private EmptyCoroutineContext() {
    }

    private final Object readResolve() {
        return INSTANCE;
    }
}
