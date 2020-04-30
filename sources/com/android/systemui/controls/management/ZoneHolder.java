package com.android.systemui.controls.management;

import android.view.View;
import android.widget.TextView;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlAdapter.kt */
final class ZoneHolder extends Holder {
    private final TextView zone;

    public ZoneHolder(View view) {
        Intrinsics.checkParameterIsNotNull(view, "view");
        super(view, null);
        View view2 = this.itemView;
        if (view2 != null) {
            this.zone = (TextView) view2;
            return;
        }
        throw new TypeCastException("null cannot be cast to non-null type android.widget.TextView");
    }

    public void bindData(ElementWrapper elementWrapper) {
        Intrinsics.checkParameterIsNotNull(elementWrapper, "wrapper");
        this.zone.setText(((ZoneNameWrapper) elementWrapper).getZoneName());
    }
}
