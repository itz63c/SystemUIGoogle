package com.android.systemui.controls.management;

import android.view.View;
import android.view.View.OnClickListener;

/* compiled from: ControlAdapter.kt */
final class ControlHolder$bindData$2 implements OnClickListener {
    final /* synthetic */ ControlHolder this$0;

    ControlHolder$bindData$2(ControlHolder controlHolder) {
        this.this$0 = controlHolder;
    }

    public final void onClick(View view) {
        this.this$0.favorite.performClick();
    }
}
