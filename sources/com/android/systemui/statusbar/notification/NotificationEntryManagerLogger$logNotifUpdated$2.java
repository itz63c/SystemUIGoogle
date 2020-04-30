package com.android.systemui.statusbar.notification;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* compiled from: NotificationEntryManagerLogger.kt */
final class NotificationEntryManagerLogger$logNotifUpdated$2 extends Lambda implements Function1<LogMessage, String> {
    public static final NotificationEntryManagerLogger$logNotifUpdated$2 INSTANCE = new NotificationEntryManagerLogger$logNotifUpdated$2();

    NotificationEntryManagerLogger$logNotifUpdated$2() {
        super(1);
    }

    public final String invoke(LogMessage logMessage) {
        Intrinsics.checkParameterIsNotNull(logMessage, "$receiver");
        StringBuilder sb = new StringBuilder();
        sb.append("NOTIF UPDATED ");
        sb.append(logMessage.getStr1());
        return sb.toString();
    }
}
