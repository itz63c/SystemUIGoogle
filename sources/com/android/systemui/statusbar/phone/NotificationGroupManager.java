package com.android.systemui.statusbar.phone;

import android.service.notification.StatusBarNotification;
import android.util.ArraySet;
import android.util.Log;
import com.android.systemui.Dependency;
import com.android.systemui.bubbles.BubbleController;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Objects;

public class NotificationGroupManager implements OnHeadsUpChangedListener, StateListener {
    private int mBarState = -1;
    private BubbleController mBubbleController = null;
    private final HashMap<String, NotificationGroup> mGroupMap = new HashMap<>();
    private HeadsUpManager mHeadsUpManager;
    private boolean mIsUpdatingUnchangedGroup;
    private HashMap<String, StatusBarNotification> mIsolatedEntries = new HashMap<>();
    private final ArraySet<OnGroupChangeListener> mListeners = new ArraySet<>();

    public static class NotificationGroup {
        public final HashMap<String, NotificationEntry> children = new HashMap<>();
        public boolean expanded;
        public NotificationEntry summary;
        public boolean suppressed;

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("    summary:\n      ");
            NotificationEntry notificationEntry = this.summary;
            sb.append(notificationEntry != null ? notificationEntry.getSbn() : "null");
            NotificationEntry notificationEntry2 = this.summary;
            String str = "";
            sb.append((notificationEntry2 == null || notificationEntry2.getDebugThrowable() == null) ? str : Log.getStackTraceString(this.summary.getDebugThrowable()));
            String sb2 = sb.toString();
            StringBuilder sb3 = new StringBuilder();
            sb3.append(sb2);
            sb3.append("\n    children size: ");
            sb3.append(this.children.size());
            String sb4 = sb3.toString();
            for (NotificationEntry notificationEntry3 : this.children.values()) {
                StringBuilder sb5 = new StringBuilder();
                sb5.append(sb4);
                sb5.append("\n      ");
                sb5.append(notificationEntry3.getSbn());
                sb5.append(notificationEntry3.getDebugThrowable() != null ? Log.getStackTraceString(notificationEntry3.getDebugThrowable()) : str);
                sb4 = sb5.toString();
            }
            StringBuilder sb6 = new StringBuilder();
            sb6.append(sb4);
            sb6.append("\n    summary suppressed: ");
            sb6.append(this.suppressed);
            return sb6.toString();
        }
    }

    public interface OnGroupChangeListener {
        void onGroupCreated(NotificationGroup notificationGroup, String str) {
        }

        void onGroupCreatedFromChildren(NotificationGroup notificationGroup) {
        }

        void onGroupExpansionChanged(ExpandableNotificationRow expandableNotificationRow, boolean z) {
        }

        void onGroupRemoved(NotificationGroup notificationGroup, String str) {
        }

        void onGroupSuppressionChanged(NotificationGroup notificationGroup, boolean z) {
        }

        void onGroupsChanged() {
        }
    }

    public NotificationGroupManager(StatusBarStateController statusBarStateController) {
        statusBarStateController.addCallback(this);
    }

    private BubbleController getBubbleController() {
        if (this.mBubbleController == null) {
            this.mBubbleController = (BubbleController) Dependency.get(BubbleController.class);
        }
        return this.mBubbleController;
    }

    public void addOnGroupChangeListener(OnGroupChangeListener onGroupChangeListener) {
        this.mListeners.add(onGroupChangeListener);
    }

    public boolean isGroupExpanded(StatusBarNotification statusBarNotification) {
        NotificationGroup notificationGroup = (NotificationGroup) this.mGroupMap.get(getGroupKey(statusBarNotification));
        if (notificationGroup == null) {
            return false;
        }
        return notificationGroup.expanded;
    }

    public void setGroupExpanded(StatusBarNotification statusBarNotification, boolean z) {
        NotificationGroup notificationGroup = (NotificationGroup) this.mGroupMap.get(getGroupKey(statusBarNotification));
        if (notificationGroup != null) {
            setGroupExpanded(notificationGroup, z);
        }
    }

    private void setGroupExpanded(NotificationGroup notificationGroup, boolean z) {
        notificationGroup.expanded = z;
        if (notificationGroup.summary != null) {
            Iterator it = this.mListeners.iterator();
            while (it.hasNext()) {
                ((OnGroupChangeListener) it.next()).onGroupExpansionChanged(notificationGroup.summary.getRow(), z);
            }
        }
    }

    public void onEntryRemoved(NotificationEntry notificationEntry) {
        onEntryRemovedInternal(notificationEntry, notificationEntry.getSbn());
        this.mIsolatedEntries.remove(notificationEntry.getKey());
    }

    private void onEntryRemovedInternal(NotificationEntry notificationEntry, StatusBarNotification statusBarNotification) {
        onEntryRemovedInternal(notificationEntry, statusBarNotification.getGroupKey(), statusBarNotification.isGroup(), statusBarNotification.getNotification().isGroupSummary());
    }

    private void onEntryRemovedInternal(NotificationEntry notificationEntry, String str, boolean z, boolean z2) {
        String groupKey = getGroupKey(notificationEntry.getKey(), str);
        NotificationGroup notificationGroup = (NotificationGroup) this.mGroupMap.get(groupKey);
        if (notificationGroup != null) {
            if (isGroupChild(notificationEntry.getKey(), z, z2)) {
                notificationGroup.children.remove(notificationEntry.getKey());
            } else {
                notificationGroup.summary = null;
            }
            updateSuppression(notificationGroup);
            if (notificationGroup.children.isEmpty() && notificationGroup.summary == null) {
                this.mGroupMap.remove(groupKey);
                Iterator it = this.mListeners.iterator();
                while (it.hasNext()) {
                    ((OnGroupChangeListener) it.next()).onGroupRemoved(notificationGroup, groupKey);
                }
            }
        }
    }

    public void onEntryAdded(NotificationEntry notificationEntry) {
        String str;
        if (notificationEntry.isRowRemoved()) {
            notificationEntry.setDebugThrowable(new Throwable());
        }
        StatusBarNotification sbn = notificationEntry.getSbn();
        boolean isGroupChild = isGroupChild(sbn);
        String groupKey = getGroupKey(sbn);
        NotificationGroup notificationGroup = (NotificationGroup) this.mGroupMap.get(groupKey);
        if (notificationGroup == null) {
            notificationGroup = new NotificationGroup();
            this.mGroupMap.put(groupKey, notificationGroup);
            Iterator it = this.mListeners.iterator();
            while (it.hasNext()) {
                ((OnGroupChangeListener) it.next()).onGroupCreated(notificationGroup, groupKey);
            }
        }
        if (isGroupChild) {
            NotificationEntry notificationEntry2 = (NotificationEntry) notificationGroup.children.get(notificationEntry.getKey());
            if (!(notificationEntry2 == null || notificationEntry2 == notificationEntry)) {
                Throwable debugThrowable = notificationEntry2.getDebugThrowable();
                StringBuilder sb = new StringBuilder();
                sb.append("Inconsistent entries found with the same key ");
                sb.append(notificationEntry.getKey());
                sb.append("existing removed: ");
                sb.append(notificationEntry2.isRowRemoved());
                if (debugThrowable != null) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(Log.getStackTraceString(debugThrowable));
                    sb2.append("\n");
                    str = sb2.toString();
                } else {
                    str = "";
                }
                sb.append(str);
                sb.append(" added removed");
                sb.append(notificationEntry.isRowRemoved());
                Log.wtf("NotificationGroupManager", sb.toString(), new Throwable());
            }
            notificationGroup.children.put(notificationEntry.getKey(), notificationEntry);
            updateSuppression(notificationGroup);
            return;
        }
        notificationGroup.summary = notificationEntry;
        notificationGroup.expanded = notificationEntry.areChildrenExpanded();
        updateSuppression(notificationGroup);
        if (!notificationGroup.children.isEmpty()) {
            Iterator it2 = new ArrayList(notificationGroup.children.values()).iterator();
            while (it2.hasNext()) {
                onEntryBecomingChild((NotificationEntry) it2.next());
            }
            Iterator it3 = this.mListeners.iterator();
            while (it3.hasNext()) {
                ((OnGroupChangeListener) it3.next()).onGroupCreatedFromChildren(notificationGroup);
            }
        }
    }

    private void onEntryBecomingChild(NotificationEntry notificationEntry) {
        if (shouldIsolate(notificationEntry)) {
            isolateNotification(notificationEntry);
        }
    }

    private void updateSuppression(NotificationGroup notificationGroup) {
        if (notificationGroup != null) {
            boolean z = false;
            int i = 0;
            boolean z2 = false;
            for (NotificationEntry isBubbleNotificationSuppressedFromShade : notificationGroup.children.values()) {
                if (!getBubbleController().isBubbleNotificationSuppressedFromShade(isBubbleNotificationSuppressedFromShade)) {
                    i++;
                } else {
                    z2 = true;
                }
            }
            boolean z3 = notificationGroup.suppressed;
            NotificationEntry notificationEntry = notificationGroup.summary;
            if (notificationEntry != null && !notificationGroup.expanded && (i == 1 || (i == 0 && notificationEntry.getSbn().getNotification().isGroupSummary() && (hasIsolatedChildren(notificationGroup) || z2)))) {
                z = true;
            }
            notificationGroup.suppressed = z;
            if (z3 != z) {
                Iterator it = this.mListeners.iterator();
                while (it.hasNext()) {
                    OnGroupChangeListener onGroupChangeListener = (OnGroupChangeListener) it.next();
                    if (!this.mIsUpdatingUnchangedGroup) {
                        onGroupChangeListener.onGroupSuppressionChanged(notificationGroup, notificationGroup.suppressed);
                        onGroupChangeListener.onGroupsChanged();
                    }
                }
            }
        }
    }

    private boolean hasIsolatedChildren(NotificationGroup notificationGroup) {
        return getNumberOfIsolatedChildren(notificationGroup.summary.getSbn().getGroupKey()) != 0;
    }

    private int getNumberOfIsolatedChildren(String str) {
        int i = 0;
        for (StatusBarNotification statusBarNotification : this.mIsolatedEntries.values()) {
            if (statusBarNotification.getGroupKey().equals(str) && isIsolated(statusBarNotification.getKey())) {
                i++;
            }
        }
        return i;
    }

    private NotificationEntry getIsolatedChild(String str) {
        for (StatusBarNotification statusBarNotification : this.mIsolatedEntries.values()) {
            if (statusBarNotification.getGroupKey().equals(str) && isIsolated(statusBarNotification.getKey())) {
                return ((NotificationGroup) this.mGroupMap.get(statusBarNotification.getKey())).summary;
            }
        }
        return null;
    }

    public void onEntryUpdated(NotificationEntry notificationEntry, StatusBarNotification statusBarNotification) {
        onEntryUpdated(notificationEntry, statusBarNotification.getGroupKey(), statusBarNotification.isGroup(), statusBarNotification.getNotification().isGroupSummary());
    }

    public void onEntryUpdated(NotificationEntry notificationEntry, String str, boolean z, boolean z2) {
        String groupKey = notificationEntry.getSbn().getGroupKey();
        boolean z3 = true;
        boolean z4 = !str.equals(groupKey);
        boolean isGroupChild = isGroupChild(notificationEntry.getKey(), z, z2);
        boolean isGroupChild2 = isGroupChild(notificationEntry.getSbn());
        if (z4 || isGroupChild != isGroupChild2) {
            z3 = false;
        }
        this.mIsUpdatingUnchangedGroup = z3;
        if (this.mGroupMap.get(getGroupKey(notificationEntry.getKey(), str)) != null) {
            onEntryRemovedInternal(notificationEntry, str, z, z2);
        }
        onEntryAdded(notificationEntry);
        this.mIsUpdatingUnchangedGroup = false;
        if (isIsolated(notificationEntry.getSbn().getKey())) {
            this.mIsolatedEntries.put(notificationEntry.getKey(), notificationEntry.getSbn());
            if (z4) {
                updateSuppression((NotificationGroup) this.mGroupMap.get(str));
                updateSuppression((NotificationGroup) this.mGroupMap.get(groupKey));
            }
        } else if (!isGroupChild && isGroupChild2) {
            onEntryBecomingChild(notificationEntry);
        }
    }

    public boolean isSummaryOfSuppressedGroup(StatusBarNotification statusBarNotification) {
        return isGroupSuppressed(getGroupKey(statusBarNotification)) && statusBarNotification.getNotification().isGroupSummary();
    }

    private boolean isOnlyChild(StatusBarNotification statusBarNotification) {
        if (statusBarNotification.getNotification().isGroupSummary() || getTotalNumberOfChildren(statusBarNotification) != 1) {
            return false;
        }
        return true;
    }

    public boolean isOnlyChildInGroup(StatusBarNotification statusBarNotification) {
        boolean z = false;
        if (!isOnlyChild(statusBarNotification)) {
            return false;
        }
        NotificationEntry logicalGroupSummary = getLogicalGroupSummary(statusBarNotification);
        if (logicalGroupSummary != null && !logicalGroupSummary.getSbn().equals(statusBarNotification)) {
            z = true;
        }
        return z;
    }

    private int getTotalNumberOfChildren(StatusBarNotification statusBarNotification) {
        int numberOfIsolatedChildren = getNumberOfIsolatedChildren(statusBarNotification.getGroupKey());
        NotificationGroup notificationGroup = (NotificationGroup) this.mGroupMap.get(statusBarNotification.getGroupKey());
        return numberOfIsolatedChildren + (notificationGroup != null ? notificationGroup.children.size() : 0);
    }

    private boolean isGroupSuppressed(String str) {
        NotificationGroup notificationGroup = (NotificationGroup) this.mGroupMap.get(str);
        return notificationGroup != null && notificationGroup.suppressed;
    }

    private void setStatusBarState(int i) {
        this.mBarState = i;
        if (i == 1) {
            collapseAllGroups();
        }
    }

    public void collapseAllGroups() {
        ArrayList arrayList = new ArrayList(this.mGroupMap.values());
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            NotificationGroup notificationGroup = (NotificationGroup) arrayList.get(i);
            if (notificationGroup.expanded) {
                setGroupExpanded(notificationGroup, false);
            }
            updateSuppression(notificationGroup);
        }
    }

    public boolean isChildInGroupWithSummary(StatusBarNotification statusBarNotification) {
        if (!isGroupChild(statusBarNotification)) {
            return false;
        }
        NotificationGroup notificationGroup = (NotificationGroup) this.mGroupMap.get(getGroupKey(statusBarNotification));
        if (notificationGroup == null || notificationGroup.summary == null || notificationGroup.suppressed || notificationGroup.children.isEmpty()) {
            return false;
        }
        return true;
    }

    public boolean isSummaryOfGroup(StatusBarNotification statusBarNotification) {
        boolean z = false;
        if (!isGroupSummary(statusBarNotification)) {
            return false;
        }
        NotificationGroup notificationGroup = (NotificationGroup) this.mGroupMap.get(getGroupKey(statusBarNotification));
        if (notificationGroup != null && notificationGroup.summary != null && !notificationGroup.children.isEmpty() && Objects.equals(notificationGroup.summary.getSbn(), statusBarNotification)) {
            z = true;
        }
        return z;
    }

    public NotificationEntry getGroupSummary(StatusBarNotification statusBarNotification) {
        return getGroupSummary(getGroupKey(statusBarNotification));
    }

    public NotificationEntry getLogicalGroupSummary(StatusBarNotification statusBarNotification) {
        return getGroupSummary(statusBarNotification.getGroupKey());
    }

    private NotificationEntry getGroupSummary(String str) {
        NotificationGroup notificationGroup = (NotificationGroup) this.mGroupMap.get(str);
        if (notificationGroup == null) {
            return null;
        }
        return notificationGroup.summary;
    }

    public ArrayList<NotificationEntry> getLogicalChildren(StatusBarNotification statusBarNotification) {
        NotificationGroup notificationGroup = (NotificationGroup) this.mGroupMap.get(statusBarNotification.getGroupKey());
        if (notificationGroup == null) {
            return null;
        }
        ArrayList<NotificationEntry> arrayList = new ArrayList<>(notificationGroup.children.values());
        NotificationEntry isolatedChild = getIsolatedChild(statusBarNotification.getGroupKey());
        if (isolatedChild != null) {
            arrayList.add(isolatedChild);
        }
        return arrayList;
    }

    public void updateSuppression(NotificationEntry notificationEntry) {
        NotificationGroup notificationGroup = (NotificationGroup) this.mGroupMap.get(getGroupKey(notificationEntry.getSbn()));
        if (notificationGroup != null) {
            updateSuppression(notificationGroup);
        }
    }

    public String getGroupKey(StatusBarNotification statusBarNotification) {
        return getGroupKey(statusBarNotification.getKey(), statusBarNotification.getGroupKey());
    }

    private String getGroupKey(String str, String str2) {
        return isIsolated(str) ? str : str2;
    }

    public boolean toggleGroupExpansion(StatusBarNotification statusBarNotification) {
        NotificationGroup notificationGroup = (NotificationGroup) this.mGroupMap.get(getGroupKey(statusBarNotification));
        if (notificationGroup == null) {
            return false;
        }
        setGroupExpanded(notificationGroup, !notificationGroup.expanded);
        return notificationGroup.expanded;
    }

    private boolean isIsolated(String str) {
        return this.mIsolatedEntries.containsKey(str);
    }

    public boolean isGroupSummary(StatusBarNotification statusBarNotification) {
        if (isIsolated(statusBarNotification.getKey())) {
            return true;
        }
        return statusBarNotification.getNotification().isGroupSummary();
    }

    public boolean isGroupChild(StatusBarNotification statusBarNotification) {
        return isGroupChild(statusBarNotification.getKey(), statusBarNotification.isGroup(), statusBarNotification.getNotification().isGroupSummary());
    }

    private boolean isGroupChild(String str, boolean z, boolean z2) {
        boolean isIsolated = isIsolated(str);
        boolean z3 = false;
        if (isIsolated) {
            return false;
        }
        if (z && !z2) {
            z3 = true;
        }
        return z3;
    }

    public void onHeadsUpStateChanged(NotificationEntry notificationEntry, boolean z) {
        onAlertStateChanged(notificationEntry, z);
    }

    private void onAlertStateChanged(NotificationEntry notificationEntry, boolean z) {
        if (!z) {
            stopIsolatingNotification(notificationEntry);
        } else if (shouldIsolate(notificationEntry)) {
            isolateNotification(notificationEntry);
        }
    }

    private boolean shouldIsolate(NotificationEntry notificationEntry) {
        StatusBarNotification sbn = notificationEntry.getSbn();
        NotificationGroup notificationGroup = (NotificationGroup) this.mGroupMap.get(sbn.getGroupKey());
        boolean z = false;
        if (sbn.isGroup() && !sbn.getNotification().isGroupSummary()) {
            HeadsUpManager headsUpManager = this.mHeadsUpManager;
            if (headsUpManager != null && !headsUpManager.isAlerting(notificationEntry.getKey())) {
                return false;
            }
            if (sbn.getNotification().fullScreenIntent != null || notificationGroup == null || !notificationGroup.expanded || isGroupNotFullyVisible(notificationGroup)) {
                z = true;
            }
        }
        return z;
    }

    private void isolateNotification(NotificationEntry notificationEntry) {
        StatusBarNotification sbn = notificationEntry.getSbn();
        onEntryRemovedInternal(notificationEntry, notificationEntry.getSbn());
        this.mIsolatedEntries.put(sbn.getKey(), sbn);
        onEntryAdded(notificationEntry);
        updateSuppression((NotificationGroup) this.mGroupMap.get(notificationEntry.getSbn().getGroupKey()));
        Iterator it = this.mListeners.iterator();
        while (it.hasNext()) {
            ((OnGroupChangeListener) it.next()).onGroupsChanged();
        }
    }

    private void stopIsolatingNotification(NotificationEntry notificationEntry) {
        StatusBarNotification sbn = notificationEntry.getSbn();
        if (this.mIsolatedEntries.containsKey(sbn.getKey())) {
            onEntryRemovedInternal(notificationEntry, notificationEntry.getSbn());
            this.mIsolatedEntries.remove(sbn.getKey());
            onEntryAdded(notificationEntry);
            Iterator it = this.mListeners.iterator();
            while (it.hasNext()) {
                ((OnGroupChangeListener) it.next()).onGroupsChanged();
            }
        }
    }

    private boolean isGroupNotFullyVisible(NotificationGroup notificationGroup) {
        NotificationEntry notificationEntry = notificationGroup.summary;
        return notificationEntry == null || notificationEntry.isGroupNotFullyVisible();
    }

    public void setHeadsUpManager(HeadsUpManager headsUpManager) {
        this.mHeadsUpManager = headsUpManager;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("GroupManager state:");
        StringBuilder sb = new StringBuilder();
        sb.append("  number of groups: ");
        sb.append(this.mGroupMap.size());
        printWriter.println(sb.toString());
        for (Entry entry : this.mGroupMap.entrySet()) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("\n    key: ");
            sb2.append((String) entry.getKey());
            printWriter.println(sb2.toString());
            printWriter.println(entry.getValue());
        }
        StringBuilder sb3 = new StringBuilder();
        sb3.append("\n    isolated entries: ");
        sb3.append(this.mIsolatedEntries.size());
        printWriter.println(sb3.toString());
        for (Entry entry2 : this.mIsolatedEntries.entrySet()) {
            printWriter.print("      ");
            printWriter.print((String) entry2.getKey());
            printWriter.print(", ");
            printWriter.println(entry2.getValue());
        }
    }

    public void onStateChanged(int i) {
        setStatusBarState(i);
    }
}
