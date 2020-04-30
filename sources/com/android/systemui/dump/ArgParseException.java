package com.android.systemui.dump;

import kotlin.jvm.internal.Intrinsics;

/* compiled from: DumpManager.kt */
public final class ArgParseException extends Exception {
    public ArgParseException(String str) {
        Intrinsics.checkParameterIsNotNull(str, "message");
        super(str);
    }
}
