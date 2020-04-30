package com.android.systemui.statusbar;

import android.content.Context;
import android.os.Handler;
import android.os.Trace;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.android.systemui.C2007R$bool;
import com.android.systemui.bubbles.BubbleController;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.notification.DynamicChildBindController;
import com.android.systemui.statusbar.notification.DynamicPrivacyController;
import com.android.systemui.statusbar.notification.DynamicPrivacyController.Listener;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.stack.ForegroundServiceSectionController;
import com.android.systemui.statusbar.notification.stack.NotificationListContainer;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.util.Assert;
import com.android.systemui.util.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class NotificationViewHierarchyManager implements Listener {
    private final boolean mAlwaysExpandNonGroupedNotification;
    private final BubbleController mBubbleController;
    private final KeyguardBypassController mBypassController;
    private final Context mContext;
    private final DynamicChildBindController mDynamicChildBindController;
    private final DynamicPrivacyController mDynamicPrivacyController;
    private final NotificationEntryManager mEntryManager;
    private final ForegroundServiceSectionController mFgsSectionController;
    protected final NotificationGroupManager mGroupManager;
    private final Handler mHandler;
    private boolean mIsHandleDynamicPrivacyChangeScheduled;
    private NotificationListContainer mListContainer;
    protected final NotificationLockscreenUserManager mLockscreenUserManager;
    private boolean mPerformingUpdate;
    private NotificationPresenter mPresenter;
    private final SysuiStatusBarStateController mStatusBarStateController;
    private final HashMap<NotificationEntry, List<NotificationEntry>> mTmpChildOrderMap = new HashMap<>();
    protected final VisualStabilityManager mVisualStabilityManager;

    public NotificationViewHierarchyManager(Context context, Handler handler, NotificationLockscreenUserManager notificationLockscreenUserManager, NotificationGroupManager notificationGroupManager, VisualStabilityManager visualStabilityManager, StatusBarStateController statusBarStateController, NotificationEntryManager notificationEntryManager, KeyguardBypassController keyguardBypassController, BubbleController bubbleController, DynamicPrivacyController dynamicPrivacyController, ForegroundServiceSectionController foregroundServiceSectionController, DynamicChildBindController dynamicChildBindController) {
        this.mContext = context;
        this.mHandler = handler;
        this.mLockscreenUserManager = notificationLockscreenUserManager;
        this.mBypassController = keyguardBypassController;
        this.mGroupManager = notificationGroupManager;
        this.mVisualStabilityManager = visualStabilityManager;
        this.mStatusBarStateController = (SysuiStatusBarStateController) statusBarStateController;
        this.mEntryManager = notificationEntryManager;
        this.mFgsSectionController = foregroundServiceSectionController;
        this.mAlwaysExpandNonGroupedNotification = context.getResources().getBoolean(C2007R$bool.config_alwaysExpandNonGroupedNotifications);
        this.mBubbleController = bubbleController;
        this.mDynamicPrivacyController = dynamicPrivacyController;
        dynamicPrivacyController.addListener(this);
        this.mDynamicChildBindController = dynamicChildBindController;
    }

    public void setUpWithPresenter(NotificationPresenter notificationPresenter, NotificationListContainer notificationListContainer) {
        this.mPresenter = notificationPresenter;
        this.mListContainer = notificationListContainer;
    }

    public void updateNotificationViews() {
        Assert.isMainThread();
        beginUpdate();
        List visibleNotifications = this.mEntryManager.getVisibleNotifications();
        ArrayList arrayList = new ArrayList(visibleNotifications.size());
        int size = visibleNotifications.size();
        int i = 0;
        while (true) {
            boolean z = true;
            if (i >= size) {
                break;
            }
            NotificationEntry notificationEntry = (NotificationEntry) visibleNotifications.get(i);
            boolean useQsMediaPlayer = Utils.useQsMediaPlayer(this.mContext);
            if (!notificationEntry.isRowDismissed() && !notificationEntry.isRowRemoved() && ((!notificationEntry.isMediaNotification() || !useQsMediaPlayer) && !this.mBubbleController.isBubbleNotificationSuppressedFromShade(notificationEntry) && !this.mFgsSectionController.hasEntry(notificationEntry))) {
                int userId = notificationEntry.getSbn().getUserId();
                int currentUserId = this.mLockscreenUserManager.getCurrentUserId();
                boolean isLockscreenPublicMode = this.mLockscreenUserManager.isLockscreenPublicMode(currentUserId);
                boolean z2 = isLockscreenPublicMode || this.mLockscreenUserManager.isLockscreenPublicMode(userId);
                if (z2 && this.mDynamicPrivacyController.isDynamicallyUnlocked() && (userId == currentUserId || userId == -1 || !this.mLockscreenUserManager.needsSeparateWorkChallenge(userId))) {
                    z2 = false;
                }
                boolean needsRedaction = this.mLockscreenUserManager.needsRedaction(notificationEntry);
                boolean z3 = z2 && needsRedaction;
                if (!isLockscreenPublicMode || this.mLockscreenUserManager.userAllowsPrivateNotificationsInPublic(currentUserId)) {
                    z = false;
                }
                notificationEntry.setSensitive(z3, z);
                notificationEntry.getRow().setNeedsRedaction(needsRedaction);
                if (this.mGroupManager.isChildInGroupWithSummary(notificationEntry.getSbn())) {
                    NotificationEntry groupSummary = this.mGroupManager.getGroupSummary(notificationEntry.getSbn());
                    List list = (List) this.mTmpChildOrderMap.get(groupSummary);
                    if (list == null) {
                        list = new ArrayList();
                        this.mTmpChildOrderMap.put(groupSummary, list);
                    }
                    list.add(notificationEntry);
                } else {
                    arrayList.add(notificationEntry.getRow());
                }
            }
            i++;
        }
        ArrayList arrayList2 = new ArrayList();
        for (int i2 = 0; i2 < this.mListContainer.getContainerChildCount(); i2++) {
            View containerChildAt = this.mListContainer.getContainerChildAt(i2);
            if (!arrayList.contains(containerChildAt) && (containerChildAt instanceof ExpandableNotificationRow)) {
                ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) containerChildAt;
                if (!expandableNotificationRow.isBlockingHelperShowing()) {
                    arrayList2.add(expandableNotificationRow);
                }
            }
        }
        Iterator it = arrayList2.iterator();
        while (it.hasNext()) {
            ExpandableNotificationRow expandableNotificationRow2 = (ExpandableNotificationRow) it.next();
            if (this.mGroupManager.isChildInGroupWithSummary(expandableNotificationRow2.getEntry().getSbn())) {
                this.mListContainer.setChildTransferInProgress(true);
            }
            if (expandableNotificationRow2.isSummaryWithChildren()) {
                expandableNotificationRow2.removeAllChildren();
            }
            this.mListContainer.removeContainerView(expandableNotificationRow2);
            this.mListContainer.setChildTransferInProgress(false);
        }
        removeNotificationChildren();
        int i3 = 0;
        while (i3 < arrayList.size()) {
            View view = (View) arrayList.get(i3);
            if (view.getParent() == null) {
                this.mVisualStabilityManager.notifyViewAddition(view);
                this.mListContainer.addContainerView(view);
            } else if (!this.mListContainer.containsView(view)) {
                arrayList.remove(view);
                i3--;
            }
            i3++;
        }
        addNotificationChildrenAndSort();
        int i4 = 0;
        for (int i5 = 0; i5 < this.mListContainer.getContainerChildCount(); i5++) {
            View containerChildAt2 = this.mListContainer.getContainerChildAt(i5);
            if ((containerChildAt2 instanceof ExpandableNotificationRow) && !((ExpandableNotificationRow) containerChildAt2).isBlockingHelperShowing()) {
                ExpandableNotificationRow expandableNotificationRow3 = (ExpandableNotificationRow) arrayList.get(i4);
                if (containerChildAt2 != expandableNotificationRow3) {
                    if (this.mVisualStabilityManager.canReorderNotification(expandableNotificationRow3)) {
                        this.mListContainer.changeViewPosition(expandableNotificationRow3, i5);
                    } else {
                        this.mVisualStabilityManager.addReorderingAllowedCallback(this.mEntryManager);
                    }
                }
                i4++;
            }
        }
        this.mDynamicChildBindController.updateChildContentViews(this.mTmpChildOrderMap);
        this.mVisualStabilityManager.onReorderingFinished();
        this.mTmpChildOrderMap.clear();
        updateRowStatesInternal();
        this.mListContainer.onNotificationViewUpdateFinished();
        endUpdate();
    }

    private void addNotificationChildrenAndSort() {
        ArrayList arrayList = new ArrayList();
        boolean z = false;
        for (int i = 0; i < this.mListContainer.getContainerChildCount(); i++) {
            View containerChildAt = this.mListContainer.getContainerChildAt(i);
            if (containerChildAt instanceof ExpandableNotificationRow) {
                ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) containerChildAt;
                List notificationChildren = expandableNotificationRow.getNotificationChildren();
                List list = (List) this.mTmpChildOrderMap.get(expandableNotificationRow.getEntry());
                int i2 = 0;
                while (list != null && i2 < list.size()) {
                    ExpandableNotificationRow row = ((NotificationEntry) list.get(i2)).getRow();
                    if (notificationChildren == null || !notificationChildren.contains(row)) {
                        if (row.getParent() != null) {
                            StringBuilder sb = new StringBuilder();
                            sb.append("trying to add a notification child that already has a parent. class:");
                            sb.append(row.getParent().getClass());
                            sb.append("\n child: ");
                            sb.append(row);
                            Log.wtf("NotificationViewHierarchyManager", sb.toString());
                            ((ViewGroup) row.getParent()).removeView(row);
                        }
                        this.mVisualStabilityManager.notifyViewAddition(row);
                        expandableNotificationRow.addChildNotification(row, i2);
                        this.mListContainer.notifyGroupChildAdded(row);
                    }
                    arrayList.add(row);
                    i2++;
                }
                z |= expandableNotificationRow.applyChildOrder(arrayList, this.mVisualStabilityManager, this.mEntryManager);
                arrayList.clear();
            }
        }
        if (z) {
            this.mListContainer.generateChildOrderChangedEvent();
        }
    }

    private void removeNotificationChildren() {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < this.mListContainer.getContainerChildCount(); i++) {
            View containerChildAt = this.mListContainer.getContainerChildAt(i);
            if (containerChildAt instanceof ExpandableNotificationRow) {
                ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) containerChildAt;
                List<ExpandableNotificationRow> notificationChildren = expandableNotificationRow.getNotificationChildren();
                List list = (List) this.mTmpChildOrderMap.get(expandableNotificationRow.getEntry());
                if (notificationChildren != null) {
                    arrayList.clear();
                    for (ExpandableNotificationRow expandableNotificationRow2 : notificationChildren) {
                        if ((list == null || !list.contains(expandableNotificationRow2.getEntry())) && !expandableNotificationRow2.keepInParent()) {
                            arrayList.add(expandableNotificationRow2);
                        }
                    }
                    Iterator it = arrayList.iterator();
                    while (it.hasNext()) {
                        ExpandableNotificationRow expandableNotificationRow3 = (ExpandableNotificationRow) it.next();
                        expandableNotificationRow.removeChildNotification(expandableNotificationRow3);
                        if (this.mEntryManager.getActiveNotificationUnfiltered(expandableNotificationRow3.getEntry().getSbn().getKey()) == null) {
                            this.mListContainer.notifyGroupChildRemoved(expandableNotificationRow3, expandableNotificationRow.getChildrenContainer());
                        }
                    }
                }
            }
        }
    }

    public void updateRowStates() {
        Assert.isMainThread();
        beginUpdate();
        updateRowStatesInternal();
        endUpdate();
    }

    private void updateRowStatesInternal() {
        Trace.beginSection("NotificationViewHierarchyManager#updateRowStates");
        int containerChildCount = this.mListContainer.getContainerChildCount();
        boolean z = this.mStatusBarStateController.getState() == 1;
        this.mListContainer.setMaxDisplayedNotifications((!z || this.mBypassController.getBypassEnabled()) ? -1 : this.mPresenter.getMaxNotificationsWhileLocked(true));
        Stack stack = new Stack();
        for (int i = containerChildCount - 1; i >= 0; i--) {
            View containerChildAt = this.mListContainer.getContainerChildAt(i);
            if (containerChildAt instanceof ExpandableNotificationRow) {
                stack.push((ExpandableNotificationRow) containerChildAt);
            }
        }
        int i2 = 0;
        while (!stack.isEmpty()) {
            ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) stack.pop();
            NotificationEntry entry = expandableNotificationRow.getEntry();
            boolean isChildInGroupWithSummary = this.mGroupManager.isChildInGroupWithSummary(entry.getSbn());
            expandableNotificationRow.setOnKeyguard(z);
            if (!z) {
                expandableNotificationRow.setSystemExpanded(this.mAlwaysExpandNonGroupedNotification || (i2 == 0 && !isChildInGroupWithSummary && !expandableNotificationRow.isLowPriority()));
            }
            int userId = entry.getSbn().getUserId();
            boolean z2 = this.mGroupManager.isSummaryOfSuppressedGroup(entry.getSbn()) && !entry.isRowRemoved();
            boolean shouldShowOnKeyguard = this.mLockscreenUserManager.shouldShowOnKeyguard(entry);
            if (!shouldShowOnKeyguard && this.mGroupManager.isChildInGroupWithSummary(entry.getSbn())) {
                NotificationEntry logicalGroupSummary = this.mGroupManager.getLogicalGroupSummary(entry.getSbn());
                if (logicalGroupSummary != null && this.mLockscreenUserManager.shouldShowOnKeyguard(logicalGroupSummary)) {
                    shouldShowOnKeyguard = true;
                }
            }
            if (z2 || this.mLockscreenUserManager.shouldHideNotifications(userId) || (z && !shouldShowOnKeyguard)) {
                entry.getRow().setVisibility(8);
            } else {
                boolean z3 = entry.getRow().getVisibility() == 8;
                if (z3) {
                    entry.getRow().setVisibility(0);
                }
                if (!isChildInGroupWithSummary && !entry.getRow().isRemoved()) {
                    if (z3) {
                        this.mListContainer.generateAddAnimation(entry.getRow(), !shouldShowOnKeyguard);
                    }
                    i2++;
                }
            }
            if (expandableNotificationRow.isSummaryWithChildren()) {
                List notificationChildren = expandableNotificationRow.getNotificationChildren();
                for (int size = notificationChildren.size() - 1; size >= 0; size--) {
                    stack.push((ExpandableNotificationRow) notificationChildren.get(size));
                }
            }
            expandableNotificationRow.showAppOpsIcons(entry.mActiveAppOps);
            expandableNotificationRow.setLastAudiblyAlertedMs(entry.getLastAudiblyAlertedMs());
        }
        Trace.beginSection("NotificationPresenter#onUpdateRowStates");
        this.mPresenter.onUpdateRowStates();
        Trace.endSection();
        Trace.endSection();
    }

    public void onDynamicPrivacyChanged() {
        if (this.mPerformingUpdate) {
            Log.w("NotificationViewHierarchyManager", "onDynamicPrivacyChanged made a re-entrant call");
        }
        if (!this.mIsHandleDynamicPrivacyChangeScheduled) {
            this.mIsHandleDynamicPrivacyChangeScheduled = true;
            this.mHandler.post(new Runnable() {
                public final void run() {
                    NotificationViewHierarchyManager.this.onHandleDynamicPrivacyChanged();
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void onHandleDynamicPrivacyChanged() {
        this.mIsHandleDynamicPrivacyChangeScheduled = false;
        updateNotificationViews();
    }

    private void beginUpdate() {
        if (this.mPerformingUpdate) {
            Log.wtf("NotificationViewHierarchyManager", "Re-entrant code during update", new Exception());
        }
        this.mPerformingUpdate = true;
    }

    private void endUpdate() {
        if (!this.mPerformingUpdate) {
            Log.wtf("NotificationViewHierarchyManager", "Manager state has become desynced", new Exception());
        }
        this.mPerformingUpdate = false;
    }
}
