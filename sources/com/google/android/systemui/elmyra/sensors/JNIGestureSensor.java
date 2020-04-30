package com.google.android.systemui.elmyra.sensors;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.provider.Settings.Secure;
import android.util.Log;
import com.android.keyguard.KeyguardUpdateMonitor;
import com.android.keyguard.KeyguardUpdateMonitorCallback;
import com.android.systemui.C2016R$raw;
import com.android.systemui.C2017R$string;
import com.android.systemui.Dependency;
import com.google.android.systemui.elmyra.proto.nano.ChassisProtos$Chassis;
import com.google.android.systemui.elmyra.sensors.config.GestureConfiguration;
import com.google.android.systemui.elmyra.sensors.config.GestureConfiguration.Listener;
import com.google.android.systemui.elmyra.sensors.config.SensorCalibration;
import com.google.protobuf.nano.MessageNano;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class JNIGestureSensor implements GestureSensor {
    private static final String DISABLE_SETTING = "com.google.android.systemui.elmyra.disable_jni";
    private static final int SENSOR_RATE = 20000;
    private static final String TAG = "Elmyra/JNIGestureSensor";
    private static boolean sLibraryLoaded;
    private final Context mContext;
    /* access modifiers changed from: private */
    public final AssistGestureController mController;
    private final GestureConfiguration mGestureConfiguration;
    private boolean mIsListening;
    private final KeyguardUpdateMonitorCallback mKeyguardUpdateMonitorCallback = new KeyguardUpdateMonitorCallback() {
        private boolean mWasListening;

        public void onStartedWakingUp() {
            JNIGestureSensor.this.mController.onGestureProgress(0.0f);
            if (this.mWasListening) {
                JNIGestureSensor.this.startListening();
            }
        }

        public void onFinishedGoingToSleep(int i) {
            JNIGestureSensor.this.mController.onGestureProgress(0.0f);
            this.mWasListening = JNIGestureSensor.this.isListening();
            JNIGestureSensor.this.stopListening();
        }
    };
    private long mNativeService;
    private int mSensorCount;
    private final String mSensorStringType;

    private native boolean createNativeService(byte[] bArr);

    private native void destroyNativeService();

    private native boolean setGestureDetector(byte[] bArr);

    private native boolean startListeningNative(String str, int i);

    private native void stopListeningNative();

    private void updateConfiguration() {
    }

    static {
        try {
            System.loadLibrary("elmyra");
            sLibraryLoaded = true;
        } catch (Throwable th) {
            StringBuilder sb = new StringBuilder();
            sb.append("Could not load JNI component: ");
            sb.append(th);
            Log.w(TAG, sb.toString());
            sLibraryLoaded = false;
        }
    }

    public static boolean isAvailable(Context context) {
        if (Secure.getInt(context.getContentResolver(), DISABLE_SETTING, 0) == 1 || !sLibraryLoaded) {
            return false;
        }
        byte[] chassisAsset = getChassisAsset(context);
        return (chassisAsset == null || chassisAsset.length == 0) ? false : true;
    }

    private static byte[] getChassisAsset(Context context) {
        try {
            return readAllBytes(context.getResources().openRawResource(C2016R$raw.elmyra_chassis));
        } catch (NotFoundException | IOException e) {
            Log.e(TAG, "Could not load chassis resource", e);
            return null;
        }
    }

    private static byte[] readAllBytes(InputStream inputStream) throws IOException {
        int i = 1024;
        byte[] bArr = new byte[1024];
        int i2 = 0;
        while (true) {
            int read = inputStream.read(bArr, i2, i - i2);
            if (read > 0) {
                i2 += read;
            } else if (read < 0) {
                break;
            } else {
                i <<= 1;
                bArr = Arrays.copyOf(bArr, i);
            }
        }
        return i == i2 ? bArr : Arrays.copyOf(bArr, i2);
    }

    public JNIGestureSensor(Context context, GestureConfiguration gestureConfiguration) {
        String str = "touch_2_sensitivity";
        String str2 = TAG;
        this.mContext = context;
        this.mController = new AssistGestureController(context, this, gestureConfiguration);
        this.mSensorStringType = context.getResources().getString(C2017R$string.elmyra_raw_sensor_string_type);
        this.mGestureConfiguration = gestureConfiguration;
        gestureConfiguration.setListener(new Listener() {
            public final void onGestureConfigurationChanged(GestureConfiguration gestureConfiguration) {
                JNIGestureSensor.this.lambda$new$0$JNIGestureSensor(gestureConfiguration);
            }
        });
        ((KeyguardUpdateMonitor) Dependency.get(KeyguardUpdateMonitor.class)).registerCallback(this.mKeyguardUpdateMonitorCallback);
        byte[] chassisAsset = getChassisAsset(context);
        if (chassisAsset != null && chassisAsset.length != 0) {
            try {
                ChassisProtos$Chassis chassisProtos$Chassis = new ChassisProtos$Chassis();
                MessageNano.mergeFrom(chassisProtos$Chassis, chassisAsset);
                this.mSensorCount = chassisProtos$Chassis.sensors.length;
                for (int i = 0; i < this.mSensorCount; i++) {
                    SensorCalibration calibration = SensorCalibration.getCalibration(i);
                    if (calibration == null || !calibration.contains(str)) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Error reading calibration for sensor ");
                        sb.append(i);
                        Log.w(str2, sb.toString());
                    } else {
                        chassisProtos$Chassis.sensors[i].sensitivity = 1.0f / calibration.get(str);
                    }
                }
                createNativeService(chassisAsset);
            } catch (Exception e) {
                Log.e(str2, "Error reading chassis file", e);
                this.mSensorCount = 0;
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$JNIGestureSensor(GestureConfiguration gestureConfiguration) {
        updateConfiguration();
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        super.finalize();
        destroyNativeService();
    }

    public void setGestureListener(GestureSensor.Listener listener) {
        this.mController.setGestureListener(listener);
    }

    public void startListening() {
        if (!this.mIsListening && startListeningNative(this.mSensorStringType, SENSOR_RATE)) {
            updateConfiguration();
            this.mIsListening = true;
        }
    }

    public boolean isListening() {
        return this.mIsListening;
    }

    public void stopListening() {
        if (this.mIsListening) {
            stopListeningNative();
            this.mIsListening = false;
        }
    }

    private void onGestureDetected() {
        this.mController.onGestureDetected(null);
    }

    private void onGestureProgress(float f) {
        this.mController.onGestureProgress(f);
    }
}
