package androidx.leanback.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.AccelerateDecelerateInterpolator;
import androidx.collection.CircularIntArray;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.CollectionInfoCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.CollectionItemInfoCompat;
import androidx.leanback.widget.BaseGridView.OnLayoutCompletedListener;
import androidx.leanback.widget.Grid.Location;
import androidx.leanback.widget.Grid.Provider;
import androidx.leanback.widget.ItemAlignmentFacet.ItemAlignmentDef;
import androidx.leanback.widget.WindowAlignment.Axis;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import androidx.recyclerview.widget.RecyclerView.LayoutManager.LayoutPrefetchRegistry;
import androidx.recyclerview.widget.RecyclerView.OnScrollListener;
import androidx.recyclerview.widget.RecyclerView.Recycler;
import androidx.recyclerview.widget.RecyclerView.SmoothScroller;
import androidx.recyclerview.widget.RecyclerView.SmoothScroller.Action;
import androidx.recyclerview.widget.RecyclerView.State;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

final class GridLayoutManager extends LayoutManager {
    private static final Rect sTempRect = new Rect();
    static int[] sTwoInts = new int[2];
    final BaseGridView mBaseGridView;
    OnChildLaidOutListener mChildLaidOutListener = null;
    private OnChildSelectedListener mChildSelectedListener = null;
    private ArrayList<OnChildViewHolderSelectedListener> mChildViewHolderSelectedListeners = null;
    int mChildVisibility;
    final ViewsStateBundle mChildrenStates = new ViewsStateBundle();
    GridLinearSmoothScroller mCurrentSmoothScroller;
    int[] mDisappearingPositions;
    private int mExtraLayoutSpace;
    int mExtraLayoutSpaceInPreLayout;
    private FacetProviderAdapter mFacetProviderAdapter;
    private int mFixedRowSizeSecondary;
    int mFlag = 221696;
    int mFocusPosition = -1;
    private int mFocusPositionOffset = 0;
    private int mFocusScrollStrategy = 0;
    private int mGravity = 8388659;
    Grid mGrid;
    private Provider mGridProvider = new Provider() {
        public int getMinIndex() {
            return GridLayoutManager.this.mPositionDeltaInPreLayout;
        }

        public int getCount() {
            return GridLayoutManager.this.mState.getItemCount() + GridLayoutManager.this.mPositionDeltaInPreLayout;
        }

        /* JADX WARNING: type inference failed for: r8v0, types: [java.lang.Object[]] */
        /* JADX WARNING: Incorrect type for immutable var: ssa=java.lang.Object[], code=null, for r8v0, types: [java.lang.Object[]] */
        /* JADX WARNING: Unknown variable types count: 1 */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public int createItem(int r6, boolean r7, java.lang.Object[] r8, boolean r9) {
            /*
                r5 = this;
                androidx.leanback.widget.GridLayoutManager r0 = androidx.leanback.widget.GridLayoutManager.this
                int r1 = r0.mPositionDeltaInPreLayout
                int r1 = r6 - r1
                android.view.View r0 = r0.getViewForPosition(r1)
                android.view.ViewGroup$LayoutParams r1 = r0.getLayoutParams()
                androidx.leanback.widget.GridLayoutManager$LayoutParams r1 = (androidx.leanback.widget.GridLayoutManager.LayoutParams) r1
                boolean r1 = r1.isItemRemoved()
                r2 = 0
                if (r1 != 0) goto L_0x00a4
                if (r9 == 0) goto L_0x0027
                if (r7 == 0) goto L_0x0021
                androidx.leanback.widget.GridLayoutManager r7 = androidx.leanback.widget.GridLayoutManager.this
                r7.addDisappearingView(r0)
                goto L_0x0034
            L_0x0021:
                androidx.leanback.widget.GridLayoutManager r7 = androidx.leanback.widget.GridLayoutManager.this
                r7.addDisappearingView(r0, r2)
                goto L_0x0034
            L_0x0027:
                if (r7 == 0) goto L_0x002f
                androidx.leanback.widget.GridLayoutManager r7 = androidx.leanback.widget.GridLayoutManager.this
                r7.addView(r0)
                goto L_0x0034
            L_0x002f:
                androidx.leanback.widget.GridLayoutManager r7 = androidx.leanback.widget.GridLayoutManager.this
                r7.addView(r0, r2)
            L_0x0034:
                androidx.leanback.widget.GridLayoutManager r7 = androidx.leanback.widget.GridLayoutManager.this
                int r7 = r7.mChildVisibility
                r9 = -1
                if (r7 == r9) goto L_0x003e
                r0.setVisibility(r7)
            L_0x003e:
                androidx.leanback.widget.GridLayoutManager r7 = androidx.leanback.widget.GridLayoutManager.this
                androidx.leanback.widget.GridLayoutManager$PendingMoveSmoothScroller r7 = r7.mPendingMoveSmoothScroller
                if (r7 == 0) goto L_0x0047
                r7.consumePendingMovesBeforeLayout()
            L_0x0047:
                androidx.leanback.widget.GridLayoutManager r7 = androidx.leanback.widget.GridLayoutManager.this
                android.view.View r9 = r0.findFocus()
                int r7 = r7.getSubPositionByView(r0, r9)
                androidx.leanback.widget.GridLayoutManager r9 = androidx.leanback.widget.GridLayoutManager.this
                int r1 = r9.mFlag
                r3 = r1 & 3
                r4 = 1
                if (r3 == r4) goto L_0x006a
                int r1 = r9.mFocusPosition
                if (r6 != r1) goto L_0x009f
                int r6 = r9.mSubFocusPosition
                if (r7 != r6) goto L_0x009f
                androidx.leanback.widget.GridLayoutManager$PendingMoveSmoothScroller r6 = r9.mPendingMoveSmoothScroller
                if (r6 != 0) goto L_0x009f
                r9.dispatchChildSelected()
                goto L_0x009f
            L_0x006a:
                r3 = r1 & 4
                if (r3 != 0) goto L_0x009f
                r1 = r1 & 16
                if (r1 != 0) goto L_0x007e
                int r1 = r9.mFocusPosition
                if (r6 != r1) goto L_0x007e
                int r1 = r9.mSubFocusPosition
                if (r7 != r1) goto L_0x007e
                r9.dispatchChildSelected()
                goto L_0x009f
            L_0x007e:
                androidx.leanback.widget.GridLayoutManager r9 = androidx.leanback.widget.GridLayoutManager.this
                int r1 = r9.mFlag
                r1 = r1 & 16
                if (r1 == 0) goto L_0x009f
                int r9 = r9.mFocusPosition
                if (r6 < r9) goto L_0x009f
                boolean r9 = r0.hasFocusable()
                if (r9 == 0) goto L_0x009f
                androidx.leanback.widget.GridLayoutManager r9 = androidx.leanback.widget.GridLayoutManager.this
                r9.mFocusPosition = r6
                r9.mSubFocusPosition = r7
                int r6 = r9.mFlag
                r6 = r6 & -17
                r9.mFlag = r6
                r9.dispatchChildSelected()
            L_0x009f:
                androidx.leanback.widget.GridLayoutManager r6 = androidx.leanback.widget.GridLayoutManager.this
                r6.measureChild(r0)
            L_0x00a4:
                r8[r2] = r0
                androidx.leanback.widget.GridLayoutManager r5 = androidx.leanback.widget.GridLayoutManager.this
                int r6 = r5.mOrientation
                if (r6 != 0) goto L_0x00b1
                int r5 = r5.getDecoratedMeasuredWidthWithMargin(r0)
                goto L_0x00b5
            L_0x00b1:
                int r5 = r5.getDecoratedMeasuredHeightWithMargin(r0)
            L_0x00b5:
                return r5
            */
            throw new UnsupportedOperationException("Method not decompiled: androidx.leanback.widget.GridLayoutManager.C02922.createItem(int, boolean, java.lang.Object[], boolean):int");
        }

        public void addItem(Object obj, int i, int i2, int i3, int i4) {
            int i5;
            int i6;
            long j;
            View view = (View) obj;
            if (i4 == Integer.MIN_VALUE || i4 == Integer.MAX_VALUE) {
                if (!GridLayoutManager.this.mGrid.isReversedFlow()) {
                    i4 = GridLayoutManager.this.mWindowAlignment.mainAxis().getPaddingMin();
                } else {
                    i4 = GridLayoutManager.this.mWindowAlignment.mainAxis().getSize() - GridLayoutManager.this.mWindowAlignment.mainAxis().getPaddingMax();
                }
            }
            if (!GridLayoutManager.this.mGrid.isReversedFlow()) {
                i5 = i2 + i4;
                i6 = i4;
            } else {
                i6 = i4 - i2;
                i5 = i4;
            }
            int rowStartSecondary = GridLayoutManager.this.getRowStartSecondary(i3) + GridLayoutManager.this.mWindowAlignment.secondAxis().getPaddingMin();
            GridLayoutManager gridLayoutManager = GridLayoutManager.this;
            int i7 = rowStartSecondary - gridLayoutManager.mScrollOffsetSecondary;
            gridLayoutManager.mChildrenStates.loadView(view, i);
            GridLayoutManager.this.layoutChild(i3, view, i6, i5, i7);
            if (!GridLayoutManager.this.mState.isPreLayout()) {
                GridLayoutManager.this.updateScrollLimits();
            }
            GridLayoutManager gridLayoutManager2 = GridLayoutManager.this;
            if ((gridLayoutManager2.mFlag & 3) != 1) {
                PendingMoveSmoothScroller pendingMoveSmoothScroller = gridLayoutManager2.mPendingMoveSmoothScroller;
                if (pendingMoveSmoothScroller != null) {
                    pendingMoveSmoothScroller.consumePendingMovesAfterLayout();
                }
            }
            GridLayoutManager gridLayoutManager3 = GridLayoutManager.this;
            if (gridLayoutManager3.mChildLaidOutListener != null) {
                ViewHolder childViewHolder = gridLayoutManager3.mBaseGridView.getChildViewHolder(view);
                GridLayoutManager gridLayoutManager4 = GridLayoutManager.this;
                OnChildLaidOutListener onChildLaidOutListener = gridLayoutManager4.mChildLaidOutListener;
                BaseGridView baseGridView = gridLayoutManager4.mBaseGridView;
                if (childViewHolder == null) {
                    j = -1;
                } else {
                    j = childViewHolder.getItemId();
                }
                onChildLaidOutListener.onChildLaidOut(baseGridView, view, i, j);
            }
        }

        public void removeItem(int i) {
            GridLayoutManager gridLayoutManager = GridLayoutManager.this;
            View findViewByPosition = gridLayoutManager.findViewByPosition(i - gridLayoutManager.mPositionDeltaInPreLayout);
            GridLayoutManager gridLayoutManager2 = GridLayoutManager.this;
            if ((gridLayoutManager2.mFlag & 3) == 1) {
                gridLayoutManager2.detachAndScrapView(findViewByPosition, gridLayoutManager2.mRecycler);
            } else {
                gridLayoutManager2.removeAndRecycleView(findViewByPosition, gridLayoutManager2.mRecycler);
            }
        }

        public int getEdge(int i) {
            GridLayoutManager gridLayoutManager = GridLayoutManager.this;
            View findViewByPosition = gridLayoutManager.findViewByPosition(i - gridLayoutManager.mPositionDeltaInPreLayout);
            GridLayoutManager gridLayoutManager2 = GridLayoutManager.this;
            return (gridLayoutManager2.mFlag & 262144) != 0 ? gridLayoutManager2.getViewMax(findViewByPosition) : gridLayoutManager2.getViewMin(findViewByPosition);
        }

        public int getSize(int i) {
            GridLayoutManager gridLayoutManager = GridLayoutManager.this;
            return gridLayoutManager.getViewPrimarySize(gridLayoutManager.findViewByPosition(i - gridLayoutManager.mPositionDeltaInPreLayout));
        }
    };
    private final ItemAlignment mItemAlignment = new ItemAlignment();
    int mMaxPendingMoves = 10;
    private int mMaxSizeSecondary;
    private int[] mMeasuredDimension = new int[2];
    int mNumRows;
    private int mNumRowsRequested = 1;
    ArrayList<OnLayoutCompletedListener> mOnLayoutCompletedListeners = null;
    int mOrientation = 0;
    private OrientationHelper mOrientationHelper = OrientationHelper.createHorizontalHelper(this);
    PendingMoveSmoothScroller mPendingMoveSmoothScroller;
    int mPositionDeltaInPreLayout;
    final SparseIntArray mPositionToRowInPostLayout = new SparseIntArray();
    private int mPrimaryScrollExtra;
    Recycler mRecycler;
    private final Runnable mRequestLayoutRunnable = new Runnable() {
        public void run() {
            GridLayoutManager.this.requestLayout();
        }
    };
    private int[] mRowSizeSecondary;
    private int mRowSizeSecondaryRequested;
    int mScrollOffsetSecondary;
    private int mSizePrimary;
    float mSmoothScrollSpeedFactor = 1.0f;
    private int mSpacingPrimary;
    private int mSpacingSecondary;
    State mState;
    int mSubFocusPosition = 0;
    private int mVerticalSpacing;
    final WindowAlignment mWindowAlignment = new WindowAlignment();

    abstract class GridLinearSmoothScroller extends LinearSmoothScroller {
        boolean mSkipOnStopInternal;

        GridLinearSmoothScroller() {
            super(GridLayoutManager.this.mBaseGridView.getContext());
        }

        /* access modifiers changed from: protected */
        public void onStop() {
            super.onStop();
            if (!this.mSkipOnStopInternal) {
                onStopInternal();
            }
            GridLayoutManager gridLayoutManager = GridLayoutManager.this;
            if (gridLayoutManager.mCurrentSmoothScroller == this) {
                gridLayoutManager.mCurrentSmoothScroller = null;
            }
            GridLayoutManager gridLayoutManager2 = GridLayoutManager.this;
            if (gridLayoutManager2.mPendingMoveSmoothScroller == this) {
                gridLayoutManager2.mPendingMoveSmoothScroller = null;
            }
        }

        /* access modifiers changed from: protected */
        public void onStopInternal() {
            View findViewByPosition = findViewByPosition(getTargetPosition());
            if (findViewByPosition == null) {
                if (getTargetPosition() >= 0) {
                    GridLayoutManager.this.scrollToSelection(getTargetPosition(), 0, false, 0);
                }
                return;
            }
            if (GridLayoutManager.this.mFocusPosition != getTargetPosition()) {
                GridLayoutManager.this.mFocusPosition = getTargetPosition();
            }
            if (GridLayoutManager.this.hasFocus()) {
                GridLayoutManager.this.mFlag |= 32;
                findViewByPosition.requestFocus();
                GridLayoutManager.this.mFlag &= -33;
            }
            GridLayoutManager.this.dispatchChildSelected();
            GridLayoutManager.this.dispatchChildSelectedAndPositioned();
        }

        /* access modifiers changed from: protected */
        public float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
            return super.calculateSpeedPerPixel(displayMetrics) * GridLayoutManager.this.mSmoothScrollSpeedFactor;
        }

        /* access modifiers changed from: protected */
        public int calculateTimeForScrolling(int i) {
            int calculateTimeForScrolling = super.calculateTimeForScrolling(i);
            if (GridLayoutManager.this.mWindowAlignment.mainAxis().getSize() <= 0) {
                return calculateTimeForScrolling;
            }
            float size = (30.0f / ((float) GridLayoutManager.this.mWindowAlignment.mainAxis().getSize())) * ((float) i);
            return ((float) calculateTimeForScrolling) < size ? (int) size : calculateTimeForScrolling;
        }

        /* access modifiers changed from: protected */
        public void onTargetFound(View view, State state, Action action) {
            int i;
            int i2;
            if (GridLayoutManager.this.getScrollPosition(view, null, GridLayoutManager.sTwoInts)) {
                if (GridLayoutManager.this.mOrientation == 0) {
                    int[] iArr = GridLayoutManager.sTwoInts;
                    i = iArr[0];
                    i2 = iArr[1];
                } else {
                    int[] iArr2 = GridLayoutManager.sTwoInts;
                    int i3 = iArr2[1];
                    i2 = iArr2[0];
                    i = i3;
                }
                action.update(i, i2, calculateTimeForDeceleration((int) Math.sqrt((double) ((i * i) + (i2 * i2)))), this.mDecelerateInterpolator);
            }
        }
    }

    static final class LayoutParams extends androidx.recyclerview.widget.RecyclerView.LayoutParams {
        private int[] mAlignMultiple;
        private int mAlignX;
        private int mAlignY;
        private ItemAlignmentFacet mAlignmentFacet;
        int mBottomInset;
        int mLeftInset;
        int mRightInset;
        int mTopInset;

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        public LayoutParams(int i, int i2) {
            super(i, i2);
        }

        public LayoutParams(MarginLayoutParams marginLayoutParams) {
            super(marginLayoutParams);
        }

        public LayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
        }

        public LayoutParams(androidx.recyclerview.widget.RecyclerView.LayoutParams layoutParams) {
            super(layoutParams);
        }

        public LayoutParams(LayoutParams layoutParams) {
            super((androidx.recyclerview.widget.RecyclerView.LayoutParams) layoutParams);
        }

        /* access modifiers changed from: 0000 */
        public int getAlignX() {
            return this.mAlignX;
        }

        /* access modifiers changed from: 0000 */
        public int getAlignY() {
            return this.mAlignY;
        }

        /* access modifiers changed from: 0000 */
        public int getOpticalLeft(View view) {
            return view.getLeft() + this.mLeftInset;
        }

        /* access modifiers changed from: 0000 */
        public int getOpticalTop(View view) {
            return view.getTop() + this.mTopInset;
        }

        /* access modifiers changed from: 0000 */
        public int getOpticalRight(View view) {
            return view.getRight() - this.mRightInset;
        }

        /* access modifiers changed from: 0000 */
        public int getOpticalWidth(View view) {
            return (view.getWidth() - this.mLeftInset) - this.mRightInset;
        }

        /* access modifiers changed from: 0000 */
        public int getOpticalHeight(View view) {
            return (view.getHeight() - this.mTopInset) - this.mBottomInset;
        }

        /* access modifiers changed from: 0000 */
        public int getOpticalLeftInset() {
            return this.mLeftInset;
        }

        /* access modifiers changed from: 0000 */
        public int getOpticalRightInset() {
            return this.mRightInset;
        }

        /* access modifiers changed from: 0000 */
        public int getOpticalTopInset() {
            return this.mTopInset;
        }

        /* access modifiers changed from: 0000 */
        public void setAlignX(int i) {
            this.mAlignX = i;
        }

        /* access modifiers changed from: 0000 */
        public void setAlignY(int i) {
            this.mAlignY = i;
        }

        /* access modifiers changed from: 0000 */
        public void setItemAlignmentFacet(ItemAlignmentFacet itemAlignmentFacet) {
            this.mAlignmentFacet = itemAlignmentFacet;
        }

        /* access modifiers changed from: 0000 */
        public ItemAlignmentFacet getItemAlignmentFacet() {
            return this.mAlignmentFacet;
        }

        /* access modifiers changed from: 0000 */
        public void calculateItemAlignments(int i, View view) {
            ItemAlignmentDef[] alignmentDefs = this.mAlignmentFacet.getAlignmentDefs();
            int[] iArr = this.mAlignMultiple;
            if (iArr == null || iArr.length != alignmentDefs.length) {
                this.mAlignMultiple = new int[alignmentDefs.length];
            }
            for (int i2 = 0; i2 < alignmentDefs.length; i2++) {
                this.mAlignMultiple[i2] = ItemAlignmentFacetHelper.getAlignmentPosition(view, alignmentDefs[i2], i);
            }
            if (i == 0) {
                this.mAlignX = this.mAlignMultiple[0];
            } else {
                this.mAlignY = this.mAlignMultiple[0];
            }
        }

        /* access modifiers changed from: 0000 */
        public int[] getAlignMultiple() {
            return this.mAlignMultiple;
        }

        /* access modifiers changed from: 0000 */
        public void setOpticalInsets(int i, int i2, int i3, int i4) {
            this.mLeftInset = i;
            this.mTopInset = i2;
            this.mRightInset = i3;
            this.mBottomInset = i4;
        }
    }

    final class PendingMoveSmoothScroller extends GridLinearSmoothScroller {
        private int mPendingMoves;
        private final boolean mStaggeredGrid;

        PendingMoveSmoothScroller(int i, boolean z) {
            super();
            this.mPendingMoves = i;
            this.mStaggeredGrid = z;
            setTargetPosition(-2);
        }

        /* access modifiers changed from: 0000 */
        public void increasePendingMoves() {
            int i = this.mPendingMoves;
            if (i < GridLayoutManager.this.mMaxPendingMoves) {
                this.mPendingMoves = i + 1;
            }
        }

        /* access modifiers changed from: 0000 */
        public void decreasePendingMoves() {
            int i = this.mPendingMoves;
            if (i > (-GridLayoutManager.this.mMaxPendingMoves)) {
                this.mPendingMoves = i - 1;
            }
        }

        /* access modifiers changed from: 0000 */
        /* JADX WARNING: Removed duplicated region for block: B:12:0x001f  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void consumePendingMovesBeforeLayout() {
            /*
                r4 = this;
                boolean r0 = r4.mStaggeredGrid
                if (r0 != 0) goto L_0x006f
                int r0 = r4.mPendingMoves
                if (r0 != 0) goto L_0x0009
                goto L_0x006f
            L_0x0009:
                r1 = 0
                if (r0 <= 0) goto L_0x0014
                androidx.leanback.widget.GridLayoutManager r0 = androidx.leanback.widget.GridLayoutManager.this
                int r2 = r0.mFocusPosition
                int r0 = r0.mNumRows
            L_0x0012:
                int r2 = r2 + r0
                goto L_0x001b
            L_0x0014:
                androidx.leanback.widget.GridLayoutManager r0 = androidx.leanback.widget.GridLayoutManager.this
                int r2 = r0.mFocusPosition
                int r0 = r0.mNumRows
            L_0x001a:
                int r2 = r2 - r0
            L_0x001b:
                int r0 = r4.mPendingMoves
                if (r0 == 0) goto L_0x0052
                android.view.View r0 = r4.findViewByPosition(r2)
                if (r0 != 0) goto L_0x0026
                goto L_0x0052
            L_0x0026:
                androidx.leanback.widget.GridLayoutManager r3 = androidx.leanback.widget.GridLayoutManager.this
                boolean r3 = r3.canScrollTo(r0)
                if (r3 != 0) goto L_0x002f
                goto L_0x0044
            L_0x002f:
                androidx.leanback.widget.GridLayoutManager r1 = androidx.leanback.widget.GridLayoutManager.this
                r1.mFocusPosition = r2
                r3 = 0
                r1.mSubFocusPosition = r3
                int r1 = r4.mPendingMoves
                if (r1 <= 0) goto L_0x003f
                int r1 = r1 + -1
                r4.mPendingMoves = r1
                goto L_0x0043
            L_0x003f:
                int r1 = r1 + 1
                r4.mPendingMoves = r1
            L_0x0043:
                r1 = r0
            L_0x0044:
                int r0 = r4.mPendingMoves
                if (r0 <= 0) goto L_0x004d
                androidx.leanback.widget.GridLayoutManager r0 = androidx.leanback.widget.GridLayoutManager.this
                int r0 = r0.mNumRows
                goto L_0x0012
            L_0x004d:
                androidx.leanback.widget.GridLayoutManager r0 = androidx.leanback.widget.GridLayoutManager.this
                int r0 = r0.mNumRows
                goto L_0x001a
            L_0x0052:
                if (r1 == 0) goto L_0x006f
                androidx.leanback.widget.GridLayoutManager r0 = androidx.leanback.widget.GridLayoutManager.this
                boolean r0 = r0.hasFocus()
                if (r0 == 0) goto L_0x006f
                androidx.leanback.widget.GridLayoutManager r0 = androidx.leanback.widget.GridLayoutManager.this
                int r2 = r0.mFlag
                r2 = r2 | 32
                r0.mFlag = r2
                r1.requestFocus()
                androidx.leanback.widget.GridLayoutManager r4 = androidx.leanback.widget.GridLayoutManager.this
                int r0 = r4.mFlag
                r0 = r0 & -33
                r4.mFlag = r0
            L_0x006f:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: androidx.leanback.widget.GridLayoutManager.PendingMoveSmoothScroller.consumePendingMovesBeforeLayout():void");
        }

        /* access modifiers changed from: 0000 */
        public void consumePendingMovesAfterLayout() {
            if (this.mStaggeredGrid) {
                int i = this.mPendingMoves;
                if (i != 0) {
                    this.mPendingMoves = GridLayoutManager.this.processSelectionMoves(true, i);
                }
            }
            int i2 = this.mPendingMoves;
            if (i2 == 0 || ((i2 > 0 && GridLayoutManager.this.hasCreatedLastItem()) || (this.mPendingMoves < 0 && GridLayoutManager.this.hasCreatedFirstItem()))) {
                setTargetPosition(GridLayoutManager.this.mFocusPosition);
                stop();
            }
        }

        public PointF computeScrollVectorForPosition(int i) {
            int i2 = this.mPendingMoves;
            if (i2 == 0) {
                return null;
            }
            int i3 = ((GridLayoutManager.this.mFlag & 262144) == 0 ? i2 >= 0 : i2 <= 0) ? 1 : -1;
            if (GridLayoutManager.this.mOrientation == 0) {
                return new PointF((float) i3, 0.0f);
            }
            return new PointF(0.0f, (float) i3);
        }

        /* access modifiers changed from: protected */
        public void onStopInternal() {
            super.onStopInternal();
            this.mPendingMoves = 0;
            View findViewByPosition = findViewByPosition(getTargetPosition());
            if (findViewByPosition != null) {
                GridLayoutManager.this.scrollToView(findViewByPosition, true);
            }
        }
    }

    @SuppressLint({"BanParcelableUsage"})
    static final class SavedState implements Parcelable {
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        Bundle childStates = Bundle.EMPTY;
        int index;

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.index);
            parcel.writeBundle(this.childStates);
        }

        SavedState(Parcel parcel) {
            this.index = parcel.readInt();
            this.childStates = parcel.readBundle(GridLayoutManager.class.getClassLoader());
        }

        SavedState() {
        }
    }

    public boolean requestChildRectangleOnScreen(RecyclerView recyclerView, View view, Rect rect, boolean z) {
        return false;
    }

    public boolean supportsPredictiveItemAnimations() {
        return true;
    }

    /* access modifiers changed from: 0000 */
    public String getTag() {
        StringBuilder sb = new StringBuilder();
        sb.append("GridLayoutManager:");
        sb.append(this.mBaseGridView.getId());
        return sb.toString();
    }

    public GridLayoutManager(BaseGridView baseGridView) {
        this.mBaseGridView = baseGridView;
        this.mChildVisibility = -1;
        setItemPrefetchEnabled(false);
    }

    public void setOrientation(int i) {
        if (i == 0 || i == 1) {
            this.mOrientation = i;
            this.mOrientationHelper = OrientationHelper.createOrientationHelper(this, i);
            this.mWindowAlignment.setOrientation(i);
            this.mItemAlignment.setOrientation(i);
            this.mFlag |= 256;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x0019  */
    /* JADX WARNING: Removed duplicated region for block: B:9:0x0018 A[RETURN] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onRtlPropertiesChanged(int r6) {
        /*
            r5 = this;
            int r0 = r5.mOrientation
            r1 = 0
            r2 = 1
            if (r0 != 0) goto L_0x000d
            if (r6 != r2) goto L_0x000b
            r0 = 262144(0x40000, float:3.67342E-40)
            goto L_0x0011
        L_0x000b:
            r0 = r1
            goto L_0x0011
        L_0x000d:
            if (r6 != r2) goto L_0x000b
            r0 = 524288(0x80000, float:7.34684E-40)
        L_0x0011:
            int r3 = r5.mFlag
            r4 = 786432(0xc0000, float:1.102026E-39)
            r4 = r4 & r3
            if (r4 != r0) goto L_0x0019
            return
        L_0x0019:
            r4 = -786433(0xfffffffffff3ffff, float:NaN)
            r3 = r3 & r4
            r0 = r0 | r3
            r5.mFlag = r0
            r0 = r0 | 256(0x100, float:3.59E-43)
            r5.mFlag = r0
            androidx.leanback.widget.WindowAlignment r5 = r5.mWindowAlignment
            androidx.leanback.widget.WindowAlignment$Axis r5 = r5.horizontal
            if (r6 != r2) goto L_0x002b
            r1 = r2
        L_0x002b:
            r5.setReversedFlow(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.leanback.widget.GridLayoutManager.onRtlPropertiesChanged(int):void");
    }

    public void setWindowAlignment(int i) {
        this.mWindowAlignment.mainAxis().setWindowAlignment(i);
    }

    public void setWindowAlignmentOffset(int i) {
        this.mWindowAlignment.mainAxis().setWindowAlignmentOffset(i);
    }

    public void setWindowAlignmentOffsetPercent(float f) {
        this.mWindowAlignment.mainAxis().setWindowAlignmentOffsetPercent(f);
    }

    public void setItemAlignmentOffset(int i) {
        this.mItemAlignment.mainAxis().setItemAlignmentOffset(i);
        updateChildAlignments();
    }

    public void setItemAlignmentOffsetWithPadding(boolean z) {
        this.mItemAlignment.mainAxis().setItemAlignmentOffsetWithPadding(z);
        updateChildAlignments();
    }

    public void setItemAlignmentOffsetPercent(float f) {
        this.mItemAlignment.mainAxis().setItemAlignmentOffsetPercent(f);
        updateChildAlignments();
    }

    public void setItemAlignmentViewId(int i) {
        this.mItemAlignment.mainAxis().setItemAlignmentViewId(i);
        updateChildAlignments();
    }

    public void setFocusOutAllowed(boolean z, boolean z2) {
        int i = 0;
        int i2 = (z ? 2048 : 0) | (this.mFlag & -6145);
        if (z2) {
            i = 4096;
        }
        this.mFlag = i2 | i;
    }

    public void setFocusOutSideAllowed(boolean z, boolean z2) {
        int i = 0;
        int i2 = (z ? 8192 : 0) | (this.mFlag & -24577);
        if (z2) {
            i = 16384;
        }
        this.mFlag = i2 | i;
    }

    public void setNumRows(int i) {
        if (i >= 0) {
            this.mNumRowsRequested = i;
            return;
        }
        throw new IllegalArgumentException();
    }

    public void setRowHeight(int i) {
        if (i >= 0 || i == -2) {
            this.mRowSizeSecondaryRequested = i;
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Invalid row height: ");
        sb.append(i);
        throw new IllegalArgumentException(sb.toString());
    }

    public void setVerticalSpacing(int i) {
        if (this.mOrientation == 1) {
            this.mVerticalSpacing = i;
            this.mSpacingPrimary = i;
            return;
        }
        this.mVerticalSpacing = i;
        this.mSpacingSecondary = i;
    }

    public void setHorizontalSpacing(int i) {
        if (this.mOrientation == 0) {
            this.mSpacingPrimary = i;
        } else {
            this.mSpacingSecondary = i;
        }
    }

    public int getVerticalSpacing() {
        return this.mVerticalSpacing;
    }

    public void setGravity(int i) {
        this.mGravity = i;
    }

    /* access modifiers changed from: protected */
    public boolean hasDoneFirstLayout() {
        return this.mGrid != null;
    }

    public void setOnChildSelectedListener(OnChildSelectedListener onChildSelectedListener) {
        this.mChildSelectedListener = onChildSelectedListener;
    }

    public void setOnChildViewHolderSelectedListener(OnChildViewHolderSelectedListener onChildViewHolderSelectedListener) {
        if (onChildViewHolderSelectedListener == null) {
            this.mChildViewHolderSelectedListeners = null;
            return;
        }
        ArrayList<OnChildViewHolderSelectedListener> arrayList = this.mChildViewHolderSelectedListeners;
        if (arrayList == null) {
            this.mChildViewHolderSelectedListeners = new ArrayList<>();
        } else {
            arrayList.clear();
        }
        this.mChildViewHolderSelectedListeners.add(onChildViewHolderSelectedListener);
    }

    public void addOnChildViewHolderSelectedListener(OnChildViewHolderSelectedListener onChildViewHolderSelectedListener) {
        if (this.mChildViewHolderSelectedListeners == null) {
            this.mChildViewHolderSelectedListeners = new ArrayList<>();
        }
        this.mChildViewHolderSelectedListeners.add(onChildViewHolderSelectedListener);
    }

    public void removeOnChildViewHolderSelectedListener(OnChildViewHolderSelectedListener onChildViewHolderSelectedListener) {
        ArrayList<OnChildViewHolderSelectedListener> arrayList = this.mChildViewHolderSelectedListeners;
        if (arrayList != null) {
            arrayList.remove(onChildViewHolderSelectedListener);
        }
    }

    /* access modifiers changed from: 0000 */
    public boolean hasOnChildViewHolderSelectedListener() {
        ArrayList<OnChildViewHolderSelectedListener> arrayList = this.mChildViewHolderSelectedListeners;
        return arrayList != null && arrayList.size() > 0;
    }

    /* access modifiers changed from: 0000 */
    public void fireOnChildViewHolderSelected(RecyclerView recyclerView, ViewHolder viewHolder, int i, int i2) {
        ArrayList<OnChildViewHolderSelectedListener> arrayList = this.mChildViewHolderSelectedListeners;
        if (arrayList != null) {
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                ((OnChildViewHolderSelectedListener) this.mChildViewHolderSelectedListeners.get(size)).onChildViewHolderSelected(recyclerView, viewHolder, i, i2);
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void fireOnChildViewHolderSelectedAndPositioned(RecyclerView recyclerView, ViewHolder viewHolder, int i, int i2) {
        ArrayList<OnChildViewHolderSelectedListener> arrayList = this.mChildViewHolderSelectedListeners;
        if (arrayList != null) {
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                ((OnChildViewHolderSelectedListener) this.mChildViewHolderSelectedListeners.get(size)).onChildViewHolderSelectedAndPositioned(recyclerView, viewHolder, i, i2);
            }
        }
    }

    private int getAdapterPositionByView(View view) {
        int i = -1;
        if (view == null) {
            return -1;
        }
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        if (layoutParams != null && !layoutParams.isItemRemoved()) {
            i = layoutParams.getViewAdapterPosition();
        }
        return i;
    }

    /* access modifiers changed from: 0000 */
    public int getSubPositionByView(View view, View view2) {
        if (!(view == null || view2 == null)) {
            ItemAlignmentFacet itemAlignmentFacet = ((LayoutParams) view.getLayoutParams()).getItemAlignmentFacet();
            if (itemAlignmentFacet != null) {
                ItemAlignmentDef[] alignmentDefs = itemAlignmentFacet.getAlignmentDefs();
                if (alignmentDefs.length > 1) {
                    while (view2 != view) {
                        int id = view2.getId();
                        if (id != -1) {
                            for (int i = 1; i < alignmentDefs.length; i++) {
                                if (alignmentDefs[i].getItemAlignmentFocusViewId() == id) {
                                    return i;
                                }
                            }
                            continue;
                        }
                        view2 = (View) view2.getParent();
                    }
                }
            }
        }
        return 0;
    }

    private int getAdapterPositionByIndex(int i) {
        return getAdapterPositionByView(getChildAt(i));
    }

    /* access modifiers changed from: 0000 */
    public void dispatchChildSelected() {
        long j;
        if (this.mChildSelectedListener != null || hasOnChildViewHolderSelectedListener()) {
            int i = this.mFocusPosition;
            View findViewByPosition = i == -1 ? null : findViewByPosition(i);
            int i2 = 0;
            if (findViewByPosition != null) {
                ViewHolder childViewHolder = this.mBaseGridView.getChildViewHolder(findViewByPosition);
                OnChildSelectedListener onChildSelectedListener = this.mChildSelectedListener;
                if (onChildSelectedListener != null) {
                    BaseGridView baseGridView = this.mBaseGridView;
                    int i3 = this.mFocusPosition;
                    if (childViewHolder == null) {
                        j = -1;
                    } else {
                        j = childViewHolder.getItemId();
                    }
                    onChildSelectedListener.onChildSelected(baseGridView, findViewByPosition, i3, j);
                }
                fireOnChildViewHolderSelected(this.mBaseGridView, childViewHolder, this.mFocusPosition, this.mSubFocusPosition);
            } else {
                OnChildSelectedListener onChildSelectedListener2 = this.mChildSelectedListener;
                if (onChildSelectedListener2 != null) {
                    onChildSelectedListener2.onChildSelected(this.mBaseGridView, null, -1, -1);
                }
                fireOnChildViewHolderSelected(this.mBaseGridView, null, -1, 0);
            }
            if ((this.mFlag & 3) != 1 && !this.mBaseGridView.isLayoutRequested()) {
                int childCount = getChildCount();
                while (true) {
                    if (i2 >= childCount) {
                        break;
                    } else if (getChildAt(i2).isLayoutRequested()) {
                        forceRequestLayout();
                        break;
                    } else {
                        i2++;
                    }
                }
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void dispatchChildSelectedAndPositioned() {
        if (hasOnChildViewHolderSelectedListener()) {
            int i = this.mFocusPosition;
            View findViewByPosition = i == -1 ? null : findViewByPosition(i);
            if (findViewByPosition != null) {
                fireOnChildViewHolderSelectedAndPositioned(this.mBaseGridView, this.mBaseGridView.getChildViewHolder(findViewByPosition), this.mFocusPosition, this.mSubFocusPosition);
            } else {
                OnChildSelectedListener onChildSelectedListener = this.mChildSelectedListener;
                if (onChildSelectedListener != null) {
                    onChildSelectedListener.onChildSelected(this.mBaseGridView, null, -1, -1);
                }
                fireOnChildViewHolderSelectedAndPositioned(this.mBaseGridView, null, -1, 0);
            }
        }
    }

    public boolean canScrollHorizontally() {
        return this.mOrientation == 0 || this.mNumRows > 1;
    }

    public boolean canScrollVertically() {
        return this.mOrientation == 1 || this.mNumRows > 1;
    }

    public androidx.recyclerview.widget.RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2);
    }

    public androidx.recyclerview.widget.RecyclerView.LayoutParams generateLayoutParams(Context context, AttributeSet attributeSet) {
        return new LayoutParams(context, attributeSet);
    }

    public androidx.recyclerview.widget.RecyclerView.LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
        if (layoutParams instanceof LayoutParams) {
            return new LayoutParams((LayoutParams) layoutParams);
        }
        if (layoutParams instanceof androidx.recyclerview.widget.RecyclerView.LayoutParams) {
            return new LayoutParams((androidx.recyclerview.widget.RecyclerView.LayoutParams) layoutParams);
        }
        if (layoutParams instanceof MarginLayoutParams) {
            return new LayoutParams((MarginLayoutParams) layoutParams);
        }
        return new LayoutParams(layoutParams);
    }

    /* access modifiers changed from: protected */
    public View getViewForPosition(int i) {
        View viewForPosition = this.mRecycler.getViewForPosition(i);
        ((LayoutParams) viewForPosition.getLayoutParams()).setItemAlignmentFacet((ItemAlignmentFacet) getFacet(this.mBaseGridView.getChildViewHolder(viewForPosition), ItemAlignmentFacet.class));
        return viewForPosition;
    }

    /* access modifiers changed from: 0000 */
    public final int getOpticalLeft(View view) {
        return ((LayoutParams) view.getLayoutParams()).getOpticalLeft(view);
    }

    /* access modifiers changed from: 0000 */
    public final int getOpticalRight(View view) {
        return ((LayoutParams) view.getLayoutParams()).getOpticalRight(view);
    }

    public int getDecoratedLeft(View view) {
        return super.getDecoratedLeft(view) + ((LayoutParams) view.getLayoutParams()).mLeftInset;
    }

    public int getDecoratedTop(View view) {
        return super.getDecoratedTop(view) + ((LayoutParams) view.getLayoutParams()).mTopInset;
    }

    public int getDecoratedRight(View view) {
        return super.getDecoratedRight(view) - ((LayoutParams) view.getLayoutParams()).mRightInset;
    }

    public int getDecoratedBottom(View view) {
        return super.getDecoratedBottom(view) - ((LayoutParams) view.getLayoutParams()).mBottomInset;
    }

    public void getDecoratedBoundsWithMargins(View view, Rect rect) {
        super.getDecoratedBoundsWithMargins(view, rect);
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        rect.left += layoutParams.mLeftInset;
        rect.top += layoutParams.mTopInset;
        rect.right -= layoutParams.mRightInset;
        rect.bottom -= layoutParams.mBottomInset;
    }

    /* access modifiers changed from: 0000 */
    public int getViewMin(View view) {
        return this.mOrientationHelper.getDecoratedStart(view);
    }

    /* access modifiers changed from: 0000 */
    public int getViewMax(View view) {
        return this.mOrientationHelper.getDecoratedEnd(view);
    }

    /* access modifiers changed from: 0000 */
    public int getViewPrimarySize(View view) {
        getDecoratedBoundsWithMargins(view, sTempRect);
        return this.mOrientation == 0 ? sTempRect.width() : sTempRect.height();
    }

    private int getViewCenter(View view) {
        return this.mOrientation == 0 ? getViewCenterX(view) : getViewCenterY(view);
    }

    private int getViewCenterSecondary(View view) {
        return this.mOrientation == 0 ? getViewCenterY(view) : getViewCenterX(view);
    }

    private int getViewCenterX(View view) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        return layoutParams.getOpticalLeft(view) + layoutParams.getAlignX();
    }

    private int getViewCenterY(View view) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        return layoutParams.getOpticalTop(view) + layoutParams.getAlignY();
    }

    private void saveContext(Recycler recycler, State state) {
        if (!(this.mRecycler == null && this.mState == null)) {
            Log.e("GridLayoutManager", "Recycler information was not released, bug!");
        }
        this.mRecycler = recycler;
        this.mState = state;
        this.mPositionDeltaInPreLayout = 0;
        this.mExtraLayoutSpaceInPreLayout = 0;
    }

    private void leaveContext() {
        this.mRecycler = null;
        this.mState = null;
        this.mPositionDeltaInPreLayout = 0;
        this.mExtraLayoutSpaceInPreLayout = 0;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0074, code lost:
        if (((r5.mFlag & 262144) != 0) != r5.mGrid.isReversedFlow()) goto L_0x0076;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean layoutInit() {
        /*
            r5 = this;
            androidx.recyclerview.widget.RecyclerView$State r0 = r5.mState
            int r0 = r0.getItemCount()
            r1 = -1
            r2 = 1
            r3 = 0
            if (r0 != 0) goto L_0x0010
            r5.mFocusPosition = r1
            r5.mSubFocusPosition = r3
            goto L_0x0022
        L_0x0010:
            int r4 = r5.mFocusPosition
            if (r4 < r0) goto L_0x001a
            int r0 = r0 - r2
            r5.mFocusPosition = r0
            r5.mSubFocusPosition = r3
            goto L_0x0022
        L_0x001a:
            if (r4 != r1) goto L_0x0022
            if (r0 <= 0) goto L_0x0022
            r5.mFocusPosition = r3
            r5.mSubFocusPosition = r3
        L_0x0022:
            androidx.recyclerview.widget.RecyclerView$State r0 = r5.mState
            boolean r0 = r0.didStructureChange()
            if (r0 != 0) goto L_0x0052
            androidx.leanback.widget.Grid r0 = r5.mGrid
            if (r0 == 0) goto L_0x0052
            int r0 = r0.getFirstVisibleIndex()
            if (r0 < 0) goto L_0x0052
            int r0 = r5.mFlag
            r0 = r0 & 256(0x100, float:3.59E-43)
            if (r0 != 0) goto L_0x0052
            androidx.leanback.widget.Grid r0 = r5.mGrid
            int r0 = r0.getNumRows()
            int r1 = r5.mNumRows
            if (r0 != r1) goto L_0x0052
            r5.updateScrollController()
            r5.updateSecondaryScrollLimits()
            androidx.leanback.widget.Grid r0 = r5.mGrid
            int r5 = r5.mSpacingPrimary
            r0.setSpacing(r5)
            return r2
        L_0x0052:
            int r0 = r5.mFlag
            r0 = r0 & -257(0xfffffffffffffeff, float:NaN)
            r5.mFlag = r0
            androidx.leanback.widget.Grid r0 = r5.mGrid
            r1 = 262144(0x40000, float:3.67342E-40)
            if (r0 == 0) goto L_0x0076
            int r4 = r5.mNumRows
            int r0 = r0.getNumRows()
            if (r4 != r0) goto L_0x0076
            int r0 = r5.mFlag
            r0 = r0 & r1
            if (r0 == 0) goto L_0x006d
            r0 = r2
            goto L_0x006e
        L_0x006d:
            r0 = r3
        L_0x006e:
            androidx.leanback.widget.Grid r4 = r5.mGrid
            boolean r4 = r4.isReversedFlow()
            if (r0 == r4) goto L_0x008f
        L_0x0076:
            int r0 = r5.mNumRows
            androidx.leanback.widget.Grid r0 = androidx.leanback.widget.Grid.createGrid(r0)
            r5.mGrid = r0
            androidx.leanback.widget.Grid$Provider r4 = r5.mGridProvider
            r0.setProvider(r4)
            androidx.leanback.widget.Grid r0 = r5.mGrid
            int r4 = r5.mFlag
            r1 = r1 & r4
            if (r1 == 0) goto L_0x008b
            goto L_0x008c
        L_0x008b:
            r2 = r3
        L_0x008c:
            r0.setReversedFlow(r2)
        L_0x008f:
            r5.initScrollController()
            r5.updateSecondaryScrollLimits()
            androidx.leanback.widget.Grid r0 = r5.mGrid
            int r1 = r5.mSpacingPrimary
            r0.setSpacing(r1)
            androidx.recyclerview.widget.RecyclerView$Recycler r0 = r5.mRecycler
            r5.detachAndScrapAttachedViews(r0)
            androidx.leanback.widget.Grid r0 = r5.mGrid
            r0.resetVisibleIndex()
            androidx.leanback.widget.WindowAlignment r0 = r5.mWindowAlignment
            androidx.leanback.widget.WindowAlignment$Axis r0 = r0.mainAxis()
            r0.invalidateScrollMin()
            androidx.leanback.widget.WindowAlignment r5 = r5.mWindowAlignment
            androidx.leanback.widget.WindowAlignment$Axis r5 = r5.mainAxis()
            r5.invalidateScrollMax()
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.leanback.widget.GridLayoutManager.layoutInit():boolean");
    }

    private int getRowSizeSecondary(int i) {
        int i2 = this.mFixedRowSizeSecondary;
        if (i2 != 0) {
            return i2;
        }
        int[] iArr = this.mRowSizeSecondary;
        if (iArr == null) {
            return 0;
        }
        return iArr[i];
    }

    /* access modifiers changed from: 0000 */
    public int getRowStartSecondary(int i) {
        int i2 = 0;
        if ((this.mFlag & 524288) != 0) {
            for (int i3 = this.mNumRows - 1; i3 > i; i3--) {
                i2 += getRowSizeSecondary(i3) + this.mSpacingSecondary;
            }
            return i2;
        }
        int i4 = 0;
        while (i2 < i) {
            i4 += getRowSizeSecondary(i2) + this.mSpacingSecondary;
            i2++;
        }
        return i4;
    }

    private int getSizeSecondary() {
        int i = (this.mFlag & 524288) != 0 ? 0 : this.mNumRows - 1;
        return getRowStartSecondary(i) + getRowSizeSecondary(i);
    }

    /* access modifiers changed from: 0000 */
    public int getDecoratedMeasuredWidthWithMargin(View view) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        return getDecoratedMeasuredWidth(view) + layoutParams.leftMargin + layoutParams.rightMargin;
    }

    /* access modifiers changed from: 0000 */
    public int getDecoratedMeasuredHeightWithMargin(View view) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        return getDecoratedMeasuredHeight(view) + layoutParams.topMargin + layoutParams.bottomMargin;
    }

    private void measureScrapChild(int i, int i2, int i3, int[] iArr) {
        View viewForPosition = this.mRecycler.getViewForPosition(i);
        if (viewForPosition != null) {
            LayoutParams layoutParams = (LayoutParams) viewForPosition.getLayoutParams();
            calculateItemDecorationsForChild(viewForPosition, sTempRect);
            int i4 = layoutParams.leftMargin + layoutParams.rightMargin;
            Rect rect = sTempRect;
            viewForPosition.measure(ViewGroup.getChildMeasureSpec(i2, getPaddingLeft() + getPaddingRight() + i4 + rect.left + rect.right, layoutParams.width), ViewGroup.getChildMeasureSpec(i3, getPaddingTop() + getPaddingBottom() + layoutParams.topMargin + layoutParams.bottomMargin + rect.top + rect.bottom, layoutParams.height));
            iArr[0] = getDecoratedMeasuredWidthWithMargin(viewForPosition);
            iArr[1] = getDecoratedMeasuredHeightWithMargin(viewForPosition);
            this.mRecycler.recycleView(viewForPosition);
        }
    }

    private boolean processRowSizeSecondary(boolean z) {
        CircularIntArray circularIntArray;
        int i;
        int i2;
        int i3;
        if (this.mFixedRowSizeSecondary != 0 || this.mRowSizeSecondary == null) {
            return false;
        }
        Grid grid = this.mGrid;
        CircularIntArray[] itemPositionsInRows = grid == null ? null : grid.getItemPositionsInRows();
        boolean z2 = false;
        int i4 = -1;
        for (int i5 = 0; i5 < this.mNumRows; i5++) {
            if (itemPositionsInRows == null) {
                circularIntArray = null;
            } else {
                circularIntArray = itemPositionsInRows[i5];
            }
            if (circularIntArray == null) {
                i = 0;
            } else {
                i = circularIntArray.size();
            }
            int i6 = -1;
            for (int i7 = 0; i7 < i; i7 += 2) {
                int i8 = circularIntArray.get(i7 + 1);
                for (int i9 = circularIntArray.get(i7); i9 <= i8; i9++) {
                    View findViewByPosition = findViewByPosition(i9 - this.mPositionDeltaInPreLayout);
                    if (findViewByPosition != null) {
                        if (z) {
                            measureChild(findViewByPosition);
                        }
                        if (this.mOrientation == 0) {
                            i3 = getDecoratedMeasuredHeightWithMargin(findViewByPosition);
                        } else {
                            i3 = getDecoratedMeasuredWidthWithMargin(findViewByPosition);
                        }
                        if (i3 > i6) {
                            i6 = i3;
                        }
                    }
                }
            }
            int itemCount = this.mState.getItemCount();
            if (!this.mBaseGridView.hasFixedSize() && z && i6 < 0 && itemCount > 0) {
                if (i4 < 0) {
                    int i10 = this.mFocusPosition;
                    if (i10 < 0) {
                        i10 = 0;
                    } else if (i10 >= itemCount) {
                        i10 = itemCount - 1;
                    }
                    if (getChildCount() > 0) {
                        int layoutPosition = this.mBaseGridView.getChildViewHolder(getChildAt(0)).getLayoutPosition();
                        int layoutPosition2 = this.mBaseGridView.getChildViewHolder(getChildAt(getChildCount() - 1)).getLayoutPosition();
                        if (i2 >= layoutPosition && i2 <= layoutPosition2) {
                            i2 = i2 - layoutPosition <= layoutPosition2 - i2 ? layoutPosition - 1 : layoutPosition2 + 1;
                            if (i2 < 0 && layoutPosition2 < itemCount - 1) {
                                i2 = layoutPosition2 + 1;
                            } else if (i2 >= itemCount && layoutPosition > 0) {
                                i2 = layoutPosition - 1;
                            }
                        }
                    }
                    if (i2 >= 0 && i2 < itemCount) {
                        measureScrapChild(i2, MeasureSpec.makeMeasureSpec(0, 0), MeasureSpec.makeMeasureSpec(0, 0), this.mMeasuredDimension);
                        i4 = this.mOrientation == 0 ? this.mMeasuredDimension[1] : this.mMeasuredDimension[0];
                    }
                }
                if (i4 >= 0) {
                    i6 = i4;
                }
            }
            if (i6 < 0) {
                i6 = 0;
            }
            int[] iArr = this.mRowSizeSecondary;
            if (iArr[i5] != i6) {
                iArr[i5] = i6;
                z2 = true;
            }
        }
        return z2;
    }

    private void updateRowSecondarySizeRefresh() {
        int i = this.mFlag & -1025;
        int i2 = 0;
        if (processRowSizeSecondary(false)) {
            i2 = 1024;
        }
        int i3 = i | i2;
        this.mFlag = i3;
        if ((i3 & 1024) != 0) {
            forceRequestLayout();
        }
    }

    private void forceRequestLayout() {
        ViewCompat.postOnAnimation(this.mBaseGridView, this.mRequestLayoutRunnable);
    }

    public void onMeasure(Recycler recycler, State state, int i, int i2) {
        int i3;
        int i4;
        int i5;
        int i6;
        int i7;
        saveContext(recycler, state);
        if (this.mOrientation == 0) {
            i6 = MeasureSpec.getSize(i);
            i5 = MeasureSpec.getSize(i2);
            i4 = MeasureSpec.getMode(i2);
            i3 = getPaddingTop();
            i7 = getPaddingBottom();
        } else {
            i5 = MeasureSpec.getSize(i);
            i6 = MeasureSpec.getSize(i2);
            i4 = MeasureSpec.getMode(i);
            i3 = getPaddingLeft();
            i7 = getPaddingRight();
        }
        int i8 = i3 + i7;
        this.mMaxSizeSecondary = i5;
        int i9 = this.mRowSizeSecondaryRequested;
        String str = "wrong spec";
        if (i9 == -2) {
            int i10 = this.mNumRowsRequested;
            if (i10 == 0) {
                i10 = 1;
            }
            this.mNumRows = i10;
            this.mFixedRowSizeSecondary = 0;
            int[] iArr = this.mRowSizeSecondary;
            if (iArr == null || iArr.length != i10) {
                this.mRowSizeSecondary = new int[this.mNumRows];
            }
            if (this.mState.isPreLayout()) {
                updatePositionDeltaInPreLayout();
            }
            processRowSizeSecondary(true);
            if (i4 == Integer.MIN_VALUE) {
                i5 = Math.min(getSizeSecondary() + i8, this.mMaxSizeSecondary);
            } else if (i4 == 0) {
                i5 = getSizeSecondary() + i8;
            } else if (i4 == 1073741824) {
                i5 = this.mMaxSizeSecondary;
            } else {
                throw new IllegalStateException(str);
            }
        } else {
            if (i4 != Integer.MIN_VALUE) {
                if (i4 == 0) {
                    if (i9 == 0) {
                        i9 = i5 - i8;
                    }
                    this.mFixedRowSizeSecondary = i9;
                    int i11 = this.mNumRowsRequested;
                    if (i11 == 0) {
                        i11 = 1;
                    }
                    this.mNumRows = i11;
                    i5 = (this.mFixedRowSizeSecondary * i11) + (this.mSpacingSecondary * (i11 - 1)) + i8;
                } else if (i4 != 1073741824) {
                    throw new IllegalStateException(str);
                }
            }
            if (this.mNumRowsRequested == 0 && this.mRowSizeSecondaryRequested == 0) {
                this.mNumRows = 1;
                this.mFixedRowSizeSecondary = i5 - i8;
            } else {
                int i12 = this.mNumRowsRequested;
                if (i12 == 0) {
                    int i13 = this.mRowSizeSecondaryRequested;
                    this.mFixedRowSizeSecondary = i13;
                    int i14 = this.mSpacingSecondary;
                    this.mNumRows = (i5 + i14) / (i13 + i14);
                } else {
                    int i15 = this.mRowSizeSecondaryRequested;
                    if (i15 == 0) {
                        this.mNumRows = i12;
                        this.mFixedRowSizeSecondary = ((i5 - i8) - (this.mSpacingSecondary * (i12 - 1))) / i12;
                    } else {
                        this.mNumRows = i12;
                        this.mFixedRowSizeSecondary = i15;
                    }
                }
            }
            if (i4 == Integer.MIN_VALUE) {
                int i16 = this.mFixedRowSizeSecondary;
                int i17 = this.mNumRows;
                int i18 = (i16 * i17) + (this.mSpacingSecondary * (i17 - 1)) + i8;
                if (i18 < i5) {
                    i5 = i18;
                }
            }
        }
        if (this.mOrientation == 0) {
            setMeasuredDimension(i6, i5);
        } else {
            setMeasuredDimension(i5, i6);
        }
        leaveContext();
    }

    /* access modifiers changed from: 0000 */
    public void measureChild(View view) {
        int i;
        int i2;
        int i3;
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        calculateItemDecorationsForChild(view, sTempRect);
        int i4 = layoutParams.leftMargin + layoutParams.rightMargin;
        Rect rect = sTempRect;
        int i5 = i4 + rect.left + rect.right;
        int i6 = layoutParams.topMargin + layoutParams.bottomMargin + rect.top + rect.bottom;
        if (this.mRowSizeSecondaryRequested == -2) {
            i = MeasureSpec.makeMeasureSpec(0, 0);
        } else {
            i = MeasureSpec.makeMeasureSpec(this.mFixedRowSizeSecondary, 1073741824);
        }
        if (this.mOrientation == 0) {
            i2 = ViewGroup.getChildMeasureSpec(MeasureSpec.makeMeasureSpec(0, 0), i5, layoutParams.width);
            i3 = ViewGroup.getChildMeasureSpec(i, i6, layoutParams.height);
        } else {
            int childMeasureSpec = ViewGroup.getChildMeasureSpec(MeasureSpec.makeMeasureSpec(0, 0), i6, layoutParams.height);
            int childMeasureSpec2 = ViewGroup.getChildMeasureSpec(i, i5, layoutParams.width);
            i3 = childMeasureSpec;
            i2 = childMeasureSpec2;
        }
        view.measure(i2, i3);
    }

    /* access modifiers changed from: 0000 */
    public <E> E getFacet(ViewHolder viewHolder, Class<? extends E> cls) {
        E facet = viewHolder instanceof FacetProvider ? ((FacetProvider) viewHolder).getFacet(cls) : null;
        if (facet != null) {
            return facet;
        }
        FacetProviderAdapter facetProviderAdapter = this.mFacetProviderAdapter;
        if (facetProviderAdapter == null) {
            return facet;
        }
        FacetProvider facetProvider = facetProviderAdapter.getFacetProvider(viewHolder.getItemViewType());
        return facetProvider != null ? facetProvider.getFacet(cls) : facet;
    }

    /* access modifiers changed from: 0000 */
    public void layoutChild(int i, View view, int i2, int i3, int i4) {
        int i5;
        int i6;
        int i7;
        if (this.mOrientation == 0) {
            i5 = getDecoratedMeasuredHeightWithMargin(view);
        } else {
            i5 = getDecoratedMeasuredWidthWithMargin(view);
        }
        int i8 = this.mFixedRowSizeSecondary;
        if (i8 > 0) {
            i5 = Math.min(i5, i8);
        }
        int i9 = this.mGravity;
        int i10 = i9 & 112;
        int absoluteGravity = (this.mFlag & 786432) != 0 ? Gravity.getAbsoluteGravity(i9 & 8388615, 1) : i9 & 7;
        if (!((this.mOrientation == 0 && i10 == 48) || (this.mOrientation == 1 && absoluteGravity == 3))) {
            if ((this.mOrientation == 0 && i10 == 80) || (this.mOrientation == 1 && absoluteGravity == 5)) {
                i7 = getRowSizeSecondary(i) - i5;
            } else if ((this.mOrientation == 0 && i10 == 16) || (this.mOrientation == 1 && absoluteGravity == 1)) {
                i7 = (getRowSizeSecondary(i) - i5) / 2;
            }
            i4 += i7;
        }
        if (this.mOrientation == 0) {
            i6 = i5 + i4;
        } else {
            int i11 = i5 + i4;
            int i12 = i4;
            i4 = i2;
            i2 = i12;
            int i13 = i11;
            i6 = i3;
            i3 = i13;
        }
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        layoutDecoratedWithMargins(view, i2, i4, i3, i6);
        super.getDecoratedBoundsWithMargins(view, sTempRect);
        Rect rect = sTempRect;
        layoutParams.setOpticalInsets(i2 - rect.left, i4 - rect.top, rect.right - i3, rect.bottom - i6);
        updateChildAlignments(view);
    }

    private void updateChildAlignments(View view) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        if (layoutParams.getItemAlignmentFacet() == null) {
            layoutParams.setAlignX(this.mItemAlignment.horizontal.getAlignmentPosition(view));
            layoutParams.setAlignY(this.mItemAlignment.vertical.getAlignmentPosition(view));
            return;
        }
        layoutParams.calculateItemAlignments(this.mOrientation, view);
        if (this.mOrientation == 0) {
            layoutParams.setAlignY(this.mItemAlignment.vertical.getAlignmentPosition(view));
        } else {
            layoutParams.setAlignX(this.mItemAlignment.horizontal.getAlignmentPosition(view));
        }
    }

    private void updateChildAlignments() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            updateChildAlignments(getChildAt(i));
        }
    }

    private void removeInvisibleViewsAtEnd() {
        int i;
        int i2 = this.mFlag;
        if ((65600 & i2) == 65536) {
            Grid grid = this.mGrid;
            int i3 = this.mFocusPosition;
            if ((i2 & 262144) != 0) {
                i = -this.mExtraLayoutSpace;
            } else {
                i = this.mExtraLayoutSpace + this.mSizePrimary;
            }
            grid.removeInvisibleItemsAtEnd(i3, i);
        }
    }

    private void removeInvisibleViewsAtFront() {
        int i = this.mFlag;
        if ((65600 & i) == 65536) {
            this.mGrid.removeInvisibleItemsAtFront(this.mFocusPosition, (i & 262144) != 0 ? this.mSizePrimary + this.mExtraLayoutSpace : -this.mExtraLayoutSpace);
        }
    }

    private boolean appendOneColumnVisibleItems() {
        return this.mGrid.appendOneColumnVisibleItems();
    }

    /* access modifiers changed from: 0000 */
    public void slideIn() {
        int i = this.mFlag;
        if ((i & 64) != 0) {
            int i2 = i & -65;
            this.mFlag = i2;
            int i3 = this.mFocusPosition;
            if (i3 >= 0) {
                scrollToSelection(i3, this.mSubFocusPosition, true, this.mPrimaryScrollExtra);
            } else {
                this.mFlag = i2 & -129;
                requestLayout();
            }
            int i4 = this.mFlag;
            if ((i4 & 128) != 0) {
                this.mFlag = i4 & -129;
                if (this.mBaseGridView.getScrollState() != 0 || isSmoothScrolling()) {
                    this.mBaseGridView.addOnScrollListener(new OnScrollListener() {
                        public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                            if (i == 0) {
                                GridLayoutManager.this.mBaseGridView.removeOnScrollListener(this);
                                GridLayoutManager.this.requestLayout();
                            }
                        }
                    });
                } else {
                    requestLayout();
                }
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public int getSlideOutDistance() {
        int i;
        int i2;
        if (this.mOrientation == 1) {
            i2 = -getHeight();
            if (getChildCount() <= 0) {
                return i2;
            }
            i = getChildAt(0).getTop();
            if (i >= 0) {
                return i2;
            }
        } else if ((this.mFlag & 262144) != 0) {
            int width = getWidth();
            if (getChildCount() <= 0) {
                return width;
            }
            int right = getChildAt(0).getRight();
            if (right > width) {
                return right;
            }
            return width;
        } else {
            i2 = -getWidth();
            if (getChildCount() <= 0) {
                return i2;
            }
            i = getChildAt(0).getLeft();
            if (i >= 0) {
                return i2;
            }
        }
        return i2 + i;
    }

    /* access modifiers changed from: 0000 */
    public boolean isSlidingChildViews() {
        return (this.mFlag & 64) != 0;
    }

    /* access modifiers changed from: 0000 */
    public void slideOut() {
        int i = this.mFlag;
        if ((i & 64) == 0) {
            this.mFlag = i | 64;
            if (getChildCount() != 0) {
                if (this.mOrientation == 1) {
                    this.mBaseGridView.smoothScrollBy(0, getSlideOutDistance(), new AccelerateDecelerateInterpolator());
                } else {
                    this.mBaseGridView.smoothScrollBy(getSlideOutDistance(), 0, new AccelerateDecelerateInterpolator());
                }
            }
        }
    }

    private boolean prependOneColumnVisibleItems() {
        return this.mGrid.prependOneColumnVisibleItems();
    }

    private void appendVisibleItems() {
        this.mGrid.appendVisibleItems((this.mFlag & 262144) != 0 ? (-this.mExtraLayoutSpace) - this.mExtraLayoutSpaceInPreLayout : this.mSizePrimary + this.mExtraLayoutSpace + this.mExtraLayoutSpaceInPreLayout);
    }

    private void prependVisibleItems() {
        this.mGrid.prependVisibleItems((this.mFlag & 262144) != 0 ? this.mSizePrimary + this.mExtraLayoutSpace + this.mExtraLayoutSpaceInPreLayout : (-this.mExtraLayoutSpace) - this.mExtraLayoutSpaceInPreLayout);
    }

    private void fastRelayout() {
        int i;
        int childCount = getChildCount();
        int firstVisibleIndex = this.mGrid.getFirstVisibleIndex();
        this.mFlag &= -9;
        boolean z = false;
        int i2 = 0;
        while (true) {
            if (i2 >= childCount) {
                break;
            }
            View childAt = getChildAt(i2);
            if (firstVisibleIndex != getAdapterPositionByView(childAt)) {
                break;
            }
            Location location = this.mGrid.getLocation(firstVisibleIndex);
            if (location == null) {
                break;
            }
            int rowStartSecondary = (getRowStartSecondary(location.row) + this.mWindowAlignment.secondAxis().getPaddingMin()) - this.mScrollOffsetSecondary;
            int viewMin = getViewMin(childAt);
            int viewPrimarySize = getViewPrimarySize(childAt);
            if (((LayoutParams) childAt.getLayoutParams()).viewNeedsUpdate()) {
                this.mFlag |= 8;
                detachAndScrapView(childAt, this.mRecycler);
                childAt = getViewForPosition(firstVisibleIndex);
                addView(childAt, i2);
            }
            View view = childAt;
            measureChild(view);
            if (this.mOrientation == 0) {
                i = getDecoratedMeasuredWidthWithMargin(view);
            } else {
                i = getDecoratedMeasuredHeightWithMargin(view);
            }
            layoutChild(location.row, view, viewMin, viewMin + i, rowStartSecondary);
            if (viewPrimarySize != i) {
                break;
            }
            i2++;
            firstVisibleIndex++;
        }
        z = true;
        if (z) {
            int lastVisibleIndex = this.mGrid.getLastVisibleIndex();
            for (int i3 = childCount - 1; i3 >= i2; i3--) {
                detachAndScrapView(getChildAt(i3), this.mRecycler);
            }
            this.mGrid.invalidateItemsAfter(firstVisibleIndex);
            if ((this.mFlag & 65536) == 0) {
                while (this.mGrid.appendOneColumnVisibleItems()) {
                    if (this.mGrid.getLastVisibleIndex() >= lastVisibleIndex) {
                        break;
                    }
                }
            } else {
                appendVisibleItems();
                int i4 = this.mFocusPosition;
                if (i4 >= 0 && i4 <= lastVisibleIndex) {
                    while (this.mGrid.getLastVisibleIndex() < this.mFocusPosition) {
                        this.mGrid.appendOneColumnVisibleItems();
                    }
                }
            }
        }
        updateScrollLimits();
        updateSecondaryScrollLimits();
    }

    public void removeAndRecycleAllViews(Recycler recycler) {
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            removeAndRecycleViewAt(childCount, recycler);
        }
    }

    private void focusToViewInLayout(boolean z, boolean z2, int i, int i2) {
        View findViewByPosition = findViewByPosition(this.mFocusPosition);
        if (findViewByPosition != null && z2) {
            scrollToView(findViewByPosition, false, i, i2);
        }
        if (findViewByPosition != null && z && !findViewByPosition.hasFocus()) {
            findViewByPosition.requestFocus();
        } else if (!z && !this.mBaseGridView.hasFocus()) {
            if (findViewByPosition == null || !findViewByPosition.hasFocusable()) {
                int childCount = getChildCount();
                int i3 = 0;
                while (true) {
                    if (i3 >= childCount) {
                        break;
                    }
                    findViewByPosition = getChildAt(i3);
                    if (findViewByPosition != null && findViewByPosition.hasFocusable()) {
                        this.mBaseGridView.focusableViewAvailable(findViewByPosition);
                        break;
                    }
                    i3++;
                }
            } else {
                this.mBaseGridView.focusableViewAvailable(findViewByPosition);
            }
            if (z2 && findViewByPosition != null && findViewByPosition.hasFocus()) {
                scrollToView(findViewByPosition, false, i, i2);
            }
        }
    }

    public void onLayoutCompleted(State state) {
        ArrayList<OnLayoutCompletedListener> arrayList = this.mOnLayoutCompletedListeners;
        if (arrayList != null) {
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                ((OnLayoutCompletedListener) it.next()).onLayoutCompleted(state);
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void updatePositionToRowMapInPostLayout() {
        this.mPositionToRowInPostLayout.clear();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            int oldPosition = this.mBaseGridView.getChildViewHolder(getChildAt(i)).getOldPosition();
            if (oldPosition >= 0) {
                Location location = this.mGrid.getLocation(oldPosition);
                if (location != null) {
                    this.mPositionToRowInPostLayout.put(oldPosition, location.row);
                }
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void fillScrapViewsInPostLayout() {
        List scrapList = this.mRecycler.getScrapList();
        int size = scrapList.size();
        if (size != 0) {
            int[] iArr = this.mDisappearingPositions;
            if (iArr == null || size > iArr.length) {
                int[] iArr2 = this.mDisappearingPositions;
                int length = iArr2 == null ? 16 : iArr2.length;
                while (length < size) {
                    length <<= 1;
                }
                this.mDisappearingPositions = new int[length];
            }
            int i = 0;
            for (int i2 = 0; i2 < size; i2++) {
                int absoluteAdapterPosition = ((ViewHolder) scrapList.get(i2)).getAbsoluteAdapterPosition();
                if (absoluteAdapterPosition >= 0) {
                    int i3 = i + 1;
                    this.mDisappearingPositions[i] = absoluteAdapterPosition;
                    i = i3;
                }
            }
            if (i > 0) {
                Arrays.sort(this.mDisappearingPositions, 0, i);
                this.mGrid.fillDisappearingItems(this.mDisappearingPositions, i, this.mPositionToRowInPostLayout);
            }
            this.mPositionToRowInPostLayout.clear();
        }
    }

    /* access modifiers changed from: 0000 */
    public void updatePositionDeltaInPreLayout() {
        if (getChildCount() > 0) {
            this.mPositionDeltaInPreLayout = this.mGrid.getFirstVisibleIndex() - ((LayoutParams) getChildAt(0).getLayoutParams()).getViewLayoutPosition();
            return;
        }
        this.mPositionDeltaInPreLayout = 0;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:82:0x0165, code lost:
        if (r2 >= r6) goto L_0x016a;
     */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x01f5  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onLayoutChildren(androidx.recyclerview.widget.RecyclerView.Recycler r13, androidx.recyclerview.widget.RecyclerView.State r14) {
        /*
            r12 = this;
            int r0 = r12.mNumRows
            if (r0 != 0) goto L_0x0005
            return
        L_0x0005:
            int r0 = r14.getItemCount()
            if (r0 >= 0) goto L_0x000c
            return
        L_0x000c:
            int r0 = r12.mFlag
            r0 = r0 & 64
            if (r0 == 0) goto L_0x001f
            int r0 = r12.getChildCount()
            if (r0 <= 0) goto L_0x001f
            int r13 = r12.mFlag
            r13 = r13 | 128(0x80, float:1.794E-43)
            r12.mFlag = r13
            return
        L_0x001f:
            int r0 = r12.mFlag
            r1 = r0 & 512(0x200, float:7.175E-43)
            if (r1 != 0) goto L_0x002c
            r12.discardLayoutInfo()
            r12.removeAndRecycleAllViews(r13)
            return
        L_0x002c:
            r0 = r0 & -4
            r1 = 1
            r0 = r0 | r1
            r12.mFlag = r0
            r12.saveContext(r13, r14)
            boolean r13 = r14.isPreLayout()
            r0 = -2147483648(0xffffffff80000000, float:-0.0)
            r2 = 0
            if (r13 == 0) goto L_0x00d8
            r12.updatePositionDeltaInPreLayout()
            int r13 = r12.getChildCount()
            androidx.leanback.widget.Grid r14 = r12.mGrid
            if (r14 == 0) goto L_0x00ce
            if (r13 <= 0) goto L_0x00ce
            r14 = 2147483647(0x7fffffff, float:NaN)
            androidx.leanback.widget.BaseGridView r1 = r12.mBaseGridView
            android.view.View r3 = r12.getChildAt(r2)
            androidx.recyclerview.widget.RecyclerView$ViewHolder r1 = r1.getChildViewHolder(r3)
            int r1 = r1.getOldPosition()
            androidx.leanback.widget.BaseGridView r3 = r12.mBaseGridView
            int r4 = r13 + -1
            android.view.View r4 = r12.getChildAt(r4)
            androidx.recyclerview.widget.RecyclerView$ViewHolder r3 = r3.getChildViewHolder(r4)
            int r3 = r3.getOldPosition()
        L_0x006c:
            if (r2 >= r13) goto L_0x00c3
            android.view.View r4 = r12.getChildAt(r2)
            android.view.ViewGroup$LayoutParams r5 = r4.getLayoutParams()
            androidx.leanback.widget.GridLayoutManager$LayoutParams r5 = (androidx.leanback.widget.GridLayoutManager.LayoutParams) r5
            androidx.leanback.widget.BaseGridView r6 = r12.mBaseGridView
            int r6 = r6.getChildAdapterPosition(r4)
            boolean r7 = r5.isItemChanged()
            if (r7 != 0) goto L_0x00b0
            boolean r7 = r5.isItemRemoved()
            if (r7 != 0) goto L_0x00b0
            boolean r7 = r4.isLayoutRequested()
            if (r7 != 0) goto L_0x00b0
            boolean r7 = r4.hasFocus()
            if (r7 != 0) goto L_0x009e
            int r7 = r12.mFocusPosition
            int r8 = r5.getViewAdapterPosition()
            if (r7 == r8) goto L_0x00b0
        L_0x009e:
            boolean r7 = r4.hasFocus()
            if (r7 == 0) goto L_0x00ac
            int r7 = r12.mFocusPosition
            int r5 = r5.getViewAdapterPosition()
            if (r7 != r5) goto L_0x00b0
        L_0x00ac:
            if (r6 < r1) goto L_0x00b0
            if (r6 <= r3) goto L_0x00c0
        L_0x00b0:
            int r5 = r12.getViewMin(r4)
            int r14 = java.lang.Math.min(r14, r5)
            int r4 = r12.getViewMax(r4)
            int r0 = java.lang.Math.max(r0, r4)
        L_0x00c0:
            int r2 = r2 + 1
            goto L_0x006c
        L_0x00c3:
            if (r0 <= r14) goto L_0x00c8
            int r0 = r0 - r14
            r12.mExtraLayoutSpaceInPreLayout = r0
        L_0x00c8:
            r12.appendVisibleItems()
            r12.prependVisibleItems()
        L_0x00ce:
            int r13 = r12.mFlag
            r13 = r13 & -4
            r12.mFlag = r13
            r12.leaveContext()
            return
        L_0x00d8:
            boolean r13 = r14.willRunPredictiveAnimations()
            if (r13 == 0) goto L_0x00e1
            r12.updatePositionToRowMapInPostLayout()
        L_0x00e1:
            boolean r13 = r12.isSmoothScrolling()
            if (r13 != 0) goto L_0x00ec
            int r13 = r12.mFocusScrollStrategy
            if (r13 != 0) goto L_0x00ec
            goto L_0x00ed
        L_0x00ec:
            r1 = r2
        L_0x00ed:
            int r13 = r12.mFocusPosition
            r3 = -1
            if (r13 == r3) goto L_0x00fb
            int r4 = r12.mFocusPositionOffset
            if (r4 == r0) goto L_0x00fb
            int r13 = r13 + r4
            r12.mFocusPosition = r13
            r12.mSubFocusPosition = r2
        L_0x00fb:
            r12.mFocusPositionOffset = r2
            int r13 = r12.mFocusPosition
            android.view.View r13 = r12.findViewByPosition(r13)
            int r0 = r12.mFocusPosition
            int r4 = r12.mSubFocusPosition
            androidx.leanback.widget.BaseGridView r5 = r12.mBaseGridView
            boolean r5 = r5.hasFocus()
            androidx.leanback.widget.Grid r6 = r12.mGrid
            if (r6 == 0) goto L_0x0116
            int r6 = r6.getFirstVisibleIndex()
            goto L_0x0117
        L_0x0116:
            r6 = r3
        L_0x0117:
            androidx.leanback.widget.Grid r7 = r12.mGrid
            if (r7 == 0) goto L_0x0120
            int r7 = r7.getLastVisibleIndex()
            goto L_0x0121
        L_0x0120:
            r7 = r3
        L_0x0121:
            int r8 = r12.mOrientation
            if (r8 != 0) goto L_0x012e
            int r8 = r14.getRemainingScrollHorizontal()
            int r9 = r14.getRemainingScrollVertical()
            goto L_0x0136
        L_0x012e:
            int r9 = r14.getRemainingScrollHorizontal()
            int r8 = r14.getRemainingScrollVertical()
        L_0x0136:
            boolean r10 = r12.layoutInit()
            r11 = 16
            if (r10 == 0) goto L_0x014f
            int r2 = r12.mFlag
            r2 = r2 | 4
            r12.mFlag = r2
            androidx.leanback.widget.Grid r2 = r12.mGrid
            int r3 = r12.mFocusPosition
            r2.setStart(r3)
            r12.fastRelayout()
            goto L_0x017e
        L_0x014f:
            int r10 = r12.mFlag
            r10 = r10 & -5
            r12.mFlag = r10
            r10 = r10 & -17
            if (r1 == 0) goto L_0x015a
            r2 = r11
        L_0x015a:
            r2 = r2 | r10
            r12.mFlag = r2
            if (r1 == 0) goto L_0x016a
            if (r6 < 0) goto L_0x0167
            int r2 = r12.mFocusPosition
            if (r2 > r7) goto L_0x0167
            if (r2 >= r6) goto L_0x016a
        L_0x0167:
            int r6 = r12.mFocusPosition
            r7 = r6
        L_0x016a:
            androidx.leanback.widget.Grid r2 = r12.mGrid
            r2.setStart(r6)
            if (r7 == r3) goto L_0x017e
        L_0x0171:
            boolean r2 = r12.appendOneColumnVisibleItems()
            if (r2 == 0) goto L_0x017e
            android.view.View r2 = r12.findViewByPosition(r7)
            if (r2 != 0) goto L_0x017e
            goto L_0x0171
        L_0x017e:
            r12.updateScrollLimits()
            androidx.leanback.widget.Grid r2 = r12.mGrid
            int r2 = r2.getFirstVisibleIndex()
            androidx.leanback.widget.Grid r3 = r12.mGrid
            int r3 = r3.getLastVisibleIndex()
            int r6 = -r8
            int r7 = -r9
            r12.focusToViewInLayout(r5, r1, r6, r7)
            r12.appendVisibleItems()
            r12.prependVisibleItems()
            androidx.leanback.widget.Grid r6 = r12.mGrid
            int r6 = r6.getFirstVisibleIndex()
            if (r6 != r2) goto L_0x017e
            androidx.leanback.widget.Grid r2 = r12.mGrid
            int r2 = r2.getLastVisibleIndex()
            if (r2 != r3) goto L_0x017e
            r12.removeInvisibleViewsAtFront()
            r12.removeInvisibleViewsAtEnd()
            boolean r14 = r14.willRunPredictiveAnimations()
            if (r14 == 0) goto L_0x01b7
            r12.fillScrapViewsInPostLayout()
        L_0x01b7:
            int r14 = r12.mFlag
            r1 = r14 & 1024(0x400, float:1.435E-42)
            if (r1 == 0) goto L_0x01c2
            r14 = r14 & -1025(0xfffffffffffffbff, float:NaN)
            r12.mFlag = r14
            goto L_0x01c5
        L_0x01c2:
            r12.updateRowSecondarySizeRefresh()
        L_0x01c5:
            int r14 = r12.mFlag
            r14 = r14 & 4
            if (r14 == 0) goto L_0x01e3
            int r14 = r12.mFocusPosition
            if (r14 != r0) goto L_0x01df
            int r0 = r12.mSubFocusPosition
            if (r0 != r4) goto L_0x01df
            android.view.View r14 = r12.findViewByPosition(r14)
            if (r14 != r13) goto L_0x01df
            int r13 = r12.mFlag
            r13 = r13 & 8
            if (r13 == 0) goto L_0x01e3
        L_0x01df:
            r12.dispatchChildSelected()
            goto L_0x01ec
        L_0x01e3:
            int r13 = r12.mFlag
            r13 = r13 & 20
            if (r13 != r11) goto L_0x01ec
            r12.dispatchChildSelected()
        L_0x01ec:
            r12.dispatchChildSelectedAndPositioned()
            int r13 = r12.mFlag
            r13 = r13 & 64
            if (r13 == 0) goto L_0x01fc
            int r13 = r12.getSlideOutDistance()
            r12.scrollDirectionPrimary(r13)
        L_0x01fc:
            int r13 = r12.mFlag
            r13 = r13 & -4
            r12.mFlag = r13
            r12.leaveContext()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.leanback.widget.GridLayoutManager.onLayoutChildren(androidx.recyclerview.widget.RecyclerView$Recycler, androidx.recyclerview.widget.RecyclerView$State):void");
    }

    private void offsetChildrenSecondary(int i) {
        int childCount = getChildCount();
        int i2 = 0;
        if (this.mOrientation == 0) {
            while (i2 < childCount) {
                getChildAt(i2).offsetTopAndBottom(i);
                i2++;
            }
            return;
        }
        while (i2 < childCount) {
            getChildAt(i2).offsetLeftAndRight(i);
            i2++;
        }
    }

    private void offsetChildrenPrimary(int i) {
        int childCount = getChildCount();
        int i2 = 0;
        if (this.mOrientation == 1) {
            while (i2 < childCount) {
                getChildAt(i2).offsetTopAndBottom(i);
                i2++;
            }
            return;
        }
        while (i2 < childCount) {
            getChildAt(i2).offsetLeftAndRight(i);
            i2++;
        }
    }

    public int scrollHorizontallyBy(int i, Recycler recycler, State state) {
        int i2;
        if ((this.mFlag & 512) == 0 || !hasDoneFirstLayout()) {
            return 0;
        }
        saveContext(recycler, state);
        this.mFlag = (this.mFlag & -4) | 2;
        if (this.mOrientation == 0) {
            i2 = scrollDirectionPrimary(i);
        } else {
            i2 = scrollDirectionSecondary(i);
        }
        leaveContext();
        this.mFlag &= -4;
        return i2;
    }

    public int scrollVerticallyBy(int i, Recycler recycler, State state) {
        int i2;
        if ((this.mFlag & 512) == 0 || !hasDoneFirstLayout()) {
            return 0;
        }
        this.mFlag = (this.mFlag & -4) | 2;
        saveContext(recycler, state);
        if (this.mOrientation == 1) {
            i2 = scrollDirectionPrimary(i);
        } else {
            i2 = scrollDirectionSecondary(i);
        }
        leaveContext();
        this.mFlag &= -4;
        return i2;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x003e, code lost:
        if (r7 < r0) goto L_0x0040;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0023, code lost:
        if (r7 > r0) goto L_0x0040;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int scrollDirectionPrimary(int r7) {
        /*
            r6 = this;
            int r0 = r6.mFlag
            r1 = r0 & 64
            r2 = 1
            if (r1 != 0) goto L_0x0041
            r0 = r0 & 3
            if (r0 == r2) goto L_0x0041
            if (r7 <= 0) goto L_0x0026
            androidx.leanback.widget.WindowAlignment r0 = r6.mWindowAlignment
            androidx.leanback.widget.WindowAlignment$Axis r0 = r0.mainAxis()
            boolean r0 = r0.isMaxUnknown()
            if (r0 != 0) goto L_0x0041
            androidx.leanback.widget.WindowAlignment r0 = r6.mWindowAlignment
            androidx.leanback.widget.WindowAlignment$Axis r0 = r0.mainAxis()
            int r0 = r0.getMaxScroll()
            if (r7 <= r0) goto L_0x0041
            goto L_0x0040
        L_0x0026:
            if (r7 >= 0) goto L_0x0041
            androidx.leanback.widget.WindowAlignment r0 = r6.mWindowAlignment
            androidx.leanback.widget.WindowAlignment$Axis r0 = r0.mainAxis()
            boolean r0 = r0.isMinUnknown()
            if (r0 != 0) goto L_0x0041
            androidx.leanback.widget.WindowAlignment r0 = r6.mWindowAlignment
            androidx.leanback.widget.WindowAlignment$Axis r0 = r0.mainAxis()
            int r0 = r0.getMinScroll()
            if (r7 >= r0) goto L_0x0041
        L_0x0040:
            r7 = r0
        L_0x0041:
            r0 = 0
            if (r7 != 0) goto L_0x0045
            return r0
        L_0x0045:
            int r1 = -r7
            r6.offsetChildrenPrimary(r1)
            int r1 = r6.mFlag
            r1 = r1 & 3
            if (r1 != r2) goto L_0x0053
            r6.updateScrollLimits()
            return r7
        L_0x0053:
            int r1 = r6.getChildCount()
            int r3 = r6.mFlag
            r4 = 262144(0x40000, float:3.67342E-40)
            r3 = r3 & r4
            if (r3 == 0) goto L_0x0061
            if (r7 <= 0) goto L_0x0067
            goto L_0x0063
        L_0x0061:
            if (r7 >= 0) goto L_0x0067
        L_0x0063:
            r6.prependVisibleItems()
            goto L_0x006a
        L_0x0067:
            r6.appendVisibleItems()
        L_0x006a:
            int r3 = r6.getChildCount()
            if (r3 <= r1) goto L_0x0072
            r1 = r2
            goto L_0x0073
        L_0x0072:
            r1 = r0
        L_0x0073:
            int r3 = r6.getChildCount()
            int r5 = r6.mFlag
            r4 = r4 & r5
            if (r4 == 0) goto L_0x007f
            if (r7 <= 0) goto L_0x0085
            goto L_0x0081
        L_0x007f:
            if (r7 >= 0) goto L_0x0085
        L_0x0081:
            r6.removeInvisibleViewsAtEnd()
            goto L_0x0088
        L_0x0085:
            r6.removeInvisibleViewsAtFront()
        L_0x0088:
            int r4 = r6.getChildCount()
            if (r4 >= r3) goto L_0x008f
            goto L_0x0090
        L_0x008f:
            r2 = r0
        L_0x0090:
            r0 = r1 | r2
            if (r0 == 0) goto L_0x0097
            r6.updateRowSecondarySizeRefresh()
        L_0x0097:
            androidx.leanback.widget.BaseGridView r0 = r6.mBaseGridView
            r0.invalidate()
            r6.updateScrollLimits()
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.leanback.widget.GridLayoutManager.scrollDirectionPrimary(int):int");
    }

    private int scrollDirectionSecondary(int i) {
        if (i == 0) {
            return 0;
        }
        offsetChildrenSecondary(-i);
        this.mScrollOffsetSecondary += i;
        updateSecondaryScrollLimits();
        this.mBaseGridView.invalidate();
        return i;
    }

    public void collectAdjacentPrefetchPositions(int i, int i2, State state, LayoutPrefetchRegistry layoutPrefetchRegistry) {
        try {
            saveContext(null, state);
            if (this.mOrientation != 0) {
                i = i2;
            }
            if (getChildCount() != 0) {
                if (i != 0) {
                    this.mGrid.collectAdjacentPrefetchPositions(i < 0 ? -this.mExtraLayoutSpace : this.mSizePrimary + this.mExtraLayoutSpace, i, layoutPrefetchRegistry);
                    leaveContext();
                }
            }
        } finally {
            leaveContext();
        }
    }

    public void collectInitialPrefetchPositions(int i, LayoutPrefetchRegistry layoutPrefetchRegistry) {
        int i2 = this.mBaseGridView.mInitialPrefetchItemCount;
        if (i != 0 && i2 != 0) {
            int max = Math.max(0, Math.min(this.mFocusPosition - ((i2 - 1) / 2), i - i2));
            int i3 = max;
            while (i3 < i && i3 < max + i2) {
                layoutPrefetchRegistry.addPosition(i3, 0);
                i3++;
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void updateScrollLimits() {
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        int i6;
        if (this.mState.getItemCount() != 0) {
            if ((this.mFlag & 262144) == 0) {
                i4 = this.mGrid.getLastVisibleIndex();
                i3 = this.mState.getItemCount() - 1;
                i = this.mGrid.getFirstVisibleIndex();
                i2 = 0;
            } else {
                i4 = this.mGrid.getFirstVisibleIndex();
                i = this.mGrid.getLastVisibleIndex();
                i2 = this.mState.getItemCount() - 1;
                i3 = 0;
            }
            if (i4 >= 0 && i >= 0) {
                boolean z = i4 == i3;
                boolean z2 = i == i2;
                if (z || !this.mWindowAlignment.mainAxis().isMaxUnknown() || z2 || !this.mWindowAlignment.mainAxis().isMinUnknown()) {
                    int i7 = Integer.MAX_VALUE;
                    if (z) {
                        i7 = this.mGrid.findRowMax(true, sTwoInts);
                        View findViewByPosition = findViewByPosition(sTwoInts[1]);
                        i5 = getViewCenter(findViewByPosition);
                        int[] alignMultiple = ((LayoutParams) findViewByPosition.getLayoutParams()).getAlignMultiple();
                        if (alignMultiple != null && alignMultiple.length > 0) {
                            i5 += alignMultiple[alignMultiple.length - 1] - alignMultiple[0];
                        }
                    } else {
                        i5 = Integer.MAX_VALUE;
                    }
                    int i8 = Integer.MIN_VALUE;
                    if (z2) {
                        i8 = this.mGrid.findRowMin(false, sTwoInts);
                        i6 = getViewCenter(findViewByPosition(sTwoInts[1]));
                    } else {
                        i6 = Integer.MIN_VALUE;
                    }
                    this.mWindowAlignment.mainAxis().updateMinMax(i8, i7, i6, i5);
                }
            }
        }
    }

    private void updateSecondaryScrollLimits() {
        Axis secondAxis = this.mWindowAlignment.secondAxis();
        int paddingMin = secondAxis.getPaddingMin() - this.mScrollOffsetSecondary;
        int sizeSecondary = getSizeSecondary() + paddingMin;
        secondAxis.updateMinMax(paddingMin, sizeSecondary, paddingMin, sizeSecondary);
    }

    private void initScrollController() {
        this.mWindowAlignment.reset();
        this.mWindowAlignment.horizontal.setSize(getWidth());
        this.mWindowAlignment.vertical.setSize(getHeight());
        this.mWindowAlignment.horizontal.setPadding(getPaddingLeft(), getPaddingRight());
        this.mWindowAlignment.vertical.setPadding(getPaddingTop(), getPaddingBottom());
        this.mSizePrimary = this.mWindowAlignment.mainAxis().getSize();
        this.mScrollOffsetSecondary = 0;
    }

    private void updateScrollController() {
        this.mWindowAlignment.horizontal.setSize(getWidth());
        this.mWindowAlignment.vertical.setSize(getHeight());
        this.mWindowAlignment.horizontal.setPadding(getPaddingLeft(), getPaddingRight());
        this.mWindowAlignment.vertical.setPadding(getPaddingTop(), getPaddingBottom());
        this.mSizePrimary = this.mWindowAlignment.mainAxis().getSize();
    }

    public void scrollToPosition(int i) {
        setSelection(i, 0, false, 0);
    }

    public void smoothScrollToPosition(RecyclerView recyclerView, State state, int i) {
        setSelection(i, 0, true, 0);
    }

    public void setSelection(int i, int i2) {
        setSelection(i, 0, false, i2);
    }

    public void setSelectionSmooth(int i) {
        setSelection(i, 0, true, 0);
    }

    public void setSelectionWithSub(int i, int i2, int i3) {
        setSelection(i, i2, false, i3);
    }

    public int getSelection() {
        return this.mFocusPosition;
    }

    public int getSubSelection() {
        return this.mSubFocusPosition;
    }

    public void setSelection(int i, int i2, boolean z, int i3) {
        if ((this.mFocusPosition != i && i != -1) || i2 != this.mSubFocusPosition || i3 != this.mPrimaryScrollExtra) {
            scrollToSelection(i, i2, z, i3);
        }
    }

    /* access modifiers changed from: 0000 */
    public void scrollToSelection(int i, int i2, boolean z, int i3) {
        this.mPrimaryScrollExtra = i3;
        View findViewByPosition = findViewByPosition(i);
        boolean z2 = !isSmoothScrolling();
        if (!z2 || this.mBaseGridView.isLayoutRequested() || findViewByPosition == null || getAdapterPositionByView(findViewByPosition) != i) {
            int i4 = this.mFlag;
            if ((i4 & 512) == 0 || (i4 & 64) != 0) {
                this.mFocusPosition = i;
                this.mSubFocusPosition = i2;
                this.mFocusPositionOffset = Integer.MIN_VALUE;
            } else if (!z || this.mBaseGridView.isLayoutRequested()) {
                if (!z2) {
                    skipSmoothScrollerOnStopInternal();
                    this.mBaseGridView.stopScroll();
                }
                if (this.mBaseGridView.isLayoutRequested() || findViewByPosition == null || getAdapterPositionByView(findViewByPosition) != i) {
                    this.mFocusPosition = i;
                    this.mSubFocusPosition = i2;
                    this.mFocusPositionOffset = Integer.MIN_VALUE;
                    this.mFlag |= 256;
                    requestLayout();
                } else {
                    this.mFlag |= 32;
                    scrollToView(findViewByPosition, z);
                    this.mFlag &= -33;
                }
            } else {
                this.mFocusPosition = i;
                this.mSubFocusPosition = i2;
                this.mFocusPositionOffset = Integer.MIN_VALUE;
                if (!hasDoneFirstLayout()) {
                    Log.w(getTag(), "setSelectionSmooth should not be called before first layout pass");
                    return;
                }
                int startPositionSmoothScroller = startPositionSmoothScroller(i);
                if (startPositionSmoothScroller != this.mFocusPosition) {
                    this.mFocusPosition = startPositionSmoothScroller;
                    this.mSubFocusPosition = 0;
                }
            }
        } else {
            this.mFlag |= 32;
            scrollToView(findViewByPosition, z);
            this.mFlag &= -33;
        }
    }

    /* access modifiers changed from: 0000 */
    public int startPositionSmoothScroller(int i) {
        C02944 r0 = new GridLinearSmoothScroller() {
            public PointF computeScrollVectorForPosition(int i) {
                if (getChildCount() == 0) {
                    return null;
                }
                GridLayoutManager gridLayoutManager = GridLayoutManager.this;
                boolean z = false;
                int position = gridLayoutManager.getPosition(gridLayoutManager.getChildAt(0));
                int i2 = 1;
                if ((GridLayoutManager.this.mFlag & 262144) == 0 ? i < position : i > position) {
                    z = true;
                }
                if (z) {
                    i2 = -1;
                }
                if (GridLayoutManager.this.mOrientation == 0) {
                    return new PointF((float) i2, 0.0f);
                }
                return new PointF(0.0f, (float) i2);
            }
        };
        r0.setTargetPosition(i);
        startSmoothScroll(r0);
        return r0.getTargetPosition();
    }

    /* access modifiers changed from: 0000 */
    public void skipSmoothScrollerOnStopInternal() {
        GridLinearSmoothScroller gridLinearSmoothScroller = this.mCurrentSmoothScroller;
        if (gridLinearSmoothScroller != null) {
            gridLinearSmoothScroller.mSkipOnStopInternal = true;
        }
    }

    public void startSmoothScroll(SmoothScroller smoothScroller) {
        skipSmoothScrollerOnStopInternal();
        super.startSmoothScroll(smoothScroller);
        if (!smoothScroller.isRunning() || !(smoothScroller instanceof GridLinearSmoothScroller)) {
            this.mCurrentSmoothScroller = null;
            this.mPendingMoveSmoothScroller = null;
            return;
        }
        GridLinearSmoothScroller gridLinearSmoothScroller = (GridLinearSmoothScroller) smoothScroller;
        this.mCurrentSmoothScroller = gridLinearSmoothScroller;
        if (gridLinearSmoothScroller instanceof PendingMoveSmoothScroller) {
            this.mPendingMoveSmoothScroller = (PendingMoveSmoothScroller) gridLinearSmoothScroller;
        } else {
            this.mPendingMoveSmoothScroller = null;
        }
    }

    /* access modifiers changed from: 0000 */
    public void processPendingMovement(boolean z) {
        if (!z ? !hasCreatedFirstItem() : !hasCreatedLastItem()) {
            PendingMoveSmoothScroller pendingMoveSmoothScroller = this.mPendingMoveSmoothScroller;
            if (pendingMoveSmoothScroller == null) {
                boolean z2 = true;
                int i = z ? 1 : -1;
                if (this.mNumRows <= 1) {
                    z2 = false;
                }
                PendingMoveSmoothScroller pendingMoveSmoothScroller2 = new PendingMoveSmoothScroller(i, z2);
                this.mFocusPositionOffset = 0;
                startSmoothScroll(pendingMoveSmoothScroller2);
            } else if (z) {
                pendingMoveSmoothScroller.increasePendingMoves();
            } else {
                pendingMoveSmoothScroller.decreasePendingMoves();
            }
        }
    }

    public void onItemsAdded(RecyclerView recyclerView, int i, int i2) {
        if (this.mFocusPosition != -1) {
            Grid grid = this.mGrid;
            if (grid != null && grid.getFirstVisibleIndex() >= 0) {
                int i3 = this.mFocusPositionOffset;
                if (i3 != Integer.MIN_VALUE && i <= this.mFocusPosition + i3) {
                    this.mFocusPositionOffset = i3 + i2;
                }
            }
        }
        this.mChildrenStates.clear();
    }

    public void onItemsChanged(RecyclerView recyclerView) {
        this.mFocusPositionOffset = 0;
        this.mChildrenStates.clear();
    }

    public void onItemsRemoved(RecyclerView recyclerView, int i, int i2) {
        if (this.mFocusPosition != -1) {
            Grid grid = this.mGrid;
            if (grid != null && grid.getFirstVisibleIndex() >= 0) {
                int i3 = this.mFocusPositionOffset;
                if (i3 != Integer.MIN_VALUE) {
                    int i4 = this.mFocusPosition;
                    int i5 = i4 + i3;
                    if (i <= i5) {
                        if (i + i2 > i5) {
                            int i6 = i3 + (i - i5);
                            this.mFocusPositionOffset = i6;
                            this.mFocusPosition = i4 + i6;
                            this.mFocusPositionOffset = Integer.MIN_VALUE;
                        } else {
                            this.mFocusPositionOffset = i3 - i2;
                        }
                    }
                }
            }
        }
        this.mChildrenStates.clear();
    }

    public void onItemsMoved(RecyclerView recyclerView, int i, int i2, int i3) {
        int i4 = this.mFocusPosition;
        if (i4 != -1) {
            int i5 = this.mFocusPositionOffset;
            if (i5 != Integer.MIN_VALUE) {
                int i6 = i4 + i5;
                if (i <= i6 && i6 < i + i3) {
                    this.mFocusPositionOffset = i5 + (i2 - i);
                } else if (i < i6 && i2 > i6 - i3) {
                    this.mFocusPositionOffset -= i3;
                } else if (i > i6 && i2 < i6) {
                    this.mFocusPositionOffset += i3;
                }
            }
        }
        this.mChildrenStates.clear();
    }

    public void onItemsUpdated(RecyclerView recyclerView, int i, int i2) {
        int i3 = i2 + i;
        while (i < i3) {
            this.mChildrenStates.remove(i);
            i++;
        }
    }

    public boolean onRequestChildFocus(RecyclerView recyclerView, View view, View view2) {
        if ((this.mFlag & 32768) == 0 && getAdapterPositionByView(view) != -1 && (this.mFlag & 35) == 0) {
            scrollToView(view, view2, true);
        }
        return true;
    }

    private int getPrimaryAlignedScrollDistance(View view) {
        return this.mWindowAlignment.mainAxis().getScroll(getViewCenter(view));
    }

    private int getAdjustedPrimaryAlignedScrollDistance(int i, View view, View view2) {
        int subPositionByView = getSubPositionByView(view, view2);
        if (subPositionByView == 0) {
            return i;
        }
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        return i + (layoutParams.getAlignMultiple()[subPositionByView] - layoutParams.getAlignMultiple()[0]);
    }

    private int getSecondaryScrollDistance(View view) {
        return this.mWindowAlignment.secondAxis().getScroll(getViewCenterSecondary(view));
    }

    /* access modifiers changed from: 0000 */
    public void scrollToView(View view, boolean z) {
        scrollToView(view, view == null ? null : view.findFocus(), z);
    }

    /* access modifiers changed from: 0000 */
    public void scrollToView(View view, boolean z, int i, int i2) {
        scrollToView(view, view == null ? null : view.findFocus(), z, i, i2);
    }

    private void scrollToView(View view, View view2, boolean z) {
        scrollToView(view, view2, z, 0, 0);
    }

    private void scrollToView(View view, View view2, boolean z, int i, int i2) {
        if ((this.mFlag & 64) == 0) {
            int adapterPositionByView = getAdapterPositionByView(view);
            int subPositionByView = getSubPositionByView(view, view2);
            if (!(adapterPositionByView == this.mFocusPosition && subPositionByView == this.mSubFocusPosition)) {
                this.mFocusPosition = adapterPositionByView;
                this.mSubFocusPosition = subPositionByView;
                this.mFocusPositionOffset = 0;
                if ((this.mFlag & 3) != 1) {
                    dispatchChildSelected();
                }
                if (this.mBaseGridView.isChildrenDrawingOrderEnabledInternal()) {
                    this.mBaseGridView.invalidate();
                }
            }
            if (view != null) {
                if (!view.hasFocus() && this.mBaseGridView.hasFocus()) {
                    view.requestFocus();
                }
                if ((this.mFlag & 131072) != 0 || !z) {
                    if (!(!getScrollPosition(view, view2, sTwoInts) && i == 0 && i2 == 0)) {
                        int[] iArr = sTwoInts;
                        scrollGrid(iArr[0] + i, iArr[1] + i2, z);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public boolean getScrollPosition(View view, View view2, int[] iArr) {
        int i = this.mFocusScrollStrategy;
        if (i == 1 || i == 2) {
            return getNoneAlignedPosition(view, iArr);
        }
        return getAlignedPosition(view, view2, iArr);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:26:0x009f, code lost:
        if (r2 != null) goto L_0x00a5;
     */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00a7  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00ad  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x00b8  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00ba  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00c3 A[ADDED_TO_REGION] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean getNoneAlignedPosition(android.view.View r13, int[] r14) {
        /*
            r12 = this;
            int r0 = r12.getAdapterPositionByView(r13)
            int r1 = r12.getViewMin(r13)
            int r2 = r12.getViewMax(r13)
            androidx.leanback.widget.WindowAlignment r3 = r12.mWindowAlignment
            androidx.leanback.widget.WindowAlignment$Axis r3 = r3.mainAxis()
            int r3 = r3.getPaddingMin()
            androidx.leanback.widget.WindowAlignment r4 = r12.mWindowAlignment
            androidx.leanback.widget.WindowAlignment$Axis r4 = r4.mainAxis()
            int r4 = r4.getClientSize()
            androidx.leanback.widget.Grid r5 = r12.mGrid
            int r5 = r5.getRowIndex(r0)
            r6 = 1
            r7 = 0
            r8 = 2
            r9 = 0
            if (r1 >= r3) goto L_0x006f
            int r1 = r12.mFocusScrollStrategy
            if (r1 != r8) goto L_0x006c
            r1 = r13
        L_0x0031:
            boolean r10 = r12.prependOneColumnVisibleItems()
            if (r10 == 0) goto L_0x0069
            androidx.leanback.widget.Grid r1 = r12.mGrid
            int r10 = r1.getFirstVisibleIndex()
            androidx.collection.CircularIntArray[] r1 = r1.getItemPositionsInRows(r10, r0)
            r1 = r1[r5]
            int r10 = r1.get(r7)
            android.view.View r10 = r12.findViewByPosition(r10)
            int r11 = r12.getViewMin(r10)
            int r11 = r2 - r11
            if (r11 <= r4) goto L_0x0067
            int r0 = r1.size()
            if (r0 <= r8) goto L_0x0064
            int r0 = r1.get(r8)
            android.view.View r0 = r12.findViewByPosition(r0)
            r2 = r9
            r9 = r0
            goto L_0x00a5
        L_0x0064:
            r2 = r9
            r9 = r10
            goto L_0x00a5
        L_0x0067:
            r1 = r10
            goto L_0x0031
        L_0x0069:
            r2 = r9
            r9 = r1
            goto L_0x00a5
        L_0x006c:
            r2 = r9
        L_0x006d:
            r9 = r13
            goto L_0x00a5
        L_0x006f:
            int r10 = r4 + r3
            if (r2 <= r10) goto L_0x00a4
            int r2 = r12.mFocusScrollStrategy
            if (r2 != r8) goto L_0x00a2
        L_0x0077:
            androidx.leanback.widget.Grid r2 = r12.mGrid
            int r8 = r2.getLastVisibleIndex()
            androidx.collection.CircularIntArray[] r2 = r2.getItemPositionsInRows(r0, r8)
            r2 = r2[r5]
            int r8 = r2.size()
            int r8 = r8 - r6
            int r2 = r2.get(r8)
            android.view.View r2 = r12.findViewByPosition(r2)
            int r8 = r12.getViewMax(r2)
            int r8 = r8 - r1
            if (r8 <= r4) goto L_0x0099
            r2 = r9
            goto L_0x009f
        L_0x0099:
            boolean r8 = r12.appendOneColumnVisibleItems()
            if (r8 != 0) goto L_0x0077
        L_0x009f:
            if (r2 == 0) goto L_0x006d
            goto L_0x00a5
        L_0x00a2:
            r2 = r13
            goto L_0x00a5
        L_0x00a4:
            r2 = r9
        L_0x00a5:
            if (r9 == 0) goto L_0x00ad
            int r0 = r12.getViewMin(r9)
        L_0x00ab:
            int r0 = r0 - r3
            goto L_0x00b6
        L_0x00ad:
            if (r2 == 0) goto L_0x00b5
            int r0 = r12.getViewMax(r2)
            int r3 = r3 + r4
            goto L_0x00ab
        L_0x00b5:
            r0 = r7
        L_0x00b6:
            if (r9 == 0) goto L_0x00ba
            r13 = r9
            goto L_0x00bd
        L_0x00ba:
            if (r2 == 0) goto L_0x00bd
            r13 = r2
        L_0x00bd:
            int r12 = r12.getSecondaryScrollDistance(r13)
            if (r0 != 0) goto L_0x00c7
            if (r12 == 0) goto L_0x00c6
            goto L_0x00c7
        L_0x00c6:
            return r7
        L_0x00c7:
            r14[r7] = r0
            r14[r6] = r12
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.leanback.widget.GridLayoutManager.getNoneAlignedPosition(android.view.View, int[]):boolean");
    }

    private boolean getAlignedPosition(View view, View view2, int[] iArr) {
        int primaryAlignedScrollDistance = getPrimaryAlignedScrollDistance(view);
        if (view2 != null) {
            primaryAlignedScrollDistance = getAdjustedPrimaryAlignedScrollDistance(primaryAlignedScrollDistance, view, view2);
        }
        int secondaryScrollDistance = getSecondaryScrollDistance(view);
        int i = primaryAlignedScrollDistance + this.mPrimaryScrollExtra;
        if (i == 0 && secondaryScrollDistance == 0) {
            iArr[0] = 0;
            iArr[1] = 0;
            return false;
        }
        iArr[0] = i;
        iArr[1] = secondaryScrollDistance;
        return true;
    }

    private void scrollGrid(int i, int i2, boolean z) {
        if ((this.mFlag & 3) == 1) {
            scrollDirectionPrimary(i);
            scrollDirectionSecondary(i2);
            return;
        }
        if (this.mOrientation != 0) {
            int i3 = i2;
            i2 = i;
            i = i3;
        }
        if (z) {
            this.mBaseGridView.smoothScrollBy(i, i2);
            return;
        }
        this.mBaseGridView.scrollBy(i, i2);
        dispatchChildSelectedAndPositioned();
    }

    public void setPruneChild(boolean z) {
        int i = 65536;
        if (((this.mFlag & 65536) != 0) != z) {
            int i2 = this.mFlag & -65537;
            if (!z) {
                i = 0;
            }
            this.mFlag = i2 | i;
            if (z) {
                requestLayout();
            }
        }
    }

    public void setScrollEnabled(boolean z) {
        int i = 0;
        if (((this.mFlag & 131072) != 0) != z) {
            int i2 = this.mFlag & -131073;
            if (z) {
                i = 131072;
            }
            int i3 = i2 | i;
            this.mFlag = i3;
            if ((i3 & 131072) != 0 && this.mFocusScrollStrategy == 0) {
                int i4 = this.mFocusPosition;
                if (i4 != -1) {
                    scrollToSelection(i4, this.mSubFocusPosition, true, this.mPrimaryScrollExtra);
                }
            }
        }
    }

    public boolean isScrollEnabled() {
        return (this.mFlag & 131072) != 0;
    }

    private int findImmediateChildIndex(View view) {
        BaseGridView baseGridView = this.mBaseGridView;
        if (!(baseGridView == null || view == baseGridView)) {
            View findContainingItemView = findContainingItemView(view);
            if (findContainingItemView != null) {
                int childCount = getChildCount();
                for (int i = 0; i < childCount; i++) {
                    if (getChildAt(i) == findContainingItemView) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    /* access modifiers changed from: 0000 */
    public void onFocusChanged(boolean z, int i, Rect rect) {
        if (z) {
            int i2 = this.mFocusPosition;
            while (true) {
                View findViewByPosition = findViewByPosition(i2);
                if (findViewByPosition != null) {
                    if (findViewByPosition.getVisibility() != 0 || !findViewByPosition.hasFocusable()) {
                        i2++;
                    } else {
                        findViewByPosition.requestFocus();
                        return;
                    }
                } else {
                    return;
                }
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void setFocusSearchDisabled(boolean z) {
        this.mFlag = (z ? 32768 : 0) | (this.mFlag & -32769);
    }

    /* JADX WARNING: Removed duplicated region for block: B:70:0x00ca A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x00cb  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View onInterceptFocusSearch(android.view.View r8, int r9) {
        /*
            r7 = this;
            int r0 = r7.mFlag
            r1 = 32768(0x8000, float:4.5918E-41)
            r0 = r0 & r1
            if (r0 == 0) goto L_0x0009
            return r8
        L_0x0009:
            android.view.FocusFinder r0 = android.view.FocusFinder.getInstance()
            r1 = 0
            r2 = 0
            r3 = 2
            r4 = 1
            if (r9 == r3) goto L_0x001d
            if (r9 != r4) goto L_0x0016
            goto L_0x001d
        L_0x0016:
            androidx.leanback.widget.BaseGridView r1 = r7.mBaseGridView
            android.view.View r0 = r0.findNextFocus(r1, r8, r9)
            goto L_0x0054
        L_0x001d:
            boolean r5 = r7.canScrollVertically()
            if (r5 == 0) goto L_0x0030
            if (r9 != r3) goto L_0x0028
            r1 = 130(0x82, float:1.82E-43)
            goto L_0x002a
        L_0x0028:
            r1 = 33
        L_0x002a:
            androidx.leanback.widget.BaseGridView r5 = r7.mBaseGridView
            android.view.View r1 = r0.findNextFocus(r5, r8, r1)
        L_0x0030:
            boolean r5 = r7.canScrollHorizontally()
            if (r5 == 0) goto L_0x0053
            int r1 = r7.getLayoutDirection()
            if (r1 != r4) goto L_0x003e
            r1 = r4
            goto L_0x003f
        L_0x003e:
            r1 = r2
        L_0x003f:
            if (r9 != r3) goto L_0x0043
            r5 = r4
            goto L_0x0044
        L_0x0043:
            r5 = r2
        L_0x0044:
            r1 = r1 ^ r5
            if (r1 == 0) goto L_0x004a
            r1 = 66
            goto L_0x004c
        L_0x004a:
            r1 = 17
        L_0x004c:
            androidx.leanback.widget.BaseGridView r5 = r7.mBaseGridView
            android.view.View r0 = r0.findNextFocus(r5, r8, r1)
            goto L_0x0054
        L_0x0053:
            r0 = r1
        L_0x0054:
            if (r0 == 0) goto L_0x0057
            return r0
        L_0x0057:
            androidx.leanback.widget.BaseGridView r1 = r7.mBaseGridView
            int r1 = r1.getDescendantFocusability()
            r5 = 393216(0x60000, float:5.51013E-40)
            if (r1 != r5) goto L_0x006c
            androidx.leanback.widget.BaseGridView r7 = r7.mBaseGridView
            android.view.ViewParent r7 = r7.getParent()
            android.view.View r7 = r7.focusSearch(r8, r9)
            return r7
        L_0x006c:
            int r1 = r7.getMovement(r9)
            androidx.leanback.widget.BaseGridView r5 = r7.mBaseGridView
            int r5 = r5.getScrollState()
            if (r5 == 0) goto L_0x007a
            r5 = r4
            goto L_0x007b
        L_0x007a:
            r5 = r2
        L_0x007b:
            r6 = 131072(0x20000, float:1.83671E-40)
            if (r1 != r4) goto L_0x0097
            if (r5 != 0) goto L_0x0087
            int r1 = r7.mFlag
            r1 = r1 & 4096(0x1000, float:5.74E-42)
            if (r1 != 0) goto L_0x0088
        L_0x0087:
            r0 = r8
        L_0x0088:
            int r1 = r7.mFlag
            r1 = r1 & r6
            if (r1 == 0) goto L_0x00c8
            boolean r1 = r7.hasCreatedLastItem()
            if (r1 != 0) goto L_0x00c8
            r7.processPendingMovement(r4)
            goto L_0x00c7
        L_0x0097:
            if (r1 != 0) goto L_0x00b1
            if (r5 != 0) goto L_0x00a1
            int r1 = r7.mFlag
            r1 = r1 & 2048(0x800, float:2.87E-42)
            if (r1 != 0) goto L_0x00a2
        L_0x00a1:
            r0 = r8
        L_0x00a2:
            int r1 = r7.mFlag
            r1 = r1 & r6
            if (r1 == 0) goto L_0x00c8
            boolean r1 = r7.hasCreatedFirstItem()
            if (r1 != 0) goto L_0x00c8
            r7.processPendingMovement(r2)
            goto L_0x00c7
        L_0x00b1:
            r2 = 3
            if (r1 != r2) goto L_0x00bd
            if (r5 != 0) goto L_0x00c7
            int r1 = r7.mFlag
            r1 = r1 & 16384(0x4000, float:2.2959E-41)
            if (r1 != 0) goto L_0x00c8
            goto L_0x00c7
        L_0x00bd:
            if (r1 != r3) goto L_0x00c8
            if (r5 != 0) goto L_0x00c7
            int r1 = r7.mFlag
            r1 = r1 & 8192(0x2000, float:1.14794E-41)
            if (r1 != 0) goto L_0x00c8
        L_0x00c7:
            r0 = r8
        L_0x00c8:
            if (r0 == 0) goto L_0x00cb
            return r0
        L_0x00cb:
            androidx.leanback.widget.BaseGridView r0 = r7.mBaseGridView
            android.view.ViewParent r0 = r0.getParent()
            android.view.View r9 = r0.focusSearch(r8, r9)
            if (r9 == 0) goto L_0x00d8
            return r9
        L_0x00d8:
            if (r8 == 0) goto L_0x00db
            goto L_0x00dd
        L_0x00db:
            androidx.leanback.widget.BaseGridView r8 = r7.mBaseGridView
        L_0x00dd:
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.leanback.widget.GridLayoutManager.onInterceptFocusSearch(android.view.View, int):android.view.View");
    }

    /* access modifiers changed from: 0000 */
    public boolean hasPreviousViewInSameRow(int i) {
        Grid grid = this.mGrid;
        if (!(grid == null || i == -1 || grid.getFirstVisibleIndex() < 0)) {
            if (this.mGrid.getFirstVisibleIndex() > 0) {
                return true;
            }
            int i2 = this.mGrid.getLocation(i).row;
            for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
                int adapterPositionByIndex = getAdapterPositionByIndex(childCount);
                Location location = this.mGrid.getLocation(adapterPositionByIndex);
                if (location != null && location.row == i2 && adapterPositionByIndex < i) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean onAddFocusables(RecyclerView recyclerView, ArrayList<View> arrayList, int i, int i2) {
        View view;
        ArrayList<View> arrayList2 = arrayList;
        int i3 = i;
        int i4 = i2;
        if ((this.mFlag & 32768) != 0) {
            return true;
        }
        if (!recyclerView.hasFocus()) {
            int size = arrayList.size();
            if (this.mFocusScrollStrategy != 0) {
                int paddingMin = this.mWindowAlignment.mainAxis().getPaddingMin();
                int clientSize = this.mWindowAlignment.mainAxis().getClientSize() + paddingMin;
                int childCount = getChildCount();
                for (int i5 = 0; i5 < childCount; i5++) {
                    View childAt = getChildAt(i5);
                    if (childAt.getVisibility() == 0 && getViewMin(childAt) >= paddingMin && getViewMax(childAt) <= clientSize) {
                        childAt.addFocusables(arrayList2, i3, i4);
                    }
                }
                if (arrayList.size() == size) {
                    int childCount2 = getChildCount();
                    for (int i6 = 0; i6 < childCount2; i6++) {
                        View childAt2 = getChildAt(i6);
                        if (childAt2.getVisibility() == 0) {
                            childAt2.addFocusables(arrayList2, i3, i4);
                        }
                    }
                }
            } else {
                View findViewByPosition = findViewByPosition(this.mFocusPosition);
                if (findViewByPosition != null) {
                    findViewByPosition.addFocusables(arrayList2, i3, i4);
                }
            }
            if (arrayList.size() == size && recyclerView.isFocusable()) {
                arrayList2.add(recyclerView);
            }
        } else if (this.mPendingMoveSmoothScroller != null) {
            return true;
        } else {
            int movement = getMovement(i3);
            int findImmediateChildIndex = findImmediateChildIndex(recyclerView.findFocus());
            int adapterPositionByIndex = getAdapterPositionByIndex(findImmediateChildIndex);
            if (adapterPositionByIndex == -1) {
                view = null;
            } else {
                view = findViewByPosition(adapterPositionByIndex);
            }
            if (view != null) {
                view.addFocusables(arrayList2, i3, i4);
            }
            if (this.mGrid == null || getChildCount() == 0) {
                return true;
            }
            if ((movement == 3 || movement == 2) && this.mGrid.getNumRows() <= 1) {
                return true;
            }
            Grid grid = this.mGrid;
            int i7 = (grid == null || view == null) ? -1 : grid.getLocation(adapterPositionByIndex).row;
            int size2 = arrayList.size();
            int i8 = (movement == 1 || movement == 3) ? 1 : -1;
            int childCount3 = i8 > 0 ? getChildCount() - 1 : 0;
            int i9 = findImmediateChildIndex == -1 ? i8 > 0 ? 0 : getChildCount() - 1 : findImmediateChildIndex + i8;
            int i10 = i9;
            while (true) {
                if (i8 <= 0) {
                    if (i10 < childCount3) {
                        break;
                    }
                } else if (i10 > childCount3) {
                    break;
                }
                View childAt3 = getChildAt(i10);
                if (childAt3.getVisibility() == 0 && childAt3.hasFocusable()) {
                    if (view == null) {
                        childAt3.addFocusables(arrayList2, i3, i4);
                        if (arrayList.size() > size2) {
                            break;
                        }
                    } else {
                        int adapterPositionByIndex2 = getAdapterPositionByIndex(i10);
                        Location location = this.mGrid.getLocation(adapterPositionByIndex2);
                        if (location != null) {
                            if (movement == 1) {
                                if (location.row == i7 && adapterPositionByIndex2 > adapterPositionByIndex) {
                                    childAt3.addFocusables(arrayList2, i3, i4);
                                    if (arrayList.size() > size2) {
                                        break;
                                    }
                                }
                            } else if (movement == 0) {
                                if (location.row == i7 && adapterPositionByIndex2 < adapterPositionByIndex) {
                                    childAt3.addFocusables(arrayList2, i3, i4);
                                    if (arrayList.size() > size2) {
                                        break;
                                    }
                                }
                            } else if (movement == 3) {
                                int i11 = location.row;
                                if (i11 != i7) {
                                    if (i11 < i7) {
                                        break;
                                    }
                                    childAt3.addFocusables(arrayList2, i3, i4);
                                }
                            } else if (movement == 2) {
                                int i12 = location.row;
                                if (i12 == i7) {
                                    continue;
                                } else if (i12 > i7) {
                                    break;
                                } else {
                                    childAt3.addFocusables(arrayList2, i3, i4);
                                }
                            } else {
                                continue;
                            }
                        }
                    }
                }
                i10 += i8;
            }
        }
        return true;
    }

    /* access modifiers changed from: 0000 */
    public boolean hasCreatedLastItem() {
        int itemCount = getItemCount();
        if (itemCount == 0 || this.mBaseGridView.findViewHolderForAdapterPosition(itemCount - 1) != null) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: 0000 */
    public boolean hasCreatedFirstItem() {
        if (getItemCount() == 0 || this.mBaseGridView.findViewHolderForAdapterPosition(0) != null) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: 0000 */
    public boolean isItemFullyVisible(int i) {
        ViewHolder findViewHolderForAdapterPosition = this.mBaseGridView.findViewHolderForAdapterPosition(i);
        boolean z = false;
        if (findViewHolderForAdapterPosition == null) {
            return false;
        }
        if (findViewHolderForAdapterPosition.itemView.getLeft() >= 0 && findViewHolderForAdapterPosition.itemView.getRight() <= this.mBaseGridView.getWidth() && findViewHolderForAdapterPosition.itemView.getTop() >= 0 && findViewHolderForAdapterPosition.itemView.getBottom() <= this.mBaseGridView.getHeight()) {
            z = true;
        }
        return z;
    }

    /* access modifiers changed from: 0000 */
    public boolean canScrollTo(View view) {
        return view.getVisibility() == 0 && (!hasFocus() || view.hasFocusable());
    }

    /* access modifiers changed from: 0000 */
    public boolean gridOnRequestFocusInDescendants(RecyclerView recyclerView, int i, Rect rect) {
        int i2 = this.mFocusScrollStrategy;
        if (i2 == 1 || i2 == 2) {
            return gridOnRequestFocusInDescendantsUnaligned(i, rect);
        }
        return gridOnRequestFocusInDescendantsAligned(i, rect);
    }

    private boolean gridOnRequestFocusInDescendantsAligned(int i, Rect rect) {
        View findViewByPosition = findViewByPosition(this.mFocusPosition);
        if (findViewByPosition != null) {
            return findViewByPosition.requestFocus(i, rect);
        }
        return false;
    }

    private boolean gridOnRequestFocusInDescendantsUnaligned(int i, Rect rect) {
        int i2;
        int i3;
        int childCount = getChildCount();
        int i4 = -1;
        if ((i & 2) != 0) {
            i4 = childCount;
            i3 = 0;
            i2 = 1;
        } else {
            i3 = childCount - 1;
            i2 = -1;
        }
        int paddingMin = this.mWindowAlignment.mainAxis().getPaddingMin();
        int clientSize = this.mWindowAlignment.mainAxis().getClientSize() + paddingMin;
        while (i3 != i4) {
            View childAt = getChildAt(i3);
            if (childAt.getVisibility() == 0 && getViewMin(childAt) >= paddingMin && getViewMax(childAt) <= clientSize && childAt.requestFocus(i, rect)) {
                return true;
            }
            i3 += i2;
        }
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0035, code lost:
        if (r10 != 130) goto L_0x0046;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x003d, code lost:
        if ((r9.mFlag & 524288) == 0) goto L_0x001b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0043, code lost:
        if ((r9.mFlag & 524288) == 0) goto L_0x0023;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0018, code lost:
        if (r10 != 130) goto L_0x0046;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int getMovement(int r10) {
        /*
            r9 = this;
            int r0 = r9.mOrientation
            r1 = 130(0x82, float:1.82E-43)
            r2 = 66
            r3 = 33
            r4 = 0
            r5 = 3
            r6 = 2
            r7 = 17
            r8 = 1
            if (r0 != 0) goto L_0x002b
            r0 = 262144(0x40000, float:3.67342E-40)
            if (r10 == r7) goto L_0x0025
            if (r10 == r3) goto L_0x0023
            if (r10 == r2) goto L_0x001d
            if (r10 == r1) goto L_0x001b
            goto L_0x0046
        L_0x001b:
            r4 = r5
            goto L_0x0047
        L_0x001d:
            int r9 = r9.mFlag
            r9 = r9 & r0
            if (r9 != 0) goto L_0x0047
            goto L_0x0038
        L_0x0023:
            r4 = r6
            goto L_0x0047
        L_0x0025:
            int r9 = r9.mFlag
            r9 = r9 & r0
            if (r9 != 0) goto L_0x0038
            goto L_0x0047
        L_0x002b:
            if (r0 != r8) goto L_0x0046
            r0 = 524288(0x80000, float:7.34684E-40)
            if (r10 == r7) goto L_0x0040
            if (r10 == r3) goto L_0x0047
            if (r10 == r2) goto L_0x003a
            if (r10 == r1) goto L_0x0038
            goto L_0x0046
        L_0x0038:
            r4 = r8
            goto L_0x0047
        L_0x003a:
            int r9 = r9.mFlag
            r9 = r9 & r0
            if (r9 != 0) goto L_0x0023
            goto L_0x001b
        L_0x0040:
            int r9 = r9.mFlag
            r9 = r9 & r0
            if (r9 != 0) goto L_0x001b
            goto L_0x0023
        L_0x0046:
            r4 = r7
        L_0x0047:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.leanback.widget.GridLayoutManager.getMovement(int):int");
    }

    /* access modifiers changed from: 0000 */
    public int getChildDrawingOrder(RecyclerView recyclerView, int i, int i2) {
        View findViewByPosition = findViewByPosition(this.mFocusPosition);
        if (findViewByPosition == null) {
            return i2;
        }
        int indexOfChild = recyclerView.indexOfChild(findViewByPosition);
        if (i2 < indexOfChild) {
            return i2;
        }
        if (i2 < i - 1) {
            indexOfChild = ((indexOfChild + i) - 1) - i2;
        }
        return indexOfChild;
    }

    public void onAdapterChanged(Adapter adapter, Adapter adapter2) {
        if (adapter != null) {
            discardLayoutInfo();
            this.mFocusPosition = -1;
            this.mFocusPositionOffset = 0;
            this.mChildrenStates.clear();
        }
        if (adapter2 instanceof FacetProviderAdapter) {
            this.mFacetProviderAdapter = (FacetProviderAdapter) adapter2;
        } else {
            this.mFacetProviderAdapter = null;
        }
        super.onAdapterChanged(adapter, adapter2);
    }

    private void discardLayoutInfo() {
        this.mGrid = null;
        this.mRowSizeSecondary = null;
        this.mFlag &= -1025;
    }

    /* access modifiers changed from: 0000 */
    public void setChildrenVisibility(int i) {
        this.mChildVisibility = i;
        if (i != -1) {
            int childCount = getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                getChildAt(i2).setVisibility(this.mChildVisibility);
            }
        }
    }

    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState();
        savedState.index = getSelection();
        Bundle saveAsBundle = this.mChildrenStates.saveAsBundle();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            int adapterPositionByView = getAdapterPositionByView(childAt);
            if (adapterPositionByView != -1) {
                saveAsBundle = this.mChildrenStates.saveOnScreenView(saveAsBundle, childAt, adapterPositionByView);
            }
        }
        savedState.childStates = saveAsBundle;
        return savedState;
    }

    /* access modifiers changed from: 0000 */
    public void onChildRecycled(ViewHolder viewHolder) {
        int absoluteAdapterPosition = viewHolder.getAbsoluteAdapterPosition();
        if (absoluteAdapterPosition != -1) {
            this.mChildrenStates.saveOffscreenView(viewHolder.itemView, absoluteAdapterPosition);
        }
    }

    public void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable instanceof SavedState) {
            SavedState savedState = (SavedState) parcelable;
            this.mFocusPosition = savedState.index;
            this.mFocusPositionOffset = 0;
            this.mChildrenStates.loadFromBundle(savedState.childStates);
            this.mFlag |= 256;
            requestLayout();
        }
    }

    public int getRowCountForAccessibility(Recycler recycler, State state) {
        if (this.mOrientation == 0) {
            Grid grid = this.mGrid;
            if (grid != null) {
                return grid.getNumRows();
            }
        }
        return super.getRowCountForAccessibility(recycler, state);
    }

    public int getColumnCountForAccessibility(Recycler recycler, State state) {
        if (this.mOrientation == 1) {
            Grid grid = this.mGrid;
            if (grid != null) {
                return grid.getNumRows();
            }
        }
        return super.getColumnCountForAccessibility(recycler, state);
    }

    public void onInitializeAccessibilityNodeInfoForItem(Recycler recycler, State state, View view, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
        android.view.ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (this.mGrid != null && (layoutParams instanceof LayoutParams)) {
            int viewAdapterPosition = ((LayoutParams) layoutParams).getViewAdapterPosition();
            int rowIndex = viewAdapterPosition >= 0 ? this.mGrid.getRowIndex(viewAdapterPosition) : -1;
            if (rowIndex >= 0) {
                int numRows = viewAdapterPosition / this.mGrid.getNumRows();
                if (this.mOrientation == 0) {
                    accessibilityNodeInfoCompat.setCollectionItemInfo(CollectionItemInfoCompat.obtain(rowIndex, 1, numRows, 1, false, false));
                } else {
                    accessibilityNodeInfoCompat.setCollectionItemInfo(CollectionItemInfoCompat.obtain(numRows, 1, rowIndex, 1, false, false));
                }
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x002c, code lost:
        if (r5 != false) goto L_0x002e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x003a, code lost:
        if (r5 != false) goto L_0x0030;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x004c, code lost:
        if (r7 == androidx.core.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_DOWN.getId()) goto L_0x002e;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean performAccessibilityAction(androidx.recyclerview.widget.RecyclerView.Recycler r5, androidx.recyclerview.widget.RecyclerView.State r6, int r7, android.os.Bundle r8) {
        /*
            r4 = this;
            boolean r8 = r4.isScrollEnabled()
            r0 = 1
            if (r8 != 0) goto L_0x0008
            return r0
        L_0x0008:
            r4.saveContext(r5, r6)
            int r5 = r4.mFlag
            r6 = 262144(0x40000, float:3.67342E-40)
            r5 = r5 & r6
            r6 = 0
            if (r5 == 0) goto L_0x0015
            r5 = r0
            goto L_0x0016
        L_0x0015:
            r5 = r6
        L_0x0016:
            int r8 = android.os.Build.VERSION.SDK_INT
            r1 = 23
            r2 = 8192(0x2000, float:1.14794E-41)
            r3 = 4096(0x1000, float:5.74E-42)
            if (r8 < r1) goto L_0x004f
            int r8 = r4.mOrientation
            if (r8 != 0) goto L_0x003d
            androidx.core.view.accessibility.AccessibilityNodeInfoCompat$AccessibilityActionCompat r8 = androidx.core.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_LEFT
            int r8 = r8.getId()
            if (r7 != r8) goto L_0x0032
            if (r5 == 0) goto L_0x0030
        L_0x002e:
            r7 = r3
            goto L_0x004f
        L_0x0030:
            r7 = r2
            goto L_0x004f
        L_0x0032:
            androidx.core.view.accessibility.AccessibilityNodeInfoCompat$AccessibilityActionCompat r8 = androidx.core.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_RIGHT
            int r8 = r8.getId()
            if (r7 != r8) goto L_0x004f
            if (r5 == 0) goto L_0x002e
            goto L_0x0030
        L_0x003d:
            androidx.core.view.accessibility.AccessibilityNodeInfoCompat$AccessibilityActionCompat r5 = androidx.core.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_UP
            int r5 = r5.getId()
            if (r7 != r5) goto L_0x0046
            goto L_0x0030
        L_0x0046:
            androidx.core.view.accessibility.AccessibilityNodeInfoCompat$AccessibilityActionCompat r5 = androidx.core.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_DOWN
            int r5 = r5.getId()
            if (r7 != r5) goto L_0x004f
            goto L_0x002e
        L_0x004f:
            if (r7 == r3) goto L_0x005c
            if (r7 == r2) goto L_0x0054
            goto L_0x0062
        L_0x0054:
            r4.processPendingMovement(r6)
            r5 = -1
            r4.processSelectionMoves(r6, r5)
            goto L_0x0062
        L_0x005c:
            r4.processPendingMovement(r0)
            r4.processSelectionMoves(r6, r0)
        L_0x0062:
            r4.leaveContext()
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.leanback.widget.GridLayoutManager.performAccessibilityAction(androidx.recyclerview.widget.RecyclerView$Recycler, androidx.recyclerview.widget.RecyclerView$State, int, android.os.Bundle):boolean");
    }

    /* access modifiers changed from: 0000 */
    public int processSelectionMoves(boolean z, int i) {
        Grid grid = this.mGrid;
        if (grid == null) {
            return i;
        }
        int i2 = this.mFocusPosition;
        int rowIndex = i2 != -1 ? grid.getRowIndex(i2) : -1;
        View view = null;
        int childCount = getChildCount();
        int i3 = 0;
        while (i3 < childCount && i != 0) {
            int i4 = i > 0 ? i3 : (childCount - 1) - i3;
            View childAt = getChildAt(i4);
            if (canScrollTo(childAt)) {
                int adapterPositionByIndex = getAdapterPositionByIndex(i4);
                int rowIndex2 = this.mGrid.getRowIndex(adapterPositionByIndex);
                if (rowIndex == -1) {
                    i2 = adapterPositionByIndex;
                    view = childAt;
                    rowIndex = rowIndex2;
                } else if (rowIndex2 == rowIndex && ((i > 0 && adapterPositionByIndex > i2) || (i < 0 && adapterPositionByIndex < i2))) {
                    i = i > 0 ? i - 1 : i + 1;
                    i2 = adapterPositionByIndex;
                    view = childAt;
                }
            }
            i3++;
        }
        if (view != null) {
            if (z) {
                if (hasFocus()) {
                    this.mFlag |= 32;
                    view.requestFocus();
                    this.mFlag &= -33;
                }
                this.mFocusPosition = i2;
                this.mSubFocusPosition = 0;
            } else {
                scrollToView(view, true);
            }
        }
        return i;
    }

    public void onInitializeAccessibilityNodeInfo(Recycler recycler, State state, AccessibilityNodeInfoCompat accessibilityNodeInfoCompat) {
        int i = VERSION.SDK_INT;
        saveContext(recycler, state);
        int itemCount = state.getItemCount();
        boolean z = (this.mFlag & 262144) != 0;
        if (itemCount > 1 && !isItemFullyVisible(0)) {
            if (i < 23) {
                accessibilityNodeInfoCompat.addAction(8192);
            } else if (this.mOrientation == 0) {
                accessibilityNodeInfoCompat.addAction(z ? AccessibilityActionCompat.ACTION_SCROLL_RIGHT : AccessibilityActionCompat.ACTION_SCROLL_LEFT);
            } else {
                accessibilityNodeInfoCompat.addAction(AccessibilityActionCompat.ACTION_SCROLL_UP);
            }
            accessibilityNodeInfoCompat.setScrollable(true);
        }
        if (itemCount > 1 && !isItemFullyVisible(itemCount - 1)) {
            if (i < 23) {
                accessibilityNodeInfoCompat.addAction(4096);
            } else if (this.mOrientation == 0) {
                accessibilityNodeInfoCompat.addAction(z ? AccessibilityActionCompat.ACTION_SCROLL_LEFT : AccessibilityActionCompat.ACTION_SCROLL_RIGHT);
            } else {
                accessibilityNodeInfoCompat.addAction(AccessibilityActionCompat.ACTION_SCROLL_DOWN);
            }
            accessibilityNodeInfoCompat.setScrollable(true);
        }
        accessibilityNodeInfoCompat.setCollectionInfo(CollectionInfoCompat.obtain(getRowCountForAccessibility(recycler, state), getColumnCountForAccessibility(recycler, state), isLayoutHierarchical(recycler, state), getSelectionModeForAccessibility(recycler, state)));
        leaveContext();
    }
}
