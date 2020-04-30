package com.android.systemui.p007qs.tileimpl;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.RelativeLayout;

/* renamed from: com.android.systemui.qs.tileimpl.ButtonRelativeLayout */
public class ButtonRelativeLayout extends RelativeLayout {
    public ButtonRelativeLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public CharSequence getAccessibilityClassName() {
        return Button.class.getName();
    }
}
