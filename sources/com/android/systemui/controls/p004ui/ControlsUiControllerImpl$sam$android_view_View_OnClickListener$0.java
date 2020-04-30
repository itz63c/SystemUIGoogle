package com.android.systemui.controls.p004ui;

import android.view.View;
import android.view.View.OnClickListener;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

/* renamed from: com.android.systemui.controls.ui.ControlsUiControllerImpl$sam$android_view_View_OnClickListener$0 */
/* compiled from: ControlsUiControllerImpl.kt */
final class ControlsUiControllerImpl$sam$android_view_View_OnClickListener$0 implements OnClickListener {
    private final /* synthetic */ Function1 function;

    ControlsUiControllerImpl$sam$android_view_View_OnClickListener$0(Function1 function1) {
        this.function = function1;
    }

    public final /* synthetic */ void onClick(View view) {
        Intrinsics.checkExpressionValueIsNotNull(this.function.invoke(view), "invoke(...)");
    }
}
