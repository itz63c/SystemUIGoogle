package kotlin.collections;

import java.util.Collection;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: Collections.kt */
class CollectionsKt__CollectionsKt extends CollectionsKt__CollectionsJVMKt {
    public static final <T> Collection<T> asCollection(T[] tArr) {
        Intrinsics.checkParameterIsNotNull(tArr, "$this$asCollection");
        return new ArrayAsCollection(tArr, false);
    }

    public static <T> List<T> emptyList() {
        return EmptyList.INSTANCE;
    }

    public static <T> List<T> listOf(T... tArr) {
        Intrinsics.checkParameterIsNotNull(tArr, "elements");
        return tArr.length > 0 ? ArraysKt___ArraysJvmKt.asList(tArr) : emptyList();
    }

    public static final <T> int getLastIndex(List<? extends T> list) {
        Intrinsics.checkParameterIsNotNull(list, "$this$lastIndex");
        return list.size() - 1;
    }

    public static <T> List<T> optimizeReadOnlyList(List<? extends T> list) {
        Intrinsics.checkParameterIsNotNull(list, "$this$optimizeReadOnlyList");
        int size = list.size();
        if (size == 0) {
            return emptyList();
        }
        if (size != 1) {
            return list;
        }
        return CollectionsKt__CollectionsJVMKt.listOf(list.get(0));
    }

    public static void throwIndexOverflow() {
        throw new ArithmeticException("Index overflow has happened.");
    }
}
