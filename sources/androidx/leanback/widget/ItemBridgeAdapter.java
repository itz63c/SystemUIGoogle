package androidx.leanback.widget;

import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import androidx.leanback.widget.ObjectAdapter.DataObserver;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import java.util.ArrayList;
import java.util.List;

public class ItemBridgeAdapter extends Adapter implements FacetProviderAdapter {
    private ObjectAdapter mAdapter;
    private AdapterListener mAdapterListener;
    private DataObserver mDataObserver = new DataObserver() {
        public void onChanged() {
            ItemBridgeAdapter.this.notifyDataSetChanged();
        }

        public void onItemRangeChanged(int i, int i2) {
            ItemBridgeAdapter.this.notifyItemRangeChanged(i, i2);
        }

        public void onItemRangeInserted(int i, int i2) {
            ItemBridgeAdapter.this.notifyItemRangeInserted(i, i2);
        }

        public void onItemRangeRemoved(int i, int i2) {
            ItemBridgeAdapter.this.notifyItemRangeRemoved(i, i2);
        }
    };
    FocusHighlightHandler mFocusHighlight;
    private PresenterSelector mPresenterSelector;
    private ArrayList<Presenter> mPresenters = new ArrayList<>();
    Wrapper mWrapper;

    public static class AdapterListener {
        public void onAddPresenter(Presenter presenter, int i) {
        }

        public void onAttachedToWindow(ViewHolder viewHolder) {
        }

        public void onBind(ViewHolder viewHolder) {
        }

        public abstract void onCreate(ViewHolder viewHolder);

        public void onDetachedFromWindow(ViewHolder viewHolder) {
        }

        public void onUnbind(ViewHolder viewHolder) {
        }

        public void onBind(ViewHolder viewHolder, List list) {
            onBind(viewHolder);
        }
    }

    static final class ChainingFocusChangeListener implements OnFocusChangeListener {
        final OnFocusChangeListener mChainedListener;
        FocusHighlightHandler mFocusHighlight;
        boolean mHasWrapper;

        ChainingFocusChangeListener(OnFocusChangeListener onFocusChangeListener, boolean z, FocusHighlightHandler focusHighlightHandler) {
            this.mChainedListener = onFocusChangeListener;
            this.mHasWrapper = z;
            this.mFocusHighlight = focusHighlightHandler;
        }

        /* access modifiers changed from: 0000 */
        public void update(boolean z, FocusHighlightHandler focusHighlightHandler) {
            this.mHasWrapper = z;
            this.mFocusHighlight = focusHighlightHandler;
        }

        public void onFocusChange(View view, boolean z) {
            if (this.mHasWrapper) {
                view = (View) view.getParent();
            }
            this.mFocusHighlight.onItemFocused(view, z);
            OnFocusChangeListener onFocusChangeListener = this.mChainedListener;
            if (onFocusChangeListener != null) {
                onFocusChangeListener.onFocusChange(view, z);
            }
        }
    }

    public static class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder implements FacetProvider {
        Object mExtraObject;
        final androidx.leanback.widget.Presenter.ViewHolder mHolder;
        Object mItem;
        final Presenter mPresenter;

        public final Presenter getPresenter() {
            return this.mPresenter;
        }

        public final androidx.leanback.widget.Presenter.ViewHolder getViewHolder() {
            return this.mHolder;
        }

        public final Object getItem() {
            return this.mItem;
        }

        public final Object getExtraObject() {
            return this.mExtraObject;
        }

        public void setExtraObject(Object obj) {
            this.mExtraObject = obj;
        }

        public Object getFacet(Class<?> cls) {
            return this.mHolder.getFacet(cls);
        }

        ViewHolder(Presenter presenter, View view, androidx.leanback.widget.Presenter.ViewHolder viewHolder) {
            super(view);
            this.mPresenter = presenter;
            this.mHolder = viewHolder;
        }
    }

    public static abstract class Wrapper {
        public abstract View createWrapper(View view);

        public abstract void wrap(View view, View view2);
    }

    /* access modifiers changed from: protected */
    public void onAddPresenter(Presenter presenter, int i) {
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow(ViewHolder viewHolder) {
    }

    /* access modifiers changed from: protected */
    public void onBind(ViewHolder viewHolder) {
    }

    /* access modifiers changed from: protected */
    public void onCreate(ViewHolder viewHolder) {
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow(ViewHolder viewHolder) {
    }

    /* access modifiers changed from: protected */
    public void onUnbind(ViewHolder viewHolder) {
    }

    public void setAdapter(ObjectAdapter objectAdapter) {
        ObjectAdapter objectAdapter2 = this.mAdapter;
        if (objectAdapter != objectAdapter2) {
            if (objectAdapter2 != null) {
                objectAdapter2.unregisterObserver(this.mDataObserver);
            }
            this.mAdapter = objectAdapter;
            if (objectAdapter == null) {
                notifyDataSetChanged();
                return;
            }
            objectAdapter.registerObserver(this.mDataObserver);
            if (hasStableIds() != this.mAdapter.hasStableIds()) {
                setHasStableIds(this.mAdapter.hasStableIds());
            }
            notifyDataSetChanged();
        }
    }

    public void setPresenter(PresenterSelector presenterSelector) {
        this.mPresenterSelector = presenterSelector;
        notifyDataSetChanged();
    }

    public void setWrapper(Wrapper wrapper) {
        this.mWrapper = wrapper;
    }

    /* access modifiers changed from: 0000 */
    public void setFocusHighlight(FocusHighlightHandler focusHighlightHandler) {
        this.mFocusHighlight = focusHighlightHandler;
    }

    public void setPresenterMapper(ArrayList<Presenter> arrayList) {
        this.mPresenters = arrayList;
    }

    public ArrayList<Presenter> getPresenterMapper() {
        return this.mPresenters;
    }

    public int getItemCount() {
        ObjectAdapter objectAdapter = this.mAdapter;
        if (objectAdapter != null) {
            return objectAdapter.size();
        }
        return 0;
    }

    public int getItemViewType(int i) {
        PresenterSelector presenterSelector = this.mPresenterSelector;
        if (presenterSelector == null) {
            presenterSelector = this.mAdapter.getPresenterSelector();
        }
        Presenter presenter = presenterSelector.getPresenter(this.mAdapter.get(i));
        int indexOf = this.mPresenters.indexOf(presenter);
        if (indexOf < 0) {
            this.mPresenters.add(presenter);
            indexOf = this.mPresenters.indexOf(presenter);
            onAddPresenter(presenter, indexOf);
            AdapterListener adapterListener = this.mAdapterListener;
            if (adapterListener != null) {
                adapterListener.onAddPresenter(presenter, indexOf);
            }
        }
        return indexOf;
    }

    public final androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        androidx.leanback.widget.Presenter.ViewHolder viewHolder;
        View view;
        Presenter presenter = (Presenter) this.mPresenters.get(i);
        Wrapper wrapper = this.mWrapper;
        if (wrapper != null) {
            view = wrapper.createWrapper(viewGroup);
            viewHolder = presenter.onCreateViewHolder(viewGroup);
            this.mWrapper.wrap(view, viewHolder.view);
        } else {
            viewHolder = presenter.onCreateViewHolder(viewGroup);
            view = viewHolder.view;
        }
        ViewHolder viewHolder2 = new ViewHolder(presenter, view, viewHolder);
        onCreate(viewHolder2);
        AdapterListener adapterListener = this.mAdapterListener;
        if (adapterListener != null) {
            adapterListener.onCreate(viewHolder2);
        }
        View view2 = viewHolder2.mHolder.view;
        OnFocusChangeListener onFocusChangeListener = view2.getOnFocusChangeListener();
        if (this.mFocusHighlight != null) {
            boolean z = true;
            if (onFocusChangeListener instanceof ChainingFocusChangeListener) {
                ChainingFocusChangeListener chainingFocusChangeListener = (ChainingFocusChangeListener) onFocusChangeListener;
                if (this.mWrapper == null) {
                    z = false;
                }
                chainingFocusChangeListener.update(z, this.mFocusHighlight);
            } else {
                if (this.mWrapper == null) {
                    z = false;
                }
                view2.setOnFocusChangeListener(new ChainingFocusChangeListener(onFocusChangeListener, z, this.mFocusHighlight));
            }
            this.mFocusHighlight.onInitializeView(view);
        } else if (onFocusChangeListener instanceof ChainingFocusChangeListener) {
            view2.setOnFocusChangeListener(((ChainingFocusChangeListener) onFocusChangeListener).mChainedListener);
        }
        return viewHolder2;
    }

    public void setAdapterListener(AdapterListener adapterListener) {
        this.mAdapterListener = adapterListener;
    }

    public final void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder viewHolder, int i) {
        ViewHolder viewHolder2 = (ViewHolder) viewHolder;
        Object obj = this.mAdapter.get(i);
        viewHolder2.mItem = obj;
        viewHolder2.mPresenter.onBindViewHolder(viewHolder2.mHolder, obj);
        onBind(viewHolder2);
        AdapterListener adapterListener = this.mAdapterListener;
        if (adapterListener != null) {
            adapterListener.onBind(viewHolder2);
        }
    }

    public final void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder viewHolder, int i, List list) {
        ViewHolder viewHolder2 = (ViewHolder) viewHolder;
        Object obj = this.mAdapter.get(i);
        viewHolder2.mItem = obj;
        viewHolder2.mPresenter.onBindViewHolder(viewHolder2.mHolder, obj, list);
        onBind(viewHolder2);
        AdapterListener adapterListener = this.mAdapterListener;
        if (adapterListener != null) {
            adapterListener.onBind(viewHolder2, list);
        }
    }

    public final void onViewRecycled(androidx.recyclerview.widget.RecyclerView.ViewHolder viewHolder) {
        ViewHolder viewHolder2 = (ViewHolder) viewHolder;
        viewHolder2.mPresenter.onUnbindViewHolder(viewHolder2.mHolder);
        onUnbind(viewHolder2);
        AdapterListener adapterListener = this.mAdapterListener;
        if (adapterListener != null) {
            adapterListener.onUnbind(viewHolder2);
        }
        viewHolder2.mItem = null;
    }

    public final boolean onFailedToRecycleView(androidx.recyclerview.widget.RecyclerView.ViewHolder viewHolder) {
        onViewRecycled(viewHolder);
        return false;
    }

    public final void onViewAttachedToWindow(androidx.recyclerview.widget.RecyclerView.ViewHolder viewHolder) {
        ViewHolder viewHolder2 = (ViewHolder) viewHolder;
        onAttachedToWindow(viewHolder2);
        AdapterListener adapterListener = this.mAdapterListener;
        if (adapterListener != null) {
            adapterListener.onAttachedToWindow(viewHolder2);
        }
        viewHolder2.mPresenter.onViewAttachedToWindow(viewHolder2.mHolder);
    }

    public final void onViewDetachedFromWindow(androidx.recyclerview.widget.RecyclerView.ViewHolder viewHolder) {
        ViewHolder viewHolder2 = (ViewHolder) viewHolder;
        viewHolder2.mPresenter.onViewDetachedFromWindow(viewHolder2.mHolder);
        onDetachedFromWindow(viewHolder2);
        AdapterListener adapterListener = this.mAdapterListener;
        if (adapterListener != null) {
            adapterListener.onDetachedFromWindow(viewHolder2);
        }
    }

    public long getItemId(int i) {
        return this.mAdapter.getId(i);
    }

    public FacetProvider getFacetProvider(int i) {
        return (FacetProvider) this.mPresenters.get(i);
    }
}
