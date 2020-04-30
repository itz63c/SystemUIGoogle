package androidx.leanback.widget;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.leanback.R$layout;
import androidx.leanback.widget.Presenter.ViewHolder;

public class DividerPresenter extends Presenter {
    private final int mLayoutResourceId;

    public void onBindViewHolder(ViewHolder viewHolder, Object obj) {
    }

    public void onUnbindViewHolder(ViewHolder viewHolder) {
    }

    public DividerPresenter() {
        this(R$layout.lb_divider);
    }

    public DividerPresenter(int i) {
        this.mLayoutResourceId = i;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(this.mLayoutResourceId, viewGroup, false));
    }
}
