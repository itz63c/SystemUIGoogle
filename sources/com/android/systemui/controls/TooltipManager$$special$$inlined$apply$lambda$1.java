package com.android.systemui.controls;

import android.view.View;
import android.view.View.OnClickListener;

/* compiled from: TooltipManager.kt */
final class TooltipManager$$special$$inlined$apply$lambda$1 implements OnClickListener {
    final /* synthetic */ TooltipManager this$0;

    TooltipManager$$special$$inlined$apply$lambda$1(TooltipManager tooltipManager) {
        this.this$0 = tooltipManager;
    }

    public final void onClick(View view) {
        this.this$0.hide(true);
    }
}
