package com.android.keyguard;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.telephony.PinResult;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.android.systemui.C2006R$attr;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2015R$plurals;
import com.android.systemui.C2017R$string;
import com.android.systemui.Dependency;

public class KeyguardSimPinView extends KeyguardPinBasedInputView {
    /* access modifiers changed from: private */
    public CheckSimPin mCheckSimPinThread;
    /* access modifiers changed from: private */
    public int mRemainingAttempts;
    private AlertDialog mRemainingAttemptsDialog;
    /* access modifiers changed from: private */
    public boolean mShowDefaultMessage;
    private ImageView mSimImageView;
    /* access modifiers changed from: private */
    public ProgressDialog mSimUnlockProgressDialog;
    /* access modifiers changed from: private */
    public int mSubId;
    KeyguardUpdateMonitorCallback mUpdateMonitorCallback;

    private abstract class CheckSimPin extends Thread {
        private final String mPin;
        private int mSubId;

        /* access modifiers changed from: 0000 */
        public abstract void onSimCheckResponse(PinResult pinResult);

        protected CheckSimPin(String str, int i) {
            this.mPin = str;
            this.mSubId = i;
        }

        public void run() {
            StringBuilder sb = new StringBuilder();
            sb.append("call supplyPinReportResultForSubscriber(subid=");
            sb.append(this.mSubId);
            sb.append(")");
            String str = "KeyguardSimPinView";
            Log.v(str, sb.toString());
            final PinResult supplyPinReportPinResult = ((TelephonyManager) KeyguardSimPinView.this.mContext.getSystemService("phone")).createForSubscriptionId(this.mSubId).supplyPinReportPinResult(this.mPin);
            if (supplyPinReportPinResult == null) {
                Log.e(str, "Error result for supplyPinReportResult.");
                KeyguardSimPinView.this.post(new Runnable() {
                    public void run() {
                        CheckSimPin.this.onSimCheckResponse(PinResult.getDefaultFailedResult());
                    }
                });
                return;
            }
            StringBuilder sb2 = new StringBuilder();
            sb2.append("supplyPinReportResult returned: ");
            sb2.append(supplyPinReportPinResult.toString());
            Log.v(str, sb2.toString());
            KeyguardSimPinView.this.post(new Runnable() {
                public void run() {
                    CheckSimPin.this.onSimCheckResponse(supplyPinReportPinResult);
                }
            });
        }
    }

    /* access modifiers changed from: protected */
    public int getPromptReasonStringRes(int i) {
        return 0;
    }

    /* access modifiers changed from: protected */
    public boolean shouldLockout(long j) {
        return false;
    }

    public void startAppearAnimation() {
    }

    public boolean startDisappearAnimation(Runnable runnable) {
        return false;
    }

    public KeyguardSimPinView(Context context) {
        this(context, null);
    }

    public KeyguardSimPinView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mSimUnlockProgressDialog = null;
        this.mShowDefaultMessage = true;
        this.mRemainingAttempts = -1;
        this.mSubId = -1;
        this.mUpdateMonitorCallback = new KeyguardUpdateMonitorCallback() {
            public void onSimStateChanged(int i, int i2, int i3) {
                StringBuilder sb = new StringBuilder();
                sb.append("onSimStateChanged(subId=");
                sb.append(i);
                sb.append(",state=");
                sb.append(i3);
                sb.append(")");
                Log.v("KeyguardSimPinView", sb.toString());
                if (i3 != 5) {
                    KeyguardSimPinView.this.resetState();
                    return;
                }
                KeyguardSimPinView.this.mRemainingAttempts = -1;
                KeyguardSimPinView.this.resetState();
            }
        };
    }

    public void resetState() {
        super.resetState();
        Log.v("KeyguardSimPinView", "Resetting state");
        handleSubInfoChangeIfNeeded();
        if (this.mShowDefaultMessage) {
            showDefaultMessage();
        }
        ((KeyguardEsimArea) findViewById(C2011R$id.keyguard_esim_area)).setVisibility(KeyguardEsimArea.isEsimLocked(this.mContext, this.mSubId) ? 0 : 8);
    }

    /* access modifiers changed from: private */
    public void setLockedSimMessage() {
        String str;
        boolean isEsimLocked = KeyguardEsimArea.isEsimLocked(this.mContext, this.mSubId);
        TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService("phone");
        int activeModemCount = telephonyManager != null ? telephonyManager.getActiveModemCount() : 1;
        Resources resources = getResources();
        TypedArray obtainStyledAttributes = this.mContext.obtainStyledAttributes(new int[]{C2006R$attr.wallpaperTextColor});
        int color = obtainStyledAttributes.getColor(0, -1);
        obtainStyledAttributes.recycle();
        if (activeModemCount < 2) {
            str = resources.getString(C2017R$string.kg_sim_pin_instructions);
        } else {
            SubscriptionInfo subscriptionInfoForSubId = ((KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class)).getSubscriptionInfoForSubId(this.mSubId);
            String string = resources.getString(C2017R$string.kg_sim_pin_instructions_multi, new Object[]{subscriptionInfoForSubId != null ? subscriptionInfoForSubId.getDisplayName() : ""});
            if (subscriptionInfoForSubId != null) {
                color = subscriptionInfoForSubId.getIconTint();
            }
            str = string;
        }
        if (isEsimLocked) {
            str = resources.getString(C2017R$string.kg_sim_lock_esim_instructions, new Object[]{str});
        }
        if (this.mSecurityMessageDisplay != null && getVisibility() == 0) {
            this.mSecurityMessageDisplay.setMessage((CharSequence) str);
        }
        this.mSimImageView.setImageTintList(ColorStateList.valueOf(color));
    }

    private void showDefaultMessage() {
        setLockedSimMessage();
        if (this.mRemainingAttempts < 0) {
            new CheckSimPin("", this.mSubId) {
                /* access modifiers changed from: 0000 */
                public void onSimCheckResponse(PinResult pinResult) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("onSimCheckResponse  dummy One result ");
                    sb.append(pinResult.toString());
                    Log.d("KeyguardSimPinView", sb.toString());
                    if (pinResult.getAttemptsRemaining() >= 0) {
                        KeyguardSimPinView.this.mRemainingAttempts = pinResult.getAttemptsRemaining();
                        KeyguardSimPinView.this.setLockedSimMessage();
                    }
                }
            }.start();
        }
    }

    private void handleSubInfoChangeIfNeeded() {
        int nextSubIdForState = ((KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class)).getNextSubIdForState(2);
        if (nextSubIdForState != this.mSubId && SubscriptionManager.isValidSubscriptionId(nextSubIdForState)) {
            this.mSubId = nextSubIdForState;
            this.mShowDefaultMessage = true;
            this.mRemainingAttempts = -1;
        }
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        resetState();
    }

    /* access modifiers changed from: private */
    public String getPinPasswordErrorMessage(int i, boolean z) {
        String str;
        int i2;
        if (i == 0) {
            str = getContext().getString(C2017R$string.kg_password_wrong_pin_code_pukked);
        } else if (i > 0) {
            if (z) {
                i2 = C2015R$plurals.kg_password_default_pin_message;
            } else {
                i2 = C2015R$plurals.kg_password_wrong_pin_code;
            }
            str = getContext().getResources().getQuantityString(i2, i, new Object[]{Integer.valueOf(i)});
        } else {
            str = getContext().getString(z ? C2017R$string.kg_sim_pin_instructions : C2017R$string.kg_password_pin_failed);
        }
        if (KeyguardEsimArea.isEsimLocked(this.mContext, this.mSubId)) {
            str = getResources().getString(C2017R$string.kg_sim_lock_esim_instructions, new Object[]{str});
        }
        StringBuilder sb = new StringBuilder();
        sb.append("getPinPasswordErrorMessage: attemptsRemaining=");
        sb.append(i);
        sb.append(" displayMessage=");
        sb.append(str);
        Log.d("KeyguardSimPinView", sb.toString());
        return str;
    }

    /* access modifiers changed from: protected */
    public int getPasswordTextViewId() {
        return C2011R$id.simPinEntry;
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        View view = this.mEcaView;
        if (view instanceof EmergencyCarrierArea) {
            ((EmergencyCarrierArea) view).setCarrierTextVisible(true);
        }
        this.mSimImageView = (ImageView) findViewById(C2011R$id.keyguard_sim);
    }

    public void onResume(int i) {
        super.onResume(i);
        ((KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class)).registerCallback(this.mUpdateMonitorCallback);
        resetState();
    }

    public void onPause() {
        ProgressDialog progressDialog = this.mSimUnlockProgressDialog;
        if (progressDialog != null) {
            progressDialog.dismiss();
            this.mSimUnlockProgressDialog = null;
        }
        ((KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class)).removeCallback(this.mUpdateMonitorCallback);
    }

    private Dialog getSimUnlockProgressDialog() {
        if (this.mSimUnlockProgressDialog == null) {
            ProgressDialog progressDialog = new ProgressDialog(this.mContext);
            this.mSimUnlockProgressDialog = progressDialog;
            progressDialog.setMessage(this.mContext.getString(C2017R$string.kg_sim_unlock_progress_dialog_message));
            this.mSimUnlockProgressDialog.setIndeterminate(true);
            this.mSimUnlockProgressDialog.setCancelable(false);
            this.mSimUnlockProgressDialog.getWindow().setType(2009);
        }
        return this.mSimUnlockProgressDialog;
    }

    /* access modifiers changed from: private */
    public Dialog getSimRemainingAttemptsDialog(int i) {
        String pinPasswordErrorMessage = getPinPasswordErrorMessage(i, false);
        AlertDialog alertDialog = this.mRemainingAttemptsDialog;
        if (alertDialog == null) {
            Builder builder = new Builder(this.mContext);
            builder.setMessage(pinPasswordErrorMessage);
            builder.setCancelable(false);
            builder.setNeutralButton(C2017R$string.f31ok, null);
            AlertDialog create = builder.create();
            this.mRemainingAttemptsDialog = create;
            create.getWindow().setType(2009);
        } else {
            alertDialog.setMessage(pinPasswordErrorMessage);
        }
        return this.mRemainingAttemptsDialog;
    }

    /* access modifiers changed from: protected */
    public void verifyPasswordAndUnlock() {
        if (this.mPasswordEntry.getText().length() < 4) {
            this.mSecurityMessageDisplay.setMessage(C2017R$string.kg_invalid_sim_pin_hint);
            resetPasswordText(true, true);
            this.mCallback.userActivity();
            return;
        }
        getSimUnlockProgressDialog().show();
        if (this.mCheckSimPinThread == null) {
            C05543 r0 = new CheckSimPin(this.mPasswordEntry.getText(), this.mSubId) {
                /* access modifiers changed from: 0000 */
                public void onSimCheckResponse(final PinResult pinResult) {
                    KeyguardSimPinView.this.post(new Runnable() {
                        public void run() {
                            KeyguardSimPinView.this.mRemainingAttempts = pinResult.getAttemptsRemaining();
                            if (KeyguardSimPinView.this.mSimUnlockProgressDialog != null) {
                                KeyguardSimPinView.this.mSimUnlockProgressDialog.hide();
                            }
                            KeyguardSimPinView.this.resetPasswordText(true, pinResult.getType() != 0);
                            if (pinResult.getType() == 0) {
                                ((KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class)).reportSimUnlocked(KeyguardSimPinView.this.mSubId);
                                KeyguardSimPinView.this.mRemainingAttempts = -1;
                                KeyguardSimPinView.this.mShowDefaultMessage = true;
                                KeyguardSecurityCallback keyguardSecurityCallback = KeyguardSimPinView.this.mCallback;
                                if (keyguardSecurityCallback != null) {
                                    keyguardSecurityCallback.dismiss(true, KeyguardUpdateMonitor.getCurrentUser());
                                }
                            } else {
                                KeyguardSimPinView.this.mShowDefaultMessage = false;
                                if (pinResult.getType() != 1) {
                                    KeyguardSimPinView keyguardSimPinView = KeyguardSimPinView.this;
                                    keyguardSimPinView.mSecurityMessageDisplay.setMessage((CharSequence) keyguardSimPinView.getContext().getString(C2017R$string.kg_password_pin_failed));
                                } else if (pinResult.getAttemptsRemaining() <= 2) {
                                    KeyguardSimPinView.this.getSimRemainingAttemptsDialog(pinResult.getAttemptsRemaining()).show();
                                } else {
                                    KeyguardSimPinView keyguardSimPinView2 = KeyguardSimPinView.this;
                                    keyguardSimPinView2.mSecurityMessageDisplay.setMessage((CharSequence) keyguardSimPinView2.getPinPasswordErrorMessage(pinResult.getAttemptsRemaining(), false));
                                }
                                StringBuilder sb = new StringBuilder();
                                sb.append("verifyPasswordAndUnlock  CheckSimPin.onSimCheckResponse: ");
                                sb.append(pinResult);
                                sb.append(" attemptsRemaining=");
                                sb.append(pinResult.getAttemptsRemaining());
                                Log.d("KeyguardSimPinView", sb.toString());
                            }
                            KeyguardSimPinView.this.mCallback.userActivity();
                            KeyguardSimPinView.this.mCheckSimPinThread = null;
                        }
                    });
                }
            };
            this.mCheckSimPinThread = r0;
            r0.start();
        }
    }

    public CharSequence getTitle() {
        return getContext().getString(17040253);
    }
}
