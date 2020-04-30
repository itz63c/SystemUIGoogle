package kotlin.sequences;

import java.util.Iterator;
import java.util.NoSuchElementException;
import kotlin.jvm.internal.markers.KMappedMarker;

/* compiled from: Sequences.kt */
public final class TakeSequence$iterator$1 implements Iterator<T>, KMappedMarker {
    private final Iterator<T> iterator;
    private int left;

    public void remove() {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }

    TakeSequence$iterator$1(TakeSequence takeSequence) {
        this.left = takeSequence.count;
        this.iterator = takeSequence.sequence.iterator();
    }

    public T next() {
        int i = this.left;
        if (i != 0) {
            this.left = i - 1;
            return this.iterator.next();
        }
        throw new NoSuchElementException();
    }

    public boolean hasNext() {
        return this.left > 0 && this.iterator.hasNext();
    }
}
