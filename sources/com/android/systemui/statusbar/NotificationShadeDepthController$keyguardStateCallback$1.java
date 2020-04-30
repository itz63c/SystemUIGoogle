package com.android.systemui.statusbar;

import android.animation.Animator;
import android.animation.ValueAnimator;
import com.android.systemui.Interpolators;
import com.android.systemui.statusbar.policy.KeyguardStateController.Callback;

/* compiled from: NotificationShadeDepthController.kt */
public final class NotificationShadeDepthController$keyguardStateCallback$1 implements Callback {
    final /* synthetic */ NotificationShadeDepthController this$0;

    NotificationShadeDepthController$keyguardStateCallback$1(NotificationShadeDepthController notificationShadeDepthController) {
        this.this$0 = notificationShadeDepthController;
    }

    public void onKeyguardFadingAwayChanged() {
        if (this.this$0.keyguardStateController.isKeyguardFadingAway() && this.this$0.biometricUnlockController.getMode() == 1) {
            Animator access$getKeyguardAnimator$p = this.this$0.keyguardAnimator;
            if (access$getKeyguardAnimator$p != null) {
                access$getKeyguardAnimator$p.cancel();
            }
            NotificationShadeDepthController notificationShadeDepthController = this.this$0;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{1.0f, 0.0f});
            ofFloat.setDuration(this.this$0.keyguardStateController.getKeyguardFadingAwayDuration());
            ofFloat.setStartDelay(this.this$0.keyguardStateController.getKeyguardFadingAwayDelay());
            ofFloat.setInterpolator(Interpolators.DECELERATE_QUINT);
            ofFloat.addUpdateListener(new C1155xb3d629dd(this));
            ofFloat.addListener(new C1156xb3d629de(this));
            ofFloat.start();
            notificationShadeDepthController.keyguardAnimator = ofFloat;
        }
    }

    public void onKeyguardShowingChanged() {
        if (this.this$0.keyguardStateController.isShowing()) {
            Animator access$getKeyguardAnimator$p = this.this$0.keyguardAnimator;
            if (access$getKeyguardAnimator$p != null) {
                access$getKeyguardAnimator$p.cancel();
            }
            Animator access$getNotificationAnimator$p = this.this$0.notificationAnimator;
            if (access$getNotificationAnimator$p != null) {
                access$getNotificationAnimator$p.cancel();
            }
        }
    }
}
