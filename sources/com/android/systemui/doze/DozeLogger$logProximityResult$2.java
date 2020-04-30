package com.android.systemui.doze;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* compiled from: DozeLogger.kt */
final class DozeLogger$logProximityResult$2 extends Lambda implements Function1<LogMessage, String> {
    public static final DozeLogger$logProximityResult$2 INSTANCE = new DozeLogger$logProximityResult$2();

    DozeLogger$logProximityResult$2() {
        super(1);
    }

    public final String invoke(LogMessage logMessage) {
        Intrinsics.checkParameterIsNotNull(logMessage, "$receiver");
        StringBuilder sb = new StringBuilder();
        sb.append("Proximity result reason=");
        sb.append(DozeLog.reasonToString(logMessage.getInt1()));
        sb.append(" near=");
        sb.append(logMessage.getBool1());
        sb.append(" millis=");
        sb.append(logMessage.getLong1());
        return sb.toString();
    }
}
