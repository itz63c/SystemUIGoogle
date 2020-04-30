package com.android.systemui.classifier.brightline;

import android.view.MotionEvent;
import com.android.systemui.util.sensors.ProximitySensor.ProximityEvent;
import java.util.List;

abstract class FalsingClassifier {
    private final FalsingDataProvider mDataProvider;

    /* access modifiers changed from: 0000 */
    public abstract String getReason();

    /* access modifiers changed from: 0000 */
    public abstract boolean isFalseTouch();

    /* access modifiers changed from: 0000 */
    public void onProximityEvent(ProximityEvent proximityEvent) {
    }

    /* access modifiers changed from: 0000 */
    public void onSessionEnded() {
    }

    /* access modifiers changed from: 0000 */
    public void onSessionStarted() {
    }

    /* access modifiers changed from: 0000 */
    public void onTouchEvent(MotionEvent motionEvent) {
    }

    FalsingClassifier(FalsingDataProvider falsingDataProvider) {
        this.mDataProvider = falsingDataProvider;
    }

    /* access modifiers changed from: 0000 */
    public List<MotionEvent> getRecentMotionEvents() {
        return this.mDataProvider.getRecentMotionEvents();
    }

    /* access modifiers changed from: 0000 */
    public MotionEvent getFirstMotionEvent() {
        return this.mDataProvider.getFirstRecentMotionEvent();
    }

    /* access modifiers changed from: 0000 */
    public MotionEvent getLastMotionEvent() {
        return this.mDataProvider.getLastMotionEvent();
    }

    /* access modifiers changed from: 0000 */
    public boolean isHorizontal() {
        return this.mDataProvider.isHorizontal();
    }

    /* access modifiers changed from: 0000 */
    public boolean isRight() {
        return this.mDataProvider.isRight();
    }

    /* access modifiers changed from: 0000 */
    public boolean isVertical() {
        return this.mDataProvider.isVertical();
    }

    /* access modifiers changed from: 0000 */
    public boolean isUp() {
        return this.mDataProvider.isUp();
    }

    /* access modifiers changed from: 0000 */
    public float getAngle() {
        return this.mDataProvider.getAngle();
    }

    /* access modifiers changed from: 0000 */
    public int getWidthPixels() {
        return this.mDataProvider.getWidthPixels();
    }

    /* access modifiers changed from: 0000 */
    public int getHeightPixels() {
        return this.mDataProvider.getHeightPixels();
    }

    /* access modifiers changed from: 0000 */
    public float getXdpi() {
        return this.mDataProvider.getXdpi();
    }

    /* access modifiers changed from: 0000 */
    public float getYdpi() {
        return this.mDataProvider.getYdpi();
    }

    /* access modifiers changed from: 0000 */
    public final int getInteractionType() {
        return this.mDataProvider.getInteractionType();
    }

    static void logDebug(String str) {
        BrightLineFalsingManager.logDebug(str);
    }

    static void logInfo(String str) {
        BrightLineFalsingManager.logInfo(str);
    }
}
