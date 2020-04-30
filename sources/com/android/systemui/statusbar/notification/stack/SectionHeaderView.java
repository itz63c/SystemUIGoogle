package com.android.systemui.statusbar.notification.stack;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.systemui.C2011R$id;
import com.android.systemui.statusbar.notification.row.StackScrollerDecorView;

public class SectionHeaderView extends StackScrollerDecorView {
    private ImageView mClearAllButton;
    private ViewGroup mContents;
    private OnClickListener mLabelClickListener = null;
    private Integer mLabelTextId;
    private TextView mLabelView;
    private OnClickListener mOnClearClickListener = null;

    /* access modifiers changed from: protected */
    public View findSecondaryView() {
        return null;
    }

    public boolean isTransparent() {
        return true;
    }

    public boolean needsClippingToShelf() {
        return true;
    }

    public SectionHeaderView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        this.mContents = (ViewGroup) requireViewById(C2011R$id.content);
        bindContents();
        super.onFinishInflate();
        setVisible(true, false);
    }

    private void bindContents() {
        this.mLabelView = (TextView) requireViewById(C2011R$id.header_label);
        ImageView imageView = (ImageView) requireViewById(C2011R$id.btn_clear_all);
        this.mClearAllButton = imageView;
        OnClickListener onClickListener = this.mOnClearClickListener;
        if (onClickListener != null) {
            imageView.setOnClickListener(onClickListener);
        }
        OnClickListener onClickListener2 = this.mLabelClickListener;
        if (onClickListener2 != null) {
            this.mLabelView.setOnClickListener(onClickListener2);
        }
        Integer num = this.mLabelTextId;
        if (num != null) {
            this.mLabelView.setText(num.intValue());
        }
    }

    /* access modifiers changed from: protected */
    public View findContentView() {
        return this.mContents;
    }

    /* access modifiers changed from: 0000 */
    public void setAreThereDismissableGentleNotifs(boolean z) {
        this.mClearAllButton.setVisibility(z ? 0 : 8);
    }

    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return super.onInterceptTouchEvent(motionEvent);
    }

    /* access modifiers changed from: 0000 */
    public void setOnHeaderClickListener(OnClickListener onClickListener) {
        this.mLabelClickListener = onClickListener;
        this.mLabelView.setOnClickListener(onClickListener);
    }

    /* access modifiers changed from: protected */
    public void applyContentTransformation(float f, float f2) {
        super.applyContentTransformation(f, f2);
        this.mLabelView.setAlpha(f);
        this.mLabelView.setTranslationY(f2);
        this.mClearAllButton.setAlpha(f);
        this.mClearAllButton.setTranslationY(f2);
    }

    /* access modifiers changed from: 0000 */
    public void setOnClearAllClickListener(OnClickListener onClickListener) {
        this.mOnClearClickListener = onClickListener;
        this.mClearAllButton.setOnClickListener(onClickListener);
    }

    /* access modifiers changed from: 0000 */
    public void setHeaderText(int i) {
        this.mLabelTextId = Integer.valueOf(i);
        this.mLabelView.setText(i);
    }
}
