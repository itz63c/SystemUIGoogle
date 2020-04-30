package com.android.systemui.statusbar.notification.icon;

import com.android.systemui.statusbar.StatusBarIconView.OnVisibilityChangedListener;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;

/* compiled from: IconManager.kt */
final class IconManager$createIcons$1 implements OnVisibilityChangedListener {
    final /* synthetic */ NotificationEntry $entry;

    IconManager$createIcons$1(NotificationEntry notificationEntry) {
        this.$entry = notificationEntry;
    }

    public final void onVisibilityChanged(int i) {
        if (this.$entry.getRow() != null) {
            this.$entry.getRow().setIconsVisible(i != 0);
        }
    }
}
