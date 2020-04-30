package com.google.android.systemui.columbus.feedback;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import kotlin.TypeCastException;

/* compiled from: AssistInvocationEffect.kt */
final class AssistInvocationEffect$animatorUpdateListener$1 implements AnimatorUpdateListener {
    final /* synthetic */ AssistInvocationEffect this$0;

    AssistInvocationEffect$animatorUpdateListener$1(AssistInvocationEffect assistInvocationEffect) {
        this.this$0 = assistInvocationEffect;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        if (valueAnimator != null) {
            AssistInvocationEffect assistInvocationEffect = this.this$0;
            Object animatedValue = valueAnimator.getAnimatedValue();
            if (animatedValue != null) {
                assistInvocationEffect.progress = ((Float) animatedValue).floatValue();
                this.this$0.updateAssistManager();
                return;
            }
            throw new TypeCastException("null cannot be cast to non-null type kotlin.Float");
        }
    }
}
