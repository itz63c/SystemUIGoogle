package com.google.android.systemui.columbus.actions;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: DeskClockAction.kt */
public final class DeskClockAction$alertReceiver$1 extends BroadcastReceiver {
    final /* synthetic */ DeskClockAction this$0;

    DeskClockAction$alertReceiver$1(DeskClockAction deskClockAction) {
        this.this$0 = deskClockAction;
    }

    public void onReceive(Context context, Intent intent) {
        String str = null;
        if (Intrinsics.areEqual(intent != null ? intent.getAction() : null, (Object) this.this$0.getAlertAction())) {
            this.this$0.alertFiring = true;
        } else {
            if (intent != null) {
                str = intent.getAction();
            }
            if (Intrinsics.areEqual((Object) str, (Object) this.this$0.getDoneAction())) {
                this.this$0.alertFiring = false;
            }
        }
        this.this$0.notifyListener();
    }
}
