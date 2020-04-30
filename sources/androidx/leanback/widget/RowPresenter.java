package androidx.leanback.widget;

import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import androidx.leanback.graphics.ColorOverlayDimmer;

public abstract class RowPresenter extends Presenter {
    private RowHeaderPresenter mHeaderPresenter;
    boolean mSelectEffectEnabled = true;
    int mSyncActivatePolicy = 1;

    static class ContainerViewHolder extends androidx.leanback.widget.Presenter.ViewHolder {
        final ViewHolder mRowViewHolder;

        public ContainerViewHolder(RowContainerView rowContainerView, ViewHolder viewHolder) {
            super(rowContainerView);
            rowContainerView.addRowView(viewHolder.view);
            androidx.leanback.widget.RowHeaderPresenter.ViewHolder viewHolder2 = viewHolder.mHeaderViewHolder;
            if (viewHolder2 != null) {
                rowContainerView.addHeaderView(viewHolder2.view);
            }
            this.mRowViewHolder = viewHolder;
            viewHolder.mContainerViewHolder = this;
        }
    }

    public static class ViewHolder extends androidx.leanback.widget.Presenter.ViewHolder {
        int mActivated = 0;
        protected final ColorOverlayDimmer mColorDimmer;
        ContainerViewHolder mContainerViewHolder;
        boolean mExpanded;
        androidx.leanback.widget.RowHeaderPresenter.ViewHolder mHeaderViewHolder;
        boolean mInitialzed;
        private BaseOnItemViewClickedListener mOnItemViewClickedListener;
        BaseOnItemViewSelectedListener mOnItemViewSelectedListener;
        private OnKeyListener mOnKeyListener;
        Row mRow;
        Object mRowObject;
        float mSelectLevel = 0.0f;
        boolean mSelected;

        public ViewHolder(View view) {
            super(view);
            this.mColorDimmer = ColorOverlayDimmer.createDefault(view.getContext());
        }

        public final Row getRow() {
            return this.mRow;
        }

        public final Object getRowObject() {
            return this.mRowObject;
        }

        public final boolean isExpanded() {
            return this.mExpanded;
        }

        public final boolean isSelected() {
            return this.mSelected;
        }

        public final void setActivated(boolean z) {
            this.mActivated = z ? 1 : 2;
        }

        public final void syncActivatedStatus(View view) {
            int i = this.mActivated;
            if (i == 1) {
                view.setActivated(true);
            } else if (i == 2) {
                view.setActivated(false);
            }
        }

        public OnKeyListener getOnKeyListener() {
            return this.mOnKeyListener;
        }

        public final void setOnItemViewSelectedListener(BaseOnItemViewSelectedListener baseOnItemViewSelectedListener) {
            this.mOnItemViewSelectedListener = baseOnItemViewSelectedListener;
        }

        public final BaseOnItemViewSelectedListener getOnItemViewSelectedListener() {
            return this.mOnItemViewSelectedListener;
        }

        public final void setOnItemViewClickedListener(BaseOnItemViewClickedListener baseOnItemViewClickedListener) {
            this.mOnItemViewClickedListener = baseOnItemViewClickedListener;
        }

        public final BaseOnItemViewClickedListener getOnItemViewClickedListener() {
            return this.mOnItemViewClickedListener;
        }
    }

    /* access modifiers changed from: protected */
    public abstract ViewHolder createRowViewHolder(ViewGroup viewGroup);

    public void freeze(ViewHolder viewHolder, boolean z) {
    }

    /* access modifiers changed from: protected */
    public boolean isClippingChildren() {
        return false;
    }

    public boolean isUsingDefaultSelectEffect() {
        return true;
    }

    public RowPresenter() {
        RowHeaderPresenter rowHeaderPresenter = new RowHeaderPresenter();
        this.mHeaderPresenter = rowHeaderPresenter;
        rowHeaderPresenter.setNullItemVisibilityGone(true);
    }

    public final androidx.leanback.widget.Presenter.ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        androidx.leanback.widget.Presenter.ViewHolder viewHolder;
        ViewHolder createRowViewHolder = createRowViewHolder(viewGroup);
        createRowViewHolder.mInitialzed = false;
        if (needsRowContainerView()) {
            RowContainerView rowContainerView = new RowContainerView(viewGroup.getContext());
            RowHeaderPresenter rowHeaderPresenter = this.mHeaderPresenter;
            if (rowHeaderPresenter != null) {
                createRowViewHolder.mHeaderViewHolder = (androidx.leanback.widget.RowHeaderPresenter.ViewHolder) rowHeaderPresenter.onCreateViewHolder((ViewGroup) createRowViewHolder.view);
            }
            viewHolder = new ContainerViewHolder(rowContainerView, createRowViewHolder);
        } else {
            viewHolder = createRowViewHolder;
        }
        initializeRowViewHolder(createRowViewHolder);
        if (createRowViewHolder.mInitialzed) {
            return viewHolder;
        }
        throw new RuntimeException("super.initializeRowViewHolder() must be called");
    }

    /* access modifiers changed from: protected */
    public void initializeRowViewHolder(ViewHolder viewHolder) {
        viewHolder.mInitialzed = true;
        if (!isClippingChildren()) {
            View view = viewHolder.view;
            if (view instanceof ViewGroup) {
                ((ViewGroup) view).setClipChildren(false);
            }
            ContainerViewHolder containerViewHolder = viewHolder.mContainerViewHolder;
            if (containerViewHolder != null) {
                ((ViewGroup) containerViewHolder.view).setClipChildren(false);
            }
        }
    }

    public final ViewHolder getRowViewHolder(androidx.leanback.widget.Presenter.ViewHolder viewHolder) {
        if (viewHolder instanceof ContainerViewHolder) {
            return ((ContainerViewHolder) viewHolder).mRowViewHolder;
        }
        return (ViewHolder) viewHolder;
    }

    public final void setRowViewExpanded(androidx.leanback.widget.Presenter.ViewHolder viewHolder, boolean z) {
        ViewHolder rowViewHolder = getRowViewHolder(viewHolder);
        rowViewHolder.mExpanded = z;
        onRowViewExpanded(rowViewHolder, z);
    }

    public final void setRowViewSelected(androidx.leanback.widget.Presenter.ViewHolder viewHolder, boolean z) {
        ViewHolder rowViewHolder = getRowViewHolder(viewHolder);
        rowViewHolder.mSelected = z;
        onRowViewSelected(rowViewHolder, z);
    }

    /* access modifiers changed from: protected */
    public void onRowViewExpanded(ViewHolder viewHolder, boolean z) {
        updateHeaderViewVisibility(viewHolder);
        updateActivateStatus(viewHolder, viewHolder.view);
    }

    private void updateActivateStatus(ViewHolder viewHolder, View view) {
        int i = this.mSyncActivatePolicy;
        boolean z = true;
        if (i == 1) {
            viewHolder.setActivated(viewHolder.isExpanded());
        } else if (i == 2) {
            viewHolder.setActivated(viewHolder.isSelected());
        } else if (i == 3) {
            if (!viewHolder.isExpanded() || !viewHolder.isSelected()) {
                z = false;
            }
            viewHolder.setActivated(z);
        }
        viewHolder.syncActivatedStatus(view);
    }

    /* access modifiers changed from: protected */
    public void dispatchItemSelectedListener(ViewHolder viewHolder, boolean z) {
        if (z) {
            BaseOnItemViewSelectedListener baseOnItemViewSelectedListener = viewHolder.mOnItemViewSelectedListener;
            if (baseOnItemViewSelectedListener != null) {
                baseOnItemViewSelectedListener.onItemSelected(null, null, viewHolder, viewHolder.getRowObject());
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onRowViewSelected(ViewHolder viewHolder, boolean z) {
        dispatchItemSelectedListener(viewHolder, z);
        updateHeaderViewVisibility(viewHolder);
        updateActivateStatus(viewHolder, viewHolder.view);
    }

    private void updateHeaderViewVisibility(ViewHolder viewHolder) {
        if (this.mHeaderPresenter != null && viewHolder.mHeaderViewHolder != null) {
            ((RowContainerView) viewHolder.mContainerViewHolder.view).showHeader(viewHolder.isExpanded());
        }
    }

    public final void setSelectLevel(androidx.leanback.widget.Presenter.ViewHolder viewHolder, float f) {
        ViewHolder rowViewHolder = getRowViewHolder(viewHolder);
        rowViewHolder.mSelectLevel = f;
        onSelectLevelChanged(rowViewHolder);
    }

    public final float getSelectLevel(androidx.leanback.widget.Presenter.ViewHolder viewHolder) {
        return getRowViewHolder(viewHolder).mSelectLevel;
    }

    /* access modifiers changed from: protected */
    public void onSelectLevelChanged(ViewHolder viewHolder) {
        if (getSelectEffectEnabled()) {
            viewHolder.mColorDimmer.setActiveLevel(viewHolder.mSelectLevel);
            androidx.leanback.widget.RowHeaderPresenter.ViewHolder viewHolder2 = viewHolder.mHeaderViewHolder;
            if (viewHolder2 != null) {
                this.mHeaderPresenter.setSelectLevel(viewHolder2, viewHolder.mSelectLevel);
            }
            if (isUsingDefaultSelectEffect()) {
                ((RowContainerView) viewHolder.mContainerViewHolder.view).setForegroundColor(viewHolder.mColorDimmer.getPaint().getColor());
            }
        }
    }

    public final boolean getSelectEffectEnabled() {
        return this.mSelectEffectEnabled;
    }

    /* access modifiers changed from: 0000 */
    public final boolean needsDefaultSelectEffect() {
        return isUsingDefaultSelectEffect() && getSelectEffectEnabled();
    }

    /* access modifiers changed from: 0000 */
    public final boolean needsRowContainerView() {
        return this.mHeaderPresenter != null || needsDefaultSelectEffect();
    }

    public final void onBindViewHolder(androidx.leanback.widget.Presenter.ViewHolder viewHolder, Object obj) {
        onBindRowViewHolder(getRowViewHolder(viewHolder), obj);
    }

    /* access modifiers changed from: protected */
    public void onBindRowViewHolder(ViewHolder viewHolder, Object obj) {
        viewHolder.mRowObject = obj;
        viewHolder.mRow = obj instanceof Row ? (Row) obj : null;
        if (viewHolder.mHeaderViewHolder != null && viewHolder.getRow() != null) {
            this.mHeaderPresenter.onBindViewHolder(viewHolder.mHeaderViewHolder, obj);
        }
    }

    public final void onUnbindViewHolder(androidx.leanback.widget.Presenter.ViewHolder viewHolder) {
        onUnbindRowViewHolder(getRowViewHolder(viewHolder));
    }

    /* access modifiers changed from: protected */
    public void onUnbindRowViewHolder(ViewHolder viewHolder) {
        androidx.leanback.widget.RowHeaderPresenter.ViewHolder viewHolder2 = viewHolder.mHeaderViewHolder;
        if (viewHolder2 != null) {
            this.mHeaderPresenter.onUnbindViewHolder(viewHolder2);
        }
        viewHolder.mRow = null;
        viewHolder.mRowObject = null;
    }

    public final void onViewAttachedToWindow(androidx.leanback.widget.Presenter.ViewHolder viewHolder) {
        onRowViewAttachedToWindow(getRowViewHolder(viewHolder));
    }

    /* access modifiers changed from: protected */
    public void onRowViewAttachedToWindow(ViewHolder viewHolder) {
        androidx.leanback.widget.RowHeaderPresenter.ViewHolder viewHolder2 = viewHolder.mHeaderViewHolder;
        if (viewHolder2 != null) {
            this.mHeaderPresenter.onViewAttachedToWindow(viewHolder2);
        }
    }

    public final void onViewDetachedFromWindow(androidx.leanback.widget.Presenter.ViewHolder viewHolder) {
        onRowViewDetachedFromWindow(getRowViewHolder(viewHolder));
    }

    /* access modifiers changed from: protected */
    public void onRowViewDetachedFromWindow(ViewHolder viewHolder) {
        androidx.leanback.widget.RowHeaderPresenter.ViewHolder viewHolder2 = viewHolder.mHeaderViewHolder;
        if (viewHolder2 != null) {
            this.mHeaderPresenter.onViewDetachedFromWindow(viewHolder2);
        }
        Presenter.cancelAnimationsRecursive(viewHolder.view);
    }

    public void setEntranceTransitionState(ViewHolder viewHolder, boolean z) {
        androidx.leanback.widget.RowHeaderPresenter.ViewHolder viewHolder2 = viewHolder.mHeaderViewHolder;
        if (viewHolder2 != null && viewHolder2.view.getVisibility() != 8) {
            viewHolder.mHeaderViewHolder.view.setVisibility(z ? 0 : 4);
        }
    }
}
