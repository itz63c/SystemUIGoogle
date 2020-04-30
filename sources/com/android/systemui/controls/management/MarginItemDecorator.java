package com.android.systemui.controls.management;

import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ItemDecoration;
import androidx.recyclerview.widget.RecyclerView.State;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlAdapter.kt */
public final class MarginItemDecorator extends ItemDecoration {
    private final int sideMargins;
    private final int topMargin;

    public MarginItemDecorator(int i, int i2) {
        this.topMargin = i;
        this.sideMargins = i2;
    }

    public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, State state) {
        Intrinsics.checkParameterIsNotNull(rect, "outRect");
        Intrinsics.checkParameterIsNotNull(view, "view");
        Intrinsics.checkParameterIsNotNull(recyclerView, "parent");
        Intrinsics.checkParameterIsNotNull(state, "state");
        rect.top = this.topMargin;
        int i = this.sideMargins;
        rect.left = i;
        rect.right = i;
    }
}
