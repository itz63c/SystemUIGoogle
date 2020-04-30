package com.android.systemui.statusbar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.android.systemui.C2011R$id;

public class KeyboardShortcutAppItemLayout extends RelativeLayout {
    public KeyboardShortcutAppItemLayout(Context context) {
        super(context);
    }

    public KeyboardShortcutAppItemLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        if (MeasureSpec.getMode(i) == 1073741824) {
            ImageView imageView = (ImageView) findViewById(C2011R$id.keyboard_shortcuts_icon);
            TextView textView = (TextView) findViewById(C2011R$id.keyboard_shortcuts_keyword);
            int size = MeasureSpec.getSize(i) - (getPaddingLeft() + getPaddingRight());
            if (imageView.getVisibility() == 0) {
                size -= imageView.getMeasuredWidth();
            }
            textView.setMaxWidth((int) Math.round(((double) size) * 0.7d));
        }
        super.onMeasure(i, i2);
    }
}
