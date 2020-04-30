package com.android.systemui.statusbar.notification.people;

import android.view.View;
import android.view.ViewGroup;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* compiled from: PeopleHubNotificationListener.kt */
final class PeopleHubNotificationListenerKt$extractAvatarFromRow$3 extends Lambda implements Function1<View, ViewGroup> {
    public static final PeopleHubNotificationListenerKt$extractAvatarFromRow$3 INSTANCE = new PeopleHubNotificationListenerKt$extractAvatarFromRow$3();

    PeopleHubNotificationListenerKt$extractAvatarFromRow$3() {
        super(1);
    }

    public final ViewGroup invoke(View view) {
        Intrinsics.checkParameterIsNotNull(view, "it");
        return (ViewGroup) view.findViewById(16909217);
    }
}
