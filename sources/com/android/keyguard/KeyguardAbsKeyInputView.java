package com.android.keyguard;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import com.android.internal.util.LatencyTracker;
import com.android.internal.widget.LockPatternChecker;
import com.android.internal.widget.LockPatternChecker.OnCheckCallback;
import com.android.internal.widget.LockPatternUtils;
import com.android.internal.widget.LockscreenCredential;
import com.android.keyguard.EmergencyButton.EmergencyButtonCallback;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2015R$plurals;
import com.android.systemui.C2017R$string;

public abstract class KeyguardAbsKeyInputView extends LinearLayout implements KeyguardSecurityView, EmergencyButtonCallback {
    protected KeyguardSecurityCallback mCallback;
    private CountDownTimer mCountdownTimer;
    private boolean mDismissing;
    protected View mEcaView;
    protected boolean mEnableHaptics;
    protected LockPatternUtils mLockPatternUtils;
    protected AsyncTask<?, ?, ?> mPendingLockCheck;
    protected boolean mResumed;
    protected SecurityMessageDisplay mSecurityMessageDisplay;

    /* access modifiers changed from: protected */
    public abstract LockscreenCredential getEnteredCredential();

    /* access modifiers changed from: protected */
    public abstract int getPasswordTextViewId();

    /* access modifiers changed from: protected */
    public abstract int getPromptReasonStringRes(int i);

    public boolean needsInput() {
        return false;
    }

    /* access modifiers changed from: protected */
    public abstract void resetPasswordText(boolean z, boolean z2);

    /* access modifiers changed from: protected */
    public abstract void resetState();

    /* access modifiers changed from: protected */
    public abstract void setPasswordEntryEnabled(boolean z);

    /* access modifiers changed from: protected */
    public abstract void setPasswordEntryInputEnabled(boolean z);

    /* access modifiers changed from: protected */
    public boolean shouldLockout(long j) {
        return j != 0;
    }

    public boolean startDisappearAnimation(Runnable runnable) {
        return false;
    }

    public KeyguardAbsKeyInputView(Context context) {
        this(context, null);
    }

    public KeyguardAbsKeyInputView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mCountdownTimer = null;
    }

    public void setKeyguardCallback(KeyguardSecurityCallback keyguardSecurityCallback) {
        this.mCallback = keyguardSecurityCallback;
    }

    public void setLockPatternUtils(LockPatternUtils lockPatternUtils) {
        this.mLockPatternUtils = lockPatternUtils;
        this.mEnableHaptics = lockPatternUtils.isTactileFeedbackEnabled();
    }

    public void reset() {
        this.mDismissing = false;
        resetPasswordText(false, false);
        long lockoutAttemptDeadline = this.mLockPatternUtils.getLockoutAttemptDeadline(KeyguardUpdateMonitor.getCurrentUser());
        if (shouldLockout(lockoutAttemptDeadline)) {
            handleAttemptLockout(lockoutAttemptDeadline);
        } else {
            resetState();
        }
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        this.mLockPatternUtils = new LockPatternUtils(this.mContext);
        this.mEcaView = findViewById(C2011R$id.keyguard_selector_fade_container);
        EmergencyButton emergencyButton = (EmergencyButton) findViewById(C2011R$id.emergency_call_button);
        if (emergencyButton != null) {
            emergencyButton.setCallback(this);
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mSecurityMessageDisplay = KeyguardMessageArea.findSecurityMessageDisplay(this);
    }

    public void onEmergencyButtonClickedWhenInCall() {
        this.mCallback.reset();
    }

    /* access modifiers changed from: protected */
    public int getWrongPasswordStringId() {
        return C2017R$string.kg_wrong_password;
    }

    /* access modifiers changed from: protected */
    public void verifyPasswordAndUnlock() {
        if (!this.mDismissing) {
            final LockscreenCredential enteredCredential = getEnteredCredential();
            setPasswordEntryInputEnabled(false);
            AsyncTask<?, ?, ?> asyncTask = this.mPendingLockCheck;
            if (asyncTask != null) {
                asyncTask.cancel(false);
            }
            final int currentUser = KeyguardUpdateMonitor.getCurrentUser();
            if (enteredCredential.size() <= 3) {
                setPasswordEntryInputEnabled(true);
                onPasswordChecked(currentUser, false, 0, false);
                enteredCredential.zeroize();
                return;
            }
            if (LatencyTracker.isEnabled(this.mContext)) {
                LatencyTracker.getInstance(this.mContext).onActionStart(3);
                LatencyTracker.getInstance(this.mContext).onActionStart(4);
            }
            this.mPendingLockCheck = LockPatternChecker.checkCredential(this.mLockPatternUtils, enteredCredential, currentUser, new OnCheckCallback() {
                public void onEarlyMatched() {
                    if (LatencyTracker.isEnabled(KeyguardAbsKeyInputView.this.mContext)) {
                        LatencyTracker.getInstance(KeyguardAbsKeyInputView.this.mContext).onActionEnd(3);
                    }
                    KeyguardAbsKeyInputView.this.onPasswordChecked(currentUser, true, 0, true);
                    enteredCredential.zeroize();
                }

                public void onChecked(boolean z, int i) {
                    if (LatencyTracker.isEnabled(KeyguardAbsKeyInputView.this.mContext)) {
                        LatencyTracker.getInstance(KeyguardAbsKeyInputView.this.mContext).onActionEnd(4);
                    }
                    KeyguardAbsKeyInputView.this.setPasswordEntryInputEnabled(true);
                    KeyguardAbsKeyInputView keyguardAbsKeyInputView = KeyguardAbsKeyInputView.this;
                    keyguardAbsKeyInputView.mPendingLockCheck = null;
                    if (!z) {
                        keyguardAbsKeyInputView.onPasswordChecked(currentUser, false, i, true);
                    }
                    enteredCredential.zeroize();
                }

                public void onCancelled() {
                    if (LatencyTracker.isEnabled(KeyguardAbsKeyInputView.this.mContext)) {
                        LatencyTracker.getInstance(KeyguardAbsKeyInputView.this.mContext).onActionEnd(4);
                    }
                    enteredCredential.zeroize();
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void onPasswordChecked(int i, boolean z, int i2, boolean z2) {
        boolean z3 = KeyguardUpdateMonitor.getCurrentUser() == i;
        if (z) {
            this.mCallback.reportUnlockAttempt(i, true, 0);
            if (z3) {
                this.mDismissing = true;
                this.mCallback.dismiss(true, i);
            }
        } else {
            if (z2) {
                this.mCallback.reportUnlockAttempt(i, false, i2);
                if (i2 > 0) {
                    handleAttemptLockout(this.mLockPatternUtils.setLockoutAttemptDeadline(i, i2));
                }
            }
            if (i2 == 0) {
                this.mSecurityMessageDisplay.setMessage(getWrongPasswordStringId());
            }
        }
        resetPasswordText(true, !z);
    }

    /* access modifiers changed from: protected */
    public void handleAttemptLockout(long j) {
        setPasswordEntryEnabled(false);
        C05242 r0 = new CountDownTimer(((long) Math.ceil(((double) (j - SystemClock.elapsedRealtime())) / 1000.0d)) * 1000, 1000) {
            public void onTick(long j) {
                int round = (int) Math.round(((double) j) / 1000.0d);
                KeyguardAbsKeyInputView keyguardAbsKeyInputView = KeyguardAbsKeyInputView.this;
                keyguardAbsKeyInputView.mSecurityMessageDisplay.setMessage((CharSequence) keyguardAbsKeyInputView.mContext.getResources().getQuantityString(C2015R$plurals.kg_too_many_failed_attempts_countdown, round, new Object[]{Integer.valueOf(round)}));
            }

            public void onFinish() {
                KeyguardAbsKeyInputView.this.mSecurityMessageDisplay.setMessage((CharSequence) "");
                KeyguardAbsKeyInputView.this.resetState();
            }
        };
        this.mCountdownTimer = r0.start();
    }

    /* access modifiers changed from: protected */
    public void onUserInput() {
        KeyguardSecurityCallback keyguardSecurityCallback = this.mCallback;
        if (keyguardSecurityCallback != null) {
            keyguardSecurityCallback.userActivity();
            this.mCallback.onUserInput();
        }
        this.mSecurityMessageDisplay.setMessage((CharSequence) "");
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i != 0) {
            onUserInput();
        }
        return false;
    }

    public void onPause() {
        this.mResumed = false;
        CountDownTimer countDownTimer = this.mCountdownTimer;
        if (countDownTimer != null) {
            countDownTimer.cancel();
            this.mCountdownTimer = null;
        }
        AsyncTask<?, ?, ?> asyncTask = this.mPendingLockCheck;
        if (asyncTask != null) {
            asyncTask.cancel(false);
            this.mPendingLockCheck = null;
        }
        reset();
    }

    public void onResume(int i) {
        this.mResumed = true;
    }

    public void showPromptReason(int i) {
        if (i != 0) {
            int promptReasonStringRes = getPromptReasonStringRes(i);
            if (promptReasonStringRes != 0) {
                this.mSecurityMessageDisplay.setMessage(promptReasonStringRes);
            }
        }
    }

    public void showMessage(CharSequence charSequence, ColorStateList colorStateList) {
        if (colorStateList != null) {
            this.mSecurityMessageDisplay.setNextMessageColor(colorStateList);
        }
        this.mSecurityMessageDisplay.setMessage(charSequence);
    }

    public void doHapticKeyClick() {
        if (this.mEnableHaptics) {
            performHapticFeedback(1, 3);
        }
    }
}
