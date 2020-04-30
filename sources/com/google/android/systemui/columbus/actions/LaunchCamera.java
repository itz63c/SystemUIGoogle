package com.google.android.systemui.columbus.actions;

import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import com.google.android.systemui.columbus.sensors.GestureSensor.DetectionProperties;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: LaunchCamera.kt */
public final class LaunchCamera extends Action {
    private final boolean cameraAvailable;

    public LaunchCamera(Context context) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        super(context, null);
        this.cameraAvailable = context.getPackageManager().hasSystemFeature("android.hardware.camera");
    }

    public boolean isAvailable() {
        return this.cameraAvailable;
    }

    public void onProgress(int i, DetectionProperties detectionProperties) {
        if (i == 3) {
            onTrigger();
        }
    }

    public void onTrigger() {
        String str = "android.media.action.IMAGE_CAPTURE";
        new Intent(str).setFlags(268468224);
        getContext().startActivityAsUser(new Intent(str), UserHandle.of(-2));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" [cameraAvailable -> ");
        sb.append(this.cameraAvailable);
        sb.append("]");
        return sb.toString();
    }
}
