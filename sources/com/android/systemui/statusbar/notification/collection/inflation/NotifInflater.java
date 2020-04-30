package com.android.systemui.statusbar.notification.collection.inflation;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;

public interface NotifInflater {

    public interface InflationCallback {
        void onInflationFinished(NotificationEntry notificationEntry);
    }

    void inflateViews(NotificationEntry notificationEntry);

    void rebindViews(NotificationEntry notificationEntry);

    void setInflationCallback(InflationCallback inflationCallback);
}
