package androidx.slice.widget;

import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Handler;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.slice.SliceItem;
import androidx.slice.core.SliceAction;
import androidx.slice.core.SliceActionImpl;
import androidx.slice.core.SliceQuery;
import androidx.slice.view.R$dimen;
import androidx.slice.view.R$id;
import androidx.slice.view.R$layout;
import androidx.slice.view.R$plurals;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RowView extends SliceChildView implements OnClickListener, OnItemSelectedListener {
    private static final boolean sCanSpecifyLargerRangeBarHeight = (VERSION.SDK_INT >= 23);
    private View mActionDivider;
    private ProgressBar mActionSpinner;
    private ArrayMap<SliceActionImpl, SliceActionView> mActions = new ArrayMap<>();
    private boolean mAllowTwoLines;
    private View mBottomDivider;
    private LinearLayout mContent;
    private LinearLayout mEndContainer;
    Handler mHandler;
    private List<SliceAction> mHeaderActions;
    private int mIconSize = getContext().getResources().getDimensionPixelSize(R$dimen.abc_slice_icon_size);
    private int mImageSize = getContext().getResources().getDimensionPixelSize(R$dimen.abc_slice_small_image_size);
    private boolean mIsHeader;
    boolean mIsRangeSliding;
    long mLastSentRangeUpdate;
    private TextView mLastUpdatedText;
    protected Set<SliceItem> mLoadingActions = new HashSet();
    private int mMeasuredRangeHeight;
    private TextView mPrimaryText;
    private ProgressBar mRangeBar;
    boolean mRangeHasPendingUpdate;
    private SliceItem mRangeItem;
    int mRangeMaxValue;
    int mRangeMinValue;
    Runnable mRangeUpdater = new Runnable() {
        public void run() {
            RowView.this.sendSliderValue();
            RowView.this.mRangeUpdaterRunning = false;
        }
    };
    boolean mRangeUpdaterRunning;
    int mRangeValue;
    private LinearLayout mRootView;
    private SliceActionImpl mRowAction;
    RowContent mRowContent;
    int mRowIndex;
    private TextView mSecondaryText;
    private View mSeeMoreView;
    private OnSeekBarChangeListener mSeekBarChangeListener = new OnSeekBarChangeListener() {
        public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
            RowView rowView = RowView.this;
            rowView.mRangeValue = i + rowView.mRangeMinValue;
            long currentTimeMillis = System.currentTimeMillis();
            RowView rowView2 = RowView.this;
            long j = rowView2.mLastSentRangeUpdate;
            if (j == 0 || currentTimeMillis - j <= 200) {
                RowView rowView3 = RowView.this;
                if (!rowView3.mRangeUpdaterRunning) {
                    rowView3.mRangeUpdaterRunning = true;
                    rowView3.mHandler.postDelayed(rowView3.mRangeUpdater, 200);
                    return;
                }
                return;
            }
            rowView2.mRangeUpdaterRunning = false;
            rowView2.mHandler.removeCallbacks(rowView2.mRangeUpdater);
            RowView.this.sendSliderValue();
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
            RowView.this.mIsRangeSliding = true;
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            RowView rowView = RowView.this;
            rowView.mIsRangeSliding = false;
            if (rowView.mRangeUpdaterRunning || rowView.mRangeHasPendingUpdate) {
                RowView rowView2 = RowView.this;
                rowView2.mRangeUpdaterRunning = false;
                rowView2.mRangeHasPendingUpdate = false;
                rowView2.mHandler.removeCallbacks(rowView2.mRangeUpdater);
                RowView rowView3 = RowView.this;
                int progress = seekBar.getProgress();
                RowView rowView4 = RowView.this;
                rowView3.mRangeValue = progress + rowView4.mRangeMinValue;
                rowView4.sendSliderValue();
            }
        }
    };
    private SliceItem mSelectionItem;
    private ArrayList<String> mSelectionOptionKeys;
    private ArrayList<CharSequence> mSelectionOptionValues;
    private Spinner mSelectionSpinner;
    boolean mShowActionSpinner;
    private LinearLayout mStartContainer;
    private SliceItem mStartItem;
    private LinearLayout mSubContent;
    private ArrayMap<SliceActionImpl, SliceActionView> mToggles = new ArrayMap<>();

    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public RowView(Context context) {
        super(context);
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R$layout.abc_slice_small_template, this, false);
        this.mRootView = linearLayout;
        addView(linearLayout);
        this.mStartContainer = (LinearLayout) findViewById(R$id.icon_frame);
        this.mContent = (LinearLayout) findViewById(16908290);
        this.mSubContent = (LinearLayout) findViewById(R$id.subcontent);
        this.mPrimaryText = (TextView) findViewById(16908310);
        this.mSecondaryText = (TextView) findViewById(16908304);
        this.mLastUpdatedText = (TextView) findViewById(R$id.last_updated);
        this.mBottomDivider = findViewById(R$id.bottom_divider);
        this.mActionDivider = findViewById(R$id.action_divider);
        this.mActionSpinner = (ProgressBar) findViewById(R$id.action_sent_indicator);
        SliceViewUtil.tintIndeterminateProgressBar(getContext(), this.mActionSpinner);
        this.mEndContainer = (LinearLayout) findViewById(16908312);
        ViewCompat.setImportantForAccessibility(this, 2);
        ViewCompat.setImportantForAccessibility(this.mContent, 2);
    }

    public void setStyle(SliceStyle sliceStyle) {
        super.setStyle(sliceStyle);
        applyRowStyle();
    }

    private void applyRowStyle() {
        SliceStyle sliceStyle = this.mSliceStyle;
        if (sliceStyle != null && sliceStyle.getRowStyle() != null) {
            RowStyle rowStyle = this.mSliceStyle.getRowStyle();
            setViewSidePaddings(this.mStartContainer, rowStyle.getTitleItemStartPadding(), rowStyle.getTitleItemEndPadding());
            setViewSidePaddings(this.mContent, rowStyle.getContentStartPadding(), rowStyle.getContentEndPadding());
            setViewSidePaddings(this.mPrimaryText, rowStyle.getTitleStartPadding(), rowStyle.getTitleEndPadding());
            setViewSidePaddings(this.mSubContent, rowStyle.getSubContentStartPadding(), rowStyle.getSubContentEndPadding());
            setViewSidePaddings(this.mEndContainer, rowStyle.getEndItemStartPadding(), rowStyle.getEndItemEndPadding());
            setViewSideMargins(this.mBottomDivider, rowStyle.getBottomDividerStartPadding(), rowStyle.getBottomDividerEndPadding());
            setViewHeight(this.mActionDivider, rowStyle.getActionDividerHeight());
        }
    }

    private void setViewSidePaddings(View view, int i, int i2) {
        boolean z = i < 0 && i2 < 0;
        if (view != null && !z) {
            if (i < 0) {
                i = view.getPaddingStart();
            }
            int paddingTop = view.getPaddingTop();
            if (i2 < 0) {
                i2 = view.getPaddingEnd();
            }
            view.setPaddingRelative(i, paddingTop, i2, view.getPaddingBottom());
        }
    }

    private void setViewSideMargins(View view, int i, int i2) {
        boolean z = i < 0 && i2 < 0;
        if (view != null && !z) {
            MarginLayoutParams marginLayoutParams = (MarginLayoutParams) view.getLayoutParams();
            if (i >= 0) {
                marginLayoutParams.setMarginStart(i);
            }
            if (i2 >= 0) {
                marginLayoutParams.setMarginEnd(i2);
            }
            view.setLayoutParams(marginLayoutParams);
        }
    }

    private void setViewHeight(View view, int i) {
        if (view != null && i >= 0) {
            LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = i;
            view.setLayoutParams(layoutParams);
        }
    }

    public void setInsets(int i, int i2, int i3, int i4) {
        super.setInsets(i, i2, i3, i4);
        setPadding(i, i2, i3, i4);
    }

    private int getRowContentHeight() {
        int height = this.mRowContent.getHeight(this.mSliceStyle, this.mViewPolicy);
        if (this.mRangeBar != null && this.mStartItem == null) {
            height -= this.mSliceStyle.getRowRangeHeight();
        }
        return this.mSelectionSpinner != null ? height - this.mSliceStyle.getRowSelectionHeight() : height;
    }

    public void setTint(int i) {
        super.setTint(i);
        if (this.mRowContent != null) {
            populateViews(true);
        }
    }

    public void setSliceActions(List<SliceAction> list) {
        this.mHeaderActions = list;
        if (this.mRowContent != null) {
            updateEndItems();
        }
    }

    public void setShowLastUpdated(boolean z) {
        super.setShowLastUpdated(z);
        if (this.mRowContent != null) {
            populateViews(true);
        }
    }

    public void setAllowTwoLines(boolean z) {
        this.mAllowTwoLines = z;
        if (this.mRowContent != null) {
            populateViews(true);
        }
    }

    private void measureChildWithExactHeight(View view, int i, int i2) {
        measureChild(view, i, MeasureSpec.makeMeasureSpec(i2 + this.mInsetTop + this.mInsetBottom, 1073741824));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int i3;
        int rowContentHeight = getRowContentHeight();
        if (rowContentHeight != 0) {
            this.mRootView.setVisibility(0);
            measureChildWithExactHeight(this.mRootView, i, rowContentHeight);
            i3 = this.mRootView.getMeasuredWidth();
        } else {
            this.mRootView.setVisibility(8);
            i3 = 0;
        }
        ProgressBar progressBar = this.mRangeBar;
        if (progressBar == null || this.mStartItem != null) {
            Spinner spinner = this.mSelectionSpinner;
            if (spinner != null) {
                measureChildWithExactHeight(spinner, i, this.mSliceStyle.getRowSelectionHeight());
                i3 = Math.max(i3, this.mSelectionSpinner.getMeasuredWidth());
            }
        } else {
            if (sCanSpecifyLargerRangeBarHeight) {
                measureChildWithExactHeight(progressBar, i, this.mSliceStyle.getRowRangeHeight());
            } else {
                measureChild(progressBar, i, MeasureSpec.makeMeasureSpec(0, 0));
            }
            this.mMeasuredRangeHeight = this.mRangeBar.getMeasuredHeight();
            i3 = Math.max(i3, this.mRangeBar.getMeasuredWidth());
        }
        int max = Math.max(i3 + this.mInsetStart + this.mInsetEnd, getSuggestedMinimumWidth());
        RowContent rowContent = this.mRowContent;
        setMeasuredDimension(FrameLayout.resolveSizeAndState(max, i, 0), (rowContent != null ? rowContent.getHeight(this.mSliceStyle, this.mViewPolicy) : 0) + this.mInsetTop + this.mInsetBottom);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int paddingLeft = getPaddingLeft();
        LinearLayout linearLayout = this.mRootView;
        linearLayout.layout(paddingLeft, this.mInsetTop, linearLayout.getMeasuredWidth() + paddingLeft, getRowContentHeight() + this.mInsetTop);
        if (this.mRangeBar != null && this.mStartItem == null) {
            int rowContentHeight = getRowContentHeight() + ((this.mSliceStyle.getRowRangeHeight() - this.mMeasuredRangeHeight) / 2) + this.mInsetTop;
            int i5 = this.mMeasuredRangeHeight + rowContentHeight;
            ProgressBar progressBar = this.mRangeBar;
            progressBar.layout(paddingLeft, rowContentHeight, progressBar.getMeasuredWidth() + paddingLeft, i5);
        } else if (this.mSelectionSpinner != null) {
            int rowContentHeight2 = getRowContentHeight() + this.mInsetTop;
            int measuredHeight = this.mSelectionSpinner.getMeasuredHeight() + rowContentHeight2;
            Spinner spinner = this.mSelectionSpinner;
            spinner.layout(paddingLeft, rowContentHeight2, spinner.getMeasuredWidth() + paddingLeft, measuredHeight);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0053, code lost:
        if (r2 != false) goto L_0x0057;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setSliceItem(androidx.slice.widget.SliceContent r5, boolean r6, int r7, int r8, androidx.slice.widget.SliceView.OnSliceActionListener r9) {
        /*
            r4 = this;
            r4.setSliceActionListener(r9)
            r8 = 0
            if (r5 == 0) goto L_0x0056
            androidx.slice.widget.RowContent r9 = r4.mRowContent
            if (r9 == 0) goto L_0x0056
            boolean r9 = r9.isValid()
            if (r9 == 0) goto L_0x0056
            androidx.slice.widget.RowContent r9 = r4.mRowContent
            if (r9 == 0) goto L_0x001e
            androidx.slice.SliceStructure r0 = new androidx.slice.SliceStructure
            androidx.slice.SliceItem r9 = r9.getSliceItem()
            r0.<init>(r9)
            goto L_0x001f
        L_0x001e:
            r0 = 0
        L_0x001f:
            androidx.slice.SliceStructure r9 = new androidx.slice.SliceStructure
            androidx.slice.SliceItem r1 = r5.getSliceItem()
            androidx.slice.Slice r1 = r1.getSlice()
            r9.<init>(r1)
            r1 = 1
            if (r0 == 0) goto L_0x0037
            boolean r2 = r0.equals(r9)
            if (r2 == 0) goto L_0x0037
            r2 = r1
            goto L_0x0038
        L_0x0037:
            r2 = r8
        L_0x0038:
            if (r0 == 0) goto L_0x0050
            android.net.Uri r3 = r0.getUri()
            if (r3 == 0) goto L_0x0050
            android.net.Uri r0 = r0.getUri()
            android.net.Uri r9 = r9.getUri()
            boolean r9 = r0.equals(r9)
            if (r9 == 0) goto L_0x0050
            r9 = r1
            goto L_0x0051
        L_0x0050:
            r9 = r8
        L_0x0051:
            if (r9 == 0) goto L_0x0056
            if (r2 == 0) goto L_0x0056
            goto L_0x0057
        L_0x0056:
            r1 = r8
        L_0x0057:
            r4.mShowActionSpinner = r8
            r4.mIsHeader = r6
            androidx.slice.widget.RowContent r5 = (androidx.slice.widget.RowContent) r5
            r4.mRowContent = r5
            r4.mRowIndex = r7
            r4.populateViews(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.slice.widget.RowView.setSliceItem(androidx.slice.widget.SliceContent, boolean, int, int, androidx.slice.widget.SliceView$OnSliceActionListener):void");
    }

    private void populateViews(boolean z) {
        int i;
        boolean z2 = z && this.mIsRangeSliding;
        if (!z2) {
            resetViewState();
        }
        if (this.mRowContent.getLayoutDir() != -1) {
            setLayoutDirection(this.mRowContent.getLayoutDir());
        }
        if (this.mRowContent.isDefaultSeeMore()) {
            showSeeMore();
            return;
        }
        CharSequence contentDescription = this.mRowContent.getContentDescription();
        if (contentDescription != null) {
            this.mContent.setContentDescription(contentDescription);
        }
        SliceItem startItem = this.mRowContent.getStartItem();
        this.mStartItem = startItem;
        boolean z3 = startItem != null && (this.mRowIndex > 0 || this.mRowContent.hasTitleItems());
        if (z3) {
            z3 = addItem(this.mStartItem, this.mTintColor, true);
        }
        int i2 = 8;
        this.mStartContainer.setVisibility(z3 ? 0 : 8);
        SliceItem titleItem = this.mRowContent.getTitleItem();
        if (titleItem != null) {
            this.mPrimaryText.setText(titleItem.getSanitizedText());
        }
        SliceStyle sliceStyle = this.mSliceStyle;
        if (sliceStyle != null) {
            TextView textView = this.mPrimaryText;
            if (this.mIsHeader) {
                i = sliceStyle.getHeaderTitleSize();
            } else {
                i = sliceStyle.getTitleSize();
            }
            textView.setTextSize(0, (float) i);
            this.mPrimaryText.setTextColor(this.mSliceStyle.getTitleColor());
        }
        this.mPrimaryText.setVisibility(titleItem != null ? 0 : 8);
        addSubtitle(titleItem != null);
        View view = this.mBottomDivider;
        if (this.mRowContent.hasBottomDivider()) {
            i2 = 0;
        }
        view.setVisibility(i2);
        SliceItem primaryAction = this.mRowContent.getPrimaryAction();
        if (!(primaryAction == null || primaryAction == this.mStartItem)) {
            SliceActionImpl sliceActionImpl = new SliceActionImpl(primaryAction);
            this.mRowAction = sliceActionImpl;
            if (sliceActionImpl.isToggle()) {
                addAction(this.mRowAction, this.mTintColor, this.mEndContainer, false);
                setViewClickable(this.mRootView, true);
                return;
            }
        }
        SliceItem range = this.mRowContent.getRange();
        if (range != null) {
            if (this.mRowAction != null) {
                setViewClickable(this.mRootView, true);
            }
            this.mRangeItem = range;
            if (!z2) {
                setRangeBounds();
                addRange();
            }
            if (this.mStartItem == null) {
                return;
            }
        }
        SliceItem selection = this.mRowContent.getSelection();
        if (selection != null) {
            this.mSelectionItem = selection;
            addSelection(selection);
            return;
        }
        updateEndItems();
        updateActionSpinner();
    }

    private void updateEndItems() {
        String str;
        boolean z;
        SliceItem sliceItem;
        RowContent rowContent = this.mRowContent;
        if (rowContent == null) {
            return;
        }
        if (rowContent.getRange() == null || this.mStartItem != null) {
            this.mEndContainer.removeAllViews();
            List<SliceAction> endItems = this.mRowContent.getEndItems();
            List<SliceAction> list = this.mHeaderActions;
            if (list != null) {
                endItems = list;
            }
            if (this.mRowIndex == 0 && this.mStartItem != null && endItems.isEmpty() && !this.mRowContent.hasTitleItems()) {
                endItems.add(this.mStartItem);
            }
            SliceItem sliceItem2 = null;
            int i = 0;
            int i2 = 0;
            int i3 = 0;
            boolean z2 = false;
            boolean z3 = false;
            while (true) {
                str = "action";
                if (i2 >= endItems.size()) {
                    break;
                }
                if (endItems.get(i2) instanceof SliceItem) {
                    sliceItem = (SliceItem) endItems.get(i2);
                } else {
                    sliceItem = ((SliceActionImpl) endItems.get(i2)).getSliceItem();
                }
                if (i3 < 3 && addItem(sliceItem, this.mTintColor, false)) {
                    if (sliceItem2 == null && SliceQuery.find(sliceItem, str) != null) {
                        sliceItem2 = sliceItem;
                    }
                    i3++;
                    if (i3 == 1) {
                        z2 = !this.mToggles.isEmpty() && SliceQuery.find(sliceItem.getSlice(), "image") == null;
                        z3 = endItems.size() == 1 && SliceQuery.find(sliceItem, str) != null;
                    }
                }
                i2++;
            }
            int i4 = 8;
            this.mEndContainer.setVisibility(i3 > 0 ? 0 : 8);
            View view = this.mActionDivider;
            if (this.mRowAction != null && (z2 || (this.mRowContent.hasActionDivider() && z3))) {
                i4 = 0;
            }
            view.setVisibility(i4);
            SliceItem sliceItem3 = this.mStartItem;
            boolean z4 = (sliceItem3 == null || SliceQuery.find(sliceItem3, str) == null) ? false : true;
            boolean z5 = sliceItem2 != null;
            if (this.mRowAction != null) {
                setViewClickable(this.mRootView, true);
            } else if (z5 != z4 && (i3 == 1 || z4)) {
                if (!this.mToggles.isEmpty()) {
                    this.mRowAction = (SliceActionImpl) this.mToggles.keySet().iterator().next();
                } else if (!this.mActions.isEmpty() && this.mActions.size() == 1) {
                    this.mRowAction = ((SliceActionView) this.mActions.valueAt(0)).getAction();
                }
                setViewClickable(this.mRootView, true);
                z = true;
                SliceActionImpl sliceActionImpl = this.mRowAction;
                if (sliceActionImpl != null && !z && this.mLoadingActions.contains(sliceActionImpl.getSliceItem())) {
                    this.mShowActionSpinner = true;
                }
                LinearLayout linearLayout = this.mRootView;
                if (!linearLayout.isClickable() || !this.mToggles.isEmpty() || !this.mActions.isEmpty()) {
                    i = 2;
                }
                ViewCompat.setImportantForAccessibility(linearLayout, i);
            }
            z = false;
            SliceActionImpl sliceActionImpl2 = this.mRowAction;
            this.mShowActionSpinner = true;
            LinearLayout linearLayout2 = this.mRootView;
            i = 2;
            ViewCompat.setImportantForAccessibility(linearLayout2, i);
        }
    }

    public void setLastUpdated(long j) {
        super.setLastUpdated(j);
        RowContent rowContent = this.mRowContent;
        if (rowContent != null) {
            addSubtitle(rowContent.getTitleItem() != null && TextUtils.isEmpty(this.mRowContent.getTitleItem().getSanitizedText()));
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x004a  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0064  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00a4  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x00fd  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x00ff  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x0107  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x012a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void addSubtitle(boolean r10) {
        /*
            r9 = this;
            androidx.slice.widget.RowContent r0 = r9.mRowContent
            if (r0 == 0) goto L_0x013d
            androidx.slice.SliceItem r0 = r0.getRange()
            if (r0 == 0) goto L_0x0010
            androidx.slice.SliceItem r0 = r9.mStartItem
            if (r0 == 0) goto L_0x0010
            goto L_0x013d
        L_0x0010:
            int r0 = r9.getMode()
            r1 = 1
            if (r0 != r1) goto L_0x001e
            androidx.slice.widget.RowContent r0 = r9.mRowContent
            androidx.slice.SliceItem r0 = r0.getSummaryItem()
            goto L_0x0024
        L_0x001e:
            androidx.slice.widget.RowContent r0 = r9.mRowContent
            androidx.slice.SliceItem r0 = r0.getSubtitleItem()
        L_0x0024:
            boolean r2 = r9.mShowLastUpdated
            r3 = 0
            r4 = 0
            if (r2 == 0) goto L_0x0047
            long r5 = r9.mLastUpdated
            r7 = -1
            int r2 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r2 == 0) goto L_0x0047
            java.lang.CharSequence r2 = r9.getRelativeTimeString(r5)
            if (r2 == 0) goto L_0x0047
            android.content.res.Resources r5 = r9.getResources()
            int r6 = androidx.slice.view.R$string.abc_slice_updated
            java.lang.Object[] r7 = new java.lang.Object[r1]
            r7[r4] = r2
            java.lang.String r2 = r5.getString(r6, r7)
            goto L_0x0048
        L_0x0047:
            r2 = r3
        L_0x0048:
            if (r0 == 0) goto L_0x004e
            java.lang.CharSequence r3 = r0.getSanitizedText()
        L_0x004e:
            boolean r5 = android.text.TextUtils.isEmpty(r3)
            if (r5 == 0) goto L_0x0061
            if (r0 == 0) goto L_0x005f
            java.lang.String r5 = "partial"
            boolean r0 = r0.hasHint(r5)
            if (r0 == 0) goto L_0x005f
            goto L_0x0061
        L_0x005f:
            r0 = r4
            goto L_0x0062
        L_0x0061:
            r0 = r1
        L_0x0062:
            if (r0 == 0) goto L_0x00a1
            android.widget.TextView r5 = r9.mSecondaryText
            r5.setText(r3)
            androidx.slice.widget.SliceStyle r5 = r9.mSliceStyle
            if (r5 == 0) goto L_0x00a1
            android.widget.TextView r6 = r9.mSecondaryText
            boolean r7 = r9.mIsHeader
            if (r7 == 0) goto L_0x0078
            int r5 = r5.getHeaderSubtitleSize()
            goto L_0x007c
        L_0x0078:
            int r5 = r5.getSubtitleSize()
        L_0x007c:
            float r5 = (float) r5
            r6.setTextSize(r4, r5)
            android.widget.TextView r5 = r9.mSecondaryText
            androidx.slice.widget.SliceStyle r6 = r9.mSliceStyle
            int r6 = r6.getSubtitleColor()
            r5.setTextColor(r6)
            boolean r5 = r9.mIsHeader
            if (r5 == 0) goto L_0x0096
            androidx.slice.widget.SliceStyle r5 = r9.mSliceStyle
            int r5 = r5.getVerticalHeaderTextPadding()
            goto L_0x009c
        L_0x0096:
            androidx.slice.widget.SliceStyle r5 = r9.mSliceStyle
            int r5 = r5.getVerticalTextPadding()
        L_0x009c:
            android.widget.TextView r6 = r9.mSecondaryText
            r6.setPadding(r4, r5, r4, r4)
        L_0x00a1:
            r5 = 2
            if (r2 == 0) goto L_0x00f3
            boolean r3 = android.text.TextUtils.isEmpty(r3)
            if (r3 != 0) goto L_0x00bb
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r6 = " · "
            r3.append(r6)
            r3.append(r2)
            java.lang.String r2 = r3.toString()
        L_0x00bb:
            android.text.SpannableString r3 = new android.text.SpannableString
            r3.<init>(r2)
            android.text.style.StyleSpan r6 = new android.text.style.StyleSpan
            r6.<init>(r5)
            int r7 = r2.length()
            r3.setSpan(r6, r4, r7, r4)
            android.widget.TextView r6 = r9.mLastUpdatedText
            r6.setText(r3)
            androidx.slice.widget.SliceStyle r3 = r9.mSliceStyle
            if (r3 == 0) goto L_0x00f3
            android.widget.TextView r6 = r9.mLastUpdatedText
            boolean r7 = r9.mIsHeader
            if (r7 == 0) goto L_0x00e0
            int r3 = r3.getHeaderSubtitleSize()
            goto L_0x00e4
        L_0x00e0:
            int r3 = r3.getSubtitleSize()
        L_0x00e4:
            float r3 = (float) r3
            r6.setTextSize(r4, r3)
            android.widget.TextView r3 = r9.mLastUpdatedText
            androidx.slice.widget.SliceStyle r6 = r9.mSliceStyle
            int r6 = r6.getSubtitleColor()
            r3.setTextColor(r6)
        L_0x00f3:
            android.widget.TextView r3 = r9.mLastUpdatedText
            boolean r6 = android.text.TextUtils.isEmpty(r2)
            r7 = 8
            if (r6 == 0) goto L_0x00ff
            r6 = r7
            goto L_0x0100
        L_0x00ff:
            r6 = r4
        L_0x0100:
            r3.setVisibility(r6)
            android.widget.TextView r3 = r9.mSecondaryText
            if (r0 == 0) goto L_0x0108
            r7 = r4
        L_0x0108:
            r3.setVisibility(r7)
            int r3 = r9.mRowIndex
            if (r3 > 0) goto L_0x0116
            boolean r3 = r9.mAllowTwoLines
            if (r3 == 0) goto L_0x0114
            goto L_0x0116
        L_0x0114:
            r3 = r4
            goto L_0x0117
        L_0x0116:
            r3 = r1
        L_0x0117:
            if (r3 == 0) goto L_0x0124
            if (r10 != 0) goto L_0x0124
            if (r0 == 0) goto L_0x0124
            boolean r10 = android.text.TextUtils.isEmpty(r2)
            if (r10 == 0) goto L_0x0124
            goto L_0x0125
        L_0x0124:
            r5 = r1
        L_0x0125:
            android.widget.TextView r10 = r9.mSecondaryText
            if (r5 != r1) goto L_0x012a
            goto L_0x012b
        L_0x012a:
            r1 = r4
        L_0x012b:
            r10.setSingleLine(r1)
            android.widget.TextView r10 = r9.mSecondaryText
            r10.setMaxLines(r5)
            android.widget.TextView r10 = r9.mSecondaryText
            r10.requestLayout()
            android.widget.TextView r9 = r9.mLastUpdatedText
            r9.requestLayout()
        L_0x013d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.slice.widget.RowView.addSubtitle(boolean):void");
    }

    private CharSequence getRelativeTimeString(long j) {
        long currentTimeMillis = System.currentTimeMillis() - j;
        if (currentTimeMillis > 31449600000L) {
            int i = (int) (currentTimeMillis / 31449600000L);
            return getResources().getQuantityString(R$plurals.abc_slice_duration_years, i, new Object[]{Integer.valueOf(i)});
        } else if (currentTimeMillis > 86400000) {
            int i2 = (int) (currentTimeMillis / 86400000);
            return getResources().getQuantityString(R$plurals.abc_slice_duration_days, i2, new Object[]{Integer.valueOf(i2)});
        } else if (currentTimeMillis <= 60000) {
            return null;
        } else {
            int i3 = (int) (currentTimeMillis / 60000);
            return getResources().getQuantityString(R$plurals.abc_slice_duration_min, i3, new Object[]{Integer.valueOf(i3)});
        }
    }

    private void setRangeBounds() {
        String str = "int";
        SliceItem findSubtype = SliceQuery.findSubtype(this.mRangeItem, str, "min");
        int i = 0;
        int i2 = findSubtype != null ? findSubtype.getInt() : 0;
        this.mRangeMinValue = i2;
        SliceItem findSubtype2 = SliceQuery.findSubtype(this.mRangeItem, str, "max");
        int i3 = 100;
        if (findSubtype2 != null) {
            i3 = findSubtype2.getInt();
        }
        this.mRangeMaxValue = i3;
        SliceItem findSubtype3 = SliceQuery.findSubtype(this.mRangeItem, str, "value");
        if (findSubtype3 != null) {
            i = findSubtype3.getInt() - i2;
        }
        this.mRangeValue = i;
    }

    private void addRange() {
        ProgressBar progressBar;
        if (this.mHandler == null) {
            this.mHandler = new Handler();
        }
        boolean equals = "action".equals(this.mRangeItem.getFormat());
        boolean z = this.mStartItem == null;
        if (!equals) {
            progressBar = new ProgressBar(getContext(), null, 16842872);
        } else if (z) {
            progressBar = new SeekBar(getContext());
        } else {
            progressBar = (SeekBar) LayoutInflater.from(getContext()).inflate(R$layout.abc_slice_seekbar_view, this, false);
        }
        Drawable wrap = DrawableCompat.wrap(progressBar.getProgressDrawable());
        int i = this.mTintColor;
        if (!(i == -1 || wrap == null)) {
            DrawableCompat.setTint(wrap, i);
            progressBar.setProgressDrawable(wrap);
        }
        progressBar.setMax(this.mRangeMaxValue - this.mRangeMinValue);
        progressBar.setProgress(this.mRangeValue);
        progressBar.setVisibility(0);
        if (this.mStartItem == null) {
            addView(progressBar);
        } else {
            this.mSubContent.setVisibility(8);
            this.mContent.addView(progressBar, 1);
        }
        this.mRangeBar = progressBar;
        if (equals) {
            SliceItem inputRangeThumb = this.mRowContent.getInputRangeThumb();
            SeekBar seekBar = (SeekBar) this.mRangeBar;
            if (!(inputRangeThumb == null || inputRangeThumb.getIcon() == null)) {
                Drawable loadDrawable = inputRangeThumb.getIcon().loadDrawable(getContext());
                if (loadDrawable != null) {
                    seekBar.setThumb(loadDrawable);
                }
            }
            Drawable wrap2 = DrawableCompat.wrap(seekBar.getThumb());
            int i2 = this.mTintColor;
            if (!(i2 == -1 || wrap2 == null)) {
                DrawableCompat.setTint(wrap2, i2);
                seekBar.setThumb(wrap2);
            }
            seekBar.setOnSeekBarChangeListener(this.mSeekBarChangeListener);
        }
    }

    /* access modifiers changed from: 0000 */
    public void sendSliderValue() {
        if (this.mRangeItem != null) {
            try {
                this.mLastSentRangeUpdate = System.currentTimeMillis();
                this.mRangeItem.fireAction(getContext(), new Intent().addFlags(268435456).putExtra("android.app.slice.extra.RANGE_VALUE", this.mRangeValue));
                if (this.mObserver != null) {
                    EventInfo eventInfo = new EventInfo(getMode(), 2, 4, this.mRowIndex);
                    eventInfo.state = this.mRangeValue;
                    this.mObserver.onSliceAction(eventInfo, this.mRangeItem);
                }
            } catch (CanceledException e) {
                Log.e("RowView", "PendingIntent for slice cannot be sent", e);
            }
        }
    }

    private void addSelection(SliceItem sliceItem) {
        if (this.mHandler == null) {
            this.mHandler = new Handler();
        }
        this.mSelectionOptionKeys = new ArrayList<>();
        this.mSelectionOptionValues = new ArrayList<>();
        List items = sliceItem.getSlice().getItems();
        for (int i = 0; i < items.size(); i++) {
            SliceItem sliceItem2 = (SliceItem) items.get(i);
            if (sliceItem2.hasHint("selection_option")) {
                String str = "text";
                SliceItem findSubtype = SliceQuery.findSubtype(sliceItem2, str, "selection_option_key");
                SliceItem findSubtype2 = SliceQuery.findSubtype(sliceItem2, str, "selection_option_value");
                if (!(findSubtype == null || findSubtype2 == null)) {
                    this.mSelectionOptionKeys.add(findSubtype.getText().toString());
                    this.mSelectionOptionValues.add(findSubtype2.getSanitizedText());
                }
            }
        }
        this.mSelectionSpinner = (Spinner) LayoutInflater.from(getContext()).inflate(R$layout.abc_slice_row_selection, this, false);
        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), R$layout.abc_slice_row_selection_text, this.mSelectionOptionValues);
        arrayAdapter.setDropDownViewResource(R$layout.abc_slice_row_selection_dropdown_text);
        this.mSelectionSpinner.setAdapter(arrayAdapter);
        addView(this.mSelectionSpinner);
        this.mSelectionSpinner.setOnItemSelectedListener(this);
    }

    private void addAction(SliceActionImpl sliceActionImpl, int i, ViewGroup viewGroup, boolean z) {
        SliceActionView sliceActionView = new SliceActionView(getContext());
        viewGroup.addView(sliceActionView);
        if (viewGroup.getVisibility() == 8) {
            viewGroup.setVisibility(0);
        }
        boolean isToggle = sliceActionImpl.isToggle();
        boolean z2 = !isToggle;
        EventInfo eventInfo = new EventInfo(getMode(), z2 ? 1 : 0, isToggle ? 3 : 0, this.mRowIndex);
        if (z) {
            eventInfo.setPosition(0, 0, 1);
        }
        sliceActionView.setAction(sliceActionImpl, eventInfo, this.mObserver, i, this.mLoadingListener);
        if (this.mLoadingActions.contains(sliceActionImpl.getSliceItem())) {
            sliceActionView.setLoading(true);
        }
        if (isToggle) {
            this.mToggles.put(sliceActionImpl, sliceActionView);
        } else {
            this.mActions.put(sliceActionImpl, sliceActionView);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x001f, code lost:
        if ("action".equals(r8.getFormat()) != false) goto L_0x0021;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean addItem(androidx.slice.SliceItem r8, int r9, boolean r10) {
        /*
            r7 = this;
            if (r10 == 0) goto L_0x0005
            android.widget.LinearLayout r0 = r7.mStartContainer
            goto L_0x0007
        L_0x0005:
            android.widget.LinearLayout r0 = r7.mEndContainer
        L_0x0007:
            java.lang.String r1 = r8.getFormat()
            java.lang.String r2 = "slice"
            boolean r1 = r2.equals(r1)
            r2 = 1
            r3 = 0
            if (r1 != 0) goto L_0x0021
            java.lang.String r1 = r8.getFormat()
            java.lang.String r4 = "action"
            boolean r1 = r4.equals(r1)
            if (r1 == 0) goto L_0x004f
        L_0x0021:
            java.lang.String r1 = "shortcut"
            boolean r1 = r8.hasHint(r1)
            if (r1 == 0) goto L_0x0032
            androidx.slice.core.SliceActionImpl r1 = new androidx.slice.core.SliceActionImpl
            r1.<init>(r8)
            r7.addAction(r1, r9, r0, r10)
            return r2
        L_0x0032:
            androidx.slice.Slice r10 = r8.getSlice()
            java.util.List r10 = r10.getItems()
            int r10 = r10.size()
            if (r10 != 0) goto L_0x0041
            return r3
        L_0x0041:
            androidx.slice.Slice r8 = r8.getSlice()
            java.util.List r8 = r8.getItems()
            java.lang.Object r8 = r8.get(r3)
            androidx.slice.SliceItem r8 = (androidx.slice.SliceItem) r8
        L_0x004f:
            java.lang.String r10 = r8.getFormat()
            java.lang.String r1 = "image"
            boolean r10 = r1.equals(r10)
            r1 = 0
            if (r10 == 0) goto L_0x0062
            androidx.core.graphics.drawable.IconCompat r10 = r8.getIcon()
            r4 = r1
            goto L_0x0073
        L_0x0062:
            java.lang.String r10 = r8.getFormat()
            java.lang.String r4 = "long"
            boolean r10 = r4.equals(r10)
            if (r10 == 0) goto L_0x0071
            r4 = r8
            r10 = r1
            goto L_0x0073
        L_0x0071:
            r10 = r1
            r4 = r10
        L_0x0073:
            if (r10 == 0) goto L_0x00ee
            java.lang.String r1 = "no_tint"
            boolean r1 = r8.hasHint(r1)
            r1 = r1 ^ r2
            java.lang.String r4 = "raw"
            boolean r8 = r8.hasHint(r4)
            android.content.res.Resources r4 = r7.getResources()
            android.util.DisplayMetrics r4 = r4.getDisplayMetrics()
            float r4 = r4.density
            android.widget.ImageView r5 = new android.widget.ImageView
            android.content.Context r6 = r7.getContext()
            r5.<init>(r6)
            android.content.Context r6 = r7.getContext()
            android.graphics.drawable.Drawable r10 = r10.loadDrawable(r6)
            r5.setImageDrawable(r10)
            if (r1 == 0) goto L_0x00a8
            r6 = -1
            if (r9 == r6) goto L_0x00a8
            r5.setColorFilter(r9)
        L_0x00a8:
            boolean r9 = r7.mIsRangeSliding
            if (r9 == 0) goto L_0x00b3
            r0.removeAllViews()
            r0.addView(r5)
            goto L_0x00b6
        L_0x00b3:
            r0.addView(r5)
        L_0x00b6:
            android.view.ViewGroup$LayoutParams r9 = r5.getLayoutParams()
            android.widget.LinearLayout$LayoutParams r9 = (android.widget.LinearLayout.LayoutParams) r9
            if (r8 == 0) goto L_0x00c9
            int r0 = r10.getIntrinsicWidth()
            float r0 = (float) r0
            float r0 = r0 / r4
            int r0 = java.lang.Math.round(r0)
            goto L_0x00cb
        L_0x00c9:
            int r0 = r7.mImageSize
        L_0x00cb:
            r9.width = r0
            if (r8 == 0) goto L_0x00da
            int r8 = r10.getIntrinsicHeight()
            float r8 = (float) r8
            float r8 = r8 / r4
            int r8 = java.lang.Math.round(r8)
            goto L_0x00dc
        L_0x00da:
            int r8 = r7.mImageSize
        L_0x00dc:
            r9.height = r8
            r5.setLayoutParams(r9)
            if (r1 == 0) goto L_0x00e8
            int r7 = r7.mIconSize
            int r7 = r7 / 2
            goto L_0x00e9
        L_0x00e8:
            r7 = r3
        L_0x00e9:
            r5.setPadding(r7, r7, r7, r7)
            r1 = r5
            goto L_0x0120
        L_0x00ee:
            if (r4 == 0) goto L_0x0120
            android.widget.TextView r1 = new android.widget.TextView
            android.content.Context r9 = r7.getContext()
            r1.<init>(r9)
            android.content.Context r9 = r7.getContext()
            long r4 = r8.getLong()
            java.lang.CharSequence r8 = androidx.slice.widget.SliceViewUtil.getTimestampString(r9, r4)
            r1.setText(r8)
            androidx.slice.widget.SliceStyle r8 = r7.mSliceStyle
            if (r8 == 0) goto L_0x011d
            int r8 = r8.getSubtitleSize()
            float r8 = (float) r8
            r1.setTextSize(r3, r8)
            androidx.slice.widget.SliceStyle r7 = r7.mSliceStyle
            int r7 = r7.getSubtitleColor()
            r1.setTextColor(r7)
        L_0x011d:
            r0.addView(r1)
        L_0x0120:
            if (r1 == 0) goto L_0x0123
            goto L_0x0124
        L_0x0123:
            r2 = r3
        L_0x0124:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.slice.widget.RowView.addItem(androidx.slice.SliceItem, int, boolean):boolean");
    }

    private void showSeeMore() {
        final Button button = (Button) LayoutInflater.from(getContext()).inflate(R$layout.abc_slice_row_show_more, this, false);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                try {
                    if (RowView.this.mObserver != null) {
                        RowView.this.mObserver.onSliceAction(new EventInfo(RowView.this.getMode(), 4, 0, RowView.this.mRowIndex), RowView.this.mRowContent.getSliceItem());
                    }
                    RowView.this.mShowActionSpinner = RowView.this.mRowContent.getSliceItem().fireActionInternal(RowView.this.getContext(), null);
                    if (RowView.this.mShowActionSpinner) {
                        if (RowView.this.mLoadingListener != null) {
                            RowView.this.mLoadingListener.onSliceActionLoading(RowView.this.mRowContent.getSliceItem(), RowView.this.mRowIndex);
                        }
                        RowView.this.mLoadingActions.add(RowView.this.mRowContent.getSliceItem());
                        button.setVisibility(8);
                    }
                    RowView.this.updateActionSpinner();
                } catch (CanceledException e) {
                    Log.e("RowView", "PendingIntent for slice cannot be sent", e);
                }
            }
        });
        int i = this.mTintColor;
        if (i != -1) {
            button.setTextColor(i);
        }
        this.mSeeMoreView = button;
        this.mRootView.addView(button);
        if (this.mLoadingActions.contains(this.mRowContent.getSliceItem())) {
            this.mShowActionSpinner = true;
            button.setVisibility(8);
            updateActionSpinner();
        }
    }

    /* access modifiers changed from: 0000 */
    public void updateActionSpinner() {
        this.mActionSpinner.setVisibility(this.mShowActionSpinner ? 0 : 8);
    }

    public void setLoadingActions(Set<SliceItem> set) {
        if (set == null) {
            this.mLoadingActions.clear();
            this.mShowActionSpinner = false;
        } else {
            this.mLoadingActions = set;
        }
        updateEndItems();
        updateActionSpinner();
    }

    public void onClick(View view) {
        SliceActionView sliceActionView;
        SliceActionImpl sliceActionImpl = this.mRowAction;
        if (sliceActionImpl != null && sliceActionImpl.getActionItem() != null) {
            if (this.mRowAction.isToggle()) {
                sliceActionView = (SliceActionView) this.mToggles.get(this.mRowAction);
            } else {
                sliceActionView = (SliceActionView) this.mActions.get(this.mRowAction);
            }
            if (sliceActionView != null && !(view instanceof SliceActionView)) {
                sliceActionView.sendAction();
            } else if (this.mRowIndex == 0) {
                performClick();
            } else {
                try {
                    this.mShowActionSpinner = this.mRowAction.getActionItem().fireActionInternal(getContext(), null);
                    if (this.mObserver != null) {
                        this.mObserver.onSliceAction(new EventInfo(getMode(), 3, 0, this.mRowIndex), this.mRowAction.getSliceItem());
                    }
                    if (this.mShowActionSpinner && this.mLoadingListener != null) {
                        this.mLoadingListener.onSliceActionLoading(this.mRowAction.getSliceItem(), this.mRowIndex);
                        this.mLoadingActions.add(this.mRowAction.getSliceItem());
                    }
                    updateActionSpinner();
                } catch (CanceledException e) {
                    Log.e("RowView", "PendingIntent for slice cannot be sent", e);
                }
            }
        }
    }

    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
        if (this.mSelectionItem != null && adapterView == this.mSelectionSpinner && i >= 0 && i < this.mSelectionOptionKeys.size()) {
            if (this.mObserver != null) {
                this.mObserver.onSliceAction(new EventInfo(getMode(), 5, 6, this.mRowIndex), this.mSelectionItem);
            }
            try {
                if (this.mSelectionItem.fireActionInternal(getContext(), new Intent().addFlags(268435456).putExtra("android.app.slice.extra.SELECTION", (String) this.mSelectionOptionKeys.get(i)))) {
                    this.mShowActionSpinner = true;
                    if (this.mLoadingListener != null) {
                        this.mLoadingListener.onSliceActionLoading(this.mRowAction.getSliceItem(), this.mRowIndex);
                        this.mLoadingActions.add(this.mRowAction.getSliceItem());
                    }
                    updateActionSpinner();
                }
            } catch (CanceledException e) {
                Log.e("RowView", "PendingIntent for slice cannot be sent", e);
            }
        }
    }

    private void setViewClickable(View view, boolean z) {
        Drawable drawable = null;
        view.setOnClickListener(z ? this : null);
        if (z) {
            drawable = SliceViewUtil.getDrawable(getContext(), 16843534);
        }
        view.setBackground(drawable);
        view.setClickable(z);
    }

    public void resetView() {
        this.mRowContent = null;
        this.mLoadingActions.clear();
        resetViewState();
    }

    private void resetViewState() {
        this.mRootView.setVisibility(0);
        setLayoutDirection(2);
        setViewClickable(this.mRootView, false);
        setViewClickable(this.mContent, false);
        this.mStartContainer.removeAllViews();
        this.mEndContainer.removeAllViews();
        this.mEndContainer.setVisibility(8);
        this.mPrimaryText.setText(null);
        this.mSecondaryText.setText(null);
        this.mLastUpdatedText.setText(null);
        this.mLastUpdatedText.setVisibility(8);
        this.mToggles.clear();
        this.mActions.clear();
        this.mRowAction = null;
        this.mBottomDivider.setVisibility(8);
        this.mActionDivider.setVisibility(8);
        View view = this.mSeeMoreView;
        if (view != null) {
            this.mRootView.removeView(view);
            this.mSeeMoreView = null;
        }
        this.mIsRangeSliding = false;
        this.mRangeHasPendingUpdate = false;
        this.mRangeItem = null;
        this.mRangeMinValue = 0;
        this.mRangeMaxValue = 0;
        this.mRangeValue = 0;
        this.mLastSentRangeUpdate = 0;
        this.mHandler = null;
        ProgressBar progressBar = this.mRangeBar;
        if (progressBar != null) {
            if (this.mStartItem == null) {
                removeView(progressBar);
            } else {
                this.mContent.removeView(progressBar);
            }
            this.mRangeBar = null;
        }
        this.mSubContent.setVisibility(0);
        this.mStartItem = null;
        this.mActionSpinner.setVisibility(8);
        Spinner spinner = this.mSelectionSpinner;
        if (spinner != null) {
            removeView(spinner);
            this.mSelectionSpinner = null;
        }
        this.mSelectionItem = null;
    }
}
