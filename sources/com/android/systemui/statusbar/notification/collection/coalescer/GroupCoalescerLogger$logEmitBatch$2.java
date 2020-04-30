package com.android.systemui.statusbar.notification.collection.coalescer;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* compiled from: GroupCoalescerLogger.kt */
final class GroupCoalescerLogger$logEmitBatch$2 extends Lambda implements Function1<LogMessage, String> {
    public static final GroupCoalescerLogger$logEmitBatch$2 INSTANCE = new GroupCoalescerLogger$logEmitBatch$2();

    GroupCoalescerLogger$logEmitBatch$2() {
        super(1);
    }

    public final String invoke(LogMessage logMessage) {
        Intrinsics.checkParameterIsNotNull(logMessage, "$receiver");
        StringBuilder sb = new StringBuilder();
        sb.append("Emitting event batch for group ");
        sb.append(logMessage.getStr1());
        return sb.toString();
    }
}
