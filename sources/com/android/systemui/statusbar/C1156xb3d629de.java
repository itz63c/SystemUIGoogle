package com.android.systemui.statusbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

/* renamed from: com.android.systemui.statusbar.NotificationShadeDepthController$keyguardStateCallback$1$onKeyguardFadingAwayChanged$$inlined$apply$lambda$2 */
/* compiled from: NotificationShadeDepthController.kt */
public final class C1156xb3d629de extends AnimatorListenerAdapter {
    final /* synthetic */ NotificationShadeDepthController$keyguardStateCallback$1 this$0;

    C1156xb3d629de(NotificationShadeDepthController$keyguardStateCallback$1 notificationShadeDepthController$keyguardStateCallback$1) {
        this.this$0 = notificationShadeDepthController$keyguardStateCallback$1;
    }

    public void onAnimationEnd(Animator animator) {
        this.this$0.this$0.keyguardAnimator = null;
        NotificationShadeDepthController.scheduleUpdate$default(this.this$0.this$0, null, 1, null);
    }
}
