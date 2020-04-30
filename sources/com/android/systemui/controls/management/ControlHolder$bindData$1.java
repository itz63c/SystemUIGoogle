package com.android.systemui.controls.management;

import android.view.View;
import android.view.View.OnClickListener;
import com.android.systemui.controls.ControlStatus;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlAdapter.kt */
final class ControlHolder$bindData$1 implements OnClickListener {
    final /* synthetic */ ControlStatus $data;
    final /* synthetic */ ControlHolder this$0;

    ControlHolder$bindData$1(ControlHolder controlHolder, ControlStatus controlStatus) {
        this.this$0 = controlHolder;
        this.$data = controlStatus;
    }

    public final void onClick(View view) {
        Function2 favoriteCallback = this.this$0.getFavoriteCallback();
        String controlId = this.$data.getControl().getControlId();
        Intrinsics.checkExpressionValueIsNotNull(controlId, "data.control.controlId");
        favoriteCallback.invoke(controlId, Boolean.valueOf(this.this$0.favorite.isChecked()));
    }
}
