package com.android.systemui.controls.p004ui;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListPopupWindow;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;

/* renamed from: com.android.systemui.controls.ui.ControlsUiControllerImpl$createDropDown$4$onClick$$inlined$apply$lambda$1 */
/* compiled from: ControlsUiControllerImpl.kt */
public final class C0816x852a0ca3 implements OnItemClickListener {
    final /* synthetic */ ListPopupWindow $this_apply;
    final /* synthetic */ ControlsUiControllerImpl$createDropDown$4 this$0;

    C0816x852a0ca3(ListPopupWindow listPopupWindow, ControlsUiControllerImpl$createDropDown$4 controlsUiControllerImpl$createDropDown$4) {
        this.$this_apply = listPopupWindow;
        this.this$0 = controlsUiControllerImpl$createDropDown$4;
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        Intrinsics.checkParameterIsNotNull(adapterView, "parent");
        Intrinsics.checkParameterIsNotNull(view, "view");
        Object itemAtPosition = adapterView.getItemAtPosition(i);
        if (itemAtPosition != null) {
            this.this$0.this$0.switchAppOrStructure((SelectionItem) itemAtPosition);
            this.$this_apply.dismiss();
            return;
        }
        throw new TypeCastException("null cannot be cast to non-null type com.android.systemui.controls.ui.SelectionItem");
    }
}
