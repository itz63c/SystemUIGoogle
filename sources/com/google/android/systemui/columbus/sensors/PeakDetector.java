package com.google.android.systemui.columbus.sensors;

public class PeakDetector {
    private float _amplitudeMajorPeak = 0.0f;
    private float _amplitudeReference = 0.0f;
    private int _idMajorPeak = -1;
    private float _minNoiseTolerate = 0.0f;
    private float _noiseTolerate;
    private int _windowSize = 0;

    public void setMinNoiseTolerate(float f) {
        this._minNoiseTolerate = f;
    }

    public void setWindowSize(int i) {
        this._windowSize = i;
    }

    public int getIdMajorPeak() {
        return this._idMajorPeak;
    }

    public void update(float f) {
        int i = this._idMajorPeak - 1;
        this._idMajorPeak = i;
        if (i < 0) {
            this._amplitudeMajorPeak = 0.0f;
        }
        float f2 = this._minNoiseTolerate;
        this._noiseTolerate = f2;
        float f3 = this._amplitudeMajorPeak;
        if (f3 / 5.0f > f2) {
            this._noiseTolerate = f3 / 5.0f;
        }
        float f4 = this._amplitudeReference - f;
        float f5 = this._noiseTolerate;
        if (f4 >= f5) {
            this._amplitudeReference = f;
        } else if (f4 < 0.0f && f > f5) {
            this._amplitudeReference = f;
            if (f > this._amplitudeMajorPeak) {
                this._idMajorPeak = this._windowSize - 1;
                this._amplitudeMajorPeak = f;
            }
        }
    }
}
