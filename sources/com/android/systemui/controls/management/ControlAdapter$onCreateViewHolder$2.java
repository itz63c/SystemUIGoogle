package com.android.systemui.controls.management;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Lambda;

/* compiled from: ControlAdapter.kt */
final class ControlAdapter$onCreateViewHolder$2 extends Lambda implements Function2<String, Boolean, Unit> {
    final /* synthetic */ ControlAdapter this$0;

    ControlAdapter$onCreateViewHolder$2(ControlAdapter controlAdapter) {
        this.this$0 = controlAdapter;
        super(2);
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj, Object obj2) {
        invoke((String) obj, ((Boolean) obj2).booleanValue());
        return Unit.INSTANCE;
    }

    public final void invoke(String str, boolean z) {
        Intrinsics.checkParameterIsNotNull(str, "id");
        ControlsModel access$getModel$p = this.this$0.model;
        if (access$getModel$p != null) {
            access$getModel$p.changeFavoriteStatus(str, z);
        }
    }
}
