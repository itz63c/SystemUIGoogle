package com.android.systemui.statusbar.notification.collection.notifcollection;

import android.service.notification.NotificationListenerService.RankingMap;
import com.android.systemui.log.LogBuffer;
import com.android.systemui.log.LogLevel;
import com.android.systemui.log.LogMessageImpl;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: NotifCollectionLogger.kt */
public final class NotifCollectionLogger {
    private final LogBuffer buffer;

    public NotifCollectionLogger(LogBuffer logBuffer) {
        Intrinsics.checkParameterIsNotNull(logBuffer, "buffer");
        this.buffer = logBuffer;
    }

    public final void logNotifPosted(String str) {
        Intrinsics.checkParameterIsNotNull(str, "key");
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("NotifCollection", LogLevel.INFO, NotifCollectionLogger$logNotifPosted$2.INSTANCE);
        obtain.setStr1(str);
        logBuffer.push(obtain);
    }

    public final void logNotifGroupPosted(String str, int i) {
        Intrinsics.checkParameterIsNotNull(str, "groupKey");
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("NotifCollection", LogLevel.INFO, NotifCollectionLogger$logNotifGroupPosted$2.INSTANCE);
        obtain.setStr1(str);
        obtain.setInt1(i);
        logBuffer.push(obtain);
    }

    public final void logNotifUpdated(String str) {
        Intrinsics.checkParameterIsNotNull(str, "key");
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("NotifCollection", LogLevel.INFO, NotifCollectionLogger$logNotifUpdated$2.INSTANCE);
        obtain.setStr1(str);
        logBuffer.push(obtain);
    }

    public final void logNotifRemoved(String str, int i) {
        Intrinsics.checkParameterIsNotNull(str, "key");
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("NotifCollection", LogLevel.INFO, NotifCollectionLogger$logNotifRemoved$2.INSTANCE);
        obtain.setStr1(str);
        obtain.setInt1(i);
        logBuffer.push(obtain);
    }

    public final void logNotifDismissed(String str) {
        Intrinsics.checkParameterIsNotNull(str, "key");
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("NotifCollection", LogLevel.INFO, NotifCollectionLogger$logNotifDismissed$2.INSTANCE);
        obtain.setStr1(str);
        logBuffer.push(obtain);
    }

    public final void logNotifDismissedIntercepted(String str) {
        Intrinsics.checkParameterIsNotNull(str, "key");
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("NotifCollection", LogLevel.INFO, NotifCollectionLogger$logNotifDismissedIntercepted$2.INSTANCE);
        obtain.setStr1(str);
        logBuffer.push(obtain);
    }

    public final void logNotifClearAllDismissalIntercepted(String str) {
        Intrinsics.checkParameterIsNotNull(str, "key");
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("NotifCollection", LogLevel.INFO, NotifCollectionLogger$logNotifClearAllDismissalIntercepted$2.INSTANCE);
        obtain.setStr1(str);
        logBuffer.push(obtain);
    }

    public final void logRankingMissing(String str, RankingMap rankingMap) {
        String[] orderedKeys;
        Intrinsics.checkParameterIsNotNull(str, "key");
        Intrinsics.checkParameterIsNotNull(rankingMap, "rankingMap");
        LogBuffer logBuffer = this.buffer;
        String str2 = "NotifCollection";
        LogMessageImpl obtain = logBuffer.obtain(str2, LogLevel.WARNING, NotifCollectionLogger$logRankingMissing$2.INSTANCE);
        obtain.setStr1(str);
        logBuffer.push(obtain);
        LogBuffer logBuffer2 = this.buffer;
        logBuffer2.push(logBuffer2.obtain(str2, LogLevel.DEBUG, NotifCollectionLogger$logRankingMissing$4.INSTANCE));
        for (String str3 : rankingMap.getOrderedKeys()) {
            LogBuffer logBuffer3 = this.buffer;
            LogMessageImpl obtain2 = logBuffer3.obtain(str2, LogLevel.DEBUG, NotifCollectionLogger$logRankingMissing$6.INSTANCE);
            obtain2.setStr1(str3);
            logBuffer3.push(obtain2);
        }
    }
}
