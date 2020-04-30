package com.android.systemui.statusbar.notification.row;

import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.PluginListener;
import com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin;
import com.android.systemui.plugins.statusbar.NotificationMenuRowPlugin.MenuItem;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.shared.plugins.PluginManager;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.notification.logging.NotificationLogger;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow.ExpansionLogger;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow.LongPressListener;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow.OnAppOpsClickListener;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow.OnExpandClickListener;
import com.android.systemui.statusbar.notification.row.NotificationRowContentBinder.InflationCallback;
import com.android.systemui.statusbar.phone.KeyguardBypassController;
import com.android.systemui.statusbar.phone.NotificationGroupManager;
import com.android.systemui.statusbar.policy.HeadsUpManager;
import com.android.systemui.util.time.SystemClock;
import java.util.Objects;

public class ExpandableNotificationRowController {
    private final ActivatableNotificationViewController mActivatableNotificationViewController;
    private final boolean mAllowLongPress;
    private final String mAppName;
    /* access modifiers changed from: private */
    public final SystemClock mClock;
    private final ExpansionLogger mExpansionLogger = new ExpansionLogger() {
        public final void logNotificationExpansion(String str, boolean z, boolean z2) {
            ExpandableNotificationRowController.this.logNotificationExpansion(str, z, z2);
        }
    };
    private final FalsingManager mFalsingManager;
    private final HeadsUpManager mHeadsUpManager;
    private final KeyguardBypassController mKeyguardBypassController;
    private final NotificationMediaManager mMediaManager;
    private final NotificationGroupManager mNotificationGroupManager;
    private final NotificationGutsManager mNotificationGutsManager;
    private final String mNotificationKey;
    private final NotificationLogger mNotificationLogger;
    private final OnAppOpsClickListener mOnAppOpsClickListener;
    private Runnable mOnDismissRunnable;
    private final OnExpandClickListener mOnExpandClickListener;
    /* access modifiers changed from: private */
    public final PluginManager mPluginManager;
    private final RowContentBindStage mRowContentBindStage;
    private final StatusBarStateController mStatusBarStateController;
    /* access modifiers changed from: private */
    public final ExpandableNotificationRow mView;

    public ExpandableNotificationRowController(ExpandableNotificationRow expandableNotificationRow, ActivatableNotificationViewController activatableNotificationViewController, NotificationMediaManager notificationMediaManager, PluginManager pluginManager, SystemClock systemClock, String str, String str2, KeyguardBypassController keyguardBypassController, NotificationGroupManager notificationGroupManager, RowContentBindStage rowContentBindStage, NotificationLogger notificationLogger, HeadsUpManager headsUpManager, OnExpandClickListener onExpandClickListener, StatusBarStateController statusBarStateController, InflationCallback inflationCallback, NotificationGutsManager notificationGutsManager, boolean z, Runnable runnable, FalsingManager falsingManager) {
        NotificationGutsManager notificationGutsManager2 = notificationGutsManager;
        this.mView = expandableNotificationRow;
        this.mActivatableNotificationViewController = activatableNotificationViewController;
        this.mMediaManager = notificationMediaManager;
        this.mPluginManager = pluginManager;
        this.mClock = systemClock;
        this.mAppName = str;
        this.mNotificationKey = str2;
        this.mKeyguardBypassController = keyguardBypassController;
        this.mNotificationGroupManager = notificationGroupManager;
        this.mRowContentBindStage = rowContentBindStage;
        this.mNotificationLogger = notificationLogger;
        this.mHeadsUpManager = headsUpManager;
        this.mOnExpandClickListener = onExpandClickListener;
        this.mStatusBarStateController = statusBarStateController;
        this.mNotificationGutsManager = notificationGutsManager2;
        this.mOnDismissRunnable = runnable;
        Objects.requireNonNull(notificationGutsManager);
        this.mOnAppOpsClickListener = new OnAppOpsClickListener() {
            public final boolean onClick(View view, int i, int i2, MenuItem menuItem) {
                return NotificationGutsManager.this.openGuts(view, i, i2, menuItem);
            }
        };
        this.mAllowLongPress = z;
        this.mFalsingManager = falsingManager;
    }

    public void init() {
        this.mActivatableNotificationViewController.init();
        this.mView.initialize(this.mAppName, this.mNotificationKey, this.mExpansionLogger, this.mKeyguardBypassController, this.mNotificationGroupManager, this.mHeadsUpManager, this.mRowContentBindStage, this.mOnExpandClickListener, this.mMediaManager, this.mOnAppOpsClickListener, this.mFalsingManager, this.mStatusBarStateController);
        this.mView.setOnDismissRunnable(this.mOnDismissRunnable);
        this.mView.setDescendantFocusability(393216);
        if (this.mAllowLongPress) {
            ExpandableNotificationRow expandableNotificationRow = this.mView;
            NotificationGutsManager notificationGutsManager = this.mNotificationGutsManager;
            Objects.requireNonNull(notificationGutsManager);
            expandableNotificationRow.setLongPressListener(new LongPressListener() {
                public final boolean onLongPress(View view, int i, int i2, MenuItem menuItem) {
                    return NotificationGutsManager.this.openGuts(view, i, i2, menuItem);
                }
            });
        }
        if (NotificationRemoteInputManager.ENABLE_REMOTE_INPUT) {
            this.mView.setDescendantFocusability(131072);
        }
        this.mView.addOnAttachStateChangeListener(new OnAttachStateChangeListener() {
            public void onViewAttachedToWindow(View view) {
                ExpandableNotificationRowController.this.mView.getEntry().setInitializationTime(ExpandableNotificationRowController.this.mClock.elapsedRealtime());
                ExpandableNotificationRowController.this.mPluginManager.addPluginListener((PluginListener<T>) ExpandableNotificationRowController.this.mView, NotificationMenuRowPlugin.class, false);
            }

            public void onViewDetachedFromWindow(View view) {
                ExpandableNotificationRowController.this.mPluginManager.removePluginListener(ExpandableNotificationRowController.this.mView);
            }
        });
    }

    /* access modifiers changed from: private */
    public void logNotificationExpansion(String str, boolean z, boolean z2) {
        this.mNotificationLogger.onExpansionChanged(str, z, z2);
    }

    public void setOnDismissRunnable(Runnable runnable) {
        this.mOnDismissRunnable = runnable;
        this.mView.setOnDismissRunnable(runnable);
    }
}
