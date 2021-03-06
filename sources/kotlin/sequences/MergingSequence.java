package kotlin.sequences;

import java.util.Iterator;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: Sequences.kt */
public final class MergingSequence<T1, T2, V> implements Sequence<V> {
    /* access modifiers changed from: private */
    public final Sequence<T1> sequence1;
    /* access modifiers changed from: private */
    public final Sequence<T2> sequence2;
    /* access modifiers changed from: private */
    public final Function2<T1, T2, V> transform;

    public MergingSequence(Sequence<? extends T1> sequence, Sequence<? extends T2> sequence3, Function2<? super T1, ? super T2, ? extends V> function2) {
        Intrinsics.checkParameterIsNotNull(sequence, "sequence1");
        Intrinsics.checkParameterIsNotNull(sequence3, "sequence2");
        Intrinsics.checkParameterIsNotNull(function2, "transform");
        this.sequence1 = sequence;
        this.sequence2 = sequence3;
        this.transform = function2;
    }

    public Iterator<V> iterator() {
        return new MergingSequence$iterator$1(this);
    }
}
