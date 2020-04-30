package com.android.systemui.statusbar.notification.icon;

import android.app.Notification;
import android.content.Context;
import android.service.notification.StatusBarNotification;
import com.android.systemui.statusbar.StatusBarIconView;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: IconBuilder.kt */
public final class IconBuilder {
    private final Context context;

    public IconBuilder(Context context2) {
        Intrinsics.checkParameterIsNotNull(context2, "context");
        this.context = context2;
    }

    public final StatusBarIconView createIconView(NotificationEntry notificationEntry) {
        Intrinsics.checkParameterIsNotNull(notificationEntry, "entry");
        Context context2 = this.context;
        StringBuilder sb = new StringBuilder();
        StatusBarNotification sbn = notificationEntry.getSbn();
        String str = "entry.sbn";
        Intrinsics.checkExpressionValueIsNotNull(sbn, str);
        sb.append(sbn.getPackageName());
        sb.append("/0x");
        StatusBarNotification sbn2 = notificationEntry.getSbn();
        Intrinsics.checkExpressionValueIsNotNull(sbn2, str);
        sb.append(Integer.toHexString(sbn2.getId()));
        return new StatusBarIconView(context2, sb.toString(), notificationEntry.getSbn());
    }

    public final CharSequence getIconContentDescription(Notification notification) {
        Intrinsics.checkParameterIsNotNull(notification, "n");
        String contentDescForNotification = StatusBarIconView.contentDescForNotification(this.context, notification);
        Intrinsics.checkExpressionValueIsNotNull(contentDescForNotification, "StatusBarIconView.conten…rNotification(context, n)");
        return contentDescForNotification;
    }
}
