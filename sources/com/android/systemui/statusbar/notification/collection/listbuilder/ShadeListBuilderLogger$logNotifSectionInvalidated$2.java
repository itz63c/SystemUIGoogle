package com.android.systemui.statusbar.notification.collection.listbuilder;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* compiled from: ShadeListBuilderLogger.kt */
final class ShadeListBuilderLogger$logNotifSectionInvalidated$2 extends Lambda implements Function1<LogMessage, String> {
    public static final ShadeListBuilderLogger$logNotifSectionInvalidated$2 INSTANCE = new ShadeListBuilderLogger$logNotifSectionInvalidated$2();

    ShadeListBuilderLogger$logNotifSectionInvalidated$2() {
        super(1);
    }

    public final String invoke(LogMessage logMessage) {
        Intrinsics.checkParameterIsNotNull(logMessage, "$receiver");
        StringBuilder sb = new StringBuilder();
        sb.append("NotifSection \"");
        sb.append(logMessage.getStr1());
        sb.append("\" invalidated; pipeline state is ");
        sb.append(logMessage.getInt1());
        return sb.toString();
    }
}
