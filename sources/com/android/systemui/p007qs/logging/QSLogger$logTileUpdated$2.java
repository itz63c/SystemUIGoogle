package com.android.systemui.p007qs.logging;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* renamed from: com.android.systemui.qs.logging.QSLogger$logTileUpdated$2 */
/* compiled from: QSLogger.kt */
final class QSLogger$logTileUpdated$2 extends Lambda implements Function1<LogMessage, String> {
    public static final QSLogger$logTileUpdated$2 INSTANCE = new QSLogger$logTileUpdated$2();

    QSLogger$logTileUpdated$2() {
        super(1);
    }

    public final String invoke(LogMessage logMessage) {
        String str;
        Intrinsics.checkParameterIsNotNull(logMessage, "$receiver");
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        sb.append(logMessage.getStr1());
        sb.append("] Tile updated. Label=");
        sb.append(logMessage.getStr2());
        sb.append(". State=");
        sb.append(logMessage.getInt1());
        sb.append(". Icon=");
        sb.append(logMessage.getStr3());
        sb.append('.');
        if (logMessage.getBool1()) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(" Activity in/out=");
            sb2.append(logMessage.getBool2());
            sb2.append('/');
            sb2.append(logMessage.getBool3());
            str = sb2.toString();
        } else {
            str = "";
        }
        sb.append(str);
        return sb.toString();
    }
}
