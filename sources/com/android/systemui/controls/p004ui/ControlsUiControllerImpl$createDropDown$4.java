package com.android.systemui.controls.p004ui;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2010R$drawable;
import com.android.systemui.C2018R$style;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref$ObjectRef;

/* renamed from: com.android.systemui.controls.ui.ControlsUiControllerImpl$createDropDown$4 */
/* compiled from: ControlsUiControllerImpl.kt */
public final class ControlsUiControllerImpl$createDropDown$4 implements OnClickListener {
    final /* synthetic */ Ref$ObjectRef $adapter;
    final /* synthetic */ ViewGroup $anchor;
    final /* synthetic */ ControlsUiControllerImpl this$0;

    ControlsUiControllerImpl$createDropDown$4(ControlsUiControllerImpl controlsUiControllerImpl, ViewGroup viewGroup, Ref$ObjectRef ref$ObjectRef) {
        this.this$0 = controlsUiControllerImpl;
        this.$anchor = viewGroup;
        this.$adapter = ref$ObjectRef;
    }

    public void onClick(View view) {
        Intrinsics.checkParameterIsNotNull(view, "v");
        this.this$0.popup = new ListPopupWindow(new ContextThemeWrapper(this.this$0.getContext(), C2018R$style.Control_ListPopupWindow));
        ListPopupWindow access$getPopup$p = this.this$0.popup;
        if (access$getPopup$p != null) {
            access$getPopup$p.setWindowLayoutType(2020);
            access$getPopup$p.setAnchorView(this.$anchor);
            access$getPopup$p.setAdapter((ItemAdapter) this.$adapter.element);
            access$getPopup$p.setModal(true);
            access$getPopup$p.setOnItemClickListener(new C0816x852a0ca3(access$getPopup$p, this));
            access$getPopup$p.show();
            ListView listView = access$getPopup$p.getListView();
            if (listView != null) {
                Context context = listView.getContext();
                String str = "context";
                Intrinsics.checkExpressionValueIsNotNull(context, str);
                listView.setDividerHeight(context.getResources().getDimensionPixelSize(C2009R$dimen.control_list_divider));
                Context context2 = listView.getContext();
                Intrinsics.checkExpressionValueIsNotNull(context2, str);
                listView.setDivider(context2.getResources().getDrawable(C2010R$drawable.controls_list_divider));
            }
            access$getPopup$p.show();
        }
    }
}
