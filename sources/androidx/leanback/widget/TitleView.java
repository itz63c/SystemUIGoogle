package androidx.leanback.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.leanback.R$attr;
import androidx.leanback.R$id;
import androidx.leanback.R$layout;
import androidx.leanback.widget.SearchOrbView.Colors;
import androidx.leanback.widget.TitleViewAdapter.Provider;

public class TitleView extends FrameLayout implements Provider {
    private int flags;
    private ImageView mBadgeView;
    private boolean mHasSearchListener;
    private SearchOrbView mSearchOrbView;
    private TextView mTextView;
    private final TitleViewAdapter mTitleViewAdapter;

    public TitleView(Context context) {
        this(context, null);
    }

    public TitleView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R$attr.browseTitleViewStyle);
    }

    public TitleView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.flags = 6;
        this.mHasSearchListener = false;
        this.mTitleViewAdapter = new TitleViewAdapter() {
            public View getSearchAffordanceView() {
                return TitleView.this.getSearchAffordanceView();
            }

            public void setOnSearchClickedListener(OnClickListener onClickListener) {
                TitleView.this.setOnSearchClickedListener(onClickListener);
            }

            public void setAnimationEnabled(boolean z) {
                TitleView.this.enableAnimation(z);
            }

            public void setBadgeDrawable(Drawable drawable) {
                TitleView.this.setBadgeDrawable(drawable);
            }

            public void setSearchAffordanceColors(Colors colors) {
                TitleView.this.setSearchAffordanceColors(colors);
            }

            public void setTitle(CharSequence charSequence) {
                TitleView.this.setTitle(charSequence);
            }

            public void updateComponentsVisibility(int i) {
                TitleView.this.updateComponentsVisibility(i);
            }
        };
        View inflate = LayoutInflater.from(context).inflate(R$layout.lb_title_view, this);
        this.mBadgeView = (ImageView) inflate.findViewById(R$id.title_badge);
        this.mTextView = (TextView) inflate.findViewById(R$id.title_text);
        this.mSearchOrbView = (SearchOrbView) inflate.findViewById(R$id.title_orb);
        setClipToPadding(false);
        setClipChildren(false);
    }

    public void setTitle(CharSequence charSequence) {
        this.mTextView.setText(charSequence);
        updateBadgeVisibility();
    }

    public void setBadgeDrawable(Drawable drawable) {
        this.mBadgeView.setImageDrawable(drawable);
        updateBadgeVisibility();
    }

    public void setOnSearchClickedListener(OnClickListener onClickListener) {
        this.mHasSearchListener = onClickListener != null;
        this.mSearchOrbView.setOnOrbClickedListener(onClickListener);
        updateSearchOrbViewVisiblity();
    }

    public View getSearchAffordanceView() {
        return this.mSearchOrbView;
    }

    public void setSearchAffordanceColors(Colors colors) {
        this.mSearchOrbView.setOrbColors(colors);
    }

    public void enableAnimation(boolean z) {
        SearchOrbView searchOrbView = this.mSearchOrbView;
        searchOrbView.enableOrbColorAnimation(z && searchOrbView.hasFocus());
    }

    public void updateComponentsVisibility(int i) {
        this.flags = i;
        if ((i & 2) == 2) {
            updateBadgeVisibility();
        } else {
            this.mBadgeView.setVisibility(8);
            this.mTextView.setVisibility(8);
        }
        updateSearchOrbViewVisiblity();
    }

    private void updateSearchOrbViewVisiblity() {
        int i = 4;
        if (this.mHasSearchListener && (this.flags & 4) == 4) {
            i = 0;
        }
        this.mSearchOrbView.setVisibility(i);
    }

    private void updateBadgeVisibility() {
        if (this.mBadgeView.getDrawable() != null) {
            this.mBadgeView.setVisibility(0);
            this.mTextView.setVisibility(8);
            return;
        }
        this.mBadgeView.setVisibility(8);
        this.mTextView.setVisibility(0);
    }

    public TitleViewAdapter getTitleViewAdapter() {
        return this.mTitleViewAdapter;
    }
}
