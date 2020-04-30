package com.android.systemui.statusbar.notification;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* compiled from: NotificationEntryManagerLogger.kt */
final class NotificationEntryManagerLogger$logInflationAborted$2 extends Lambda implements Function1<LogMessage, String> {
    public static final NotificationEntryManagerLogger$logInflationAborted$2 INSTANCE = new NotificationEntryManagerLogger$logInflationAborted$2();

    NotificationEntryManagerLogger$logInflationAborted$2() {
        super(1);
    }

    public final String invoke(LogMessage logMessage) {
        Intrinsics.checkParameterIsNotNull(logMessage, "$receiver");
        StringBuilder sb = new StringBuilder();
        sb.append("NOTIF INFLATION ABORTED ");
        sb.append(logMessage.getStr1());
        sb.append(" notifStatus=");
        sb.append(logMessage.getStr2());
        sb.append(" reason=");
        sb.append(logMessage.getStr3());
        return sb.toString();
    }
}
