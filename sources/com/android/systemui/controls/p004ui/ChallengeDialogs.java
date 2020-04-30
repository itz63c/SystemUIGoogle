package com.android.systemui.controls.p004ui;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.service.controls.actions.BooleanAction;
import android.service.controls.actions.CommandAction;
import android.service.controls.actions.ControlAction;
import android.service.controls.actions.FloatAction;
import android.service.controls.actions.ModeAction;
import android.util.Log;
import android.view.Window;
import com.android.systemui.C2013R$layout;
import com.android.systemui.C2017R$string;
import kotlin.jvm.internal.Intrinsics;

/* renamed from: com.android.systemui.controls.ui.ChallengeDialogs */
/* compiled from: ChallengeDialogs.kt */
public final class ChallengeDialogs {
    public static final ChallengeDialogs INSTANCE = new ChallengeDialogs();

    private ChallengeDialogs() {
    }

    public final Dialog createPinDialog(ControlViewHolder controlViewHolder) {
        Intrinsics.checkParameterIsNotNull(controlViewHolder, "cvh");
        ControlAction lastAction = controlViewHolder.getLastAction();
        if (lastAction == null) {
            Log.e("ControlsUiController", "PIN Dialog attempted but no last action is set. Will not show");
            return null;
        }
        Builder builder = new Builder(controlViewHolder.getContext(), 16974545);
        builder.setTitle(C2017R$string.controls_pin_verify);
        builder.setView(C2013R$layout.controls_dialog_pin);
        builder.setPositiveButton(17039370, new ChallengeDialogs$createPinDialog$$inlined$apply$lambda$1(controlViewHolder, lastAction));
        builder.setNegativeButton(17039360, ChallengeDialogs$createPinDialog$builder$1$2.INSTANCE);
        AlertDialog create = builder.create();
        Window window = create.getWindow();
        window.setType(2020);
        window.setSoftInputMode(4);
        create.setOnShowListener(new ChallengeDialogs$createPinDialog$1$2(create));
        return create;
    }

    /* access modifiers changed from: private */
    public final ControlAction addChallengeValue(ControlAction controlAction, String str) {
        String templateId = controlAction.getTemplateId();
        if (controlAction instanceof BooleanAction) {
            return new BooleanAction(templateId, ((BooleanAction) controlAction).getNewState(), str);
        }
        if (controlAction instanceof FloatAction) {
            return new FloatAction(templateId, ((FloatAction) controlAction).getNewValue(), str);
        }
        if (controlAction instanceof CommandAction) {
            return new CommandAction(templateId, str);
        }
        if (controlAction instanceof ModeAction) {
            return new ModeAction(templateId, ((ModeAction) controlAction).getNewMode(), str);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("'action' is not a known type: ");
        sb.append(controlAction);
        throw new IllegalStateException(sb.toString());
    }
}
