package androidx.recyclerview.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.accessibility.AccessibilityEvent;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import androidx.recyclerview.widget.RecyclerView.LayoutManager.LayoutPrefetchRegistry;
import androidx.recyclerview.widget.RecyclerView.LayoutManager.Properties;
import androidx.recyclerview.widget.RecyclerView.Recycler;
import androidx.recyclerview.widget.RecyclerView.SmoothScroller.ScrollVectorProvider;
import androidx.recyclerview.widget.RecyclerView.State;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

public class StaggeredGridLayoutManager extends LayoutManager implements ScrollVectorProvider {
    private final AnchorInfo mAnchorInfo = new AnchorInfo();
    private final Runnable mCheckForGapsRunnable = new Runnable() {
        public void run() {
            StaggeredGridLayoutManager.this.checkForGaps();
        }
    };
    private int mFullSizeSpec;
    private int mGapStrategy = 2;
    private boolean mLaidOutInvalidFullSpan = false;
    private boolean mLastLayoutFromEnd;
    private boolean mLastLayoutRTL;
    private final LayoutState mLayoutState;
    LazySpanLookup mLazySpanLookup = new LazySpanLookup();
    private int mOrientation;
    private SavedState mPendingSavedState;
    int mPendingScrollPosition = -1;
    int mPendingScrollPositionOffset = Integer.MIN_VALUE;
    private int[] mPrefetchDistances;
    OrientationHelper mPrimaryOrientation;
    private BitSet mRemainingSpans;
    boolean mReverseLayout = false;
    OrientationHelper mSecondaryOrientation;
    boolean mShouldReverseLayout = false;
    private int mSizePerSpan;
    private boolean mSmoothScrollbarEnabled = true;
    private int mSpanCount = -1;
    Span[] mSpans;
    private final Rect mTmpRect = new Rect();

    class AnchorInfo {
        boolean mInvalidateOffsets;
        boolean mLayoutFromEnd;
        int mOffset;
        int mPosition;
        int[] mSpanReferenceLines;
        boolean mValid;

        AnchorInfo() {
            reset();
        }

        /* access modifiers changed from: 0000 */
        public void reset() {
            this.mPosition = -1;
            this.mOffset = Integer.MIN_VALUE;
            this.mLayoutFromEnd = false;
            this.mInvalidateOffsets = false;
            this.mValid = false;
            int[] iArr = this.mSpanReferenceLines;
            if (iArr != null) {
                Arrays.fill(iArr, -1);
            }
        }

        /* access modifiers changed from: 0000 */
        public void saveSpanReferenceLines(Span[] spanArr) {
            int length = spanArr.length;
            int[] iArr = this.mSpanReferenceLines;
            if (iArr == null || iArr.length < length) {
                this.mSpanReferenceLines = new int[StaggeredGridLayoutManager.this.mSpans.length];
            }
            for (int i = 0; i < length; i++) {
                this.mSpanReferenceLines[i] = spanArr[i].getStartLine(Integer.MIN_VALUE);
            }
        }

        /* access modifiers changed from: 0000 */
        public void assignCoordinateFromPadding() {
            int i;
            if (this.mLayoutFromEnd) {
                i = StaggeredGridLayoutManager.this.mPrimaryOrientation.getEndAfterPadding();
            } else {
                i = StaggeredGridLayoutManager.this.mPrimaryOrientation.getStartAfterPadding();
            }
            this.mOffset = i;
        }

        /* access modifiers changed from: 0000 */
        public void assignCoordinateFromPadding(int i) {
            if (this.mLayoutFromEnd) {
                this.mOffset = StaggeredGridLayoutManager.this.mPrimaryOrientation.getEndAfterPadding() - i;
            } else {
                this.mOffset = StaggeredGridLayoutManager.this.mPrimaryOrientation.getStartAfterPadding() + i;
            }
        }
    }

    public static class LayoutParams extends androidx.recyclerview.widget.RecyclerView.LayoutParams {
        boolean mFullSpan;
        Span mSpan;

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

        public boolean isFullSpan() {
            return this.mFullSpan;
        }
    }

    static class LazySpanLookup {
        int[] mData;
        List<FullSpanItem> mFullSpanItems;

        @SuppressLint({"BanParcelableUsage"})
        static class FullSpanItem implements Parcelable {
            public static final Creator<FullSpanItem> CREATOR = new Creator<FullSpanItem>() {
                public FullSpanItem createFromParcel(Parcel parcel) {
                    return new FullSpanItem(parcel);
                }

                public FullSpanItem[] newArray(int i) {
                    return new FullSpanItem[i];
                }
            };
            int mGapDir;
            int[] mGapPerSpan;
            boolean mHasUnwantedGapAfter;
            int mPosition;

            public int describeContents() {
                return 0;
            }

            FullSpanItem(Parcel parcel) {
                this.mPosition = parcel.readInt();
                this.mGapDir = parcel.readInt();
                boolean z = true;
                if (parcel.readInt() != 1) {
                    z = false;
                }
                this.mHasUnwantedGapAfter = z;
                int readInt = parcel.readInt();
                if (readInt > 0) {
                    int[] iArr = new int[readInt];
                    this.mGapPerSpan = iArr;
                    parcel.readIntArray(iArr);
                }
            }

            FullSpanItem() {
            }

            /* access modifiers changed from: 0000 */
            public int getGapForSpan(int i) {
                int[] iArr = this.mGapPerSpan;
                if (iArr == null) {
                    return 0;
                }
                return iArr[i];
            }

            public void writeToParcel(Parcel parcel, int i) {
                parcel.writeInt(this.mPosition);
                parcel.writeInt(this.mGapDir);
                parcel.writeInt(this.mHasUnwantedGapAfter ? 1 : 0);
                int[] iArr = this.mGapPerSpan;
                if (iArr == null || iArr.length <= 0) {
                    parcel.writeInt(0);
                    return;
                }
                parcel.writeInt(iArr.length);
                parcel.writeIntArray(this.mGapPerSpan);
            }

            public String toString() {
                StringBuilder sb = new StringBuilder();
                sb.append("FullSpanItem{mPosition=");
                sb.append(this.mPosition);
                sb.append(", mGapDir=");
                sb.append(this.mGapDir);
                sb.append(", mHasUnwantedGapAfter=");
                sb.append(this.mHasUnwantedGapAfter);
                sb.append(", mGapPerSpan=");
                sb.append(Arrays.toString(this.mGapPerSpan));
                sb.append('}');
                return sb.toString();
            }
        }

        LazySpanLookup() {
        }

        /* access modifiers changed from: 0000 */
        public int forceInvalidateAfter(int i) {
            List<FullSpanItem> list = this.mFullSpanItems;
            if (list != null) {
                for (int size = list.size() - 1; size >= 0; size--) {
                    if (((FullSpanItem) this.mFullSpanItems.get(size)).mPosition >= i) {
                        this.mFullSpanItems.remove(size);
                    }
                }
            }
            return invalidateAfter(i);
        }

        /* access modifiers changed from: 0000 */
        public int invalidateAfter(int i) {
            int[] iArr = this.mData;
            if (iArr == null || i >= iArr.length) {
                return -1;
            }
            int invalidateFullSpansAfter = invalidateFullSpansAfter(i);
            if (invalidateFullSpansAfter == -1) {
                int[] iArr2 = this.mData;
                Arrays.fill(iArr2, i, iArr2.length, -1);
                return this.mData.length;
            }
            int i2 = invalidateFullSpansAfter + 1;
            Arrays.fill(this.mData, i, i2, -1);
            return i2;
        }

        /* access modifiers changed from: 0000 */
        public int getSpan(int i) {
            int[] iArr = this.mData;
            if (iArr == null || i >= iArr.length) {
                return -1;
            }
            return iArr[i];
        }

        /* access modifiers changed from: 0000 */
        public void setSpan(int i, Span span) {
            ensureSize(i);
            this.mData[i] = span.mIndex;
        }

        /* access modifiers changed from: 0000 */
        public int sizeForPosition(int i) {
            int length = this.mData.length;
            while (length <= i) {
                length *= 2;
            }
            return length;
        }

        /* access modifiers changed from: 0000 */
        public void ensureSize(int i) {
            int[] iArr = this.mData;
            if (iArr == null) {
                int[] iArr2 = new int[(Math.max(i, 10) + 1)];
                this.mData = iArr2;
                Arrays.fill(iArr2, -1);
            } else if (i >= iArr.length) {
                int[] iArr3 = new int[sizeForPosition(i)];
                this.mData = iArr3;
                System.arraycopy(iArr, 0, iArr3, 0, iArr.length);
                int[] iArr4 = this.mData;
                Arrays.fill(iArr4, iArr.length, iArr4.length, -1);
            }
        }

        /* access modifiers changed from: 0000 */
        public void clear() {
            int[] iArr = this.mData;
            if (iArr != null) {
                Arrays.fill(iArr, -1);
            }
            this.mFullSpanItems = null;
        }

        /* access modifiers changed from: 0000 */
        public void offsetForRemoval(int i, int i2) {
            int[] iArr = this.mData;
            if (iArr != null && i < iArr.length) {
                int i3 = i + i2;
                ensureSize(i3);
                int[] iArr2 = this.mData;
                System.arraycopy(iArr2, i3, iArr2, i, (iArr2.length - i) - i2);
                int[] iArr3 = this.mData;
                Arrays.fill(iArr3, iArr3.length - i2, iArr3.length, -1);
                offsetFullSpansForRemoval(i, i2);
            }
        }

        private void offsetFullSpansForRemoval(int i, int i2) {
            List<FullSpanItem> list = this.mFullSpanItems;
            if (list != null) {
                int i3 = i + i2;
                for (int size = list.size() - 1; size >= 0; size--) {
                    FullSpanItem fullSpanItem = (FullSpanItem) this.mFullSpanItems.get(size);
                    int i4 = fullSpanItem.mPosition;
                    if (i4 >= i) {
                        if (i4 < i3) {
                            this.mFullSpanItems.remove(size);
                        } else {
                            fullSpanItem.mPosition = i4 - i2;
                        }
                    }
                }
            }
        }

        /* access modifiers changed from: 0000 */
        public void offsetForAddition(int i, int i2) {
            int[] iArr = this.mData;
            if (iArr != null && i < iArr.length) {
                int i3 = i + i2;
                ensureSize(i3);
                int[] iArr2 = this.mData;
                System.arraycopy(iArr2, i, iArr2, i3, (iArr2.length - i) - i2);
                Arrays.fill(this.mData, i, i3, -1);
                offsetFullSpansForAddition(i, i2);
            }
        }

        private void offsetFullSpansForAddition(int i, int i2) {
            List<FullSpanItem> list = this.mFullSpanItems;
            if (list != null) {
                for (int size = list.size() - 1; size >= 0; size--) {
                    FullSpanItem fullSpanItem = (FullSpanItem) this.mFullSpanItems.get(size);
                    int i3 = fullSpanItem.mPosition;
                    if (i3 >= i) {
                        fullSpanItem.mPosition = i3 + i2;
                    }
                }
            }
        }

        private int invalidateFullSpansAfter(int i) {
            if (this.mFullSpanItems == null) {
                return -1;
            }
            FullSpanItem fullSpanItem = getFullSpanItem(i);
            if (fullSpanItem != null) {
                this.mFullSpanItems.remove(fullSpanItem);
            }
            int size = this.mFullSpanItems.size();
            int i2 = 0;
            while (true) {
                if (i2 >= size) {
                    i2 = -1;
                    break;
                } else if (((FullSpanItem) this.mFullSpanItems.get(i2)).mPosition >= i) {
                    break;
                } else {
                    i2++;
                }
            }
            if (i2 == -1) {
                return -1;
            }
            FullSpanItem fullSpanItem2 = (FullSpanItem) this.mFullSpanItems.get(i2);
            this.mFullSpanItems.remove(i2);
            return fullSpanItem2.mPosition;
        }

        public void addFullSpanItem(FullSpanItem fullSpanItem) {
            if (this.mFullSpanItems == null) {
                this.mFullSpanItems = new ArrayList();
            }
            int size = this.mFullSpanItems.size();
            for (int i = 0; i < size; i++) {
                FullSpanItem fullSpanItem2 = (FullSpanItem) this.mFullSpanItems.get(i);
                if (fullSpanItem2.mPosition == fullSpanItem.mPosition) {
                    this.mFullSpanItems.remove(i);
                }
                if (fullSpanItem2.mPosition >= fullSpanItem.mPosition) {
                    this.mFullSpanItems.add(i, fullSpanItem);
                    return;
                }
            }
            this.mFullSpanItems.add(fullSpanItem);
        }

        public FullSpanItem getFullSpanItem(int i) {
            List<FullSpanItem> list = this.mFullSpanItems;
            if (list == null) {
                return null;
            }
            for (int size = list.size() - 1; size >= 0; size--) {
                FullSpanItem fullSpanItem = (FullSpanItem) this.mFullSpanItems.get(size);
                if (fullSpanItem.mPosition == i) {
                    return fullSpanItem;
                }
            }
            return null;
        }

        public FullSpanItem getFirstFullSpanItemInRange(int i, int i2, int i3, boolean z) {
            List<FullSpanItem> list = this.mFullSpanItems;
            if (list == null) {
                return null;
            }
            int size = list.size();
            for (int i4 = 0; i4 < size; i4++) {
                FullSpanItem fullSpanItem = (FullSpanItem) this.mFullSpanItems.get(i4);
                int i5 = fullSpanItem.mPosition;
                if (i5 >= i2) {
                    return null;
                }
                if (i5 >= i && (i3 == 0 || fullSpanItem.mGapDir == i3 || (z && fullSpanItem.mHasUnwantedGapAfter))) {
                    return fullSpanItem;
                }
            }
            return null;
        }
    }

    @SuppressLint({"BanParcelableUsage"})
    public static class SavedState implements Parcelable {
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        boolean mAnchorLayoutFromEnd;
        int mAnchorPosition;
        List<FullSpanItem> mFullSpanItems;
        boolean mLastLayoutRTL;
        boolean mReverseLayout;
        int[] mSpanLookup;
        int mSpanLookupSize;
        int[] mSpanOffsets;
        int mSpanOffsetsSize;
        int mVisibleAnchorPosition;

        public int describeContents() {
            return 0;
        }

        public SavedState() {
        }

        SavedState(Parcel parcel) {
            this.mAnchorPosition = parcel.readInt();
            this.mVisibleAnchorPosition = parcel.readInt();
            int readInt = parcel.readInt();
            this.mSpanOffsetsSize = readInt;
            if (readInt > 0) {
                int[] iArr = new int[readInt];
                this.mSpanOffsets = iArr;
                parcel.readIntArray(iArr);
            }
            int readInt2 = parcel.readInt();
            this.mSpanLookupSize = readInt2;
            if (readInt2 > 0) {
                int[] iArr2 = new int[readInt2];
                this.mSpanLookup = iArr2;
                parcel.readIntArray(iArr2);
            }
            boolean z = false;
            this.mReverseLayout = parcel.readInt() == 1;
            this.mAnchorLayoutFromEnd = parcel.readInt() == 1;
            if (parcel.readInt() == 1) {
                z = true;
            }
            this.mLastLayoutRTL = z;
            this.mFullSpanItems = parcel.readArrayList(FullSpanItem.class.getClassLoader());
        }

        public SavedState(SavedState savedState) {
            this.mSpanOffsetsSize = savedState.mSpanOffsetsSize;
            this.mAnchorPosition = savedState.mAnchorPosition;
            this.mVisibleAnchorPosition = savedState.mVisibleAnchorPosition;
            this.mSpanOffsets = savedState.mSpanOffsets;
            this.mSpanLookupSize = savedState.mSpanLookupSize;
            this.mSpanLookup = savedState.mSpanLookup;
            this.mReverseLayout = savedState.mReverseLayout;
            this.mAnchorLayoutFromEnd = savedState.mAnchorLayoutFromEnd;
            this.mLastLayoutRTL = savedState.mLastLayoutRTL;
            this.mFullSpanItems = savedState.mFullSpanItems;
        }

        /* access modifiers changed from: 0000 */
        public void invalidateSpanInfo() {
            this.mSpanOffsets = null;
            this.mSpanOffsetsSize = 0;
            this.mSpanLookupSize = 0;
            this.mSpanLookup = null;
            this.mFullSpanItems = null;
        }

        /* access modifiers changed from: 0000 */
        public void invalidateAnchorPositionInfo() {
            this.mSpanOffsets = null;
            this.mSpanOffsetsSize = 0;
            this.mAnchorPosition = -1;
            this.mVisibleAnchorPosition = -1;
        }

        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.mAnchorPosition);
            parcel.writeInt(this.mVisibleAnchorPosition);
            parcel.writeInt(this.mSpanOffsetsSize);
            if (this.mSpanOffsetsSize > 0) {
                parcel.writeIntArray(this.mSpanOffsets);
            }
            parcel.writeInt(this.mSpanLookupSize);
            if (this.mSpanLookupSize > 0) {
                parcel.writeIntArray(this.mSpanLookup);
            }
            parcel.writeInt(this.mReverseLayout ? 1 : 0);
            parcel.writeInt(this.mAnchorLayoutFromEnd ? 1 : 0);
            parcel.writeInt(this.mLastLayoutRTL ? 1 : 0);
            parcel.writeList(this.mFullSpanItems);
        }
    }

    class Span {
        int mCachedEnd = Integer.MIN_VALUE;
        int mCachedStart = Integer.MIN_VALUE;
        int mDeletedSize = 0;
        final int mIndex;
        ArrayList<View> mViews = new ArrayList<>();

        Span(int i) {
            this.mIndex = i;
        }

        /* access modifiers changed from: 0000 */
        public int getStartLine(int i) {
            int i2 = this.mCachedStart;
            if (i2 != Integer.MIN_VALUE) {
                return i2;
            }
            if (this.mViews.size() == 0) {
                return i;
            }
            calculateCachedStart();
            return this.mCachedStart;
        }

        /* access modifiers changed from: 0000 */
        public void calculateCachedStart() {
            View view = (View) this.mViews.get(0);
            LayoutParams layoutParams = getLayoutParams(view);
            this.mCachedStart = StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedStart(view);
            if (layoutParams.mFullSpan) {
                FullSpanItem fullSpanItem = StaggeredGridLayoutManager.this.mLazySpanLookup.getFullSpanItem(layoutParams.getViewLayoutPosition());
                if (fullSpanItem != null && fullSpanItem.mGapDir == -1) {
                    this.mCachedStart -= fullSpanItem.getGapForSpan(this.mIndex);
                }
            }
        }

        /* access modifiers changed from: 0000 */
        public int getStartLine() {
            int i = this.mCachedStart;
            if (i != Integer.MIN_VALUE) {
                return i;
            }
            calculateCachedStart();
            return this.mCachedStart;
        }

        /* access modifiers changed from: 0000 */
        public int getEndLine(int i) {
            int i2 = this.mCachedEnd;
            if (i2 != Integer.MIN_VALUE) {
                return i2;
            }
            if (this.mViews.size() == 0) {
                return i;
            }
            calculateCachedEnd();
            return this.mCachedEnd;
        }

        /* access modifiers changed from: 0000 */
        public void calculateCachedEnd() {
            ArrayList<View> arrayList = this.mViews;
            View view = (View) arrayList.get(arrayList.size() - 1);
            LayoutParams layoutParams = getLayoutParams(view);
            this.mCachedEnd = StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedEnd(view);
            if (layoutParams.mFullSpan) {
                FullSpanItem fullSpanItem = StaggeredGridLayoutManager.this.mLazySpanLookup.getFullSpanItem(layoutParams.getViewLayoutPosition());
                if (fullSpanItem != null && fullSpanItem.mGapDir == 1) {
                    this.mCachedEnd += fullSpanItem.getGapForSpan(this.mIndex);
                }
            }
        }

        /* access modifiers changed from: 0000 */
        public int getEndLine() {
            int i = this.mCachedEnd;
            if (i != Integer.MIN_VALUE) {
                return i;
            }
            calculateCachedEnd();
            return this.mCachedEnd;
        }

        /* access modifiers changed from: 0000 */
        public void prependToSpan(View view) {
            LayoutParams layoutParams = getLayoutParams(view);
            layoutParams.mSpan = this;
            this.mViews.add(0, view);
            this.mCachedStart = Integer.MIN_VALUE;
            if (this.mViews.size() == 1) {
                this.mCachedEnd = Integer.MIN_VALUE;
            }
            if (layoutParams.isItemRemoved() || layoutParams.isItemChanged()) {
                this.mDeletedSize += StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedMeasurement(view);
            }
        }

        /* access modifiers changed from: 0000 */
        public void appendToSpan(View view) {
            LayoutParams layoutParams = getLayoutParams(view);
            layoutParams.mSpan = this;
            this.mViews.add(view);
            this.mCachedEnd = Integer.MIN_VALUE;
            if (this.mViews.size() == 1) {
                this.mCachedStart = Integer.MIN_VALUE;
            }
            if (layoutParams.isItemRemoved() || layoutParams.isItemChanged()) {
                this.mDeletedSize += StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedMeasurement(view);
            }
        }

        /* access modifiers changed from: 0000 */
        public void cacheReferenceLineAndClear(boolean z, int i) {
            int i2;
            if (z) {
                i2 = getEndLine(Integer.MIN_VALUE);
            } else {
                i2 = getStartLine(Integer.MIN_VALUE);
            }
            clear();
            if (i2 != Integer.MIN_VALUE) {
                if ((!z || i2 >= StaggeredGridLayoutManager.this.mPrimaryOrientation.getEndAfterPadding()) && (z || i2 <= StaggeredGridLayoutManager.this.mPrimaryOrientation.getStartAfterPadding())) {
                    if (i != Integer.MIN_VALUE) {
                        i2 += i;
                    }
                    this.mCachedEnd = i2;
                    this.mCachedStart = i2;
                }
            }
        }

        /* access modifiers changed from: 0000 */
        public void clear() {
            this.mViews.clear();
            invalidateCache();
            this.mDeletedSize = 0;
        }

        /* access modifiers changed from: 0000 */
        public void invalidateCache() {
            this.mCachedStart = Integer.MIN_VALUE;
            this.mCachedEnd = Integer.MIN_VALUE;
        }

        /* access modifiers changed from: 0000 */
        public void setLine(int i) {
            this.mCachedStart = i;
            this.mCachedEnd = i;
        }

        /* access modifiers changed from: 0000 */
        public void popEnd() {
            int size = this.mViews.size();
            View view = (View) this.mViews.remove(size - 1);
            LayoutParams layoutParams = getLayoutParams(view);
            layoutParams.mSpan = null;
            if (layoutParams.isItemRemoved() || layoutParams.isItemChanged()) {
                this.mDeletedSize -= StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedMeasurement(view);
            }
            if (size == 1) {
                this.mCachedStart = Integer.MIN_VALUE;
            }
            this.mCachedEnd = Integer.MIN_VALUE;
        }

        /* access modifiers changed from: 0000 */
        public void popStart() {
            View view = (View) this.mViews.remove(0);
            LayoutParams layoutParams = getLayoutParams(view);
            layoutParams.mSpan = null;
            if (this.mViews.size() == 0) {
                this.mCachedEnd = Integer.MIN_VALUE;
            }
            if (layoutParams.isItemRemoved() || layoutParams.isItemChanged()) {
                this.mDeletedSize -= StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedMeasurement(view);
            }
            this.mCachedStart = Integer.MIN_VALUE;
        }

        public int getDeletedSize() {
            return this.mDeletedSize;
        }

        /* access modifiers changed from: 0000 */
        public LayoutParams getLayoutParams(View view) {
            return (LayoutParams) view.getLayoutParams();
        }

        /* access modifiers changed from: 0000 */
        public void onOffset(int i) {
            int i2 = this.mCachedStart;
            if (i2 != Integer.MIN_VALUE) {
                this.mCachedStart = i2 + i;
            }
            int i3 = this.mCachedEnd;
            if (i3 != Integer.MIN_VALUE) {
                this.mCachedEnd = i3 + i;
            }
        }

        public int findFirstPartiallyVisibleItemPosition() {
            if (StaggeredGridLayoutManager.this.mReverseLayout) {
                return findOnePartiallyVisibleChild(this.mViews.size() - 1, -1, true);
            }
            return findOnePartiallyVisibleChild(0, this.mViews.size(), true);
        }

        public int findLastPartiallyVisibleItemPosition() {
            if (StaggeredGridLayoutManager.this.mReverseLayout) {
                return findOnePartiallyVisibleChild(0, this.mViews.size(), true);
            }
            return findOnePartiallyVisibleChild(this.mViews.size() - 1, -1, true);
        }

        /* access modifiers changed from: 0000 */
        public int findOnePartiallyOrCompletelyVisibleChild(int i, int i2, boolean z, boolean z2, boolean z3) {
            int startAfterPadding = StaggeredGridLayoutManager.this.mPrimaryOrientation.getStartAfterPadding();
            int endAfterPadding = StaggeredGridLayoutManager.this.mPrimaryOrientation.getEndAfterPadding();
            int i3 = i2 > i ? 1 : -1;
            while (i != i2) {
                View view = (View) this.mViews.get(i);
                int decoratedStart = StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedStart(view);
                int decoratedEnd = StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedEnd(view);
                boolean z4 = false;
                boolean z5 = !z3 ? decoratedStart < endAfterPadding : decoratedStart <= endAfterPadding;
                if (!z3 ? decoratedEnd > startAfterPadding : decoratedEnd >= startAfterPadding) {
                    z4 = true;
                }
                if (z5 && z4) {
                    if (!z || !z2) {
                        if (z2) {
                            return StaggeredGridLayoutManager.this.getPosition(view);
                        }
                        if (decoratedStart < startAfterPadding || decoratedEnd > endAfterPadding) {
                            return StaggeredGridLayoutManager.this.getPosition(view);
                        }
                    } else if (decoratedStart >= startAfterPadding && decoratedEnd <= endAfterPadding) {
                        return StaggeredGridLayoutManager.this.getPosition(view);
                    }
                }
                i += i3;
            }
            return -1;
        }

        /* access modifiers changed from: 0000 */
        public int findOnePartiallyVisibleChild(int i, int i2, boolean z) {
            return findOnePartiallyOrCompletelyVisibleChild(i, i2, false, false, z);
        }

        public View getFocusableViewAfter(int i, int i2) {
            View view = null;
            if (i2 != -1) {
                int size = this.mViews.size() - 1;
                while (size >= 0) {
                    View view2 = (View) this.mViews.get(size);
                    StaggeredGridLayoutManager staggeredGridLayoutManager = StaggeredGridLayoutManager.this;
                    if (staggeredGridLayoutManager.mReverseLayout && staggeredGridLayoutManager.getPosition(view2) >= i) {
                        break;
                    }
                    StaggeredGridLayoutManager staggeredGridLayoutManager2 = StaggeredGridLayoutManager.this;
                    if ((!staggeredGridLayoutManager2.mReverseLayout && staggeredGridLayoutManager2.getPosition(view2) <= i) || !view2.hasFocusable()) {
                        break;
                    }
                    size--;
                    view = view2;
                }
            } else {
                int size2 = this.mViews.size();
                int i3 = 0;
                while (i3 < size2) {
                    View view3 = (View) this.mViews.get(i3);
                    StaggeredGridLayoutManager staggeredGridLayoutManager3 = StaggeredGridLayoutManager.this;
                    if (staggeredGridLayoutManager3.mReverseLayout && staggeredGridLayoutManager3.getPosition(view3) <= i) {
                        break;
                    }
                    StaggeredGridLayoutManager staggeredGridLayoutManager4 = StaggeredGridLayoutManager.this;
                    if ((!staggeredGridLayoutManager4.mReverseLayout && staggeredGridLayoutManager4.getPosition(view3) >= i) || !view3.hasFocusable()) {
                        break;
                    }
                    i3++;
                    view = view3;
                }
            }
            return view;
        }
    }

    public StaggeredGridLayoutManager(Context context, AttributeSet attributeSet, int i, int i2) {
        Properties properties = LayoutManager.getProperties(context, attributeSet, i, i2);
        setOrientation(properties.orientation);
        setSpanCount(properties.spanCount);
        setReverseLayout(properties.reverseLayout);
        this.mLayoutState = new LayoutState();
        createOrientationHelpers();
    }

    public boolean isAutoMeasureEnabled() {
        return this.mGapStrategy != 0;
    }

    private void createOrientationHelpers() {
        this.mPrimaryOrientation = OrientationHelper.createOrientationHelper(this, this.mOrientation);
        this.mSecondaryOrientation = OrientationHelper.createOrientationHelper(this, 1 - this.mOrientation);
    }

    /* access modifiers changed from: 0000 */
    public boolean checkForGaps() {
        int i;
        int i2;
        if (getChildCount() == 0 || this.mGapStrategy == 0 || !isAttachedToWindow()) {
            return false;
        }
        if (this.mShouldReverseLayout) {
            i2 = getLastChildPosition();
            i = getFirstChildPosition();
        } else {
            i2 = getFirstChildPosition();
            i = getLastChildPosition();
        }
        if (i2 == 0 && hasGapsToFix() != null) {
            this.mLazySpanLookup.clear();
            requestSimpleAnimationsInNextLayout();
            requestLayout();
            return true;
        } else if (!this.mLaidOutInvalidFullSpan) {
            return false;
        } else {
            int i3 = this.mShouldReverseLayout ? -1 : 1;
            int i4 = i + 1;
            FullSpanItem firstFullSpanItemInRange = this.mLazySpanLookup.getFirstFullSpanItemInRange(i2, i4, i3, true);
            if (firstFullSpanItemInRange == null) {
                this.mLaidOutInvalidFullSpan = false;
                this.mLazySpanLookup.forceInvalidateAfter(i4);
                return false;
            }
            FullSpanItem firstFullSpanItemInRange2 = this.mLazySpanLookup.getFirstFullSpanItemInRange(i2, firstFullSpanItemInRange.mPosition, i3 * -1, true);
            if (firstFullSpanItemInRange2 == null) {
                this.mLazySpanLookup.forceInvalidateAfter(firstFullSpanItemInRange.mPosition);
            } else {
                this.mLazySpanLookup.forceInvalidateAfter(firstFullSpanItemInRange2.mPosition + 1);
            }
            requestSimpleAnimationsInNextLayout();
            requestLayout();
            return true;
        }
    }

    public void onScrollStateChanged(int i) {
        if (i == 0) {
            checkForGaps();
        }
    }

    public void onDetachedFromWindow(RecyclerView recyclerView, Recycler recycler) {
        super.onDetachedFromWindow(recyclerView, recycler);
        removeCallbacks(this.mCheckForGapsRunnable);
        for (int i = 0; i < this.mSpanCount; i++) {
            this.mSpans[i].clear();
        }
        recyclerView.requestLayout();
    }

    /* access modifiers changed from: 0000 */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0074, code lost:
        if (r10 == r11) goto L_0x0088;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0086, code lost:
        if (r10 == r11) goto L_0x0088;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x008a, code lost:
        r10 = false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View hasGapsToFix() {
        /*
            r12 = this;
            int r0 = r12.getChildCount()
            r1 = 1
            int r0 = r0 - r1
            java.util.BitSet r2 = new java.util.BitSet
            int r3 = r12.mSpanCount
            r2.<init>(r3)
            int r3 = r12.mSpanCount
            r4 = 0
            r2.set(r4, r3, r1)
            int r3 = r12.mOrientation
            r5 = -1
            if (r3 != r1) goto L_0x0020
            boolean r3 = r12.isLayoutRTL()
            if (r3 == 0) goto L_0x0020
            r3 = r1
            goto L_0x0021
        L_0x0020:
            r3 = r5
        L_0x0021:
            boolean r6 = r12.mShouldReverseLayout
            if (r6 == 0) goto L_0x0027
            r6 = r5
            goto L_0x002b
        L_0x0027:
            int r0 = r0 + 1
            r6 = r0
            r0 = r4
        L_0x002b:
            if (r0 >= r6) goto L_0x002e
            r5 = r1
        L_0x002e:
            if (r0 == r6) goto L_0x00ab
            android.view.View r7 = r12.getChildAt(r0)
            android.view.ViewGroup$LayoutParams r8 = r7.getLayoutParams()
            androidx.recyclerview.widget.StaggeredGridLayoutManager$LayoutParams r8 = (androidx.recyclerview.widget.StaggeredGridLayoutManager.LayoutParams) r8
            androidx.recyclerview.widget.StaggeredGridLayoutManager$Span r9 = r8.mSpan
            int r9 = r9.mIndex
            boolean r9 = r2.get(r9)
            if (r9 == 0) goto L_0x0054
            androidx.recyclerview.widget.StaggeredGridLayoutManager$Span r9 = r8.mSpan
            boolean r9 = r12.checkSpanForGap(r9)
            if (r9 == 0) goto L_0x004d
            return r7
        L_0x004d:
            androidx.recyclerview.widget.StaggeredGridLayoutManager$Span r9 = r8.mSpan
            int r9 = r9.mIndex
            r2.clear(r9)
        L_0x0054:
            boolean r9 = r8.mFullSpan
            if (r9 == 0) goto L_0x0059
            goto L_0x00a9
        L_0x0059:
            int r9 = r0 + r5
            if (r9 == r6) goto L_0x00a9
            android.view.View r9 = r12.getChildAt(r9)
            boolean r10 = r12.mShouldReverseLayout
            if (r10 == 0) goto L_0x0077
            androidx.recyclerview.widget.OrientationHelper r10 = r12.mPrimaryOrientation
            int r10 = r10.getDecoratedEnd(r7)
            androidx.recyclerview.widget.OrientationHelper r11 = r12.mPrimaryOrientation
            int r11 = r11.getDecoratedEnd(r9)
            if (r10 >= r11) goto L_0x0074
            return r7
        L_0x0074:
            if (r10 != r11) goto L_0x008a
            goto L_0x0088
        L_0x0077:
            androidx.recyclerview.widget.OrientationHelper r10 = r12.mPrimaryOrientation
            int r10 = r10.getDecoratedStart(r7)
            androidx.recyclerview.widget.OrientationHelper r11 = r12.mPrimaryOrientation
            int r11 = r11.getDecoratedStart(r9)
            if (r10 <= r11) goto L_0x0086
            return r7
        L_0x0086:
            if (r10 != r11) goto L_0x008a
        L_0x0088:
            r10 = r1
            goto L_0x008b
        L_0x008a:
            r10 = r4
        L_0x008b:
            if (r10 == 0) goto L_0x00a9
            android.view.ViewGroup$LayoutParams r9 = r9.getLayoutParams()
            androidx.recyclerview.widget.StaggeredGridLayoutManager$LayoutParams r9 = (androidx.recyclerview.widget.StaggeredGridLayoutManager.LayoutParams) r9
            androidx.recyclerview.widget.StaggeredGridLayoutManager$Span r8 = r8.mSpan
            int r8 = r8.mIndex
            androidx.recyclerview.widget.StaggeredGridLayoutManager$Span r9 = r9.mSpan
            int r9 = r9.mIndex
            int r8 = r8 - r9
            if (r8 >= 0) goto L_0x00a0
            r8 = r1
            goto L_0x00a1
        L_0x00a0:
            r8 = r4
        L_0x00a1:
            if (r3 >= 0) goto L_0x00a5
            r9 = r1
            goto L_0x00a6
        L_0x00a5:
            r9 = r4
        L_0x00a6:
            if (r8 == r9) goto L_0x00a9
            return r7
        L_0x00a9:
            int r0 = r0 + r5
            goto L_0x002e
        L_0x00ab:
            r12 = 0
            return r12
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.recyclerview.widget.StaggeredGridLayoutManager.hasGapsToFix():android.view.View");
    }

    private boolean checkSpanForGap(Span span) {
        if (this.mShouldReverseLayout) {
            if (span.getEndLine() < this.mPrimaryOrientation.getEndAfterPadding()) {
                ArrayList<View> arrayList = span.mViews;
                return !span.getLayoutParams((View) arrayList.get(arrayList.size() - 1)).mFullSpan;
            }
        } else if (span.getStartLine() > this.mPrimaryOrientation.getStartAfterPadding()) {
            return !span.getLayoutParams((View) span.mViews.get(0)).mFullSpan;
        }
        return false;
    }

    public void setSpanCount(int i) {
        assertNotInLayoutOrScroll(null);
        if (i != this.mSpanCount) {
            invalidateSpanAssignments();
            this.mSpanCount = i;
            this.mRemainingSpans = new BitSet(this.mSpanCount);
            this.mSpans = new Span[this.mSpanCount];
            for (int i2 = 0; i2 < this.mSpanCount; i2++) {
                this.mSpans[i2] = new Span(i2);
            }
            requestLayout();
        }
    }

    public void setOrientation(int i) {
        if (i == 0 || i == 1) {
            assertNotInLayoutOrScroll(null);
            if (i != this.mOrientation) {
                this.mOrientation = i;
                OrientationHelper orientationHelper = this.mPrimaryOrientation;
                this.mPrimaryOrientation = this.mSecondaryOrientation;
                this.mSecondaryOrientation = orientationHelper;
                requestLayout();
                return;
            }
            return;
        }
        throw new IllegalArgumentException("invalid orientation.");
    }

    public void setReverseLayout(boolean z) {
        assertNotInLayoutOrScroll(null);
        SavedState savedState = this.mPendingSavedState;
        if (!(savedState == null || savedState.mReverseLayout == z)) {
            savedState.mReverseLayout = z;
        }
        this.mReverseLayout = z;
        requestLayout();
    }

    public void assertNotInLayoutOrScroll(String str) {
        if (this.mPendingSavedState == null) {
            super.assertNotInLayoutOrScroll(str);
        }
    }

    public void invalidateSpanAssignments() {
        this.mLazySpanLookup.clear();
        requestLayout();
    }

    private void resolveShouldLayoutReverse() {
        if (this.mOrientation == 1 || !isLayoutRTL()) {
            this.mShouldReverseLayout = this.mReverseLayout;
        } else {
            this.mShouldReverseLayout = !this.mReverseLayout;
        }
    }

    /* access modifiers changed from: 0000 */
    public boolean isLayoutRTL() {
        return getLayoutDirection() == 1;
    }

    public void setMeasuredDimension(Rect rect, int i, int i2) {
        int i3;
        int i4;
        int paddingLeft = getPaddingLeft() + getPaddingRight();
        int paddingTop = getPaddingTop() + getPaddingBottom();
        if (this.mOrientation == 1) {
            i4 = LayoutManager.chooseSize(i2, rect.height() + paddingTop, getMinimumHeight());
            i3 = LayoutManager.chooseSize(i, (this.mSizePerSpan * this.mSpanCount) + paddingLeft, getMinimumWidth());
        } else {
            i3 = LayoutManager.chooseSize(i, rect.width() + paddingLeft, getMinimumWidth());
            i4 = LayoutManager.chooseSize(i2, (this.mSizePerSpan * this.mSpanCount) + paddingTop, getMinimumHeight());
        }
        setMeasuredDimension(i3, i4);
    }

    public void onLayoutChildren(Recycler recycler, State state) {
        onLayoutChildren(recycler, state, true);
    }

    public void onAdapterChanged(Adapter adapter, Adapter adapter2) {
        this.mLazySpanLookup.clear();
        for (int i = 0; i < this.mSpanCount; i++) {
            this.mSpans[i].clear();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:83:0x0157, code lost:
        if (checkForGaps() != false) goto L_0x015b;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void onLayoutChildren(androidx.recyclerview.widget.RecyclerView.Recycler r9, androidx.recyclerview.widget.RecyclerView.State r10, boolean r11) {
        /*
            r8 = this;
            androidx.recyclerview.widget.StaggeredGridLayoutManager$AnchorInfo r0 = r8.mAnchorInfo
            androidx.recyclerview.widget.StaggeredGridLayoutManager$SavedState r1 = r8.mPendingSavedState
            r2 = -1
            if (r1 != 0) goto L_0x000b
            int r1 = r8.mPendingScrollPosition
            if (r1 == r2) goto L_0x0018
        L_0x000b:
            int r1 = r10.getItemCount()
            if (r1 != 0) goto L_0x0018
            r8.removeAndRecycleAllViews(r9)
            r0.reset()
            return
        L_0x0018:
            boolean r1 = r0.mValid
            r3 = 0
            r4 = 1
            if (r1 == 0) goto L_0x0029
            int r1 = r8.mPendingScrollPosition
            if (r1 != r2) goto L_0x0029
            androidx.recyclerview.widget.StaggeredGridLayoutManager$SavedState r1 = r8.mPendingSavedState
            if (r1 == 0) goto L_0x0027
            goto L_0x0029
        L_0x0027:
            r1 = r3
            goto L_0x002a
        L_0x0029:
            r1 = r4
        L_0x002a:
            if (r1 == 0) goto L_0x0043
            r0.reset()
            androidx.recyclerview.widget.StaggeredGridLayoutManager$SavedState r5 = r8.mPendingSavedState
            if (r5 == 0) goto L_0x0037
            r8.applyPendingSavedState(r0)
            goto L_0x003e
        L_0x0037:
            r8.resolveShouldLayoutReverse()
            boolean r5 = r8.mShouldReverseLayout
            r0.mLayoutFromEnd = r5
        L_0x003e:
            r8.updateAnchorInfoForLayout(r10, r0)
            r0.mValid = r4
        L_0x0043:
            androidx.recyclerview.widget.StaggeredGridLayoutManager$SavedState r5 = r8.mPendingSavedState
            if (r5 != 0) goto L_0x0060
            int r5 = r8.mPendingScrollPosition
            if (r5 != r2) goto L_0x0060
            boolean r5 = r0.mLayoutFromEnd
            boolean r6 = r8.mLastLayoutFromEnd
            if (r5 != r6) goto L_0x0059
            boolean r5 = r8.isLayoutRTL()
            boolean r6 = r8.mLastLayoutRTL
            if (r5 == r6) goto L_0x0060
        L_0x0059:
            androidx.recyclerview.widget.StaggeredGridLayoutManager$LazySpanLookup r5 = r8.mLazySpanLookup
            r5.clear()
            r0.mInvalidateOffsets = r4
        L_0x0060:
            int r5 = r8.getChildCount()
            if (r5 <= 0) goto L_0x00c9
            androidx.recyclerview.widget.StaggeredGridLayoutManager$SavedState r5 = r8.mPendingSavedState
            if (r5 == 0) goto L_0x006e
            int r5 = r5.mSpanOffsetsSize
            if (r5 >= r4) goto L_0x00c9
        L_0x006e:
            boolean r5 = r0.mInvalidateOffsets
            if (r5 == 0) goto L_0x008e
            r1 = r3
        L_0x0073:
            int r5 = r8.mSpanCount
            if (r1 >= r5) goto L_0x00c9
            androidx.recyclerview.widget.StaggeredGridLayoutManager$Span[] r5 = r8.mSpans
            r5 = r5[r1]
            r5.clear()
            int r5 = r0.mOffset
            r6 = -2147483648(0xffffffff80000000, float:-0.0)
            if (r5 == r6) goto L_0x008b
            androidx.recyclerview.widget.StaggeredGridLayoutManager$Span[] r6 = r8.mSpans
            r6 = r6[r1]
            r6.setLine(r5)
        L_0x008b:
            int r1 = r1 + 1
            goto L_0x0073
        L_0x008e:
            if (r1 != 0) goto L_0x00af
            androidx.recyclerview.widget.StaggeredGridLayoutManager$AnchorInfo r1 = r8.mAnchorInfo
            int[] r1 = r1.mSpanReferenceLines
            if (r1 != 0) goto L_0x0097
            goto L_0x00af
        L_0x0097:
            r1 = r3
        L_0x0098:
            int r5 = r8.mSpanCount
            if (r1 >= r5) goto L_0x00c9
            androidx.recyclerview.widget.StaggeredGridLayoutManager$Span[] r5 = r8.mSpans
            r5 = r5[r1]
            r5.clear()
            androidx.recyclerview.widget.StaggeredGridLayoutManager$AnchorInfo r6 = r8.mAnchorInfo
            int[] r6 = r6.mSpanReferenceLines
            r6 = r6[r1]
            r5.setLine(r6)
            int r1 = r1 + 1
            goto L_0x0098
        L_0x00af:
            r1 = r3
        L_0x00b0:
            int r5 = r8.mSpanCount
            if (r1 >= r5) goto L_0x00c2
            androidx.recyclerview.widget.StaggeredGridLayoutManager$Span[] r5 = r8.mSpans
            r5 = r5[r1]
            boolean r6 = r8.mShouldReverseLayout
            int r7 = r0.mOffset
            r5.cacheReferenceLineAndClear(r6, r7)
            int r1 = r1 + 1
            goto L_0x00b0
        L_0x00c2:
            androidx.recyclerview.widget.StaggeredGridLayoutManager$AnchorInfo r1 = r8.mAnchorInfo
            androidx.recyclerview.widget.StaggeredGridLayoutManager$Span[] r5 = r8.mSpans
            r1.saveSpanReferenceLines(r5)
        L_0x00c9:
            r8.detachAndScrapAttachedViews(r9)
            androidx.recyclerview.widget.LayoutState r1 = r8.mLayoutState
            r1.mRecycle = r3
            r8.mLaidOutInvalidFullSpan = r3
            androidx.recyclerview.widget.OrientationHelper r1 = r8.mSecondaryOrientation
            int r1 = r1.getTotalSpace()
            r8.updateMeasureSpecs(r1)
            int r1 = r0.mPosition
            r8.updateLayoutState(r1, r10)
            boolean r1 = r0.mLayoutFromEnd
            if (r1 == 0) goto L_0x00fc
            r8.setLayoutStateDirection(r2)
            androidx.recyclerview.widget.LayoutState r1 = r8.mLayoutState
            r8.fill(r9, r1, r10)
            r8.setLayoutStateDirection(r4)
            androidx.recyclerview.widget.LayoutState r1 = r8.mLayoutState
            int r2 = r0.mPosition
            int r5 = r1.mItemDirection
            int r2 = r2 + r5
            r1.mCurrentPosition = r2
            r8.fill(r9, r1, r10)
            goto L_0x0113
        L_0x00fc:
            r8.setLayoutStateDirection(r4)
            androidx.recyclerview.widget.LayoutState r1 = r8.mLayoutState
            r8.fill(r9, r1, r10)
            r8.setLayoutStateDirection(r2)
            androidx.recyclerview.widget.LayoutState r1 = r8.mLayoutState
            int r2 = r0.mPosition
            int r5 = r1.mItemDirection
            int r2 = r2 + r5
            r1.mCurrentPosition = r2
            r8.fill(r9, r1, r10)
        L_0x0113:
            r8.repositionToWrapContentIfNecessary()
            int r1 = r8.getChildCount()
            if (r1 <= 0) goto L_0x012d
            boolean r1 = r8.mShouldReverseLayout
            if (r1 == 0) goto L_0x0127
            r8.fixEndGap(r9, r10, r4)
            r8.fixStartGap(r9, r10, r3)
            goto L_0x012d
        L_0x0127:
            r8.fixStartGap(r9, r10, r4)
            r8.fixEndGap(r9, r10, r3)
        L_0x012d:
            if (r11 == 0) goto L_0x015a
            boolean r11 = r10.isPreLayout()
            if (r11 != 0) goto L_0x015a
            int r11 = r8.mGapStrategy
            if (r11 == 0) goto L_0x014b
            int r11 = r8.getChildCount()
            if (r11 <= 0) goto L_0x014b
            boolean r11 = r8.mLaidOutInvalidFullSpan
            if (r11 != 0) goto L_0x0149
            android.view.View r11 = r8.hasGapsToFix()
            if (r11 == 0) goto L_0x014b
        L_0x0149:
            r11 = r4
            goto L_0x014c
        L_0x014b:
            r11 = r3
        L_0x014c:
            if (r11 == 0) goto L_0x015a
            java.lang.Runnable r11 = r8.mCheckForGapsRunnable
            r8.removeCallbacks(r11)
            boolean r11 = r8.checkForGaps()
            if (r11 == 0) goto L_0x015a
            goto L_0x015b
        L_0x015a:
            r4 = r3
        L_0x015b:
            boolean r11 = r10.isPreLayout()
            if (r11 == 0) goto L_0x0166
            androidx.recyclerview.widget.StaggeredGridLayoutManager$AnchorInfo r11 = r8.mAnchorInfo
            r11.reset()
        L_0x0166:
            boolean r11 = r0.mLayoutFromEnd
            r8.mLastLayoutFromEnd = r11
            boolean r11 = r8.isLayoutRTL()
            r8.mLastLayoutRTL = r11
            if (r4 == 0) goto L_0x017a
            androidx.recyclerview.widget.StaggeredGridLayoutManager$AnchorInfo r11 = r8.mAnchorInfo
            r11.reset()
            r8.onLayoutChildren(r9, r10, r3)
        L_0x017a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.recyclerview.widget.StaggeredGridLayoutManager.onLayoutChildren(androidx.recyclerview.widget.RecyclerView$Recycler, androidx.recyclerview.widget.RecyclerView$State, boolean):void");
    }

    public void onLayoutCompleted(State state) {
        super.onLayoutCompleted(state);
        this.mPendingScrollPosition = -1;
        this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
        this.mPendingSavedState = null;
        this.mAnchorInfo.reset();
    }

    private void repositionToWrapContentIfNecessary() {
        if (this.mSecondaryOrientation.getMode() != 1073741824) {
            float f = 0.0f;
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = getChildAt(i);
                float decoratedMeasurement = (float) this.mSecondaryOrientation.getDecoratedMeasurement(childAt);
                if (decoratedMeasurement >= f) {
                    if (((LayoutParams) childAt.getLayoutParams()).isFullSpan()) {
                        decoratedMeasurement = (decoratedMeasurement * 1.0f) / ((float) this.mSpanCount);
                    }
                    f = Math.max(f, decoratedMeasurement);
                }
            }
            int i2 = this.mSizePerSpan;
            int round = Math.round(f * ((float) this.mSpanCount));
            if (this.mSecondaryOrientation.getMode() == Integer.MIN_VALUE) {
                round = Math.min(round, this.mSecondaryOrientation.getTotalSpace());
            }
            updateMeasureSpecs(round);
            if (this.mSizePerSpan != i2) {
                for (int i3 = 0; i3 < childCount; i3++) {
                    View childAt2 = getChildAt(i3);
                    LayoutParams layoutParams = (LayoutParams) childAt2.getLayoutParams();
                    if (!layoutParams.mFullSpan) {
                        if (!isLayoutRTL() || this.mOrientation != 1) {
                            int i4 = layoutParams.mSpan.mIndex;
                            int i5 = this.mSizePerSpan * i4;
                            int i6 = i4 * i2;
                            if (this.mOrientation == 1) {
                                childAt2.offsetLeftAndRight(i5 - i6);
                            } else {
                                childAt2.offsetTopAndBottom(i5 - i6);
                            }
                        } else {
                            int i7 = this.mSpanCount;
                            int i8 = i7 - 1;
                            int i9 = layoutParams.mSpan.mIndex;
                            childAt2.offsetLeftAndRight(((-(i8 - i9)) * this.mSizePerSpan) - ((-((i7 - 1) - i9)) * i2));
                        }
                    }
                }
            }
        }
    }

    private void applyPendingSavedState(AnchorInfo anchorInfo) {
        int i;
        SavedState savedState = this.mPendingSavedState;
        int i2 = savedState.mSpanOffsetsSize;
        if (i2 > 0) {
            if (i2 == this.mSpanCount) {
                for (int i3 = 0; i3 < this.mSpanCount; i3++) {
                    this.mSpans[i3].clear();
                    SavedState savedState2 = this.mPendingSavedState;
                    int i4 = savedState2.mSpanOffsets[i3];
                    if (i4 != Integer.MIN_VALUE) {
                        if (savedState2.mAnchorLayoutFromEnd) {
                            i = this.mPrimaryOrientation.getEndAfterPadding();
                        } else {
                            i = this.mPrimaryOrientation.getStartAfterPadding();
                        }
                        i4 += i;
                    }
                    this.mSpans[i3].setLine(i4);
                }
            } else {
                savedState.invalidateSpanInfo();
                SavedState savedState3 = this.mPendingSavedState;
                savedState3.mAnchorPosition = savedState3.mVisibleAnchorPosition;
            }
        }
        SavedState savedState4 = this.mPendingSavedState;
        this.mLastLayoutRTL = savedState4.mLastLayoutRTL;
        setReverseLayout(savedState4.mReverseLayout);
        resolveShouldLayoutReverse();
        SavedState savedState5 = this.mPendingSavedState;
        int i5 = savedState5.mAnchorPosition;
        if (i5 != -1) {
            this.mPendingScrollPosition = i5;
            anchorInfo.mLayoutFromEnd = savedState5.mAnchorLayoutFromEnd;
        } else {
            anchorInfo.mLayoutFromEnd = this.mShouldReverseLayout;
        }
        SavedState savedState6 = this.mPendingSavedState;
        if (savedState6.mSpanLookupSize > 1) {
            LazySpanLookup lazySpanLookup = this.mLazySpanLookup;
            lazySpanLookup.mData = savedState6.mSpanLookup;
            lazySpanLookup.mFullSpanItems = savedState6.mFullSpanItems;
        }
    }

    /* access modifiers changed from: 0000 */
    public void updateAnchorInfoForLayout(State state, AnchorInfo anchorInfo) {
        if (!updateAnchorFromPendingData(state, anchorInfo) && !updateAnchorFromChildren(state, anchorInfo)) {
            anchorInfo.assignCoordinateFromPadding();
            anchorInfo.mPosition = 0;
        }
    }

    private boolean updateAnchorFromChildren(State state, AnchorInfo anchorInfo) {
        int i;
        if (this.mLastLayoutFromEnd) {
            i = findLastReferenceChildPosition(state.getItemCount());
        } else {
            i = findFirstReferenceChildPosition(state.getItemCount());
        }
        anchorInfo.mPosition = i;
        anchorInfo.mOffset = Integer.MIN_VALUE;
        return true;
    }

    /* access modifiers changed from: 0000 */
    public boolean updateAnchorFromPendingData(State state, AnchorInfo anchorInfo) {
        int i;
        int i2;
        boolean z = false;
        if (!state.isPreLayout()) {
            int i3 = this.mPendingScrollPosition;
            if (i3 != -1) {
                if (i3 < 0 || i3 >= state.getItemCount()) {
                    this.mPendingScrollPosition = -1;
                    this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
                } else {
                    SavedState savedState = this.mPendingSavedState;
                    if (savedState == null || savedState.mAnchorPosition == -1 || savedState.mSpanOffsetsSize < 1) {
                        View findViewByPosition = findViewByPosition(this.mPendingScrollPosition);
                        if (findViewByPosition != null) {
                            if (this.mShouldReverseLayout) {
                                i = getLastChildPosition();
                            } else {
                                i = getFirstChildPosition();
                            }
                            anchorInfo.mPosition = i;
                            if (this.mPendingScrollPositionOffset != Integer.MIN_VALUE) {
                                if (anchorInfo.mLayoutFromEnd) {
                                    anchorInfo.mOffset = (this.mPrimaryOrientation.getEndAfterPadding() - this.mPendingScrollPositionOffset) - this.mPrimaryOrientation.getDecoratedEnd(findViewByPosition);
                                } else {
                                    anchorInfo.mOffset = (this.mPrimaryOrientation.getStartAfterPadding() + this.mPendingScrollPositionOffset) - this.mPrimaryOrientation.getDecoratedStart(findViewByPosition);
                                }
                                return true;
                            } else if (this.mPrimaryOrientation.getDecoratedMeasurement(findViewByPosition) > this.mPrimaryOrientation.getTotalSpace()) {
                                if (anchorInfo.mLayoutFromEnd) {
                                    i2 = this.mPrimaryOrientation.getEndAfterPadding();
                                } else {
                                    i2 = this.mPrimaryOrientation.getStartAfterPadding();
                                }
                                anchorInfo.mOffset = i2;
                                return true;
                            } else {
                                int decoratedStart = this.mPrimaryOrientation.getDecoratedStart(findViewByPosition) - this.mPrimaryOrientation.getStartAfterPadding();
                                if (decoratedStart < 0) {
                                    anchorInfo.mOffset = -decoratedStart;
                                    return true;
                                }
                                int endAfterPadding = this.mPrimaryOrientation.getEndAfterPadding() - this.mPrimaryOrientation.getDecoratedEnd(findViewByPosition);
                                if (endAfterPadding < 0) {
                                    anchorInfo.mOffset = endAfterPadding;
                                    return true;
                                }
                                anchorInfo.mOffset = Integer.MIN_VALUE;
                            }
                        } else {
                            int i4 = this.mPendingScrollPosition;
                            anchorInfo.mPosition = i4;
                            int i5 = this.mPendingScrollPositionOffset;
                            if (i5 == Integer.MIN_VALUE) {
                                if (calculateScrollDirectionForPosition(i4) == 1) {
                                    z = true;
                                }
                                anchorInfo.mLayoutFromEnd = z;
                                anchorInfo.assignCoordinateFromPadding();
                            } else {
                                anchorInfo.assignCoordinateFromPadding(i5);
                            }
                            anchorInfo.mInvalidateOffsets = true;
                        }
                    } else {
                        anchorInfo.mOffset = Integer.MIN_VALUE;
                        anchorInfo.mPosition = this.mPendingScrollPosition;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /* access modifiers changed from: 0000 */
    public void updateMeasureSpecs(int i) {
        this.mSizePerSpan = i / this.mSpanCount;
        this.mFullSizeSpec = MeasureSpec.makeMeasureSpec(i, this.mSecondaryOrientation.getMode());
    }

    public boolean supportsPredictiveItemAnimations() {
        return this.mPendingSavedState == null;
    }

    public int computeHorizontalScrollOffset(State state) {
        return computeScrollOffset(state);
    }

    private int computeScrollOffset(State state) {
        if (getChildCount() == 0) {
            return 0;
        }
        return ScrollbarHelper.computeScrollOffset(state, this.mPrimaryOrientation, findFirstVisibleItemClosestToStart(!this.mSmoothScrollbarEnabled), findFirstVisibleItemClosestToEnd(!this.mSmoothScrollbarEnabled), this, this.mSmoothScrollbarEnabled, this.mShouldReverseLayout);
    }

    public int computeVerticalScrollOffset(State state) {
        return computeScrollOffset(state);
    }

    public int computeHorizontalScrollExtent(State state) {
        return computeScrollExtent(state);
    }

    private int computeScrollExtent(State state) {
        if (getChildCount() == 0) {
            return 0;
        }
        return ScrollbarHelper.computeScrollExtent(state, this.mPrimaryOrientation, findFirstVisibleItemClosestToStart(!this.mSmoothScrollbarEnabled), findFirstVisibleItemClosestToEnd(!this.mSmoothScrollbarEnabled), this, this.mSmoothScrollbarEnabled);
    }

    public int computeVerticalScrollExtent(State state) {
        return computeScrollExtent(state);
    }

    public int computeHorizontalScrollRange(State state) {
        return computeScrollRange(state);
    }

    private int computeScrollRange(State state) {
        if (getChildCount() == 0) {
            return 0;
        }
        return ScrollbarHelper.computeScrollRange(state, this.mPrimaryOrientation, findFirstVisibleItemClosestToStart(!this.mSmoothScrollbarEnabled), findFirstVisibleItemClosestToEnd(!this.mSmoothScrollbarEnabled), this, this.mSmoothScrollbarEnabled);
    }

    public int computeVerticalScrollRange(State state) {
        return computeScrollRange(state);
    }

    private void measureChildWithDecorationsAndMargin(View view, LayoutParams layoutParams, boolean z) {
        if (layoutParams.mFullSpan) {
            if (this.mOrientation == 1) {
                measureChildWithDecorationsAndMargin(view, this.mFullSizeSpec, LayoutManager.getChildMeasureSpec(getHeight(), getHeightMode(), getPaddingTop() + getPaddingBottom(), layoutParams.height, true), z);
            } else {
                measureChildWithDecorationsAndMargin(view, LayoutManager.getChildMeasureSpec(getWidth(), getWidthMode(), getPaddingLeft() + getPaddingRight(), layoutParams.width, true), this.mFullSizeSpec, z);
            }
        } else if (this.mOrientation == 1) {
            measureChildWithDecorationsAndMargin(view, LayoutManager.getChildMeasureSpec(this.mSizePerSpan, getWidthMode(), 0, layoutParams.width, false), LayoutManager.getChildMeasureSpec(getHeight(), getHeightMode(), getPaddingTop() + getPaddingBottom(), layoutParams.height, true), z);
        } else {
            measureChildWithDecorationsAndMargin(view, LayoutManager.getChildMeasureSpec(getWidth(), getWidthMode(), getPaddingLeft() + getPaddingRight(), layoutParams.width, true), LayoutManager.getChildMeasureSpec(this.mSizePerSpan, getHeightMode(), 0, layoutParams.height, false), z);
        }
    }

    private void measureChildWithDecorationsAndMargin(View view, int i, int i2, boolean z) {
        boolean z2;
        calculateItemDecorationsForChild(view, this.mTmpRect);
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        int i3 = layoutParams.leftMargin;
        Rect rect = this.mTmpRect;
        int updateSpecWithExtra = updateSpecWithExtra(i, i3 + rect.left, layoutParams.rightMargin + rect.right);
        int i4 = layoutParams.topMargin;
        Rect rect2 = this.mTmpRect;
        int updateSpecWithExtra2 = updateSpecWithExtra(i2, i4 + rect2.top, layoutParams.bottomMargin + rect2.bottom);
        if (z) {
            z2 = shouldReMeasureChild(view, updateSpecWithExtra, updateSpecWithExtra2, layoutParams);
        } else {
            z2 = shouldMeasureChild(view, updateSpecWithExtra, updateSpecWithExtra2, layoutParams);
        }
        if (z2) {
            view.measure(updateSpecWithExtra, updateSpecWithExtra2);
        }
    }

    private int updateSpecWithExtra(int i, int i2, int i3) {
        if (i2 == 0 && i3 == 0) {
            return i;
        }
        int mode = MeasureSpec.getMode(i);
        if (mode == Integer.MIN_VALUE || mode == 1073741824) {
            return MeasureSpec.makeMeasureSpec(Math.max(0, (MeasureSpec.getSize(i) - i2) - i3), mode);
        }
        return i;
    }

    public void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable instanceof SavedState) {
            SavedState savedState = (SavedState) parcelable;
            this.mPendingSavedState = savedState;
            if (this.mPendingScrollPosition != -1) {
                savedState.invalidateAnchorPositionInfo();
                this.mPendingSavedState.invalidateSpanInfo();
            }
            requestLayout();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:12:0x0038  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0088  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.os.Parcelable onSaveInstanceState() {
        /*
            r4 = this;
            androidx.recyclerview.widget.StaggeredGridLayoutManager$SavedState r0 = r4.mPendingSavedState
            if (r0 == 0) goto L_0x000c
            androidx.recyclerview.widget.StaggeredGridLayoutManager$SavedState r0 = new androidx.recyclerview.widget.StaggeredGridLayoutManager$SavedState
            androidx.recyclerview.widget.StaggeredGridLayoutManager$SavedState r4 = r4.mPendingSavedState
            r0.<init>(r4)
            return r0
        L_0x000c:
            androidx.recyclerview.widget.StaggeredGridLayoutManager$SavedState r0 = new androidx.recyclerview.widget.StaggeredGridLayoutManager$SavedState
            r0.<init>()
            boolean r1 = r4.mReverseLayout
            r0.mReverseLayout = r1
            boolean r1 = r4.mLastLayoutFromEnd
            r0.mAnchorLayoutFromEnd = r1
            boolean r1 = r4.mLastLayoutRTL
            r0.mLastLayoutRTL = r1
            androidx.recyclerview.widget.StaggeredGridLayoutManager$LazySpanLookup r1 = r4.mLazySpanLookup
            r2 = 0
            if (r1 == 0) goto L_0x0030
            int[] r3 = r1.mData
            if (r3 == 0) goto L_0x0030
            r0.mSpanLookup = r3
            int r3 = r3.length
            r0.mSpanLookupSize = r3
            java.util.List<androidx.recyclerview.widget.StaggeredGridLayoutManager$LazySpanLookup$FullSpanItem> r1 = r1.mFullSpanItems
            r0.mFullSpanItems = r1
            goto L_0x0032
        L_0x0030:
            r0.mSpanLookupSize = r2
        L_0x0032:
            int r1 = r4.getChildCount()
            if (r1 <= 0) goto L_0x0088
            boolean r1 = r4.mLastLayoutFromEnd
            if (r1 == 0) goto L_0x0041
            int r1 = r4.getLastChildPosition()
            goto L_0x0045
        L_0x0041:
            int r1 = r4.getFirstChildPosition()
        L_0x0045:
            r0.mAnchorPosition = r1
            int r1 = r4.findFirstVisibleItemPositionInt()
            r0.mVisibleAnchorPosition = r1
            int r1 = r4.mSpanCount
            r0.mSpanOffsetsSize = r1
            int[] r1 = new int[r1]
            r0.mSpanOffsets = r1
        L_0x0055:
            int r1 = r4.mSpanCount
            if (r2 >= r1) goto L_0x008f
            boolean r1 = r4.mLastLayoutFromEnd
            r3 = -2147483648(0xffffffff80000000, float:-0.0)
            if (r1 == 0) goto L_0x0070
            androidx.recyclerview.widget.StaggeredGridLayoutManager$Span[] r1 = r4.mSpans
            r1 = r1[r2]
            int r1 = r1.getEndLine(r3)
            if (r1 == r3) goto L_0x0081
            androidx.recyclerview.widget.OrientationHelper r3 = r4.mPrimaryOrientation
            int r3 = r3.getEndAfterPadding()
            goto L_0x0080
        L_0x0070:
            androidx.recyclerview.widget.StaggeredGridLayoutManager$Span[] r1 = r4.mSpans
            r1 = r1[r2]
            int r1 = r1.getStartLine(r3)
            if (r1 == r3) goto L_0x0081
            androidx.recyclerview.widget.OrientationHelper r3 = r4.mPrimaryOrientation
            int r3 = r3.getStartAfterPadding()
        L_0x0080:
            int r1 = r1 - r3
        L_0x0081:
            int[] r3 = r0.mSpanOffsets
            r3[r2] = r1
            int r2 = r2 + 1
            goto L_0x0055
        L_0x0088:
            r4 = -1
            r0.mAnchorPosition = r4
            r0.mVisibleAnchorPosition = r4
            r0.mSpanOffsetsSize = r2
        L_0x008f:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.recyclerview.widget.StaggeredGridLayoutManager.onSaveInstanceState():android.os.Parcelable");
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEvent(accessibilityEvent);
        if (getChildCount() > 0) {
            View findFirstVisibleItemClosestToStart = findFirstVisibleItemClosestToStart(false);
            View findFirstVisibleItemClosestToEnd = findFirstVisibleItemClosestToEnd(false);
            if (findFirstVisibleItemClosestToStart != null && findFirstVisibleItemClosestToEnd != null) {
                int position = getPosition(findFirstVisibleItemClosestToStart);
                int position2 = getPosition(findFirstVisibleItemClosestToEnd);
                if (position < position2) {
                    accessibilityEvent.setFromIndex(position);
                    accessibilityEvent.setToIndex(position2);
                    return;
                }
                accessibilityEvent.setFromIndex(position2);
                accessibilityEvent.setToIndex(position);
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public int findFirstVisibleItemPositionInt() {
        View view;
        if (this.mShouldReverseLayout) {
            view = findFirstVisibleItemClosestToEnd(true);
        } else {
            view = findFirstVisibleItemClosestToStart(true);
        }
        if (view == null) {
            return -1;
        }
        return getPosition(view);
    }

    /* access modifiers changed from: 0000 */
    public View findFirstVisibleItemClosestToStart(boolean z) {
        int startAfterPadding = this.mPrimaryOrientation.getStartAfterPadding();
        int endAfterPadding = this.mPrimaryOrientation.getEndAfterPadding();
        int childCount = getChildCount();
        View view = null;
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            int decoratedStart = this.mPrimaryOrientation.getDecoratedStart(childAt);
            if (this.mPrimaryOrientation.getDecoratedEnd(childAt) > startAfterPadding && decoratedStart < endAfterPadding) {
                if (decoratedStart >= startAfterPadding || !z) {
                    return childAt;
                }
                if (view == null) {
                    view = childAt;
                }
            }
        }
        return view;
    }

    /* access modifiers changed from: 0000 */
    public View findFirstVisibleItemClosestToEnd(boolean z) {
        int startAfterPadding = this.mPrimaryOrientation.getStartAfterPadding();
        int endAfterPadding = this.mPrimaryOrientation.getEndAfterPadding();
        View view = null;
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            View childAt = getChildAt(childCount);
            int decoratedStart = this.mPrimaryOrientation.getDecoratedStart(childAt);
            int decoratedEnd = this.mPrimaryOrientation.getDecoratedEnd(childAt);
            if (decoratedEnd > startAfterPadding && decoratedStart < endAfterPadding) {
                if (decoratedEnd <= endAfterPadding || !z) {
                    return childAt;
                }
                if (view == null) {
                    view = childAt;
                }
            }
        }
        return view;
    }

    private void fixEndGap(Recycler recycler, State state, boolean z) {
        int maxEnd = getMaxEnd(Integer.MIN_VALUE);
        if (maxEnd != Integer.MIN_VALUE) {
            int endAfterPadding = this.mPrimaryOrientation.getEndAfterPadding() - maxEnd;
            if (endAfterPadding > 0) {
                int i = endAfterPadding - (-scrollBy(-endAfterPadding, recycler, state));
                if (z && i > 0) {
                    this.mPrimaryOrientation.offsetChildren(i);
                }
            }
        }
    }

    private void fixStartGap(Recycler recycler, State state, boolean z) {
        int minStart = getMinStart(Integer.MAX_VALUE);
        if (minStart != Integer.MAX_VALUE) {
            int startAfterPadding = minStart - this.mPrimaryOrientation.getStartAfterPadding();
            if (startAfterPadding > 0) {
                int scrollBy = startAfterPadding - scrollBy(startAfterPadding, recycler, state);
                if (z && scrollBy > 0) {
                    this.mPrimaryOrientation.offsetChildren(-scrollBy);
                }
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:14:0x0037  */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x004e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateLayoutState(int r5, androidx.recyclerview.widget.RecyclerView.State r6) {
        /*
            r4 = this;
            androidx.recyclerview.widget.LayoutState r0 = r4.mLayoutState
            r1 = 0
            r0.mAvailable = r1
            r0.mCurrentPosition = r5
            boolean r0 = r4.isSmoothScrolling()
            r2 = 1
            if (r0 == 0) goto L_0x002f
            int r6 = r6.getTargetScrollPosition()
            r0 = -1
            if (r6 == r0) goto L_0x002f
            boolean r0 = r4.mShouldReverseLayout
            if (r6 >= r5) goto L_0x001b
            r5 = r2
            goto L_0x001c
        L_0x001b:
            r5 = r1
        L_0x001c:
            if (r0 != r5) goto L_0x0026
            androidx.recyclerview.widget.OrientationHelper r5 = r4.mPrimaryOrientation
            int r5 = r5.getTotalSpace()
            r6 = r1
            goto L_0x0031
        L_0x0026:
            androidx.recyclerview.widget.OrientationHelper r5 = r4.mPrimaryOrientation
            int r5 = r5.getTotalSpace()
            r6 = r5
            r5 = r1
            goto L_0x0031
        L_0x002f:
            r5 = r1
            r6 = r5
        L_0x0031:
            boolean r0 = r4.getClipToPadding()
            if (r0 == 0) goto L_0x004e
            androidx.recyclerview.widget.LayoutState r0 = r4.mLayoutState
            androidx.recyclerview.widget.OrientationHelper r3 = r4.mPrimaryOrientation
            int r3 = r3.getStartAfterPadding()
            int r3 = r3 - r6
            r0.mStartLine = r3
            androidx.recyclerview.widget.LayoutState r6 = r4.mLayoutState
            androidx.recyclerview.widget.OrientationHelper r0 = r4.mPrimaryOrientation
            int r0 = r0.getEndAfterPadding()
            int r0 = r0 + r5
            r6.mEndLine = r0
            goto L_0x005e
        L_0x004e:
            androidx.recyclerview.widget.LayoutState r0 = r4.mLayoutState
            androidx.recyclerview.widget.OrientationHelper r3 = r4.mPrimaryOrientation
            int r3 = r3.getEnd()
            int r3 = r3 + r5
            r0.mEndLine = r3
            androidx.recyclerview.widget.LayoutState r5 = r4.mLayoutState
            int r6 = -r6
            r5.mStartLine = r6
        L_0x005e:
            androidx.recyclerview.widget.LayoutState r5 = r4.mLayoutState
            r5.mStopInFocusable = r1
            r5.mRecycle = r2
            androidx.recyclerview.widget.OrientationHelper r6 = r4.mPrimaryOrientation
            int r6 = r6.getMode()
            if (r6 != 0) goto L_0x0075
            androidx.recyclerview.widget.OrientationHelper r4 = r4.mPrimaryOrientation
            int r4 = r4.getEnd()
            if (r4 != 0) goto L_0x0075
            r1 = r2
        L_0x0075:
            r5.mInfinite = r1
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.recyclerview.widget.StaggeredGridLayoutManager.updateLayoutState(int, androidx.recyclerview.widget.RecyclerView$State):void");
    }

    private void setLayoutStateDirection(int i) {
        LayoutState layoutState = this.mLayoutState;
        layoutState.mLayoutDirection = i;
        int i2 = 1;
        if (this.mShouldReverseLayout != (i == -1)) {
            i2 = -1;
        }
        layoutState.mItemDirection = i2;
    }

    public void offsetChildrenHorizontal(int i) {
        super.offsetChildrenHorizontal(i);
        for (int i2 = 0; i2 < this.mSpanCount; i2++) {
            this.mSpans[i2].onOffset(i);
        }
    }

    public void offsetChildrenVertical(int i) {
        super.offsetChildrenVertical(i);
        for (int i2 = 0; i2 < this.mSpanCount; i2++) {
            this.mSpans[i2].onOffset(i);
        }
    }

    public void onItemsRemoved(RecyclerView recyclerView, int i, int i2) {
        handleUpdate(i, i2, 2);
    }

    public void onItemsAdded(RecyclerView recyclerView, int i, int i2) {
        handleUpdate(i, i2, 1);
    }

    public void onItemsChanged(RecyclerView recyclerView) {
        this.mLazySpanLookup.clear();
        requestLayout();
    }

    public void onItemsMoved(RecyclerView recyclerView, int i, int i2, int i3) {
        handleUpdate(i, i2, 8);
    }

    public void onItemsUpdated(RecyclerView recyclerView, int i, int i2, Object obj) {
        handleUpdate(i, i2, 4);
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x0025  */
    /* JADX WARNING: Removed duplicated region for block: B:18:0x003c  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0043 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0044  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void handleUpdate(int r7, int r8, int r9) {
        /*
            r6 = this;
            boolean r0 = r6.mShouldReverseLayout
            if (r0 == 0) goto L_0x0009
            int r0 = r6.getLastChildPosition()
            goto L_0x000d
        L_0x0009:
            int r0 = r6.getFirstChildPosition()
        L_0x000d:
            r1 = 8
            if (r9 != r1) goto L_0x001a
            if (r7 >= r8) goto L_0x0016
            int r2 = r8 + 1
            goto L_0x001c
        L_0x0016:
            int r2 = r7 + 1
            r3 = r8
            goto L_0x001d
        L_0x001a:
            int r2 = r7 + r8
        L_0x001c:
            r3 = r7
        L_0x001d:
            androidx.recyclerview.widget.StaggeredGridLayoutManager$LazySpanLookup r4 = r6.mLazySpanLookup
            r4.invalidateAfter(r3)
            r4 = 1
            if (r9 == r4) goto L_0x003c
            r5 = 2
            if (r9 == r5) goto L_0x0036
            if (r9 == r1) goto L_0x002b
            goto L_0x0041
        L_0x002b:
            androidx.recyclerview.widget.StaggeredGridLayoutManager$LazySpanLookup r9 = r6.mLazySpanLookup
            r9.offsetForRemoval(r7, r4)
            androidx.recyclerview.widget.StaggeredGridLayoutManager$LazySpanLookup r7 = r6.mLazySpanLookup
            r7.offsetForAddition(r8, r4)
            goto L_0x0041
        L_0x0036:
            androidx.recyclerview.widget.StaggeredGridLayoutManager$LazySpanLookup r9 = r6.mLazySpanLookup
            r9.offsetForRemoval(r7, r8)
            goto L_0x0041
        L_0x003c:
            androidx.recyclerview.widget.StaggeredGridLayoutManager$LazySpanLookup r9 = r6.mLazySpanLookup
            r9.offsetForAddition(r7, r8)
        L_0x0041:
            if (r2 > r0) goto L_0x0044
            return
        L_0x0044:
            boolean r7 = r6.mShouldReverseLayout
            if (r7 == 0) goto L_0x004d
            int r7 = r6.getFirstChildPosition()
            goto L_0x0051
        L_0x004d:
            int r7 = r6.getLastChildPosition()
        L_0x0051:
            if (r3 > r7) goto L_0x0056
            r6.requestLayout()
        L_0x0056:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.recyclerview.widget.StaggeredGridLayoutManager.handleUpdate(int, int, int):void");
    }

    /* JADX WARNING: type inference failed for: r9v0 */
    /* JADX WARNING: type inference failed for: r9v1, types: [boolean, int] */
    /* JADX WARNING: type inference failed for: r3v0 */
    /* JADX WARNING: type inference failed for: r9v2, types: [int] */
    /* JADX WARNING: type inference failed for: r3v1 */
    /* JADX WARNING: type inference failed for: r3v3 */
    /* JADX WARNING: type inference failed for: r9v5 */
    /* JADX WARNING: type inference failed for: r3v4 */
    /* JADX WARNING: type inference failed for: r3v5 */
    /* JADX WARNING: type inference failed for: r3v9 */
    /* JADX WARNING: type inference failed for: r3v10 */
    /* JADX WARNING: type inference failed for: r9v10 */
    /* JADX WARNING: type inference failed for: r3v11 */
    /* JADX WARNING: type inference failed for: r3v12 */
    /* JADX WARNING: Multi-variable type inference failed. Error: jadx.core.utils.exceptions.JadxRuntimeException: No candidate types for var: r9v1, types: [boolean, int]
      assigns: []
      uses: [?[int, float, boolean, short, byte, char, OBJECT, ARRAY], boolean, int, ?[int, short, byte, char]]
      mth insns count: 226
    	at jadx.core.dex.visitors.typeinference.TypeSearch.fillTypeCandidates(TypeSearch.java:237)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.run(TypeSearch.java:53)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runMultiVariableSearch(TypeInferenceVisitor.java:99)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:92)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
    	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
    	at jadx.core.ProcessClass.process(ProcessClass.java:30)
    	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:311)
    	at jadx.api.JavaClass.decompile(JavaClass.java:62)
    	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:217)
     */
    /* JADX WARNING: Unknown variable types count: 8 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int fill(androidx.recyclerview.widget.RecyclerView.Recycler r17, androidx.recyclerview.widget.LayoutState r18, androidx.recyclerview.widget.RecyclerView.State r19) {
        /*
            r16 = this;
            r6 = r16
            r7 = r17
            r8 = r18
            java.util.BitSet r0 = r6.mRemainingSpans
            int r1 = r6.mSpanCount
            r9 = 0
            r10 = 1
            r0.set(r9, r1, r10)
            androidx.recyclerview.widget.LayoutState r0 = r6.mLayoutState
            boolean r0 = r0.mInfinite
            if (r0 == 0) goto L_0x0020
            int r0 = r8.mLayoutDirection
            if (r0 != r10) goto L_0x001d
            r0 = 2147483647(0x7fffffff, float:NaN)
            goto L_0x002f
        L_0x001d:
            r0 = -2147483648(0xffffffff80000000, float:-0.0)
            goto L_0x002f
        L_0x0020:
            int r0 = r8.mLayoutDirection
            if (r0 != r10) goto L_0x002a
            int r0 = r8.mEndLine
            int r1 = r8.mAvailable
            int r0 = r0 + r1
            goto L_0x002f
        L_0x002a:
            int r0 = r8.mStartLine
            int r1 = r8.mAvailable
            int r0 = r0 - r1
        L_0x002f:
            r11 = r0
            int r0 = r8.mLayoutDirection
            r6.updateAllRemainingSpans(r0, r11)
            boolean r0 = r6.mShouldReverseLayout
            if (r0 == 0) goto L_0x0040
            androidx.recyclerview.widget.OrientationHelper r0 = r6.mPrimaryOrientation
            int r0 = r0.getEndAfterPadding()
            goto L_0x0046
        L_0x0040:
            androidx.recyclerview.widget.OrientationHelper r0 = r6.mPrimaryOrientation
            int r0 = r0.getStartAfterPadding()
        L_0x0046:
            r12 = r0
            r0 = r9
        L_0x0048:
            boolean r1 = r18.hasMore(r19)
            r2 = -1
            if (r1 == 0) goto L_0x01c7
            androidx.recyclerview.widget.LayoutState r1 = r6.mLayoutState
            boolean r1 = r1.mInfinite
            if (r1 != 0) goto L_0x005d
            java.util.BitSet r1 = r6.mRemainingSpans
            boolean r1 = r1.isEmpty()
            if (r1 != 0) goto L_0x01c7
        L_0x005d:
            android.view.View r13 = r8.next(r7)
            android.view.ViewGroup$LayoutParams r0 = r13.getLayoutParams()
            r14 = r0
            androidx.recyclerview.widget.StaggeredGridLayoutManager$LayoutParams r14 = (androidx.recyclerview.widget.StaggeredGridLayoutManager.LayoutParams) r14
            int r0 = r14.getViewLayoutPosition()
            androidx.recyclerview.widget.StaggeredGridLayoutManager$LazySpanLookup r1 = r6.mLazySpanLookup
            int r1 = r1.getSpan(r0)
            if (r1 != r2) goto L_0x0076
            r3 = r10
            goto L_0x0077
        L_0x0076:
            r3 = r9
        L_0x0077:
            if (r3 == 0) goto L_0x008c
            boolean r1 = r14.mFullSpan
            if (r1 == 0) goto L_0x0082
            androidx.recyclerview.widget.StaggeredGridLayoutManager$Span[] r1 = r6.mSpans
            r1 = r1[r9]
            goto L_0x0086
        L_0x0082:
            androidx.recyclerview.widget.StaggeredGridLayoutManager$Span r1 = r6.getNextSpan(r8)
        L_0x0086:
            androidx.recyclerview.widget.StaggeredGridLayoutManager$LazySpanLookup r4 = r6.mLazySpanLookup
            r4.setSpan(r0, r1)
            goto L_0x0090
        L_0x008c:
            androidx.recyclerview.widget.StaggeredGridLayoutManager$Span[] r4 = r6.mSpans
            r1 = r4[r1]
        L_0x0090:
            r15 = r1
            r14.mSpan = r15
            int r1 = r8.mLayoutDirection
            if (r1 != r10) goto L_0x009b
            r6.addView(r13)
            goto L_0x009e
        L_0x009b:
            r6.addView(r13, r9)
        L_0x009e:
            r6.measureChildWithDecorationsAndMargin(r13, r14, r9)
            int r1 = r8.mLayoutDirection
            if (r1 != r10) goto L_0x00cf
            boolean r1 = r14.mFullSpan
            if (r1 == 0) goto L_0x00ae
            int r1 = r6.getMaxEnd(r12)
            goto L_0x00b2
        L_0x00ae:
            int r1 = r15.getEndLine(r12)
        L_0x00b2:
            androidx.recyclerview.widget.OrientationHelper r4 = r6.mPrimaryOrientation
            int r4 = r4.getDecoratedMeasurement(r13)
            int r4 = r4 + r1
            if (r3 == 0) goto L_0x00cc
            boolean r5 = r14.mFullSpan
            if (r5 == 0) goto L_0x00cc
            androidx.recyclerview.widget.StaggeredGridLayoutManager$LazySpanLookup$FullSpanItem r5 = r6.createFullSpanItemFromEnd(r1)
            r5.mGapDir = r2
            r5.mPosition = r0
            androidx.recyclerview.widget.StaggeredGridLayoutManager$LazySpanLookup r9 = r6.mLazySpanLookup
            r9.addFullSpanItem(r5)
        L_0x00cc:
            r5 = r4
            r4 = r1
            goto L_0x00f8
        L_0x00cf:
            boolean r1 = r14.mFullSpan
            if (r1 == 0) goto L_0x00d8
            int r1 = r6.getMinStart(r12)
            goto L_0x00dc
        L_0x00d8:
            int r1 = r15.getStartLine(r12)
        L_0x00dc:
            androidx.recyclerview.widget.OrientationHelper r4 = r6.mPrimaryOrientation
            int r4 = r4.getDecoratedMeasurement(r13)
            int r4 = r1 - r4
            if (r3 == 0) goto L_0x00f7
            boolean r5 = r14.mFullSpan
            if (r5 == 0) goto L_0x00f7
            androidx.recyclerview.widget.StaggeredGridLayoutManager$LazySpanLookup$FullSpanItem r5 = r6.createFullSpanItemFromStart(r1)
            r5.mGapDir = r10
            r5.mPosition = r0
            androidx.recyclerview.widget.StaggeredGridLayoutManager$LazySpanLookup r9 = r6.mLazySpanLookup
            r9.addFullSpanItem(r5)
        L_0x00f7:
            r5 = r1
        L_0x00f8:
            boolean r1 = r14.mFullSpan
            if (r1 == 0) goto L_0x0121
            int r1 = r8.mItemDirection
            if (r1 != r2) goto L_0x0121
            if (r3 == 0) goto L_0x0105
            r6.mLaidOutInvalidFullSpan = r10
            goto L_0x0121
        L_0x0105:
            int r1 = r8.mLayoutDirection
            if (r1 != r10) goto L_0x010e
            boolean r1 = r16.areAllEndsEqual()
            goto L_0x0112
        L_0x010e:
            boolean r1 = r16.areAllStartsEqual()
        L_0x0112:
            r1 = r1 ^ r10
            if (r1 == 0) goto L_0x0121
            androidx.recyclerview.widget.StaggeredGridLayoutManager$LazySpanLookup r1 = r6.mLazySpanLookup
            androidx.recyclerview.widget.StaggeredGridLayoutManager$LazySpanLookup$FullSpanItem r0 = r1.getFullSpanItem(r0)
            if (r0 == 0) goto L_0x011f
            r0.mHasUnwantedGapAfter = r10
        L_0x011f:
            r6.mLaidOutInvalidFullSpan = r10
        L_0x0121:
            r6.attachViewToSpans(r13, r14, r8)
            boolean r0 = r16.isLayoutRTL()
            if (r0 == 0) goto L_0x0154
            int r0 = r6.mOrientation
            if (r0 != r10) goto L_0x0154
            boolean r0 = r14.mFullSpan
            if (r0 == 0) goto L_0x0139
            androidx.recyclerview.widget.OrientationHelper r0 = r6.mSecondaryOrientation
            int r0 = r0.getEndAfterPadding()
            goto L_0x0149
        L_0x0139:
            androidx.recyclerview.widget.OrientationHelper r0 = r6.mSecondaryOrientation
            int r0 = r0.getEndAfterPadding()
            int r1 = r6.mSpanCount
            int r1 = r1 - r10
            int r2 = r15.mIndex
            int r1 = r1 - r2
            int r2 = r6.mSizePerSpan
            int r1 = r1 * r2
            int r0 = r0 - r1
        L_0x0149:
            androidx.recyclerview.widget.OrientationHelper r1 = r6.mSecondaryOrientation
            int r1 = r1.getDecoratedMeasurement(r13)
            int r1 = r0 - r1
            r9 = r0
            r3 = r1
            goto L_0x0174
        L_0x0154:
            boolean r0 = r14.mFullSpan
            if (r0 == 0) goto L_0x015f
            androidx.recyclerview.widget.OrientationHelper r0 = r6.mSecondaryOrientation
            int r0 = r0.getStartAfterPadding()
            goto L_0x016b
        L_0x015f:
            int r0 = r15.mIndex
            int r1 = r6.mSizePerSpan
            int r0 = r0 * r1
            androidx.recyclerview.widget.OrientationHelper r1 = r6.mSecondaryOrientation
            int r1 = r1.getStartAfterPadding()
            int r0 = r0 + r1
        L_0x016b:
            androidx.recyclerview.widget.OrientationHelper r1 = r6.mSecondaryOrientation
            int r1 = r1.getDecoratedMeasurement(r13)
            int r1 = r1 + r0
            r3 = r0
            r9 = r1
        L_0x0174:
            int r0 = r6.mOrientation
            if (r0 != r10) goto L_0x0182
            r0 = r16
            r1 = r13
            r2 = r3
            r3 = r4
            r4 = r9
            r0.layoutDecoratedWithMargins(r1, r2, r3, r4, r5)
            goto L_0x018b
        L_0x0182:
            r0 = r16
            r1 = r13
            r2 = r4
            r4 = r5
            r5 = r9
            r0.layoutDecoratedWithMargins(r1, r2, r3, r4, r5)
        L_0x018b:
            boolean r0 = r14.mFullSpan
            if (r0 == 0) goto L_0x0197
            androidx.recyclerview.widget.LayoutState r0 = r6.mLayoutState
            int r0 = r0.mLayoutDirection
            r6.updateAllRemainingSpans(r0, r11)
            goto L_0x019e
        L_0x0197:
            androidx.recyclerview.widget.LayoutState r0 = r6.mLayoutState
            int r0 = r0.mLayoutDirection
            r6.updateRemainingSpans(r15, r0, r11)
        L_0x019e:
            androidx.recyclerview.widget.LayoutState r0 = r6.mLayoutState
            r6.recycle(r7, r0)
            androidx.recyclerview.widget.LayoutState r0 = r6.mLayoutState
            boolean r0 = r0.mStopInFocusable
            if (r0 == 0) goto L_0x01c2
            boolean r0 = r13.hasFocusable()
            if (r0 == 0) goto L_0x01c2
            boolean r0 = r14.mFullSpan
            if (r0 == 0) goto L_0x01b9
            java.util.BitSet r0 = r6.mRemainingSpans
            r0.clear()
            goto L_0x01c2
        L_0x01b9:
            java.util.BitSet r0 = r6.mRemainingSpans
            int r1 = r15.mIndex
            r3 = 0
            r0.set(r1, r3)
            goto L_0x01c3
        L_0x01c2:
            r3 = 0
        L_0x01c3:
            r9 = r3
            r0 = r10
            goto L_0x0048
        L_0x01c7:
            r3 = r9
            if (r0 != 0) goto L_0x01cf
            androidx.recyclerview.widget.LayoutState r0 = r6.mLayoutState
            r6.recycle(r7, r0)
        L_0x01cf:
            androidx.recyclerview.widget.LayoutState r0 = r6.mLayoutState
            int r0 = r0.mLayoutDirection
            if (r0 != r2) goto L_0x01e7
            androidx.recyclerview.widget.OrientationHelper r0 = r6.mPrimaryOrientation
            int r0 = r0.getStartAfterPadding()
            int r0 = r6.getMinStart(r0)
            androidx.recyclerview.widget.OrientationHelper r1 = r6.mPrimaryOrientation
            int r1 = r1.getStartAfterPadding()
            int r1 = r1 - r0
            goto L_0x01f9
        L_0x01e7:
            androidx.recyclerview.widget.OrientationHelper r0 = r6.mPrimaryOrientation
            int r0 = r0.getEndAfterPadding()
            int r0 = r6.getMaxEnd(r0)
            androidx.recyclerview.widget.OrientationHelper r1 = r6.mPrimaryOrientation
            int r1 = r1.getEndAfterPadding()
            int r1 = r0 - r1
        L_0x01f9:
            if (r1 <= 0) goto L_0x0202
            int r0 = r8.mAvailable
            int r9 = java.lang.Math.min(r0, r1)
            goto L_0x0203
        L_0x0202:
            r9 = r3
        L_0x0203:
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.recyclerview.widget.StaggeredGridLayoutManager.fill(androidx.recyclerview.widget.RecyclerView$Recycler, androidx.recyclerview.widget.LayoutState, androidx.recyclerview.widget.RecyclerView$State):int");
    }

    private FullSpanItem createFullSpanItemFromEnd(int i) {
        FullSpanItem fullSpanItem = new FullSpanItem();
        fullSpanItem.mGapPerSpan = new int[this.mSpanCount];
        for (int i2 = 0; i2 < this.mSpanCount; i2++) {
            fullSpanItem.mGapPerSpan[i2] = i - this.mSpans[i2].getEndLine(i);
        }
        return fullSpanItem;
    }

    private FullSpanItem createFullSpanItemFromStart(int i) {
        FullSpanItem fullSpanItem = new FullSpanItem();
        fullSpanItem.mGapPerSpan = new int[this.mSpanCount];
        for (int i2 = 0; i2 < this.mSpanCount; i2++) {
            fullSpanItem.mGapPerSpan[i2] = this.mSpans[i2].getStartLine(i) - i;
        }
        return fullSpanItem;
    }

    private void attachViewToSpans(View view, LayoutParams layoutParams, LayoutState layoutState) {
        if (layoutState.mLayoutDirection == 1) {
            if (layoutParams.mFullSpan) {
                appendViewToAllSpans(view);
            } else {
                layoutParams.mSpan.appendToSpan(view);
            }
        } else if (layoutParams.mFullSpan) {
            prependViewToAllSpans(view);
        } else {
            layoutParams.mSpan.prependToSpan(view);
        }
    }

    private void recycle(Recycler recycler, LayoutState layoutState) {
        int i;
        int i2;
        if (layoutState.mRecycle && !layoutState.mInfinite) {
            if (layoutState.mAvailable == 0) {
                if (layoutState.mLayoutDirection == -1) {
                    recycleFromEnd(recycler, layoutState.mEndLine);
                } else {
                    recycleFromStart(recycler, layoutState.mStartLine);
                }
            } else if (layoutState.mLayoutDirection == -1) {
                int i3 = layoutState.mStartLine;
                int maxStart = i3 - getMaxStart(i3);
                if (maxStart < 0) {
                    i2 = layoutState.mEndLine;
                } else {
                    i2 = layoutState.mEndLine - Math.min(maxStart, layoutState.mAvailable);
                }
                recycleFromEnd(recycler, i2);
            } else {
                int minEnd = getMinEnd(layoutState.mEndLine) - layoutState.mEndLine;
                if (minEnd < 0) {
                    i = layoutState.mStartLine;
                } else {
                    i = Math.min(minEnd, layoutState.mAvailable) + layoutState.mStartLine;
                }
                recycleFromStart(recycler, i);
            }
        }
    }

    private void appendViewToAllSpans(View view) {
        for (int i = this.mSpanCount - 1; i >= 0; i--) {
            this.mSpans[i].appendToSpan(view);
        }
    }

    private void prependViewToAllSpans(View view) {
        for (int i = this.mSpanCount - 1; i >= 0; i--) {
            this.mSpans[i].prependToSpan(view);
        }
    }

    private void updateAllRemainingSpans(int i, int i2) {
        for (int i3 = 0; i3 < this.mSpanCount; i3++) {
            if (!this.mSpans[i3].mViews.isEmpty()) {
                updateRemainingSpans(this.mSpans[i3], i, i2);
            }
        }
    }

    private void updateRemainingSpans(Span span, int i, int i2) {
        int deletedSize = span.getDeletedSize();
        if (i == -1) {
            if (span.getStartLine() + deletedSize <= i2) {
                this.mRemainingSpans.set(span.mIndex, false);
            }
        } else if (span.getEndLine() - deletedSize >= i2) {
            this.mRemainingSpans.set(span.mIndex, false);
        }
    }

    private int getMaxStart(int i) {
        int startLine = this.mSpans[0].getStartLine(i);
        for (int i2 = 1; i2 < this.mSpanCount; i2++) {
            int startLine2 = this.mSpans[i2].getStartLine(i);
            if (startLine2 > startLine) {
                startLine = startLine2;
            }
        }
        return startLine;
    }

    private int getMinStart(int i) {
        int startLine = this.mSpans[0].getStartLine(i);
        for (int i2 = 1; i2 < this.mSpanCount; i2++) {
            int startLine2 = this.mSpans[i2].getStartLine(i);
            if (startLine2 < startLine) {
                startLine = startLine2;
            }
        }
        return startLine;
    }

    /* access modifiers changed from: 0000 */
    public boolean areAllEndsEqual() {
        int endLine = this.mSpans[0].getEndLine(Integer.MIN_VALUE);
        for (int i = 1; i < this.mSpanCount; i++) {
            if (this.mSpans[i].getEndLine(Integer.MIN_VALUE) != endLine) {
                return false;
            }
        }
        return true;
    }

    /* access modifiers changed from: 0000 */
    public boolean areAllStartsEqual() {
        int startLine = this.mSpans[0].getStartLine(Integer.MIN_VALUE);
        for (int i = 1; i < this.mSpanCount; i++) {
            if (this.mSpans[i].getStartLine(Integer.MIN_VALUE) != startLine) {
                return false;
            }
        }
        return true;
    }

    private int getMaxEnd(int i) {
        int endLine = this.mSpans[0].getEndLine(i);
        for (int i2 = 1; i2 < this.mSpanCount; i2++) {
            int endLine2 = this.mSpans[i2].getEndLine(i);
            if (endLine2 > endLine) {
                endLine = endLine2;
            }
        }
        return endLine;
    }

    private int getMinEnd(int i) {
        int endLine = this.mSpans[0].getEndLine(i);
        for (int i2 = 1; i2 < this.mSpanCount; i2++) {
            int endLine2 = this.mSpans[i2].getEndLine(i);
            if (endLine2 < endLine) {
                endLine = endLine2;
            }
        }
        return endLine;
    }

    private void recycleFromStart(Recycler recycler, int i) {
        while (getChildCount() > 0) {
            View childAt = getChildAt(0);
            if (this.mPrimaryOrientation.getDecoratedEnd(childAt) > i || this.mPrimaryOrientation.getTransformedEndWithDecoration(childAt) > i) {
                break;
            }
            LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
            if (layoutParams.mFullSpan) {
                int i2 = 0;
                while (i2 < this.mSpanCount) {
                    if (this.mSpans[i2].mViews.size() != 1) {
                        i2++;
                    } else {
                        return;
                    }
                }
                for (int i3 = 0; i3 < this.mSpanCount; i3++) {
                    this.mSpans[i3].popStart();
                }
            } else if (layoutParams.mSpan.mViews.size() != 1) {
                layoutParams.mSpan.popStart();
            } else {
                return;
            }
            removeAndRecycleView(childAt, recycler);
        }
    }

    private void recycleFromEnd(Recycler recycler, int i) {
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            View childAt = getChildAt(childCount);
            if (this.mPrimaryOrientation.getDecoratedStart(childAt) < i || this.mPrimaryOrientation.getTransformedStartWithDecoration(childAt) < i) {
                break;
            }
            LayoutParams layoutParams = (LayoutParams) childAt.getLayoutParams();
            if (layoutParams.mFullSpan) {
                int i2 = 0;
                while (i2 < this.mSpanCount) {
                    if (this.mSpans[i2].mViews.size() != 1) {
                        i2++;
                    } else {
                        return;
                    }
                }
                for (int i3 = 0; i3 < this.mSpanCount; i3++) {
                    this.mSpans[i3].popEnd();
                }
            } else if (layoutParams.mSpan.mViews.size() != 1) {
                layoutParams.mSpan.popEnd();
            } else {
                return;
            }
            removeAndRecycleView(childAt, recycler);
        }
    }

    private boolean preferLastSpan(int i) {
        boolean z = true;
        if (this.mOrientation == 0) {
            if ((i == -1) == this.mShouldReverseLayout) {
                z = false;
            }
            return z;
        }
        if (((i == -1) == this.mShouldReverseLayout) != isLayoutRTL()) {
            z = false;
        }
        return z;
    }

    private Span getNextSpan(LayoutState layoutState) {
        int i;
        int i2;
        int i3 = -1;
        if (preferLastSpan(layoutState.mLayoutDirection)) {
            i2 = this.mSpanCount - 1;
            i = -1;
        } else {
            i2 = 0;
            i3 = this.mSpanCount;
            i = 1;
        }
        Span span = null;
        if (layoutState.mLayoutDirection == 1) {
            int i4 = Integer.MAX_VALUE;
            int startAfterPadding = this.mPrimaryOrientation.getStartAfterPadding();
            while (i2 != i3) {
                Span span2 = this.mSpans[i2];
                int endLine = span2.getEndLine(startAfterPadding);
                if (endLine < i4) {
                    span = span2;
                    i4 = endLine;
                }
                i2 += i;
            }
            return span;
        }
        int i5 = Integer.MIN_VALUE;
        int endAfterPadding = this.mPrimaryOrientation.getEndAfterPadding();
        while (i2 != i3) {
            Span span3 = this.mSpans[i2];
            int startLine = span3.getStartLine(endAfterPadding);
            if (startLine > i5) {
                span = span3;
                i5 = startLine;
            }
            i2 += i;
        }
        return span;
    }

    public boolean canScrollVertically() {
        return this.mOrientation == 1;
    }

    public boolean canScrollHorizontally() {
        return this.mOrientation == 0;
    }

    public int scrollHorizontallyBy(int i, Recycler recycler, State state) {
        return scrollBy(i, recycler, state);
    }

    public int scrollVerticallyBy(int i, Recycler recycler, State state) {
        return scrollBy(i, recycler, state);
    }

    private int calculateScrollDirectionForPosition(int i) {
        int i2 = -1;
        if (getChildCount() == 0) {
            if (this.mShouldReverseLayout) {
                i2 = 1;
            }
            return i2;
        }
        if ((i < getFirstChildPosition()) == this.mShouldReverseLayout) {
            i2 = 1;
        }
        return i2;
    }

    public PointF computeScrollVectorForPosition(int i) {
        int calculateScrollDirectionForPosition = calculateScrollDirectionForPosition(i);
        PointF pointF = new PointF();
        if (calculateScrollDirectionForPosition == 0) {
            return null;
        }
        if (this.mOrientation == 0) {
            pointF.x = (float) calculateScrollDirectionForPosition;
            pointF.y = 0.0f;
        } else {
            pointF.x = 0.0f;
            pointF.y = (float) calculateScrollDirectionForPosition;
        }
        return pointF;
    }

    public void smoothScrollToPosition(RecyclerView recyclerView, State state, int i) {
        LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext());
        linearSmoothScroller.setTargetPosition(i);
        startSmoothScroll(linearSmoothScroller);
    }

    public void scrollToPosition(int i) {
        SavedState savedState = this.mPendingSavedState;
        if (!(savedState == null || savedState.mAnchorPosition == i)) {
            savedState.invalidateAnchorPositionInfo();
        }
        this.mPendingScrollPosition = i;
        this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
        requestLayout();
    }

    public void collectAdjacentPrefetchPositions(int i, int i2, State state, LayoutPrefetchRegistry layoutPrefetchRegistry) {
        int i3;
        int i4;
        if (this.mOrientation != 0) {
            i = i2;
        }
        if (getChildCount() != 0 && i != 0) {
            prepareLayoutStateForDelta(i, state);
            int[] iArr = this.mPrefetchDistances;
            if (iArr == null || iArr.length < this.mSpanCount) {
                this.mPrefetchDistances = new int[this.mSpanCount];
            }
            int i5 = 0;
            for (int i6 = 0; i6 < this.mSpanCount; i6++) {
                LayoutState layoutState = this.mLayoutState;
                if (layoutState.mItemDirection == -1) {
                    i4 = layoutState.mStartLine;
                    i3 = this.mSpans[i6].getStartLine(i4);
                } else {
                    i4 = this.mSpans[i6].getEndLine(layoutState.mEndLine);
                    i3 = this.mLayoutState.mEndLine;
                }
                int i7 = i4 - i3;
                if (i7 >= 0) {
                    this.mPrefetchDistances[i5] = i7;
                    i5++;
                }
            }
            Arrays.sort(this.mPrefetchDistances, 0, i5);
            for (int i8 = 0; i8 < i5 && this.mLayoutState.hasMore(state); i8++) {
                layoutPrefetchRegistry.addPosition(this.mLayoutState.mCurrentPosition, this.mPrefetchDistances[i8]);
                LayoutState layoutState2 = this.mLayoutState;
                layoutState2.mCurrentPosition += layoutState2.mItemDirection;
            }
        }
    }

    /* access modifiers changed from: 0000 */
    public void prepareLayoutStateForDelta(int i, State state) {
        int i2;
        int i3;
        if (i > 0) {
            i3 = getLastChildPosition();
            i2 = 1;
        } else {
            i2 = -1;
            i3 = getFirstChildPosition();
        }
        this.mLayoutState.mRecycle = true;
        updateLayoutState(i3, state);
        setLayoutStateDirection(i2);
        LayoutState layoutState = this.mLayoutState;
        layoutState.mCurrentPosition = i3 + layoutState.mItemDirection;
        layoutState.mAvailable = Math.abs(i);
    }

    /* access modifiers changed from: 0000 */
    public int scrollBy(int i, Recycler recycler, State state) {
        if (getChildCount() == 0 || i == 0) {
            return 0;
        }
        prepareLayoutStateForDelta(i, state);
        int fill = fill(recycler, this.mLayoutState, state);
        if (this.mLayoutState.mAvailable >= fill) {
            i = i < 0 ? -fill : fill;
        }
        this.mPrimaryOrientation.offsetChildren(-i);
        this.mLastLayoutFromEnd = this.mShouldReverseLayout;
        LayoutState layoutState = this.mLayoutState;
        layoutState.mAvailable = 0;
        recycle(recycler, layoutState);
        return i;
    }

    /* access modifiers changed from: 0000 */
    public int getLastChildPosition() {
        int childCount = getChildCount();
        if (childCount == 0) {
            return 0;
        }
        return getPosition(getChildAt(childCount - 1));
    }

    /* access modifiers changed from: 0000 */
    public int getFirstChildPosition() {
        if (getChildCount() == 0) {
            return 0;
        }
        return getPosition(getChildAt(0));
    }

    private int findFirstReferenceChildPosition(int i) {
        int childCount = getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            int position = getPosition(getChildAt(i2));
            if (position >= 0 && position < i) {
                return position;
            }
        }
        return 0;
    }

    private int findLastReferenceChildPosition(int i) {
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            int position = getPosition(getChildAt(childCount));
            if (position >= 0 && position < i) {
                return position;
            }
        }
        return 0;
    }

    public androidx.recyclerview.widget.RecyclerView.LayoutParams generateDefaultLayoutParams() {
        if (this.mOrientation == 0) {
            return new LayoutParams(-2, -1);
        }
        return new LayoutParams(-1, -2);
    }

    public androidx.recyclerview.widget.RecyclerView.LayoutParams generateLayoutParams(Context context, AttributeSet attributeSet) {
        return new LayoutParams(context, attributeSet);
    }

    public androidx.recyclerview.widget.RecyclerView.LayoutParams generateLayoutParams(android.view.ViewGroup.LayoutParams layoutParams) {
        if (layoutParams instanceof MarginLayoutParams) {
            return new LayoutParams((MarginLayoutParams) layoutParams);
        }
        return new LayoutParams(layoutParams);
    }

    public boolean checkLayoutParams(androidx.recyclerview.widget.RecyclerView.LayoutParams layoutParams) {
        return layoutParams instanceof LayoutParams;
    }

    public View onFocusSearchFailed(View view, int i, Recycler recycler, State state) {
        int i2;
        int i3;
        int i4;
        int i5;
        if (getChildCount() == 0) {
            return null;
        }
        View findContainingItemView = findContainingItemView(view);
        if (findContainingItemView == null) {
            return null;
        }
        resolveShouldLayoutReverse();
        int convertFocusDirectionToLayoutDirection = convertFocusDirectionToLayoutDirection(i);
        if (convertFocusDirectionToLayoutDirection == Integer.MIN_VALUE) {
            return null;
        }
        LayoutParams layoutParams = (LayoutParams) findContainingItemView.getLayoutParams();
        boolean z = layoutParams.mFullSpan;
        Span span = layoutParams.mSpan;
        if (convertFocusDirectionToLayoutDirection == 1) {
            i2 = getLastChildPosition();
        } else {
            i2 = getFirstChildPosition();
        }
        updateLayoutState(i2, state);
        setLayoutStateDirection(convertFocusDirectionToLayoutDirection);
        LayoutState layoutState = this.mLayoutState;
        layoutState.mCurrentPosition = layoutState.mItemDirection + i2;
        layoutState.mAvailable = (int) (((float) this.mPrimaryOrientation.getTotalSpace()) * 0.33333334f);
        LayoutState layoutState2 = this.mLayoutState;
        layoutState2.mStopInFocusable = true;
        layoutState2.mRecycle = false;
        fill(recycler, layoutState2, state);
        this.mLastLayoutFromEnd = this.mShouldReverseLayout;
        if (!z) {
            View focusableViewAfter = span.getFocusableViewAfter(i2, convertFocusDirectionToLayoutDirection);
            if (!(focusableViewAfter == null || focusableViewAfter == findContainingItemView)) {
                return focusableViewAfter;
            }
        }
        if (preferLastSpan(convertFocusDirectionToLayoutDirection)) {
            for (int i6 = this.mSpanCount - 1; i6 >= 0; i6--) {
                View focusableViewAfter2 = this.mSpans[i6].getFocusableViewAfter(i2, convertFocusDirectionToLayoutDirection);
                if (focusableViewAfter2 != null && focusableViewAfter2 != findContainingItemView) {
                    return focusableViewAfter2;
                }
            }
        } else {
            for (int i7 = 0; i7 < this.mSpanCount; i7++) {
                View focusableViewAfter3 = this.mSpans[i7].getFocusableViewAfter(i2, convertFocusDirectionToLayoutDirection);
                if (focusableViewAfter3 != null && focusableViewAfter3 != findContainingItemView) {
                    return focusableViewAfter3;
                }
            }
        }
        boolean z2 = (this.mReverseLayout ^ true) == (convertFocusDirectionToLayoutDirection == -1);
        if (!z) {
            if (z2) {
                i5 = span.findFirstPartiallyVisibleItemPosition();
            } else {
                i5 = span.findLastPartiallyVisibleItemPosition();
            }
            View findViewByPosition = findViewByPosition(i5);
            if (!(findViewByPosition == null || findViewByPosition == findContainingItemView)) {
                return findViewByPosition;
            }
        }
        if (preferLastSpan(convertFocusDirectionToLayoutDirection)) {
            for (int i8 = this.mSpanCount - 1; i8 >= 0; i8--) {
                if (i8 != span.mIndex) {
                    if (z2) {
                        i4 = this.mSpans[i8].findFirstPartiallyVisibleItemPosition();
                    } else {
                        i4 = this.mSpans[i8].findLastPartiallyVisibleItemPosition();
                    }
                    View findViewByPosition2 = findViewByPosition(i4);
                    if (!(findViewByPosition2 == null || findViewByPosition2 == findContainingItemView)) {
                        return findViewByPosition2;
                    }
                }
            }
        } else {
            for (int i9 = 0; i9 < this.mSpanCount; i9++) {
                if (z2) {
                    i3 = this.mSpans[i9].findFirstPartiallyVisibleItemPosition();
                } else {
                    i3 = this.mSpans[i9].findLastPartiallyVisibleItemPosition();
                }
                View findViewByPosition3 = findViewByPosition(i3);
                if (findViewByPosition3 != null && findViewByPosition3 != findContainingItemView) {
                    return findViewByPosition3;
                }
            }
        }
        return null;
    }

    private int convertFocusDirectionToLayoutDirection(int i) {
        int i2 = -1;
        int i3 = 1;
        if (i == 1) {
            return (this.mOrientation != 1 && isLayoutRTL()) ? 1 : -1;
        }
        if (i == 2) {
            return (this.mOrientation != 1 && isLayoutRTL()) ? -1 : 1;
        }
        if (i == 17) {
            if (this.mOrientation != 0) {
                i2 = Integer.MIN_VALUE;
            }
            return i2;
        } else if (i == 33) {
            if (this.mOrientation != 1) {
                i2 = Integer.MIN_VALUE;
            }
            return i2;
        } else if (i == 66) {
            if (this.mOrientation != 0) {
                i3 = Integer.MIN_VALUE;
            }
            return i3;
        } else if (i != 130) {
            return Integer.MIN_VALUE;
        } else {
            if (this.mOrientation != 1) {
                i3 = Integer.MIN_VALUE;
            }
            return i3;
        }
    }
}
