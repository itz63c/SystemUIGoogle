package com.android.systemui.statusbar.notification.collection.notifcollection;

import android.service.notification.NotificationListenerService.RankingMap;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: NotifEvent.kt */
public final class RankingUpdatedEvent extends NotifEvent {
    private final RankingMap rankingMap;

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x0010, code lost:
        if (kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) r1.rankingMap, (java.lang.Object) ((com.android.systemui.statusbar.notification.collection.notifcollection.RankingUpdatedEvent) r2).rankingMap) != false) goto L_0x0015;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean equals(java.lang.Object r2) {
        /*
            r1 = this;
            if (r1 == r2) goto L_0x0015
            boolean r0 = r2 instanceof com.android.systemui.statusbar.notification.collection.notifcollection.RankingUpdatedEvent
            if (r0 == 0) goto L_0x0013
            com.android.systemui.statusbar.notification.collection.notifcollection.RankingUpdatedEvent r2 = (com.android.systemui.statusbar.notification.collection.notifcollection.RankingUpdatedEvent) r2
            android.service.notification.NotificationListenerService$RankingMap r1 = r1.rankingMap
            android.service.notification.NotificationListenerService$RankingMap r2 = r2.rankingMap
            boolean r1 = kotlin.jvm.internal.Intrinsics.areEqual(r1, r2)
            if (r1 == 0) goto L_0x0013
            goto L_0x0015
        L_0x0013:
            r1 = 0
            return r1
        L_0x0015:
            r1 = 1
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.notification.collection.notifcollection.RankingUpdatedEvent.equals(java.lang.Object):boolean");
    }

    public int hashCode() {
        RankingMap rankingMap2 = this.rankingMap;
        if (rankingMap2 != null) {
            return rankingMap2.hashCode();
        }
        return 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("RankingUpdatedEvent(rankingMap=");
        sb.append(this.rankingMap);
        sb.append(")");
        return sb.toString();
    }

    public RankingUpdatedEvent(RankingMap rankingMap2) {
        Intrinsics.checkParameterIsNotNull(rankingMap2, "rankingMap");
        super(null);
        this.rankingMap = rankingMap2;
    }

    public void dispatchToListener(NotifCollectionListener notifCollectionListener) {
        Intrinsics.checkParameterIsNotNull(notifCollectionListener, "listener");
        notifCollectionListener.onRankingUpdate(this.rankingMap);
    }
}
