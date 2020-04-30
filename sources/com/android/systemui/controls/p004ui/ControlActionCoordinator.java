package com.android.systemui.controls.p004ui;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings.Secure;
import android.service.controls.Control;
import android.service.controls.actions.BooleanAction;
import android.service.controls.actions.CommandAction;
import android.util.Log;
import kotlin.jvm.internal.Intrinsics;

/* renamed from: com.android.systemui.controls.ui.ControlActionCoordinator */
/* compiled from: ControlActionCoordinator.kt */
public final class ControlActionCoordinator {
    public static final ControlActionCoordinator INSTANCE = new ControlActionCoordinator();
    private static Boolean useDetailDialog;

    private ControlActionCoordinator() {
    }

    public final void toggle(ControlViewHolder controlViewHolder, String str, boolean z) {
        Intrinsics.checkParameterIsNotNull(controlViewHolder, "cvh");
        Intrinsics.checkParameterIsNotNull(str, "templateId");
        controlViewHolder.action(new BooleanAction(str, !z));
        controlViewHolder.getClipLayer().setLevel(z ? 0 : 10000);
    }

    public final void touch(ControlViewHolder controlViewHolder, String str) {
        Intrinsics.checkParameterIsNotNull(controlViewHolder, "cvh");
        Intrinsics.checkParameterIsNotNull(str, "templateId");
        controlViewHolder.action(new CommandAction(str));
    }

    public final void longPress(ControlViewHolder controlViewHolder) {
        Intrinsics.checkParameterIsNotNull(controlViewHolder, "cvh");
        Control control = controlViewHolder.getCws().getControl();
        if (control != null) {
            if (useDetailDialog == null) {
                useDetailDialog = Boolean.valueOf(Secure.getInt(controlViewHolder.getContext().getContentResolver(), "systemui.controls_use_detail_panel", 0) != 0);
            }
            try {
                controlViewHolder.getLayout().performHapticFeedback(0);
                Boolean bool = useDetailDialog;
                if (bool == null) {
                    Intrinsics.throwNpe();
                    throw null;
                } else if (bool.booleanValue()) {
                    Context context = controlViewHolder.getContext();
                    PendingIntent appIntent = control.getAppIntent();
                    Intrinsics.checkExpressionValueIsNotNull(appIntent, "it.getAppIntent()");
                    new DetailDialog(context, appIntent).show();
                } else {
                    control.getAppIntent().send();
                    controlViewHolder.getContext().sendBroadcast(new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
                }
            } catch (CanceledException e) {
                Log.e("ControlsUiController", "Error sending pending intent", e);
                controlViewHolder.setTransientStatus("Error opening application");
            }
        }
    }
}
