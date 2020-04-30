package com.google.common.collect;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;

public final class ObjectArrays {
    public static <T> T[] newArray(T[] tArr, int i) {
        return Platform.newArray(tArr, i);
    }

    static <T> T[] toArrayImpl(Collection<?> collection, T[] tArr) {
        int size = collection.size();
        if (tArr.length < size) {
            tArr = newArray(tArr, size);
        }
        fillArray(collection, tArr);
        if (tArr.length > size) {
            tArr[size] = null;
        }
        return tArr;
    }

    static <T> T[] toArrayImpl(Object[] objArr, int i, int i2, T[] tArr) {
        Preconditions.checkPositionIndexes(i, i + i2, objArr.length);
        if (tArr.length < i2) {
            tArr = newArray(tArr, i2);
        } else if (tArr.length > i2) {
            tArr[i2] = null;
        }
        System.arraycopy(objArr, i, tArr, 0, i2);
        return tArr;
    }

    static Object[] toArrayImpl(Collection<?> collection) {
        Object[] objArr = new Object[collection.size()];
        fillArray(collection, objArr);
        return objArr;
    }

    @CanIgnoreReturnValue
    private static Object[] fillArray(Iterable<?> iterable, Object[] objArr) {
        int i = 0;
        for (Object obj : iterable) {
            int i2 = i + 1;
            objArr[i] = obj;
            i = i2;
        }
        return objArr;
    }

    @CanIgnoreReturnValue
    static Object[] checkElementsNotNull(Object... objArr) {
        checkElementsNotNull(objArr, objArr.length);
        return objArr;
    }

    @CanIgnoreReturnValue
    static Object[] checkElementsNotNull(Object[] objArr, int i) {
        for (int i2 = 0; i2 < i; i2++) {
            checkElementNotNull(objArr[i2], i2);
        }
        return objArr;
    }

    @CanIgnoreReturnValue
    static Object checkElementNotNull(Object obj, int i) {
        if (obj != null) {
            return obj;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("at index ");
        sb.append(i);
        throw new NullPointerException(sb.toString());
    }
}
