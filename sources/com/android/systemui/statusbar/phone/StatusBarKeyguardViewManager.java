package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewRootImpl;
import android.view.WindowInsets.Type;
import android.view.WindowManagerGlobal;
import androidx.appcompat.R$styleable;
import com.android.internal.util.LatencyTracker;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.keyguard.KeyguardViewController;
import com.android.keyguard.ViewMediatorCallback;
import com.android.systemui.DejankUtils;
import com.android.systemui.SystemUIFactory;
import com.android.systemui.dock.DockManager;
import com.android.systemui.dock.DockManager.DockEventListener;
import com.android.systemui.keyguard.DismissCallbackRegistry;
import com.android.systemui.plugins.ActivityStarter.OnDismissAction;
import com.android.systemui.plugins.FalsingManager;
import com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener;
import com.android.systemui.shared.system.QuickStepContract;
import com.android.systemui.shared.system.SysUiStatsLog;
import com.android.systemui.statusbar.CrossFadeHelper;
import com.android.systemui.statusbar.NotificationMediaManager;
import com.android.systemui.statusbar.RemoteInputController.Callback;
import com.android.systemui.statusbar.SysuiStatusBarStateController;
import com.android.systemui.statusbar.notification.ViewGroupFadeHelper;
import com.android.systemui.statusbar.phone.KeyguardBouncer.BouncerExpansionCallback;
import com.android.systemui.statusbar.phone.NavigationModeController.ModeChangedListener;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import java.io.PrintWriter;
import java.util.ArrayList;

public class StatusBarKeyguardViewManager implements Callback, StateListener, ConfigurationListener, PanelExpansionListener, ModeChangedListener, KeyguardViewController {
    private OnDismissAction mAfterKeyguardGoneAction;
    private final ArrayList<Runnable> mAfterKeyguardGoneRunnables = new ArrayList<>();
    private BiometricUnlockController mBiometricUnlockController;
    protected KeyguardBouncer mBouncer;
    private KeyguardBypassController mBypassController;
    private final ConfigurationController mConfigurationController;
    /* access modifiers changed from: private */
    public ViewGroup mContainer;
    protected final Context mContext;
    private final DockEventListener mDockEventListener = new DockEventListener() {
        public void onEvent(int i) {
            boolean isDocked = StatusBarKeyguardViewManager.this.mDockManager.isDocked();
            if (isDocked != StatusBarKeyguardViewManager.this.mIsDocked) {
                StatusBarKeyguardViewManager.this.mIsDocked = isDocked;
                StatusBarKeyguardViewManager.this.updateStates();
            }
        }
    };
    /* access modifiers changed from: private */
    public final DockManager mDockManager;
    private boolean mDozing;
    private final BouncerExpansionCallback mExpansionCallback = new BouncerExpansionCallback() {
        public void onFullyShown() {
            StatusBarKeyguardViewManager.this.updateStates();
            StatusBarKeyguardViewManager.this.mStatusBar.wakeUpIfDozing(SystemClock.uptimeMillis(), StatusBarKeyguardViewManager.this.mContainer, "BOUNCER_VISIBLE");
            StatusBarKeyguardViewManager.this.updateLockIcon();
        }

        public void onStartingToHide() {
            StatusBarKeyguardViewManager.this.updateStates();
        }

        public void onStartingToShow() {
            StatusBarKeyguardViewManager.this.updateLockIcon();
        }

        public void onFullyHidden() {
            StatusBarKeyguardViewManager.this.updateStates();
            StatusBarKeyguardViewManager.this.updateLockIcon();
        }
    };
    protected boolean mFirstUpdate = true;
    private boolean mGesturalNav;
    private boolean mGoingToSleepVisibleNotOccluded;
    /* access modifiers changed from: private */
    public boolean mIsDocked;
    private Runnable mKeyguardGoneCancelAction;
    private final KeyguardStateController mKeyguardStateController;
    private final KeyguardUpdateMonitor mKeyguardUpdateManager;
    private int mLastBiometricMode;
    private boolean mLastBouncerDismissible;
    private boolean mLastBouncerShowing;
    private boolean mLastDozing;
    private boolean mLastGesturalNav;
    private boolean mLastIsDocked;
    private boolean mLastLockVisible;
    protected boolean mLastOccluded;
    private boolean mLastPulsing;
    protected boolean mLastRemoteInputActive;
    protected boolean mLastShowing;
    private ViewGroup mLockIconContainer;
    protected LockPatternUtils mLockPatternUtils;
    private Runnable mMakeNavigationBarVisibleRunnable = new Runnable() {
        public void run() {
            if (ViewRootImpl.sNewInsetsMode == 2) {
                StatusBarKeyguardViewManager.this.mStatusBar.getNotificationShadeWindowView().getWindowInsetsController().show(Type.navigationBars());
            } else {
                StatusBarKeyguardViewManager.this.mStatusBar.getNavigationBarView().getRootView().setVisibility(0);
            }
        }
    };
    private final NotificationMediaManager mMediaManager;
    private final NavigationModeController mNavigationModeController;
    private View mNotificationContainer;
    private NotificationPanelViewController mNotificationPanelViewController;
    /* access modifiers changed from: private */
    public final NotificationShadeWindowController mNotificationShadeWindowController;
    protected boolean mOccluded;
    private DismissWithActionRequest mPendingWakeupAction;
    private boolean mPulsing;
    protected boolean mRemoteInputActive;
    protected boolean mShowing;
    protected StatusBar mStatusBar;
    private final SysuiStatusBarStateController mStatusBarStateController;
    private final KeyguardUpdateMonitorCallback mUpdateMonitorCallback = new KeyguardUpdateMonitorCallback() {
        public void onEmergencyCallAction() {
            StatusBarKeyguardViewManager statusBarKeyguardViewManager = StatusBarKeyguardViewManager.this;
            if (statusBarKeyguardViewManager.mOccluded) {
                statusBarKeyguardViewManager.reset(true);
            }
        }
    };
    protected ViewMediatorCallback mViewMediatorCallback;

    private static class DismissWithActionRequest {
        final boolean afterKeyguardGone;
        final Runnable cancelAction;
        final OnDismissAction dismissAction;
        final String message;

        DismissWithActionRequest(OnDismissAction onDismissAction, Runnable runnable, boolean z, String str) {
            this.dismissAction = onDismissAction;
            this.cancelAction = runnable;
            this.afterKeyguardGone = z;
            this.message = str;
        }
    }

    public void onCancelClicked() {
    }

    public void onScreenTurnedOn() {
    }

    public void onScreenTurningOn() {
    }

    public void onStartedWakingUp() {
    }

    /* access modifiers changed from: protected */
    public boolean shouldDestroyViewOnReset() {
        return false;
    }

    public StatusBarKeyguardViewManager(Context context, ViewMediatorCallback viewMediatorCallback, LockPatternUtils lockPatternUtils, SysuiStatusBarStateController sysuiStatusBarStateController, ConfigurationController configurationController, KeyguardUpdateMonitor keyguardUpdateMonitor, NavigationModeController navigationModeController, DockManager dockManager, NotificationShadeWindowController notificationShadeWindowController, KeyguardStateController keyguardStateController, NotificationMediaManager notificationMediaManager) {
        this.mContext = context;
        this.mViewMediatorCallback = viewMediatorCallback;
        this.mLockPatternUtils = lockPatternUtils;
        this.mConfigurationController = configurationController;
        this.mNavigationModeController = navigationModeController;
        this.mNotificationShadeWindowController = notificationShadeWindowController;
        this.mKeyguardStateController = keyguardStateController;
        this.mMediaManager = notificationMediaManager;
        this.mKeyguardUpdateManager = keyguardUpdateMonitor;
        this.mStatusBarStateController = sysuiStatusBarStateController;
        this.mDockManager = dockManager;
    }

    public void registerStatusBar(StatusBar statusBar, ViewGroup viewGroup, NotificationPanelViewController notificationPanelViewController, BiometricUnlockController biometricUnlockController, DismissCallbackRegistry dismissCallbackRegistry, ViewGroup viewGroup2, View view, KeyguardBypassController keyguardBypassController, FalsingManager falsingManager) {
        NotificationPanelViewController notificationPanelViewController2 = notificationPanelViewController;
        ViewGroup viewGroup3 = viewGroup2;
        this.mStatusBar = statusBar;
        this.mContainer = viewGroup;
        this.mLockIconContainer = viewGroup3;
        if (viewGroup3 != null) {
            this.mLastLockVisible = viewGroup2.getVisibility() == 0;
        }
        this.mBiometricUnlockController = biometricUnlockController;
        this.mBouncer = SystemUIFactory.getInstance().createKeyguardBouncer(this.mContext, this.mViewMediatorCallback, this.mLockPatternUtils, viewGroup, dismissCallbackRegistry, this.mExpansionCallback, this.mKeyguardStateController, falsingManager, keyguardBypassController);
        this.mNotificationPanelViewController = notificationPanelViewController2;
        notificationPanelViewController.addExpansionListener(this);
        this.mBypassController = keyguardBypassController;
        this.mNotificationContainer = view;
        registerListeners();
    }

    private void registerListeners() {
        this.mKeyguardUpdateManager.registerCallback(this.mUpdateMonitorCallback);
        this.mStatusBarStateController.addCallback(this);
        this.mConfigurationController.addCallback(this);
        this.mGesturalNav = QuickStepContract.isGesturalMode(this.mNavigationModeController.addListener(this));
        DockManager dockManager = this.mDockManager;
        if (dockManager != null) {
            dockManager.addListener(this.mDockEventListener);
            this.mIsDocked = this.mDockManager.isDocked();
        }
    }

    public void onPanelExpansionChanged(float f, boolean z) {
        if (this.mNotificationPanelViewController.isUnlockHintRunning()) {
            this.mBouncer.setExpansion(1.0f);
        } else if (bouncerNeedsScrimming()) {
            this.mBouncer.setExpansion(0.0f);
        } else if (this.mShowing) {
            if (!isWakeAndUnlocking() && !this.mStatusBar.isInLaunchTransition()) {
                this.mBouncer.setExpansion(f);
            }
            if (f != 1.0f && z && !this.mKeyguardStateController.canDismissLockScreen() && !this.mBouncer.isShowing() && !this.mBouncer.isAnimatingAway()) {
                this.mBouncer.show(false, false);
            }
        } else if (this.mPulsing && f == 0.0f) {
            this.mStatusBar.wakeUpIfDozing(SystemClock.uptimeMillis(), this.mContainer, "BOUNCER_VISIBLE");
        }
    }

    public void onQsExpansionChanged(float f) {
        updateLockIcon();
    }

    /* access modifiers changed from: private */
    public void updateLockIcon() {
        long j;
        if (this.mLockIconContainer != null) {
            boolean z = true;
            int i = 0;
            boolean z2 = this.mStatusBarStateController.getState() == 1 && !this.mNotificationPanelViewController.isQsExpanded();
            if ((!this.mBouncer.isShowing() && !z2) || this.mBouncer.isAnimatingAway() || this.mKeyguardStateController.isKeyguardFadingAway()) {
                z = false;
            }
            if (this.mLastLockVisible != z) {
                this.mLastLockVisible = z;
                if (z) {
                    CrossFadeHelper.fadeIn((View) this.mLockIconContainer, 220, 0);
                } else {
                    if (needsBypassFading()) {
                        j = 67;
                    } else {
                        j = 110;
                        i = R$styleable.AppCompatTheme_windowFixedWidthMajor;
                    }
                    CrossFadeHelper.fadeOut(this.mLockIconContainer, j, i, null);
                }
            }
        }
    }

    public void show(Bundle bundle) {
        this.mShowing = true;
        this.mNotificationShadeWindowController.setKeyguardShowing(true);
        KeyguardStateController keyguardStateController = this.mKeyguardStateController;
        keyguardStateController.notifyKeyguardState(this.mShowing, keyguardStateController.isOccluded());
        reset(true);
        SysUiStatsLog.write(62, 2);
    }

    /* access modifiers changed from: protected */
    public void showBouncerOrKeyguard(boolean z) {
        if (!this.mBouncer.needsFullscreenBouncer() || this.mDozing) {
            this.mStatusBar.showKeyguard();
            if (z) {
                hideBouncer(shouldDestroyViewOnReset());
                this.mBouncer.prepare();
            }
        } else {
            this.mStatusBar.hideKeyguard();
            this.mBouncer.show(true);
        }
        updateStates();
    }

    /* access modifiers changed from: 0000 */
    public void hideBouncer(boolean z) {
        if (this.mBouncer != null) {
            if (this.mShowing) {
                this.mAfterKeyguardGoneAction = null;
                Runnable runnable = this.mKeyguardGoneCancelAction;
                if (runnable != null) {
                    runnable.run();
                    this.mKeyguardGoneCancelAction = null;
                }
            }
            this.mBouncer.hide(z);
            cancelPendingWakeupAction();
        }
    }

    public void showBouncer(boolean z) {
        if (this.mShowing && !this.mBouncer.isShowing()) {
            this.mBouncer.show(false, z);
        }
        updateStates();
    }

    public void dismissWithAction(OnDismissAction onDismissAction, Runnable runnable, boolean z) {
        dismissWithAction(onDismissAction, runnable, z, null);
    }

    public void dismissWithAction(OnDismissAction onDismissAction, Runnable runnable, boolean z, String str) {
        if (this.mShowing) {
            cancelPendingWakeupAction();
            if (this.mDozing && !isWakeAndUnlocking()) {
                this.mPendingWakeupAction = new DismissWithActionRequest(onDismissAction, runnable, z, str);
                return;
            } else if (!z) {
                this.mBouncer.showWithDismissAction(onDismissAction, runnable);
            } else {
                this.mAfterKeyguardGoneAction = onDismissAction;
                this.mKeyguardGoneCancelAction = runnable;
                this.mBouncer.show(false);
            }
        }
        updateStates();
    }

    private boolean isWakeAndUnlocking() {
        int mode = this.mBiometricUnlockController.getMode();
        return mode == 1 || mode == 2;
    }

    public void addAfterKeyguardGoneRunnable(Runnable runnable) {
        this.mAfterKeyguardGoneRunnables.add(runnable);
    }

    public void reset(boolean z) {
        if (this.mShowing) {
            if (!this.mOccluded || this.mDozing) {
                showBouncerOrKeyguard(z);
            } else {
                this.mStatusBar.hideKeyguard();
                if (z || this.mBouncer.needsFullscreenBouncer()) {
                    hideBouncer(false);
                }
            }
            this.mKeyguardUpdateManager.sendKeyguardReset();
            updateStates();
        }
    }

    public boolean isGoingToSleepVisibleNotOccluded() {
        return this.mGoingToSleepVisibleNotOccluded;
    }

    public void onStartedGoingToSleep() {
        this.mGoingToSleepVisibleNotOccluded = isShowing() && !isOccluded();
    }

    public void onFinishedGoingToSleep() {
        this.mGoingToSleepVisibleNotOccluded = false;
        this.mBouncer.onScreenTurnedOff();
    }

    public void onRemoteInputActive(boolean z) {
        this.mRemoteInputActive = z;
        updateStates();
    }

    private void setDozing(boolean z) {
        if (this.mDozing != z) {
            this.mDozing = z;
            if (z || this.mBouncer.needsFullscreenBouncer() || this.mOccluded) {
                reset(z);
            }
            updateStates();
            if (!z) {
                launchPendingWakeupAction();
            }
        }
    }

    public void setPulsing(boolean z) {
        if (this.mPulsing != z) {
            this.mPulsing = z;
            updateStates();
        }
    }

    public void setNeedsInput(boolean z) {
        this.mNotificationShadeWindowController.setKeyguardNeedsInput(z);
    }

    public boolean isUnlockWithWallpaper() {
        return this.mNotificationShadeWindowController.isShowingWallpaper();
    }

    public void setOccluded(boolean z, boolean z2) {
        this.mStatusBar.setOccluded(z);
        boolean z3 = true;
        if (z && !this.mOccluded && this.mShowing) {
            SysUiStatsLog.write(62, 3);
            if (this.mStatusBar.isInLaunchTransition()) {
                this.mOccluded = true;
                this.mStatusBar.fadeKeyguardAfterLaunchTransition(null, new Runnable() {
                    public void run() {
                        StatusBarKeyguardViewManager.this.mNotificationShadeWindowController.setKeyguardOccluded(StatusBarKeyguardViewManager.this.mOccluded);
                        StatusBarKeyguardViewManager.this.reset(true);
                    }
                });
                return;
            }
        } else if (!z && this.mOccluded && this.mShowing) {
            SysUiStatsLog.write(62, 2);
        }
        boolean z4 = !this.mOccluded && z;
        this.mOccluded = z;
        if (this.mShowing) {
            NotificationMediaManager notificationMediaManager = this.mMediaManager;
            if (!z2 || z) {
                z3 = false;
            }
            notificationMediaManager.updateMediaMetaData(false, z3);
        }
        this.mNotificationShadeWindowController.setKeyguardOccluded(z);
        if (!this.mDozing) {
            reset(z4);
        }
        if (z2 && !z && this.mShowing && !this.mBouncer.isShowing()) {
            this.mStatusBar.animateKeyguardUnoccluding();
        }
    }

    public boolean isOccluded() {
        return this.mOccluded;
    }

    public void startPreHideAnimation(Runnable runnable) {
        if (this.mBouncer.isShowing()) {
            this.mBouncer.startPreHideAnimation(runnable);
            this.mStatusBar.onBouncerPreHideAnimation();
        } else if (runnable != null) {
            runnable.run();
        }
        this.mNotificationPanelViewController.blockExpansionForCurrentTouch();
        updateLockIcon();
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x007e  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x009a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void hide(long r19, long r21) {
        /*
            r18 = this;
            r0 = r18
            r1 = 0
            r0.mShowing = r1
            com.android.systemui.statusbar.policy.KeyguardStateController r2 = r0.mKeyguardStateController
            boolean r3 = r2.isOccluded()
            r2.notifyKeyguardState(r1, r3)
            r18.launchPendingWakeupAction()
            com.android.keyguard.KeyguardUpdateMonitor r2 = r0.mKeyguardUpdateManager
            boolean r2 = r2.needsSlowUnlockTransition()
            if (r2 == 0) goto L_0x001c
            r2 = 2000(0x7d0, double:9.88E-321)
            goto L_0x001e
        L_0x001c:
            r2 = r21
        L_0x001e:
            long r4 = android.os.SystemClock.uptimeMillis()
            r6 = -48
            long r6 = r19 + r6
            long r6 = r6 - r4
            r4 = 0
            long r6 = java.lang.Math.max(r4, r6)
            com.android.systemui.statusbar.phone.StatusBar r8 = r0.mStatusBar
            boolean r8 = r8.isInLaunchTransition()
            r15 = 1
            if (r8 == 0) goto L_0x0048
            com.android.systemui.statusbar.phone.StatusBar r1 = r0.mStatusBar
            com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager$5 r2 = new com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager$5
            r2.<init>()
            com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager$6 r3 = new com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager$6
            r3.<init>()
            r1.fadeKeyguardAfterLaunchTransition(r2, r3)
            r4 = r15
            goto L_0x00e7
        L_0x0048:
            r18.executeAfterKeyguardGoneAction()
            com.android.systemui.statusbar.phone.BiometricUnlockController r8 = r0.mBiometricUnlockController
            int r8 = r8.getMode()
            r9 = 2
            if (r8 != r9) goto L_0x0057
            r16 = r15
            goto L_0x0059
        L_0x0057:
            r16 = r1
        L_0x0059:
            boolean r17 = r18.needsBypassFading()
            if (r17 == 0) goto L_0x0063
            r2 = 67
        L_0x0061:
            r11 = r4
            goto L_0x0069
        L_0x0063:
            if (r16 == 0) goto L_0x0068
            r2 = 240(0xf0, double:1.186E-321)
            goto L_0x0061
        L_0x0068:
            r11 = r6
        L_0x0069:
            com.android.systemui.statusbar.phone.StatusBar r8 = r0.mStatusBar
            r9 = r19
            r13 = r2
            r4 = r15
            r15 = r17
            r8.setKeyguardFadingAway(r9, r11, r13, r15)
            com.android.systemui.statusbar.phone.BiometricUnlockController r5 = r0.mBiometricUnlockController
            r5.startKeyguardFadingAway()
            r0.hideBouncer(r4)
            if (r16 == 0) goto L_0x009a
            if (r17 == 0) goto L_0x0091
            com.android.systemui.statusbar.phone.NotificationPanelViewController r5 = r0.mNotificationPanelViewController
            android.view.ViewGroup r5 = r5.getView()
            android.view.View r6 = r0.mNotificationContainer
            com.android.systemui.statusbar.phone.-$$Lambda$StatusBarKeyguardViewManager$aIusP5sgaSr59XXK3nFh48FBNI4 r7 = new com.android.systemui.statusbar.phone.-$$Lambda$StatusBarKeyguardViewManager$aIusP5sgaSr59XXK3nFh48FBNI4
            r7.<init>()
            com.android.systemui.statusbar.notification.ViewGroupFadeHelper.fadeOutAllChildrenExcept(r5, r6, r2, r7)
            goto L_0x0096
        L_0x0091:
            com.android.systemui.statusbar.phone.StatusBar r2 = r0.mStatusBar
            r2.fadeKeyguardWhilePulsing()
        L_0x0096:
            r18.wakeAndUnlockDejank()
            goto L_0x00d7
        L_0x009a:
            com.android.systemui.statusbar.SysuiStatusBarStateController r5 = r0.mStatusBarStateController
            boolean r5 = r5.leaveOpenOnKeyguardHide()
            if (r5 != 0) goto L_0x00c8
            com.android.systemui.statusbar.phone.NotificationShadeWindowController r5 = r0.mNotificationShadeWindowController
            r5.setKeyguardFadingAway(r4)
            if (r17 == 0) goto L_0x00ba
            com.android.systemui.statusbar.phone.NotificationPanelViewController r5 = r0.mNotificationPanelViewController
            android.view.ViewGroup r5 = r5.getView()
            android.view.View r6 = r0.mNotificationContainer
            com.android.systemui.statusbar.phone.-$$Lambda$StatusBarKeyguardViewManager$EJI38cHcIk60L5eHmdpMvFRistw r7 = new com.android.systemui.statusbar.phone.-$$Lambda$StatusBarKeyguardViewManager$EJI38cHcIk60L5eHmdpMvFRistw
            r7.<init>()
            com.android.systemui.statusbar.notification.ViewGroupFadeHelper.fadeOutAllChildrenExcept(r5, r6, r2, r7)
            goto L_0x00bf
        L_0x00ba:
            com.android.systemui.statusbar.phone.StatusBar r2 = r0.mStatusBar
            r2.hideKeyguard()
        L_0x00bf:
            com.android.systemui.statusbar.phone.StatusBar r2 = r0.mStatusBar
            r2.updateScrimController()
            r18.wakeAndUnlockDejank()
            goto L_0x00d7
        L_0x00c8:
            com.android.systemui.statusbar.phone.StatusBar r2 = r0.mStatusBar
            r2.hideKeyguard()
            com.android.systemui.statusbar.phone.StatusBar r2 = r0.mStatusBar
            r2.finishKeyguardFadingAway()
            com.android.systemui.statusbar.phone.BiometricUnlockController r2 = r0.mBiometricUnlockController
            r2.finishKeyguardFadingAway()
        L_0x00d7:
            r18.updateLockIcon()
            r18.updateStates()
            com.android.systemui.statusbar.phone.NotificationShadeWindowController r2 = r0.mNotificationShadeWindowController
            r2.setKeyguardShowing(r1)
            com.android.keyguard.ViewMediatorCallback r0 = r0.mViewMediatorCallback
            r0.keyguardGone()
        L_0x00e7:
            r0 = 62
            com.android.systemui.shared.system.SysUiStatsLog.write(r0, r4)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.phone.StatusBarKeyguardViewManager.hide(long, long):void");
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$hide$0 */
    public /* synthetic */ void lambda$hide$0$StatusBarKeyguardViewManager() {
        this.mStatusBar.hideKeyguard();
        onKeyguardFadedAway();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$hide$1 */
    public /* synthetic */ void lambda$hide$1$StatusBarKeyguardViewManager() {
        this.mStatusBar.hideKeyguard();
    }

    private boolean needsBypassFading() {
        if ((this.mBiometricUnlockController.getMode() == 7 || this.mBiometricUnlockController.getMode() == 2 || this.mBiometricUnlockController.getMode() == 1) && this.mBypassController.getBypassEnabled()) {
            return true;
        }
        return false;
    }

    public void onDensityOrFontScaleChanged() {
        hideBouncer(true);
    }

    public void onNavigationModeChanged(int i) {
        boolean isGesturalMode = QuickStepContract.isGesturalMode(i);
        if (isGesturalMode != this.mGesturalNav) {
            this.mGesturalNav = isGesturalMode;
            updateStates();
        }
    }

    public void onThemeChanged() {
        hideBouncer(true);
        this.mBouncer.prepare();
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onKeyguardFadedAway$2 */
    public /* synthetic */ void lambda$onKeyguardFadedAway$2$StatusBarKeyguardViewManager() {
        this.mNotificationShadeWindowController.setKeyguardFadingAway(false);
    }

    public void onKeyguardFadedAway() {
        this.mContainer.postDelayed(new Runnable() {
            public final void run() {
                StatusBarKeyguardViewManager.this.lambda$onKeyguardFadedAway$2$StatusBarKeyguardViewManager();
            }
        }, 100);
        ViewGroupFadeHelper.reset(this.mNotificationPanelViewController.getView());
        this.mStatusBar.finishKeyguardFadingAway();
        this.mBiometricUnlockController.finishKeyguardFadingAway();
        WindowManagerGlobal.getInstance().trimMemory(20);
    }

    private void wakeAndUnlockDejank() {
        if (this.mBiometricUnlockController.getMode() == 1 && LatencyTracker.isEnabled(this.mContext)) {
            DejankUtils.postAfterTraversal(new Runnable() {
                public final void run() {
                    StatusBarKeyguardViewManager.this.lambda$wakeAndUnlockDejank$3$StatusBarKeyguardViewManager();
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$wakeAndUnlockDejank$3 */
    public /* synthetic */ void lambda$wakeAndUnlockDejank$3$StatusBarKeyguardViewManager() {
        LatencyTracker.getInstance(this.mContext).onActionEnd(2);
    }

    /* access modifiers changed from: private */
    public void executeAfterKeyguardGoneAction() {
        OnDismissAction onDismissAction = this.mAfterKeyguardGoneAction;
        if (onDismissAction != null) {
            onDismissAction.onDismiss();
            this.mAfterKeyguardGoneAction = null;
        }
        this.mKeyguardGoneCancelAction = null;
        for (int i = 0; i < this.mAfterKeyguardGoneRunnables.size(); i++) {
            ((Runnable) this.mAfterKeyguardGoneRunnables.get(i)).run();
        }
        this.mAfterKeyguardGoneRunnables.clear();
    }

    public void dismissAndCollapse() {
        this.mStatusBar.executeRunnableDismissingKeyguard(null, null, true, false, true);
    }

    public boolean isSecure() {
        return this.mBouncer.isSecure();
    }

    public boolean isShowing() {
        return this.mShowing;
    }

    public boolean onBackPressed(boolean z) {
        if (!this.mBouncer.isShowing()) {
            return false;
        }
        this.mStatusBar.endAffordanceLaunch();
        if (!this.mBouncer.isScrimmed() || this.mBouncer.needsFullscreenBouncer()) {
            reset(z);
        } else {
            hideBouncer(false);
            updateStates();
        }
        return true;
    }

    public boolean isBouncerShowing() {
        return this.mBouncer.isShowing();
    }

    public boolean bouncerIsOrWillBeShowing() {
        return this.mBouncer.isShowing() || this.mBouncer.inTransit();
    }

    private long getNavBarShowDelay() {
        if (this.mKeyguardStateController.isKeyguardFadingAway()) {
            return this.mKeyguardStateController.getKeyguardFadingAwayDelay();
        }
        return this.mBouncer.isShowing() ? 320 : 0;
    }

    /* access modifiers changed from: protected */
    public void updateStates() {
        int systemUiVisibility = this.mContainer.getSystemUiVisibility();
        boolean z = this.mShowing;
        boolean z2 = this.mOccluded;
        boolean isShowing = this.mBouncer.isShowing();
        boolean z3 = true;
        boolean z4 = !this.mBouncer.isFullscreenBouncer();
        boolean z5 = this.mRemoteInputActive;
        if ((z4 || !z || z5) != (this.mLastBouncerDismissible || !this.mLastShowing || this.mLastRemoteInputActive) || this.mFirstUpdate) {
            if (z4 || !z || z5) {
                this.mContainer.setSystemUiVisibility(systemUiVisibility & -4194305);
            } else {
                this.mContainer.setSystemUiVisibility(systemUiVisibility | 4194304);
            }
        }
        boolean isNavBarVisible = isNavBarVisible();
        if (isNavBarVisible != getLastNavBarVisible() || this.mFirstUpdate) {
            updateNavigationBarVisibility(isNavBarVisible);
        }
        if (isShowing != this.mLastBouncerShowing || this.mFirstUpdate) {
            this.mNotificationShadeWindowController.setBouncerShowing(isShowing);
            this.mStatusBar.setBouncerShowing(isShowing);
            updateLockIcon();
        }
        if ((z && !z2) != (this.mLastShowing && !this.mLastOccluded) || this.mFirstUpdate) {
            KeyguardUpdateMonitor keyguardUpdateMonitor = this.mKeyguardUpdateManager;
            if (!z || z2) {
                z3 = false;
            }
            keyguardUpdateMonitor.onKeyguardVisibilityChanged(z3);
        }
        if (isShowing != this.mLastBouncerShowing || this.mFirstUpdate) {
            this.mKeyguardUpdateManager.sendKeyguardBouncerChanged(isShowing);
        }
        this.mFirstUpdate = false;
        this.mLastShowing = z;
        this.mLastOccluded = z2;
        this.mLastBouncerShowing = isShowing;
        this.mLastBouncerDismissible = z4;
        this.mLastRemoteInputActive = z5;
        this.mLastDozing = this.mDozing;
        this.mLastPulsing = this.mPulsing;
        this.mLastBiometricMode = this.mBiometricUnlockController.getMode();
        this.mLastGesturalNav = this.mGesturalNav;
        this.mLastIsDocked = this.mIsDocked;
        this.mStatusBar.onKeyguardViewManagerStatesUpdated();
    }

    /* access modifiers changed from: protected */
    public void updateNavigationBarVisibility(boolean z) {
        if (this.mStatusBar.getNavigationBarView() == null) {
            return;
        }
        if (z) {
            long navBarShowDelay = getNavBarShowDelay();
            if (navBarShowDelay == 0) {
                this.mMakeNavigationBarVisibleRunnable.run();
            } else {
                this.mContainer.postOnAnimationDelayed(this.mMakeNavigationBarVisibleRunnable, navBarShowDelay);
            }
        } else {
            this.mContainer.removeCallbacks(this.mMakeNavigationBarVisibleRunnable);
            if (ViewRootImpl.sNewInsetsMode == 2) {
                this.mStatusBar.getNotificationShadeWindowView().getWindowInsetsController().hide(Type.navigationBars());
            } else {
                this.mStatusBar.getNavigationBarView().getRootView().setVisibility(8);
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean isNavBarVisible() {
        int mode = this.mBiometricUnlockController.getMode();
        boolean z = this.mShowing && !this.mOccluded;
        boolean z2 = this.mDozing && mode != 2;
        boolean z3 = ((z && !this.mDozing) || (this.mPulsing && !this.mIsDocked)) && this.mGesturalNav;
        if ((z || z2) && !this.mBouncer.isShowing() && !this.mRemoteInputActive && !z3) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean getLastNavBarVisible() {
        boolean z = this.mLastShowing && !this.mLastOccluded;
        boolean z2 = this.mLastDozing && this.mLastBiometricMode != 2;
        boolean z3 = ((z && !this.mLastDozing) || (this.mLastPulsing && !this.mLastIsDocked)) && this.mLastGesturalNav;
        if ((z || z2) && !this.mLastBouncerShowing && !this.mLastRemoteInputActive && !z3) {
            return false;
        }
        return true;
    }

    public boolean shouldDismissOnMenuPressed() {
        return this.mBouncer.shouldDismissOnMenuPressed();
    }

    public boolean interceptMediaKey(KeyEvent keyEvent) {
        return this.mBouncer.interceptMediaKey(keyEvent);
    }

    public void readyForKeyguardDone() {
        this.mViewMediatorCallback.readyForKeyguardDone();
    }

    public boolean shouldDisableWindowAnimationsForUnlock() {
        return this.mStatusBar.isInLaunchTransition();
    }

    public boolean shouldSubtleWindowAnimationsForUnlock() {
        return needsBypassFading();
    }

    public boolean isGoingToNotificationShade() {
        return this.mStatusBarStateController.leaveOpenOnKeyguardHide();
    }

    public void keyguardGoingAway() {
        this.mStatusBar.keyguardGoingAway();
    }

    public void notifyKeyguardAuthenticated(boolean z) {
        this.mBouncer.notifyKeyguardAuthenticated(z);
    }

    public void showBouncerMessage(String str, ColorStateList colorStateList) {
        this.mBouncer.showMessage(str, colorStateList);
    }

    public ViewRootImpl getViewRootImpl() {
        return this.mStatusBar.getStatusBarView().getViewRootImpl();
    }

    public void launchPendingWakeupAction() {
        DismissWithActionRequest dismissWithActionRequest = this.mPendingWakeupAction;
        this.mPendingWakeupAction = null;
        if (dismissWithActionRequest == null) {
            return;
        }
        if (this.mShowing) {
            dismissWithAction(dismissWithActionRequest.dismissAction, dismissWithActionRequest.cancelAction, dismissWithActionRequest.afterKeyguardGone, dismissWithActionRequest.message);
            return;
        }
        OnDismissAction onDismissAction = dismissWithActionRequest.dismissAction;
        if (onDismissAction != null) {
            onDismissAction.onDismiss();
        }
    }

    public void cancelPendingWakeupAction() {
        DismissWithActionRequest dismissWithActionRequest = this.mPendingWakeupAction;
        this.mPendingWakeupAction = null;
        if (dismissWithActionRequest != null) {
            Runnable runnable = dismissWithActionRequest.cancelAction;
            if (runnable != null) {
                runnable.run();
            }
        }
    }

    public boolean bouncerNeedsScrimming() {
        return this.mOccluded || this.mBouncer.willDismissWithAction() || this.mStatusBar.isFullScreenUserSwitcherState() || (this.mBouncer.isShowing() && this.mBouncer.isScrimmed()) || this.mBouncer.isFullscreenBouncer();
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("StatusBarKeyguardViewManager:");
        StringBuilder sb = new StringBuilder();
        sb.append("  mShowing: ");
        sb.append(this.mShowing);
        printWriter.println(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("  mOccluded: ");
        sb2.append(this.mOccluded);
        printWriter.println(sb2.toString());
        StringBuilder sb3 = new StringBuilder();
        sb3.append("  mRemoteInputActive: ");
        sb3.append(this.mRemoteInputActive);
        printWriter.println(sb3.toString());
        StringBuilder sb4 = new StringBuilder();
        sb4.append("  mDozing: ");
        sb4.append(this.mDozing);
        printWriter.println(sb4.toString());
        StringBuilder sb5 = new StringBuilder();
        sb5.append("  mGoingToSleepVisibleNotOccluded: ");
        sb5.append(this.mGoingToSleepVisibleNotOccluded);
        printWriter.println(sb5.toString());
        StringBuilder sb6 = new StringBuilder();
        sb6.append("  mAfterKeyguardGoneAction: ");
        sb6.append(this.mAfterKeyguardGoneAction);
        printWriter.println(sb6.toString());
        StringBuilder sb7 = new StringBuilder();
        sb7.append("  mAfterKeyguardGoneRunnables: ");
        sb7.append(this.mAfterKeyguardGoneRunnables);
        printWriter.println(sb7.toString());
        StringBuilder sb8 = new StringBuilder();
        sb8.append("  mPendingWakeupAction: ");
        sb8.append(this.mPendingWakeupAction);
        printWriter.println(sb8.toString());
        KeyguardBouncer keyguardBouncer = this.mBouncer;
        if (keyguardBouncer != null) {
            keyguardBouncer.dump(printWriter);
        }
    }

    public void onStateChanged(int i) {
        updateLockIcon();
    }

    public void onDozingChanged(boolean z) {
        setDozing(z);
    }
}
