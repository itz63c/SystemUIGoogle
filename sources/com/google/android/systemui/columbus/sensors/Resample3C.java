package com.google.android.systemui.columbus.sensors;

public class Resample3C extends Resample1C {
    private float yRawLast;
    private float yResampledThis;
    private float zRawLast;
    private float zResampledThis;

    public void init(float f, float f2, float f3, long j, long j2) {
        init(f, j, j2);
        this.yRawLast = f2;
        this.zRawLast = f3;
        this.yResampledThis = f2;
        this.zResampledThis = f3;
    }

    public boolean update(float f, float f2, float f3, long j) {
        long j2 = this.tRawLast;
        if (j == j2) {
            return false;
        }
        long j3 = this.tInterval;
        if (j3 <= 0) {
            j3 = j - j2;
        }
        long j4 = this.tResampledLast + j3;
        if (j < j4) {
            this.tRawLast = j;
            this.xRawLast = f;
            this.yRawLast = f2;
            this.zRawLast = f3;
            return false;
        }
        long j5 = this.tRawLast;
        float f4 = ((float) (j4 - j5)) / ((float) (j - j5));
        float f5 = this.xRawLast;
        this.xResampledThis = ((f - f5) * f4) + f5;
        float f6 = this.yRawLast;
        this.yResampledThis = ((f2 - f6) * f4) + f6;
        float f7 = this.zRawLast;
        this.zResampledThis = ((f3 - f7) * f4) + f7;
        this.tResampledLast = j4;
        if (j5 < j4) {
            this.tRawLast = j;
            this.xRawLast = f;
            this.yRawLast = f2;
            this.zRawLast = f3;
        }
        return true;
    }

    public Sample3C getResults() {
        Sample3C sample3C = new Sample3C(this.xResampledThis, this.yResampledThis, this.zResampledThis, this.tResampledLast);
        return sample3C;
    }
}
