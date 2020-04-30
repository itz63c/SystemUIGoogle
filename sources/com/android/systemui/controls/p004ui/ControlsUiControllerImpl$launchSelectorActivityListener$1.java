package com.android.systemui.controls.p004ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import com.android.systemui.controls.management.ControlsProviderSelectorActivity;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* renamed from: com.android.systemui.controls.ui.ControlsUiControllerImpl$launchSelectorActivityListener$1 */
/* compiled from: ControlsUiControllerImpl.kt */
final class ControlsUiControllerImpl$launchSelectorActivityListener$1 extends Lambda implements Function1<View, Unit> {
    final /* synthetic */ Context $context;

    ControlsUiControllerImpl$launchSelectorActivityListener$1(Context context) {
        this.$context = context;
        super(1);
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj) {
        invoke((View) obj);
        return Unit.INSTANCE;
    }

    public final void invoke(View view) {
        Intrinsics.checkParameterIsNotNull(view, "<anonymous parameter 0>");
        this.$context.sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(this.$context, ControlsProviderSelectorActivity.class));
        intent.addFlags(335544320);
        this.$context.startActivity(intent);
    }
}
