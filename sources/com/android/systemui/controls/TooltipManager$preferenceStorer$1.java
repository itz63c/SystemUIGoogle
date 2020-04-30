package com.android.systemui.controls;

import android.content.Context;
import com.android.systemui.Prefs;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Lambda;

/* compiled from: TooltipManager.kt */
final class TooltipManager$preferenceStorer$1 extends Lambda implements Function1<Integer, Unit> {
    final /* synthetic */ Context $context;
    final /* synthetic */ TooltipManager this$0;

    TooltipManager$preferenceStorer$1(TooltipManager tooltipManager, Context context) {
        this.this$0 = tooltipManager;
        this.$context = context;
        super(1);
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj) {
        invoke(((Number) obj).intValue());
        return Unit.INSTANCE;
    }

    public final void invoke(int i) {
        Prefs.putInt(this.$context, this.this$0.preferenceName, i);
    }
}
