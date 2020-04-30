package com.android.systemui.statusbar.notification.collection.listbuilder;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* compiled from: ShadeListBuilderLogger.kt */
final class ShadeListBuilderLogger$logSectionChanged$2 extends Lambda implements Function1<LogMessage, String> {
    public static final ShadeListBuilderLogger$logSectionChanged$2 INSTANCE = new ShadeListBuilderLogger$logSectionChanged$2();

    ShadeListBuilderLogger$logSectionChanged$2() {
        super(1);
    }

    public final String invoke(LogMessage logMessage) {
        Intrinsics.checkParameterIsNotNull(logMessage, "$receiver");
        String str = ": '";
        String str2 = "' (#";
        if (logMessage.getStr3() == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Section assigned for ");
            sb.append(logMessage.getStr1());
            sb.append(str);
            sb.append(logMessage.getStr2());
            sb.append(str2);
            sb.append(logMessage.getInt1());
            sb.append(')');
            return sb.toString();
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append("Section changed for ");
        sb2.append(logMessage.getStr1());
        sb2.append(str);
        sb2.append(logMessage.getStr3());
        sb2.append(str2);
        sb2.append(logMessage.getInt2());
        sb2.append(") -> '");
        sb2.append(logMessage.getStr2());
        sb2.append(str2);
        sb2.append(logMessage.getInt1());
        sb2.append(')');
        return sb2.toString();
    }
}
