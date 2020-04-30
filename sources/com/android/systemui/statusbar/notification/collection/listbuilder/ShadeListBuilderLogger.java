package com.android.systemui.statusbar.notification.collection.listbuilder;

import com.android.systemui.log.LogBuffer;
import com.android.systemui.log.LogLevel;
import com.android.systemui.log.LogMessageImpl;
import com.android.systemui.statusbar.notification.collection.GroupEntry;
import com.android.systemui.statusbar.notification.collection.ListEntry;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ShadeListBuilderLogger.kt */
public final class ShadeListBuilderLogger {
    private final LogBuffer buffer;

    public ShadeListBuilderLogger(LogBuffer logBuffer) {
        Intrinsics.checkParameterIsNotNull(logBuffer, "buffer");
        this.buffer = logBuffer;
    }

    public final void logOnBuildList() {
        LogBuffer logBuffer = this.buffer;
        logBuffer.push(logBuffer.obtain("ShadeListBuilder", LogLevel.INFO, ShadeListBuilderLogger$logOnBuildList$2.INSTANCE));
    }

    public final void logStartBuildList(int i) {
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("ShadeListBuilder", LogLevel.INFO, ShadeListBuilderLogger$logStartBuildList$2.INSTANCE);
        obtain.setInt1(i);
        logBuffer.push(obtain);
    }

    public final void logEndBuildList(int i) {
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("ShadeListBuilder", LogLevel.INFO, ShadeListBuilderLogger$logEndBuildList$2.INSTANCE);
        obtain.setInt1(i);
        logBuffer.push(obtain);
    }

    public final void logPreGroupFilterInvalidated(String str, int i) {
        Intrinsics.checkParameterIsNotNull(str, "filterName");
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("ShadeListBuilder", LogLevel.DEBUG, ShadeListBuilderLogger$logPreGroupFilterInvalidated$2.INSTANCE);
        obtain.setStr1(str);
        obtain.setInt1(i);
        logBuffer.push(obtain);
    }

    public final void logPromoterInvalidated(String str, int i) {
        Intrinsics.checkParameterIsNotNull(str, "name");
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("ShadeListBuilder", LogLevel.DEBUG, ShadeListBuilderLogger$logPromoterInvalidated$2.INSTANCE);
        obtain.setStr1(str);
        obtain.setInt1(i);
        logBuffer.push(obtain);
    }

    public final void logNotifSectionInvalidated(String str, int i) {
        Intrinsics.checkParameterIsNotNull(str, "name");
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("ShadeListBuilder", LogLevel.DEBUG, ShadeListBuilderLogger$logNotifSectionInvalidated$2.INSTANCE);
        obtain.setStr1(str);
        obtain.setInt1(i);
        logBuffer.push(obtain);
    }

    public final void logFinalizeFilterInvalidated(String str, int i) {
        Intrinsics.checkParameterIsNotNull(str, "name");
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("ShadeListBuilder", LogLevel.DEBUG, ShadeListBuilderLogger$logFinalizeFilterInvalidated$2.INSTANCE);
        obtain.setStr1(str);
        obtain.setInt1(i);
        logBuffer.push(obtain);
    }

    public final void logDuplicateSummary(String str, String str2, String str3) {
        Intrinsics.checkParameterIsNotNull(str, "groupKey");
        Intrinsics.checkParameterIsNotNull(str2, "existingKey");
        Intrinsics.checkParameterIsNotNull(str3, "newKey");
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("ShadeListBuilder", LogLevel.WARNING, ShadeListBuilderLogger$logDuplicateSummary$2.INSTANCE);
        obtain.setStr1(str);
        obtain.setStr2(str2);
        obtain.setStr3(str3);
        logBuffer.push(obtain);
    }

    public final void logDuplicateTopLevelKey(String str) {
        Intrinsics.checkParameterIsNotNull(str, "topLevelKey");
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("ShadeListBuilder", LogLevel.WARNING, ShadeListBuilderLogger$logDuplicateTopLevelKey$2.INSTANCE);
        obtain.setStr1(str);
        logBuffer.push(obtain);
    }

    public final void logParentChanged(String str, String str2, String str3) {
        Intrinsics.checkParameterIsNotNull(str, "key");
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("ShadeListBuilder", LogLevel.INFO, ShadeListBuilderLogger$logParentChanged$2.INSTANCE);
        obtain.setStr1(str);
        obtain.setStr2(str2);
        obtain.setStr3(str3);
        logBuffer.push(obtain);
    }

    public final void logFilterChanged(String str, String str2, String str3) {
        Intrinsics.checkParameterIsNotNull(str, "key");
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("ShadeListBuilder", LogLevel.INFO, ShadeListBuilderLogger$logFilterChanged$2.INSTANCE);
        obtain.setStr1(str);
        obtain.setStr2(str2);
        obtain.setStr3(str3);
        logBuffer.push(obtain);
    }

    public final void logPromoterChanged(String str, String str2, String str3) {
        Intrinsics.checkParameterIsNotNull(str, "key");
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("ShadeListBuilder", LogLevel.INFO, ShadeListBuilderLogger$logPromoterChanged$2.INSTANCE);
        obtain.setStr1(str);
        obtain.setStr2(str2);
        obtain.setStr3(str3);
        logBuffer.push(obtain);
    }

    public final void logSectionChanged(String str, String str2, int i, String str3, int i2) {
        Intrinsics.checkParameterIsNotNull(str, "key");
        Intrinsics.checkParameterIsNotNull(str3, "section");
        LogBuffer logBuffer = this.buffer;
        LogMessageImpl obtain = logBuffer.obtain("ShadeListBuilder", LogLevel.INFO, ShadeListBuilderLogger$logSectionChanged$2.INSTANCE);
        obtain.setStr1(str);
        obtain.setStr2(str3);
        obtain.setInt1(i2);
        obtain.setStr3(str2);
        obtain.setInt2(i);
        logBuffer.push(obtain);
    }

    public final void logFinalList(List<? extends ListEntry> list) {
        Intrinsics.checkParameterIsNotNull(list, "entries");
        LogBuffer logBuffer = this.buffer;
        String str = "ShadeListBuilder";
        LogMessageImpl obtain = logBuffer.obtain(str, LogLevel.DEBUG, ShadeListBuilderLogger$logFinalList$2.INSTANCE);
        obtain.setInt1(list.size());
        logBuffer.push(obtain);
        if (list.isEmpty()) {
            LogBuffer logBuffer2 = this.buffer;
            logBuffer2.push(logBuffer2.obtain(str, LogLevel.DEBUG, ShadeListBuilderLogger$logFinalList$4.INSTANCE));
        }
        int size = list.size();
        for (int i = 0; i < size; i++) {
            ListEntry listEntry = (ListEntry) list.get(i);
            LogBuffer logBuffer3 = this.buffer;
            LogMessageImpl obtain2 = logBuffer3.obtain(str, LogLevel.DEBUG, ShadeListBuilderLogger$logFinalList$6.INSTANCE);
            obtain2.setInt1(i);
            obtain2.setStr1(listEntry.getKey());
            logBuffer3.push(obtain2);
            if (listEntry instanceof GroupEntry) {
                GroupEntry groupEntry = (GroupEntry) listEntry;
                NotificationEntry summary = groupEntry.getSummary();
                if (summary != null) {
                    LogBuffer logBuffer4 = this.buffer;
                    LogMessageImpl obtain3 = logBuffer4.obtain(str, LogLevel.DEBUG, ShadeListBuilderLogger$logFinalList$7$2.INSTANCE);
                    Intrinsics.checkExpressionValueIsNotNull(summary, "it");
                    obtain3.setStr1(summary.getKey());
                    logBuffer4.push(obtain3);
                }
                List children = groupEntry.getChildren();
                Intrinsics.checkExpressionValueIsNotNull(children, "entry.children");
                int size2 = children.size();
                for (int i2 = 0; i2 < size2; i2++) {
                    NotificationEntry notificationEntry = (NotificationEntry) groupEntry.getChildren().get(i2);
                    LogBuffer logBuffer5 = this.buffer;
                    LogMessageImpl obtain4 = logBuffer5.obtain(str, LogLevel.DEBUG, ShadeListBuilderLogger$logFinalList$9.INSTANCE);
                    obtain4.setInt1(i2);
                    Intrinsics.checkExpressionValueIsNotNull(notificationEntry, "child");
                    obtain4.setStr1(notificationEntry.getKey());
                    logBuffer5.push(obtain4);
                }
            }
        }
    }
}
