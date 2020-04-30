package com.android.systemui.statusbar.notification.collection.coalescer;

import com.android.systemui.log.LogBuffer;
import com.android.systemui.log.LogLevel;
import com.android.systemui.log.LogMessageImpl;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: GroupCoalescerLogger.kt */
public final class GroupCoalescerLogger {
    private final LogBuffer buffer;

    public GroupCoalescerLogger(LogBuffer logBuffer) {
        Intrinsics.checkParameterIsNotNull(logBuffer, "buffer");
        this.buffer = logBuffer;
    }

    public final void logEventCoalesced(String str) {
        Intrinsics.checkParameterIsNotNull(str, "key");
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("GroupCoalescer", LogLevel.INFO, GroupCoalescerLogger$logEventCoalesced$2.INSTANCE);
        obtain.setStr1(str);
        logBuffer.push(obtain);
    }

    public final void logEmitBatch(String str) {
        Intrinsics.checkParameterIsNotNull(str, "groupKey");
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("GroupCoalescer", LogLevel.DEBUG, GroupCoalescerLogger$logEmitBatch$2.INSTANCE);
        obtain.setStr1(str);
        logBuffer.push(obtain);
    }

    public final void logEarlyEmit(String str, String str2) {
        Intrinsics.checkParameterIsNotNull(str, "modifiedKey");
        Intrinsics.checkParameterIsNotNull(str2, "groupKey");
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("GroupCoalescer", LogLevel.DEBUG, GroupCoalescerLogger$logEarlyEmit$2.INSTANCE);
        obtain.setStr1(str);
        obtain.setStr2(str2);
        logBuffer.push(obtain);
    }

    public final void logMaxBatchTimeout(String str, String str2) {
        Intrinsics.checkParameterIsNotNull(str, "modifiedKey");
        Intrinsics.checkParameterIsNotNull(str2, "groupKey");
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("GroupCoalescer", LogLevel.INFO, GroupCoalescerLogger$logMaxBatchTimeout$2.INSTANCE);
        obtain.setStr1(str);
        obtain.setStr2(str2);
        logBuffer.push(obtain);
    }

    public final void logMissingRanking(String str) {
        Intrinsics.checkParameterIsNotNull(str, "forKey");
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("GroupCoalescer", LogLevel.WARNING, GroupCoalescerLogger$logMissingRanking$2.INSTANCE);
        obtain.setStr1(str);
        logBuffer.push(obtain);
    }
}
