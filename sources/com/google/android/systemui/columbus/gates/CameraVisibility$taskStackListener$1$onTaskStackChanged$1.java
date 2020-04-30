package com.google.android.systemui.columbus.gates;

/* compiled from: CameraVisibility.kt */
final class CameraVisibility$taskStackListener$1$onTaskStackChanged$1 implements Runnable {
    final /* synthetic */ CameraVisibility$taskStackListener$1 this$0;

    CameraVisibility$taskStackListener$1$onTaskStackChanged$1(CameraVisibility$taskStackListener$1 cameraVisibility$taskStackListener$1) {
        this.this$0 = cameraVisibility$taskStackListener$1;
    }

    public final void run() {
        this.this$0.this$0.updateCameraIsShowing();
    }
}
