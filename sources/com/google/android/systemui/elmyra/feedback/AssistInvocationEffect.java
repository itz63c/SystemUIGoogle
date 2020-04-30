package com.google.android.systemui.elmyra.feedback;

import com.google.android.systemui.assist.AssistManagerGoogle;
import com.google.android.systemui.elmyra.sensors.GestureSensor.DetectionProperties;

public class AssistInvocationEffect implements FeedbackEffect {
    private final AssistManagerGoogle mAssistManager;
    private final FeedbackEffect mOpaHomeButton;
    private final FeedbackEffect mOpaLockscreen;

    public AssistInvocationEffect(AssistManagerGoogle assistManagerGoogle, OpaHomeButton opaHomeButton, OpaLockscreen opaLockscreen) {
        this.mAssistManager = assistManagerGoogle;
        this.mOpaHomeButton = opaHomeButton;
        this.mOpaLockscreen = opaLockscreen;
    }

    public void onProgress(float f, int i) {
        if (this.mAssistManager.shouldUseHomeButtonAnimations()) {
            this.mOpaHomeButton.onProgress(f, i);
            this.mOpaLockscreen.onProgress(f, i);
            return;
        }
        this.mAssistManager.onInvocationProgress(2, f);
    }

    public void onRelease() {
        if (this.mAssistManager.shouldUseHomeButtonAnimations()) {
            this.mOpaHomeButton.onRelease();
            this.mOpaLockscreen.onRelease();
            return;
        }
        this.mAssistManager.onInvocationProgress(2, 0.0f);
    }

    public void onResolve(DetectionProperties detectionProperties) {
        if (this.mAssistManager.shouldUseHomeButtonAnimations()) {
            this.mOpaHomeButton.onResolve(detectionProperties);
            this.mOpaLockscreen.onResolve(detectionProperties);
            return;
        }
        this.mAssistManager.onInvocationProgress(2, 1.0f);
    }
}
