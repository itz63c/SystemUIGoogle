package com.android.systemui.controls.p004ui;

import android.content.ComponentName;
import android.service.controls.Control;

/* renamed from: com.android.systemui.controls.ui.ControlsUiControllerImpl$onRefreshState$$inlined$forEach$lambda$1 */
/* compiled from: ControlsUiControllerImpl.kt */
final class C0817x27f1e8ba implements Runnable {
    final /* synthetic */ ControlWithState $cws;
    final /* synthetic */ ControlKey $key;
    final /* synthetic */ ControlsUiControllerImpl this$0;

    C0817x27f1e8ba(ControlKey controlKey, ControlWithState controlWithState, Control control, ControlsUiControllerImpl controlsUiControllerImpl, ComponentName componentName) {
        this.$key = controlKey;
        this.$cws = controlWithState;
        this.this$0 = controlsUiControllerImpl;
    }

    public final void run() {
        ControlViewHolder controlViewHolder = (ControlViewHolder) this.this$0.controlViewsById.get(this.$key);
        if (controlViewHolder != null) {
            controlViewHolder.bindData(this.$cws);
        }
    }
}
