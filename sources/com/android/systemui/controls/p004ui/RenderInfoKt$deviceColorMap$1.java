package com.android.systemui.controls.p004ui;

import com.android.systemui.C2008R$color;
import kotlin.Pair;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Lambda;

/* renamed from: com.android.systemui.controls.ui.RenderInfoKt$deviceColorMap$1 */
/* compiled from: RenderInfo.kt */
final class RenderInfoKt$deviceColorMap$1 extends Lambda implements Function1<Integer, Pair<? extends Integer, ? extends Integer>> {
    public static final RenderInfoKt$deviceColorMap$1 INSTANCE = new RenderInfoKt$deviceColorMap$1();

    RenderInfoKt$deviceColorMap$1() {
        super(1);
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj) {
        return invoke(((Number) obj).intValue());
    }

    public final Pair<Integer, Integer> invoke(int i) {
        return new Pair<>(Integer.valueOf(C2008R$color.control_foreground), Integer.valueOf(C2008R$color.control_enabled_default_background));
    }
}
