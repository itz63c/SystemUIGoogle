package com.android.systemui.statusbar.notification.collection.notifcollection;

import com.android.systemui.log.LogMessage;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* compiled from: NotifCollectionLogger.kt */
final class NotifCollectionLogger$logNotifDismissed$2 extends Lambda implements Function1<LogMessage, String> {
    public static final NotifCollectionLogger$logNotifDismissed$2 INSTANCE = new NotifCollectionLogger$logNotifDismissed$2();

    NotifCollectionLogger$logNotifDismissed$2() {
        super(1);
    }

    public final String invoke(LogMessage logMessage) {
        Intrinsics.checkParameterIsNotNull(logMessage, "$receiver");
        StringBuilder sb = new StringBuilder();
        sb.append("DISMISSED ");
        sb.append(logMessage.getStr1());
        return sb.toString();
    }
}
