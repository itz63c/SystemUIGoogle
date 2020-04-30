package androidx.recyclerview.widget;

import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import androidx.recyclerview.widget.RecyclerView.OnFlingListener;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.RecyclerView.SmoothScroller;
import androidx.recyclerview.widget.RecyclerView.SmoothScroller.ScrollVectorProvider;

public abstract class SnapHelper extends OnFlingListener {
    RecyclerView mRecyclerView;
    private final OnScrollListener mScrollListener = new OnScrollListener() {
        boolean mScrolled = false;

        public void onScrollStateChanged(RecyclerView recyclerView, int i) {
            super.onScrollStateChanged(recyclerView, i);
            if (i == 0 && this.mScrolled) {
                this.mScrolled = false;
                SnapHelper.this.snapToTargetExistingView();
            }
        }

        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            if (i != 0 || i2 != 0) {
                this.mScrolled = true;
            }
        }
    };

    public abstract int[] calculateDistanceToFinalSnap(LayoutManager layoutManager, View view);

    /* access modifiers changed from: protected */
    public abstract SmoothScroller createScroller(LayoutManager layoutManager);

    public abstract View findSnapView(LayoutManager layoutManager);

    public abstract int findTargetSnapPosition(LayoutManager layoutManager, int i, int i2);

    public boolean onFling(int i, int i2) {
        LayoutManager layoutManager = this.mRecyclerView.getLayoutManager();
        boolean z = false;
        if (layoutManager == null || this.mRecyclerView.getAdapter() == null) {
            return false;
        }
        int minFlingVelocity = this.mRecyclerView.getMinFlingVelocity();
        if ((Math.abs(i2) > minFlingVelocity || Math.abs(i) > minFlingVelocity) && snapFromFling(layoutManager, i, i2)) {
            z = true;
        }
        return z;
    }

    public void attachToRecyclerView(RecyclerView recyclerView) throws IllegalStateException {
        RecyclerView recyclerView2 = this.mRecyclerView;
        if (recyclerView2 != recyclerView) {
            if (recyclerView2 != null) {
                destroyCallbacks();
            }
            this.mRecyclerView = recyclerView;
            if (recyclerView != null) {
                setupCallbacks();
                new Scroller(this.mRecyclerView.getContext(), new DecelerateInterpolator());
                snapToTargetExistingView();
            }
        }
    }

    private void setupCallbacks() throws IllegalStateException {
        if (this.mRecyclerView.getOnFlingListener() == null) {
            this.mRecyclerView.addOnScrollListener(this.mScrollListener);
            this.mRecyclerView.setOnFlingListener(this);
            return;
        }
        throw new IllegalStateException("An instance of OnFlingListener already set.");
    }

    private void destroyCallbacks() {
        this.mRecyclerView.removeOnScrollListener(this.mScrollListener);
        this.mRecyclerView.setOnFlingListener(null);
    }

    private boolean snapFromFling(LayoutManager layoutManager, int i, int i2) {
        if (!(layoutManager instanceof ScrollVectorProvider)) {
            return false;
        }
        SmoothScroller createScroller = createScroller(layoutManager);
        if (createScroller == null) {
            return false;
        }
        int findTargetSnapPosition = findTargetSnapPosition(layoutManager, i, i2);
        if (findTargetSnapPosition == -1) {
            return false;
        }
        createScroller.setTargetPosition(findTargetSnapPosition);
        layoutManager.startSmoothScroll(createScroller);
        return true;
    }

    /* access modifiers changed from: 0000 */
    public void snapToTargetExistingView() {
        RecyclerView recyclerView = this.mRecyclerView;
        if (recyclerView != null) {
            LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager != null) {
                View findSnapView = findSnapView(layoutManager);
                if (findSnapView != null) {
                    int[] calculateDistanceToFinalSnap = calculateDistanceToFinalSnap(layoutManager, findSnapView);
                    if (!(calculateDistanceToFinalSnap[0] == 0 && calculateDistanceToFinalSnap[1] == 0)) {
                        this.mRecyclerView.smoothScrollBy(calculateDistanceToFinalSnap[0], calculateDistanceToFinalSnap[1]);
                    }
                }
            }
        }
    }
}
