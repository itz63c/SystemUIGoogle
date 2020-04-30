package kotlin.sequences;

import java.util.Iterator;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: Sequences.kt */
public final class TakeSequence<T> implements Sequence<T>, DropTakeSequence<T> {
    /* access modifiers changed from: private */
    public final int count;
    /* access modifiers changed from: private */
    public final Sequence<T> sequence;

    public TakeSequence(Sequence<? extends T> sequence2, int i) {
        Intrinsics.checkParameterIsNotNull(sequence2, "sequence");
        this.sequence = sequence2;
        this.count = i;
        if (!(i >= 0)) {
            StringBuilder sb = new StringBuilder();
            sb.append("count must be non-negative, but was ");
            sb.append(this.count);
            sb.append('.');
            throw new IllegalArgumentException(sb.toString().toString());
        }
    }

    public Sequence<T> take(int i) {
        return i >= this.count ? this : new TakeSequence(this.sequence, i);
    }

    public Iterator<T> iterator() {
        return new TakeSequence$iterator$1(this);
    }
}
