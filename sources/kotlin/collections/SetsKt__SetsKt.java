package kotlin.collections;

import java.util.Set;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: Sets.kt */
class SetsKt__SetsKt extends SetsKt__SetsJVMKt {
    public static <T> Set<T> emptySet() {
        return EmptySet.INSTANCE;
    }

    public static <T> Set<T> setOf(T... tArr) {
        Intrinsics.checkParameterIsNotNull(tArr, "elements");
        return tArr.length > 0 ? ArraysKt___ArraysKt.toSet(tArr) : emptySet();
    }

    public static final <T> Set<T> optimizeReadOnlySet(Set<? extends T> set) {
        Intrinsics.checkParameterIsNotNull(set, "$this$optimizeReadOnlySet");
        int size = set.size();
        if (size == 0) {
            return emptySet();
        }
        if (size != 1) {
            return set;
        }
        return SetsKt__SetsJVMKt.setOf(set.iterator().next());
    }
}
