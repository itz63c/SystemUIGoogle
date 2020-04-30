package com.android.systemui.statusbar.notification.stack;

import com.android.systemui.statusbar.NotificationRemoveInterceptor;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

/* renamed from: com.android.systemui.statusbar.notification.stack.ForegroundServiceSectionController$sam$com_android_systemui_statusbar_NotificationRemoveInterceptor$0 */
/* compiled from: ForegroundServiceSectionController.kt */
final class C1335x60a9eb70 implements NotificationRemoveInterceptor {
    private final /* synthetic */ Function3 function;

    C1335x60a9eb70(Function3 function3) {
        this.function = function3;
    }

    public final /* synthetic */ boolean onNotificationRemoveRequested(String str, NotificationEntry notificationEntry, int i) {
        Intrinsics.checkParameterIsNotNull(str, "key");
        Object invoke = this.function.invoke(str, notificationEntry, Integer.valueOf(i));
        Intrinsics.checkExpressionValueIsNotNull(invoke, "invoke(...)");
        return ((Boolean) invoke).booleanValue();
    }
}
