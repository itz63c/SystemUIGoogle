package com.android.systemui.controls.p004ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.systemui.C2011R$id;
import kotlin.jvm.internal.Intrinsics;

/* renamed from: com.android.systemui.controls.ui.ItemAdapter */
/* compiled from: ControlsUiControllerImpl.kt */
final class ItemAdapter extends ArrayAdapter<SelectionItem> {
    private final LayoutInflater layoutInflater = LayoutInflater.from(getContext());
    private final int resource;

    public ItemAdapter(Context context, int i) {
        Intrinsics.checkParameterIsNotNull(context, "parentContext");
        super(context, i);
        this.resource = i;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        Intrinsics.checkParameterIsNotNull(viewGroup, "parent");
        SelectionItem selectionItem = (SelectionItem) getItem(i);
        if (view == null) {
            view = this.layoutInflater.inflate(this.resource, viewGroup, false);
        }
        ((TextView) view.requireViewById(C2011R$id.controls_spinner_item)).setText(selectionItem.getTitle());
        ImageView imageView = (ImageView) view.requireViewById(C2011R$id.app_icon);
        imageView.setContentDescription(selectionItem.getAppName());
        imageView.setImageDrawable(selectionItem.getIcon());
        Intrinsics.checkExpressionValueIsNotNull(view, "view");
        return view;
    }
}
