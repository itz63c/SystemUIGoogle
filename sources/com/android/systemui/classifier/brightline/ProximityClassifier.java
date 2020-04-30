package com.android.systemui.classifier.brightline;

import android.view.MotionEvent;
import com.android.systemui.util.DeviceConfigProxy;
import com.android.systemui.util.sensors.ProximitySensor.ProximityEvent;

class ProximityClassifier extends FalsingClassifier {
    private final DistanceClassifier mDistanceClassifier;
    private long mGestureStartTimeNs;
    private boolean mNear;
    private long mNearDurationNs;
    private final float mPercentCoveredThreshold;
    private float mPercentNear;
    private long mPrevNearTimeNs;

    ProximityClassifier(DistanceClassifier distanceClassifier, FalsingDataProvider falsingDataProvider, DeviceConfigProxy deviceConfigProxy) {
        super(falsingDataProvider);
        this.mDistanceClassifier = distanceClassifier;
        this.mPercentCoveredThreshold = deviceConfigProxy.getFloat("systemui", "brightline_falsing_proximity_percent_covered_threshold", 0.1f);
    }

    /* access modifiers changed from: 0000 */
    public void onSessionStarted() {
        this.mPrevNearTimeNs = 0;
        this.mPercentNear = 0.0f;
    }

    /* access modifiers changed from: 0000 */
    public void onSessionEnded() {
        this.mPrevNearTimeNs = 0;
        this.mPercentNear = 0.0f;
    }

    public void onTouchEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            this.mGestureStartTimeNs = motionEvent.getEventTimeNano();
            if (this.mPrevNearTimeNs > 0) {
                this.mPrevNearTimeNs = motionEvent.getEventTimeNano();
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Gesture start time: ");
            sb.append(this.mGestureStartTimeNs);
            FalsingClassifier.logDebug(sb.toString());
            this.mNearDurationNs = 0;
        }
        if (actionMasked == 1 || actionMasked == 3) {
            update(this.mNear, motionEvent.getEventTimeNano());
            long eventTimeNano = motionEvent.getEventTimeNano() - this.mGestureStartTimeNs;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Gesture duration, Proximity duration: ");
            sb2.append(eventTimeNano);
            sb2.append(", ");
            sb2.append(this.mNearDurationNs);
            FalsingClassifier.logDebug(sb2.toString());
            if (eventTimeNano == 0) {
                this.mPercentNear = this.mNear ? 1.0f : 0.0f;
            } else {
                this.mPercentNear = ((float) this.mNearDurationNs) / ((float) eventTimeNano);
            }
        }
    }

    public void onProximityEvent(ProximityEvent proximityEvent) {
        boolean near = proximityEvent.getNear();
        long timestampNs = proximityEvent.getTimestampNs();
        StringBuilder sb = new StringBuilder();
        sb.append("Sensor is: ");
        sb.append(near);
        sb.append(" at time ");
        sb.append(timestampNs);
        FalsingClassifier.logDebug(sb.toString());
        update(near, timestampNs);
    }

    public boolean isFalseTouch() {
        if (getInteractionType() == 0) {
            return false;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Percent of gesture in proximity: ");
        sb.append(this.mPercentNear);
        FalsingClassifier.logInfo(sb.toString());
        if (this.mPercentNear > this.mPercentCoveredThreshold) {
            return !this.mDistanceClassifier.isLongSwipe();
        }
        return false;
    }

    /* access modifiers changed from: 0000 */
    public String getReason() {
        return String.format(null, "{percentInProximity=%f, threshold=%f, distanceClassifier=%s}", new Object[]{Float.valueOf(this.mPercentNear), Float.valueOf(this.mPercentCoveredThreshold), this.mDistanceClassifier.getReason()});
    }

    private void update(boolean z, long j) {
        long j2 = this.mPrevNearTimeNs;
        if (j2 != 0 && j > j2 && this.mNear) {
            this.mNearDurationNs += j - j2;
            StringBuilder sb = new StringBuilder();
            sb.append("Updating duration: ");
            sb.append(this.mNearDurationNs);
            FalsingClassifier.logDebug(sb.toString());
        }
        if (z) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Set prevNearTimeNs: ");
            sb2.append(j);
            FalsingClassifier.logDebug(sb2.toString());
            this.mPrevNearTimeNs = j;
        }
        this.mNear = z;
    }
}
