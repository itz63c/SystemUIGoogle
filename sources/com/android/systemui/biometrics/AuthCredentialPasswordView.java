package com.android.systemui.biometrics;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.android.internal.widget.LockPatternChecker;
import com.android.internal.widget.LockPatternChecker.OnVerifyCallback;
import com.android.internal.widget.LockscreenCredential;
import com.android.systemui.C2011R$id;

public class AuthCredentialPasswordView extends AuthCredentialView implements OnEditorActionListener {
    private final InputMethodManager mImm = ((InputMethodManager) this.mContext.getSystemService(InputMethodManager.class));
    private EditText mPasswordField;

    public AuthCredentialPasswordView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        EditText editText = (EditText) findViewById(C2011R$id.lockPassword);
        this.mPasswordField = editText;
        editText.setOnEditorActionListener(this);
        this.mPasswordField.setOnKeyListener(new OnKeyListener() {
            public final boolean onKey(View view, int i, KeyEvent keyEvent) {
                return AuthCredentialPasswordView.this.lambda$onFinishInflate$0$AuthCredentialPasswordView(view, i, keyEvent);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onFinishInflate$0 */
    public /* synthetic */ boolean lambda$onFinishInflate$0$AuthCredentialPasswordView(View view, int i, KeyEvent keyEvent) {
        if (i != 4) {
            return false;
        }
        if (keyEvent.getAction() == 1) {
            this.mContainerView.animateAway(1);
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mCredentialType == 1) {
            this.mPasswordField.setInputType(18);
        }
        postDelayed(new Runnable() {
            public final void run() {
                AuthCredentialPasswordView.this.lambda$onAttachedToWindow$1$AuthCredentialPasswordView();
            }
        }, 100);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onAttachedToWindow$1 */
    public /* synthetic */ void lambda$onAttachedToWindow$1$AuthCredentialPasswordView() {
        this.mPasswordField.requestFocus();
        this.mImm.showSoftInput(this.mPasswordField, 1);
    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        boolean z = keyEvent == null && (i == 0 || i == 6 || i == 5);
        boolean z2 = keyEvent != null && KeyEvent.isConfirmKey(keyEvent.getKeyCode()) && keyEvent.getAction() == 0;
        if (!z && !z2) {
            return false;
        }
        checkPasswordAndUnlock();
        return true;
    }

    private void checkPasswordAndUnlock() {
        LockscreenCredential lockscreenCredential;
        if (this.mCredentialType == 1) {
            lockscreenCredential = LockscreenCredential.createPinOrNone(this.mPasswordField.getText());
        } else {
            lockscreenCredential = LockscreenCredential.createPasswordOrNone(this.mPasswordField.getText());
        }
        try {
            if (lockscreenCredential.isNone()) {
                if (lockscreenCredential != null) {
                    lockscreenCredential.close();
                }
                return;
            }
            this.mPendingLockCheck = LockPatternChecker.verifyCredential(this.mLockPatternUtils, lockscreenCredential, this.mOperationId, this.mEffectiveUserId, new OnVerifyCallback() {
                public final void onVerified(byte[] bArr, int i) {
                    AuthCredentialPasswordView.this.onCredentialVerified(bArr, i);
                }
            });
            if (lockscreenCredential != null) {
                lockscreenCredential.close();
            }
            return;
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        throw th;
    }

    /* access modifiers changed from: protected */
    public void onCredentialVerified(byte[] bArr, int i) {
        super.onCredentialVerified(bArr, i);
        if (bArr != null) {
            this.mImm.hideSoftInputFromWindow(getWindowToken(), 0);
        } else {
            this.mPasswordField.setText("");
        }
    }
}
