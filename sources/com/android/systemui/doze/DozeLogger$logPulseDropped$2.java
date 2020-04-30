package com.android.systemui.doze;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* compiled from: DozeLogger.kt */
final class DozeLogger$logPulseDropped$2 extends Lambda implements Function1<LogMessage, String> {
    public static final DozeLogger$logPulseDropped$2 INSTANCE = new DozeLogger$logPulseDropped$2();

    DozeLogger$logPulseDropped$2() {
        super(1);
    }

    public final String invoke(LogMessage logMessage) {
        Intrinsics.checkParameterIsNotNull(logMessage, "$receiver");
        StringBuilder sb = new StringBuilder();
        sb.append("Pulse dropped, pulsePending=");
        sb.append(logMessage.getBool1());
        sb.append(" state=");
        sb.append(logMessage.getStr1());
        sb.append(" blocked=");
        sb.append(logMessage.getBool2());
        return sb.toString();
    }
}
