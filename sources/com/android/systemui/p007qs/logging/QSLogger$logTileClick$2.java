package com.android.systemui.p007qs.logging;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* renamed from: com.android.systemui.qs.logging.QSLogger$logTileClick$2 */
/* compiled from: QSLogger.kt */
final class QSLogger$logTileClick$2 extends Lambda implements Function1<LogMessage, String> {
    public static final QSLogger$logTileClick$2 INSTANCE = new QSLogger$logTileClick$2();

    QSLogger$logTileClick$2() {
        super(1);
    }

    public final String invoke(LogMessage logMessage) {
        Intrinsics.checkParameterIsNotNull(logMessage, "$receiver");
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        sb.append(logMessage.getStr1());
        sb.append("] Tile clicked. StatusBarState=");
        sb.append(logMessage.getStr2());
        sb.append(". TileState=");
        sb.append(logMessage.getStr3());
        return sb.toString();
    }
}
