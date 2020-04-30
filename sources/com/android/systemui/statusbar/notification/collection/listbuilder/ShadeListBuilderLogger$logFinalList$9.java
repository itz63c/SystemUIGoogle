package com.android.systemui.statusbar.notification.collection.listbuilder;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* compiled from: ShadeListBuilderLogger.kt */
final class ShadeListBuilderLogger$logFinalList$9 extends Lambda implements Function1<LogMessage, String> {
    public static final ShadeListBuilderLogger$logFinalList$9 INSTANCE = new ShadeListBuilderLogger$logFinalList$9();

    ShadeListBuilderLogger$logFinalList$9() {
        super(1);
    }

    public final String invoke(LogMessage logMessage) {
        Intrinsics.checkParameterIsNotNull(logMessage, "$receiver");
        StringBuilder sb = new StringBuilder();
        sb.append("  [");
        sb.append(logMessage.getInt1());
        sb.append("] ");
        sb.append(logMessage.getStr1());
        return sb.toString();
    }
}
