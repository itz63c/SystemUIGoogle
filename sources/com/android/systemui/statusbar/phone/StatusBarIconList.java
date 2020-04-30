package com.android.systemui.statusbar.phone;

import com.android.internal.annotations.VisibleForTesting;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StatusBarIconList {
    private ArrayList<Slot> mSlots = new ArrayList<>();

    public static class Slot {
        private StatusBarIconHolder mHolder;
        private final String mName;
        private ArrayList<StatusBarIconHolder> mSubSlots;

        public Slot(String str, StatusBarIconHolder statusBarIconHolder) {
            this.mName = str;
            this.mHolder = statusBarIconHolder;
        }

        public String getName() {
            return this.mName;
        }

        public StatusBarIconHolder getHolderForTag(int i) {
            if (i == 0) {
                return this.mHolder;
            }
            ArrayList<StatusBarIconHolder> arrayList = this.mSubSlots;
            if (arrayList != null) {
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    StatusBarIconHolder statusBarIconHolder = (StatusBarIconHolder) it.next();
                    if (statusBarIconHolder.getTag() == i) {
                        return statusBarIconHolder;
                    }
                }
            }
            return null;
        }

        public void addHolder(StatusBarIconHolder statusBarIconHolder) {
            int tag = statusBarIconHolder.getTag();
            if (tag == 0) {
                this.mHolder = statusBarIconHolder;
            } else {
                setSubSlot(statusBarIconHolder, tag);
            }
        }

        public void removeForTag(int i) {
            if (i == 0) {
                this.mHolder = null;
                return;
            }
            int indexForTag = getIndexForTag(i);
            if (indexForTag != -1) {
                this.mSubSlots.remove(indexForTag);
            }
        }

        @VisibleForTesting
        public void clear() {
            this.mHolder = null;
            if (this.mSubSlots != null) {
                this.mSubSlots = null;
            }
        }

        private void setSubSlot(StatusBarIconHolder statusBarIconHolder, int i) {
            if (this.mSubSlots == null) {
                ArrayList<StatusBarIconHolder> arrayList = new ArrayList<>();
                this.mSubSlots = arrayList;
                arrayList.add(statusBarIconHolder);
            } else if (getIndexForTag(i) == -1) {
                this.mSubSlots.add(statusBarIconHolder);
            }
        }

        private int getIndexForTag(int i) {
            for (int i2 = 0; i2 < this.mSubSlots.size(); i2++) {
                if (((StatusBarIconHolder) this.mSubSlots.get(i2)).getTag() == i) {
                    return i2;
                }
            }
            return -1;
        }

        public boolean hasIconsInSlot() {
            boolean z = true;
            if (this.mHolder != null) {
                return true;
            }
            ArrayList<StatusBarIconHolder> arrayList = this.mSubSlots;
            if (arrayList == null) {
                return false;
            }
            if (arrayList.size() <= 0) {
                z = false;
            }
            return z;
        }

        public int numberOfIcons() {
            int i = this.mHolder == null ? 0 : 1;
            ArrayList<StatusBarIconHolder> arrayList = this.mSubSlots;
            if (arrayList == null) {
                return i;
            }
            return i + arrayList.size();
        }

        public int viewIndexOffsetForTag(int i) {
            ArrayList<StatusBarIconHolder> arrayList = this.mSubSlots;
            if (arrayList == null) {
                return 0;
            }
            int size = arrayList.size();
            if (i == 0) {
                return size;
            }
            return (size - getIndexForTag(i)) - 1;
        }

        public List<StatusBarIconHolder> getHolderListInViewOrder() {
            ArrayList arrayList = new ArrayList();
            ArrayList<StatusBarIconHolder> arrayList2 = this.mSubSlots;
            if (arrayList2 != null) {
                for (int size = arrayList2.size() - 1; size >= 0; size--) {
                    arrayList.add((StatusBarIconHolder) this.mSubSlots.get(size));
                }
            }
            StatusBarIconHolder statusBarIconHolder = this.mHolder;
            if (statusBarIconHolder != null) {
                arrayList.add(statusBarIconHolder);
            }
            return arrayList;
        }

        public List<StatusBarIconHolder> getHolderList() {
            ArrayList arrayList = new ArrayList();
            StatusBarIconHolder statusBarIconHolder = this.mHolder;
            if (statusBarIconHolder != null) {
                arrayList.add(statusBarIconHolder);
            }
            ArrayList<StatusBarIconHolder> arrayList2 = this.mSubSlots;
            if (arrayList2 != null) {
                arrayList.addAll(arrayList2);
            }
            return arrayList;
        }

        public String toString() {
            return String.format("(%s) %s", new Object[]{this.mName, subSlotsString()});
        }

        private String subSlotsString() {
            String str = "";
            if (this.mSubSlots == null) {
                return str;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append(this.mSubSlots.size());
            sb.append(" subSlots");
            return sb.toString();
        }
    }

    public StatusBarIconList(String[] strArr) {
        for (String slot : strArr) {
            this.mSlots.add(new Slot(slot, null));
        }
    }

    public int getSlotIndex(String str) {
        int size = this.mSlots.size();
        for (int i = 0; i < size; i++) {
            if (((Slot) this.mSlots.get(i)).getName().equals(str)) {
                return i;
            }
        }
        this.mSlots.add(0, new Slot(str, null));
        return 0;
    }

    /* access modifiers changed from: protected */
    public ArrayList<Slot> getSlots() {
        return new ArrayList<>(this.mSlots);
    }

    /* access modifiers changed from: protected */
    public Slot getSlot(String str) {
        return (Slot) this.mSlots.get(getSlotIndex(str));
    }

    public void setIcon(int i, StatusBarIconHolder statusBarIconHolder) {
        ((Slot) this.mSlots.get(i)).addHolder(statusBarIconHolder);
    }

    public void removeIcon(int i, int i2) {
        ((Slot) this.mSlots.get(i)).removeForTag(i2);
    }

    public String getSlotName(int i) {
        return ((Slot) this.mSlots.get(i)).getName();
    }

    public StatusBarIconHolder getIcon(int i, int i2) {
        return ((Slot) this.mSlots.get(i)).getHolderForTag(i2);
    }

    public int getViewIndex(int i, int i2) {
        int i3 = 0;
        for (int i4 = 0; i4 < i; i4++) {
            Slot slot = (Slot) this.mSlots.get(i4);
            if (slot.hasIconsInSlot()) {
                i3 += slot.numberOfIcons();
            }
        }
        return i3 + ((Slot) this.mSlots.get(i)).viewIndexOffsetForTag(i2);
    }

    public void dump(PrintWriter printWriter) {
        printWriter.println("StatusBarIconList state:");
        int size = this.mSlots.size();
        StringBuilder sb = new StringBuilder();
        sb.append("  icon slots: ");
        sb.append(size);
        printWriter.println(sb.toString());
        for (int i = 0; i < size; i++) {
            printWriter.printf("    %2d:%s\n", new Object[]{Integer.valueOf(i), ((Slot) this.mSlots.get(i)).toString()});
        }
    }
}
