package com.android.launcher3.icons;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;
import java.nio.ByteBuffer;

public class IconNormalizer {
    private final RectF mAdaptiveIconBounds;
    private float mAdaptiveIconScale;
    private final Bitmap mBitmap;
    private final Rect mBounds;
    private final Canvas mCanvas = new Canvas(this.mBitmap);
    private boolean mEnableShapeDetection;
    private final float[] mLeftBorder;
    private final Matrix mMatrix;
    private final int mMaxSize;
    private final Paint mPaintMaskShape;
    private final Paint mPaintMaskShapeOutline;
    private final byte[] mPixels;
    private final float[] mRightBorder;
    private final Path mShapePath;

    IconNormalizer(Context context, int i, boolean z) {
        int i2 = i * 2;
        this.mMaxSize = i2;
        this.mBitmap = Bitmap.createBitmap(i2, i2, Config.ALPHA_8);
        int i3 = this.mMaxSize;
        this.mPixels = new byte[(i3 * i3)];
        this.mLeftBorder = new float[i3];
        this.mRightBorder = new float[i3];
        this.mBounds = new Rect();
        this.mAdaptiveIconBounds = new RectF();
        Paint paint = new Paint();
        this.mPaintMaskShape = paint;
        paint.setColor(-65536);
        this.mPaintMaskShape.setStyle(Style.FILL);
        this.mPaintMaskShape.setXfermode(new PorterDuffXfermode(Mode.XOR));
        Paint paint2 = new Paint();
        this.mPaintMaskShapeOutline = paint2;
        paint2.setStrokeWidth(context.getResources().getDisplayMetrics().density * 2.0f);
        this.mPaintMaskShapeOutline.setStyle(Style.STROKE);
        this.mPaintMaskShapeOutline.setColor(-16777216);
        this.mPaintMaskShapeOutline.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
        this.mShapePath = new Path();
        this.mMatrix = new Matrix();
        this.mAdaptiveIconScale = 0.0f;
        this.mEnableShapeDetection = z;
    }

    private static float getScale(float f, float f2, float f3) {
        float f4 = f / f2;
        float f5 = f4 < 0.7853982f ? 0.6597222f : ((1.0f - f4) * 0.040449437f) + 0.6510417f;
        float f6 = f / f3;
        if (f6 > f5) {
            return (float) Math.sqrt((double) (f5 / f6));
        }
        return 1.0f;
    }

    @TargetApi(26)
    public static float normalizeAdaptiveIcon(Drawable drawable, int i, RectF rectF) {
        Rect rect = new Rect(drawable.getBounds());
        drawable.setBounds(0, 0, i, i);
        Path iconMask = ((AdaptiveIconDrawable) drawable).getIconMask();
        Region region = new Region();
        region.setPath(iconMask, new Region(0, 0, i, i));
        Rect bounds = region.getBounds();
        int area = GraphicsUtils.getArea(region);
        if (rectF != null) {
            float f = (float) i;
            rectF.set(((float) bounds.left) / f, ((float) bounds.top) / f, 1.0f - (((float) bounds.right) / f), 1.0f - (((float) bounds.bottom) / f));
        }
        drawable.setBounds(rect);
        float f2 = (float) area;
        return getScale(f2, f2, (float) (i * i));
    }

    private boolean isShape(Path path) {
        if (Math.abs((((float) this.mBounds.width()) / ((float) this.mBounds.height())) - 1.0f) > 0.05f) {
            return false;
        }
        this.mMatrix.reset();
        this.mMatrix.setScale((float) this.mBounds.width(), (float) this.mBounds.height());
        Matrix matrix = this.mMatrix;
        Rect rect = this.mBounds;
        matrix.postTranslate((float) rect.left, (float) rect.top);
        path.transform(this.mMatrix, this.mShapePath);
        this.mCanvas.drawPath(this.mShapePath, this.mPaintMaskShape);
        this.mCanvas.drawPath(this.mShapePath, this.mPaintMaskShapeOutline);
        return isTransparentBitmap();
    }

    private boolean isTransparentBitmap() {
        Rect rect;
        ByteBuffer wrap = ByteBuffer.wrap(this.mPixels);
        wrap.rewind();
        this.mBitmap.copyPixelsToBuffer(wrap);
        Rect rect2 = this.mBounds;
        int i = rect2.top;
        int i2 = this.mMaxSize;
        int i3 = i * i2;
        int i4 = i2 - rect2.right;
        int i5 = 0;
        while (true) {
            rect = this.mBounds;
            if (i >= rect.bottom) {
                break;
            }
            int i6 = rect.left;
            int i7 = i3 + i6;
            while (i6 < this.mBounds.right) {
                if ((this.mPixels[i7] & 255) > 40) {
                    i5++;
                }
                i7++;
                i6++;
            }
            i3 = i7 + i4;
            i++;
        }
        if (((float) i5) / ((float) (rect.width() * this.mBounds.height())) < 0.005f) {
            return true;
        }
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:75:0x014f, code lost:
        return 1.0f;
     */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x008b  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x00cd  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized float getScale(android.graphics.drawable.Drawable r17, android.graphics.RectF r18, android.graphics.Path r19, boolean[] r20) {
        /*
            r16 = this;
            r1 = r16
            r0 = r17
            r2 = r18
            r3 = r20
            monitor-enter(r16)
            boolean r4 = com.android.launcher3.icons.BaseIconFactory.ATLEAST_OREO     // Catch:{ all -> 0x0150 }
            r5 = 0
            if (r4 == 0) goto L_0x002d
            boolean r4 = r0 instanceof android.graphics.drawable.AdaptiveIconDrawable     // Catch:{ all -> 0x0150 }
            if (r4 == 0) goto L_0x002d
            float r3 = r1.mAdaptiveIconScale     // Catch:{ all -> 0x0150 }
            int r3 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r3 != 0) goto L_0x0022
            int r3 = r1.mMaxSize     // Catch:{ all -> 0x0150 }
            android.graphics.RectF r4 = r1.mAdaptiveIconBounds     // Catch:{ all -> 0x0150 }
            float r0 = normalizeAdaptiveIcon(r0, r3, r4)     // Catch:{ all -> 0x0150 }
            r1.mAdaptiveIconScale = r0     // Catch:{ all -> 0x0150 }
        L_0x0022:
            if (r2 == 0) goto L_0x0029
            android.graphics.RectF r0 = r1.mAdaptiveIconBounds     // Catch:{ all -> 0x0150 }
            r2.set(r0)     // Catch:{ all -> 0x0150 }
        L_0x0029:
            float r0 = r1.mAdaptiveIconScale     // Catch:{ all -> 0x0150 }
            monitor-exit(r16)
            return r0
        L_0x002d:
            int r4 = r17.getIntrinsicWidth()     // Catch:{ all -> 0x0150 }
            int r6 = r17.getIntrinsicHeight()     // Catch:{ all -> 0x0150 }
            if (r4 <= 0) goto L_0x0051
            if (r6 > 0) goto L_0x003a
            goto L_0x0051
        L_0x003a:
            int r7 = r1.mMaxSize     // Catch:{ all -> 0x0150 }
            if (r4 > r7) goto L_0x0042
            int r7 = r1.mMaxSize     // Catch:{ all -> 0x0150 }
            if (r6 <= r7) goto L_0x0061
        L_0x0042:
            int r7 = java.lang.Math.max(r4, r6)     // Catch:{ all -> 0x0150 }
            int r8 = r1.mMaxSize     // Catch:{ all -> 0x0150 }
            int r8 = r8 * r4
            int r4 = r8 / r7
            int r8 = r1.mMaxSize     // Catch:{ all -> 0x0150 }
            int r8 = r8 * r6
            int r6 = r8 / r7
            goto L_0x0061
        L_0x0051:
            if (r4 <= 0) goto L_0x0057
            int r7 = r1.mMaxSize     // Catch:{ all -> 0x0150 }
            if (r4 <= r7) goto L_0x0059
        L_0x0057:
            int r4 = r1.mMaxSize     // Catch:{ all -> 0x0150 }
        L_0x0059:
            if (r6 <= 0) goto L_0x005f
            int r7 = r1.mMaxSize     // Catch:{ all -> 0x0150 }
            if (r6 <= r7) goto L_0x0061
        L_0x005f:
            int r6 = r1.mMaxSize     // Catch:{ all -> 0x0150 }
        L_0x0061:
            android.graphics.Bitmap r7 = r1.mBitmap     // Catch:{ all -> 0x0150 }
            r8 = 0
            r7.eraseColor(r8)     // Catch:{ all -> 0x0150 }
            r0.setBounds(r8, r8, r4, r6)     // Catch:{ all -> 0x0150 }
            android.graphics.Canvas r7 = r1.mCanvas     // Catch:{ all -> 0x0150 }
            r0.draw(r7)     // Catch:{ all -> 0x0150 }
            byte[] r0 = r1.mPixels     // Catch:{ all -> 0x0150 }
            java.nio.ByteBuffer r0 = java.nio.ByteBuffer.wrap(r0)     // Catch:{ all -> 0x0150 }
            r0.rewind()     // Catch:{ all -> 0x0150 }
            android.graphics.Bitmap r7 = r1.mBitmap     // Catch:{ all -> 0x0150 }
            r7.copyPixelsToBuffer(r0)     // Catch:{ all -> 0x0150 }
            int r0 = r1.mMaxSize     // Catch:{ all -> 0x0150 }
            r7 = 1
            int r0 = r0 + r7
            int r9 = r1.mMaxSize     // Catch:{ all -> 0x0150 }
            int r9 = r9 - r4
            r11 = r8
            r15 = r11
            r12 = -1
            r13 = -1
            r14 = -1
        L_0x0089:
            if (r11 >= r6) goto L_0x00c8
            r5 = r8
            r7 = -1
            r8 = -1
        L_0x008e:
            if (r5 >= r4) goto L_0x00a6
            byte[] r10 = r1.mPixels     // Catch:{ all -> 0x0150 }
            byte r10 = r10[r15]     // Catch:{ all -> 0x0150 }
            r10 = r10 & 255(0xff, float:3.57E-43)
            r3 = 40
            if (r10 <= r3) goto L_0x009f
            r3 = -1
            if (r8 != r3) goto L_0x009e
            r8 = r5
        L_0x009e:
            r7 = r5
        L_0x009f:
            int r15 = r15 + 1
            int r5 = r5 + 1
            r3 = r20
            goto L_0x008e
        L_0x00a6:
            int r15 = r15 + r9
            float[] r3 = r1.mLeftBorder     // Catch:{ all -> 0x0150 }
            float r5 = (float) r8     // Catch:{ all -> 0x0150 }
            r3[r11] = r5     // Catch:{ all -> 0x0150 }
            float[] r3 = r1.mRightBorder     // Catch:{ all -> 0x0150 }
            float r5 = (float) r7     // Catch:{ all -> 0x0150 }
            r3[r11] = r5     // Catch:{ all -> 0x0150 }
            r3 = -1
            if (r8 == r3) goto L_0x00c0
            if (r12 != r3) goto L_0x00b7
            r12 = r11
        L_0x00b7:
            int r0 = java.lang.Math.min(r0, r8)     // Catch:{ all -> 0x0150 }
            int r13 = java.lang.Math.max(r13, r7)     // Catch:{ all -> 0x0150 }
            r14 = r11
        L_0x00c0:
            int r11 = r11 + 1
            r3 = r20
            r5 = 0
            r7 = 1
            r8 = 0
            goto L_0x0089
        L_0x00c8:
            r3 = 1065353216(0x3f800000, float:1.0)
            r5 = -1
            if (r12 == r5) goto L_0x014e
            if (r13 != r5) goto L_0x00d1
            goto L_0x014e
        L_0x00d1:
            float[] r7 = r1.mLeftBorder     // Catch:{ all -> 0x0150 }
            r8 = 1
            convertToConvexArray(r7, r8, r12, r14)     // Catch:{ all -> 0x0150 }
            float[] r7 = r1.mRightBorder     // Catch:{ all -> 0x0150 }
            convertToConvexArray(r7, r5, r12, r14)     // Catch:{ all -> 0x0150 }
            r5 = 0
            r7 = 0
        L_0x00de:
            if (r7 >= r6) goto L_0x00f9
            float[] r8 = r1.mLeftBorder     // Catch:{ all -> 0x0150 }
            r8 = r8[r7]     // Catch:{ all -> 0x0150 }
            r9 = -1082130432(0xffffffffbf800000, float:-1.0)
            int r8 = (r8 > r9 ? 1 : (r8 == r9 ? 0 : -1))
            if (r8 > 0) goto L_0x00eb
            goto L_0x00f6
        L_0x00eb:
            float[] r8 = r1.mRightBorder     // Catch:{ all -> 0x0150 }
            r8 = r8[r7]     // Catch:{ all -> 0x0150 }
            float[] r9 = r1.mLeftBorder     // Catch:{ all -> 0x0150 }
            r9 = r9[r7]     // Catch:{ all -> 0x0150 }
            float r8 = r8 - r9
            float r8 = r8 + r3
            float r5 = r5 + r8
        L_0x00f6:
            int r7 = r7 + 1
            goto L_0x00de
        L_0x00f9:
            android.graphics.Rect r7 = r1.mBounds     // Catch:{ all -> 0x0150 }
            r7.left = r0     // Catch:{ all -> 0x0150 }
            android.graphics.Rect r7 = r1.mBounds     // Catch:{ all -> 0x0150 }
            r7.right = r13     // Catch:{ all -> 0x0150 }
            android.graphics.Rect r7 = r1.mBounds     // Catch:{ all -> 0x0150 }
            r7.top = r12     // Catch:{ all -> 0x0150 }
            android.graphics.Rect r7 = r1.mBounds     // Catch:{ all -> 0x0150 }
            r7.bottom = r14     // Catch:{ all -> 0x0150 }
            if (r2 == 0) goto L_0x012b
            android.graphics.Rect r7 = r1.mBounds     // Catch:{ all -> 0x0150 }
            int r7 = r7.left     // Catch:{ all -> 0x0150 }
            float r7 = (float) r7     // Catch:{ all -> 0x0150 }
            float r8 = (float) r4     // Catch:{ all -> 0x0150 }
            float r7 = r7 / r8
            android.graphics.Rect r9 = r1.mBounds     // Catch:{ all -> 0x0150 }
            int r9 = r9.top     // Catch:{ all -> 0x0150 }
            float r9 = (float) r9     // Catch:{ all -> 0x0150 }
            float r10 = (float) r6     // Catch:{ all -> 0x0150 }
            float r9 = r9 / r10
            android.graphics.Rect r11 = r1.mBounds     // Catch:{ all -> 0x0150 }
            int r11 = r11.right     // Catch:{ all -> 0x0150 }
            float r11 = (float) r11     // Catch:{ all -> 0x0150 }
            float r11 = r11 / r8
            float r8 = r3 - r11
            android.graphics.Rect r11 = r1.mBounds     // Catch:{ all -> 0x0150 }
            int r11 = r11.bottom     // Catch:{ all -> 0x0150 }
            float r11 = (float) r11     // Catch:{ all -> 0x0150 }
            float r11 = r11 / r10
            float r3 = r3 - r11
            r2.set(r7, r9, r8, r3)     // Catch:{ all -> 0x0150 }
        L_0x012b:
            r2 = r20
            if (r2 == 0) goto L_0x013f
            boolean r3 = r1.mEnableShapeDetection     // Catch:{ all -> 0x0150 }
            if (r3 == 0) goto L_0x013f
            int r3 = r2.length     // Catch:{ all -> 0x0150 }
            if (r3 <= 0) goto L_0x013f
            r3 = r19
            boolean r3 = r1.isShape(r3)     // Catch:{ all -> 0x0150 }
            r7 = 0
            r2[r7] = r3     // Catch:{ all -> 0x0150 }
        L_0x013f:
            r2 = 1
            int r14 = r14 + r2
            int r14 = r14 - r12
            int r13 = r13 + r2
            int r13 = r13 - r0
            int r14 = r14 * r13
            float r0 = (float) r14     // Catch:{ all -> 0x0150 }
            int r4 = r4 * r6
            float r2 = (float) r4     // Catch:{ all -> 0x0150 }
            float r0 = getScale(r5, r0, r2)     // Catch:{ all -> 0x0150 }
            monitor-exit(r16)
            return r0
        L_0x014e:
            monitor-exit(r16)
            return r3
        L_0x0150:
            r0 = move-exception
            monitor-exit(r16)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.icons.IconNormalizer.getScale(android.graphics.drawable.Drawable, android.graphics.RectF, android.graphics.Path, boolean[]):float");
    }

    private static void convertToConvexArray(float[] fArr, int i, int i2, int i3) {
        float[] fArr2 = new float[(fArr.length - 1)];
        int i4 = -1;
        float f = Float.MAX_VALUE;
        for (int i5 = i2 + 1; i5 <= i3; i5++) {
            if (fArr[i5] > -1.0f) {
                if (f == Float.MAX_VALUE) {
                    i4 = i2;
                } else {
                    float f2 = ((fArr[i5] - fArr[i4]) / ((float) (i5 - i4))) - f;
                    float f3 = (float) i;
                    if (f2 * f3 < 0.0f) {
                        while (i4 > i2) {
                            i4--;
                            if ((((fArr[i5] - fArr[i4]) / ((float) (i5 - i4))) - fArr2[i4]) * f3 >= 0.0f) {
                                break;
                            }
                        }
                    }
                }
                f = (fArr[i5] - fArr[i4]) / ((float) (i5 - i4));
                for (int i6 = i4; i6 < i5; i6++) {
                    fArr2[i6] = f;
                    fArr[i6] = fArr[i4] + (((float) (i6 - i4)) * f);
                }
                i4 = i5;
            }
        }
    }
}
