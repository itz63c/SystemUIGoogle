package com.android.systemui.doze;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* compiled from: DozeLogger.kt */
final class DozeLogger$logMissedTick$2 extends Lambda implements Function1<LogMessage, String> {
    public static final DozeLogger$logMissedTick$2 INSTANCE = new DozeLogger$logMissedTick$2();

    DozeLogger$logMissedTick$2() {
        super(1);
    }

    public final String invoke(LogMessage logMessage) {
        Intrinsics.checkParameterIsNotNull(logMessage, "$receiver");
        StringBuilder sb = new StringBuilder();
        sb.append("Missed AOD time tick by ");
        sb.append(logMessage.getStr1());
        return sb.toString();
    }
}