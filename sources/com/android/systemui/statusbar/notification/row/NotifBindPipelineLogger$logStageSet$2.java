package com.android.systemui.statusbar.notification.row;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* compiled from: NotifBindPipelineLogger.kt */
final class NotifBindPipelineLogger$logStageSet$2 extends Lambda implements Function1<LogMessage, String> {
    public static final NotifBindPipelineLogger$logStageSet$2 INSTANCE = new NotifBindPipelineLogger$logStageSet$2();

    NotifBindPipelineLogger$logStageSet$2() {
        super(1);
    }

    public final String invoke(LogMessage logMessage) {
        Intrinsics.checkParameterIsNotNull(logMessage, "$receiver");
        StringBuilder sb = new StringBuilder();
        sb.append("Stage set: ");
        sb.append(logMessage.getStr1());
        return sb.toString();
    }
}
