package com.android.systemui.controls;

import android.content.Context;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.TextView;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2011R$id;
import com.android.systemui.C2013R$layout;
import com.android.systemui.Prefs;
import com.android.systemui.recents.TriangleShape;
import kotlin.TypeCastException;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: TooltipManager.kt */
public final class TooltipManager {
    private final View arrowView;
    /* access modifiers changed from: private */
    public final boolean below;
    private final ViewGroup layout;
    private final int maxTimesShown;
    /* access modifiers changed from: private */
    public final String preferenceName;
    private final Function1<Integer, Unit> preferenceStorer;
    private int shown;
    private final TextView textView;

    public TooltipManager(Context context, String str, int i, boolean z) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(str, "preferenceName");
        this.preferenceName = str;
        this.maxTimesShown = i;
        this.below = z;
        this.shown = Prefs.getInt(context, str, 0);
        View inflate = LayoutInflater.from(context).inflate(C2013R$layout.controls_onboarding, null);
        if (inflate != null) {
            this.layout = (ViewGroup) inflate;
            this.preferenceStorer = new TooltipManager$preferenceStorer$1(this, context);
            this.layout.setAlpha(0.0f);
            this.textView = (TextView) this.layout.requireViewById(C2011R$id.onboarding_text);
            this.layout.requireViewById(C2011R$id.dismiss).setOnClickListener(new TooltipManager$$special$$inlined$apply$lambda$1(this));
            View requireViewById = this.layout.requireViewById(C2011R$id.arrow);
            TypedValue typedValue = new TypedValue();
            context.getTheme().resolveAttribute(16843829, typedValue, true);
            int color = context.getResources().getColor(typedValue.resourceId, context.getTheme());
            int dimensionPixelSize = context.getResources().getDimensionPixelSize(C2009R$dimen.recents_onboarding_toast_arrow_corner_radius);
            LayoutParams layoutParams = requireViewById.getLayoutParams();
            ShapeDrawable shapeDrawable = new ShapeDrawable(TriangleShape.create((float) layoutParams.width, (float) layoutParams.height, this.below));
            Paint paint = shapeDrawable.getPaint();
            Intrinsics.checkExpressionValueIsNotNull(paint, "arrowPaint");
            paint.setColor(color);
            paint.setPathEffect(new CornerPathEffect((float) dimensionPixelSize));
            requireViewById.setBackground(shapeDrawable);
            this.arrowView = requireViewById;
            if (!this.below) {
                this.layout.removeView(requireViewById);
                this.layout.addView(this.arrowView);
                View view = this.arrowView;
                Intrinsics.checkExpressionValueIsNotNull(view, "arrowView");
                LayoutParams layoutParams2 = view.getLayoutParams();
                if (layoutParams2 != null) {
                    MarginLayoutParams marginLayoutParams = (MarginLayoutParams) layoutParams2;
                    marginLayoutParams.bottomMargin = marginLayoutParams.topMargin;
                    marginLayoutParams.topMargin = 0;
                    return;
                }
                throw new TypeCastException("null cannot be cast to non-null type android.view.ViewGroup.MarginLayoutParams");
            }
            return;
        }
        throw new TypeCastException("null cannot be cast to non-null type android.view.ViewGroup");
    }

    public /* synthetic */ TooltipManager(Context context, String str, int i, boolean z, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        if ((i2 & 4) != 0) {
            i = 2;
        }
        if ((i2 & 8) != 0) {
            z = true;
        }
        this(context, str, i, z);
    }

    public final ViewGroup getLayout() {
        return this.layout;
    }

    public final void show(int i, int i2, int i3) {
        if (shouldShow()) {
            this.textView.setText(i);
            int i4 = this.shown + 1;
            this.shown = i4;
            this.preferenceStorer.invoke(Integer.valueOf(i4));
            this.layout.post(new TooltipManager$show$1(this, i2, i3));
        }
    }

    public final void hide(boolean z) {
        if (this.layout.getAlpha() != 0.0f) {
            this.layout.post(new TooltipManager$hide$1(this, z));
        }
    }

    private final boolean shouldShow() {
        return this.shown < this.maxTimesShown;
    }
}
