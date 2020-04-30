package com.android.systemui;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.view.ContextThemeWrapper;
import com.android.settingslib.Utils;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: DualToneHandler.kt */
public final class DualToneHandler {
    private Color darkColor;
    private Color lightColor;

    /* compiled from: DualToneHandler.kt */
    private static final class Color {
        private final int background;
        private final int fill;
        private final int single;

        /* JADX WARNING: Code restructure failed: missing block: B:8:0x0018, code lost:
            if (r2.fill == r3.fill) goto L_0x001d;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean equals(java.lang.Object r3) {
            /*
                r2 = this;
                if (r2 == r3) goto L_0x001d
                boolean r0 = r3 instanceof com.android.systemui.DualToneHandler.Color
                if (r0 == 0) goto L_0x001b
                com.android.systemui.DualToneHandler$Color r3 = (com.android.systemui.DualToneHandler.Color) r3
                int r0 = r2.single
                int r1 = r3.single
                if (r0 != r1) goto L_0x001b
                int r0 = r2.background
                int r1 = r3.background
                if (r0 != r1) goto L_0x001b
                int r2 = r2.fill
                int r3 = r3.fill
                if (r2 != r3) goto L_0x001b
                goto L_0x001d
            L_0x001b:
                r2 = 0
                return r2
            L_0x001d:
                r2 = 1
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.DualToneHandler.Color.equals(java.lang.Object):boolean");
        }

        public int hashCode() {
            return (((Integer.hashCode(this.single) * 31) + Integer.hashCode(this.background)) * 31) + Integer.hashCode(this.fill);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Color(single=");
            sb.append(this.single);
            sb.append(", background=");
            sb.append(this.background);
            sb.append(", fill=");
            sb.append(this.fill);
            sb.append(")");
            return sb.toString();
        }

        public Color(int i, int i2, int i3) {
            this.single = i;
            this.background = i2;
            this.fill = i3;
        }

        public final int getBackground() {
            return this.background;
        }

        public final int getFill() {
            return this.fill;
        }

        public final int getSingle() {
            return this.single;
        }
    }

    public DualToneHandler(Context context) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        setColorsFromContext(context);
    }

    public final void setColorsFromContext(Context context) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(context, Utils.getThemeAttr(context, C2006R$attr.darkIconTheme));
        ContextThemeWrapper contextThemeWrapper2 = new ContextThemeWrapper(context, Utils.getThemeAttr(context, C2006R$attr.lightIconTheme));
        this.darkColor = new Color(Utils.getColorAttrDefaultColor(contextThemeWrapper, C2006R$attr.singleToneColor), Utils.getColorAttrDefaultColor(contextThemeWrapper, C2006R$attr.backgroundColor), Utils.getColorAttrDefaultColor(contextThemeWrapper, C2006R$attr.fillColor));
        this.lightColor = new Color(Utils.getColorAttrDefaultColor(contextThemeWrapper2, C2006R$attr.singleToneColor), Utils.getColorAttrDefaultColor(contextThemeWrapper2, C2006R$attr.backgroundColor), Utils.getColorAttrDefaultColor(contextThemeWrapper2, C2006R$attr.fillColor));
    }

    private final int getColorForDarkIntensity(float f, int i, int i2) {
        Object evaluate = ArgbEvaluator.getInstance().evaluate(f, Integer.valueOf(i), Integer.valueOf(i2));
        if (evaluate != null) {
            return ((Integer) evaluate).intValue();
        }
        throw new TypeCastException("null cannot be cast to non-null type kotlin.Int");
    }

    public final int getSingleColor(float f) {
        Color color = this.lightColor;
        if (color != null) {
            int single = color.getSingle();
            Color color2 = this.darkColor;
            if (color2 != null) {
                return getColorForDarkIntensity(f, single, color2.getSingle());
            }
            Intrinsics.throwUninitializedPropertyAccessException("darkColor");
            throw null;
        }
        Intrinsics.throwUninitializedPropertyAccessException("lightColor");
        throw null;
    }

    public final int getBackgroundColor(float f) {
        Color color = this.lightColor;
        if (color != null) {
            int background = color.getBackground();
            Color color2 = this.darkColor;
            if (color2 != null) {
                return getColorForDarkIntensity(f, background, color2.getBackground());
            }
            Intrinsics.throwUninitializedPropertyAccessException("darkColor");
            throw null;
        }
        Intrinsics.throwUninitializedPropertyAccessException("lightColor");
        throw null;
    }

    public final int getFillColor(float f) {
        Color color = this.lightColor;
        if (color != null) {
            int fill = color.getFill();
            Color color2 = this.darkColor;
            if (color2 != null) {
                return getColorForDarkIntensity(f, fill, color2.getFill());
            }
            Intrinsics.throwUninitializedPropertyAccessException("darkColor");
            throw null;
        }
        Intrinsics.throwUninitializedPropertyAccessException("lightColor");
        throw null;
    }
}
