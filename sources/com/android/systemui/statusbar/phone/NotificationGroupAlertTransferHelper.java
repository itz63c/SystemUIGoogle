package com.android.systemui.statusbar.phone;

import android.os.SystemClock;
import android.service.notification.StatusBarNotification;
import android.util.ArrayMap;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.systemui.Dependency;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener;
import com.android.systemui.statusbar.AlertingNotificationManager;
import com.android.systemui.statusbar.notification.NotificationEntryListener;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.NotifBindPipeline.BindCallback;
import com.android.systemui.statusbar.notification.row.RowContentBindParams;
import com.android.systemui.statusbar.notification.row.RowContentBindStage;
import com.android.systemui.statusbar.phone.NotificationGroupManager.NotificationGroup;
import com.android.systemui.statusbar.phone.NotificationGroupManager.OnGroupChangeListener;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;
import java.util.ArrayList;
import java.util.Objects;

public class NotificationGroupAlertTransferHelper implements OnHeadsUpChangedListener, StateListener {
    private NotificationEntryManager mEntryManager;
    /* access modifiers changed from: private */
    public final ArrayMap<String, GroupAlertEntry> mGroupAlertEntries = new ArrayMap<>();
    /* access modifiers changed from: private */
    public final NotificationGroupManager mGroupManager = ((NotificationGroupManager) Dependency.get(NotificationGroupManager.class));
    /* access modifiers changed from: private */
    public HeadsUpManager mHeadsUpManager;
    private boolean mIsDozing;
    private final NotificationEntryListener mNotificationEntryListener = new NotificationEntryListener() {
        public void onPendingEntryAdded(NotificationEntry notificationEntry) {
            GroupAlertEntry groupAlertEntry = (GroupAlertEntry) NotificationGroupAlertTransferHelper.this.mGroupAlertEntries.get(NotificationGroupAlertTransferHelper.this.mGroupManager.getGroupKey(notificationEntry.getSbn()));
            if (groupAlertEntry != null) {
                NotificationGroupAlertTransferHelper.this.checkShouldTransferBack(groupAlertEntry);
            }
        }

        public void onEntryRemoved(NotificationEntry notificationEntry, NotificationVisibility notificationVisibility, boolean z, int i) {
            NotificationGroupAlertTransferHelper.this.mPendingAlerts.remove(notificationEntry.getKey());
        }
    };
    private final OnGroupChangeListener mOnGroupChangeListener = new OnGroupChangeListener() {
        public void onGroupCreated(NotificationGroup notificationGroup, String str) {
            NotificationGroupAlertTransferHelper.this.mGroupAlertEntries.put(str, new GroupAlertEntry(notificationGroup));
        }

        public void onGroupRemoved(NotificationGroup notificationGroup, String str) {
            NotificationGroupAlertTransferHelper.this.mGroupAlertEntries.remove(str);
        }

        public void onGroupSuppressionChanged(NotificationGroup notificationGroup, boolean z) {
            if (z) {
                if (NotificationGroupAlertTransferHelper.this.mHeadsUpManager.isAlerting(notificationGroup.summary.getKey())) {
                    NotificationGroupAlertTransferHelper notificationGroupAlertTransferHelper = NotificationGroupAlertTransferHelper.this;
                    notificationGroupAlertTransferHelper.handleSuppressedSummaryAlerted(notificationGroup.summary, notificationGroupAlertTransferHelper.mHeadsUpManager);
                }
            } else if (notificationGroup.summary != null) {
                GroupAlertEntry groupAlertEntry = (GroupAlertEntry) NotificationGroupAlertTransferHelper.this.mGroupAlertEntries.get(NotificationGroupAlertTransferHelper.this.mGroupManager.getGroupKey(notificationGroup.summary.getSbn()));
                if (groupAlertEntry.mAlertSummaryOnNextAddition) {
                    if (!NotificationGroupAlertTransferHelper.this.mHeadsUpManager.isAlerting(notificationGroup.summary.getKey())) {
                        NotificationGroupAlertTransferHelper notificationGroupAlertTransferHelper2 = NotificationGroupAlertTransferHelper.this;
                        notificationGroupAlertTransferHelper2.alertNotificationWhenPossible(notificationGroup.summary, notificationGroupAlertTransferHelper2.mHeadsUpManager);
                    }
                    groupAlertEntry.mAlertSummaryOnNextAddition = false;
                } else {
                    NotificationGroupAlertTransferHelper.this.checkShouldTransferBack(groupAlertEntry);
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public final ArrayMap<String, PendingAlertInfo> mPendingAlerts = new ArrayMap<>();
    private final RowContentBindStage mRowContentBindStage;

    private static class GroupAlertEntry {
        boolean mAlertSummaryOnNextAddition;
        final NotificationGroup mGroup;
        long mLastAlertTransferTime;

        GroupAlertEntry(NotificationGroup notificationGroup) {
            this.mGroup = notificationGroup;
        }
    }

    private class PendingAlertInfo {
        boolean mAbortOnInflation;
        final NotificationEntry mEntry;
        final StatusBarNotification mOriginalNotification;

        PendingAlertInfo(NotificationGroupAlertTransferHelper notificationGroupAlertTransferHelper, NotificationEntry notificationEntry) {
            this.mOriginalNotification = notificationEntry.getSbn();
            this.mEntry = notificationEntry;
        }

        /* access modifiers changed from: private */
        public boolean isStillValid() {
            if (!this.mAbortOnInflation && this.mEntry.getSbn().getGroupKey() == this.mOriginalNotification.getGroupKey() && this.mEntry.getSbn().getNotification().isGroupSummary() == this.mOriginalNotification.getNotification().isGroupSummary()) {
                return true;
            }
            return false;
        }
    }

    public void onStateChanged(int i) {
    }

    public NotificationGroupAlertTransferHelper(RowContentBindStage rowContentBindStage) {
        ((StatusBarStateController) Dependency.get(StatusBarStateController.class)).addCallback(this);
        this.mRowContentBindStage = rowContentBindStage;
    }

    public void bind(NotificationEntryManager notificationEntryManager, NotificationGroupManager notificationGroupManager) {
        if (this.mEntryManager == null) {
            this.mEntryManager = notificationEntryManager;
            notificationEntryManager.addNotificationEntryListener(this.mNotificationEntryListener);
            notificationGroupManager.addOnGroupChangeListener(this.mOnGroupChangeListener);
            return;
        }
        throw new IllegalStateException("Already bound.");
    }

    public void setHeadsUpManager(HeadsUpManager headsUpManager) {
        this.mHeadsUpManager = headsUpManager;
    }

    public void onDozingChanged(boolean z) {
        if (this.mIsDozing != z) {
            for (GroupAlertEntry groupAlertEntry : this.mGroupAlertEntries.values()) {
                groupAlertEntry.mLastAlertTransferTime = 0;
                groupAlertEntry.mAlertSummaryOnNextAddition = false;
            }
        }
        this.mIsDozing = z;
    }

    public void onHeadsUpStateChanged(NotificationEntry notificationEntry, boolean z) {
        onAlertStateChanged(notificationEntry, z, this.mHeadsUpManager);
    }

    private void onAlertStateChanged(NotificationEntry notificationEntry, boolean z, AlertingNotificationManager alertingNotificationManager) {
        if (z && this.mGroupManager.isSummaryOfSuppressedGroup(notificationEntry.getSbn())) {
            handleSuppressedSummaryAlerted(notificationEntry, alertingNotificationManager);
        }
    }

    private int getPendingChildrenNotAlerting(NotificationGroup notificationGroup) {
        NotificationEntryManager notificationEntryManager = this.mEntryManager;
        int i = 0;
        if (notificationEntryManager == null) {
            return 0;
        }
        for (NotificationEntry notificationEntry : notificationEntryManager.getPendingNotificationsIterator()) {
            if (isPendingNotificationInGroup(notificationEntry, notificationGroup) && onlySummaryAlerts(notificationEntry)) {
                i++;
            }
        }
        return i;
    }

    private boolean pendingInflationsWillAddChildren(NotificationGroup notificationGroup) {
        NotificationEntryManager notificationEntryManager = this.mEntryManager;
        if (notificationEntryManager == null) {
            return false;
        }
        for (NotificationEntry isPendingNotificationInGroup : notificationEntryManager.getPendingNotificationsIterator()) {
            if (isPendingNotificationInGroup(isPendingNotificationInGroup, notificationGroup)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPendingNotificationInGroup(NotificationEntry notificationEntry, NotificationGroup notificationGroup) {
        return this.mGroupManager.isGroupChild(notificationEntry.getSbn()) && Objects.equals(this.mGroupManager.getGroupKey(notificationEntry.getSbn()), this.mGroupManager.getGroupKey(notificationGroup.summary.getSbn())) && !notificationGroup.children.containsKey(notificationEntry.getKey());
    }

    /* access modifiers changed from: private */
    public void handleSuppressedSummaryAlerted(NotificationEntry notificationEntry, AlertingNotificationManager alertingNotificationManager) {
        StatusBarNotification sbn = notificationEntry.getSbn();
        GroupAlertEntry groupAlertEntry = (GroupAlertEntry) this.mGroupAlertEntries.get(this.mGroupManager.getGroupKey(sbn));
        if (this.mGroupManager.isSummaryOfSuppressedGroup(notificationEntry.getSbn()) && alertingNotificationManager.isAlerting(sbn.getKey()) && groupAlertEntry != null && !pendingInflationsWillAddChildren(groupAlertEntry.mGroup)) {
            NotificationEntry notificationEntry2 = (NotificationEntry) this.mGroupManager.getLogicalChildren(notificationEntry.getSbn()).iterator().next();
            if (notificationEntry2 != null && !notificationEntry2.getRow().keepInParent() && !notificationEntry2.isRowRemoved() && !notificationEntry2.isRowDismissed()) {
                if (!alertingNotificationManager.isAlerting(notificationEntry2.getKey()) && onlySummaryAlerts(notificationEntry)) {
                    groupAlertEntry.mLastAlertTransferTime = SystemClock.elapsedRealtime();
                }
                transferAlertState(notificationEntry, notificationEntry2, alertingNotificationManager);
            }
        }
    }

    private void transferAlertState(NotificationEntry notificationEntry, NotificationEntry notificationEntry2, AlertingNotificationManager alertingNotificationManager) {
        alertingNotificationManager.removeNotification(notificationEntry.getKey(), true);
        alertNotificationWhenPossible(notificationEntry2, alertingNotificationManager);
    }

    /* access modifiers changed from: private */
    public void checkShouldTransferBack(GroupAlertEntry groupAlertEntry) {
        if (SystemClock.elapsedRealtime() - groupAlertEntry.mLastAlertTransferTime < 300) {
            NotificationEntry notificationEntry = groupAlertEntry.mGroup.summary;
            if (onlySummaryAlerts(notificationEntry)) {
                ArrayList logicalChildren = this.mGroupManager.getLogicalChildren(notificationEntry.getSbn());
                int size = logicalChildren.size();
                int pendingChildrenNotAlerting = getPendingChildrenNotAlerting(groupAlertEntry.mGroup);
                int i = size + pendingChildrenNotAlerting;
                if (i > 1) {
                    boolean z = false;
                    boolean z2 = false;
                    for (int i2 = 0; i2 < logicalChildren.size(); i2++) {
                        NotificationEntry notificationEntry2 = (NotificationEntry) logicalChildren.get(i2);
                        if (onlySummaryAlerts(notificationEntry2) && this.mHeadsUpManager.isAlerting(notificationEntry2.getKey())) {
                            this.mHeadsUpManager.removeNotification(notificationEntry2.getKey(), true);
                            z2 = true;
                        }
                        if (this.mPendingAlerts.containsKey(notificationEntry2.getKey())) {
                            ((PendingAlertInfo) this.mPendingAlerts.get(notificationEntry2.getKey())).mAbortOnInflation = true;
                            z2 = true;
                        }
                    }
                    if (z2 && !this.mHeadsUpManager.isAlerting(notificationEntry.getKey())) {
                        if (i - pendingChildrenNotAlerting > 1) {
                            z = true;
                        }
                        if (z) {
                            alertNotificationWhenPossible(notificationEntry, this.mHeadsUpManager);
                        } else {
                            groupAlertEntry.mAlertSummaryOnNextAddition = true;
                        }
                        groupAlertEntry.mLastAlertTransferTime = 0;
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void alertNotificationWhenPossible(NotificationEntry notificationEntry, AlertingNotificationManager alertingNotificationManager) {
        int contentFlag = alertingNotificationManager.getContentFlag();
        RowContentBindParams rowContentBindParams = (RowContentBindParams) this.mRowContentBindStage.getStageParams(notificationEntry);
        if ((rowContentBindParams.getContentViews() & contentFlag) == 0) {
            this.mPendingAlerts.put(notificationEntry.getKey(), new PendingAlertInfo(this, notificationEntry));
            rowContentBindParams.requireContentViews(contentFlag);
            this.mRowContentBindStage.requestRebind(notificationEntry, new BindCallback(notificationEntry) {
                public final /* synthetic */ NotificationEntry f$1;

                {
                    this.f$1 = r2;
                }

                public final void onBindFinished(NotificationEntry notificationEntry) {
                    NotificationGroupAlertTransferHelper.this.mo17546x595059e4(this.f$1, notificationEntry);
                }
            });
            return;
        }
        if (alertingNotificationManager.isAlerting(notificationEntry.getKey())) {
            alertingNotificationManager.updateNotification(notificationEntry.getKey(), true);
        } else {
            alertingNotificationManager.showNotification(notificationEntry);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$alertNotificationWhenPossible$0 */
    public /* synthetic */ void mo17546x595059e4(NotificationEntry notificationEntry, NotificationEntry notificationEntry2) {
        PendingAlertInfo pendingAlertInfo = (PendingAlertInfo) this.mPendingAlerts.remove(notificationEntry.getKey());
        if (pendingAlertInfo == null) {
            return;
        }
        if (pendingAlertInfo.isStillValid()) {
            alertNotificationWhenPossible(notificationEntry, this.mHeadsUpManager);
        } else {
            notificationEntry.getRow().freeContentViewWhenSafe(this.mHeadsUpManager.getContentFlag());
        }
    }

    private boolean onlySummaryAlerts(NotificationEntry notificationEntry) {
        return notificationEntry.getSbn().getNotification().getGroupAlertBehavior() == 1;
    }
}
