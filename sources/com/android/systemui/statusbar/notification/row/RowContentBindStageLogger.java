package com.android.systemui.statusbar.notification.row;

import com.android.systemui.log.LogBuffer;
import com.android.systemui.log.LogLevel;
import com.android.systemui.log.LogMessageImpl;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: RowContentBindStageLogger.kt */
public final class RowContentBindStageLogger {
    private final LogBuffer buffer;

    public RowContentBindStageLogger(LogBuffer logBuffer) {
        Intrinsics.checkParameterIsNotNull(logBuffer, "buffer");
        this.buffer = logBuffer;
    }

    public final void logStageParams(String str, String str2) {
        Intrinsics.checkParameterIsNotNull(str, "notifKey");
        Intrinsics.checkParameterIsNotNull(str2, "stageParams");
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("RowContentBindStage", LogLevel.INFO, RowContentBindStageLogger$logStageParams$2.INSTANCE);
        obtain.setStr1(str);
        obtain.setStr2(str2);
        logBuffer.push(obtain);
    }
}
