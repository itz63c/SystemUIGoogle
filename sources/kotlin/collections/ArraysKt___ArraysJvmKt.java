package kotlin.collections;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: _ArraysJvm.kt */
class ArraysKt___ArraysJvmKt extends ArraysKt__ArraysKt {
    public static final <T> List<T> asList(T[] tArr) {
        Intrinsics.checkParameterIsNotNull(tArr, "$this$asList");
        List<T> asList = ArraysUtilJVM.asList(tArr);
        Intrinsics.checkExpressionValueIsNotNull(asList, "ArraysUtilJVM.asList(this)");
        return asList;
    }

    public static final <T> void sortWith(T[] tArr, Comparator<? super T> comparator) {
        Intrinsics.checkParameterIsNotNull(tArr, "$this$sortWith");
        Intrinsics.checkParameterIsNotNull(comparator, "comparator");
        if (tArr.length > 1) {
            Arrays.sort(tArr, comparator);
        }
    }
}
