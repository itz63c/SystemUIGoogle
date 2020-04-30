package com.android.systemui.statusbar.notification;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.RowContentBindParams;
import com.android.systemui.statusbar.notification.row.RowContentBindStage;
import java.util.List;
import java.util.Map;

public class DynamicChildBindController {
    private final int mChildBindCutoff;
    private final RowContentBindStage mStage;

    public DynamicChildBindController(RowContentBindStage rowContentBindStage) {
        this(rowContentBindStage, 9);
    }

    DynamicChildBindController(RowContentBindStage rowContentBindStage, int i) {
        this.mStage = rowContentBindStage;
        this.mChildBindCutoff = i;
    }

    public void updateChildContentViews(Map<NotificationEntry, List<NotificationEntry>> map) {
        for (NotificationEntry notificationEntry : map.keySet()) {
            List list = (List) map.get(notificationEntry);
            for (int i = 0; i < list.size(); i++) {
                NotificationEntry notificationEntry2 = (NotificationEntry) list.get(i);
                if (i >= this.mChildBindCutoff) {
                    if (hasChildContent(notificationEntry2)) {
                        freeChildContent(notificationEntry2);
                    }
                } else if (!hasChildContent(notificationEntry2)) {
                    bindChildContent(notificationEntry2);
                }
            }
        }
    }

    private boolean hasChildContent(NotificationEntry notificationEntry) {
        ExpandableNotificationRow row = notificationEntry.getRow();
        return (row.getPrivateLayout().getContractedChild() == null && row.getPrivateLayout().getExpandedChild() == null) ? false : true;
    }

    private void freeChildContent(NotificationEntry notificationEntry) {
        RowContentBindParams rowContentBindParams = (RowContentBindParams) this.mStage.getStageParams(notificationEntry);
        rowContentBindParams.freeContentViews(1);
        rowContentBindParams.freeContentViews(2);
        this.mStage.requestRebind(notificationEntry, null);
    }

    private void bindChildContent(NotificationEntry notificationEntry) {
        RowContentBindParams rowContentBindParams = (RowContentBindParams) this.mStage.getStageParams(notificationEntry);
        rowContentBindParams.requireContentViews(1);
        rowContentBindParams.requireContentViews(2);
        this.mStage.requestRebind(notificationEntry, null);
    }
}
