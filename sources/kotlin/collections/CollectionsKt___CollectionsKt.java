package kotlin.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.Sequence;

/* compiled from: _Collections.kt */
class CollectionsKt___CollectionsKt extends CollectionsKt___CollectionsJvmKt {
    public static <T> boolean contains(Iterable<? extends T> iterable, T t) {
        Intrinsics.checkParameterIsNotNull(iterable, "$this$contains");
        if (iterable instanceof Collection) {
            return ((Collection) iterable).contains(t);
        }
        return indexOf(iterable, t) >= 0;
    }

    public static final <T> int indexOf(Iterable<? extends T> iterable, T t) {
        Intrinsics.checkParameterIsNotNull(iterable, "$this$indexOf");
        if (iterable instanceof List) {
            return ((List) iterable).indexOf(t);
        }
        int i = 0;
        for (Object next : iterable) {
            if (i < 0) {
                CollectionsKt.throwIndexOverflow();
                throw null;
            } else if (Intrinsics.areEqual((Object) t, next)) {
                return i;
            } else {
                i++;
            }
        }
        return -1;
    }

    public static <T> List<T> sortedWith(Iterable<? extends T> iterable, Comparator<? super T> comparator) {
        Intrinsics.checkParameterIsNotNull(iterable, "$this$sortedWith");
        Intrinsics.checkParameterIsNotNull(comparator, "comparator");
        if (iterable instanceof Collection) {
            Collection collection = (Collection) iterable;
            if (collection.size() <= 1) {
                return toList(iterable);
            }
            Object[] array = collection.toArray(new Object[0]);
            String str = "null cannot be cast to non-null type kotlin.Array<T>";
            if (array == null) {
                throw new TypeCastException(str);
            } else if (array != null) {
                ArraysKt___ArraysJvmKt.sortWith(array, comparator);
                return ArraysKt___ArraysJvmKt.asList(array);
            } else {
                throw new TypeCastException(str);
            }
        } else {
            List<T> mutableList = toMutableList(iterable);
            CollectionsKt__MutableCollectionsJVMKt.sortWith(mutableList, comparator);
            return mutableList;
        }
    }

    public static final <T, C extends Collection<? super T>> C toCollection(Iterable<? extends T> iterable, C c) {
        Intrinsics.checkParameterIsNotNull(iterable, "$this$toCollection");
        Intrinsics.checkParameterIsNotNull(c, "destination");
        for (Object add : iterable) {
            c.add(add);
        }
        return c;
    }

    public static final <T> HashSet<T> toHashSet(Iterable<? extends T> iterable) {
        Intrinsics.checkParameterIsNotNull(iterable, "$this$toHashSet");
        HashSet<T> hashSet = new HashSet<>(MapsKt__MapsKt.mapCapacity(CollectionsKt__IterablesKt.collectionSizeOrDefault(iterable, 12)));
        toCollection(iterable, hashSet);
        return hashSet;
    }

    public static <T> List<T> toList(Iterable<? extends T> iterable) {
        List<T> list;
        Intrinsics.checkParameterIsNotNull(iterable, "$this$toList");
        if (!(iterable instanceof Collection)) {
            return CollectionsKt__CollectionsKt.optimizeReadOnlyList(toMutableList(iterable));
        }
        Collection collection = (Collection) iterable;
        int size = collection.size();
        if (size == 0) {
            list = CollectionsKt__CollectionsKt.emptyList();
        } else if (size != 1) {
            list = toMutableList(collection);
        } else {
            list = CollectionsKt__CollectionsJVMKt.listOf(iterable instanceof List ? ((List) iterable).get(0) : iterable.iterator().next());
        }
        return list;
    }

    public static final <T> List<T> toMutableList(Iterable<? extends T> iterable) {
        Intrinsics.checkParameterIsNotNull(iterable, "$this$toMutableList");
        if (iterable instanceof Collection) {
            return toMutableList((Collection) iterable);
        }
        ArrayList arrayList = new ArrayList();
        toCollection(iterable, arrayList);
        return arrayList;
    }

    public static <T> List<T> toMutableList(Collection<? extends T> collection) {
        Intrinsics.checkParameterIsNotNull(collection, "$this$toMutableList");
        return new ArrayList(collection);
    }

    public static <T> Set<T> toSet(Iterable<? extends T> iterable) {
        Set<T> set;
        Intrinsics.checkParameterIsNotNull(iterable, "$this$toSet");
        if (iterable instanceof Collection) {
            Collection collection = (Collection) iterable;
            int size = collection.size();
            if (size == 0) {
                set = SetsKt__SetsKt.emptySet();
            } else if (size != 1) {
                LinkedHashSet linkedHashSet = new LinkedHashSet(MapsKt__MapsKt.mapCapacity(collection.size()));
                toCollection(iterable, linkedHashSet);
                set = linkedHashSet;
            } else {
                set = SetsKt__SetsJVMKt.setOf(iterable instanceof List ? ((List) iterable).get(0) : iterable.iterator().next());
            }
            return set;
        }
        LinkedHashSet linkedHashSet2 = new LinkedHashSet();
        toCollection(iterable, linkedHashSet2);
        return SetsKt__SetsKt.optimizeReadOnlySet(linkedHashSet2);
    }

    public static <T> Set<T> subtract(Iterable<? extends T> iterable, Iterable<? extends T> iterable2) {
        Intrinsics.checkParameterIsNotNull(iterable, "$this$subtract");
        Intrinsics.checkParameterIsNotNull(iterable2, "other");
        Set<T> mutableSet = toMutableSet(iterable);
        CollectionsKt__MutableCollectionsKt.removeAll((Collection<? super T>) mutableSet, iterable2);
        return mutableSet;
    }

    public static final <T> Set<T> toMutableSet(Iterable<? extends T> iterable) {
        Intrinsics.checkParameterIsNotNull(iterable, "$this$toMutableSet");
        if (iterable instanceof Collection) {
            return new LinkedHashSet((Collection) iterable);
        }
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        toCollection(iterable, linkedHashSet);
        return linkedHashSet;
    }

    public static <T> Set<T> union(Iterable<? extends T> iterable, Iterable<? extends T> iterable2) {
        Intrinsics.checkParameterIsNotNull(iterable, "$this$union");
        Intrinsics.checkParameterIsNotNull(iterable2, "other");
        Set<T> mutableSet = toMutableSet(iterable);
        CollectionsKt__MutableCollectionsKt.addAll((Collection<? super T>) mutableSet, iterable2);
        return mutableSet;
    }

    public static <T> List<T> minus(Iterable<? extends T> iterable, T t) {
        Intrinsics.checkParameterIsNotNull(iterable, "$this$minus");
        ArrayList arrayList = new ArrayList(CollectionsKt__IterablesKt.collectionSizeOrDefault(iterable, 10));
        boolean z = false;
        for (Object next : iterable) {
            boolean z2 = true;
            if (!z && Intrinsics.areEqual(next, (Object) t)) {
                z = true;
                z2 = false;
            }
            if (z2) {
                arrayList.add(next);
            }
        }
        return arrayList;
    }

    public static <T> List<T> plus(Collection<? extends T> collection, T t) {
        Intrinsics.checkParameterIsNotNull(collection, "$this$plus");
        ArrayList arrayList = new ArrayList(collection.size() + 1);
        arrayList.addAll(collection);
        arrayList.add(t);
        return arrayList;
    }

    public static <T> List<T> plus(Collection<? extends T> collection, Iterable<? extends T> iterable) {
        Intrinsics.checkParameterIsNotNull(collection, "$this$plus");
        Intrinsics.checkParameterIsNotNull(iterable, "elements");
        if (iterable instanceof Collection) {
            Collection collection2 = (Collection) iterable;
            ArrayList arrayList = new ArrayList(collection.size() + collection2.size());
            arrayList.addAll(collection);
            arrayList.addAll(collection2);
            return arrayList;
        }
        ArrayList arrayList2 = new ArrayList(collection);
        CollectionsKt__MutableCollectionsKt.addAll((Collection<? super T>) arrayList2, iterable);
        return arrayList2;
    }

    public static <T> Sequence<T> asSequence(Iterable<? extends T> iterable) {
        Intrinsics.checkParameterIsNotNull(iterable, "$this$asSequence");
        return new CollectionsKt___CollectionsKt$asSequence$$inlined$Sequence$1(iterable);
    }
}
