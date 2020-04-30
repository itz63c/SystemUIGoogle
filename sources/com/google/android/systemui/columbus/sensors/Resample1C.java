package com.google.android.systemui.columbus.sensors;

public class Resample1C {
    protected long tInterval = 0;
    protected long tRawLast;
    protected long tResampledLast;
    protected float xRawLast;
    protected float xResampledThis = 0.0f;

    public void init(float f, long j, long j2) {
        this.xRawLast = f;
        this.tRawLast = j;
        this.xResampledThis = f;
        this.tResampledLast = j;
        this.tInterval = j2;
    }

    public long getInterval() {
        return this.tInterval;
    }

    public void setSyncTime(long j) {
        this.tResampledLast = j;
    }
}
