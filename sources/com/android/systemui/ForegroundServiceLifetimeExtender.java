package com.android.systemui;

import android.os.Handler;
import android.os.Looper;
import android.util.ArraySet;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.statusbar.NotificationLifetimeExtender;
import com.android.systemui.statusbar.NotificationLifetimeExtender.NotificationSafeToRemoveCallback;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;

public class ForegroundServiceLifetimeExtender implements NotificationLifetimeExtender {
    @VisibleForTesting
    static final int MIN_FGS_TIME_MS = 5000;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private ArraySet<NotificationEntry> mManagedEntries = new ArraySet<>();
    private NotificationSafeToRemoveCallback mNotificationSafeToRemoveCallback;

    public void setCallback(NotificationSafeToRemoveCallback notificationSafeToRemoveCallback) {
        this.mNotificationSafeToRemoveCallback = notificationSafeToRemoveCallback;
    }

    public boolean shouldExtendLifetime(NotificationEntry notificationEntry) {
        boolean z = false;
        if ((notificationEntry.getSbn().getNotification().flags & 64) == 0) {
            return false;
        }
        if (System.currentTimeMillis() - notificationEntry.getSbn().getPostTime() < 5000) {
            z = true;
        }
        return z;
    }

    public boolean shouldExtendLifetimeForPendingNotification(NotificationEntry notificationEntry) {
        return shouldExtendLifetime(notificationEntry);
    }

    public void setShouldManageLifetime(NotificationEntry notificationEntry, boolean z) {
        if (!z) {
            this.mManagedEntries.remove(notificationEntry);
            return;
        }
        this.mManagedEntries.add(notificationEntry);
        this.mHandler.postDelayed(new Runnable(notificationEntry) {
            public final /* synthetic */ NotificationEntry f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ForegroundServiceLifetimeExtender.this.mo9529xdfc60990(this.f$1);
            }
        }, 5000 - (System.currentTimeMillis() - notificationEntry.getSbn().getPostTime()));
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setShouldManageLifetime$0 */
    public /* synthetic */ void mo9529xdfc60990(NotificationEntry notificationEntry) {
        if (this.mManagedEntries.contains(notificationEntry)) {
            this.mManagedEntries.remove(notificationEntry);
            NotificationSafeToRemoveCallback notificationSafeToRemoveCallback = this.mNotificationSafeToRemoveCallback;
            if (notificationSafeToRemoveCallback != null) {
                notificationSafeToRemoveCallback.onSafeToRemove(notificationEntry.getKey());
            }
        }
    }
}
