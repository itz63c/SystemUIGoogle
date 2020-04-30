package com.android.keyguard;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.LatencyTracker;
import com.android.internal.widget.LockPatternChecker;
import com.android.internal.widget.LockPatternChecker.OnCheckCallback;
import com.android.internal.widget.LockPatternUtils;
import com.android.internal.widget.LockPatternView;
import com.android.internal.widget.LockPatternView.Cell;
import com.android.internal.widget.LockPatternView.CellState;
import com.android.internal.widget.LockPatternView.DisplayMode;
import com.android.internal.widget.LockPatternView.OnPatternListener;
import com.android.internal.widget.LockscreenCredential;
import com.android.keyguard.EmergencyButton.EmergencyButtonCallback;
import com.android.settingslib.animation.AppearAnimationCreator;
import com.android.settingslib.animation.AppearAnimationUtils;
import com.android.settingslib.animation.DisappearAnimationUtils;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2015R$plurals;
import com.android.systemui.C2017R$string;
import com.android.systemui.Dependency;
import java.util.List;

public class KeyguardPatternView extends LinearLayout implements KeyguardSecurityView, AppearAnimationCreator<CellState>, EmergencyButtonCallback {
    private final AppearAnimationUtils mAppearAnimationUtils;
    /* access modifiers changed from: private */
    public KeyguardSecurityCallback mCallback;
    /* access modifiers changed from: private */
    public Runnable mCancelPatternRunnable;
    private ViewGroup mContainer;
    private CountDownTimer mCountdownTimer;
    private final DisappearAnimationUtils mDisappearAnimationUtils;
    private final DisappearAnimationUtils mDisappearAnimationUtilsLocked;
    private View mEcaView;
    private final KeyguardUpdateMonitor mKeyguardUpdateMonitor;
    private long mLastPokeTime;
    private final Rect mLockPatternScreenBounds;
    /* access modifiers changed from: private */
    public LockPatternUtils mLockPatternUtils;
    /* access modifiers changed from: private */
    public LockPatternView mLockPatternView;
    /* access modifiers changed from: private */
    public AsyncTask<?, ?, ?> mPendingLockCheck;
    @VisibleForTesting
    KeyguardMessageArea mSecurityMessageDisplay;
    private final Rect mTempRect;
    private final int[] mTmpPosition;

    private class UnlockPatternListener implements OnPatternListener {
        public void onPatternCleared() {
        }

        private UnlockPatternListener() {
        }

        public void onPatternStart() {
            KeyguardPatternView.this.mLockPatternView.removeCallbacks(KeyguardPatternView.this.mCancelPatternRunnable);
            KeyguardPatternView.this.mSecurityMessageDisplay.setMessage((CharSequence) "");
        }

        public void onPatternCellAdded(List<Cell> list) {
            KeyguardPatternView.this.mCallback.userActivity();
            KeyguardPatternView.this.mCallback.onUserInput();
        }

        public void onPatternDetected(List<Cell> list) {
            KeyguardPatternView.this.mLockPatternView.disableInput();
            if (KeyguardPatternView.this.mPendingLockCheck != null) {
                KeyguardPatternView.this.mPendingLockCheck.cancel(false);
            }
            final int currentUser = KeyguardUpdateMonitor.getCurrentUser();
            if (list.size() < 4) {
                KeyguardPatternView.this.mLockPatternView.enableInput();
                onPatternChecked(currentUser, false, 0, false);
                return;
            }
            if (LatencyTracker.isEnabled(KeyguardPatternView.this.mContext)) {
                LatencyTracker.getInstance(KeyguardPatternView.this.mContext).onActionStart(3);
                LatencyTracker.getInstance(KeyguardPatternView.this.mContext).onActionStart(4);
            }
            KeyguardPatternView keyguardPatternView = KeyguardPatternView.this;
            keyguardPatternView.mPendingLockCheck = LockPatternChecker.checkCredential(keyguardPatternView.mLockPatternUtils, LockscreenCredential.createPattern(list), currentUser, new OnCheckCallback() {
                public void onEarlyMatched() {
                    if (LatencyTracker.isEnabled(KeyguardPatternView.this.mContext)) {
                        LatencyTracker.getInstance(KeyguardPatternView.this.mContext).onActionEnd(3);
                    }
                    UnlockPatternListener.this.onPatternChecked(currentUser, true, 0, true);
                }

                public void onChecked(boolean z, int i) {
                    if (LatencyTracker.isEnabled(KeyguardPatternView.this.mContext)) {
                        LatencyTracker.getInstance(KeyguardPatternView.this.mContext).onActionEnd(4);
                    }
                    KeyguardPatternView.this.mLockPatternView.enableInput();
                    KeyguardPatternView.this.mPendingLockCheck = null;
                    if (!z) {
                        UnlockPatternListener.this.onPatternChecked(currentUser, false, i, true);
                    }
                }

                public void onCancelled() {
                    if (LatencyTracker.isEnabled(KeyguardPatternView.this.mContext)) {
                        LatencyTracker.getInstance(KeyguardPatternView.this.mContext).onActionEnd(4);
                    }
                }
            });
            if (list.size() > 2) {
                KeyguardPatternView.this.mCallback.userActivity();
                KeyguardPatternView.this.mCallback.onUserInput();
            }
        }

        /* access modifiers changed from: private */
        public void onPatternChecked(int i, boolean z, int i2, boolean z2) {
            boolean z3 = KeyguardUpdateMonitor.getCurrentUser() == i;
            if (z) {
                KeyguardPatternView.this.mCallback.reportUnlockAttempt(i, true, 0);
                if (z3) {
                    KeyguardPatternView.this.mLockPatternView.setDisplayMode(DisplayMode.Correct);
                    KeyguardPatternView.this.mCallback.dismiss(true, i);
                    return;
                }
                return;
            }
            KeyguardPatternView.this.mLockPatternView.setDisplayMode(DisplayMode.Wrong);
            if (z2) {
                KeyguardPatternView.this.mCallback.reportUnlockAttempt(i, false, i2);
                if (i2 > 0) {
                    KeyguardPatternView.this.handleAttemptLockout(KeyguardPatternView.this.mLockPatternUtils.setLockoutAttemptDeadline(i, i2));
                }
            }
            if (i2 == 0) {
                KeyguardPatternView.this.mSecurityMessageDisplay.setMessage(C2017R$string.kg_wrong_pattern);
                KeyguardPatternView.this.mLockPatternView.postDelayed(KeyguardPatternView.this.mCancelPatternRunnable, 2000);
            }
        }
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public boolean needsInput() {
        return false;
    }

    public void onResume(int i) {
    }

    public KeyguardPatternView(Context context) {
        this(context, null);
    }

    public KeyguardPatternView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mTmpPosition = new int[2];
        this.mTempRect = new Rect();
        this.mLockPatternScreenBounds = new Rect();
        this.mCountdownTimer = null;
        this.mLastPokeTime = -7000;
        this.mCancelPatternRunnable = new Runnable() {
            public void run() {
                KeyguardPatternView.this.mLockPatternView.clearPattern();
            }
        };
        this.mKeyguardUpdateMonitor = (KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class);
        AppearAnimationUtils appearAnimationUtils = new AppearAnimationUtils(context, 220, 1.5f, 2.0f, AnimationUtils.loadInterpolator(this.mContext, 17563662));
        this.mAppearAnimationUtils = appearAnimationUtils;
        DisappearAnimationUtils disappearAnimationUtils = new DisappearAnimationUtils(context, 125, 1.2f, 0.6f, AnimationUtils.loadInterpolator(this.mContext, 17563663));
        this.mDisappearAnimationUtils = disappearAnimationUtils;
        DisappearAnimationUtils disappearAnimationUtils2 = new DisappearAnimationUtils(context, 187, 1.2f, 0.6f, AnimationUtils.loadInterpolator(this.mContext, 17563663));
        this.mDisappearAnimationUtilsLocked = disappearAnimationUtils2;
        getResources().getDimensionPixelSize(C2009R$dimen.disappear_y_translation);
    }

    public void setKeyguardCallback(KeyguardSecurityCallback keyguardSecurityCallback) {
        this.mCallback = keyguardSecurityCallback;
    }

    public void setLockPatternUtils(LockPatternUtils lockPatternUtils) {
        this.mLockPatternUtils = lockPatternUtils;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        LockPatternUtils lockPatternUtils = this.mLockPatternUtils;
        if (lockPatternUtils == null) {
            lockPatternUtils = new LockPatternUtils(this.mContext);
        }
        this.mLockPatternUtils = lockPatternUtils;
        LockPatternView findViewById = findViewById(C2011R$id.lockPatternView);
        this.mLockPatternView = findViewById;
        findViewById.setSaveEnabled(false);
        this.mLockPatternView.setOnPatternListener(new UnlockPatternListener());
        this.mLockPatternView.setInStealthMode(!this.mLockPatternUtils.isVisiblePatternEnabled(KeyguardUpdateMonitor.getCurrentUser()));
        this.mLockPatternView.setTactileFeedbackEnabled(this.mLockPatternUtils.isTactileFeedbackEnabled());
        this.mEcaView = findViewById(C2011R$id.keyguard_selector_fade_container);
        this.mContainer = (ViewGroup) findViewById(C2011R$id.container);
        EmergencyButton emergencyButton = (EmergencyButton) findViewById(C2011R$id.emergency_call_button);
        if (emergencyButton != null) {
            emergencyButton.setCallback(this);
        }
        View findViewById2 = findViewById(C2011R$id.cancel_button);
        if (findViewById2 != null) {
            findViewById2.setOnClickListener(new OnClickListener() {
                public final void onClick(View view) {
                    KeyguardPatternView.this.lambda$onFinishInflate$0$KeyguardPatternView(view);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onFinishInflate$0 */
    public /* synthetic */ void lambda$onFinishInflate$0$KeyguardPatternView(View view) {
        this.mCallback.reset();
        this.mCallback.onCancelClicked();
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mSecurityMessageDisplay = KeyguardMessageArea.findSecurityMessageDisplay(this);
    }

    public void onEmergencyButtonClickedWhenInCall() {
        this.mCallback.reset();
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        boolean onTouchEvent = super.onTouchEvent(motionEvent);
        long elapsedRealtime = SystemClock.elapsedRealtime() - this.mLastPokeTime;
        if (onTouchEvent && elapsedRealtime > 6900) {
            this.mLastPokeTime = SystemClock.elapsedRealtime();
        }
        boolean z = false;
        this.mTempRect.set(0, 0, 0, 0);
        offsetRectIntoDescendantCoords(this.mLockPatternView, this.mTempRect);
        Rect rect = this.mTempRect;
        motionEvent.offsetLocation((float) rect.left, (float) rect.top);
        if (this.mLockPatternView.dispatchTouchEvent(motionEvent) || onTouchEvent) {
            z = true;
        }
        Rect rect2 = this.mTempRect;
        motionEvent.offsetLocation((float) (-rect2.left), (float) (-rect2.top));
        return z;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        this.mLockPatternView.getLocationOnScreen(this.mTmpPosition);
        Rect rect = this.mLockPatternScreenBounds;
        int[] iArr = this.mTmpPosition;
        rect.set(iArr[0] - 40, iArr[1] - 40, iArr[0] + this.mLockPatternView.getWidth() + 40, this.mTmpPosition[1] + this.mLockPatternView.getHeight() + 40);
    }

    public void reset() {
        this.mLockPatternView.setInStealthMode(!this.mLockPatternUtils.isVisiblePatternEnabled(KeyguardUpdateMonitor.getCurrentUser()));
        this.mLockPatternView.enableInput();
        this.mLockPatternView.setEnabled(true);
        this.mLockPatternView.clearPattern();
        if (this.mSecurityMessageDisplay != null) {
            long lockoutAttemptDeadline = this.mLockPatternUtils.getLockoutAttemptDeadline(KeyguardUpdateMonitor.getCurrentUser());
            if (lockoutAttemptDeadline != 0) {
                handleAttemptLockout(lockoutAttemptDeadline);
            } else {
                displayDefaultSecurityMessage();
            }
        }
    }

    /* access modifiers changed from: private */
    public void displayDefaultSecurityMessage() {
        KeyguardMessageArea keyguardMessageArea = this.mSecurityMessageDisplay;
        if (keyguardMessageArea != null) {
            keyguardMessageArea.setMessage((CharSequence) "");
        }
    }

    public boolean disallowInterceptTouch(MotionEvent motionEvent) {
        return !this.mLockPatternView.isEmpty() || this.mLockPatternScreenBounds.contains((int) motionEvent.getRawX(), (int) motionEvent.getRawY());
    }

    /* access modifiers changed from: private */
    public void handleAttemptLockout(long j) {
        this.mLockPatternView.clearPattern();
        this.mLockPatternView.setEnabled(false);
        C05422 r0 = new CountDownTimer(((long) Math.ceil(((double) (j - SystemClock.elapsedRealtime())) / 1000.0d)) * 1000, 1000) {
            public void onTick(long j) {
                int round = (int) Math.round(((double) j) / 1000.0d);
                KeyguardPatternView keyguardPatternView = KeyguardPatternView.this;
                keyguardPatternView.mSecurityMessageDisplay.setMessage((CharSequence) keyguardPatternView.mContext.getResources().getQuantityString(C2015R$plurals.kg_too_many_failed_attempts_countdown, round, new Object[]{Integer.valueOf(round)}));
            }

            public void onFinish() {
                KeyguardPatternView.this.mLockPatternView.setEnabled(true);
                KeyguardPatternView.this.displayDefaultSecurityMessage();
            }
        };
        this.mCountdownTimer = r0.start();
    }

    public void onPause() {
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
        displayDefaultSecurityMessage();
    }

    public void showPromptReason(int i) {
        if (i == 0) {
            return;
        }
        if (i == 1) {
            this.mSecurityMessageDisplay.setMessage(C2017R$string.kg_prompt_reason_restart_pattern);
        } else if (i == 2) {
            this.mSecurityMessageDisplay.setMessage(C2017R$string.kg_prompt_reason_timeout_pattern);
        } else if (i == 3) {
            this.mSecurityMessageDisplay.setMessage(C2017R$string.kg_prompt_reason_device_admin);
        } else if (i == 4) {
            this.mSecurityMessageDisplay.setMessage(C2017R$string.kg_prompt_reason_user_request);
        } else if (i != 6) {
            this.mSecurityMessageDisplay.setMessage(C2017R$string.kg_prompt_reason_timeout_pattern);
        } else {
            this.mSecurityMessageDisplay.setMessage(C2017R$string.kg_prompt_reason_prepare_for_update_pattern);
        }
    }

    public void showMessage(CharSequence charSequence, ColorStateList colorStateList) {
        if (colorStateList != null) {
            this.mSecurityMessageDisplay.setNextMessageColor(colorStateList);
        }
        this.mSecurityMessageDisplay.setMessage(charSequence);
    }

    public void startAppearAnimation() {
        enableClipping(false);
        setAlpha(1.0f);
        setTranslationY(this.mAppearAnimationUtils.getStartTranslation());
        AppearAnimationUtils.startTranslationYAnimation(this, 0, 500, 0.0f, this.mAppearAnimationUtils.getInterpolator());
        this.mAppearAnimationUtils.startAnimation2d(this.mLockPatternView.getCellStates(), new Runnable() {
            public void run() {
                KeyguardPatternView.this.enableClipping(true);
            }
        }, this);
        if (!TextUtils.isEmpty(this.mSecurityMessageDisplay.getText())) {
            AppearAnimationUtils appearAnimationUtils = this.mAppearAnimationUtils;
            appearAnimationUtils.createAnimation((View) this.mSecurityMessageDisplay, 0, 220, appearAnimationUtils.getStartTranslation(), true, this.mAppearAnimationUtils.getInterpolator(), (Runnable) null);
        }
    }

    public boolean startDisappearAnimation(Runnable runnable) {
        DisappearAnimationUtils disappearAnimationUtils;
        float f = this.mKeyguardUpdateMonitor.needsSlowUnlockTransition() ? 1.5f : 1.0f;
        this.mLockPatternView.clearPattern();
        enableClipping(false);
        setTranslationY(0.0f);
        AppearAnimationUtils.startTranslationYAnimation(this, 0, (long) (300.0f * f), -this.mDisappearAnimationUtils.getStartTranslation(), this.mDisappearAnimationUtils.getInterpolator());
        if (this.mKeyguardUpdateMonitor.needsSlowUnlockTransition()) {
            disappearAnimationUtils = this.mDisappearAnimationUtilsLocked;
        } else {
            disappearAnimationUtils = this.mDisappearAnimationUtils;
        }
        disappearAnimationUtils.startAnimation2d(this.mLockPatternView.getCellStates(), new Runnable(runnable) {
            public final /* synthetic */ Runnable f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                KeyguardPatternView.this.lambda$startDisappearAnimation$1$KeyguardPatternView(this.f$1);
            }
        }, this);
        if (!TextUtils.isEmpty(this.mSecurityMessageDisplay.getText())) {
            DisappearAnimationUtils disappearAnimationUtils2 = this.mDisappearAnimationUtils;
            disappearAnimationUtils2.createAnimation((View) this.mSecurityMessageDisplay, 0, (long) (f * 200.0f), (-disappearAnimationUtils2.getStartTranslation()) * 3.0f, false, this.mDisappearAnimationUtils.getInterpolator(), (Runnable) null);
        }
        return true;
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$startDisappearAnimation$1 */
    public /* synthetic */ void lambda$startDisappearAnimation$1$KeyguardPatternView(Runnable runnable) {
        enableClipping(true);
        if (runnable != null) {
            runnable.run();
        }
    }

    /* access modifiers changed from: private */
    public void enableClipping(boolean z) {
        setClipChildren(z);
        this.mContainer.setClipToPadding(z);
        this.mContainer.setClipChildren(z);
    }

    public void createAnimation(CellState cellState, long j, long j2, float f, boolean z, Interpolator interpolator, Runnable runnable) {
        this.mLockPatternView.startCellStateAnimation(cellState, 1.0f, z ? 1.0f : 0.0f, z ? f : 0.0f, z ? 0.0f : f, z ? 0.0f : 1.0f, 1.0f, j, j2, interpolator, runnable);
        if (runnable != null) {
            this.mAppearAnimationUtils.createAnimation(this.mEcaView, j, j2, f, z, interpolator, (Runnable) null);
        }
    }

    public CharSequence getTitle() {
        return getContext().getString(17040251);
    }
}
