package com.android.systemui.controls.p004ui;

import java.util.List;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.FunctionReference;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KDeclarationContainer;

/* renamed from: com.android.systemui.controls.ui.ControlsUiControllerImpl$show$2 */
/* compiled from: ControlsUiControllerImpl.kt */
final /* synthetic */ class ControlsUiControllerImpl$show$2 extends FunctionReference implements Function1<List<? extends SelectionItem>, Unit> {
    ControlsUiControllerImpl$show$2(ControlsUiControllerImpl controlsUiControllerImpl) {
        super(1, controlsUiControllerImpl);
    }

    public final String getName() {
        return "showInitialSetupView";
    }

    public final KDeclarationContainer getOwner() {
        return Reflection.getOrCreateKotlinClass(ControlsUiControllerImpl.class);
    }

    public final String getSignature() {
        return "showInitialSetupView(Ljava/util/List;)V";
    }

    public /* bridge */ /* synthetic */ Object invoke(Object obj) {
        invoke((List) obj);
        return Unit.INSTANCE;
    }

    public final void invoke(List<SelectionItem> list) {
        Intrinsics.checkParameterIsNotNull(list, "p1");
        ((ControlsUiControllerImpl) this.receiver).showInitialSetupView(list);
    }
}
