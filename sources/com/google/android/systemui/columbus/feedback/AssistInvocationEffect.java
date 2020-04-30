package com.google.android.systemui.columbus.feedback;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import com.android.systemui.assist.AssistManager;
import com.google.android.systemui.assist.AssistManagerGoogle;
import com.google.android.systemui.columbus.sensors.GestureSensor.DetectionProperties;
import java.util.concurrent.TimeUnit;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: AssistInvocationEffect.kt */
public final class AssistInvocationEffect implements FeedbackEffect {
    private static final long DECAY_DURATION = TimeUnit.SECONDS.toMillis(5);
    private AnimatorSet animation;
    private final AnimatorUpdateListener animatorUpdateListener;
    private final AssistManagerGoogle assistManager;
    /* access modifiers changed from: private */
    public float progress;

    public AssistInvocationEffect(AssistManager assistManager2) {
        Intrinsics.checkParameterIsNotNull(assistManager2, "assistManager");
        if (!(assistManager2 instanceof AssistManagerGoogle)) {
            assistManager2 = null;
        }
        this.assistManager = (AssistManagerGoogle) assistManager2;
        this.animatorUpdateListener = new AssistInvocationEffect$animatorUpdateListener$1(this);
        this.animation = new AnimatorSet();
    }

    public void onProgress(int i, DetectionProperties detectionProperties) {
        AssistManagerGoogle assistManagerGoogle = this.assistManager;
        if (assistManagerGoogle == null || !assistManagerGoogle.shouldUseHomeButtonAnimations()) {
            if (i == 1) {
                setProgress(0.99f, true);
            } else if (i != 3) {
                setProgress(0.0f, false);
            } else {
                setProgress(1.0f, false);
            }
        }
    }

    private final void setProgress(float f, boolean z) {
        if (this.animation.isRunning()) {
            this.animation.cancel();
        }
        if (z) {
            this.animation = new AnimatorSet();
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{this.progress, f});
            Intrinsics.checkExpressionValueIsNotNull(ofFloat, "toValueAnimator");
            ofFloat.setDuration(200);
            ofFloat.setInterpolator(new DecelerateInterpolator());
            ofFloat.addUpdateListener(this.animatorUpdateListener);
            this.animation.play(ofFloat);
            if (f > 0.0f) {
                ValueAnimator ofFloat2 = ValueAnimator.ofFloat(new float[]{f, 0.0f});
                Intrinsics.checkExpressionValueIsNotNull(ofFloat2, "decayAnimator");
                ofFloat2.setDuration((long) (((float) DECAY_DURATION) * f));
                ofFloat2.setInterpolator(new LinearInterpolator());
                ofFloat2.addUpdateListener(this.animatorUpdateListener);
                this.animation.play(ofFloat2).after(ofFloat);
            }
            this.animation.start();
            return;
        }
        this.progress = f;
        updateAssistManager();
    }

    /* access modifiers changed from: private */
    public final void updateAssistManager() {
        AssistManagerGoogle assistManagerGoogle = this.assistManager;
        if (assistManagerGoogle != null) {
            assistManagerGoogle.onInvocationProgress(0, this.progress);
        }
    }
}
