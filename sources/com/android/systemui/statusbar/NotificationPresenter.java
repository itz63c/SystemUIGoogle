package com.android.systemui.statusbar;

import com.android.systemui.statusbar.notification.row.ActivatableNotificationView.OnActivatedListener;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow.OnExpandClickListener;

public interface NotificationPresenter extends OnExpandClickListener, OnActivatedListener {
    int getMaxNotificationsWhileLocked(boolean z);

    boolean isCollapsing();

    boolean isDeviceInVrMode();

    boolean isPresenterFullyCollapsed();

    void onUpdateRowStates();

    void onUserSwitched(int i);

    void updateMediaMetaData(boolean z, boolean z2);

    void updateNotificationViews();
}
