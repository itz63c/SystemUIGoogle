package com.android.systemui.statusbar.notification.people;

import android.view.View;
import android.view.ViewGroup;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;
import kotlin.sequences.Sequence;

/* compiled from: PeopleHubNotificationListener.kt */
final class PeopleHubNotificationListenerKt$extractAvatarFromRow$2 extends Lambda implements Function1<ViewGroup, Sequence<? extends View>> {
    public static final PeopleHubNotificationListenerKt$extractAvatarFromRow$2 INSTANCE = new PeopleHubNotificationListenerKt$extractAvatarFromRow$2();

    PeopleHubNotificationListenerKt$extractAvatarFromRow$2() {
        super(1);
    }

    public final Sequence<View> invoke(ViewGroup viewGroup) {
        Intrinsics.checkParameterIsNotNull(viewGroup, "it");
        return PeopleHubNotificationListenerKt.childrenWithId(viewGroup, 16909454);
    }
}
