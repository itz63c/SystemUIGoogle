package com.android.systemui.glwallpaper;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;

class ImageProcessHelper {
    /* access modifiers changed from: private */
    public static final float[] LUMINOSITY_MATRIX = {0.2126f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.7152f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0722f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f};
    /* access modifiers changed from: private */
    public static final String TAG = "ImageProcessHelper";
    private final Handler mHandler = new Handler(new Callback() {
        public boolean handleMessage(Message message) {
            if (message.what != 1) {
                return false;
            }
            ImageProcessHelper.this.mThreshold = ((Float) message.obj).floatValue();
            return true;
        }
    });
    /* access modifiers changed from: private */
    public float mThreshold = 0.8f;

    private static class Otsus implements ThresholdAlgorithm {
        private Otsus() {
        }

        public float compute(Bitmap bitmap, int[] iArr) {
            float width = (float) (bitmap.getWidth() * bitmap.getHeight());
            float[] fArr = new float[2];
            float[] fArr2 = new float[2];
            float[] fArr3 = new float[2];
            for (int i = 0; i < iArr.length; i++) {
                fArr2[1] = fArr2[1] + ((float) (iArr[i] * i));
            }
            fArr[1] = width;
            float f = 0.0f;
            float f2 = 0.0f;
            for (int i2 = 0; i2 < iArr.length; i2++) {
                float f3 = (float) iArr[i2];
                float f4 = (float) i2;
                float f5 = f3 * f4;
                fArr[0] = fArr[0] + f3;
                fArr[1] = fArr[1] - f3;
                if (!(fArr[0] == 0.0f || fArr[1] == 0.0f)) {
                    fArr2[0] = fArr2[0] + f5;
                    fArr2[1] = fArr2[1] - f5;
                    fArr3[0] = fArr2[0] / fArr[0];
                    fArr3[1] = fArr2[1] / fArr[1];
                    float f6 = fArr3[0] - fArr3[1];
                    float f7 = fArr[0] * fArr[1] * f6 * f6;
                    if (f7 > f2) {
                        f = (f4 + 1.0f) / ((float) iArr.length);
                        f2 = f7;
                    }
                }
            }
            return f;
        }
    }

    private static class Percentile85 implements ThresholdAlgorithm {
        private Percentile85() {
        }

        public float compute(Bitmap bitmap, int[] iArr) {
            float f;
            int width = bitmap.getWidth() * bitmap.getHeight();
            float[] fArr = new float[256];
            float f2 = 0.8f;
            int i = 0;
            while (i < 256) {
                fArr[i] = ((float) iArr[i]) / ((float) width);
                if (i == 0) {
                    f = 0.0f;
                } else {
                    f = fArr[i - 1];
                }
                int i2 = i + 1;
                float f3 = ((float) i2) / 255.0f;
                float f4 = fArr[i] + f;
                if (f < 0.85f && f4 >= 0.85f) {
                    f2 = f3;
                }
                if (i > 0) {
                    fArr[i] = fArr[i] + fArr[i - 1];
                }
                i = i2;
            }
            return f2;
        }
    }

    private static class Threshold {
        private Threshold() {
        }

        public float compute(Bitmap bitmap) {
            Bitmap grayscale = toGrayscale(bitmap);
            int[] histogram = getHistogram(grayscale);
            return (isSolidColor(grayscale, histogram) ? new Percentile85() : new Otsus()).compute(grayscale, histogram);
        }

        private Bitmap toGrayscale(Bitmap bitmap) {
            Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig(), false, bitmap.getColorSpace());
            Canvas canvas = new Canvas(createBitmap);
            ColorMatrix colorMatrix = new ColorMatrix(ImageProcessHelper.LUMINOSITY_MATRIX);
            Paint paint = new Paint();
            paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
            canvas.drawBitmap(bitmap, new Matrix(), paint);
            return createBitmap;
        }

        private int[] getHistogram(Bitmap bitmap) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int[] iArr = new int[256];
            for (int i = 0; i < height; i++) {
                for (int i2 = 0; i2 < width; i2++) {
                    int pixel = bitmap.getPixel(i2, i);
                    int red = Color.red(pixel) + Color.green(pixel) + Color.blue(pixel);
                    iArr[red] = iArr[red] + 1;
                }
            }
            return iArr;
        }

        private boolean isSolidColor(Bitmap bitmap, int[] iArr) {
            int width = bitmap.getWidth() * bitmap.getHeight();
            for (int i : iArr) {
                if (i != 0 && i != width) {
                    return false;
                }
                if (i == width) {
                    return true;
                }
            }
            return false;
        }
    }

    private interface ThresholdAlgorithm {
        float compute(Bitmap bitmap, int[] iArr);
    }

    private static class ThresholdComputeTask extends AsyncTask<Bitmap, Void, Float> {
        private Handler mUpdateHandler;

        ThresholdComputeTask(Handler handler) {
            super(handler);
            this.mUpdateHandler = handler;
        }

        /* access modifiers changed from: protected */
        public Float doInBackground(Bitmap... bitmapArr) {
            Bitmap bitmap = bitmapArr[0];
            if (bitmap != null) {
                return Float.valueOf(new Threshold().compute(bitmap));
            }
            Log.e(ImageProcessHelper.TAG, "ThresholdComputeTask: Can't get bitmap");
            return Float.valueOf(0.8f);
        }

        /* access modifiers changed from: protected */
        public void onPostExecute(Float f) {
            this.mUpdateHandler.sendMessage(this.mUpdateHandler.obtainMessage(1, f));
        }
    }

    ImageProcessHelper() {
    }

    /* access modifiers changed from: 0000 */
    public void start(Bitmap bitmap) {
        new ThresholdComputeTask(this.mHandler).execute(new Bitmap[]{bitmap});
    }

    /* access modifiers changed from: 0000 */
    public float getThreshold() {
        return Math.min(this.mThreshold, 0.89f);
    }
}
