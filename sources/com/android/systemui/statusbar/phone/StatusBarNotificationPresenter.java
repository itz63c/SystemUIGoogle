package com.android.systemui.statusbar.phone;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.service.notification.StatusBarNotification;
import android.service.vr.IVrManager;
import android.service.vr.IVrStateCallbacks;
import android.service.vr.IVrStateCallbacks.Stub;
import android.util.Slog;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.internal.widget.MessagingGroup;
import com.android.internal.widget.MessagingMessage;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2012R$integer;
import com.android.systemui.C2017R$string;
import com.android.systemui.Dependency;
import com.android.systemui.ForegroundServiceNotificationListener;
import com.android.systemui.InitController;
import com.android.systemui.plugins.ActivityStarter;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.statusbar.CommandQueue;
import com.android.systemui.statusbar.CommandQueue.Callbacks;
import com.android.systemui.statusbar.KeyguardIndicationController;
import com.android.systemui.statusbar.NotificationLockscreenUserManager;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.NotificationPresenter;
import com.android.systemui.statusbar.NotificationRemoteInputManager;
import com.android.systemui.statusbar.NotificationRemoteInputManager.Callback;
import com.android.systemui.statusbar.NotificationViewHierarchyManager;
import com.android.systemui.statusbar.RemoteInputController;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.statusbar.notification.AboveShelfObserver;
import com.android.systemui.statusbar.notification.AboveShelfObserver.HasViewAboveShelfChangedListener;
import com.android.systemui.statusbar.notification.ActivityLaunchAnimator;
import com.android.systemui.statusbar.notification.DynamicPrivacyController;
import com.android.systemui.statusbar.notification.NotificationEntryListener;
import com.android.systemui.statusbar.notification.NotificationEntryManager;
import com.android.systemui.statusbar.notification.VisualStabilityManager;
import com.android.systemui.statusbar.notification.collection.NotificationEntry;
import com.android.systemui.statusbar.notification.collection.inflation.NotificationRowBinderImpl.BindRowCallback;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptStateProvider;
import com.android.systemui.statusbar.notification.interruption.NotificationInterruptSuppressor;
import com.android.systemui.statusbar.notification.row.ActivatableNotificationView;
import com.android.systemui.statusbar.notification.row.ExpandableNotificationRow;
import com.android.systemui.statusbar.notification.row.NotificationGutsManager;
import com.android.systemui.statusbar.notification.row.NotificationGutsManager.OnSettingsClickListener;
import com.android.systemui.statusbar.notification.row.NotificationInfo.CheckSaveListener;
import com.android.systemui.statusbar.notification.stack.NotificationListContainer;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import java.util.List;
import java.util.Objects;
import java.util.function.BooleanSupplier;

public class StatusBarNotificationPresenter implements NotificationPresenter, ConfigurationListener, BindRowCallback, Callbacks {
    private final AboveShelfObserver mAboveShelfObserver;
    /* access modifiers changed from: private */
    public final AccessibilityManager mAccessibilityManager;
    private final ActivityLaunchAnimator mActivityLaunchAnimator;
    private final ActivityStarter mActivityStarter = ((ActivityStarter) Dependency.get(ActivityStarter.class));
    /* access modifiers changed from: private */
    public final IStatusBarService mBarService;
    private final CheckSaveListener mCheckSaveListener = new CheckSaveListener(this) {
    };
    /* access modifiers changed from: private */
    public final CommandQueue mCommandQueue;
    private boolean mDispatchUiModeChangeOnUserSwitched;
    private final DozeScrimController mDozeScrimController;
    private final DynamicPrivacyController mDynamicPrivacyController;
    private final NotificationEntryManager mEntryManager = ((NotificationEntryManager) Dependency.get(NotificationEntryManager.class));
    private final NotificationGutsManager mGutsManager = ((NotificationGutsManager) Dependency.get(NotificationGutsManager.class));
    private final HeadsUpManagerPhone mHeadsUpManager;
    private final NotificationInterruptSuppressor mInterruptSuppressor = new NotificationInterruptSuppressor() {
        public String getName() {
            return "StatusBarNotificationPresenter";
        }

        public boolean suppressAwakeHeadsUp(NotificationEntry notificationEntry) {
            StatusBarNotification sbn = notificationEntry.getSbn();
            if (StatusBarNotificationPresenter.this.mStatusBar.isOccluded()) {
                boolean z = StatusBarNotificationPresenter.this.mLockscreenUserManager.isLockscreenPublicMode(StatusBarNotificationPresenter.this.mLockscreenUserManager.getCurrentUserId()) || StatusBarNotificationPresenter.this.mLockscreenUserManager.isLockscreenPublicMode(sbn.getUserId());
                boolean needsRedaction = StatusBarNotificationPresenter.this.mLockscreenUserManager.needsRedaction(notificationEntry);
                if (z && needsRedaction) {
                    return true;
                }
            }
            if (!StatusBarNotificationPresenter.this.mCommandQueue.panelsEnabled()) {
                return true;
            }
            return sbn.getNotification().fullScreenIntent != null && ((StatusBarNotificationPresenter.this.mKeyguardStateController.isShowing() && !StatusBarNotificationPresenter.this.mStatusBar.isOccluded()) || StatusBarNotificationPresenter.this.mAccessibilityManager.isTouchExplorationEnabled());
        }

        public boolean suppressAwakeInterruptions(NotificationEntry notificationEntry) {
            return StatusBarNotificationPresenter.this.isDeviceInVrMode();
        }

        public boolean suppressInterruptions(NotificationEntry notificationEntry) {
            return StatusBarNotificationPresenter.this.mStatusBar.areNotificationAlertsDisabled();
        }
    };
    private final KeyguardIndicationController mKeyguardIndicationController;
    /* access modifiers changed from: private */
    public final KeyguardStateController mKeyguardStateController;
    private final LockscreenGestureLogger mLockscreenGestureLogger = ((LockscreenGestureLogger) Dependency.get(LockscreenGestureLogger.class));
    /* access modifiers changed from: private */
    public final NotificationLockscreenUserManager mLockscreenUserManager = ((NotificationLockscreenUserManager) Dependency.get(NotificationLockscreenUserManager.class));
    private final int mMaxAllowedKeyguardNotifications;
    private int mMaxKeyguardNotifications;
    private final NotificationMediaManager mMediaManager = ((NotificationMediaManager) Dependency.get(NotificationMediaManager.class));
    private final NotificationPanelViewController mNotificationPanel;
    private final OnSettingsClickListener mOnSettingsClickListener = new OnSettingsClickListener() {
        public void onSettingsClick(String str) {
            try {
                StatusBarNotificationPresenter.this.mBarService.onNotificationSettingsViewed(str);
            } catch (RemoteException unused) {
            }
        }
    };
    private boolean mReinflateNotificationsOnUserSwitched;
    private final ScrimController mScrimController;
    private final ShadeController mShadeController;
    /* access modifiers changed from: private */
    public final StatusBar mStatusBar;
    private final SysuiStatusBarStateController mStatusBarStateController = ((SysuiStatusBarStateController) Dependency.get(StatusBarStateController.class));
    private final NotificationViewHierarchyManager mViewHierarchyManager = ((NotificationViewHierarchyManager) Dependency.get(NotificationViewHierarchyManager.class));
    private final VisualStabilityManager mVisualStabilityManager = ((VisualStabilityManager) Dependency.get(VisualStabilityManager.class));
    protected boolean mVrMode;
    private final IVrStateCallbacks mVrStateCallbacks = new Stub() {
        public void onVrStateChanged(boolean z) {
            StatusBarNotificationPresenter.this.mVrMode = z;
        }
    };

    static /* synthetic */ boolean lambda$onExpandClicked$1() {
        return false;
    }

    public StatusBarNotificationPresenter(Context context, NotificationPanelViewController notificationPanelViewController, HeadsUpManagerPhone headsUpManagerPhone, NotificationShadeWindowView notificationShadeWindowView, ViewGroup viewGroup, DozeScrimController dozeScrimController, ScrimController scrimController, ActivityLaunchAnimator activityLaunchAnimator, DynamicPrivacyController dynamicPrivacyController, KeyguardStateController keyguardStateController, KeyguardIndicationController keyguardIndicationController, StatusBar statusBar, ShadeController shadeController, CommandQueue commandQueue, InitController initController, NotificationInterruptStateProvider notificationInterruptStateProvider) {
        Context context2 = context;
        ViewGroup viewGroup2 = viewGroup;
        this.mKeyguardStateController = keyguardStateController;
        this.mNotificationPanel = notificationPanelViewController;
        this.mHeadsUpManager = headsUpManagerPhone;
        this.mDynamicPrivacyController = dynamicPrivacyController;
        this.mKeyguardIndicationController = keyguardIndicationController;
        this.mStatusBar = statusBar;
        this.mShadeController = shadeController;
        this.mCommandQueue = commandQueue;
        AboveShelfObserver aboveShelfObserver = new AboveShelfObserver(viewGroup);
        this.mAboveShelfObserver = aboveShelfObserver;
        this.mActivityLaunchAnimator = activityLaunchAnimator;
        NotificationShadeWindowView notificationShadeWindowView2 = notificationShadeWindowView;
        aboveShelfObserver.setListener((HasViewAboveShelfChangedListener) notificationShadeWindowView.findViewById(C2011R$id.notification_container_parent));
        this.mAccessibilityManager = (AccessibilityManager) context.getSystemService(AccessibilityManager.class);
        this.mDozeScrimController = dozeScrimController;
        this.mScrimController = scrimController;
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(KeyguardManager.class);
        this.mMaxAllowedKeyguardNotifications = context.getResources().getInteger(C2012R$integer.keyguard_max_notification_count);
        this.mBarService = IStatusBarService.Stub.asInterface(ServiceManager.getService("statusbar"));
        IVrManager asInterface = IVrManager.Stub.asInterface(ServiceManager.getService("vrmanager"));
        if (asInterface != null) {
            try {
                asInterface.registerListener(this.mVrStateCallbacks);
            } catch (RemoteException e) {
                StringBuilder sb = new StringBuilder();
                sb.append("Failed to register VR mode state listener: ");
                sb.append(e);
                Slog.e("StatusBarNotificationPresenter", sb.toString());
            }
        }
        NotificationRemoteInputManager notificationRemoteInputManager = (NotificationRemoteInputManager) Dependency.get(NotificationRemoteInputManager.class);
        notificationRemoteInputManager.setUpWithCallback((Callback) Dependency.get(Callback.class), this.mNotificationPanel.createRemoteInputDelegate());
        notificationRemoteInputManager.getController().addCallback((RemoteInputController.Callback) Dependency.get(NotificationShadeWindowController.class));
        initController.addPostInitTask(new Runnable((NotificationListContainer) viewGroup2, notificationRemoteInputManager, notificationInterruptStateProvider) {
            public final /* synthetic */ NotificationListContainer f$1;
            public final /* synthetic */ NotificationRemoteInputManager f$2;
            public final /* synthetic */ NotificationInterruptStateProvider f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                StatusBarNotificationPresenter.this.lambda$new$0$StatusBarNotificationPresenter(this.f$1, this.f$2, this.f$3);
            }
        });
        ((ConfigurationController) Dependency.get(ConfigurationController.class)).addCallback(this);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$StatusBarNotificationPresenter(NotificationListContainer notificationListContainer, NotificationRemoteInputManager notificationRemoteInputManager, NotificationInterruptStateProvider notificationInterruptStateProvider) {
        C16131 r0 = new NotificationEntryListener() {
            public void onEntryRemoved(NotificationEntry notificationEntry, NotificationVisibility notificationVisibility, boolean z, int i) {
                StatusBarNotificationPresenter.this.onNotificationRemoved(notificationEntry.getKey(), notificationEntry.getSbn());
                if (z) {
                    StatusBarNotificationPresenter.this.maybeEndAmbientPulse();
                }
            }
        };
        this.mViewHierarchyManager.setUpWithPresenter(this, notificationListContainer);
        this.mEntryManager.setUpWithPresenter(this);
        this.mEntryManager.addNotificationEntryListener(r0);
        this.mEntryManager.addNotificationLifetimeExtender(this.mHeadsUpManager);
        this.mEntryManager.addNotificationLifetimeExtender(this.mGutsManager);
        this.mEntryManager.addNotificationLifetimeExtenders(notificationRemoteInputManager.getLifetimeExtenders());
        notificationInterruptStateProvider.addSuppressor(this.mInterruptSuppressor);
        this.mLockscreenUserManager.setUpWithPresenter(this);
        this.mMediaManager.setUpWithPresenter(this);
        this.mVisualStabilityManager.setUpWithPresenter(this);
        this.mGutsManager.setUpWithPresenter(this, notificationListContainer, this.mCheckSaveListener, this.mOnSettingsClickListener);
        Dependency.get(ForegroundServiceNotificationListener.class);
        onUserSwitched(this.mLockscreenUserManager.getCurrentUserId());
    }

    public void onDensityOrFontScaleChanged() {
        MessagingMessage.dropCache();
        MessagingGroup.dropCache();
        if (!((KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class)).isSwitchingUser()) {
            updateNotificationsOnDensityOrFontScaleChanged();
        } else {
            this.mReinflateNotificationsOnUserSwitched = true;
        }
    }

    public void onUiModeChanged() {
        if (!((KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class)).isSwitchingUser()) {
            updateNotificationOnUiModeChanged();
        } else {
            this.mDispatchUiModeChangeOnUserSwitched = true;
        }
    }

    public void onOverlayChanged() {
        onDensityOrFontScaleChanged();
    }

    private void updateNotificationOnUiModeChanged() {
        List activeNotificationsForCurrentUser = this.mEntryManager.getActiveNotificationsForCurrentUser();
        for (int i = 0; i < activeNotificationsForCurrentUser.size(); i++) {
            ExpandableNotificationRow row = ((NotificationEntry) activeNotificationsForCurrentUser.get(i)).getRow();
            if (row != null) {
                row.onUiModeChanged();
            }
        }
    }

    private void updateNotificationsOnDensityOrFontScaleChanged() {
        List activeNotificationsForCurrentUser = this.mEntryManager.getActiveNotificationsForCurrentUser();
        for (int i = 0; i < activeNotificationsForCurrentUser.size(); i++) {
            NotificationEntry notificationEntry = (NotificationEntry) activeNotificationsForCurrentUser.get(i);
            notificationEntry.onDensityOrFontScaleChanged();
            if (notificationEntry.areGutsExposed()) {
                this.mGutsManager.onDensityOrFontScaleChanged(notificationEntry);
            }
        }
    }

    public boolean isCollapsing() {
        return this.mNotificationPanel.isCollapsing() || this.mActivityLaunchAnimator.isAnimationPending() || this.mActivityLaunchAnimator.isAnimationRunning();
    }

    /* access modifiers changed from: private */
    public void maybeEndAmbientPulse() {
        if (this.mNotificationPanel.hasPulsingNotifications() && !this.mHeadsUpManager.hasNotifications()) {
            this.mDozeScrimController.pulseOutNow();
        }
    }

    public void updateNotificationViews() {
        if (this.mScrimController != null) {
            if (isCollapsing()) {
                this.mShadeController.addPostCollapseAction(new Runnable() {
                    public final void run() {
                        StatusBarNotificationPresenter.this.updateNotificationViews();
                    }
                });
                return;
            }
            this.mViewHierarchyManager.updateNotificationViews();
            this.mNotificationPanel.updateNotificationViews();
        }
    }

    public void onNotificationRemoved(String str, StatusBarNotification statusBarNotification) {
        if (statusBarNotification != null && !hasActiveNotifications() && !this.mNotificationPanel.isTracking() && !this.mNotificationPanel.isQsExpanded()) {
            if (this.mStatusBarStateController.getState() == 0) {
                this.mCommandQueue.animateCollapsePanels();
            } else if (this.mStatusBarStateController.getState() == 2 && !isCollapsing()) {
                this.mStatusBarStateController.setState(1);
            }
        }
    }

    public boolean hasActiveNotifications() {
        return this.mEntryManager.hasActiveNotifications();
    }

    public void onUserSwitched(int i) {
        this.mHeadsUpManager.setUser(i);
        this.mCommandQueue.animateCollapsePanels();
        if (this.mReinflateNotificationsOnUserSwitched) {
            updateNotificationsOnDensityOrFontScaleChanged();
            this.mReinflateNotificationsOnUserSwitched = false;
        }
        if (this.mDispatchUiModeChangeOnUserSwitched) {
            updateNotificationOnUiModeChanged();
            this.mDispatchUiModeChangeOnUserSwitched = false;
        }
        updateNotificationViews();
        this.mMediaManager.clearCurrentMediaNotification();
        this.mStatusBar.setLockscreenUser(i);
        updateMediaMetaData(true, false);
    }

    public void onBindRow(NotificationEntry notificationEntry, PackageManager packageManager, StatusBarNotification statusBarNotification, ExpandableNotificationRow expandableNotificationRow) {
        expandableNotificationRow.setAboveShelfChangedListener(this.mAboveShelfObserver);
        KeyguardStateController keyguardStateController = this.mKeyguardStateController;
        Objects.requireNonNull(keyguardStateController);
        expandableNotificationRow.setSecureStateProvider(new BooleanSupplier() {
            public final boolean getAsBoolean() {
                return KeyguardStateController.this.canDismissLockScreen();
            }
        });
    }

    public boolean isPresenterFullyCollapsed() {
        return this.mNotificationPanel.isFullyCollapsed();
    }

    public void onActivated(ActivatableNotificationView activatableNotificationView) {
        onActivated();
        if (activatableNotificationView != null) {
            this.mNotificationPanel.setActivatedChild(activatableNotificationView);
        }
    }

    public void onActivated() {
        this.mLockscreenGestureLogger.write(192, 0, 0);
        this.mNotificationPanel.showTransientIndication(C2017R$string.notification_tap_again);
        ActivatableNotificationView activatedChild = this.mNotificationPanel.getActivatedChild();
        if (activatedChild != null) {
            activatedChild.makeInactive(true);
        }
    }

    public void onActivationReset(ActivatableNotificationView activatableNotificationView) {
        if (activatableNotificationView == this.mNotificationPanel.getActivatedChild()) {
            this.mNotificationPanel.setActivatedChild(null);
            this.mKeyguardIndicationController.hideTransientIndication();
        }
    }

    public void updateMediaMetaData(boolean z, boolean z2) {
        this.mMediaManager.updateMediaMetaData(z, z2);
    }

    public int getMaxNotificationsWhileLocked(boolean z) {
        if (!z) {
            return this.mMaxKeyguardNotifications;
        }
        int max = Math.max(1, this.mNotificationPanel.computeMaxKeyguardNotifications(this.mMaxAllowedKeyguardNotifications));
        this.mMaxKeyguardNotifications = max;
        return max;
    }

    public void onUpdateRowStates() {
        this.mNotificationPanel.onUpdateRowStates();
    }

    public void onExpandClicked(NotificationEntry notificationEntry, boolean z) {
        this.mHeadsUpManager.setExpanded(notificationEntry, z);
        if (!z) {
            return;
        }
        if (this.mStatusBarStateController.getState() == 1) {
            this.mShadeController.goToLockedShade(notificationEntry.getRow());
        } else if (notificationEntry.isSensitive() && this.mDynamicPrivacyController.isInLockedDownShade()) {
            this.mStatusBarStateController.setLeaveOpenOnKeyguardHide(true);
            this.mActivityStarter.dismissKeyguardThenExecute(C1429xc0ee9f6.INSTANCE, null, false);
        }
    }

    public boolean isDeviceInVrMode() {
        return this.mVrMode;
    }
}
