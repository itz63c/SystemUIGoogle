package com.android.systemui.statusbar.notification;

import com.android.systemui.log.LogBuffer;
import com.android.systemui.log.LogLevel;
import com.android.systemui.log.LogMessageImpl;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: NotificationEntryManagerLogger.kt */
public final class NotificationEntryManagerLogger {
    private final LogBuffer buffer;

    public NotificationEntryManagerLogger(LogBuffer logBuffer) {
        Intrinsics.checkParameterIsNotNull(logBuffer, "buffer");
        this.buffer = logBuffer;
    }

    public final void logNotifAdded(String str) {
        Intrinsics.checkParameterIsNotNull(str, "key");
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("NotificationEntryMgr", LogLevel.INFO, NotificationEntryManagerLogger$logNotifAdded$2.INSTANCE);
        obtain.setStr1(str);
        logBuffer.push(obtain);
    }

    public final void logNotifUpdated(String str) {
        Intrinsics.checkParameterIsNotNull(str, "key");
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("NotificationEntryMgr", LogLevel.INFO, NotificationEntryManagerLogger$logNotifUpdated$2.INSTANCE);
        obtain.setStr1(str);
        logBuffer.push(obtain);
    }

    public final void logInflationAborted(String str, String str2, String str3) {
        Intrinsics.checkParameterIsNotNull(str, "key");
        Intrinsics.checkParameterIsNotNull(str2, "status");
        Intrinsics.checkParameterIsNotNull(str3, "reason");
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("NotificationEntryMgr", LogLevel.DEBUG, NotificationEntryManagerLogger$logInflationAborted$2.INSTANCE);
        obtain.setStr1(str);
        obtain.setStr2(str2);
        obtain.setStr3(str3);
        logBuffer.push(obtain);
    }

    public final void logNotifInflated(String str, boolean z) {
        Intrinsics.checkParameterIsNotNull(str, "key");
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("NotificationEntryMgr", LogLevel.DEBUG, NotificationEntryManagerLogger$logNotifInflated$2.INSTANCE);
        obtain.setStr1(str);
        obtain.setBool1(z);
        logBuffer.push(obtain);
    }

    public final void logRemovalIntercepted(String str) {
        Intrinsics.checkParameterIsNotNull(str, "key");
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("NotificationEntryMgr", LogLevel.INFO, NotificationEntryManagerLogger$logRemovalIntercepted$2.INSTANCE);
        obtain.setStr1(str);
        logBuffer.push(obtain);
    }

    public final void logLifetimeExtended(String str, String str2, String str3) {
        Intrinsics.checkParameterIsNotNull(str, "key");
        Intrinsics.checkParameterIsNotNull(str2, "extenderName");
        Intrinsics.checkParameterIsNotNull(str3, "status");
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("NotificationEntryMgr", LogLevel.INFO, NotificationEntryManagerLogger$logLifetimeExtended$2.INSTANCE);
        obtain.setStr1(str);
        obtain.setStr2(str2);
        obtain.setStr3(str3);
        logBuffer.push(obtain);
    }

    public final void logNotifRemoved(String str, boolean z) {
        Intrinsics.checkParameterIsNotNull(str, "key");
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("NotificationEntryMgr", LogLevel.INFO, NotificationEntryManagerLogger$logNotifRemoved$2.INSTANCE);
        obtain.setStr1(str);
        obtain.setBool1(z);
        logBuffer.push(obtain);
    }

    public final void logFilterAndSort(String str) {
        Intrinsics.checkParameterIsNotNull(str, "reason");
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("NotificationEntryMgr", LogLevel.INFO, NotificationEntryManagerLogger$logFilterAndSort$2.INSTANCE);
        obtain.setStr1(str);
        logBuffer.push(obtain);
    }
}
