package com.android.systemui.statusbar.notification.collection.listbuilder;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* compiled from: ShadeListBuilderLogger.kt */
final class ShadeListBuilderLogger$logPromoterInvalidated$2 extends Lambda implements Function1<LogMessage, String> {
    public static final ShadeListBuilderLogger$logPromoterInvalidated$2 INSTANCE = new ShadeListBuilderLogger$logPromoterInvalidated$2();

    ShadeListBuilderLogger$logPromoterInvalidated$2() {
        super(1);
    }

    public final String invoke(LogMessage logMessage) {
        Intrinsics.checkParameterIsNotNull(logMessage, "$receiver");
        StringBuilder sb = new StringBuilder();
        sb.append("NotifPromoter \"");
        sb.append(logMessage.getStr1());
        sb.append("\" invalidated; pipeline state is ");
        sb.append(logMessage.getInt1());
        return sb.toString();
    }
}
