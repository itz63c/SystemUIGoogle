package com.android.systemui.statusbar.notification.people;

import android.graphics.drawable.Drawable;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.android.internal.widget.MessagingGroup;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* compiled from: PeopleHubNotificationListener.kt */
final class PeopleHubNotificationListenerKt$extractAvatarFromRow$4 extends Lambda implements Function1<ViewGroup, Drawable> {
    public static final PeopleHubNotificationListenerKt$extractAvatarFromRow$4 INSTANCE = new PeopleHubNotificationListenerKt$extractAvatarFromRow$4();

    PeopleHubNotificationListenerKt$extractAvatarFromRow$4() {
        super(1);
    }

    public final Drawable invoke(ViewGroup viewGroup) {
        Intrinsics.checkParameterIsNotNull(viewGroup, "messagesView");
        MessagingGroup messagingGroup = (MessagingGroup) SequencesKt___SequencesKt.lastOrNull(SequencesKt___SequencesKt.mapNotNull(PeopleHubNotificationListenerKt.getChildren(viewGroup), C12371.INSTANCE));
        if (messagingGroup != null) {
            ImageView imageView = (ImageView) messagingGroup.findViewById(16909141);
            if (imageView != null) {
                return imageView.getDrawable();
            }
        }
        return null;
    }
}
