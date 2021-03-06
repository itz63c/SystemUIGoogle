package com.android.systemui.statusbar.notification.row;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: DungeonRow.kt */
public final class DungeonRow extends LinearLayout {
    private NotificationEntry entry;

    public DungeonRow(Context context, AttributeSet attributeSet) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(attributeSet, "attrs");
        super(context, attributeSet);
    }

    public final NotificationEntry getEntry() {
        return this.entry;
    }

    public final void setEntry(NotificationEntry notificationEntry) {
        this.entry = notificationEntry;
        update();
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x0026  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0040  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final void update() {
        /*
            r3 = this;
            int r0 = com.android.systemui.C2011R$id.app_name
            android.view.View r0 = r3.findViewById(r0)
            if (r0 == 0) goto L_0x0048
            android.widget.TextView r0 = (android.widget.TextView) r0
            com.android.systemui.statusbar.notification.collection.NotificationEntry r1 = r3.entry
            r2 = 0
            if (r1 == 0) goto L_0x001a
            com.android.systemui.statusbar.notification.row.ExpandableNotificationRow r1 = r1.getRow()
            if (r1 == 0) goto L_0x001a
            java.lang.String r1 = r1.getAppName()
            goto L_0x001b
        L_0x001a:
            r1 = r2
        L_0x001b:
            r0.setText(r1)
            int r0 = com.android.systemui.C2011R$id.icon
            android.view.View r0 = r3.findViewById(r0)
            if (r0 == 0) goto L_0x0040
            com.android.systemui.statusbar.StatusBarIconView r0 = (com.android.systemui.statusbar.StatusBarIconView) r0
            com.android.systemui.statusbar.notification.collection.NotificationEntry r3 = r3.entry
            if (r3 == 0) goto L_0x003c
            com.android.systemui.statusbar.notification.icon.IconPack r3 = r3.getIcons()
            if (r3 == 0) goto L_0x003c
            com.android.systemui.statusbar.StatusBarIconView r3 = r3.getStatusBarIcon()
            if (r3 == 0) goto L_0x003c
            com.android.internal.statusbar.StatusBarIcon r2 = r3.getStatusBarIcon()
        L_0x003c:
            r0.set(r2)
            return
        L_0x0040:
            kotlin.TypeCastException r3 = new kotlin.TypeCastException
            java.lang.String r0 = "null cannot be cast to non-null type com.android.systemui.statusbar.StatusBarIconView"
            r3.<init>(r0)
            throw r3
        L_0x0048:
            kotlin.TypeCastException r3 = new kotlin.TypeCastException
            java.lang.String r0 = "null cannot be cast to non-null type android.widget.TextView"
            r3.<init>(r0)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.notification.row.DungeonRow.update():void");
    }
}
