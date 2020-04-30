package com.android.systemui.bubbles;

import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.service.notification.NotificationListenerService.Ranking;
import android.service.notification.NotificationListenerService.RankingMap;
import android.service.notification.ZenModeConfig;
import android.util.ArraySet;
import android.util.Log;
import android.util.Pair;
import android.util.SparseSetArray;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.statusbar.IStatusBarService.Stub;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.systemui.C2011R$id;
import com.android.systemui.Dumpable;
import com.android.systemui.bubbles.BubbleController.BubbleExpandListener;
import com.android.systemui.dump.DumpManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener;
import com.android.systemui.shared.system.ActivityManagerWrapper;
import com.android.systemui.shared.system.PinnedStackListenerForwarder.PinnedStackListener;
import com.android.systemui.shared.system.TaskStackChangeListener;
import com.android.systemui.shared.system.WindowManagerWrapper;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationLockscreenUserManager.UserChangedListener;
import com.android.systemui.statusbar.NotificationRemoveInterceptor;
import com.android.systemui.statusbar.notification.NotificationEntryListener;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProvider;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.phone.NotificationGroupManager.NotificationGroup;
import com.android.systemui.statusbar.phone.NotificationGroupManager.OnGroupChangeListener;
import com.android.systemui.statusbar.phone.NotificationShadeWindowController;
import com.android.systemui.statusbar.phone.ShadeController;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener;
import com.android.systemui.statusbar.policy.ZenModeController;
import com.android.systemui.statusbar.policy.ZenModeController.Callback;
import com.android.systemui.util.FloatingContentCoordinator;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class BubbleController implements ConfigurationListener, Dumpable {
    /* access modifiers changed from: private */
    public IStatusBarService mBarService;
    /* access modifiers changed from: private */
    public BubbleData mBubbleData;
    private final Listener mBubbleDataListener = new Listener() {
        public void applyUpdate(Update update) {
            if (BubbleController.this.mOverflowCallback != null) {
                BubbleController.this.mOverflowCallback.run();
            }
            if (update.expandedChanged && !update.expanded) {
                BubbleController.this.mStackView.setExpanded(false);
            }
            Iterator it = new ArrayList(update.removedBubbles).iterator();
            while (it.hasNext()) {
                Pair pair = (Pair) it.next();
                Bubble bubble = (Bubble) pair.first;
                int intValue = ((Integer) pair.second).intValue();
                BubbleController.this.mStackView.removeBubble(bubble);
                if (intValue != 8) {
                    if (BubbleController.this.mBubbleData.hasBubbleWithKey(bubble.getKey()) || bubble.showInShade()) {
                        Notification notification = bubble.getEntry().getSbn().getNotification();
                        notification.flags &= -4097;
                        try {
                            BubbleController.this.mBarService.onNotificationBubbleChanged(bubble.getKey(), false);
                        } catch (RemoteException unused) {
                        }
                    } else {
                        for (NotifCallback removeNotification : BubbleController.this.mCallbacks) {
                            removeNotification.removeNotification(bubble.getEntry(), 2);
                        }
                    }
                    if (BubbleController.this.mBubbleData.getBubblesInGroup(bubble.getEntry().getSbn().getGroupKey()).isEmpty()) {
                        for (NotifCallback maybeCancelSummary : BubbleController.this.mCallbacks) {
                            maybeCancelSummary.maybeCancelSummary(bubble.getEntry());
                        }
                    }
                }
            }
            if (update.addedBubble != null) {
                BubbleController.this.mStackView.addBubble(update.addedBubble);
            }
            if (update.updatedBubble != null) {
                BubbleController.this.mStackView.updateBubble(update.updatedBubble);
            }
            if (update.orderChanged) {
                BubbleController.this.mStackView.updateBubbleOrder(update.bubbles);
            }
            if (update.selectionChanged) {
                BubbleController.this.mStackView.setSelectedBubble(update.selectedBubble);
                if (update.selectedBubble != null) {
                    BubbleController.this.mNotificationGroupManager.updateSuppression(update.selectedBubble.getEntry());
                }
            }
            if (update.expandedChanged && update.expanded) {
                BubbleController.this.mStackView.setExpanded(true);
            }
            for (NotifCallback invalidateNotifications : BubbleController.this.mCallbacks) {
                invalidateNotifications.invalidateNotifications("BubbleData.Listener.applyUpdate");
            }
            BubbleController.this.updateStack();
        }
    };
    private BubbleIconFactory mBubbleIconFactory;
    /* access modifiers changed from: private */
    public final List<NotifCallback> mCallbacks = new ArrayList();
    /* access modifiers changed from: private */
    public final Context mContext;
    /* access modifiers changed from: private */
    public int mCurrentUserId;
    private BubbleExpandListener mExpandListener;
    private final FloatingContentCoordinator mFloatingContentCoordinator;
    private boolean mInflateSynchronously;
    private final NotifPipeline mNotifPipeline;
    private final NotificationLockscreenUserManager mNotifUserManager;
    /* access modifiers changed from: private */
    public final NotificationEntryManager mNotificationEntryManager;
    /* access modifiers changed from: private */
    public final NotificationGroupManager mNotificationGroupManager;
    private final NotificationInterruptStateProvider mNotificationInterruptStateProvider;
    private final NotificationShadeWindowController mNotificationShadeWindowController;
    private int mOrientation = 0;
    /* access modifiers changed from: private */
    public Runnable mOverflowCallback = null;
    private final SparseSetArray<String> mSavedBubbleKeysPerUser;
    private final ShadeController mShadeController;
    /* access modifiers changed from: private */
    public BubbleStackView mStackView;
    private BubbleStateChangeListener mStateChangeListener;
    private StatusBarStateListener mStatusBarStateListener;
    private SurfaceSynchronizer mSurfaceSynchronizer;
    private final BubbleTaskStackListener mTaskStackListener;
    private Rect mTempRect = new Rect();
    private Ranking mTmpRanking;
    private final HashSet<String> mUserBlockedBubbles;
    private final HashSet<String> mUserCreatedBubbles;
    private final ZenModeController mZenModeController;

    public interface BubbleExpandListener {
        void onBubbleExpandChanged(boolean z, String str);
    }

    public interface BubbleStateChangeListener {
        void onHasBubblesChanged(boolean z);
    }

    private class BubbleTaskStackListener extends TaskStackChangeListener {
        private BubbleTaskStackListener() {
        }

        public void onTaskMovedToFront(RunningTaskInfo runningTaskInfo) {
            if (BubbleController.this.mStackView != null && runningTaskInfo.displayId == 0 && !BubbleController.this.mStackView.isExpansionAnimating()) {
                BubbleController.this.mBubbleData.setExpanded(false);
            }
        }

        public void onActivityRestartAttempt(RunningTaskInfo runningTaskInfo, boolean z, boolean z2) {
            for (Bubble bubble : BubbleController.this.mBubbleData.getBubbles()) {
                if (bubble.getDisplayId() == runningTaskInfo.displayId) {
                    BubbleController.this.expandStackAndSelectBubble(bubble.getKey());
                    return;
                }
            }
        }

        public void onActivityLaunchOnSecondaryDisplayRerouted() {
            if (BubbleController.this.mStackView != null) {
                BubbleController.this.mBubbleData.setExpanded(false);
            }
        }

        public void onBackPressedOnTaskRoot(RunningTaskInfo runningTaskInfo) {
            if (BubbleController.this.mStackView != null) {
                int i = runningTaskInfo.displayId;
                BubbleController bubbleController = BubbleController.this;
                if (i == bubbleController.getExpandedDisplayId(bubbleController.mContext)) {
                    BubbleController.this.mBubbleData.setExpanded(false);
                }
            }
        }

        public void onSingleTaskDisplayDrawn(int i) {
            if (BubbleController.this.mStackView != null) {
                BubbleController.this.mStackView.showExpandedViewContents(i);
            }
        }

        public void onSingleTaskDisplayEmpty(int i) {
            BubbleViewProvider expandedBubble = BubbleController.this.mStackView != null ? BubbleController.this.mStackView.getExpandedBubble() : null;
            int displayId = expandedBubble != null ? expandedBubble.getDisplayId() : -1;
            if (BubbleController.this.mStackView != null && BubbleController.this.mStackView.isExpanded() && displayId == i) {
                BubbleController.this.mBubbleData.setExpanded(false);
            }
            BubbleController.this.mBubbleData.notifyDisplayEmpty(i);
        }
    }

    private class BubblesImeListener extends PinnedStackListener {
        private BubblesImeListener() {
        }

        public void onImeVisibilityChanged(boolean z, int i) {
            if (BubbleController.this.mStackView != null) {
                BubbleController.this.mStackView.post(new Runnable(z, i) {
                    public final /* synthetic */ boolean f$1;
                    public final /* synthetic */ int f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        BubblesImeListener.this.mo10351xb7519a2f(this.f$1, this.f$2);
                    }
                });
            }
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onImeVisibilityChanged$0 */
        public /* synthetic */ void mo10351xb7519a2f(boolean z, int i) {
            BubbleController.this.mStackView.onImeVisibilityChanged(z, i);
        }
    }

    public interface NotifCallback {
        void invalidateNotifications(String str);

        void maybeCancelSummary(NotificationEntry notificationEntry);

        void removeNotification(NotificationEntry notificationEntry, int i);
    }

    public interface NotificationSuppressionChangedListener {
        void onBubbleNotificationSuppressionChange(Bubble bubble);
    }

    private class StatusBarStateListener implements StateListener {
        private int mState;

        private StatusBarStateListener() {
        }

        public int getCurrentState() {
            return this.mState;
        }

        public void onStateChanged(int i) {
            this.mState = i;
            if (i != 0) {
                BubbleController.this.collapseStack();
            }
            BubbleController.this.updateStack();
        }
    }

    public BubbleController(Context context, NotificationShadeWindowController notificationShadeWindowController, StatusBarStateController statusBarStateController, ShadeController shadeController, BubbleData bubbleData, SurfaceSynchronizer surfaceSynchronizer, ConfigurationController configurationController, NotificationInterruptStateProvider notificationInterruptStateProvider, ZenModeController zenModeController, NotificationLockscreenUserManager notificationLockscreenUserManager, NotificationGroupManager notificationGroupManager, NotificationEntryManager notificationEntryManager, NotifPipeline notifPipeline, FeatureFlags featureFlags, DumpManager dumpManager, FloatingContentCoordinator floatingContentCoordinator) {
        Context context2 = context;
        BubbleData bubbleData2 = bubbleData;
        ZenModeController zenModeController2 = zenModeController;
        dumpManager.registerDumpable("Bubbles", this);
        this.mContext = context2;
        this.mShadeController = shadeController;
        this.mNotificationInterruptStateProvider = notificationInterruptStateProvider;
        this.mNotifUserManager = notificationLockscreenUserManager;
        this.mZenModeController = zenModeController2;
        this.mFloatingContentCoordinator = floatingContentCoordinator;
        zenModeController2.addCallback(new Callback() {
            public void onZenChanged(int i) {
                for (Bubble bubble : BubbleController.this.mBubbleData.getBubbles()) {
                    bubble.setShowDot(bubble.showInShade(), true);
                }
            }

            public void onConfigChanged(ZenModeConfig zenModeConfig) {
                for (Bubble bubble : BubbleController.this.mBubbleData.getBubbles()) {
                    bubble.setShowDot(bubble.showInShade(), true);
                }
            }
        });
        ConfigurationController configurationController2 = configurationController;
        configurationController.addCallback(this);
        this.mBubbleData = bubbleData2;
        bubbleData.setListener(this.mBubbleDataListener);
        this.mBubbleData.setSuppressionChangedListener(new NotificationSuppressionChangedListener() {
            public void onBubbleNotificationSuppressionChange(Bubble bubble) {
                try {
                    BubbleController.this.mBarService.onBubbleNotificationSuppressionChanged(bubble.getKey(), !bubble.showInShade());
                } catch (RemoteException unused) {
                }
            }
        });
        this.mNotificationEntryManager = notificationEntryManager;
        this.mNotificationGroupManager = notificationGroupManager;
        this.mNotifPipeline = notifPipeline;
        if (!featureFlags.isNewNotifPipelineRenderingEnabled()) {
            setupNEM();
        } else {
            setupNotifPipeline();
        }
        this.mNotificationShadeWindowController = notificationShadeWindowController;
        StatusBarStateListener statusBarStateListener = new StatusBarStateListener();
        this.mStatusBarStateListener = statusBarStateListener;
        StatusBarStateController statusBarStateController2 = statusBarStateController;
        statusBarStateController.addCallback(statusBarStateListener);
        this.mTaskStackListener = new BubbleTaskStackListener();
        ActivityManagerWrapper.getInstance().registerTaskStackListener(this.mTaskStackListener);
        try {
            WindowManagerWrapper.getInstance().addPinnedStackListener(new BubblesImeListener());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        this.mSurfaceSynchronizer = surfaceSynchronizer;
        this.mBarService = Stub.asInterface(ServiceManager.getService("statusbar"));
        this.mSavedBubbleKeysPerUser = new SparseSetArray<>();
        this.mCurrentUserId = this.mNotifUserManager.getCurrentUserId();
        this.mNotifUserManager.addUserChangedListener(new UserChangedListener() {
            public void onUserChanged(int i) {
                BubbleController bubbleController = BubbleController.this;
                bubbleController.saveBubbles(bubbleController.mCurrentUserId);
                BubbleController.this.mBubbleData.dismissAll(8);
                BubbleController.this.restoreBubbles(i);
                BubbleController.this.mCurrentUserId = i;
            }
        });
        this.mUserCreatedBubbles = new HashSet<>();
        this.mUserBlockedBubbles = new HashSet<>();
        this.mBubbleIconFactory = new BubbleIconFactory(context);
    }

    public void addNotifCallback(NotifCallback notifCallback) {
        this.mCallbacks.add(notifCallback);
    }

    private void setupNEM() {
        this.mNotificationEntryManager.addNotificationEntryListener(new NotificationEntryListener() {
            public void onPendingEntryAdded(NotificationEntry notificationEntry) {
                BubbleController.this.onEntryAdded(notificationEntry);
            }

            public void onPreEntryUpdated(NotificationEntry notificationEntry) {
                BubbleController.this.onEntryUpdated(notificationEntry);
            }

            public void onEntryRemoved(NotificationEntry notificationEntry, NotificationVisibility notificationVisibility, boolean z, int i) {
                BubbleController.this.onEntryRemoved(notificationEntry);
            }

            public void onNotificationRankingUpdated(RankingMap rankingMap) {
                BubbleController.this.onRankingUpdated(rankingMap);
            }
        });
        this.mNotificationEntryManager.addNotificationRemoveInterceptor(new NotificationRemoveInterceptor() {
            public boolean onNotificationRemoveRequested(String str, NotificationEntry notificationEntry, int i) {
                boolean z = true;
                boolean z2 = i == 3;
                boolean z3 = i == 2 || i == 1;
                boolean z4 = i == 8 || i == 9;
                boolean z5 = i == 12;
                if ((notificationEntry == null || !notificationEntry.isRowDismissed() || z4) && !z2 && !z3 && !z5) {
                    z = false;
                }
                if (z || BubbleController.this.isUserCreatedBubble(str) || BubbleController.this.isSummaryOfUserCreatedBubble(notificationEntry)) {
                    return BubbleController.this.handleDismissalInterception(notificationEntry);
                }
                return false;
            }
        });
        this.mNotificationGroupManager.addOnGroupChangeListener(new OnGroupChangeListener() {
            public void onGroupSuppressionChanged(NotificationGroup notificationGroup, boolean z) {
                NotificationEntry notificationEntry = notificationGroup.summary;
                String groupKey = notificationEntry != null ? notificationEntry.getSbn().getGroupKey() : null;
                if (!z && groupKey != null && BubbleController.this.mBubbleData.isSummarySuppressed(groupKey)) {
                    BubbleController.this.mBubbleData.removeSuppressedSummary(groupKey);
                }
            }
        });
        addNotifCallback(new NotifCallback() {
            public void removeNotification(NotificationEntry notificationEntry, int i) {
                BubbleController.this.mNotificationEntryManager.performRemoveNotification(notificationEntry.getSbn(), i);
            }

            public void invalidateNotifications(String str) {
                BubbleController.this.mNotificationEntryManager.updateNotifications(str);
            }

            public void maybeCancelSummary(NotificationEntry notificationEntry) {
                String groupKey = notificationEntry.getSbn().getGroupKey();
                if (BubbleController.this.mBubbleData.isSummarySuppressed(groupKey)) {
                    BubbleController.this.mBubbleData.removeSuppressedSummary(groupKey);
                    NotificationEntry activeNotificationUnfiltered = BubbleController.this.mNotificationEntryManager.getActiveNotificationUnfiltered(BubbleController.this.mBubbleData.getSummaryKey(groupKey));
                    if (activeNotificationUnfiltered != null) {
                        BubbleController.this.mNotificationEntryManager.performRemoveNotification(activeNotificationUnfiltered.getSbn(), 0);
                    }
                }
                NotificationEntry logicalGroupSummary = BubbleController.this.mNotificationGroupManager.getLogicalGroupSummary(notificationEntry.getSbn());
                if (logicalGroupSummary != null) {
                    ArrayList logicalChildren = BubbleController.this.mNotificationGroupManager.getLogicalChildren(logicalGroupSummary.getSbn());
                    if (logicalGroupSummary.getKey().equals(notificationEntry.getKey())) {
                        return;
                    }
                    if (logicalChildren == null || logicalChildren.isEmpty()) {
                        BubbleController.this.mNotificationEntryManager.performRemoveNotification(logicalGroupSummary.getSbn(), 0);
                    }
                }
            }
        });
    }

    private void setupNotifPipeline() {
        this.mNotifPipeline.addCollectionListener(new NotifCollectionListener() {
            public void onEntryAdded(NotificationEntry notificationEntry) {
                BubbleController.this.onEntryAdded(notificationEntry);
            }

            public void onEntryUpdated(NotificationEntry notificationEntry) {
                BubbleController.this.onEntryUpdated(notificationEntry);
            }

            public void onRankingUpdate(RankingMap rankingMap) {
                BubbleController.this.onRankingUpdated(rankingMap);
            }

            public void onEntryRemoved(NotificationEntry notificationEntry, int i) {
                BubbleController.this.onEntryRemoved(notificationEntry);
            }
        });
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void setInflateSynchronously(boolean z) {
        this.mInflateSynchronously = z;
    }

    /* access modifiers changed from: 0000 */
    public void setOverflowCallback(Runnable runnable) {
        this.mOverflowCallback = runnable;
    }

    /* access modifiers changed from: 0000 */
    public List<Bubble> getOverflowBubbles() {
        return this.mBubbleData.getOverflowBubbles();
    }

    private void ensureStackViewCreated() {
        if (this.mStackView == null) {
            this.mStackView = new BubbleStackView(this.mContext, this.mBubbleData, this.mSurfaceSynchronizer, this.mFloatingContentCoordinator);
            ViewGroup notificationShadeView = this.mNotificationShadeWindowController.getNotificationShadeView();
            notificationShadeView.addView(this.mStackView, notificationShadeView.indexOfChild(notificationShadeView.findViewById(C2011R$id.scrim_for_bubble)) + 1, new LayoutParams(-1, -1));
            BubbleExpandListener bubbleExpandListener = this.mExpandListener;
            if (bubbleExpandListener != null) {
                this.mStackView.setExpandListener(bubbleExpandListener);
            }
        }
    }

    /* access modifiers changed from: private */
    public void saveBubbles(int i) {
        this.mSavedBubbleKeysPerUser.remove(i);
        for (Bubble key : this.mBubbleData.getBubbles()) {
            this.mSavedBubbleKeysPerUser.add(i, key.getKey());
        }
    }

    /* access modifiers changed from: private */
    public void restoreBubbles(int i) {
        ArraySet arraySet = this.mSavedBubbleKeysPerUser.get(i);
        if (arraySet != null) {
            for (NotificationEntry notificationEntry : this.mNotificationEntryManager.getActiveNotificationsForCurrentUser()) {
                if (arraySet.contains(notificationEntry.getKey()) && this.mNotificationInterruptStateProvider.shouldBubbleUp(notificationEntry) && canLaunchInActivityView(this.mContext, notificationEntry)) {
                    updateBubble(notificationEntry, true);
                }
            }
            this.mSavedBubbleKeysPerUser.remove(this.mCurrentUserId);
        }
    }

    public void onUiModeChanged() {
        updateForThemeChanges();
    }

    public void onOverlayChanged() {
        updateForThemeChanges();
    }

    private void updateForThemeChanges() {
        BubbleStackView bubbleStackView = this.mStackView;
        if (bubbleStackView != null) {
            bubbleStackView.onThemeChanged();
        }
        this.mBubbleIconFactory = new BubbleIconFactory(this.mContext);
        for (Bubble inflate : this.mBubbleData.getBubbles()) {
            inflate.inflate(null, this.mContext, this.mStackView, this.mBubbleIconFactory);
        }
    }

    public void onConfigChanged(Configuration configuration) {
        BubbleStackView bubbleStackView = this.mStackView;
        if (bubbleStackView != null && configuration != null) {
            int i = configuration.orientation;
            if (i != this.mOrientation) {
                this.mOrientation = i;
                bubbleStackView.onOrientationChanged(i);
            }
        }
    }

    public void setBubbleStateChangeListener(BubbleStateChangeListener bubbleStateChangeListener) {
        this.mStateChangeListener = bubbleStateChangeListener;
    }

    public void setExpandListener(BubbleExpandListener bubbleExpandListener) {
        $$Lambda$BubbleController$B9Rf8Lqgsvsjhuncdnt9rJlYfA r0 = new BubbleExpandListener(bubbleExpandListener) {
            public final /* synthetic */ BubbleExpandListener f$1;

            {
                this.f$1 = r2;
            }

            public final void onBubbleExpandChanged(boolean z, String str) {
                BubbleController.this.lambda$setExpandListener$0$BubbleController(this.f$1, z, str);
            }
        };
        this.mExpandListener = r0;
        BubbleStackView bubbleStackView = this.mStackView;
        if (bubbleStackView != null) {
            bubbleStackView.setExpandListener(r0);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setExpandListener$0 */
    public /* synthetic */ void lambda$setExpandListener$0$BubbleController(BubbleExpandListener bubbleExpandListener, boolean z, String str) {
        if (bubbleExpandListener != null) {
            bubbleExpandListener.onBubbleExpandChanged(z, str);
        }
        this.mNotificationShadeWindowController.setBubbleExpanded(z);
    }

    public boolean hasBubbles() {
        if (this.mStackView == null) {
            return false;
        }
        return this.mBubbleData.hasBubbles();
    }

    public boolean isStackExpanded() {
        return this.mBubbleData.isExpanded();
    }

    public void collapseStack() {
        this.mBubbleData.setExpanded(false);
    }

    public boolean isBubbleNotificationSuppressedFromShade(NotificationEntry notificationEntry) {
        String key = notificationEntry.getKey();
        boolean z = this.mBubbleData.hasBubbleWithKey(key) && !this.mBubbleData.getBubbleWithKey(key).showInShade();
        String groupKey = notificationEntry.getSbn().getGroupKey();
        boolean isSummarySuppressed = this.mBubbleData.isSummarySuppressed(groupKey);
        if ((!key.equals(this.mBubbleData.getSummaryKey(groupKey)) || !isSummarySuppressed) && !z) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void selectBubble(String str) {
        this.mBubbleData.setSelectedBubble(this.mBubbleData.getBubbleWithKey(str));
    }

    /* access modifiers changed from: 0000 */
    public void promoteBubbleFromOverflow(Bubble bubble) {
        bubble.setInflateSynchronously(this.mInflateSynchronously);
        this.mBubbleData.promoteBubbleFromOverflow(bubble, this.mStackView, this.mBubbleIconFactory);
    }

    public void expandStackAndSelectBubble(String str) {
        Bubble bubbleWithKey = this.mBubbleData.getBubbleWithKey(str);
        if (bubbleWithKey != null) {
            this.mBubbleData.setSelectedBubble(bubbleWithKey);
            this.mBubbleData.setExpanded(true);
        }
    }

    /* access modifiers changed from: 0000 */
    public void dismissStack(int i) {
        this.mBubbleData.dismissAll(i);
    }

    public void performBackPressIfNeeded() {
        BubbleStackView bubbleStackView = this.mStackView;
        if (bubbleStackView != null) {
            bubbleStackView.performBackPressIfNeeded();
        }
    }

    /* access modifiers changed from: 0000 */
    public void updateBubble(NotificationEntry notificationEntry) {
        updateBubble(notificationEntry, false);
    }

    /* access modifiers changed from: 0000 */
    public void updateBubble(NotificationEntry notificationEntry, boolean z) {
        updateBubble(notificationEntry, z, true);
    }

    /* access modifiers changed from: 0000 */
    public void updateBubble(NotificationEntry notificationEntry, boolean z, boolean z2) {
        if (this.mStackView == null) {
            ensureStackViewCreated();
        }
        if (notificationEntry.getImportance() >= 4) {
            notificationEntry.setInterruption();
        }
        Bubble orCreateBubble = this.mBubbleData.getOrCreateBubble(notificationEntry);
        orCreateBubble.setInflateSynchronously(this.mInflateSynchronously);
        orCreateBubble.inflate(new BubbleViewInfoTask.Callback(z, z2) {
            public final /* synthetic */ boolean f$1;
            public final /* synthetic */ boolean f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void onBubbleViewsReady(Bubble bubble) {
                BubbleController.this.lambda$updateBubble$1$BubbleController(this.f$1, this.f$2, bubble);
            }
        }, this.mContext, this.mStackView, this.mBubbleIconFactory);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$updateBubble$1 */
    public /* synthetic */ void lambda$updateBubble$1$BubbleController(boolean z, boolean z2, Bubble bubble) {
        this.mBubbleData.notificationEntryUpdated(bubble, z, z2);
    }

    public void onUserCreatedBubbleFromNotification(NotificationEntry notificationEntry) {
        StringBuilder sb = new StringBuilder();
        sb.append("onUserCreatedBubble: ");
        sb.append(notificationEntry.getKey());
        Log.d("Bubbles", sb.toString());
        this.mShadeController.collapsePanel(true);
        notificationEntry.setFlagBubble(true);
        updateBubble(notificationEntry, true, false);
        this.mUserCreatedBubbles.add(notificationEntry.getKey());
        this.mUserBlockedBubbles.remove(notificationEntry.getKey());
    }

    public void onUserDemotedBubbleFromNotification(NotificationEntry notificationEntry) {
        StringBuilder sb = new StringBuilder();
        sb.append("onUserDemotedBubble: ");
        sb.append(notificationEntry.getKey());
        Log.d("Bubbles", sb.toString());
        notificationEntry.setFlagBubble(false);
        removeBubble(notificationEntry, 4);
        this.mUserCreatedBubbles.remove(notificationEntry.getKey());
        if (BubbleExperimentConfig.isPackageWhitelistedToAutoBubble(this.mContext, notificationEntry.getSbn().getPackageName())) {
            this.mUserBlockedBubbles.add(notificationEntry.getKey());
        }
    }

    /* access modifiers changed from: 0000 */
    public boolean isUserCreatedBubble(String str) {
        return this.mUserCreatedBubbles.contains(str);
    }

    /* access modifiers changed from: 0000 */
    public boolean isSummaryOfUserCreatedBubble(NotificationEntry notificationEntry) {
        if (isSummaryOfBubbles(notificationEntry)) {
            ArrayList bubblesInGroup = this.mBubbleData.getBubblesInGroup(notificationEntry.getSbn().getGroupKey());
            for (int i = 0; i < bubblesInGroup.size(); i++) {
                if (isUserCreatedBubble(((Bubble) bubblesInGroup.get(i)).getKey())) {
                    return true;
                }
            }
        }
        return false;
    }

    /* access modifiers changed from: 0000 */
    public void removeBubble(NotificationEntry notificationEntry, int i) {
        if (this.mBubbleData.hasBubbleWithKey(notificationEntry.getKey())) {
            this.mBubbleData.notificationEntryRemoved(notificationEntry, i);
        }
    }

    /* access modifiers changed from: private */
    public void onEntryAdded(NotificationEntry notificationEntry) {
        boolean contains = this.mUserCreatedBubbles.contains(notificationEntry.getKey());
        boolean adjustForExperiments = BubbleExperimentConfig.adjustForExperiments(this.mContext, notificationEntry, contains, this.mUserBlockedBubbles.contains(notificationEntry.getKey()));
        if (!this.mNotificationInterruptStateProvider.shouldBubbleUp(notificationEntry)) {
            return;
        }
        if (canLaunchInActivityView(this.mContext, notificationEntry) || adjustForExperiments) {
            if (adjustForExperiments && !contains) {
                this.mUserCreatedBubbles.add(notificationEntry.getKey());
            }
            updateBubble(notificationEntry);
        }
    }

    /* access modifiers changed from: private */
    public void onEntryUpdated(NotificationEntry notificationEntry) {
        boolean contains = this.mUserCreatedBubbles.contains(notificationEntry.getKey());
        boolean adjustForExperiments = BubbleExperimentConfig.adjustForExperiments(this.mContext, notificationEntry, contains, this.mUserBlockedBubbles.contains(notificationEntry.getKey()));
        boolean z = this.mNotificationInterruptStateProvider.shouldBubbleUp(notificationEntry) && (canLaunchInActivityView(this.mContext, notificationEntry) || adjustForExperiments);
        if (!z && this.mBubbleData.hasBubbleWithKey(notificationEntry.getKey())) {
            removeBubble(notificationEntry, 7);
        } else if (z) {
            if (adjustForExperiments && !contains) {
                this.mUserCreatedBubbles.add(notificationEntry.getKey());
            }
            updateBubble(notificationEntry);
        }
    }

    /* access modifiers changed from: private */
    public void onEntryRemoved(NotificationEntry notificationEntry) {
        if (isSummaryOfBubbles(notificationEntry)) {
            String groupKey = notificationEntry.getSbn().getGroupKey();
            this.mBubbleData.removeSuppressedSummary(groupKey);
            ArrayList bubblesInGroup = this.mBubbleData.getBubblesInGroup(groupKey);
            for (int i = 0; i < bubblesInGroup.size(); i++) {
                removeBubble(((Bubble) bubblesInGroup.get(i)).getEntry(), 9);
            }
            return;
        }
        removeBubble(notificationEntry, 5);
    }

    /* access modifiers changed from: private */
    public void onRankingUpdated(RankingMap rankingMap) {
        if (this.mTmpRanking == null) {
            this.mTmpRanking = new Ranking();
        }
        String[] orderedKeys = rankingMap.getOrderedKeys();
        for (String str : orderedKeys) {
            NotificationEntry pendingOrActiveNotif = this.mNotificationEntryManager.getPendingOrActiveNotif(str);
            rankingMap.getRanking(str, this.mTmpRanking);
            if (this.mBubbleData.hasBubbleWithKey(str) && !this.mTmpRanking.canBubble()) {
                this.mBubbleData.notificationEntryRemoved(pendingOrActiveNotif, 4);
            } else if (pendingOrActiveNotif != null && this.mTmpRanking.isBubble()) {
                pendingOrActiveNotif.setFlagBubble(true);
                onEntryUpdated(pendingOrActiveNotif);
            }
        }
    }

    public boolean handleDismissalInterception(NotificationEntry notificationEntry) {
        if (notificationEntry == null) {
            return false;
        }
        boolean z = this.mBubbleData.hasBubbleWithKey(notificationEntry.getKey()) && notificationEntry.isBubble();
        if (isSummaryOfBubbles(notificationEntry)) {
            handleSummaryDismissalInterception(notificationEntry);
        } else if (!z) {
            return false;
        } else {
            Bubble bubbleWithKey = this.mBubbleData.getBubbleWithKey(notificationEntry.getKey());
            bubbleWithKey.setSuppressNotification(true);
            bubbleWithKey.setShowDot(false, true);
        }
        for (NotifCallback invalidateNotifications : this.mCallbacks) {
            invalidateNotifications.invalidateNotifications("BubbleController.handleDismissalInterception");
        }
        return true;
    }

    private boolean isSummaryOfBubbles(NotificationEntry notificationEntry) {
        boolean z = false;
        if (notificationEntry == null) {
            return false;
        }
        String groupKey = notificationEntry.getSbn().getGroupKey();
        ArrayList bubblesInGroup = this.mBubbleData.getBubblesInGroup(groupKey);
        boolean z2 = this.mBubbleData.isSummarySuppressed(groupKey) && this.mBubbleData.getSummaryKey(groupKey).equals(notificationEntry.getKey());
        boolean isGroupSummary = notificationEntry.getSbn().getNotification().isGroupSummary();
        if ((z2 || isGroupSummary) && bubblesInGroup != null && !bubblesInGroup.isEmpty()) {
            z = true;
        }
        return z;
    }

    private void handleSummaryDismissalInterception(NotificationEntry notificationEntry) {
        List children = notificationEntry.getChildren();
        if (children != null) {
            for (int i = 0; i < children.size(); i++) {
                NotificationEntry notificationEntry2 = (NotificationEntry) children.get(i);
                if (this.mBubbleData.hasBubbleWithKey(notificationEntry2.getKey())) {
                    Bubble bubbleWithKey = this.mBubbleData.getBubbleWithKey(notificationEntry2.getKey());
                    this.mNotificationGroupManager.onEntryRemoved(bubbleWithKey.getEntry());
                    bubbleWithKey.setSuppressNotification(true);
                    bubbleWithKey.setShowDot(false, true);
                } else {
                    for (NotifCallback removeNotification : this.mCallbacks) {
                        removeNotification.removeNotification(notificationEntry2, 12);
                    }
                }
            }
        }
        this.mNotificationGroupManager.onEntryRemoved(notificationEntry);
        this.mBubbleData.addSummaryToSuppress(notificationEntry.getSbn().getGroupKey(), notificationEntry.getKey());
    }

    public void updateStack() {
        if (this.mStackView != null) {
            boolean z = false;
            int i = 4;
            if (this.mStatusBarStateListener.getCurrentState() != 0 || !hasBubbles()) {
                BubbleStackView bubbleStackView = this.mStackView;
                if (bubbleStackView != null) {
                    bubbleStackView.setVisibility(4);
                }
            } else {
                BubbleStackView bubbleStackView2 = this.mStackView;
                if (hasBubbles()) {
                    i = 0;
                }
                bubbleStackView2.setVisibility(i);
            }
            boolean bubblesShowing = this.mNotificationShadeWindowController.getBubblesShowing();
            if (hasBubbles() && this.mStackView.getVisibility() == 0) {
                z = true;
            }
            this.mNotificationShadeWindowController.setBubblesShowing(z);
            BubbleStateChangeListener bubbleStateChangeListener = this.mStateChangeListener;
            if (!(bubbleStateChangeListener == null || bubblesShowing == z)) {
                bubbleStateChangeListener.onHasBubblesChanged(z);
            }
            this.mStackView.updateContentDescription();
        }
    }

    public Rect getTouchableRegion() {
        BubbleStackView bubbleStackView = this.mStackView;
        if (bubbleStackView == null || bubbleStackView.getVisibility() != 0) {
            return null;
        }
        this.mStackView.getBoundsOnScreen(this.mTempRect);
        return this.mTempRect;
    }

    public int getExpandedDisplayId(Context context) {
        if (this.mStackView == null) {
            return -1;
        }
        boolean z = context.getDisplay() != null && context.getDisplay().getDisplayId() == 0;
        BubbleViewProvider expandedBubble = this.mStackView.getExpandedBubble();
        if (!z || expandedBubble == null || !isStackExpanded() || this.mNotificationShadeWindowController.getPanelExpanded()) {
            return -1;
        }
        return expandedBubble.getDisplayId();
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public BubbleStackView getStackView() {
        return this.mStackView;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("BubbleController state:");
        this.mBubbleData.dump(fileDescriptor, printWriter, strArr);
        printWriter.println();
        BubbleStackView bubbleStackView = this.mStackView;
        if (bubbleStackView != null) {
            bubbleStackView.dump(fileDescriptor, printWriter, strArr);
        }
        printWriter.println();
    }

    static boolean canLaunchInActivityView(Context context, NotificationEntry notificationEntry) {
        PendingIntent bubbleIntent = notificationEntry.getBubbleMetadata() != null ? notificationEntry.getBubbleMetadata().getBubbleIntent() : null;
        if (notificationEntry.getBubbleMetadata() != null && notificationEntry.getBubbleMetadata().getShortcutId() != null) {
            return true;
        }
        String str = "Bubbles";
        if (bubbleIntent == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Unable to create bubble -- no intent: ");
            sb.append(notificationEntry.getKey());
            Log.w(str, sb.toString());
            return false;
        }
        ActivityInfo resolveActivityInfo = bubbleIntent.getIntent().resolveActivityInfo(StatusBar.getPackageManagerForUser(context, notificationEntry.getSbn().getUser().getIdentifier()), 0);
        String str2 = "Unable to send as bubble, ";
        if (resolveActivityInfo == null) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(str2);
            sb2.append(notificationEntry.getKey());
            sb2.append(" couldn't find activity info for intent: ");
            sb2.append(bubbleIntent);
            Log.w(str, sb2.toString());
            return false;
        } else if (ActivityInfo.isResizeableMode(resolveActivityInfo.resizeMode)) {
            return true;
        } else {
            StringBuilder sb3 = new StringBuilder();
            sb3.append(str2);
            sb3.append(notificationEntry.getKey());
            sb3.append(" activity is not resizable for intent: ");
            sb3.append(bubbleIntent);
            Log.w(str, sb3.toString());
            return false;
        }
    }
}
