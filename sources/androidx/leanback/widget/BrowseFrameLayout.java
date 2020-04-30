package androidx.leanback.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.FrameLayout;

public class BrowseFrameLayout extends FrameLayout {
    private OnFocusSearchListener mListener;
    private OnChildFocusListener mOnChildFocusListener;
    private OnKeyListener mOnDispatchKeyListener;

    public interface OnChildFocusListener {
        void onRequestChildFocus(View view, View view2);

        boolean onRequestFocusInDescendants(int i, Rect rect);
    }

    public interface OnFocusSearchListener {
        View onFocusSearch(View view, int i);
    }

    public BrowseFrameLayout(Context context) {
        this(context, null, 0);
    }

    public BrowseFrameLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public BrowseFrameLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public void setOnFocusSearchListener(OnFocusSearchListener onFocusSearchListener) {
        this.mListener = onFocusSearchListener;
    }

    public void setOnChildFocusListener(OnChildFocusListener onChildFocusListener) {
        this.mOnChildFocusListener = onChildFocusListener;
    }

    /* access modifiers changed from: protected */
    public boolean onRequestFocusInDescendants(int i, Rect rect) {
        OnChildFocusListener onChildFocusListener = this.mOnChildFocusListener;
        if (onChildFocusListener == null || !onChildFocusListener.onRequestFocusInDescendants(i, rect)) {
            return super.onRequestFocusInDescendants(i, rect);
        }
        return true;
    }

    public View focusSearch(View view, int i) {
        OnFocusSearchListener onFocusSearchListener = this.mListener;
        if (onFocusSearchListener != null) {
            View onFocusSearch = onFocusSearchListener.onFocusSearch(view, i);
            if (onFocusSearch != null) {
                return onFocusSearch;
            }
        }
        return super.focusSearch(view, i);
    }

    public void requestChildFocus(View view, View view2) {
        OnChildFocusListener onChildFocusListener = this.mOnChildFocusListener;
        if (onChildFocusListener != null) {
            onChildFocusListener.onRequestChildFocus(view, view2);
        }
        super.requestChildFocus(view, view2);
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        boolean dispatchKeyEvent = super.dispatchKeyEvent(keyEvent);
        OnKeyListener onKeyListener = this.mOnDispatchKeyListener;
        return (onKeyListener == null || dispatchKeyEvent) ? dispatchKeyEvent : onKeyListener.onKey(getRootView(), keyEvent.getKeyCode(), keyEvent);
    }

    public void setOnDispatchKeyListener(OnKeyListener onKeyListener) {
        this.mOnDispatchKeyListener = onKeyListener;
    }
}
