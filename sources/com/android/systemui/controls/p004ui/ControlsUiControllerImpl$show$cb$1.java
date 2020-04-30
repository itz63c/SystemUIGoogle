package com.android.systemui.controls.p004ui;

import android.view.ViewGroup;
import java.util.function.Consumer;

/* renamed from: com.android.systemui.controls.ui.ControlsUiControllerImpl$show$cb$1 */
/* compiled from: ControlsUiControllerImpl.kt */
final class ControlsUiControllerImpl$show$cb$1<T> implements Consumer<Boolean> {
    final /* synthetic */ ViewGroup $parent;
    final /* synthetic */ ControlsUiControllerImpl this$0;

    ControlsUiControllerImpl$show$cb$1(ControlsUiControllerImpl controlsUiControllerImpl, ViewGroup viewGroup) {
        this.this$0 = controlsUiControllerImpl;
        this.$parent = viewGroup;
    }

    public /* bridge */ /* synthetic */ void accept(Object obj) {
        accept(((Boolean) obj).booleanValue());
    }

    public final void accept(boolean z) {
        this.this$0.reload(this.$parent);
    }
}
