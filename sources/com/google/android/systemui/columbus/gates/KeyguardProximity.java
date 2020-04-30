package com.google.android.systemui.columbus.gates;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.util.Log;
import com.android.systemui.util.sensors.AsyncSensorManager;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: KeyguardProximity.kt */
public final class KeyguardProximity extends Gate {
    private final KeyguardProximity$gateListener$1 gateListener = new KeyguardProximity$gateListener$1(this);
    private boolean isListening;
    private final KeyguardVisibility keyguardGate;
    private boolean proximityBlocked;
    private final float proximityThreshold;
    private final Sensor sensor;
    private final KeyguardProximity$sensorListener$1 sensorListener = new KeyguardProximity$sensorListener$1(this);
    private final SensorManager sensorManager;

    public KeyguardProximity(Context context, AsyncSensorManager asyncSensorManager, KeyguardVisibility keyguardVisibility) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(asyncSensorManager, "asyncSensorManager");
        Intrinsics.checkParameterIsNotNull(keyguardVisibility, "keyguardGate");
        super(context);
        this.keyguardGate = keyguardVisibility;
        this.sensorManager = asyncSensorManager;
        this.sensor = asyncSensorManager.getDefaultSensor(8);
        Sensor sensor2 = this.sensor;
        if (sensor2 == null) {
            this.proximityThreshold = 0.0f;
            Log.e("Columbus/KeyguardProximity", "Could not find any Sensor.TYPE_PROXIMITY");
            return;
        }
        this.proximityThreshold = Math.min(sensor2.getMaximumRange(), 5.0f);
        this.keyguardGate.setListener(this.gateListener);
        updateProximityListener();
    }

    /* access modifiers changed from: protected */
    public void onActivate() {
        if (this.sensor != null) {
            this.keyguardGate.activate();
            updateProximityListener();
        }
    }

    /* access modifiers changed from: protected */
    public void onDeactivate() {
        if (this.sensor != null) {
            this.keyguardGate.deactivate();
            updateProximityListener();
        }
    }

    /* access modifiers changed from: protected */
    public boolean isBlocked() {
        return this.isListening && this.proximityBlocked;
    }

    /* access modifiers changed from: private */
    public final void handleSensorEvent(SensorEvent sensorEvent) {
        boolean z = false;
        if (sensorEvent.values[0] < this.proximityThreshold) {
            z = true;
        }
        if (this.isListening && z != this.proximityBlocked) {
            this.proximityBlocked = z;
            notifyListener();
        }
    }

    /* access modifiers changed from: private */
    public final void updateProximityListener() {
        if (this.proximityBlocked) {
            this.proximityBlocked = false;
            notifyListener();
        }
        if (!getActive() || !this.keyguardGate.isKeyguardShowing() || this.keyguardGate.isKeyguardOccluded()) {
            this.sensorManager.unregisterListener(this.sensorListener);
            this.isListening = false;
        } else if (!this.isListening) {
            Sensor sensor2 = this.sensor;
            if (sensor2 != null) {
                this.sensorManager.registerListener(this.sensorListener, sensor2, 3);
                this.isListening = true;
            }
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" [isListening -> ");
        sb.append(this.isListening);
        sb.append("]");
        return sb.toString();
    }
}
