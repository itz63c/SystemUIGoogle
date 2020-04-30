package com.google.common.collect;

import java.lang.Comparable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NavigableMap;

final class TreeRangeSet$RangesByUpperBound<C extends Comparable<?>> extends AbstractNavigableMap<Cut<C>, Range<C>> {
    private final NavigableMap<Cut<C>, Range<C>> rangesByLowerBound;
    /* access modifiers changed from: private */
    public final Range<Cut<C>> upperBoundWindow;

    private TreeRangeSet$RangesByUpperBound(NavigableMap<Cut<C>, Range<C>> navigableMap, Range<Cut<C>> range) {
        this.rangesByLowerBound = navigableMap;
        this.upperBoundWindow = range;
    }

    private NavigableMap<Cut<C>, Range<C>> subMap(Range<Cut<C>> range) {
        if (range.isConnected(this.upperBoundWindow)) {
            return new TreeRangeSet$RangesByUpperBound(this.rangesByLowerBound, range.intersection(this.upperBoundWindow));
        }
        return ImmutableSortedMap.m134of();
    }

    public NavigableMap<Cut<C>, Range<C>> subMap(Cut<C> cut, boolean z, Cut<C> cut2, boolean z2) {
        return subMap(Range.range(cut, BoundType.forBoolean(z), cut2, BoundType.forBoolean(z2)));
    }

    public NavigableMap<Cut<C>, Range<C>> headMap(Cut<C> cut, boolean z) {
        return subMap(Range.upTo(cut, BoundType.forBoolean(z)));
    }

    public NavigableMap<Cut<C>, Range<C>> tailMap(Cut<C> cut, boolean z) {
        return subMap(Range.downTo(cut, BoundType.forBoolean(z)));
    }

    public Comparator<? super Cut<C>> comparator() {
        return Ordering.natural();
    }

    public boolean containsKey(Object obj) {
        return get(obj) != null;
    }

    public Range<C> get(Object obj) {
        if (obj instanceof Cut) {
            try {
                Cut cut = (Cut) obj;
                if (!this.upperBoundWindow.contains(cut)) {
                    return null;
                }
                Entry lowerEntry = this.rangesByLowerBound.lowerEntry(cut);
                if (lowerEntry != null && ((Range) lowerEntry.getValue()).upperBound.equals(cut)) {
                    return (Range) lowerEntry.getValue();
                }
            } catch (ClassCastException unused) {
            }
        }
        return null;
    }

    /* access modifiers changed from: 0000 */
    public Iterator<Entry<Cut<C>, Range<C>>> entryIterator() {
        final Iterator it;
        if (!this.upperBoundWindow.hasLowerBound()) {
            it = this.rangesByLowerBound.values().iterator();
        } else {
            Entry lowerEntry = this.rangesByLowerBound.lowerEntry((Cut) this.upperBoundWindow.lowerEndpoint());
            if (lowerEntry == null) {
                it = this.rangesByLowerBound.values().iterator();
            } else if (this.upperBoundWindow.lowerBound.isLessThan(((Range) lowerEntry.getValue()).upperBound)) {
                it = this.rangesByLowerBound.tailMap((Cut) lowerEntry.getKey(), true).values().iterator();
            } else {
                it = this.rangesByLowerBound.tailMap((Cut) this.upperBoundWindow.lowerEndpoint(), true).values().iterator();
            }
        }
        return new AbstractIterator<Entry<Cut<C>, Range<C>>>() {
            /* access modifiers changed from: protected */
            public Entry<Cut<C>, Range<C>> computeNext() {
                if (!it.hasNext()) {
                    return (Entry) endOfData();
                }
                Range range = (Range) it.next();
                if (TreeRangeSet$RangesByUpperBound.this.upperBoundWindow.upperBound.isLessThan(range.upperBound)) {
                    return (Entry) endOfData();
                }
                return Maps.immutableEntry(range.upperBound, range);
            }
        };
    }

    /* access modifiers changed from: 0000 */
    public Iterator<Entry<Cut<C>, Range<C>>> descendingEntryIterator() {
        Collection collection;
        if (this.upperBoundWindow.hasUpperBound()) {
            collection = this.rangesByLowerBound.headMap((Cut) this.upperBoundWindow.upperEndpoint(), false).descendingMap().values();
        } else {
            collection = this.rangesByLowerBound.descendingMap().values();
        }
        final PeekingIterator peekingIterator = Iterators.peekingIterator(collection.iterator());
        if (peekingIterator.hasNext() && this.upperBoundWindow.upperBound.isLessThan(((Range) peekingIterator.peek()).upperBound)) {
            peekingIterator.next();
        }
        return new AbstractIterator<Entry<Cut<C>, Range<C>>>() {
            /* access modifiers changed from: protected */
            public Entry<Cut<C>, Range<C>> computeNext() {
                Entry<Cut<C>, Range<C>> entry;
                if (!peekingIterator.hasNext()) {
                    return (Entry) endOfData();
                }
                Range range = (Range) peekingIterator.next();
                if (TreeRangeSet$RangesByUpperBound.this.upperBoundWindow.lowerBound.isLessThan(range.upperBound)) {
                    entry = Maps.immutableEntry(range.upperBound, range);
                } else {
                    entry = (Entry) endOfData();
                }
                return entry;
            }
        };
    }

    public int size() {
        if (this.upperBoundWindow.equals(Range.all())) {
            return this.rangesByLowerBound.size();
        }
        return Iterators.size(entryIterator());
    }

    public boolean isEmpty() {
        if (this.upperBoundWindow.equals(Range.all())) {
            return this.rangesByLowerBound.isEmpty();
        }
        return !entryIterator().hasNext();
    }
}
