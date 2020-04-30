package com.google.android.systemui.columbus.sensors;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

public class EventIMURT {
    protected ArrayList<Float> _fv = new ArrayList<>();
    protected boolean _gotAcc = false;
    protected boolean _gotGyro = false;
    protected Highpass3C _highpassAcc = new Highpass3C();
    protected Highpass3C _highpassGyro = new Highpass3C();
    protected Lowpass3C _lowpassAcc = new Lowpass3C();
    protected Lowpass3C _lowpassGyro = new Lowpass3C();
    protected int _numberFeature;
    protected Resample3C _resampleAcc = new Resample3C();
    protected Resample3C _resampleGyro = new Resample3C();
    protected int _sizeFeatureWindow;
    protected long _sizeWindowNs;
    protected Slope3C _slopeAcc = new Slope3C();
    protected Slope3C _slopeGyro = new Slope3C();
    protected long _syncTime = 0;
    protected Deque<Float> _xsAcc = new ArrayDeque();
    protected Deque<Float> _xsGyro = new ArrayDeque();
    protected Deque<Float> _ysAcc = new ArrayDeque();
    protected Deque<Float> _ysGyro = new ArrayDeque();
    protected Deque<Float> _zsAcc = new ArrayDeque();
    protected Deque<Float> _zsGyro = new ArrayDeque();

    public void reset() {
        this._xsAcc.clear();
        this._ysAcc.clear();
        this._zsAcc.clear();
        this._xsGyro.clear();
        this._ysGyro.clear();
        this._zsGyro.clear();
        this._gotAcc = false;
        this._gotGyro = false;
        this._syncTime = 0;
    }

    public void processGyro() {
        Point3f update = this._highpassGyro.update(this._lowpassGyro.update(this._slopeGyro.update(this._resampleGyro.getResults().point, 2500000.0f / ((float) this._resampleGyro.getInterval()))));
        this._xsGyro.add(Float.valueOf(update.f101x));
        this._ysGyro.add(Float.valueOf(update.f102y));
        this._zsGyro.add(Float.valueOf(update.f103z));
        int interval = (int) (this._sizeWindowNs / this._resampleGyro.getInterval());
        while (this._xsGyro.size() > interval) {
            this._xsGyro.removeFirst();
            this._ysGyro.removeFirst();
            this._zsGyro.removeFirst();
        }
    }

    public ArrayList<Float> scaleGyroData(ArrayList<Float> arrayList, float f) {
        for (int size = arrayList.size() / 2; size < arrayList.size(); size++) {
            arrayList.set(size, Float.valueOf(((Float) arrayList.get(size)).floatValue() * f));
        }
        return arrayList;
    }
}
