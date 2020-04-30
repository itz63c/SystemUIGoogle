package androidx.slice.widget;

import androidx.slice.SliceItem;
import androidx.slice.SliceUtils;
import androidx.slice.core.SliceQuery;
import java.util.ArrayList;
import java.util.List;

public class GridContent extends SliceContent {
    private boolean mAllImages;
    private ArrayList<CellContent> mGridContent = new ArrayList<>();
    private boolean mHasImage;
    private boolean mIsLastIndex;
    private int mLargestImageMode = 5;
    private int mMaxCellLineCount;
    private SliceItem mPrimaryAction;
    private SliceItem mSeeMoreItem;
    private SliceItem mTitleItem;

    public static class CellContent {
        private ArrayList<SliceItem> mCellItems = new ArrayList<>();
        private SliceItem mContentDescr;
        private SliceItem mContentIntent;
        private boolean mHasImage;
        private int mImageMode = -1;
        private int mTextCount;
        private SliceItem mTitleItem;

        public CellContent(SliceItem sliceItem) {
            populate(sliceItem);
        }

        public boolean populate(SliceItem sliceItem) {
            String format = sliceItem.getFormat();
            if (!sliceItem.hasHint("shortcut")) {
                String str = "slice";
                String str2 = "action";
                if (str.equals(format) || str2.equals(format)) {
                    List items = sliceItem.getSlice().getItems();
                    if (items.size() == 1 && (str2.equals(((SliceItem) items.get(0)).getFormat()) || str.equals(((SliceItem) items.get(0)).getFormat()))) {
                        this.mContentIntent = (SliceItem) items.get(0);
                        items = ((SliceItem) items.get(0)).getSlice().getItems();
                    }
                    if (str2.equals(format)) {
                        this.mContentIntent = sliceItem;
                    }
                    this.mTextCount = 0;
                    int i = 0;
                    for (int i2 = 0; i2 < items.size(); i2++) {
                        SliceItem sliceItem2 = (SliceItem) items.get(i2);
                        String format2 = sliceItem2.getFormat();
                        if ("content_description".equals(sliceItem2.getSubType())) {
                            this.mContentDescr = sliceItem2;
                        } else if (this.mTextCount < 2 && ("text".equals(format2) || "long".equals(format2))) {
                            this.mTextCount++;
                            this.mCellItems.add(sliceItem2);
                            SliceItem sliceItem3 = this.mTitleItem;
                            if (sliceItem3 != null) {
                                String str3 = "title";
                                if (!sliceItem3.hasHint(str3)) {
                                    if (!sliceItem2.hasHint(str3)) {
                                    }
                                }
                            }
                            this.mTitleItem = sliceItem2;
                        } else if (i < 1) {
                            if ("image".equals(sliceItem2.getFormat())) {
                                this.mImageMode = SliceUtils.parseImageMode(sliceItem2);
                                i++;
                                this.mHasImage = true;
                                this.mCellItems.add(sliceItem2);
                            }
                        }
                    }
                    return isValid();
                }
            }
            if (isValidCellContent(sliceItem)) {
                this.mCellItems.add(sliceItem);
            }
            return isValid();
        }

        public SliceItem getTitleItem() {
            return this.mTitleItem;
        }

        public SliceItem getContentIntent() {
            return this.mContentIntent;
        }

        public ArrayList<SliceItem> getCellItems() {
            return this.mCellItems;
        }

        private boolean isValidCellContent(SliceItem sliceItem) {
            String format = sliceItem.getFormat();
            if ("content_description".equals(sliceItem.getSubType()) || sliceItem.hasAnyHints("keywords", "ttl", "last_updated")) {
                return false;
            }
            if ("text".equals(format) || "long".equals(format) || "image".equals(format)) {
                return true;
            }
            return false;
        }

        public boolean isValid() {
            return this.mCellItems.size() > 0 && this.mCellItems.size() <= 3;
        }

        public boolean isImageOnly() {
            if (this.mCellItems.size() != 1) {
                return false;
            }
            return "image".equals(((SliceItem) this.mCellItems.get(0)).getFormat());
        }

        public int getTextCount() {
            return this.mTextCount;
        }

        public boolean hasImage() {
            return this.mHasImage;
        }

        public int getImageMode() {
            return this.mImageMode;
        }

        public CharSequence getContentDescription() {
            SliceItem sliceItem = this.mContentDescr;
            if (sliceItem != null) {
                return sliceItem.getText();
            }
            return null;
        }
    }

    public GridContent(SliceItem sliceItem, int i) {
        super(sliceItem, i);
        populate(sliceItem);
    }

    private boolean populate(SliceItem sliceItem) {
        SliceItem find = SliceQuery.find(sliceItem, (String) null, "see_more", (String) null);
        this.mSeeMoreItem = find;
        String str = "slice";
        if (find != null && str.equals(find.getFormat())) {
            List items = this.mSeeMoreItem.getSlice().getItems();
            if (items != null && items.size() > 0) {
                this.mSeeMoreItem = (SliceItem) items.get(0);
            }
        }
        this.mPrimaryAction = SliceQuery.find(sliceItem, str, new String[]{"shortcut", "title"}, new String[]{"actions"});
        this.mAllImages = true;
        if (str.equals(sliceItem.getFormat())) {
            List filterAndProcessItems = filterAndProcessItems(sliceItem.getSlice().getItems());
            for (int i = 0; i < filterAndProcessItems.size(); i++) {
                SliceItem sliceItem2 = (SliceItem) filterAndProcessItems.get(i);
                if (!"content_description".equals(sliceItem2.getSubType())) {
                    processContent(new CellContent(sliceItem2));
                }
            }
        } else {
            processContent(new CellContent(sliceItem));
        }
        return isValid();
    }

    private void processContent(CellContent cellContent) {
        int i;
        if (cellContent.isValid()) {
            if (this.mTitleItem == null && cellContent.getTitleItem() != null) {
                this.mTitleItem = cellContent.getTitleItem();
            }
            this.mGridContent.add(cellContent);
            if (!cellContent.isImageOnly()) {
                this.mAllImages = false;
            }
            this.mMaxCellLineCount = Math.max(this.mMaxCellLineCount, cellContent.getTextCount());
            this.mHasImage |= cellContent.hasImage();
            int i2 = this.mLargestImageMode;
            if (i2 == 5) {
                i = cellContent.getImageMode();
            } else {
                i = Math.max(i2, cellContent.getImageMode());
            }
            this.mLargestImageMode = i;
        }
    }

    public ArrayList<CellContent> getGridContent() {
        return this.mGridContent;
    }

    public SliceItem getContentIntent() {
        return this.mPrimaryAction;
    }

    public SliceItem getSeeMoreItem() {
        return this.mSeeMoreItem;
    }

    public boolean isValid() {
        return super.isValid() && this.mGridContent.size() > 0;
    }

    public boolean isAllImages() {
        return this.mAllImages;
    }

    public int getLargestImageMode() {
        return this.mLargestImageMode;
    }

    private List<SliceItem> filterAndProcessItems(List<SliceItem> list) {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            SliceItem sliceItem = (SliceItem) list.get(i);
            String str = "see_more";
            boolean z = true;
            if (!(SliceQuery.find(sliceItem, (String) null, str, (String) null) != null) && !sliceItem.hasAnyHints("shortcut", str, "keywords", "ttl", "last_updated")) {
                z = false;
            }
            if ("content_description".equals(sliceItem.getSubType())) {
                this.mContentDescr = sliceItem;
            } else if (!z) {
                arrayList.add(sliceItem);
            }
        }
        return arrayList;
    }

    public int getMaxCellLineCount() {
        return this.mMaxCellLineCount;
    }

    public boolean hasImage() {
        return this.mHasImage;
    }

    public boolean getIsLastIndex() {
        return this.mIsLastIndex;
    }

    public void setIsLastIndex(boolean z) {
        this.mIsLastIndex = z;
    }

    public int getHeight(SliceStyle sliceStyle, SliceViewPolicy sliceViewPolicy) {
        return sliceStyle.getGridHeight(this, sliceViewPolicy);
    }
}
