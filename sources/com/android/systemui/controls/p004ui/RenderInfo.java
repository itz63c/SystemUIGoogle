package com.android.systemui.controls.p004ui;

import android.content.ComponentName;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.ArrayMap;
import android.util.SparseArray;
import com.android.systemui.C2010R$drawable;
import kotlin.Pair;
import kotlin.jvm.internal.Intrinsics;

/* renamed from: com.android.systemui.controls.ui.RenderInfo */
/* compiled from: RenderInfo.kt */
public final class RenderInfo {
    public static final Companion Companion = new Companion(null);
    /* access modifiers changed from: private */
    public static final ArrayMap<ComponentName, Drawable> appIconMap = new ArrayMap<>();
    /* access modifiers changed from: private */
    public static final SparseArray<Drawable> iconMap = new SparseArray<>();
    private final int enabledBackground;
    private final int foreground;
    private final Drawable icon;

    /* renamed from: com.android.systemui.controls.ui.RenderInfo$Companion */
    /* compiled from: RenderInfo.kt */
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        public static /* synthetic */ RenderInfo lookup$default(Companion companion, Context context, ComponentName componentName, int i, boolean z, int i2, int i3, Object obj) {
            if ((i3 & 16) != 0) {
                i2 = 0;
            }
            return companion.lookup(context, componentName, i, z, i2);
        }

        public final RenderInfo lookup(Context context, ComponentName componentName, int i, boolean z, int i2) {
            Drawable drawable;
            Intrinsics.checkParameterIsNotNull(context, "context");
            Intrinsics.checkParameterIsNotNull(componentName, "componentName");
            Pair pair = (Pair) MapsKt__MapsKt.getValue(RenderInfoKt.deviceColorMap, Integer.valueOf(i));
            int intValue = ((Number) pair.component1()).intValue();
            int intValue2 = ((Number) pair.component2()).intValue();
            if (i2 > 0) {
                i = (i * 1000) + i2;
            }
            int i3 = ((IconState) MapsKt__MapsKt.getValue(RenderInfoKt.deviceIconMap, Integer.valueOf(i))).get(z);
            if (i3 == -1) {
                drawable = (Drawable) RenderInfo.appIconMap.get(componentName);
                if (drawable == null) {
                    drawable = context.getResources().getDrawable(C2010R$drawable.ic_device_unknown_gm2_24px, null);
                    RenderInfo.appIconMap.put(componentName, drawable);
                }
            } else {
                Drawable drawable2 = (Drawable) RenderInfo.iconMap.get(i3);
                if (drawable2 == null) {
                    Drawable drawable3 = context.getResources().getDrawable(i3, null);
                    drawable3.mutate();
                    RenderInfo.iconMap.put(i3, drawable3);
                    drawable = drawable3;
                } else {
                    drawable = drawable2;
                }
            }
            if (drawable != null) {
                return new RenderInfo(drawable, intValue, intValue2);
            }
            Intrinsics.throwNpe();
            throw null;
        }

        public final void registerComponentIcon(ComponentName componentName, Drawable drawable) {
            Intrinsics.checkParameterIsNotNull(componentName, "componentName");
            Intrinsics.checkParameterIsNotNull(drawable, "icon");
            RenderInfo.appIconMap.put(componentName, drawable);
        }

        public final void clearCache() {
            RenderInfo.iconMap.clear();
            RenderInfo.appIconMap.clear();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:8:0x001c, code lost:
        if (r2.enabledBackground == r3.enabledBackground) goto L_0x0021;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean equals(java.lang.Object r3) {
        /*
            r2 = this;
            if (r2 == r3) goto L_0x0021
            boolean r0 = r3 instanceof com.android.systemui.controls.p004ui.RenderInfo
            if (r0 == 0) goto L_0x001f
            com.android.systemui.controls.ui.RenderInfo r3 = (com.android.systemui.controls.p004ui.RenderInfo) r3
            android.graphics.drawable.Drawable r0 = r2.icon
            android.graphics.drawable.Drawable r1 = r3.icon
            boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual(r0, r1)
            if (r0 == 0) goto L_0x001f
            int r0 = r2.foreground
            int r1 = r3.foreground
            if (r0 != r1) goto L_0x001f
            int r2 = r2.enabledBackground
            int r3 = r3.enabledBackground
            if (r2 != r3) goto L_0x001f
            goto L_0x0021
        L_0x001f:
            r2 = 0
            return r2
        L_0x0021:
            r2 = 1
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.controls.p004ui.RenderInfo.equals(java.lang.Object):boolean");
    }

    public int hashCode() {
        Drawable drawable = this.icon;
        return ((((drawable != null ? drawable.hashCode() : 0) * 31) + Integer.hashCode(this.foreground)) * 31) + Integer.hashCode(this.enabledBackground);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("RenderInfo(icon=");
        sb.append(this.icon);
        sb.append(", foreground=");
        sb.append(this.foreground);
        sb.append(", enabledBackground=");
        sb.append(this.enabledBackground);
        sb.append(")");
        return sb.toString();
    }

    public RenderInfo(Drawable drawable, int i, int i2) {
        Intrinsics.checkParameterIsNotNull(drawable, "icon");
        this.icon = drawable;
        this.foreground = i;
        this.enabledBackground = i2;
    }

    public final Drawable getIcon() {
        return this.icon;
    }

    public final int getForeground() {
        return this.foreground;
    }

    public final int getEnabledBackground() {
        return this.enabledBackground;
    }
}
