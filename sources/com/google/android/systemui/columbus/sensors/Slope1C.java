package com.google.android.systemui.columbus.sensors;

public class Slope1C {
    private float xDelta = 0.0f;
    private float xRawLast;

    public void init(float f) {
        this.xRawLast = f;
    }

    public float update(float f, float f2) {
        float f3 = f * f2;
        float f4 = f3 - this.xRawLast;
        this.xDelta = f4;
        this.xRawLast = f3;
        return f4;
    }
}
