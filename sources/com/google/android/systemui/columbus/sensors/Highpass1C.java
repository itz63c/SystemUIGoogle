package com.google.android.systemui.columbus.sensors;

public class Highpass1C {
    private float para = 1.0f;
    private float xLast = 0.0f;
    private float yLast = 0.0f;

    public void setPara(float f) {
        this.para = f;
    }

    public void init(float f) {
        this.xLast = f;
        this.yLast = f;
    }

    public float update(float f) {
        float f2 = this.para;
        float f3 = (this.yLast * f2) + (f2 * (f - this.xLast));
        this.yLast = f3;
        this.xLast = f;
        return f3;
    }
}
