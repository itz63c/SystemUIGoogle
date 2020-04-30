package com.android.systemui.p007qs;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

/* renamed from: com.android.systemui.qs.QuickTileLayout */
public class QuickTileLayout extends LinearLayout {
    public QuickTileLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setGravity(17);
    }

    public void addView(View view, int i, LayoutParams layoutParams) {
        int i2 = layoutParams.height;
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(i2, i2);
        layoutParams2.weight = 1.0f;
        super.addView(view, i, layoutParams2);
    }
}
