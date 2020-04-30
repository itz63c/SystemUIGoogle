package kotlin.coroutines;

/* compiled from: CoroutineContext.kt */
public interface CoroutineContext {

    /* compiled from: CoroutineContext.kt */
    public interface Element extends CoroutineContext {
    }

    /* compiled from: CoroutineContext.kt */
    public interface Key<E extends Element> {
    }

    <E extends Element> E get(Key<E> key);
}
