package com.android.systemui.statusbar.notification;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* compiled from: NotificationEntryManagerLogger.kt */
final class NotificationEntryManagerLogger$logLifetimeExtended$2 extends Lambda implements Function1<LogMessage, String> {
    public static final NotificationEntryManagerLogger$logLifetimeExtended$2 INSTANCE = new NotificationEntryManagerLogger$logLifetimeExtended$2();

    NotificationEntryManagerLogger$logLifetimeExtended$2() {
        super(1);
    }

    public final String invoke(LogMessage logMessage) {
        Intrinsics.checkParameterIsNotNull(logMessage, "$receiver");
        StringBuilder sb = new StringBuilder();
        sb.append("NOTIF LIFETIME EXTENDED ");
        sb.append(logMessage.getStr1());
        sb.append(" extender=");
        sb.append(logMessage.getStr2());
        sb.append(" status=");
        sb.append(logMessage.getStr3());
        return sb.toString();
    }
}
