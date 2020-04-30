package com.google.android.systemui.columbus.sensors;

public class Highpass3C {
    private Highpass1C xHighpass = new Highpass1C();
    private Highpass1C yHighpass = new Highpass1C();
    private Highpass1C zHighpass = new Highpass1C();

    public void setPara(float f) {
        this.xHighpass.setPara(f);
        this.yHighpass.setPara(f);
        this.zHighpass.setPara(f);
    }

    public void init(Point3f point3f) {
        this.xHighpass.init(point3f.f101x);
        this.yHighpass.init(point3f.f102y);
        this.zHighpass.init(point3f.f103z);
    }

    public Point3f update(Point3f point3f) {
        return new Point3f(this.xHighpass.update(point3f.f101x), this.yHighpass.update(point3f.f102y), this.zHighpass.update(point3f.f103z));
    }
}
