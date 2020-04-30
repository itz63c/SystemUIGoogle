package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Region;
import android.util.Pools.Pool;
import androidx.collection.ArraySet;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2012R$integer;
import com.android.systemui.Dumpable;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import com.android.systemui.statusbar.notification.VisualStabilityManager.Callback;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.statusbar.policy.OnHeadsUpChangedListener;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class HeadsUpManagerPhone extends HeadsUpManager implements Dumpable, Callback, OnHeadsUpChangedListener {
    private AnimationStateHandler mAnimationStateHandler;
    /* access modifiers changed from: private */
    public final int mAutoHeadsUpNotificationDecay;
    private final KeyguardBypassController mBypassController;
    /* access modifiers changed from: private */
    public HashSet<NotificationEntry> mEntriesToRemoveAfterExpand = new HashSet<>();
    /* access modifiers changed from: private */
    public ArraySet<NotificationEntry> mEntriesToRemoveWhenReorderingAllowed = new ArraySet<>();
    private final Pool<HeadsUpEntryPhone> mEntryPool = new Pool<HeadsUpEntryPhone>() {
        private Stack<HeadsUpEntryPhone> mPoolObjects = new Stack<>();

        public HeadsUpEntryPhone acquire() {
            if (!this.mPoolObjects.isEmpty()) {
                return (HeadsUpEntryPhone) this.mPoolObjects.pop();
            }
            return new HeadsUpEntryPhone();
        }

        public boolean release(HeadsUpEntryPhone headsUpEntryPhone) {
            this.mPoolObjects.push(headsUpEntryPhone);
            return true;
        }
    };
    @VisibleForTesting
    final int mExtensionTime;
    private final NotificationGroupManager mGroupManager;
    private boolean mHeadsUpGoingAway;
    private int mHeadsUpInset;
    private final List<OnHeadsUpPhoneListenerChange> mHeadsUpPhoneListeners = new ArrayList();
    private boolean mIsExpanded;
    /* access modifiers changed from: private */
    public HashSet<String> mKeysToRemoveWhenLeavingKeyguard = new HashSet<>();
    private boolean mReleaseOnExpandFinish;
    /* access modifiers changed from: private */
    public int mStatusBarState;
    private final StateListener mStatusBarStateListener = new StateListener() {
        public void onStateChanged(int i) {
            boolean z = true;
            boolean z2 = HeadsUpManagerPhone.this.mStatusBarState == 1;
            if (i != 1) {
                z = false;
            }
            HeadsUpManagerPhone.this.mStatusBarState = i;
            if (z2 && !z && HeadsUpManagerPhone.this.mKeysToRemoveWhenLeavingKeyguard.size() != 0) {
                for (String access$1200 : (String[]) HeadsUpManagerPhone.this.mKeysToRemoveWhenLeavingKeyguard.toArray(new String[0])) {
                    HeadsUpManagerPhone.this.removeAlertEntry(access$1200);
                }
                HeadsUpManagerPhone.this.mKeysToRemoveWhenLeavingKeyguard.clear();
            }
        }

        public void onDozingChanged(boolean z) {
            if (!z) {
                for (AlertEntry updateEntry : HeadsUpManagerPhone.this.mAlertEntries.values()) {
                    updateEntry.updateEntry(true);
                }
            }
        }
    };
    private HashSet<String> mSwipedOutKeys = new HashSet<>();
    private final Region mTouchableRegion = new Region();
    /* access modifiers changed from: private */
    public boolean mTrackingHeadsUp;
    /* access modifiers changed from: private */
    public VisualStabilityManager mVisualStabilityManager;

    public interface AnimationStateHandler {
        void setHeadsUpGoingAwayAnimationsAllowed(boolean z);
    }

    protected class HeadsUpEntryPhone extends HeadsUpEntry {
        private boolean extended;
        private boolean mIsAutoHeadsUp;
        private boolean mMenuShownPinned;

        protected HeadsUpEntryPhone() {
            super();
        }

        /* access modifiers changed from: protected */
        public boolean isSticky() {
            return super.isSticky() || this.mMenuShownPinned;
        }

        public void setEntry(NotificationEntry notificationEntry) {
            setEntry(notificationEntry, new Runnable(notificationEntry) {
                public final /* synthetic */ NotificationEntry f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    HeadsUpEntryPhone.this.lambda$setEntry$0$HeadsUpManagerPhone$HeadsUpEntryPhone(this.f$1);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$setEntry$0 */
        public /* synthetic */ void lambda$setEntry$0$HeadsUpManagerPhone$HeadsUpEntryPhone(NotificationEntry notificationEntry) {
            if (!HeadsUpManagerPhone.this.mVisualStabilityManager.isReorderingAllowed() && !notificationEntry.showingPulsing()) {
                HeadsUpManagerPhone.this.mEntriesToRemoveWhenReorderingAllowed.add(notificationEntry);
                HeadsUpManagerPhone.this.mVisualStabilityManager.addReorderingAllowedCallback(HeadsUpManagerPhone.this);
            } else if (HeadsUpManagerPhone.this.mTrackingHeadsUp) {
                HeadsUpManagerPhone.this.mEntriesToRemoveAfterExpand.add(notificationEntry);
            } else if (!this.mIsAutoHeadsUp || HeadsUpManagerPhone.this.mStatusBarState != 1) {
                HeadsUpManagerPhone.this.removeAlertEntry(notificationEntry.getKey());
            } else {
                HeadsUpManagerPhone.this.mKeysToRemoveWhenLeavingKeyguard.add(notificationEntry.getKey());
            }
        }

        public void updateEntry(boolean z) {
            this.mIsAutoHeadsUp = this.mEntry.isAutoHeadsUp();
            super.updateEntry(z);
            if (HeadsUpManagerPhone.this.mEntriesToRemoveAfterExpand.contains(this.mEntry)) {
                HeadsUpManagerPhone.this.mEntriesToRemoveAfterExpand.remove(this.mEntry);
            }
            if (HeadsUpManagerPhone.this.mEntriesToRemoveWhenReorderingAllowed.contains(this.mEntry)) {
                HeadsUpManagerPhone.this.mEntriesToRemoveWhenReorderingAllowed.remove(this.mEntry);
            }
            HeadsUpManagerPhone.this.mKeysToRemoveWhenLeavingKeyguard.remove(this.mEntry.getKey());
        }

        public void setExpanded(boolean z) {
            if (this.expanded != z) {
                this.expanded = z;
                if (z) {
                    removeAutoRemovalCallbacks();
                } else {
                    updateEntry(false);
                }
            }
        }

        public void setMenuShownPinned(boolean z) {
            if (this.mMenuShownPinned != z) {
                this.mMenuShownPinned = z;
                if (z) {
                    removeAutoRemovalCallbacks();
                } else {
                    updateEntry(false);
                }
            }
        }

        public void reset() {
            super.reset();
            this.mMenuShownPinned = false;
            this.extended = false;
            this.mIsAutoHeadsUp = false;
        }

        /* access modifiers changed from: private */
        public void extendPulse() {
            if (!this.extended) {
                this.extended = true;
                updateEntry(false);
            }
        }

        public int compareTo(AlertEntry alertEntry) {
            HeadsUpEntryPhone headsUpEntryPhone = (HeadsUpEntryPhone) alertEntry;
            boolean isAutoHeadsUp = isAutoHeadsUp();
            boolean isAutoHeadsUp2 = headsUpEntryPhone.isAutoHeadsUp();
            if (isAutoHeadsUp && !isAutoHeadsUp2) {
                return 1;
            }
            if (isAutoHeadsUp || !isAutoHeadsUp2) {
                return super.compareTo(alertEntry);
            }
            return -1;
        }

        /* access modifiers changed from: protected */
        public long calculateFinishTime() {
            return this.mPostTime + ((long) getDecayDuration()) + ((long) (this.extended ? HeadsUpManagerPhone.this.mExtensionTime : 0));
        }

        private int getDecayDuration() {
            if (isAutoHeadsUp()) {
                return getRecommendedHeadsUpTimeoutMs(HeadsUpManagerPhone.this.mAutoHeadsUpNotificationDecay);
            }
            return getRecommendedHeadsUpTimeoutMs(HeadsUpManagerPhone.this.mAutoDismissNotificationDecay);
        }

        /* access modifiers changed from: private */
        public boolean isAutoHeadsUp() {
            return this.mIsAutoHeadsUp;
        }
    }

    public interface OnHeadsUpPhoneListenerChange {
        void onHeadsUpGoingAwayStateChanged(boolean z);
    }

    public HeadsUpManagerPhone(Context context, StatusBarStateController statusBarStateController, KeyguardBypassController keyguardBypassController, NotificationGroupManager notificationGroupManager, ConfigurationController configurationController) {
        super(context);
        Resources resources = this.mContext.getResources();
        this.mExtensionTime = resources.getInteger(C2012R$integer.ambient_notification_extension_time);
        this.mAutoHeadsUpNotificationDecay = resources.getInteger(C2012R$integer.auto_heads_up_notification_decay);
        statusBarStateController.addCallback(this.mStatusBarStateListener);
        this.mBypassController = keyguardBypassController;
        this.mGroupManager = notificationGroupManager;
        updateResources();
        configurationController.addCallback(new ConfigurationListener() {
            public void onDensityOrFontScaleChanged() {
                HeadsUpManagerPhone.this.updateResources();
            }

            public void onOverlayChanged() {
                HeadsUpManagerPhone.this.updateResources();
            }
        });
    }

    /* access modifiers changed from: 0000 */
    public void setup(VisualStabilityManager visualStabilityManager) {
        this.mVisualStabilityManager = visualStabilityManager;
    }

    public void setAnimationStateHandler(AnimationStateHandler animationStateHandler) {
        this.mAnimationStateHandler = animationStateHandler;
    }

    /* access modifiers changed from: private */
    public void updateResources() {
        Resources resources = this.mContext.getResources();
        this.mHeadsUpInset = resources.getDimensionPixelSize(17105462) + resources.getDimensionPixelSize(C2009R$dimen.heads_up_status_bar_padding);
    }

    /* access modifiers changed from: 0000 */
    public void addHeadsUpPhoneListener(OnHeadsUpPhoneListenerChange onHeadsUpPhoneListenerChange) {
        this.mHeadsUpPhoneListeners.add(onHeadsUpPhoneListenerChange);
    }

    /* access modifiers changed from: 0000 */
    public Region getTouchableRegion() {
        NotificationEntry topEntry = getTopEntry();
        if (!hasPinnedHeadsUp() || topEntry == null) {
            return null;
        }
        if (topEntry.isChildInGroup()) {
            NotificationEntry groupSummary = this.mGroupManager.getGroupSummary(topEntry.getSbn());
            if (groupSummary != null) {
                topEntry = groupSummary;
            }
        }
        ExpandableNotificationRow row = topEntry.getRow();
        int[] iArr = new int[2];
        row.getLocationOnScreen(iArr);
        this.mTouchableRegion.set(iArr[0], 0, iArr[0] + row.getWidth(), this.mHeadsUpInset + row.getIntrinsicHeight());
        return this.mTouchableRegion;
    }

    /* access modifiers changed from: 0000 */
    public boolean shouldSwallowClick(String str) {
        HeadsUpEntry headsUpEntry = getHeadsUpEntry(str);
        return headsUpEntry != null && this.mClock.currentTimeMillis() < headsUpEntry.mPostTime;
    }

    public void onExpandingFinished() {
        if (this.mReleaseOnExpandFinish) {
            releaseAllImmediately();
            this.mReleaseOnExpandFinish = false;
        } else {
            Iterator it = this.mEntriesToRemoveAfterExpand.iterator();
            while (it.hasNext()) {
                NotificationEntry notificationEntry = (NotificationEntry) it.next();
                if (isAlerting(notificationEntry.getKey())) {
                    removeAlertEntry(notificationEntry.getKey());
                }
            }
        }
        this.mEntriesToRemoveAfterExpand.clear();
    }

    public void setTrackingHeadsUp(boolean z) {
        this.mTrackingHeadsUp = z;
    }

    /* access modifiers changed from: 0000 */
    public void setIsPanelExpanded(boolean z) {
        if (z != this.mIsExpanded) {
            this.mIsExpanded = z;
            if (z) {
                this.mHeadsUpGoingAway = false;
            }
        }
    }

    public boolean isEntryAutoHeadsUpped(String str) {
        HeadsUpEntryPhone headsUpEntryPhone = getHeadsUpEntryPhone(str);
        if (headsUpEntryPhone == null) {
            return false;
        }
        return headsUpEntryPhone.isAutoHeadsUp();
    }

    /* access modifiers changed from: 0000 */
    public void setHeadsUpGoingAway(boolean z) {
        if (z != this.mHeadsUpGoingAway) {
            this.mHeadsUpGoingAway = z;
            for (OnHeadsUpPhoneListenerChange onHeadsUpGoingAwayStateChanged : this.mHeadsUpPhoneListeners) {
                onHeadsUpGoingAwayStateChanged.onHeadsUpGoingAwayStateChanged(z);
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public boolean isHeadsUpGoingAway() {
        return this.mHeadsUpGoingAway;
    }

    public void setRemoteInputActive(NotificationEntry notificationEntry, boolean z) {
        HeadsUpEntryPhone headsUpEntryPhone = getHeadsUpEntryPhone(notificationEntry.getKey());
        if (headsUpEntryPhone != null && headsUpEntryPhone.remoteInputActive != z) {
            headsUpEntryPhone.remoteInputActive = z;
            if (z) {
                headsUpEntryPhone.removeAutoRemovalCallbacks();
            } else {
                headsUpEntryPhone.updateEntry(false);
            }
        }
    }

    public void setMenuShown(NotificationEntry notificationEntry, boolean z) {
        HeadsUpEntry headsUpEntry = getHeadsUpEntry(notificationEntry.getKey());
        if ((headsUpEntry instanceof HeadsUpEntryPhone) && notificationEntry.isRowPinned()) {
            ((HeadsUpEntryPhone) headsUpEntry).setMenuShownPinned(z);
        }
    }

    public void extendHeadsUp() {
        HeadsUpEntryPhone topHeadsUpEntryPhone = getTopHeadsUpEntryPhone();
        if (topHeadsUpEntryPhone != null) {
            topHeadsUpEntryPhone.extendPulse();
        }
    }

    public boolean isTrackingHeadsUp() {
        return this.mTrackingHeadsUp;
    }

    public void snooze() {
        super.snooze();
        this.mReleaseOnExpandFinish = true;
    }

    public void addSwipedOutNotification(String str) {
        this.mSwipedOutKeys.add(str);
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("HeadsUpManagerPhone state:");
        dumpInternal(fileDescriptor, printWriter, strArr);
    }

    public boolean shouldExtendLifetime(NotificationEntry notificationEntry) {
        return this.mVisualStabilityManager.isReorderingAllowed() && super.shouldExtendLifetime(notificationEntry);
    }

    public void onReorderingAllowed() {
        this.mAnimationStateHandler.setHeadsUpGoingAwayAnimationsAllowed(false);
        Iterator it = this.mEntriesToRemoveWhenReorderingAllowed.iterator();
        while (it.hasNext()) {
            NotificationEntry notificationEntry = (NotificationEntry) it.next();
            if (isAlerting(notificationEntry.getKey())) {
                removeAlertEntry(notificationEntry.getKey());
            }
        }
        this.mEntriesToRemoveWhenReorderingAllowed.clear();
        this.mAnimationStateHandler.setHeadsUpGoingAwayAnimationsAllowed(true);
    }

    /* access modifiers changed from: protected */
    public HeadsUpEntry createAlertEntry() {
        return (HeadsUpEntry) this.mEntryPool.acquire();
    }

    /* access modifiers changed from: protected */
    public void onAlertEntryRemoved(AlertEntry alertEntry) {
        this.mKeysToRemoveWhenLeavingKeyguard.remove(alertEntry.mEntry.getKey());
        super.onAlertEntryRemoved(alertEntry);
        this.mEntryPool.release((HeadsUpEntryPhone) alertEntry);
    }

    /* access modifiers changed from: protected */
    public boolean shouldHeadsUpBecomePinned(NotificationEntry notificationEntry) {
        boolean z = this.mStatusBarState == 0 && !this.mIsExpanded;
        if (this.mBypassController.getBypassEnabled()) {
            z |= this.mStatusBarState == 1;
        }
        if (z || super.shouldHeadsUpBecomePinned(notificationEntry)) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void dumpInternal(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        super.dumpInternal(fileDescriptor, printWriter, strArr);
        printWriter.print("  mBarState=");
        printWriter.println(this.mStatusBarState);
        printWriter.print("  mTouchableRegion=");
        printWriter.println(this.mTouchableRegion);
    }

    private HeadsUpEntryPhone getHeadsUpEntryPhone(String str) {
        return (HeadsUpEntryPhone) this.mAlertEntries.get(str);
    }

    private HeadsUpEntryPhone getTopHeadsUpEntryPhone() {
        return (HeadsUpEntryPhone) getTopHeadsUpEntry();
    }

    /* access modifiers changed from: protected */
    public boolean canRemoveImmediately(String str) {
        boolean z = true;
        if (this.mSwipedOutKeys.contains(str)) {
            this.mSwipedOutKeys.remove(str);
            return true;
        }
        HeadsUpEntryPhone headsUpEntryPhone = getHeadsUpEntryPhone(str);
        HeadsUpEntryPhone topHeadsUpEntryPhone = getTopHeadsUpEntryPhone();
        if (headsUpEntryPhone != null && headsUpEntryPhone == topHeadsUpEntryPhone && !super.canRemoveImmediately(str)) {
            z = false;
        }
        return z;
    }
}
