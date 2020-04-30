package com.android.systemui.assist.p003ui;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.util.MathUtils;
import android.view.ContextThemeWrapper;
import android.view.View;
import com.android.settingslib.Utils;
import com.android.systemui.C2006R$attr;
import com.android.systemui.Dependency;
import com.android.systemui.assist.p003ui.PerimeterPathGuide.Region;
import com.android.systemui.statusbar.NavigationBarController;
import com.android.systemui.statusbar.phone.NavigationBarFragment;
import com.android.systemui.statusbar.phone.NavigationBarTransitions.DarkIntensityListener;
import java.util.ArrayList;
import java.util.Iterator;

/* renamed from: com.android.systemui.assist.ui.InvocationLightsView */
public class InvocationLightsView extends View implements DarkIntensityListener {
    protected final ArrayList<EdgeLight> mAssistInvocationLights;
    private final int mDarkColor;
    protected final PerimeterPathGuide mGuide;
    private final int mLightColor;
    private final Paint mPaint;
    private final Path mPath;
    private boolean mRegistered;
    private int[] mScreenLocation;
    private final int mStrokeWidth;
    private boolean mUseNavBarColor;
    private final int mViewHeight;

    public InvocationLightsView(Context context) {
        this(context, null);
    }

    public InvocationLightsView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public InvocationLightsView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public InvocationLightsView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mAssistInvocationLights = new ArrayList<>();
        this.mPaint = new Paint();
        this.mPath = new Path();
        this.mScreenLocation = new int[2];
        this.mRegistered = false;
        this.mUseNavBarColor = true;
        int convertDpToPx = DisplayUtils.convertDpToPx(3.0f, context);
        this.mStrokeWidth = convertDpToPx;
        this.mPaint.setStrokeWidth((float) convertDpToPx);
        this.mPaint.setStyle(Style.STROKE);
        this.mPaint.setStrokeJoin(Join.MITER);
        this.mPaint.setAntiAlias(true);
        Context context2 = context;
        PerimeterPathGuide perimeterPathGuide = new PerimeterPathGuide(context2, createCornerPathRenderer(context), this.mStrokeWidth / 2, DisplayUtils.getWidth(context), DisplayUtils.getHeight(context));
        this.mGuide = perimeterPathGuide;
        this.mViewHeight = Math.max(Math.max(DisplayUtils.getCornerRadiusBottom(context), DisplayUtils.getCornerRadiusTop(context)), DisplayUtils.convertDpToPx(3.0f, context));
        int themeAttr = Utils.getThemeAttr(this.mContext, C2006R$attr.darkIconTheme);
        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(this.mContext, Utils.getThemeAttr(this.mContext, C2006R$attr.lightIconTheme));
        ContextThemeWrapper contextThemeWrapper2 = new ContextThemeWrapper(this.mContext, themeAttr);
        this.mLightColor = Utils.getColorAttrDefaultColor(contextThemeWrapper, C2006R$attr.singleToneColor);
        this.mDarkColor = Utils.getColorAttrDefaultColor(contextThemeWrapper2, C2006R$attr.singleToneColor);
        for (int i3 = 0; i3 < 4; i3++) {
            this.mAssistInvocationLights.add(new EdgeLight(0, 0.0f, 0.0f));
        }
    }

    public void onInvocationProgress(float f) {
        if (f == 0.0f) {
            setVisibility(8);
        } else {
            attemptRegisterNavBarListener();
            float regionWidth = this.mGuide.getRegionWidth(Region.BOTTOM_LEFT);
            float f2 = (regionWidth - (0.6f * regionWidth)) / 2.0f;
            float lerp = MathUtils.lerp(0.0f, this.mGuide.getRegionWidth(Region.BOTTOM) / 4.0f, f);
            float f3 = 1.0f - f;
            float f4 = ((-regionWidth) + f2) * f3;
            float regionWidth2 = this.mGuide.getRegionWidth(Region.BOTTOM) + ((regionWidth - f2) * f3);
            float f5 = f4 + lerp;
            setLight(0, f4, f5);
            float f6 = 2.0f * lerp;
            setLight(1, f5, f4 + f6);
            float f7 = regionWidth2 - lerp;
            setLight(2, regionWidth2 - f6, f7);
            setLight(3, f7, regionWidth2);
            setVisibility(0);
        }
        invalidate();
    }

    public void hide() {
        setVisibility(8);
        Iterator it = this.mAssistInvocationLights.iterator();
        while (it.hasNext()) {
            ((EdgeLight) it.next()).setEndpoints(0.0f, 0.0f);
        }
        attemptUnregisterNavBarListener();
    }

    public void setColors(Integer num) {
        if (num == null) {
            this.mUseNavBarColor = true;
            this.mPaint.setStrokeCap(Cap.BUTT);
            attemptRegisterNavBarListener();
            return;
        }
        setColors(num.intValue(), num.intValue(), num.intValue(), num.intValue());
    }

    public void setColors(int i, int i2, int i3, int i4) {
        this.mUseNavBarColor = false;
        attemptUnregisterNavBarListener();
        ((EdgeLight) this.mAssistInvocationLights.get(0)).setColor(i);
        ((EdgeLight) this.mAssistInvocationLights.get(1)).setColor(i2);
        ((EdgeLight) this.mAssistInvocationLights.get(2)).setColor(i3);
        ((EdgeLight) this.mAssistInvocationLights.get(3)).setColor(i4);
    }

    public void onDarkIntensity(float f) {
        updateDarkness(f);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        getLayoutParams().height = this.mViewHeight;
        requestLayout();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        this.mGuide.setRotation(getContext().getDisplay().getRotation());
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        getLocationOnScreen(this.mScreenLocation);
        int[] iArr = this.mScreenLocation;
        canvas.translate((float) (-iArr[0]), (float) (-iArr[1]));
        if (this.mUseNavBarColor) {
            Iterator it = this.mAssistInvocationLights.iterator();
            while (it.hasNext()) {
                renderLight((EdgeLight) it.next(), canvas);
            }
            return;
        }
        this.mPaint.setStrokeCap(Cap.ROUND);
        renderLight((EdgeLight) this.mAssistInvocationLights.get(0), canvas);
        renderLight((EdgeLight) this.mAssistInvocationLights.get(3), canvas);
        this.mPaint.setStrokeCap(Cap.BUTT);
        renderLight((EdgeLight) this.mAssistInvocationLights.get(1), canvas);
        renderLight((EdgeLight) this.mAssistInvocationLights.get(2), canvas);
    }

    /* access modifiers changed from: protected */
    public void setLight(int i, float f, float f2) {
        if (i < 0 || i >= 4) {
            StringBuilder sb = new StringBuilder();
            sb.append("invalid invocation light index: ");
            sb.append(i);
            Log.w("InvocationLightsView", sb.toString());
        }
        ((EdgeLight) this.mAssistInvocationLights.get(i)).setEndpoints(f, f2);
    }

    /* access modifiers changed from: protected */
    public CornerPathRenderer createCornerPathRenderer(Context context) {
        return new CircularCornerPathRenderer(context);
    }

    /* access modifiers changed from: protected */
    public void updateDarkness(float f) {
        if (this.mUseNavBarColor) {
            int intValue = ((Integer) ArgbEvaluator.getInstance().evaluate(f, Integer.valueOf(this.mLightColor), Integer.valueOf(this.mDarkColor))).intValue();
            boolean z = true;
            Iterator it = this.mAssistInvocationLights.iterator();
            while (it.hasNext()) {
                z &= ((EdgeLight) it.next()).setColor(intValue);
            }
            if (z) {
                invalidate();
            }
        }
    }

    private void renderLight(EdgeLight edgeLight, Canvas canvas) {
        if (edgeLight.getLength() > 0.0f) {
            this.mGuide.strokeSegment(this.mPath, edgeLight.getStart(), edgeLight.getStart() + edgeLight.getLength());
            this.mPaint.setColor(edgeLight.getColor());
            canvas.drawPath(this.mPath, this.mPaint);
        }
    }

    private void attemptRegisterNavBarListener() {
        if (!this.mRegistered) {
            NavigationBarController navigationBarController = (NavigationBarController) Dependency.get(NavigationBarController.class);
            if (navigationBarController != null) {
                NavigationBarFragment defaultNavigationBarFragment = navigationBarController.getDefaultNavigationBarFragment();
                if (defaultNavigationBarFragment != null) {
                    updateDarkness(defaultNavigationBarFragment.getBarTransitions().addDarkIntensityListener(this));
                    this.mRegistered = true;
                }
            }
        }
    }

    private void attemptUnregisterNavBarListener() {
        if (this.mRegistered) {
            NavigationBarController navigationBarController = (NavigationBarController) Dependency.get(NavigationBarController.class);
            if (navigationBarController != null) {
                NavigationBarFragment defaultNavigationBarFragment = navigationBarController.getDefaultNavigationBarFragment();
                if (defaultNavigationBarFragment != null) {
                    defaultNavigationBarFragment.getBarTransitions().removeDarkIntensityListener(this);
                    this.mRegistered = false;
                }
            }
        }
    }
}
