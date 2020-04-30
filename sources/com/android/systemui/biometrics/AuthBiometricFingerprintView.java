package com.android.systemui.biometrics;

import android.content.Context;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import com.android.systemui.C2008R$color;
import com.android.systemui.C2010R$drawable;
import com.android.systemui.C2017R$string;

public class AuthBiometricFingerprintView extends AuthBiometricView {
    private boolean shouldAnimateForTransition(int i, int i2) {
        return (i2 == 1 || i2 == 2) ? i == 4 || i == 3 : i2 == 3 || i2 == 4;
    }

    /* access modifiers changed from: protected */
    public int getDelayAfterAuthenticatedDurationMs() {
        return 0;
    }

    /* access modifiers changed from: protected */
    public int getStateForAfterError() {
        return 2;
    }

    /* access modifiers changed from: protected */
    public boolean supportsSmallDialog() {
        return false;
    }

    public AuthBiometricFingerprintView(Context context) {
        this(context, null);
    }

    public AuthBiometricFingerprintView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    public void handleResetAfterError() {
        showTouchSensorString();
    }

    /* access modifiers changed from: protected */
    public void handleResetAfterHelp() {
        showTouchSensorString();
    }

    public void updateState(int i) {
        updateIcon(this.mState, i);
        super.updateState(i);
    }

    /* access modifiers changed from: 0000 */
    public void onAttachedToWindowInternal() {
        super.onAttachedToWindowInternal();
        showTouchSensorString();
    }

    private void showTouchSensorString() {
        this.mIndicatorView.setText(C2017R$string.fingerprint_dialog_touch_sensor);
        this.mIndicatorView.setTextColor(C2008R$color.biometric_dialog_gray);
    }

    private void updateIcon(int i, int i2) {
        Drawable animationForTransition = getAnimationForTransition(i, i2);
        if (animationForTransition == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Animation not found, ");
            sb.append(i);
            sb.append(" -> ");
            sb.append(i2);
            Log.e("BiometricPrompt/AuthBiometricFingerprintView", sb.toString());
            return;
        }
        AnimatedVectorDrawable animatedVectorDrawable = animationForTransition instanceof AnimatedVectorDrawable ? (AnimatedVectorDrawable) animationForTransition : null;
        this.mIconView.setImageDrawable(animationForTransition);
        if (animatedVectorDrawable != null && shouldAnimateForTransition(i, i2)) {
            animatedVectorDrawable.forceAnimationOnUI();
            animatedVectorDrawable.start();
        }
    }

    private Drawable getAnimationForTransition(int i, int i2) {
        int i3;
        if (i2 == 1 || i2 == 2) {
            if (i == 4 || i == 3) {
                i3 = C2010R$drawable.fingerprint_dialog_error_to_fp;
            } else {
                i3 = C2010R$drawable.fingerprint_dialog_fp_to_error;
            }
        } else if (i2 == 3 || i2 == 4) {
            i3 = C2010R$drawable.fingerprint_dialog_fp_to_error;
        } else if (i2 != 6) {
            return null;
        } else {
            i3 = C2010R$drawable.fingerprint_dialog_fp_to_error;
        }
        return this.mContext.getDrawable(i3);
    }
}
