package com.android.systemui.statusbar.notification.collection.coalescer;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* compiled from: GroupCoalescerLogger.kt */
final class GroupCoalescerLogger$logMaxBatchTimeout$2 extends Lambda implements Function1<LogMessage, String> {
    public static final GroupCoalescerLogger$logMaxBatchTimeout$2 INSTANCE = new GroupCoalescerLogger$logMaxBatchTimeout$2();

    GroupCoalescerLogger$logMaxBatchTimeout$2() {
        super(1);
    }

    public final String invoke(LogMessage logMessage) {
        Intrinsics.checkParameterIsNotNull(logMessage, "$receiver");
        StringBuilder sb = new StringBuilder();
        sb.append("Modification of notif ");
        sb.append(logMessage.getStr1());
        sb.append(" triggered TIMEOUT emit of batched group ");
        sb.append(logMessage.getStr2());
        return sb.toString();
    }
}