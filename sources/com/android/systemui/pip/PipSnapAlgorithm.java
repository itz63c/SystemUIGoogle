package com.android.systemui.pip;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Size;
import java.io.PrintWriter;

public class PipSnapAlgorithm {
    private final Context mContext;
    private final float mDefaultSizePercent;
    private final float mMaxAspectRatioForMinSize;
    private final float mMinAspectRatioForMinSize;
    private int mOrientation = 0;

    public PipSnapAlgorithm(Context context) {
        Resources resources = context.getResources();
        this.mContext = context;
        this.mDefaultSizePercent = resources.getFloat(17105072);
        float f = resources.getFloat(17105070);
        this.mMaxAspectRatioForMinSize = f;
        this.mMinAspectRatioForMinSize = 1.0f / f;
        onConfigurationChanged();
    }

    public void onConfigurationChanged() {
        this.mOrientation = this.mContext.getResources().getConfiguration().orientation;
    }

    public float getSnapFraction(Rect rect, Rect rect2) {
        Rect rect3 = new Rect();
        snapRectToClosestEdge(rect, rect2, rect3);
        float width = ((float) (rect3.left - rect2.left)) / ((float) rect2.width());
        float height = ((float) (rect3.top - rect2.top)) / ((float) rect2.height());
        int i = rect3.top;
        if (i == rect2.top) {
            return width;
        }
        if (rect3.left == rect2.right) {
            return height + 1.0f;
        }
        return i == rect2.bottom ? (1.0f - width) + 2.0f : (1.0f - height) + 3.0f;
    }

    public void applySnapFraction(Rect rect, Rect rect2, float f) {
        if (f < 1.0f) {
            rect.offsetTo(rect2.left + ((int) (f * ((float) rect2.width()))), rect2.top);
        } else if (f < 2.0f) {
            rect.offsetTo(rect2.right, rect2.top + ((int) ((f - 1.0f) * ((float) rect2.height()))));
        } else if (f < 3.0f) {
            rect.offsetTo(rect2.left + ((int) ((1.0f - (f - 2.0f)) * ((float) rect2.width()))), rect2.bottom);
        } else {
            rect.offsetTo(rect2.left, rect2.top + ((int) ((1.0f - (f - 3.0f)) * ((float) rect2.height()))));
        }
    }

    public void getMovementBounds(Rect rect, Rect rect2, Rect rect3, int i) {
        rect3.set(rect2);
        rect3.right = Math.max(rect2.left, rect2.right - rect.width());
        int max = Math.max(rect2.top, rect2.bottom - rect.height());
        rect3.bottom = max;
        rect3.bottom = max - i;
    }

    public Size getSizeForAspectRatio(float f, float f2, int i, int i2) {
        int i3;
        int i4;
        int max = (int) Math.max(f2, ((float) Math.min(i, i2)) * this.mDefaultSizePercent);
        if (f > this.mMinAspectRatioForMinSize) {
            float f3 = this.mMaxAspectRatioForMinSize;
            if (f <= f3) {
                float f4 = (float) max;
                float length = PointF.length(f3 * f4, f4);
                max = (int) Math.round(Math.sqrt((double) ((length * length) / ((f * f) + 1.0f))));
                i4 = Math.round(((float) max) * f);
                int i5 = max;
                max = i4;
                i3 = i5;
                return new Size(max, i3);
            }
        }
        if (f <= 1.0f) {
            i3 = Math.round(((float) max) / f);
            return new Size(max, i3);
        }
        i4 = Math.round(((float) max) * f);
        int i52 = max;
        max = i4;
        i3 = i52;
        return new Size(max, i3);
    }

    public Size getSizeForAspectRatio(Size size, float f, float f2) {
        int i;
        int max = (int) Math.max(f2, (float) Math.min(size.getWidth(), size.getHeight()));
        if (f <= 1.0f) {
            i = Math.round(((float) max) / f);
        } else {
            i = max;
            max = Math.round(((float) max) * f);
        }
        return new Size(max, i);
    }

    public void snapRectToClosestEdge(Rect rect, Rect rect2, Rect rect3) {
        int max = Math.max(rect2.left, Math.min(rect2.right, rect.left));
        int max2 = Math.max(rect2.top, Math.min(rect2.bottom, rect.top));
        rect3.set(rect);
        int abs = Math.abs(rect.left - rect2.left);
        int abs2 = Math.abs(rect.top - rect2.top);
        int abs3 = Math.abs(rect2.right - rect.left);
        int min = Math.min(Math.min(abs, abs3), Math.min(abs2, Math.abs(rect2.bottom - rect.top)));
        if (min == abs) {
            rect3.offsetTo(rect2.left, max2);
        } else if (min == abs2) {
            rect3.offsetTo(max, rect2.top);
        } else if (min == abs3) {
            rect3.offsetTo(rect2.right, max2);
        } else {
            rect3.offsetTo(max, rect2.bottom);
        }
    }

    public void dump(PrintWriter printWriter, String str) {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append("  ");
        String sb2 = sb.toString();
        StringBuilder sb3 = new StringBuilder();
        sb3.append(str);
        sb3.append(PipSnapAlgorithm.class.getSimpleName());
        printWriter.println(sb3.toString());
        StringBuilder sb4 = new StringBuilder();
        sb4.append(sb2);
        sb4.append("mOrientation=");
        sb4.append(this.mOrientation);
        printWriter.println(sb4.toString());
    }
}
