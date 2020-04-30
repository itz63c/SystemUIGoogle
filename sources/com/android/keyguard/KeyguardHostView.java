package com.android.keyguard;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.media.AudioManager;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import com.android.internal.widget.LockPatternUtils;
import com.android.keyguard.KeyguardSecurityContainer.SecurityCallback;
import com.android.keyguard.KeyguardSecurityModel.SecurityMode;
import com.android.settingslib.Utils;
import com.android.systemui.C2007R$bool;
import com.android.systemui.C2011R$id;
import com.android.systemui.Dependency;
import com.android.systemui.plugins.ActivityStarter.OnDismissAction;
import java.io.File;

public class KeyguardHostView extends FrameLayout implements SecurityCallback {
    private AudioManager mAudioManager;
    private Runnable mCancelAction;
    private OnDismissAction mDismissAction;
    protected LockPatternUtils mLockPatternUtils;
    private KeyguardSecurityContainer mSecurityContainer;
    private TelephonyManager mTelephonyManager;
    private final KeyguardUpdateMonitorCallback mUpdateCallback;
    protected ViewMediatorCallback mViewMediatorCallback;

    public KeyguardHostView(Context context) {
        this(context, null);
    }

    public KeyguardHostView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mTelephonyManager = null;
        this.mUpdateCallback = new KeyguardUpdateMonitorCallback() {
            public void onUserSwitchComplete(int i) {
                KeyguardHostView.this.getSecurityContainer().showPrimarySecurityScreen(false);
            }

            public void onTrustGrantedWithFlags(int i, int i2) {
                if (i2 == KeyguardUpdateMonitor.getCurrentUser() && KeyguardHostView.this.isAttachedToWindow()) {
                    boolean isVisibleToUser = KeyguardHostView.this.isVisibleToUser();
                    boolean z = true;
                    boolean z2 = (i & 1) != 0;
                    if ((i & 2) == 0) {
                        z = false;
                    }
                    if (z2 || z) {
                        if (!KeyguardHostView.this.mViewMediatorCallback.isScreenOn() || (!isVisibleToUser && !z)) {
                            KeyguardHostView.this.mViewMediatorCallback.playTrustedSound();
                        } else {
                            if (!isVisibleToUser) {
                                Log.i("KeyguardViewBase", "TrustAgent dismissed Keyguard.");
                            }
                            KeyguardHostView.this.dismiss(false, i2);
                        }
                    }
                }
            }
        };
        ((KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class)).registerCallback(this.mUpdateCallback);
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        ViewMediatorCallback viewMediatorCallback = this.mViewMediatorCallback;
        if (viewMediatorCallback != null) {
            viewMediatorCallback.keyguardDoneDrawing();
        }
    }

    public void setOnDismissAction(OnDismissAction onDismissAction, Runnable runnable) {
        Runnable runnable2 = this.mCancelAction;
        if (runnable2 != null) {
            runnable2.run();
            this.mCancelAction = null;
        }
        this.mDismissAction = onDismissAction;
        this.mCancelAction = runnable;
    }

    public boolean hasDismissActions() {
        return (this.mDismissAction == null && this.mCancelAction == null) ? false : true;
    }

    public void cancelDismissAction() {
        setOnDismissAction(null, null);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        this.mSecurityContainer = (KeyguardSecurityContainer) findViewById(C2011R$id.keyguard_security_container);
        LockPatternUtils lockPatternUtils = new LockPatternUtils(this.mContext);
        this.mLockPatternUtils = lockPatternUtils;
        this.mSecurityContainer.setLockPatternUtils(lockPatternUtils);
        this.mSecurityContainer.setSecurityCallback(this);
        this.mSecurityContainer.showPrimarySecurityScreen(false);
    }

    public void showPrimarySecurityScreen() {
        this.mSecurityContainer.showPrimarySecurityScreen(false);
    }

    public void showPromptReason(int i) {
        this.mSecurityContainer.showPromptReason(i);
    }

    public void showMessage(CharSequence charSequence, ColorStateList colorStateList) {
        this.mSecurityContainer.showMessage(charSequence, colorStateList);
    }

    public void showErrorMessage(CharSequence charSequence) {
        showMessage(charSequence, Utils.getColorError(this.mContext));
    }

    public boolean dismiss(int i) {
        return dismiss(false, i);
    }

    /* access modifiers changed from: protected */
    public KeyguardSecurityContainer getSecurityContainer() {
        return this.mSecurityContainer;
    }

    public boolean dismiss(boolean z, int i) {
        return this.mSecurityContainer.showNextSecurityScreenOrFinish(z, i);
    }

    public void finish(boolean z, int i) {
        boolean z2;
        OnDismissAction onDismissAction = this.mDismissAction;
        if (onDismissAction != null) {
            z2 = onDismissAction.onDismiss();
            this.mDismissAction = null;
            this.mCancelAction = null;
        } else {
            z2 = false;
        }
        ViewMediatorCallback viewMediatorCallback = this.mViewMediatorCallback;
        if (viewMediatorCallback == null) {
            return;
        }
        if (z2) {
            viewMediatorCallback.keyguardDonePending(z, i);
        } else {
            viewMediatorCallback.keyguardDone(z, i);
        }
    }

    public void reset() {
        this.mViewMediatorCallback.resetKeyguard();
    }

    public void onCancelClicked() {
        this.mViewMediatorCallback.onCancelClicked();
    }

    public void resetSecurityContainer() {
        this.mSecurityContainer.reset();
    }

    public void onSecurityModeChanged(SecurityMode securityMode, boolean z) {
        ViewMediatorCallback viewMediatorCallback = this.mViewMediatorCallback;
        if (viewMediatorCallback != null) {
            viewMediatorCallback.setNeedsInput(z);
        }
    }

    public CharSequence getAccessibilityTitleForCurrentMode() {
        return this.mSecurityContainer.getTitle();
    }

    public void userActivity() {
        ViewMediatorCallback viewMediatorCallback = this.mViewMediatorCallback;
        if (viewMediatorCallback != null) {
            viewMediatorCallback.userActivity();
        }
    }

    public void onPause() {
        this.mSecurityContainer.showPrimarySecurityScreen(true);
        this.mSecurityContainer.onPause();
        clearFocus();
    }

    public void onResume() {
        this.mSecurityContainer.onResume(1);
        requestFocus();
    }

    public void startAppearAnimation() {
        this.mSecurityContainer.startAppearAnimation();
    }

    public void startDisappearAnimation(Runnable runnable) {
        if (!this.mSecurityContainer.startDisappearAnimation(runnable) && runnable != null) {
            runnable.run();
        }
    }

    public void cleanUp() {
        getSecurityContainer().onPause();
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (interceptMediaKey(keyEvent)) {
            return true;
        }
        return super.dispatchKeyEvent(keyEvent);
    }

    public boolean interceptMediaKey(KeyEvent keyEvent) {
        int keyCode = keyEvent.getKeyCode();
        if (keyEvent.getAction() == 0) {
            if (!(keyCode == 79 || keyCode == 130 || keyCode == 222)) {
                if (!(keyCode == 126 || keyCode == 127)) {
                    switch (keyCode) {
                        case 85:
                            break;
                        case 86:
                        case 87:
                        case 88:
                        case 89:
                        case 90:
                        case 91:
                            break;
                    }
                }
                if (this.mTelephonyManager == null) {
                    this.mTelephonyManager = (TelephonyManager) getContext().getSystemService("phone");
                }
                TelephonyManager telephonyManager = this.mTelephonyManager;
                if (!(telephonyManager == null || telephonyManager.getCallState() == 0)) {
                    return true;
                }
            }
            handleMediaKeyEvent(keyEvent);
            return true;
        } else if (keyEvent.getAction() == 1) {
            if (!(keyCode == 79 || keyCode == 130 || keyCode == 222 || keyCode == 126 || keyCode == 127)) {
                switch (keyCode) {
                    case 85:
                    case 86:
                    case 87:
                    case 88:
                    case 89:
                    case 90:
                    case 91:
                        break;
                }
            }
            handleMediaKeyEvent(keyEvent);
            return true;
        }
        return false;
    }

    private void handleMediaKeyEvent(KeyEvent keyEvent) {
        synchronized (this) {
            if (this.mAudioManager == null) {
                this.mAudioManager = (AudioManager) getContext().getSystemService("audio");
            }
        }
        this.mAudioManager.dispatchMediaKeyEvent(keyEvent);
    }

    public boolean shouldEnableMenuKey() {
        return !getResources().getBoolean(C2007R$bool.config_disableMenuKeyInLockScreen) || ActivityManager.isRunningInTestHarness() || new File("/data/local/enable_menu_key").exists();
    }

    public void setViewMediatorCallback(ViewMediatorCallback viewMediatorCallback) {
        this.mViewMediatorCallback = viewMediatorCallback;
        viewMediatorCallback.setNeedsInput(this.mSecurityContainer.needsInput());
    }

    public void setLockPatternUtils(LockPatternUtils lockPatternUtils) {
        this.mLockPatternUtils = lockPatternUtils;
        this.mSecurityContainer.setLockPatternUtils(lockPatternUtils);
    }

    public SecurityMode getSecurityMode() {
        return this.mSecurityContainer.getSecurityMode();
    }

    public SecurityMode getCurrentSecurityMode() {
        return this.mSecurityContainer.getCurrentSecurityMode();
    }
}
