package com.android.systemui.statusbar.notification.people;

import android.view.View;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* compiled from: PeopleHubNotificationListener.kt */
final class PeopleHubNotificationListenerKt$childrenWithId$1 extends Lambda implements Function1<View, Boolean> {
    final /* synthetic */ int $id;

    PeopleHubNotificationListenerKt$childrenWithId$1(int i) {
        this.$id = i;
        super(1);
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj) {
        return Boolean.valueOf(invoke((View) obj));
    }

    public final boolean invoke(View view) {
        Intrinsics.checkParameterIsNotNull(view, "it");
        return view.getId() == this.$id;
    }
}
