package com.android.systemui.statusbar.notification.collection.listbuilder;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* compiled from: ShadeListBuilderLogger.kt */
final class ShadeListBuilderLogger$logDuplicateSummary$2 extends Lambda implements Function1<LogMessage, String> {
    public static final ShadeListBuilderLogger$logDuplicateSummary$2 INSTANCE = new ShadeListBuilderLogger$logDuplicateSummary$2();

    ShadeListBuilderLogger$logDuplicateSummary$2() {
        super(1);
    }

    public final String invoke(LogMessage logMessage) {
        Intrinsics.checkParameterIsNotNull(logMessage, "$receiver");
        StringBuilder sb = new StringBuilder();
        sb.append("Duplicate summary for group \"");
        sb.append(logMessage.getStr1());
        sb.append("\": \"");
        sb.append(logMessage.getStr2());
        sb.append("\" vs. \"");
        sb.append(logMessage.getStr3());
        sb.append('\"');
        return sb.toString();
    }
}
