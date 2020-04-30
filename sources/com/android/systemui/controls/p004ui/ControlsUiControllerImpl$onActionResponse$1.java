package com.android.systemui.controls.p004ui;

import android.app.Dialog;

/* renamed from: com.android.systemui.controls.ui.ControlsUiControllerImpl$onActionResponse$1 */
/* compiled from: ControlsUiControllerImpl.kt */
final class ControlsUiControllerImpl$onActionResponse$1 implements Runnable {
    final /* synthetic */ ControlKey $key;
    final /* synthetic */ int $response;
    final /* synthetic */ ControlsUiControllerImpl this$0;

    ControlsUiControllerImpl$onActionResponse$1(ControlsUiControllerImpl controlsUiControllerImpl, ControlKey controlKey, int i) {
        this.this$0 = controlsUiControllerImpl;
        this.$key = controlKey;
        this.$response = i;
    }

    public final void run() {
        ControlViewHolder controlViewHolder = (ControlViewHolder) this.this$0.controlViewsById.get(this.$key);
        if (controlViewHolder != null) {
            int i = this.$response;
            if (i != 4) {
                controlViewHolder.actionResponse(i);
                return;
            }
            this.this$0.activeDialog = ChallengeDialogs.INSTANCE.createPinDialog(controlViewHolder);
            Dialog access$getActiveDialog$p = this.this$0.activeDialog;
            if (access$getActiveDialog$p != null) {
                access$getActiveDialog$p.show();
            }
        }
    }
}
