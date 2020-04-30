package com.android.systemui.classifier.brightline;

import android.view.MotionEvent;
import android.view.VelocityTracker;
import com.android.systemui.util.DeviceConfigProxy;
import java.util.List;

class DistanceClassifier extends FalsingClassifier {
    private DistanceVectors mCachedDistance;
    private boolean mDistanceDirty = true;
    private final float mHorizontalFlingThresholdPx;
    private final float mHorizontalSwipeThresholdPx;
    private final float mVelocityToDistanceMultiplier;
    private final float mVerticalFlingThresholdPx;
    private final float mVerticalSwipeThresholdPx;

    private class DistanceVectors {
        final float mDx;
        final float mDy;
        /* access modifiers changed from: private */
        public final float mVx;
        /* access modifiers changed from: private */
        public final float mVy;

        DistanceVectors(DistanceClassifier distanceClassifier, float f, float f2, float f3, float f4) {
            this.mDx = f;
            this.mDy = f2;
            this.mVx = f3;
            this.mVy = f4;
        }

        public String toString() {
            return String.format(null, "{dx=%f, vx=%f, dy=%f, vy=%f}", new Object[]{Float.valueOf(this.mDx), Float.valueOf(this.mVx), Float.valueOf(this.mDy), Float.valueOf(this.mVy)});
        }
    }

    DistanceClassifier(FalsingDataProvider falsingDataProvider, DeviceConfigProxy deviceConfigProxy) {
        super(falsingDataProvider);
        String str = "systemui";
        this.mVelocityToDistanceMultiplier = deviceConfigProxy.getFloat(str, "brightline_falsing_distance_velcoity_to_distance", 30.0f);
        float f = deviceConfigProxy.getFloat(str, "brightline_falsing_distance_horizontal_fling_threshold_in", 1.0f);
        float f2 = deviceConfigProxy.getFloat(str, "brightline_falsing_distance_vertical_fling_threshold_in", 1.5f);
        String str2 = "brightline_falsing_distance_horizontal_swipe_threshold_in";
        float f3 = deviceConfigProxy.getFloat(str, str2, 3.0f);
        float f4 = deviceConfigProxy.getFloat(str, str2, 3.0f);
        float f5 = deviceConfigProxy.getFloat(str, "brightline_falsing_distance_screen_fraction_max_distance", 0.8f);
        this.mHorizontalFlingThresholdPx = Math.min(((float) getWidthPixels()) * f5, f * getXdpi());
        this.mVerticalFlingThresholdPx = Math.min(((float) getHeightPixels()) * f5, f2 * getYdpi());
        this.mHorizontalSwipeThresholdPx = Math.min(((float) getWidthPixels()) * f5, f3 * getXdpi());
        this.mVerticalSwipeThresholdPx = Math.min(((float) getHeightPixels()) * f5, f4 * getYdpi());
    }

    private DistanceVectors getDistances() {
        if (this.mDistanceDirty) {
            this.mCachedDistance = calculateDistances();
            this.mDistanceDirty = false;
        }
        return this.mCachedDistance;
    }

    private DistanceVectors calculateDistances() {
        VelocityTracker obtain = VelocityTracker.obtain();
        List<MotionEvent> recentMotionEvents = getRecentMotionEvents();
        if (recentMotionEvents.size() < 3) {
            StringBuilder sb = new StringBuilder();
            sb.append("Only ");
            sb.append(recentMotionEvents.size());
            sb.append(" motion events recorded.");
            FalsingClassifier.logDebug(sb.toString());
            DistanceVectors distanceVectors = new DistanceVectors(this, 0.0f, 0.0f, 0.0f, 0.0f);
            return distanceVectors;
        }
        for (MotionEvent addMovement : recentMotionEvents) {
            obtain.addMovement(addMovement);
        }
        obtain.computeCurrentVelocity(1);
        float xVelocity = obtain.getXVelocity();
        float yVelocity = obtain.getYVelocity();
        obtain.recycle();
        float x = getLastMotionEvent().getX() - getFirstMotionEvent().getX();
        float y = getLastMotionEvent().getY() - getFirstMotionEvent().getY();
        StringBuilder sb2 = new StringBuilder();
        sb2.append("dX: ");
        sb2.append(x);
        sb2.append(" dY: ");
        sb2.append(y);
        sb2.append(" xV: ");
        sb2.append(xVelocity);
        sb2.append(" yV: ");
        sb2.append(yVelocity);
        FalsingClassifier.logInfo(sb2.toString());
        DistanceVectors distanceVectors2 = new DistanceVectors(this, x, y, xVelocity, yVelocity);
        return distanceVectors2;
    }

    public void onTouchEvent(MotionEvent motionEvent) {
        this.mDistanceDirty = true;
    }

    public boolean isFalseTouch() {
        return !getPassedFlingThreshold();
    }

    /* access modifiers changed from: 0000 */
    public String getReason() {
        return String.format(null, "{distanceVectors=%s, isHorizontal=%s, velocityToDistanceMultiplier=%f, horizontalFlingThreshold=%f, verticalFlingThreshold=%f, horizontalSwipeThreshold=%f, verticalSwipeThreshold=%s}", new Object[]{getDistances(), Boolean.valueOf(isHorizontal()), Float.valueOf(this.mVelocityToDistanceMultiplier), Float.valueOf(this.mHorizontalFlingThresholdPx), Float.valueOf(this.mVerticalFlingThresholdPx), Float.valueOf(this.mHorizontalSwipeThresholdPx), Float.valueOf(this.mVerticalSwipeThresholdPx)});
    }

    /* access modifiers changed from: 0000 */
    public boolean isLongSwipe() {
        boolean passedDistanceThreshold = getPassedDistanceThreshold();
        StringBuilder sb = new StringBuilder();
        sb.append("Is longSwipe? ");
        sb.append(passedDistanceThreshold);
        FalsingClassifier.logDebug(sb.toString());
        return passedDistanceThreshold;
    }

    private boolean getPassedDistanceThreshold() {
        DistanceVectors distances = getDistances();
        boolean z = true;
        String str = "Threshold: ";
        if (isHorizontal()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Horizontal swipe distance: ");
            sb.append(Math.abs(distances.mDx));
            FalsingClassifier.logDebug(sb.toString());
            StringBuilder sb2 = new StringBuilder();
            sb2.append(str);
            sb2.append(this.mHorizontalSwipeThresholdPx);
            FalsingClassifier.logDebug(sb2.toString());
            if (Math.abs(distances.mDx) < this.mHorizontalSwipeThresholdPx) {
                z = false;
            }
            return z;
        }
        StringBuilder sb3 = new StringBuilder();
        sb3.append("Vertical swipe distance: ");
        sb3.append(Math.abs(distances.mDy));
        FalsingClassifier.logDebug(sb3.toString());
        StringBuilder sb4 = new StringBuilder();
        sb4.append(str);
        sb4.append(this.mVerticalSwipeThresholdPx);
        FalsingClassifier.logDebug(sb4.toString());
        if (Math.abs(distances.mDy) < this.mVerticalSwipeThresholdPx) {
            z = false;
        }
        return z;
    }

    private boolean getPassedFlingThreshold() {
        DistanceVectors distances = getDistances();
        float access$000 = distances.mDx + (distances.mVx * this.mVelocityToDistanceMultiplier);
        float access$100 = distances.mDy + (distances.mVy * this.mVelocityToDistanceMultiplier);
        boolean z = true;
        String str = "Threshold: ";
        String str2 = ", ";
        if (isHorizontal()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Horizontal swipe and fling distance: ");
            sb.append(distances.mDx);
            sb.append(str2);
            sb.append(distances.mVx * this.mVelocityToDistanceMultiplier);
            FalsingClassifier.logDebug(sb.toString());
            StringBuilder sb2 = new StringBuilder();
            sb2.append(str);
            sb2.append(this.mHorizontalFlingThresholdPx);
            FalsingClassifier.logDebug(sb2.toString());
            if (Math.abs(access$000) < this.mHorizontalFlingThresholdPx) {
                z = false;
            }
            return z;
        }
        StringBuilder sb3 = new StringBuilder();
        sb3.append("Vertical swipe and fling distance: ");
        sb3.append(distances.mDy);
        sb3.append(str2);
        sb3.append(distances.mVy * this.mVelocityToDistanceMultiplier);
        FalsingClassifier.logDebug(sb3.toString());
        StringBuilder sb4 = new StringBuilder();
        sb4.append(str);
        sb4.append(this.mVerticalFlingThresholdPx);
        FalsingClassifier.logDebug(sb4.toString());
        if (Math.abs(access$100) < this.mVerticalFlingThresholdPx) {
            z = false;
        }
        return z;
    }
}
