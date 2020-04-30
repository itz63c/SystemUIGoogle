package com.google.android.systemui.columbus.sensors;

import java.util.Random;

/* compiled from: GestureSensor.kt */
public interface GestureSensor extends Sensor {

    /* compiled from: GestureSensor.kt */
    public static final class DetectionProperties {
        private final long actionId = new Random().nextLong();
        private final boolean isHapticConsumed;
        private final boolean isHostSuspended;

        public DetectionProperties(boolean z, boolean z2) {
            this.isHostSuspended = z;
            this.isHapticConsumed = z2;
        }

        public final boolean isHapticConsumed() {
            return this.isHapticConsumed;
        }

        public final boolean isHostSuspended() {
            return this.isHostSuspended;
        }

        public final long getActionId() {
            return this.actionId;
        }
    }

    /* compiled from: GestureSensor.kt */
    public interface Listener {

        /* compiled from: GestureSensor.kt */
        public static final class DefaultImpls {
            public static /* synthetic */ void onGestureProgress$default(Listener listener, GestureSensor gestureSensor, int i, DetectionProperties detectionProperties, int i2, Object obj) {
                if (obj == null) {
                    if ((i2 & 4) != 0) {
                        detectionProperties = null;
                    }
                    listener.onGestureProgress(gestureSensor, i, detectionProperties);
                    return;
                }
                throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: onGestureProgress");
            }
        }

        void onGestureProgress(GestureSensor gestureSensor, int i, DetectionProperties detectionProperties);
    }

    void setGestureListener(Listener listener);
}
