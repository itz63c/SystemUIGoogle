package androidx.leanback.widget;

import androidx.leanback.widget.Presenter.ViewHolder;

public interface BaseOnItemViewSelectedListener<T> {
    void onItemSelected(ViewHolder viewHolder, Object obj, RowPresenter.ViewHolder viewHolder2, T t);
}
