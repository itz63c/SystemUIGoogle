package com.android.systemui.controls.management;

import com.android.systemui.controls.TooltipManager;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Lambda;

/* compiled from: ControlsFavoritingActivity.kt */
final class ControlsFavoritingActivity$bindViews$$inlined$apply$lambda$2 extends Lambda implements Function1<Integer, Unit> {
    final /* synthetic */ ControlsFavoritingActivity this$0;

    ControlsFavoritingActivity$bindViews$$inlined$apply$lambda$2(ControlsFavoritingActivity controlsFavoritingActivity) {
        this.this$0 = controlsFavoritingActivity;
        super(1);
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj) {
        invoke(((Number) obj).intValue());
        return Unit.INSTANCE;
    }

    public final void invoke(int i) {
        if (i != 0) {
            TooltipManager access$getMTooltipManager$p = this.this$0.mTooltipManager;
            if (access$getMTooltipManager$p != null) {
                access$getMTooltipManager$p.hide(true);
            }
        }
    }
}
