package com.android.systemui.statusbar.notification.collection.listbuilder;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* compiled from: ShadeListBuilderLogger.kt */
final class ShadeListBuilderLogger$logEndBuildList$2 extends Lambda implements Function1<LogMessage, String> {
    public static final ShadeListBuilderLogger$logEndBuildList$2 INSTANCE = new ShadeListBuilderLogger$logEndBuildList$2();

    ShadeListBuilderLogger$logEndBuildList$2() {
        super(1);
    }

    public final String invoke(LogMessage logMessage) {
        Intrinsics.checkParameterIsNotNull(logMessage, "$receiver");
        StringBuilder sb = new StringBuilder();
        sb.append("Finished building shade list (run #");
        sb.append(logMessage.getInt1());
        sb.append(')');
        return sb.toString();
    }
}
