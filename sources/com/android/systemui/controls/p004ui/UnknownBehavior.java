package com.android.systemui.controls.p004ui;

import android.widget.TextView;
import kotlin.jvm.internal.Intrinsics;

/* renamed from: com.android.systemui.controls.ui.UnknownBehavior */
/* compiled from: UnknownBehavior.kt */
public final class UnknownBehavior implements Behavior {
    public ControlViewHolder cvh;

    public void initialize(ControlViewHolder controlViewHolder) {
        Intrinsics.checkParameterIsNotNull(controlViewHolder, "cvh");
        this.cvh = controlViewHolder;
    }

    public void bind(ControlWithState controlWithState) {
        Intrinsics.checkParameterIsNotNull(controlWithState, "cws");
        ControlViewHolder controlViewHolder = this.cvh;
        String str = "cvh";
        if (controlViewHolder != null) {
            TextView status = controlViewHolder.getStatus();
            ControlViewHolder controlViewHolder2 = this.cvh;
            if (controlViewHolder2 != null) {
                status.setText(controlViewHolder2.getContext().getString(17040319));
                ControlViewHolder controlViewHolder3 = this.cvh;
                if (controlViewHolder3 != null) {
                    ControlViewHolder.m29x1a61c355(controlViewHolder3, false, 0, 2, null);
                } else {
                    Intrinsics.throwUninitializedPropertyAccessException(str);
                    throw null;
                }
            } else {
                Intrinsics.throwUninitializedPropertyAccessException(str);
                throw null;
            }
        } else {
            Intrinsics.throwUninitializedPropertyAccessException(str);
            throw null;
        }
    }
}
