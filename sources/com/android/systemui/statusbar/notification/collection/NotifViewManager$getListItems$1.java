package com.android.systemui.statusbar.notification.collection;

import android.view.View;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Lambda;

/* compiled from: NotifViewManager.kt */
final class NotifViewManager$getListItems$1 extends Lambda implements Function1<Integer, View> {
    final /* synthetic */ SimpleNotificationListContainer $container;

    NotifViewManager$getListItems$1(SimpleNotificationListContainer simpleNotificationListContainer) {
        this.$container = simpleNotificationListContainer;
        super(1);
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj) {
        return invoke(((Number) obj).intValue());
    }

    public final View invoke(int i) {
        return this.$container.getContainerChildAt(i);
    }
}
