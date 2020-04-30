package androidx.slice.widget;

import android.content.Context;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout.LayoutParams;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.slice.SliceItem;
import androidx.slice.core.SliceAction;
import androidx.slice.widget.SliceView.OnSliceActionListener;
import androidx.slice.widget.SliceViewPolicy.PolicyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TemplateView extends SliceChildView implements PolicyChangeListener {
    private final SliceAdapter mAdapter;
    private ArrayList<SliceContent> mDisplayedItems = new ArrayList<>();
    private int mDisplayedItemsHeight = 0;
    private final View mForeground;
    private ListContent mListContent;
    private int[] mLoc = new int[2];
    private SliceView mParent;
    private final RecyclerView mRecyclerView;

    public TemplateView(Context context) {
        super(context);
        RecyclerView recyclerView = new RecyclerView(getContext());
        this.mRecyclerView = recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        SliceAdapter sliceAdapter = new SliceAdapter(context);
        this.mAdapter = sliceAdapter;
        this.mRecyclerView.setAdapter(sliceAdapter);
        addView(this.mRecyclerView);
        View view = new View(getContext());
        this.mForeground = view;
        view.setBackground(SliceViewUtil.getDrawable(getContext(), 16843534));
        addView(this.mForeground);
        LayoutParams layoutParams = (LayoutParams) this.mForeground.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        this.mForeground.setLayoutParams(layoutParams);
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        SliceView sliceView = (SliceView) getParent();
        this.mParent = sliceView;
        this.mAdapter.setParents(sliceView, this);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int size = MeasureSpec.getSize(i2);
        if (!this.mViewPolicy.isScrollable() && this.mDisplayedItems.size() > 0 && this.mDisplayedItemsHeight != size) {
            updateDisplayedItems(size);
        }
        super.onMeasure(i, i2);
    }

    public void setInsets(int i, int i2, int i3, int i4) {
        super.setInsets(i, i2, i3, i4);
        this.mAdapter.setInsets(i, i2, i3, i4);
    }

    public void onForegroundActivated(MotionEvent motionEvent) {
        SliceView sliceView = this.mParent;
        if (sliceView == null || sliceView.isSliceViewClickable()) {
            if (VERSION.SDK_INT >= 21) {
                this.mForeground.getLocationOnScreen(this.mLoc);
                this.mForeground.getBackground().setHotspot((float) ((int) (motionEvent.getRawX() - ((float) this.mLoc[0]))), (float) ((int) (motionEvent.getRawY() - ((float) this.mLoc[1]))));
            }
            int actionMasked = motionEvent.getActionMasked();
            if (actionMasked == 0) {
                this.mForeground.setPressed(true);
            } else if (actionMasked == 3 || actionMasked == 1 || actionMasked == 2) {
                this.mForeground.setPressed(false);
            }
            return;
        }
        this.mForeground.setPressed(false);
    }

    public void setPolicy(SliceViewPolicy sliceViewPolicy) {
        super.setPolicy(sliceViewPolicy);
        this.mAdapter.setPolicy(sliceViewPolicy);
        sliceViewPolicy.setListener(this);
    }

    public void setActionLoading(SliceItem sliceItem) {
        this.mAdapter.onSliceActionLoading(sliceItem, 0);
    }

    public void setLoadingActions(Set<SliceItem> set) {
        this.mAdapter.setLoadingActions(set);
    }

    public void setTint(int i) {
        super.setTint(i);
        updateDisplayedItems(getMeasuredHeight());
    }

    public void setSliceActionListener(OnSliceActionListener onSliceActionListener) {
        this.mObserver = onSliceActionListener;
        SliceAdapter sliceAdapter = this.mAdapter;
        if (sliceAdapter != null) {
            sliceAdapter.setSliceObserver(onSliceActionListener);
        }
    }

    public void setSliceActions(List<SliceAction> list) {
        this.mAdapter.setSliceActions(list);
    }

    public void setSliceContent(ListContent listContent) {
        this.mListContent = listContent;
        updateDisplayedItems(listContent.getHeight(this.mSliceStyle, this.mViewPolicy));
    }

    public void setStyle(SliceStyle sliceStyle) {
        super.setStyle(sliceStyle);
        this.mAdapter.setStyle(sliceStyle);
    }

    public void setShowLastUpdated(boolean z) {
        super.setShowLastUpdated(z);
        this.mAdapter.setShowLastUpdated(z);
    }

    public void setLastUpdated(long j) {
        super.setLastUpdated(j);
        this.mAdapter.setLastUpdated(j);
    }

    public void setAllowTwoLines(boolean z) {
        this.mAdapter.setAllowTwoLines(z);
    }

    private void updateDisplayedItems(int i) {
        ListContent listContent = this.mListContent;
        if (listContent == null || !listContent.isValid()) {
            resetView();
            return;
        }
        ArrayList<SliceContent> rowItems = this.mListContent.getRowItems(i, this.mSliceStyle, this.mViewPolicy);
        this.mDisplayedItems = rowItems;
        this.mDisplayedItemsHeight = ListContent.getListHeight(rowItems, this.mSliceStyle, this.mViewPolicy);
        this.mAdapter.setSliceItems(this.mDisplayedItems, this.mTintColor, this.mViewPolicy.getMode());
        updateOverscroll();
    }

    private void updateOverscroll() {
        int i = 1;
        boolean z = this.mDisplayedItemsHeight > getMeasuredHeight();
        RecyclerView recyclerView = this.mRecyclerView;
        if (!this.mViewPolicy.isScrollable() || !z) {
            i = 2;
        }
        recyclerView.setOverScrollMode(i);
    }

    public void resetView() {
        this.mDisplayedItemsHeight = 0;
        this.mDisplayedItems.clear();
        this.mAdapter.setSliceItems(null, -1, getMode());
        this.mListContent = null;
    }

    public void onMaxHeightChanged(int i) {
        ListContent listContent = this.mListContent;
        if (listContent != null) {
            updateDisplayedItems(listContent.getHeight(this.mSliceStyle, this.mViewPolicy));
        }
    }

    public void onMaxSmallChanged(int i) {
        SliceAdapter sliceAdapter = this.mAdapter;
        if (sliceAdapter != null) {
            sliceAdapter.notifyHeaderChanged();
        }
    }
}
