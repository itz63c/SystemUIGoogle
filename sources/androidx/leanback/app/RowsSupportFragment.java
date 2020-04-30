package androidx.leanback.app;

import android.animation.TimeAnimator;
import android.animation.TimeAnimator.TimeListener;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import androidx.leanback.R$id;
import androidx.leanback.R$integer;
import androidx.leanback.R$layout;
import androidx.leanback.app.BrowseSupportFragment.FragmentHost;
import androidx.leanback.app.BrowseSupportFragment.MainFragmentAdapterProvider;
import androidx.leanback.app.BrowseSupportFragment.MainFragmentRowsAdapterProvider;
import androidx.leanback.widget.BaseOnItemViewClickedListener;
import androidx.leanback.widget.BaseOnItemViewSelectedListener;
import androidx.leanback.widget.HorizontalGridView;
import androidx.leanback.widget.ItemBridgeAdapter;
import androidx.leanback.widget.ItemBridgeAdapter.AdapterListener;
import androidx.leanback.widget.ItemBridgeAdapter.ViewHolder;
import androidx.leanback.widget.ListRowPresenter$ViewHolder;
import androidx.leanback.widget.ObjectAdapter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.OnItemViewSelectedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.RowPresenter;
import androidx.leanback.widget.VerticalGridView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool;
import java.util.ArrayList;

public class RowsSupportFragment extends BaseRowSupportFragment implements MainFragmentRowsAdapterProvider, MainFragmentAdapterProvider {
    boolean mAfterEntranceTransition = true;
    private int mAlignedTop = Integer.MIN_VALUE;
    private final AdapterListener mBridgeAdapterListener = new AdapterListener() {
        public void onAddPresenter(Presenter presenter, int i) {
            AdapterListener adapterListener = RowsSupportFragment.this.mExternalAdapterListener;
            if (adapterListener != null) {
                adapterListener.onAddPresenter(presenter, i);
            }
        }

        public void onCreate(ViewHolder viewHolder) {
            VerticalGridView verticalGridView = RowsSupportFragment.this.getVerticalGridView();
            if (verticalGridView != null) {
                verticalGridView.setClipChildren(false);
            }
            RowsSupportFragment.this.setupSharedViewPool(viewHolder);
            RowsSupportFragment.this.mViewsCreated = true;
            viewHolder.setExtraObject(new RowViewHolderExtra(viewHolder));
            RowsSupportFragment.setRowViewSelected(viewHolder, false, true);
            AdapterListener adapterListener = RowsSupportFragment.this.mExternalAdapterListener;
            if (adapterListener != null) {
                adapterListener.onCreate(viewHolder);
            }
        }

        public void onAttachedToWindow(ViewHolder viewHolder) {
            RowsSupportFragment.setRowViewExpanded(viewHolder, RowsSupportFragment.this.mExpand);
            RowPresenter rowPresenter = (RowPresenter) viewHolder.getPresenter();
            RowPresenter.ViewHolder rowViewHolder = rowPresenter.getRowViewHolder(viewHolder.getViewHolder());
            rowPresenter.setEntranceTransitionState(rowViewHolder, RowsSupportFragment.this.mAfterEntranceTransition);
            rowViewHolder.setOnItemViewSelectedListener(RowsSupportFragment.this.mOnItemViewSelectedListener);
            rowViewHolder.setOnItemViewClickedListener(RowsSupportFragment.this.mOnItemViewClickedListener);
            rowPresenter.freeze(rowViewHolder, RowsSupportFragment.this.mFreezeRows);
            AdapterListener adapterListener = RowsSupportFragment.this.mExternalAdapterListener;
            if (adapterListener != null) {
                adapterListener.onAttachedToWindow(viewHolder);
            }
        }

        public void onDetachedFromWindow(ViewHolder viewHolder) {
            ViewHolder viewHolder2 = RowsSupportFragment.this.mSelectedViewHolder;
            if (viewHolder2 == viewHolder) {
                RowsSupportFragment.setRowViewSelected(viewHolder2, false, true);
                RowsSupportFragment.this.mSelectedViewHolder = null;
            }
            RowPresenter.ViewHolder rowViewHolder = ((RowPresenter) viewHolder.getPresenter()).getRowViewHolder(viewHolder.getViewHolder());
            rowViewHolder.setOnItemViewSelectedListener(null);
            rowViewHolder.setOnItemViewClickedListener(null);
            AdapterListener adapterListener = RowsSupportFragment.this.mExternalAdapterListener;
            if (adapterListener != null) {
                adapterListener.onDetachedFromWindow(viewHolder);
            }
        }

        public void onBind(ViewHolder viewHolder) {
            AdapterListener adapterListener = RowsSupportFragment.this.mExternalAdapterListener;
            if (adapterListener != null) {
                adapterListener.onBind(viewHolder);
            }
        }

        public void onUnbind(ViewHolder viewHolder) {
            RowsSupportFragment.setRowViewSelected(viewHolder, false, true);
            AdapterListener adapterListener = RowsSupportFragment.this.mExternalAdapterListener;
            if (adapterListener != null) {
                adapterListener.onUnbind(viewHolder);
            }
        }
    };
    boolean mExpand = true;
    AdapterListener mExternalAdapterListener;
    boolean mFreezeRows;
    private MainFragmentAdapter mMainFragmentAdapter;
    private MainFragmentRowsAdapter mMainFragmentRowsAdapter;
    BaseOnItemViewClickedListener mOnItemViewClickedListener;
    BaseOnItemViewSelectedListener mOnItemViewSelectedListener;
    private ArrayList<Presenter> mPresenterMapper;
    private RecycledViewPool mRecycledViewPool;
    ViewHolder mSelectedViewHolder;
    private int mSubPosition;
    boolean mViewsCreated;

    public static class MainFragmentAdapter extends androidx.leanback.app.BrowseSupportFragment.MainFragmentAdapter<RowsSupportFragment> {
        public MainFragmentAdapter(RowsSupportFragment rowsSupportFragment) {
            super(rowsSupportFragment);
            setScalingEnabled(true);
        }

        public boolean isScrolling() {
            return ((RowsSupportFragment) getFragment()).isScrolling();
        }

        public void setExpand(boolean z) {
            ((RowsSupportFragment) getFragment()).setExpand(z);
        }

        public void setEntranceTransitionState(boolean z) {
            ((RowsSupportFragment) getFragment()).setEntranceTransitionState(z);
        }

        public void setAlignment(int i) {
            ((RowsSupportFragment) getFragment()).setAlignment(i);
        }

        public boolean onTransitionPrepare() {
            return ((RowsSupportFragment) getFragment()).onTransitionPrepare();
        }

        public void onTransitionStart() {
            ((RowsSupportFragment) getFragment()).onTransitionStart();
        }

        public void onTransitionEnd() {
            ((RowsSupportFragment) getFragment()).onTransitionEnd();
        }
    }

    public static class MainFragmentRowsAdapter extends androidx.leanback.app.BrowseSupportFragment.MainFragmentRowsAdapter<RowsSupportFragment> {
        public MainFragmentRowsAdapter(RowsSupportFragment rowsSupportFragment) {
            super(rowsSupportFragment);
        }

        public void setAdapter(ObjectAdapter objectAdapter) {
            ((RowsSupportFragment) getFragment()).setAdapter(objectAdapter);
        }

        public void setOnItemViewClickedListener(OnItemViewClickedListener onItemViewClickedListener) {
            ((RowsSupportFragment) getFragment()).setOnItemViewClickedListener(onItemViewClickedListener);
        }

        public void setOnItemViewSelectedListener(OnItemViewSelectedListener onItemViewSelectedListener) {
            ((RowsSupportFragment) getFragment()).setOnItemViewSelectedListener(onItemViewSelectedListener);
        }

        public void setSelectedPosition(int i, boolean z) {
            ((RowsSupportFragment) getFragment()).setSelectedPosition(i, z);
        }

        public int getSelectedPosition() {
            return ((RowsSupportFragment) getFragment()).getSelectedPosition();
        }
    }

    static final class RowViewHolderExtra implements TimeListener {
        static final Interpolator sSelectAnimatorInterpolator = new DecelerateInterpolator(2.0f);
        final RowPresenter mRowPresenter;
        final Presenter.ViewHolder mRowViewHolder;
        final TimeAnimator mSelectAnimator = new TimeAnimator();
        final int mSelectAnimatorDurationInUse;
        final Interpolator mSelectAnimatorInterpolatorInUse;
        float mSelectLevelAnimDelta;
        float mSelectLevelAnimStart;

        RowViewHolderExtra(ViewHolder viewHolder) {
            this.mRowPresenter = (RowPresenter) viewHolder.getPresenter();
            this.mRowViewHolder = viewHolder.getViewHolder();
            this.mSelectAnimator.setTimeListener(this);
            this.mSelectAnimatorDurationInUse = viewHolder.itemView.getResources().getInteger(R$integer.lb_browse_rows_anim_duration);
            this.mSelectAnimatorInterpolatorInUse = sSelectAnimatorInterpolator;
        }

        public void onTimeUpdate(TimeAnimator timeAnimator, long j, long j2) {
            if (this.mSelectAnimator.isRunning()) {
                updateSelect(j, j2);
            }
        }

        /* access modifiers changed from: 0000 */
        public void updateSelect(long j, long j2) {
            float f;
            int i = this.mSelectAnimatorDurationInUse;
            if (j >= ((long) i)) {
                f = 1.0f;
                this.mSelectAnimator.end();
            } else {
                f = (float) (((double) j) / ((double) i));
            }
            Interpolator interpolator = this.mSelectAnimatorInterpolatorInUse;
            if (interpolator != null) {
                f = interpolator.getInterpolation(f);
            }
            this.mRowPresenter.setSelectLevel(this.mRowViewHolder, this.mSelectLevelAnimStart + (f * this.mSelectLevelAnimDelta));
        }

        /* access modifiers changed from: 0000 */
        public void animateSelect(boolean z, boolean z2) {
            this.mSelectAnimator.end();
            float f = z ? 1.0f : 0.0f;
            if (z2) {
                this.mRowPresenter.setSelectLevel(this.mRowViewHolder, f);
            } else if (this.mRowPresenter.getSelectLevel(this.mRowViewHolder) != f) {
                float selectLevel = this.mRowPresenter.getSelectLevel(this.mRowViewHolder);
                this.mSelectLevelAnimStart = selectLevel;
                this.mSelectLevelAnimDelta = f - selectLevel;
                this.mSelectAnimator.start();
            }
        }
    }

    public androidx.leanback.app.BrowseSupportFragment.MainFragmentAdapter getMainFragmentAdapter() {
        if (this.mMainFragmentAdapter == null) {
            this.mMainFragmentAdapter = new MainFragmentAdapter(this);
        }
        return this.mMainFragmentAdapter;
    }

    public androidx.leanback.app.BrowseSupportFragment.MainFragmentRowsAdapter getMainFragmentRowsAdapter() {
        if (this.mMainFragmentRowsAdapter == null) {
            this.mMainFragmentRowsAdapter = new MainFragmentRowsAdapter(this);
        }
        return this.mMainFragmentRowsAdapter;
    }

    /* access modifiers changed from: protected */
    public VerticalGridView findGridViewFromRoot(View view) {
        return (VerticalGridView) view.findViewById(R$id.container_list);
    }

    public void setOnItemViewClickedListener(BaseOnItemViewClickedListener baseOnItemViewClickedListener) {
        this.mOnItemViewClickedListener = baseOnItemViewClickedListener;
        if (this.mViewsCreated) {
            throw new IllegalStateException("Item clicked listener must be set before views are created");
        }
    }

    public void setExpand(boolean z) {
        this.mExpand = z;
        VerticalGridView verticalGridView = getVerticalGridView();
        if (verticalGridView != null) {
            int childCount = verticalGridView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                setRowViewExpanded((ViewHolder) verticalGridView.getChildViewHolder(verticalGridView.getChildAt(i)), this.mExpand);
            }
        }
    }

    public void setOnItemViewSelectedListener(BaseOnItemViewSelectedListener baseOnItemViewSelectedListener) {
        this.mOnItemViewSelectedListener = baseOnItemViewSelectedListener;
        VerticalGridView verticalGridView = getVerticalGridView();
        if (verticalGridView != null) {
            int childCount = verticalGridView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                getRowViewHolder((ViewHolder) verticalGridView.getChildViewHolder(verticalGridView.getChildAt(i))).setOnItemViewSelectedListener(this.mOnItemViewSelectedListener);
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void onRowSelected(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, int i, int i2) {
        boolean z = true;
        if (!(this.mSelectedViewHolder == viewHolder && this.mSubPosition == i2)) {
            this.mSubPosition = i2;
            ViewHolder viewHolder2 = this.mSelectedViewHolder;
            if (viewHolder2 != null) {
                setRowViewSelected(viewHolder2, false, false);
            }
            ViewHolder viewHolder3 = (ViewHolder) viewHolder;
            this.mSelectedViewHolder = viewHolder3;
            if (viewHolder3 != null) {
                setRowViewSelected(viewHolder3, true, false);
            }
        }
        MainFragmentAdapter mainFragmentAdapter = this.mMainFragmentAdapter;
        if (mainFragmentAdapter != null) {
            FragmentHost fragmentHost = mainFragmentAdapter.getFragmentHost();
            if (i > 0) {
                z = false;
            }
            fragmentHost.showTitleView(z);
        }
    }

    /* access modifiers changed from: 0000 */
    public int getLayoutResourceId() {
        return R$layout.lb_rows_fragment;
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        getVerticalGridView().setItemAlignmentViewId(R$id.row_content);
        getVerticalGridView().setSaveChildrenPolicy(2);
        setAlignment(this.mAlignedTop);
        this.mRecycledViewPool = null;
        this.mPresenterMapper = null;
        MainFragmentAdapter mainFragmentAdapter = this.mMainFragmentAdapter;
        if (mainFragmentAdapter != null) {
            mainFragmentAdapter.getFragmentHost().notifyViewCreated(this.mMainFragmentAdapter);
        }
    }

    public void onDestroyView() {
        this.mViewsCreated = false;
        super.onDestroyView();
    }

    /* access modifiers changed from: 0000 */
    public void setExternalAdapterListener(AdapterListener adapterListener) {
        this.mExternalAdapterListener = adapterListener;
    }

    static void setRowViewExpanded(ViewHolder viewHolder, boolean z) {
        ((RowPresenter) viewHolder.getPresenter()).setRowViewExpanded(viewHolder.getViewHolder(), z);
    }

    static void setRowViewSelected(ViewHolder viewHolder, boolean z, boolean z2) {
        ((RowViewHolderExtra) viewHolder.getExtraObject()).animateSelect(z, z2);
        ((RowPresenter) viewHolder.getPresenter()).setRowViewSelected(viewHolder.getViewHolder(), z);
    }

    /* access modifiers changed from: 0000 */
    public void setupSharedViewPool(ViewHolder viewHolder) {
        RowPresenter.ViewHolder rowViewHolder = ((RowPresenter) viewHolder.getPresenter()).getRowViewHolder(viewHolder.getViewHolder());
        if (rowViewHolder instanceof ListRowPresenter$ViewHolder) {
            ListRowPresenter$ViewHolder listRowPresenter$ViewHolder = (ListRowPresenter$ViewHolder) rowViewHolder;
            HorizontalGridView gridView = listRowPresenter$ViewHolder.getGridView();
            RecycledViewPool recycledViewPool = this.mRecycledViewPool;
            if (recycledViewPool == null) {
                this.mRecycledViewPool = gridView.getRecycledViewPool();
            } else {
                gridView.setRecycledViewPool(recycledViewPool);
            }
            ItemBridgeAdapter bridgeAdapter = listRowPresenter$ViewHolder.getBridgeAdapter();
            ArrayList<Presenter> arrayList = this.mPresenterMapper;
            if (arrayList == null) {
                this.mPresenterMapper = bridgeAdapter.getPresenterMapper();
            } else {
                bridgeAdapter.setPresenterMapper(arrayList);
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void updateAdapter() {
        super.updateAdapter();
        this.mSelectedViewHolder = null;
        this.mViewsCreated = false;
        ItemBridgeAdapter bridgeAdapter = getBridgeAdapter();
        if (bridgeAdapter != null) {
            bridgeAdapter.setAdapterListener(this.mBridgeAdapterListener);
        }
    }

    public boolean onTransitionPrepare() {
        boolean onTransitionPrepare = super.onTransitionPrepare();
        if (onTransitionPrepare) {
            freezeRows(true);
        }
        return onTransitionPrepare;
    }

    public void onTransitionEnd() {
        super.onTransitionEnd();
        freezeRows(false);
    }

    private void freezeRows(boolean z) {
        this.mFreezeRows = z;
        VerticalGridView verticalGridView = getVerticalGridView();
        if (verticalGridView != null) {
            int childCount = verticalGridView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                ViewHolder viewHolder = (ViewHolder) verticalGridView.getChildViewHolder(verticalGridView.getChildAt(i));
                RowPresenter rowPresenter = (RowPresenter) viewHolder.getPresenter();
                rowPresenter.freeze(rowPresenter.getRowViewHolder(viewHolder.getViewHolder()), z);
            }
        }
    }

    public void setEntranceTransitionState(boolean z) {
        this.mAfterEntranceTransition = z;
        VerticalGridView verticalGridView = getVerticalGridView();
        if (verticalGridView != null) {
            int childCount = verticalGridView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                ViewHolder viewHolder = (ViewHolder) verticalGridView.getChildViewHolder(verticalGridView.getChildAt(i));
                RowPresenter rowPresenter = (RowPresenter) viewHolder.getPresenter();
                rowPresenter.setEntranceTransitionState(rowPresenter.getRowViewHolder(viewHolder.getViewHolder()), this.mAfterEntranceTransition);
            }
        }
    }

    static RowPresenter.ViewHolder getRowViewHolder(ViewHolder viewHolder) {
        if (viewHolder == null) {
            return null;
        }
        return ((RowPresenter) viewHolder.getPresenter()).getRowViewHolder(viewHolder.getViewHolder());
    }

    public boolean isScrolling() {
        boolean z = false;
        if (getVerticalGridView() == null) {
            return false;
        }
        if (getVerticalGridView().getScrollState() != 0) {
            z = true;
        }
        return z;
    }

    public void setAlignment(int i) {
        if (i != Integer.MIN_VALUE) {
            this.mAlignedTop = i;
            VerticalGridView verticalGridView = getVerticalGridView();
            if (verticalGridView != null) {
                verticalGridView.setItemAlignmentOffset(0);
                verticalGridView.setItemAlignmentOffsetPercent(-1.0f);
                verticalGridView.setItemAlignmentOffsetWithPadding(true);
                verticalGridView.setWindowAlignmentOffset(this.mAlignedTop);
                verticalGridView.setWindowAlignmentOffsetPercent(-1.0f);
                verticalGridView.setWindowAlignment(0);
            }
        }
    }
}
