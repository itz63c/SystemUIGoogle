package com.android.systemui.p007qs;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/* renamed from: com.android.systemui.qs.NonInterceptingScrollView */
public class NonInterceptingScrollView extends ScrollView {
    public NonInterceptingScrollView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getActionMasked() == 0 && canScrollVertically(1)) {
            requestDisallowInterceptTouchEvent(true);
        }
        return super.onTouchEvent(motionEvent);
    }
}
