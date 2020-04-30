package com.android.keyguard;

import android.content.Context;
import android.graphics.Rect;
import android.os.UserHandle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.TextKeyListener;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.android.internal.widget.LockscreenCredential;
import com.android.internal.widget.TextViewInputDisabler;
import com.android.systemui.C2007R$bool;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2017R$string;
import java.util.List;

public class KeyguardPasswordView extends KeyguardAbsKeyInputView implements KeyguardSecurityView, OnEditorActionListener, TextWatcher {
    private final int mDisappearYTranslation;
    private Interpolator mFastOutLinearInInterpolator;
    InputMethodManager mImm;
    private Interpolator mLinearOutSlowInInterpolator;
    /* access modifiers changed from: private */
    public TextView mPasswordEntry;
    private TextViewInputDisabler mPasswordEntryDisabler;
    /* access modifiers changed from: private */
    public final boolean mShowImeAtScreenOn;
    private View mSwitchImeButton;

    public boolean needsInput() {
        return true;
    }

    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    public KeyguardPasswordView(Context context) {
        this(context, null);
    }

    public KeyguardPasswordView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mShowImeAtScreenOn = context.getResources().getBoolean(C2007R$bool.kg_show_ime_at_screen_on);
        this.mDisappearYTranslation = getResources().getDimensionPixelSize(C2009R$dimen.disappear_y_translation);
        this.mLinearOutSlowInInterpolator = AnimationUtils.loadInterpolator(context, 17563662);
        this.mFastOutLinearInInterpolator = AnimationUtils.loadInterpolator(context, 17563663);
    }

    /* access modifiers changed from: protected */
    public void resetState() {
        this.mPasswordEntry.setTextOperationUser(UserHandle.of(KeyguardUpdateMonitor.getCurrentUser()));
        SecurityMessageDisplay securityMessageDisplay = this.mSecurityMessageDisplay;
        if (securityMessageDisplay != null) {
            securityMessageDisplay.setMessage((CharSequence) "");
        }
        boolean isEnabled = this.mPasswordEntry.isEnabled();
        setPasswordEntryEnabled(true);
        setPasswordEntryInputEnabled(true);
        if (this.mResumed && this.mPasswordEntry.isVisibleToUser() && isEnabled) {
            this.mImm.showSoftInput(this.mPasswordEntry, 1);
        }
    }

    /* access modifiers changed from: protected */
    public int getPasswordTextViewId() {
        return C2011R$id.passwordEntry;
    }

    public void onResume(final int i) {
        super.onResume(i);
        post(new Runnable() {
            public void run() {
                if (KeyguardPasswordView.this.isShown() && KeyguardPasswordView.this.mPasswordEntry.isEnabled()) {
                    KeyguardPasswordView.this.mPasswordEntry.requestFocus();
                    if (i != 1 || KeyguardPasswordView.this.mShowImeAtScreenOn) {
                        KeyguardPasswordView keyguardPasswordView = KeyguardPasswordView.this;
                        keyguardPasswordView.mImm.showSoftInput(keyguardPasswordView.mPasswordEntry, 1);
                    }
                }
            }
        });
    }

    /* access modifiers changed from: protected */
    public int getPromptReasonStringRes(int i) {
        if (i == 0) {
            return 0;
        }
        if (i == 1) {
            return C2017R$string.kg_prompt_reason_restart_password;
        }
        if (i == 2) {
            return C2017R$string.kg_prompt_reason_timeout_password;
        }
        if (i == 3) {
            return C2017R$string.kg_prompt_reason_device_admin;
        }
        if (i == 4) {
            return C2017R$string.kg_prompt_reason_user_request;
        }
        if (i != 6) {
            return C2017R$string.kg_prompt_reason_timeout_password;
        }
        return C2017R$string.kg_prompt_reason_prepare_for_update_password;
    }

    public void onPause() {
        super.onPause();
        this.mImm.hideSoftInputFromWindow(getWindowToken(), 0);
    }

    /* access modifiers changed from: private */
    public void updateSwitchImeButton() {
        boolean z = this.mSwitchImeButton.getVisibility() == 0;
        boolean hasMultipleEnabledIMEsOrSubtypes = hasMultipleEnabledIMEsOrSubtypes(this.mImm, false);
        if (z != hasMultipleEnabledIMEsOrSubtypes) {
            this.mSwitchImeButton.setVisibility(hasMultipleEnabledIMEsOrSubtypes ? 0 : 8);
        }
        if (this.mSwitchImeButton.getVisibility() != 0) {
            LayoutParams layoutParams = this.mPasswordEntry.getLayoutParams();
            if (layoutParams instanceof MarginLayoutParams) {
                ((MarginLayoutParams) layoutParams).setMarginStart(0);
                this.mPasswordEntry.setLayoutParams(layoutParams);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mImm = (InputMethodManager) getContext().getSystemService("input_method");
        TextView textView = (TextView) findViewById(getPasswordTextViewId());
        this.mPasswordEntry = textView;
        textView.setTextOperationUser(UserHandle.of(KeyguardUpdateMonitor.getCurrentUser()));
        this.mPasswordEntryDisabler = new TextViewInputDisabler(this.mPasswordEntry);
        this.mPasswordEntry.setKeyListener(TextKeyListener.getInstance());
        this.mPasswordEntry.setInputType(129);
        this.mPasswordEntry.setOnEditorActionListener(this);
        this.mPasswordEntry.addTextChangedListener(this);
        this.mPasswordEntry.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                KeyguardPasswordView.this.mCallback.userActivity();
            }
        });
        this.mPasswordEntry.setSelected(true);
        View findViewById = findViewById(C2011R$id.switch_ime_button);
        this.mSwitchImeButton = findViewById;
        findViewById.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                KeyguardPasswordView.this.mCallback.userActivity();
                KeyguardPasswordView keyguardPasswordView = KeyguardPasswordView.this;
                keyguardPasswordView.mImm.showInputMethodPickerFromSystem(false, keyguardPasswordView.getContext().getDisplayId());
            }
        });
        View findViewById2 = findViewById(C2011R$id.cancel_button);
        if (findViewById2 != null) {
            findViewById2.setOnClickListener(new OnClickListener() {
                public final void onClick(View view) {
                    KeyguardPasswordView.this.lambda$onFinishInflate$0$KeyguardPasswordView(view);
                }
            });
        }
        updateSwitchImeButton();
        postDelayed(new Runnable() {
            public void run() {
                KeyguardPasswordView.this.updateSwitchImeButton();
            }
        }, 500);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onFinishInflate$0 */
    public /* synthetic */ void lambda$onFinishInflate$0$KeyguardPasswordView(View view) {
        this.mCallback.reset();
        this.mCallback.onCancelClicked();
    }

    /* access modifiers changed from: protected */
    public boolean onRequestFocusInDescendants(int i, Rect rect) {
        return this.mPasswordEntry.requestFocus(i, rect);
    }

    /* access modifiers changed from: protected */
    public void resetPasswordText(boolean z, boolean z2) {
        this.mPasswordEntry.setText("");
    }

    /* access modifiers changed from: protected */
    public LockscreenCredential getEnteredCredential() {
        return LockscreenCredential.createPasswordOrNone(this.mPasswordEntry.getText());
    }

    /* access modifiers changed from: protected */
    public void setPasswordEntryEnabled(boolean z) {
        this.mPasswordEntry.setEnabled(z);
    }

    /* access modifiers changed from: protected */
    public void setPasswordEntryInputEnabled(boolean z) {
        this.mPasswordEntryDisabler.setInputEnabled(z);
    }

    private boolean hasMultipleEnabledIMEsOrSubtypes(InputMethodManager inputMethodManager, boolean z) {
        boolean z2 = false;
        int i = 0;
        for (InputMethodInfo inputMethodInfo : inputMethodManager.getEnabledInputMethodListAsUser(KeyguardUpdateMonitor.getCurrentUser())) {
            if (i > 1) {
                return true;
            }
            List<InputMethodSubtype> enabledInputMethodSubtypeList = inputMethodManager.getEnabledInputMethodSubtypeList(inputMethodInfo, true);
            if (!enabledInputMethodSubtypeList.isEmpty()) {
                int i2 = 0;
                for (InputMethodSubtype isAuxiliary : enabledInputMethodSubtypeList) {
                    if (isAuxiliary.isAuxiliary()) {
                        i2++;
                    }
                }
                if (enabledInputMethodSubtypeList.size() - i2 <= 0) {
                    if (z) {
                        if (i2 <= 1) {
                        }
                    }
                }
            }
            i++;
        }
        if (i > 1 || inputMethodManager.getEnabledInputMethodSubtypeList(null, false).size() > 1) {
            z2 = true;
        }
        return z2;
    }

    public int getWrongPasswordStringId() {
        return C2017R$string.kg_wrong_password;
    }

    public void startAppearAnimation() {
        setAlpha(0.0f);
        setTranslationY(0.0f);
        animate().alpha(1.0f).withLayer().setDuration(300).setInterpolator(this.mLinearOutSlowInInterpolator);
    }

    public boolean startDisappearAnimation(Runnable runnable) {
        animate().alpha(0.0f).translationY((float) this.mDisappearYTranslation).setInterpolator(this.mFastOutLinearInInterpolator).setDuration(100).withEndAction(runnable);
        return true;
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        KeyguardSecurityCallback keyguardSecurityCallback = this.mCallback;
        if (keyguardSecurityCallback != null) {
            keyguardSecurityCallback.userActivity();
        }
    }

    public void afterTextChanged(Editable editable) {
        if (!TextUtils.isEmpty(editable)) {
            onUserInput();
        }
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        boolean z = keyEvent == null && (i == 0 || i == 6 || i == 5);
        boolean z2 = keyEvent != null && KeyEvent.isConfirmKey(keyEvent.getKeyCode()) && keyEvent.getAction() == 0;
        if (!z && !z2) {
            return false;
        }
        verifyPasswordAndUnlock();
        return true;
    }

    public CharSequence getTitle() {
        return getContext().getString(17040249);
    }
}
