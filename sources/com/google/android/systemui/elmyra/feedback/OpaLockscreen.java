package com.google.android.systemui.elmyra.feedback;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import com.android.systemui.C2011R$id;
import com.android.systemui.statusbar.phone.KeyguardBottomAreaView;
import com.android.systemui.statusbar.phone.StatusBar;
import com.android.systemui.statusbar.policy.KeyguardStateController;
import com.google.android.systemui.elmyra.sensors.GestureSensor.DetectionProperties;

public class OpaLockscreen implements FeedbackEffect {
    private KeyguardBottomAreaView mKeyguardBottomAreaView;
    private final KeyguardStateController mKeyguardStateController;
    private FeedbackEffect mLockscreenOpaLayout;
    private final StatusBar mStatusBar;

    static {
        new DecelerateInterpolator();
        new AccelerateInterpolator();
    }

    public OpaLockscreen(StatusBar statusBar, KeyguardStateController keyguardStateController) {
        this.mStatusBar = statusBar;
        this.mKeyguardStateController = keyguardStateController;
        refreshLockscreenOpaLayout();
    }

    public void onProgress(float f, int i) {
        refreshLockscreenOpaLayout();
        FeedbackEffect feedbackEffect = this.mLockscreenOpaLayout;
        if (feedbackEffect != null) {
            feedbackEffect.onProgress(f, i);
        }
    }

    public void onRelease() {
        refreshLockscreenOpaLayout();
        FeedbackEffect feedbackEffect = this.mLockscreenOpaLayout;
        if (feedbackEffect != null) {
            feedbackEffect.onRelease();
        }
    }

    public void onResolve(DetectionProperties detectionProperties) {
        refreshLockscreenOpaLayout();
        FeedbackEffect feedbackEffect = this.mLockscreenOpaLayout;
        if (feedbackEffect != null) {
            feedbackEffect.onResolve(detectionProperties);
        }
    }

    private void refreshLockscreenOpaLayout() {
        if (this.mStatusBar.getKeyguardBottomAreaView() == null || !this.mKeyguardStateController.isShowing()) {
            this.mKeyguardBottomAreaView = null;
            this.mLockscreenOpaLayout = null;
            return;
        }
        KeyguardBottomAreaView keyguardBottomAreaView = this.mStatusBar.getKeyguardBottomAreaView();
        if (this.mLockscreenOpaLayout == null || !keyguardBottomAreaView.equals(this.mKeyguardBottomAreaView)) {
            this.mKeyguardBottomAreaView = keyguardBottomAreaView;
            FeedbackEffect feedbackEffect = this.mLockscreenOpaLayout;
            if (feedbackEffect != null) {
                feedbackEffect.onRelease();
            }
            this.mLockscreenOpaLayout = (FeedbackEffect) keyguardBottomAreaView.findViewById(C2011R$id.lockscreen_opa);
        }
    }
}
