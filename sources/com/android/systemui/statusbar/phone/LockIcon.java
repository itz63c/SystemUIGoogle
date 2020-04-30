package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.drawable.Animatable2.AnimationCallback;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Trace;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;
import com.android.internal.graphics.ColorUtils;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.systemui.C2004R$anim;
import com.android.systemui.C2017R$string;
import com.android.systemui.Dependency;
import com.android.systemui.Interpolators;
import com.android.systemui.statusbar.KeyguardAffordanceView;
import com.android.systemui.statusbar.notification.NotificationWakeUpCoordinator;
import com.android.systemui.statusbar.policy.AccessibilityController;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.android.systemui.statusbar.policy.KeyguardStateController.Callback;

public class LockIcon extends KeyguardAffordanceView implements OnPreDrawListener {
    private static final int[][] LOCK_ANIM_RES_IDS = {new int[]{C2004R$anim.lock_to_error, C2004R$anim.lock_unlock, C2004R$anim.lock_lock, C2004R$anim.lock_scanning}, new int[]{C2004R$anim.lock_to_error_circular, C2004R$anim.lock_unlock_circular, C2004R$anim.lock_lock_circular, C2004R$anim.lock_scanning_circular}, new int[]{C2004R$anim.lock_to_error_filled, C2004R$anim.lock_unlock_filled, C2004R$anim.lock_lock_filled, C2004R$anim.lock_scanning_filled}, new int[]{C2004R$anim.lock_to_error_rounded, C2004R$anim.lock_unlock_rounded, C2004R$anim.lock_lock_rounded, C2004R$anim.lock_scanning_rounded}};
    private final AccessibilityController mAccessibilityController;
    /* access modifiers changed from: private */
    public boolean mBlockUpdates;
    /* access modifiers changed from: private */
    public boolean mBouncerPreHideAnimation;
    private boolean mBouncerShowingScrimmed;
    private final KeyguardBypassController mBypassController;
    private int mDensity;
    private boolean mDocked;
    private float mDozeAmount;
    private boolean mDozing;
    private boolean mForceUpdate;
    private final HeadsUpManagerPhone mHeadsUpManager;
    private int mIconColor;
    private boolean mIsFaceUnlockState;
    /* access modifiers changed from: private */
    public boolean mKeyguardJustShown;
    private final Callback mKeyguardMonitorCallback = new Callback() {
        public void onKeyguardShowingChanged() {
            boolean access$000 = LockIcon.this.mKeyguardShowing;
            LockIcon lockIcon = LockIcon.this;
            lockIcon.mKeyguardShowing = lockIcon.mKeyguardStateController.isShowing();
            boolean z = false;
            if (!access$000 && LockIcon.this.mKeyguardShowing && LockIcon.this.mBlockUpdates) {
                LockIcon.this.mBlockUpdates = false;
                z = true;
            }
            if (!access$000 && LockIcon.this.mKeyguardShowing) {
                LockIcon.this.mKeyguardJustShown = true;
            }
            LockIcon.this.update(z);
        }

        public void onKeyguardFadingAwayChanged() {
            if (!LockIcon.this.mKeyguardStateController.isKeyguardFadingAway()) {
                LockIcon.this.mBouncerPreHideAnimation = false;
                if (LockIcon.this.mBlockUpdates) {
                    LockIcon.this.mBlockUpdates = false;
                    LockIcon.this.update(true);
                }
            }
        }

        public void onUnlockedChanged() {
            LockIcon.this.update();
        }
    };
    /* access modifiers changed from: private */
    public boolean mKeyguardShowing;
    /* access modifiers changed from: private */
    public final KeyguardStateController mKeyguardStateController;
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private int mLastState = 0;
    private boolean mPulsing;
    private boolean mShowingLaunchAffordance;
    private boolean mSimLocked;
    private int mStatusBarState = 0;
    private boolean mTransientBiometricsError;
    private boolean mUpdatePending;
    private boolean mWakeAndUnlockRunning;
    private final NotificationWakeUpCoordinator mWakeUpCoordinator;

    /* access modifiers changed from: private */
    public boolean doesAnimationLoop(int i) {
        return i == 3;
    }

    private static int getAnimationIndexForTransition(int i, int i2, boolean z, boolean z2, boolean z3) {
        if (z2 && !z) {
            return -1;
        }
        if (i2 == 3) {
            return 0;
        }
        if (i != 1 && i2 == 1) {
            return 1;
        }
        if (i == 1 && i2 == 0 && !z3) {
            return 2;
        }
        return i2 == 2 ? 3 : -1;
    }

    public LockIcon(Context context, AttributeSet attributeSet, AccessibilityController accessibilityController, KeyguardBypassController keyguardBypassController, NotificationWakeUpCoordinator notificationWakeUpCoordinator, KeyguardStateController keyguardStateController, HeadsUpManagerPhone headsUpManagerPhone) {
        super(context, attributeSet);
        this.mContext = context;
        this.mKeyguardUpdateMonitor = (KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class);
        this.mAccessibilityController = accessibilityController;
        this.mBypassController = keyguardBypassController;
        this.mWakeUpCoordinator = notificationWakeUpCoordinator;
        this.mKeyguardStateController = keyguardStateController;
        this.mHeadsUpManager = headsUpManagerPhone;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mKeyguardStateController.addCallback(this.mKeyguardMonitorCallback);
        this.mSimLocked = this.mKeyguardUpdateMonitor.isSimPinSecure();
        update();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mKeyguardStateController.removeCallback(this.mKeyguardMonitorCallback);
    }

    public void setTransientBiometricsError(boolean z) {
        this.mTransientBiometricsError = z;
        update();
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        int i = configuration.densityDpi;
        if (i != this.mDensity) {
            this.mDensity = i;
            update();
        }
    }

    public void update() {
        update(false);
    }

    public void update(boolean z) {
        if (z) {
            this.mForceUpdate = true;
        }
        if (!this.mUpdatePending) {
            this.mUpdatePending = true;
            getViewTreeObserver().addOnPreDrawListener(this);
        }
    }

    public boolean onPreDraw() {
        this.mUpdatePending = false;
        getViewTreeObserver().removeOnPreDrawListener(this);
        final int state = getState();
        int i = this.mLastState;
        boolean z = this.mKeyguardJustShown;
        this.mIsFaceUnlockState = state == 2;
        this.mLastState = state;
        this.mKeyguardJustShown = false;
        boolean z2 = i != state || this.mForceUpdate;
        if (this.mBlockUpdates && canBlockUpdates()) {
            z2 = false;
        }
        if (z2) {
            this.mForceUpdate = false;
            final int animationIndexForTransition = getAnimationIndexForTransition(i, state, this.mPulsing, this.mDozing, z);
            boolean z3 = animationIndexForTransition != -1;
            Drawable drawable = this.mContext.getDrawable(z3 ? getThemedAnimationResId(animationIndexForTransition) : getIconForState(state));
            final AnimatedVectorDrawable animatedVectorDrawable = drawable instanceof AnimatedVectorDrawable ? (AnimatedVectorDrawable) drawable : null;
            setImageDrawable(drawable, false);
            if (this.mIsFaceUnlockState) {
                announceForAccessibility(getContext().getString(C2017R$string.accessibility_scanning_face));
            }
            if (animatedVectorDrawable != null && z3) {
                animatedVectorDrawable.forceAnimationOnUI();
                animatedVectorDrawable.clearAnimationCallbacks();
                animatedVectorDrawable.registerAnimationCallback(new AnimationCallback() {
                    public void onAnimationEnd(Drawable drawable) {
                        if (LockIcon.this.getDrawable() == animatedVectorDrawable && state == LockIcon.this.getState() && LockIcon.this.doesAnimationLoop(animationIndexForTransition)) {
                            animatedVectorDrawable.start();
                            return;
                        }
                        Trace.endAsyncSection("LockIcon#Animation", state);
                    }
                });
                Trace.beginAsyncSection("LockIcon#Animation", state);
                animatedVectorDrawable.start();
            }
        }
        updateDarkTint();
        updateIconVisibility();
        updateClickability();
        return true;
    }

    /* access modifiers changed from: 0000 */
    public boolean updateIconVisibility() {
        int i = 0;
        boolean z = (this.mDozing && (!this.mPulsing || this.mDocked)) || this.mWakeAndUnlockRunning || this.mShowingLaunchAffordance;
        if (this.mBypassController.getBypassEnabled() && !this.mBouncerShowingScrimmed && ((this.mHeadsUpManager.isHeadsUpGoingAway() || this.mHeadsUpManager.hasPinnedHeadsUp() || this.mStatusBarState == 1) && !this.mWakeUpCoordinator.getNotificationsFullyHidden())) {
            z = true;
        }
        if (z == (getVisibility() == 4)) {
            return false;
        }
        if (z) {
            i = 4;
        }
        setVisibility(i);
        animate().cancel();
        if (!z) {
            setScaleX(0.0f);
            setScaleY(0.0f);
            animate().setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN).scaleX(1.0f).scaleY(1.0f).withLayer().setDuration(233).start();
        }
        return true;
    }

    private boolean canBlockUpdates() {
        return this.mKeyguardShowing || this.mKeyguardStateController.isKeyguardFadingAway();
    }

    private void updateClickability() {
        if (this.mAccessibilityController != null) {
            boolean z = true;
            boolean z2 = this.mKeyguardStateController.isMethodSecure() && this.mKeyguardStateController.canDismissLockScreen();
            boolean isAccessibilityEnabled = this.mAccessibilityController.isAccessibilityEnabled();
            setClickable(isAccessibilityEnabled);
            if (!z2 || isAccessibilityEnabled) {
                z = false;
            }
            setLongClickable(z);
            setFocusable(this.mAccessibilityController.isAccessibilityEnabled());
        }
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        boolean isFingerprintDetectionRunning = this.mKeyguardUpdateMonitor.isFingerprintDetectionRunning();
        boolean isUnlockingWithBiometricAllowed = this.mKeyguardUpdateMonitor.isUnlockingWithBiometricAllowed(true);
        if (isFingerprintDetectionRunning && isUnlockingWithBiometricAllowed) {
            accessibilityNodeInfo.addAction(new AccessibilityAction(16, getContext().getString(C2017R$string.accessibility_unlock_without_fingerprint)));
            accessibilityNodeInfo.setHintText(getContext().getString(C2017R$string.accessibility_waiting_for_fingerprint));
        } else if (this.mIsFaceUnlockState) {
            accessibilityNodeInfo.setClassName(LockIcon.class.getName());
            accessibilityNodeInfo.setContentDescription(getContext().getString(C2017R$string.accessibility_scanning_face));
        }
    }

    private int getIconForState(int i) {
        if (i != 0) {
            if (i == 1) {
                return 17302467;
            }
            if (!(i == 2 || i == 3)) {
                throw new IllegalArgumentException();
            }
        }
        return 17302458;
    }

    public void setBouncerShowingScrimmed(boolean z) {
        this.mBouncerShowingScrimmed = z;
        if (this.mBypassController.getBypassEnabled()) {
            update();
        }
    }

    public void onBouncerPreHideAnimation() {
        update();
    }

    /* access modifiers changed from: 0000 */
    public void setIconColor(int i) {
        this.mIconColor = i;
        updateDarkTint();
    }

    /* access modifiers changed from: 0000 */
    public void setSimLocked(boolean z) {
        this.mSimLocked = z;
    }

    public void setDocked(boolean z) {
        if (this.mDocked != z) {
            this.mDocked = z;
            update();
        }
    }

    private int getThemedAnimationResId(int i) {
        int[][] iArr = LOCK_ANIM_RES_IDS;
        String emptyIfNull = TextUtils.emptyIfNull(Secure.getString(getContext().getContentResolver(), "theme_customization_overlay_packages"));
        if (emptyIfNull.contains("com.android.theme.icon_pack.circular.android")) {
            return iArr[1][i];
        }
        if (emptyIfNull.contains("com.android.theme.icon_pack.filled.android")) {
            return iArr[2][i];
        }
        if (emptyIfNull.contains("com.android.theme.icon_pack.rounded.android")) {
            return iArr[3][i];
        }
        return iArr[0][i];
    }

    /* access modifiers changed from: private */
    public int getState() {
        KeyguardUpdateMonitor keyguardUpdateMonitor = (KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class);
        if ((this.mKeyguardStateController.canDismissLockScreen() || !this.mKeyguardShowing || this.mKeyguardStateController.isKeyguardGoingAway()) && !this.mSimLocked) {
            return 1;
        }
        if (this.mTransientBiometricsError) {
            return 3;
        }
        return (!keyguardUpdateMonitor.isFaceDetectionRunning() || this.mPulsing) ? 0 : 2;
    }

    public void setPulsing(boolean z) {
        this.mPulsing = z;
        update();
    }

    private void updateDarkTint() {
        setImageTintList(ColorStateList.valueOf(ColorUtils.blendARGB(this.mIconColor, -1, this.mDozeAmount)));
    }

    public void onBiometricAuthModeChanged(boolean z, boolean z2) {
        if (z) {
            this.mWakeAndUnlockRunning = true;
        }
        if (z2 && this.mBypassController.getBypassEnabled() && canBlockUpdates()) {
            this.mBlockUpdates = true;
        }
        update();
    }

    public void onShowingLaunchAffordanceChanged(boolean z) {
        this.mShowingLaunchAffordance = z;
        update();
    }

    public void onScrimVisibilityChanged(int i) {
        if (this.mWakeAndUnlockRunning && i == 0) {
            this.mWakeAndUnlockRunning = false;
            update();
        }
    }

    /* access modifiers changed from: 0000 */
    public void setDozing(boolean z) {
        this.mDozing = z;
        update();
    }

    /* access modifiers changed from: 0000 */
    public void setDozeAmount(float f) {
        this.mDozeAmount = f;
        updateDarkTint();
    }

    public void setStatusBarState(int i) {
        this.mStatusBarState = i;
        updateIconVisibility();
    }
}
