package com.android.systemui.statusbar.notification;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* compiled from: NotificationEntryManagerLogger.kt */
final class NotificationEntryManagerLogger$logRemovalIntercepted$2 extends Lambda implements Function1<LogMessage, String> {
    public static final NotificationEntryManagerLogger$logRemovalIntercepted$2 INSTANCE = new NotificationEntryManagerLogger$logRemovalIntercepted$2();

    NotificationEntryManagerLogger$logRemovalIntercepted$2() {
        super(1);
    }

    public final String invoke(LogMessage logMessage) {
        Intrinsics.checkParameterIsNotNull(logMessage, "$receiver");
        StringBuilder sb = new StringBuilder();
        sb.append("NOTIF REMOVE INTERCEPTED for ");
        sb.append(logMessage.getStr1());
        return sb.toString();
    }
}
