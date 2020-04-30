package com.android.systemui.statusbar.notification.row;

import com.android.systemui.log.LogBuffer;
import com.android.systemui.log.LogLevel;
import com.android.systemui.log.LogMessageImpl;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: NotifBindPipelineLogger.kt */
public final class NotifBindPipelineLogger {
    private final LogBuffer buffer;

    public NotifBindPipelineLogger(LogBuffer logBuffer) {
        Intrinsics.checkParameterIsNotNull(logBuffer, "buffer");
        this.buffer = logBuffer;
    }

    public final void logStageSet(String str) {
        Intrinsics.checkParameterIsNotNull(str, "stageName");
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("NotifBindPipeline", LogLevel.INFO, NotifBindPipelineLogger$logStageSet$2.INSTANCE);
        obtain.setStr1(str);
        logBuffer.push(obtain);
    }

    public final void logManagedRow(String str) {
        Intrinsics.checkParameterIsNotNull(str, "notifKey");
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("NotifBindPipeline", LogLevel.INFO, NotifBindPipelineLogger$logManagedRow$2.INSTANCE);
        obtain.setStr1(str);
        logBuffer.push(obtain);
    }

    public final void logStartPipeline(String str) {
        Intrinsics.checkParameterIsNotNull(str, "notifKey");
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("NotifBindPipeline", LogLevel.INFO, NotifBindPipelineLogger$logStartPipeline$2.INSTANCE);
        obtain.setStr1(str);
        logBuffer.push(obtain);
    }

    public final void logFinishedPipeline(String str, int i) {
        Intrinsics.checkParameterIsNotNull(str, "notifKey");
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("NotifBindPipeline", LogLevel.INFO, NotifBindPipelineLogger$logFinishedPipeline$2.INSTANCE);
        obtain.setStr1(str);
        obtain.setInt1(i);
        logBuffer.push(obtain);
    }
}
