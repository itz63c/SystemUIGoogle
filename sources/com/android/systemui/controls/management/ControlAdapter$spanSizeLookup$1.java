package com.android.systemui.controls.management;

import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup;

/* compiled from: ControlAdapter.kt */
public final class ControlAdapter$spanSizeLookup$1 extends SpanSizeLookup {
    final /* synthetic */ ControlAdapter this$0;

    ControlAdapter$spanSizeLookup$1(ControlAdapter controlAdapter) {
        this.this$0 = controlAdapter;
    }

    public int getSpanSize(int i) {
        return this.this$0.getItemViewType(i) == 0 ? 2 : 1;
    }
}
