package com.android.systemui.dump;

import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* compiled from: DumpManager.kt */
final class DumpManager$parseArgs$2 extends Lambda implements Function1<String, Integer> {
    public static final DumpManager$parseArgs$2 INSTANCE = new DumpManager$parseArgs$2();

    DumpManager$parseArgs$2() {
        super(1);
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj) {
        return Integer.valueOf(invoke((String) obj));
    }

    public final int invoke(String str) {
        Intrinsics.checkParameterIsNotNull(str, "it");
        return Integer.parseInt(str);
    }
}
