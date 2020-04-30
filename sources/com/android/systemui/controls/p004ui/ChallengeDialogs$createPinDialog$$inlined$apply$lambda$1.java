package com.android.systemui.controls.p004ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.service.controls.actions.ControlAction;
import android.widget.EditText;
import com.android.systemui.C2011R$id;

/* renamed from: com.android.systemui.controls.ui.ChallengeDialogs$createPinDialog$$inlined$apply$lambda$1 */
/* compiled from: ChallengeDialogs.kt */
final class ChallengeDialogs$createPinDialog$$inlined$apply$lambda$1 implements OnClickListener {
    final /* synthetic */ ControlViewHolder $cvh$inlined;
    final /* synthetic */ ControlAction $lastAction$inlined;

    ChallengeDialogs$createPinDialog$$inlined$apply$lambda$1(ControlViewHolder controlViewHolder, ControlAction controlAction) {
        this.$cvh$inlined = controlViewHolder;
        this.$lastAction$inlined = controlAction;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        if (dialogInterface instanceof Dialog) {
            Dialog dialog = (Dialog) dialogInterface;
            dialog.requireViewById(C2011R$id.controls_pin_input);
            this.$cvh$inlined.action(ChallengeDialogs.INSTANCE.addChallengeValue(this.$lastAction$inlined, ((EditText) dialog.requireViewById(C2011R$id.controls_pin_input)).getText().toString()));
            dialogInterface.dismiss();
        }
    }
}
