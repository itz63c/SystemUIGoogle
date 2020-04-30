package com.android.systemui.statusbar.notification.icon;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: IconManager.kt */
public final class IconManager$entryListener$1 implements NotifCollectionListener {
    final /* synthetic */ IconManager this$0;

    IconManager$entryListener$1(IconManager iconManager) {
        this.this$0 = iconManager;
    }

    public void onEntryInit(NotificationEntry notificationEntry) {
        Intrinsics.checkParameterIsNotNull(notificationEntry, "entry");
        notificationEntry.addOnSensitivityChangedListener(this.this$0.sensitivityListener);
    }

    public void onEntryCleanUp(NotificationEntry notificationEntry) {
        Intrinsics.checkParameterIsNotNull(notificationEntry, "entry");
        notificationEntry.removeOnSensitivityChangedListener(this.this$0.sensitivityListener);
    }

    public void onRankingApplied() {
        for (NotificationEntry notificationEntry : this.this$0.notifCollection.getAllNotifs()) {
            IconManager iconManager = this.this$0;
            Intrinsics.checkExpressionValueIsNotNull(notificationEntry, "entry");
            boolean access$isImportantConversation = iconManager.isImportantConversation(notificationEntry);
            IconPack icons = notificationEntry.getIcons();
            String str = "entry.icons";
            Intrinsics.checkExpressionValueIsNotNull(icons, str);
            if (icons.getAreIconsAvailable()) {
                IconPack icons2 = notificationEntry.getIcons();
                Intrinsics.checkExpressionValueIsNotNull(icons2, str);
                if (access$isImportantConversation != icons2.isImportantConversation()) {
                    this.this$0.updateIconsSafe(notificationEntry);
                }
            }
            IconPack icons3 = notificationEntry.getIcons();
            Intrinsics.checkExpressionValueIsNotNull(icons3, str);
            icons3.setImportantConversation(access$isImportantConversation);
        }
    }
}
