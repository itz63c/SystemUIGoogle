package com.android.systemui.controls.management;

import android.view.View;
import android.view.View.OnLayoutChangeListener;
import com.android.systemui.C2017R$string;
import com.android.systemui.controls.TooltipManager;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlsFavoritingActivity.kt */
public final class ControlsFavoritingActivity$bindViews$$inlined$apply$lambda$1 implements OnLayoutChangeListener {
    final /* synthetic */ ControlsFavoritingActivity this$0;

    ControlsFavoritingActivity$bindViews$$inlined$apply$lambda$1(ControlsFavoritingActivity controlsFavoritingActivity) {
        this.this$0 = controlsFavoritingActivity;
    }

    public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        Intrinsics.checkParameterIsNotNull(view, "v");
        if (view.getVisibility() == 0 && this.this$0.mTooltipManager != null) {
            int[] iArr = new int[2];
            view.getLocationOnScreen(iArr);
            int i9 = iArr[0] + ((i3 - i) / 2);
            int i10 = (iArr[1] + i4) - i2;
            TooltipManager access$getMTooltipManager$p = this.this$0.mTooltipManager;
            if (access$getMTooltipManager$p != null) {
                access$getMTooltipManager$p.show(C2017R$string.controls_structure_tooltip, i9, i10);
            }
        }
    }
}
