package com.android.systemui.statusbar.notification.collection.notifcollection;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: NotifEvent.kt */
public final class InitEntryEvent extends NotifEvent {
    private final NotificationEntry entry;

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x0010, code lost:
        if (kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) r1.entry, (java.lang.Object) ((com.android.systemui.statusbar.notification.collection.notifcollection.InitEntryEvent) r2).entry) != false) goto L_0x0015;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean equals(java.lang.Object r2) {
        /*
            r1 = this;
            if (r1 == r2) goto L_0x0015
            boolean r0 = r2 instanceof com.android.systemui.statusbar.notification.collection.notifcollection.InitEntryEvent
            if (r0 == 0) goto L_0x0013
            com.android.systemui.statusbar.notification.collection.notifcollection.InitEntryEvent r2 = (com.android.systemui.statusbar.notification.collection.notifcollection.InitEntryEvent) r2
            com.android.systemui.statusbar.notification.collection.NotificationEntry r1 = r1.entry
            com.android.systemui.statusbar.notification.collection.NotificationEntry r2 = r2.entry
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
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.notification.collection.notifcollection.InitEntryEvent.equals(java.lang.Object):boolean");
    }

    public int hashCode() {
        NotificationEntry notificationEntry = this.entry;
        if (notificationEntry != null) {
            return notificationEntry.hashCode();
        }
        return 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("InitEntryEvent(entry=");
        sb.append(this.entry);
        sb.append(")");
        return sb.toString();
    }

    public InitEntryEvent(NotificationEntry notificationEntry) {
        Intrinsics.checkParameterIsNotNull(notificationEntry, "entry");
        super(null);
        this.entry = notificationEntry;
    }

    public void dispatchToListener(NotifCollectionListener notifCollectionListener) {
        Intrinsics.checkParameterIsNotNull(notifCollectionListener, "listener");
        notifCollectionListener.onEntryInit(this.entry);
    }
}
