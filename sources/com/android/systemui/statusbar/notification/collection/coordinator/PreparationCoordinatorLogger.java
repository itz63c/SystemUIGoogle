package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.systemui.log.LogBuffer;
import com.android.systemui.log.LogLevel;
import com.android.systemui.log.LogMessageImpl;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: PreparationCoordinatorLogger.kt */
public final class PreparationCoordinatorLogger {
    private final LogBuffer buffer;

    public PreparationCoordinatorLogger(LogBuffer logBuffer) {
        Intrinsics.checkParameterIsNotNull(logBuffer, "buffer");
        this.buffer = logBuffer;
    }

    public final void logNotifInflated(String str) {
        Intrinsics.checkParameterIsNotNull(str, "key");
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("PreparationCoordinator", LogLevel.DEBUG, PreparationCoordinatorLogger$logNotifInflated$2.INSTANCE);
        obtain.setStr1(str);
        logBuffer.push(obtain);
    }

    public final void logInflationAborted(String str, String str2) {
        Intrinsics.checkParameterIsNotNull(str, "key");
        Intrinsics.checkParameterIsNotNull(str2, "reason");
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("PreparationCoordinator", LogLevel.DEBUG, PreparationCoordinatorLogger$logInflationAborted$2.INSTANCE);
        obtain.setStr1(str);
        obtain.setStr2(str2);
        logBuffer.push(obtain);
    }
}
