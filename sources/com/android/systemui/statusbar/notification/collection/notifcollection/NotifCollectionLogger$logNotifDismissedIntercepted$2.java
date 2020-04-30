package com.android.systemui.statusbar.notification.collection.notifcollection;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* compiled from: NotifCollectionLogger.kt */
final class NotifCollectionLogger$logNotifDismissedIntercepted$2 extends Lambda implements Function1<LogMessage, String> {
    public static final NotifCollectionLogger$logNotifDismissedIntercepted$2 INSTANCE = new NotifCollectionLogger$logNotifDismissedIntercepted$2();

    NotifCollectionLogger$logNotifDismissedIntercepted$2() {
        super(1);
    }

    public final String invoke(LogMessage logMessage) {
        Intrinsics.checkParameterIsNotNull(logMessage, "$receiver");
        StringBuilder sb = new StringBuilder();
        sb.append("DISMISS INTERCEPTED ");
        sb.append(logMessage.getStr1());
        return sb.toString();
    }
}
