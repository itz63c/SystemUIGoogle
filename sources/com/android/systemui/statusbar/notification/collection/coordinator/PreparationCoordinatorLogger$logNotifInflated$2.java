package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* compiled from: PreparationCoordinatorLogger.kt */
final class PreparationCoordinatorLogger$logNotifInflated$2 extends Lambda implements Function1<LogMessage, String> {
    public static final PreparationCoordinatorLogger$logNotifInflated$2 INSTANCE = new PreparationCoordinatorLogger$logNotifInflated$2();

    PreparationCoordinatorLogger$logNotifInflated$2() {
        super(1);
    }

    public final String invoke(LogMessage logMessage) {
        Intrinsics.checkParameterIsNotNull(logMessage, "$receiver");
        StringBuilder sb = new StringBuilder();
        sb.append("NOTIF INFLATED ");
        sb.append(logMessage.getStr1());
        return sb.toString();
    }
}
