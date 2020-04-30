package com.android.systemui.statusbar.notification.collection.coalescer;

import android.service.notification.NotificationListenerService.Ranking;
import android.service.notification.StatusBarNotification;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: CoalescedEvent.kt */
public final class CoalescedEvent {
    private EventBatch batch;
    private final String key;
    private int position;
    private Ranking ranking;
    private StatusBarNotification sbn;

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0034, code lost:
        if (kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) r2.batch, (java.lang.Object) r3.batch) != false) goto L_0x0039;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean equals(java.lang.Object r3) {
        /*
            r2 = this;
            if (r2 == r3) goto L_0x0039
            boolean r0 = r3 instanceof com.android.systemui.statusbar.notification.collection.coalescer.CoalescedEvent
            if (r0 == 0) goto L_0x0037
            com.android.systemui.statusbar.notification.collection.coalescer.CoalescedEvent r3 = (com.android.systemui.statusbar.notification.collection.coalescer.CoalescedEvent) r3
            java.lang.String r0 = r2.key
            java.lang.String r1 = r3.key
            boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual(r0, r1)
            if (r0 == 0) goto L_0x0037
            int r0 = r2.position
            int r1 = r3.position
            if (r0 != r1) goto L_0x0037
            android.service.notification.StatusBarNotification r0 = r2.sbn
            android.service.notification.StatusBarNotification r1 = r3.sbn
            boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual(r0, r1)
            if (r0 == 0) goto L_0x0037
            android.service.notification.NotificationListenerService$Ranking r0 = r2.ranking
            android.service.notification.NotificationListenerService$Ranking r1 = r3.ranking
            boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual(r0, r1)
            if (r0 == 0) goto L_0x0037
            com.android.systemui.statusbar.notification.collection.coalescer.EventBatch r2 = r2.batch
            com.android.systemui.statusbar.notification.collection.coalescer.EventBatch r3 = r3.batch
            boolean r2 = kotlin.jvm.internal.Intrinsics.areEqual(r2, r3)
            if (r2 == 0) goto L_0x0037
            goto L_0x0039
        L_0x0037:
            r2 = 0
            return r2
        L_0x0039:
            r2 = 1
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.notification.collection.coalescer.CoalescedEvent.equals(java.lang.Object):boolean");
    }

    public int hashCode() {
        String str = this.key;
        int i = 0;
        int hashCode = (((str != null ? str.hashCode() : 0) * 31) + Integer.hashCode(this.position)) * 31;
        StatusBarNotification statusBarNotification = this.sbn;
        int hashCode2 = (hashCode + (statusBarNotification != null ? statusBarNotification.hashCode() : 0)) * 31;
        Ranking ranking2 = this.ranking;
        int hashCode3 = (hashCode2 + (ranking2 != null ? ranking2.hashCode() : 0)) * 31;
        EventBatch eventBatch = this.batch;
        if (eventBatch != null) {
            i = eventBatch.hashCode();
        }
        return hashCode3 + i;
    }

    public CoalescedEvent(String str, int i, StatusBarNotification statusBarNotification, Ranking ranking2, EventBatch eventBatch) {
        Intrinsics.checkParameterIsNotNull(str, "key");
        Intrinsics.checkParameterIsNotNull(statusBarNotification, "sbn");
        Intrinsics.checkParameterIsNotNull(ranking2, "ranking");
        this.key = str;
        this.position = i;
        this.sbn = statusBarNotification;
        this.ranking = ranking2;
        this.batch = eventBatch;
    }

    public final String getKey() {
        return this.key;
    }

    public final int getPosition() {
        return this.position;
    }

    public final StatusBarNotification getSbn() {
        return this.sbn;
    }

    public final Ranking getRanking() {
        return this.ranking;
    }

    public final void setRanking(Ranking ranking2) {
        Intrinsics.checkParameterIsNotNull(ranking2, "<set-?>");
        this.ranking = ranking2;
    }

    public final EventBatch getBatch() {
        return this.batch;
    }

    public final void setBatch(EventBatch eventBatch) {
        this.batch = eventBatch;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CoalescedEvent(key=");
        sb.append(this.key);
        sb.append(')');
        return sb.toString();
    }
}
