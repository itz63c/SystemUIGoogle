package com.android.keyguard;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.ColorStateList;
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

public class KeyguardSimPukView extends KeyguardPinBasedInputView {
    /* access modifiers changed from: private */
    public CheckSimPuk mCheckSimPukThread;
    /* access modifiers changed from: private */
    public String mPinText;
    /* access modifiers changed from: private */
    public String mPukText;
    /* access modifiers changed from: private */
    public int mRemainingAttempts;
    private AlertDialog mRemainingAttemptsDialog;
    /* access modifiers changed from: private */
    public boolean mShowDefaultMessage;
    private ImageView mSimImageView;
    /* access modifiers changed from: private */
    public ProgressDialog mSimUnlockProgressDialog;
    /* access modifiers changed from: private */
    public StateMachine mStateMachine;
    /* access modifiers changed from: private */
    public int mSubId;
    KeyguardUpdateMonitorCallback mUpdateMonitorCallback;

    private abstract class CheckSimPuk extends Thread {
        private final String mPin;
        private final String mPuk;
        private final int mSubId;

        /* access modifiers changed from: 0000 */
        public abstract void onSimLockChangedResponse(PinResult pinResult);

        protected CheckSimPuk(String str, String str2, int i) {
            this.mPuk = str;
            this.mPin = str2;
            this.mSubId = i;
        }

        public void run() {
            final PinResult supplyPukReportPinResult = ((TelephonyManager) KeyguardSimPukView.this.mContext.getSystemService("phone")).createForSubscriptionId(this.mSubId).supplyPukReportPinResult(this.mPuk, this.mPin);
            if (supplyPukReportPinResult == null) {
                Log.e("KeyguardSimPukView", "Error result for supplyPukReportResult.");
                KeyguardSimPukView.this.post(new Runnable() {
                    public void run() {
                        CheckSimPuk.this.onSimLockChangedResponse(PinResult.getDefaultFailedResult());
                    }
                });
                return;
            }
            KeyguardSimPukView.this.post(new Runnable() {
                public void run() {
                    CheckSimPuk.this.onSimLockChangedResponse(supplyPukReportPinResult);
                }
            });
        }
    }

    private class StateMachine {
        private int state;

        private StateMachine() {
            this.state = 0;
        }

        public void next() {
            int i;
            int i2 = this.state;
            if (i2 == 0) {
                if (KeyguardSimPukView.this.checkPuk()) {
                    this.state = 1;
                    i = C2017R$string.kg_puk_enter_pin_hint;
                } else {
                    i = C2017R$string.kg_invalid_sim_puk_hint;
                }
            } else if (i2 == 1) {
                if (KeyguardSimPukView.this.checkPin()) {
                    this.state = 2;
                    i = C2017R$string.kg_enter_confirm_pin_hint;
                } else {
                    i = C2017R$string.kg_invalid_sim_pin_hint;
                }
            } else if (i2 != 2) {
                i = 0;
            } else if (KeyguardSimPukView.this.confirmPin()) {
                this.state = 3;
                i = C2017R$string.keyguard_sim_unlock_progress_dialog_message;
                KeyguardSimPukView.this.updateSim();
            } else {
                this.state = 1;
                i = C2017R$string.kg_invalid_confirm_pin_hint;
            }
            KeyguardSimPukView.this.resetPasswordText(true, true);
            if (i != 0) {
                KeyguardSimPukView.this.mSecurityMessageDisplay.setMessage(i);
            }
        }

        /* access modifiers changed from: 0000 */
        public void reset() {
            String str = "";
            KeyguardSimPukView.this.mPinText = str;
            KeyguardSimPukView.this.mPukText = str;
            int i = 0;
            this.state = 0;
            KeyguardSimPukView.this.handleSubInfoChangeIfNeeded();
            if (KeyguardSimPukView.this.mShowDefaultMessage) {
                KeyguardSimPukView.this.showDefaultMessage();
            }
            KeyguardEsimArea keyguardEsimArea = (KeyguardEsimArea) KeyguardSimPukView.this.findViewById(C2011R$id.keyguard_esim_area);
            if (!KeyguardEsimArea.isEsimLocked(KeyguardSimPukView.this.mContext, KeyguardSimPukView.this.mSubId)) {
                i = 8;
            }
            keyguardEsimArea.setVisibility(i);
            KeyguardSimPukView.this.mPasswordEntry.requestFocus();
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

    public KeyguardSimPukView(Context context) {
        this(context, null);
    }

    public KeyguardSimPukView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mSimUnlockProgressDialog = null;
        this.mShowDefaultMessage = true;
        this.mRemainingAttempts = -1;
        this.mStateMachine = new StateMachine();
        this.mSubId = -1;
        this.mUpdateMonitorCallback = new KeyguardUpdateMonitorCallback() {
            public void onSimStateChanged(int i, int i2, int i3) {
                if (i3 != 5) {
                    KeyguardSimPukView.this.resetState();
                    return;
                }
                KeyguardSimPukView.this.mRemainingAttempts = -1;
                KeyguardSimPukView.this.mShowDefaultMessage = true;
                KeyguardSecurityCallback keyguardSecurityCallback = KeyguardSimPukView.this.mCallback;
                if (keyguardSecurityCallback != null) {
                    keyguardSecurityCallback.dismiss(true, KeyguardUpdateMonitor.getCurrentUser());
                }
            }
        };
    }

    /* access modifiers changed from: private */
    public void showDefaultMessage() {
        String str;
        int i = this.mRemainingAttempts;
        if (i >= 0) {
            this.mSecurityMessageDisplay.setMessage((CharSequence) getPukPasswordErrorMessage(i, true));
            return;
        }
        boolean isEsimLocked = KeyguardEsimArea.isEsimLocked(this.mContext, this.mSubId);
        TelephonyManager telephonyManager = (TelephonyManager) this.mContext.getSystemService("phone");
        int activeModemCount = telephonyManager != null ? telephonyManager.getActiveModemCount() : 1;
        Resources resources = getResources();
        TypedArray obtainStyledAttributes = this.mContext.obtainStyledAttributes(new int[]{C2006R$attr.wallpaperTextColor});
        int color = obtainStyledAttributes.getColor(0, -1);
        obtainStyledAttributes.recycle();
        String str2 = "";
        if (activeModemCount < 2) {
            str = resources.getString(C2017R$string.kg_puk_enter_puk_hint);
        } else {
            SubscriptionInfo subscriptionInfoForSubId = ((KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class)).getSubscriptionInfoForSubId(this.mSubId);
            String string = resources.getString(C2017R$string.kg_puk_enter_puk_hint_multi, new Object[]{subscriptionInfoForSubId != null ? subscriptionInfoForSubId.getDisplayName() : str2});
            if (subscriptionInfoForSubId != null) {
                color = subscriptionInfoForSubId.getIconTint();
            }
            str = string;
        }
        if (isEsimLocked) {
            str = resources.getString(C2017R$string.kg_sim_lock_esim_instructions, new Object[]{str});
        }
        SecurityMessageDisplay securityMessageDisplay = this.mSecurityMessageDisplay;
        if (securityMessageDisplay != null) {
            securityMessageDisplay.setMessage((CharSequence) str);
        }
        this.mSimImageView.setImageTintList(ColorStateList.valueOf(color));
        new CheckSimPuk(str2, str2, this.mSubId) {
            /* access modifiers changed from: 0000 */
            public void onSimLockChangedResponse(PinResult pinResult) {
                String str = "KeyguardSimPukView";
                if (pinResult == null) {
                    Log.e(str, "onSimCheckResponse, pin result is NULL");
                    return;
                }
                StringBuilder sb = new StringBuilder();
                sb.append("onSimCheckResponse  dummy One result ");
                sb.append(pinResult.toString());
                Log.d(str, sb.toString());
                if (pinResult.getAttemptsRemaining() >= 0) {
                    KeyguardSimPukView.this.mRemainingAttempts = pinResult.getAttemptsRemaining();
                    KeyguardSimPukView keyguardSimPukView = KeyguardSimPukView.this;
                    keyguardSimPukView.mSecurityMessageDisplay.setMessage((CharSequence) keyguardSimPukView.getPukPasswordErrorMessage(pinResult.getAttemptsRemaining(), true));
                }
            }
        }.start();
    }

    /* access modifiers changed from: private */
    public void handleSubInfoChangeIfNeeded() {
        int nextSubIdForState = ((KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class)).getNextSubIdForState(3);
        if (nextSubIdForState != this.mSubId && SubscriptionManager.isValidSubscriptionId(nextSubIdForState)) {
            this.mSubId = nextSubIdForState;
            this.mShowDefaultMessage = true;
            this.mRemainingAttempts = -1;
        }
    }

    /* access modifiers changed from: private */
    public String getPukPasswordErrorMessage(int i, boolean z) {
        String str;
        int i2;
        int i3;
        if (i == 0) {
            str = getContext().getString(C2017R$string.kg_password_wrong_puk_code_dead);
        } else if (i > 0) {
            if (z) {
                i3 = C2015R$plurals.kg_password_default_puk_message;
            } else {
                i3 = C2015R$plurals.kg_password_wrong_puk_code;
            }
            str = getContext().getResources().getQuantityString(i3, i, new Object[]{Integer.valueOf(i)});
        } else {
            if (z) {
                i2 = C2017R$string.kg_puk_enter_puk_hint;
            } else {
                i2 = C2017R$string.kg_password_puk_failed;
            }
            str = getContext().getString(i2);
        }
        if (!KeyguardEsimArea.isEsimLocked(this.mContext, this.mSubId)) {
            return str;
        }
        return getResources().getString(C2017R$string.kg_sim_lock_esim_instructions, new Object[]{str});
    }

    public void resetState() {
        super.resetState();
        this.mStateMachine.reset();
    }

    /* access modifiers changed from: protected */
    public int getPasswordTextViewId() {
        return C2011R$id.pukEntry;
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

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        ((KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class)).registerCallback(this.mUpdateMonitorCallback);
        resetState();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ((KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class)).removeCallback(this.mUpdateMonitorCallback);
    }

    public void onPause() {
        ProgressDialog progressDialog = this.mSimUnlockProgressDialog;
        if (progressDialog != null) {
            progressDialog.dismiss();
            this.mSimUnlockProgressDialog = null;
        }
    }

    private Dialog getSimUnlockProgressDialog() {
        if (this.mSimUnlockProgressDialog == null) {
            ProgressDialog progressDialog = new ProgressDialog(this.mContext);
            this.mSimUnlockProgressDialog = progressDialog;
            progressDialog.setMessage(this.mContext.getString(C2017R$string.kg_sim_unlock_progress_dialog_message));
            this.mSimUnlockProgressDialog.setIndeterminate(true);
            this.mSimUnlockProgressDialog.setCancelable(false);
            if (!(this.mContext instanceof Activity)) {
                this.mSimUnlockProgressDialog.getWindow().setType(2009);
            }
        }
        return this.mSimUnlockProgressDialog;
    }

    /* access modifiers changed from: private */
    public Dialog getPukRemainingAttemptsDialog(int i) {
        String pukPasswordErrorMessage = getPukPasswordErrorMessage(i, false);
        AlertDialog alertDialog = this.mRemainingAttemptsDialog;
        if (alertDialog == null) {
            Builder builder = new Builder(this.mContext);
            builder.setMessage(pukPasswordErrorMessage);
            builder.setCancelable(false);
            builder.setNeutralButton(C2017R$string.f31ok, null);
            AlertDialog create = builder.create();
            this.mRemainingAttemptsDialog = create;
            create.getWindow().setType(2009);
        } else {
            alertDialog.setMessage(pukPasswordErrorMessage);
        }
        return this.mRemainingAttemptsDialog;
    }

    /* access modifiers changed from: private */
    public boolean checkPuk() {
        if (this.mPasswordEntry.getText().length() != 8) {
            return false;
        }
        this.mPukText = this.mPasswordEntry.getText();
        return true;
    }

    /* access modifiers changed from: private */
    public boolean checkPin() {
        int length = this.mPasswordEntry.getText().length();
        if (length < 4 || length > 8) {
            return false;
        }
        this.mPinText = this.mPasswordEntry.getText();
        return true;
    }

    public boolean confirmPin() {
        return this.mPinText.equals(this.mPasswordEntry.getText());
    }

    /* access modifiers changed from: private */
    public void updateSim() {
        getSimUnlockProgressDialog().show();
        if (this.mCheckSimPukThread == null) {
            C05603 r0 = new CheckSimPuk(this.mPukText, this.mPinText, this.mSubId) {
                /* access modifiers changed from: 0000 */
                public void onSimLockChangedResponse(final PinResult pinResult) {
                    KeyguardSimPukView.this.post(new Runnable() {
                        public void run() {
                            if (KeyguardSimPukView.this.mSimUnlockProgressDialog != null) {
                                KeyguardSimPukView.this.mSimUnlockProgressDialog.hide();
                            }
                            KeyguardSimPukView.this.resetPasswordText(true, pinResult.getType() != 0);
                            if (pinResult.getType() == 0) {
                                ((KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class)).reportSimUnlocked(KeyguardSimPukView.this.mSubId);
                                KeyguardSimPukView.this.mRemainingAttempts = -1;
                                KeyguardSimPukView.this.mShowDefaultMessage = true;
                                KeyguardSecurityCallback keyguardSecurityCallback = KeyguardSimPukView.this.mCallback;
                                if (keyguardSecurityCallback != null) {
                                    keyguardSecurityCallback.dismiss(true, KeyguardUpdateMonitor.getCurrentUser());
                                }
                            } else {
                                KeyguardSimPukView.this.mShowDefaultMessage = false;
                                if (pinResult.getType() == 1) {
                                    KeyguardSimPukView keyguardSimPukView = KeyguardSimPukView.this;
                                    keyguardSimPukView.mSecurityMessageDisplay.setMessage((CharSequence) keyguardSimPukView.getPukPasswordErrorMessage(pinResult.getAttemptsRemaining(), false));
                                    if (pinResult.getAttemptsRemaining() <= 2) {
                                        KeyguardSimPukView.this.getPukRemainingAttemptsDialog(pinResult.getAttemptsRemaining()).show();
                                    } else {
                                        KeyguardSimPukView keyguardSimPukView2 = KeyguardSimPukView.this;
                                        keyguardSimPukView2.mSecurityMessageDisplay.setMessage((CharSequence) keyguardSimPukView2.getPukPasswordErrorMessage(pinResult.getAttemptsRemaining(), false));
                                    }
                                } else {
                                    KeyguardSimPukView keyguardSimPukView3 = KeyguardSimPukView.this;
                                    keyguardSimPukView3.mSecurityMessageDisplay.setMessage((CharSequence) keyguardSimPukView3.getContext().getString(C2017R$string.kg_password_puk_failed));
                                }
                                KeyguardSimPukView.this.mStateMachine.reset();
                            }
                            KeyguardSimPukView.this.mCheckSimPukThread = null;
                        }
                    });
                }
            };
            this.mCheckSimPukThread = r0;
            r0.start();
        }
    }

    /* access modifiers changed from: protected */
    public void verifyPasswordAndUnlock() {
        this.mStateMachine.next();
    }

    public CharSequence getTitle() {
        return getContext().getString(17040254);
    }
}
