package com.android.systemui.biometrics;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.pm.UserInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.internal.widget.LockPatternUtils;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2010R$drawable;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2017R$string;
import com.android.systemui.Interpolators;

public abstract class AuthCredentialView extends LinearLayout {
    private final AccessibilityManager mAccessibilityManager = ((AccessibilityManager) this.mContext.getSystemService(AccessibilityManager.class));
    private Bundle mBiometricPromptBundle;
    protected Callback mCallback;
    protected final Runnable mClearErrorRunnable = new Runnable() {
        public void run() {
            TextView textView = AuthCredentialView.this.mErrorView;
            if (textView != null) {
                textView.setText("");
            }
        }
    };
    protected AuthContainerView mContainerView;
    protected int mCredentialType;
    private TextView mDescriptionView;
    private final DevicePolicyManager mDevicePolicyManager = ((DevicePolicyManager) this.mContext.getSystemService(DevicePolicyManager.class));
    protected int mEffectiveUserId;
    protected ErrorTimer mErrorTimer;
    protected TextView mErrorView;
    protected final Handler mHandler = new Handler(Looper.getMainLooper());
    private ImageView mIconView;
    protected final LockPatternUtils mLockPatternUtils = new LockPatternUtils(this.mContext);
    protected long mOperationId;
    private AuthPanelController mPanelController;
    protected AsyncTask<?, ?, ?> mPendingLockCheck;
    private boolean mShouldAnimateContents;
    private boolean mShouldAnimatePanel;
    private TextView mSubtitleView;
    private TextView mTitleView;
    protected int mUserId;
    private final UserManager mUserManager = ((UserManager) this.mContext.getSystemService(UserManager.class));

    interface Callback {
        void onCredentialMatched(byte[] bArr);
    }

    protected static class ErrorTimer extends CountDownTimer {
        private final Context mContext;
        private final TextView mErrorView;

        public ErrorTimer(Context context, long j, long j2, TextView textView) {
            super(j, j2);
            this.mErrorView = textView;
            this.mContext = context;
        }

        public void onTick(long j) {
            int i = (int) (j / 1000);
            this.mErrorView.setText(this.mContext.getString(C2017R$string.biometric_dialog_credential_too_many_attempts, new Object[]{Integer.valueOf(i)}));
        }
    }

    /* access modifiers changed from: protected */
    public void onErrorTimeoutFinish() {
    }

    public AuthCredentialView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    public void showError(String str) {
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.removeCallbacks(this.mClearErrorRunnable);
            this.mHandler.postDelayed(this.mClearErrorRunnable, 3000);
        }
        TextView textView = this.mErrorView;
        if (textView != null) {
            textView.setText(str);
        }
    }

    private void setTextOrHide(TextView textView, CharSequence charSequence) {
        if (TextUtils.isEmpty(charSequence)) {
            textView.setVisibility(8);
        } else {
            textView.setText(charSequence);
        }
        Utils.notifyAccessibilityContentChanged(this.mAccessibilityManager, this);
    }

    private void setText(TextView textView, CharSequence charSequence) {
        textView.setText(charSequence);
    }

    /* access modifiers changed from: 0000 */
    public void setUserId(int i) {
        this.mUserId = i;
    }

    /* access modifiers changed from: 0000 */
    public void setOperationId(long j) {
        this.mOperationId = j;
    }

    /* access modifiers changed from: 0000 */
    public void setEffectiveUserId(int i) {
        this.mEffectiveUserId = i;
    }

    /* access modifiers changed from: 0000 */
    public void setCredentialType(int i) {
        this.mCredentialType = i;
    }

    /* access modifiers changed from: 0000 */
    public void setCallback(Callback callback) {
        this.mCallback = callback;
    }

    /* access modifiers changed from: 0000 */
    public void setBiometricPromptBundle(Bundle bundle) {
        this.mBiometricPromptBundle = bundle;
    }

    /* access modifiers changed from: 0000 */
    public void setPanelController(AuthPanelController authPanelController, boolean z) {
        this.mPanelController = authPanelController;
        this.mShouldAnimatePanel = z;
    }

    /* access modifiers changed from: 0000 */
    public void setShouldAnimateContents(boolean z) {
        this.mShouldAnimateContents = z;
    }

    /* access modifiers changed from: 0000 */
    public void setContainerView(AuthContainerView authContainerView) {
        this.mContainerView = authContainerView;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        Drawable drawable;
        super.onAttachedToWindow();
        setText(this.mTitleView, getTitle(this.mBiometricPromptBundle));
        setTextOrHide(this.mSubtitleView, getSubtitle(this.mBiometricPromptBundle));
        setTextOrHide(this.mDescriptionView, getDescription(this.mBiometricPromptBundle));
        if (Utils.isManagedProfile(this.mContext, this.mEffectiveUserId)) {
            drawable = getResources().getDrawable(C2010R$drawable.auth_dialog_enterprise, this.mContext.getTheme());
        } else {
            drawable = getResources().getDrawable(C2010R$drawable.auth_dialog_lock, this.mContext.getTheme());
        }
        this.mIconView.setImageDrawable(drawable);
        if (this.mShouldAnimateContents) {
            setTranslationY(getResources().getDimension(C2009R$dimen.biometric_dialog_credential_translation_offset));
            setAlpha(0.0f);
            postOnAnimation(new Runnable() {
                public final void run() {
                    AuthCredentialView.this.lambda$onAttachedToWindow$0$AuthCredentialView();
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onAttachedToWindow$0 */
    public /* synthetic */ void lambda$onAttachedToWindow$0$AuthCredentialView() {
        animate().translationY(0.0f).setDuration(150).alpha(1.0f).setInterpolator(Interpolators.LINEAR_OUT_SLOW_IN).withLayer().start();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ErrorTimer errorTimer = this.mErrorTimer;
        if (errorTimer != null) {
            errorTimer.cancel();
        }
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mTitleView = (TextView) findViewById(C2011R$id.title);
        this.mSubtitleView = (TextView) findViewById(C2011R$id.subtitle);
        this.mDescriptionView = (TextView) findViewById(C2011R$id.description);
        this.mIconView = (ImageView) findViewById(C2011R$id.icon);
        this.mErrorView = (TextView) findViewById(C2011R$id.error);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (this.mShouldAnimatePanel) {
            this.mPanelController.setUseFullScreen(true);
            AuthPanelController authPanelController = this.mPanelController;
            authPanelController.updateForContentDimensions(authPanelController.getContainerWidth(), this.mPanelController.getContainerHeight(), 0);
            this.mShouldAnimatePanel = false;
        }
    }

    /* access modifiers changed from: protected */
    public void onCredentialVerified(byte[] bArr, int i) {
        int i2;
        if (bArr != null) {
            this.mClearErrorRunnable.run();
            this.mCallback.onCredentialMatched(bArr);
        } else if (i > 0) {
            this.mHandler.removeCallbacks(this.mClearErrorRunnable);
            C07542 r0 = new ErrorTimer(this.mContext, this.mLockPatternUtils.setLockoutAttemptDeadline(this.mEffectiveUserId, i) - SystemClock.elapsedRealtime(), 1000, this.mErrorView) {
                public void onFinish() {
                    AuthCredentialView.this.onErrorTimeoutFinish();
                    AuthCredentialView.this.mClearErrorRunnable.run();
                }
            };
            this.mErrorTimer = r0;
            r0.start();
        } else if (!reportFailedAttempt()) {
            int i3 = this.mCredentialType;
            if (i3 == 1) {
                i2 = C2017R$string.biometric_dialog_wrong_pin;
            } else if (i3 != 2) {
                i2 = C2017R$string.biometric_dialog_wrong_password;
            } else {
                i2 = C2017R$string.biometric_dialog_wrong_pattern;
            }
            showError(getResources().getString(i2));
        }
    }

    private boolean reportFailedAttempt() {
        boolean updateErrorMessage = updateErrorMessage(this.mLockPatternUtils.getCurrentFailedPasswordAttempts(this.mEffectiveUserId) + 1);
        this.mLockPatternUtils.reportFailedPasswordAttempt(this.mEffectiveUserId);
        return updateErrorMessage;
    }

    private boolean updateErrorMessage(int i) {
        int maximumFailedPasswordsForWipe = this.mLockPatternUtils.getMaximumFailedPasswordsForWipe(this.mEffectiveUserId);
        if (maximumFailedPasswordsForWipe <= 0 || i <= 0) {
            return false;
        }
        if (this.mErrorView != null) {
            showError(getResources().getString(C2017R$string.biometric_dialog_credential_attempts_before_wipe, new Object[]{Integer.valueOf(i), Integer.valueOf(maximumFailedPasswordsForWipe)}));
        }
        if (maximumFailedPasswordsForWipe - i <= 0) {
            showNowWipingMessage();
            this.mContainerView.animateAway(5);
        }
        return true;
    }

    private void showNowWipingMessage() {
        AlertDialog create = new Builder(this.mContext).setMessage(getNowWipingMessageRes(getUserTypeForWipe())).setPositiveButton(C2017R$string.biometric_dialog_now_wiping_dialog_dismiss, null).create();
        create.getWindow().setType(2003);
        create.show();
    }

    private int getUserTypeForWipe() {
        UserInfo userInfo = this.mUserManager.getUserInfo(this.mDevicePolicyManager.getProfileWithMinimumFailedPasswordsForWipe(this.mEffectiveUserId));
        if (userInfo == null || userInfo.isPrimary()) {
            return 1;
        }
        return userInfo.isManagedProfile() ? 2 : 3;
    }

    private static int getNowWipingMessageRes(int i) {
        if (i == 1) {
            return C2017R$string.biometric_dialog_failed_attempts_now_wiping_device;
        }
        if (i == 2) {
            return C2017R$string.biometric_dialog_failed_attempts_now_wiping_profile;
        }
        if (i == 3) {
            return C2017R$string.biometric_dialog_failed_attempts_now_wiping_user;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Unrecognized user type:");
        sb.append(i);
        throw new IllegalArgumentException(sb.toString());
    }

    private static CharSequence getTitle(Bundle bundle) {
        CharSequence charSequence = bundle.getCharSequence("device_credential_title");
        return charSequence != null ? charSequence : bundle.getCharSequence("title");
    }

    private static CharSequence getSubtitle(Bundle bundle) {
        CharSequence charSequence = bundle.getCharSequence("device_credential_subtitle");
        return charSequence != null ? charSequence : bundle.getCharSequence("subtitle");
    }

    private static CharSequence getDescription(Bundle bundle) {
        CharSequence charSequence = bundle.getCharSequence("device_credential_description");
        return charSequence != null ? charSequence : bundle.getCharSequence("description");
    }
}
