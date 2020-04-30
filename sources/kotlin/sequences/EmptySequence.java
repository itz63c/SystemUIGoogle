package kotlin.sequences;

import java.util.Iterator;
import kotlin.collections.EmptyIterator;

/* compiled from: Sequences.kt */
final class EmptySequence implements Sequence, DropTakeSequence {
    public static final EmptySequence INSTANCE = new EmptySequence();

    private EmptySequence() {
    }

    public Iterator iterator() {
        return EmptyIterator.INSTANCE;
    }

    public EmptySequence take(int i) {
        return INSTANCE;
    }
}
