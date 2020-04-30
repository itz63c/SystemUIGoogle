package androidx.viewpager2.adapter;

import android.widget.FrameLayout;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

public final class FragmentViewHolder extends ViewHolder {
    /* access modifiers changed from: 0000 */
    public FrameLayout getContainer() {
        return (FrameLayout) this.itemView;
    }
}
