package androidx.slice.widget;

import android.content.Context;
import androidx.slice.Slice;
import androidx.slice.SliceItem;
import androidx.slice.SliceMetadata;
import androidx.slice.core.SliceAction;
import androidx.slice.core.SliceActionImpl;
import androidx.slice.core.SliceQuery;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ListContent extends SliceContent {
    private RowContent mHeaderContent;
    private SliceAction mPrimaryAction;
    private ArrayList<SliceContent> mRowItems = new ArrayList<>();
    private RowContent mSeeMoreContent;
    private List<SliceAction> mSliceActions;

    public ListContent(Slice slice) {
        super(slice);
        if (this.mSliceItem != null) {
            populate(slice);
        }
    }

    @Deprecated
    public ListContent(Context context, Slice slice) {
        super(slice);
        if (this.mSliceItem != null) {
            populate(slice);
        }
    }

    private void populate(Slice slice) {
        if (slice != null) {
            this.mSliceActions = SliceMetadata.getSliceActions(slice);
            SliceItem findHeaderItem = findHeaderItem(slice);
            if (findHeaderItem != null) {
                RowContent rowContent = new RowContent(findHeaderItem, 0);
                this.mHeaderContent = rowContent;
                this.mRowItems.add(rowContent);
            }
            SliceItem seeMoreItem = getSeeMoreItem(slice);
            if (seeMoreItem != null) {
                this.mSeeMoreContent = new RowContent(seeMoreItem, -1);
            }
            List items = slice.getItems();
            for (int i = 0; i < items.size(); i++) {
                SliceItem sliceItem = (SliceItem) items.get(i);
                String format = sliceItem.getFormat();
                if (!sliceItem.hasAnyHints("actions", "see_more", "keywords", "ttl", "last_updated") && ("action".equals(format) || "slice".equals(format))) {
                    String str = "list_item";
                    if (this.mHeaderContent == null && !sliceItem.hasHint(str)) {
                        RowContent rowContent2 = new RowContent(sliceItem, 0);
                        this.mHeaderContent = rowContent2;
                        this.mRowItems.add(0, rowContent2);
                    } else if (sliceItem.hasHint(str)) {
                        if (sliceItem.hasHint("horizontal")) {
                            this.mRowItems.add(new GridContent(sliceItem, i));
                        } else {
                            this.mRowItems.add(new RowContent(sliceItem, i));
                        }
                    }
                }
            }
            if (this.mHeaderContent == null && this.mRowItems.size() >= 1) {
                RowContent rowContent3 = (RowContent) this.mRowItems.get(0);
                this.mHeaderContent = rowContent3;
                rowContent3.setIsHeader(true);
            }
            if (this.mRowItems.size() > 0) {
                ArrayList<SliceContent> arrayList = this.mRowItems;
                if (arrayList.get(arrayList.size() - 1) instanceof GridContent) {
                    ArrayList<SliceContent> arrayList2 = this.mRowItems;
                    ((GridContent) arrayList2.get(arrayList2.size() - 1)).setIsLastIndex(true);
                }
            }
            this.mPrimaryAction = findPrimaryAction();
        }
    }

    public int getHeight(SliceStyle sliceStyle, SliceViewPolicy sliceViewPolicy) {
        return sliceStyle.getListHeight(this, sliceViewPolicy);
    }

    public ArrayList<SliceContent> getRowItems(int i, SliceStyle sliceStyle, SliceViewPolicy sliceViewPolicy) {
        if (sliceViewPolicy.getMode() == 1) {
            return new ArrayList<>(Arrays.asList(new RowContent[]{getHeader()}));
        } else if (sliceViewPolicy.isScrollable() || i <= 0) {
            return getRowItems();
        } else {
            return sliceStyle.getListItemsForNonScrollingList(this, i, sliceViewPolicy);
        }
    }

    public boolean isValid() {
        return super.isValid() && this.mRowItems.size() > 0;
    }

    public RowContent getHeader() {
        return this.mHeaderContent;
    }

    public List<SliceAction> getSliceActions() {
        return this.mSliceActions;
    }

    public ArrayList<SliceContent> getRowItems() {
        return this.mRowItems;
    }

    public SliceContent getSeeMoreItem() {
        return this.mSeeMoreContent;
    }

    public int getHeaderTemplateType() {
        return getRowType(this.mHeaderContent, true, this.mSliceActions);
    }

    public SliceAction getShortcut(Context context) {
        SliceAction sliceAction = this.mPrimaryAction;
        return sliceAction != null ? sliceAction : super.getShortcut(context);
    }

    public void showTitleItems(boolean z) {
        RowContent rowContent = this.mHeaderContent;
        if (rowContent != null) {
            rowContent.showTitleItems(z);
        }
    }

    public void showHeaderDivider(boolean z) {
        if (this.mHeaderContent != null && this.mRowItems.size() > 1) {
            this.mHeaderContent.showBottomDivider(z);
        }
    }

    public void showActionDividers(boolean z) {
        Iterator it = this.mRowItems.iterator();
        while (it.hasNext()) {
            SliceContent sliceContent = (SliceContent) it.next();
            if (sliceContent instanceof RowContent) {
                ((RowContent) sliceContent).showActionDivider(z);
            }
        }
    }

    private SliceAction findPrimaryAction() {
        RowContent rowContent = this.mHeaderContent;
        SliceItem primaryAction = rowContent != null ? rowContent.getPrimaryAction() : null;
        String str = "action";
        if (primaryAction == null) {
            primaryAction = SliceQuery.find(this.mSliceItem, str, new String[]{"shortcut", "title"}, (String[]) null);
        }
        if (primaryAction == null) {
            primaryAction = SliceQuery.find(this.mSliceItem, str, (String) null, (String) null);
        }
        if (primaryAction != null) {
            return new SliceActionImpl(primaryAction);
        }
        return null;
    }

    public static int getRowType(SliceContent sliceContent, boolean z, List<SliceAction> list) {
        int i = 0;
        if (sliceContent != null) {
            if (sliceContent instanceof GridContent) {
                return 1;
            }
            RowContent rowContent = (RowContent) sliceContent;
            SliceItem primaryAction = rowContent.getPrimaryAction();
            SliceActionImpl sliceActionImpl = null;
            if (primaryAction != null) {
                sliceActionImpl = new SliceActionImpl(primaryAction);
            }
            if (rowContent.getRange() != null) {
                return "action".equals(rowContent.getRange().getFormat()) ? 4 : 5;
            } else if (rowContent.getSelection() != null) {
                return 6;
            } else {
                if (sliceActionImpl != null && sliceActionImpl.isToggle()) {
                    return 3;
                }
                if (z && list != null) {
                    for (int i2 = 0; i2 < list.size(); i2++) {
                        if (((SliceAction) list.get(i2)).isToggle()) {
                            return 3;
                        }
                    }
                    return 0;
                } else if (rowContent.getToggleItems().size() > 0) {
                    i = 3;
                }
            }
        }
        return i;
    }

    public static int getListHeight(List<SliceContent> list, SliceStyle sliceStyle, SliceViewPolicy sliceViewPolicy) {
        if (list == null) {
            return 0;
        }
        SliceContent sliceContent = null;
        if (!list.isEmpty()) {
            sliceContent = (SliceContent) list.get(0);
        }
        if (list.size() == 1 && !sliceContent.getSliceItem().hasHint("horizontal")) {
            return sliceContent.getHeight(sliceStyle, sliceViewPolicy);
        }
        int i = 0;
        for (int i2 = 0; i2 < list.size(); i2++) {
            i += ((SliceContent) list.get(i2)).getHeight(sliceStyle, sliceViewPolicy);
        }
        return i;
    }

    private static SliceItem findHeaderItem(Slice slice) {
        SliceItem find = SliceQuery.find(slice, "slice", (String[]) null, new String[]{"list_item", "shortcut", "actions", "keywords", "ttl", "last_updated", "horizontal", "selection_option"});
        if (find == null || !isValidHeader(find)) {
            return null;
        }
        return find;
    }

    private static SliceItem getSeeMoreItem(Slice slice) {
        SliceItem findTopLevelItem = SliceQuery.findTopLevelItem(slice, null, null, new String[]{"see_more"}, null);
        if (findTopLevelItem != null) {
            if ("slice".equals(findTopLevelItem.getFormat())) {
                List items = findTopLevelItem.getSlice().getItems();
                if (items.size() == 1) {
                    if ("action".equals(((SliceItem) items.get(0)).getFormat())) {
                        findTopLevelItem = (SliceItem) items.get(0);
                    }
                }
                return findTopLevelItem;
            }
        }
        return null;
    }

    private static boolean isValidHeader(SliceItem sliceItem) {
        if (!"slice".equals(sliceItem.getFormat()) || sliceItem.hasAnyHints("actions", "keywords", "see_more") || SliceQuery.find(sliceItem, "text", (String) null, (String) null) == null) {
            return false;
        }
        return true;
    }
}
