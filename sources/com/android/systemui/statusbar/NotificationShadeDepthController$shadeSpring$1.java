package com.android.systemui.statusbar;

import androidx.dynamicanimation.animation.FloatPropertyCompat;

/* compiled from: NotificationShadeDepthController.kt */
public final class NotificationShadeDepthController$shadeSpring$1 extends FloatPropertyCompat<NotificationShadeDepthController> {
    final /* synthetic */ NotificationShadeDepthController this$0;

    NotificationShadeDepthController$shadeSpring$1(NotificationShadeDepthController notificationShadeDepthController, String str) {
        this.this$0 = notificationShadeDepthController;
        super(str);
    }

    public void setValue(NotificationShadeDepthController notificationShadeDepthController, float f) {
        this.this$0.setShadeBlurRadius((int) f);
    }

    public float getValue(NotificationShadeDepthController notificationShadeDepthController) {
        return (float) this.this$0.shadeBlurRadius;
    }
}
