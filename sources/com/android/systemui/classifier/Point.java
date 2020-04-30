package com.android.systemui.classifier;

public class Point {
    public long timeOffsetNano;

    /* renamed from: x */
    public float f35x;

    /* renamed from: y */
    public float f36y;

    public Point(float f, float f2) {
        this.f35x = f;
        this.f36y = f2;
        this.timeOffsetNano = 0;
    }

    public Point(float f, float f2, long j) {
        this.f35x = f;
        this.f36y = f2;
        this.timeOffsetNano = j;
    }

    public boolean equals(Point point) {
        return this.f35x == point.f35x && this.f36y == point.f36y;
    }

    public float dist(Point point) {
        return (float) Math.hypot((double) (point.f35x - this.f35x), (double) (point.f36y - this.f36y));
    }

    public float crossProduct(Point point, Point point2) {
        float f = point.f35x;
        float f2 = this.f35x;
        float f3 = f - f2;
        float f4 = point2.f36y;
        float f5 = this.f36y;
        return (f3 * (f4 - f5)) - ((point.f36y - f5) * (point2.f35x - f2));
    }

    public float dotProduct(Point point, Point point2) {
        float f = point.f35x;
        float f2 = this.f35x;
        float f3 = (f - f2) * (point2.f35x - f2);
        float f4 = point.f36y;
        float f5 = this.f36y;
        return f3 + ((f4 - f5) * (point2.f36y - f5));
    }

    public float getAngle(Point point, Point point2) {
        float dist = dist(point);
        float dist2 = dist(point2);
        if (dist == 0.0f || dist2 == 0.0f) {
            return 0.0f;
        }
        float crossProduct = crossProduct(point, point2);
        float acos = (float) Math.acos((double) Math.min(1.0f, Math.max(-1.0f, (dotProduct(point, point2) / dist) / dist2)));
        if (((double) crossProduct) < 0.0d) {
            acos = 6.2831855f - acos;
        }
        return acos;
    }
}
