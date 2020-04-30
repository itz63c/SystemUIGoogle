package com.google.common.collect;

import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

abstract class AbstractMapBasedMultimap<K, V> extends AbstractMultimap<K, V> implements Serializable {
    private static final long serialVersionUID = 2447537837011683357L;
    /* access modifiers changed from: private */
    public transient Map<K, Collection<V>> map;
    /* access modifiers changed from: private */
    public transient int totalSize;

    private class AsMap extends ViewCachingAbstractMap<K, Collection<V>> {
        final transient Map<K, Collection<V>> submap;

        class AsMapEntries extends EntrySet<K, Collection<V>> {
            AsMapEntries() {
            }

            /* access modifiers changed from: 0000 */
            public Map<K, Collection<V>> map() {
                return AsMap.this;
            }

            public Iterator<Entry<K, Collection<V>>> iterator() {
                return new AsMapIterator();
            }

            public boolean contains(Object obj) {
                return Collections2.safeContains(AsMap.this.submap.entrySet(), obj);
            }

            public boolean remove(Object obj) {
                if (!contains(obj)) {
                    return false;
                }
                AbstractMapBasedMultimap.this.removeValuesForKey(((Entry) obj).getKey());
                return true;
            }
        }

        class AsMapIterator implements Iterator<Entry<K, Collection<V>>> {
            Collection<V> collection;
            final Iterator<Entry<K, Collection<V>>> delegateIterator = AsMap.this.submap.entrySet().iterator();

            AsMapIterator() {
            }

            public boolean hasNext() {
                return this.delegateIterator.hasNext();
            }

            public Entry<K, Collection<V>> next() {
                Entry entry = (Entry) this.delegateIterator.next();
                this.collection = (Collection) entry.getValue();
                return AsMap.this.wrapEntry(entry);
            }

            public void remove() {
                CollectPreconditions.checkRemove(this.collection != null);
                this.delegateIterator.remove();
                AbstractMapBasedMultimap.access$220(AbstractMapBasedMultimap.this, this.collection.size());
                this.collection.clear();
                this.collection = null;
            }
        }

        AsMap(Map<K, Collection<V>> map) {
            this.submap = map;
        }

        /* access modifiers changed from: protected */
        public Set<Entry<K, Collection<V>>> createEntrySet() {
            return new AsMapEntries();
        }

        public boolean containsKey(Object obj) {
            return Maps.safeContainsKey(this.submap, obj);
        }

        public Collection<V> get(Object obj) {
            Collection collection = (Collection) Maps.safeGet(this.submap, obj);
            if (collection == null) {
                return null;
            }
            return AbstractMapBasedMultimap.this.wrapCollection(obj, collection);
        }

        public Set<K> keySet() {
            return AbstractMapBasedMultimap.this.keySet();
        }

        public int size() {
            return this.submap.size();
        }

        public Collection<V> remove(Object obj) {
            Collection collection = (Collection) this.submap.remove(obj);
            if (collection == null) {
                return null;
            }
            Collection<V> createCollection = AbstractMapBasedMultimap.this.createCollection();
            createCollection.addAll(collection);
            AbstractMapBasedMultimap.access$220(AbstractMapBasedMultimap.this, collection.size());
            collection.clear();
            return createCollection;
        }

        public boolean equals(Object obj) {
            return this == obj || this.submap.equals(obj);
        }

        public int hashCode() {
            return this.submap.hashCode();
        }

        public String toString() {
            return this.submap.toString();
        }

        public void clear() {
            if (this.submap == AbstractMapBasedMultimap.this.map) {
                AbstractMapBasedMultimap.this.clear();
            } else {
                Iterators.clear(new AsMapIterator());
            }
        }

        /* access modifiers changed from: 0000 */
        public Entry<K, Collection<V>> wrapEntry(Entry<K, Collection<V>> entry) {
            Object key = entry.getKey();
            return Maps.immutableEntry(key, AbstractMapBasedMultimap.this.wrapCollection(key, (Collection) entry.getValue()));
        }
    }

    private abstract class Itr<T> implements Iterator<T> {
        Collection<V> collection = null;
        K key = null;
        final Iterator<Entry<K, Collection<V>>> keyIterator;
        Iterator<V> valueIterator = Iterators.emptyModifiableIterator();

        /* access modifiers changed from: 0000 */
        public abstract T output(K k, V v);

        Itr() {
            this.keyIterator = AbstractMapBasedMultimap.this.map.entrySet().iterator();
        }

        public boolean hasNext() {
            return this.keyIterator.hasNext() || this.valueIterator.hasNext();
        }

        public T next() {
            if (!this.valueIterator.hasNext()) {
                Entry entry = (Entry) this.keyIterator.next();
                this.key = entry.getKey();
                Collection<V> collection2 = (Collection) entry.getValue();
                this.collection = collection2;
                this.valueIterator = collection2.iterator();
            }
            return output(this.key, this.valueIterator.next());
        }

        public void remove() {
            this.valueIterator.remove();
            if (this.collection.isEmpty()) {
                this.keyIterator.remove();
            }
            AbstractMapBasedMultimap.this.totalSize = AbstractMapBasedMultimap.this.totalSize - 1;
        }
    }

    private class KeySet extends KeySet<K, Collection<V>> {
        KeySet(Map<K, Collection<V>> map) {
            super(map);
        }

        public Iterator<K> iterator() {
            final Iterator it = map().entrySet().iterator();
            return new Iterator<K>() {
                Entry<K, Collection<V>> entry;

                public boolean hasNext() {
                    return it.hasNext();
                }

                public K next() {
                    Entry<K, Collection<V>> entry2 = (Entry) it.next();
                    this.entry = entry2;
                    return entry2.getKey();
                }

                public void remove() {
                    CollectPreconditions.checkRemove(this.entry != null);
                    Collection collection = (Collection) this.entry.getValue();
                    it.remove();
                    AbstractMapBasedMultimap.access$220(AbstractMapBasedMultimap.this, collection.size());
                    collection.clear();
                    this.entry = null;
                }
            };
        }

        public boolean remove(Object obj) {
            int i;
            Collection collection = (Collection) map().remove(obj);
            if (collection != null) {
                i = collection.size();
                collection.clear();
                AbstractMapBasedMultimap.access$220(AbstractMapBasedMultimap.this, i);
            } else {
                i = 0;
            }
            if (i > 0) {
                return true;
            }
            return false;
        }

        public void clear() {
            Iterators.clear(iterator());
        }

        public boolean containsAll(Collection<?> collection) {
            return map().keySet().containsAll(collection);
        }

        public boolean equals(Object obj) {
            return this == obj || map().keySet().equals(obj);
        }

        public int hashCode() {
            return map().keySet().hashCode();
        }
    }

    class NavigableAsMap extends SortedAsMap implements NavigableMap<K, Collection<V>> {
        NavigableAsMap(NavigableMap<K, Collection<V>> navigableMap) {
            super(navigableMap);
        }

        /* access modifiers changed from: 0000 */
        public NavigableMap<K, Collection<V>> sortedMap() {
            return (NavigableMap) super.sortedMap();
        }

        public Entry<K, Collection<V>> lowerEntry(K k) {
            Entry lowerEntry = sortedMap().lowerEntry(k);
            if (lowerEntry == null) {
                return null;
            }
            return wrapEntry(lowerEntry);
        }

        public K lowerKey(K k) {
            return sortedMap().lowerKey(k);
        }

        public Entry<K, Collection<V>> floorEntry(K k) {
            Entry floorEntry = sortedMap().floorEntry(k);
            if (floorEntry == null) {
                return null;
            }
            return wrapEntry(floorEntry);
        }

        public K floorKey(K k) {
            return sortedMap().floorKey(k);
        }

        public Entry<K, Collection<V>> ceilingEntry(K k) {
            Entry ceilingEntry = sortedMap().ceilingEntry(k);
            if (ceilingEntry == null) {
                return null;
            }
            return wrapEntry(ceilingEntry);
        }

        public K ceilingKey(K k) {
            return sortedMap().ceilingKey(k);
        }

        public Entry<K, Collection<V>> higherEntry(K k) {
            Entry higherEntry = sortedMap().higherEntry(k);
            if (higherEntry == null) {
                return null;
            }
            return wrapEntry(higherEntry);
        }

        public K higherKey(K k) {
            return sortedMap().higherKey(k);
        }

        public Entry<K, Collection<V>> firstEntry() {
            Entry firstEntry = sortedMap().firstEntry();
            if (firstEntry == null) {
                return null;
            }
            return wrapEntry(firstEntry);
        }

        public Entry<K, Collection<V>> lastEntry() {
            Entry lastEntry = sortedMap().lastEntry();
            if (lastEntry == null) {
                return null;
            }
            return wrapEntry(lastEntry);
        }

        public Entry<K, Collection<V>> pollFirstEntry() {
            return pollAsMapEntry(entrySet().iterator());
        }

        public Entry<K, Collection<V>> pollLastEntry() {
            return pollAsMapEntry(descendingMap().entrySet().iterator());
        }

        /* access modifiers changed from: 0000 */
        public Entry<K, Collection<V>> pollAsMapEntry(Iterator<Entry<K, Collection<V>>> it) {
            if (!it.hasNext()) {
                return null;
            }
            Entry entry = (Entry) it.next();
            Collection createCollection = AbstractMapBasedMultimap.this.createCollection();
            createCollection.addAll((Collection) entry.getValue());
            it.remove();
            return Maps.immutableEntry(entry.getKey(), AbstractMapBasedMultimap.this.unmodifiableCollectionSubclass(createCollection));
        }

        public NavigableMap<K, Collection<V>> descendingMap() {
            return new NavigableAsMap(sortedMap().descendingMap());
        }

        public NavigableSet<K> keySet() {
            return (NavigableSet) super.keySet();
        }

        /* access modifiers changed from: 0000 */
        public NavigableSet<K> createKeySet() {
            return new NavigableKeySet(sortedMap());
        }

        public NavigableSet<K> navigableKeySet() {
            return keySet();
        }

        public NavigableSet<K> descendingKeySet() {
            return descendingMap().navigableKeySet();
        }

        public NavigableMap<K, Collection<V>> subMap(K k, K k2) {
            return subMap(k, true, k2, false);
        }

        public NavigableMap<K, Collection<V>> subMap(K k, boolean z, K k2, boolean z2) {
            return new NavigableAsMap(sortedMap().subMap(k, z, k2, z2));
        }

        public NavigableMap<K, Collection<V>> headMap(K k) {
            return headMap(k, false);
        }

        public NavigableMap<K, Collection<V>> headMap(K k, boolean z) {
            return new NavigableAsMap(sortedMap().headMap(k, z));
        }

        public NavigableMap<K, Collection<V>> tailMap(K k) {
            return tailMap(k, true);
        }

        public NavigableMap<K, Collection<V>> tailMap(K k, boolean z) {
            return new NavigableAsMap(sortedMap().tailMap(k, z));
        }
    }

    class NavigableKeySet extends SortedKeySet implements NavigableSet<K> {
        NavigableKeySet(NavigableMap<K, Collection<V>> navigableMap) {
            super(navigableMap);
        }

        /* access modifiers changed from: 0000 */
        public NavigableMap<K, Collection<V>> sortedMap() {
            return (NavigableMap) super.sortedMap();
        }

        public K lower(K k) {
            return sortedMap().lowerKey(k);
        }

        public K floor(K k) {
            return sortedMap().floorKey(k);
        }

        public K ceiling(K k) {
            return sortedMap().ceilingKey(k);
        }

        public K higher(K k) {
            return sortedMap().higherKey(k);
        }

        public K pollFirst() {
            return Iterators.pollNext(iterator());
        }

        public K pollLast() {
            return Iterators.pollNext(descendingIterator());
        }

        public NavigableSet<K> descendingSet() {
            return new NavigableKeySet(sortedMap().descendingMap());
        }

        public Iterator<K> descendingIterator() {
            return descendingSet().iterator();
        }

        public NavigableSet<K> headSet(K k) {
            return headSet(k, false);
        }

        public NavigableSet<K> headSet(K k, boolean z) {
            return new NavigableKeySet(sortedMap().headMap(k, z));
        }

        public NavigableSet<K> subSet(K k, K k2) {
            return subSet(k, true, k2, false);
        }

        public NavigableSet<K> subSet(K k, boolean z, K k2, boolean z2) {
            return new NavigableKeySet(sortedMap().subMap(k, z, k2, z2));
        }

        public NavigableSet<K> tailSet(K k) {
            return tailSet(k, true);
        }

        public NavigableSet<K> tailSet(K k, boolean z) {
            return new NavigableKeySet(sortedMap().tailMap(k, z));
        }
    }

    private class SortedAsMap extends AsMap implements SortedMap<K, Collection<V>> {
        SortedSet<K> sortedKeySet;

        SortedAsMap(SortedMap<K, Collection<V>> sortedMap) {
            super(sortedMap);
        }

        /* access modifiers changed from: 0000 */
        public SortedMap<K, Collection<V>> sortedMap() {
            return (SortedMap) this.submap;
        }

        public Comparator<? super K> comparator() {
            return sortedMap().comparator();
        }

        public K firstKey() {
            return sortedMap().firstKey();
        }

        public K lastKey() {
            return sortedMap().lastKey();
        }

        public SortedMap<K, Collection<V>> headMap(K k) {
            return new SortedAsMap(sortedMap().headMap(k));
        }

        public SortedMap<K, Collection<V>> subMap(K k, K k2) {
            return new SortedAsMap(sortedMap().subMap(k, k2));
        }

        public SortedMap<K, Collection<V>> tailMap(K k) {
            return new SortedAsMap(sortedMap().tailMap(k));
        }

        public SortedSet<K> keySet() {
            SortedSet<K> sortedSet = this.sortedKeySet;
            if (sortedSet != null) {
                return sortedSet;
            }
            SortedSet<K> createKeySet = createKeySet();
            this.sortedKeySet = createKeySet;
            return createKeySet;
        }

        /* access modifiers changed from: 0000 */
        public SortedSet<K> createKeySet() {
            return new SortedKeySet(sortedMap());
        }
    }

    private class SortedKeySet extends KeySet implements SortedSet<K> {
        SortedKeySet(SortedMap<K, Collection<V>> sortedMap) {
            super(sortedMap);
        }

        /* access modifiers changed from: 0000 */
        public SortedMap<K, Collection<V>> sortedMap() {
            return (SortedMap) super.map();
        }

        public Comparator<? super K> comparator() {
            return sortedMap().comparator();
        }

        public K first() {
            return sortedMap().firstKey();
        }

        public SortedSet<K> headSet(K k) {
            return new SortedKeySet(sortedMap().headMap(k));
        }

        public K last() {
            return sortedMap().lastKey();
        }

        public SortedSet<K> subSet(K k, K k2) {
            return new SortedKeySet(sortedMap().subMap(k, k2));
        }

        public SortedSet<K> tailSet(K k) {
            return new SortedKeySet(sortedMap().tailMap(k));
        }
    }

    class WrappedCollection extends AbstractCollection<V> {
        final WrappedCollection ancestor;
        final Collection<V> ancestorDelegate;
        Collection<V> delegate;
        final K key;

        class WrappedIterator implements Iterator<V> {
            final Iterator<V> delegateIterator;
            final Collection<V> originalDelegate = WrappedCollection.this.delegate;

            WrappedIterator() {
                this.delegateIterator = AbstractMapBasedMultimap.iteratorOrListIterator(WrappedCollection.this.delegate);
            }

            WrappedIterator(Iterator<V> it) {
                this.delegateIterator = it;
            }

            /* access modifiers changed from: 0000 */
            public void validateIterator() {
                WrappedCollection.this.refreshIfEmpty();
                if (WrappedCollection.this.delegate != this.originalDelegate) {
                    throw new ConcurrentModificationException();
                }
            }

            public boolean hasNext() {
                validateIterator();
                return this.delegateIterator.hasNext();
            }

            public V next() {
                validateIterator();
                return this.delegateIterator.next();
            }

            public void remove() {
                this.delegateIterator.remove();
                AbstractMapBasedMultimap.this.totalSize = AbstractMapBasedMultimap.this.totalSize - 1;
                WrappedCollection.this.removeIfEmpty();
            }
        }

        WrappedCollection(K k, Collection<V> collection, WrappedCollection wrappedCollection) {
            Collection<V> collection2;
            this.key = k;
            this.delegate = collection;
            this.ancestor = wrappedCollection;
            if (wrappedCollection == null) {
                collection2 = null;
            } else {
                collection2 = wrappedCollection.getDelegate();
            }
            this.ancestorDelegate = collection2;
        }

        /* access modifiers changed from: 0000 */
        public void refreshIfEmpty() {
            WrappedCollection wrappedCollection = this.ancestor;
            if (wrappedCollection != null) {
                wrappedCollection.refreshIfEmpty();
                if (this.ancestor.getDelegate() != this.ancestorDelegate) {
                    throw new ConcurrentModificationException();
                }
            } else if (this.delegate.isEmpty()) {
                Collection<V> collection = (Collection) AbstractMapBasedMultimap.this.map.get(this.key);
                if (collection != null) {
                    this.delegate = collection;
                }
            }
        }

        /* access modifiers changed from: 0000 */
        public void removeIfEmpty() {
            WrappedCollection wrappedCollection = this.ancestor;
            if (wrappedCollection != null) {
                wrappedCollection.removeIfEmpty();
            } else if (this.delegate.isEmpty()) {
                AbstractMapBasedMultimap.this.map.remove(this.key);
            }
        }

        /* access modifiers changed from: 0000 */
        public K getKey() {
            return this.key;
        }

        /* access modifiers changed from: 0000 */
        public void addToMap() {
            WrappedCollection wrappedCollection = this.ancestor;
            if (wrappedCollection != null) {
                wrappedCollection.addToMap();
            } else {
                AbstractMapBasedMultimap.this.map.put(this.key, this.delegate);
            }
        }

        public int size() {
            refreshIfEmpty();
            return this.delegate.size();
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            refreshIfEmpty();
            return this.delegate.equals(obj);
        }

        public int hashCode() {
            refreshIfEmpty();
            return this.delegate.hashCode();
        }

        public String toString() {
            refreshIfEmpty();
            return this.delegate.toString();
        }

        /* access modifiers changed from: 0000 */
        public Collection<V> getDelegate() {
            return this.delegate;
        }

        public Iterator<V> iterator() {
            refreshIfEmpty();
            return new WrappedIterator();
        }

        public boolean add(V v) {
            refreshIfEmpty();
            boolean isEmpty = this.delegate.isEmpty();
            boolean add = this.delegate.add(v);
            if (add) {
                AbstractMapBasedMultimap.this.totalSize = AbstractMapBasedMultimap.this.totalSize + 1;
                if (isEmpty) {
                    addToMap();
                }
            }
            return add;
        }

        /* access modifiers changed from: 0000 */
        public WrappedCollection getAncestor() {
            return this.ancestor;
        }

        public boolean addAll(Collection<? extends V> collection) {
            if (collection.isEmpty()) {
                return false;
            }
            int size = size();
            boolean addAll = this.delegate.addAll(collection);
            if (addAll) {
                AbstractMapBasedMultimap.access$212(AbstractMapBasedMultimap.this, this.delegate.size() - size);
                if (size == 0) {
                    addToMap();
                }
            }
            return addAll;
        }

        public boolean contains(Object obj) {
            refreshIfEmpty();
            return this.delegate.contains(obj);
        }

        public boolean containsAll(Collection<?> collection) {
            refreshIfEmpty();
            return this.delegate.containsAll(collection);
        }

        public void clear() {
            int size = size();
            if (size != 0) {
                this.delegate.clear();
                AbstractMapBasedMultimap.access$220(AbstractMapBasedMultimap.this, size);
                removeIfEmpty();
            }
        }

        public boolean remove(Object obj) {
            refreshIfEmpty();
            boolean remove = this.delegate.remove(obj);
            if (remove) {
                AbstractMapBasedMultimap.this.totalSize = AbstractMapBasedMultimap.this.totalSize - 1;
                removeIfEmpty();
            }
            return remove;
        }

        public boolean removeAll(Collection<?> collection) {
            if (collection.isEmpty()) {
                return false;
            }
            int size = size();
            boolean removeAll = this.delegate.removeAll(collection);
            if (removeAll) {
                AbstractMapBasedMultimap.access$212(AbstractMapBasedMultimap.this, this.delegate.size() - size);
                removeIfEmpty();
            }
            return removeAll;
        }

        public boolean retainAll(Collection<?> collection) {
            Preconditions.checkNotNull(collection);
            int size = size();
            boolean retainAll = this.delegate.retainAll(collection);
            if (retainAll) {
                AbstractMapBasedMultimap.access$212(AbstractMapBasedMultimap.this, this.delegate.size() - size);
                removeIfEmpty();
            }
            return retainAll;
        }
    }

    class WrappedNavigableSet extends WrappedSortedSet implements NavigableSet<V> {
        WrappedNavigableSet(K k, NavigableSet<V> navigableSet, WrappedCollection wrappedCollection) {
            super(k, navigableSet, wrappedCollection);
        }

        /* access modifiers changed from: 0000 */
        public NavigableSet<V> getSortedSetDelegate() {
            return (NavigableSet) super.getSortedSetDelegate();
        }

        public V lower(V v) {
            return getSortedSetDelegate().lower(v);
        }

        public V floor(V v) {
            return getSortedSetDelegate().floor(v);
        }

        public V ceiling(V v) {
            return getSortedSetDelegate().ceiling(v);
        }

        public V higher(V v) {
            return getSortedSetDelegate().higher(v);
        }

        public V pollFirst() {
            return Iterators.pollNext(iterator());
        }

        public V pollLast() {
            return Iterators.pollNext(descendingIterator());
        }

        /* JADX WARNING: type inference failed for: r4v2, types: [com.google.common.collect.AbstractMapBasedMultimap$WrappedCollection] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private java.util.NavigableSet<V> wrap(java.util.NavigableSet<V> r5) {
            /*
                r4 = this;
                com.google.common.collect.AbstractMapBasedMultimap$WrappedNavigableSet r0 = new com.google.common.collect.AbstractMapBasedMultimap$WrappedNavigableSet
                com.google.common.collect.AbstractMapBasedMultimap r1 = com.google.common.collect.AbstractMapBasedMultimap.this
                K r2 = r4.key
                com.google.common.collect.AbstractMapBasedMultimap$WrappedCollection r3 = r4.getAncestor()
                if (r3 != 0) goto L_0x000d
                goto L_0x0011
            L_0x000d:
                com.google.common.collect.AbstractMapBasedMultimap$WrappedCollection r4 = r4.getAncestor()
            L_0x0011:
                r0.<init>(r2, r5, r4)
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.common.collect.AbstractMapBasedMultimap.WrappedNavigableSet.wrap(java.util.NavigableSet):java.util.NavigableSet");
        }

        public NavigableSet<V> descendingSet() {
            return wrap(getSortedSetDelegate().descendingSet());
        }

        public Iterator<V> descendingIterator() {
            return new WrappedIterator(getSortedSetDelegate().descendingIterator());
        }

        public NavigableSet<V> subSet(V v, boolean z, V v2, boolean z2) {
            return wrap(getSortedSetDelegate().subSet(v, z, v2, z2));
        }

        public NavigableSet<V> headSet(V v, boolean z) {
            return wrap(getSortedSetDelegate().headSet(v, z));
        }

        public NavigableSet<V> tailSet(V v, boolean z) {
            return wrap(getSortedSetDelegate().tailSet(v, z));
        }
    }

    class WrappedSet extends WrappedCollection implements Set<V> {
        WrappedSet(K k, Set<V> set) {
            super(k, set, null);
        }

        public boolean removeAll(Collection<?> collection) {
            if (collection.isEmpty()) {
                return false;
            }
            int size = size();
            boolean removeAllImpl = Sets.removeAllImpl((Set) this.delegate, collection);
            if (removeAllImpl) {
                AbstractMapBasedMultimap.access$212(AbstractMapBasedMultimap.this, this.delegate.size() - size);
                removeIfEmpty();
            }
            return removeAllImpl;
        }
    }

    class WrappedSortedSet extends WrappedCollection implements SortedSet<V> {
        WrappedSortedSet(K k, SortedSet<V> sortedSet, WrappedCollection wrappedCollection) {
            super(k, sortedSet, wrappedCollection);
        }

        /* access modifiers changed from: 0000 */
        public SortedSet<V> getSortedSetDelegate() {
            return (SortedSet) getDelegate();
        }

        public Comparator<? super V> comparator() {
            return getSortedSetDelegate().comparator();
        }

        public V first() {
            refreshIfEmpty();
            return getSortedSetDelegate().first();
        }

        public V last() {
            refreshIfEmpty();
            return getSortedSetDelegate().last();
        }

        /* JADX WARNING: type inference failed for: r4v2, types: [com.google.common.collect.AbstractMapBasedMultimap$WrappedCollection] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public java.util.SortedSet<V> headSet(V r5) {
            /*
                r4 = this;
                r4.refreshIfEmpty()
                com.google.common.collect.AbstractMapBasedMultimap$WrappedSortedSet r0 = new com.google.common.collect.AbstractMapBasedMultimap$WrappedSortedSet
                com.google.common.collect.AbstractMapBasedMultimap r1 = com.google.common.collect.AbstractMapBasedMultimap.this
                java.lang.Object r2 = r4.getKey()
                java.util.SortedSet r3 = r4.getSortedSetDelegate()
                java.util.SortedSet r5 = r3.headSet(r5)
                com.google.common.collect.AbstractMapBasedMultimap$WrappedCollection r3 = r4.getAncestor()
                if (r3 != 0) goto L_0x001a
                goto L_0x001e
            L_0x001a:
                com.google.common.collect.AbstractMapBasedMultimap$WrappedCollection r4 = r4.getAncestor()
            L_0x001e:
                r0.<init>(r2, r5, r4)
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.common.collect.AbstractMapBasedMultimap.WrappedSortedSet.headSet(java.lang.Object):java.util.SortedSet");
        }

        /* JADX WARNING: type inference failed for: r4v2, types: [com.google.common.collect.AbstractMapBasedMultimap$WrappedCollection] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public java.util.SortedSet<V> subSet(V r5, V r6) {
            /*
                r4 = this;
                r4.refreshIfEmpty()
                com.google.common.collect.AbstractMapBasedMultimap$WrappedSortedSet r0 = new com.google.common.collect.AbstractMapBasedMultimap$WrappedSortedSet
                com.google.common.collect.AbstractMapBasedMultimap r1 = com.google.common.collect.AbstractMapBasedMultimap.this
                java.lang.Object r2 = r4.getKey()
                java.util.SortedSet r3 = r4.getSortedSetDelegate()
                java.util.SortedSet r5 = r3.subSet(r5, r6)
                com.google.common.collect.AbstractMapBasedMultimap$WrappedCollection r6 = r4.getAncestor()
                if (r6 != 0) goto L_0x001a
                goto L_0x001e
            L_0x001a:
                com.google.common.collect.AbstractMapBasedMultimap$WrappedCollection r4 = r4.getAncestor()
            L_0x001e:
                r0.<init>(r2, r5, r4)
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.common.collect.AbstractMapBasedMultimap.WrappedSortedSet.subSet(java.lang.Object, java.lang.Object):java.util.SortedSet");
        }

        /* JADX WARNING: type inference failed for: r4v2, types: [com.google.common.collect.AbstractMapBasedMultimap$WrappedCollection] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public java.util.SortedSet<V> tailSet(V r5) {
            /*
                r4 = this;
                r4.refreshIfEmpty()
                com.google.common.collect.AbstractMapBasedMultimap$WrappedSortedSet r0 = new com.google.common.collect.AbstractMapBasedMultimap$WrappedSortedSet
                com.google.common.collect.AbstractMapBasedMultimap r1 = com.google.common.collect.AbstractMapBasedMultimap.this
                java.lang.Object r2 = r4.getKey()
                java.util.SortedSet r3 = r4.getSortedSetDelegate()
                java.util.SortedSet r5 = r3.tailSet(r5)
                com.google.common.collect.AbstractMapBasedMultimap$WrappedCollection r3 = r4.getAncestor()
                if (r3 != 0) goto L_0x001a
                goto L_0x001e
            L_0x001a:
                com.google.common.collect.AbstractMapBasedMultimap$WrappedCollection r4 = r4.getAncestor()
            L_0x001e:
                r0.<init>(r2, r5, r4)
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.common.collect.AbstractMapBasedMultimap.WrappedSortedSet.tailSet(java.lang.Object):java.util.SortedSet");
        }
    }

    /* access modifiers changed from: 0000 */
    public abstract Collection<V> createCollection();

    /* access modifiers changed from: 0000 */
    public abstract <E> Collection<E> unmodifiableCollectionSubclass(Collection<E> collection);

    /* access modifiers changed from: 0000 */
    public abstract Collection<V> wrapCollection(K k, Collection<V> collection);

    static /* synthetic */ int access$212(AbstractMapBasedMultimap abstractMapBasedMultimap, int i) {
        int i2 = abstractMapBasedMultimap.totalSize + i;
        abstractMapBasedMultimap.totalSize = i2;
        return i2;
    }

    static /* synthetic */ int access$220(AbstractMapBasedMultimap abstractMapBasedMultimap, int i) {
        int i2 = abstractMapBasedMultimap.totalSize - i;
        abstractMapBasedMultimap.totalSize = i2;
        return i2;
    }

    protected AbstractMapBasedMultimap(Map<K, Collection<V>> map2) {
        Preconditions.checkArgument(map2.isEmpty());
        this.map = map2;
    }

    /* access modifiers changed from: 0000 */
    public final void setMap(Map<K, Collection<V>> map2) {
        this.map = map2;
        this.totalSize = 0;
        for (Collection collection : map2.values()) {
            Preconditions.checkArgument(!collection.isEmpty());
            this.totalSize += collection.size();
        }
    }

    /* access modifiers changed from: 0000 */
    public Collection<V> createCollection(K k) {
        return createCollection();
    }

    /* access modifiers changed from: 0000 */
    public Map<K, Collection<V>> backingMap() {
        return this.map;
    }

    public int size() {
        return this.totalSize;
    }

    public void clear() {
        for (Collection clear : this.map.values()) {
            clear.clear();
        }
        this.map.clear();
        this.totalSize = 0;
    }

    public Collection<V> get(K k) {
        Collection collection = (Collection) this.map.get(k);
        if (collection == null) {
            collection = createCollection(k);
        }
        return wrapCollection(k, collection);
    }

    /* access modifiers changed from: private */
    public static <E> Iterator<E> iteratorOrListIterator(Collection<E> collection) {
        if (collection instanceof List) {
            return ((List) collection).listIterator();
        }
        return collection.iterator();
    }

    /* access modifiers changed from: 0000 */
    public Set<K> createKeySet() {
        return new KeySet(this.map);
    }

    /* access modifiers changed from: 0000 */
    public final Set<K> createMaybeNavigableKeySet() {
        Map<K, Collection<V>> map2 = this.map;
        if (map2 instanceof NavigableMap) {
            return new NavigableKeySet((NavigableMap) this.map);
        }
        if (map2 instanceof SortedMap) {
            return new SortedKeySet((SortedMap) this.map);
        }
        return new KeySet(this.map);
    }

    /* access modifiers changed from: private */
    public void removeValuesForKey(Object obj) {
        Collection collection = (Collection) Maps.safeRemove(this.map, obj);
        if (collection != null) {
            int size = collection.size();
            collection.clear();
            this.totalSize -= size;
        }
    }

    public Collection<Entry<K, V>> entries() {
        return super.entries();
    }

    /* access modifiers changed from: 0000 */
    public Collection<Entry<K, V>> createEntries() {
        if (this instanceof SetMultimap) {
            return new EntrySet(this);
        }
        return new Entries();
    }

    /* access modifiers changed from: 0000 */
    public Iterator<Entry<K, V>> entryIterator() {
        return new Itr<Entry<K, V>>(this) {
            /* access modifiers changed from: 0000 */
            public Entry<K, V> output(K k, V v) {
                return Maps.immutableEntry(k, v);
            }
        };
    }

    /* access modifiers changed from: 0000 */
    public Map<K, Collection<V>> createAsMap() {
        return new AsMap(this.map);
    }

    /* access modifiers changed from: 0000 */
    public final Map<K, Collection<V>> createMaybeNavigableAsMap() {
        Map<K, Collection<V>> map2 = this.map;
        if (map2 instanceof NavigableMap) {
            return new NavigableAsMap((NavigableMap) this.map);
        }
        if (map2 instanceof SortedMap) {
            return new SortedAsMap((SortedMap) this.map);
        }
        return new AsMap(this.map);
    }
}
