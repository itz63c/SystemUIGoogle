package com.android.systemui.analytics;

import android.os.Build;
import android.view.MotionEvent;
import com.android.systemui.statusbar.phone.nano.TouchAnalyticsProto$Session;
import com.android.systemui.statusbar.phone.nano.TouchAnalyticsProto$Session.PhoneEvent;
import com.android.systemui.statusbar.phone.nano.TouchAnalyticsProto$Session.SensorEvent;
import com.android.systemui.statusbar.phone.nano.TouchAnalyticsProto$Session.TouchEvent;
import com.android.systemui.statusbar.phone.nano.TouchAnalyticsProto.Session.TouchEvent.Pointer;
import java.util.ArrayList;

public class SensorLoggerSession {
    private long mEndTimestampMillis;
    private ArrayList<TouchEvent> mMotionEvents = new ArrayList<>();
    private ArrayList<PhoneEvent> mPhoneEvents = new ArrayList<>();
    private int mResult = 2;
    private ArrayList<SensorEvent> mSensorEvents = new ArrayList<>();
    private final long mStartSystemTimeNanos;
    private final long mStartTimestampMillis;
    private int mTouchAreaHeight;
    private int mTouchAreaWidth;
    private int mType;

    public SensorLoggerSession(long j, long j2) {
        this.mStartTimestampMillis = j;
        this.mStartSystemTimeNanos = j2;
        this.mType = 3;
    }

    public void setType(int i) {
        this.mType = i;
    }

    public void end(long j, int i) {
        this.mResult = i;
        this.mEndTimestampMillis = j;
    }

    public void addMotionEvent(MotionEvent motionEvent) {
        this.mMotionEvents.add(motionEventToProto(motionEvent));
    }

    public void addSensorEvent(android.hardware.SensorEvent sensorEvent, long j) {
        this.mSensorEvents.add(sensorEventToProto(sensorEvent, j));
    }

    public void addPhoneEvent(int i, long j) {
        this.mPhoneEvents.add(phoneEventToProto(i, j));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("Session{");
        sb.append("mStartTimestampMillis=");
        sb.append(this.mStartTimestampMillis);
        sb.append(", mStartSystemTimeNanos=");
        sb.append(this.mStartSystemTimeNanos);
        sb.append(", mEndTimestampMillis=");
        sb.append(this.mEndTimestampMillis);
        sb.append(", mResult=");
        sb.append(this.mResult);
        sb.append(", mTouchAreaHeight=");
        sb.append(this.mTouchAreaHeight);
        sb.append(", mTouchAreaWidth=");
        sb.append(this.mTouchAreaWidth);
        sb.append(", mMotionEvents=[size=");
        sb.append(this.mMotionEvents.size());
        String str = "]";
        sb.append(str);
        sb.append(", mSensorEvents=[size=");
        sb.append(this.mSensorEvents.size());
        sb.append(str);
        sb.append(", mPhoneEvents=[size=");
        sb.append(this.mPhoneEvents.size());
        sb.append(str);
        sb.append('}');
        return sb.toString();
    }

    public TouchAnalyticsProto$Session toProto() {
        TouchAnalyticsProto$Session touchAnalyticsProto$Session = new TouchAnalyticsProto$Session();
        long j = this.mStartTimestampMillis;
        touchAnalyticsProto$Session.startTimestampMillis = j;
        touchAnalyticsProto$Session.durationMillis = this.mEndTimestampMillis - j;
        touchAnalyticsProto$Session.build = Build.FINGERPRINT;
        touchAnalyticsProto$Session.deviceId = Build.DEVICE;
        touchAnalyticsProto$Session.result = this.mResult;
        touchAnalyticsProto$Session.type = this.mType;
        touchAnalyticsProto$Session.sensorEvents = (SensorEvent[]) this.mSensorEvents.toArray(touchAnalyticsProto$Session.sensorEvents);
        touchAnalyticsProto$Session.touchEvents = (TouchEvent[]) this.mMotionEvents.toArray(touchAnalyticsProto$Session.touchEvents);
        touchAnalyticsProto$Session.phoneEvents = (PhoneEvent[]) this.mPhoneEvents.toArray(touchAnalyticsProto$Session.phoneEvents);
        touchAnalyticsProto$Session.touchAreaWidth = this.mTouchAreaWidth;
        touchAnalyticsProto$Session.touchAreaHeight = this.mTouchAreaHeight;
        return touchAnalyticsProto$Session;
    }

    private PhoneEvent phoneEventToProto(int i, long j) {
        PhoneEvent phoneEvent = new PhoneEvent();
        phoneEvent.type = i;
        phoneEvent.timeOffsetNanos = j - this.mStartSystemTimeNanos;
        return phoneEvent;
    }

    private SensorEvent sensorEventToProto(android.hardware.SensorEvent sensorEvent, long j) {
        SensorEvent sensorEvent2 = new SensorEvent();
        sensorEvent2.type = sensorEvent.sensor.getType();
        sensorEvent2.timeOffsetNanos = j - this.mStartSystemTimeNanos;
        sensorEvent2.timestamp = sensorEvent.timestamp;
        sensorEvent2.values = (float[]) sensorEvent.values.clone();
        return sensorEvent2;
    }

    private TouchEvent motionEventToProto(MotionEvent motionEvent) {
        int pointerCount = motionEvent.getPointerCount();
        TouchEvent touchEvent = new TouchEvent();
        touchEvent.timeOffsetNanos = motionEvent.getEventTimeNano() - this.mStartSystemTimeNanos;
        touchEvent.action = motionEvent.getActionMasked();
        touchEvent.actionIndex = motionEvent.getActionIndex();
        touchEvent.pointers = new Pointer[pointerCount];
        for (int i = 0; i < pointerCount; i++) {
            Pointer pointer = new Pointer();
            pointer.f77x = motionEvent.getX(i);
            pointer.f78y = motionEvent.getY(i);
            pointer.size = motionEvent.getSize(i);
            pointer.pressure = motionEvent.getPressure(i);
            pointer.f76id = motionEvent.getPointerId(i);
            touchEvent.pointers[i] = pointer;
        }
        return touchEvent;
    }

    public void setTouchArea(int i, int i2) {
        this.mTouchAreaWidth = i;
        this.mTouchAreaHeight = i2;
    }

    public int getResult() {
        return this.mResult;
    }
}
