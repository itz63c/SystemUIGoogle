package androidx.leanback.widget;

import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.leanback.R$fraction;
import androidx.leanback.R$id;
import androidx.leanback.R$layout;

public class RowHeaderPresenter extends Presenter {
    private final boolean mAnimateSelect;
    private final int mLayoutResourceId;
    private boolean mNullItemVisibilityGone;

    public static class ViewHolder extends androidx.leanback.widget.Presenter.ViewHolder {
        TextView mDescriptionView;
        float mSelectLevel;
        RowHeaderView mTitleView;
        float mUnselectAlpha;

        public ViewHolder(View view) {
            super(view);
            this.mTitleView = (RowHeaderView) view.findViewById(R$id.row_header);
            this.mDescriptionView = (TextView) view.findViewById(R$id.row_header_description);
            initColors();
        }

        /* access modifiers changed from: 0000 */
        public void initColors() {
            RowHeaderView rowHeaderView = this.mTitleView;
            if (rowHeaderView != null) {
                rowHeaderView.getCurrentTextColor();
            }
            this.mUnselectAlpha = this.view.getResources().getFraction(R$fraction.lb_browse_header_unselect_alpha, 1, 1);
        }
    }

    public RowHeaderPresenter() {
        this(R$layout.lb_row_header);
    }

    public RowHeaderPresenter(int i) {
        this(i, true);
    }

    public RowHeaderPresenter(int i, boolean z) {
        new Paint(1);
        this.mLayoutResourceId = i;
        this.mAnimateSelect = z;
    }

    public void setNullItemVisibilityGone(boolean z) {
        this.mNullItemVisibilityGone = z;
    }

    public androidx.leanback.widget.Presenter.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(this.mLayoutResourceId, viewGroup, false));
        if (this.mAnimateSelect) {
            setSelectLevel(viewHolder, 0.0f);
        }
        return viewHolder;
    }

    public void onBindViewHolder(androidx.leanback.widget.Presenter.ViewHolder viewHolder, Object obj) {
        HeaderItem headerItem = obj == null ? null : ((Row) obj).getHeaderItem();
        ViewHolder viewHolder2 = (ViewHolder) viewHolder;
        if (headerItem == null) {
            RowHeaderView rowHeaderView = viewHolder2.mTitleView;
            if (rowHeaderView != null) {
                rowHeaderView.setText(null);
            }
            TextView textView = viewHolder2.mDescriptionView;
            if (textView != null) {
                textView.setText(null);
            }
            viewHolder.view.setContentDescription(null);
            if (this.mNullItemVisibilityGone) {
                viewHolder.view.setVisibility(8);
            }
        } else if (viewHolder2.mTitleView == null) {
            if (viewHolder2.mDescriptionView != null) {
                if (TextUtils.isEmpty(headerItem.getDescription())) {
                    viewHolder2.mDescriptionView.setVisibility(8);
                } else {
                    viewHolder2.mDescriptionView.setVisibility(0);
                }
                viewHolder2.mDescriptionView.setText(headerItem.getDescription());
            }
            viewHolder.view.setContentDescription(headerItem.getContentDescription());
            viewHolder.view.setVisibility(0);
        } else {
            headerItem.getName();
            throw null;
        }
    }

    public void onUnbindViewHolder(androidx.leanback.widget.Presenter.ViewHolder viewHolder) {
        ViewHolder viewHolder2 = (ViewHolder) viewHolder;
        RowHeaderView rowHeaderView = viewHolder2.mTitleView;
        if (rowHeaderView != null) {
            rowHeaderView.setText(null);
        }
        TextView textView = viewHolder2.mDescriptionView;
        if (textView != null) {
            textView.setText(null);
        }
        if (this.mAnimateSelect) {
            setSelectLevel(viewHolder2, 0.0f);
        }
    }

    public final void setSelectLevel(ViewHolder viewHolder, float f) {
        viewHolder.mSelectLevel = f;
        onSelectLevelChanged(viewHolder);
    }

    /* access modifiers changed from: protected */
    public void onSelectLevelChanged(ViewHolder viewHolder) {
        if (this.mAnimateSelect) {
            View view = viewHolder.view;
            float f = viewHolder.mUnselectAlpha;
            view.setAlpha(f + (viewHolder.mSelectLevel * (1.0f - f)));
        }
    }
}
