package com.android.systemui.statusbar.notification.collection.listbuilder;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* compiled from: ShadeListBuilderLogger.kt */
final class ShadeListBuilderLogger$logPreGroupFilterInvalidated$2 extends Lambda implements Function1<LogMessage, String> {
    public static final ShadeListBuilderLogger$logPreGroupFilterInvalidated$2 INSTANCE = new ShadeListBuilderLogger$logPreGroupFilterInvalidated$2();

    ShadeListBuilderLogger$logPreGroupFilterInvalidated$2() {
        super(1);
    }

    public final String invoke(LogMessage logMessage) {
        Intrinsics.checkParameterIsNotNull(logMessage, "$receiver");
        StringBuilder sb = new StringBuilder();
        sb.append("Pre-group NotifFilter \"");
        sb.append(logMessage.getStr1());
        sb.append("\" invalidated; pipeline state is ");
        sb.append(logMessage.getInt1());
        return sb.toString();
    }
}
