package com.android.systemui.statusbar.notification;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* compiled from: NotificationEntryManagerLogger.kt */
final class NotificationEntryManagerLogger$logNotifRemoved$2 extends Lambda implements Function1<LogMessage, String> {
    public static final NotificationEntryManagerLogger$logNotifRemoved$2 INSTANCE = new NotificationEntryManagerLogger$logNotifRemoved$2();

    NotificationEntryManagerLogger$logNotifRemoved$2() {
        super(1);
    }

    public final String invoke(LogMessage logMessage) {
        Intrinsics.checkParameterIsNotNull(logMessage, "$receiver");
        StringBuilder sb = new StringBuilder();
        sb.append("NOTIF REMOVED ");
        sb.append(logMessage.getStr1());
        sb.append(" removedByUser=");
        sb.append(logMessage.getBool1());
        return sb.toString();
    }
}
