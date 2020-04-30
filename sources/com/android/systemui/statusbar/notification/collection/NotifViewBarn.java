package com.android.systemui.statusbar.notification.collection;

import android.view.textclassifier.Log;
import com.android.systemui.statusbar.notification.stack.NotificationListItem;
import java.util.LinkedHashMap;
import java.util.Map;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: NotifViewBarn.kt */
public final class NotifViewBarn {
    private final boolean DEBUG;
    private final Map<String, NotificationListItem> rowMap = new LinkedHashMap();

    public final NotificationListItem requireView(ListEntry listEntry) {
        Intrinsics.checkParameterIsNotNull(listEntry, "forEntry");
        if (this.DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("requireView: ");
            sb.append(listEntry);
            sb.append(".key");
            Log.d("NotifViewBarn", sb.toString());
        }
        NotificationListItem notificationListItem = (NotificationListItem) this.rowMap.get(listEntry.getKey());
        if (notificationListItem != null) {
            return notificationListItem;
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append("No view has been registered for entry: ");
        sb2.append(listEntry);
        throw new IllegalStateException(sb2.toString());
    }

    public final void registerViewForEntry(ListEntry listEntry, NotificationListItem notificationListItem) {
        Intrinsics.checkParameterIsNotNull(listEntry, "entry");
        Intrinsics.checkParameterIsNotNull(notificationListItem, "view");
        if (this.DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("registerViewForEntry: ");
            sb.append(listEntry);
            sb.append(".key");
            Log.d("NotifViewBarn", sb.toString());
        }
        Map<String, NotificationListItem> map = this.rowMap;
        String key = listEntry.getKey();
        Intrinsics.checkExpressionValueIsNotNull(key, "entry.key");
        map.put(key, notificationListItem);
    }

    public final void removeViewForEntry(ListEntry listEntry) {
        Intrinsics.checkParameterIsNotNull(listEntry, "entry");
        if (this.DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("removeViewForEntry: ");
            sb.append(listEntry);
            sb.append(".key");
            Log.d("NotifViewBarn", sb.toString());
        }
        this.rowMap.remove(listEntry.getKey());
    }
}
