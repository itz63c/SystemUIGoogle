package com.android.systemui.classifier.brightline;

import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.MotionEvent.PointerProperties;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FalsingDataProvider {
    private float mAngle = 0.0f;
    private boolean mDirty = true;
    private MotionEvent mFirstRecentMotionEvent;
    private final int mHeightPixels;
    private int mInteractionType;
    private MotionEvent mLastMotionEvent;
    private final TimeLimitedMotionEventBuffer mRecentMotionEvents = new TimeLimitedMotionEventBuffer(1000);
    private final int mWidthPixels;
    private final float mXdpi;
    private final float mYdpi;

    public FalsingDataProvider(DisplayMetrics displayMetrics) {
        this.mXdpi = displayMetrics.xdpi;
        this.mYdpi = displayMetrics.ydpi;
        this.mWidthPixels = displayMetrics.widthPixels;
        this.mHeightPixels = displayMetrics.heightPixels;
        StringBuilder sb = new StringBuilder();
        sb.append("xdpi, ydpi: ");
        sb.append(getXdpi());
        String str = ", ";
        sb.append(str);
        sb.append(getYdpi());
        FalsingClassifier.logInfo(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("width, height: ");
        sb2.append(getWidthPixels());
        sb2.append(str);
        sb2.append(getHeightPixels());
        FalsingClassifier.logInfo(sb2.toString());
    }

    /* access modifiers changed from: 0000 */
    public void onMotionEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        List<MotionEvent> unpackMotionEvent = unpackMotionEvent(motionEvent);
        StringBuilder sb = new StringBuilder();
        sb.append("Unpacked into: ");
        sb.append(unpackMotionEvent.size());
        FalsingClassifier.logDebug(sb.toString());
        if (BrightLineFalsingManager.DEBUG) {
            for (MotionEvent motionEvent2 : unpackMotionEvent) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("x,y,t: ");
                sb2.append(motionEvent2.getX());
                String str = ",";
                sb2.append(str);
                sb2.append(motionEvent2.getY());
                sb2.append(str);
                sb2.append(motionEvent2.getEventTime());
                FalsingClassifier.logDebug(sb2.toString());
            }
        }
        if (motionEvent.getActionMasked() == 0) {
            this.mRecentMotionEvents.clear();
        }
        this.mRecentMotionEvents.addAll(unpackMotionEvent);
        StringBuilder sb3 = new StringBuilder();
        sb3.append("Size: ");
        sb3.append(this.mRecentMotionEvents.size());
        FalsingClassifier.logDebug(sb3.toString());
        this.mDirty = true;
    }

    /* access modifiers changed from: 0000 */
    public int getWidthPixels() {
        return this.mWidthPixels;
    }

    /* access modifiers changed from: 0000 */
    public int getHeightPixels() {
        return this.mHeightPixels;
    }

    /* access modifiers changed from: 0000 */
    public float getXdpi() {
        return this.mXdpi;
    }

    /* access modifiers changed from: 0000 */
    public float getYdpi() {
        return this.mYdpi;
    }

    /* access modifiers changed from: 0000 */
    public List<MotionEvent> getRecentMotionEvents() {
        return this.mRecentMotionEvents;
    }

    /* access modifiers changed from: 0000 */
    public final void setInteractionType(int i) {
        this.mInteractionType = i;
    }

    public boolean isDirty() {
        return this.mDirty;
    }

    /* access modifiers changed from: 0000 */
    public final int getInteractionType() {
        return this.mInteractionType;
    }

    /* access modifiers changed from: 0000 */
    public MotionEvent getFirstRecentMotionEvent() {
        recalculateData();
        return this.mFirstRecentMotionEvent;
    }

    /* access modifiers changed from: 0000 */
    public MotionEvent getLastMotionEvent() {
        recalculateData();
        return this.mLastMotionEvent;
    }

    /* access modifiers changed from: 0000 */
    public float getAngle() {
        recalculateData();
        return this.mAngle;
    }

    /* access modifiers changed from: 0000 */
    public boolean isHorizontal() {
        recalculateData();
        boolean z = false;
        if (this.mRecentMotionEvents.isEmpty()) {
            return false;
        }
        if (Math.abs(this.mFirstRecentMotionEvent.getX() - this.mLastMotionEvent.getX()) > Math.abs(this.mFirstRecentMotionEvent.getY() - this.mLastMotionEvent.getY())) {
            z = true;
        }
        return z;
    }

    /* access modifiers changed from: 0000 */
    public boolean isRight() {
        recalculateData();
        boolean z = false;
        if (this.mRecentMotionEvents.isEmpty()) {
            return false;
        }
        if (this.mLastMotionEvent.getX() > this.mFirstRecentMotionEvent.getX()) {
            z = true;
        }
        return z;
    }

    /* access modifiers changed from: 0000 */
    public boolean isVertical() {
        return !isHorizontal();
    }

    /* access modifiers changed from: 0000 */
    public boolean isUp() {
        recalculateData();
        boolean z = false;
        if (this.mRecentMotionEvents.isEmpty()) {
            return false;
        }
        if (this.mLastMotionEvent.getY() < this.mFirstRecentMotionEvent.getY()) {
            z = true;
        }
        return z;
    }

    private void recalculateData() {
        if (this.mDirty) {
            if (this.mRecentMotionEvents.isEmpty()) {
                this.mFirstRecentMotionEvent = null;
                this.mLastMotionEvent = null;
            } else {
                this.mFirstRecentMotionEvent = this.mRecentMotionEvents.get(0);
                TimeLimitedMotionEventBuffer timeLimitedMotionEventBuffer = this.mRecentMotionEvents;
                this.mLastMotionEvent = timeLimitedMotionEventBuffer.get(timeLimitedMotionEventBuffer.size() - 1);
            }
            calculateAngleInternal();
            this.mDirty = false;
        }
    }

    private void calculateAngleInternal() {
        if (this.mRecentMotionEvents.size() < 2) {
            this.mAngle = Float.MAX_VALUE;
            return;
        }
        this.mAngle = (float) Math.atan2((double) (this.mLastMotionEvent.getY() - this.mFirstRecentMotionEvent.getY()), (double) (this.mLastMotionEvent.getX() - this.mFirstRecentMotionEvent.getX()));
        while (true) {
            float f = this.mAngle;
            if (f >= 0.0f) {
                break;
            }
            this.mAngle = f + 6.2831855f;
        }
        while (true) {
            float f2 = this.mAngle;
            if (f2 > 6.2831855f) {
                this.mAngle = f2 - 6.2831855f;
            } else {
                return;
            }
        }
    }

    private List<MotionEvent> unpackMotionEvent(MotionEvent motionEvent) {
        MotionEvent motionEvent2 = motionEvent;
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        int pointerCount = motionEvent.getPointerCount();
        int i = 0;
        for (int i2 = 0; i2 < pointerCount; i2++) {
            PointerProperties pointerProperties = new PointerProperties();
            motionEvent2.getPointerProperties(i2, pointerProperties);
            arrayList2.add(pointerProperties);
        }
        PointerProperties[] pointerPropertiesArr = new PointerProperties[arrayList2.size()];
        arrayList2.toArray(pointerPropertiesArr);
        int historySize = motionEvent.getHistorySize();
        int i3 = 0;
        while (i3 < historySize) {
            ArrayList arrayList3 = new ArrayList();
            for (int i4 = i; i4 < pointerCount; i4++) {
                PointerCoords pointerCoords = new PointerCoords();
                motionEvent2.getHistoricalPointerCoords(i4, i3, pointerCoords);
                arrayList3.add(pointerCoords);
            }
            int i5 = i3;
            PointerProperties[] pointerPropertiesArr2 = pointerPropertiesArr;
            int i6 = i;
            int i7 = pointerCount;
            arrayList.add(MotionEvent.obtain(motionEvent.getDownTime(), motionEvent2.getHistoricalEventTime(i3), motionEvent.getAction(), pointerCount, pointerPropertiesArr, (PointerCoords[]) arrayList3.toArray(new PointerCoords[i]), motionEvent.getMetaState(), motionEvent.getButtonState(), motionEvent.getXPrecision(), motionEvent.getYPrecision(), motionEvent.getDeviceId(), motionEvent.getEdgeFlags(), motionEvent.getSource(), motionEvent.getFlags()));
            i3 = i5 + 1;
            pointerPropertiesArr = pointerPropertiesArr2;
            i = i6;
            pointerCount = i7;
        }
        arrayList.add(MotionEvent.obtainNoHistory(motionEvent));
        return arrayList;
    }

    /* access modifiers changed from: 0000 */
    public void onSessionEnd() {
        Iterator it = this.mRecentMotionEvents.iterator();
        while (it.hasNext()) {
            ((MotionEvent) it.next()).recycle();
        }
        this.mRecentMotionEvents.clear();
        this.mDirty = true;
    }
}
