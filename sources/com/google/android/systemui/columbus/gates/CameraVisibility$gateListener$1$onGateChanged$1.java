package com.google.android.systemui.columbus.gates;

/* compiled from: CameraVisibility.kt */
final class CameraVisibility$gateListener$1$onGateChanged$1 implements Runnable {
    final /* synthetic */ CameraVisibility$gateListener$1 this$0;

    CameraVisibility$gateListener$1$onGateChanged$1(CameraVisibility$gateListener$1 cameraVisibility$gateListener$1) {
        this.this$0 = cameraVisibility$gateListener$1;
    }

    public final void run() {
        this.this$0.this$0.updateCameraIsShowing();
    }
}
