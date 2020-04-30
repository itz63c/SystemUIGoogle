package com.android.systemui.statusbar.notification.collection;

import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.systemui.statusbar.notification.InflationException;
import com.android.systemui.statusbar.notification.collection.inflation.NotifInflater;
import com.android.systemui.statusbar.notification.collection.inflation.NotifInflater.InflationCallback;
import com.android.systemui.statusbar.notification.collection.inflation.NotificationRowBinderImpl;
import com.android.systemui.statusbar.notification.collection.notifcollection.DismissedByUserStats;
import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import com.android.systemui.statusbar.notification.row.NotifInflationErrorManager;
import com.android.systemui.statusbar.notification.row.NotificationRowContentBinder;

public class NotifInflaterImpl implements NotifInflater {
    /* access modifiers changed from: private */
    public InflationCallback mExternalInflationCallback;
    private final NotificationRowContentBinder.InflationCallback mInflationCallback = new NotificationRowContentBinder.InflationCallback() {
        public void handleInflationException(NotificationEntry notificationEntry, Exception exc) {
            NotifInflaterImpl.this.mNotifErrorManager.setInflationError(notificationEntry, exc);
        }

        public void onAsyncInflationFinished(NotificationEntry notificationEntry) {
            NotifInflaterImpl.this.mNotifErrorManager.clearInflationError(notificationEntry);
            if (NotifInflaterImpl.this.mExternalInflationCallback != null) {
                NotifInflaterImpl.this.mExternalInflationCallback.onInflationFinished(notificationEntry);
            }
        }
    };
    /* access modifiers changed from: private */
    public final NotifCollection mNotifCollection;
    /* access modifiers changed from: private */
    public final NotifInflationErrorManager mNotifErrorManager;
    /* access modifiers changed from: private */
    public final NotifPipeline mNotifPipeline;
    private NotificationRowBinderImpl mNotificationRowBinder;

    public NotifInflaterImpl(IStatusBarService iStatusBarService, NotifCollection notifCollection, NotifInflationErrorManager notifInflationErrorManager, NotifPipeline notifPipeline) {
        this.mNotifCollection = notifCollection;
        this.mNotifErrorManager = notifInflationErrorManager;
        this.mNotifPipeline = notifPipeline;
    }

    public void setRowBinder(NotificationRowBinderImpl notificationRowBinderImpl) {
        this.mNotificationRowBinder = notificationRowBinderImpl;
        notificationRowBinderImpl.setInflationCallback(this.mInflationCallback);
    }

    public void setInflationCallback(InflationCallback inflationCallback) {
        this.mExternalInflationCallback = inflationCallback;
    }

    public void rebindViews(NotificationEntry notificationEntry) {
        inflateViews(notificationEntry);
    }

    public void inflateViews(NotificationEntry notificationEntry) {
        try {
            requireBinder().inflateViews(notificationEntry, getDismissCallback(notificationEntry));
        } catch (InflationException unused) {
        }
    }

    private Runnable getDismissCallback(final NotificationEntry notificationEntry) {
        return new Runnable() {
            public void run() {
                NotifCollection access$100 = NotifInflaterImpl.this.mNotifCollection;
                NotificationEntry notificationEntry = notificationEntry;
                access$100.dismissNotification(notificationEntry, new DismissedByUserStats(3, 1, NotificationVisibility.obtain(notificationEntry.getKey(), notificationEntry.getRanking().getRank(), NotifInflaterImpl.this.mNotifPipeline.getShadeListCount(), true, NotificationLogger.getNotificationLocation(notificationEntry))));
            }
        };
    }

    private NotificationRowBinderImpl requireBinder() {
        NotificationRowBinderImpl notificationRowBinderImpl = this.mNotificationRowBinder;
        if (notificationRowBinderImpl != null) {
            return notificationRowBinderImpl;
        }
        throw new RuntimeException("NotificationRowBinder must be attached before using NotifInflaterImpl.");
    }
}
