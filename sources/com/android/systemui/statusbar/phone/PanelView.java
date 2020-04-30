package com.android.systemui.statusbar.phone;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import com.android.systemui.statusbar.phone.PanelViewController.TouchHandler;

public abstract class PanelView extends FrameLayout {
    private OnConfigurationChangedListener mOnConfigurationChangedListener;
    private TouchHandler mTouchHandler;

    interface OnConfigurationChangedListener {
        void onConfigurationChanged(Configuration configuration);
    }

    static {
        Class<PanelView> cls = PanelView.class;
    }

    public PanelView(Context context) {
        super(context);
    }

    public PanelView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public PanelView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public PanelView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    public void setOnTouchListener(TouchHandler touchHandler) {
        super.setOnTouchListener(touchHandler);
        this.mTouchHandler = touchHandler;
    }

    public void setOnConfigurationChangedListener(OnConfigurationChangedListener onConfigurationChangedListener) {
        this.mOnConfigurationChangedListener = onConfigurationChangedListener;
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return this.mTouchHandler.onInterceptTouchEvent(motionEvent);
    }

    public void dispatchConfigurationChanged(Configuration configuration) {
        super.dispatchConfigurationChanged(configuration);
        this.mOnConfigurationChangedListener.onConfigurationChanged(configuration);
    }
}
