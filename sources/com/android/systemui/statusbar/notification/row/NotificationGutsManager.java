package com.android.systemui.statusbar.notification.row;

import android.app.INotificationManager;
import android.app.NotificationChannel;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.service.notification.StatusBarNotification;
import android.util.ArraySet;
import android.util.IconDrawableFactory;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.settingslib.notification.ConversationIconFactory;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.Dependency;
import com.android.systemui.Dumpable;
import com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin.MenuItem;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.NotificationLifetimeExtender;
import com.android.systemui.statusbar.NotificationLifetimeExtender.NotificationSafeToRemoveCallback;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationPresenter;
import com.android.systemui.statusbar.StatusBarStateControllerImpl;
import com.android.systemui.statusbar.notification.NotificationActivityStarter;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.provider.HighPriorityProvider;
import com.android.systemui.statusbar.notification.row.NotificationConversationInfo.OnSnoozeClickListener;
import com.android.systemui.statusbar.notification.row.NotificationGuts.OnGutsClosedListener;
import com.android.systemui.statusbar.notification.row.NotificationGuts.OnHeightChangedListener;
import com.android.systemui.statusbar.notification.row.NotificationInfo.CheckSaveListener;
import com.android.systemui.statusbar.notification.row.NotificationInfo.OnAppSettingsClickListener;
import com.android.systemui.statusbar.notification.stack.NotificationListContainer;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.policy.DeviceProvisionedController;
import dagger.Lazy;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Objects;

public class NotificationGutsManager implements Dumpable, NotificationLifetimeExtender {
    /* access modifiers changed from: private */
    public final AccessibilityManager mAccessibilityManager;
    private final Context mContext;
    private final DeviceProvisionedController mDeviceProvisionedController = ((DeviceProvisionedController) Dependency.get(DeviceProvisionedController.class));
    /* access modifiers changed from: private */
    public MenuItem mGutsMenuItem;
    private final HighPriorityProvider mHighPriorityProvider;
    @VisibleForTesting
    protected String mKeyToRemoveOnGutsClosed;
    private final LauncherApps mLauncherApps;
    /* access modifiers changed from: private */
    public NotificationListContainer mListContainer;
    private final NotificationLockscreenUserManager mLockscreenUserManager = ((NotificationLockscreenUserManager) Dependency.get(NotificationLockscreenUserManager.class));
    private final Handler mMainHandler;
    private final MetricsLogger mMetricsLogger = ((MetricsLogger) Dependency.get(MetricsLogger.class));
    private NotificationActivityStarter mNotificationActivityStarter;
    private NotificationGuts mNotificationGutsExposed;
    private NotificationSafeToRemoveCallback mNotificationLifetimeFinishedCallback;
    private final INotificationManager mNotificationManager;
    private OnSettingsClickListener mOnSettingsClickListener;
    private Runnable mOpenRunnable;
    private NotificationPresenter mPresenter;
    private final ShortcutManager mShortcutManager;
    private final Lazy<StatusBar> mStatusBarLazy;
    /* access modifiers changed from: private */
    public final StatusBarStateController mStatusBarStateController = ((StatusBarStateController) Dependency.get(StatusBarStateController.class));
    private final VisualStabilityManager mVisualStabilityManager;

    public interface OnSettingsClickListener {
        void onSettingsClick(String str);
    }

    public NotificationGutsManager(Context context, VisualStabilityManager visualStabilityManager, Lazy<StatusBar> lazy, Handler handler, AccessibilityManager accessibilityManager, HighPriorityProvider highPriorityProvider, INotificationManager iNotificationManager, LauncherApps launcherApps, ShortcutManager shortcutManager) {
        this.mContext = context;
        this.mVisualStabilityManager = visualStabilityManager;
        this.mStatusBarLazy = lazy;
        this.mMainHandler = handler;
        this.mAccessibilityManager = accessibilityManager;
        this.mHighPriorityProvider = highPriorityProvider;
        this.mNotificationManager = iNotificationManager;
        this.mLauncherApps = launcherApps;
        this.mShortcutManager = shortcutManager;
    }

    public void setUpWithPresenter(NotificationPresenter notificationPresenter, NotificationListContainer notificationListContainer, CheckSaveListener checkSaveListener, OnSettingsClickListener onSettingsClickListener) {
        this.mPresenter = notificationPresenter;
        this.mListContainer = notificationListContainer;
        this.mOnSettingsClickListener = onSettingsClickListener;
    }

    public void setNotificationActivityStarter(NotificationActivityStarter notificationActivityStarter) {
        this.mNotificationActivityStarter = notificationActivityStarter;
    }

    public void onDensityOrFontScaleChanged(NotificationEntry notificationEntry) {
        setExposedGuts(notificationEntry.getGuts());
        bindGuts(notificationEntry.getRow());
    }

    private void startAppNotificationSettingsActivity(String str, int i, NotificationChannel notificationChannel, ExpandableNotificationRow expandableNotificationRow) {
        Intent intent = new Intent("android.settings.APP_NOTIFICATION_SETTINGS");
        intent.putExtra("android.provider.extra.APP_PACKAGE", str);
        intent.putExtra("app_uid", i);
        if (notificationChannel != null) {
            Bundle bundle = new Bundle();
            String str2 = ":settings:fragment_args_key";
            intent.putExtra(str2, notificationChannel.getId());
            bundle.putString(str2, notificationChannel.getId());
            intent.putExtra(":settings:show_fragment_args", bundle);
        }
        this.mNotificationActivityStarter.startNotificationGutsIntent(intent, i, expandableNotificationRow);
    }

    private void startAppDetailsSettingsActivity(String str, int i, NotificationChannel notificationChannel, ExpandableNotificationRow expandableNotificationRow) {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", str, null));
        intent.putExtra("android.provider.extra.APP_PACKAGE", str);
        intent.putExtra("app_uid", i);
        if (notificationChannel != null) {
            intent.putExtra(":settings:fragment_args_key", notificationChannel.getId());
        }
        this.mNotificationActivityStarter.startNotificationGutsIntent(intent, i, expandableNotificationRow);
    }

    /* access modifiers changed from: protected */
    public void startAppOpsSettingsActivity(String str, int i, ArraySet<Integer> arraySet, ExpandableNotificationRow expandableNotificationRow) {
        boolean contains = arraySet.contains(Integer.valueOf(24));
        Integer valueOf = Integer.valueOf(27);
        Integer valueOf2 = Integer.valueOf(26);
        if (contains) {
            if (arraySet.contains(valueOf2) || arraySet.contains(valueOf)) {
                startAppDetailsSettingsActivity(str, i, null, expandableNotificationRow);
                return;
            }
            Intent intent = new Intent("android.settings.MANAGE_APP_OVERLAY_PERMISSION");
            intent.setData(Uri.fromParts("package", str, null));
            this.mNotificationActivityStarter.startNotificationGutsIntent(intent, i, expandableNotificationRow);
        } else if (arraySet.contains(valueOf2) || arraySet.contains(valueOf)) {
            Intent intent2 = new Intent("android.intent.action.MANAGE_APP_PERMISSIONS");
            intent2.putExtra("android.intent.extra.PACKAGE_NAME", str);
            this.mNotificationActivityStarter.startNotificationGutsIntent(intent2, i, expandableNotificationRow);
        }
    }

    private boolean bindGuts(ExpandableNotificationRow expandableNotificationRow) {
        expandableNotificationRow.ensureGutsInflated();
        return bindGuts(expandableNotificationRow, this.mGutsMenuItem);
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public boolean bindGuts(ExpandableNotificationRow expandableNotificationRow, MenuItem menuItem) {
        StatusBarNotification sbn = expandableNotificationRow.getEntry().getSbn();
        expandableNotificationRow.setGutsView(menuItem);
        expandableNotificationRow.setTag(sbn.getPackageName());
        expandableNotificationRow.getGuts().setClosedListener(new OnGutsClosedListener(expandableNotificationRow, sbn) {
            public final /* synthetic */ ExpandableNotificationRow f$1;
            public final /* synthetic */ StatusBarNotification f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void onGutsClosed(NotificationGuts notificationGuts) {
                NotificationGutsManager.this.lambda$bindGuts$0$NotificationGutsManager(this.f$1, this.f$2, notificationGuts);
            }
        });
        View gutsView = menuItem.getGutsView();
        try {
            if (gutsView instanceof NotificationSnooze) {
                initializeSnoozeView(expandableNotificationRow, (NotificationSnooze) gutsView);
            } else if (gutsView instanceof AppOpsInfo) {
                initializeAppOpsInfo(expandableNotificationRow, (AppOpsInfo) gutsView);
            } else if (gutsView instanceof NotificationInfo) {
                initializeNotificationInfo(expandableNotificationRow, (NotificationInfo) gutsView);
            } else if (gutsView instanceof NotificationConversationInfo) {
                initializeConversationNotificationInfo(expandableNotificationRow, (NotificationConversationInfo) gutsView);
            }
            return true;
        } catch (Exception e) {
            Log.e("NotificationGutsManager", "error binding guts", e);
            return false;
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$bindGuts$0 */
    public /* synthetic */ void lambda$bindGuts$0$NotificationGutsManager(ExpandableNotificationRow expandableNotificationRow, StatusBarNotification statusBarNotification, NotificationGuts notificationGuts) {
        expandableNotificationRow.onGutsClosed();
        if (!notificationGuts.willBeRemoved() && !expandableNotificationRow.isRemoved()) {
            this.mListContainer.onHeightChanged(expandableNotificationRow, !this.mPresenter.isPresenterFullyCollapsed());
        }
        if (this.mNotificationGutsExposed == notificationGuts) {
            this.mNotificationGutsExposed = null;
            this.mGutsMenuItem = null;
        }
        String key = statusBarNotification.getKey();
        if (key.equals(this.mKeyToRemoveOnGutsClosed)) {
            this.mKeyToRemoveOnGutsClosed = null;
            NotificationSafeToRemoveCallback notificationSafeToRemoveCallback = this.mNotificationLifetimeFinishedCallback;
            if (notificationSafeToRemoveCallback != null) {
                notificationSafeToRemoveCallback.onSafeToRemove(key);
            }
        }
    }

    private void initializeSnoozeView(ExpandableNotificationRow expandableNotificationRow, NotificationSnooze notificationSnooze) {
        NotificationGuts guts = expandableNotificationRow.getGuts();
        StatusBarNotification sbn = expandableNotificationRow.getEntry().getSbn();
        notificationSnooze.setSnoozeListener(this.mListContainer.getSwipeActionHelper());
        notificationSnooze.setStatusBarNotification(sbn);
        notificationSnooze.setSnoozeOptions(expandableNotificationRow.getEntry().getSnoozeCriteria());
        guts.setHeightChangedListener(new OnHeightChangedListener(expandableNotificationRow) {
            public final /* synthetic */ ExpandableNotificationRow f$1;

            {
                this.f$1 = r2;
            }

            public final void onHeightChanged(NotificationGuts notificationGuts) {
                NotificationGutsManager.this.lambda$initializeSnoozeView$1$NotificationGutsManager(this.f$1, notificationGuts);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$initializeSnoozeView$1 */
    public /* synthetic */ void lambda$initializeSnoozeView$1$NotificationGutsManager(ExpandableNotificationRow expandableNotificationRow, NotificationGuts notificationGuts) {
        this.mListContainer.onHeightChanged(expandableNotificationRow, expandableNotificationRow.isShown());
    }

    private void initializeAppOpsInfo(ExpandableNotificationRow expandableNotificationRow, AppOpsInfo appOpsInfo) {
        NotificationGuts guts = expandableNotificationRow.getGuts();
        StatusBarNotification sbn = expandableNotificationRow.getEntry().getSbn();
        PackageManager packageManagerForUser = StatusBar.getPackageManagerForUser(this.mContext, sbn.getUser().getIdentifier());
        $$Lambda$NotificationGutsManager$QUX76CVRNteGCzCinyuNeuYX3tU r3 = new com.android.systemui.statusbar.notification.row.AppOpsInfo.OnSettingsClickListener(guts, expandableNotificationRow) {
            public final /* synthetic */ NotificationGuts f$1;
            public final /* synthetic */ ExpandableNotificationRow f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void onClick(View view, String str, int i, ArraySet arraySet) {
                NotificationGutsManager.this.lambda$initializeAppOpsInfo$2$NotificationGutsManager(this.f$1, this.f$2, view, str, i, arraySet);
            }
        };
        if (!expandableNotificationRow.getEntry().mActiveAppOps.isEmpty()) {
            appOpsInfo.bindGuts(packageManagerForUser, r3, sbn, expandableNotificationRow.getEntry().mActiveAppOps);
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$initializeAppOpsInfo$2 */
    public /* synthetic */ void lambda$initializeAppOpsInfo$2$NotificationGutsManager(NotificationGuts notificationGuts, ExpandableNotificationRow expandableNotificationRow, View view, String str, int i, ArraySet arraySet) {
        this.mMetricsLogger.action(1346);
        notificationGuts.resetFalsingCheck();
        startAppOpsSettingsActivity(str, i, arraySet, expandableNotificationRow);
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void initializeNotificationInfo(ExpandableNotificationRow expandableNotificationRow, NotificationInfo notificationInfo) throws Exception {
        $$Lambda$NotificationGutsManager$Q50_8sHdIRaYdx4NmoW9bex_4o r12;
        NotificationGuts guts = expandableNotificationRow.getGuts();
        StatusBarNotification sbn = expandableNotificationRow.getEntry().getSbn();
        String packageName = sbn.getPackageName();
        UserHandle user = sbn.getUser();
        PackageManager packageManagerForUser = StatusBar.getPackageManagerForUser(this.mContext, user.getIdentifier());
        $$Lambda$NotificationGutsManager$5sbilrrQIt_lf8k9ZdwNLnjs r13 = new OnAppSettingsClickListener(guts, sbn, expandableNotificationRow) {
            public final /* synthetic */ NotificationGuts f$1;
            public final /* synthetic */ StatusBarNotification f$2;
            public final /* synthetic */ ExpandableNotificationRow f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void onClick(View view, Intent intent) {
                NotificationGutsManager.this.lambda$initializeNotificationInfo$3$NotificationGutsManager(this.f$1, this.f$2, this.f$3, view, intent);
            }
        };
        if (!user.equals(UserHandle.ALL) || this.mLockscreenUserManager.getCurrentUserId() == 0) {
            $$Lambda$NotificationGutsManager$Q50_8sHdIRaYdx4NmoW9bex_4o r0 = new com.android.systemui.statusbar.notification.row.NotificationInfo.OnSettingsClickListener(guts, sbn, packageName, expandableNotificationRow) {
                public final /* synthetic */ NotificationGuts f$1;
                public final /* synthetic */ StatusBarNotification f$2;
                public final /* synthetic */ String f$3;
                public final /* synthetic */ ExpandableNotificationRow f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void onClick(View view, NotificationChannel notificationChannel, int i) {
                    NotificationGutsManager.this.lambda$initializeNotificationInfo$4$NotificationGutsManager(this.f$1, this.f$2, this.f$3, this.f$4, view, notificationChannel, i);
                }
            };
            r12 = r0;
        } else {
            r12 = null;
        }
        notificationInfo.bindNotification(packageManagerForUser, this.mNotificationManager, this.mVisualStabilityManager, packageName, expandableNotificationRow.getEntry().getChannel(), expandableNotificationRow.getUniqueChannels(), expandableNotificationRow.getEntry(), r12, r13, this.mDeviceProvisionedController.isDeviceProvisioned(), expandableNotificationRow.getIsNonblockable(), this.mHighPriorityProvider.isHighPriority(expandableNotificationRow.getEntry()));
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$initializeNotificationInfo$3 */
    public /* synthetic */ void lambda$initializeNotificationInfo$3$NotificationGutsManager(NotificationGuts notificationGuts, StatusBarNotification statusBarNotification, ExpandableNotificationRow expandableNotificationRow, View view, Intent intent) {
        this.mMetricsLogger.action(206);
        notificationGuts.resetFalsingCheck();
        this.mNotificationActivityStarter.startNotificationGutsIntent(intent, statusBarNotification.getUid(), expandableNotificationRow);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$initializeNotificationInfo$4 */
    public /* synthetic */ void lambda$initializeNotificationInfo$4$NotificationGutsManager(NotificationGuts notificationGuts, StatusBarNotification statusBarNotification, String str, ExpandableNotificationRow expandableNotificationRow, View view, NotificationChannel notificationChannel, int i) {
        this.mMetricsLogger.action(205);
        notificationGuts.resetFalsingCheck();
        this.mOnSettingsClickListener.onSettingsClick(statusBarNotification.getKey());
        startAppNotificationSettingsActivity(str, i, notificationChannel, expandableNotificationRow);
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    public void initializeConversationNotificationInfo(ExpandableNotificationRow expandableNotificationRow, NotificationConversationInfo notificationConversationInfo) throws Exception {
        $$Lambda$NotificationGutsManager$FTSuXAqt9_sMxBGLPWZSSAYCzbM r0;
        NotificationGuts guts = expandableNotificationRow.getGuts();
        StatusBarNotification sbn = expandableNotificationRow.getEntry().getSbn();
        String packageName = sbn.getPackageName();
        UserHandle user = sbn.getUser();
        PackageManager packageManagerForUser = StatusBar.getPackageManagerForUser(this.mContext, user.getIdentifier());
        $$Lambda$NotificationGutsManager$z_qzI7cIFjhU3TtZhUx5iGjftzo r9 = new NotificationConversationInfo.OnAppSettingsClickListener(guts, sbn, expandableNotificationRow) {
            public final /* synthetic */ NotificationGuts f$1;
            public final /* synthetic */ StatusBarNotification f$2;
            public final /* synthetic */ ExpandableNotificationRow f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }
        };
        $$Lambda$NotificationGutsManager$9FsF_zUJ5zlFxqpy3aSUEhBYXvI r14 = new OnSnoozeClickListener(sbn) {
            public final /* synthetic */ StatusBarNotification f$1;

            {
                this.f$1 = r2;
            }

            public final void onClick(View view, int i) {
                NotificationGutsManager.this.mo16044x472083ac(this.f$1, view, i);
            }
        };
        if (!user.equals(UserHandle.ALL) || this.mLockscreenUserManager.getCurrentUserId() == 0) {
            r0 = new com.android.systemui.statusbar.notification.row.NotificationConversationInfo.OnSettingsClickListener(guts, sbn, packageName, expandableNotificationRow, notificationConversationInfo) {
                public final /* synthetic */ NotificationGuts f$1;
                public final /* synthetic */ StatusBarNotification f$2;
                public final /* synthetic */ String f$3;
                public final /* synthetic */ ExpandableNotificationRow f$4;
                public final /* synthetic */ NotificationConversationInfo f$5;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                }

                public final void onClick(View view, NotificationChannel notificationChannel, int i) {
                    NotificationGutsManager.this.mo16045xcb50d0ad(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, view, notificationChannel, i);
                }
            };
        } else {
            r0 = null;
        }
        Context context = this.mContext;
        $$Lambda$NotificationGutsManager$9FsF_zUJ5zlFxqpy3aSUEhBYXvI r3 = r14;
        ConversationIconFactory conversationIconFactory = new ConversationIconFactory(context, this.mLauncherApps, packageManagerForUser, IconDrawableFactory.newInstance(context, false), this.mContext.getResources().getDimensionPixelSize(C2009R$dimen.notification_guts_conversation_icon_size));
        ShortcutManager shortcutManager = this.mShortcutManager;
        LauncherApps launcherApps = this.mLauncherApps;
        INotificationManager iNotificationManager = this.mNotificationManager;
        VisualStabilityManager visualStabilityManager = this.mVisualStabilityManager;
        NotificationChannel channel = expandableNotificationRow.getEntry().getChannel();
        NotificationEntry entry = expandableNotificationRow.getEntry();
        boolean isDeviceProvisioned = this.mDeviceProvisionedController.isDeviceProvisioned();
        notificationConversationInfo.bindNotification(shortcutManager, launcherApps, packageManagerForUser, iNotificationManager, visualStabilityManager, packageName, channel, entry, r0, r9, r3, conversationIconFactory, isDeviceProvisioned);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$initializeConversationNotificationInfo$6 */
    public /* synthetic */ void mo16044x472083ac(StatusBarNotification statusBarNotification, View view, int i) {
        this.mListContainer.getSwipeActionHelper().snooze(statusBarNotification, i);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$initializeConversationNotificationInfo$7 */
    public /* synthetic */ void mo16045xcb50d0ad(NotificationGuts notificationGuts, StatusBarNotification statusBarNotification, String str, ExpandableNotificationRow expandableNotificationRow, NotificationConversationInfo notificationConversationInfo, View view, NotificationChannel notificationChannel, int i) {
        this.mMetricsLogger.action(205);
        notificationGuts.resetFalsingCheck();
        this.mOnSettingsClickListener.onSettingsClick(statusBarNotification.getKey());
        startAppNotificationSettingsActivity(str, i, notificationChannel, expandableNotificationRow);
        notificationConversationInfo.closeControls(view, false);
    }

    public void closeAndSaveGuts(boolean z, boolean z2, boolean z3, int i, int i2, boolean z4) {
        NotificationGuts notificationGuts = this.mNotificationGutsExposed;
        if (notificationGuts != null) {
            notificationGuts.removeCallbacks(this.mOpenRunnable);
            this.mNotificationGutsExposed.closeControls(z, z3, i, i2, z2);
        }
        if (z4) {
            this.mListContainer.resetExposedMenuView(false, true);
        }
    }

    public NotificationGuts getExposedGuts() {
        return this.mNotificationGutsExposed;
    }

    public void setExposedGuts(NotificationGuts notificationGuts) {
        this.mNotificationGutsExposed = notificationGuts;
    }

    public boolean openGuts(View view, int i, int i2, MenuItem menuItem) {
        if (!(menuItem.getGutsView() instanceof NotificationInfo)) {
            return lambda$openGuts$8(view, i, i2, menuItem);
        }
        StatusBarStateController statusBarStateController = this.mStatusBarStateController;
        if (statusBarStateController instanceof StatusBarStateControllerImpl) {
            ((StatusBarStateControllerImpl) statusBarStateController).setLeaveOpenOnKeyguardHide(true);
        }
        $$Lambda$NotificationGutsManager$8gGDBkjiNygwZVxEnvaniT49x6g r3 = new Runnable(view, i, i2, menuItem) {
            public final /* synthetic */ View f$1;
            public final /* synthetic */ int f$2;
            public final /* synthetic */ int f$3;
            public final /* synthetic */ MenuItem f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                NotificationGutsManager.this.lambda$openGuts$9$NotificationGutsManager(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        };
        ((StatusBar) this.mStatusBarLazy.get()).executeRunnableDismissingKeyguard(r3, null, false, true, true);
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$openGuts$9 */
    public /* synthetic */ void lambda$openGuts$9$NotificationGutsManager(View view, int i, int i2, MenuItem menuItem) {
        Handler handler = this.mMainHandler;
        $$Lambda$NotificationGutsManager$ZHwFNbwUTErHx0EtBnhdmWYO2hI r1 = new Runnable(view, i, i2, menuItem) {
            public final /* synthetic */ View f$1;
            public final /* synthetic */ int f$2;
            public final /* synthetic */ int f$3;
            public final /* synthetic */ MenuItem f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                NotificationGutsManager.this.lambda$openGuts$8$NotificationGutsManager(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        };
        handler.post(r1);
    }

    /* access modifiers changed from: 0000 */
    @VisibleForTesting
    /* renamed from: openGutsInternal */
    public boolean lambda$openGuts$8(View view, int i, int i2, MenuItem menuItem) {
        if (!(view instanceof ExpandableNotificationRow)) {
            return false;
        }
        if (view.getWindowToken() == null) {
            Log.e("NotificationGutsManager", "Trying to show notification guts, but not attached to window");
            return false;
        }
        final ExpandableNotificationRow expandableNotificationRow = (ExpandableNotificationRow) view;
        view.performHapticFeedback(0);
        if (expandableNotificationRow.areGutsExposed()) {
            closeAndSaveGuts(false, false, true, -1, -1, true);
            return false;
        }
        expandableNotificationRow.ensureGutsInflated();
        NotificationGuts guts = expandableNotificationRow.getGuts();
        this.mNotificationGutsExposed = guts;
        if (!bindGuts(expandableNotificationRow, menuItem) || guts == null) {
            return false;
        }
        guts.setVisibility(4);
        final NotificationGuts notificationGuts = guts;
        final int i3 = i;
        final int i4 = i2;
        final MenuItem menuItem2 = menuItem;
        C12871 r0 = new Runnable() {
            public void run() {
                if (expandableNotificationRow.getWindowToken() == null) {
                    Log.e("NotificationGutsManager", "Trying to show notification guts in post(), but not attached to window");
                    return;
                }
                notificationGuts.setVisibility(0);
                boolean z = NotificationGutsManager.this.mStatusBarStateController.getState() == 1 && !NotificationGutsManager.this.mAccessibilityManager.isTouchExplorationEnabled();
                NotificationGuts notificationGuts = notificationGuts;
                boolean z2 = !expandableNotificationRow.isBlockingHelperShowing();
                int i = i3;
                int i2 = i4;
                ExpandableNotificationRow expandableNotificationRow = expandableNotificationRow;
                Objects.requireNonNull(expandableNotificationRow);
                notificationGuts.openControls(z2, i, i2, z, new Runnable() {
                    public final void run() {
                        ExpandableNotificationRow.this.onGutsOpened();
                    }
                });
                expandableNotificationRow.closeRemoteInput();
                NotificationGutsManager.this.mListContainer.onHeightChanged(expandableNotificationRow, true);
                NotificationGutsManager.this.mGutsMenuItem = menuItem2;
            }
        };
        this.mOpenRunnable = r0;
        guts.post(r0);
        return true;
    }

    public void setCallback(NotificationSafeToRemoveCallback notificationSafeToRemoveCallback) {
        this.mNotificationLifetimeFinishedCallback = notificationSafeToRemoveCallback;
    }

    public boolean shouldExtendLifetime(NotificationEntry notificationEntry) {
        return (notificationEntry == null || this.mNotificationGutsExposed == null || notificationEntry.getGuts() == null || this.mNotificationGutsExposed != notificationEntry.getGuts() || this.mNotificationGutsExposed.isLeavebehind()) ? false : true;
    }

    public void setShouldManageLifetime(NotificationEntry notificationEntry, boolean z) {
        String str = "NotificationGutsManager";
        if (z) {
            this.mKeyToRemoveOnGutsClosed = notificationEntry.getKey();
            if (Log.isLoggable(str, 3)) {
                StringBuilder sb = new StringBuilder();
                sb.append("Keeping notification because it's showing guts. ");
                sb.append(notificationEntry.getKey());
                Log.d(str, sb.toString());
                return;
            }
            return;
        }
        String str2 = this.mKeyToRemoveOnGutsClosed;
        if (str2 != null && str2.equals(notificationEntry.getKey())) {
            this.mKeyToRemoveOnGutsClosed = null;
            if (Log.isLoggable(str, 3)) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Notification that was kept for guts was updated. ");
                sb2.append(notificationEntry.getKey());
                Log.d(str, sb2.toString());
            }
        }
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        printWriter.println("NotificationGutsManager state:");
        printWriter.print("  mKeyToRemoveOnGutsClosed: ");
        printWriter.println(this.mKeyToRemoveOnGutsClosed);
    }
}
