package com.google.android.systemui.columbus.gates;

import android.app.TaskStackListener;

/* compiled from: CameraVisibility.kt */
public final class CameraVisibility$taskStackListener$1 extends TaskStackListener {
    final /* synthetic */ CameraVisibility this$0;

    CameraVisibility$taskStackListener$1(CameraVisibility cameraVisibility) {
        this.this$0 = cameraVisibility;
    }

    public void onTaskStackChanged() {
        this.this$0.updateHandler.post(new CameraVisibility$taskStackListener$1$onTaskStackChanged$1(this));
    }
}
