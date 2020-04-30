package com.android.systemui;

import android.app.Notification.Builder;
import android.content.Context;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.util.ArraySet;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.systemui.statusbar.notification.NotificationEntryListener;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener;

public class ForegroundServiceNotificationListener {
    private final Context mContext;
    private final NotificationEntryManager mEntryManager;
    /* access modifiers changed from: private */
    public final ForegroundServiceController mForegroundServiceController;

    public ForegroundServiceNotificationListener(Context context, ForegroundServiceController foregroundServiceController, NotificationEntryManager notificationEntryManager, NotifPipeline notifPipeline) {
        this.mContext = context;
        this.mForegroundServiceController = foregroundServiceController;
        this.mEntryManager = notificationEntryManager;
        notificationEntryManager.addNotificationEntryListener(new NotificationEntryListener() {
            public void onPendingEntryAdded(NotificationEntry notificationEntry) {
                ForegroundServiceNotificationListener.this.addNotification(notificationEntry, notificationEntry.getImportance());
            }

            public void onPreEntryUpdated(NotificationEntry notificationEntry) {
                ForegroundServiceNotificationListener.this.updateNotification(notificationEntry, notificationEntry.getImportance());
            }

            public void onEntryRemoved(NotificationEntry notificationEntry, NotificationVisibility notificationVisibility, boolean z, int i) {
                ForegroundServiceNotificationListener.this.removeNotification(notificationEntry.getSbn());
            }
        });
        this.mEntryManager.addNotificationLifetimeExtender(new ForegroundServiceLifetimeExtender());
        notifPipeline.addCollectionListener(new NotifCollectionListener() {
            public void onEntryAdded(NotificationEntry notificationEntry) {
                ForegroundServiceNotificationListener.this.addNotification(notificationEntry, notificationEntry.getImportance());
            }

            public void onEntryUpdated(NotificationEntry notificationEntry) {
                ForegroundServiceNotificationListener.this.updateNotification(notificationEntry, notificationEntry.getImportance());
            }

            public void onEntryRemoved(NotificationEntry notificationEntry, int i) {
                ForegroundServiceNotificationListener.this.removeNotification(notificationEntry.getSbn());
            }
        });
    }

    /* access modifiers changed from: private */
    public void addNotification(NotificationEntry notificationEntry, int i) {
        updateNotification(notificationEntry, i);
    }

    /* access modifiers changed from: private */
    public void removeNotification(final StatusBarNotification statusBarNotification) {
        this.mForegroundServiceController.updateUserState(statusBarNotification.getUserId(), new UserStateUpdateCallback() {
            public boolean updateUserState(ForegroundServicesUserState foregroundServicesUserState) {
                if (!ForegroundServiceNotificationListener.this.mForegroundServiceController.isDisclosureNotification(statusBarNotification)) {
                    return foregroundServicesUserState.removeNotification(statusBarNotification.getPackageName(), statusBarNotification.getKey());
                }
                foregroundServicesUserState.setRunningServices(null, 0);
                return true;
            }
        }, false);
    }

    /* access modifiers changed from: private */
    public void updateNotification(NotificationEntry notificationEntry, int i) {
        StatusBarNotification sbn = notificationEntry.getSbn();
        this.mForegroundServiceController.updateUserState(sbn.getUserId(), new UserStateUpdateCallback(sbn, i, notificationEntry) {
            public final /* synthetic */ StatusBarNotification f$1;
            public final /* synthetic */ int f$2;
            public final /* synthetic */ NotificationEntry f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final boolean updateUserState(ForegroundServicesUserState foregroundServicesUserState) {
                return ForegroundServiceNotificationListener.this.mo9534xa2f2beea(this.f$1, this.f$2, this.f$3, foregroundServicesUserState);
            }
        }, true);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateNotification$0 */
    public /* synthetic */ boolean mo9534xa2f2beea(StatusBarNotification statusBarNotification, int i, NotificationEntry notificationEntry, ForegroundServicesUserState foregroundServicesUserState) {
        if (this.mForegroundServiceController.isDisclosureNotification(statusBarNotification)) {
            Bundle bundle = statusBarNotification.getNotification().extras;
            if (bundle != null) {
                foregroundServicesUserState.setRunningServices(bundle.getStringArray("android.foregroundApps"), statusBarNotification.getNotification().when);
            }
        } else {
            foregroundServicesUserState.removeNotification(statusBarNotification.getPackageName(), statusBarNotification.getKey());
            if ((statusBarNotification.getNotification().flags & 64) != 0) {
                if (i > 1) {
                    foregroundServicesUserState.addImportantNotification(statusBarNotification.getPackageName(), statusBarNotification.getKey());
                }
                if (Builder.recoverBuilder(this.mContext, statusBarNotification.getNotification()).usesStandardHeader()) {
                    foregroundServicesUserState.addStandardLayoutNotification(statusBarNotification.getPackageName(), statusBarNotification.getKey());
                }
            }
        }
        tagForeground(notificationEntry);
        return true;
    }

    private void tagForeground(NotificationEntry notificationEntry) {
        StatusBarNotification sbn = notificationEntry.getSbn();
        ArraySet appOps = this.mForegroundServiceController.getAppOps(sbn.getUserId(), sbn.getPackageName());
        if (appOps != null) {
            synchronized (notificationEntry.mActiveAppOps) {
                notificationEntry.mActiveAppOps.clear();
                notificationEntry.mActiveAppOps.addAll(appOps);
            }
        }
    }
}
