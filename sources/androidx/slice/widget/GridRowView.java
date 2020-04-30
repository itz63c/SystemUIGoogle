package androidx.slice.widget;

import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.slice.SliceItem;
import androidx.slice.core.SliceQuery;
import androidx.slice.view.R$dimen;
import androidx.slice.view.R$layout;
import androidx.slice.widget.GridContent.CellContent;
import androidx.slice.widget.SliceView.OnSliceActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GridRowView extends SliceChildView implements OnClickListener, OnTouchListener {
    private static final int TEXT_LAYOUT = R$layout.abc_slice_secondary_text;
    private static final int TITLE_TEXT_LAYOUT = R$layout.abc_slice_title;
    private View mForeground;
    private GridContent mGridContent;
    private int mGutter;
    private int mIconSize;
    private int mLargeImageHeight;
    private int[] mLoc;
    boolean mMaxCellUpdateScheduled;
    int mMaxCells;
    private OnPreDrawListener mMaxCellsUpdater;
    private int mRowCount;
    private int mRowIndex;
    private int mSmallImageMinWidth;
    private int mSmallImageSize;
    private int mTextPadding;
    private LinearLayout mViewContainer;

    public GridRowView(Context context) {
        this(context, null);
    }

    public GridRowView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mMaxCells = -1;
        this.mLoc = new int[2];
        this.mMaxCellsUpdater = new OnPreDrawListener() {
            public boolean onPreDraw() {
                GridRowView gridRowView = GridRowView.this;
                gridRowView.mMaxCells = gridRowView.getMaxCells();
                GridRowView.this.populateViews();
                GridRowView.this.getViewTreeObserver().removeOnPreDrawListener(this);
                GridRowView.this.mMaxCellUpdateScheduled = false;
                return true;
            }
        };
        Resources resources = getContext().getResources();
        LinearLayout linearLayout = new LinearLayout(getContext());
        this.mViewContainer = linearLayout;
        linearLayout.setOrientation(0);
        addView(this.mViewContainer, new LayoutParams(-1, -1));
        this.mViewContainer.setGravity(16);
        this.mIconSize = resources.getDimensionPixelSize(R$dimen.abc_slice_icon_size);
        this.mSmallImageSize = resources.getDimensionPixelSize(R$dimen.abc_slice_small_image_size);
        this.mLargeImageHeight = resources.getDimensionPixelSize(R$dimen.abc_slice_grid_image_only_height);
        this.mSmallImageMinWidth = resources.getDimensionPixelSize(R$dimen.abc_slice_grid_image_min_width);
        this.mGutter = resources.getDimensionPixelSize(R$dimen.abc_slice_grid_gutter);
        this.mTextPadding = resources.getDimensionPixelSize(R$dimen.abc_slice_grid_text_padding);
        View view = new View(getContext());
        this.mForeground = view;
        addView(view, new LayoutParams(-1, -1));
    }

    public void setInsets(int i, int i2, int i3, int i4) {
        super.setInsets(i, i2, i3, i4);
        this.mViewContainer.setPadding(i, i2 + getExtraTopPadding(), i3, i4 + getExtraBottomPadding());
    }

    private int getExtraTopPadding() {
        GridContent gridContent = this.mGridContent;
        if (gridContent == null || !gridContent.isAllImages() || this.mRowIndex != 0) {
            return 0;
        }
        SliceStyle sliceStyle = this.mSliceStyle;
        if (sliceStyle != null) {
            return sliceStyle.getGridTopPadding();
        }
        return 0;
    }

    private int getExtraBottomPadding() {
        GridContent gridContent = this.mGridContent;
        if (gridContent == null || !gridContent.isAllImages()) {
            return 0;
        }
        if (this.mRowIndex != this.mRowCount - 1 && getMode() != 1) {
            return 0;
        }
        SliceStyle sliceStyle = this.mSliceStyle;
        if (sliceStyle != null) {
            return sliceStyle.getGridBottomPadding();
        }
        return 0;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        int height = this.mGridContent.getHeight(this.mSliceStyle, this.mViewPolicy) + this.mInsetTop + this.mInsetBottom;
        int makeMeasureSpec = MeasureSpec.makeMeasureSpec(height, 1073741824);
        this.mViewContainer.getLayoutParams().height = height;
        super.onMeasure(i, makeMeasureSpec);
    }

    public void setTint(int i) {
        super.setTint(i);
        if (this.mGridContent != null) {
            resetView();
            populateViews();
        }
    }

    public void setSliceItem(SliceContent sliceContent, boolean z, int i, int i2, OnSliceActionListener onSliceActionListener) {
        resetView();
        setSliceActionListener(onSliceActionListener);
        this.mRowIndex = i;
        this.mRowCount = i2;
        this.mGridContent = (GridContent) sliceContent;
        if (!scheduleMaxCellsUpdate()) {
            populateViews();
        }
        this.mViewContainer.setPadding(this.mInsetStart, this.mInsetTop + getExtraTopPadding(), this.mInsetEnd, this.mInsetBottom + getExtraBottomPadding());
    }

    private boolean scheduleMaxCellsUpdate() {
        GridContent gridContent = this.mGridContent;
        if (gridContent == null || !gridContent.isValid()) {
            return true;
        }
        if (getWidth() == 0) {
            this.mMaxCellUpdateScheduled = true;
            getViewTreeObserver().addOnPreDrawListener(this.mMaxCellsUpdater);
            return true;
        }
        this.mMaxCells = getMaxCells();
        return false;
    }

    /* access modifiers changed from: 0000 */
    public int getMaxCells() {
        GridContent gridContent = this.mGridContent;
        if (gridContent == null || !gridContent.isValid() || getWidth() == 0) {
            return -1;
        }
        int i = 1;
        if (this.mGridContent.getGridContent().size() > 1) {
            i = getWidth() / ((this.mGridContent.getLargestImageMode() == 2 ? this.mLargeImageHeight : this.mSmallImageMinWidth) + this.mGutter);
        }
        return i;
    }

    /* access modifiers changed from: 0000 */
    public void populateViews() {
        GridContent gridContent = this.mGridContent;
        if (gridContent == null || !gridContent.isValid()) {
            resetView();
        } else if (!scheduleMaxCellsUpdate()) {
            if (this.mGridContent.getLayoutDir() != -1) {
                setLayoutDirection(this.mGridContent.getLayoutDir());
            }
            boolean z = true;
            if (this.mGridContent.getContentIntent() != null) {
                this.mViewContainer.setTag(new Pair(this.mGridContent.getContentIntent(), new EventInfo(getMode(), 3, 1, this.mRowIndex)));
                makeEntireGridClickable(true);
            }
            CharSequence contentDescription = this.mGridContent.getContentDescription();
            if (contentDescription != null) {
                this.mViewContainer.setContentDescription(contentDescription);
            }
            ArrayList gridContent2 = this.mGridContent.getGridContent();
            if (this.mGridContent.getLargestImageMode() == 2) {
                this.mViewContainer.setGravity(48);
            } else {
                this.mViewContainer.setGravity(16);
            }
            int i = this.mMaxCells;
            int i2 = 0;
            if (this.mGridContent.getSeeMoreItem() == null) {
                z = false;
            }
            while (true) {
                if (i2 >= gridContent2.size()) {
                    break;
                } else if (this.mViewContainer.getChildCount() < i) {
                    addCell((CellContent) gridContent2.get(i2), i2, Math.min(gridContent2.size(), i));
                    i2++;
                } else if (z) {
                    addSeeMoreCount(gridContent2.size() - i);
                }
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x0035, code lost:
        if ("action".equals(r1.getFormat()) != false) goto L_0x0037;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void addSeeMoreCount(int r12) {
        /*
            r11 = this;
            android.widget.LinearLayout r0 = r11.mViewContainer
            int r1 = r0.getChildCount()
            r2 = 1
            int r1 = r1 - r2
            android.view.View r0 = r0.getChildAt(r1)
            android.widget.LinearLayout r1 = r11.mViewContainer
            r1.removeView(r0)
            androidx.slice.widget.GridContent r1 = r11.mGridContent
            androidx.slice.SliceItem r1 = r1.getSeeMoreItem()
            android.widget.LinearLayout r3 = r11.mViewContainer
            int r3 = r3.getChildCount()
            int r4 = r11.mMaxCells
            java.lang.String r5 = r1.getFormat()
            java.lang.String r6 = "slice"
            boolean r5 = r6.equals(r5)
            if (r5 != 0) goto L_0x0037
            java.lang.String r5 = r1.getFormat()
            java.lang.String r6 = "action"
            boolean r5 = r6.equals(r5)
            if (r5 == 0) goto L_0x004e
        L_0x0037:
            androidx.slice.Slice r5 = r1.getSlice()
            java.util.List r5 = r5.getItems()
            int r5 = r5.size()
            if (r5 <= 0) goto L_0x004e
            androidx.slice.widget.GridContent$CellContent r12 = new androidx.slice.widget.GridContent$CellContent
            r12.<init>(r1)
            r11.addCell(r12, r3, r4)
            return
        L_0x004e:
            android.content.Context r5 = r11.getContext()
            android.view.LayoutInflater r5 = android.view.LayoutInflater.from(r5)
            androidx.slice.widget.GridContent r6 = r11.mGridContent
            boolean r6 = r6.isAllImages()
            r7 = -1
            r8 = 0
            if (r6 == 0) goto L_0x007b
            int r6 = androidx.slice.view.R$layout.abc_slice_grid_see_more_overlay
            android.widget.LinearLayout r9 = r11.mViewContainer
            android.view.View r5 = r5.inflate(r6, r9, r8)
            android.widget.FrameLayout r5 = (android.widget.FrameLayout) r5
            android.widget.FrameLayout$LayoutParams r6 = new android.widget.FrameLayout$LayoutParams
            r6.<init>(r7, r7)
            r5.addView(r0, r8, r6)
            int r0 = androidx.slice.view.R$id.text_see_more_count
            android.view.View r0 = r5.findViewById(r0)
            android.widget.TextView r0 = (android.widget.TextView) r0
            goto L_0x00ab
        L_0x007b:
            int r0 = androidx.slice.view.R$layout.abc_slice_grid_see_more
            android.widget.LinearLayout r6 = r11.mViewContainer
            android.view.View r0 = r5.inflate(r0, r6, r8)
            r5 = r0
            android.widget.LinearLayout r5 = (android.widget.LinearLayout) r5
            int r0 = androidx.slice.view.R$id.text_see_more_count
            android.view.View r0 = r5.findViewById(r0)
            android.widget.TextView r0 = (android.widget.TextView) r0
            int r6 = androidx.slice.view.R$id.text_see_more
            android.view.View r6 = r5.findViewById(r6)
            android.widget.TextView r6 = (android.widget.TextView) r6
            androidx.slice.widget.SliceStyle r9 = r11.mSliceStyle
            if (r9 == 0) goto L_0x00ab
            int r9 = r9.getGridTitleSize()
            float r9 = (float) r9
            r6.setTextSize(r8, r9)
            androidx.slice.widget.SliceStyle r9 = r11.mSliceStyle
            int r9 = r9.getTitleColor()
            r6.setTextColor(r9)
        L_0x00ab:
            android.widget.LinearLayout r6 = r11.mViewContainer
            android.widget.LinearLayout$LayoutParams r9 = new android.widget.LinearLayout$LayoutParams
            r10 = 1065353216(0x3f800000, float:1.0)
            r9.<init>(r8, r7, r10)
            r6.addView(r5, r9)
            android.content.res.Resources r6 = r11.getResources()
            int r7 = androidx.slice.view.R$string.abc_slice_more_content
            java.lang.Object[] r9 = new java.lang.Object[r2]
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
            r9[r8] = r12
            java.lang.String r12 = r6.getString(r7, r9)
            r0.setText(r12)
            androidx.slice.widget.EventInfo r12 = new androidx.slice.widget.EventInfo
            int r0 = r11.getMode()
            r6 = 4
            int r7 = r11.mRowIndex
            r12.<init>(r0, r6, r2, r7)
            r0 = 2
            r12.setPosition(r0, r3, r4)
            android.util.Pair r0 = new android.util.Pair
            r0.<init>(r1, r12)
            r5.setTag(r0)
            r11.makeClickable(r5, r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.slice.widget.GridRowView.addSeeMoreCount(int):void");
    }

    private void addCell(CellContent cellContent, int i, int i2) {
        List list;
        List list2;
        int i3;
        String str;
        int i4;
        int i5;
        SliceItem sliceItem;
        int i6 = i;
        int i7 = i2;
        int i8 = (getMode() != 1 || !this.mGridContent.hasImage()) ? 2 : 1;
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(1);
        linearLayout.setGravity(1);
        ArrayList cellItems = cellContent.getCellItems();
        SliceItem contentIntent = cellContent.getContentIntent();
        boolean z = cellItems.size() == 1;
        String str2 = "text";
        if (z || getMode() != 1) {
            list = null;
        } else {
            ArrayList arrayList = new ArrayList();
            Iterator it = cellItems.iterator();
            while (it.hasNext()) {
                SliceItem sliceItem2 = (SliceItem) it.next();
                if (str2.equals(sliceItem2.getFormat())) {
                    arrayList.add(sliceItem2);
                }
            }
            Iterator it2 = arrayList.iterator();
            while (arrayList.size() > i8) {
                if (!((SliceItem) it2.next()).hasAnyHints("title", "large")) {
                    it2.remove();
                }
            }
            list = arrayList;
        }
        int i9 = 0;
        int i10 = 0;
        int i11 = 0;
        SliceItem sliceItem3 = null;
        boolean z2 = false;
        while (i11 < cellItems.size()) {
            SliceItem sliceItem4 = (SliceItem) cellItems.get(i11);
            String format = sliceItem4.getFormat();
            int determinePadding = determinePadding(sliceItem3);
            if (i10 >= i8 || (!str2.equals(format) && !"long".equals(format))) {
                i5 = i9;
                i4 = i10;
                i3 = i11;
                sliceItem = sliceItem3;
                list2 = list;
                str = str2;
                if (i5 < 1) {
                    if ("image".equals(sliceItem4.getFormat())) {
                        if (addItem(sliceItem4, this.mTintColor, linearLayout, 0, z)) {
                            i9 = i5 + 1;
                            sliceItem3 = sliceItem4;
                            i10 = i4;
                        }
                    }
                }
                sliceItem3 = sliceItem;
                i9 = i5;
                i10 = i4;
                i11 = i3 + 1;
                str2 = str;
                list = list2;
            } else if (list == null || list.contains(sliceItem4)) {
                i5 = i9;
                i4 = i10;
                i3 = i11;
                sliceItem = sliceItem3;
                list2 = list;
                int i12 = determinePadding;
                str = str2;
                if (addItem(sliceItem4, this.mTintColor, linearLayout, i12, z)) {
                    i10 = i4 + 1;
                    sliceItem3 = sliceItem4;
                    i9 = i5;
                }
                sliceItem3 = sliceItem;
                i9 = i5;
                i10 = i4;
                i11 = i3 + 1;
                str2 = str;
                list = list2;
            } else {
                i5 = i9;
                i4 = i10;
                i3 = i11;
                sliceItem = sliceItem3;
                list2 = list;
                str = str2;
                sliceItem3 = sliceItem;
                i9 = i5;
                i10 = i4;
                i11 = i3 + 1;
                str2 = str;
                list = list2;
            }
            z2 = true;
            i11 = i3 + 1;
            str2 = str;
            list = list2;
        }
        if (z2) {
            CharSequence contentDescription = cellContent.getContentDescription();
            if (contentDescription != null) {
                linearLayout.setContentDescription(contentDescription);
            }
            this.mViewContainer.addView(linearLayout, new LinearLayout.LayoutParams(0, -2, 1.0f));
            if (i6 != i7 - 1) {
                MarginLayoutParams marginLayoutParams = (MarginLayoutParams) linearLayout.getLayoutParams();
                marginLayoutParams.setMarginEnd(this.mGutter);
                linearLayout.setLayoutParams(marginLayoutParams);
            }
            if (contentIntent != null) {
                EventInfo eventInfo = new EventInfo(getMode(), 1, 1, this.mRowIndex);
                eventInfo.setPosition(2, i6, i7);
                linearLayout.setTag(new Pair(contentIntent, eventInfo));
                makeClickable(linearLayout, true);
            }
        }
    }

    private boolean addItem(SliceItem sliceItem, int i, ViewGroup viewGroup, int i2, boolean z) {
        CharSequence charSequence;
        LinearLayout.LayoutParams layoutParams;
        LinearLayout.LayoutParams layoutParams2;
        int i3;
        String format = sliceItem.getFormat();
        String str = "long";
        String str2 = "large";
        Object obj = 0;
        if ("text".equals(format) || str.equals(format)) {
            boolean hasAnyHints = SliceQuery.hasAnyHints(sliceItem, str2, "title");
            TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(hasAnyHints ? TITLE_TEXT_LAYOUT : TEXT_LAYOUT, null);
            SliceStyle sliceStyle = this.mSliceStyle;
            if (sliceStyle != null) {
                textView.setTextSize(0, (float) (hasAnyHints ? sliceStyle.getGridTitleSize() : sliceStyle.getGridSubtitleSize()));
                textView.setTextColor(hasAnyHints ? this.mSliceStyle.getTitleColor() : this.mSliceStyle.getSubtitleColor());
            }
            if (str.equals(format)) {
                charSequence = SliceViewUtil.getTimestampString(getContext(), sliceItem.getLong());
            } else {
                charSequence = sliceItem.getSanitizedText();
            }
            textView.setText(charSequence);
            viewGroup.addView(textView);
            textView.setPadding(0, i2, 0, 0);
            obj = textView;
        } else if ("image".equals(format) && sliceItem.getIcon() != null) {
            Drawable loadDrawable = sliceItem.getIcon().loadDrawable(getContext());
            if (loadDrawable != null) {
                ImageView imageView = new ImageView(getContext());
                imageView.setImageDrawable(loadDrawable);
                String str3 = "no_tint";
                if (sliceItem.hasHint("raw")) {
                    imageView.setScaleType(ScaleType.CENTER_INSIDE);
                    layoutParams = new LinearLayout.LayoutParams(-1, -2);
                } else {
                    if (sliceItem.hasHint(str2)) {
                        imageView.setScaleType(ScaleType.CENTER_CROP);
                        if (z) {
                            i3 = -1;
                        } else {
                            i3 = this.mLargeImageHeight;
                        }
                        layoutParams2 = new LinearLayout.LayoutParams(-1, i3);
                    } else {
                        boolean z2 = !sliceItem.hasHint(str3);
                        int i4 = !z2 ? this.mSmallImageSize : this.mIconSize;
                        imageView.setScaleType(z2 ? ScaleType.CENTER_INSIDE : ScaleType.CENTER_CROP);
                        layoutParams2 = new LinearLayout.LayoutParams(i4, i4);
                    }
                    layoutParams = layoutParams2;
                }
                if (i != -1 && !sliceItem.hasHint(str3)) {
                    imageView.setColorFilter(i);
                }
                viewGroup.addView(imageView, layoutParams);
                obj = imageView;
            }
        }
        if (obj != 0) {
            return true;
        }
        return false;
    }

    private int determinePadding(SliceItem sliceItem) {
        int i = 0;
        if (sliceItem == null) {
            return 0;
        }
        if ("image".equals(sliceItem.getFormat())) {
            return this.mTextPadding;
        }
        if (!"text".equals(sliceItem.getFormat())) {
            if (!"long".equals(sliceItem.getFormat())) {
                return 0;
            }
        }
        SliceStyle sliceStyle = this.mSliceStyle;
        if (sliceStyle != null) {
            i = sliceStyle.getVerticalGridTextPadding();
        }
        return i;
    }

    private void makeEntireGridClickable(boolean z) {
        Drawable drawable = null;
        this.mViewContainer.setOnTouchListener(z ? this : null);
        this.mViewContainer.setOnClickListener(z ? this : null);
        View view = this.mForeground;
        if (z) {
            drawable = SliceViewUtil.getDrawable(getContext(), 16843534);
        }
        view.setBackground(drawable);
        this.mViewContainer.setClickable(z);
    }

    private void makeClickable(View view, boolean z) {
        Drawable drawable = null;
        view.setOnClickListener(z ? this : null);
        int i = 16843534;
        if (VERSION.SDK_INT >= 21) {
            i = 16843868;
        }
        if (z) {
            drawable = SliceViewUtil.getDrawable(getContext(), i);
        }
        view.setBackground(drawable);
        view.setClickable(z);
    }

    public void onClick(View view) {
        Pair pair = (Pair) view.getTag();
        SliceItem sliceItem = (SliceItem) pair.first;
        EventInfo eventInfo = (EventInfo) pair.second;
        if (sliceItem != null) {
            SliceItem find = SliceQuery.find(sliceItem, "action", (String) null, (String) null);
            if (find != null) {
                try {
                    find.fireAction(null, null);
                    if (this.mObserver != null) {
                        this.mObserver.onSliceAction(eventInfo, find);
                    }
                } catch (CanceledException e) {
                    Log.e("GridRowView", "PendingIntent for slice cannot be sent", e);
                }
            }
        }
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        onForegroundActivated(motionEvent);
        return false;
    }

    private void onForegroundActivated(MotionEvent motionEvent) {
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
    }

    public void resetView() {
        if (this.mMaxCellUpdateScheduled) {
            this.mMaxCellUpdateScheduled = false;
            getViewTreeObserver().removeOnPreDrawListener(this.mMaxCellsUpdater);
        }
        this.mViewContainer.removeAllViews();
        setLayoutDirection(2);
        makeEntireGridClickable(false);
    }
}
