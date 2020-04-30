package com.android.systemui.log;

import android.util.Log;
import com.android.systemui.dump.DumpManager;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: LogBuffer.kt */
public final class LogBuffer {
    private final ArrayDeque<LogMessageImpl> buffer = new ArrayDeque<>();
    private final LogcatEchoTracker logcatEchoTracker;
    private final int maxLogs;
    private final String name;
    private final int poolSize;

    public final /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[LogLevel.values().length];
            $EnumSwitchMapping$0 = iArr;
            iArr[LogLevel.VERBOSE.ordinal()] = 1;
            $EnumSwitchMapping$0[LogLevel.DEBUG.ordinal()] = 2;
            $EnumSwitchMapping$0[LogLevel.INFO.ordinal()] = 3;
            $EnumSwitchMapping$0[LogLevel.WARNING.ordinal()] = 4;
            $EnumSwitchMapping$0[LogLevel.ERROR.ordinal()] = 5;
            $EnumSwitchMapping$0[LogLevel.WTF.ordinal()] = 6;
        }
    }

    public LogBuffer(String str, int i, int i2, LogcatEchoTracker logcatEchoTracker2) {
        Intrinsics.checkParameterIsNotNull(str, "name");
        Intrinsics.checkParameterIsNotNull(logcatEchoTracker2, "logcatEchoTracker");
        this.name = str;
        this.maxLogs = i;
        this.poolSize = i2;
        this.logcatEchoTracker = logcatEchoTracker2;
    }

    public final void attach(DumpManager dumpManager) {
        Intrinsics.checkParameterIsNotNull(dumpManager, "dumpManager");
        dumpManager.registerBuffer(this.name, this);
    }

    public final LogMessageImpl obtain(String str, LogLevel logLevel, Function1<? super LogMessage, String> function1) {
        LogMessageImpl logMessageImpl;
        Intrinsics.checkParameterIsNotNull(str, "tag");
        Intrinsics.checkParameterIsNotNull(logLevel, "level");
        Intrinsics.checkParameterIsNotNull(function1, "printer");
        synchronized (this.buffer) {
            if (this.buffer.size() > this.maxLogs - this.poolSize) {
                logMessageImpl = (LogMessageImpl) this.buffer.removeFirst();
            } else {
                logMessageImpl = LogMessageImpl.Factory.create();
            }
        }
        logMessageImpl.reset(str, logLevel, System.currentTimeMillis(), function1);
        Intrinsics.checkExpressionValueIsNotNull(logMessageImpl, "message");
        return logMessageImpl;
    }

    public final void push(LogMessage logMessage) {
        Intrinsics.checkParameterIsNotNull(logMessage, "message");
        synchronized (this.buffer) {
            if (this.buffer.size() == this.maxLogs) {
                StringBuilder sb = new StringBuilder();
                sb.append("LogBuffer ");
                sb.append(this.name);
                sb.append(" has exceeded its pool size");
                Log.e("LogBuffer", sb.toString());
                this.buffer.removeFirst();
            }
            this.buffer.add((LogMessageImpl) logMessage);
            if (this.logcatEchoTracker.isBufferLoggable(this.name, ((LogMessageImpl) logMessage).getLevel()) || this.logcatEchoTracker.isTagLoggable(((LogMessageImpl) logMessage).getTag(), ((LogMessageImpl) logMessage).getLevel())) {
                echoToLogcat(logMessage);
            }
            Unit unit = Unit.INSTANCE;
        }
    }

    public final void dump(PrintWriter printWriter, int i) {
        int i2;
        Intrinsics.checkParameterIsNotNull(printWriter, "pw");
        synchronized (this.buffer) {
            int i3 = 0;
            if (i <= 0) {
                i2 = 0;
            } else {
                i2 = this.buffer.size() - i;
            }
            for (LogMessageImpl logMessageImpl : this.buffer) {
                if (i3 >= i2) {
                    Intrinsics.checkExpressionValueIsNotNull(logMessageImpl, "message");
                    dumpMessage(logMessageImpl, printWriter);
                }
                i3++;
            }
            Unit unit = Unit.INSTANCE;
        }
    }

    private final void dumpMessage(LogMessage logMessage, PrintWriter printWriter) {
        printWriter.print(LogBufferKt.DATE_FORMAT.format(Long.valueOf(logMessage.getTimestamp())));
        String str = " ";
        printWriter.print(str);
        printWriter.print(logMessage.getLevel());
        printWriter.print(str);
        printWriter.print(logMessage.getTag());
        printWriter.print(str);
        printWriter.println((String) logMessage.getPrinter().invoke(logMessage));
    }

    private final void echoToLogcat(LogMessage logMessage) {
        String str = (String) logMessage.getPrinter().invoke(logMessage);
        switch (WhenMappings.$EnumSwitchMapping$0[logMessage.getLevel().ordinal()]) {
            case 1:
                Log.v(logMessage.getTag(), str);
                return;
            case 2:
                Log.d(logMessage.getTag(), str);
                return;
            case 3:
                Log.i(logMessage.getTag(), str);
                return;
            case 4:
                Log.w(logMessage.getTag(), str);
                return;
            case 5:
                Log.e(logMessage.getTag(), str);
                return;
            case 6:
                Log.wtf(logMessage.getTag(), str);
                return;
            default:
                return;
        }
    }
}
