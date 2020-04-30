package com.android.systemui.controls.management;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

/* compiled from: ControlAdapter.kt */
public abstract class Holder extends ViewHolder {
    public abstract void bindData(ElementWrapper elementWrapper);

    private Holder(View view) {
        super(view);
    }

    public /* synthetic */ Holder(View view, DefaultConstructorMarker defaultConstructorMarker) {
        this(view);
    }
}
