package androidx.slice.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import androidx.collection.ArrayMap;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.slice.SliceItem;
import androidx.slice.core.SliceAction;
import androidx.slice.core.SliceQuery;
import androidx.slice.view.R$layout;
import androidx.slice.widget.SliceView.OnSliceActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SliceAdapter extends Adapter<SliceViewHolder> implements SliceActionLoadingListener {
    boolean mAllowTwoLines;
    int mColor;
    final Context mContext;
    private final IdGenerator mIdGen = new IdGenerator();
    int mInsetBottom;
    int mInsetEnd;
    int mInsetStart;
    int mInsetTop;
    long mLastUpdated;
    Set<SliceItem> mLoadingActions = new HashSet();
    SliceView mParent;
    SliceViewPolicy mPolicy;
    boolean mShowLastUpdated;
    List<SliceAction> mSliceActions;
    OnSliceActionListener mSliceObserver;
    SliceStyle mSliceStyle;
    private List<SliceWrapper> mSlices = new ArrayList();
    TemplateView mTemplateView;

    private static class IdGenerator {
        private final ArrayMap<String, Long> mCurrentIds = new ArrayMap<>();
        private long mNextLong = 0;
        private final ArrayMap<String, Integer> mUsedIds = new ArrayMap<>();

        IdGenerator() {
        }

        public long getId(SliceItem sliceItem) {
            String genString = genString(sliceItem);
            if (!this.mCurrentIds.containsKey(genString)) {
                ArrayMap<String, Long> arrayMap = this.mCurrentIds;
                long j = this.mNextLong;
                this.mNextLong = 1 + j;
                arrayMap.put(genString, Long.valueOf(j));
            }
            long longValue = ((Long) this.mCurrentIds.get(genString)).longValue();
            Integer num = (Integer) this.mUsedIds.get(genString);
            int intValue = num != null ? num.intValue() : 0;
            this.mUsedIds.put(genString, Integer.valueOf(intValue + 1));
            return longValue + ((long) (intValue * 10000));
        }

        private String genString(SliceItem sliceItem) {
            if (!"slice".equals(sliceItem.getFormat())) {
                if (!"action".equals(sliceItem.getFormat())) {
                    return sliceItem.toString();
                }
            }
            return String.valueOf(sliceItem.getSlice().getItems().size());
        }

        public void resetUsage() {
            this.mUsedIds.clear();
        }
    }

    public class SliceViewHolder extends ViewHolder implements OnTouchListener, OnClickListener {
        public final SliceChildView mSliceChildView;

        public SliceViewHolder(View view) {
            super(view);
            this.mSliceChildView = view instanceof SliceChildView ? (SliceChildView) view : null;
        }

        /* access modifiers changed from: 0000 */
        public void bind(SliceContent sliceContent, int i) {
            SliceChildView sliceChildView = this.mSliceChildView;
            if (sliceChildView != null && sliceContent != null) {
                sliceChildView.setOnClickListener(this);
                this.mSliceChildView.setOnTouchListener(this);
                this.mSliceChildView.setSliceActionLoadingListener(SliceAdapter.this);
                boolean z = i == 0;
                this.mSliceChildView.setLoadingActions(SliceAdapter.this.mLoadingActions);
                this.mSliceChildView.setPolicy(SliceAdapter.this.mPolicy);
                this.mSliceChildView.setTint(SliceAdapter.this.mColor);
                this.mSliceChildView.setStyle(SliceAdapter.this.mSliceStyle);
                this.mSliceChildView.setShowLastUpdated(z && SliceAdapter.this.mShowLastUpdated);
                this.mSliceChildView.setLastUpdated(z ? SliceAdapter.this.mLastUpdated : -1);
                int i2 = i == 0 ? SliceAdapter.this.mInsetTop : 0;
                int i3 = i == SliceAdapter.this.getItemCount() - 1 ? SliceAdapter.this.mInsetBottom : 0;
                SliceChildView sliceChildView2 = this.mSliceChildView;
                SliceAdapter sliceAdapter = SliceAdapter.this;
                sliceChildView2.setInsets(sliceAdapter.mInsetStart, i2, sliceAdapter.mInsetEnd, i3);
                this.mSliceChildView.setAllowTwoLines(SliceAdapter.this.mAllowTwoLines);
                this.mSliceChildView.setSliceActions(z ? SliceAdapter.this.mSliceActions : null);
                this.mSliceChildView.setSliceItem(sliceContent, z, i, SliceAdapter.this.getItemCount(), SliceAdapter.this.mSliceObserver);
                this.mSliceChildView.setTag(new int[]{ListContent.getRowType(sliceContent, z, SliceAdapter.this.mSliceActions), i});
            }
        }

        public void onClick(View view) {
            SliceView sliceView = SliceAdapter.this.mParent;
            if (sliceView != null) {
                sliceView.setClickInfo((int[]) view.getTag());
                SliceAdapter.this.mParent.performClick();
            }
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            TemplateView templateView = SliceAdapter.this.mTemplateView;
            if (templateView != null) {
                templateView.onForegroundActivated(motionEvent);
            }
            return false;
        }
    }

    protected static class SliceWrapper {
        final long mId;
        final SliceContent mItem;
        final int mType;

        public SliceWrapper(SliceContent sliceContent, IdGenerator idGenerator, int i) {
            this.mItem = sliceContent;
            this.mType = getFormat(sliceContent.getSliceItem());
            this.mId = idGenerator.getId(sliceContent.getSliceItem());
        }

        public static int getFormat(SliceItem sliceItem) {
            if ("message".equals(sliceItem.getSubType())) {
                return SliceQuery.findSubtype(sliceItem, (String) null, "source") != null ? 4 : 5;
            }
            if (sliceItem.hasHint("horizontal")) {
                return 3;
            }
            return !sliceItem.hasHint("list_item") ? 2 : 1;
        }
    }

    public SliceAdapter(Context context) {
        this.mContext = context;
        setHasStableIds(true);
    }

    public void setParents(SliceView sliceView, TemplateView templateView) {
        this.mParent = sliceView;
        this.mTemplateView = templateView;
    }

    public void setInsets(int i, int i2, int i3, int i4) {
        this.mInsetStart = i;
        this.mInsetTop = i2;
        this.mInsetEnd = i3;
        this.mInsetBottom = i4;
    }

    public void setSliceObserver(OnSliceActionListener onSliceActionListener) {
        this.mSliceObserver = onSliceActionListener;
    }

    public void setSliceActions(List<SliceAction> list) {
        this.mSliceActions = list;
        notifyHeaderChanged();
    }

    public void setSliceItems(List<SliceContent> list, int i, int i2) {
        if (list == null) {
            this.mLoadingActions.clear();
            this.mSlices.clear();
        } else {
            this.mIdGen.resetUsage();
            this.mSlices = new ArrayList(list.size());
            for (SliceContent sliceWrapper : list) {
                this.mSlices.add(new SliceWrapper(sliceWrapper, this.mIdGen, i2));
            }
        }
        this.mColor = i;
        notifyDataSetChanged();
    }

    public void setStyle(SliceStyle sliceStyle) {
        this.mSliceStyle = sliceStyle;
        notifyDataSetChanged();
    }

    public void setPolicy(SliceViewPolicy sliceViewPolicy) {
        this.mPolicy = sliceViewPolicy;
    }

    public void setShowLastUpdated(boolean z) {
        if (this.mShowLastUpdated != z) {
            this.mShowLastUpdated = z;
            notifyHeaderChanged();
        }
    }

    public void setLastUpdated(long j) {
        if (this.mLastUpdated != j) {
            this.mLastUpdated = j;
            notifyHeaderChanged();
        }
    }

    public void setLoadingActions(Set<SliceItem> set) {
        if (set == null) {
            this.mLoadingActions.clear();
        } else {
            this.mLoadingActions = set;
        }
        notifyDataSetChanged();
    }

    public void onSliceActionLoading(SliceItem sliceItem, int i) {
        this.mLoadingActions.add(sliceItem);
        if (getItemCount() > i) {
            notifyItemChanged(i);
        } else {
            notifyDataSetChanged();
        }
    }

    public void setAllowTwoLines(boolean z) {
        this.mAllowTwoLines = z;
        notifyHeaderChanged();
    }

    public void notifyHeaderChanged() {
        if (getItemCount() > 0) {
            notifyItemChanged(0);
        }
    }

    public SliceViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View inflateForType = inflateForType(i);
        inflateForType.setLayoutParams(new LayoutParams(-1, -2));
        return new SliceViewHolder(inflateForType);
    }

    public int getItemViewType(int i) {
        return ((SliceWrapper) this.mSlices.get(i)).mType;
    }

    public long getItemId(int i) {
        return ((SliceWrapper) this.mSlices.get(i)).mId;
    }

    public int getItemCount() {
        return this.mSlices.size();
    }

    public void onBindViewHolder(SliceViewHolder sliceViewHolder, int i) {
        sliceViewHolder.bind(((SliceWrapper) this.mSlices.get(i)).mItem, i);
    }

    private View inflateForType(int i) {
        if (i == 3) {
            return LayoutInflater.from(this.mContext).inflate(R$layout.abc_slice_grid, null);
        }
        if (i == 4) {
            return LayoutInflater.from(this.mContext).inflate(R$layout.abc_slice_message, null);
        }
        if (i != 5) {
            return new RowView(this.mContext);
        }
        return LayoutInflater.from(this.mContext).inflate(R$layout.abc_slice_message_local, null);
    }
}
