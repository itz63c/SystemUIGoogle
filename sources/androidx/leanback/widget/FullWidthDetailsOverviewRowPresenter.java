package androidx.leanback.widget;

import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.FrameLayout;
import androidx.leanback.R$dimen;
import androidx.leanback.R$id;
import androidx.leanback.R$layout;
import androidx.leanback.widget.BaseGridView.OnUnhandledKeyListener;
import androidx.leanback.widget.DetailsOverviewRow.Listener;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;

public class FullWidthDetailsOverviewRowPresenter extends RowPresenter {
    static final Handler sHandler = new Handler();
    OnActionClickedListener mActionClickedListener;
    private int mActionsBackgroundColor;
    private boolean mActionsBackgroundColorSet;
    private int mAlignmentMode;
    private int mBackgroundColor;
    private boolean mBackgroundColorSet;
    final DetailsOverviewLogoPresenter mDetailsOverviewLogoPresenter;
    final Presenter mDetailsPresenter;
    protected int mInitialState;
    private boolean mParticipatingEntranceTransition;

    class ActionsItemBridgeAdapter extends ItemBridgeAdapter {
        ViewHolder mViewHolder;

        ActionsItemBridgeAdapter(ViewHolder viewHolder) {
            this.mViewHolder = viewHolder;
        }

        public void onBind(final androidx.leanback.widget.ItemBridgeAdapter.ViewHolder viewHolder) {
            if (this.mViewHolder.getOnItemViewClickedListener() != null || FullWidthDetailsOverviewRowPresenter.this.mActionClickedListener != null) {
                viewHolder.getPresenter().setOnClickListener(viewHolder.getViewHolder(), new OnClickListener() {
                    public void onClick(View view) {
                        if (ActionsItemBridgeAdapter.this.mViewHolder.getOnItemViewClickedListener() != null) {
                            BaseOnItemViewClickedListener onItemViewClickedListener = ActionsItemBridgeAdapter.this.mViewHolder.getOnItemViewClickedListener();
                            androidx.leanback.widget.Presenter.ViewHolder viewHolder = viewHolder.getViewHolder();
                            Object item = viewHolder.getItem();
                            ViewHolder viewHolder2 = ActionsItemBridgeAdapter.this.mViewHolder;
                            onItemViewClickedListener.onItemClicked(viewHolder, item, viewHolder2, viewHolder2.getRow());
                        }
                        OnActionClickedListener onActionClickedListener = FullWidthDetailsOverviewRowPresenter.this.mActionClickedListener;
                        if (onActionClickedListener != null) {
                            onActionClickedListener.onActionClicked((Action) viewHolder.getItem());
                        }
                    }
                });
            }
        }

        public void onUnbind(androidx.leanback.widget.ItemBridgeAdapter.ViewHolder viewHolder) {
            if (this.mViewHolder.getOnItemViewClickedListener() != null || FullWidthDetailsOverviewRowPresenter.this.mActionClickedListener != null) {
                viewHolder.getPresenter().setOnClickListener(viewHolder.getViewHolder(), null);
            }
        }

        public void onAttachedToWindow(androidx.leanback.widget.ItemBridgeAdapter.ViewHolder viewHolder) {
            viewHolder.itemView.removeOnLayoutChangeListener(this.mViewHolder.mLayoutChangeListener);
            viewHolder.itemView.addOnLayoutChangeListener(this.mViewHolder.mLayoutChangeListener);
        }

        public void onDetachedFromWindow(androidx.leanback.widget.ItemBridgeAdapter.ViewHolder viewHolder) {
            viewHolder.itemView.removeOnLayoutChangeListener(this.mViewHolder.mLayoutChangeListener);
            this.mViewHolder.checkFirstAndLastPosition(false);
        }
    }

    public class ViewHolder extends androidx.leanback.widget.RowPresenter.ViewHolder {
        ItemBridgeAdapter mActionBridgeAdapter;
        final HorizontalGridView mActionsRow;
        final OnChildSelectedListener mChildSelectedListener = new OnChildSelectedListener() {
            public void onChildSelected(ViewGroup viewGroup, View view, int i, long j) {
                ViewHolder.this.dispatchItemSelection(view);
            }
        };
        final ViewGroup mDetailsDescriptionFrame;
        final androidx.leanback.widget.Presenter.ViewHolder mDetailsDescriptionViewHolder;
        final androidx.leanback.widget.DetailsOverviewLogoPresenter.ViewHolder mDetailsLogoViewHolder;
        final OnLayoutChangeListener mLayoutChangeListener = new OnLayoutChangeListener() {
            public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                ViewHolder.this.checkFirstAndLastPosition(false);
            }
        };
        int mNumItems;
        final FrameLayout mOverviewFrame;
        final ViewGroup mOverviewRoot;
        protected final Listener mRowListener = createRowListener();
        final OnScrollListener mScrollListener = new OnScrollListener() {
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
            }

            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                ViewHolder.this.checkFirstAndLastPosition(true);
            }
        };
        int mState = 0;
        final Runnable mUpdateDrawableCallback = new Runnable() {
            public void run() {
                Row row = ViewHolder.this.getRow();
                if (row != null) {
                    ViewHolder viewHolder = ViewHolder.this;
                    FullWidthDetailsOverviewRowPresenter.this.mDetailsOverviewLogoPresenter.onBindViewHolder(viewHolder.mDetailsLogoViewHolder, row);
                }
            }
        };

        public class DetailsOverviewRowListener extends Listener {
            public DetailsOverviewRowListener(ViewHolder viewHolder) {
            }
        }

        /* access modifiers changed from: protected */
        public Listener createRowListener() {
            return new DetailsOverviewRowListener(this);
        }

        /* access modifiers changed from: 0000 */
        public void bindActions(ObjectAdapter objectAdapter) {
            this.mActionBridgeAdapter.setAdapter(objectAdapter);
            this.mActionsRow.setAdapter(this.mActionBridgeAdapter);
            this.mNumItems = this.mActionBridgeAdapter.getItemCount();
        }

        /* access modifiers changed from: 0000 */
        public void onBind() {
            DetailsOverviewRow detailsOverviewRow = (DetailsOverviewRow) getRow();
            bindActions(detailsOverviewRow.getActionsAdapter());
            detailsOverviewRow.addListener(this.mRowListener);
        }

        /* access modifiers changed from: 0000 */
        public void onUnbind() {
            ((DetailsOverviewRow) getRow()).removeListener(this.mRowListener);
            FullWidthDetailsOverviewRowPresenter.sHandler.removeCallbacks(this.mUpdateDrawableCallback);
        }

        /* access modifiers changed from: 0000 */
        public void dispatchItemSelection(View view) {
            androidx.recyclerview.widget.RecyclerView.ViewHolder viewHolder;
            if (isSelected()) {
                if (view != null) {
                    viewHolder = this.mActionsRow.getChildViewHolder(view);
                } else {
                    HorizontalGridView horizontalGridView = this.mActionsRow;
                    viewHolder = horizontalGridView.findViewHolderForPosition(horizontalGridView.getSelectedPosition());
                }
                androidx.leanback.widget.ItemBridgeAdapter.ViewHolder viewHolder2 = (androidx.leanback.widget.ItemBridgeAdapter.ViewHolder) viewHolder;
                if (viewHolder2 == null) {
                    if (getOnItemViewSelectedListener() != null) {
                        getOnItemViewSelectedListener().onItemSelected(null, null, this, getRow());
                    }
                } else if (getOnItemViewSelectedListener() != null) {
                    getOnItemViewSelectedListener().onItemSelected(viewHolder2.getViewHolder(), viewHolder2.getItem(), this, getRow());
                }
            }
        }

        /* access modifiers changed from: 0000 */
        public void checkFirstAndLastPosition(boolean z) {
            androidx.recyclerview.widget.RecyclerView.ViewHolder findViewHolderForPosition = this.mActionsRow.findViewHolderForPosition(this.mNumItems - 1);
            if (findViewHolderForPosition != null) {
                int right = findViewHolderForPosition.itemView.getRight();
                int width = this.mActionsRow.getWidth();
            }
            androidx.recyclerview.widget.RecyclerView.ViewHolder findViewHolderForPosition2 = this.mActionsRow.findViewHolderForPosition(0);
            if (findViewHolderForPosition2 != null) {
                int left = findViewHolderForPosition2.itemView.getLeft();
            }
        }

        public ViewHolder(View view, Presenter presenter, DetailsOverviewLogoPresenter detailsOverviewLogoPresenter) {
            super(view);
            this.mOverviewRoot = (ViewGroup) view.findViewById(R$id.details_root);
            this.mOverviewFrame = (FrameLayout) view.findViewById(R$id.details_frame);
            this.mDetailsDescriptionFrame = (ViewGroup) view.findViewById(R$id.details_overview_description);
            HorizontalGridView horizontalGridView = (HorizontalGridView) this.mOverviewFrame.findViewById(R$id.details_overview_actions);
            this.mActionsRow = horizontalGridView;
            horizontalGridView.setHasOverlappingRendering(false);
            this.mActionsRow.setOnScrollListener(this.mScrollListener);
            this.mActionsRow.setAdapter(this.mActionBridgeAdapter);
            this.mActionsRow.setOnChildSelectedListener(this.mChildSelectedListener);
            int dimensionPixelSize = view.getResources().getDimensionPixelSize(R$dimen.lb_details_overview_actions_fade_size);
            this.mActionsRow.setFadingRightEdgeLength(dimensionPixelSize);
            this.mActionsRow.setFadingLeftEdgeLength(dimensionPixelSize);
            androidx.leanback.widget.Presenter.ViewHolder onCreateViewHolder = presenter.onCreateViewHolder(this.mDetailsDescriptionFrame);
            this.mDetailsDescriptionViewHolder = onCreateViewHolder;
            this.mDetailsDescriptionFrame.addView(onCreateViewHolder.view);
            androidx.leanback.widget.DetailsOverviewLogoPresenter.ViewHolder viewHolder = (androidx.leanback.widget.DetailsOverviewLogoPresenter.ViewHolder) detailsOverviewLogoPresenter.onCreateViewHolder(this.mOverviewRoot);
            this.mDetailsLogoViewHolder = viewHolder;
            this.mOverviewRoot.addView(viewHolder.view);
        }

        public final ViewGroup getOverviewView() {
            return this.mOverviewFrame;
        }

        public final androidx.leanback.widget.DetailsOverviewLogoPresenter.ViewHolder getLogoViewHolder() {
            return this.mDetailsLogoViewHolder;
        }

        public final ViewGroup getDetailsDescriptionFrame() {
            return this.mDetailsDescriptionFrame;
        }

        public final ViewGroup getActionsRow() {
            return this.mActionsRow;
        }

        public final int getState() {
            return this.mState;
        }
    }

    /* access modifiers changed from: protected */
    public boolean isClippingChildren() {
        return true;
    }

    public final boolean isUsingDefaultSelectEffect() {
        return false;
    }

    /* access modifiers changed from: protected */
    public int getLayoutResourceId() {
        return R$layout.lb_fullwidth_details_overview;
    }

    /* access modifiers changed from: protected */
    public androidx.leanback.widget.RowPresenter.ViewHolder createRowViewHolder(ViewGroup viewGroup) {
        final ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(getLayoutResourceId(), viewGroup, false), this.mDetailsPresenter, this.mDetailsOverviewLogoPresenter);
        this.mDetailsOverviewLogoPresenter.setContext(viewHolder.mDetailsLogoViewHolder, viewHolder, this);
        setState(viewHolder, this.mInitialState);
        viewHolder.mActionBridgeAdapter = new ActionsItemBridgeAdapter(viewHolder);
        FrameLayout frameLayout = viewHolder.mOverviewFrame;
        if (this.mBackgroundColorSet) {
            frameLayout.setBackgroundColor(this.mBackgroundColor);
        }
        if (this.mActionsBackgroundColorSet) {
            frameLayout.findViewById(R$id.details_overview_actions_background).setBackgroundColor(this.mActionsBackgroundColor);
        }
        RoundedRectHelper.setClipToRoundedOutline(frameLayout, true);
        if (!getSelectEffectEnabled()) {
            viewHolder.mOverviewFrame.setForeground(null);
        }
        viewHolder.mActionsRow.setOnUnhandledKeyListener(new OnUnhandledKeyListener(this) {
            public boolean onUnhandledKey(KeyEvent keyEvent) {
                if (viewHolder.getOnKeyListener() != null) {
                    return viewHolder.getOnKeyListener().onKey(viewHolder.view, keyEvent.getKeyCode(), keyEvent);
                }
                return false;
            }
        });
        return viewHolder;
    }

    /* access modifiers changed from: protected */
    public void onBindRowViewHolder(androidx.leanback.widget.RowPresenter.ViewHolder viewHolder, Object obj) {
        super.onBindRowViewHolder(viewHolder, obj);
        DetailsOverviewRow detailsOverviewRow = (DetailsOverviewRow) obj;
        ViewHolder viewHolder2 = (ViewHolder) viewHolder;
        this.mDetailsOverviewLogoPresenter.onBindViewHolder(viewHolder2.mDetailsLogoViewHolder, detailsOverviewRow);
        this.mDetailsPresenter.onBindViewHolder(viewHolder2.mDetailsDescriptionViewHolder, detailsOverviewRow.getItem());
        viewHolder2.onBind();
    }

    /* access modifiers changed from: protected */
    public void onUnbindRowViewHolder(androidx.leanback.widget.RowPresenter.ViewHolder viewHolder) {
        ViewHolder viewHolder2 = (ViewHolder) viewHolder;
        viewHolder2.onUnbind();
        this.mDetailsPresenter.onUnbindViewHolder(viewHolder2.mDetailsDescriptionViewHolder);
        this.mDetailsOverviewLogoPresenter.onUnbindViewHolder(viewHolder2.mDetailsLogoViewHolder);
        super.onUnbindRowViewHolder(viewHolder);
    }

    /* access modifiers changed from: protected */
    public void onSelectLevelChanged(androidx.leanback.widget.RowPresenter.ViewHolder viewHolder) {
        super.onSelectLevelChanged(viewHolder);
        if (getSelectEffectEnabled()) {
            ViewHolder viewHolder2 = (ViewHolder) viewHolder;
            ((ColorDrawable) viewHolder2.mOverviewFrame.getForeground().mutate()).setColor(viewHolder2.mColorDimmer.getPaint().getColor());
        }
    }

    /* access modifiers changed from: protected */
    public void onRowViewAttachedToWindow(androidx.leanback.widget.RowPresenter.ViewHolder viewHolder) {
        super.onRowViewAttachedToWindow(viewHolder);
        ViewHolder viewHolder2 = (ViewHolder) viewHolder;
        this.mDetailsPresenter.onViewAttachedToWindow(viewHolder2.mDetailsDescriptionViewHolder);
        this.mDetailsOverviewLogoPresenter.onViewAttachedToWindow(viewHolder2.mDetailsLogoViewHolder);
    }

    /* access modifiers changed from: protected */
    public void onRowViewDetachedFromWindow(androidx.leanback.widget.RowPresenter.ViewHolder viewHolder) {
        super.onRowViewDetachedFromWindow(viewHolder);
        ViewHolder viewHolder2 = (ViewHolder) viewHolder;
        this.mDetailsPresenter.onViewDetachedFromWindow(viewHolder2.mDetailsDescriptionViewHolder);
        this.mDetailsOverviewLogoPresenter.onViewDetachedFromWindow(viewHolder2.mDetailsLogoViewHolder);
    }

    /* access modifiers changed from: protected */
    public void onLayoutLogo(ViewHolder viewHolder, int i, boolean z) {
        View view = viewHolder.getLogoViewHolder().view;
        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) view.getLayoutParams();
        if (this.mAlignmentMode != 1) {
            marginLayoutParams.setMarginStart(view.getResources().getDimensionPixelSize(R$dimen.lb_details_v2_logo_margin_start));
        } else {
            marginLayoutParams.setMarginStart(view.getResources().getDimensionPixelSize(R$dimen.lb_details_v2_left) - marginLayoutParams.width);
        }
        int state = viewHolder.getState();
        if (state == 0) {
            marginLayoutParams.topMargin = view.getResources().getDimensionPixelSize(R$dimen.lb_details_v2_blank_height) + view.getResources().getDimensionPixelSize(R$dimen.lb_details_v2_actions_height) + view.getResources().getDimensionPixelSize(R$dimen.lb_details_v2_description_margin_top);
        } else if (state != 2) {
            marginLayoutParams.topMargin = view.getResources().getDimensionPixelSize(R$dimen.lb_details_v2_blank_height) - (marginLayoutParams.height / 2);
        } else {
            marginLayoutParams.topMargin = 0;
        }
        view.setLayoutParams(marginLayoutParams);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x006e  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0070  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00a3  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onLayoutOverviewFrame(androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter.ViewHolder r6, int r7, boolean r8) {
        /*
            r5 = this;
            r0 = 2
            r1 = 1
            r2 = 0
            if (r7 != r0) goto L_0x0007
            r7 = r1
            goto L_0x0008
        L_0x0007:
            r7 = r2
        L_0x0008:
            int r3 = r6.getState()
            if (r3 != r0) goto L_0x0010
            r0 = r1
            goto L_0x0011
        L_0x0010:
            r0 = r2
        L_0x0011:
            if (r7 != r0) goto L_0x0015
            if (r8 == 0) goto L_0x00ae
        L_0x0015:
            android.view.View r7 = r6.view
            android.content.res.Resources r7 = r7.getResources()
            androidx.leanback.widget.DetailsOverviewLogoPresenter r8 = r5.mDetailsOverviewLogoPresenter
            androidx.leanback.widget.DetailsOverviewLogoPresenter$ViewHolder r3 = r6.getLogoViewHolder()
            androidx.leanback.widget.Row r4 = r6.getRow()
            androidx.leanback.widget.DetailsOverviewRow r4 = (androidx.leanback.widget.DetailsOverviewRow) r4
            boolean r8 = r8.isBoundToImage(r3, r4)
            if (r8 == 0) goto L_0x003a
            androidx.leanback.widget.DetailsOverviewLogoPresenter$ViewHolder r8 = r6.getLogoViewHolder()
            android.view.View r8 = r8.view
            android.view.ViewGroup$LayoutParams r8 = r8.getLayoutParams()
            int r8 = r8.width
            goto L_0x003b
        L_0x003a:
            r8 = r2
        L_0x003b:
            int r5 = r5.mAlignmentMode
            if (r5 == r1) goto L_0x0051
            if (r0 == 0) goto L_0x0048
            int r5 = androidx.leanback.R$dimen.lb_details_v2_logo_margin_start
            int r5 = r7.getDimensionPixelSize(r5)
            goto L_0x0062
        L_0x0048:
            int r5 = androidx.leanback.R$dimen.lb_details_v2_logo_margin_start
            int r5 = r7.getDimensionPixelSize(r5)
            int r8 = r8 + r5
        L_0x004f:
            r5 = r2
            goto L_0x0062
        L_0x0051:
            if (r0 == 0) goto L_0x005b
            int r5 = androidx.leanback.R$dimen.lb_details_v2_left
            int r5 = r7.getDimensionPixelSize(r5)
            int r5 = r5 - r8
            goto L_0x0062
        L_0x005b:
            int r5 = androidx.leanback.R$dimen.lb_details_v2_left
            int r8 = r7.getDimensionPixelSize(r5)
            goto L_0x004f
        L_0x0062:
            android.view.ViewGroup r1 = r6.getOverviewView()
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.view.ViewGroup$MarginLayoutParams r1 = (android.view.ViewGroup.MarginLayoutParams) r1
            if (r0 == 0) goto L_0x0070
            r3 = r2
            goto L_0x0076
        L_0x0070:
            int r3 = androidx.leanback.R$dimen.lb_details_v2_blank_height
            int r3 = r7.getDimensionPixelSize(r3)
        L_0x0076:
            r1.topMargin = r3
            r1.rightMargin = r5
            r1.leftMargin = r5
            android.view.ViewGroup r5 = r6.getOverviewView()
            r5.setLayoutParams(r1)
            android.view.ViewGroup r5 = r6.getDetailsDescriptionFrame()
            android.view.ViewGroup$LayoutParams r1 = r5.getLayoutParams()
            android.view.ViewGroup$MarginLayoutParams r1 = (android.view.ViewGroup.MarginLayoutParams) r1
            r1.setMarginStart(r8)
            r5.setLayoutParams(r1)
            android.view.ViewGroup r5 = r6.getActionsRow()
            android.view.ViewGroup$LayoutParams r6 = r5.getLayoutParams()
            android.view.ViewGroup$MarginLayoutParams r6 = (android.view.ViewGroup.MarginLayoutParams) r6
            r6.setMarginStart(r8)
            if (r0 == 0) goto L_0x00a3
            goto L_0x00a9
        L_0x00a3:
            int r8 = androidx.leanback.R$dimen.lb_details_v2_actions_height
            int r2 = r7.getDimensionPixelSize(r8)
        L_0x00a9:
            r6.height = r2
            r5.setLayoutParams(r6)
        L_0x00ae:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter.onLayoutOverviewFrame(androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter$ViewHolder, int, boolean):void");
    }

    public final void setState(ViewHolder viewHolder, int i) {
        if (viewHolder.getState() != i) {
            int state = viewHolder.getState();
            viewHolder.mState = i;
            onStateChanged(viewHolder, state);
        }
    }

    /* access modifiers changed from: protected */
    public void onStateChanged(ViewHolder viewHolder, int i) {
        onLayoutOverviewFrame(viewHolder, i, false);
        onLayoutLogo(viewHolder, i, false);
    }

    public void setEntranceTransitionState(androidx.leanback.widget.RowPresenter.ViewHolder viewHolder, boolean z) {
        super.setEntranceTransitionState(viewHolder, z);
        if (this.mParticipatingEntranceTransition) {
            viewHolder.view.setVisibility(z ? 0 : 4);
        }
    }
}
