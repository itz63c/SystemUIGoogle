package com.android.systemui.controls.p004ui;

import com.android.systemui.C2010R$drawable;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Lambda;

/* renamed from: com.android.systemui.controls.ui.RenderInfoKt$deviceIconMap$1 */
/* compiled from: RenderInfo.kt */
final class RenderInfoKt$deviceIconMap$1 extends Lambda implements Function1<Integer, IconState> {
    public static final RenderInfoKt$deviceIconMap$1 INSTANCE = new RenderInfoKt$deviceIconMap$1();

    RenderInfoKt$deviceIconMap$1() {
        super(1);
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj) {
        return invoke(((Number) obj).intValue());
    }

    public final IconState invoke(int i) {
        int i2 = C2010R$drawable.ic_device_unknown_gm2_24px;
        return new IconState(i2, i2);
    }
}
