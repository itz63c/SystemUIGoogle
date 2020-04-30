package androidx.leanback.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import androidx.leanback.R$styleable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ItemAnimator;
import androidx.recyclerview.widget.RecyclerView.RecyclerListener;
import androidx.recyclerview.widget.RecyclerView.State;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.recyclerview.widget.SimpleItemAnimator;

public abstract class BaseGridView extends RecyclerView {
    private boolean mAnimateChildLayout = true;
    RecyclerListener mChainedRecyclerListener;
    private boolean mHasOverlappingRendering = true;
    int mInitialPrefetchItemCount = 4;
    final GridLayoutManager mLayoutManager;
    private OnKeyInterceptListener mOnKeyInterceptListener;
    private OnMotionInterceptListener mOnMotionInterceptListener;
    private OnTouchInterceptListener mOnTouchInterceptListener;
    private OnUnhandledKeyListener mOnUnhandledKeyListener;
    private int mPrivateFlag;
    private ItemAnimator mSavedItemAnimator;
    private SmoothScrollByBehavior mSmoothScrollByBehavior;

    public interface OnKeyInterceptListener {
        boolean onInterceptKeyEvent(KeyEvent keyEvent);
    }

    public interface OnLayoutCompletedListener {
        void onLayoutCompleted(State state);
    }

    public interface OnMotionInterceptListener {
        boolean onInterceptMotionEvent(MotionEvent motionEvent);
    }

    public interface OnTouchInterceptListener {
        boolean onInterceptTouchEvent(MotionEvent motionEvent);
    }

    public interface OnUnhandledKeyListener {
        boolean onUnhandledKey(KeyEvent keyEvent);
    }

    public interface SmoothScrollByBehavior {
        int configSmoothScrollByDuration(int i, int i2);

        Interpolator configSmoothScrollByInterpolator(int i, int i2);
    }

    BaseGridView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this);
        this.mLayoutManager = gridLayoutManager;
        setLayoutManager(gridLayoutManager);
        setPreserveFocusAfterLayout(false);
        setDescendantFocusability(262144);
        setHasFixedSize(true);
        setChildrenDrawingOrderEnabled(true);
        setWillNotDraw(true);
        setOverScrollMode(2);
        ((SimpleItemAnimator) getItemAnimator()).setSupportsChangeAnimations(false);
        super.setRecyclerListener(new RecyclerListener() {
            public void onViewRecycled(ViewHolder viewHolder) {
                BaseGridView.this.mLayoutManager.onChildRecycled(viewHolder);
                RecyclerListener recyclerListener = BaseGridView.this.mChainedRecyclerListener;
                if (recyclerListener != null) {
                    recyclerListener.onViewRecycled(viewHolder);
                }
            }
        });
    }

    /* access modifiers changed from: 0000 */
    public void initBaseGridViewAttributes(Context context, AttributeSet attributeSet) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.lbBaseGridView);
        this.mLayoutManager.setFocusOutAllowed(obtainStyledAttributes.getBoolean(R$styleable.lbBaseGridView_focusOutFront, false), obtainStyledAttributes.getBoolean(R$styleable.lbBaseGridView_focusOutEnd, false));
        this.mLayoutManager.setFocusOutSideAllowed(obtainStyledAttributes.getBoolean(R$styleable.lbBaseGridView_focusOutSideStart, true), obtainStyledAttributes.getBoolean(R$styleable.lbBaseGridView_focusOutSideEnd, true));
        this.mLayoutManager.setVerticalSpacing(obtainStyledAttributes.getDimensionPixelSize(R$styleable.lbBaseGridView_android_verticalSpacing, obtainStyledAttributes.getDimensionPixelSize(R$styleable.lbBaseGridView_verticalMargin, 0)));
        this.mLayoutManager.setHorizontalSpacing(obtainStyledAttributes.getDimensionPixelSize(R$styleable.lbBaseGridView_android_horizontalSpacing, obtainStyledAttributes.getDimensionPixelSize(R$styleable.lbBaseGridView_horizontalMargin, 0)));
        if (obtainStyledAttributes.hasValue(R$styleable.lbBaseGridView_android_gravity)) {
            setGravity(obtainStyledAttributes.getInt(R$styleable.lbBaseGridView_android_gravity, 0));
        }
        obtainStyledAttributes.recycle();
    }

    public void setWindowAlignment(int i) {
        this.mLayoutManager.setWindowAlignment(i);
        requestLayout();
    }

    public void setWindowAlignmentOffset(int i) {
        this.mLayoutManager.setWindowAlignmentOffset(i);
        requestLayout();
    }

    public void setWindowAlignmentOffsetPercent(float f) {
        this.mLayoutManager.setWindowAlignmentOffsetPercent(f);
        requestLayout();
    }

    public void setItemAlignmentOffset(int i) {
        this.mLayoutManager.setItemAlignmentOffset(i);
        requestLayout();
    }

    public void setItemAlignmentOffsetWithPadding(boolean z) {
        this.mLayoutManager.setItemAlignmentOffsetWithPadding(z);
        requestLayout();
    }

    public void setItemAlignmentOffsetPercent(float f) {
        this.mLayoutManager.setItemAlignmentOffsetPercent(f);
        requestLayout();
    }

    public void setItemAlignmentViewId(int i) {
        this.mLayoutManager.setItemAlignmentViewId(i);
    }

    public int getVerticalSpacing() {
        return this.mLayoutManager.getVerticalSpacing();
    }

    @SuppressLint({"ReferencesDeprecated"})
    public void setOnChildSelectedListener(OnChildSelectedListener onChildSelectedListener) {
        this.mLayoutManager.setOnChildSelectedListener(onChildSelectedListener);
    }

    public void setOnChildViewHolderSelectedListener(OnChildViewHolderSelectedListener onChildViewHolderSelectedListener) {
        this.mLayoutManager.setOnChildViewHolderSelectedListener(onChildViewHolderSelectedListener);
    }

    public void addOnChildViewHolderSelectedListener(OnChildViewHolderSelectedListener onChildViewHolderSelectedListener) {
        this.mLayoutManager.addOnChildViewHolderSelectedListener(onChildViewHolderSelectedListener);
    }

    public void removeOnChildViewHolderSelectedListener(OnChildViewHolderSelectedListener onChildViewHolderSelectedListener) {
        this.mLayoutManager.removeOnChildViewHolderSelectedListener(onChildViewHolderSelectedListener);
    }

    public void setSelectedPosition(int i) {
        this.mLayoutManager.setSelection(i, 0);
    }

    public void setSelectedPositionSmooth(int i) {
        this.mLayoutManager.setSelectionSmooth(i);
    }

    public void setSelectedPosition(final int i, final ViewHolderTask viewHolderTask) {
        if (viewHolderTask != null) {
            ViewHolder findViewHolderForPosition = findViewHolderForPosition(i);
            if (findViewHolderForPosition == null || hasPendingAdapterUpdates()) {
                addOnChildViewHolderSelectedListener(new OnChildViewHolderSelectedListener() {
                    public void onChildViewHolderSelectedAndPositioned(RecyclerView recyclerView, ViewHolder viewHolder, int i, int i2) {
                        if (i == i) {
                            BaseGridView.this.removeOnChildViewHolderSelectedListener(this);
                            viewHolderTask.run(viewHolder);
                        }
                    }
                });
            } else {
                viewHolderTask.run(findViewHolderForPosition);
            }
        }
        setSelectedPosition(i);
    }

    public int getSelectedPosition() {
        return this.mLayoutManager.getSelection();
    }

    public int getSelectedSubPosition() {
        return this.mLayoutManager.getSubSelection();
    }

    public void setAnimateChildLayout(boolean z) {
        if (this.mAnimateChildLayout != z) {
            this.mAnimateChildLayout = z;
            if (!z) {
                this.mSavedItemAnimator = getItemAnimator();
                super.setItemAnimator(null);
                return;
            }
            super.setItemAnimator(this.mSavedItemAnimator);
        }
    }

    public void setGravity(int i) {
        this.mLayoutManager.setGravity(i);
        requestLayout();
    }

    public boolean onRequestFocusInDescendants(int i, Rect rect) {
        if ((this.mPrivateFlag & 1) == 1) {
            return false;
        }
        return this.mLayoutManager.gridOnRequestFocusInDescendants(this, i, rect);
    }

    public int getChildDrawingOrder(int i, int i2) {
        return this.mLayoutManager.getChildDrawingOrder(this, i, i2);
    }

    /* access modifiers changed from: 0000 */
    public final boolean isChildrenDrawingOrderEnabledInternal() {
        return isChildrenDrawingOrderEnabled();
    }

    public View focusSearch(int i) {
        if (isFocused()) {
            GridLayoutManager gridLayoutManager = this.mLayoutManager;
            View findViewByPosition = gridLayoutManager.findViewByPosition(gridLayoutManager.getSelection());
            if (findViewByPosition != null) {
                return focusSearch(findViewByPosition, i);
            }
        }
        return super.focusSearch(i);
    }

    /* access modifiers changed from: protected */
    public void onFocusChanged(boolean z, int i, Rect rect) {
        super.onFocusChanged(z, i, rect);
        this.mLayoutManager.onFocusChanged(z, i, rect);
    }

    public final void setFocusSearchDisabled(boolean z) {
        setDescendantFocusability(z ? 393216 : 262144);
        this.mLayoutManager.setFocusSearchDisabled(z);
    }

    public void setChildrenVisibility(int i) {
        this.mLayoutManager.setChildrenVisibility(i);
    }

    public void setPruneChild(boolean z) {
        this.mLayoutManager.setPruneChild(z);
    }

    public void setScrollEnabled(boolean z) {
        this.mLayoutManager.setScrollEnabled(z);
    }

    public boolean hasPreviousViewInSameRow(int i) {
        return this.mLayoutManager.hasPreviousViewInSameRow(i);
    }

    public void setOnTouchInterceptListener(OnTouchInterceptListener onTouchInterceptListener) {
        this.mOnTouchInterceptListener = onTouchInterceptListener;
    }

    public void setOnKeyInterceptListener(OnKeyInterceptListener onKeyInterceptListener) {
        this.mOnKeyInterceptListener = onKeyInterceptListener;
    }

    public void setOnUnhandledKeyListener(OnUnhandledKeyListener onUnhandledKeyListener) {
        this.mOnUnhandledKeyListener = onUnhandledKeyListener;
    }

    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        OnKeyInterceptListener onKeyInterceptListener = this.mOnKeyInterceptListener;
        boolean z = true;
        if ((onKeyInterceptListener != null && onKeyInterceptListener.onInterceptKeyEvent(keyEvent)) || super.dispatchKeyEvent(keyEvent)) {
            return true;
        }
        OnUnhandledKeyListener onUnhandledKeyListener = this.mOnUnhandledKeyListener;
        if (onUnhandledKeyListener == null || !onUnhandledKeyListener.onUnhandledKey(keyEvent)) {
            z = false;
        }
        return z;
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        OnTouchInterceptListener onTouchInterceptListener = this.mOnTouchInterceptListener;
        if (onTouchInterceptListener == null || !onTouchInterceptListener.onInterceptTouchEvent(motionEvent)) {
            return super.dispatchTouchEvent(motionEvent);
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean dispatchGenericFocusedEvent(MotionEvent motionEvent) {
        OnMotionInterceptListener onMotionInterceptListener = this.mOnMotionInterceptListener;
        if (onMotionInterceptListener == null || !onMotionInterceptListener.onInterceptMotionEvent(motionEvent)) {
            return super.dispatchGenericFocusedEvent(motionEvent);
        }
        return true;
    }

    public final void setSaveChildrenPolicy(int i) {
        this.mLayoutManager.mChildrenStates.setSavePolicy(i);
    }

    public boolean hasOverlappingRendering() {
        return this.mHasOverlappingRendering;
    }

    public void setHasOverlappingRendering(boolean z) {
        this.mHasOverlappingRendering = z;
    }

    public void onRtlPropertiesChanged(int i) {
        this.mLayoutManager.onRtlPropertiesChanged(i);
    }

    public void animateOut() {
        this.mLayoutManager.slideOut();
    }

    public void animateIn() {
        this.mLayoutManager.slideIn();
    }

    public void scrollToPosition(int i) {
        if (this.mLayoutManager.isSlidingChildViews()) {
            this.mLayoutManager.setSelectionWithSub(i, 0, 0);
        } else {
            super.scrollToPosition(i);
        }
    }

    public void smoothScrollToPosition(int i) {
        if (this.mLayoutManager.isSlidingChildViews()) {
            this.mLayoutManager.setSelectionWithSub(i, 0, 0);
        } else {
            super.smoothScrollToPosition(i);
        }
    }

    public void smoothScrollBy(int i, int i2) {
        SmoothScrollByBehavior smoothScrollByBehavior = this.mSmoothScrollByBehavior;
        if (smoothScrollByBehavior != null) {
            smoothScrollBy(i, i2, smoothScrollByBehavior.configSmoothScrollByInterpolator(i, i2), this.mSmoothScrollByBehavior.configSmoothScrollByDuration(i, i2));
        } else {
            smoothScrollBy(i, i2, null, Integer.MIN_VALUE);
        }
    }

    public void smoothScrollBy(int i, int i2, Interpolator interpolator) {
        SmoothScrollByBehavior smoothScrollByBehavior = this.mSmoothScrollByBehavior;
        if (smoothScrollByBehavior != null) {
            smoothScrollBy(i, i2, interpolator, smoothScrollByBehavior.configSmoothScrollByDuration(i, i2));
        } else {
            smoothScrollBy(i, i2, interpolator, Integer.MIN_VALUE);
        }
    }

    public void removeView(View view) {
        boolean z = view.hasFocus() && isFocusable();
        if (z) {
            this.mPrivateFlag = 1 | this.mPrivateFlag;
            requestFocus();
        }
        super.removeView(view);
        if (z) {
            this.mPrivateFlag ^= -2;
        }
    }

    public void removeViewAt(int i) {
        boolean hasFocus = getChildAt(i).hasFocus();
        if (hasFocus) {
            this.mPrivateFlag |= 1;
            requestFocus();
        }
        super.removeViewAt(i);
        if (hasFocus) {
            this.mPrivateFlag ^= -2;
        }
    }
}
