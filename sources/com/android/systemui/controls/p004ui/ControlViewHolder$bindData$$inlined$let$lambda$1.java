package com.android.systemui.controls.p004ui;

import android.view.View;
import android.view.View.OnLongClickListener;

/* renamed from: com.android.systemui.controls.ui.ControlViewHolder$bindData$$inlined$let$lambda$1 */
/* compiled from: ControlViewHolder.kt */
final class ControlViewHolder$bindData$$inlined$let$lambda$1 implements OnLongClickListener {
    final /* synthetic */ ControlViewHolder this$0;

    ControlViewHolder$bindData$$inlined$let$lambda$1(ControlViewHolder controlViewHolder) {
        this.this$0 = controlViewHolder;
    }

    public final boolean onLongClick(View view) {
        ControlActionCoordinator.INSTANCE.longPress(this.this$0);
        return true;
    }
}
