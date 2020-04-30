package com.android.systemui.statusbar.notification.collection;

import android.service.notification.StatusBarNotification;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: NotificationRankingManager.kt */
public final class NotificationRankingManagerKt {
    /* access modifiers changed from: private */
    public static final boolean isSystemMax(NotificationEntry notificationEntry) {
        if (notificationEntry.getImportance() >= 4) {
            StatusBarNotification sbn = notificationEntry.getSbn();
            Intrinsics.checkExpressionValueIsNotNull(sbn, "sbn");
            if (isSystemNotification(sbn)) {
                return true;
            }
        }
        return false;
    }

    private static final boolean isSystemNotification(StatusBarNotification statusBarNotification) {
        if (!Intrinsics.areEqual((Object) "android", (Object) statusBarNotification.getPackageName())) {
            if (!Intrinsics.areEqual((Object) "com.android.systemui", (Object) statusBarNotification.getPackageName())) {
                return false;
            }
        }
        return true;
    }
}
