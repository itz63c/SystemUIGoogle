package com.android.systemui.util.sensors;

import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Handler;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2017R$string;
import com.android.systemui.util.sensors.ProximitySensor.ProximityCheck;
import com.android.systemui.util.sensors.ProximitySensor.ProximityEvent;
import com.android.systemui.util.sensors.ProximitySensor.ProximitySensorListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class ProximitySensor {
    private static final boolean DEBUG = Log.isLoggable("ProxSensor", 3);
    @VisibleForTesting
    ProximityEvent mLastEvent;
    private List<ProximitySensorListener> mListeners = new ArrayList();
    private boolean mPaused;
    private boolean mRegistered;
    private final Sensor mSensor;
    private int mSensorDelay;
    private SensorEventListener mSensorEventListener;
    private final AsyncSensorManager mSensorManager;
    private String mTag;
    private final float mThreshold;

    public static class ProximityCheck implements Runnable {
        private List<Consumer<Boolean>> mCallbacks = new ArrayList();
        private final Handler mHandler;
        private final ProximitySensor mSensor;

        public ProximityCheck(ProximitySensor proximitySensor, Handler handler) {
            this.mSensor = proximitySensor;
            proximitySensor.setTag("prox_check");
            this.mHandler = handler;
            this.mSensor.pause();
            this.mSensor.register(new ProximitySensorListener() {
                public final void onSensorEvent(ProximityEvent proximityEvent) {
                    ProximityCheck.this.lambda$new$1$ProximitySensor$ProximityCheck(proximityEvent);
                }
            });
        }

        /* access modifiers changed from: private */
        /* renamed from: lambda$new$1 */
        public /* synthetic */ void lambda$new$1$ProximitySensor$ProximityCheck(ProximityEvent proximityEvent) {
            this.mCallbacks.forEach(new Consumer() {
                public final void accept(Object obj) {
                    ProximityCheck.lambda$new$0(ProximityEvent.this, (Consumer) obj);
                }
            });
            this.mCallbacks.clear();
            this.mSensor.pause();
        }

        static /* synthetic */ void lambda$new$0(ProximityEvent proximityEvent, Consumer consumer) {
            consumer.accept(proximityEvent == null ? null : Boolean.valueOf(proximityEvent.getNear()));
        }

        public void run() {
            this.mSensor.pause();
            this.mSensor.alertListeners();
        }

        public void check(long j, Consumer<Boolean> consumer) {
            if (!this.mSensor.getSensorAvailable()) {
                consumer.accept(null);
            }
            this.mCallbacks.add(consumer);
            if (!this.mSensor.isRegistered()) {
                this.mSensor.resume();
                this.mHandler.postDelayed(this, j);
            }
        }
    }

    public static class ProximityEvent {
        private final boolean mNear;
        private final long mTimestampNs;

        public ProximityEvent(boolean z, long j) {
            this.mNear = z;
            this.mTimestampNs = j;
        }

        public boolean getNear() {
            return this.mNear;
        }

        public long getTimestampNs() {
            return this.mTimestampNs;
        }

        public String toString() {
            return String.format(null, "{near=%s, timestamp_ns=%d}", new Object[]{Boolean.valueOf(this.mNear), Long.valueOf(this.mTimestampNs)});
        }
    }

    public interface ProximitySensorListener {
        void onSensorEvent(ProximityEvent proximityEvent);
    }

    public ProximitySensor(Resources resources, AsyncSensorManager asyncSensorManager) {
        Sensor sensor = null;
        this.mTag = null;
        this.mSensorDelay = 3;
        this.mSensorEventListener = new SensorEventListener() {
            public void onAccuracyChanged(Sensor sensor, int i) {
            }

            public synchronized void onSensorChanged(SensorEvent sensorEvent) {
                ProximitySensor.this.onSensorEvent(sensorEvent);
            }
        };
        this.mSensorManager = asyncSensorManager;
        Sensor findCustomProxSensor = findCustomProxSensor(resources);
        float f = 0.0f;
        if (findCustomProxSensor != null) {
            try {
                f = getCustomProxThreshold(resources);
            } catch (IllegalStateException e) {
                Log.e("ProxSensor", "Can not load custom proximity sensor.", e);
            }
        }
        sensor = findCustomProxSensor;
        if (sensor == null) {
            sensor = asyncSensorManager.getDefaultSensor(8);
            if (sensor != null) {
                f = sensor.getMaximumRange();
            }
        }
        this.mThreshold = f;
        this.mSensor = sensor;
    }

    public void setTag(String str) {
        this.mTag = str;
    }

    public void setSensorDelay(int i) {
        this.mSensorDelay = i;
    }

    public void pause() {
        this.mPaused = true;
        unregisterInternal();
    }

    public void resume() {
        this.mPaused = false;
        registerInternal();
    }

    private Sensor findCustomProxSensor(Resources resources) {
        String string = resources.getString(C2017R$string.proximity_sensor_type);
        Sensor sensor = null;
        if (string.isEmpty()) {
            return null;
        }
        Iterator it = this.mSensorManager.getSensorList(-1).iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Sensor sensor2 = (Sensor) it.next();
            if (string.equals(sensor2.getStringType())) {
                sensor = sensor2;
                break;
            }
        }
        return sensor;
    }

    private float getCustomProxThreshold(Resources resources) {
        try {
            return resources.getFloat(C2009R$dimen.proximity_sensor_threshold);
        } catch (NotFoundException unused) {
            throw new IllegalStateException("R.dimen.proximity_sensor_threshold must be set.");
        }
    }

    public boolean isRegistered() {
        return this.mRegistered;
    }

    public boolean getSensorAvailable() {
        return this.mSensor != null;
    }

    public boolean register(ProximitySensorListener proximitySensorListener) {
        if (!getSensorAvailable()) {
            return false;
        }
        if (this.mListeners.contains(proximitySensorListener)) {
            StringBuilder sb = new StringBuilder();
            sb.append("ProxListener registered multiple times: ");
            sb.append(proximitySensorListener);
            Log.d("ProxSensor", sb.toString());
        } else {
            this.mListeners.add(proximitySensorListener);
        }
        registerInternal();
        return true;
    }

    /* access modifiers changed from: protected */
    public void registerInternal() {
        if (!this.mRegistered && !this.mPaused && !this.mListeners.isEmpty()) {
            logDebug("Registering sensor listener");
            this.mRegistered = true;
            this.mSensorManager.registerListener(this.mSensorEventListener, this.mSensor, this.mSensorDelay);
        }
    }

    public void unregister(ProximitySensorListener proximitySensorListener) {
        this.mListeners.remove(proximitySensorListener);
        if (this.mListeners.size() == 0) {
            unregisterInternal();
        }
    }

    /* access modifiers changed from: protected */
    public void unregisterInternal() {
        if (this.mRegistered) {
            logDebug("unregistering sensor listener");
            this.mSensorManager.unregisterListener(this.mSensorEventListener);
            this.mRegistered = false;
        }
    }

    public Boolean isNear() {
        if (getSensorAvailable()) {
            ProximityEvent proximityEvent = this.mLastEvent;
            if (proximityEvent != null) {
                return Boolean.valueOf(proximityEvent.getNear());
            }
        }
        return null;
    }

    public void alertListeners() {
        this.mListeners.forEach(new Consumer() {
            public final void accept(Object obj) {
                ProximitySensor.this.lambda$alertListeners$0$ProximitySensor((ProximitySensorListener) obj);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$alertListeners$0 */
    public /* synthetic */ void lambda$alertListeners$0$ProximitySensor(ProximitySensorListener proximitySensorListener) {
        proximitySensorListener.onSensorEvent(this.mLastEvent);
    }

    /* access modifiers changed from: private */
    public void onSensorEvent(SensorEvent sensorEvent) {
        boolean z = false;
        if (sensorEvent.values[0] < this.mThreshold) {
            z = true;
        }
        this.mLastEvent = new ProximityEvent(z, sensorEvent.timestamp);
        alertListeners();
    }

    public String toString() {
        return String.format("{registered=%s, paused=%s, near=%s, sensor=%s}", new Object[]{Boolean.valueOf(isRegistered()), Boolean.valueOf(this.mPaused), isNear(), this.mSensor});
    }

    private void logDebug(String str) {
        String str2;
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            if (this.mTag != null) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("[");
                sb2.append(this.mTag);
                sb2.append("] ");
                str2 = sb2.toString();
            } else {
                str2 = "";
            }
            sb.append(str2);
            sb.append(str);
            Log.d("ProxSensor", sb.toString());
        }
    }
}
