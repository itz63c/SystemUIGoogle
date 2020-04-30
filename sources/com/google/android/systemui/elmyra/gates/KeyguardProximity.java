package com.google.android.systemui.elmyra.gates;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import com.android.systemui.C2012R$integer;
import com.android.systemui.Dependency;
import com.android.systemui.util.sensors.AsyncSensorManager;
import com.google.android.systemui.elmyra.gates.Gate.Listener;

public class KeyguardProximity extends Gate {
    private final Listener mGateListener = new Listener() {
        public void onGateChanged(Gate gate) {
            KeyguardProximity.this.updateProximityListener();
        }
    };
    /* access modifiers changed from: private */
    public boolean mIsListening = false;
    private final KeyguardVisibility mKeyguardGate;
    /* access modifiers changed from: private */
    public boolean mProximityBlocked = false;
    /* access modifiers changed from: private */
    public final float mProximityThreshold;
    private final Sensor mSensor;
    private final SensorEventListener mSensorListener = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int i) {
        }

        public void onSensorChanged(SensorEvent sensorEvent) {
            boolean z = false;
            if (sensorEvent.values[0] < KeyguardProximity.this.mProximityThreshold) {
                z = true;
            }
            if (KeyguardProximity.this.mIsListening && z != KeyguardProximity.this.mProximityBlocked) {
                KeyguardProximity.this.mProximityBlocked = z;
                KeyguardProximity.this.notifyListener();
            }
        }
    };
    private final SensorManager mSensorManager;

    public KeyguardProximity(Context context) {
        super(context);
        SensorManager sensorManager = (SensorManager) Dependency.get(AsyncSensorManager.class);
        this.mSensorManager = sensorManager;
        Sensor defaultSensor = sensorManager.getDefaultSensor(8);
        this.mSensor = defaultSensor;
        if (defaultSensor == null) {
            this.mProximityThreshold = 0.0f;
            this.mKeyguardGate = null;
            Log.e("Elmyra/KeyguardProximity", "Could not find any Sensor.TYPE_PROXIMITY");
            return;
        }
        this.mProximityThreshold = Math.min(defaultSensor.getMaximumRange(), (float) context.getResources().getInteger(C2012R$integer.elmyra_keyguard_proximity_threshold));
        KeyguardVisibility keyguardVisibility = new KeyguardVisibility(context);
        this.mKeyguardGate = keyguardVisibility;
        keyguardVisibility.setListener(this.mGateListener);
        updateProximityListener();
    }

    /* access modifiers changed from: protected */
    public void onActivate() {
        if (this.mSensor != null) {
            this.mKeyguardGate.activate();
            updateProximityListener();
        }
    }

    /* access modifiers changed from: protected */
    public void onDeactivate() {
        if (this.mSensor != null) {
            this.mKeyguardGate.deactivate();
            updateProximityListener();
        }
    }

    /* access modifiers changed from: protected */
    public boolean isBlocked() {
        return this.mIsListening && this.mProximityBlocked;
    }

    /* access modifiers changed from: private */
    public void updateProximityListener() {
        if (this.mProximityBlocked) {
            this.mProximityBlocked = false;
            notifyListener();
        }
        if (!isActive() || !this.mKeyguardGate.isKeyguardShowing() || this.mKeyguardGate.isKeyguardOccluded()) {
            this.mSensorManager.unregisterListener(this.mSensorListener);
            this.mIsListening = false;
        } else if (!this.mIsListening) {
            this.mSensorManager.registerListener(this.mSensorListener, this.mSensor, 3);
            this.mIsListening = true;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" [mIsListening -> ");
        sb.append(this.mIsListening);
        sb.append("]");
        return sb.toString();
    }
}
