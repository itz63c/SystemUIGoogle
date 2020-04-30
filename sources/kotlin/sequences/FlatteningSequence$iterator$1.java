package kotlin.sequences;

import java.util.Iterator;
import java.util.NoSuchElementException;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.markers.KMappedMarker;

/* compiled from: Sequences.kt */
public final class FlatteningSequence$iterator$1 implements Iterator<E>, KMappedMarker {
    private Iterator<? extends E> itemIterator;
    private final Iterator<T> iterator;
    final /* synthetic */ FlatteningSequence this$0;

    public void remove() {
        throw new UnsupportedOperationException("Operation is not supported for read-only collection");
    }

    FlatteningSequence$iterator$1(FlatteningSequence flatteningSequence) {
        this.this$0 = flatteningSequence;
        this.iterator = flatteningSequence.sequence.iterator();
    }

    public E next() {
        if (ensureItemIterator()) {
            Iterator<? extends E> it = this.itemIterator;
            if (it != null) {
                return it.next();
            }
            Intrinsics.throwNpe();
            throw null;
        }
        throw new NoSuchElementException();
    }

    public boolean hasNext() {
        return ensureItemIterator();
    }

    private final boolean ensureItemIterator() {
        Iterator<? extends E> it = this.itemIterator;
        if (it != null && !it.hasNext()) {
            this.itemIterator = null;
        }
        while (true) {
            if (this.itemIterator == null) {
                if (this.iterator.hasNext()) {
                    Iterator<? extends E> it2 = (Iterator) this.this$0.iterator.invoke(this.this$0.transformer.invoke(this.iterator.next()));
                    if (it2.hasNext()) {
                        this.itemIterator = it2;
                        break;
                    }
                } else {
                    return false;
                }
            } else {
                break;
            }
        }
        return true;
    }
}
