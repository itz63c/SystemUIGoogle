package com.android.systemui.doze;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* compiled from: DozeLogger.kt */
final class DozeLogger$logSensorTriggered$2 extends Lambda implements Function1<LogMessage, String> {
    public static final DozeLogger$logSensorTriggered$2 INSTANCE = new DozeLogger$logSensorTriggered$2();

    DozeLogger$logSensorTriggered$2() {
        super(1);
    }

    public final String invoke(LogMessage logMessage) {
        Intrinsics.checkParameterIsNotNull(logMessage, "$receiver");
        StringBuilder sb = new StringBuilder();
        sb.append("Sensor triggered, type=");
        sb.append(DozeLog.reasonToString(logMessage.getInt1()));
        return sb.toString();
    }
}
