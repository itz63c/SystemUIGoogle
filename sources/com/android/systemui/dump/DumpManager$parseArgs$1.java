package com.android.systemui.dump;

import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* compiled from: DumpManager.kt */
final class DumpManager$parseArgs$1 extends Lambda implements Function1<String, String> {
    public static final DumpManager$parseArgs$1 INSTANCE = new DumpManager$parseArgs$1();

    DumpManager$parseArgs$1() {
        super(1);
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj) {
        String str = (String) obj;
        invoke(str);
        return str;
    }

    public final String invoke(String str) {
        Intrinsics.checkParameterIsNotNull(str, "it");
        if (ArraysKt___ArraysKt.contains(DumpManagerKt.PRIORITY_OPTIONS, str)) {
            return str;
        }
        throw new IllegalArgumentException();
    }
}
