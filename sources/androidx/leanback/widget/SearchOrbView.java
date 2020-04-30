package androidx.leanback.widget;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.core.view.ViewCompat;
import androidx.leanback.R$attr;
import androidx.leanback.R$color;
import androidx.leanback.R$dimen;
import androidx.leanback.R$drawable;
import androidx.leanback.R$fraction;
import androidx.leanback.R$id;
import androidx.leanback.R$integer;
import androidx.leanback.R$layout;
import androidx.leanback.R$styleable;

public class SearchOrbView extends FrameLayout implements OnClickListener {
    private boolean mAttachedToWindow;
    private boolean mColorAnimationEnabled;
    private ValueAnimator mColorAnimator;
    private final ArgbEvaluator mColorEvaluator;
    private Colors mColors;
    private final AnimatorUpdateListener mFocusUpdateListener;
    private final float mFocusedZ;
    private final float mFocusedZoom;
    private ImageView mIcon;
    private Drawable mIconDrawable;
    private OnClickListener mListener;
    private final int mPulseDurationMs;
    private View mRootView;
    private final int mScaleDurationMs;
    private View mSearchOrbView;
    private ValueAnimator mShadowFocusAnimator;
    private final float mUnfocusedZ;
    private final AnimatorUpdateListener mUpdateListener;

    public static class Colors {
        public int brightColor;
        public int color;
        public int iconColor;

        public Colors(int i, int i2, int i3) {
            this.color = i;
            if (i2 == i) {
                i2 = getBrightColor(i);
            }
            this.brightColor = i2;
            this.iconColor = i3;
        }

        public static int getBrightColor(int i) {
            return Color.argb((int) ((((float) Color.alpha(i)) * 0.85f) + 38.25f), (int) ((((float) Color.red(i)) * 0.85f) + 38.25f), (int) ((((float) Color.green(i)) * 0.85f) + 38.25f), (int) ((((float) Color.blue(i)) * 0.85f) + 38.25f));
        }
    }

    /* access modifiers changed from: 0000 */
    public void setSearchOrbZ(float f) {
        View view = this.mSearchOrbView;
        float f2 = this.mUnfocusedZ;
        ViewCompat.setZ(view, f2 + (f * (this.mFocusedZ - f2)));
    }

    public SearchOrbView(Context context) {
        this(context, null);
    }

    public SearchOrbView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R$attr.searchOrbViewStyle);
    }

    public SearchOrbView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mColorEvaluator = new ArgbEvaluator();
        this.mUpdateListener = new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                SearchOrbView.this.setOrbViewColor(((Integer) valueAnimator.getAnimatedValue()).intValue());
            }
        };
        this.mFocusUpdateListener = new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                SearchOrbView.this.setSearchOrbZ(valueAnimator.getAnimatedFraction());
            }
        };
        Resources resources = context.getResources();
        View inflate = ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(getLayoutResourceId(), this, true);
        this.mRootView = inflate;
        this.mSearchOrbView = inflate.findViewById(R$id.search_orb);
        this.mIcon = (ImageView) this.mRootView.findViewById(R$id.icon);
        this.mFocusedZoom = context.getResources().getFraction(R$fraction.lb_search_orb_focused_zoom, 1, 1);
        this.mPulseDurationMs = context.getResources().getInteger(R$integer.lb_search_orb_pulse_duration_ms);
        this.mScaleDurationMs = context.getResources().getInteger(R$integer.lb_search_orb_scale_duration_ms);
        this.mFocusedZ = (float) context.getResources().getDimensionPixelSize(R$dimen.lb_search_orb_focused_z);
        this.mUnfocusedZ = (float) context.getResources().getDimensionPixelSize(R$dimen.lb_search_orb_unfocused_z);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.lbSearchOrbView, i, 0);
        ViewCompat.saveAttributeDataForStyleable(this, context, R$styleable.lbSearchOrbView, attributeSet, obtainStyledAttributes, i, 0);
        Drawable drawable = obtainStyledAttributes.getDrawable(R$styleable.lbSearchOrbView_searchOrbIcon);
        if (drawable == null) {
            drawable = resources.getDrawable(R$drawable.lb_ic_in_app_search);
        }
        setOrbIcon(drawable);
        int color = obtainStyledAttributes.getColor(R$styleable.lbSearchOrbView_searchOrbColor, resources.getColor(R$color.lb_default_search_color));
        setOrbColors(new Colors(color, obtainStyledAttributes.getColor(R$styleable.lbSearchOrbView_searchOrbBrightColor, color), obtainStyledAttributes.getColor(R$styleable.lbSearchOrbView_searchOrbIconColor, 0)));
        obtainStyledAttributes.recycle();
        setFocusable(true);
        setClipChildren(false);
        setOnClickListener(this);
        setSoundEffectsEnabled(false);
        setSearchOrbZ(0.0f);
        ViewCompat.setZ(this.mIcon, this.mFocusedZ);
    }

    /* access modifiers changed from: 0000 */
    public int getLayoutResourceId() {
        return R$layout.lb_search_orb;
    }

    /* access modifiers changed from: 0000 */
    public void scaleOrbViewOnly(float f) {
        this.mSearchOrbView.setScaleX(f);
        this.mSearchOrbView.setScaleY(f);
    }

    /* access modifiers changed from: 0000 */
    public float getFocusedZoom() {
        return this.mFocusedZoom;
    }

    public void onClick(View view) {
        OnClickListener onClickListener = this.mListener;
        if (onClickListener != null) {
            onClickListener.onClick(view);
        }
    }

    private void startShadowFocusAnimation(boolean z, int i) {
        if (this.mShadowFocusAnimator == null) {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
            this.mShadowFocusAnimator = ofFloat;
            ofFloat.addUpdateListener(this.mFocusUpdateListener);
        }
        if (z) {
            this.mShadowFocusAnimator.start();
        } else {
            this.mShadowFocusAnimator.reverse();
        }
        this.mShadowFocusAnimator.setDuration((long) i);
    }

    /* access modifiers changed from: protected */
    public void onFocusChanged(boolean z, int i, Rect rect) {
        super.onFocusChanged(z, i, rect);
        animateOnFocus(z);
    }

    /* access modifiers changed from: 0000 */
    public void animateOnFocus(boolean z) {
        float f = z ? this.mFocusedZoom : 1.0f;
        this.mRootView.animate().scaleX(f).scaleY(f).setDuration((long) this.mScaleDurationMs).start();
        startShadowFocusAnimation(z, this.mScaleDurationMs);
        enableOrbColorAnimation(z);
    }

    public void setOrbIcon(Drawable drawable) {
        this.mIconDrawable = drawable;
        this.mIcon.setImageDrawable(drawable);
    }

    public void setOnOrbClickedListener(OnClickListener onClickListener) {
        this.mListener = onClickListener;
    }

    public void setOrbColors(Colors colors) {
        this.mColors = colors;
        this.mIcon.setColorFilter(colors.iconColor);
        if (this.mColorAnimator == null) {
            setOrbViewColor(this.mColors.color);
        } else {
            enableOrbColorAnimation(true);
        }
    }

    public void enableOrbColorAnimation(boolean z) {
        this.mColorAnimationEnabled = z;
        updateColorAnimator();
    }

    private void updateColorAnimator() {
        ValueAnimator valueAnimator = this.mColorAnimator;
        if (valueAnimator != null) {
            valueAnimator.end();
            this.mColorAnimator = null;
        }
        if (this.mColorAnimationEnabled && this.mAttachedToWindow) {
            ValueAnimator ofObject = ValueAnimator.ofObject(this.mColorEvaluator, new Object[]{Integer.valueOf(this.mColors.color), Integer.valueOf(this.mColors.brightColor), Integer.valueOf(this.mColors.color)});
            this.mColorAnimator = ofObject;
            ofObject.setRepeatCount(-1);
            this.mColorAnimator.setDuration((long) (this.mPulseDurationMs * 2));
            this.mColorAnimator.addUpdateListener(this.mUpdateListener);
            this.mColorAnimator.start();
        }
    }

    /* access modifiers changed from: 0000 */
    public void setOrbViewColor(int i) {
        if (this.mSearchOrbView.getBackground() instanceof GradientDrawable) {
            ((GradientDrawable) this.mSearchOrbView.getBackground()).setColor(i);
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mAttachedToWindow = true;
        updateColorAnimator();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        this.mAttachedToWindow = false;
        updateColorAnimator();
        super.onDetachedFromWindow();
    }
}
