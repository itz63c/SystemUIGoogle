package com.android.systemui.p007qs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Scroller;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.C2013R$layout;
import com.android.systemui.p007qs.QSPanel.QSTileLayout;
import com.android.systemui.p007qs.QSPanel.TileRecord;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

/* renamed from: com.android.systemui.qs.PagedTileLayout */
public class PagedTileLayout extends ViewPager implements QSTileLayout {
    private static final Interpolator SCROLL_CUBIC = $$Lambda$PagedTileLayout$fHkBmUM3caZV4_eDd9apVT7Ho.INSTANCE;
    private final PagerAdapter mAdapter = new PagerAdapter() {
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            viewGroup.removeView((View) obj);
            PagedTileLayout.this.updateListening();
        }

        public Object instantiateItem(ViewGroup viewGroup, int i) {
            if (PagedTileLayout.this.isLayoutRtl()) {
                i = (PagedTileLayout.this.mPages.size() - 1) - i;
            }
            ViewGroup viewGroup2 = (ViewGroup) PagedTileLayout.this.mPages.get(i);
            if (viewGroup2.getParent() != null) {
                viewGroup.removeView(viewGroup2);
            }
            viewGroup.addView(viewGroup2);
            PagedTileLayout.this.updateListening();
            return viewGroup2;
        }

        public int getCount() {
            return PagedTileLayout.this.mPages.size();
        }
    };
    /* access modifiers changed from: private */
    public AnimatorSet mBounceAnimatorSet;
    private final Rect mClippingRect;
    private boolean mDistributeTiles = false;
    private int mHorizontalClipBound;
    private float mLastExpansion;
    private int mLastMaxHeight = -1;
    private int mLayoutDirection;
    private int mLayoutOrientation;
    private boolean mListening;
    private final OnPageChangeListener mOnPageChangeListener = new SimpleOnPageChangeListener() {
        public void onPageSelected(int i) {
            PagedTileLayout.this.updateSelected();
            if (!(PagedTileLayout.this.mPageIndicator == null || PagedTileLayout.this.mPageListener == null)) {
                PageListener access$300 = PagedTileLayout.this.mPageListener;
                boolean z = false;
                if (!PagedTileLayout.this.isLayoutRtl() ? i == 0 : i == PagedTileLayout.this.mPages.size() - 1) {
                    z = true;
                }
                access$300.onPageChanged(z);
            }
        }

        public void onPageScrolled(int i, float f, int i2) {
            if (PagedTileLayout.this.mPageIndicator != null) {
                PagedTileLayout.this.mPageIndicatorPosition = ((float) i) + f;
                PagedTileLayout.this.mPageIndicator.setLocation(PagedTileLayout.this.mPageIndicatorPosition);
                if (PagedTileLayout.this.mPageListener != null) {
                    PageListener access$300 = PagedTileLayout.this.mPageListener;
                    boolean z = true;
                    if (i2 != 0 || (!PagedTileLayout.this.isLayoutRtl() ? i != 0 : i != PagedTileLayout.this.mPages.size() - 1)) {
                        z = false;
                    }
                    access$300.onPageChanged(z);
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public PageIndicator mPageIndicator;
    /* access modifiers changed from: private */
    public float mPageIndicatorPosition;
    /* access modifiers changed from: private */
    public PageListener mPageListener;
    private int mPageToRestore = -1;
    /* access modifiers changed from: private */
    public final ArrayList<TilePage> mPages = new ArrayList<>();
    private Scroller mScroller;
    private final ArrayList<TileRecord> mTiles = new ArrayList<>();

    /* renamed from: com.android.systemui.qs.PagedTileLayout$PageListener */
    public interface PageListener {
        void onPageChanged(boolean z);
    }

    /* renamed from: com.android.systemui.qs.PagedTileLayout$TilePage */
    public static class TilePage extends TileLayout {
        public TilePage(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        public int maxTiles() {
            return Math.max(this.mColumns * this.mRows, 1);
        }

        public boolean updateResources() {
            int dimensionPixelSize = getContext().getResources().getDimensionPixelSize(C2009R$dimen.notification_side_paddings);
            setPadding(dimensionPixelSize, 0, dimensionPixelSize, 0);
            return super.updateResources();
        }
    }

    static /* synthetic */ float lambda$static$0(float f) {
        float f2 = f - 1.0f;
        return (f2 * f2 * f2) + 1.0f;
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public PagedTileLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mScroller = new Scroller(context, SCROLL_CUBIC);
        setAdapter(this.mAdapter);
        setOnPageChangeListener(this.mOnPageChangeListener);
        setCurrentItem(0, false);
        this.mLayoutOrientation = getResources().getConfiguration().orientation;
        this.mLayoutDirection = getLayoutDirection();
        this.mClippingRect = new Rect();
    }

    public void saveInstanceState(Bundle bundle) {
        bundle.putInt("current_page", getCurrentItem());
    }

    public void restoreInstanceState(Bundle bundle) {
        this.mPageToRestore = bundle.getInt("current_page", -1);
    }

    /* access modifiers changed from: protected */
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        int i = this.mLayoutOrientation;
        int i2 = configuration.orientation;
        if (i != i2) {
            this.mLayoutOrientation = i2;
            setCurrentItem(0, false);
            this.mPageToRestore = 0;
        }
    }

    public void onRtlPropertiesChanged(int i) {
        super.onRtlPropertiesChanged(i);
        if (this.mLayoutDirection != i) {
            this.mLayoutDirection = i;
            setAdapter(this.mAdapter);
            setCurrentItem(0, false);
            this.mPageToRestore = 0;
        }
    }

    public void setCurrentItem(int i, boolean z) {
        if (isLayoutRtl()) {
            i = (this.mPages.size() - 1) - i;
        }
        super.setCurrentItem(i, z);
    }

    private int getCurrentPageNumber() {
        int currentItem = getCurrentItem();
        return this.mLayoutDirection == 1 ? (this.mPages.size() - 1) - currentItem : currentItem;
    }

    public void setListening(boolean z) {
        if (this.mListening != z) {
            this.mListening = z;
            updateListening();
        }
    }

    /* access modifiers changed from: private */
    public void updateListening() {
        Iterator it = this.mPages.iterator();
        while (it.hasNext()) {
            TilePage tilePage = (TilePage) it.next();
            tilePage.setListening(tilePage.getParent() == null ? false : this.mListening);
        }
    }

    public void computeScroll() {
        if (this.mScroller.isFinished() || !this.mScroller.computeScrollOffset()) {
            if (isFakeDragging()) {
                endFakeDrag();
                this.mBounceAnimatorSet.start();
                setOffscreenPageLimit(1);
            }
            super.computeScroll();
            return;
        }
        fakeDragBy((float) (getScrollX() - this.mScroller.getCurrX()));
        postInvalidateOnAnimation();
    }

    /* access modifiers changed from: protected */
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mPages.add((TilePage) LayoutInflater.from(getContext()).inflate(C2013R$layout.qs_paged_page, this, false));
        this.mAdapter.notifyDataSetChanged();
    }

    public void setPageIndicator(PageIndicator pageIndicator) {
        this.mPageIndicator = pageIndicator;
        pageIndicator.setNumPages(this.mPages.size());
        this.mPageIndicator.setLocation(this.mPageIndicatorPosition);
    }

    public int getOffsetTop(TileRecord tileRecord) {
        ViewGroup viewGroup = (ViewGroup) tileRecord.tileView.getParent();
        if (viewGroup == null) {
            return 0;
        }
        return viewGroup.getTop() + getTop();
    }

    public void addTile(TileRecord tileRecord) {
        this.mTiles.add(tileRecord);
        this.mDistributeTiles = true;
        requestLayout();
    }

    public void removeTile(TileRecord tileRecord) {
        if (this.mTiles.remove(tileRecord)) {
            this.mDistributeTiles = true;
            requestLayout();
        }
    }

    public void setExpansion(float f) {
        this.mLastExpansion = f;
        updateSelected();
    }

    /* access modifiers changed from: private */
    public void updateSelected() {
        float f = this.mLastExpansion;
        if (f <= 0.0f || f >= 1.0f) {
            boolean z = this.mLastExpansion == 1.0f;
            setImportantForAccessibility(4);
            int currentPageNumber = getCurrentPageNumber();
            int i = 0;
            while (i < this.mPages.size()) {
                ((TilePage) this.mPages.get(i)).setSelected(i == currentPageNumber ? z : false);
                i++;
            }
            setImportantForAccessibility(0);
        }
    }

    public void setPageListener(PageListener pageListener) {
        this.mPageListener = pageListener;
    }

    private void distributeTiles() {
        emptyAndInflateOrRemovePages();
        int maxTiles = ((TilePage) this.mPages.get(0)).maxTiles();
        int size = this.mTiles.size();
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            TileRecord tileRecord = (TileRecord) this.mTiles.get(i2);
            if (((TilePage) this.mPages.get(i)).mRecords.size() == maxTiles) {
                i++;
            }
            ((TilePage) this.mPages.get(i)).addTile(tileRecord);
        }
    }

    private void emptyAndInflateOrRemovePages() {
        int size = this.mTiles.size();
        int max = Math.max(size / ((TilePage) this.mPages.get(0)).maxTiles(), 1);
        if (size > ((TilePage) this.mPages.get(0)).maxTiles() * max) {
            max++;
        }
        int size2 = this.mPages.size();
        for (int i = 0; i < size2; i++) {
            ((TilePage) this.mPages.get(i)).removeAllViews();
        }
        if (size2 != max) {
            while (this.mPages.size() < max) {
                this.mPages.add((TilePage) LayoutInflater.from(getContext()).inflate(C2013R$layout.qs_paged_page, this, false));
            }
            while (this.mPages.size() > max) {
                ArrayList<TilePage> arrayList = this.mPages;
                arrayList.remove(arrayList.size() - 1);
            }
            this.mPageIndicator.setNumPages(this.mPages.size());
            setAdapter(this.mAdapter);
            this.mAdapter.notifyDataSetChanged();
            int i2 = this.mPageToRestore;
            if (i2 != -1) {
                setCurrentItem(i2, false);
                this.mPageToRestore = -1;
            }
        }
    }

    public boolean updateResources() {
        this.mHorizontalClipBound = getContext().getResources().getDimensionPixelSize(C2009R$dimen.notification_side_paddings);
        setPadding(0, 0, 0, getContext().getResources().getDimensionPixelSize(C2009R$dimen.qs_paged_tile_layout_padding_bottom));
        boolean z = false;
        for (int i = 0; i < this.mPages.size(); i++) {
            z |= ((TilePage) this.mPages.get(i)).updateResources();
        }
        if (z) {
            this.mDistributeTiles = true;
            requestLayout();
        }
        return z;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        Rect rect = this.mClippingRect;
        int i5 = this.mHorizontalClipBound;
        rect.set(i5, 0, (i3 - i) - i5, i4 - i2);
        setClipBounds(this.mClippingRect);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int size = this.mTiles.size();
        if (this.mDistributeTiles || this.mLastMaxHeight != MeasureSpec.getSize(i2)) {
            this.mLastMaxHeight = MeasureSpec.getSize(i2);
            if (((TilePage) this.mPages.get(0)).updateMaxRows(i2, size) || this.mDistributeTiles) {
                this.mDistributeTiles = false;
                distributeTiles();
            }
            int i3 = ((TilePage) this.mPages.get(0)).mRows;
            for (int i4 = 0; i4 < this.mPages.size(); i4++) {
                ((TilePage) this.mPages.get(i4)).mRows = i3;
            }
        }
        super.onMeasure(i, i2);
        int childCount = getChildCount();
        int i5 = 0;
        for (int i6 = 0; i6 < childCount; i6++) {
            int measuredHeight = getChildAt(i6).getMeasuredHeight();
            if (measuredHeight > i5) {
                i5 = measuredHeight;
            }
        }
        setMeasuredDimension(getMeasuredWidth(), i5 + getPaddingBottom());
    }

    public int getColumnCount() {
        if (this.mPages.size() == 0) {
            return 0;
        }
        return ((TilePage) this.mPages.get(0)).mColumns;
    }

    public int getNumVisibleTiles() {
        if (this.mPages.size() == 0) {
            return 0;
        }
        return ((TilePage) this.mPages.get(getCurrentPageNumber())).mRecords.size();
    }

    public void startTileReveal(Set<String> set, final Runnable runnable) {
        if (!set.isEmpty() && this.mPages.size() >= 2 && getScrollX() == 0 && beginFakeDrag()) {
            int size = this.mPages.size() - 1;
            TilePage tilePage = (TilePage) this.mPages.get(size);
            ArrayList arrayList = new ArrayList();
            Iterator it = tilePage.mRecords.iterator();
            while (it.hasNext()) {
                TileRecord tileRecord = (TileRecord) it.next();
                if (set.contains(tileRecord.tile.getTileSpec())) {
                    arrayList.add(setupBounceAnimator(tileRecord.tileView, arrayList.size()));
                }
            }
            if (arrayList.isEmpty()) {
                endFakeDrag();
                return;
            }
            AnimatorSet animatorSet = new AnimatorSet();
            this.mBounceAnimatorSet = animatorSet;
            animatorSet.playTogether(arrayList);
            this.mBounceAnimatorSet.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animator) {
                    PagedTileLayout.this.mBounceAnimatorSet = null;
                    runnable.run();
                }
            });
            setOffscreenPageLimit(size);
            int width = getWidth() * size;
            Scroller scroller = this.mScroller;
            int scrollX = getScrollX();
            int scrollY = getScrollY();
            if (isLayoutRtl()) {
                width = -width;
            }
            scroller.startScroll(scrollX, scrollY, width, 0, 750);
            postInvalidateOnAnimation();
        }
    }

    private static Animator setupBounceAnimator(View view, int i) {
        view.setAlpha(0.0f);
        view.setScaleX(0.0f);
        view.setScaleY(0.0f);
        ObjectAnimator ofPropertyValuesHolder = ObjectAnimator.ofPropertyValuesHolder(view, new PropertyValuesHolder[]{PropertyValuesHolder.ofFloat(View.ALPHA, new float[]{1.0f}), PropertyValuesHolder.ofFloat(View.SCALE_X, new float[]{1.0f}), PropertyValuesHolder.ofFloat(View.SCALE_Y, new float[]{1.0f})});
        ofPropertyValuesHolder.setDuration(450);
        ofPropertyValuesHolder.setStartDelay((long) (i * 85));
        ofPropertyValuesHolder.setInterpolator(new OvershootInterpolator(1.3f));
        return ofPropertyValuesHolder;
    }
}
