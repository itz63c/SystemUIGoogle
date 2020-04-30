package com.android.systemui.statusbar.notification.row;

import android.util.ArrayMap;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import java.util.Map;

public abstract class BindStage<Params> extends BindRequester {
    private Map<NotificationEntry, Params> mContentParams = new ArrayMap();

    interface StageCallback {
        void onStageFinished(NotificationEntry notificationEntry);
    }

    /* access modifiers changed from: protected */
    public abstract void abortStage(NotificationEntry notificationEntry, ExpandableNotificationRow expandableNotificationRow);

    /* access modifiers changed from: protected */
    public abstract void executeStage(NotificationEntry notificationEntry, ExpandableNotificationRow expandableNotificationRow, StageCallback stageCallback);

    /* access modifiers changed from: protected */
    public abstract Params newStageParams();

    public final Params getStageParams(NotificationEntry notificationEntry) {
        Params params = this.mContentParams.get(notificationEntry);
        if (params != null) {
            return params;
        }
        throw new IllegalStateException(String.format("Entry does not have any stage parameters. key: %s", new Object[]{notificationEntry.getKey()}));
    }

    /* access modifiers changed from: 0000 */
    public final void createStageParams(NotificationEntry notificationEntry) {
        this.mContentParams.put(notificationEntry, newStageParams());
    }

    /* access modifiers changed from: 0000 */
    public final void deleteStageParams(NotificationEntry notificationEntry) {
        this.mContentParams.remove(notificationEntry);
    }
}
