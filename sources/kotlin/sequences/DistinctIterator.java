package kotlin.sequences;

import java.util.HashSet;
import java.util.Iterator;
import kotlin.collections.AbstractIterator;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: Sequences.kt */
final class DistinctIterator<T, K> extends AbstractIterator<T> {
    private final Function1<T, K> keySelector;
    private final HashSet<K> observed = new HashSet<>();
    private final Iterator<T> source;

    public DistinctIterator(Iterator<? extends T> it, Function1<? super T, ? extends K> function1) {
        Intrinsics.checkParameterIsNotNull(it, "source");
        Intrinsics.checkParameterIsNotNull(function1, "keySelector");
        this.source = it;
        this.keySelector = function1;
    }

    /* access modifiers changed from: protected */
    public void computeNext() {
        while (this.source.hasNext()) {
            Object next = this.source.next();
            if (this.observed.add(this.keySelector.invoke(next))) {
                setNext(next);
                return;
            }
        }
        done();
    }
}
