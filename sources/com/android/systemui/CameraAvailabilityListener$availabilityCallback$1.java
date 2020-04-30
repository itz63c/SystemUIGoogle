package com.android.systemui;

import android.hardware.camera2.CameraManager.AvailabilityCallback;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: CameraAvailabilityListener.kt */
public final class CameraAvailabilityListener$availabilityCallback$1 extends AvailabilityCallback {
    final /* synthetic */ CameraAvailabilityListener this$0;

    CameraAvailabilityListener$availabilityCallback$1(CameraAvailabilityListener cameraAvailabilityListener) {
        this.this$0 = cameraAvailabilityListener;
    }

    public void onCameraAvailable(String str) {
        Intrinsics.checkParameterIsNotNull(str, "cameraId");
        if (Intrinsics.areEqual((Object) this.this$0.targetCameraId, (Object) str)) {
            this.this$0.notifyCameraInactive();
        }
    }

    public void onCameraUnavailable(String str) {
        Intrinsics.checkParameterIsNotNull(str, "cameraId");
        if (Intrinsics.areEqual((Object) this.this$0.targetCameraId, (Object) str)) {
            this.this$0.notifyCameraActive();
        }
    }
}
