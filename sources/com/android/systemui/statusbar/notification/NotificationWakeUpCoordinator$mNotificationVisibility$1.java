package com.android.systemui.statusbar.notification;

import android.util.FloatProperty;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: NotificationWakeUpCoordinator.kt */
public final class NotificationWakeUpCoordinator$mNotificationVisibility$1 extends FloatProperty<NotificationWakeUpCoordinator> {
    NotificationWakeUpCoordinator$mNotificationVisibility$1(String str) {
        super(str);
    }

    public void setValue(NotificationWakeUpCoordinator notificationWakeUpCoordinator, float f) {
        Intrinsics.checkParameterIsNotNull(notificationWakeUpCoordinator, "coordinator");
        notificationWakeUpCoordinator.setVisibilityAmount(f);
    }

    public Float get(NotificationWakeUpCoordinator notificationWakeUpCoordinator) {
        Intrinsics.checkParameterIsNotNull(notificationWakeUpCoordinator, "coordinator");
        return Float.valueOf(notificationWakeUpCoordinator.mLinearVisibilityAmount);
    }
}
