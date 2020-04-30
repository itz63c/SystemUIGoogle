package com.google.common.collect;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

abstract class AbstractMultimap<K, V> implements Multimap<K, V> {
    private transient Map<K, Collection<V>> asMap;
    private transient Collection<Entry<K, V>> entries;
    private transient Set<K> keySet;

    class Entries extends Entries<K, V> {
        Entries() {
        }

        /* access modifiers changed from: 0000 */
        public Multimap<K, V> multimap() {
            return AbstractMultimap.this;
        }

        public Iterator<Entry<K, V>> iterator() {
            return AbstractMultimap.this.entryIterator();
        }
    }

    class EntrySet extends Entries implements Set<Entry<K, V>> {
        EntrySet(AbstractMultimap abstractMultimap) {
            super();
        }

        public int hashCode() {
            return Sets.hashCodeImpl(this);
        }

        public boolean equals(Object obj) {
            return Sets.equalsImpl(this, obj);
        }
    }

    /* access modifiers changed from: 0000 */
    public abstract Map<K, Collection<V>> createAsMap();

    /* access modifiers changed from: 0000 */
    public abstract Collection<Entry<K, V>> createEntries();

    /* access modifiers changed from: 0000 */
    public abstract Set<K> createKeySet();

    /* access modifiers changed from: 0000 */
    public abstract Iterator<Entry<K, V>> entryIterator();

    AbstractMultimap() {
    }

    public boolean containsEntry(Object obj, Object obj2) {
        Collection collection = (Collection) asMap().get(obj);
        return collection != null && collection.contains(obj2);
    }

    @CanIgnoreReturnValue
    public boolean remove(Object obj, Object obj2) {
        Collection collection = (Collection) asMap().get(obj);
        return collection != null && collection.remove(obj2);
    }

    public Collection<Entry<K, V>> entries() {
        Collection<Entry<K, V>> collection = this.entries;
        if (collection != null) {
            return collection;
        }
        Collection<Entry<K, V>> createEntries = createEntries();
        this.entries = createEntries;
        return createEntries;
    }

    public Set<K> keySet() {
        Set<K> set = this.keySet;
        if (set != null) {
            return set;
        }
        Set<K> createKeySet = createKeySet();
        this.keySet = createKeySet;
        return createKeySet;
    }

    public Map<K, Collection<V>> asMap() {
        Map<K, Collection<V>> map = this.asMap;
        if (map != null) {
            return map;
        }
        Map<K, Collection<V>> createAsMap = createAsMap();
        this.asMap = createAsMap;
        return createAsMap;
    }

    public boolean equals(Object obj) {
        return Multimaps.equalsImpl(this, obj);
    }

    public int hashCode() {
        return asMap().hashCode();
    }

    public String toString() {
        return asMap().toString();
    }
}
