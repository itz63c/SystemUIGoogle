package kotlin.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.Sequence;

/* compiled from: _Arrays.kt */
class ArraysKt___ArraysKt extends ArraysKt___ArraysJvmKt {
    public static <T> boolean contains(T[] tArr, T t) {
        Intrinsics.checkParameterIsNotNull(tArr, "$this$contains");
        return indexOf(tArr, t) >= 0;
    }

    public static final <T> int indexOf(T[] tArr, T t) {
        Intrinsics.checkParameterIsNotNull(tArr, "$this$indexOf");
        int i = 0;
        if (t == null) {
            int length = tArr.length;
            while (i < length) {
                if (tArr[i] == null) {
                    return i;
                }
                i++;
            }
        } else {
            int length2 = tArr.length;
            while (i < length2) {
                if (Intrinsics.areEqual((Object) t, (Object) tArr[i])) {
                    return i;
                }
                i++;
            }
        }
        return -1;
    }

    public static char single(char[] cArr) {
        Intrinsics.checkParameterIsNotNull(cArr, "$this$single");
        int length = cArr.length;
        if (length == 0) {
            throw new NoSuchElementException("Array is empty.");
        } else if (length == 1) {
            return cArr[0];
        } else {
            throw new IllegalArgumentException("Array has more than one element.");
        }
    }

    public static <T> T singleOrNull(T[] tArr) {
        Intrinsics.checkParameterIsNotNull(tArr, "$this$singleOrNull");
        if (tArr.length == 1) {
            return tArr[0];
        }
        return null;
    }

    public static <T> List<T> filterNotNull(T[] tArr) {
        Intrinsics.checkParameterIsNotNull(tArr, "$this$filterNotNull");
        ArrayList arrayList = new ArrayList();
        filterNotNullTo(tArr, arrayList);
        return arrayList;
    }

    public static final <C extends Collection<? super T>, T> C filterNotNullTo(T[] tArr, C c) {
        Intrinsics.checkParameterIsNotNull(tArr, "$this$filterNotNullTo");
        Intrinsics.checkParameterIsNotNull(c, "destination");
        for (T t : tArr) {
            if (t != null) {
                c.add(t);
            }
        }
        return c;
    }

    public static final <T, C extends Collection<? super T>> C toCollection(T[] tArr, C c) {
        Intrinsics.checkParameterIsNotNull(tArr, "$this$toCollection");
        Intrinsics.checkParameterIsNotNull(c, "destination");
        for (T add : tArr) {
            c.add(add);
        }
        return c;
    }

    public static <T> List<T> toMutableList(T[] tArr) {
        Intrinsics.checkParameterIsNotNull(tArr, "$this$toMutableList");
        return new ArrayList(CollectionsKt__CollectionsKt.asCollection(tArr));
    }

    public static <T> Set<T> toSet(T[] tArr) {
        Intrinsics.checkParameterIsNotNull(tArr, "$this$toSet");
        int length = tArr.length;
        if (length == 0) {
            return SetsKt__SetsKt.emptySet();
        }
        if (length == 1) {
            return SetsKt__SetsJVMKt.setOf(tArr[0]);
        }
        LinkedHashSet linkedHashSet = new LinkedHashSet(MapsKt__MapsKt.mapCapacity(tArr.length));
        toCollection(tArr, linkedHashSet);
        return linkedHashSet;
    }

    public static <T> Sequence<T> asSequence(T[] tArr) {
        Intrinsics.checkParameterIsNotNull(tArr, "$this$asSequence");
        if (tArr.length == 0) {
            return SequencesKt__SequencesKt.emptySequence();
        }
        return new ArraysKt___ArraysKt$asSequence$$inlined$Sequence$1(tArr);
    }
}
