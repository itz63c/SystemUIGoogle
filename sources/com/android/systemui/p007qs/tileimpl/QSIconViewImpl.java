package com.android.systemui.p007qs.tileimpl;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.Animatable2.AnimationCallback;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import com.android.systemui.C2008R$color;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2011R$id;
import com.android.systemui.p007qs.AlphaControlledSignalTileView.AlphaControlledSlashImageView;
import com.android.systemui.plugins.p006qs.QSIconView;
import com.android.systemui.plugins.p006qs.QSTile.Icon;
import com.android.systemui.plugins.p006qs.QSTile.State;
import java.util.Objects;
import java.util.function.Supplier;

/* renamed from: com.android.systemui.qs.tileimpl.QSIconViewImpl */
public class QSIconViewImpl extends QSIconView {
    private boolean mAnimationEnabled = true;
    protected final View mIcon;
    protected final int mIconSizePx;
    private Icon mLastIcon;
    private int mState = -1;
    private int mTint;

    /* access modifiers changed from: protected */
    public int getIconMeasureMode() {
        return 1073741824;
    }

    public QSIconViewImpl(Context context) {
        super(context);
        this.mIconSizePx = context.getResources().getDimensionPixelSize(C2009R$dimen.qs_tile_icon_size);
        View createIcon = createIcon();
        this.mIcon = createIcon;
        addView(createIcon);
    }

    public void disableAnimation() {
        this.mAnimationEnabled = false;
    }

    public View getIconView() {
        return this.mIcon;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int size = MeasureSpec.getSize(i);
        this.mIcon.measure(MeasureSpec.makeMeasureSpec(size, getIconMeasureMode()), exactly(this.mIconSizePx));
        setMeasuredDimension(size, this.mIcon.getMeasuredHeight());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append('[');
        StringBuilder sb2 = new StringBuilder();
        sb2.append("state=");
        sb2.append(this.mState);
        sb.append(sb2.toString());
        StringBuilder sb3 = new StringBuilder();
        sb3.append(", tint=");
        sb3.append(this.mTint);
        sb.append(sb3.toString());
        if (this.mLastIcon != null) {
            StringBuilder sb4 = new StringBuilder();
            sb4.append(", lastIcon=");
            sb4.append(this.mLastIcon.toString());
            sb.append(sb4.toString());
        }
        sb.append("]");
        return sb.toString();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        layout(this.mIcon, (getMeasuredWidth() - this.mIcon.getMeasuredWidth()) / 2, 0);
    }

    public void setIcon(State state, boolean z) {
        setIcon((ImageView) this.mIcon, state, z);
    }

    /* access modifiers changed from: protected */
    /* renamed from: updateIcon */
    public void lambda$setIcon$0(ImageView imageView, State state, boolean z) {
        Supplier<Icon> supplier = state.iconSupplier;
        Icon icon = supplier != null ? (Icon) supplier.get() : state.icon;
        if (!Objects.equals(icon, imageView.getTag(C2011R$id.qs_icon_tag)) || !Objects.equals(state.slash, imageView.getTag(C2011R$id.qs_slash_tag))) {
            boolean z2 = z && shouldAnimate(imageView);
            this.mLastIcon = icon;
            Drawable drawable = icon != null ? z2 ? icon.getDrawable(this.mContext) : icon.getInvisibleDrawable(this.mContext) : null;
            int padding = icon != null ? icon.getPadding() : 0;
            if (drawable != null) {
                drawable.setAutoMirrored(false);
                drawable.setLayoutDirection(getLayoutDirection());
            }
            if (imageView instanceof SlashImageView) {
                SlashImageView slashImageView = (SlashImageView) imageView;
                slashImageView.setAnimationEnabled(z2);
                slashImageView.setState(null, drawable);
            } else {
                imageView.setImageDrawable(drawable);
            }
            imageView.setTag(C2011R$id.qs_icon_tag, icon);
            imageView.setTag(C2011R$id.qs_slash_tag, state.slash);
            imageView.setPadding(0, padding, 0, padding);
            if (drawable instanceof Animatable2) {
                final Animatable2 animatable2 = (Animatable2) drawable;
                animatable2.start();
                if (state.isTransient) {
                    animatable2.registerAnimationCallback(new AnimationCallback(this) {
                        public void onAnimationEnd(Drawable drawable) {
                            animatable2.start();
                        }
                    });
                }
            }
        }
    }

    private boolean shouldAnimate(ImageView imageView) {
        return this.mAnimationEnabled && imageView.isShown() && imageView.getDrawable() != null;
    }

    /* access modifiers changed from: protected */
    public void setIcon(ImageView imageView, State state, boolean z) {
        if (state.disabledByPolicy) {
            imageView.setColorFilter(getContext().getColor(C2008R$color.qs_tile_disabled_color));
        } else {
            imageView.clearColorFilter();
        }
        int i = state.state;
        if (i != this.mState) {
            int color = getColor(i);
            this.mState = state.state;
            if (this.mTint == 0 || !z || !shouldAnimate(imageView)) {
                if (imageView instanceof AlphaControlledSlashImageView) {
                    ((AlphaControlledSlashImageView) imageView).setFinalImageTintList(ColorStateList.valueOf(color));
                } else {
                    setTint(imageView, color);
                }
                this.mTint = color;
                lambda$setIcon$0(imageView, state, z);
                return;
            }
            animateGrayScale(this.mTint, color, imageView, new Runnable(imageView, state, z) {
                public final /* synthetic */ ImageView f$1;
                public final /* synthetic */ State f$2;
                public final /* synthetic */ boolean f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    QSIconViewImpl.this.lambda$setIcon$0$QSIconViewImpl(this.f$1, this.f$2, this.f$3);
                }
            });
            this.mTint = color;
            return;
        }
        lambda$setIcon$0(imageView, state, z);
    }

    /* access modifiers changed from: protected */
    public int getColor(int i) {
        return QSTileImpl.getColorForState(getContext(), i);
    }

    private void animateGrayScale(int i, int i2, ImageView imageView, final Runnable runnable) {
        if (imageView instanceof AlphaControlledSlashImageView) {
            ((AlphaControlledSlashImageView) imageView).setFinalImageTintList(ColorStateList.valueOf(i2));
        }
        if (!this.mAnimationEnabled || !ValueAnimator.areAnimatorsEnabled()) {
            setTint(imageView, i2);
            runnable.run();
            return;
        }
        float alpha = (float) Color.alpha(i);
        float alpha2 = (float) Color.alpha(i2);
        float red = (float) Color.red(i);
        float red2 = (float) Color.red(i2);
        ValueAnimator ofFloat = ValueAnimator.ofFloat(new float[]{0.0f, 1.0f});
        ofFloat.setDuration(350);
        $$Lambda$QSIconViewImpl$CeqSBPdIhNYTow_6QM6a9ZwQyb8 r1 = new AnimatorUpdateListener(alpha, alpha2, red, red2, imageView) {
            public final /* synthetic */ float f$0;
            public final /* synthetic */ float f$1;
            public final /* synthetic */ float f$2;
            public final /* synthetic */ float f$3;
            public final /* synthetic */ ImageView f$4;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                QSIconViewImpl.lambda$animateGrayScale$1(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, valueAnimator);
            }
        };
        ofFloat.addUpdateListener(r1);
        ofFloat.addListener(new AnimatorListenerAdapter(this) {
            public void onAnimationEnd(Animator animator) {
                runnable.run();
            }
        });
        ofFloat.start();
    }

    static /* synthetic */ void lambda$animateGrayScale$1(float f, float f2, float f3, float f4, ImageView imageView, ValueAnimator valueAnimator) {
        float animatedFraction = valueAnimator.getAnimatedFraction();
        int i = (int) (f3 + ((f4 - f3) * animatedFraction));
        setTint(imageView, Color.argb((int) (f + ((f2 - f) * animatedFraction)), i, i, i));
    }

    public static void setTint(ImageView imageView, int i) {
        imageView.setImageTintList(ColorStateList.valueOf(i));
    }

    /* access modifiers changed from: protected */
    public View createIcon() {
        SlashImageView slashImageView = new SlashImageView(this.mContext);
        slashImageView.setId(16908294);
        slashImageView.setScaleType(ScaleType.FIT_CENTER);
        return slashImageView;
    }

    /* access modifiers changed from: protected */
    public final int exactly(int i) {
        return MeasureSpec.makeMeasureSpec(i, 1073741824);
    }

    /* access modifiers changed from: protected */
    public final void layout(View view, int i, int i2) {
        view.layout(i, i2, view.getMeasuredWidth() + i, view.getMeasuredHeight() + i2);
    }
}
