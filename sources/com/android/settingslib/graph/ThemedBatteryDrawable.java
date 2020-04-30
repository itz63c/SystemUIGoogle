package com.android.settingslib.graph;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Path.Op;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.PathParser;
import com.android.settingslib.R$array;
import com.android.settingslib.R$color;
import com.android.settingslib.Utils;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ThemedBatteryDrawable.kt */
public class ThemedBatteryDrawable extends Drawable {
    private int batteryLevel;
    private final Path boltPath;
    private boolean charging;
    private int[] colorLevels;
    private final Context context;
    private boolean dualTone;
    private final Paint dualToneBackgroundFill;
    private final Paint errorPaint;
    private final Path errorPerimeterPath = new Path();
    private int fillColor;
    private final Paint fillColorStrokePaint;
    private final Paint fillColorStrokeProtection;
    private final Path fillMask = new Path();
    private final Paint fillPaint;
    private final RectF fillRect = new RectF();
    private int intrinsicHeight;
    private int intrinsicWidth;
    private final Function0<Unit> invalidateRunnable;
    private boolean invertFillIcon;
    private int levelColor;
    private final Path levelPath = new Path();
    private final RectF levelRect = new RectF();
    private final Path perimeterPath = new Path();
    private final Path plusPath;
    private boolean powerSaveEnabled;
    private final Matrix scaleMatrix = new Matrix();
    private final Path scaledBolt;
    private final Path scaledErrorPerimeter = new Path();
    private final Path scaledFill = new Path();
    private final Path scaledPerimeter = new Path();
    private final Path scaledPlus;
    private final Path unifiedPath;

    public int getOpacity() {
        return -1;
    }

    public void setAlpha(int i) {
    }

    public ThemedBatteryDrawable(Context context2, int i) {
        Intrinsics.checkParameterIsNotNull(context2, "context");
        this.context = context2;
        new Rect();
        this.unifiedPath = new Path();
        this.boltPath = new Path();
        this.scaledBolt = new Path();
        this.plusPath = new Path();
        this.scaledPlus = new Path();
        this.fillColor = -65281;
        this.levelColor = -65281;
        this.invalidateRunnable = new ThemedBatteryDrawable$invalidateRunnable$1(this);
        this.context.getResources().getInteger(17694765);
        Paint paint = new Paint(1);
        paint.setColor(i);
        paint.setDither(true);
        paint.setStrokeWidth(5.0f);
        paint.setStyle(Style.STROKE);
        paint.setBlendMode(BlendMode.SRC);
        paint.setStrokeMiter(5.0f);
        paint.setStrokeJoin(Join.ROUND);
        this.fillColorStrokePaint = paint;
        Paint paint2 = new Paint(1);
        paint2.setDither(true);
        paint2.setStrokeWidth(5.0f);
        paint2.setStyle(Style.STROKE);
        paint2.setBlendMode(BlendMode.CLEAR);
        paint2.setStrokeMiter(5.0f);
        paint2.setStrokeJoin(Join.ROUND);
        this.fillColorStrokeProtection = paint2;
        Paint paint3 = new Paint(1);
        paint3.setColor(i);
        paint3.setAlpha(255);
        paint3.setDither(true);
        paint3.setStrokeWidth(0.0f);
        paint3.setStyle(Style.FILL_AND_STROKE);
        this.fillPaint = paint3;
        Paint paint4 = new Paint(1);
        paint4.setColor(Utils.getColorStateListDefaultColor(this.context, R$color.batterymeter_plus_color));
        paint4.setAlpha(255);
        paint4.setDither(true);
        paint4.setStrokeWidth(0.0f);
        paint4.setStyle(Style.FILL_AND_STROKE);
        paint4.setBlendMode(BlendMode.SRC);
        this.errorPaint = paint4;
        Paint paint5 = new Paint(1);
        paint5.setColor(i);
        paint5.setAlpha(255);
        paint5.setDither(true);
        paint5.setStrokeWidth(0.0f);
        paint5.setStyle(Style.FILL_AND_STROKE);
        this.dualToneBackgroundFill = paint5;
        Resources resources = this.context.getResources();
        Intrinsics.checkExpressionValueIsNotNull(resources, "context.resources");
        float f = resources.getDisplayMetrics().density;
        this.intrinsicHeight = (int) (20.0f * f);
        this.intrinsicWidth = (int) (f * 12.0f);
        Resources resources2 = this.context.getResources();
        TypedArray obtainTypedArray = resources2.obtainTypedArray(R$array.batterymeter_color_levels);
        TypedArray obtainTypedArray2 = resources2.obtainTypedArray(R$array.batterymeter_color_values);
        int length = obtainTypedArray.length();
        this.colorLevels = new int[(length * 2)];
        for (int i2 = 0; i2 < length; i2++) {
            int i3 = i2 * 2;
            this.colorLevels[i3] = obtainTypedArray.getInt(i2, 0);
            if (obtainTypedArray2.getType(i2) == 2) {
                this.colorLevels[i3 + 1] = Utils.getColorAttrDefaultColor(this.context, obtainTypedArray2.getThemeAttributeId(i2, 0));
            } else {
                this.colorLevels[i3 + 1] = obtainTypedArray2.getColor(i2, 0);
            }
        }
        obtainTypedArray.recycle();
        obtainTypedArray2.recycle();
        loadPaths();
    }

    public final void setCharging(boolean z) {
        this.charging = z;
        postInvalidate();
    }

    public final boolean getPowerSaveEnabled() {
        return this.powerSaveEnabled;
    }

    public final void setPowerSaveEnabled(boolean z) {
        this.powerSaveEnabled = z;
        postInvalidate();
    }

    public void draw(Canvas canvas) {
        float f;
        Intrinsics.checkParameterIsNotNull(canvas, "c");
        canvas.saveLayer(null, null);
        this.unifiedPath.reset();
        this.levelPath.reset();
        this.levelRect.set(this.fillRect);
        int i = this.batteryLevel;
        float f2 = ((float) i) / 100.0f;
        if (i >= 95) {
            f = this.fillRect.top;
        } else {
            RectF rectF = this.fillRect;
            f = (rectF.height() * (((float) 1) - f2)) + rectF.top;
        }
        this.levelRect.top = (float) Math.floor((double) f);
        this.levelPath.addRect(this.levelRect, Direction.CCW);
        this.unifiedPath.addPath(this.scaledPerimeter);
        if (!this.dualTone) {
            this.unifiedPath.op(this.levelPath, Op.UNION);
        }
        this.fillPaint.setColor(this.levelColor);
        if (this.charging) {
            this.unifiedPath.op(this.scaledBolt, Op.DIFFERENCE);
            if (!this.invertFillIcon) {
                canvas.drawPath(this.scaledBolt, this.fillPaint);
            }
        }
        if (this.dualTone) {
            canvas.drawPath(this.unifiedPath, this.dualToneBackgroundFill);
            canvas.save();
            canvas.clipRect(0.0f, ((float) getBounds().bottom) - (((float) getBounds().height()) * f2), (float) getBounds().right, (float) getBounds().bottom);
            canvas.drawPath(this.unifiedPath, this.fillPaint);
            canvas.restore();
        } else {
            this.fillPaint.setColor(this.fillColor);
            canvas.drawPath(this.unifiedPath, this.fillPaint);
            this.fillPaint.setColor(this.levelColor);
            if (this.batteryLevel <= 15 && !this.charging) {
                canvas.save();
                canvas.clipPath(this.scaledFill);
                canvas.drawPath(this.levelPath, this.fillPaint);
                canvas.restore();
            }
        }
        if (this.charging) {
            canvas.clipOutPath(this.scaledBolt);
            if (this.invertFillIcon) {
                canvas.drawPath(this.scaledBolt, this.fillColorStrokePaint);
            } else {
                canvas.drawPath(this.scaledBolt, this.fillColorStrokeProtection);
            }
        } else if (this.powerSaveEnabled) {
            canvas.drawPath(this.scaledErrorPerimeter, this.errorPaint);
            canvas.drawPath(this.scaledPlus, this.errorPaint);
        }
        canvas.restore();
    }

    private final int batteryColorForLevel(int i) {
        if (this.charging || this.powerSaveEnabled) {
            return this.fillColor;
        }
        return getColorForLevel(i);
    }

    private final int getColorForLevel(int i) {
        int i2 = 0;
        int i3 = 0;
        while (true) {
            int[] iArr = this.colorLevels;
            if (i2 >= iArr.length) {
                return i3;
            }
            int i4 = iArr[i2];
            int i5 = iArr[i2 + 1];
            if (i <= i4) {
                if (i2 == iArr.length - 2) {
                    i5 = this.fillColor;
                }
                return i5;
            }
            i2 += 2;
            i3 = i5;
        }
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.fillPaint.setColorFilter(colorFilter);
        this.fillColorStrokePaint.setColorFilter(colorFilter);
        this.dualToneBackgroundFill.setColorFilter(colorFilter);
    }

    public int getIntrinsicHeight() {
        return this.intrinsicHeight;
    }

    public int getIntrinsicWidth() {
        return this.intrinsicWidth;
    }

    public void setBatteryLevel(int i) {
        boolean z = i >= 67 ? true : i <= 33 ? false : this.invertFillIcon;
        this.invertFillIcon = z;
        this.batteryLevel = i;
        this.levelColor = batteryColorForLevel(i);
        invalidateSelf();
    }

    /* access modifiers changed from: protected */
    public void onBoundsChange(Rect rect) {
        super.onBoundsChange(rect);
        updateSize();
    }

    public final void setColors(int i, int i2, int i3) {
        if (!this.dualTone) {
            i = i3;
        }
        this.fillColor = i;
        this.fillPaint.setColor(i);
        this.fillColorStrokePaint.setColor(this.fillColor);
        this.dualToneBackgroundFill.setColor(i2);
        this.levelColor = batteryColorForLevel(this.batteryLevel);
        invalidateSelf();
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [kotlin.jvm.functions.Function0<kotlin.Unit>, kotlin.jvm.functions.Function0] */
    /* JADX WARNING: type inference failed for: r0v1 */
    /* JADX WARNING: type inference failed for: r0v3, types: [kotlin.jvm.functions.Function0<kotlin.Unit>, kotlin.jvm.functions.Function0] */
    /* JADX WARNING: type inference failed for: r0v4 */
    /* JADX WARNING: type inference failed for: r1v1, types: [com.android.settingslib.graph.ThemedBatteryDrawable$sam$java_lang_Runnable$0] */
    /* JADX WARNING: type inference failed for: r0v6 */
    /* JADX WARNING: type inference failed for: r1v2, types: [com.android.settingslib.graph.ThemedBatteryDrawable$sam$java_lang_Runnable$0] */
    /* JADX WARNING: type inference failed for: r0v7 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 4 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final void postInvalidate() {
        /*
            r3 = this;
            kotlin.jvm.functions.Function0<kotlin.Unit> r0 = r3.invalidateRunnable
            if (r0 == 0) goto L_0x000a
            com.android.settingslib.graph.ThemedBatteryDrawable$sam$java_lang_Runnable$0 r1 = new com.android.settingslib.graph.ThemedBatteryDrawable$sam$java_lang_Runnable$0
            r1.<init>(r0)
            r0 = r1
        L_0x000a:
            java.lang.Runnable r0 = (java.lang.Runnable) r0
            r3.unscheduleSelf(r0)
            kotlin.jvm.functions.Function0<kotlin.Unit> r0 = r3.invalidateRunnable
            if (r0 == 0) goto L_0x0019
            com.android.settingslib.graph.ThemedBatteryDrawable$sam$java_lang_Runnable$0 r1 = new com.android.settingslib.graph.ThemedBatteryDrawable$sam$java_lang_Runnable$0
            r1.<init>(r0)
            r0 = r1
        L_0x0019:
            java.lang.Runnable r0 = (java.lang.Runnable) r0
            r1 = 0
            r3.scheduleSelf(r0, r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.graph.ThemedBatteryDrawable.postInvalidate():void");
    }

    private final void updateSize() {
        Rect bounds = getBounds();
        Intrinsics.checkExpressionValueIsNotNull(bounds, "b");
        if (bounds.isEmpty()) {
            this.scaleMatrix.setScale(1.0f, 1.0f);
        } else {
            this.scaleMatrix.setScale(((float) bounds.right) / 12.0f, ((float) bounds.bottom) / 20.0f);
        }
        this.perimeterPath.transform(this.scaleMatrix, this.scaledPerimeter);
        this.errorPerimeterPath.transform(this.scaleMatrix, this.scaledErrorPerimeter);
        this.fillMask.transform(this.scaleMatrix, this.scaledFill);
        this.scaledFill.computeBounds(this.fillRect, true);
        this.boltPath.transform(this.scaleMatrix, this.scaledBolt);
        this.plusPath.transform(this.scaleMatrix, this.scaledPlus);
        float max = Math.max((((float) bounds.right) / 12.0f) * 3.0f, 6.0f);
        this.fillColorStrokePaint.setStrokeWidth(max);
        this.fillColorStrokeProtection.setStrokeWidth(max);
    }

    private final void loadPaths() {
        this.perimeterPath.set(PathParser.createPathFromPathData(this.context.getResources().getString(17039729)));
        this.perimeterPath.computeBounds(new RectF(), true);
        this.errorPerimeterPath.set(PathParser.createPathFromPathData(this.context.getResources().getString(17039727)));
        this.errorPerimeterPath.computeBounds(new RectF(), true);
        this.fillMask.set(PathParser.createPathFromPathData(this.context.getResources().getString(17039728)));
        this.fillMask.computeBounds(this.fillRect, true);
        this.boltPath.set(PathParser.createPathFromPathData(this.context.getResources().getString(17039726)));
        this.plusPath.set(PathParser.createPathFromPathData(this.context.getResources().getString(17039730)));
        this.dualTone = this.context.getResources().getBoolean(17891375);
    }
}
