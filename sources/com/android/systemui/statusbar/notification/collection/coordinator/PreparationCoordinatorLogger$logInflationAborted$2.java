package com.android.systemui.statusbar.notification.collection.coordinator;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* compiled from: PreparationCoordinatorLogger.kt */
final class PreparationCoordinatorLogger$logInflationAborted$2 extends Lambda implements Function1<LogMessage, String> {
    public static final PreparationCoordinatorLogger$logInflationAborted$2 INSTANCE = new PreparationCoordinatorLogger$logInflationAborted$2();

    PreparationCoordinatorLogger$logInflationAborted$2() {
        super(1);
    }

    public final String invoke(LogMessage logMessage) {
        Intrinsics.checkParameterIsNotNull(logMessage, "$receiver");
        StringBuilder sb = new StringBuilder();
        sb.append("NOTIF INFLATION ABORTED ");
        sb.append(logMessage.getStr1());
        sb.append(" reason=");
        sb.append(logMessage.getStr2());
        return sb.toString();
    }
}
