package com.android.systemui.log;

import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* compiled from: LogMessageImpl.kt */
final class LogMessageImplKt$DEFAULT_RENDERER$1 extends Lambda implements Function1<LogMessage, String> {
    public static final LogMessageImplKt$DEFAULT_RENDERER$1 INSTANCE = new LogMessageImplKt$DEFAULT_RENDERER$1();

    LogMessageImplKt$DEFAULT_RENDERER$1() {
        super(1);
    }

    public final String invoke(LogMessage logMessage) {
        Intrinsics.checkParameterIsNotNull(logMessage, "$receiver");
        StringBuilder sb = new StringBuilder();
        sb.append("Unknown message: ");
        sb.append(logMessage);
        return sb.toString();
    }
}
