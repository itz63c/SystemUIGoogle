package androidx.leanback.widget;

import android.util.SparseArray;

public class SparseArrayObjectAdapter extends ObjectAdapter {
    private SparseArray<Object> mItems = new SparseArray<>();

    public boolean isImmediateNotifySupported() {
        return true;
    }

    public int size() {
        return this.mItems.size();
    }

    public Object get(int i) {
        return this.mItems.valueAt(i);
    }

    public void set(int i, Object obj) {
        int indexOfKey = this.mItems.indexOfKey(i);
        if (indexOfKey < 0) {
            this.mItems.append(i, obj);
            notifyItemRangeInserted(this.mItems.indexOfKey(i), 1);
        } else if (this.mItems.valueAt(indexOfKey) != obj) {
            this.mItems.setValueAt(indexOfKey, obj);
            notifyItemRangeChanged(indexOfKey, 1);
        }
    }
}
