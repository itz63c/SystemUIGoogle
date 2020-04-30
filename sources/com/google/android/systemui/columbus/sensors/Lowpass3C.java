package com.google.android.systemui.columbus.sensors;

public class Lowpass3C extends Lowpass1C {
    private Lowpass1C xLowpass = new Lowpass1C();
    private Lowpass1C yLowpass = new Lowpass1C();
    private Lowpass1C zLowpass = new Lowpass1C();

    public void setPara(float f) {
        this.xLowpass.setPara(f);
        this.yLowpass.setPara(f);
        this.zLowpass.setPara(f);
    }

    public void init(Point3f point3f) {
        this.xLowpass.init(point3f.f101x);
        this.yLowpass.init(point3f.f102y);
        this.zLowpass.init(point3f.f103z);
    }

    public Point3f update(Point3f point3f) {
        return new Point3f(this.xLowpass.update(point3f.f101x), this.yLowpass.update(point3f.f102y), this.zLowpass.update(point3f.f103z));
    }
}
