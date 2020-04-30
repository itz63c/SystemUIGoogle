package com.android.systemui.statusbar.phone;

import android.app.ActivityTaskManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.os.UserHandle;
import android.service.dreams.IDreamManager;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.EventLog;
import android.util.Log;
import android.view.RemoteAnimationAdapter;
import android.view.View;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.internal.widget.LockPatternUtils;
import com.android.systemui.ActivityIntentHelper;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.bubbles.BubbleController;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.ActivityStarter.OnDismissAction;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.FeatureFlags;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationPresenter;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.RemoteInputController;
import com.android.systemui.statusbar.notification.ActivityLaunchAnimator;
import com.android.systemui.statusbar.notification.NotificationActivityStarter;
import com.android.systemui.statusbar.notification.NotificationEntryListener;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.collection.NotifCollection;
import com.android.systemui.statusbar.notification.collection.NotifPipeline;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.notifcollection.DismissedByUserStats;
import com.android.systemui.statusbar.notification.collection.notifcollection.NotifCollectionListener;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProvider;
import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import dagger.Lazy;
import java.util.Objects;
import java.util.concurrent.Executor;

public class StatusBarNotificationActivityStarter implements NotificationActivityStarter {
    protected static final boolean DEBUG = Log.isLoggable("NotifActivityStarter", 3);
    private final ActivityIntentHelper mActivityIntentHelper;
    private final ActivityLaunchAnimator mActivityLaunchAnimator;
    private final ActivityStarter mActivityStarter;
    private final Lazy<AssistManager> mAssistManagerLazy;
    private final Handler mBackgroundHandler;
    private final IStatusBarService mBarService;
    private final BubbleController mBubbleController;
    private final CommandQueue mCommandQueue;
    private final Context mContext;
    private final IDreamManager mDreamManager;
    /* access modifiers changed from: private */
    public final NotificationEntryManager mEntryManager;
    private final FeatureFlags mFeatureFlags;
    private final NotificationGroupManager mGroupManager;
    /* access modifiers changed from: private */
    public final HeadsUpManagerPhone mHeadsUpManager;
    private boolean mIsCollapsingToShowActivityOverLockscreen;
    private final KeyguardManager mKeyguardManager;
    private final KeyguardStateController mKeyguardStateController;
    private final LockPatternUtils mLockPatternUtils;
    private final NotificationLockscreenUserManager mLockscreenUserManager;
    private final Handler mMainThreadHandler;
    private final MetricsLogger mMetricsLogger;
    /* access modifiers changed from: private */
    public final NotifCollection mNotifCollection;
    /* access modifiers changed from: private */
    public final NotifPipeline mNotifPipeline;
    private final NotificationInterruptStateProvider mNotificationInterruptStateProvider;
    /* access modifiers changed from: private */
    public final NotificationPanelViewController mNotificationPanel;
    private final NotificationPresenter mPresenter;
    private final NotificationRemoteInputManager mRemoteInputManager;
    private final ShadeController mShadeController;
    private final StatusBar mStatusBar;
    private final StatusBarKeyguardViewManager mStatusBarKeyguardViewManager;
    private final StatusBarRemoteInputCallback mStatusBarRemoteInputCallback;
    private final StatusBarStateController mStatusBarStateController;
    private final Executor mUiBgExecutor;

    public static class Builder {
        private final ActivityIntentHelper mActivityIntentHelper;
        private ActivityLaunchAnimator mActivityLaunchAnimator;
        private final ActivityStarter mActivityStarter;
        private final Lazy<AssistManager> mAssistManagerLazy;
        private final Handler mBackgroundHandler;
        private final BubbleController mBubbleController;
        private final CommandQueue mCommandQueue;
        private final Context mContext;
        private final IDreamManager mDreamManager;
        private final NotificationEntryManager mEntryManager;
        private final FeatureFlags mFeatureFlags;
        private final NotificationGroupManager mGroupManager;
        private final HeadsUpManagerPhone mHeadsUpManager;
        private final KeyguardManager mKeyguardManager;
        private final KeyguardStateController mKeyguardStateController;
        private final LockPatternUtils mLockPatternUtils;
        private final NotificationLockscreenUserManager mLockscreenUserManager;
        private final Handler mMainThreadHandler;
        private final MetricsLogger mMetricsLogger;
        private final NotifCollection mNotifCollection;
        private final NotifPipeline mNotifPipeline;
        private NotificationInterruptStateProvider mNotificationInterruptStateProvider;
        private NotificationPanelViewController mNotificationPanelViewController;
        private NotificationPresenter mNotificationPresenter;
        private final StatusBarRemoteInputCallback mRemoteInputCallback;
        private final NotificationRemoteInputManager mRemoteInputManager;
        private final ShadeController mShadeController;
        private StatusBar mStatusBar;
        private final StatusBarKeyguardViewManager mStatusBarKeyguardViewManager;
        private final IStatusBarService mStatusBarService;
        private final StatusBarStateController mStatusBarStateController;
        private final Executor mUiBgExecutor;

        public Builder(Context context, CommandQueue commandQueue, Lazy<AssistManager> lazy, NotificationEntryManager notificationEntryManager, HeadsUpManagerPhone headsUpManagerPhone, ActivityStarter activityStarter, IStatusBarService iStatusBarService, StatusBarStateController statusBarStateController, StatusBarKeyguardViewManager statusBarKeyguardViewManager, KeyguardManager keyguardManager, IDreamManager iDreamManager, NotificationRemoteInputManager notificationRemoteInputManager, StatusBarRemoteInputCallback statusBarRemoteInputCallback, NotificationGroupManager notificationGroupManager, NotificationLockscreenUserManager notificationLockscreenUserManager, KeyguardStateController keyguardStateController, NotificationInterruptStateProvider notificationInterruptStateProvider, MetricsLogger metricsLogger, LockPatternUtils lockPatternUtils, Handler handler, Handler handler2, Executor executor, ActivityIntentHelper activityIntentHelper, BubbleController bubbleController, ShadeController shadeController, FeatureFlags featureFlags, NotifPipeline notifPipeline, NotifCollection notifCollection) {
            this.mContext = context;
            this.mCommandQueue = commandQueue;
            this.mAssistManagerLazy = lazy;
            this.mEntryManager = notificationEntryManager;
            this.mHeadsUpManager = headsUpManagerPhone;
            this.mActivityStarter = activityStarter;
            this.mStatusBarService = iStatusBarService;
            this.mStatusBarStateController = statusBarStateController;
            this.mStatusBarKeyguardViewManager = statusBarKeyguardViewManager;
            this.mKeyguardManager = keyguardManager;
            this.mDreamManager = iDreamManager;
            this.mRemoteInputManager = notificationRemoteInputManager;
            this.mRemoteInputCallback = statusBarRemoteInputCallback;
            this.mGroupManager = notificationGroupManager;
            this.mLockscreenUserManager = notificationLockscreenUserManager;
            this.mKeyguardStateController = keyguardStateController;
            this.mNotificationInterruptStateProvider = notificationInterruptStateProvider;
            this.mMetricsLogger = metricsLogger;
            this.mLockPatternUtils = lockPatternUtils;
            this.mMainThreadHandler = handler;
            this.mBackgroundHandler = handler2;
            this.mUiBgExecutor = executor;
            this.mActivityIntentHelper = activityIntentHelper;
            this.mBubbleController = bubbleController;
            this.mShadeController = shadeController;
            this.mFeatureFlags = featureFlags;
            this.mNotifPipeline = notifPipeline;
            this.mNotifCollection = notifCollection;
        }

        public Builder setStatusBar(StatusBar statusBar) {
            this.mStatusBar = statusBar;
            return this;
        }

        public Builder setNotificationPresenter(NotificationPresenter notificationPresenter) {
            this.mNotificationPresenter = notificationPresenter;
            return this;
        }

        public Builder setActivityLaunchAnimator(ActivityLaunchAnimator activityLaunchAnimator) {
            this.mActivityLaunchAnimator = activityLaunchAnimator;
            return this;
        }

        public Builder setNotificationPanelViewController(NotificationPanelViewController notificationPanelViewController) {
            this.mNotificationPanelViewController = notificationPanelViewController;
            return this;
        }

        public StatusBarNotificationActivityStarter build() {
            StatusBarNotificationActivityStarter statusBarNotificationActivityStarter = new StatusBarNotificationActivityStarter(this.mContext, this.mCommandQueue, this.mAssistManagerLazy, this.mNotificationPanelViewController, this.mNotificationPresenter, this.mEntryManager, this.mHeadsUpManager, this.mActivityStarter, this.mActivityLaunchAnimator, this.mStatusBarService, this.mStatusBarStateController, this.mStatusBarKeyguardViewManager, this.mKeyguardManager, this.mDreamManager, this.mRemoteInputManager, this.mRemoteInputCallback, this.mGroupManager, this.mLockscreenUserManager, this.mShadeController, this.mStatusBar, this.mKeyguardStateController, this.mNotificationInterruptStateProvider, this.mMetricsLogger, this.mLockPatternUtils, this.mMainThreadHandler, this.mBackgroundHandler, this.mUiBgExecutor, this.mActivityIntentHelper, this.mBubbleController, this.mFeatureFlags, this.mNotifPipeline, this.mNotifCollection);
            return statusBarNotificationActivityStarter;
        }
    }

    private StatusBarNotificationActivityStarter(Context context, CommandQueue commandQueue, Lazy<AssistManager> lazy, NotificationPanelViewController notificationPanelViewController, NotificationPresenter notificationPresenter, NotificationEntryManager notificationEntryManager, HeadsUpManagerPhone headsUpManagerPhone, ActivityStarter activityStarter, ActivityLaunchAnimator activityLaunchAnimator, IStatusBarService iStatusBarService, StatusBarStateController statusBarStateController, StatusBarKeyguardViewManager statusBarKeyguardViewManager, KeyguardManager keyguardManager, IDreamManager iDreamManager, NotificationRemoteInputManager notificationRemoteInputManager, StatusBarRemoteInputCallback statusBarRemoteInputCallback, NotificationGroupManager notificationGroupManager, NotificationLockscreenUserManager notificationLockscreenUserManager, ShadeController shadeController, StatusBar statusBar, KeyguardStateController keyguardStateController, NotificationInterruptStateProvider notificationInterruptStateProvider, MetricsLogger metricsLogger, LockPatternUtils lockPatternUtils, Handler handler, Handler handler2, Executor executor, ActivityIntentHelper activityIntentHelper, BubbleController bubbleController, FeatureFlags featureFlags, NotifPipeline notifPipeline, NotifCollection notifCollection) {
        this.mContext = context;
        this.mNotificationPanel = notificationPanelViewController;
        this.mPresenter = notificationPresenter;
        this.mHeadsUpManager = headsUpManagerPhone;
        this.mActivityLaunchAnimator = activityLaunchAnimator;
        this.mBarService = iStatusBarService;
        this.mCommandQueue = commandQueue;
        this.mStatusBarKeyguardViewManager = statusBarKeyguardViewManager;
        this.mKeyguardManager = keyguardManager;
        this.mDreamManager = iDreamManager;
        this.mRemoteInputManager = notificationRemoteInputManager;
        this.mLockscreenUserManager = notificationLockscreenUserManager;
        this.mShadeController = shadeController;
        this.mStatusBar = statusBar;
        this.mKeyguardStateController = keyguardStateController;
        this.mActivityStarter = activityStarter;
        this.mEntryManager = notificationEntryManager;
        this.mStatusBarStateController = statusBarStateController;
        this.mNotificationInterruptStateProvider = notificationInterruptStateProvider;
        this.mMetricsLogger = metricsLogger;
        this.mAssistManagerLazy = lazy;
        this.mGroupManager = notificationGroupManager;
        this.mLockPatternUtils = lockPatternUtils;
        this.mBackgroundHandler = handler2;
        this.mUiBgExecutor = executor;
        this.mFeatureFlags = featureFlags;
        this.mNotifPipeline = notifPipeline;
        this.mNotifCollection = notifCollection;
        if (!featureFlags.isNewNotifPipelineRenderingEnabled()) {
            this.mEntryManager.addNotificationEntryListener(new NotificationEntryListener() {
                public void onPendingEntryAdded(NotificationEntry notificationEntry) {
                    StatusBarNotificationActivityStarter.this.handleFullScreenIntent(notificationEntry);
                }
            });
        } else {
            this.mNotifPipeline.addCollectionListener(new NotifCollectionListener() {
                public void onEntryAdded(NotificationEntry notificationEntry) {
                    StatusBarNotificationActivityStarter.this.handleFullScreenIntent(notificationEntry);
                }
            });
        }
        this.mStatusBarRemoteInputCallback = statusBarRemoteInputCallback;
        this.mMainThreadHandler = handler;
        this.mActivityIntentHelper = activityIntentHelper;
        this.mBubbleController = bubbleController;
    }

    public void onNotificationClicked(StatusBarNotification statusBarNotification, ExpandableNotificationRow expandableNotificationRow) {
        PendingIntent pendingIntent;
        RemoteInputController controller = this.mRemoteInputManager.getController();
        if (!controller.isRemoteInputActive(expandableNotificationRow.getEntry()) || TextUtils.isEmpty(expandableNotificationRow.getActiveRemoteInputText())) {
            Notification notification = statusBarNotification.getNotification();
            PendingIntent pendingIntent2 = notification.contentIntent;
            if (pendingIntent2 != null) {
                pendingIntent = pendingIntent2;
            } else {
                pendingIntent = notification.fullScreenIntent;
            }
            boolean isBubble = expandableNotificationRow.getEntry().isBubble();
            if (pendingIntent != null || isBubble) {
                boolean z = pendingIntent != null && pendingIntent.isActivity() && !isBubble;
                boolean z2 = z && this.mActivityIntentHelper.wouldLaunchResolverActivity(pendingIntent.getIntent(), this.mLockscreenUserManager.getCurrentUserId());
                boolean isOccluded = this.mStatusBar.isOccluded();
                boolean z3 = this.mKeyguardStateController.isShowing() && pendingIntent != null && this.mActivityIntentHelper.wouldShowOverLockscreen(pendingIntent.getIntent(), this.mLockscreenUserManager.getCurrentUserId());
                C1422x7665ae55 r1 = new OnDismissAction(statusBarNotification, expandableNotificationRow, controller, pendingIntent, z, isOccluded, z3) {
                    public final /* synthetic */ StatusBarNotification f$1;
                    public final /* synthetic */ ExpandableNotificationRow f$2;
                    public final /* synthetic */ RemoteInputController f$3;
                    public final /* synthetic */ PendingIntent f$4;
                    public final /* synthetic */ boolean f$5;
                    public final /* synthetic */ boolean f$6;
                    public final /* synthetic */ boolean f$7;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                        this.f$5 = r6;
                        this.f$6 = r7;
                        this.f$7 = r8;
                    }

                    public final boolean onDismiss() {
                        return StatusBarNotificationActivityStarter.this.mo18461x7ba4f48c(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7);
                    }
                };
                if (z3) {
                    this.mIsCollapsingToShowActivityOverLockscreen = true;
                    r1.onDismiss();
                } else {
                    this.mActivityStarter.dismissKeyguardThenExecute(r1, null, z2);
                }
                return;
            }
            Log.e("NotifActivityStarter", "onNotificationClicked called for non-clickable notification!");
            return;
        }
        controller.closeRemoteInputs();
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x005c  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0067  */
    /* renamed from: handleNotificationClickAfterKeyguardDismissed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean lambda$onNotificationClicked$0(android.service.notification.StatusBarNotification r13, com.android.systemui.statusbar.notification.row.ExpandableNotificationRow r14, com.android.systemui.statusbar.RemoteInputController r15, android.app.PendingIntent r16, boolean r17, boolean r18, boolean r19) {
        /*
            r12 = this;
            r9 = r12
            r2 = r13
            com.android.systemui.statusbar.phone.HeadsUpManagerPhone r0 = r9.mHeadsUpManager
            r10 = 1
            if (r0 == 0) goto L_0x0027
            java.lang.String r1 = r13.getKey()
            boolean r0 = r0.isAlerting(r1)
            if (r0 == 0) goto L_0x0027
            com.android.systemui.statusbar.NotificationPresenter r0 = r9.mPresenter
            boolean r0 = r0.isPresenterFullyCollapsed()
            r3 = r14
            if (r0 == 0) goto L_0x001d
            com.android.systemui.statusbar.policy.HeadsUpUtil.setIsClickedHeadsUpNotification(r14, r10)
        L_0x001d:
            com.android.systemui.statusbar.phone.HeadsUpManagerPhone r0 = r9.mHeadsUpManager
            java.lang.String r1 = r13.getKey()
            r0.removeNotification(r1, r10)
            goto L_0x0028
        L_0x0027:
            r3 = r14
        L_0x0028:
            r0 = 0
            boolean r1 = shouldAutoCancel(r13)
            if (r1 == 0) goto L_0x0049
            com.android.systemui.statusbar.phone.NotificationGroupManager r1 = r9.mGroupManager
            boolean r1 = r1.isOnlyChildInGroup(r13)
            if (r1 == 0) goto L_0x0049
            com.android.systemui.statusbar.phone.NotificationGroupManager r1 = r9.mGroupManager
            com.android.systemui.statusbar.notification.collection.NotificationEntry r1 = r1.getLogicalGroupSummary(r13)
            android.service.notification.StatusBarNotification r4 = r1.getSbn()
            boolean r4 = shouldAutoCancel(r4)
            if (r4 == 0) goto L_0x0049
            r8 = r1
            goto L_0x004a
        L_0x0049:
            r8 = r0
        L_0x004a:
            com.android.systemui.statusbar.phone.-$$Lambda$StatusBarNotificationActivityStarter$7mfSGy2G6exE-3cGRoA3iww8GIU r11 = new com.android.systemui.statusbar.phone.-$$Lambda$StatusBarNotificationActivityStarter$7mfSGy2G6exE-3cGRoA3iww8GIU
            r0 = r11
            r1 = r12
            r2 = r13
            r3 = r14
            r4 = r15
            r5 = r16
            r6 = r17
            r7 = r18
            r0.<init>(r2, r3, r4, r5, r6, r7, r8)
            if (r19 == 0) goto L_0x0067
            com.android.systemui.statusbar.phone.ShadeController r0 = r9.mShadeController
            r0.addPostCollapseAction(r11)
            com.android.systemui.statusbar.phone.ShadeController r0 = r9.mShadeController
            r0.collapsePanel(r10)
            goto L_0x0087
        L_0x0067:
            com.android.systemui.statusbar.policy.KeyguardStateController r0 = r9.mKeyguardStateController
            boolean r0 = r0.isShowing()
            if (r0 == 0) goto L_0x0082
            com.android.systemui.statusbar.phone.StatusBar r0 = r9.mStatusBar
            boolean r0 = r0.isOccluded()
            if (r0 == 0) goto L_0x0082
            com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager r0 = r9.mStatusBarKeyguardViewManager
            r0.addAfterKeyguardGoneRunnable(r11)
            com.android.systemui.statusbar.phone.ShadeController r0 = r9.mShadeController
            r0.collapsePanel()
            goto L_0x0087
        L_0x0082:
            android.os.Handler r0 = r9.mBackgroundHandler
            r0.postAtFrontOfQueue(r11)
        L_0x0087:
            com.android.systemui.statusbar.phone.NotificationPanelViewController r0 = r9.mNotificationPanel
            boolean r0 = r0.isFullyCollapsed()
            r0 = r0 ^ r10
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.StatusBarNotificationActivityStarter.lambda$onNotificationClicked$0(android.service.notification.StatusBarNotification, com.android.systemui.statusbar.notification.row.ExpandableNotificationRow, com.android.systemui.statusbar.RemoteInputController, android.app.PendingIntent, boolean, boolean, boolean):boolean");
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x006e  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0072  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0091  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00b0  */
    /* renamed from: handleNotificationClickAfterPanelCollapsed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void lambda$handleNotificationClickAfterKeyguardDismissed$1(android.service.notification.StatusBarNotification r12, com.android.systemui.statusbar.notification.row.ExpandableNotificationRow r13, com.android.systemui.statusbar.RemoteInputController r14, android.app.PendingIntent r15, boolean r16, boolean r17, com.android.systemui.statusbar.notification.collection.NotificationEntry r18) {
        /*
            r11 = this;
            r6 = r11
            r7 = r18
            java.lang.String r8 = r12.getKey()
            android.app.IActivityManager r0 = android.app.ActivityManager.getService()     // Catch:{ RemoteException -> 0x000e }
            r0.resumeAppSwitches()     // Catch:{ RemoteException -> 0x000e }
        L_0x000e:
            if (r16 == 0) goto L_0x0038
            android.os.UserHandle r0 = r15.getCreatorUserHandle()
            int r0 = r0.getIdentifier()
            com.android.internal.widget.LockPatternUtils r1 = r6.mLockPatternUtils
            boolean r1 = r1.isSeparateProfileChallengeEnabled(r0)
            if (r1 == 0) goto L_0x0038
            android.app.KeyguardManager r1 = r6.mKeyguardManager
            boolean r1 = r1.isDeviceLocked(r0)
            if (r1 == 0) goto L_0x0038
            com.android.systemui.statusbar.phone.StatusBarRemoteInputCallback r1 = r6.mStatusBarRemoteInputCallback
            android.content.IntentSender r2 = r15.getIntentSender()
            boolean r0 = r1.startWorkChallengeIfNecessary(r0, r2, r8)
            if (r0 == 0) goto L_0x0038
            r11.collapseOnMainThread()
            return
        L_0x0038:
            com.android.systemui.statusbar.notification.collection.NotificationEntry r9 = r13.getEntry()
            boolean r10 = r9.isBubble()
            java.lang.CharSequence r0 = r9.remoteInputText
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            r1 = 0
            if (r0 != 0) goto L_0x004c
            java.lang.CharSequence r0 = r9.remoteInputText
            goto L_0x004d
        L_0x004c:
            r0 = r1
        L_0x004d:
            boolean r2 = android.text.TextUtils.isEmpty(r0)
            if (r2 != 0) goto L_0x006b
            r2 = r14
            boolean r2 = r14.isSpinning(r8)
            if (r2 != 0) goto L_0x006b
            android.content.Intent r1 = new android.content.Intent
            r1.<init>()
            java.lang.String r0 = r0.toString()
            java.lang.String r2 = "android.remoteInputDraft"
            android.content.Intent r0 = r1.putExtra(r2, r0)
            r2 = r0
            goto L_0x006c
        L_0x006b:
            r2 = r1
        L_0x006c:
            if (r10 == 0) goto L_0x0072
            r11.expandBubbleStackOnMainThread(r8)
            goto L_0x007c
        L_0x0072:
            r0 = r11
            r1 = r15
            r3 = r13
            r4 = r17
            r5 = r16
            r0.startNotificationIntent(r1, r2, r3, r4, r5)
        L_0x007c:
            if (r16 != 0) goto L_0x0080
            if (r10 == 0) goto L_0x008b
        L_0x0080:
            dagger.Lazy<com.android.systemui.assist.AssistManager> r0 = r6.mAssistManagerLazy
            java.lang.Object r0 = r0.get()
            com.android.systemui.assist.AssistManager r0 = (com.android.systemui.assist.AssistManager) r0
            r0.hideAssist()
        L_0x008b:
            boolean r0 = r11.shouldCollapse()
            if (r0 == 0) goto L_0x0094
            r11.collapseOnMainThread()
        L_0x0094:
            int r0 = r11.getVisibleNotificationsCount()
            android.service.notification.NotificationListenerService$Ranking r1 = r9.getRanking()
            int r1 = r1.getRank()
            com.android.internal.statusbar.NotificationVisibility$NotificationLocation r2 = com.android.systemui.statusbar.notification.logging.NotificationLogger.getNotificationLocation(r9)
            r3 = 1
            com.android.internal.statusbar.NotificationVisibility r0 = com.android.internal.statusbar.NotificationVisibility.obtain(r8, r1, r0, r3, r2)
            com.android.internal.statusbar.IStatusBarService r1 = r6.mBarService     // Catch:{ RemoteException -> 0x00ae }
            r1.onNotificationClick(r8, r0)     // Catch:{ RemoteException -> 0x00ae }
        L_0x00ae:
            if (r10 != 0) goto L_0x00ca
            if (r7 == 0) goto L_0x00b5
            r11.removeNotification(r7)
        L_0x00b5:
            boolean r0 = shouldAutoCancel(r12)
            if (r0 != 0) goto L_0x00c3
            com.android.systemui.statusbar.NotificationRemoteInputManager r0 = r6.mRemoteInputManager
            boolean r0 = r0.isNotificationKeptForRemoteInputHistory(r8)
            if (r0 == 0) goto L_0x00ca
        L_0x00c3:
            com.android.systemui.statusbar.notification.collection.NotificationEntry r0 = r13.getEntry()
            r11.removeNotification(r0)
        L_0x00ca:
            r0 = 0
            r6.mIsCollapsingToShowActivityOverLockscreen = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.StatusBarNotificationActivityStarter.lambda$handleNotificationClickAfterKeyguardDismissed$1(android.service.notification.StatusBarNotification, com.android.systemui.statusbar.notification.row.ExpandableNotificationRow, com.android.systemui.statusbar.RemoteInputController, android.app.PendingIntent, boolean, boolean, com.android.systemui.statusbar.notification.collection.NotificationEntry):void");
    }

    private void expandBubbleStackOnMainThread(String str) {
        if (Looper.getMainLooper().isCurrentThread()) {
            this.mBubbleController.expandStackAndSelectBubble(str);
        } else {
            this.mMainThreadHandler.post(new Runnable(str) {
                public final /* synthetic */ String f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    StatusBarNotificationActivityStarter.this.mo18458xcfcf1363(this.f$1);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$expandBubbleStackOnMainThread$2 */
    public /* synthetic */ void mo18458xcfcf1363(String str) {
        this.mBubbleController.expandStackAndSelectBubble(str);
    }

    private void startNotificationIntent(PendingIntent pendingIntent, Intent intent, View view, boolean z, boolean z2) {
        RemoteAnimationAdapter launchAnimation = this.mActivityLaunchAnimator.getLaunchAnimation(view, z);
        if (launchAnimation != null) {
            try {
                ActivityTaskManager.getService().registerRemoteAnimationForNextActivityStart(pendingIntent.getCreatorPackage(), launchAnimation);
            } catch (CanceledException | RemoteException e) {
                StringBuilder sb = new StringBuilder();
                sb.append("Sending contentIntent failed: ");
                sb.append(e);
                Log.w("NotifActivityStarter", sb.toString());
                return;
            }
        }
        this.mActivityLaunchAnimator.setLaunchResult(pendingIntent.sendAndReturnResult(this.mContext, 0, intent, null, null, null, StatusBar.getActivityOptions(launchAnimation)), z2);
    }

    public void startNotificationGutsIntent(Intent intent, int i, ExpandableNotificationRow expandableNotificationRow) {
        this.mActivityStarter.dismissKeyguardThenExecute(new OnDismissAction(intent, expandableNotificationRow, i) {
            public final /* synthetic */ Intent f$1;
            public final /* synthetic */ ExpandableNotificationRow f$2;
            public final /* synthetic */ int f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final boolean onDismiss() {
                return StatusBarNotificationActivityStarter.this.mo18465x977eacee(this.f$1, this.f$2, this.f$3);
            }
        }, null, false);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startNotificationGutsIntent$5 */
    public /* synthetic */ boolean mo18465x977eacee(Intent intent, ExpandableNotificationRow expandableNotificationRow, int i) {
        AsyncTask.execute(new Runnable(intent, expandableNotificationRow, i) {
            public final /* synthetic */ Intent f$1;
            public final /* synthetic */ ExpandableNotificationRow f$2;
            public final /* synthetic */ int f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                StatusBarNotificationActivityStarter.this.mo18464x362c104f(this.f$1, this.f$2, this.f$3);
            }
        });
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startNotificationGutsIntent$4 */
    public /* synthetic */ void mo18464x362c104f(Intent intent, ExpandableNotificationRow expandableNotificationRow, int i) {
        this.mActivityLaunchAnimator.setLaunchResult(TaskStackBuilder.create(this.mContext).addNextIntentWithParentStack(intent).startActivities(StatusBar.getActivityOptions(this.mActivityLaunchAnimator.getLaunchAnimation(expandableNotificationRow, this.mStatusBar.isOccluded())), new UserHandle(UserHandle.getUserId(i))), true);
        if (shouldCollapse()) {
            this.mMainThreadHandler.post(new Runnable() {
                public final void run() {
                    StatusBarNotificationActivityStarter.this.mo18463xd4d973b0();
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startNotificationGutsIntent$3 */
    public /* synthetic */ void mo18463xd4d973b0() {
        this.mCommandQueue.animateCollapsePanels(2, true);
    }

    /* access modifiers changed from: private */
    public void handleFullScreenIntent(NotificationEntry notificationEntry) {
        if (this.mNotificationInterruptStateProvider.shouldLaunchFullScreenIntentWhenAdded(notificationEntry)) {
            String str = "NotifActivityStarter";
            if (shouldSuppressFullScreenIntent(notificationEntry)) {
                if (DEBUG) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("No Fullscreen intent: suppressed by DND: ");
                    sb.append(notificationEntry.getKey());
                    Log.d(str, sb.toString());
                }
            } else if (notificationEntry.getImportance() >= 4) {
                this.mUiBgExecutor.execute(new Runnable() {
                    public final void run() {
                        StatusBarNotificationActivityStarter.this.mo18459xd1b1b5ea();
                    }
                });
                if (DEBUG) {
                    Log.d(str, "Notification has fullScreenIntent; sending fullScreenIntent");
                }
                try {
                    EventLog.writeEvent(36002, notificationEntry.getKey());
                    notificationEntry.getSbn().getNotification().fullScreenIntent.send();
                    notificationEntry.notifyFullScreenIntentLaunched();
                    this.mMetricsLogger.count("note_fullscreen", 1);
                } catch (CanceledException unused) {
                }
            } else if (DEBUG) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("No Fullscreen intent: not important enough: ");
                sb2.append(notificationEntry.getKey());
                Log.d(str, sb2.toString());
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$handleFullScreenIntent$6 */
    public /* synthetic */ void mo18459xd1b1b5ea() {
        try {
            this.mDreamManager.awaken();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public boolean isCollapsingToShowActivityOverLockscreen() {
        return this.mIsCollapsingToShowActivityOverLockscreen;
    }

    private static boolean shouldAutoCancel(StatusBarNotification statusBarNotification) {
        int i = statusBarNotification.getNotification().flags;
        return (i & 16) == 16 && (i & 64) == 0;
    }

    private void collapseOnMainThread() {
        if (Looper.getMainLooper().isCurrentThread()) {
            this.mShadeController.collapsePanel();
            return;
        }
        Handler handler = this.mMainThreadHandler;
        ShadeController shadeController = this.mShadeController;
        Objects.requireNonNull(shadeController);
        handler.post(new Runnable() {
            public final void run() {
                ShadeController.this.collapsePanel();
            }
        });
    }

    private boolean shouldCollapse() {
        return this.mStatusBarStateController.getState() != 0 || !this.mActivityLaunchAnimator.isAnimationPending();
    }

    private boolean shouldSuppressFullScreenIntent(NotificationEntry notificationEntry) {
        if (this.mPresenter.isDeviceInVrMode()) {
            return true;
        }
        return notificationEntry.shouldSuppressFullScreenIntent();
    }

    private void removeNotification(NotificationEntry notificationEntry) {
        this.mMainThreadHandler.post(new Runnable(notificationEntry) {
            public final /* synthetic */ NotificationEntry f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                StatusBarNotificationActivityStarter.this.lambda$removeNotification$7$StatusBarNotificationActivityStarter(this.f$1);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$removeNotification$7 */
    public /* synthetic */ void lambda$removeNotification$7$StatusBarNotificationActivityStarter(NotificationEntry notificationEntry) {
        Runnable createRemoveRunnable = createRemoveRunnable(notificationEntry);
        if (this.mPresenter.isCollapsing()) {
            this.mShadeController.addPostCollapseAction(createRemoveRunnable);
        } else {
            createRemoveRunnable.run();
        }
    }

    private int getVisibleNotificationsCount() {
        if (this.mFeatureFlags.isNewNotifPipelineRenderingEnabled()) {
            return this.mNotifPipeline.getShadeListCount();
        }
        return this.mEntryManager.getActiveNotificationsCount();
    }

    private Runnable createRemoveRunnable(final NotificationEntry notificationEntry) {
        return this.mFeatureFlags.isNewNotifPipelineRenderingEnabled() ? new Runnable() {
            public void run() {
                int i = StatusBarNotificationActivityStarter.this.mHeadsUpManager.isAlerting(notificationEntry.getKey()) ? 1 : StatusBarNotificationActivityStarter.this.mNotificationPanel.hasPulsingNotifications() ? 2 : 3;
                NotifCollection access$400 = StatusBarNotificationActivityStarter.this.mNotifCollection;
                NotificationEntry notificationEntry = notificationEntry;
                access$400.dismissNotification(notificationEntry, new DismissedByUserStats(i, 1, NotificationVisibility.obtain(notificationEntry.getKey(), notificationEntry.getRanking().getRank(), StatusBarNotificationActivityStarter.this.mNotifPipeline.getShadeListCount(), true, NotificationLogger.getNotificationLocation(notificationEntry))));
            }
        } : new Runnable() {
            public void run() {
                StatusBarNotificationActivityStarter.this.mEntryManager.performRemoveNotification(notificationEntry.getSbn(), 1);
            }
        };
    }
}
