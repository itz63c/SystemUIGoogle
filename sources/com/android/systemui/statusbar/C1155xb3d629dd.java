package com.android.systemui.statusbar;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;

/* renamed from: com.android.systemui.statusbar.NotificationShadeDepthController$keyguardStateCallback$1$onKeyguardFadingAwayChanged$$inlined$apply$lambda$1 */
/* compiled from: NotificationShadeDepthController.kt */
final class C1155xb3d629dd implements AnimatorUpdateListener {
    final /* synthetic */ NotificationShadeDepthController$keyguardStateCallback$1 this$0;

    C1155xb3d629dd(NotificationShadeDepthController$keyguardStateCallback$1 notificationShadeDepthController$keyguardStateCallback$1) {
        this.this$0 = notificationShadeDepthController$keyguardStateCallback$1;
    }

    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        Intrinsics.checkParameterIsNotNull(valueAnimator, "animation");
        NotificationShadeDepthController notificationShadeDepthController = this.this$0.this$0;
        BlurUtils access$getBlurUtils$p = notificationShadeDepthController.blurUtils;
        Object animatedValue = valueAnimator.getAnimatedValue();
        if (animatedValue != null) {
            notificationShadeDepthController.setWakeAndUnlockBlurRadius(access$getBlurUtils$p.blurRadiusOfRatio(((Float) animatedValue).floatValue()));
            return;
        }
        throw new TypeCastException("null cannot be cast to non-null type kotlin.Float");
    }
}
