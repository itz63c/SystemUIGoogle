package kotlin.collections;

import java.util.Map;
import java.util.NoSuchElementException;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: MapWithDefault.kt */
class MapsKt__MapWithDefaultKt {
    public static final <K, V> V getOrImplicitDefaultNullable(Map<K, ? extends V> map, K k) {
        Intrinsics.checkParameterIsNotNull(map, "$this$getOrImplicitDefault");
        if (map instanceof MapWithDefault) {
            return ((MapWithDefault) map).getOrImplicitDefault(k);
        }
        V v = map.get(k);
        if (v != null || map.containsKey(k)) {
            return v;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Key ");
        sb.append(k);
        sb.append(" is missing in the map.");
        throw new NoSuchElementException(sb.toString());
    }

    public static <K, V> Map<K, V> withDefault(Map<K, ? extends V> map, Function1<? super K, ? extends V> function1) {
        Intrinsics.checkParameterIsNotNull(map, "$this$withDefault");
        Intrinsics.checkParameterIsNotNull(function1, "defaultValue");
        if (map instanceof MapWithDefault) {
            return withDefault(((MapWithDefault) map).getMap(), function1);
        }
        return new MapWithDefaultImpl(map, function1);
    }
}
