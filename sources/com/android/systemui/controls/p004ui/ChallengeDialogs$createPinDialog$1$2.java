package com.android.systemui.controls.p004ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import com.android.systemui.C2011R$id;
import kotlin.TypeCastException;

/* renamed from: com.android.systemui.controls.ui.ChallengeDialogs$createPinDialog$1$2 */
/* compiled from: ChallengeDialogs.kt */
final class ChallengeDialogs$createPinDialog$1$2 implements OnShowListener {
    final /* synthetic */ AlertDialog $this_apply;

    ChallengeDialogs$createPinDialog$1$2(AlertDialog alertDialog) {
        this.$this_apply = alertDialog;
    }

    public final void onShow(DialogInterface dialogInterface) {
        final EditText editText = (EditText) this.$this_apply.requireViewById(C2011R$id.controls_pin_input);
        ((CheckBox) this.$this_apply.requireViewById(C2011R$id.controls_pin_use_alpha)).setOnClickListener(new OnClickListener() {
            public final void onClick(View view) {
                if (view == null) {
                    throw new TypeCastException("null cannot be cast to non-null type android.widget.CheckBox");
                } else if (((CheckBox) view).isChecked()) {
                    editText.setInputType(129);
                } else {
                    editText.setInputType(18);
                }
            }
        });
        editText.requestFocus();
    }
}
