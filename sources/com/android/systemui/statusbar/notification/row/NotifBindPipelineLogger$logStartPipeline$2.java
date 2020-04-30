package com.android.systemui.statusbar.notification.row;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* compiled from: NotifBindPipelineLogger.kt */
final class NotifBindPipelineLogger$logStartPipeline$2 extends Lambda implements Function1<LogMessage, String> {
    public static final NotifBindPipelineLogger$logStartPipeline$2 INSTANCE = new NotifBindPipelineLogger$logStartPipeline$2();

    NotifBindPipelineLogger$logStartPipeline$2() {
        super(1);
    }

    public final String invoke(LogMessage logMessage) {
        Intrinsics.checkParameterIsNotNull(logMessage, "$receiver");
        StringBuilder sb = new StringBuilder();
        sb.append("Start pipeline for notif: ");
        sb.append(logMessage.getStr1());
        return sb.toString();
    }
}
