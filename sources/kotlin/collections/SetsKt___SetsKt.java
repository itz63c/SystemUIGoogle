package kotlin.collections;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: _Sets.kt */
class SetsKt___SetsKt extends SetsKt__SetsKt {
    public static <T> Set<T> minus(Set<? extends T> set, Iterable<? extends T> iterable) {
        Intrinsics.checkParameterIsNotNull(set, "$this$minus");
        Intrinsics.checkParameterIsNotNull(iterable, "elements");
        Collection convertToSetForSetOperationWith = CollectionsKt__IterablesKt.convertToSetForSetOperationWith(iterable, set);
        if (convertToSetForSetOperationWith.isEmpty()) {
            return CollectionsKt___CollectionsKt.toSet(set);
        }
        if (convertToSetForSetOperationWith instanceof Set) {
            LinkedHashSet linkedHashSet = new LinkedHashSet();
            for (Object next : set) {
                if (!convertToSetForSetOperationWith.contains(next)) {
                    linkedHashSet.add(next);
                }
            }
            return linkedHashSet;
        }
        LinkedHashSet linkedHashSet2 = new LinkedHashSet(set);
        linkedHashSet2.removeAll(convertToSetForSetOperationWith);
        return linkedHashSet2;
    }
}
