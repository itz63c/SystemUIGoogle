package kotlin.sequences;

import java.util.Iterator;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: Sequences.kt */
public final class FilteringSequence<T> implements Sequence<T> {
    /* access modifiers changed from: private */
    public final Function1<T, Boolean> predicate;
    /* access modifiers changed from: private */
    public final boolean sendWhen;
    /* access modifiers changed from: private */
    public final Sequence<T> sequence;

    public FilteringSequence(Sequence<? extends T> sequence2, boolean z, Function1<? super T, Boolean> function1) {
        Intrinsics.checkParameterIsNotNull(sequence2, "sequence");
        Intrinsics.checkParameterIsNotNull(function1, "predicate");
        this.sequence = sequence2;
        this.sendWhen = z;
        this.predicate = function1;
    }

    public Iterator<T> iterator() {
        return new FilteringSequence$iterator$1(this);
    }
}
