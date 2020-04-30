package androidx.leanback.widget;

import android.view.ViewGroup;

public class VerticalGridPresenter extends Presenter {

    public static class ViewHolder extends androidx.leanback.widget.Presenter.ViewHolder {
        public abstract VerticalGridView getGridView();
    }

    public final ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        throw null;
    }

    public abstract void setEntranceTransitionState(ViewHolder viewHolder, boolean z);
}
