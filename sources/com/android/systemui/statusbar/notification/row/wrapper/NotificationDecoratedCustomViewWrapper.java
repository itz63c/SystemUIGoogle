package com.android.systemui.statusbar.notification.row.wrapper;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;

public class NotificationDecoratedCustomViewWrapper extends NotificationTemplateViewWrapper {
    private View mWrappedView = null;

    protected NotificationDecoratedCustomViewWrapper(Context context, View view, ExpandableNotificationRow expandableNotificationRow) {
        super(context, view, expandableNotificationRow);
    }

    public void onContentUpdated(ExpandableNotificationRow expandableNotificationRow) {
        ViewGroup viewGroup = (ViewGroup) this.mView.findViewById(16909203);
        Integer num = (Integer) viewGroup.getTag(16909201);
        if (!(num == null || num.intValue() == -1)) {
            this.mWrappedView = viewGroup.getChildAt(num.intValue());
        }
        if (needsInversion(resolveBackgroundColor(), this.mWrappedView)) {
            invertViewLuminosity(this.mWrappedView);
        }
        super.onContentUpdated(expandableNotificationRow);
    }
}
