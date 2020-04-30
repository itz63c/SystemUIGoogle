package com.android.systemui.statusbar.phone;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.View;
import com.android.settingslib.Utils;
import com.android.systemui.C2006R$attr;
import com.android.systemui.C2009R$dimen;

public class NavigationHandle extends View implements ButtonInterface {
    private final int mBottom;
    private final int mDarkColor;
    private final int mLightColor;
    private final Paint mPaint;
    private final int mRadius;
    private boolean mRequiresInvalidate;

    public void abortCurrentGesture() {
    }

    public void setDelayTouchFeedback(boolean z) {
    }

    public void setImageDrawable(Drawable drawable) {
    }

    public void setVertical(boolean z) {
    }

    public NavigationHandle(Context context) {
        this(context, null);
    }

    public NavigationHandle(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mPaint = new Paint();
        Resources resources = context.getResources();
        this.mRadius = resources.getDimensionPixelSize(C2009R$dimen.navigation_handle_radius);
        this.mBottom = resources.getDimensionPixelSize(C2009R$dimen.navigation_handle_bottom);
        int themeAttr = Utils.getThemeAttr(context, C2006R$attr.darkIconTheme);
        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(context, Utils.getThemeAttr(context, C2006R$attr.lightIconTheme));
        ContextThemeWrapper contextThemeWrapper2 = new ContextThemeWrapper(context, themeAttr);
        this.mLightColor = Utils.getColorAttrDefaultColor(contextThemeWrapper, C2006R$attr.homeHandleColor);
        this.mDarkColor = Utils.getColorAttrDefaultColor(contextThemeWrapper2, C2006R$attr.homeHandleColor);
        this.mPaint.setAntiAlias(true);
        setFocusable(false);
    }

    public void setAlpha(float f) {
        super.setAlpha(f);
        if (f > 0.0f && this.mRequiresInvalidate) {
            this.mRequiresInvalidate = false;
            invalidate();
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int height = getHeight();
        int i = this.mRadius * 2;
        int i2 = (height - this.mBottom) - i;
        float f = (float) i2;
        float width = (float) getWidth();
        float f2 = (float) (i2 + i);
        int i3 = this.mRadius;
        canvas.drawRoundRect(0.0f, f, width, f2, (float) i3, (float) i3, this.mPaint);
    }

    public void setDarkIntensity(float f) {
        int intValue = ((Integer) ArgbEvaluator.getInstance().evaluate(f, Integer.valueOf(this.mLightColor), Integer.valueOf(this.mDarkColor))).intValue();
        if (this.mPaint.getColor() != intValue) {
            this.mPaint.setColor(intValue);
            if (getVisibility() != 0 || getAlpha() <= 0.0f) {
                this.mRequiresInvalidate = true;
            } else {
                invalidate();
            }
        }
    }
}
