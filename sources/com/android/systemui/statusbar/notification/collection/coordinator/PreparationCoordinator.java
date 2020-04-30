package com.android.systemui.statusbar.notification.collection.coordinator;

import android.os.RemoteException;
import android.service.notification.StatusBarNotification;
import android.util.ArrayMap;
import com.android.internal.statusbar.IStatusBarService;
import com.android.systemui.statusbar.notification.collection.GroupEntry;
import com.android.systemui.statusbar.notification.collection.ListEntry;
import com.android.systemui.statusbar.notification.collection.NotifInflaterImpl;
import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import com.android.systemui.statusbar.notification.collection.NotifViewBarn;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.inflation.NotifInflater;
import com.android.systemui.statusbar.notification.collection.inflation.NotifInflater.InflationCallback;
import com.android.systemui.statusbar.notification.collection.listbuilder.OnBeforeFinalizeFilterListener;
import com.android.systemui.statusbar.notification.collection.listbuilder.pluggable.NotifFilter;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProvider;
import com.android.systemui.statusbar.notification.row.NotifInflationErrorManager;
import com.android.systemui.statusbar.notification.row.NotifInflationErrorManager.NotifInflationErrorListener;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PreparationCoordinator implements Coordinator {
    /* access modifiers changed from: private */
    public final HeadsUpManager mHeadsUpManager;
    private final InflationCallback mInflationCallback = new InflationCallback() {
        public void onInflationFinished(NotificationEntry notificationEntry) {
            PreparationCoordinator.this.mLogger.logNotifInflated(notificationEntry.getKey());
            PreparationCoordinator.this.mViewBarn.registerViewForEntry(notificationEntry, notificationEntry.getRow());
            PreparationCoordinator.this.mInflationStates.put(notificationEntry, Integer.valueOf(1));
            if (PreparationCoordinator.this.mNotificationInterruptStateProvider.shouldHeadsUp(notificationEntry)) {
                PreparationCoordinator.this.mHeadsUpManager.showNotification(notificationEntry);
            }
            PreparationCoordinator.this.mNotifInflatingFilter.invalidateList();
        }
    };
    private final NotifInflationErrorListener mInflationErrorListener = new NotifInflationErrorListener() {
        public void onNotifInflationError(NotificationEntry notificationEntry, Exception exc) {
            PreparationCoordinator.this.mViewBarn.removeViewForEntry(notificationEntry);
            PreparationCoordinator.this.mInflationStates.put(notificationEntry, Integer.valueOf(-1));
            try {
                StatusBarNotification sbn = notificationEntry.getSbn();
                PreparationCoordinator.this.mStatusBarService.onNotificationError(sbn.getPackageName(), sbn.getTag(), sbn.getId(), sbn.getUid(), sbn.getInitialPid(), exc.getMessage(), sbn.getUserId());
            } catch (RemoteException unused) {
            }
            PreparationCoordinator.this.mNotifInflationErrorFilter.invalidateList();
        }

        public void onNotifInflationErrorCleared(NotificationEntry notificationEntry) {
            PreparationCoordinator.this.mNotifInflationErrorFilter.invalidateList();
        }
    };
    /* access modifiers changed from: private */
    public final Map<NotificationEntry, Integer> mInflationStates = new ArrayMap();
    /* access modifiers changed from: private */
    public final PreparationCoordinatorLogger mLogger;
    private final NotifCollectionListener mNotifCollectionListener = new NotifCollectionListener() {
        public void onEntryInit(NotificationEntry notificationEntry) {
            PreparationCoordinator.this.mInflationStates.put(notificationEntry, Integer.valueOf(0));
        }

        public void onEntryUpdated(NotificationEntry notificationEntry) {
            int access$100 = PreparationCoordinator.this.getInflationState(notificationEntry);
            if (access$100 == 1) {
                PreparationCoordinator.this.mInflationStates.put(notificationEntry, Integer.valueOf(2));
            } else if (access$100 == -1) {
                PreparationCoordinator.this.mInflationStates.put(notificationEntry, Integer.valueOf(0));
            }
        }

        public void onEntryRemoved(NotificationEntry notificationEntry, int i) {
            PreparationCoordinator preparationCoordinator = PreparationCoordinator.this;
            StringBuilder sb = new StringBuilder();
            sb.append("entryRemoved reason=");
            sb.append(i);
            preparationCoordinator.abortInflation(notificationEntry, sb.toString());
        }

        public void onEntryCleanUp(NotificationEntry notificationEntry) {
            PreparationCoordinator.this.mInflationStates.remove(notificationEntry);
            PreparationCoordinator.this.mViewBarn.removeViewForEntry(notificationEntry);
        }
    };
    private final NotifInflationErrorManager mNotifErrorManager;
    private final NotifInflater mNotifInflater;
    /* access modifiers changed from: private */
    public final NotifFilter mNotifInflatingFilter = new NotifFilter("PreparationCoordinatorInflating") {
        public boolean shouldFilterOut(NotificationEntry notificationEntry, long j) {
            int access$100 = PreparationCoordinator.this.getInflationState(notificationEntry);
            return (access$100 == 1 || access$100 == 2) ? false : true;
        }
    };
    /* access modifiers changed from: private */
    public final NotifFilter mNotifInflationErrorFilter = new NotifFilter("PreparationCoordinatorInflationError") {
        public boolean shouldFilterOut(NotificationEntry notificationEntry, long j) {
            return PreparationCoordinator.this.getInflationState(notificationEntry) == -1;
        }
    };
    /* access modifiers changed from: private */
    public final NotificationInterruptStateProvider mNotificationInterruptStateProvider;
    private final OnBeforeFinalizeFilterListener mOnBeforeFinalizeFilterListener = new OnBeforeFinalizeFilterListener() {
        public final void onBeforeFinalizeFilter(List list) {
            PreparationCoordinator.this.lambda$new$0$PreparationCoordinator(list);
        }
    };
    /* access modifiers changed from: private */
    public final IStatusBarService mStatusBarService;
    /* access modifiers changed from: private */
    public final NotifViewBarn mViewBarn;

    public PreparationCoordinator(PreparationCoordinatorLogger preparationCoordinatorLogger, NotifInflaterImpl notifInflaterImpl, NotifInflationErrorManager notifInflationErrorManager, NotifViewBarn notifViewBarn, IStatusBarService iStatusBarService, NotificationInterruptStateProvider notificationInterruptStateProvider, HeadsUpManager headsUpManager) {
        this.mLogger = preparationCoordinatorLogger;
        this.mNotifInflater = notifInflaterImpl;
        notifInflaterImpl.setInflationCallback(this.mInflationCallback);
        this.mNotifErrorManager = notifInflationErrorManager;
        notifInflationErrorManager.addInflationErrorListener(this.mInflationErrorListener);
        this.mViewBarn = notifViewBarn;
        this.mStatusBarService = iStatusBarService;
        this.mNotificationInterruptStateProvider = notificationInterruptStateProvider;
        this.mHeadsUpManager = headsUpManager;
    }

    public void attach(NotifPipeline notifPipeline) {
        notifPipeline.addCollectionListener(this.mNotifCollectionListener);
        notifPipeline.addOnBeforeFinalizeFilterListener(this.mOnBeforeFinalizeFilterListener);
        notifPipeline.addFinalizeFilter(this.mNotifInflationErrorFilter);
        notifPipeline.addFinalizeFilter(this.mNotifInflatingFilter);
    }

    /* access modifiers changed from: private */
    /* renamed from: inflateAllRequiredViews */
    public void lambda$new$0(List<ListEntry> list) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            ListEntry listEntry = (ListEntry) list.get(i);
            if (listEntry instanceof GroupEntry) {
                GroupEntry groupEntry = (GroupEntry) listEntry;
                inflateNotifRequiredViews(groupEntry.getSummary());
                List children = groupEntry.getChildren();
                int size2 = children.size();
                for (int i2 = 0; i2 < size2; i2++) {
                    inflateNotifRequiredViews((NotificationEntry) children.get(i2));
                }
            } else {
                inflateNotifRequiredViews((NotificationEntry) listEntry);
            }
        }
    }

    private void inflateNotifRequiredViews(NotificationEntry notificationEntry) {
        int intValue = ((Integer) this.mInflationStates.get(notificationEntry)).intValue();
        if (intValue == 0) {
            inflateEntry(notificationEntry, "entryAdded");
        } else if (intValue == 2) {
            rebind(notificationEntry, "entryUpdated");
        }
    }

    private void inflateEntry(NotificationEntry notificationEntry, String str) {
        abortInflation(notificationEntry, str);
        this.mNotifInflater.inflateViews(notificationEntry);
    }

    private void rebind(NotificationEntry notificationEntry, String str) {
        this.mNotifInflater.rebindViews(notificationEntry);
    }

    /* access modifiers changed from: private */
    public void abortInflation(NotificationEntry notificationEntry, String str) {
        this.mLogger.logInflationAborted(notificationEntry.getKey(), str);
        notificationEntry.abortTask();
    }

    /* access modifiers changed from: private */
    public int getInflationState(NotificationEntry notificationEntry) {
        Integer num = (Integer) this.mInflationStates.get(notificationEntry);
        Objects.requireNonNull(num, "Asking state of a notification preparation coordinator doesn't know about");
        return num.intValue();
    }
}
