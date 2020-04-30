package com.android.systemui.statusbar.notification.collection.notifcollection;

import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: NotifEvent.kt */
public final class EntryRemovedEvent extends NotifEvent {
    private final NotificationEntry entry;
    private final int reason;

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0016, code lost:
        if (r2.reason == r3.reason) goto L_0x001b;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean equals(java.lang.Object r3) {
        /*
            r2 = this;
            if (r2 == r3) goto L_0x001b
            boolean r0 = r3 instanceof com.android.systemui.statusbar.notification.collection.notifcollection.EntryRemovedEvent
            if (r0 == 0) goto L_0x0019
            com.android.systemui.statusbar.notification.collection.notifcollection.EntryRemovedEvent r3 = (com.android.systemui.statusbar.notification.collection.notifcollection.EntryRemovedEvent) r3
            com.android.systemui.statusbar.notification.collection.NotificationEntry r0 = r2.entry
            com.android.systemui.statusbar.notification.collection.NotificationEntry r1 = r3.entry
            boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual(r0, r1)
            if (r0 == 0) goto L_0x0019
            int r2 = r2.reason
            int r3 = r3.reason
            if (r2 != r3) goto L_0x0019
            goto L_0x001b
        L_0x0019:
            r2 = 0
            return r2
        L_0x001b:
            r2 = 1
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.notification.collection.notifcollection.EntryRemovedEvent.equals(java.lang.Object):boolean");
    }

    public int hashCode() {
        NotificationEntry notificationEntry = this.entry;
        return ((notificationEntry != null ? notificationEntry.hashCode() : 0) * 31) + Integer.hashCode(this.reason);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("EntryRemovedEvent(entry=");
        sb.append(this.entry);
        sb.append(", reason=");
        sb.append(this.reason);
        sb.append(")");
        return sb.toString();
    }

    public EntryRemovedEvent(NotificationEntry notificationEntry, int i) {
        Intrinsics.checkParameterIsNotNull(notificationEntry, "entry");
        super(null);
        this.entry = notificationEntry;
        this.reason = i;
    }

    public void dispatchToListener(NotifCollectionListener notifCollectionListener) {
        Intrinsics.checkParameterIsNotNull(notifCollectionListener, "listener");
        notifCollectionListener.onEntryRemoved(this.entry, this.reason);
    }
}
