package com.android.systemui.statusbar.notification.row;

import android.app.NotificationChannel;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* compiled from: ChannelEditorDialogController.kt */
final class ChannelEditorDialogController$padToFourChannels$1 extends Lambda implements Function1<NotificationChannel, Boolean> {
    final /* synthetic */ ChannelEditorDialogController this$0;

    ChannelEditorDialogController$padToFourChannels$1(ChannelEditorDialogController channelEditorDialogController) {
        this.this$0 = channelEditorDialogController;
        super(1);
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj) {
        return Boolean.valueOf(invoke((NotificationChannel) obj));
    }

    public final boolean invoke(NotificationChannel notificationChannel) {
        Intrinsics.checkParameterIsNotNull(notificationChannel, "it");
        return this.this$0.mo15531x20ef0f().contains(notificationChannel);
    }
}
