package com.android.systemui.statusbar.phone;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.os.Handler;
import android.os.RemoteException;
import android.os.UserHandle;
import android.view.View;
import android.view.ViewParent;
import com.android.systemui.ActivityIntentHelper;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.ActivityStarter.OnDismissAction;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.CommandQueue.Callbacks;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationRemoteInputManager.Callback;
import com.android.systemui.statusbar.NotificationRemoteInputManager.ClickHandler;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.NotificationContentView;
import com.android.systemui.statusbar.notification.stack.NotificationStackScrollLayout;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import java.util.Objects;

public class StatusBarRemoteInputCallback implements Callback, Callbacks, StateListener {
    private final ActivityIntentHelper mActivityIntentHelper;
    private final ActivityStarter mActivityStarter;
    protected BroadcastReceiver mChallengeReceiver = new ChallengeReceiver();
    private final CommandQueue mCommandQueue;
    private final Context mContext;
    private int mDisabled2;
    private final NotificationGroupManager mGroupManager;
    private KeyguardManager mKeyguardManager;
    private final KeyguardStateController mKeyguardStateController;
    /* access modifiers changed from: private */
    public final NotificationLockscreenUserManager mLockscreenUserManager;
    private Handler mMainHandler = new Handler();
    private View mPendingRemoteInputView;
    private View mPendingWorkRemoteInputView;
    private final ShadeController mShadeController;
    private final StatusBarKeyguardViewManager mStatusBarKeyguardViewManager;
    private final SysuiStatusBarStateController mStatusBarStateController;

    protected class ChallengeReceiver extends BroadcastReceiver {
        protected ChallengeReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int intExtra = intent.getIntExtra("android.intent.extra.user_handle", -10000);
            if ("android.intent.action.DEVICE_LOCKED_CHANGED".equals(action) && intExtra != StatusBarRemoteInputCallback.this.mLockscreenUserManager.getCurrentUserId() && StatusBarRemoteInputCallback.this.mLockscreenUserManager.isCurrentProfile(intExtra)) {
                StatusBarRemoteInputCallback.this.onWorkChallengeChanged();
            }
        }
    }

    public StatusBarRemoteInputCallback(Context context, NotificationGroupManager notificationGroupManager, NotificationLockscreenUserManager notificationLockscreenUserManager, KeyguardStateController keyguardStateController, StatusBarStateController statusBarStateController, StatusBarKeyguardViewManager statusBarKeyguardViewManager, ActivityStarter activityStarter, ShadeController shadeController, CommandQueue commandQueue) {
        Context context2 = context;
        CommandQueue commandQueue2 = commandQueue;
        this.mContext = context2;
        this.mStatusBarKeyguardViewManager = statusBarKeyguardViewManager;
        this.mShadeController = shadeController;
        context.registerReceiverAsUser(this.mChallengeReceiver, UserHandle.ALL, new IntentFilter("android.intent.action.DEVICE_LOCKED_CHANGED"), null, null);
        this.mLockscreenUserManager = notificationLockscreenUserManager;
        this.mKeyguardStateController = keyguardStateController;
        SysuiStatusBarStateController sysuiStatusBarStateController = (SysuiStatusBarStateController) statusBarStateController;
        this.mStatusBarStateController = sysuiStatusBarStateController;
        this.mActivityStarter = activityStarter;
        sysuiStatusBarStateController.addCallback(this);
        this.mKeyguardManager = (KeyguardManager) context.getSystemService(KeyguardManager.class);
        this.mCommandQueue = commandQueue2;
        commandQueue2.addCallback((Callbacks) this);
        this.mActivityIntentHelper = new ActivityIntentHelper(this.mContext);
        this.mGroupManager = notificationGroupManager;
    }

    public void onStateChanged(int i) {
        boolean z = this.mPendingRemoteInputView != null;
        if (i != 0) {
            return;
        }
        if ((this.mStatusBarStateController.leaveOpenOnKeyguardHide() || z) && !this.mStatusBarStateController.isKeyguardRequested()) {
            if (z) {
                Handler handler = this.mMainHandler;
                View view = this.mPendingRemoteInputView;
                Objects.requireNonNull(view);
                handler.post(new Runnable(view) {
                    public final /* synthetic */ View f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void run() {
                        this.f$0.callOnClick();
                    }
                });
            }
            this.mPendingRemoteInputView = null;
        }
    }

    public void onLockedRemoteInput(ExpandableNotificationRow expandableNotificationRow, View view) {
        if (!expandableNotificationRow.isPinned()) {
            this.mStatusBarStateController.setLeaveOpenOnKeyguardHide(true);
        }
        this.mStatusBarKeyguardViewManager.showBouncer(true);
        this.mPendingRemoteInputView = view;
    }

    /* access modifiers changed from: protected */
    public void onWorkChallengeChanged() {
        this.mLockscreenUserManager.updatePublicMode();
        if (this.mPendingWorkRemoteInputView != null && !this.mLockscreenUserManager.isAnyProfilePublicMode()) {
            this.mShadeController.postOnShadeExpanded(new Runnable() {
                public final void run() {
                    StatusBarRemoteInputCallback.this.lambda$onWorkChallengeChanged$2$StatusBarRemoteInputCallback();
                }
            });
            this.mShadeController.instantExpandNotificationsPanel();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onWorkChallengeChanged$2 */
    public /* synthetic */ void lambda$onWorkChallengeChanged$2$StatusBarRemoteInputCallback() {
        View view = this.mPendingWorkRemoteInputView;
        if (view != null) {
            ViewParent parent = view.getParent();
            while (!(parent instanceof ExpandableNotificationRow)) {
                if (parent != null) {
                    parent = parent.getParent();
                } else {
                    return;
                }
            }
            ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) parent;
            ViewParent parent2 = expandableNotificationRow.getParent();
            if (parent2 instanceof NotificationStackScrollLayout) {
                NotificationStackScrollLayout notificationStackScrollLayout = (NotificationStackScrollLayout) parent2;
                expandableNotificationRow.makeActionsVisibile();
                expandableNotificationRow.post(new Runnable(notificationStackScrollLayout, expandableNotificationRow) {
                    public final /* synthetic */ NotificationStackScrollLayout f$1;
                    public final /* synthetic */ ExpandableNotificationRow f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void run() {
                        StatusBarRemoteInputCallback.this.lambda$onWorkChallengeChanged$1$StatusBarRemoteInputCallback(this.f$1, this.f$2);
                    }
                });
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onWorkChallengeChanged$1 */
    public /* synthetic */ void lambda$onWorkChallengeChanged$1$StatusBarRemoteInputCallback(NotificationStackScrollLayout notificationStackScrollLayout, ExpandableNotificationRow expandableNotificationRow) {
        C1432x35deece0 r0 = new Runnable(notificationStackScrollLayout) {
            public final /* synthetic */ NotificationStackScrollLayout f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                StatusBarRemoteInputCallback.this.lambda$onWorkChallengeChanged$0$StatusBarRemoteInputCallback(this.f$1);
            }
        };
        if (notificationStackScrollLayout.scrollTo(expandableNotificationRow)) {
            notificationStackScrollLayout.setFinishScrollingCallback(r0);
        } else {
            r0.run();
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onWorkChallengeChanged$0 */
    public /* synthetic */ void lambda$onWorkChallengeChanged$0$StatusBarRemoteInputCallback(NotificationStackScrollLayout notificationStackScrollLayout) {
        this.mPendingWorkRemoteInputView.callOnClick();
        this.mPendingWorkRemoteInputView = null;
        notificationStackScrollLayout.setFinishScrollingCallback(null);
    }

    public void onMakeExpandedVisibleForRemoteInput(ExpandableNotificationRow expandableNotificationRow, View view) {
        if (this.mKeyguardStateController.isShowing()) {
            onLockedRemoteInput(expandableNotificationRow, view);
            return;
        }
        if (expandableNotificationRow.isChildInGroup() && !expandableNotificationRow.areChildrenExpanded()) {
            this.mGroupManager.toggleGroupExpansion(expandableNotificationRow.getEntry().getSbn());
        }
        expandableNotificationRow.setUserExpanded(true);
        NotificationContentView privateLayout = expandableNotificationRow.getPrivateLayout();
        Objects.requireNonNull(view);
        privateLayout.setOnExpandedVisibleListener(new Runnable(view) {
            public final /* synthetic */ View f$0;

            {
                this.f$0 = r1;
            }

            public final void run() {
                this.f$0.performClick();
            }
        });
    }

    public void onLockedWorkRemoteInput(int i, ExpandableNotificationRow expandableNotificationRow, View view) {
        this.mCommandQueue.animateCollapsePanels();
        startWorkChallengeIfNecessary(i, null, null);
        this.mPendingWorkRemoteInputView = view;
    }

    /* access modifiers changed from: 0000 */
    public boolean startWorkChallengeIfNecessary(int i, IntentSender intentSender, String str) {
        this.mPendingWorkRemoteInputView = null;
        Intent createConfirmDeviceCredentialIntent = this.mKeyguardManager.createConfirmDeviceCredentialIntent(null, null, i);
        if (createConfirmDeviceCredentialIntent == null) {
            return false;
        }
        Intent intent = new Intent("com.android.systemui.statusbar.work_challenge_unlocked_notification_action");
        String str2 = "android.intent.extra.INTENT";
        intent.putExtra(str2, intentSender);
        intent.putExtra("android.intent.extra.INDEX", str);
        intent.setPackage(this.mContext.getPackageName());
        createConfirmDeviceCredentialIntent.putExtra(str2, PendingIntent.getBroadcast(this.mContext, 0, intent, 1409286144).getIntentSender());
        try {
            ActivityManager.getService().startConfirmDeviceCredentialIntent(createConfirmDeviceCredentialIntent, null);
        } catch (RemoteException unused) {
        }
        return true;
    }

    public boolean shouldHandleRemoteInput(View view, PendingIntent pendingIntent) {
        return (this.mDisabled2 & 4) != 0;
    }

    public boolean handleRemoteViewClick(View view, PendingIntent pendingIntent, ClickHandler clickHandler) {
        if (!pendingIntent.isActivity()) {
            return clickHandler.handleClick();
        }
        this.mActivityStarter.dismissKeyguardThenExecute(new OnDismissAction(clickHandler) {
            public final /* synthetic */ ClickHandler f$1;

            {
                this.f$1 = r2;
            }

            public final boolean onDismiss() {
                return StatusBarRemoteInputCallback.this.lambda$handleRemoteViewClick$3$StatusBarRemoteInputCallback(this.f$1);
            }
        }, null, this.mActivityIntentHelper.wouldLaunchResolverActivity(pendingIntent.getIntent(), this.mLockscreenUserManager.getCurrentUserId()));
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$handleRemoteViewClick$3 */
    public /* synthetic */ boolean lambda$handleRemoteViewClick$3$StatusBarRemoteInputCallback(ClickHandler clickHandler) {
        try {
            ActivityManager.getService().resumeAppSwitches();
        } catch (RemoteException unused) {
        }
        return clickHandler.handleClick() && this.mShadeController.closeShadeIfOpen();
    }

    public void disable(int i, int i2, int i3, boolean z) {
        if (i == this.mContext.getDisplayId()) {
            this.mDisabled2 = i3;
        }
    }
}
