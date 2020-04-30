package com.android.systemui.statusbar.notification.people;

import android.view.View;
import android.view.ViewGroup;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* compiled from: PeopleHubNotificationListener.kt */
final class PeopleHubNotificationListenerKt$extractAvatarFromRow$1 extends Lambda implements Function1<View, ViewGroup> {
    public static final PeopleHubNotificationListenerKt$extractAvatarFromRow$1 INSTANCE = new PeopleHubNotificationListenerKt$extractAvatarFromRow$1();

    PeopleHubNotificationListenerKt$extractAvatarFromRow$1() {
        super(1);
    }

    public final ViewGroup invoke(View view) {
        Intrinsics.checkParameterIsNotNull(view, "it");
        if (!(view instanceof ViewGroup)) {
            view = null;
        }
        return (ViewGroup) view;
    }
}
