package com.google.android.systemui.columbus.sensors;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;

public class TapRT extends EventIMURT {
    private Highpass1C _highpassKey = new Highpass1C();
    private Lowpass1C _lowpassKey = new Lowpass1C();
    private PeakDetector _peakDetectorNegative = new PeakDetector();
    private PeakDetector _peakDetectorPositive = new PeakDetector();
    private int _result;
    private Deque<Long> _tBackTapTimestamps = new ArrayDeque();
    private TfClassifier _tflite;
    private boolean _wasPeakApproaching = true;

    public enum TapClass {
        Front,
        Back,
        Left,
        Right,
        Top,
        Bottom,
        Others
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x004f  */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0057  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public TapRT(long r4, android.content.res.AssetManager r6, java.lang.String r7) {
        /*
            r3 = this;
            r3.<init>()
            com.google.android.systemui.columbus.sensors.Lowpass1C r0 = new com.google.android.systemui.columbus.sensors.Lowpass1C
            r0.<init>()
            r3._lowpassKey = r0
            com.google.android.systemui.columbus.sensors.Highpass1C r0 = new com.google.android.systemui.columbus.sensors.Highpass1C
            r0.<init>()
            r3._highpassKey = r0
            com.google.android.systemui.columbus.sensors.PeakDetector r0 = new com.google.android.systemui.columbus.sensors.PeakDetector
            r0.<init>()
            r3._peakDetectorPositive = r0
            com.google.android.systemui.columbus.sensors.PeakDetector r0 = new com.google.android.systemui.columbus.sensors.PeakDetector
            r0.<init>()
            r3._peakDetectorNegative = r0
            java.util.ArrayDeque r0 = new java.util.ArrayDeque
            r0.<init>()
            r3._tBackTapTimestamps = r0
            r0 = 1
            r3._wasPeakApproaching = r0
            int r1 = r7.hashCode()
            r2 = 1905086331(0x718d4f7b, float:1.3994711E30)
            if (r1 == r2) goto L_0x0042
            r2 = 1905116122(0x718dc3da, float:1.403973E30)
            if (r1 == r2) goto L_0x0038
            goto L_0x004c
        L_0x0038:
            java.lang.String r1 = "Pixel 4 XL"
            boolean r7 = r7.equals(r1)
            if (r7 == 0) goto L_0x004c
            r7 = r0
            goto L_0x004d
        L_0x0042:
            java.lang.String r1 = "Pixel 3 XL"
            boolean r7 = r7.equals(r1)
            if (r7 == 0) goto L_0x004c
            r7 = 0
            goto L_0x004d
        L_0x004c:
            r7 = -1
        L_0x004d:
            if (r7 == 0) goto L_0x0057
            if (r7 == r0) goto L_0x0054
            java.lang.String r7 = "tap7cls_pixel4.tflite"
            goto L_0x0059
        L_0x0054:
            java.lang.String r7 = "tap7cls_pixel4xl.tflite"
            goto L_0x0059
        L_0x0057:
            java.lang.String r7 = "tap7cls_pixel3xl.tflite"
        L_0x0059:
            com.google.android.systemui.columbus.sensors.TfClassifier r0 = new com.google.android.systemui.columbus.sensors.TfClassifier
            r0.<init>(r6, r7)
            r3._tflite = r0
            r3._sizeWindowNs = r4
            r4 = 50
            r3._sizeFeatureWindow = r4
            int r4 = r4 * 6
            r3._numberFeature = r4
            com.google.android.systemui.columbus.sensors.Lowpass3C r4 = r3._lowpassAcc
            r5 = 1065353216(0x3f800000, float:1.0)
            r4.setPara(r5)
            com.google.android.systemui.columbus.sensors.Lowpass3C r4 = r3._lowpassGyro
            r4.setPara(r5)
            com.google.android.systemui.columbus.sensors.Highpass3C r4 = r3._highpassAcc
            r5 = 1028443341(0x3d4ccccd, float:0.05)
            r4.setPara(r5)
            com.google.android.systemui.columbus.sensors.Highpass3C r4 = r3._highpassGyro
            r4.setPara(r5)
            com.google.android.systemui.columbus.sensors.Lowpass1C r4 = r3._lowpassKey
            r5 = 1045220557(0x3e4ccccd, float:0.2)
            r4.setPara(r5)
            com.google.android.systemui.columbus.sensors.Highpass1C r3 = r3._highpassKey
            r3.setPara(r5)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.systemui.columbus.sensors.TapRT.<init>(long, android.content.res.AssetManager, java.lang.String):void");
    }

    public Lowpass1C getLowpassKey() {
        return this._lowpassKey;
    }

    public Highpass1C getHighpassKey() {
        return this._highpassKey;
    }

    public PeakDetector getPositivePeakDetector() {
        return this._peakDetectorPositive;
    }

    public PeakDetector getNegativePeakDetection() {
        return this._peakDetectorNegative;
    }

    public void reset(boolean z) {
        super.reset();
        if (!z) {
            this._fv = new ArrayList<>(this._numberFeature);
            for (int i = 0; i < this._numberFeature; i++) {
                this._fv.add(Float.valueOf(0.0f));
            }
            return;
        }
        this._fv.clear();
    }

    public void updateData(int i, float f, float f2, float f3, long j, long j2, boolean z) {
        this._result = TapClass.Others.ordinal();
        if (z) {
            updateHeuristic(i, f, f2, f3, j, j2);
        } else {
            updateML(i, f, f2, f3, j, j2);
        }
    }

    public void updateHeuristic(int i, float f, float f2, float f3, long j, long j2) {
        long j3 = j;
        if (i != 4) {
            if (0 == this._syncTime) {
                this._syncTime = j3;
                this._resampleAcc.init(f, f2, f3, j, j2);
                this._resampleAcc.setSyncTime(this._syncTime);
                this._slopeAcc.init(this._resampleAcc.getResults().point);
                this._lowpassKey.init(0.0f);
                this._highpassKey.init(0.0f);
                return;
            }
            while (this._resampleAcc.update(f, f2, f3, j)) {
                processKeySignalHeursitic(j);
            }
        }
    }

    public void processKeySignalHeursitic(long j) {
        float update = this._highpassKey.update(this._lowpassKey.update(this._slopeAcc.update(this._resampleAcc.getResults().point, 2500000.0f / ((float) this._resampleAcc.getInterval())).f103z));
        this._peakDetectorPositive.update(update);
        this._peakDetectorNegative.update(-update);
        this._zsAcc.add(Float.valueOf(update));
        int interval = (int) (this._sizeWindowNs / this._resampleAcc.getInterval());
        while (this._zsAcc.size() > interval) {
            this._zsAcc.removeFirst();
        }
        if (this._zsAcc.size() == interval) {
            recognizeTapHeursitic();
        }
        if (this._result == TapClass.Back.ordinal()) {
            this._tBackTapTimestamps.addLast(Long.valueOf(j));
        }
    }

    public void recognizeTapHeursitic() {
        int idMajorPeak = this._peakDetectorPositive.getIdMajorPeak();
        int idMajorPeak2 = this._peakDetectorNegative.getIdMajorPeak() - idMajorPeak;
        if (idMajorPeak == 4) {
            this._fv = new ArrayList<>(this._zsAcc);
            if (idMajorPeak2 <= 0 || idMajorPeak2 >= 3) {
                this._result = TapClass.Others.ordinal();
            } else {
                this._result = TapClass.Back.ordinal();
            }
        }
    }

    public void updateML(int i, float f, float f2, float f3, long j, long j2) {
        int i2 = i;
        long j3 = j;
        if (i2 == 1) {
            this._gotAcc = true;
            if (0 == this._syncTime) {
                this._resampleAcc.init(f, f2, f3, j, j2);
            }
            if (!this._gotGyro) {
                return;
            }
        } else if (i2 == 4) {
            this._gotGyro = true;
            if (0 == this._syncTime) {
                this._resampleGyro.init(f, f2, f3, j, j2);
            }
            if (!this._gotAcc) {
                return;
            }
        }
        if (0 == this._syncTime) {
            this._syncTime = j3;
            this._resampleAcc.setSyncTime(j3);
            this._resampleGyro.setSyncTime(this._syncTime);
            this._slopeAcc.init(this._resampleAcc.getResults().point);
            this._slopeGyro.init(this._resampleGyro.getResults().point);
            this._lowpassAcc.init(new Point3f(0.0f, 0.0f, 0.0f));
            this._lowpassGyro.init(new Point3f(0.0f, 0.0f, 0.0f));
            this._highpassAcc.init(new Point3f(0.0f, 0.0f, 0.0f));
            this._highpassGyro.init(new Point3f(0.0f, 0.0f, 0.0f));
            this._lowpassKey.init(0.0f);
            this._highpassKey.init(0.0f);
            return;
        }
        if (i2 == 1) {
            while (this._resampleAcc.update(f, f2, f3, j)) {
                processAccAndKeySignal();
            }
        } else if (i2 == 4) {
            while (this._resampleGyro.update(f, f2, f3, j)) {
                processGyro();
            }
        }
        recognizeTapML();
        if (this._result == TapClass.Back.ordinal()) {
            this._tBackTapTimestamps.addLast(Long.valueOf(j));
        }
    }

    public void processAccAndKeySignal() {
        Point3f update = this._slopeAcc.update(this._resampleAcc.getResults().point, 2500000.0f / ((float) this._resampleAcc.getInterval()));
        Point3f update2 = this._highpassAcc.update(this._lowpassAcc.update(update));
        this._xsAcc.add(Float.valueOf(update2.f101x));
        this._ysAcc.add(Float.valueOf(update2.f102y));
        this._zsAcc.add(Float.valueOf(update2.f103z));
        int interval = (int) (this._sizeWindowNs / this._resampleAcc.getInterval());
        while (this._xsAcc.size() > interval) {
            this._xsAcc.removeFirst();
            this._ysAcc.removeFirst();
            this._zsAcc.removeFirst();
        }
        this._peakDetectorPositive.update(this._highpassKey.update(this._lowpassKey.update(update.f103z)));
    }

    public void recognizeTapML() {
        int interval = (int) ((this._resampleAcc.getResults().f104t - this._resampleGyro.getResults().f104t) / this._resampleAcc.getInterval());
        int idMajorPeak = this._peakDetectorPositive.getIdMajorPeak();
        if (idMajorPeak > 12) {
            this._wasPeakApproaching = true;
        }
        int i = idMajorPeak - 6;
        int i2 = i - interval;
        int size = this._zsAcc.size();
        if (i >= 0 && i2 >= 0) {
            int i3 = this._sizeFeatureWindow;
            if (i + i3 < size && i3 + i2 < size && this._wasPeakApproaching && idMajorPeak <= 12) {
                this._wasPeakApproaching = false;
                addToFeatureVector(this._xsAcc, i, 0);
                addToFeatureVector(this._ysAcc, i, this._sizeFeatureWindow);
                addToFeatureVector(this._zsAcc, i, this._sizeFeatureWindow * 2);
                addToFeatureVector(this._xsGyro, i2, this._sizeFeatureWindow * 3);
                addToFeatureVector(this._ysGyro, i2, this._sizeFeatureWindow * 4);
                addToFeatureVector(this._zsGyro, i2, this._sizeFeatureWindow * 5);
                ArrayList<Float> arrayList = this._fv;
                scaleGyroData(arrayList, 10.0f);
                this._fv = arrayList;
                this._result = Util.getMaxId((ArrayList) this._tflite.predict(arrayList, 7).get(0));
            }
        }
    }

    private void addToFeatureVector(Deque<Float> deque, int i, int i2) {
        Iterator it = deque.iterator();
        int i3 = 0;
        while (it.hasNext()) {
            if (i3 < i) {
                it.next();
            } else if (i3 < this._sizeFeatureWindow + i) {
                this._fv.set(i2, (Float) it.next());
                i2++;
            } else {
                return;
            }
            i3++;
        }
    }

    public int checkDoubleTapTiming(long j) {
        Iterator it = this._tBackTapTimestamps.iterator();
        while (it.hasNext()) {
            if (j - ((Long) it.next()).longValue() > 500000000) {
                it.remove();
            }
        }
        if (this._tBackTapTimestamps.isEmpty()) {
            return 0;
        }
        for (Long longValue : this._tBackTapTimestamps) {
            if (((Long) this._tBackTapTimestamps.getLast()).longValue() - longValue.longValue() > 100000000) {
                this._tBackTapTimestamps.clear();
                return 2;
            }
        }
        return 1;
    }
}
