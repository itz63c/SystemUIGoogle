package com.android.systemui.statusbar;

import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableWrapper;
import android.util.AttributeSet;
import android.util.Log;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2010R$drawable;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class ScreenRecordDrawable extends DrawableWrapper {
    private Drawable mFillDrawable;
    private int mHorizontalPadding;
    private float mIconRadius;
    private int mLevel;
    private Paint mPaint;
    private float mTextSize;

    public ScreenRecordDrawable() {
        super(null);
    }

    public void inflate(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, Theme theme) throws XmlPullParserException, IOException {
        super.inflate(resources, xmlPullParser, attributeSet, theme);
        setDrawable(resources.getDrawable(C2010R$drawable.ic_screen_record_background, theme).mutate());
        this.mFillDrawable = resources.getDrawable(C2010R$drawable.ic_screen_record_background, theme).mutate();
        this.mHorizontalPadding = resources.getDimensionPixelSize(C2009R$dimen.status_bar_horizontal_padding);
        this.mTextSize = (float) resources.getDimensionPixelSize(C2009R$dimen.screenrecord_status_text_size);
        this.mIconRadius = (float) resources.getDimensionPixelSize(C2009R$dimen.screenrecord_status_icon_radius);
        this.mLevel = attributeSet.getAttributeIntValue(null, "level", 0);
        Paint paint = new Paint();
        this.mPaint = paint;
        paint.setTextAlign(Align.CENTER);
        this.mPaint.setColor(-1);
        this.mPaint.setTextSize(this.mTextSize);
        this.mPaint.setFakeBoldText(true);
    }

    public boolean canApplyTheme() {
        return this.mFillDrawable.canApplyTheme() || super.canApplyTheme();
    }

    public void applyTheme(Theme theme) {
        super.applyTheme(theme);
        this.mFillDrawable.applyTheme(theme);
    }

    /* access modifiers changed from: protected */
    public void onBoundsChange(Rect rect) {
        super.onBoundsChange(rect);
        this.mFillDrawable.setBounds(rect);
    }

    public boolean onLayoutDirectionChanged(int i) {
        this.mFillDrawable.setLayoutDirection(i);
        return super.onLayoutDirectionChanged(i);
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        this.mFillDrawable.draw(canvas);
        Rect bounds = this.mFillDrawable.getBounds();
        int i = this.mLevel;
        if (i > 0) {
            String valueOf = String.valueOf(i);
            Rect rect = new Rect();
            this.mPaint.getTextBounds(valueOf, 0, valueOf.length(), rect);
            canvas.drawText(valueOf, (float) bounds.centerX(), ((float) bounds.centerY()) + ((float) (rect.height() / 4)), this.mPaint);
            return;
        }
        float centerX = (float) bounds.centerX();
        float centerY = (float) bounds.centerY();
        float f = this.mIconRadius;
        canvas.drawCircle(centerX, centerY - (f / 2.0f), f, this.mPaint);
    }

    public boolean getPadding(Rect rect) {
        int i = rect.left;
        int i2 = this.mHorizontalPadding;
        rect.left = i + i2;
        rect.right += i2;
        rect.top = 0;
        rect.bottom = 0;
        Log.d("ScreenRecordDrawable", "set zero top/bottom pad");
        return true;
    }

    public void setAlpha(int i) {
        super.setAlpha(i);
        this.mFillDrawable.setAlpha(i);
    }

    public boolean setVisible(boolean z, boolean z2) {
        this.mFillDrawable.setVisible(z, z2);
        return super.setVisible(z, z2);
    }

    public Drawable mutate() {
        this.mFillDrawable.mutate();
        return super.mutate();
    }
}
