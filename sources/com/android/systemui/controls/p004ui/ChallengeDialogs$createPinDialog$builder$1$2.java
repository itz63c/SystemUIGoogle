package com.android.systemui.controls.p004ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* renamed from: com.android.systemui.controls.ui.ChallengeDialogs$createPinDialog$builder$1$2 */
/* compiled from: ChallengeDialogs.kt */
final class ChallengeDialogs$createPinDialog$builder$1$2 implements OnClickListener {
    public static final ChallengeDialogs$createPinDialog$builder$1$2 INSTANCE = new ChallengeDialogs$createPinDialog$builder$1$2();

    ChallengeDialogs$createPinDialog$builder$1$2() {
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        dialogInterface.cancel();
    }
}
