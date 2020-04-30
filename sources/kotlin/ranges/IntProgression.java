package kotlin.ranges;

import kotlin.collections.IntIterator;
import kotlin.internal.ProgressionUtilKt;
import kotlin.jvm.internal.markers.KMappedMarker;

/* compiled from: Progressions.kt */
public class IntProgression implements Iterable<Integer>, KMappedMarker {
    private final int first;
    private final int last;
    private final int step;

    public IntProgression(int i, int i2, int i3) {
        if (i3 == 0) {
            throw new IllegalArgumentException("Step must be non-zero.");
        } else if (i3 != Integer.MIN_VALUE) {
            this.first = i;
            this.last = ProgressionUtilKt.getProgressionLastElement(i, i2, i3);
            this.step = i3;
        } else {
            throw new IllegalArgumentException("Step must be greater than Int.MIN_VALUE to avoid overflow on negation.");
        }
    }

    public final int getFirst() {
        return this.first;
    }

    public final int getLast() {
        return this.last;
    }

    public IntIterator iterator() {
        return new IntProgressionIterator(this.first, this.last, this.step);
    }
}
