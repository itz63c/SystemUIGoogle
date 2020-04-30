package kotlin.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: Iterables.kt */
class CollectionsKt__IterablesKt extends CollectionsKt__CollectionsKt {
    public static <T> int collectionSizeOrDefault(Iterable<? extends T> iterable, int i) {
        Intrinsics.checkParameterIsNotNull(iterable, "$this$collectionSizeOrDefault");
        return iterable instanceof Collection ? ((Collection) iterable).size() : i;
    }

    private static final <T> boolean safeToConvertToSet$CollectionsKt__IterablesKt(Collection<? extends T> collection) {
        return collection.size() > 2 && (collection instanceof ArrayList);
    }

    public static final <T> Collection<T> convertToSetForSetOperationWith(Iterable<? extends T> iterable, Iterable<? extends T> iterable2) {
        Intrinsics.checkParameterIsNotNull(iterable, "$this$convertToSetForSetOperationWith");
        Intrinsics.checkParameterIsNotNull(iterable2, "source");
        if (iterable instanceof Set) {
            return (Collection) iterable;
        }
        if (!(iterable instanceof Collection)) {
            return CollectionsKt___CollectionsKt.toHashSet(iterable);
        }
        if ((iterable2 instanceof Collection) && ((Collection) iterable2).size() < 2) {
            return (Collection) iterable;
        }
        Collection collection = (Collection) iterable;
        return safeToConvertToSet$CollectionsKt__IterablesKt(collection) ? CollectionsKt___CollectionsKt.toHashSet(iterable) : collection;
    }

    public static <T> List<T> flatten(Iterable<? extends Iterable<? extends T>> iterable) {
        Intrinsics.checkParameterIsNotNull(iterable, "$this$flatten");
        ArrayList arrayList = new ArrayList();
        for (Iterable addAll : iterable) {
            CollectionsKt__MutableCollectionsKt.addAll((Collection<? super T>) arrayList, addAll);
        }
        return arrayList;
    }
}
