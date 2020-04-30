package com.google.android.systemui.assist.uihints;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class AssistUIView extends FrameLayout {
    private TouchOutsideHandler mTouchOutside;

    public AssistUIView(Context context) {
        this(context, null);
    }

    public AssistUIView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public AssistUIView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public AssistUIView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        setClipChildren(false);
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 4) {
            TouchOutsideHandler touchOutsideHandler = this.mTouchOutside;
            if (touchOutsideHandler != null) {
                touchOutsideHandler.onTouchOutside();
                return false;
            }
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    /* access modifiers changed from: 0000 */
    public void setTouchOutside(TouchOutsideHandler touchOutsideHandler) {
        this.mTouchOutside = touchOutsideHandler;
    }
}
