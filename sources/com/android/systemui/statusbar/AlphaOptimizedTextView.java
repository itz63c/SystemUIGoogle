package com.android.systemui.statusbar;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class AlphaOptimizedTextView extends TextView {
    public boolean hasOverlappingRendering() {
        return false;
    }

    public AlphaOptimizedTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }
}
