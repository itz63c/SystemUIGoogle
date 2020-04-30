package androidx.leanback.widget;

import androidx.leanback.widget.Presenter.ViewHolder;

public interface BaseOnItemViewClickedListener<T> {
    void onItemClicked(ViewHolder viewHolder, Object obj, RowPresenter.ViewHolder viewHolder2, T t);
}
