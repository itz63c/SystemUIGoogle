package com.android.systemui.statusbar;

import com.android.systemui.plugins.statusbar.StatusBarStateController.StateListener;

/* compiled from: NotificationShadeDepthController.kt */
public final class NotificationShadeDepthController$statusBarStateCallback$1 implements StateListener {
    final /* synthetic */ NotificationShadeDepthController this$0;

    NotificationShadeDepthController$statusBarStateCallback$1(NotificationShadeDepthController notificationShadeDepthController) {
        this.this$0 = notificationShadeDepthController;
    }

    public void onStateChanged(int i) {
        this.this$0.updateShadeBlur();
    }

    public void onDozingChanged(boolean z) {
        if (z && this.this$0.getShadeSpring().isRunning()) {
            this.this$0.getShadeSpring().skipToEnd();
        }
    }
}
