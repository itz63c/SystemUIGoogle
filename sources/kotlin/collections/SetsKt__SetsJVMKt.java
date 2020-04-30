package kotlin.collections;

import java.util.Collections;
import java.util.Set;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: SetsJVM.kt */
class SetsKt__SetsJVMKt {
    public static final <T> Set<T> setOf(T t) {
        Set<T> singleton = Collections.singleton(t);
        Intrinsics.checkExpressionValueIsNotNull(singleton, "java.util.Collections.singleton(element)");
        return singleton;
    }
}
