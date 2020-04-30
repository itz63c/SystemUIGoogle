package kotlin.jvm.internal;

import java.util.Iterator;

/* compiled from: ArrayIterator.kt */
public final class ArrayIteratorKt {
    public static final <T> Iterator<T> iterator(T[] tArr) {
        Intrinsics.checkParameterIsNotNull(tArr, "array");
        return new ArrayIterator(tArr);
    }
}
