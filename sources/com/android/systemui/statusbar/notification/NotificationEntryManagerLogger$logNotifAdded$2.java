package com.android.systemui.statusbar.notification;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* compiled from: NotificationEntryManagerLogger.kt */
final class NotificationEntryManagerLogger$logNotifAdded$2 extends Lambda implements Function1<LogMessage, String> {
    public static final NotificationEntryManagerLogger$logNotifAdded$2 INSTANCE = new NotificationEntryManagerLogger$logNotifAdded$2();

    NotificationEntryManagerLogger$logNotifAdded$2() {
        super(1);
    }

    public final String invoke(LogMessage logMessage) {
        Intrinsics.checkParameterIsNotNull(logMessage, "$receiver");
        StringBuilder sb = new StringBuilder();
        sb.append("NOTIF ADDED ");
        sb.append(logMessage.getStr1());
        return sb.toString();
    }
}
