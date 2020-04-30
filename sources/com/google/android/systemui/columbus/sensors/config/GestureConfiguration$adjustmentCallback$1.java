package com.google.android.systemui.columbus.sensors.config;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* compiled from: GestureConfiguration.kt */
final class GestureConfiguration$adjustmentCallback$1 extends Lambda implements Function1<Adjustment, Unit> {
    final /* synthetic */ GestureConfiguration this$0;

    GestureConfiguration$adjustmentCallback$1(GestureConfiguration gestureConfiguration) {
        this.this$0 = gestureConfiguration;
        super(1);
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj) {
        invoke((Adjustment) obj);
        return Unit.INSTANCE;
    }

    public final void invoke(Adjustment adjustment) {
        Intrinsics.checkParameterIsNotNull(adjustment, "it");
        this.this$0.onSensitivityChanged();
    }
}
