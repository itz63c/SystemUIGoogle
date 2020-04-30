package com.android.systemui.statusbar.notification.collection;

import android.view.View;
import android.view.ViewGroup;
import com.android.systemui.statusbar.notification.stack.NotificationListItem;

/* compiled from: SimpleNotificationListContainer.kt */
public interface SimpleNotificationListContainer {
    void addListItem(NotificationListItem notificationListItem);

    void generateChildOrderChangedEvent();

    View getContainerChildAt(int i);

    int getContainerChildCount();

    void notifyGroupChildAdded(View view);

    void notifyGroupChildRemoved(View view, ViewGroup viewGroup);

    void removeListItem(NotificationListItem notificationListItem);

    void setChildTransferInProgress(boolean z);
}
