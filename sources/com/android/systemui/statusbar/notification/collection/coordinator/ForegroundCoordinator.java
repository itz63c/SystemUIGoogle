package com.android.systemui.statusbar.notification.collection.coordinator;

import android.os.UserHandle;
import android.service.notification.StatusBarNotification;
import android.util.ArraySet;
import com.android.systemui.ForegroundServiceController;
import com.android.systemui.appops.AppOpsController;
import com.android.systemui.appops.AppOpsController.Callback;
import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifFilter;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifLifetimeExtender;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifLifetimeExtender.OnEndLifetimeExtensionCallback;
import com.android.systemui.util.Assert;
import com.android.systemui.util.concurrency.DelayableExecutor;
import java.util.HashMap;
import java.util.Map;

public class ForegroundCoordinator implements Coordinator {
    private final AppOpsController mAppOpsController;
    /* access modifiers changed from: private */
    public final NotifLifetimeExtender mForegroundLifetimeExtender = new NotifLifetimeExtender() {
        private OnEndLifetimeExtensionCallback mEndCallback;
        private Map<NotificationEntry, Runnable> mEndRunnables = new HashMap();

        public String getName() {
            return "ForegroundCoordinator";
        }

        public void setCallback(OnEndLifetimeExtensionCallback onEndLifetimeExtensionCallback) {
            this.mEndCallback = onEndLifetimeExtensionCallback;
        }

        public boolean shouldExtendLifetime(NotificationEntry notificationEntry, int i) {
            boolean z = false;
            if ((notificationEntry.getSbn().getNotification().flags & 64) == 0) {
                return false;
            }
            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis - notificationEntry.getSbn().getPostTime() < 5000) {
                z = true;
            }
            if (z && !this.mEndRunnables.containsKey(notificationEntry)) {
                this.mEndRunnables.put(notificationEntry, ForegroundCoordinator.this.mMainExecutor.executeDelayed(new Runnable(notificationEntry) {
                    public final /* synthetic */ NotificationEntry f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        C12052.this.lambda$shouldExtendLifetime$0$ForegroundCoordinator$2(this.f$1);
                    }
                }, 5000 - (currentTimeMillis - notificationEntry.getSbn().getPostTime())));
            }
            return z;
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$shouldExtendLifetime$0 */
        public /* synthetic */ void lambda$shouldExtendLifetime$0$ForegroundCoordinator$2(NotificationEntry notificationEntry) {
            this.mEndRunnables.remove(notificationEntry);
            this.mEndCallback.onEndLifetimeExtension(ForegroundCoordinator.this.mForegroundLifetimeExtender, notificationEntry);
        }

        public void cancelLifetimeExtension(NotificationEntry notificationEntry) {
            Runnable runnable = (Runnable) this.mEndRunnables.remove(notificationEntry);
            if (runnable != null) {
                runnable.run();
            }
        }
    };
    /* access modifiers changed from: private */
    public final ForegroundServiceController mForegroundServiceController;
    /* access modifiers changed from: private */
    public final DelayableExecutor mMainExecutor;
    private NotifCollectionListener mNotifCollectionListener = new NotifCollectionListener() {
        public void onEntryAdded(NotificationEntry notificationEntry) {
            tagForeground(notificationEntry);
        }

        public void onEntryUpdated(NotificationEntry notificationEntry) {
            tagForeground(notificationEntry);
        }

        private void tagForeground(NotificationEntry notificationEntry) {
            StatusBarNotification sbn = notificationEntry.getSbn();
            ArraySet appOps = ForegroundCoordinator.this.mForegroundServiceController.getAppOps(sbn.getUser().getIdentifier(), sbn.getPackageName());
            if (appOps != null) {
                notificationEntry.mActiveAppOps.clear();
                notificationEntry.mActiveAppOps.addAll(appOps);
            }
        }
    };
    private final NotifFilter mNotifFilter = new NotifFilter("ForegroundCoordinator") {
        public boolean shouldFilterOut(NotificationEntry notificationEntry, long j) {
            StatusBarNotification sbn = notificationEntry.getSbn();
            if (ForegroundCoordinator.this.mForegroundServiceController.isDisclosureNotification(sbn) && !ForegroundCoordinator.this.mForegroundServiceController.isDisclosureNeededForUser(sbn.getUser().getIdentifier())) {
                return true;
            }
            if (ForegroundCoordinator.this.mForegroundServiceController.isSystemAlertNotification(sbn)) {
                String[] stringArray = sbn.getNotification().extras.getStringArray("android.foregroundApps");
                if (stringArray == null || stringArray.length < 1 || ForegroundCoordinator.this.mForegroundServiceController.isSystemAlertWarningNeeded(sbn.getUser().getIdentifier(), stringArray[0])) {
                    return false;
                }
                return true;
            }
            return false;
        }
    };
    private NotifPipeline mNotifPipeline;

    public ForegroundCoordinator(ForegroundServiceController foregroundServiceController, AppOpsController appOpsController, DelayableExecutor delayableExecutor) {
        this.mForegroundServiceController = foregroundServiceController;
        this.mAppOpsController = appOpsController;
        this.mMainExecutor = delayableExecutor;
    }

    public void attach(NotifPipeline notifPipeline) {
        this.mNotifPipeline = notifPipeline;
        notifPipeline.addNotificationLifetimeExtender(this.mForegroundLifetimeExtender);
        this.mNotifPipeline.addCollectionListener(this.mNotifCollectionListener);
        this.mNotifPipeline.addPreGroupFilter(this.mNotifFilter);
        this.mAppOpsController.addCallback(ForegroundServiceController.APP_OPS, new Callback() {
            public final void onActiveStateChanged(int i, int i2, String str, boolean z) {
                ForegroundCoordinator.this.onAppOpsChanged(i, i2, str, z);
            }
        });
    }

    /* access modifiers changed from: private */
    public void onAppOpsChanged(int i, int i2, String str, boolean z) {
        DelayableExecutor delayableExecutor = this.mMainExecutor;
        $$Lambda$ForegroundCoordinator$NRgUpFfXHeMTuFSuWDQ6Cgb5Biw r1 = new Runnable(i, i2, str, z) {
            public final /* synthetic */ int f$1;
            public final /* synthetic */ int f$2;
            public final /* synthetic */ String f$3;
            public final /* synthetic */ boolean f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                ForegroundCoordinator.this.lambda$onAppOpsChanged$0$ForegroundCoordinator(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        };
        delayableExecutor.execute(r1);
    }

    /* access modifiers changed from: private */
    /* renamed from: handleAppOpsChanged */
    public void lambda$onAppOpsChanged$0(int i, int i2, String str, boolean z) {
        boolean z2;
        Assert.isMainThread();
        String standardLayoutKey = this.mForegroundServiceController.getStandardLayoutKey(UserHandle.getUserId(i2), str);
        if (standardLayoutKey != null) {
            NotificationEntry findNotificationEntryWithKey = findNotificationEntryWithKey(standardLayoutKey);
            if (findNotificationEntryWithKey != null && i2 == findNotificationEntryWithKey.getSbn().getUid() && str.equals(findNotificationEntryWithKey.getSbn().getPackageName())) {
                if (z) {
                    z2 = findNotificationEntryWithKey.mActiveAppOps.add(Integer.valueOf(i));
                } else {
                    z2 = findNotificationEntryWithKey.mActiveAppOps.remove(Integer.valueOf(i));
                }
                if (z2) {
                    this.mNotifFilter.invalidateList();
                }
            }
        }
    }

    private NotificationEntry findNotificationEntryWithKey(String str) {
        for (NotificationEntry notificationEntry : this.mNotifPipeline.getAllNotifs()) {
            if (notificationEntry.getKey().equals(str)) {
                return notificationEntry;
            }
        }
        return null;
    }
}
