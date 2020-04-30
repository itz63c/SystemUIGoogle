package com.google.android.systemui.columbus.sensors;

public class Lowpass1C {
    private float para = 1.0f;
    private float xLast = 0.0f;

    public void setPara(float f) {
        this.para = f;
    }

    public void init(float f) {
        this.xLast = f;
    }

    public float update(float f) {
        float f2 = this.para;
        float f3 = ((1.0f - f2) * this.xLast) + (f2 * f);
        this.xLast = f3;
        return f3;
    }
}
