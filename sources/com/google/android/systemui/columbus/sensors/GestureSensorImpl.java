package com.google.android.systemui.columbus.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import com.google.android.systemui.columbus.sensors.GestureSensor.Listener;
import com.google.android.systemui.columbus.sensors.GestureSensor.Listener.DefaultImpls;
import com.google.android.systemui.columbus.sensors.config.GestureConfiguration;
import java.util.concurrent.TimeUnit;
import kotlin.TypeCastException;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: GestureSensorImpl.kt */
public final class GestureSensorImpl implements GestureSensor {
    /* access modifiers changed from: private */
    public static final long TIMEOUT_MS = TimeUnit.SECONDS.toMillis(5);
    /* access modifiers changed from: private */
    public final Sensor accelerometer;
    private final String deviceName;
    /* access modifiers changed from: private */
    public final Sensor gyroscope;
    /* access modifiers changed from: private */
    public final Handler handler = new Handler(Looper.getMainLooper());
    private boolean isListening;
    /* access modifiers changed from: private */
    public final boolean isRunningInLowSamplingRate;
    /* access modifiers changed from: private */
    public Listener listener;
    /* access modifiers changed from: private */
    public final long samplingIntervalNs;
    /* access modifiers changed from: private */
    public final GestureSensorEventListener sensorEventListener;
    /* access modifiers changed from: private */
    public final SensorManager sensorManager;
    /* access modifiers changed from: private */
    public final TapRT tap;

    /* compiled from: GestureSensorImpl.kt */
    private final class GestureSensorEventListener implements SensorEventListener {
        /* access modifiers changed from: private */
        public final Function0<Unit> onTimeout = new GestureSensorImpl$GestureSensorEventListener$onTimeout$1(this);

        public void onAccuracyChanged(Sensor sensor, int i) {
        }

        public GestureSensorEventListener() {
        }

        /* JADX WARNING: type inference failed for: r0v7, types: [kotlin.jvm.functions.Function0<kotlin.Unit>, kotlin.jvm.functions.Function0] */
        /* JADX WARNING: type inference failed for: r0v8 */
        /* JADX WARNING: type inference failed for: r1v2, types: [com.google.android.systemui.columbus.sensors.GestureSensorImpl$sam$i$java_lang_Runnable$0] */
        /* JADX WARNING: type inference failed for: r0v11 */
        /* JADX WARNING: type inference failed for: r0v12, types: [kotlin.jvm.functions.Function0<kotlin.Unit>, kotlin.jvm.functions.Function0] */
        /* JADX WARNING: type inference failed for: r0v13 */
        /* JADX WARNING: type inference failed for: r1v3, types: [com.google.android.systemui.columbus.sensors.GestureSensorImpl$sam$i$java_lang_Runnable$0] */
        /* JADX WARNING: type inference failed for: r0v16 */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Unknown variable types count: 4 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onSensorChanged(android.hardware.SensorEvent r14) {
            /*
                r13 = this;
                if (r14 == 0) goto L_0x0087
                com.google.android.systemui.columbus.sensors.GestureSensorImpl r0 = com.google.android.systemui.columbus.sensors.GestureSensorImpl.this
                com.google.android.systemui.columbus.sensors.TapRT r1 = r0.tap
                android.hardware.Sensor r0 = r14.sensor
                java.lang.String r2 = "it.sensor"
                kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r0, r2)
                int r2 = r0.getType()
                float[] r0 = r14.values
                r3 = 0
                r3 = r0[r3]
                r11 = 1
                r4 = r0[r11]
                r12 = 2
                r5 = r0[r12]
                long r6 = r14.timestamp
                com.google.android.systemui.columbus.sensors.GestureSensorImpl r0 = com.google.android.systemui.columbus.sensors.GestureSensorImpl.this
                long r8 = r0.samplingIntervalNs
                com.google.android.systemui.columbus.sensors.GestureSensorImpl r0 = com.google.android.systemui.columbus.sensors.GestureSensorImpl.this
                boolean r10 = r0.isRunningInLowSamplingRate
                r1.updateData(r2, r3, r4, r5, r6, r8, r10)
                com.google.android.systemui.columbus.sensors.GestureSensorImpl r0 = com.google.android.systemui.columbus.sensors.GestureSensorImpl.this
                com.google.android.systemui.columbus.sensors.TapRT r0 = r0.tap
                long r1 = r14.timestamp
                int r14 = r0.checkDoubleTapTiming(r1)
                if (r14 == r11) goto L_0x0064
                if (r14 == r12) goto L_0x0040
                goto L_0x0087
            L_0x0040:
                com.google.android.systemui.columbus.sensors.GestureSensorImpl r14 = com.google.android.systemui.columbus.sensors.GestureSensorImpl.this
                android.os.Handler r14 = r14.handler
                kotlin.jvm.functions.Function0<kotlin.Unit> r0 = r13.onTimeout
                if (r0 == 0) goto L_0x0050
                com.google.android.systemui.columbus.sensors.GestureSensorImpl$sam$i$java_lang_Runnable$0 r1 = new com.google.android.systemui.columbus.sensors.GestureSensorImpl$sam$i$java_lang_Runnable$0
                r1.<init>(r0)
                r0 = r1
            L_0x0050:
                java.lang.Runnable r0 = (java.lang.Runnable) r0
                r14.removeCallbacks(r0)
                com.google.android.systemui.columbus.sensors.GestureSensorImpl r14 = com.google.android.systemui.columbus.sensors.GestureSensorImpl.this
                android.os.Handler r14 = r14.handler
                com.google.android.systemui.columbus.sensors.GestureSensorImpl$GestureSensorEventListener$onSensorChanged$$inlined$let$lambda$2 r0 = new com.google.android.systemui.columbus.sensors.GestureSensorImpl$GestureSensorEventListener$onSensorChanged$$inlined$let$lambda$2
                r0.<init>(r13)
                r14.post(r0)
                goto L_0x0087
            L_0x0064:
                com.google.android.systemui.columbus.sensors.GestureSensorImpl r14 = com.google.android.systemui.columbus.sensors.GestureSensorImpl.this
                android.os.Handler r14 = r14.handler
                kotlin.jvm.functions.Function0<kotlin.Unit> r0 = r13.onTimeout
                if (r0 == 0) goto L_0x0074
                com.google.android.systemui.columbus.sensors.GestureSensorImpl$sam$i$java_lang_Runnable$0 r1 = new com.google.android.systemui.columbus.sensors.GestureSensorImpl$sam$i$java_lang_Runnable$0
                r1.<init>(r0)
                r0 = r1
            L_0x0074:
                java.lang.Runnable r0 = (java.lang.Runnable) r0
                r14.removeCallbacks(r0)
                com.google.android.systemui.columbus.sensors.GestureSensorImpl r14 = com.google.android.systemui.columbus.sensors.GestureSensorImpl.this
                android.os.Handler r14 = r14.handler
                com.google.android.systemui.columbus.sensors.GestureSensorImpl$GestureSensorEventListener$onSensorChanged$$inlined$let$lambda$1 r0 = new com.google.android.systemui.columbus.sensors.GestureSensorImpl$GestureSensorEventListener$onSensorChanged$$inlined$let$lambda$1
                r0.<init>(r13)
                r14.post(r0)
            L_0x0087:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.android.systemui.columbus.sensors.GestureSensorImpl.GestureSensorEventListener.onSensorChanged(android.hardware.SensorEvent):void");
        }

        /* renamed from: reset$vendor__unbundled_google__packages__SystemUIGoogle__android_common__sysuig */
        public final void mo20701x8ec13776() {
            Listener access$getListener$p = GestureSensorImpl.this.listener;
            if (access$getListener$p != null) {
                DefaultImpls.onGestureProgress$default(access$getListener$p, GestureSensorImpl.this, 0, null, 4, null);
            }
        }

        /* renamed from: setListening$vendor__unbundled_google__packages__SystemUIGoogle__android_common__sysuig */
        public final void mo20702x7494940c(boolean z, int i) {
            if (!z || GestureSensorImpl.this.accelerometer == null || GestureSensorImpl.this.gyroscope == null) {
                GestureSensorImpl.this.sensorManager.unregisterListener(GestureSensorImpl.this.sensorEventListener);
                GestureSensorImpl.this.setListening(false);
                return;
            }
            GestureSensorImpl.this.sensorManager.registerListener(GestureSensorImpl.this.sensorEventListener, GestureSensorImpl.this.accelerometer, i, GestureSensorImpl.this.handler);
            GestureSensorImpl.this.sensorManager.registerListener(GestureSensorImpl.this.sensorEventListener, GestureSensorImpl.this.gyroscope, i, GestureSensorImpl.this.handler);
            GestureSensorImpl.this.setListening(true);
        }
    }

    public GestureSensorImpl(Context context, GestureConfiguration gestureConfiguration) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(gestureConfiguration, "gestureConfiguration");
        Object systemService = context.getSystemService("sensor");
        if (systemService != null) {
            SensorManager sensorManager2 = (SensorManager) systemService;
            this.sensorManager = sensorManager2;
            this.accelerometer = sensorManager2.getDefaultSensor(1);
            this.gyroscope = this.sensorManager.getDefaultSensor(4);
            this.sensorEventListener = new GestureSensorEventListener();
            this.deviceName = Build.MODEL;
            this.samplingIntervalNs = 2500000;
            this.tap = new TapRT(160000000, context.getAssets(), this.deviceName);
            return;
        }
        throw new TypeCastException("null cannot be cast to non-null type android.hardware.SensorManager");
    }

    public boolean isListening() {
        return this.isListening;
    }

    public void setListening(boolean z) {
        this.isListening = z;
    }

    public void setGestureListener(Listener listener2) {
        this.listener = listener2;
    }

    public void startListening(boolean z) {
        if (z) {
            this.sensorEventListener.mo20702x7494940c(true, 0);
            this.tap.getLowpassKey().setPara(0.2f);
            this.tap.getHighpassKey().setPara(0.2f);
            this.tap.getPositivePeakDetector().setMinNoiseTolerate(0.05f);
            this.tap.getPositivePeakDetector().setWindowSize(64);
            this.tap.reset(false);
            return;
        }
        this.sensorEventListener.mo20702x7494940c(true, 21000);
        this.tap.getLowpassKey().setPara(1.0f);
        this.tap.getHighpassKey().setPara(0.3f);
        this.tap.getPositivePeakDetector().setMinNoiseTolerate(0.02f);
        this.tap.getPositivePeakDetector().setWindowSize(8);
        this.tap.getNegativePeakDetection().setMinNoiseTolerate(0.02f);
        this.tap.getNegativePeakDetection().setWindowSize(8);
        this.tap.reset(true);
    }

    public void stopListening() {
        this.sensorEventListener.mo20702x7494940c(false, 0);
        this.sensorEventListener.mo20701x8ec13776();
    }
}
