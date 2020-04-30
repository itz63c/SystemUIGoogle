package com.android.systemui.statusbar;

import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableWrapper;
import android.util.AttributeSet;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2010R$drawable;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class CastDrawable extends DrawableWrapper {
    private Drawable mFillDrawable;
    private int mHorizontalPadding;

    public CastDrawable() {
        super(null);
    }

    public void inflate(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, Theme theme) throws XmlPullParserException, IOException {
        super.inflate(resources, xmlPullParser, attributeSet, theme);
        setDrawable(resources.getDrawable(C2010R$drawable.ic_cast, theme).mutate());
        this.mFillDrawable = resources.getDrawable(C2010R$drawable.ic_cast_connected_fill, theme).mutate();
        this.mHorizontalPadding = resources.getDimensionPixelSize(C2009R$dimen.status_bar_horizontal_padding);
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
    }

    public boolean getPadding(Rect rect) {
        int i = rect.left;
        int i2 = this.mHorizontalPadding;
        rect.left = i + i2;
        rect.right += i2;
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
