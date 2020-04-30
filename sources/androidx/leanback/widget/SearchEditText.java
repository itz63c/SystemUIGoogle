package androidx.leanback.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import androidx.leanback.R$style;

public class SearchEditText extends StreamingTextView {
    OnKeyboardDismissListener mKeyboardDismissListener;

    public interface OnKeyboardDismissListener {
        void onKeyboardDismiss();
    }

    static {
        Class<SearchEditText> cls = SearchEditText.class;
    }

    public SearchEditText(Context context) {
        this(context, null);
    }

    public SearchEditText(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R$style.TextAppearance_Leanback_SearchTextEdit);
    }

    public SearchEditText(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public boolean onKeyPreIme(int i, KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == 4 && this.mKeyboardDismissListener != null) {
            post(new Runnable() {
                public void run() {
                    OnKeyboardDismissListener onKeyboardDismissListener = SearchEditText.this.mKeyboardDismissListener;
                    if (onKeyboardDismissListener != null) {
                        onKeyboardDismissListener.onKeyboardDismiss();
                    }
                }
            });
        }
        return super.onKeyPreIme(i, keyEvent);
    }

    public void setOnKeyboardDismissListener(OnKeyboardDismissListener onKeyboardDismissListener) {
        this.mKeyboardDismissListener = onKeyboardDismissListener;
    }
}
