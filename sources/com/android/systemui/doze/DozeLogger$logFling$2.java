package com.android.systemui.doze;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* compiled from: DozeLogger.kt */
final class DozeLogger$logFling$2 extends Lambda implements Function1<LogMessage, String> {
    public static final DozeLogger$logFling$2 INSTANCE = new DozeLogger$logFling$2();

    DozeLogger$logFling$2() {
        super(1);
    }

    public final String invoke(LogMessage logMessage) {
        Intrinsics.checkParameterIsNotNull(logMessage, "$receiver");
        StringBuilder sb = new StringBuilder();
        sb.append("Fling expand=");
        sb.append(logMessage.getBool1());
        sb.append(" aboveThreshold=");
        sb.append(logMessage.getBool2());
        sb.append(" thresholdNeeded=");
        sb.append(logMessage.getBool3());
        sb.append(' ');
        sb.append("screenOnFromTouch=");
        sb.append(logMessage.getBool4());
        return sb.toString();
    }
}
