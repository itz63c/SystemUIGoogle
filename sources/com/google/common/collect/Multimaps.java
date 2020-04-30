package com.google.common.collect;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;

public final class Multimaps {

    private static class CustomSetMultimap<K, V> extends AbstractSetMultimap<K, V> {
        private static final long serialVersionUID = 0;
        transient Supplier<? extends Set<V>> factory;

        CustomSetMultimap(Map<K, Collection<V>> map, Supplier<? extends Set<V>> supplier) {
            super(map);
            Preconditions.checkNotNull(supplier);
            this.factory = supplier;
        }

        /* access modifiers changed from: 0000 */
        public Set<K> createKeySet() {
            return createMaybeNavigableKeySet();
        }

        /* access modifiers changed from: 0000 */
        public Map<K, Collection<V>> createAsMap() {
            return createMaybeNavigableAsMap();
        }

        /* access modifiers changed from: protected */
        public Set<V> createCollection() {
            return (Set) this.factory.get();
        }

        /* access modifiers changed from: 0000 */
        public <E> Collection<E> unmodifiableCollectionSubclass(Collection<E> collection) {
            if (collection instanceof NavigableSet) {
                return Sets.unmodifiableNavigableSet((NavigableSet) collection);
            }
            if (collection instanceof SortedSet) {
                return Collections.unmodifiableSortedSet((SortedSet) collection);
            }
            return Collections.unmodifiableSet((Set) collection);
        }

        /* access modifiers changed from: 0000 */
        public Collection<V> wrapCollection(K k, Collection<V> collection) {
            if (collection instanceof NavigableSet) {
                return new WrappedNavigableSet(k, (NavigableSet) collection, null);
            }
            if (collection instanceof SortedSet) {
                return new WrappedSortedSet(k, (SortedSet) collection, null);
            }
            return new WrappedSet(k, (Set) collection);
        }

        private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
            objectOutputStream.defaultWriteObject();
            objectOutputStream.writeObject(this.factory);
            objectOutputStream.writeObject(backingMap());
        }

        private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
            objectInputStream.defaultReadObject();
            this.factory = (Supplier) objectInputStream.readObject();
            setMap((Map) objectInputStream.readObject());
        }
    }

    static abstract class Entries<K, V> extends AbstractCollection<Entry<K, V>> {
        /* access modifiers changed from: 0000 */
        public abstract Multimap<K, V> multimap();

        Entries() {
        }

        public int size() {
            return multimap().size();
        }

        public boolean contains(Object obj) {
            if (!(obj instanceof Entry)) {
                return false;
            }
            Entry entry = (Entry) obj;
            return multimap().containsEntry(entry.getKey(), entry.getValue());
        }

        public boolean remove(Object obj) {
            if (!(obj instanceof Entry)) {
                return false;
            }
            Entry entry = (Entry) obj;
            return multimap().remove(entry.getKey(), entry.getValue());
        }

        public void clear() {
            multimap().clear();
        }
    }

    public static <K, V> SetMultimap<K, V> newSetMultimap(Map<K, Collection<V>> map, Supplier<? extends Set<V>> supplier) {
        return new CustomSetMultimap(map, supplier);
    }

    static boolean equalsImpl(Multimap<?, ?> multimap, Object obj) {
        if (obj == multimap) {
            return true;
        }
        if (!(obj instanceof Multimap)) {
            return false;
        }
        return multimap.asMap().equals(((Multimap) obj).asMap());
    }
}
