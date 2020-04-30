package androidx.slice.widget;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.slice.view.R$styleable;

public class RowStyle {
    private int mActionDividerHeight;
    private int mBottomDividerEndPadding;
    private int mBottomDividerStartPadding;
    private int mContentEndPadding;
    private int mContentStartPadding;
    private int mEndItemEndPadding;
    private int mEndItemStartPadding;
    private int mSubContentEndPadding;
    private int mSubContentStartPadding;
    private int mTitleEndPadding;
    private int mTitleItemEndPadding;
    private int mTitleItemStartPadding;
    private int mTitleStartPadding;

    public RowStyle(Context context, int i) {
        TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes(i, R$styleable.RowStyle);
        try {
            this.mTitleItemStartPadding = (int) obtainStyledAttributes.getDimension(R$styleable.RowStyle_titleItemStartPadding, -1.0f);
            this.mTitleItemEndPadding = (int) obtainStyledAttributes.getDimension(R$styleable.RowStyle_titleItemEndPadding, -1.0f);
            this.mContentStartPadding = (int) obtainStyledAttributes.getDimension(R$styleable.RowStyle_contentStartPadding, -1.0f);
            this.mContentEndPadding = (int) obtainStyledAttributes.getDimension(R$styleable.RowStyle_contentEndPadding, -1.0f);
            this.mTitleStartPadding = (int) obtainStyledAttributes.getDimension(R$styleable.RowStyle_titleStartPadding, -1.0f);
            this.mTitleEndPadding = (int) obtainStyledAttributes.getDimension(R$styleable.RowStyle_titleEndPadding, -1.0f);
            this.mSubContentStartPadding = (int) obtainStyledAttributes.getDimension(R$styleable.RowStyle_subContentStartPadding, -1.0f);
            this.mSubContentEndPadding = (int) obtainStyledAttributes.getDimension(R$styleable.RowStyle_subContentEndPadding, -1.0f);
            this.mEndItemStartPadding = (int) obtainStyledAttributes.getDimension(R$styleable.RowStyle_endItemStartPadding, -1.0f);
            this.mEndItemEndPadding = (int) obtainStyledAttributes.getDimension(R$styleable.RowStyle_endItemEndPadding, -1.0f);
            this.mBottomDividerStartPadding = (int) obtainStyledAttributes.getDimension(R$styleable.RowStyle_bottomDividerStartPadding, -1.0f);
            this.mBottomDividerEndPadding = (int) obtainStyledAttributes.getDimension(R$styleable.RowStyle_bottomDividerEndPadding, -1.0f);
            this.mActionDividerHeight = (int) obtainStyledAttributes.getDimension(R$styleable.RowStyle_actionDividerHeight, -1.0f);
        } finally {
            obtainStyledAttributes.recycle();
        }
    }

    public int getTitleItemStartPadding() {
        return this.mTitleItemStartPadding;
    }

    public int getTitleItemEndPadding() {
        return this.mTitleItemEndPadding;
    }

    public int getContentStartPadding() {
        return this.mContentStartPadding;
    }

    public int getContentEndPadding() {
        return this.mContentEndPadding;
    }

    public int getTitleStartPadding() {
        return this.mTitleStartPadding;
    }

    public int getTitleEndPadding() {
        return this.mTitleEndPadding;
    }

    public int getSubContentStartPadding() {
        return this.mSubContentStartPadding;
    }

    public int getSubContentEndPadding() {
        return this.mSubContentEndPadding;
    }

    public int getEndItemStartPadding() {
        return this.mEndItemStartPadding;
    }

    public int getEndItemEndPadding() {
        return this.mEndItemEndPadding;
    }

    public int getBottomDividerStartPadding() {
        return this.mBottomDividerStartPadding;
    }

    public int getBottomDividerEndPadding() {
        return this.mBottomDividerEndPadding;
    }

    public int getActionDividerHeight() {
        return this.mActionDividerHeight;
    }
}
