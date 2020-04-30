package com.android.keyguard;

import android.content.res.ColorStateList;
import android.view.MotionEvent;
import com.android.internal.widget.LockPatternUtils;

public interface KeyguardSecurityView {
    boolean disallowInterceptTouch(MotionEvent motionEvent) {
        return false;
    }

    CharSequence getTitle();

    boolean needsInput();

    void onPause();

    void onResume(int i);

    void reset();

    void setKeyguardCallback(KeyguardSecurityCallback keyguardSecurityCallback);

    void setLockPatternUtils(LockPatternUtils lockPatternUtils);

    void showMessage(CharSequence charSequence, ColorStateList colorStateList);

    void showPromptReason(int i);

    void startAppearAnimation();

    boolean startDisappearAnimation(Runnable runnable);
}
