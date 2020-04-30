package com.android.systemui.log;

/* compiled from: LogcatEchoTracker.kt */
public interface LogcatEchoTracker {
    boolean isBufferLoggable(String str, LogLevel logLevel);

    boolean isTagLoggable(String str, LogLevel logLevel);
}
