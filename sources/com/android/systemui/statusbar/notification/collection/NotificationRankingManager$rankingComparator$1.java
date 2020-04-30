package com.android.systemui.statusbar.notification.collection;

import java.util.Comparator;

/* compiled from: NotificationRankingManager.kt */
final class NotificationRankingManager$rankingComparator$1<T> implements Comparator<NotificationEntry> {
    final /* synthetic */ NotificationRankingManager this$0;

    NotificationRankingManager$rankingComparator$1(NotificationRankingManager notificationRankingManager) {
        this.this$0 = notificationRankingManager;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0085, code lost:
        if (r10 != false) goto L_0x0087;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x00a4, code lost:
        if (r11 != false) goto L_0x0087;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x00b1, code lost:
        if (r7 != false) goto L_0x0087;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x00b6, code lost:
        if (r8 != false) goto L_0x0087;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x00bb, code lost:
        if (r14 != false) goto L_0x0087;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:?, code lost:
        return 1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final int compare(com.android.systemui.statusbar.notification.collection.NotificationEntry r21, com.android.systemui.statusbar.notification.collection.NotificationEntry r22) {
        /*
            r20 = this;
            r0 = r20
            r1 = r21
            r2 = r22
            java.lang.String r3 = "a"
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r1, r3)
            android.service.notification.StatusBarNotification r3 = r21.getSbn()
            java.lang.String r4 = "b"
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r2, r4)
            android.service.notification.StatusBarNotification r4 = r22.getSbn()
            android.service.notification.NotificationListenerService$Ranking r5 = r21.getRanking()
            java.lang.String r6 = "a.ranking"
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r5, r6)
            int r5 = r5.getRank()
            android.service.notification.NotificationListenerService$Ranking r6 = r22.getRanking()
            java.lang.String r7 = "b.ranking"
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r6, r7)
            int r6 = r6.getRank()
            com.android.systemui.statusbar.notification.collection.NotificationRankingManager r7 = r0.this$0
            int r7 = r7.getPeopleNotificationType(r1)
            com.android.systemui.statusbar.notification.collection.NotificationRankingManager r8 = r0.this$0
            int r8 = r8.getPeopleNotificationType(r2)
            r9 = 0
            r10 = 1
            if (r7 != r10) goto L_0x0044
            r11 = r10
            goto L_0x0045
        L_0x0044:
            r11 = r9
        L_0x0045:
            if (r8 != r10) goto L_0x0049
            r12 = r10
            goto L_0x004a
        L_0x0049:
            r12 = r9
        L_0x004a:
            r13 = 2
            if (r7 != r13) goto L_0x004f
            r7 = r10
            goto L_0x0050
        L_0x004f:
            r7 = r9
        L_0x0050:
            if (r8 != r13) goto L_0x0053
            r9 = r10
        L_0x0053:
            com.android.systemui.statusbar.notification.collection.NotificationRankingManager r8 = r0.this$0
            boolean r8 = r8.isImportantMedia(r1)
            com.android.systemui.statusbar.notification.collection.NotificationRankingManager r13 = r0.this$0
            boolean r13 = r13.isImportantMedia(r2)
            boolean r14 = com.android.systemui.statusbar.notification.collection.NotificationRankingManagerKt.isSystemMax(r21)
            boolean r15 = com.android.systemui.statusbar.notification.collection.NotificationRankingManagerKt.isSystemMax(r22)
            boolean r10 = r21.isRowHeadsUp()
            r16 = r3
            boolean r3 = r22.isRowHeadsUp()
            r17 = r4
            com.android.systemui.statusbar.notification.collection.NotificationRankingManager r4 = r0.this$0
            boolean r4 = r4.isHighPriority(r1)
            r18 = r5
            com.android.systemui.statusbar.notification.collection.NotificationRankingManager r5 = r0.this$0
            boolean r5 = r5.isHighPriority(r2)
            r19 = -1
            if (r10 == r3) goto L_0x008d
            if (r10 == 0) goto L_0x008b
        L_0x0087:
            r10 = r19
            goto L_0x00ea
        L_0x008b:
            r10 = 1
            goto L_0x00ea
        L_0x008d:
            if (r10 == 0) goto L_0x009a
            com.android.systemui.statusbar.notification.collection.NotificationRankingManager r0 = r0.this$0
            com.android.systemui.statusbar.policy.HeadsUpManager r0 = r0.headsUpManager
            int r10 = r0.compare(r1, r2)
            goto L_0x00ea
        L_0x009a:
            com.android.systemui.statusbar.notification.collection.NotificationRankingManager r1 = r0.this$0
            boolean r1 = r1.usePeopleFiltering
            if (r1 == 0) goto L_0x00a7
            if (r11 == r12) goto L_0x00a7
            if (r11 == 0) goto L_0x008b
            goto L_0x0087
        L_0x00a7:
            com.android.systemui.statusbar.notification.collection.NotificationRankingManager r0 = r0.this$0
            boolean r0 = r0.usePeopleFiltering
            if (r0 == 0) goto L_0x00b4
            if (r7 == r9) goto L_0x00b4
            if (r7 == 0) goto L_0x008b
            goto L_0x0087
        L_0x00b4:
            if (r8 == r13) goto L_0x00b9
            if (r8 == 0) goto L_0x008b
            goto L_0x0087
        L_0x00b9:
            if (r14 == r15) goto L_0x00be
            if (r14 == 0) goto L_0x008b
            goto L_0x0087
        L_0x00be:
            if (r4 == r5) goto L_0x00c7
            int r0 = kotlin.jvm.internal.Intrinsics.compare(r4, r5)
            int r10 = r0 * -1
            goto L_0x00ea
        L_0x00c7:
            r0 = r18
            if (r0 == r6) goto L_0x00ce
            int r10 = r0 - r6
            goto L_0x00ea
        L_0x00ce:
            java.lang.String r0 = "nb"
            r1 = r17
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r1, r0)
            android.app.Notification r0 = r1.getNotification()
            long r0 = r0.when
            java.lang.String r2 = "na"
            r3 = r16
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r3, r2)
            android.app.Notification r2 = r3.getNotification()
            long r2 = r2.when
            int r10 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
        L_0x00ea:
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.notification.collection.NotificationRankingManager$rankingComparator$1.compare(com.android.systemui.statusbar.notification.collection.NotificationEntry, com.android.systemui.statusbar.notification.collection.NotificationEntry):int");
    }
}
