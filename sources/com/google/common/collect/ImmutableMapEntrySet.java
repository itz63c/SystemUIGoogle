package com.google.common.collect;

import java.io.Serializable;
import java.util.Map.Entry;

abstract class ImmutableMapEntrySet<K, V> extends ImmutableSet<Entry<K, V>> {

    private static class EntrySetSerializedForm<K, V> implements Serializable {
        private static final long serialVersionUID = 0;
        final ImmutableMap<K, V> map;

        EntrySetSerializedForm(ImmutableMap<K, V> immutableMap) {
            this.map = immutableMap;
        }

        /* access modifiers changed from: 0000 */
        public Object readResolve() {
            return this.map.entrySet();
        }
    }

    /* access modifiers changed from: 0000 */
    public abstract ImmutableMap<K, V> map();

    ImmutableMapEntrySet() {
    }

    public int size() {
        return map().size();
    }

    public boolean contains(Object obj) {
        if (!(obj instanceof Entry)) {
            return false;
        }
        Entry entry = (Entry) obj;
        Object obj2 = map().get(entry.getKey());
        if (obj2 == null || !obj2.equals(entry.getValue())) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: 0000 */
    public boolean isPartialView() {
        return map().isPartialView();
    }

    /* access modifiers changed from: 0000 */
    public boolean isHashCodeFast() {
        return map().isHashCodeFast();
    }

    public int hashCode() {
        return map().hashCode();
    }

    /* access modifiers changed from: 0000 */
    public Object writeReplace() {
        return new EntrySetSerializedForm(map());
    }
}
