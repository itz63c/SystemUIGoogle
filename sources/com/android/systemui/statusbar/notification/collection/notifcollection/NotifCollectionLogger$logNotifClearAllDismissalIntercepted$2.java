package com.android.systemui.statusbar.notification.collection.notifcollection;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* compiled from: NotifCollectionLogger.kt */
final class NotifCollectionLogger$logNotifClearAllDismissalIntercepted$2 extends Lambda implements Function1<LogMessage, String> {
    public static final NotifCollectionLogger$logNotifClearAllDismissalIntercepted$2 INSTANCE = new NotifCollectionLogger$logNotifClearAllDismissalIntercepted$2();

    NotifCollectionLogger$logNotifClearAllDismissalIntercepted$2() {
        super(1);
    }

    public final String invoke(LogMessage logMessage) {
        Intrinsics.checkParameterIsNotNull(logMessage, "$receiver");
        StringBuilder sb = new StringBuilder();
        sb.append("CLEAR ALL DISMISSAL INTERCEPTED ");
        sb.append(logMessage.getStr1());
        return sb.toString();
    }
}
