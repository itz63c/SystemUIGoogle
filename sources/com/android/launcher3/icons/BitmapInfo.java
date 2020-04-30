package com.android.launcher3.icons;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

public class BitmapInfo {
    public static final Bitmap LOW_RES_ICON;
    public final int color;
    public final Bitmap icon;

    public interface Extender {
        BitmapInfo getExtendedInfo(Bitmap bitmap, int i, BaseIconFactory baseIconFactory) {
            return BitmapInfo.m4of(bitmap, i);
        }
    }

    static {
        Bitmap createBitmap = Bitmap.createBitmap(1, 1, Config.ALPHA_8);
        LOW_RES_ICON = createBitmap;
        fromBitmap(createBitmap);
    }

    public BitmapInfo(Bitmap bitmap, int i) {
        this.icon = bitmap;
        this.color = i;
    }

    public static BitmapInfo fromBitmap(Bitmap bitmap) {
        return m4of(bitmap, 0);
    }

    /* renamed from: of */
    public static BitmapInfo m4of(Bitmap bitmap, int i) {
        return new BitmapInfo(bitmap, i);
    }
}
