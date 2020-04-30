package kotlin.sequences;

import java.util.Iterator;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: Sequences.kt */
public final class DistinctSequence<T, K> implements Sequence<T> {
    private final Function1<T, K> keySelector;
    private final Sequence<T> source;

    public DistinctSequence(Sequence<? extends T> sequence, Function1<? super T, ? extends K> function1) {
        Intrinsics.checkParameterIsNotNull(sequence, "source");
        Intrinsics.checkParameterIsNotNull(function1, "keySelector");
        this.source = sequence;
        this.keySelector = function1;
    }

    public Iterator<T> iterator() {
        return new DistinctIterator(this.source.iterator(), this.keySelector);
    }
}
