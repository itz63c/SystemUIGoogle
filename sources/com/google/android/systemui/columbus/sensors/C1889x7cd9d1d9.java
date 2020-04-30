package com.google.android.systemui.columbus.sensors;

/* renamed from: com.google.android.systemui.columbus.sensors.GestureSensorImpl$GestureSensorEventListener$onSensorChanged$$inlined$let$lambda$1 */
/* compiled from: GestureSensorImpl.kt */
final class C1889x7cd9d1d9 implements Runnable {
    final /* synthetic */ GestureSensorEventListener this$0;

    C1889x7cd9d1d9(GestureSensorEventListener gestureSensorEventListener) {
        this.this$0 = gestureSensorEventListener;
    }

    /* JADX WARNING: type inference failed for: r5v3 */
    /* JADX WARNING: type inference failed for: r1v1, types: [com.google.android.systemui.columbus.sensors.GestureSensorImpl$sam$i$java_lang_Runnable$0] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void run() {
        /*
            r5 = this;
            com.google.android.systemui.columbus.sensors.GestureSensorImpl$GestureSensorEventListener r0 = r5.this$0
            com.google.android.systemui.columbus.sensors.GestureSensorImpl r0 = com.google.android.systemui.columbus.sensors.GestureSensorImpl.this
            com.google.android.systemui.columbus.sensors.GestureSensor$Listener r0 = r0.listener
            if (r0 == 0) goto L_0x0018
            com.google.android.systemui.columbus.sensors.GestureSensorImpl$GestureSensorEventListener r1 = r5.this$0
            com.google.android.systemui.columbus.sensors.GestureSensorImpl r1 = com.google.android.systemui.columbus.sensors.GestureSensorImpl.this
            com.google.android.systemui.columbus.sensors.GestureSensor$DetectionProperties r2 = new com.google.android.systemui.columbus.sensors.GestureSensor$DetectionProperties
            r3 = 0
            r4 = 1
            r2.<init>(r3, r4)
            r0.onGestureProgress(r1, r4, r2)
        L_0x0018:
            com.google.android.systemui.columbus.sensors.GestureSensorImpl$GestureSensorEventListener r0 = r5.this$0
            com.google.android.systemui.columbus.sensors.GestureSensorImpl r0 = com.google.android.systemui.columbus.sensors.GestureSensorImpl.this
            android.os.Handler r0 = r0.handler
            com.google.android.systemui.columbus.sensors.GestureSensorImpl$GestureSensorEventListener r5 = r5.this$0
            kotlin.jvm.functions.Function0 r5 = r5.onTimeout
            if (r5 == 0) goto L_0x002e
            com.google.android.systemui.columbus.sensors.GestureSensorImpl$sam$i$java_lang_Runnable$0 r1 = new com.google.android.systemui.columbus.sensors.GestureSensorImpl$sam$i$java_lang_Runnable$0
            r1.<init>(r5)
            r5 = r1
        L_0x002e:
            java.lang.Runnable r5 = (java.lang.Runnable) r5
            long r1 = com.google.android.systemui.columbus.sensors.GestureSensorImpl.TIMEOUT_MS
            r0.postDelayed(r5, r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.systemui.columbus.sensors.C1889x7cd9d1d9.run():void");
    }
}
