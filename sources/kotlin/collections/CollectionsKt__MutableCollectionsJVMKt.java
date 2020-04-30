package kotlin.collections;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: MutableCollectionsJVM.kt */
class CollectionsKt__MutableCollectionsJVMKt extends CollectionsKt__IteratorsKt {
    public static <T> void sortWith(List<T> list, Comparator<? super T> comparator) {
        Intrinsics.checkParameterIsNotNull(list, "$this$sortWith");
        Intrinsics.checkParameterIsNotNull(comparator, "comparator");
        if (list.size() > 1) {
            Collections.sort(list, comparator);
        }
    }
}
