package com.android.systemui.statusbar.notification.people;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import com.android.systemui.C2011R$id;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationLockscreenUserManager.UserChangedListener;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.Sequence;

/* compiled from: PeopleHubNotificationListener.kt */
public final class PeopleHubNotificationListenerKt {
    /* access modifiers changed from: private */
    public static final Subscription registerListener(NotificationLockscreenUserManager notificationLockscreenUserManager, UserChangedListener userChangedListener) {
        notificationLockscreenUserManager.addUserChangedListener(userChangedListener);
        return new PeopleHubNotificationListenerKt$registerListener$1(notificationLockscreenUserManager, userChangedListener);
    }

    /* access modifiers changed from: private */
    public static final Sequence<View> getChildren(ViewGroup viewGroup) {
        return SequencesKt__SequenceBuilderKt.sequence(new PeopleHubNotificationListenerKt$children$1(viewGroup, null));
    }

    /* access modifiers changed from: private */
    public static final Sequence<View> childrenWithId(ViewGroup viewGroup, int i) {
        return SequencesKt___SequencesKt.filter(getChildren(viewGroup), new PeopleHubNotificationListenerKt$childrenWithId$1(i));
    }

    public static final Drawable extractAvatarFromRow(NotificationEntry notificationEntry) {
        Intrinsics.checkParameterIsNotNull(notificationEntry, "entry");
        ExpandableNotificationRow row = notificationEntry.getRow();
        if (row != null) {
            Sequence childrenWithId = childrenWithId(row, C2011R$id.expanded);
            if (childrenWithId != null) {
                Sequence mapNotNull = SequencesKt___SequencesKt.mapNotNull(childrenWithId, PeopleHubNotificationListenerKt$extractAvatarFromRow$1.INSTANCE);
                if (mapNotNull != null) {
                    Sequence flatMap = SequencesKt___SequencesKt.flatMap(mapNotNull, PeopleHubNotificationListenerKt$extractAvatarFromRow$2.INSTANCE);
                    if (flatMap != null) {
                        Sequence mapNotNull2 = SequencesKt___SequencesKt.mapNotNull(flatMap, PeopleHubNotificationListenerKt$extractAvatarFromRow$3.INSTANCE);
                        if (mapNotNull2 != null) {
                            Sequence mapNotNull3 = SequencesKt___SequencesKt.mapNotNull(mapNotNull2, PeopleHubNotificationListenerKt$extractAvatarFromRow$4.INSTANCE);
                            if (mapNotNull3 != null) {
                                return (Drawable) SequencesKt___SequencesKt.firstOrNull(mapNotNull3);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
