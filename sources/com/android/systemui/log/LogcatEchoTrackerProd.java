package com.android.systemui.log;

import kotlin.jvm.internal.Intrinsics;

/* compiled from: LogcatEchoTrackerProd.kt */
public final class LogcatEchoTrackerProd implements LogcatEchoTracker {
    public boolean isBufferLoggable(String str, LogLevel logLevel) {
        Intrinsics.checkParameterIsNotNull(str, "bufferName");
        Intrinsics.checkParameterIsNotNull(logLevel, "level");
        return logLevel.compareTo(LogLevel.WARNING) >= 0;
    }

    public boolean isTagLoggable(String str, LogLevel logLevel) {
        Intrinsics.checkParameterIsNotNull(str, "tagName");
        Intrinsics.checkParameterIsNotNull(logLevel, "level");
        return logLevel.compareTo(LogLevel.WARNING) >= 0;
    }
}
