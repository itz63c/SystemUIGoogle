package kotlin.collections;

import java.util.LinkedHashMap;
import java.util.Map;
import kotlin.Pair;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: Maps.kt */
class MapsKt__MapsKt extends MapsKt__MapsJVMKt {
    public static <K, V> Map<K, V> emptyMap() {
        EmptyMap emptyMap = EmptyMap.INSTANCE;
        if (emptyMap != null) {
            return emptyMap;
        }
        throw new TypeCastException("null cannot be cast to non-null type kotlin.collections.Map<K, V>");
    }

    public static <K, V> Map<K, V> mapOf(Pair<? extends K, ? extends V>... pairArr) {
        Intrinsics.checkParameterIsNotNull(pairArr, "pairs");
        if (pairArr.length <= 0) {
            return emptyMap();
        }
        LinkedHashMap linkedHashMap = new LinkedHashMap(mapCapacity(pairArr.length));
        toMap(pairArr, linkedHashMap);
        return linkedHashMap;
    }

    public static int mapCapacity(int i) {
        if (i < 3) {
            return i + 1;
        }
        if (i < 1073741824) {
            return i + (i / 3);
        }
        return Integer.MAX_VALUE;
    }

    public static <K, V> V getValue(Map<K, ? extends V> map, K k) {
        Intrinsics.checkParameterIsNotNull(map, "$this$getValue");
        return MapsKt__MapWithDefaultKt.getOrImplicitDefaultNullable(map, k);
    }

    public static final <K, V> void putAll(Map<? super K, ? super V> map, Pair<? extends K, ? extends V>[] pairArr) {
        Intrinsics.checkParameterIsNotNull(map, "$this$putAll");
        Intrinsics.checkParameterIsNotNull(pairArr, "pairs");
        for (Pair<? extends K, ? extends V> pair : pairArr) {
            map.put(pair.component1(), pair.component2());
        }
    }

    public static final <K, V, M extends Map<? super K, ? super V>> M toMap(Pair<? extends K, ? extends V>[] pairArr, M m) {
        Intrinsics.checkParameterIsNotNull(pairArr, "$this$toMap");
        Intrinsics.checkParameterIsNotNull(m, "destination");
        putAll(m, pairArr);
        return m;
    }

    public static <K, V> Map<K, V> toMutableMap(Map<? extends K, ? extends V> map) {
        Intrinsics.checkParameterIsNotNull(map, "$this$toMutableMap");
        return new LinkedHashMap(map);
    }
}
