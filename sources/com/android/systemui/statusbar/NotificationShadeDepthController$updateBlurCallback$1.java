package com.android.systemui.statusbar;

import android.view.Choreographer.FrameCallback;

/* compiled from: NotificationShadeDepthController.kt */
final class NotificationShadeDepthController$updateBlurCallback$1 implements FrameCallback {
    final /* synthetic */ NotificationShadeDepthController this$0;

    NotificationShadeDepthController$updateBlurCallback$1(NotificationShadeDepthController notificationShadeDepthController) {
        this.this$0 = notificationShadeDepthController;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x003c, code lost:
        if (r0 != null) goto L_0x0049;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void doFrame(long r4) {
        /*
            r3 = this;
            com.android.systemui.statusbar.NotificationShadeDepthController r4 = r3.this$0
            r5 = 0
            r4.updateScheduled = r5
            com.android.systemui.statusbar.NotificationShadeDepthController r4 = r3.this$0
            int r4 = r4.shadeBlurRadius
            com.android.systemui.statusbar.NotificationShadeDepthController r5 = r3.this$0
            int r5 = r5.wakeAndUnlockBlurRadius
            com.android.systemui.statusbar.NotificationShadeDepthController r0 = r3.this$0
            com.android.systemui.statusbar.BlurUtils r0 = r0.blurUtils
            com.android.systemui.statusbar.NotificationShadeDepthController r1 = r3.this$0
            float r1 = r1.globalDialogVisibility
            int r0 = r0.blurRadiusOfRatio(r1)
            int r5 = java.lang.Math.max(r5, r0)
            int r4 = java.lang.Math.max(r4, r5)
            com.android.systemui.statusbar.NotificationShadeDepthController r5 = r3.this$0
            com.android.systemui.statusbar.BlurUtils r5 = r5.blurUtils
            com.android.systemui.statusbar.NotificationShadeDepthController r0 = r3.this$0
            android.view.View r0 = r0.blurRoot
            if (r0 == 0) goto L_0x003f
            android.view.ViewRootImpl r0 = r0.getViewRootImpl()
            if (r0 == 0) goto L_0x003f
            goto L_0x0049
        L_0x003f:
            com.android.systemui.statusbar.NotificationShadeDepthController r0 = r3.this$0
            android.view.View r0 = r0.getRoot()
            android.view.ViewRootImpl r0 = r0.getViewRootImpl()
        L_0x0049:
            r5.applyBlur(r0, r4)
            com.android.systemui.statusbar.NotificationShadeDepthController r5 = r3.this$0
            com.android.systemui.statusbar.BlurUtils r5 = r5.blurUtils
            float r5 = r5.ratioOfBlurRadius(r4)
            com.android.systemui.statusbar.NotificationShadeDepthController r0 = r3.this$0
            float r0 = r0.globalDialogVisibility
            float r5 = java.lang.Math.max(r5, r0)
            com.android.systemui.statusbar.NotificationShadeDepthController r0 = r3.this$0
            android.app.WallpaperManager r0 = r0.wallpaperManager
            com.android.systemui.statusbar.NotificationShadeDepthController r1 = r3.this$0
            android.view.View r1 = r1.getRoot()
            android.os.IBinder r1 = r1.getWindowToken()
            com.android.systemui.statusbar.NotificationShadeDepthController r2 = r3.this$0
            android.view.animation.Interpolator r2 = r2.zoomInterpolator
            float r5 = r2.getInterpolation(r5)
            r0.setWallpaperZoomOut(r1, r5)
            com.android.systemui.statusbar.NotificationShadeDepthController r3 = r3.this$0
            com.android.systemui.statusbar.phone.NotificationShadeWindowController r3 = r3.notificationShadeWindowController
            r3.setBackgroundBlurRadius(r4)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.NotificationShadeDepthController$updateBlurCallback$1.doFrame(long):void");
    }
}
