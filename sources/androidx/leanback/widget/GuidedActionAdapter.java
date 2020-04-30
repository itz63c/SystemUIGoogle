package androidx.leanback.widget;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import androidx.leanback.widget.GuidedActionAutofillSupport.OnAutofillListener;
import androidx.leanback.widget.GuidedActionsStylist.ViewHolder;
import androidx.leanback.widget.ImeKeyMonitor.ImeKeyListener;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.DiffUtil.Callback;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import java.util.ArrayList;
import java.util.List;

public class GuidedActionAdapter extends Adapter {
    private final ActionAutofillListener mActionAutofillListener;
    private final ActionEditListener mActionEditListener;
    private final ActionOnFocusListener mActionOnFocusListener;
    private final ActionOnKeyListener mActionOnKeyListener;
    final List<GuidedAction> mActions;
    private ClickListener mClickListener;
    DiffCallback<GuidedAction> mDiffCallback;
    GuidedActionAdapterGroup mGroup;
    private final boolean mIsSubAdapter;
    private final OnClickListener mOnClickListener = new OnClickListener() {
        public void onClick(View view) {
            if (view != null && view.getWindowToken() != null && GuidedActionAdapter.this.getRecyclerView() != null) {
                ViewHolder viewHolder = (ViewHolder) GuidedActionAdapter.this.getRecyclerView().getChildViewHolder(view);
                GuidedAction action = viewHolder.getAction();
                if (action.hasTextEditable()) {
                    GuidedActionAdapter guidedActionAdapter = GuidedActionAdapter.this;
                    guidedActionAdapter.mGroup.openIme(guidedActionAdapter, viewHolder);
                } else if (action.hasEditableActivatorView()) {
                    GuidedActionAdapter.this.performOnActionClick(viewHolder);
                } else {
                    GuidedActionAdapter.this.handleCheckedActions(viewHolder);
                    if (action.isEnabled() && !action.infoOnly()) {
                        GuidedActionAdapter.this.performOnActionClick(viewHolder);
                    }
                }
            }
        }
    };
    final GuidedActionsStylist mStylist;

    private class ActionAutofillListener implements OnAutofillListener {
        ActionAutofillListener() {
        }

        public void onAutofill(View view) {
            GuidedActionAdapter guidedActionAdapter = GuidedActionAdapter.this;
            guidedActionAdapter.mGroup.fillAndGoNext(guidedActionAdapter, (EditText) view);
        }
    }

    private class ActionEditListener implements OnEditorActionListener, ImeKeyListener {
        ActionEditListener() {
        }

        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            if (i == 5 || i == 6) {
                GuidedActionAdapter guidedActionAdapter = GuidedActionAdapter.this;
                guidedActionAdapter.mGroup.fillAndGoNext(guidedActionAdapter, textView);
                return true;
            } else if (i != 1) {
                return false;
            } else {
                GuidedActionAdapter guidedActionAdapter2 = GuidedActionAdapter.this;
                guidedActionAdapter2.mGroup.fillAndStay(guidedActionAdapter2, textView);
                return true;
            }
        }

        public boolean onKeyPreIme(EditText editText, int i, KeyEvent keyEvent) {
            if (i == 4 && keyEvent.getAction() == 1) {
                GuidedActionAdapter guidedActionAdapter = GuidedActionAdapter.this;
                guidedActionAdapter.mGroup.fillAndStay(guidedActionAdapter, editText);
                return true;
            } else if (i != 66 || keyEvent.getAction() != 1) {
                return false;
            } else {
                GuidedActionAdapter guidedActionAdapter2 = GuidedActionAdapter.this;
                guidedActionAdapter2.mGroup.fillAndGoNext(guidedActionAdapter2, editText);
                return true;
            }
        }
    }

    private class ActionOnFocusListener implements OnFocusChangeListener {
        private FocusListener mFocusListener;
        private View mSelectedView;

        ActionOnFocusListener(FocusListener focusListener) {
            this.mFocusListener = focusListener;
        }

        public void unFocus() {
            if (this.mSelectedView != null && GuidedActionAdapter.this.getRecyclerView() != null) {
                RecyclerView.ViewHolder childViewHolder = GuidedActionAdapter.this.getRecyclerView().getChildViewHolder(this.mSelectedView);
                if (childViewHolder != null) {
                    GuidedActionAdapter.this.mStylist.onAnimateItemFocused((ViewHolder) childViewHolder, false);
                    return;
                }
                Log.w("GuidedActionAdapter", "RecyclerView returned null view holder", new Throwable());
            }
        }

        public void onFocusChange(View view, boolean z) {
            if (GuidedActionAdapter.this.getRecyclerView() != null) {
                ViewHolder viewHolder = (ViewHolder) GuidedActionAdapter.this.getRecyclerView().getChildViewHolder(view);
                if (z) {
                    this.mSelectedView = view;
                    FocusListener focusListener = this.mFocusListener;
                    if (focusListener != null) {
                        focusListener.onGuidedActionFocused(viewHolder.getAction());
                    }
                } else if (this.mSelectedView == view) {
                    GuidedActionAdapter.this.mStylist.onAnimateItemPressedCancelled(viewHolder);
                    this.mSelectedView = null;
                }
                GuidedActionAdapter.this.mStylist.onAnimateItemFocused(viewHolder, z);
            }
        }
    }

    private class ActionOnKeyListener implements OnKeyListener {
        private boolean mKeyPressed = false;

        ActionOnKeyListener() {
        }

        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if (view == null || keyEvent == null || GuidedActionAdapter.this.getRecyclerView() == null) {
                return false;
            }
            if (i == 23 || i == 66 || i == 160 || i == 99 || i == 100) {
                ViewHolder viewHolder = (ViewHolder) GuidedActionAdapter.this.getRecyclerView().getChildViewHolder(view);
                GuidedAction action = viewHolder.getAction();
                if (!action.isEnabled() || action.infoOnly()) {
                    keyEvent.getAction();
                    return true;
                }
                int action2 = keyEvent.getAction();
                if (action2 != 0) {
                    if (action2 == 1 && this.mKeyPressed) {
                        this.mKeyPressed = false;
                        GuidedActionAdapter.this.mStylist.onAnimateItemPressed(viewHolder, false);
                    }
                } else if (!this.mKeyPressed) {
                    this.mKeyPressed = true;
                    GuidedActionAdapter.this.mStylist.onAnimateItemPressed(viewHolder, true);
                }
            }
            return false;
        }
    }

    public interface ClickListener {
        void onGuidedActionClicked(GuidedAction guidedAction);
    }

    public interface EditListener {
        void onGuidedActionEditCanceled(GuidedAction guidedAction);

        long onGuidedActionEditedAndProceed(GuidedAction guidedAction);

        void onImeClose();

        void onImeOpen();
    }

    public interface FocusListener {
        void onGuidedActionFocused(GuidedAction guidedAction);
    }

    public GuidedActionAdapter(List<GuidedAction> list, ClickListener clickListener, FocusListener focusListener, GuidedActionsStylist guidedActionsStylist, boolean z) {
        this.mActions = list == null ? new ArrayList() : new ArrayList(list);
        this.mClickListener = clickListener;
        this.mStylist = guidedActionsStylist;
        this.mActionOnKeyListener = new ActionOnKeyListener();
        this.mActionOnFocusListener = new ActionOnFocusListener(focusListener);
        this.mActionEditListener = new ActionEditListener();
        this.mActionAutofillListener = new ActionAutofillListener();
        this.mIsSubAdapter = z;
        if (!z) {
            this.mDiffCallback = GuidedActionDiffCallback.getInstance();
        }
    }

    public void setActions(List<GuidedAction> list) {
        if (!this.mIsSubAdapter) {
            this.mStylist.collapseAction(false);
        }
        this.mActionOnFocusListener.unFocus();
        if (this.mDiffCallback != null) {
            final ArrayList arrayList = new ArrayList();
            arrayList.addAll(this.mActions);
            this.mActions.clear();
            this.mActions.addAll(list);
            DiffUtil.calculateDiff(new Callback() {
                public int getOldListSize() {
                    return arrayList.size();
                }

                public int getNewListSize() {
                    return GuidedActionAdapter.this.mActions.size();
                }

                public boolean areItemsTheSame(int i, int i2) {
                    return GuidedActionAdapter.this.mDiffCallback.areItemsTheSame(arrayList.get(i), GuidedActionAdapter.this.mActions.get(i2));
                }

                public boolean areContentsTheSame(int i, int i2) {
                    return GuidedActionAdapter.this.mDiffCallback.areContentsTheSame(arrayList.get(i), GuidedActionAdapter.this.mActions.get(i2));
                }

                public Object getChangePayload(int i, int i2) {
                    return GuidedActionAdapter.this.mDiffCallback.getChangePayload(arrayList.get(i), GuidedActionAdapter.this.mActions.get(i2));
                }
            }).dispatchUpdatesTo((Adapter) this);
            return;
        }
        this.mActions.clear();
        this.mActions.addAll(list);
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.mActions.size();
    }

    public GuidedAction getItem(int i) {
        return (GuidedAction) this.mActions.get(i);
    }

    public int indexOf(GuidedAction guidedAction) {
        return this.mActions.indexOf(guidedAction);
    }

    public GuidedActionsStylist getGuidedActionsStylist() {
        return this.mStylist;
    }

    public int getItemViewType(int i) {
        return this.mStylist.getItemViewType((GuidedAction) this.mActions.get(i));
    }

    /* access modifiers changed from: 0000 */
    public RecyclerView getRecyclerView() {
        boolean z = this.mIsSubAdapter;
        GuidedActionsStylist guidedActionsStylist = this.mStylist;
        return z ? guidedActionsStylist.getSubActionsGridView() : guidedActionsStylist.getActionsGridView();
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        ViewHolder onCreateViewHolder = this.mStylist.onCreateViewHolder(viewGroup, i);
        View view = onCreateViewHolder.itemView;
        view.setOnKeyListener(this.mActionOnKeyListener);
        view.setOnClickListener(this.mOnClickListener);
        view.setOnFocusChangeListener(this.mActionOnFocusListener);
        setupListeners(onCreateViewHolder.getEditableTitleView());
        setupListeners(onCreateViewHolder.getEditableDescriptionView());
        return onCreateViewHolder;
    }

    private void setupListeners(EditText editText) {
        if (editText != null) {
            editText.setPrivateImeOptions("escapeNorth");
            editText.setOnEditorActionListener(this.mActionEditListener);
            if (editText instanceof ImeKeyMonitor) {
                ((ImeKeyMonitor) editText).setImeKeyListener(this.mActionEditListener);
            }
            if (editText instanceof GuidedActionAutofillSupport) {
                ((GuidedActionAutofillSupport) editText).setOnAutofillListener(this.mActionAutofillListener);
            }
        }
    }

    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (i < this.mActions.size()) {
            this.mStylist.onBindViewHolder((ViewHolder) viewHolder, (GuidedAction) this.mActions.get(i));
        }
    }

    public int getItemCount() {
        return this.mActions.size();
    }

    public ViewHolder findSubChildViewHolder(View view) {
        ViewHolder viewHolder = null;
        if (getRecyclerView() == null) {
            return null;
        }
        ViewParent parent = view.getParent();
        while (parent != getRecyclerView() && parent != null && view != null) {
            view = (View) parent;
            parent = parent.getParent();
        }
        if (!(parent == null || view == null)) {
            viewHolder = (ViewHolder) getRecyclerView().getChildViewHolder(view);
        }
        return viewHolder;
    }

    public void handleCheckedActions(ViewHolder viewHolder) {
        GuidedAction action = viewHolder.getAction();
        int checkSetId = action.getCheckSetId();
        if (getRecyclerView() != null && checkSetId != 0) {
            if (checkSetId != -1) {
                int size = this.mActions.size();
                for (int i = 0; i < size; i++) {
                    GuidedAction guidedAction = (GuidedAction) this.mActions.get(i);
                    if (guidedAction != action && guidedAction.getCheckSetId() == checkSetId && guidedAction.isChecked()) {
                        guidedAction.setChecked(false);
                        ViewHolder viewHolder2 = (ViewHolder) getRecyclerView().findViewHolderForPosition(i);
                        if (viewHolder2 != null) {
                            this.mStylist.onAnimateItemChecked(viewHolder2, false);
                        }
                    }
                }
            }
            if (!action.isChecked()) {
                action.setChecked(true);
                this.mStylist.onAnimateItemChecked(viewHolder, true);
            } else if (checkSetId == -1) {
                action.setChecked(false);
                this.mStylist.onAnimateItemChecked(viewHolder, false);
            }
        }
    }

    public void performOnActionClick(ViewHolder viewHolder) {
        ClickListener clickListener = this.mClickListener;
        if (clickListener != null) {
            clickListener.onGuidedActionClicked(viewHolder.getAction());
        }
    }
}
