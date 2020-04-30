package com.android.systemui.statusbar.phone;

import android.content.res.TypedArray;
import android.hardware.biometrics.BiometricSourceType;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.C2006R$attr;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2017R$string;
import com.android.systemui.dock.DockManager;
import com.android.systemui.dock.DockManager.DockEventListener;
import com.android.systemui.plugins.statusbar.StatusBarStateController;
import com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener;
import com.android.systemui.statusbar.KeyguardIndicationController;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator.WakeUpListener;
import com.android.systemui.statusbar.policy.AccessibilityController;
import com.android.systemui.statusbar.policy.ConfigurationController;
import com.android.systemui.statusbar.policy.ConfigurationController.ConfigurationListener;
import java.util.Optional;
import java.util.function.Consumer;

public class LockscreenLockIconController {
    private final AccessibilityController mAccessibilityController;
    /* access modifiers changed from: private */
    public final ConfigurationController mConfigurationController;
    /* access modifiers changed from: private */
    public final ConfigurationListener mConfigurationListener = new ConfigurationListener() {
        public void onThemeChanged() {
            TypedArray obtainStyledAttributes = LockscreenLockIconController.this.mLockIcon.getContext().getTheme().obtainStyledAttributes(null, new int[]{C2006R$attr.wallpaperTextColor}, 0, 0);
            int color = obtainStyledAttributes.getColor(0, -1);
            obtainStyledAttributes.recycle();
            LockscreenLockIconController.this.mLockIcon.setIconColor(color);
        }

        public void onDensityOrFontScaleChanged() {
            LayoutParams layoutParams = LockscreenLockIconController.this.mLockIcon.getLayoutParams();
            if (layoutParams != null) {
                layoutParams.width = LockscreenLockIconController.this.mLockIcon.getResources().getDimensionPixelSize(C2009R$dimen.keyguard_lock_width);
                layoutParams.height = LockscreenLockIconController.this.mLockIcon.getResources().getDimensionPixelSize(C2009R$dimen.keyguard_lock_height);
                LockscreenLockIconController.this.mLockIcon.setLayoutParams(layoutParams);
                LockscreenLockIconController.this.mLockIcon.update(true);
            }
        }

        public void onLocaleListChanged() {
            LockscreenLockIconController.this.mLockIcon.setContentDescription(LockscreenLockIconController.this.mLockIcon.getResources().getText(C2017R$string.accessibility_unlock_button));
            LockscreenLockIconController.this.mLockIcon.update(true);
        }
    };
    /* access modifiers changed from: private */
    public final DockEventListener mDockEventListener = new DockEventListener() {
        public final void onEvent(int i) {
            LockscreenLockIconController.this.lambda$new$0$LockscreenLockIconController(i);
        }
    };
    /* access modifiers changed from: private */
    public final Optional<DockManager> mDockManager;
    /* access modifiers changed from: private */
    public final KeyguardBypassController mKeyguardBypassController;
    private final KeyguardIndicationController mKeyguardIndicationController;
    /* access modifiers changed from: private */
    public final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    /* access modifiers changed from: private */
    public LockIcon mLockIcon;
    private final LockPatternUtils mLockPatternUtils;
    private final LockscreenGestureLogger mLockscreenGestureLogger;
    /* access modifiers changed from: private */
    public final NotificationWakeUpCoordinator mNotificationWakeUpCoordinator;
    private OnAttachStateChangeListener mOnAttachStateChangeListener = new OnAttachStateChangeListener() {
        public void onViewAttachedToWindow(View view) {
            LockscreenLockIconController.this.mStatusBarStateController.addCallback(LockscreenLockIconController.this.mSBStateListener);
            LockscreenLockIconController.this.mConfigurationController.addCallback(LockscreenLockIconController.this.mConfigurationListener);
            LockscreenLockIconController.this.mNotificationWakeUpCoordinator.addListener(LockscreenLockIconController.this.mWakeUpListener);
            LockscreenLockIconController.this.mKeyguardUpdateMonitor.registerCallback(LockscreenLockIconController.this.mUpdateMonitorCallback);
            LockscreenLockIconController.this.mDockManager.ifPresent(new Consumer() {
                public final void accept(Object obj) {
                    C14901.this.lambda$onViewAttachedToWindow$0$LockscreenLockIconController$1((DockManager) obj);
                }
            });
            LockscreenLockIconController.this.mConfigurationListener.onThemeChanged();
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onViewAttachedToWindow$0 */
        public /* synthetic */ void lambda$onViewAttachedToWindow$0$LockscreenLockIconController$1(DockManager dockManager) {
            dockManager.addListener(LockscreenLockIconController.this.mDockEventListener);
        }

        public void onViewDetachedFromWindow(View view) {
            LockscreenLockIconController.this.mStatusBarStateController.removeCallback(LockscreenLockIconController.this.mSBStateListener);
            LockscreenLockIconController.this.mConfigurationController.removeCallback(LockscreenLockIconController.this.mConfigurationListener);
            LockscreenLockIconController.this.mNotificationWakeUpCoordinator.removeListener(LockscreenLockIconController.this.mWakeUpListener);
            LockscreenLockIconController.this.mKeyguardUpdateMonitor.removeCallback(LockscreenLockIconController.this.mUpdateMonitorCallback);
            LockscreenLockIconController.this.mDockManager.ifPresent(new Consumer() {
                public final void accept(Object obj) {
                    C14901.this.lambda$onViewDetachedFromWindow$1$LockscreenLockIconController$1((DockManager) obj);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$onViewDetachedFromWindow$1 */
        public /* synthetic */ void lambda$onViewDetachedFromWindow$1$LockscreenLockIconController$1(DockManager dockManager) {
            dockManager.removeListener(LockscreenLockIconController.this.mDockEventListener);
        }
    };
    /* access modifiers changed from: private */
    public final StateListener mSBStateListener = new StateListener() {
        public void onDozingChanged(boolean z) {
            LockscreenLockIconController.this.mLockIcon.setDozing(z);
        }

        public void onDozeAmountChanged(float f, float f2) {
            LockscreenLockIconController.this.mLockIcon.setDozeAmount(f2);
        }

        public void onStateChanged(int i) {
            LockscreenLockIconController.this.mLockIcon.setStatusBarState(i);
        }
    };
    private final ShadeController mShadeController;
    /* access modifiers changed from: private */
    public final StatusBarStateController mStatusBarStateController;
    /* access modifiers changed from: private */
    public final KeyguardUpdateMonitorCallback mUpdateMonitorCallback = new KeyguardUpdateMonitorCallback() {
        public void onSimStateChanged(int i, int i2, int i3) {
            LockscreenLockIconController.this.mLockIcon.setSimLocked(LockscreenLockIconController.this.mKeyguardUpdateMonitor.isSimPinSecure());
            LockscreenLockIconController.this.mLockIcon.update();
        }

        public void onKeyguardVisibilityChanged(boolean z) {
            LockscreenLockIconController.this.mLockIcon.update();
        }

        public void onBiometricRunningStateChanged(boolean z, BiometricSourceType biometricSourceType) {
            LockscreenLockIconController.this.mLockIcon.update();
        }

        public void onStrongAuthStateChanged(int i) {
            LockscreenLockIconController.this.mLockIcon.update();
        }
    };
    /* access modifiers changed from: private */
    public final WakeUpListener mWakeUpListener = new WakeUpListener() {
        public void onFullyHiddenChanged(boolean z) {
            if (LockscreenLockIconController.this.mKeyguardBypassController.getBypassEnabled() && LockscreenLockIconController.this.mLockIcon.updateIconVisibility()) {
                LockscreenLockIconController.this.mLockIcon.update();
            }
        }
    };

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$LockscreenLockIconController(int i) {
        LockIcon lockIcon = this.mLockIcon;
        boolean z = true;
        if (!(i == 1 || i == 2)) {
            z = false;
        }
        lockIcon.setDocked(z);
    }

    public LockscreenLockIconController(LockscreenGestureLogger lockscreenGestureLogger, KeyguardUpdateMonitor keyguardUpdateMonitor, LockPatternUtils lockPatternUtils, ShadeController shadeController, AccessibilityController accessibilityController, KeyguardIndicationController keyguardIndicationController, StatusBarStateController statusBarStateController, ConfigurationController configurationController, NotificationWakeUpCoordinator notificationWakeUpCoordinator, KeyguardBypassController keyguardBypassController, DockManager dockManager) {
        this.mLockscreenGestureLogger = lockscreenGestureLogger;
        this.mKeyguardUpdateMonitor = keyguardUpdateMonitor;
        this.mLockPatternUtils = lockPatternUtils;
        this.mShadeController = shadeController;
        this.mAccessibilityController = accessibilityController;
        this.mKeyguardIndicationController = keyguardIndicationController;
        this.mStatusBarStateController = statusBarStateController;
        this.mConfigurationController = configurationController;
        this.mNotificationWakeUpCoordinator = notificationWakeUpCoordinator;
        this.mKeyguardBypassController = keyguardBypassController;
        this.mDockManager = dockManager == null ? Optional.empty() : Optional.of(dockManager);
        this.mKeyguardIndicationController.setLockIconController(this);
    }

    public void attach(LockIcon lockIcon) {
        this.mLockIcon = lockIcon;
        lockIcon.setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                LockscreenLockIconController.this.handleClick(view);
            }
        });
        this.mLockIcon.setOnLongClickListener(new OnLongClickListener() {
            public final boolean onLongClick(View view) {
                return LockscreenLockIconController.this.handleLongClick(view);
            }
        });
        if (this.mLockIcon.isAttachedToWindow()) {
            this.mOnAttachStateChangeListener.onViewAttachedToWindow(this.mLockIcon);
        }
        this.mLockIcon.addOnAttachStateChangeListener(this.mOnAttachStateChangeListener);
        this.mLockIcon.setStatusBarState(this.mStatusBarStateController.getState());
    }

    public void onScrimVisibilityChanged(Integer num) {
        LockIcon lockIcon = this.mLockIcon;
        if (lockIcon != null) {
            lockIcon.onScrimVisibilityChanged(num.intValue());
        }
    }

    public void setPulsing(boolean z) {
        LockIcon lockIcon = this.mLockIcon;
        if (lockIcon != null) {
            lockIcon.setPulsing(z);
        }
    }

    public void onBiometricAuthModeChanged(boolean z, boolean z2) {
        LockIcon lockIcon = this.mLockIcon;
        if (lockIcon != null) {
            lockIcon.onBiometricAuthModeChanged(z, z2);
        }
    }

    public void onShowingLaunchAffordanceChanged(Boolean bool) {
        LockIcon lockIcon = this.mLockIcon;
        if (lockIcon != null) {
            lockIcon.onShowingLaunchAffordanceChanged(bool.booleanValue());
        }
    }

    public void setBouncerShowingScrimmed(boolean z) {
        LockIcon lockIcon = this.mLockIcon;
        if (lockIcon != null) {
            lockIcon.setBouncerShowingScrimmed(z);
        }
    }

    public void onBouncerPreHideAnimation() {
        LockIcon lockIcon = this.mLockIcon;
        if (lockIcon != null) {
            lockIcon.onBouncerPreHideAnimation();
        }
    }

    public void setTransientBiometricsError(boolean z) {
        LockIcon lockIcon = this.mLockIcon;
        if (lockIcon != null) {
            lockIcon.setTransientBiometricsError(z);
        }
    }

    /* access modifiers changed from: private */
    public boolean handleLongClick(View view) {
        this.mLockscreenGestureLogger.write(191, 0, 0);
        this.mKeyguardIndicationController.showTransientIndication(C2017R$string.keyguard_indication_trust_disabled);
        this.mKeyguardUpdateMonitor.onLockIconPressed();
        this.mLockPatternUtils.requireCredentialEntry(KeyguardUpdateMonitor.getCurrentUser());
        return true;
    }

    /* access modifiers changed from: private */
    public void handleClick(View view) {
        if (this.mAccessibilityController.isAccessibilityEnabled()) {
            this.mShadeController.animateCollapsePanels(0, true);
        }
    }
}
