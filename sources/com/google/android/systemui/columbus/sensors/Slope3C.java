package com.google.android.systemui.columbus.sensors;

public class Slope3C {
    private Slope1C _slopeX = new Slope1C();
    private Slope1C _slopeY = new Slope1C();
    private Slope1C _slopeZ = new Slope1C();

    public void init(Point3f point3f) {
        this._slopeX.init(point3f.f101x);
        this._slopeY.init(point3f.f102y);
        this._slopeZ.init(point3f.f103z);
    }

    public Point3f update(Point3f point3f, float f) {
        return new Point3f(this._slopeX.update(point3f.f101x, f), this._slopeY.update(point3f.f102y, f), this._slopeZ.update(point3f.f103z, f));
    }
}
