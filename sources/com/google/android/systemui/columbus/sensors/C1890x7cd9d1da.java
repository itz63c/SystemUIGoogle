package com.google.android.systemui.columbus.sensors;

import com.google.android.systemui.columbus.sensors.GestureSensor.DetectionProperties;
import com.google.android.systemui.columbus.sensors.GestureSensor.Listener;

/* renamed from: com.google.android.systemui.columbus.sensors.GestureSensorImpl$GestureSensorEventListener$onSensorChanged$$inlined$let$lambda$2 */
/* compiled from: GestureSensorImpl.kt */
final class C1890x7cd9d1da implements Runnable {
    final /* synthetic */ GestureSensorEventListener this$0;

    C1890x7cd9d1da(GestureSensorEventListener gestureSensorEventListener) {
        this.this$0 = gestureSensorEventListener;
    }

    public final void run() {
        Listener access$getListener$p = GestureSensorImpl.this.listener;
        if (access$getListener$p != null) {
            access$getListener$p.onGestureProgress(GestureSensorImpl.this, 3, new DetectionProperties(false, false));
        }
        this.this$0.mo20701x8ec13776();
    }
}
