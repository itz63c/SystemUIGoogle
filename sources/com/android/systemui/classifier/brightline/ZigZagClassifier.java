package com.android.systemui.classifier.brightline;

import android.graphics.Point;
import android.view.MotionEvent;
import com.android.systemui.util.DeviceConfigProxy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class ZigZagClassifier extends FalsingClassifier {
    private float mLastDevianceX;
    private float mLastDevianceY;
    private float mLastMaxXDeviance;
    private float mLastMaxYDeviance;
    private final float mMaxXPrimaryDeviance;
    private final float mMaxXSecondaryDeviance;
    private final float mMaxYPrimaryDeviance;
    private final float mMaxYSecondaryDeviance;

    ZigZagClassifier(FalsingDataProvider falsingDataProvider, DeviceConfigProxy deviceConfigProxy) {
        super(falsingDataProvider);
        String str = "systemui";
        this.mMaxXPrimaryDeviance = deviceConfigProxy.getFloat(str, "brightline_falsing_zigzag_x_primary_deviance", 0.05f);
        this.mMaxYPrimaryDeviance = deviceConfigProxy.getFloat(str, "brightline_falsing_zigzag_y_primary_deviance", 0.15f);
        this.mMaxXSecondaryDeviance = deviceConfigProxy.getFloat(str, "brightline_falsing_zigzag_x_secondary_deviance", 0.4f);
        this.mMaxYSecondaryDeviance = deviceConfigProxy.getFloat(str, "brightline_falsing_zigzag_y_secondary_deviance", 0.3f);
    }

    /* access modifiers changed from: 0000 */
    public boolean isFalseTouch() {
        List<Point> list;
        float f;
        float f2;
        float f3;
        boolean z = false;
        if (getRecentMotionEvents().size() < 3) {
            return false;
        }
        if (isHorizontal()) {
            list = rotateHorizontal();
        } else {
            list = rotateVertical();
        }
        float abs = (float) Math.abs(((Point) list.get(0)).x - ((Point) list.get(list.size() - 1)).x);
        float abs2 = (float) Math.abs(((Point) list.get(0)).y - ((Point) list.get(list.size() - 1)).y);
        StringBuilder sb = new StringBuilder();
        sb.append("Actual: (");
        sb.append(abs);
        String str = ",";
        sb.append(str);
        sb.append(abs2);
        String str2 = ")";
        sb.append(str2);
        FalsingClassifier.logDebug(sb.toString());
        float f4 = 0.0f;
        boolean z2 = true;
        float f5 = 0.0f;
        float f6 = 0.0f;
        float f7 = 0.0f;
        for (Point point : list) {
            if (z2) {
                f6 = (float) point.x;
                f7 = (float) point.y;
                z2 = false;
            } else {
                f4 += Math.abs(((float) point.x) - f6);
                f5 += Math.abs(((float) point.y) - f7);
                f6 = (float) point.x;
                f7 = (float) point.y;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("(x, y, runningAbsDx, runningAbsDy) - (");
                sb2.append(f6);
                String str3 = ", ";
                sb2.append(str3);
                sb2.append(f7);
                sb2.append(str3);
                sb2.append(f4);
                sb2.append(str3);
                sb2.append(f5);
                sb2.append(str2);
                FalsingClassifier.logDebug(sb2.toString());
            }
        }
        float f8 = f4 - abs;
        float f9 = f5 - abs2;
        float xdpi = abs / getXdpi();
        float ydpi = abs2 / getYdpi();
        float sqrt = (float) Math.sqrt((double) ((xdpi * xdpi) + (ydpi * ydpi)));
        if (abs > abs2) {
            f2 = this.mMaxXPrimaryDeviance * sqrt * getXdpi();
            f = this.mMaxYSecondaryDeviance * sqrt;
            f3 = getYdpi();
        } else {
            f2 = this.mMaxXSecondaryDeviance * sqrt * getXdpi();
            f = this.mMaxYPrimaryDeviance * sqrt;
            f3 = getYdpi();
        }
        float f10 = f * f3;
        this.mLastDevianceX = f8;
        this.mLastDevianceY = f9;
        this.mLastMaxXDeviance = f2;
        this.mLastMaxYDeviance = f10;
        StringBuilder sb3 = new StringBuilder();
        sb3.append("Straightness Deviance: (");
        sb3.append(f8);
        sb3.append(str);
        sb3.append(f9);
        sb3.append(") vs (");
        sb3.append(f2);
        sb3.append(str);
        sb3.append(f10);
        sb3.append(str2);
        FalsingClassifier.logDebug(sb3.toString());
        if (f8 > f2 || f9 > f10) {
            z = true;
        }
        return z;
    }

    /* access modifiers changed from: 0000 */
    public String getReason() {
        return String.format(null, "{devianceX=%f, maxDevianceX=%s, devianceY=%s, maxDevianceY=%s}", new Object[]{Float.valueOf(this.mLastDevianceX), Float.valueOf(this.mLastMaxXDeviance), Float.valueOf(this.mLastDevianceY), Float.valueOf(this.mLastMaxYDeviance)});
    }

    private float getAtan2LastPoint() {
        MotionEvent firstMotionEvent = getFirstMotionEvent();
        MotionEvent lastMotionEvent = getLastMotionEvent();
        float x = firstMotionEvent.getX();
        return (float) Math.atan2((double) (lastMotionEvent.getY() - firstMotionEvent.getY()), (double) (lastMotionEvent.getX() - x));
    }

    private List<Point> rotateVertical() {
        double atan2LastPoint = 1.5707963267948966d - ((double) getAtan2LastPoint());
        StringBuilder sb = new StringBuilder();
        sb.append("Rotating to vertical by: ");
        sb.append(atan2LastPoint);
        FalsingClassifier.logDebug(sb.toString());
        return rotateMotionEvents(getRecentMotionEvents(), -atan2LastPoint);
    }

    private List<Point> rotateHorizontal() {
        double atan2LastPoint = (double) getAtan2LastPoint();
        StringBuilder sb = new StringBuilder();
        sb.append("Rotating to horizontal by: ");
        sb.append(atan2LastPoint);
        FalsingClassifier.logDebug(sb.toString());
        return rotateMotionEvents(getRecentMotionEvents(), atan2LastPoint);
    }

    private List<Point> rotateMotionEvents(List<MotionEvent> list, double d) {
        List<MotionEvent> list2 = list;
        ArrayList arrayList = new ArrayList();
        double cos = Math.cos(d);
        double sin = Math.sin(d);
        MotionEvent motionEvent = (MotionEvent) list2.get(0);
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            MotionEvent motionEvent2 = (MotionEvent) it.next();
            double x2 = (double) (motionEvent2.getX() - x);
            MotionEvent motionEvent3 = motionEvent;
            double y2 = (double) (motionEvent2.getY() - y);
            Iterator it2 = it;
            arrayList.add(new Point((int) ((cos * x2) + (sin * y2) + ((double) x)), (int) (((-sin) * x2) + (y2 * cos) + ((double) y))));
            motionEvent = motionEvent3;
            it = it2;
        }
        MotionEvent motionEvent4 = motionEvent;
        MotionEvent motionEvent5 = (MotionEvent) list2.get(list.size() - 1);
        Point point = (Point) arrayList.get(0);
        Point point2 = (Point) arrayList.get(arrayList.size() - 1);
        StringBuilder sb = new StringBuilder();
        sb.append("Before: (");
        sb.append(motionEvent4.getX());
        String str = ",";
        sb.append(str);
        sb.append(motionEvent4.getY());
        String str2 = "), (";
        sb.append(str2);
        sb.append(motionEvent5.getX());
        sb.append(str);
        sb.append(motionEvent5.getY());
        String str3 = ")";
        sb.append(str3);
        FalsingClassifier.logDebug(sb.toString());
        StringBuilder sb2 = new StringBuilder();
        sb2.append("After: (");
        sb2.append(point.x);
        sb2.append(str);
        sb2.append(point.y);
        sb2.append(str2);
        sb2.append(point2.x);
        sb2.append(str);
        sb2.append(point2.y);
        sb2.append(str3);
        FalsingClassifier.logDebug(sb2.toString());
        return arrayList;
    }
}
